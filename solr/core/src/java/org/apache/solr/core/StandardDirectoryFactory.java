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
name|AtomicMoveNotSupportedException
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
name|FileSystems
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
name|StandardCopyOption
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
name|LockFactory
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
name|NativeFSLockFactory
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
name|NoLockFactory
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
name|SimpleFSLockFactory
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
name|SingleInstanceLockFactory
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
comment|/**  * Directory provider which mimics original Solr   * {@link org.apache.lucene.store.FSDirectory} based behavior.  *   * File based DirectoryFactory implementations generally extend  * this class.  *   */
end_comment

begin_class
DECL|class|StandardDirectoryFactory
specifier|public
class|class
name|StandardDirectoryFactory
extends|extends
name|CachingDirectoryFactory
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
annotation|@
name|Override
DECL|method|create
specifier|protected
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
block|{
comment|// we pass NoLockFactory, because the real lock factory is set later by injectLockFactory:
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createLockFactory
specifier|protected
name|LockFactory
name|createLockFactory
parameter_list|(
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|rawLockType
condition|)
block|{
name|rawLockType
operator|=
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NATIVE
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"No lockType configured, assuming '"
operator|+
name|rawLockType
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|lockType
init|=
name|rawLockType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|lockType
condition|)
block|{
case|case
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SIMPLE
case|:
return|return
name|SimpleFSLockFactory
operator|.
name|INSTANCE
return|;
case|case
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NATIVE
case|:
return|return
name|NativeFSLockFactory
operator|.
name|INSTANCE
return|;
case|case
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SINGLE
case|:
return|return
operator|new
name|SingleInstanceLockFactory
argument_list|()
return|;
case|case
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NONE
case|:
return|return
name|NoLockFactory
operator|.
name|INSTANCE
return|;
default|default:
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
literal|"Unrecognized lockType: "
operator|+
name|rawLockType
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
name|String
name|cpath
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
decl_stmt|;
return|return
name|super
operator|.
name|normalize
argument_list|(
name|cpath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we go by the persistent storage ...
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|dirFile
operator|.
name|canRead
argument_list|()
operator|&&
name|dirFile
operator|.
name|list
argument_list|()
operator|.
name|length
operator|>
literal|0
return|;
block|}
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|removeDirectory
specifier|protected
name|void
name|removeDirectory
parameter_list|(
name|CacheValue
name|cacheValue
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|cacheValue
operator|.
name|path
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dirFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override for more efficient moves.    *     * Intended for use with replication - use    * carefully - some Directory wrappers will    * cache files for example.    *     * You should first {@link Directory#sync(java.util.Collection)} any file that will be     * moved or avoid cached files through settings.    *     * @throws IOException    *           If there is a low-level I/O error.    */
annotation|@
name|Override
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
name|Directory
name|baseFromDir
init|=
name|getBaseDir
argument_list|(
name|fromDir
argument_list|)
decl_stmt|;
name|Directory
name|baseToDir
init|=
name|getBaseDir
argument_list|(
name|toDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseFromDir
operator|instanceof
name|FSDirectory
operator|&&
name|baseToDir
operator|instanceof
name|FSDirectory
condition|)
block|{
name|Path
name|path1
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|baseFromDir
operator|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
name|Path
name|path2
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|baseFromDir
operator|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|path1
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|path2
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AtomicMoveNotSupportedException
name|e
parameter_list|)
block|{
name|Files
operator|.
name|move
argument_list|(
name|path1
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|path2
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|move
argument_list|(
name|fromDir
argument_list|,
name|toDir
argument_list|,
name|fileName
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
block|}
comment|// perform an atomic rename if possible
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
name|Directory
name|baseDir
init|=
name|getBaseDir
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseDir
operator|instanceof
name|FSDirectory
condition|)
block|{
name|Path
name|path
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|baseDir
operator|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|path
operator|.
name|resolve
argument_list|(
name|toName
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AtomicMoveNotSupportedException
name|e
parameter_list|)
block|{
name|Files
operator|.
name|move
argument_list|(
name|FileSystems
operator|.
name|getDefault
argument_list|()
operator|.
name|getPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
argument_list|)
argument_list|,
name|FileSystems
operator|.
name|getDefault
argument_list|()
operator|.
name|getPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|toName
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|renameWithOverwrite
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|toName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

