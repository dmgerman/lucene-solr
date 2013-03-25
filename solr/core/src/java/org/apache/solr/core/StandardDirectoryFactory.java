begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|NRTCachingDirectory
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
name|RateLimitedDirectoryWrapper
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
name|CacheValue
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
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
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
argument_list|)
return|;
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
comment|/**    * Override for more efficient moves.    *     * Intended for use with replication - use    * carefully - some Directory wrappers will    * cache files for example.    *     * This implementation works with two wrappers:    * NRTCachingDirectory and RateLimitedDirectoryWrapper.    *     * You should first {@link Directory#sync(java.util.Collection)} any file that will be     * moved or avoid cached files through settings.    *     * @throws IOException    *           If there is a low-level I/O error.    */
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
name|File
name|dir1
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
decl_stmt|;
name|File
name|dir2
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|baseToDir
operator|)
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|File
name|indexFileInTmpDir
init|=
operator|new
name|File
argument_list|(
name|dir1
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|File
name|indexFileInIndex
init|=
operator|new
name|File
argument_list|(
name|dir2
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|indexFileInTmpDir
operator|.
name|renameTo
argument_list|(
name|indexFileInIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
return|return;
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
comment|// special hack to work with NRTCachingDirectory and RateLimitedDirectoryWrapper
DECL|method|getBaseDir
specifier|private
name|Directory
name|getBaseDir
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|Directory
name|baseDir
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|NRTCachingDirectory
condition|)
block|{
name|baseDir
operator|=
operator|(
operator|(
name|NRTCachingDirectory
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|instanceof
name|RateLimitedDirectoryWrapper
condition|)
block|{
name|baseDir
operator|=
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseDir
operator|=
name|dir
expr_stmt|;
block|}
return|return
name|baseDir
return|;
block|}
block|}
end_class

end_unit

