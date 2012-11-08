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
name|Collection
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
name|IOContext
operator|.
name|Context
import|;
end_import

begin_comment
comment|/**  *   * A {@link Directory} wrapper that allows {@link IndexOutput} rate limiting using  * {@link Context IO context} specific {@link RateLimiter rate limiters}.  *   *  @see #setRateLimiter(RateLimiter, Context)  * @lucene.experimental  */
end_comment

begin_class
DECL|class|RateLimitedDirectoryWrapper
specifier|public
specifier|final
class|class
name|RateLimitedDirectoryWrapper
extends|extends
name|Directory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Directory
name|delegate
decl_stmt|;
comment|// we need to be volatile here to make sure we see all the values that are set
comment|// / modified concurrently
DECL|field|contextRateLimiters
specifier|private
specifier|volatile
name|RateLimiter
index|[]
name|contextRateLimiters
init|=
operator|new
name|RateLimiter
index|[
name|IOContext
operator|.
name|Context
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
DECL|method|RateLimitedDirectoryWrapper
specifier|public
name|RateLimitedDirectoryWrapper
parameter_list|(
name|Directory
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|wrapped
expr_stmt|;
block|}
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|listAll
argument_list|()
return|;
block|}
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexOutput
name|output
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|RateLimiter
name|limiter
init|=
name|getRateLimiter
argument_list|(
name|context
operator|.
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|limiter
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|RateLimitedIndexOutput
argument_list|(
name|limiter
argument_list|,
name|output
argument_list|)
return|;
block|}
return|return
name|output
return|;
block|}
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|createSlicer
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getLockID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RateLimitedDirectoryWrapper("
operator|+
name|delegate
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|getRateLimiter
specifier|private
name|RateLimiter
name|getRateLimiter
parameter_list|(
name|IOContext
operator|.
name|Context
name|context
parameter_list|)
block|{
assert|assert
name|context
operator|!=
literal|null
assert|;
return|return
name|contextRateLimiters
index|[
name|context
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
comment|/**    * Sets the maximum (approx) MB/sec allowed by all write IO performed by    * {@link IndexOutput} created with the given {@link IOContext.Context}. Pass    *<code>null</code> to have no limit.    *     *<p>    *<b>NOTE</b>: For already created {@link IndexOutput} instances there is no    * guarantee this new rate will apply to them; it will only be guaranteed to    * apply for new created {@link IndexOutput} instances.    *<p>    *<b>NOTE</b>: this is an optional operation and might not be respected by    * all Directory implementations. Currently only {@link FSDirectory buffered}    * Directory implementations use rate-limiting.    *     * @throws IllegalArgumentException    *           if context is<code>null</code>    * @throws AlreadyClosedException if the {@link Directory} is already closed    * @lucene.experimental    */
DECL|method|setMaxWriteMBPerSec
specifier|public
name|void
name|setMaxWriteMBPerSec
parameter_list|(
name|Double
name|mbPerSec
parameter_list|,
name|IOContext
operator|.
name|Context
name|context
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Context must not be null"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|ord
init|=
name|context
operator|.
name|ordinal
argument_list|()
decl_stmt|;
specifier|final
name|RateLimiter
name|limiter
init|=
name|contextRateLimiters
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|mbPerSec
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|limiter
operator|!=
literal|null
condition|)
block|{
name|limiter
operator|.
name|setMbPerSec
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|contextRateLimiters
index|[
name|ord
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|limiter
operator|!=
literal|null
condition|)
block|{
name|limiter
operator|.
name|setMbPerSec
argument_list|(
name|mbPerSec
argument_list|)
expr_stmt|;
name|contextRateLimiters
index|[
name|ord
index|]
operator|=
name|limiter
expr_stmt|;
comment|// cross the mem barrier again
block|}
else|else
block|{
name|contextRateLimiters
index|[
name|ord
index|]
operator|=
operator|new
name|RateLimiter
operator|.
name|SimpleRateLimiter
argument_list|(
name|mbPerSec
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Sets the rate limiter to be used to limit (approx) MB/sec allowed by all IO    * performed with the given {@link Context context}. Pass<code>null</code> to    * have no limit.    *     *<p>    * Passing an instance of rate limiter compared to setting it using    * {@link #setMaxWriteMBPerSec(Double, org.apache.lucene.store.IOContext.Context)}    * allows to use the same limiter instance across several directories globally    * limiting IO across them.    *     * @throws IllegalArgumentException    *           if context is<code>null</code>    * @throws AlreadyClosedException if the {@link Directory} is already closed               * @lucene.experimental    */
DECL|method|setRateLimiter
specifier|public
name|void
name|setRateLimiter
parameter_list|(
name|RateLimiter
name|mergeWriteRateLimiter
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Context must not be null"
argument_list|)
throw|;
block|}
name|contextRateLimiters
index|[
name|context
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|mergeWriteRateLimiter
expr_stmt|;
block|}
comment|/**    * See {@link #setMaxWriteMBPerSec}.    *     * @throws IllegalArgumentException    *           if context is<code>null</code>    * @throws AlreadyClosedException if the {@link Directory} is already closed    * @lucene.experimental    */
DECL|method|getMaxWriteMBPerSec
specifier|public
name|Double
name|getMaxWriteMBPerSec
parameter_list|(
name|IOContext
operator|.
name|Context
name|context
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Context must not be null"
argument_list|)
throw|;
block|}
name|RateLimiter
name|limiter
init|=
name|getRateLimiter
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|limiter
operator|==
literal|null
condition|?
literal|null
else|:
name|limiter
operator|.
name|getMbPerSec
argument_list|()
return|;
block|}
block|}
end_class

end_unit

