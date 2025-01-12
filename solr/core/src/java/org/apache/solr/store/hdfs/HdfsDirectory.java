begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Collection
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
name|FSDataInputStream
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
name|FileContext
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
name|FileStatus
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|BaseDirectory
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
name|LockFactory
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
name|store
operator|.
name|blockcache
operator|.
name|CustomBufferedIndexInput
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

begin_class
DECL|class|HdfsDirectory
specifier|public
class|class
name|HdfsDirectory
extends|extends
name|BaseDirectory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|LF_EXT
specifier|private
specifier|static
specifier|final
name|String
name|LF_EXT
init|=
literal|".lf"
decl_stmt|;
DECL|field|hdfsDirPath
specifier|protected
specifier|final
name|Path
name|hdfsDirPath
decl_stmt|;
DECL|field|configuration
specifier|protected
specifier|final
name|Configuration
name|configuration
decl_stmt|;
DECL|field|fileSystem
specifier|private
specifier|final
name|FileSystem
name|fileSystem
decl_stmt|;
DECL|field|fileContext
specifier|private
specifier|final
name|FileContext
name|fileContext
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|method|HdfsDirectory
specifier|public
name|HdfsDirectory
parameter_list|(
name|Path
name|hdfsDirPath
parameter_list|,
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|hdfsDirPath
argument_list|,
name|HdfsLockFactory
operator|.
name|INSTANCE
argument_list|,
name|configuration
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|HdfsDirectory
specifier|public
name|HdfsDirectory
parameter_list|(
name|Path
name|hdfsDirPath
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|hdfsDirPath
operator|=
name|hdfsDirPath
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|fileSystem
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|hdfsDirPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|fileContext
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|hdfsDirPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileSystem
operator|instanceof
name|DistributedFileSystem
condition|)
block|{
comment|// Make sure dfs is not in safe mode
while|while
condition|(
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fileSystem
operator|)
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The NameNode is in SafeMode - Solr will wait 5 seconds and try again."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
comment|// continue
block|}
block|}
block|}
try|try
block|{
if|if
condition|(
operator|!
name|fileSystem
operator|.
name|exists
argument_list|(
name|hdfsDirPath
argument_list|)
condition|)
block|{
name|boolean
name|success
init|=
name|fileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfsDirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not create directory: "
operator|+
name|hdfsDirPath
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Problem creating directory: "
operator|+
name|hdfsDirPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing hdfs directory {}"
argument_list|,
name|hdfsDirPath
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|close
argument_list|()
expr_stmt|;
name|isOpen
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Check whether this directory is open or closed. This check may return stale results in the form of false negatives.    * @return true if the directory is definitely closed, false if the directory is open or is pending closure    */
DECL|method|isClosed
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
operator|!
name|isOpen
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|HdfsFileWriter
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|name
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createTempOutput
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|getNormalNames
specifier|private
name|String
index|[]
name|getNormalNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
init|=
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|files
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|toNormalName
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|files
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
return|;
block|}
DECL|method|toNormalName
specifier|private
name|String
name|toNormalName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
name|LF_EXT
argument_list|)
condition|)
block|{
return|return
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
return|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|HdfsIndexInput
argument_list|(
name|name
argument_list|,
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|name
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleting {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rename
specifier|public
name|void
name|rename
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|sourcePath
init|=
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|Path
name|destPath
init|=
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|fileContext
operator|.
name|rename
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|syncMetaData
specifier|public
name|void
name|syncMetaData
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: how?
block|}
annotation|@
name|Override
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
name|FileStatus
name|fileStatus
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|fileStatus
operator|.
name|getLen
argument_list|()
return|;
block|}
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|fileStatus
init|=
name|getFileSystem
argument_list|()
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|hdfsDirPath
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|listStatus
init|=
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
name|hdfsDirPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|listStatus
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
for|for
control|(
name|FileStatus
name|status
range|:
name|listStatus
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|getNormalNames
argument_list|(
name|files
argument_list|)
return|;
block|}
DECL|method|getHdfsDirPath
specifier|public
name|Path
name|getHdfsDirPath
parameter_list|()
block|{
return|return
name|hdfsDirPath
return|;
block|}
DECL|method|getFileSystem
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|fileSystem
return|;
block|}
DECL|method|getConfiguration
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
DECL|class|HdfsIndexInput
specifier|public
specifier|static
class|class
name|HdfsIndexInput
extends|extends
name|CustomBufferedIndexInput
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|inputStream
specifier|private
specifier|final
name|FSDataInputStream
name|inputStream
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|clone
specifier|private
name|boolean
name|clone
init|=
literal|false
decl_stmt|;
DECL|method|HdfsIndexInput
specifier|public
name|HdfsIndexInput
parameter_list|(
name|String
name|name
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Opening normal index input on {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|length
operator|=
name|fileStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|inputStream
operator|=
name|fileSystem
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
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
name|inputStream
operator|.
name|readFully
argument_list|(
name|getFilePointer
argument_list|()
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
DECL|method|closeInternal
specifier|protected
name|void
name|closeInternal
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing normal index input on {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|clone
condition|)
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IndexInput
name|clone
parameter_list|()
block|{
name|HdfsIndexInput
name|clone
init|=
operator|(
name|HdfsIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|clone
operator|=
literal|true
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
annotation|@
name|Override
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sync called on {}"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|names
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hdfsDirPath
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|HdfsDirectory
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|hdfsDirPath
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|HdfsDirectory
operator|)
name|obj
operator|)
operator|.
name|hdfsDirPath
argument_list|)
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"@"
operator|+
name|hdfsDirPath
operator|+
literal|" lockFactory="
operator|+
name|lockFactory
return|;
block|}
block|}
end_class

end_unit

