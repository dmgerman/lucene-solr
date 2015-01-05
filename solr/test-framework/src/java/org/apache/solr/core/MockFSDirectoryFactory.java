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
name|TrackingDirectoryWrapper
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

begin_comment
comment|/**  * Opens a directory with {@link LuceneTestCase#newFSDirectory(Path)}  */
end_comment

begin_class
DECL|class|MockFSDirectoryFactory
specifier|public
class|class
name|MockFSDirectoryFactory
extends|extends
name|StandardDirectoryFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
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
name|Directory
name|dir
init|=
name|LuceneTestCase
operator|.
name|newFSDirectory
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
decl_stmt|;
comment|// we can't currently do this check because of how
comment|// Solr has to reboot a new Directory sometimes when replicating
comment|// or rolling back - the old directory is closed and the following
comment|// test assumes it can open an IndexWriter when that happens - we
comment|// have a new Directory for the same dir and still an open IW at
comment|// this point
name|Directory
name|cdir
init|=
name|reduce
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|cdir
operator|=
name|reduce
argument_list|(
name|cdir
argument_list|)
expr_stmt|;
name|cdir
operator|=
name|reduce
argument_list|(
name|cdir
argument_list|)
expr_stmt|;
if|if
condition|(
name|cdir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|cdir
operator|)
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|cdir
operator|)
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|cdir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
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
comment|// TODO: kind of a hack - we don't know what the delegate is, so
comment|// we treat it as file based since this works on most ephem impls
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
DECL|method|reduce
specifier|private
name|Directory
name|reduce
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|Directory
name|cdir
init|=
name|dir
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|NRTCachingDirectory
condition|)
block|{
name|cdir
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
if|if
condition|(
name|cdir
operator|instanceof
name|TrackingDirectoryWrapper
condition|)
block|{
name|cdir
operator|=
operator|(
operator|(
name|TrackingDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
return|return
name|cdir
return|;
block|}
block|}
end_class

end_unit

