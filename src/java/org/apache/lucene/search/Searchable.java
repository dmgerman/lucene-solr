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

begin_comment
comment|// for javadoc
end_comment

begin_comment
comment|/** The interface for search implementations.  *  *<p>Implementations provide search over a single index, over multiple  * indices, and over indices on remote servers.  */
end_comment

begin_interface
DECL|interface|Searchable
specifier|public
interface|interface
name|Searchable
extends|extends
name|java
operator|.
name|rmi
operator|.
name|Remote
block|{
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every non-zero    * scoring document.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param query to match documents    * @param filter if non-null, a bitset used to eliminate some documents    * @param results to receive hits    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
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
function_decl|;
comment|/** Frees resources associated with this Searcher.    * Be careful not to call this method while you are still using objects    * like {@link Hits}.    */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns the number of documents containing<code>term</code>.    * Called by search code to compute term weights.    * @see IndexReader#docFreq(Term)    */
DECL|method|docFreq
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Returns one greater than the largest possible document number.    * Called by search code to compute term weights.    * @see IndexReader#maxDoc()    */
DECL|method|maxDoc
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Expert: Low-level search implementation.  Finds the top<code>n</code>    * hits for<code>query</code>, applying<code>filter</code> if non-null.    *    *<p>Called by {@link Hits}.    *    *<p>Applications should usually call {@link Searcher#search(Query)} or    * {@link Searcher#search(Query,Filter)} instead.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
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
function_decl|;
comment|/** Expert: Returns the stored fields of document<code>i</code>.    * Called by {@link HitCollector} implementations.    * @see IndexReader#document(int)    */
DECL|method|doc
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Expert: called to re-write queries into primitive queries.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|rewrite
name|Query
name|rewrite
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns an Explanation that describes how<code>doc</code> scored against    *<code>query</code>.    *    *<p>This is intended to be used in developing Similarity implementations,    * and, for good performance, should not be displayed with every hit.    * Computing an explanation is as expensive as executing the query over the    * entire index.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|explain
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
function_decl|;
comment|/** Expert: Low-level search implementation with arbitrary sorting.  Finds    * the top<code>n</code> hits for<code>query</code>, applying    *<code>filter</code> if non-null, and sorting the hits by the criteria in    *<code>sort</code>.    *    *<p>Applications should usually call {@link    * Searcher#search(Query,Filter,Sort)} instead.    * @throws BooleanQuery.TooManyClauses    */
DECL|method|search
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
function_decl|;
block|}
end_interface

end_unit

