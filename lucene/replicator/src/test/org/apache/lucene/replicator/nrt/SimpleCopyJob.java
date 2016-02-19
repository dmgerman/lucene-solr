begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Set
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
name|atomic
operator|.
name|AtomicLong
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
name|codecs
operator|.
name|CodecUtil
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
name|CorruptIndexException
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
name|IndexFileNames
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
name|DataInput
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
name|DataOutput
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
name|IndexInput
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
name|IndexOutput
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
name|InputStreamDataInput
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
name|MockDirectoryWrapper
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
name|OutputStreamDataOutput
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
name|OutputStreamIndexOutput
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
name|RateLimiter
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
name|IOUtils
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_comment
comment|/** Handles one set of files that need copying, either because we have a  *  new NRT point, or we are pre-copying merged files for merge warming. */
end_comment

begin_class
DECL|class|SimpleCopyJob
class|class
name|SimpleCopyJob
extends|extends
name|CopyJob
block|{
DECL|field|c
specifier|final
name|Connection
name|c
decl_stmt|;
DECL|field|copyBuffer
specifier|final
name|byte
index|[]
name|copyBuffer
init|=
operator|new
name|byte
index|[
literal|65536
index|]
decl_stmt|;
DECL|field|copyState
specifier|final
name|CopyState
name|copyState
decl_stmt|;
DECL|field|iter
specifier|private
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
argument_list|>
name|iter
decl_stmt|;
DECL|method|SimpleCopyJob
specifier|public
name|SimpleCopyJob
parameter_list|(
name|String
name|reason
parameter_list|,
name|Connection
name|c
parameter_list|,
name|CopyState
name|copyState
parameter_list|,
name|SimpleReplicaNode
name|dest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
parameter_list|,
name|boolean
name|highPriority
parameter_list|,
name|OnceDone
name|onceDone
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reason
argument_list|,
name|files
argument_list|,
name|dest
argument_list|,
name|highPriority
argument_list|,
name|onceDone
argument_list|)
expr_stmt|;
name|dest
operator|.
name|message
argument_list|(
literal|"create SimpleCopyJob o"
operator|+
name|ord
argument_list|)
expr_stmt|;
name|this
operator|.
name|c
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|copyState
operator|=
name|copyState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
block|{
name|iter
operator|=
name|toCopy
operator|.
name|iterator
argument_list|()
expr_stmt|;
comment|// Send all file names / offsets up front to avoid ping-ping latency:
try|try
block|{
comment|// This means we resumed an already in-progress copy; we do this one first:
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|current
operator|.
name|name
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVLong
argument_list|(
name|current
operator|.
name|getBytesCopied
argument_list|()
argument_list|)
expr_stmt|;
name|totBytes
operator|+=
name|current
operator|.
name|metaData
operator|.
name|length
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
range|:
name|toCopy
control|)
block|{
name|String
name|fileName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FileMetaData
name|metaData
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|totBytes
operator|+=
name|metaData
operator|.
name|length
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeString
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|c
operator|.
name|out
operator|.
name|writeVLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
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
name|current
operator|!=
literal|null
condition|)
block|{
comment|// Do this only at the end, after sending all requested files, so we don't deadlock due to socket buffering waiting for primary to
comment|// send us this length:
name|long
name|len
init|=
name|c
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|current
operator|.
name|metaData
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"file "
operator|+
name|current
operator|.
name|name
operator|+
literal|": meta data says length="
operator|+
name|current
operator|.
name|metaData
operator|.
name|length
operator|+
literal|" but c.in says "
operator|+
name|len
argument_list|)
throw|;
block|}
block|}
name|dest
operator|.
name|message
argument_list|(
literal|"SimpleCopyJob.init: done start files count="
operator|+
name|toCopy
operator|.
name|size
argument_list|()
operator|+
literal|" totBytes="
operator|+
name|totBytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|cancel
argument_list|(
literal|"exc during start"
argument_list|,
name|t
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NodeCommunicationException
argument_list|(
literal|"exc during start"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already started"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTotalBytesCopied
specifier|public
name|long
name|getTotalBytesCopied
parameter_list|()
block|{
return|return
name|totBytesCopied
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNamesToCopy
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getFileNamesToCopy
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
range|:
name|toCopy
control|)
block|{
name|fileNames
operator|.
name|add
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fileNames
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
block|{
return|return
name|files
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/** Higher priority and then "first come first serve" order. */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|CopyJob
name|_other
parameter_list|)
block|{
name|SimpleCopyJob
name|other
init|=
operator|(
name|SimpleCopyJob
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|highPriority
operator|!=
name|other
operator|.
name|highPriority
condition|)
block|{
return|return
name|highPriority
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|ord
operator|<
name|other
operator|.
name|ord
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|dest
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"top: file copy done; took %.1f msec to copy %d bytes; now rename %d tmp files"
argument_list|,
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startNS
operator|)
operator|/
literal|1000000.0
argument_list|,
name|totBytesCopied
argument_list|,
name|copiedFiles
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// NOTE: if any of the files we copied overwrote a file in the current commit point, we (ReplicaNode) removed the commit point up
comment|// front so that the commit is not corrupt.  This way if we hit exc here, or if we crash here, we won't leave a corrupt commit in
comment|// the index:
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ent
range|:
name|copiedFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|tmpFileName
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_FILES
condition|)
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"rename file "
operator|+
name|tmpFileName
operator|+
literal|" to "
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: if this throws exception, then some files have been moved to their true names, and others are leftover .tmp files.  I don't
comment|// think heroic exception handling is necessary (no harm will come, except some leftover files),  nor warranted here (would make the
comment|// code more complex, for the exceptional cases when something is wrong w/ your IO system):
name|dest
operator|.
name|dir
operator|.
name|renameFile
argument_list|(
name|tmpFileName
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
name|copiedFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** Do an iota of work; returns true if all copying is done */
DECL|method|visit
specifier|synchronized
name|boolean
name|visit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|exc
operator|!=
literal|null
condition|)
block|{
comment|// We were externally cancelled:
return|return
literal|true
return|;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
operator|==
literal|false
condition|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileMetaData
name|metaData
init|=
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|next
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|len
init|=
name|c
operator|.
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|metaData
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"file "
operator|+
name|fileName
operator|+
literal|": meta data says length="
operator|+
name|metaData
operator|.
name|length
operator|+
literal|" but c.in says "
operator|+
name|len
argument_list|)
throw|;
block|}
name|current
operator|=
operator|new
name|CopyOneFile
argument_list|(
name|c
operator|.
name|in
argument_list|,
name|dest
argument_list|,
name|fileName
argument_list|,
name|metaData
argument_list|,
name|copyBuffer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|current
operator|.
name|visit
argument_list|()
condition|)
block|{
comment|// This file is done copying
name|copiedFiles
operator|.
name|put
argument_list|(
name|current
operator|.
name|name
argument_list|,
name|current
operator|.
name|tmpName
argument_list|)
expr_stmt|;
name|totBytesCopied
operator|+=
name|current
operator|.
name|getBytesCopied
argument_list|()
expr_stmt|;
assert|assert
name|totBytesCopied
operator|<=
name|totBytes
operator|:
literal|"totBytesCopied="
operator|+
name|totBytesCopied
operator|+
literal|" totBytes="
operator|+
name|totBytes
assert|;
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|newCopyOneFile
specifier|protected
name|CopyOneFile
name|newCopyOneFile
parameter_list|(
name|CopyOneFile
name|prev
parameter_list|)
block|{
return|return
operator|new
name|CopyOneFile
argument_list|(
name|prev
argument_list|,
name|c
operator|.
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|transferAndCancel
specifier|public
specifier|synchronized
name|void
name|transferAndCancel
parameter_list|(
name|CopyJob
name|prevJob
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|transferAndCancel
argument_list|(
name|prevJob
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
operator|(
operator|(
name|SimpleCopyJob
operator|)
name|prevJob
operator|)
operator|.
name|c
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cancel
specifier|public
specifier|synchronized
name|void
name|cancel
parameter_list|(
name|String
name|reason
parameter_list|,
name|Throwable
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|cancel
argument_list|(
name|reason
argument_list|,
name|exc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFailed
specifier|public
name|boolean
name|getFailed
parameter_list|()
block|{
return|return
name|exc
operator|!=
literal|null
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
literal|"SimpleCopyJob(ord="
operator|+
name|ord
operator|+
literal|" "
operator|+
name|reason
operator|+
literal|" highPriority="
operator|+
name|highPriority
operator|+
literal|" files count="
operator|+
name|files
operator|.
name|size
argument_list|()
operator|+
literal|" bytesCopied="
operator|+
name|totBytesCopied
operator|+
literal|" (of "
operator|+
name|totBytes
operator|+
literal|") filesCopied="
operator|+
name|copiedFiles
operator|.
name|size
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|runBlocking
specifier|public
name|void
name|runBlocking
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|visit
argument_list|()
operator|==
literal|false
condition|)
empty_stmt|;
if|if
condition|(
name|getFailed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"copy failed: "
operator|+
name|cancelReason
argument_list|,
name|exc
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCopyState
specifier|public
name|CopyState
name|getCopyState
parameter_list|()
block|{
return|return
name|copyState
return|;
block|}
annotation|@
name|Override
DECL|method|conflicts
specifier|public
specifier|synchronized
name|boolean
name|conflicts
parameter_list|(
name|CopyJob
name|_other
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|filesToCopy
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
range|:
name|toCopy
control|)
block|{
name|filesToCopy
operator|.
name|add
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SimpleCopyJob
name|other
init|=
operator|(
name|SimpleCopyJob
operator|)
name|_other
decl_stmt|;
synchronized|synchronized
init|(
name|other
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
range|:
name|other
operator|.
name|toCopy
control|)
block|{
if|if
condition|(
name|filesToCopy
operator|.
name|contains
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit
