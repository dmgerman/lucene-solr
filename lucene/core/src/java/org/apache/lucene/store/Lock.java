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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** An interprocess mutex lock.  *<p>Typical use might look like:<pre>  * new Lock.With(directory.makeLock("my.lock")) {  *     public Object doBody() {  *<i>... code to execute while locked ...</i>  *     }  *   }.run();  *</pre>  *  * @see Directory#makeLock(String)  */
end_comment

begin_class
DECL|class|Lock
specifier|public
specifier|abstract
class|class
name|Lock
block|{
comment|/** How long {@link #obtain(long)} waits, in milliseconds,    *  in between attempts to acquire the lock. */
DECL|field|LOCK_POLL_INTERVAL
specifier|public
specifier|static
name|long
name|LOCK_POLL_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|/** Pass this value to {@link #obtain(long)} to try    *  forever to obtain the lock. */
DECL|field|LOCK_OBTAIN_WAIT_FOREVER
specifier|public
specifier|static
specifier|final
name|long
name|LOCK_OBTAIN_WAIT_FOREVER
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Attempts to obtain exclusive access and immediately return    *  upon success or failure.    * @return true iff exclusive access is obtained    */
DECL|method|obtain
specifier|public
specifier|abstract
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * If a lock obtain called, this failureReason may be set    * with the "root cause" Exception as to why the lock was    * not obtained.    */
DECL|field|failureReason
specifier|protected
name|Throwable
name|failureReason
decl_stmt|;
comment|/** Attempts to obtain an exclusive lock within amount of    *  time given. Polls once per {@link #LOCK_POLL_INTERVAL}    *  (currently 1000) milliseconds until lockWaitTimeout is    *  passed.    * @param lockWaitTimeout length of time to wait in    *        milliseconds or {@link    *        #LOCK_OBTAIN_WAIT_FOREVER} to retry forever    * @return true if lock was obtained    * @throws LockObtainFailedException if lock wait times out    * @throws IllegalArgumentException if lockWaitTimeout is    *         out of bounds    * @throws IOException if obtain() throws IOException    */
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|(
name|long
name|lockWaitTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|failureReason
operator|=
literal|null
expr_stmt|;
name|boolean
name|locked
init|=
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|lockWaitTimeout
operator|<
literal|0
operator|&&
name|lockWaitTimeout
operator|!=
name|LOCK_OBTAIN_WAIT_FOREVER
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lockWaitTimeout should be LOCK_OBTAIN_WAIT_FOREVER or a non-negative number (got "
operator|+
name|lockWaitTimeout
operator|+
literal|")"
argument_list|)
throw|;
name|long
name|maxSleepCount
init|=
name|lockWaitTimeout
operator|/
name|LOCK_POLL_INTERVAL
decl_stmt|;
name|long
name|sleepCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|locked
condition|)
block|{
if|if
condition|(
name|lockWaitTimeout
operator|!=
name|LOCK_OBTAIN_WAIT_FOREVER
operator|&&
name|sleepCount
operator|++
operator|>=
name|maxSleepCount
condition|)
block|{
name|String
name|reason
init|=
literal|"Lock obtain timed out: "
operator|+
name|this
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|failureReason
operator|!=
literal|null
condition|)
block|{
name|reason
operator|+=
literal|": "
operator|+
name|failureReason
expr_stmt|;
block|}
name|LockObtainFailedException
name|e
init|=
operator|new
name|LockObtainFailedException
argument_list|(
name|reason
argument_list|)
decl_stmt|;
if|if
condition|(
name|failureReason
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|initCause
argument_list|(
name|failureReason
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|LOCK_POLL_INTERVAL
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
name|locked
operator|=
name|obtain
argument_list|()
expr_stmt|;
block|}
return|return
name|locked
return|;
block|}
comment|/** Releases exclusive access. */
DECL|method|release
specifier|public
specifier|abstract
name|void
name|release
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true if the resource is currently locked.  Note that one must    * still call {@link #obtain()} before using the resource. */
DECL|method|isLocked
specifier|public
specifier|abstract
name|boolean
name|isLocked
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Utility class for executing code with exclusive access. */
DECL|class|With
specifier|public
specifier|abstract
specifier|static
class|class
name|With
block|{
DECL|field|lock
specifier|private
name|Lock
name|lock
decl_stmt|;
DECL|field|lockWaitTimeout
specifier|private
name|long
name|lockWaitTimeout
decl_stmt|;
comment|/** Constructs an executor that will grab the named lock. */
DECL|method|With
specifier|public
name|With
parameter_list|(
name|Lock
name|lock
parameter_list|,
name|long
name|lockWaitTimeout
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|lockWaitTimeout
operator|=
name|lockWaitTimeout
expr_stmt|;
block|}
comment|/** Code to execute with exclusive access. */
DECL|method|doBody
specifier|protected
specifier|abstract
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Calls {@link #doBody} while<i>lock</i> is obtained.  Blocks if lock      * cannot be obtained immediately.  Retries to obtain lock once per second      * until it is obtained, or until it has tried ten times. Lock is released when      * {@link #doBody} exits.      * @throws LockObtainFailedException if lock could not      * be obtained      * @throws IOException if {@link Lock#obtain} throws IOException      */
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|locked
init|=
literal|false
decl_stmt|;
try|try
block|{
name|locked
operator|=
name|lock
operator|.
name|obtain
argument_list|(
name|lockWaitTimeout
argument_list|)
expr_stmt|;
return|return
name|doBody
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|locked
condition|)
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

