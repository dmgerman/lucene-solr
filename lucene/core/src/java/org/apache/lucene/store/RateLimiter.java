begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/** Abstract base class to rate limit IO.  Typically implementations are  *  shared across multiple IndexInputs or IndexOutputs (for example  *  those involved all merging).  Those IndexInputs and  *  IndexOutputs would call {@link #pause} whenever the have read  *  or written more than {@link #getMinPauseCheckBytes} bytes. */
end_comment

begin_class
DECL|class|RateLimiter
specifier|public
specifier|abstract
class|class
name|RateLimiter
block|{
comment|/**    * Sets an updated mb per second rate limit.    */
DECL|method|setMbPerSec
specifier|public
specifier|abstract
name|void
name|setMbPerSec
parameter_list|(
name|double
name|mbPerSec
parameter_list|)
function_decl|;
comment|/**    * The current mb per second rate limit.    */
DECL|method|getMbPerSec
specifier|public
specifier|abstract
name|double
name|getMbPerSec
parameter_list|()
function_decl|;
comment|/** Pauses, if necessary, to keep the instantaneous IO    *  rate at or below the target.     *<p>    *  Note: the implementation is thread-safe    *</p>    *  @return the pause time in nano seconds     * */
DECL|method|pause
specifier|public
specifier|abstract
name|long
name|pause
parameter_list|(
name|long
name|bytes
parameter_list|)
function_decl|;
comment|/** How many bytes caller should add up itself before invoking {@link #pause}. */
DECL|method|getMinPauseCheckBytes
specifier|public
specifier|abstract
name|long
name|getMinPauseCheckBytes
parameter_list|()
function_decl|;
comment|/**    * Simple class to rate limit IO.    */
DECL|class|SimpleRateLimiter
specifier|public
specifier|static
class|class
name|SimpleRateLimiter
extends|extends
name|RateLimiter
block|{
DECL|field|MIN_PAUSE_CHECK_MSEC
specifier|private
specifier|final
specifier|static
name|int
name|MIN_PAUSE_CHECK_MSEC
init|=
literal|5
decl_stmt|;
DECL|field|mbPerSec
specifier|private
specifier|volatile
name|double
name|mbPerSec
decl_stmt|;
DECL|field|minPauseCheckBytes
specifier|private
specifier|volatile
name|long
name|minPauseCheckBytes
decl_stmt|;
DECL|field|lastNS
specifier|private
name|long
name|lastNS
decl_stmt|;
comment|// TODO: we could also allow eg a sub class to dynamically
comment|// determine the allowed rate, eg if an app wants to
comment|// change the allowed rate over time or something
comment|/** mbPerSec is the MB/sec max IO rate */
DECL|method|SimpleRateLimiter
specifier|public
name|SimpleRateLimiter
parameter_list|(
name|double
name|mbPerSec
parameter_list|)
block|{
name|setMbPerSec
argument_list|(
name|mbPerSec
argument_list|)
expr_stmt|;
name|lastNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets an updated mb per second rate limit.      */
annotation|@
name|Override
DECL|method|setMbPerSec
specifier|public
name|void
name|setMbPerSec
parameter_list|(
name|double
name|mbPerSec
parameter_list|)
block|{
name|this
operator|.
name|mbPerSec
operator|=
name|mbPerSec
expr_stmt|;
name|minPauseCheckBytes
operator|=
call|(
name|long
call|)
argument_list|(
operator|(
name|MIN_PAUSE_CHECK_MSEC
operator|/
literal|1000.0
operator|)
operator|*
name|mbPerSec
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinPauseCheckBytes
specifier|public
name|long
name|getMinPauseCheckBytes
parameter_list|()
block|{
return|return
name|minPauseCheckBytes
return|;
block|}
comment|/**      * The current mb per second rate limit.      */
annotation|@
name|Override
DECL|method|getMbPerSec
specifier|public
name|double
name|getMbPerSec
parameter_list|()
block|{
return|return
name|this
operator|.
name|mbPerSec
return|;
block|}
comment|/** Pauses, if necessary, to keep the instantaneous IO      *  rate at or below the target.  Be sure to only call      *  this method when bytes> {@link #getMinPauseCheckBytes},      *  otherwise it will pause way too long!      *      *  @return the pause time in nano seconds */
annotation|@
name|Override
DECL|method|pause
specifier|public
name|long
name|pause
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|long
name|startNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|double
name|secondsToPause
init|=
operator|(
name|bytes
operator|/
literal|1024.
operator|/
literal|1024.
operator|)
operator|/
name|mbPerSec
decl_stmt|;
name|long
name|targetNS
decl_stmt|;
comment|// Sync'd to read + write lastNS:
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Time we should sleep until; this is purely instantaneous
comment|// rate (just adds seconds onto the last time we had paused to);
comment|// maybe we should also offer decayed recent history one?
name|targetNS
operator|=
name|lastNS
operator|+
call|(
name|long
call|)
argument_list|(
literal|1000000000
operator|*
name|secondsToPause
argument_list|)
expr_stmt|;
if|if
condition|(
name|startNS
operator|>=
name|targetNS
condition|)
block|{
comment|// OK, current time is already beyond the target sleep time,
comment|// no pausing to do.
comment|// Set to startNS, not targetNS, to enforce the instant rate, not
comment|// the "averaaged over all history" rate:
name|lastNS
operator|=
name|startNS
expr_stmt|;
return|return
literal|0
return|;
block|}
name|lastNS
operator|=
name|targetNS
expr_stmt|;
block|}
name|long
name|curNS
init|=
name|startNS
decl_stmt|;
comment|// While loop because Thread.sleep doesn't always sleep
comment|// enough:
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|long
name|pauseNS
init|=
name|targetNS
operator|-
name|curNS
decl_stmt|;
if|if
condition|(
name|pauseNS
operator|>
literal|0
condition|)
block|{
try|try
block|{
comment|// NOTE: except maybe on real-time JVMs, minimum realistic sleep time
comment|// is 1 msec; if you pass just 1 nsec the default impl rounds
comment|// this up to 1 msec:
name|Thread
operator|.
name|sleep
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pauseNS
operator|/
literal|1000000
argument_list|)
argument_list|,
call|(
name|int
call|)
argument_list|(
name|pauseNS
operator|%
literal|1000000
argument_list|)
argument_list|)
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
name|curNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
continue|continue;
block|}
break|break;
block|}
return|return
name|curNS
operator|-
name|startNS
return|;
block|}
block|}
block|}
end_class

end_unit

