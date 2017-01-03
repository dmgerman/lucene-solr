begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
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
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|FilterDirectory
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
name|FlushInfo
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
name|CachingDirectoryFactory
operator|.
name|CloseListener
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
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
comment|/**  * Provides access to a Directory implementation. You must release every  * Directory that you get.  */
end_comment

begin_class
DECL|class|DirectoryFactory
specifier|public
specifier|abstract
class|class
name|DirectoryFactory
implements|implements
name|NamedListInitializedPlugin
implements|,
name|Closeable
block|{
comment|// Estimate 10M docs, 100GB size, to avoid caching by NRTCachingDirectory
comment|// Stayed away from upper bounds of the int/long in case any other code tried to aggregate these numbers.
comment|// A large estimate should currently have no other side effects.
DECL|field|IOCONTEXT_NO_CACHE
specifier|public
specifier|static
specifier|final
name|IOContext
name|IOCONTEXT_NO_CACHE
init|=
operator|new
name|IOContext
argument_list|(
operator|new
name|FlushInfo
argument_list|(
literal|10
operator|*
literal|1000
operator|*
literal|1000
argument_list|,
literal|100L
operator|*
literal|1000
operator|*
literal|1000
operator|*
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|INDEX_W_TIMESTAMP_REGEX
specifier|protected
specifier|static
specifier|final
name|String
name|INDEX_W_TIMESTAMP_REGEX
init|=
literal|"index\\.[0-9]{17}"
decl_stmt|;
comment|// see SnapShooter.DATE_FMT
comment|// hint about what the directory contains - default is index directory
DECL|enum|DirContext
DECL|enum constant|DEFAULT
DECL|enum constant|META_DATA
specifier|public
enum|enum
name|DirContext
block|{
name|DEFAULT
block|,
name|META_DATA
block|}
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
comment|// Available lock types
DECL|field|LOCK_TYPE_SIMPLE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|LOCK_TYPE_NATIVE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_NATIVE
init|=
literal|"native"
decl_stmt|;
DECL|field|LOCK_TYPE_SINGLE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_SINGLE
init|=
literal|"single"
decl_stmt|;
DECL|field|LOCK_TYPE_NONE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_NONE
init|=
literal|"none"
decl_stmt|;
DECL|field|LOCK_TYPE_HDFS
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_HDFS
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|coreContainer
specifier|protected
specifier|volatile
name|CoreContainer
name|coreContainer
decl_stmt|;
comment|/**    * Indicates a Directory will no longer be used, and when its ref count    * hits 0, it can be closed. On close all directories will be closed    * whether this has been called or not. This is simply to allow early cleanup.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|doneWithDirectory
specifier|public
specifier|abstract
name|void
name|doneWithDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Adds a close listener for a Directory.    */
DECL|method|addCloseListener
specifier|public
specifier|abstract
name|void
name|addCloseListener
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|CloseListener
name|closeListener
parameter_list|)
function_decl|;
comment|/**    * Close the this and all of the Directories it contains.    *     * @throws IOException If there is a low-level I/O error.    */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Directory for a given path.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|create
specifier|protected
specifier|abstract
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new LockFactory for a given path.    * @param rawLockType A string value as passed in config. Every factory should at least support 'none' to disable locking.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|createLockFactory
specifier|protected
specifier|abstract
name|LockFactory
name|createLockFactory
parameter_list|(
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns true if a Directory exists for a given path.    * @throws IOException If there is a low-level I/O error.    *     */
DECL|method|exists
specifier|public
specifier|abstract
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes the Directory's persistent storage.    * For example: A file system impl may remove the    * on disk directory.    * @throws IOException If there is a low-level I/O error.    *     */
DECL|method|remove
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes the Directory's persistent storage.    * For example: A file system impl may remove the    * on disk directory.    * @throws IOException If there is a low-level I/O error.    *     */
DECL|method|remove
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|afterCoreClose
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This remove is special in that it may be called even after    * the factory has been closed. Remove only makes sense for    * persistent directory factories.    *     * @param path to remove    * @param afterCoreClose whether to wait until after the core is closed.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|remove
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|afterCoreClose
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This remove is special in that it may be called even after    * the factory has been closed. Remove only makes sense for    * persistent directory factories.    *     * @param path to remove    * @throws IOException If there is a low-level I/O error.    */
DECL|method|remove
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @param directory to calculate size of    * @return size in bytes    * @throws IOException on low level IO error    */
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sizeOfDirectory
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/**    * @param path to calculate size of    * @return size in bytes    * @throws IOException on low level IO error    */
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|get
argument_list|(
name|path
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|long
name|size
decl_stmt|;
try|try
block|{
name|size
operator|=
name|sizeOfDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/**    * Override for more efficient moves.    *     * Intended for use with replication - use    * carefully - some Directory wrappers will    * cache files for example.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Directory
name|fromDir
parameter_list|,
name|Directory
name|toDir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
name|toDir
operator|.
name|copyFrom
argument_list|(
name|fromDir
argument_list|,
name|fileName
argument_list|,
name|fileName
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
name|fromDir
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|// sub classes perform an atomic rename if possible, otherwise fall back to delete + rename
comment|// this is important to support for index roll over durability after crashes
DECL|method|renameWithOverwrite
specifier|public
name|void
name|renameWithOverwrite
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|toName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|toName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{      }
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception deleting file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|rename
argument_list|(
name|fileName
argument_list|,
name|toName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the Directory for a given path, using the specified rawLockType.    * Will return the same Directory instance for the same path.    *     *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|get
specifier|public
specifier|abstract
name|Directory
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|,
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Increment the number of references to the given Directory. You must call    * release for every call to this method.    *     */
DECL|method|incRef
specifier|public
specifier|abstract
name|void
name|incRef
parameter_list|(
name|Directory
name|directory
parameter_list|)
function_decl|;
comment|/**    * @return true if data is kept after close.    */
DECL|method|isPersistent
specifier|public
specifier|abstract
name|boolean
name|isPersistent
parameter_list|()
function_decl|;
comment|/**    * @return true if storage is shared.    */
DECL|method|isSharedStorage
specifier|public
name|boolean
name|isSharedStorage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Releases the Directory so that it may be closed when it is no longer    * referenced.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|release
specifier|public
specifier|abstract
name|void
name|release
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Normalize a given path.    *     * @param path to normalize    * @return normalized path    * @throws IOException on io error    */
DECL|method|normalize
specifier|public
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|path
return|;
block|}
comment|/**    * @param path the path to check    * @return true if absolute, as in not relative    */
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// back compat
return|return
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|isAbsolute
argument_list|()
return|;
block|}
DECL|method|sizeOfDirectory
specifier|public
specifier|static
name|long
name|sizeOfDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|file
range|:
name|files
control|)
block|{
name|size
operator|+=
name|sizeOf
argument_list|(
name|directory
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
break|break;
block|}
block|}
return|return
name|size
return|;
block|}
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|directory
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// could be a race, file no longer exists, access denied, is a directory, etc.
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Delete the files in the Directory    */
DECL|method|empty
specifier|public
specifier|static
name|boolean
name|empty
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|boolean
name|isSuccess
init|=
literal|true
decl_stmt|;
name|String
name|contents
index|[]
decl_stmt|;
try|try
block|{
name|contents
operator|=
name|dir
operator|.
name|listAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|contents
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|file
range|:
name|contents
control|)
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error deleting files from Directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|isSuccess
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|isSuccess
return|;
block|}
comment|/**    * If your implementation can count on delete-on-last-close semantics    * or throws an exception when trying to remove a file in use, return    * false (eg NFS). Otherwise, return true. Defaults to returning false.    *     * @return true if factory impl requires that Searcher's explicitly    * reserve commit points.    */
DECL|method|searchersReserveCommitPoints
specifier|public
name|boolean
name|searchersReserveCommitPoints
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getDataHome
specifier|public
name|String
name|getDataHome
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// by default, we go off the instance directory
return|return
name|cd
operator|.
name|getInstanceDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|cd
operator|.
name|getDataDir
argument_list|()
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Optionally allow the DirectoryFactory to request registration of some MBeans.    */
DECL|method|offerMBeans
specifier|public
name|Collection
argument_list|<
name|SolrInfoMBean
argument_list|>
name|offerMBeans
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
DECL|method|cleanupOldIndexDirectories
specifier|public
name|void
name|cleanupOldIndexDirectories
parameter_list|(
specifier|final
name|String
name|dataDirPath
parameter_list|,
specifier|final
name|String
name|currentIndexDirPath
parameter_list|)
block|{
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|dataDirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dataDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{} does not point to a valid data directory; skipping clean-up of old index directories."
argument_list|,
name|dataDirPath
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|File
name|currentIndexDir
init|=
operator|new
name|File
argument_list|(
name|currentIndexDirPath
argument_list|)
decl_stmt|;
name|File
index|[]
name|oldIndexDirs
init|=
name|dataDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|String
name|fileName
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|file
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|equals
argument_list|(
name|currentIndexDir
argument_list|)
operator|&&
operator|(
name|fileName
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
operator|||
name|fileName
operator|.
name|matches
argument_list|(
name|INDEX_W_TIMESTAMP_REGEX
argument_list|)
operator|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldIndexDirs
operator|==
literal|null
operator|||
name|oldIndexDirs
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
comment|// nothing to do (no log message needed)
name|log
operator|.
name|info
argument_list|(
literal|"Found {} old index directories to clean-up under {}"
argument_list|,
name|oldIndexDirs
operator|.
name|length
argument_list|,
name|dataDirPath
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|dir
range|:
name|oldIndexDirs
control|)
block|{
name|String
name|dirToRmPath
init|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|deleteOldIndexDirectory
argument_list|(
name|dirToRmPath
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleted old index directory: {}"
argument_list|,
name|dirToRmPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Delete old index directory {} failed."
argument_list|,
name|dirToRmPath
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioExc
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to delete old directory {} due to: {}"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ioExc
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Extension point to allow sub-classes to infuse additional code when deleting old index directories
DECL|method|deleteOldIndexDirectory
specifier|protected
name|boolean
name|deleteOldIndexDirectory
parameter_list|(
name|String
name|oldDirPath
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|dirToRm
init|=
operator|new
name|File
argument_list|(
name|oldDirPath
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dirToRm
argument_list|)
expr_stmt|;
return|return
operator|!
name|dirToRm
operator|.
name|isDirectory
argument_list|()
return|;
block|}
DECL|method|initCoreContainer
specifier|public
name|void
name|initCoreContainer
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|cc
expr_stmt|;
block|}
comment|// special hack to work with FilterDirectory
DECL|method|getBaseDir
specifier|protected
name|Directory
name|getBaseDir
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|Directory
name|baseDir
init|=
name|dir
decl_stmt|;
while|while
condition|(
name|baseDir
operator|instanceof
name|FilterDirectory
condition|)
block|{
name|baseDir
operator|=
operator|(
operator|(
name|FilterDirectory
operator|)
name|baseDir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
return|return
name|baseDir
return|;
block|}
block|}
end_class

end_unit

