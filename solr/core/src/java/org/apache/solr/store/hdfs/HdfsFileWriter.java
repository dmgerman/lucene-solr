begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|hdfs
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CreateFlag
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FsServerDefaults
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|HdfsFileWriter
specifier|public
class|class
name|HdfsFileWriter
extends|extends
name|DataOutput
implements|implements
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HdfsFileWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HDFS_SYNC_BLOCK
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_SYNC_BLOCK
init|=
literal|"solr.hdfs.sync.block"
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|outputStream
specifier|private
name|FSDataOutputStream
name|outputStream
decl_stmt|;
DECL|field|currentPosition
specifier|private
name|long
name|currentPosition
decl_stmt|;
DECL|method|HdfsFileWriter
specifier|public
name|HdfsFileWriter
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating writer on {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|Configuration
name|conf
init|=
name|fileSystem
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|FsServerDefaults
name|fsDefaults
init|=
name|fileSystem
operator|.
name|getServerDefaults
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|HDFS_SYNC_BLOCK
argument_list|)
condition|)
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|SYNC_BLOCK
argument_list|)
expr_stmt|;
block|}
name|outputStream
operator|=
name|fileSystem
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|flags
argument_list|,
name|fsDefaults
operator|.
name|getFileBufferSize
argument_list|()
argument_list|,
name|fsDefaults
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fsDefaults
operator|.
name|getBlockSize
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|currentPosition
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid seek called on {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Seek not supported"
argument_list|)
throw|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
comment|// flush to the network, not guarantees it makes it to the DN (vs hflush)
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Flushed file {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closed writer on {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|b
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|currentPosition
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|currentPosition
operator|+=
name|length
expr_stmt|;
block|}
DECL|method|getPosition
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|currentPosition
return|;
block|}
block|}
end_class

end_unit

