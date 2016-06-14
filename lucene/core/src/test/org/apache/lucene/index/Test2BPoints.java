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
name|codecs
operator|.
name|Codec
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
name|LongPoint
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
name|search
operator|.
name|IndexSearcher
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
name|FSDirectory
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
name|Monster
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

begin_comment
comment|// e.g. run like this: ant test -Dtestcase=Test2BPoints -Dtests.nightly=true -Dtests.verbose=true -Dtests.monster=true
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//   or: python -u /l/util/src/python/repeatLuceneTest.py -heap 6g -once -nolog -tmpDir /b/tmp -logDir /l/logs Test2BPoints.test2D -verbose
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|,
literal|"Compressing"
block|}
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|365
operator|*
literal|24
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
comment|// hopefully ~1 year is long enough ;)
annotation|@
name|Monster
argument_list|(
literal|"takes at least 4 hours and consumes many GB of temp disk space"
argument_list|)
DECL|class|Test2BPoints
specifier|public
class|class
name|Test2BPoints
extends|extends
name|LuceneTestCase
block|{
DECL|method|test1D
specifier|public
name|void
name|test1D
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|createTempDir
argument_list|(
literal|"2BPoints1D"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
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
name|setCodec
argument_list|(
name|getCodec
argument_list|()
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
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setMaxMergesAndThreads
argument_list|(
literal|6
argument_list|,
literal|3
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
name|iwc
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
name|int
name|numDocs
init|=
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|26
operator|)
operator|+
literal|1
decl_stmt|;
name|int
name|counter
init|=
literal|0
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
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|26
condition|;
name|j
operator|++
control|)
block|{
name|long
name|x
init|=
operator|(
operator|(
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|<<
literal|32
operator|)
operator|)
operator||
operator|(
name|long
operator|)
name|counter
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongPoint
argument_list|(
literal|"long"
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
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
literal|100000
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
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|s
operator|.
name|count
argument_list|(
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"long"
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|()
operator|.
name|size
argument_list|(
literal|"long"
argument_list|)
operator|>
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now CheckIndex"
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|test2D
specifier|public
name|void
name|test2D
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|createTempDir
argument_list|(
literal|"2BPoints2D"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
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
name|setCodec
argument_list|(
name|getCodec
argument_list|()
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
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setMaxMergesAndThreads
argument_list|(
literal|6
argument_list|,
literal|3
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
name|iwc
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
name|int
name|numDocs
init|=
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|26
operator|)
operator|+
literal|1
decl_stmt|;
name|int
name|counter
init|=
literal|0
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
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|26
condition|;
name|j
operator|++
control|)
block|{
name|long
name|x
init|=
operator|(
operator|(
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|<<
literal|32
operator|)
operator|)
operator||
operator|(
name|long
operator|)
name|counter
decl_stmt|;
name|long
name|y
init|=
operator|(
operator|(
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|<<
literal|32
operator|)
operator|)
operator||
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongPoint
argument_list|(
literal|"long"
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
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
literal|100000
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
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|s
operator|.
name|count
argument_list|(
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"long"
argument_list|,
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MIN_VALUE
block|,
name|Long
operator|.
name|MIN_VALUE
block|}
argument_list|,
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MAX_VALUE
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|()
operator|.
name|size
argument_list|(
literal|"long"
argument_list|)
operator|>
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now CheckIndex"
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getCodec
specifier|private
specifier|static
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene62"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

