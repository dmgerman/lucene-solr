begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicInteger
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
name|AlreadyClosedException
import|;
end_import

begin_comment
comment|/**  * A {@link Replicator} implementation for use by the side that publishes  * {@link Revision}s, as well for clients to {@link #checkForUpdate(String)  * check for updates}. When a client needs to be updated, it is returned a  * {@link SessionToken} through which it can  * {@link #obtainFile(String, String, String) obtain} the files of that  * revision. As long as a revision is being replicated, this replicator  * guarantees that it will not be {@link Revision#release() released}.  *<p>  * Replication sessions expire by default after  * {@link #DEFAULT_SESSION_EXPIRATION_THRESHOLD}, and the threshold can be  * configured through {@link #setExpirationThreshold(long)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|LocalReplicator
specifier|public
class|class
name|LocalReplicator
implements|implements
name|Replicator
block|{
DECL|class|RefCountedRevision
specifier|private
specifier|static
class|class
name|RefCountedRevision
block|{
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|revision
specifier|public
specifier|final
name|Revision
name|revision
decl_stmt|;
DECL|method|RefCountedRevision
specifier|public
name|RefCountedRevision
parameter_list|(
name|Revision
name|revision
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
block|}
DECL|method|decRef
specifier|public
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|refCount
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this revision is already released"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|0
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|revision
operator|.
name|release
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// Put reference back on failure
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|rc
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"too many decRef calls: refCount is "
operator|+
name|rc
operator|+
literal|" after decrement"
argument_list|)
throw|;
block|}
block|}
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|()
block|{
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ReplicationSession
specifier|private
specifier|static
class|class
name|ReplicationSession
block|{
DECL|field|session
specifier|public
specifier|final
name|SessionToken
name|session
decl_stmt|;
DECL|field|revision
specifier|public
specifier|final
name|RefCountedRevision
name|revision
decl_stmt|;
DECL|field|lastAccessTime
specifier|private
specifier|volatile
name|long
name|lastAccessTime
decl_stmt|;
DECL|method|ReplicationSession
name|ReplicationSession
parameter_list|(
name|SessionToken
name|session
parameter_list|,
name|RefCountedRevision
name|revision
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|lastAccessTime
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|isExpired
name|boolean
name|isExpired
parameter_list|(
name|long
name|expirationThreshold
parameter_list|)
block|{
return|return
name|lastAccessTime
operator|<
operator|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|-
name|expirationThreshold
operator|)
return|;
block|}
DECL|method|markAccessed
name|void
name|markAccessed
parameter_list|()
block|{
name|lastAccessTime
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Threshold for expiring inactive sessions. Defaults to 30 minutes. */
DECL|field|DEFAULT_SESSION_EXPIRATION_THRESHOLD
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_SESSION_EXPIRATION_THRESHOLD
init|=
literal|1000
operator|*
literal|60
operator|*
literal|30
decl_stmt|;
DECL|field|expirationThresholdMilllis
specifier|private
name|long
name|expirationThresholdMilllis
init|=
name|LocalReplicator
operator|.
name|DEFAULT_SESSION_EXPIRATION_THRESHOLD
decl_stmt|;
DECL|field|currentRevision
specifier|private
specifier|volatile
name|RefCountedRevision
name|currentRevision
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|sessionToken
specifier|private
specifier|final
name|AtomicInteger
name|sessionToken
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|sessions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ReplicationSession
argument_list|>
name|sessions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|checkExpiredSessions
specifier|private
name|void
name|checkExpiredSessions
parameter_list|()
throws|throws
name|IOException
block|{
comment|// make a "to-delete" list so we don't risk deleting from the map while iterating it
specifier|final
name|ArrayList
argument_list|<
name|ReplicationSession
argument_list|>
name|toExpire
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ReplicationSession
name|token
range|:
name|sessions
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|token
operator|.
name|isExpired
argument_list|(
name|expirationThresholdMilllis
argument_list|)
condition|)
block|{
name|toExpire
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|ReplicationSession
name|token
range|:
name|toExpire
control|)
block|{
name|releaseSession
argument_list|(
name|token
operator|.
name|session
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|releaseSession
specifier|private
name|void
name|releaseSession
parameter_list|(
name|String
name|sessionID
parameter_list|)
throws|throws
name|IOException
block|{
name|ReplicationSession
name|session
init|=
name|sessions
operator|.
name|remove
argument_list|(
name|sessionID
argument_list|)
decl_stmt|;
comment|// if we're called concurrently by close() and release(), could be that one
comment|// thread beats the other to release the session.
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|revision
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Ensure that replicator is still open, or throw {@link AlreadyClosedException} otherwise. */
DECL|method|ensureOpen
specifier|protected
specifier|final
specifier|synchronized
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"This replicator has already been closed"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkForUpdate
specifier|public
specifier|synchronized
name|SessionToken
name|checkForUpdate
parameter_list|(
name|String
name|currentVersion
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentRevision
operator|==
literal|null
condition|)
block|{
comment|// no published revisions yet
return|return
literal|null
return|;
block|}
if|if
condition|(
name|currentVersion
operator|!=
literal|null
operator|&&
name|currentRevision
operator|.
name|revision
operator|.
name|compareTo
argument_list|(
name|currentVersion
argument_list|)
operator|<=
literal|0
condition|)
block|{
comment|// currentVersion is newer or equal to latest published revision
return|return
literal|null
return|;
block|}
comment|// currentVersion is either null or older than latest published revision
name|currentRevision
operator|.
name|incRef
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sessionID
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|sessionToken
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SessionToken
name|sessionToken
init|=
operator|new
name|SessionToken
argument_list|(
name|sessionID
argument_list|,
name|currentRevision
operator|.
name|revision
argument_list|)
decl_stmt|;
specifier|final
name|ReplicationSession
name|timedSessionToken
init|=
operator|new
name|ReplicationSession
argument_list|(
name|sessionToken
argument_list|,
name|currentRevision
argument_list|)
decl_stmt|;
name|sessions
operator|.
name|put
argument_list|(
name|sessionID
argument_list|,
name|timedSessionToken
argument_list|)
expr_stmt|;
return|return
name|sessionToken
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
comment|// release all managed revisions
for|for
control|(
name|ReplicationSession
name|session
range|:
name|sessions
operator|.
name|values
argument_list|()
control|)
block|{
name|session
operator|.
name|revision
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
name|sessions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Returns the expiration threshold.    *     * @see #setExpirationThreshold(long)    */
DECL|method|getExpirationThreshold
specifier|public
name|long
name|getExpirationThreshold
parameter_list|()
block|{
return|return
name|expirationThresholdMilllis
return|;
block|}
annotation|@
name|Override
DECL|method|obtainFile
specifier|public
specifier|synchronized
name|InputStream
name|obtainFile
parameter_list|(
name|String
name|sessionID
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|ReplicationSession
name|session
init|=
name|sessions
operator|.
name|get
argument_list|(
name|sessionID
argument_list|)
decl_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
operator|&&
name|session
operator|.
name|isExpired
argument_list|(
name|expirationThresholdMilllis
argument_list|)
condition|)
block|{
name|releaseSession
argument_list|(
name|sessionID
argument_list|)
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
block|}
comment|// session either previously expired, or we just expired it
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SessionExpiredException
argument_list|(
literal|"session ("
operator|+
name|sessionID
operator|+
literal|") expired while obtaining file: source="
operator|+
name|source
operator|+
literal|" file="
operator|+
name|fileName
argument_list|)
throw|;
block|}
name|sessions
operator|.
name|get
argument_list|(
name|sessionID
argument_list|)
operator|.
name|markAccessed
argument_list|()
expr_stmt|;
return|return
name|session
operator|.
name|revision
operator|.
name|revision
operator|.
name|open
argument_list|(
name|source
argument_list|,
name|fileName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|publish
specifier|public
specifier|synchronized
name|void
name|publish
parameter_list|(
name|Revision
name|revision
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentRevision
operator|!=
literal|null
condition|)
block|{
name|int
name|compare
init|=
name|revision
operator|.
name|compareTo
argument_list|(
name|currentRevision
operator|.
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
comment|// same revision published again, ignore but release it
name|revision
operator|.
name|release
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
name|revision
operator|.
name|release
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot publish an older revision: rev="
operator|+
name|revision
operator|+
literal|" current="
operator|+
name|currentRevision
argument_list|)
throw|;
block|}
block|}
comment|// swap revisions
specifier|final
name|RefCountedRevision
name|oldRevision
init|=
name|currentRevision
decl_stmt|;
name|currentRevision
operator|=
operator|new
name|RefCountedRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldRevision
operator|!=
literal|null
condition|)
block|{
name|oldRevision
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
comment|// check for expired sessions
name|checkExpiredSessions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|String
name|sessionID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|releaseSession
argument_list|(
name|sessionID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Modify session expiration time - if a replication session is inactive that    * long it is automatically expired, and further attempts to operate within    * this session will throw a {@link SessionExpiredException}.    */
DECL|method|setExpirationThreshold
specifier|public
specifier|synchronized
name|void
name|setExpirationThreshold
parameter_list|(
name|long
name|expirationThreshold
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|this
operator|.
name|expirationThresholdMilllis
operator|=
name|expirationThreshold
expr_stmt|;
name|checkExpiredSessions
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

