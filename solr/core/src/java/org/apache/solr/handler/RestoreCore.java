begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Callable
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
name|Future
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
name|store
operator|.
name|Directory
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
name|FSDirectory
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
name|core
operator|.
name|DirectoryFactory
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
name|core
operator|.
name|SolrCore
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
DECL|class|RestoreCore
specifier|public
class|class
name|RestoreCore
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
DECL|field|backupName
specifier|private
specifier|final
name|String
name|backupName
decl_stmt|;
DECL|field|backupLocation
specifier|private
specifier|final
name|String
name|backupLocation
decl_stmt|;
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|method|RestoreCore
specifier|public
name|RestoreCore
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|backupLocation
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|backupName
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|doRestore
argument_list|()
return|;
block|}
DECL|method|doRestore
specifier|private
name|boolean
name|doRestore
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|backupPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|backupLocation
argument_list|)
operator|.
name|resolve
argument_list|(
name|backupName
argument_list|)
decl_stmt|;
name|String
name|restoreIndexName
init|=
literal|"restore."
operator|+
name|backupName
decl_stmt|;
name|String
name|restoreIndexPath
init|=
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|restoreIndexName
decl_stmt|;
name|Directory
name|restoreIndexDir
init|=
literal|null
decl_stmt|;
name|Directory
name|indexDir
init|=
literal|null
decl_stmt|;
try|try
init|(
name|Directory
name|backupDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|backupPath
argument_list|)
init|)
block|{
name|restoreIndexDir
operator|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|restoreIndexPath
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
comment|//Prefer local copy.
name|indexDir
operator|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|core
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
comment|//Move all files from backupDir to restoreIndexDir
for|for
control|(
name|String
name|filename
range|:
name|backupDir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|checkInterrupted
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Copying file {} to restore directory "
argument_list|,
name|filename
argument_list|)
expr_stmt|;
try|try
init|(
name|IndexInput
name|indexInput
init|=
name|backupDir
operator|.
name|openInput
argument_list|(
name|filename
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|Long
name|checksum
init|=
literal|null
decl_stmt|;
try|try
block|{
name|checksum
operator|=
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|indexInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not read checksum from index file: "
operator|+
name|filename
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|long
name|length
init|=
name|indexInput
operator|.
name|length
argument_list|()
decl_stmt|;
name|IndexFetcher
operator|.
name|CompareResult
name|compareResult
init|=
name|IndexFetcher
operator|.
name|compareFile
argument_list|(
name|indexDir
argument_list|,
name|filename
argument_list|,
name|length
argument_list|,
name|checksum
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|compareResult
operator|.
name|equal
operator|||
operator|(
operator|!
name|compareResult
operator|.
name|checkSummed
operator|&&
operator|(
name|filename
operator|.
name|endsWith
argument_list|(
literal|".si"
argument_list|)
operator|||
name|filename
operator|.
name|endsWith
argument_list|(
literal|".liv"
argument_list|)
operator|||
name|filename
operator|.
name|startsWith
argument_list|(
literal|"segments_"
argument_list|)
operator|)
operator|)
condition|)
block|{
name|restoreIndexDir
operator|.
name|copyFrom
argument_list|(
name|backupDir
argument_list|,
name|filename
argument_list|,
name|filename
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//prefer local copy
name|restoreIndexDir
operator|.
name|copyFrom
argument_list|(
name|indexDir
argument_list|,
name|filename
argument_list|,
name|filename
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
name|UNKNOWN
argument_list|,
literal|"Exception while restoring the backup index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Switching directories"
argument_list|)
expr_stmt|;
name|IndexFetcher
operator|.
name|modifyIndexProps
argument_list|(
name|core
argument_list|,
name|restoreIndexName
argument_list|)
expr_stmt|;
name|boolean
name|success
decl_stmt|;
try|try
block|{
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|newIndexWriter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|openNewSearcher
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Successfully restored to the backup index"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//Rollback to the old index directory. Delete the restore index directory and mark the restore as failed.
name|log
operator|.
name|warn
argument_list|(
literal|"Could not switch to restored index. Rolling back to the current index"
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dir
operator|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|META_DATA
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
name|IndexFetcher
operator|.
name|INDEX_PROPERTIES
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|doneWithDirectory
argument_list|(
name|restoreIndexDir
argument_list|)
expr_stmt|;
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|remove
argument_list|(
name|restoreIndexDir
argument_list|)
expr_stmt|;
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|newIndexWriter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|openNewSearcher
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|UNKNOWN
argument_list|,
literal|"Exception while restoring the backup index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|success
condition|)
block|{
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|doneWithDirectory
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|remove
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|restoreIndexDir
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|restoreIndexDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexDir
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkInterrupted
specifier|private
name|void
name|checkInterrupted
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedException
argument_list|(
literal|"Stopping restore process. Thread was interrupted."
argument_list|)
throw|;
block|}
block|}
DECL|method|openNewSearcher
specifier|private
name|void
name|openNewSearcher
parameter_list|()
throws|throws
name|Exception
block|{
name|Future
index|[]
name|waitSearcher
init|=
operator|new
name|Future
index|[
literal|1
index|]
decl_stmt|;
name|core
operator|.
name|getSearcher
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|waitSearcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitSearcher
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
name|waitSearcher
index|[
literal|0
index|]
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

