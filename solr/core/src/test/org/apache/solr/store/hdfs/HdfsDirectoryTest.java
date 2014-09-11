begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|hdfs
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|RAMDirectory
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
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|hdfs
operator|.
name|HdfsTestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import

begin_class
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
comment|// hdfs client currently leaks thread (HADOOP-9049)
comment|//@Ignore("this test violates the test security policy because of org.apache.hadoop.fs.RawLocalFileSystem.mkdirs")
DECL|class|HdfsDirectoryTest
specifier|public
class|class
name|HdfsDirectoryTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|MAX_NUMBER_OF_WRITES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_NUMBER_OF_WRITES
init|=
literal|10000
decl_stmt|;
DECL|field|MIN_FILE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MIN_FILE_SIZE
init|=
literal|100
decl_stmt|;
DECL|field|MAX_FILE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_FILE_SIZE
init|=
literal|100000
decl_stmt|;
DECL|field|MIN_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MIN_BUFFER_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|MAX_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_BUFFER_SIZE
init|=
literal|5000
decl_stmt|;
DECL|field|MAX_NUMBER_OF_READS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_NUMBER_OF_READS
init|=
literal|10000
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|directory
specifier|private
name|HdfsDirectory
name|directory
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Before
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.permissions.enabled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|directory
operator|=
operator|new
name|HdfsDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|dfsCluster
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/hdfs"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|random
operator|=
name|random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWritingAndReadingAFile
specifier|public
name|void
name|testWritingAndReadingAFile
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|listAll
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|listAll
control|)
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
literal|"testing.test"
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|12345
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
name|directory
operator|.
name|openInput
argument_list|(
literal|"testing.test"
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|listAll
operator|=
name|directory
operator|.
name|listAll
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listAll
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testing.test"
argument_list|,
name|listAll
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|directory
operator|.
name|fileLength
argument_list|(
literal|"testing.test"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInput
name|input1
init|=
name|directory
operator|.
name|openInput
argument_list|(
literal|"testing.test"
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|IndexInput
name|input2
init|=
operator|(
name|IndexInput
operator|)
name|input1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|input2
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|input2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|input1
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|input1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test.other"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|deleteFile
argument_list|(
literal|"testing.test"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRename
specifier|public
name|void
name|testRename
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|listAll
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|listAll
control|)
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
literal|"testing.test"
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|renameFile
argument_list|(
literal|"testing.test"
argument_list|,
literal|"testing.test.renamed"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test.renamed"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
literal|"testing.test.renamed"
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|deleteFile
argument_list|(
literal|"testing.test.renamed"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|directory
argument_list|,
literal|"testing.test.renamed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEOF
specifier|public
name|void
name|testEOF
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|fsDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|"test.eof"
decl_stmt|;
name|createFile
argument_list|(
name|name
argument_list|,
name|fsDir
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|long
name|fsLength
init|=
name|fsDir
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|hdfsLength
init|=
name|directory
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fsLength
argument_list|,
name|hdfsLength
argument_list|)
expr_stmt|;
name|testEof
argument_list|(
name|name
argument_list|,
name|fsDir
argument_list|,
name|fsLength
argument_list|)
expr_stmt|;
name|testEof
argument_list|(
name|name
argument_list|,
name|directory
argument_list|,
name|hdfsLength
argument_list|)
expr_stmt|;
block|}
DECL|method|testEof
specifier|private
name|void
name|testEof
parameter_list|(
name|String
name|name
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should throw eof"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testRandomAccessWrites
specifier|public
name|void
name|testRandomAccessWrites
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Directory
name|fsDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|getName
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Working on pass ["
operator|+
name|i
operator|+
literal|"] contains ["
operator|+
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|name
argument_list|,
name|fsDir
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|assertInputsEquals
argument_list|(
name|name
argument_list|,
name|fsDir
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test failed on pass ["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertInputsEquals
specifier|private
name|void
name|assertInputsEquals
parameter_list|(
name|String
name|name
parameter_list|,
name|Directory
name|fsDir
parameter_list|,
name|HdfsDirectory
name|hdfs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|reads
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_NUMBER_OF_READS
argument_list|)
decl_stmt|;
name|IndexInput
name|fsInput
init|=
name|fsDir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|IndexInput
name|hdfsInput
init|=
name|hdfs
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fsInput
operator|.
name|length
argument_list|()
argument_list|,
name|hdfsInput
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|fileLength
init|=
operator|(
name|int
operator|)
name|fsInput
operator|.
name|length
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
name|reads
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nextInt
init|=
name|Math
operator|.
name|min
argument_list|(
name|MAX_BUFFER_SIZE
operator|-
name|MIN_BUFFER_SIZE
argument_list|,
name|fileLength
argument_list|)
decl_stmt|;
name|byte
index|[]
name|fsBuf
init|=
operator|new
name|byte
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|nextInt
operator|>
literal|0
condition|?
name|nextInt
else|:
literal|1
argument_list|)
operator|+
name|MIN_BUFFER_SIZE
index|]
decl_stmt|;
name|byte
index|[]
name|hdfsBuf
init|=
operator|new
name|byte
index|[
name|fsBuf
operator|.
name|length
index|]
decl_stmt|;
name|int
name|offset
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|fsBuf
operator|.
name|length
argument_list|)
decl_stmt|;
name|nextInt
operator|=
name|fsBuf
operator|.
name|length
operator|-
name|offset
expr_stmt|;
name|int
name|length
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|nextInt
operator|>
literal|0
condition|?
name|nextInt
else|:
literal|1
argument_list|)
decl_stmt|;
name|nextInt
operator|=
name|fileLength
operator|-
name|length
expr_stmt|;
name|int
name|pos
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|nextInt
operator|>
literal|0
condition|?
name|nextInt
else|:
literal|1
argument_list|)
decl_stmt|;
name|fsInput
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|fsInput
operator|.
name|readBytes
argument_list|(
name|fsBuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|hdfsInput
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|hdfsInput
operator|.
name|readBytes
argument_list|(
name|hdfsBuf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|f
init|=
name|offset
init|;
name|f
operator|<
name|length
condition|;
name|f
operator|++
control|)
block|{
if|if
condition|(
name|fsBuf
index|[
name|f
index|]
operator|!=
name|hdfsBuf
index|[
name|f
index|]
condition|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|fsInput
operator|.
name|close
argument_list|()
expr_stmt|;
name|hdfsInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createFile
specifier|private
name|void
name|createFile
parameter_list|(
name|String
name|name
parameter_list|,
name|Directory
name|fsDir
parameter_list|,
name|HdfsDirectory
name|hdfs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|writes
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_NUMBER_OF_WRITES
argument_list|)
decl_stmt|;
name|int
name|fileLength
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_FILE_SIZE
operator|-
name|MIN_FILE_SIZE
argument_list|)
operator|+
name|MIN_FILE_SIZE
decl_stmt|;
name|IndexOutput
name|fsOutput
init|=
name|fsDir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
operator|new
name|IOContext
argument_list|()
argument_list|)
decl_stmt|;
name|IndexOutput
name|hdfsOutput
init|=
name|hdfs
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
operator|new
name|IOContext
argument_list|()
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
name|writes
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|MAX_BUFFER_SIZE
operator|-
name|MIN_BUFFER_SIZE
argument_list|,
name|fileLength
argument_list|)
argument_list|)
operator|+
name|MIN_BUFFER_SIZE
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|buf
operator|.
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|fsOutput
operator|.
name|writeBytes
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|hdfsOutput
operator|.
name|writeBytes
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|fsOutput
operator|.
name|close
argument_list|()
expr_stmt|;
name|hdfsOutput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getName
specifier|private
name|String
name|getName
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

