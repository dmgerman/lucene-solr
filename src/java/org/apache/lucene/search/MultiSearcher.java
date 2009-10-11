begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|FieldSelector
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
name|CorruptIndexException
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
name|util
operator|.
name|ReaderUtil
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Implements search over a set of<code>Searchables</code>.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods.  */
end_comment

begin_class
DECL|class|MultiSearcher
specifier|public
class|class
name|MultiSearcher
extends|extends
name|Searcher
block|{
comment|/**    * Document Frequency cache acting as a Dummy-Searcher. This class is no    * full-fledged Searcher, but only supports the methods necessary to    * initialize Weights.    */
DECL|class|CachedDfSource
specifier|private
specifier|static
class|class
name|CachedDfSource
extends|extends
name|Searcher
block|{
DECL|field|dfMap
specifier|private
name|Map
name|dfMap
decl_stmt|;
comment|// Map from Terms to corresponding doc freqs
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
decl_stmt|;
comment|// document count
DECL|method|CachedDfSource
specifier|public
name|CachedDfSource
parameter_list|(
name|Map
name|dfMap
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|dfMap
operator|=
name|dfMap
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|int
name|df
decl_stmt|;
try|try
block|{
name|df
operator|=
operator|(
operator|(
name|Integer
operator|)
name|dfMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"df for term "
operator|+
name|term
operator|.
name|text
argument_list|()
operator|+
literal|" not available"
argument_list|)
throw|;
block|}
return|return
name|df
return|;
block|}
DECL|method|docFreqs
specifier|public
name|int
index|[]
name|docFreqs
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|docFreq
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
comment|// this is a bit of a hack. We know that a query which
comment|// creates a Weight based on this Dummy-Searcher is
comment|// always already rewritten (see preparedWeight()).
comment|// Therefore we just return the unmodified query here
return|return
name|query
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|results
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|field|searchables
specifier|private
name|Searchable
index|[]
name|searchables
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
init|=
literal|0
decl_stmt|;
comment|/** Creates a searcher which searches<i>searchers</i>. */
DECL|method|MultiSearcher
specifier|public
name|MultiSearcher
parameter_list|(
name|Searchable
index|[]
name|searchables
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|searchables
operator|=
name|searchables
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|searchables
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
comment|// build starts array
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|starts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|searchables
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// compute maxDocs
block|}
name|starts
index|[
name|searchables
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
block|}
comment|/** Return the array of {@link Searchable}s this searches. */
DECL|method|getSearchables
specifier|public
name|Searchable
index|[]
name|getSearchables
parameter_list|()
block|{
return|return
name|searchables
return|;
block|}
DECL|method|getStarts
specifier|protected
name|int
index|[]
name|getStarts
parameter_list|()
block|{
return|return
name|starts
return|;
block|}
comment|// inherit javadoc
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|searchables
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|docFreq
operator|+=
name|searchables
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|docFreq
return|;
block|}
comment|// inherit javadoc
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|int
name|i
init|=
name|subSearcher
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find searcher index
return|return
name|searchables
index|[
name|i
index|]
operator|.
name|doc
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to searcher
block|}
comment|// inherit javadoc
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|int
name|i
init|=
name|subSearcher
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find searcher index
return|return
name|searchables
index|[
name|i
index|]
operator|.
name|doc
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|fieldSelector
argument_list|)
return|;
comment|// dispatch to searcher
block|}
comment|/** Returns index of the searcher for document<code>n</code> in the array    * used to construct this searcher. */
DECL|method|subSearcher
specifier|public
name|int
name|subSearcher
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// find searcher for doc n:
return|return
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|n
argument_list|,
name|starts
argument_list|)
return|;
block|}
comment|/** Returns the document number of document<code>n</code> within its    * sub-index. */
DECL|method|subDoc
specifier|public
name|int
name|subDoc
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|n
operator|-
name|starts
index|[
name|subSearcher
argument_list|(
name|n
argument_list|)
index|]
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searcher
name|TopDocs
name|docs
init|=
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|)
decl_stmt|;
name|totalHits
operator|+=
name|docs
operator|.
name|totalHits
expr_stmt|;
comment|// update totalHits
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|docs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// merge scoreDocs into hq
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|scoreDoc
operator|.
name|doc
operator|+=
name|starts
index|[
name|i
index|]
expr_stmt|;
comment|// convert doc
if|if
condition|(
name|scoreDoc
operator|==
name|hq
operator|.
name|insertWithOverflow
argument_list|(
name|scoreDoc
argument_list|)
condition|)
break|break;
comment|// no more scores> minScore
block|}
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|float
name|maxScore
init|=
operator|(
name|totalHits
operator|==
literal|0
operator|)
condition|?
name|Float
operator|.
name|NEGATIVE_INFINITY
else|:
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
decl_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldDocSortedHitQueue
name|hq
init|=
literal|null
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searcher
name|TopFieldDocs
name|docs
init|=
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
decl_stmt|;
comment|// If one of the Sort fields is FIELD_DOC, need to fix its values, so that
comment|// it will break ties by doc Id properly. Otherwise, it will compare to
comment|// 'relative' doc Ids, that belong to two different searchers.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|fields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|docs
operator|.
name|fields
index|[
name|j
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|DOC
condition|)
block|{
comment|// iterate over the score docs and change their fields value
for|for
control|(
name|int
name|j2
init|=
literal|0
init|;
name|j2
operator|<
name|docs
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j2
operator|++
control|)
block|{
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|docs
operator|.
name|scoreDocs
index|[
name|j2
index|]
decl_stmt|;
name|fd
operator|.
name|fields
index|[
name|j
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|fd
operator|.
name|fields
index|[
name|j
index|]
operator|)
operator|.
name|intValue
argument_list|()
operator|+
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|hq
operator|==
literal|null
condition|)
name|hq
operator|=
operator|new
name|FieldDocSortedHitQueue
argument_list|(
name|docs
operator|.
name|fields
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|totalHits
operator|+=
name|docs
operator|.
name|totalHits
expr_stmt|;
comment|// update totalHits
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|docs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|docs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// merge scoreDocs into hq
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|scoreDoc
operator|.
name|doc
operator|+=
name|starts
index|[
name|i
index|]
expr_stmt|;
comment|// convert doc
if|if
condition|(
name|scoreDoc
operator|==
name|hq
operator|.
name|insertWithOverflow
argument_list|(
operator|(
name|FieldDoc
operator|)
name|scoreDoc
argument_list|)
condition|)
break|break;
comment|// no more scores> minScore
block|}
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|hq
operator|.
name|getFields
argument_list|()
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|start
init|=
name|starts
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Collector
name|hc
init|=
operator|new
name|Collector
argument_list|()
block|{
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
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
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|start
operator|+
name|docBase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|hc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
index|[]
name|queries
init|=
operator|new
name|Query
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queries
index|[
name|i
index|]
operator|=
name|searchables
index|[
name|i
index|]
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
return|return
name|queries
index|[
literal|0
index|]
operator|.
name|combine
argument_list|(
name|queries
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
name|subSearcher
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// find searcher index
return|return
name|searchables
index|[
name|i
index|]
operator|.
name|explain
argument_list|(
name|weight
argument_list|,
name|doc
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to searcher
block|}
comment|/**    * Create weight in multiple index scenario.    *     * Distributed query processing is done in the following steps:    * 1. rewrite query    * 2. extract necessary terms    * 3. collect dfs for these terms from the Searchables    * 4. create query weight using aggregate dfs.    * 5. distribute that weight to Searchables    * 6. merge results    *    * Steps 1-4 are done here, 5+6 in the search() methods    *    * @return rewritten queries    */
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
comment|// step 1
name|Query
name|rewrittenQuery
init|=
name|rewrite
argument_list|(
name|original
argument_list|)
decl_stmt|;
comment|// step 2
name|Set
name|terms
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|rewrittenQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
comment|// step3
name|Term
index|[]
name|allTermsArray
init|=
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|terms
operator|.
name|toArray
argument_list|(
name|allTermsArray
argument_list|)
expr_stmt|;
name|int
index|[]
name|aggregatedDfs
init|=
operator|new
name|int
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
index|[]
name|dfs
init|=
name|searchables
index|[
name|i
index|]
operator|.
name|docFreqs
argument_list|(
name|allTermsArray
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|aggregatedDfs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|aggregatedDfs
index|[
name|j
index|]
operator|+=
name|dfs
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
name|HashMap
name|dfMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allTermsArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dfMap
operator|.
name|put
argument_list|(
name|allTermsArray
index|[
name|i
index|]
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|aggregatedDfs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// step4
name|int
name|numDocs
init|=
name|maxDoc
argument_list|()
decl_stmt|;
name|CachedDfSource
name|cacheSim
init|=
operator|new
name|CachedDfSource
argument_list|(
name|dfMap
argument_list|,
name|numDocs
argument_list|,
name|getSimilarity
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|rewrittenQuery
operator|.
name|weight
argument_list|(
name|cacheSim
argument_list|)
return|;
block|}
block|}
end_class

end_unit

