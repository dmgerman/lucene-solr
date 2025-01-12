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
name|InputStream
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
name|lang
operator|.
name|Object
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Override
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
name|channels
operator|.
name|SeekableByteChannel
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
name|DirectoryStream
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
operator|.
name|HandleTrackingFS
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
name|mockfile
operator|.
name|LeakFS
import|;
end_import

begin_comment
comment|/** Basic tests for HandleTrackingFS */
end_comment

begin_class
DECL|class|TestHandleTrackingFS
specifier|public
class|class
name|TestHandleTrackingFS
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
name|LeakFS
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
comment|/** Test that the delegate gets closed on exception in HandleTrackingFS#onClose */
DECL|method|testOnCloseThrowsException
specifier|public
name|void
name|testOnCloseThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
comment|// we are using LeakFS under the hood if we don't get closed the test fails
name|FileSystem
name|fs
init|=
operator|new
name|HandleTrackingFS
argument_list|(
literal|"test://"
argument_list|,
name|path
operator|.
name|getFileSystem
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"boom"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
comment|//
block|}
block|}
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
name|Path
name|dir
init|=
operator|new
name|FilterPath
argument_list|(
name|path
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
try|try
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|SeekableByteChannel
name|channel
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|InputStream
name|stream
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|dirStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|dirStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|/** Test that the delegate gets closed on exception in HandleTrackingFS#onOpen */
DECL|method|testOnOpenThrowsException
specifier|public
name|void
name|testOnOpenThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
comment|// we are using LeakFS under the hood if we don't get closed the test fails
name|FileSystem
name|fs
init|=
operator|new
name|HandleTrackingFS
argument_list|(
literal|"test://"
argument_list|,
name|path
operator|.
name|getFileSystem
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|protected
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"boom"
argument_list|)
throw|;
block|}
block|}
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
name|Path
name|dir
init|=
operator|new
name|FilterPath
argument_list|(
name|path
argument_list|,
name|fs
argument_list|)
decl_stmt|;
try|try
block|{
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|SeekableByteChannel
name|channel
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|InputStream
name|stream
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"somefile"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|dirStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

