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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|document
operator|.
name|Document
import|;
end_import

begin_comment
comment|/** An abstract base class for search implementations.  * Implements the main search methods.  *   *<p>Note that you can only access Hits from a Searcher as long as it is  * not yet closed, otherwise an IOException will be thrown.   */
end_comment

begin_class
DECL|class|Searcher
specifier|public
specifier|abstract
class|class
name|Searcher
implements|implements
name|Searchable
block|{
comment|/** Returns the documents matching<code>query</code>.     * @throws BooleanQuery.TooManyClauses    * @deprecated Hits will be removed in Lucene 3.0. Use    * {@link #search(Query, Filter, int)} instead.    */
DECL|method|search
specifier|public
specifier|final
name|Hits
name|search
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|query
argument_list|,
operator|(
name|Filter
operator|)
literal|null
argument_list|)
return|;
block|}
comment|/** Returns the documents matching<code>query</code> and    *<code>filter</code>.    * @throws BooleanQuery.TooManyClauses    * @deprecated Hits will be removed in Lucene 3.0. Use    * {@link #search(Query, Filter, int)} instead.    */
DECL|method|search
specifier|public
name|Hits
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Hits
argument_list|(
name|this
argument_list|,
name|query
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/** Returns documents matching<code>query</code> sorted by    *<code>sort</code>.    * @throws BooleanQuery.TooManyClauses    * @deprecated Hits will be removed in Lucene 3.0. Use     * {@link #search(Query, Filter, int, Sort)} instead.    */
DECL|method|search
specifier|public
name|Hits
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Hits
argument_list|(
name|this
argument_list|,
name|query
argument_list|,
literal|null
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/** Returns documents matching<code>query</code> and<code>filter</code>,    * sorted by<code>sort</code>.    * @throws BooleanQuery.TooManyClauses    * @deprecated Hits will be removed in Lucene 3.0. Use     * {@link #search(Query, Filter, int, Sort)} instead.    */
DECL|method|search
specifier|public
name|Hits
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Hits
argument_list|(
name|this
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/** Search implementation with arbitrary sorting.  Finds    * the top<code>n</code> hits for<code>query</code>, applying    *<code>filter</code> if non-null, and sorting the hits by the criteria in    *<code>sort</code>.    *    *<p>Applications should usually call {@link    * Searcher#search(Query,Filter,Sort)} instead.    * @throws BooleanQuery.TooManyClauses    */
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
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|createWeight
argument_list|(
name|query
argument_list|)
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every matching    * document.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *<p>Note: The<code>score</code> passed to this method is a raw score.    * In other words, the score will not necessarily be a float whose value is    * between 0 and 1.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|search
argument_list|(
name|query
argument_list|,
operator|(
name|Filter
operator|)
literal|null
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every matching    * document.    *<br>HitCollector-based access to remote indexes is discouraged.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query, Filter, int)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param query to match documents    * @param filter if non-null, used to permit documents to be collected.    * @param results to receive hits    * @throws BooleanQuery.TooManyClauses    */
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
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|search
argument_list|(
name|createWeight
argument_list|(
name|query
argument_list|)
argument_list|,
name|filter
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
comment|/** Finds the top<code>n</code>    * hits for<code>query</code>, applying<code>filter</code> if non-null.    *    * @throws BooleanQuery.TooManyClauses    */
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
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|createWeight
argument_list|(
name|query
argument_list|)
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
return|;
block|}
comment|/** Finds the top<code>n</code>    * hits for<code>query</code>.    *    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|n
argument_list|)
return|;
block|}
comment|/** Returns an Explanation that describes how<code>doc</code> scored against    *<code>query</code>.    *    *<p>This is intended to be used in developing Similarity implementations,    * and, for good performance, should not be displayed with every hit.    * Computing an explanation is as expensive as executing the query over the    * entire index.    */
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
name|explain
argument_list|(
name|createWeight
argument_list|(
name|query
argument_list|)
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|/** The Similarity implementation used by this searcher. */
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/** Expert: Set the Similarity implementation used by this Searcher.    *    * @see Similarity#setDefault(Similarity)    */
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Expert: Return the Similarity implementation used by this Searcher.    *    *<p>This defaults to the current value of {@link Similarity#getDefault()}.    */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/**    * creates a weight for<code>query</code>    * @return new weight    */
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Query
name|query
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
return|;
block|}
comment|// inherit javadoc
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
throws|throws
name|IOException
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
comment|/* The following abstract methods were added as a workaround for GCJ bug #15411.    * http://gcc.gnu.org/bugzilla/show_bug.cgi?id=15411    */
DECL|method|search
specifier|abstract
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
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|abstract
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|docFreq
specifier|abstract
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|maxDoc
specifier|abstract
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|search
specifier|abstract
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
throws|throws
name|IOException
function_decl|;
DECL|method|doc
specifier|abstract
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
DECL|method|rewrite
specifier|abstract
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|explain
specifier|abstract
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
function_decl|;
DECL|method|search
specifier|abstract
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
function_decl|;
comment|/* End patch for GCJ bug #15411. */
block|}
end_class

end_unit

