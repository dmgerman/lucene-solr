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
name|AsynchronousFileChannel
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
name|NoSuchFileException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|atomic
operator|.
name|AtomicBoolean
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
name|Constants
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
name|IOUtils
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
name|InfoStream
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

begin_class
DECL|class|TestMockFilesystems
specifier|public
class|class
name|TestMockFilesystems
extends|extends
name|LuceneTestCase
block|{
DECL|method|testLeakInputStream
specifier|public
name|void
name|testLeakInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|LeakFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|leak
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file handle leaks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leak
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLeakOutputStream
specifier|public
name|void
name|testLeakOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|LeakFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|OutputStream
name|leak
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"leaky"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file handle leaks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leak
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLeakFileChannel
specifier|public
name|void
name|testLeakFileChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|LeakFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileChannel
name|leak
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file handle leaks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leak
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLeakAsyncFileChannel
specifier|public
name|void
name|testLeakAsyncFileChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|LeakFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|AsynchronousFileChannel
name|leak
init|=
name|AsynchronousFileChannel
operator|.
name|open
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file handle leaks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leak
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLeakByteChannel
specifier|public
name|void
name|testLeakByteChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|LeakFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|SeekableByteChannel
name|leak
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file handle leaks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|leak
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDeleteOpenFile
specifier|public
name|void
name|testDeleteOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeFalse
argument_list|(
literal|"windows is not supported"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|WindowsFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDeleteIfExistsOpenFile
specifier|public
name|void
name|testDeleteIfExistsOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeFalse
argument_list|(
literal|"windows is not supported"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|WindowsFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRenameOpenFile
specifier|public
name|void
name|testRenameOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeFalse
argument_list|(
literal|"windows is not supported"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|WindowsFS
argument_list|(
name|dir
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
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
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|,
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"target"
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testVerboseWrite
specifier|public
name|void
name|testVerboseWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|seenMessage
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|InfoStream
name|testStream
init|=
operator|new
name|InfoStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
literal|"FS"
operator|.
name|equals
argument_list|(
name|component
argument_list|)
operator|&&
name|message
operator|.
name|startsWith
argument_list|(
literal|"newOutputStream"
argument_list|)
condition|)
block|{
name|seenMessage
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|VerboseFS
argument_list|(
name|dir
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|testStream
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
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
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"output"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|seenMessage
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testVerboseFSNoSuchFileException
specifier|public
name|void
name|testVerboseFSNoSuchFileException
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|VerboseFS
argument_list|(
name|dir
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|InfoStream
operator|.
name|NO_OUTPUT
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
name|Path
name|wrapped
init|=
operator|new
name|FilterPath
argument_list|(
name|dir
argument_list|,
name|fs
argument_list|)
decl_stmt|;
try|try
block|{
name|AsynchronousFileChannel
operator|.
name|open
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"doesNotExist.rip"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFileException
name|nsfe
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|FileChannel
operator|.
name|open
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"doesNotExist.rip"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFileException
name|nsfe
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|Files
operator|.
name|newByteChannel
argument_list|(
name|wrapped
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFileException
name|nsfe
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testTooManyOpenFiles
specifier|public
name|void
name|testTooManyOpenFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|n
init|=
literal|60
decl_stmt|;
name|Path
name|dir
init|=
name|FilterPath
operator|.
name|unwrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|HandleLimitFS
argument_list|(
name|dir
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|n
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
name|dir
operator|=
operator|new
name|FilterPath
argument_list|(
name|dir
argument_list|,
name|fs
argument_list|)
expr_stmt|;
comment|// create open files to exact limit
name|List
argument_list|<
name|Closeable
argument_list|>
name|toClose
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now exceed
try|try
block|{
name|Files
operator|.
name|newOutputStream
argument_list|(
name|Files
operator|.
name|createTempFile
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Too many open files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|toClose
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

