begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_comment
comment|// javadocs
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
name|IndexWriter
import|;
end_import

begin_comment
comment|// javadocs
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
name|IndexWriterConfig
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Factory class used by {@link SearcherManager} to  * create new IndexSearchers. The default implementation just creates   * an IndexSearcher with no custom behavior:  *   *<pre class="prettyprint">  *   public IndexSearcher newSearcher(IndexReader r) throws IOException {  *     return new IndexSearcher(r);  *   }  *</pre>  *   * You can pass your own factory instead if you want custom behavior, such as:  *<ul>  *<li>Setting a custom scoring model: {@link IndexSearcher#setSimilarity(Similarity)}  *<li>Parallel per-segment search: {@link IndexSearcher#IndexSearcher(IndexReader, ExecutorService)}  *<li>Return custom subclasses of IndexSearcher (for example that implement distributed scoring)  *<li>Run queries to warm your IndexSearcher before it is used. Note: when using near-realtime search  *       you may want to also {@link IndexWriterConfig#setMergedSegmentWarmer(IndexWriter.IndexReaderWarmer)} to warm  *       newly merged segments in the background, outside of the reopen path.  *</ul>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SearcherFactory
specifier|public
class|class
name|SearcherFactory
block|{
comment|/**     * Returns a new IndexSearcher over the given reader.    * @param reader the reader to create a new searcher for    * @param previousReader the reader previously used to create a new searcher.    *                       This can be<code>null</code> if unknown or if the given reader is the initially opened reader.    *                       If this reader is non-null it can be used to find newly opened segments compared to the new reader to warm    *                       the searcher up before returning.    */
DECL|method|newSearcher
specifier|public
name|IndexSearcher
name|newSearcher
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|IndexReader
name|previousReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
end_class

end_unit

