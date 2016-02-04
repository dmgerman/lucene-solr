begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCheckIndex
specifier|public
class|class
name|TestCheckIndex
extends|extends
name|BaseTestCheckIndex
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeletedDocs
specifier|public
name|void
name|testDeletedDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|testDeletedDocs
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBogusTermVectors
specifier|public
name|void
name|testBogusTermVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|testBogusTermVectors
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChecksumsOnly
specifier|public
name|void
name|testChecksumsOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|testChecksumsOnly
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChecksumsOnlyVerbose
specifier|public
name|void
name|testChecksumsOnlyVerbose
parameter_list|()
throws|throws
name|IOException
block|{
name|testChecksumsOnlyVerbose
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObtainsLock
specifier|public
name|void
name|testObtainsLock
parameter_list|()
throws|throws
name|IOException
block|{
name|testObtainsLock
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

