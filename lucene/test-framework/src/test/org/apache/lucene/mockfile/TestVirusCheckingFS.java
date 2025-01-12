begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|OutputStream
import|;
end_import

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
name|file
operator|.
name|AccessDeniedException
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

begin_comment
comment|/** Basic tests for VirusCheckingFS */
end_comment

begin_class
DECL|class|TestVirusCheckingFS
specifier|public
class|class
name|TestVirusCheckingFS
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
name|VirusCheckingFS
argument_list|(
name|path
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
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
comment|/** Test Files.delete fails if a file has an open inputstream against it */
DECL|method|testDeleteSometimesFails
specifier|public
name|void
name|testDeleteSometimesFails
parameter_list|()
throws|throws
name|IOException
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
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Path
name|path
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"file"
operator|+
name|counter
argument_list|)
decl_stmt|;
name|counter
operator|++
expr_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// File is now closed, we attempt delete:
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|ade
parameter_list|)
block|{
comment|// expected (sometimes)
name|assertTrue
argument_list|(
name|ade
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"VirusCheckingFS is randomly refusing to delete file "
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

