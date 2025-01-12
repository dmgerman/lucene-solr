begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
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
name|atomic
operator|.
name|AtomicBoolean
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
name|document
operator|.
name|Document
import|;
end_import

begin_comment
comment|/** Parent JVM hold this "wrapper" to refer to each child JVM.  This is roughly equivalent e.g. to a client-side "sugar" API. */
end_comment

begin_class
DECL|class|NodeProcess
class|class
name|NodeProcess
implements|implements
name|Closeable
block|{
DECL|field|p
specifier|final
name|Process
name|p
decl_stmt|;
comment|// Port sub-process is listening on
DECL|field|tcpPort
specifier|final
name|int
name|tcpPort
decl_stmt|;
DECL|field|id
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|pumper
specifier|final
name|Thread
name|pumper
decl_stmt|;
comment|// Acquired when searching or indexing wants to use this node:
DECL|field|lock
specifier|final
name|ReentrantLock
name|lock
decl_stmt|;
DECL|field|isPrimary
specifier|final
name|boolean
name|isPrimary
decl_stmt|;
comment|// Version in the commit point we opened on init:
DECL|field|initCommitVersion
specifier|final
name|long
name|initCommitVersion
decl_stmt|;
comment|// SegmentInfos.version, which can be higher than the initCommitVersion
DECL|field|initInfosVersion
specifier|final
name|long
name|initInfosVersion
decl_stmt|;
DECL|field|isOpen
specifier|volatile
name|boolean
name|isOpen
init|=
literal|true
decl_stmt|;
DECL|field|nodeIsClosing
specifier|final
name|AtomicBoolean
name|nodeIsClosing
decl_stmt|;
DECL|method|NodeProcess
specifier|public
name|NodeProcess
parameter_list|(
name|Process
name|p
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|tcpPort
parameter_list|,
name|Thread
name|pumper
parameter_list|,
name|boolean
name|isPrimary
parameter_list|,
name|long
name|initCommitVersion
parameter_list|,
name|long
name|initInfosVersion
parameter_list|,
name|AtomicBoolean
name|nodeIsClosing
parameter_list|)
block|{
name|this
operator|.
name|p
operator|=
name|p
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|tcpPort
operator|=
name|tcpPort
expr_stmt|;
name|this
operator|.
name|pumper
operator|=
name|pumper
expr_stmt|;
name|this
operator|.
name|isPrimary
operator|=
name|isPrimary
expr_stmt|;
name|this
operator|.
name|initCommitVersion
operator|=
name|initCommitVersion
expr_stmt|;
name|this
operator|.
name|initInfosVersion
operator|=
name|initInfosVersion
expr_stmt|;
name|this
operator|.
name|nodeIsClosing
operator|=
name|nodeIsClosing
expr_stmt|;
assert|assert
name|initInfosVersion
operator|>=
name|initCommitVersion
operator|:
literal|"initInfosVersion="
operator|+
name|initInfosVersion
operator|+
literal|" initCommitVersion="
operator|+
name|initCommitVersion
assert|;
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isPrimary
condition|)
block|{
return|return
literal|"P"
operator|+
name|id
operator|+
literal|" tcpPort="
operator|+
name|tcpPort
return|;
block|}
else|else
block|{
return|return
literal|"R"
operator|+
name|id
operator|+
literal|" tcpPort="
operator|+
name|tcpPort
return|;
block|}
block|}
DECL|method|crash
specifier|public
specifier|synchronized
name|void
name|crash
parameter_list|()
block|{
if|if
condition|(
name|isOpen
condition|)
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|p
operator|.
name|destroy
argument_list|()
expr_stmt|;
try|try
block|{
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|pumper
operator|.
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|commit
specifier|public
name|boolean
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_COMMIT
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|s
operator|.
name|shutdownOutput
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"commit failed"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|method|commitAsync
specifier|public
name|void
name|commitAsync
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_COMMIT
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getSearchingVersion
specifier|public
name|long
name|getSearchingVersion
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_GET_SEARCHING_VERSION
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|s
operator|.
name|shutdownOutput
argument_list|()
expr_stmt|;
return|return
name|c
operator|.
name|in
operator|.
name|readVLong
argument_list|()
return|;
block|}
block|}
comment|/** Ask the primary node process to flush.  We send it all currently up replicas so it can notify them about the new NRT point.  Returns the newly    *  flushed version, or a negative (current) version if there were no changes. */
DECL|method|flush
specifier|public
specifier|synchronized
name|long
name|flush
parameter_list|(
name|int
name|atLeastMarkerCount
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|isPrimary
assert|;
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_FLUSH
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|atLeastMarkerCount
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|s
operator|.
name|shutdownOutput
argument_list|()
expr_stmt|;
return|return
name|c
operator|.
name|in
operator|.
name|readLong
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdown
specifier|public
specifier|synchronized
name|boolean
name|shutdown
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|//System.out.println("PARENT: now shutdown node=" + id + " isOpen=" + isOpen);
if|if
condition|(
name|isOpen
condition|)
block|{
comment|// Ask the child process to shutdown gracefully:
name|isOpen
operator|=
literal|false
expr_stmt|;
comment|//System.out.println("PARENT: send CMD_CLOSE to node=" + id);
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_CLOSE
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"shutdown failed"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"top: shutdown failed; ignoring"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|pumper
operator|.
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newNRTPoint
specifier|public
name|void
name|newNRTPoint
parameter_list|(
name|long
name|version
parameter_list|,
name|long
name|primaryGen
parameter_list|,
name|int
name|primaryTCPPort
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimpleReplicaNode
operator|.
name|CMD_NEW_NRT_POINT
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVLong
argument_list|(
name|primaryGen
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeInt
argument_list|(
name|primaryTCPPort
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addOrUpdateDocument
specifier|public
name|void
name|addOrUpdateDocument
parameter_list|(
name|Connection
name|c
parameter_list|,
name|Document
name|doc
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"only primary can index"
argument_list|)
throw|;
block|}
name|int
name|fieldCount
init|=
literal|0
decl_stmt|;
name|String
name|title
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|fieldCount
operator|++
expr_stmt|;
block|}
name|String
name|docid
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"docid"
argument_list|)
decl_stmt|;
assert|assert
name|docid
operator|!=
literal|null
assert|;
name|fieldCount
operator|++
expr_stmt|;
name|String
name|body
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|fieldCount
operator|++
expr_stmt|;
block|}
name|String
name|marker
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"marker"
argument_list|)
decl_stmt|;
if|if
condition|(
name|marker
operator|!=
literal|null
condition|)
block|{
name|fieldCount
operator|++
expr_stmt|;
block|}
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|isUpdate
condition|?
name|SimplePrimaryNode
operator|.
name|CMD_UPDATE_DOC
else|:
name|SimplePrimaryNode
operator|.
name|CMD_ADD_DOC
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVInt
argument_list|(
name|fieldCount
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
literal|"docid"
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|docid
argument_list|)
expr_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|marker
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
literal|"marker"
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|marker
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteDocument
specifier|public
name|void
name|deleteDocument
parameter_list|(
name|Connection
name|c
parameter_list|,
name|String
name|docid
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"only primary can delete documents"
argument_list|)
throw|;
block|}
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_DELETE_DOC
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|docid
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteAllDocuments
specifier|public
name|void
name|deleteAllDocuments
parameter_list|(
name|Connection
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"only primary can delete documents"
argument_list|)
throw|;
block|}
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_DELETE_ALL_DOCS
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
DECL|method|forceMerge
specifier|public
name|void
name|forceMerge
parameter_list|(
name|Connection
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isPrimary
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"only primary can force merge"
argument_list|)
throw|;
block|}
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_FORCE_MERGE
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

