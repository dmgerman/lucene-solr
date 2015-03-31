begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetsConfig
operator|.
name|DimConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitDocIdSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
import|;
end_import

begin_comment
comment|/**  * Collects hits for subsequent faceting, using sampling if needed. Once you've  * run a search and collect hits into this, instantiate one of the  * {@link Facets} subclasses to do the facet counting. Note that this collector  * does not collect the scores of matching docs (i.e.  * {@link FacetsCollector.MatchingDocs#scores}) is {@code null}.  *<p>  * If you require the original set of hits, you can call  * {@link #getOriginalMatchingDocs()}. Also, since the counts of the top-facets  * is based on the sampled set, you can amortize the counts by calling  * {@link #amortizeFacetCounts}.  */
end_comment

begin_class
DECL|class|RandomSamplingFacetsCollector
specifier|public
class|class
name|RandomSamplingFacetsCollector
extends|extends
name|FacetsCollector
block|{
comment|/**    * Faster alternative for java.util.Random, inspired by    * http://dmurphy747.wordpress.com/2011/03/23/xorshift-vs-random-    * performance-in-java/    *<p>    * Has a period of 2^64-1    */
DECL|class|XORShift64Random
specifier|private
specifier|static
class|class
name|XORShift64Random
block|{
DECL|field|x
specifier|private
name|long
name|x
decl_stmt|;
comment|/** Creates a xorshift random generator using the provided seed */
DECL|method|XORShift64Random
specifier|public
name|XORShift64Random
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|x
operator|=
name|seed
operator|==
literal|0
condition|?
literal|0xdeadbeef
else|:
name|seed
expr_stmt|;
block|}
comment|/** Get the next random long value */
DECL|method|randomLong
specifier|public
name|long
name|randomLong
parameter_list|()
block|{
name|x
operator|^=
operator|(
name|x
operator|<<
literal|21
operator|)
expr_stmt|;
name|x
operator|^=
operator|(
name|x
operator|>>>
literal|35
operator|)
expr_stmt|;
name|x
operator|^=
operator|(
name|x
operator|<<
literal|4
operator|)
expr_stmt|;
return|return
name|x
return|;
block|}
comment|/** Get the next random int, between 0 (inclusive) and n (exclusive) */
DECL|method|nextInt
specifier|public
name|int
name|nextInt
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
name|res
init|=
call|(
name|int
call|)
argument_list|(
name|randomLong
argument_list|()
operator|%
name|n
argument_list|)
decl_stmt|;
return|return
operator|(
name|res
operator|<
literal|0
operator|)
condition|?
operator|-
name|res
else|:
name|res
return|;
block|}
block|}
DECL|field|NOT_CALCULATED
specifier|private
specifier|final
specifier|static
name|int
name|NOT_CALCULATED
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|sampleSize
specifier|private
specifier|final
name|int
name|sampleSize
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|XORShift64Random
name|random
decl_stmt|;
DECL|field|samplingRate
specifier|private
name|double
name|samplingRate
decl_stmt|;
DECL|field|sampledDocs
specifier|private
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|sampledDocs
decl_stmt|;
DECL|field|totalHits
specifier|private
name|int
name|totalHits
init|=
name|NOT_CALCULATED
decl_stmt|;
DECL|field|leftoverBin
specifier|private
name|int
name|leftoverBin
init|=
name|NOT_CALCULATED
decl_stmt|;
DECL|field|leftoverIndex
specifier|private
name|int
name|leftoverIndex
init|=
name|NOT_CALCULATED
decl_stmt|;
comment|/**    * Constructor with the given sample size and default seed.    *     * @see #RandomSamplingFacetsCollector(int, long)    */
DECL|method|RandomSamplingFacetsCollector
specifier|public
name|RandomSamplingFacetsCollector
parameter_list|(
name|int
name|sampleSize
parameter_list|)
block|{
name|this
argument_list|(
name|sampleSize
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor with the given sample size and seed.    *     * @param sampleSize    *          The preferred sample size. If the number of hits is greater than    *          the size, sampling will be done using a sample ratio of sampling    *          size / totalN. For example: 1000 hits, sample size = 10 results in    *          samplingRatio of 0.01. If the number of hits is lower, no sampling    *          is done at all    * @param seed    *          The random seed. If {@code 0} then a seed will be chosen for you.    */
DECL|method|RandomSamplingFacetsCollector
specifier|public
name|RandomSamplingFacetsCollector
parameter_list|(
name|int
name|sampleSize
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|sampleSize
operator|=
name|sampleSize
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|XORShift64Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|sampledDocs
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Returns the sampled list of the matching documents. Note that a    * {@link FacetsCollector.MatchingDocs} instance is returned per segment, even    * if no hits from that segment are included in the sampled set.    *<p>    * Note: One or more of the MatchingDocs might be empty (not containing any    * hits) as result of sampling.    *<p>    * Note: {@code MatchingDocs.totalHits} is copied from the original    * MatchingDocs, scores is set to {@code null}    */
annotation|@
name|Override
DECL|method|getMatchingDocs
specifier|public
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|getMatchingDocs
parameter_list|()
block|{
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
init|=
name|super
operator|.
name|getMatchingDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|totalHits
operator|==
name|NOT_CALCULATED
condition|)
block|{
name|totalHits
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|MatchingDocs
name|md
range|:
name|matchingDocs
control|)
block|{
name|totalHits
operator|+=
name|md
operator|.
name|totalHits
expr_stmt|;
block|}
block|}
if|if
condition|(
name|totalHits
operator|<=
name|sampleSize
condition|)
block|{
return|return
name|matchingDocs
return|;
block|}
if|if
condition|(
name|sampledDocs
operator|==
literal|null
condition|)
block|{
name|samplingRate
operator|=
operator|(
literal|1.0
operator|*
name|sampleSize
operator|)
operator|/
name|totalHits
expr_stmt|;
name|sampledDocs
operator|=
name|createSampledDocs
argument_list|(
name|matchingDocs
argument_list|)
expr_stmt|;
block|}
return|return
name|sampledDocs
return|;
block|}
comment|/** Returns the original matching documents. */
DECL|method|getOriginalMatchingDocs
specifier|public
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|getOriginalMatchingDocs
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMatchingDocs
argument_list|()
return|;
block|}
comment|/** Create a sampled copy of the matching documents list. */
DECL|method|createSampledDocs
specifier|private
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|createSampledDocs
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocsList
parameter_list|)
block|{
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|sampledDocsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|matchingDocsList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|docs
range|:
name|matchingDocsList
control|)
block|{
name|sampledDocsList
operator|.
name|add
argument_list|(
name|createSample
argument_list|(
name|docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sampledDocsList
return|;
block|}
comment|/** Create a sampled of the given hits. */
DECL|method|createSample
specifier|private
name|MatchingDocs
name|createSample
parameter_list|(
name|MatchingDocs
name|docs
parameter_list|)
block|{
name|int
name|maxdoc
init|=
name|docs
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
comment|// TODO: we could try the WAH8DocIdSet here as well, as the results will be sparse
name|FixedBitSet
name|sampleDocs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxdoc
argument_list|)
decl_stmt|;
name|int
name|binSize
init|=
call|(
name|int
call|)
argument_list|(
literal|1.0
operator|/
name|samplingRate
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|int
name|limit
decl_stmt|,
name|randomIndex
decl_stmt|;
if|if
condition|(
name|leftoverBin
operator|!=
name|NOT_CALCULATED
condition|)
block|{
name|limit
operator|=
name|leftoverBin
expr_stmt|;
comment|// either NOT_CALCULATED, which means we already sampled from that bin,
comment|// or the next document to sample
name|randomIndex
operator|=
name|leftoverIndex
expr_stmt|;
block|}
else|else
block|{
name|limit
operator|=
name|binSize
expr_stmt|;
name|randomIndex
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|binSize
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docs
operator|.
name|bits
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|counter
operator|==
name|randomIndex
condition|)
block|{
name|sampleDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|>=
name|limit
condition|)
block|{
name|counter
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
name|binSize
expr_stmt|;
name|randomIndex
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|binSize
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|counter
operator|==
literal|0
condition|)
block|{
comment|// we either exhausted the bin and the iterator at the same time, or
comment|// this segment had no results. in the latter case we might want to
comment|// carry leftover to the next segment as is, but that complicates the
comment|// code and doesn't seem so important.
name|leftoverBin
operator|=
name|leftoverIndex
operator|=
name|NOT_CALCULATED
expr_stmt|;
block|}
else|else
block|{
name|leftoverBin
operator|=
name|limit
operator|-
name|counter
expr_stmt|;
if|if
condition|(
name|randomIndex
operator|>
name|counter
condition|)
block|{
comment|// the document to sample is in the next bin
name|leftoverIndex
operator|=
name|randomIndex
operator|-
name|counter
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|randomIndex
operator|<
name|counter
condition|)
block|{
comment|// we sampled a document from the bin, so just skip over remaining
comment|// documents in the bin in the next segment.
name|leftoverIndex
operator|=
name|NOT_CALCULATED
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MatchingDocs
argument_list|(
name|docs
operator|.
name|context
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|sampleDocs
argument_list|)
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Note: if you use a counting {@link Facets} implementation, you can amortize the    * sampled counts by calling this method. Uses the {@link FacetsConfig} and    * the {@link IndexSearcher} to determine the upper bound for each facet value.    */
DECL|method|amortizeFacetCounts
specifier|public
name|FacetResult
name|amortizeFacetCounts
parameter_list|(
name|FacetResult
name|res
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|res
operator|==
literal|null
operator|||
name|totalHits
operator|<=
name|sampleSize
condition|)
block|{
return|return
name|res
return|;
block|}
name|LabelAndValue
index|[]
name|fixedLabelValues
init|=
operator|new
name|LabelAndValue
index|[
name|res
operator|.
name|labelValues
operator|.
name|length
index|]
decl_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|DimConfig
name|dimConfig
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|res
operator|.
name|dim
argument_list|)
decl_stmt|;
comment|// +2 to prepend dimension, append child label
name|String
index|[]
name|childPath
init|=
operator|new
name|String
index|[
name|res
operator|.
name|path
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|childPath
index|[
literal|0
index|]
operator|=
name|res
operator|.
name|dim
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|res
operator|.
name|path
argument_list|,
literal|0
argument_list|,
name|childPath
argument_list|,
literal|1
argument_list|,
name|res
operator|.
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// reuse
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|res
operator|.
name|labelValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|childPath
index|[
name|res
operator|.
name|path
operator|.
name|length
operator|+
literal|1
index|]
operator|=
name|res
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|label
expr_stmt|;
name|String
name|fullPath
init|=
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|childPath
argument_list|,
name|childPath
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|dimConfig
operator|.
name|indexFieldName
argument_list|,
name|fullPath
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|correctedCount
init|=
call|(
name|int
call|)
argument_list|(
name|res
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|/
name|samplingRate
argument_list|)
decl_stmt|;
name|correctedCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|correctedCount
argument_list|)
expr_stmt|;
name|fixedLabelValues
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|res
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|label
argument_list|,
name|correctedCount
argument_list|)
expr_stmt|;
block|}
comment|// cap the total count on the total number of non-deleted documents in the reader
name|int
name|correctedTotalCount
init|=
name|res
operator|.
name|value
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|correctedTotalCount
operator|>
literal|0
condition|)
block|{
name|correctedTotalCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|,
call|(
name|int
call|)
argument_list|(
name|res
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|/
name|samplingRate
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|res
operator|.
name|dim
argument_list|,
name|res
operator|.
name|path
argument_list|,
name|correctedTotalCount
argument_list|,
name|fixedLabelValues
argument_list|,
name|res
operator|.
name|childCount
argument_list|)
return|;
block|}
comment|/** Returns the sampling rate that was used. */
DECL|method|getSamplingRate
specifier|public
name|double
name|getSamplingRate
parameter_list|()
block|{
return|return
name|samplingRate
return|;
block|}
block|}
end_class

end_unit

