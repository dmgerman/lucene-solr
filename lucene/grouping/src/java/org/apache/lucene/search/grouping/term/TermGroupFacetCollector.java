begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping.term
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
operator|.
name|term
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AtomicReaderContext
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|TermsEnum
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
name|FieldCache
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
name|grouping
operator|.
name|AbstractGroupFacetCollector
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
name|BytesRef
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
name|SentinelIntSet
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
name|UnicodeUtil
import|;
end_import

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

begin_comment
comment|/**  * An implementation of {@link AbstractGroupFacetCollector} that computes grouped facets based on the indexed terms  * from the {@link FieldCache}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermGroupFacetCollector
specifier|public
specifier|abstract
class|class
name|TermGroupFacetCollector
extends|extends
name|AbstractGroupFacetCollector
block|{
DECL|field|groupedFacetHits
specifier|final
name|List
argument_list|<
name|GroupedFacetHit
argument_list|>
name|groupedFacetHits
decl_stmt|;
DECL|field|segmentGroupedFacetHits
specifier|final
name|SentinelIntSet
name|segmentGroupedFacetHits
decl_stmt|;
DECL|field|groupFieldTermsIndex
name|SortedDocValues
name|groupFieldTermsIndex
decl_stmt|;
comment|/**    * Factory method for creating the right implementation based on the fact whether the facet field contains    * multiple tokens per documents.    *    * @param groupField The group field    * @param facetField The facet field    * @param facetFieldMultivalued Whether the facet field has multiple tokens per document    * @param facetPrefix The facet prefix a facet entry should start with to be included.    * @param initialSize The initial allocation size of the internal int set and group facet list which should roughly    *                    match the total number of expected unique groups. Be aware that the heap usage is    *                    4 bytes * initialSize.    * @return<code>TermGroupFacetCollector</code> implementation    */
DECL|method|createTermGroupFacetCollector
specifier|public
specifier|static
name|TermGroupFacetCollector
name|createTermGroupFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|facetField
parameter_list|,
name|boolean
name|facetFieldMultivalued
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
if|if
condition|(
name|facetFieldMultivalued
condition|)
block|{
return|return
operator|new
name|MV
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SV
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
return|;
block|}
block|}
DECL|method|TermGroupFacetCollector
name|TermGroupFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|facetField
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|)
expr_stmt|;
name|groupedFacetHits
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
name|segmentGroupedFacetHits
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|initialSize
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|// Implementation for single valued facet fields.
DECL|class|SV
specifier|static
class|class
name|SV
extends|extends
name|TermGroupFacetCollector
block|{
DECL|field|facetFieldTermsIndex
specifier|private
name|SortedDocValues
name|facetFieldTermsIndex
decl_stmt|;
DECL|method|SV
name|SV
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|facetField
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|facetOrd
init|=
name|facetFieldTermsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetOrd
operator|<
name|startFacetOrd
operator|||
name|facetOrd
operator|>=
name|endFacetOrd
condition|)
block|{
return|return;
block|}
name|int
name|groupOrd
init|=
name|groupFieldTermsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|int
name|segmentGroupedFacetsIndex
init|=
name|groupOrd
operator|*
operator|(
name|facetFieldTermsIndex
operator|.
name|getValueCount
argument_list|()
operator|+
literal|1
operator|)
operator|+
name|facetOrd
decl_stmt|;
if|if
condition|(
name|segmentGroupedFacetHits
operator|.
name|exists
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
condition|)
block|{
return|return;
block|}
name|segmentTotalCount
operator|++
expr_stmt|;
name|segmentFacetCounts
index|[
name|facetOrd
operator|+
literal|1
index|]
operator|++
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
name|BytesRef
name|groupKey
decl_stmt|;
if|if
condition|(
name|groupOrd
operator|==
operator|-
literal|1
condition|)
block|{
name|groupKey
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|groupKey
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|groupFieldTermsIndex
operator|.
name|lookupOrd
argument_list|(
name|groupOrd
argument_list|,
name|groupKey
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|facetKey
decl_stmt|;
if|if
condition|(
name|facetOrd
operator|==
operator|-
literal|1
condition|)
block|{
name|facetKey
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|facetKey
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|facetFieldTermsIndex
operator|.
name|lookupOrd
argument_list|(
name|facetOrd
argument_list|,
name|facetKey
argument_list|)
expr_stmt|;
block|}
name|groupedFacetHits
operator|.
name|add
argument_list|(
operator|new
name|GroupedFacetHit
argument_list|(
name|groupKey
argument_list|,
name|facetKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentFacetCounts
operator|!=
literal|null
condition|)
block|{
name|segmentResults
operator|.
name|add
argument_list|(
name|createSegmentResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|groupFieldTermsIndex
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
name|facetFieldTermsIndex
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|facetField
argument_list|)
expr_stmt|;
comment|// 1+ to allow for the -1 "not set":
name|segmentFacetCounts
operator|=
operator|new
name|int
index|[
name|facetFieldTermsIndex
operator|.
name|getValueCount
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
name|segmentTotalCount
operator|=
literal|0
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|GroupedFacetHit
name|groupedFacetHit
range|:
name|groupedFacetHits
control|)
block|{
name|int
name|facetOrd
init|=
name|groupedFacetHit
operator|.
name|facetValue
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|facetFieldTermsIndex
operator|.
name|lookupTerm
argument_list|(
name|groupedFacetHit
operator|.
name|facetValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupedFacetHit
operator|.
name|facetValue
operator|!=
literal|null
operator|&&
name|facetOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|groupOrd
init|=
name|groupedFacetHit
operator|.
name|groupValue
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|groupFieldTermsIndex
operator|.
name|lookupTerm
argument_list|(
name|groupedFacetHit
operator|.
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupedFacetHit
operator|.
name|groupValue
operator|!=
literal|null
operator|&&
name|groupOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|segmentGroupedFacetsIndex
init|=
name|groupOrd
operator|*
operator|(
name|facetFieldTermsIndex
operator|.
name|getValueCount
argument_list|()
operator|+
literal|1
operator|)
operator|+
name|facetOrd
decl_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facetPrefix
operator|!=
literal|null
condition|)
block|{
name|startFacetOrd
operator|=
name|facetFieldTermsIndex
operator|.
name|lookupTerm
argument_list|(
name|facetPrefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|startFacetOrd
operator|<
literal|0
condition|)
block|{
comment|// Points to the ord one higher than facetPrefix
name|startFacetOrd
operator|=
operator|-
name|startFacetOrd
operator|-
literal|1
expr_stmt|;
block|}
name|BytesRef
name|facetEndPrefix
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|facetPrefix
argument_list|)
decl_stmt|;
name|facetEndPrefix
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
name|endFacetOrd
operator|=
name|facetFieldTermsIndex
operator|.
name|lookupTerm
argument_list|(
name|facetEndPrefix
argument_list|)
expr_stmt|;
assert|assert
name|endFacetOrd
operator|<
literal|0
assert|;
name|endFacetOrd
operator|=
operator|-
name|endFacetOrd
operator|-
literal|1
expr_stmt|;
comment|// Points to the ord one higher than facetEndPrefix
block|}
else|else
block|{
name|startFacetOrd
operator|=
operator|-
literal|1
expr_stmt|;
name|endFacetOrd
operator|=
name|facetFieldTermsIndex
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createSegmentResult
specifier|protected
name|SegmentResult
name|createSegmentResult
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentResult
argument_list|(
name|segmentFacetCounts
argument_list|,
name|segmentTotalCount
argument_list|,
name|facetFieldTermsIndex
operator|.
name|termsEnum
argument_list|()
argument_list|,
name|startFacetOrd
argument_list|,
name|endFacetOrd
argument_list|)
return|;
block|}
DECL|class|SegmentResult
specifier|private
specifier|static
class|class
name|SegmentResult
extends|extends
name|AbstractGroupFacetCollector
operator|.
name|SegmentResult
block|{
DECL|field|tenum
specifier|final
name|TermsEnum
name|tenum
decl_stmt|;
DECL|method|SegmentResult
name|SegmentResult
parameter_list|(
name|int
index|[]
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|TermsEnum
name|tenum
parameter_list|,
name|int
name|startFacetOrd
parameter_list|,
name|int
name|endFacetOrd
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|counts
argument_list|,
name|total
operator|-
name|counts
index|[
literal|0
index|]
argument_list|,
name|counts
index|[
literal|0
index|]
argument_list|,
name|endFacetOrd
operator|+
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|tenum
operator|=
name|tenum
expr_stmt|;
name|this
operator|.
name|mergePos
operator|=
name|startFacetOrd
operator|==
operator|-
literal|1
condition|?
literal|1
else|:
name|startFacetOrd
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|mergePos
operator|<
name|maxTermPos
condition|)
block|{
assert|assert
name|tenum
operator|!=
literal|null
assert|;
name|tenum
operator|.
name|seekExact
argument_list|(
name|startFacetOrd
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|startFacetOrd
argument_list|)
expr_stmt|;
name|mergeTerm
operator|=
name|tenum
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextTerm
specifier|protected
name|void
name|nextTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeTerm
operator|=
name|tenum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Implementation for multi valued facet fields.
DECL|class|MV
specifier|static
class|class
name|MV
extends|extends
name|TermGroupFacetCollector
block|{
DECL|field|facetFieldDocTermOrds
specifier|private
name|SortedSetDocValues
name|facetFieldDocTermOrds
decl_stmt|;
DECL|field|facetOrdTermsEnum
specifier|private
name|TermsEnum
name|facetOrdTermsEnum
decl_stmt|;
DECL|field|facetFieldNumTerms
specifier|private
name|int
name|facetFieldNumTerms
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|MV
name|MV
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|facetField
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|groupField
argument_list|,
name|facetField
argument_list|,
name|facetPrefix
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|groupOrd
init|=
name|groupFieldTermsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetFieldNumTerms
operator|==
literal|0
condition|)
block|{
name|int
name|segmentGroupedFacetsIndex
init|=
name|groupOrd
operator|*
operator|(
name|facetFieldNumTerms
operator|+
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|facetPrefix
operator|!=
literal|null
operator|||
name|segmentGroupedFacetHits
operator|.
name|exists
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
condition|)
block|{
return|return;
block|}
name|segmentTotalCount
operator|++
expr_stmt|;
name|segmentFacetCounts
index|[
name|facetFieldNumTerms
index|]
operator|++
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
name|BytesRef
name|groupKey
decl_stmt|;
if|if
condition|(
name|groupOrd
operator|==
operator|-
literal|1
condition|)
block|{
name|groupKey
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|groupKey
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|groupFieldTermsIndex
operator|.
name|lookupOrd
argument_list|(
name|groupOrd
argument_list|,
name|groupKey
argument_list|)
expr_stmt|;
block|}
name|groupedFacetHits
operator|.
name|add
argument_list|(
operator|new
name|GroupedFacetHit
argument_list|(
name|groupKey
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|facetFieldDocTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
name|boolean
name|empty
init|=
literal|true
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|facetFieldDocTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|process
argument_list|(
name|groupOrd
argument_list|,
operator|(
name|int
operator|)
name|ord
argument_list|)
expr_stmt|;
name|empty
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|empty
condition|)
block|{
name|process
argument_list|(
name|groupOrd
argument_list|,
name|facetFieldNumTerms
argument_list|)
expr_stmt|;
comment|// this facet ord is reserved for docs not containing facet field.
block|}
block|}
DECL|method|process
specifier|private
name|void
name|process
parameter_list|(
name|int
name|groupOrd
parameter_list|,
name|int
name|facetOrd
parameter_list|)
block|{
if|if
condition|(
name|facetOrd
operator|<
name|startFacetOrd
operator|||
name|facetOrd
operator|>=
name|endFacetOrd
condition|)
block|{
return|return;
block|}
name|int
name|segmentGroupedFacetsIndex
init|=
name|groupOrd
operator|*
operator|(
name|facetFieldNumTerms
operator|+
literal|1
operator|)
operator|+
name|facetOrd
decl_stmt|;
if|if
condition|(
name|segmentGroupedFacetHits
operator|.
name|exists
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
condition|)
block|{
return|return;
block|}
name|segmentTotalCount
operator|++
expr_stmt|;
name|segmentFacetCounts
index|[
name|facetOrd
index|]
operator|++
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
name|BytesRef
name|groupKey
decl_stmt|;
if|if
condition|(
name|groupOrd
operator|==
operator|-
literal|1
condition|)
block|{
name|groupKey
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|groupKey
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|groupFieldTermsIndex
operator|.
name|lookupOrd
argument_list|(
name|groupOrd
argument_list|,
name|groupKey
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BytesRef
name|facetValue
decl_stmt|;
if|if
condition|(
name|facetOrd
operator|==
name|facetFieldNumTerms
condition|)
block|{
name|facetValue
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|facetFieldDocTermOrds
operator|.
name|lookupOrd
argument_list|(
name|facetOrd
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|facetValue
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
comment|// must we?
block|}
name|groupedFacetHits
operator|.
name|add
argument_list|(
operator|new
name|GroupedFacetHit
argument_list|(
name|groupKey
argument_list|,
name|facetValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentFacetCounts
operator|!=
literal|null
condition|)
block|{
name|segmentResults
operator|.
name|add
argument_list|(
name|createSegmentResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|groupFieldTermsIndex
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
name|facetFieldDocTermOrds
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|facetField
argument_list|)
expr_stmt|;
name|facetFieldNumTerms
operator|=
operator|(
name|int
operator|)
name|facetFieldDocTermOrds
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|facetFieldNumTerms
operator|==
literal|0
condition|)
block|{
name|facetOrdTermsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|facetOrdTermsEnum
operator|=
name|facetFieldDocTermOrds
operator|.
name|termsEnum
argument_list|()
expr_stmt|;
block|}
comment|// [facetFieldNumTerms() + 1] for all possible facet values and docs not containing facet field
name|segmentFacetCounts
operator|=
operator|new
name|int
index|[
name|facetFieldNumTerms
operator|+
literal|1
index|]
expr_stmt|;
name|segmentTotalCount
operator|=
literal|0
expr_stmt|;
name|segmentGroupedFacetHits
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|GroupedFacetHit
name|groupedFacetHit
range|:
name|groupedFacetHits
control|)
block|{
name|int
name|groupOrd
init|=
name|groupedFacetHit
operator|.
name|groupValue
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|groupFieldTermsIndex
operator|.
name|lookupTerm
argument_list|(
name|groupedFacetHit
operator|.
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupedFacetHit
operator|.
name|groupValue
operator|!=
literal|null
operator|&&
name|groupOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|facetOrd
decl_stmt|;
if|if
condition|(
name|groupedFacetHit
operator|.
name|facetValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|facetOrdTermsEnum
operator|==
literal|null
operator|||
operator|!
name|facetOrdTermsEnum
operator|.
name|seekExact
argument_list|(
name|groupedFacetHit
operator|.
name|facetValue
argument_list|,
literal|true
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|facetOrd
operator|=
operator|(
name|int
operator|)
name|facetOrdTermsEnum
operator|.
name|ord
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|facetOrd
operator|=
name|facetFieldNumTerms
expr_stmt|;
block|}
comment|// (facetFieldDocTermOrds.numTerms() + 1) for all possible facet values and docs not containing facet field
name|int
name|segmentGroupedFacetsIndex
init|=
name|groupOrd
operator|*
operator|(
name|facetFieldNumTerms
operator|+
literal|1
operator|)
operator|+
name|facetOrd
decl_stmt|;
name|segmentGroupedFacetHits
operator|.
name|put
argument_list|(
name|segmentGroupedFacetsIndex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facetPrefix
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
decl_stmt|;
if|if
condition|(
name|facetOrdTermsEnum
operator|!=
literal|null
condition|)
block|{
name|seekStatus
operator|=
name|facetOrdTermsEnum
operator|.
name|seekCeil
argument_list|(
name|facetPrefix
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|seekStatus
operator|=
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
expr_stmt|;
block|}
if|if
condition|(
name|seekStatus
operator|!=
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|startFacetOrd
operator|=
operator|(
name|int
operator|)
name|facetOrdTermsEnum
operator|.
name|ord
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startFacetOrd
operator|=
literal|0
expr_stmt|;
name|endFacetOrd
operator|=
literal|0
expr_stmt|;
return|return;
block|}
name|BytesRef
name|facetEndPrefix
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|facetPrefix
argument_list|)
decl_stmt|;
name|facetEndPrefix
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
name|seekStatus
operator|=
name|facetOrdTermsEnum
operator|.
name|seekCeil
argument_list|(
name|facetEndPrefix
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|seekStatus
operator|!=
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|endFacetOrd
operator|=
operator|(
name|int
operator|)
name|facetOrdTermsEnum
operator|.
name|ord
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|endFacetOrd
operator|=
name|facetFieldNumTerms
expr_stmt|;
comment|// Don't include null...
block|}
block|}
else|else
block|{
name|startFacetOrd
operator|=
literal|0
expr_stmt|;
name|endFacetOrd
operator|=
name|facetFieldNumTerms
operator|+
literal|1
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createSegmentResult
specifier|protected
name|SegmentResult
name|createSegmentResult
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentResult
argument_list|(
name|segmentFacetCounts
argument_list|,
name|segmentTotalCount
argument_list|,
name|facetFieldNumTerms
argument_list|,
name|facetOrdTermsEnum
argument_list|,
name|startFacetOrd
argument_list|,
name|endFacetOrd
argument_list|)
return|;
block|}
DECL|class|SegmentResult
specifier|private
specifier|static
class|class
name|SegmentResult
extends|extends
name|AbstractGroupFacetCollector
operator|.
name|SegmentResult
block|{
DECL|field|tenum
specifier|final
name|TermsEnum
name|tenum
decl_stmt|;
DECL|method|SegmentResult
name|SegmentResult
parameter_list|(
name|int
index|[]
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|missingCountIndex
parameter_list|,
name|TermsEnum
name|tenum
parameter_list|,
name|int
name|startFacetOrd
parameter_list|,
name|int
name|endFacetOrd
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|counts
argument_list|,
name|total
operator|-
name|counts
index|[
name|missingCountIndex
index|]
argument_list|,
name|counts
index|[
name|missingCountIndex
index|]
argument_list|,
name|endFacetOrd
operator|==
name|missingCountIndex
operator|+
literal|1
condition|?
name|missingCountIndex
else|:
name|endFacetOrd
argument_list|)
expr_stmt|;
name|this
operator|.
name|tenum
operator|=
name|tenum
expr_stmt|;
name|this
operator|.
name|mergePos
operator|=
name|startFacetOrd
expr_stmt|;
if|if
condition|(
name|tenum
operator|!=
literal|null
condition|)
block|{
name|tenum
operator|.
name|seekExact
argument_list|(
name|mergePos
argument_list|)
expr_stmt|;
name|mergeTerm
operator|=
name|tenum
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextTerm
specifier|protected
name|void
name|nextTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeTerm
operator|=
name|tenum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|GroupedFacetHit
class|class
name|GroupedFacetHit
block|{
DECL|field|groupValue
specifier|final
name|BytesRef
name|groupValue
decl_stmt|;
DECL|field|facetValue
specifier|final
name|BytesRef
name|facetValue
decl_stmt|;
DECL|method|GroupedFacetHit
name|GroupedFacetHit
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|BytesRef
name|facetValue
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|facetValue
operator|=
name|facetValue
expr_stmt|;
block|}
block|}
end_class

end_unit

