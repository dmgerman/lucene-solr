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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|MMapDirectory
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
name|BytesRef
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
name|TimeUnits
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
operator|.
name|SuppressCodecs
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
name|TimeoutSuite
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
name|generators
operator|.
name|RandomInts
import|;
end_import

begin_comment
comment|/**  * This test creates an index with one segment that is a little larger than 4GB.  */
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|}
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|4
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
DECL|class|Test4GBStoredFields
specifier|public
class|class
name|Test4GBStoredFields
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Nightly
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MMapDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"4GBStoredFields"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|dir
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
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|MergePolicy
name|mp
init|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogByteSizeMergePolicy
condition|)
block|{
comment|// 1 petabyte:
operator|(
operator|(
name|LogByteSizeMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMaxMergeMB
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
specifier|final
name|int
name|valueLength
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
operator|<<
literal|13
argument_list|,
literal|1
operator|<<
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|valueLength
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
name|valueLength
condition|;
operator|++
name|i
control|)
block|{
comment|// random so that even compressing codecs can't compress it
name|value
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"fld"
argument_list|,
name|value
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
call|(
name|int
call|)
argument_list|(
operator|(
literal|1L
operator|<<
literal|32
operator|)
operator|/
name|valueLength
operator|+
literal|100
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
operator|&&
name|i
operator|%
operator|(
name|numDocs
operator|/
literal|10
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" of "
operator|+
name|numDocs
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|endsWith
argument_list|(
literal|".fdt"
argument_list|)
condition|)
block|{
specifier|final
name|long
name|fileLength
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileLength
operator|>=
literal|1L
operator|<<
literal|32
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File length of "
operator|+
name|file
operator|+
literal|" : "
operator|+
name|fileLength
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No .fdt file larger than 4GB, test bug?"
argument_list|)
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Document
name|sd
init|=
name|rd
operator|.
name|document
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sd
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|valueRef
init|=
name|sd
operator|.
name|getBinaryValue
argument_list|(
literal|"fld"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|valueRef
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|,
name|valueRef
argument_list|)
expr_stmt|;
name|rd
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
block|}
end_class

end_unit

