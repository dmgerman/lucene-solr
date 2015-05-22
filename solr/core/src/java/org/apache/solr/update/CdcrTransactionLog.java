begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|File
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|FastOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|JavaBinCodec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ObjectReleaseTracker
import|;
end_import

begin_comment
comment|/**  * Extends {@link org.apache.solr.update.TransactionLog} to:  *<ul>  *<li>reopen automatically the output stream if its reference count reached 0. This is achieved by extending  * methods {@link #incref()}, {@link #close()} and {@link #reopenOutputStream()}.</li>  *<li>encode the number of records in the tlog file in the last commit record. The number of records will be  * decoded and reuse if the tlog file is reopened. This is achieved by extending the constructor, and the  * methods {@link #writeCommit(CommitUpdateCommand, int)} and {@link #getReader(long)}.</li>  *</ul>  */
end_comment

begin_class
DECL|class|CdcrTransactionLog
specifier|public
class|class
name|CdcrTransactionLog
extends|extends
name|TransactionLog
block|{
DECL|field|isReplaying
specifier|private
name|boolean
name|isReplaying
decl_stmt|;
DECL|field|startVersion
name|long
name|startVersion
decl_stmt|;
comment|// (absolute) version of the first element of this transaction log
DECL|method|CdcrTransactionLog
name|CdcrTransactionLog
parameter_list|(
name|File
name|tlogFile
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|globalStrings
parameter_list|)
block|{
name|super
argument_list|(
name|tlogFile
argument_list|,
name|globalStrings
argument_list|)
expr_stmt|;
comment|// The starting version number will be used to seek more efficiently tlogs
name|String
name|filename
init|=
name|tlogFile
operator|.
name|getName
argument_list|()
decl_stmt|;
name|startVersion
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|filename
operator|.
name|substring
argument_list|(
name|filename
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|isReplaying
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|CdcrTransactionLog
name|CdcrTransactionLog
parameter_list|(
name|File
name|tlogFile
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|globalStrings
parameter_list|,
name|boolean
name|openExisting
parameter_list|)
block|{
name|super
argument_list|(
name|tlogFile
argument_list|,
name|globalStrings
argument_list|,
name|openExisting
argument_list|)
expr_stmt|;
comment|// The starting version number will be used to seek more efficiently tlogs
name|String
name|filename
init|=
name|tlogFile
operator|.
name|getName
argument_list|()
decl_stmt|;
name|startVersion
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|filename
operator|.
name|substring
argument_list|(
name|filename
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|numRecords
operator|=
name|openExisting
condition|?
name|this
operator|.
name|readNumRecords
argument_list|()
else|:
literal|0
expr_stmt|;
comment|// if we try to reopen an existing tlog file and that the number of records is equal to 0, then we are replaying
comment|// the log and we will append a commit
if|if
condition|(
name|openExisting
operator|&&
name|numRecords
operator|==
literal|0
condition|)
block|{
name|isReplaying
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of records in the log (currently includes the header and an optional commit).    */
DECL|method|numRecords
specifier|public
name|int
name|numRecords
parameter_list|()
block|{
return|return
name|super
operator|.
name|numRecords
argument_list|()
return|;
block|}
comment|/**    * The last record of the transaction log file is expected to be a commit with a 4 byte integer that encodes the    * number of records in the file.    */
DECL|method|readNumRecords
specifier|private
name|int
name|readNumRecords
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|endsWithCommit
argument_list|()
condition|)
block|{
name|long
name|size
init|=
name|fos
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// 4 bytes for the record size, the lenght of the end message + 1 byte for its value tag,
comment|// and 4 bytes for the number of records
name|long
name|pos
init|=
name|size
operator|-
literal|4
operator|-
name|END_MESSAGE
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|-
literal|4
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
return|return
literal|0
return|;
name|ChannelFastInputStream
name|is
init|=
operator|new
name|ChannelFastInputStream
argument_list|(
name|channel
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
name|is
operator|.
name|readInt
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while reading number of records in tlog "
operator|+
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|writeCommit
specifier|public
name|long
name|writeCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|LogCodec
name|codec
init|=
operator|new
name|LogCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|long
name|pos
init|=
name|fos
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// if we had flushed, this should be equal to channel.position()
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
name|writeLogHeader
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|pos
operator|=
name|fos
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|codec
operator|.
name|init
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|ARR
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeInt
argument_list|(
name|UpdateLog
operator|.
name|COMMIT
operator||
name|flags
argument_list|)
expr_stmt|;
comment|// should just take one byte
name|codec
operator|.
name|writeLong
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|INT
argument_list|)
expr_stmt|;
comment|// Enforce the encoding of a plain integer, to simplify decoding
name|fos
operator|.
name|writeInt
argument_list|(
name|numRecords
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// the number of records in the file - +1 to account for the commit operation being written
name|codec
operator|.
name|writeStr
argument_list|(
name|END_MESSAGE
argument_list|)
expr_stmt|;
comment|// ensure these bytes are (almost) last in the file
name|endRecord
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// flush since this will be the last record in a log fill
assert|assert
name|fos
operator|.
name|size
argument_list|()
operator|==
name|channel
operator|.
name|size
argument_list|()
assert|;
name|isReplaying
operator|=
literal|false
expr_stmt|;
comment|// we have replayed and appended a commit record with the number of records in the file
return|return
name|pos
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Returns a reader that can be used while a log is still in use.    * Currently only *one* LogReader may be outstanding, and that log may only    * be used from a single thread.    */
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|LogReader
name|getReader
parameter_list|(
name|long
name|startingPos
parameter_list|)
block|{
return|return
operator|new
name|CdcrLogReader
argument_list|(
name|startingPos
argument_list|)
return|;
block|}
DECL|class|CdcrLogReader
specifier|public
class|class
name|CdcrLogReader
extends|extends
name|LogReader
block|{
DECL|field|numRecords
specifier|private
name|int
name|numRecords
init|=
literal|1
decl_stmt|;
comment|// start at 1 to account for the header record
DECL|method|CdcrLogReader
specifier|public
name|CdcrLogReader
parameter_list|(
name|long
name|startingPos
parameter_list|)
block|{
name|super
argument_list|(
name|startingPos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Object
name|o
init|=
name|super
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|numRecords
operator|++
expr_stmt|;
comment|// We are replaying the log. We need to update the number of records for the writeCommit.
if|if
condition|(
name|isReplaying
condition|)
block|{
synchronized|synchronized
init|(
name|CdcrTransactionLog
operator|.
name|this
init|)
block|{
name|CdcrTransactionLog
operator|.
name|this
operator|.
name|numRecords
operator|=
name|this
operator|.
name|numRecords
expr_stmt|;
block|}
block|}
block|}
return|return
name|o
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|incref
specifier|public
name|void
name|incref
parameter_list|()
block|{
comment|// if the refcount is 0, we need to reopen the output stream
if|if
condition|(
name|refcount
operator|.
name|getAndIncrement
argument_list|()
operator|==
literal|0
condition|)
block|{
name|reopenOutputStream
argument_list|()
expr_stmt|;
comment|// synchronised with this
block|}
block|}
comment|/**    * Modified to act like {@link #incref()} in order to be compatible with {@link UpdateLog#recoverFromLog()}.    * Otherwise, we would have to duplicate the method {@link UpdateLog#recoverFromLog()} in    * {@link org.apache.solr.update.CdcrUpdateLog} and change the call    * {@code if (!ll.try_incref()) continue; } to {@code incref(); }.    */
annotation|@
name|Override
DECL|method|try_incref
specifier|public
name|boolean
name|try_incref
parameter_list|()
block|{
name|this
operator|.
name|incref
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Closing tlog"
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// dereference these variables for GC
name|fos
operator|=
literal|null
expr_stmt|;
name|os
operator|=
literal|null
expr_stmt|;
name|channel
operator|=
literal|null
expr_stmt|;
name|raf
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|deleteOnClose
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|tlogFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: should this class care if a file couldnt be deleted?
comment|// this just emulates previous behavior, where only SecurityException would be handled.
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
assert|assert
name|ObjectReleaseTracker
operator|.
name|release
argument_list|(
name|this
argument_list|)
assert|;
block|}
block|}
comment|/**    * Re-open the output stream of the tlog and position    * the file pointer at the end of the file. It assumes    * that the tlog is non-empty and that the tlog's header    * has been already read.    */
DECL|method|reopenOutputStream
specifier|synchronized
name|void
name|reopenOutputStream
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Re-opening tlog's output stream: "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
name|raf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|this
operator|.
name|tlogFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|channel
operator|=
name|raf
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|raf
operator|.
name|length
argument_list|()
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|os
operator|=
name|Channels
operator|.
name|newOutputStream
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|fos
operator|=
operator|new
name|FastOutputStream
argument_list|(
name|os
argument_list|,
operator|new
name|byte
index|[
literal|65536
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fos
operator|.
name|setWritten
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// reflect that we aren't starting at the beginning
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

