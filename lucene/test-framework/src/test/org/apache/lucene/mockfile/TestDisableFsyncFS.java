begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|FileChannel
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
name|FileSystem
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
name|StandardOpenOption
import|;
end_import

begin_comment
comment|/** Basic tests for DisableFsyncFS */
end_comment

begin_class
DECL|class|TestDisableFsyncFS
specifier|public
class|class
name|TestDisableFsyncFS
extends|extends
name|MockFileSystemTestCase
block|{
annotation|@
name|Override
DECL|method|wrap
specifier|protected
name|Path
name|wrap
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|FileSystem
name|fs
init|=
operator|new
name|DisableFsyncFS
argument_list|(
name|path
operator|.
name|getFileSystem
argument_list|()
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterPath
argument_list|(
name|path
argument_list|,
name|fs
argument_list|)
return|;
block|}
comment|/** Test that we don't corrumpt fsync: it just doesnt happen */
DECL|method|testFsyncWorks
specifier|public
name|void
name|testFsyncWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileChannel
name|file
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"file"
argument_list|)
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|128
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

