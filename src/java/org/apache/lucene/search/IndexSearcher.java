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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BitSet
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
name|store
operator|.
name|Directory
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

begin_comment
comment|/** Implements search over a single IndexReader.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods. For performance reasons it is   * recommended to open only one IndexSearcher and use it for all of your searches.  */
end_comment

begin_class
DECL|class|IndexSearcher
specifier|public
class|class
name|IndexSearcher
extends|extends
name|Searcher
block|{
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|closeReader
specifier|private
name|boolean
name|closeReader
decl_stmt|;
comment|/** Creates a searcher searching the index in the named directory. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the provided directory. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the provided index. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|this
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexSearcher
specifier|private
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|boolean
name|closeReader
parameter_list|)
block|{
name|reader
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|closeReader
operator|=
name|closeReader
expr_stmt|;
block|}
comment|/** Return the {@link IndexReader} this searches. */
DECL|method|getIndexReader
specifier|public
name|IndexReader
name|getIndexReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
comment|/**    * Note that the underlying IndexReader is not closed, if    * IndexSearcher was constructed with IndexSearcher(IndexReader r).    * If the IndexReader was supplied implicitly by specifying a directory, then    * the IndexReader gets closed.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closeReader
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// inherit javadoc
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
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nDocs
operator|<=
literal|0
condition|)
comment|// null might be returned from hq.top() below.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"nDocs must be> 0"
argument_list|)
throw|;
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return
operator|new
name|TopDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|)
return|;
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|!=
literal|null
condition|?
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|totalHits
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|scorer
operator|.
name|score
argument_list|(
operator|new
name|HitCollector
argument_list|()
block|{
specifier|private
name|float
name|minScore
init|=
literal|0.0f
decl_stmt|;
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|score
operator|>
literal|0.0f
operator|&&
comment|// ignore zeroed buckets
operator|(
name|bits
operator|==
literal|null
operator|||
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
block|{
comment|// skip docs not in bits
name|totalHits
index|[
literal|0
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|hq
operator|.
name|size
argument_list|()
operator|<
name|nDocs
operator|||
name|score
operator|>=
name|minScore
condition|)
block|{
name|hq
operator|.
name|insert
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|minScore
operator|=
operator|(
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|score
expr_stmt|;
comment|// maintain minScore
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
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
name|TopDocs
argument_list|(
name|totalHits
index|[
literal|0
index|]
argument_list|,
name|scoreDocs
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return
operator|new
name|TopFieldDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|,
name|sort
operator|.
name|fields
argument_list|)
return|;
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|!=
literal|null
condition|?
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|FieldSortedHitQueue
name|hq
init|=
operator|new
name|FieldSortedHitQueue
argument_list|(
name|reader
argument_list|,
name|sort
operator|.
name|fields
argument_list|,
name|nDocs
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|totalHits
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|scorer
operator|.
name|score
argument_list|(
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|score
operator|>
literal|0.0f
operator|&&
comment|// ignore zeroed buckets
operator|(
name|bits
operator|==
literal|null
operator|||
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
block|{
comment|// skip docs not in bits
name|totalHits
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|hq
operator|.
name|insert
argument_list|(
operator|new
name|FieldDoc
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
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
name|hq
operator|.
name|fillFields
argument_list|(
operator|(
name|FieldDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|totalHits
index|[
literal|0
index|]
argument_list|,
name|scoreDocs
argument_list|,
name|hq
operator|.
name|getFields
argument_list|()
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|HitCollector
name|collector
init|=
name|results
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|collector
operator|=
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// skip docs not in bits
name|results
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return;
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
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
name|query
init|=
name|original
decl_stmt|;
for|for
control|(
name|Query
name|rewrittenQuery
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
init|;
name|rewrittenQuery
operator|!=
name|query
condition|;
name|rewrittenQuery
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
control|)
block|{
name|query
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

