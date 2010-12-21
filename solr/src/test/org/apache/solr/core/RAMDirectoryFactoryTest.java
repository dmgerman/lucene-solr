begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|RAMDirectory
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
name|easymock
operator|.
name|EasyMock
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
name|File
import|;
end_import

begin_comment
comment|/**  * Test-case for RAMDirectoryFactory  */
end_comment

begin_class
DECL|class|RAMDirectoryFactoryTest
specifier|public
class|class
name|RAMDirectoryFactoryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testOpenReturnsTheSameForSamePath
specifier|public
name|void
name|testOpenReturnsTheSameForSamePath
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|directory
init|=
operator|new
name|RefCntRamDirectory
argument_list|()
decl_stmt|;
name|RAMDirectoryFactory
name|factory
init|=
operator|new
name|RAMDirectoryFactory
argument_list|()
block|{
annotation|@
name|Override
name|Directory
name|openNew
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directory
return|;
block|}
block|}
decl_stmt|;
name|String
name|path
init|=
literal|"/fake/path"
decl_stmt|;
name|Directory
name|dir1
init|=
name|factory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|factory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"RAMDirectoryFactory should not create new instance of RefCntRamDirectory "
operator|+
literal|"every time open() is called for the same path"
argument_list|,
name|directory
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RAMDirectoryFactory should not create new instance of RefCntRamDirectory "
operator|+
literal|"every time open() is called for the same path"
argument_list|,
name|directory
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
block|}
DECL|method|testOpenSucceedForEmptyDir
specifier|public
name|void
name|testOpenSucceedForEmptyDir
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectoryFactory
name|factory
init|=
operator|new
name|RAMDirectoryFactory
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|factory
operator|.
name|open
argument_list|(
literal|"/fake/path"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"RAMDirectoryFactory should create RefCntRamDirectory even if the path doen't lead "
operator|+
literal|"to index directory on the file system"
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

