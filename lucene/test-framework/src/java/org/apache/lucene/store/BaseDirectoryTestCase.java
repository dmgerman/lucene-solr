begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|util
operator|.
name|Arrays
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
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexNotFoundException
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_comment
comment|/** Base class for per-Directory tests. */
end_comment

begin_class
DECL|class|BaseDirectoryTestCase
specifier|public
specifier|abstract
class|class
name|BaseDirectoryTestCase
extends|extends
name|LuceneTestCase
block|{
comment|/** Subclass returns the Directory to be tested; if it's    *  an FS-based directory it should point to the specified    *  path, else it can ignore it. */
DECL|method|getDirectory
specifier|protected
specifier|abstract
name|Directory
name|getDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Make sure directory throws AlreadyClosedException if    *  you try to createOutput after closing. */
DECL|method|testDetectClose
specifier|public
name|void
name|testDetectClose
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testDetectClose"
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ace
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|// test is occasionally very slow, i dont know why
comment|// try this seed: 7D7E036AD12927F5:93333EF9E6DE44DE
annotation|@
name|Nightly
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|raw
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testThreadSafety"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BaseDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|(
name|raw
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we arent making an index
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
comment|// makes this test really slow
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
class|class
name|TheThread
extends|extends
name|Thread
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|TheThread
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|this
operator|.
name|name
operator|+
name|i
decl_stmt|;
try|try
block|{
comment|//System.out.println("create:" + fileName);
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
empty_stmt|;
class|class
name|TheThread2
extends|extends
name|Thread
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|TheThread2
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
comment|//System.out.println("file:" + file);
try|try
block|{
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"still open for writing"
argument_list|)
condition|)
block|{
comment|// ignore
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
empty_stmt|;
name|TheThread
name|theThread
init|=
operator|new
name|TheThread
argument_list|(
literal|"t1"
argument_list|)
decl_stmt|;
name|TheThread2
name|theThread2
init|=
operator|new
name|TheThread2
argument_list|(
literal|"t2"
argument_list|)
decl_stmt|;
name|theThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|theThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|theThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|theThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|raw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** LUCENE-1464: just creating a Directory should not    *  mkdir the underling directory in the filesystem. */
DECL|method|testDontCreate
specifier|public
name|void
name|testDontCreate
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|path
init|=
name|createTempDir
argument_list|(
literal|"doesnotexist"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|path
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|path
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** LUCENE-1468: once we create an output, we should see    *  it in the dir listing and be able to open it with    *  openInput. */
DECL|method|testDirectoryFilter
specifier|public
name|void
name|testDirectoryFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
literal|"file"
decl_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testDirectoryFilter"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-2852
DECL|method|testSeekToEOFThenBack
specifier|public
name|void
name|testSeekToEOFThenBack
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testSeekToEOFThenBack"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|o
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"out"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|3
operator|*
name|RAMInputStream
operator|.
name|BUFFER_SIZE
index|]
decl_stmt|;
name|o
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|i
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"out"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|i
operator|.
name|seek
argument_list|(
literal|2
operator|*
name|RAMInputStream
operator|.
name|BUFFER_SIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|i
operator|.
name|seek
argument_list|(
literal|3
operator|*
name|RAMInputStream
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|i
operator|.
name|seek
argument_list|(
name|RAMInputStream
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|i
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
literal|2
operator|*
name|RAMInputStream
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|i
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-1196
DECL|method|testIllegalEOF
specifier|public
name|void
name|testIllegalEOF
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testIllegalEOF"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|o
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"out"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|o
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|i
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"out"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|i
operator|.
name|seek
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|i
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDeleteFile
specifier|public
name|void
name|testDeleteFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testDeleteFile"
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo.txt"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"foo.txt"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dir
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3382 -- make sure we get exception if the directory really does not exist.
DECL|method|testNoDir
specifier|public
name|void
name|testNoDir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|tempDir
init|=
name|createTempDir
argument_list|(
literal|"doesnotexist"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|rm
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempDir
argument_list|)
decl_stmt|;
try|try
block|{
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchDirectoryException
decl||
name|IndexNotFoundException
name|nsde
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3382 test that delegate compound files correctly.
DECL|method|testCompoundFileAppendTwice
specifier|public
name|void
name|testCompoundFileAppendTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|newDir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCompoundFileAppendTwice"
argument_list|)
argument_list|)
decl_stmt|;
name|CompoundFileDirectory
name|csw
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSequenceFile
argument_list|(
name|newDir
argument_list|,
literal|"d1"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|csw
operator|.
name|createOutput
argument_list|(
literal|"d.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|CompoundFileDirectory
name|cfr
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cfr
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a file of the specified size with sequential data. The first    *  byte is written as the start byte provided. All subsequent bytes are    *  computed as start + offset where offset is the number of the byte.    */
DECL|method|createSequenceFile
specifier|private
name|void
name|createSequenceFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
name|start
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|writeByte
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|start
operator|++
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCopyBytes
specifier|public
name|void
name|testCopyBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|testCopyBytes
argument_list|(
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCopyBytes"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|value
specifier|private
specifier|static
name|byte
name|value
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|idx
operator|%
literal|256
operator|)
operator|*
operator|(
literal|1
operator|+
operator|(
name|idx
operator|/
literal|256
operator|)
operator|)
argument_list|)
return|;
block|}
DECL|method|testCopyBytes
specifier|public
specifier|static
name|void
name|testCopyBytes
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// make random file
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|77777
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1777777
argument_list|)
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|int
name|byteUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
block|{
name|bytes
index|[
name|byteUpto
operator|++
index|]
operator|=
name|value
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|byteUpto
operator|==
name|bytes
operator|.
name|length
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|byteUpto
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
comment|// copy from test -> test2
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test2"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|size
operator|-
name|upto
argument_list|)
decl_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|size
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify
name|IndexInput
name|in2
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test2"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|byte
name|v
init|=
name|in2
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|(
name|upto
argument_list|)
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|size
operator|-
name|upto
argument_list|)
decl_stmt|;
name|in2
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteIdx
init|=
literal|0
init|;
name|byteIdx
operator|<
name|limit
condition|;
name|byteIdx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|value
argument_list|(
name|upto
argument_list|)
argument_list|,
name|bytes
index|[
name|byteIdx
index|]
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
block|}
name|in2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3541
DECL|method|testCopyBytesWithThreads
specifier|public
name|void
name|testCopyBytesWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|testCopyBytesWithThreads
argument_list|(
name|getDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testCopyBytesWithThreads"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCopyBytesWithThreads
specifier|public
specifier|static
name|void
name|testCopyBytesWithThreads
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|datalen
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|101
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|datalen
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|IndexOutput
name|output
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"data"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|datalen
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"data"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|IndexOutput
name|outputHeader
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"header"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
comment|// copy our 100-byte header
name|outputHeader
operator|.
name|copyBytes
argument_list|(
name|input
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|outputHeader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now make N copies of the remaining bytes
name|CopyThread
name|copies
index|[]
init|=
operator|new
name|CopyThread
index|[
literal|10
index|]
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
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|=
operator|new
name|CopyThread
argument_list|(
name|input
operator|.
name|clone
argument_list|()
argument_list|,
name|d
operator|.
name|createOutput
argument_list|(
literal|"copy"
operator|+
name|i
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexInput
name|copiedData
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"copy"
operator|+
name|i
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dataCopy
init|=
operator|new
name|byte
index|[
name|datalen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|dataCopy
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// copy the header for easy testing
name|copiedData
operator|.
name|readBytes
argument_list|(
name|dataCopy
argument_list|,
literal|100
argument_list|,
name|datalen
operator|-
literal|100
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|dataCopy
argument_list|)
expr_stmt|;
name|copiedData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|CopyThread
specifier|static
class|class
name|CopyThread
extends|extends
name|Thread
block|{
DECL|field|src
specifier|final
name|IndexInput
name|src
decl_stmt|;
DECL|field|dst
specifier|final
name|IndexOutput
name|dst
decl_stmt|;
DECL|method|CopyThread
name|CopyThread
parameter_list|(
name|IndexInput
name|src
parameter_list|,
name|IndexOutput
name|dst
parameter_list|)
block|{
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
name|this
operator|.
name|dst
operator|=
name|dst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|dst
operator|.
name|copyBytes
argument_list|(
name|src
argument_list|,
name|src
operator|.
name|length
argument_list|()
operator|-
literal|100
argument_list|)
expr_stmt|;
name|dst
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

