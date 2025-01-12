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
name|Closeable
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
name|concurrent
operator|.
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantLock
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
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/** Utility class that runs a thread to manage periodicc  *  reopens of a {@link ReferenceManager}, with methods to wait for a specific  *  index changes to become visible.  When a given search request needs to see a specific  *  index change, call the {#waitForGeneration} to wait for  *  that change to be visible.  Note that this will only  *  scale well if most searches do not need to wait for a  *  specific index generation.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|ControlledRealTimeReopenThread
specifier|public
class|class
name|ControlledRealTimeReopenThread
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Thread
implements|implements
name|Closeable
block|{
DECL|field|manager
specifier|private
specifier|final
name|ReferenceManager
argument_list|<
name|T
argument_list|>
name|manager
decl_stmt|;
DECL|field|targetMaxStaleNS
specifier|private
specifier|final
name|long
name|targetMaxStaleNS
decl_stmt|;
DECL|field|targetMinStaleNS
specifier|private
specifier|final
name|long
name|targetMinStaleNS
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|finish
specifier|private
specifier|volatile
name|boolean
name|finish
decl_stmt|;
DECL|field|waitingGen
specifier|private
specifier|volatile
name|long
name|waitingGen
decl_stmt|;
DECL|field|searchingGen
specifier|private
specifier|volatile
name|long
name|searchingGen
decl_stmt|;
DECL|field|refreshStartGen
specifier|private
name|long
name|refreshStartGen
decl_stmt|;
DECL|field|reopenLock
specifier|private
specifier|final
name|ReentrantLock
name|reopenLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|reopenCond
specifier|private
specifier|final
name|Condition
name|reopenCond
init|=
name|reopenLock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * Create ControlledRealTimeReopenThread, to periodically    * reopen the a {@link ReferenceManager}.    *    * @param targetMaxStaleSec Maximum time until a new    *        reader must be opened; this sets the upper bound    *        on how slowly reopens may occur, when no    *        caller is waiting for a specific generation to    *        become visible.    *    * @param targetMinStaleSec Mininum time until a new    *        reader can be opened; this sets the lower bound    *        on how quickly reopens may occur, when a caller    *        is waiting for a specific generation to    *        become visible.    */
DECL|method|ControlledRealTimeReopenThread
specifier|public
name|ControlledRealTimeReopenThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|ReferenceManager
argument_list|<
name|T
argument_list|>
name|manager
parameter_list|,
name|double
name|targetMaxStaleSec
parameter_list|,
name|double
name|targetMinStaleSec
parameter_list|)
block|{
if|if
condition|(
name|targetMaxStaleSec
operator|<
name|targetMinStaleSec
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"targetMaxScaleSec (= "
operator|+
name|targetMaxStaleSec
operator|+
literal|")< targetMinStaleSec (="
operator|+
name|targetMinStaleSec
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|targetMaxStaleNS
operator|=
call|(
name|long
call|)
argument_list|(
literal|1000000000
operator|*
name|targetMaxStaleSec
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetMinStaleNS
operator|=
call|(
name|long
call|)
argument_list|(
literal|1000000000
operator|*
name|targetMinStaleSec
argument_list|)
expr_stmt|;
name|manager
operator|.
name|addListener
argument_list|(
operator|new
name|HandleRefresh
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|HandleRefresh
specifier|private
class|class
name|HandleRefresh
implements|implements
name|ReferenceManager
operator|.
name|RefreshListener
block|{
annotation|@
name|Override
DECL|method|beforeRefresh
specifier|public
name|void
name|beforeRefresh
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|afterRefresh
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
block|{
name|refreshDone
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|refreshDone
specifier|private
specifier|synchronized
name|void
name|refreshDone
parameter_list|()
block|{
name|searchingGen
operator|=
name|refreshStartGen
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
comment|//System.out.println("NRT: set finish");
name|finish
operator|=
literal|true
expr_stmt|;
comment|// So thread wakes up and notices it should finish:
name|reopenLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|reopenCond
operator|.
name|signal
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
comment|// Max it out so any waiting search threads will return:
name|searchingGen
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/**    * Waits for the target generation to become visible in    * the searcher.    * If the current searcher is older than the    * target generation, this method will block    * until the searcher is reopened, by another via    * {@link ReferenceManager#maybeRefresh} or until the {@link ReferenceManager} is closed.    *     * @param targetGen the generation to wait for    */
DECL|method|waitForGeneration
specifier|public
name|void
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|waitForGeneration
argument_list|(
name|targetGen
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Waits for the target generation to become visible in    * the searcher, up to a maximum specified milli-seconds.    * If the current searcher is older than the target    * generation, this method will block until the    * searcher has been reopened by another thread via    * {@link ReferenceManager#maybeRefresh}, the given waiting time has elapsed, or until    * the {@link ReferenceManager} is closed.    *<p>    * NOTE: if the waiting time elapses before the requested target generation is    * available the current {@link SearcherManager} is returned instead.    *     * @param targetGen    *          the generation to wait for    * @param maxMS    *          maximum milliseconds to wait, or -1 to wait indefinitely    * @return true if the targetGeneration is now available,    *         or false if maxMS wait time was exceeded    */
DECL|method|waitForGeneration
specifier|public
specifier|synchronized
name|boolean
name|waitForGeneration
parameter_list|(
name|long
name|targetGen
parameter_list|,
name|int
name|maxMS
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|targetGen
operator|>
name|searchingGen
condition|)
block|{
comment|// Notify the reopen thread that the waitingGen has
comment|// changed, so it may wake up and realize it should
comment|// not sleep for much or any longer before reopening:
name|reopenLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// Need to find waitingGen inside lock as it's used to determine
comment|// stale time
name|waitingGen
operator|=
name|Math
operator|.
name|max
argument_list|(
name|waitingGen
argument_list|,
name|targetGen
argument_list|)
expr_stmt|;
try|try
block|{
name|reopenCond
operator|.
name|signal
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|long
name|startMS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
decl_stmt|;
while|while
condition|(
name|targetGen
operator|>
name|searchingGen
condition|)
block|{
if|if
condition|(
name|maxMS
operator|<
literal|0
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|long
name|msLeft
init|=
operator|(
name|startMS
operator|+
name|maxMS
operator|)
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
decl_stmt|;
if|if
condition|(
name|msLeft
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|wait
argument_list|(
name|msLeft
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// TODO: maybe use private thread ticktock timer, in
comment|// case clock shift messes up nanoTime?
name|long
name|lastReopenStartNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|//System.out.println("reopen: start");
while|while
condition|(
operator|!
name|finish
condition|)
block|{
comment|// TODO: try to guestimate how long reopen might
comment|// take based on past data?
comment|// Loop until we've waiting long enough before the
comment|// next reopen:
while|while
condition|(
operator|!
name|finish
condition|)
block|{
comment|// Need lock before finding out if has waiting
name|reopenLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// True if we have someone waiting for reopened searcher:
name|boolean
name|hasWaiting
init|=
name|waitingGen
operator|>
name|searchingGen
decl_stmt|;
specifier|final
name|long
name|nextReopenStartNS
init|=
name|lastReopenStartNS
operator|+
operator|(
name|hasWaiting
condition|?
name|targetMinStaleNS
else|:
name|targetMaxStaleNS
operator|)
decl_stmt|;
specifier|final
name|long
name|sleepNS
init|=
name|nextReopenStartNS
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|sleepNS
operator|>
literal|0
condition|)
block|{
name|reopenCond
operator|.
name|awaitNanos
argument_list|(
name|sleepNS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|finish
condition|)
block|{
break|break;
block|}
name|lastReopenStartNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
comment|// Save the gen as of when we started the reopen; the
comment|// listener (HandleRefresh above) copies this to
comment|// searchingGen once the reopen completes:
name|refreshStartGen
operator|=
name|writer
operator|.
name|getMaxCompletedSequenceNumber
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|maybeRefreshBlocking
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Returns which {@code generation} the current searcher is guaranteed to include. */
DECL|method|getSearchingGen
specifier|public
name|long
name|getSearchingGen
parameter_list|()
block|{
return|return
name|searchingGen
return|;
block|}
block|}
end_class

end_unit

