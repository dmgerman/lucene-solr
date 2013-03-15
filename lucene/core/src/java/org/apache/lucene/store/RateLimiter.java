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
comment|/** Abstract base class to rate limit IO.  Typically implementations are  *  shared across multiple IndexInputs or IndexOutputs (for example  *  those involved all merging).  Those IndexInputs and  *  IndexOutputs would call {@link #pause} whenever they  *  want to read bytes or write bytes. */
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
comment|/**    * Simple class to rate limit IO.    */
DECL|class|SimpleRateLimiter
specifier|public
specifier|static
class|class
name|SimpleRateLimiter
extends|extends
name|RateLimiter
block|{
DECL|field|mbPerSec
specifier|private
specifier|volatile
name|double
name|mbPerSec
decl_stmt|;
DECL|field|nsPerByte
specifier|private
specifier|volatile
name|double
name|nsPerByte
decl_stmt|;
DECL|field|lastNS
specifier|private
specifier|volatile
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
name|nsPerByte
operator|=
literal|1000000000.
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|*
name|mbPerSec
operator|)
expr_stmt|;
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
comment|/** Pauses, if necessary, to keep the instantaneous IO      *  rate at or below the target. NOTE: multiple threads      *  may safely use this, however the implementation is      *  not perfectly thread safe but likely in practice this      *  is harmless (just means in some rare cases the rate      *  might exceed the target).  It's best to call this      *  with a biggish count, not one byte at a time.      *  @return the pause time in nano seconds       * */
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
if|if
condition|(
name|bytes
operator|==
literal|1
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// TODO: this is purely instantaneous rate; maybe we
comment|// should also offer decayed recent history one?
specifier|final
name|long
name|targetNS
init|=
name|lastNS
operator|=
name|lastNS
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|bytes
operator|*
name|nsPerByte
argument_list|)
operator|)
decl_stmt|;
specifier|final
name|long
name|startNS
decl_stmt|;
name|long
name|curNS
init|=
name|startNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastNS
operator|<
name|curNS
condition|)
block|{
name|lastNS
operator|=
name|curNS
expr_stmt|;
block|}
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

