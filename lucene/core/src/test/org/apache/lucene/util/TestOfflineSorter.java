begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|charset
operator|.
name|StandardCharsets
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|codecs
operator|.
name|CodecUtil
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
name|CorruptIndexException
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
name|CorruptingIndexOutput
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
name|FilterDirectory
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
name|IndexInput
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
name|IndexOutput
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
name|util
operator|.
name|OfflineSorter
operator|.
name|BufferSize
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
name|OfflineSorter
operator|.
name|ByteSequencesWriter
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
name|OfflineSorter
operator|.
name|SortInfo
import|;
end_import

begin_comment
comment|/**  * Tests for on-disk merge sorting.  */
end_comment

begin_class
DECL|class|TestOfflineSorter
specifier|public
class|class
name|TestOfflineSorter
extends|extends
name|LuceneTestCase
block|{
DECL|field|tempDir
specifier|private
name|Path
name|tempDir
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
name|tempDir
operator|=
name|createTempDir
argument_list|(
literal|"mergesort"
argument_list|)
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
if|if
condition|(
name|tempDir
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSingleLine
specifier|public
name|void
name|testSingleLine
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{
literal|"Single line only."
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIntermediateMerges
specifier|public
name|void
name|testIntermediateMerges
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 20 mb worth of data with 1mb buffer, binary merging.
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|SortInfo
name|info
init|=
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|20
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|mergeRounds
operator|>
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSmallRandom
specifier|public
name|void
name|testSmallRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 20 mb worth of data with 1mb buffer.
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|SortInfo
name|sortInfo
init|=
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
name|OfflineSorter
operator|.
name|MAX_TEMPFILES
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|20
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sortInfo
operator|.
name|mergeRounds
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nightly
DECL|method|testLargerRandom
specifier|public
name|void
name|testLargerRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 100MB worth of data with 15mb buffer.
try|try
init|(
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|16
argument_list|)
argument_list|,
name|OfflineSorter
operator|.
name|MAX_TEMPFILES
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateRandom
specifier|private
name|byte
index|[]
index|[]
name|generateRandom
parameter_list|(
name|int
name|howMuchDataInBytes
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|howMuchDataInBytes
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|howMuchDataInBytes
operator|-=
name|current
operator|.
name|length
expr_stmt|;
block|}
name|byte
index|[]
index|[]
name|bytes
init|=
name|data
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|data
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
decl_stmt|;
return|return
name|bytes
return|;
block|}
comment|// Generates same data every time:
DECL|method|generateFixed
specifier|private
name|byte
index|[]
index|[]
name|generateFixed
parameter_list|(
name|int
name|howMuchDataInBytes
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|length
init|=
literal|256
decl_stmt|;
name|byte
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|howMuchDataInBytes
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|length
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
name|current
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|current
index|[
name|i
index|]
operator|=
name|counter
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
name|data
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|howMuchDataInBytes
operator|-=
name|current
operator|.
name|length
expr_stmt|;
name|length
operator|--
expr_stmt|;
if|if
condition|(
name|length
operator|<=
literal|128
condition|)
block|{
name|length
operator|=
literal|256
expr_stmt|;
block|}
block|}
name|byte
index|[]
index|[]
name|bytes
init|=
name|data
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|data
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
decl_stmt|;
return|return
name|bytes
return|;
block|}
DECL|field|unsignedByteOrderComparator
specifier|static
specifier|final
name|Comparator
argument_list|<
name|byte
index|[]
argument_list|>
name|unsignedByteOrderComparator
init|=
operator|new
name|Comparator
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|left
parameter_list|,
name|byte
index|[]
name|right
parameter_list|)
block|{
specifier|final
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
name|left
operator|.
name|length
argument_list|,
name|right
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
incr|,
name|j
operator|++
control|)
block|{
name|int
name|diff
init|=
operator|(
name|left
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
operator|(
name|right
index|[
name|j
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
return|return
name|left
operator|.
name|length
operator|-
name|right
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Check sorting data on an instance of {@link OfflineSorter}.    */
DECL|method|checkSort
specifier|private
name|SortInfo
name|checkSort
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|OfflineSorter
name|sorter
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|IndexOutput
name|golden
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"golden"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|data
argument_list|,
name|unsignedByteOrderComparator
argument_list|)
expr_stmt|;
name|writeAll
argument_list|(
name|golden
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|String
name|sorted
init|=
name|sorter
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("Input size [MB]: " + unsorted.length() / (1024 * 1024));
comment|//System.out.println(sortInfo);
name|assertFilesIdentical
argument_list|(
name|dir
argument_list|,
name|golden
operator|.
name|getName
argument_list|()
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
return|return
name|sorter
operator|.
name|sortInfo
return|;
block|}
comment|/**    * Make sure two files are byte-byte identical.    */
DECL|method|assertFilesIdentical
specifier|private
name|void
name|assertFilesIdentical
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|golden
parameter_list|,
name|String
name|sorted
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|numBytes
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|golden
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numBytes
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
name|sorted
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf1
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|buf2
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
try|try
init|(
name|IndexInput
name|in1
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|golden
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|;
name|IndexInput
name|in2
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|sorted
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|long
name|left
init|=
name|numBytes
decl_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
name|int
name|chunk
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|buf1
operator|.
name|length
argument_list|,
name|left
argument_list|)
decl_stmt|;
name|left
operator|-=
name|chunk
expr_stmt|;
name|in1
operator|.
name|readBytes
argument_list|(
name|buf1
argument_list|,
literal|0
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|in2
operator|.
name|readBytes
argument_list|(
name|buf2
argument_list|,
literal|0
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chunk
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|buf1
index|[
name|i
index|]
argument_list|,
name|buf2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** NOTE: closes the provided {@link IndexOutput} */
DECL|method|writeAll
specifier|private
name|void
name|writeAll
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ByteSequencesWriter
name|w
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|out
argument_list|)
init|)
block|{
for|for
control|(
name|byte
index|[]
name|datum
range|:
name|data
control|)
block|{
name|w
operator|.
name|write
argument_list|(
name|datum
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRamBuffer
specifier|public
name|void
name|testRamBuffer
parameter_list|()
block|{
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|10000
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2047
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|2047
argument_list|)
expr_stmt|;
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
init|)
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|threadID
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
operator|&&
name|failed
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|;
name|iter
operator|++
control|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo_"
operator|+
name|threadID
operator|+
literal|"_"
operator|+
name|iter
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
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
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Make sure corruption on the incoming (unsorted) file is caught, even if the corruption didn't confuse OfflineSorter! */
DECL|method|testBitFlippedOnInput1
specifier|public
name|void
name|testBitFlippedOnInput1
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir0
init|=
name|newMockDirectory
argument_list|()
init|)
block|{
if|if
condition|(
name|dir0
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir0
operator|)
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
operator|new
name|FilterDirectory
argument_list|(
name|dir0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|in
operator|.
name|createTempOutput
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"unsorted"
argument_list|)
condition|)
block|{
return|return
operator|new
name|CorruptingIndexOutput
argument_list|(
name|dir0
argument_list|,
literal|22
argument_list|,
name|out
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
block|}
decl_stmt|;
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|generateFixed
argument_list|(
literal|10
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|CorruptIndexException
name|e
init|=
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum failed (hardware problem?)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Make sure corruption on the incoming (unsorted) file is caught, if the corruption did confuse OfflineSorter! */
DECL|method|testBitFlippedOnInput2
specifier|public
name|void
name|testBitFlippedOnInput2
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir0
init|=
name|newMockDirectory
argument_list|()
init|)
block|{
if|if
condition|(
name|dir0
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir0
operator|)
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
operator|new
name|FilterDirectory
argument_list|(
name|dir0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|in
operator|.
name|createTempOutput
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"unsorted"
argument_list|)
condition|)
block|{
return|return
operator|new
name|CorruptingIndexOutput
argument_list|(
name|dir0
argument_list|,
literal|22
argument_list|,
name|out
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|corruptFile
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|newTempName
decl_stmt|;
try|try
init|(
name|IndexOutput
name|tmpOut
init|=
name|dir0
operator|.
name|createTempOutput
argument_list|(
literal|"tmp"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|;                     IndexInput in = dir0.openInput(out.getName()
operator|,
name|IOContext
operator|.
name|DEFAULT
block|)
block|)
block|{
name|newTempName
operator|=
name|tmpOut
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// Replace length at the end with a too-long value:
name|short
name|v
init|=
name|in
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|256
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|tmpOut
operator|.
name|writeShort
parameter_list|(
name|Short
operator|.
name|MAX_VALUE
parameter_list|)
constructor_decl|;
name|tmpOut
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|in
operator|.
name|length
argument_list|()
operator|-
name|Short
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
comment|// Delete original and copy corrupt version back:
name|dir0
operator|.
name|deleteFile
argument_list|(
name|out
operator|.
name|getName
argument_list|()
argument_list|)
return|;
name|dir0
operator|.
name|copyFrom
argument_list|(
name|dir0
argument_list|,
name|newTempName
argument_list|,
name|out
operator|.
name|getName
argument_list|()
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|dir0
operator|.
name|deleteFile
argument_list|(
name|newTempName
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
block|}
empty_stmt|;
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|generateFixed
argument_list|(
literal|5
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
comment|// This corruption made OfflineSorter fail with its own exception, but we verify it also went and added (as suppressed) that the
comment|// checksum was wrong:
name|EOFException
name|e
init|=
name|expectThrows
argument_list|(
name|EOFException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|instanceof
name|CorruptIndexException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum failed (hardware problem?)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_class

begin_comment
unit|}
comment|/** Make sure corruption on a temp file (partition) is caught, even if the corruption didn't confuse OfflineSorter! */
end_comment

begin_function
DECL|method|testBitFlippedOnPartition1
unit|public
name|void
name|testBitFlippedOnPartition1
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir0
init|=
name|newMockDirectory
argument_list|()
init|)
block|{
if|if
condition|(
name|dir0
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir0
operator|)
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
operator|new
name|FilterDirectory
argument_list|(
name|dir0
argument_list|)
block|{
name|boolean
name|corrupted
decl_stmt|;
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|in
operator|.
name|createTempOutput
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|corrupted
operator|==
literal|false
operator|&&
name|suffix
operator|.
name|equals
argument_list|(
literal|"sort"
argument_list|)
condition|)
block|{
name|corrupted
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|CorruptingIndexOutput
argument_list|(
name|dir0
argument_list|,
literal|544677
argument_list|,
name|out
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
block|}
decl_stmt|;
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|generateFixed
argument_list|(
call|(
name|int
call|)
argument_list|(
name|OfflineSorter
operator|.
name|MB
operator|*
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|CorruptIndexException
name|e
init|=
name|expectThrows
argument_list|(
name|CorruptIndexException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum failed (hardware problem?)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/** Make sure corruption on a temp file (partition) is caught, if the corruption did confuse OfflineSorter! */
end_comment

begin_function
DECL|method|testBitFlippedOnPartition2
specifier|public
name|void
name|testBitFlippedOnPartition2
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir0
init|=
name|newMockDirectory
argument_list|()
init|)
block|{
if|if
condition|(
name|dir0
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir0
operator|)
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
operator|new
name|FilterDirectory
argument_list|(
name|dir0
argument_list|)
block|{
name|boolean
name|corrupted
decl_stmt|;
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|in
operator|.
name|createTempOutput
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|corrupted
operator|==
literal|false
operator|&&
name|suffix
operator|.
name|equals
argument_list|(
literal|"sort"
argument_list|)
condition|)
block|{
name|corrupted
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|CorruptingIndexOutput
argument_list|(
name|dir0
argument_list|,
literal|544677
argument_list|,
name|out
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|corruptFile
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|newTempName
decl_stmt|;
try|try
init|(
name|IndexOutput
name|tmpOut
init|=
name|dir0
operator|.
name|createTempOutput
argument_list|(
literal|"tmp"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|;                     IndexInput in = dir0.openInput(out.getName()
operator|,
name|IOContext
operator|.
name|DEFAULT
block|)
block|)
block|{
name|newTempName
operator|=
name|tmpOut
operator|.
name|getName
argument_list|()
expr_stmt|;
name|tmpOut
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
literal|1025905
argument_list|)
expr_stmt|;
name|short
name|v
init|=
name|in
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|254
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|tmpOut
operator|.
name|writeShort
parameter_list|(
name|Short
operator|.
name|MAX_VALUE
parameter_list|)
constructor_decl|;
name|tmpOut
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|in
operator|.
name|length
argument_list|()
operator|-
literal|1025905
operator|-
name|Short
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
comment|// Delete original and copy corrupt version back:
name|dir0
operator|.
name|deleteFile
argument_list|(
name|out
operator|.
name|getName
argument_list|()
argument_list|)
return|;
name|dir0
operator|.
name|copyFrom
argument_list|(
name|dir0
argument_list|,
name|newTempName
argument_list|,
name|out
operator|.
name|getName
argument_list|()
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|dir0
operator|.
name|deleteFile
argument_list|(
name|newTempName
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
block|}
end_function

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_decl_stmt
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|generateFixed
argument_list|(
call|(
name|int
call|)
argument_list|(
name|OfflineSorter
operator|.
name|MB
operator|*
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|EOFException
name|e
init|=
name|expectThrows
argument_list|(
name|EOFException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|instanceof
name|CorruptIndexException
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"checksum failed (hardware problem?)"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

unit|}   } }
end_unit

