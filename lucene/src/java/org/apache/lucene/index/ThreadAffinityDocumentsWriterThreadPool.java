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
name|Map
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
name|ConcurrentHashMap
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
comment|/**  * A {@link DocumentsWriterPerThreadPool} implementation that tries to assign an  * indexing thread to the same {@link ThreadState} each time the thread tries to  * obtain a {@link ThreadState}. Once a new {@link ThreadState} is created it is  * associated with the creating thread. Subsequently, if the threads associated  * {@link ThreadState} is not in use it will be associated with the requesting  * thread. Otherwise, if the {@link ThreadState} is used by another thread  * {@link ThreadAffinityDocumentsWriterThreadPool} tries to find the currently  * minimal contended {@link ThreadState}.  */
end_comment

begin_class
DECL|class|ThreadAffinityDocumentsWriterThreadPool
specifier|public
class|class
name|ThreadAffinityDocumentsWriterThreadPool
extends|extends
name|DocumentsWriterPerThreadPool
block|{
DECL|field|threadBindings
specifier|private
name|Map
argument_list|<
name|Thread
argument_list|,
name|ThreadState
argument_list|>
name|threadBindings
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Thread
argument_list|,
name|ThreadState
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ThreadAffinityDocumentsWriterThreadPool
specifier|public
name|ThreadAffinityDocumentsWriterThreadPool
parameter_list|(
name|int
name|maxNumPerThreads
parameter_list|)
block|{
name|super
argument_list|(
name|maxNumPerThreads
argument_list|)
expr_stmt|;
assert|assert
name|getMaxThreadStates
argument_list|()
operator|>=
literal|1
assert|;
block|}
annotation|@
name|Override
DECL|method|getAndLock
specifier|public
name|ThreadState
name|getAndLock
parameter_list|(
name|Thread
name|requestingThread
parameter_list|,
name|DocumentsWriter
name|documentsWriter
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|ThreadState
name|threadState
init|=
name|threadBindings
operator|.
name|get
argument_list|(
name|requestingThread
argument_list|)
decl_stmt|;
if|if
condition|(
name|threadState
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|threadState
operator|.
name|tryLock
argument_list|()
condition|)
block|{
return|return
name|threadState
return|;
block|}
block|}
name|ThreadState
name|minThreadState
init|=
literal|null
decl_stmt|;
comment|/* TODO -- another thread could lock the minThreadState we just got while       we should somehow prevent this. */
comment|// Find the state that has minimum number of threads waiting
name|minThreadState
operator|=
name|minContendedThreadState
argument_list|()
expr_stmt|;
if|if
condition|(
name|minThreadState
operator|==
literal|null
operator|||
name|minThreadState
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
specifier|final
name|ThreadState
name|newState
init|=
name|newThreadState
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|newState
operator|!=
literal|null
condition|)
block|{
assert|assert
name|newState
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
name|threadBindings
operator|.
name|put
argument_list|(
name|requestingThread
argument_list|,
name|newState
argument_list|)
expr_stmt|;
return|return
name|newState
return|;
block|}
elseif|else
if|if
condition|(
name|minThreadState
operator|==
literal|null
condition|)
block|{
comment|/*          * no new threadState available we just take the minContented one          * This must return a valid thread state since we accessed the           * synced context in newThreadState() above.          */
name|minThreadState
operator|=
name|minContendedThreadState
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|minThreadState
operator|!=
literal|null
operator|:
literal|"ThreadState is null"
assert|;
name|minThreadState
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|minThreadState
return|;
block|}
comment|/*   @Override   public void clearThreadBindings(ThreadState perThread) {     threadBindings.clear();   }    @Override   public void clearAllThreadBindings() {     threadBindings.clear();   }   */
block|}
end_class

end_unit

