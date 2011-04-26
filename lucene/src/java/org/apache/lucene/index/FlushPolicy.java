begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
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
name|util
operator|.
name|SetOnce
import|;
end_import

begin_comment
comment|/**  * {@link FlushPolicy} controls when segments are flushed from a RAM resident  * internal data-structure to the {@link IndexWriter}s {@link Directory}.  *<p>  * Segments are traditionally flushed by:  *<ul>  *<li>RAM consumption - configured via  * {@link IndexWriterConfig#setRAMBufferSizeMB(double)}</li>  *<li>Number of RAM resident documents - configured via  * {@link IndexWriterConfig#setMaxBufferedDocs(int)}</li>  *<li>Number of buffered delete terms/queries - configured via  * {@link IndexWriterConfig#setMaxBufferedDeleteTerms(int)}</li>  *</ul>  *   * The {@link IndexWriter} consults a provided {@link FlushPolicy} to control the  * flushing process. The policy is informed for each added or  * updated document as well as for each delete term. Based on the  * {@link FlushPolicy}, the information provided via {@link ThreadState} and  * {@link DocumentsWriterFlushControl}, the {@link FlushPolicy} decides if a  * {@link DocumentsWriterPerThread} needs flushing and mark it as  * flush-pending via  * {@link DocumentsWriterFlushControl#setFlushPending(ThreadState)}.  *   * @see ThreadState  * @see DocumentsWriterFlushControl  * @see DocumentsWriterPerThread  * @see IndexWriterConfig#setFlushPolicy(FlushPolicy)  */
end_comment

begin_class
DECL|class|FlushPolicy
specifier|public
specifier|abstract
class|class
name|FlushPolicy
block|{
DECL|field|writer
specifier|protected
specifier|final
name|SetOnce
argument_list|<
name|DocumentsWriter
argument_list|>
name|writer
init|=
operator|new
name|SetOnce
argument_list|<
name|DocumentsWriter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|indexWriterConfig
specifier|protected
name|IndexWriterConfig
name|indexWriterConfig
decl_stmt|;
comment|/**    * Called for each delete term. If this is a delete triggered due to an update    * the given {@link ThreadState} is non-null.    *<p>    * Note: This method is called synchronized on the given    * {@link DocumentsWriterFlushControl} and it is guaranteed that the calling    * thread holds the lock on the given {@link ThreadState}    */
DECL|method|onDelete
specifier|public
specifier|abstract
name|void
name|onDelete
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|state
parameter_list|)
function_decl|;
comment|/**    * Called for each document update on the given {@link ThreadState}'s    * {@link DocumentsWriterPerThread}.    *<p>    * Note: This method is called  synchronized on the given    * {@link DocumentsWriterFlushControl} and it is guaranteed that the calling    * thread holds the lock on the given {@link ThreadState}    */
DECL|method|onUpdate
specifier|public
name|void
name|onUpdate
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|state
parameter_list|)
block|{
name|onInsert
argument_list|(
name|control
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|flushPending
condition|)
block|{
name|onDelete
argument_list|(
name|control
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Called for each document addition on the given {@link ThreadState}s    * {@link DocumentsWriterPerThread}.    *<p>    * Note: This method is synchronized by the given    * {@link DocumentsWriterFlushControl} and it is guaranteed that the calling    * thread holds the lock on the given {@link ThreadState}    */
DECL|method|onInsert
specifier|public
specifier|abstract
name|void
name|onInsert
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|state
parameter_list|)
function_decl|;
comment|/**    * Called by {@link DocumentsWriter} to initialize the FlushPolicy    */
DECL|method|init
specifier|protected
specifier|synchronized
name|void
name|init
parameter_list|(
name|DocumentsWriter
name|docsWriter
parameter_list|)
block|{
name|writer
operator|.
name|set
argument_list|(
name|docsWriter
argument_list|)
expr_stmt|;
name|indexWriterConfig
operator|=
name|docsWriter
operator|.
name|indexWriter
operator|.
name|getConfig
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the current most RAM consuming non-pending {@link ThreadState} with    * at least one indexed document.    *<p>    * This method will never return<code>null</code>    */
DECL|method|findLargestNonPendingWriter
specifier|protected
name|ThreadState
name|findLargestNonPendingWriter
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|perThreadState
parameter_list|)
block|{
assert|assert
name|perThreadState
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>
literal|0
assert|;
name|long
name|maxRamSoFar
init|=
name|perThreadState
operator|.
name|bytesUsed
decl_stmt|;
comment|// the dwpt which needs to be flushed eventually
name|ThreadState
name|maxRamUsingThreadState
init|=
name|perThreadState
decl_stmt|;
assert|assert
operator|!
name|perThreadState
operator|.
name|flushPending
operator|:
literal|"DWPT should have flushed"
assert|;
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|activePerThreadsIterator
init|=
name|control
operator|.
name|allActiveThreads
argument_list|()
decl_stmt|;
while|while
condition|(
name|activePerThreadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|next
init|=
name|activePerThreadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|next
operator|.
name|flushPending
condition|)
block|{
specifier|final
name|long
name|nextRam
init|=
name|next
operator|.
name|bytesUsed
decl_stmt|;
if|if
condition|(
name|nextRam
operator|>
name|maxRamSoFar
operator|&&
name|next
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>
literal|0
condition|)
block|{
name|maxRamSoFar
operator|=
name|nextRam
expr_stmt|;
name|maxRamUsingThreadState
operator|=
name|next
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|writer
operator|.
name|get
argument_list|()
operator|.
name|message
argument_list|(
literal|"set largest ram consuming thread pending on lower watermark"
argument_list|)
assert|;
return|return
name|maxRamUsingThreadState
return|;
block|}
block|}
end_class

end_unit

