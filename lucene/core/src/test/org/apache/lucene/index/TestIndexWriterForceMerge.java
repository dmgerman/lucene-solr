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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|MockTokenizer
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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

begin_class
DECL|class|TestIndexWriterForceMerge
specifier|public
class|class
name|TestIndexWriterForceMerge
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPartialMerge
specifier|public
name|void
name|testPartialMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|incrMin
init|=
name|TEST_NIGHTLY
condition|?
literal|15
else|:
literal|40
decl_stmt|;
for|for
control|(
name|int
name|numDocs
init|=
literal|10
init|;
name|numDocs
operator|<
literal|500
condition|;
name|numDocs
operator|+=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|incrMin
argument_list|,
literal|5
operator|*
name|incrMin
argument_list|)
control|)
block|{
name|LogDocMergePolicy
name|ldmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|ldmp
operator|.
name|setMinMergeDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ldmp
operator|.
name|setMergeFactor
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|ldmp
argument_list|)
argument_list|)
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
name|numDocs
condition|;
name|j
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|segCount
init|=
name|sis
operator|.
name|size
argument_list|()
decl_stmt|;
name|ldmp
operator|=
operator|new
name|LogDocMergePolicy
argument_list|()
expr_stmt|;
name|ldmp
operator|.
name|setMergeFactor
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|ldmp
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sis
operator|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
expr_stmt|;
specifier|final
name|int
name|optSegCount
init|=
name|sis
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|segCount
operator|<
literal|3
condition|)
name|assertEquals
argument_list|(
name|segCount
argument_list|,
name|optSegCount
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|optSegCount
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMaxNumSegments2
specifier|public
name|void
name|testMaxNumSegments2
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|LogDocMergePolicy
name|ldmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|ldmp
operator|.
name|setMinMergeDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ldmp
operator|.
name|setMergeFactor
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
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
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|ldmp
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
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
literal|19
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|segCount
init|=
name|sis
operator|.
name|size
argument_list|()
decl_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|sis
operator|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
expr_stmt|;
specifier|final
name|int
name|optSegCount
init|=
name|sis
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|segCount
operator|<
literal|7
condition|)
name|assertEquals
argument_list|(
name|segCount
argument_list|,
name|optSegCount
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
literal|"seg: "
operator|+
name|segCount
argument_list|,
literal|7
argument_list|,
name|optSegCount
argument_list|)
expr_stmt|;
block|}
name|writer
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
comment|/**    * Make sure forceMerge doesn't use any more than 1X    * starting index size as its temporary free space    * required.    */
DECL|method|testForceMergeTempSpaceUsage
specifier|public
name|void
name|testForceMergeTempSpaceUsage
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
argument_list|()
decl_stmt|;
comment|// don't use MockAnalyzer, variable length payloads can cause merge to make things bigger,
comment|// since things are optimized for fixed length case. this is a problem for MemoryPF's encoding.
comment|// (it might have other problems too)
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"TEST: config1="
operator|+
name|writer
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|500
condition|;
name|j
operator|++
control|)
block|{
name|TestIndexWriter
operator|.
name|addDocWithIndex
argument_list|(
name|writer
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
comment|// force one extra segment w/ different doc store so
comment|// we see the doc stores get merged
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TestIndexWriter
operator|.
name|addDocWithIndex
argument_list|(
name|writer
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|startDiskUsage
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|startDiskUsage
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|f
argument_list|)
expr_stmt|;
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
name|f
operator|+
literal|": "
operator|+
name|dir
operator|.
name|fileLength
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"TEST: start disk usage = "
operator|+
name|startDiskUsage
argument_list|)
expr_stmt|;
block|}
name|String
name|startListing
init|=
name|listFiles
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dir
operator|.
name|resetMaxUsedSizeInBytes
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setTrackDiskUsage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"TEST: config2="
operator|+
name|writer
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|finalDiskUsage
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|finalDiskUsage
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|f
argument_list|)
expr_stmt|;
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
name|f
operator|+
literal|": "
operator|+
name|dir
operator|.
name|fileLength
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"TEST: final disk usage = "
operator|+
name|finalDiskUsage
argument_list|)
expr_stmt|;
block|}
comment|// The result of the merged index is often smaller, but sometimes it could
comment|// be bigger (compression slightly changes, Codec changes etc.). Therefore
comment|// we compare the temp space used to the max of the initial and final index
comment|// size
name|long
name|maxStartFinalDiskUsage
init|=
name|Math
operator|.
name|max
argument_list|(
name|startDiskUsage
argument_list|,
name|finalDiskUsage
argument_list|)
decl_stmt|;
name|long
name|maxDiskUsage
init|=
name|dir
operator|.
name|getMaxUsedSizeInBytes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"forceMerge used too much temporary space: starting usage was "
operator|+
name|startDiskUsage
operator|+
literal|" bytes; final usage was "
operator|+
name|finalDiskUsage
operator|+
literal|" bytes; max temp usage was "
operator|+
name|maxDiskUsage
operator|+
literal|" but should have been at most "
operator|+
operator|(
literal|4
operator|*
name|maxStartFinalDiskUsage
operator|)
operator|+
literal|" (= 4X starting usage), BEFORE="
operator|+
name|startListing
operator|+
literal|"AFTER="
operator|+
name|listFiles
argument_list|(
name|dir
argument_list|)
argument_list|,
name|maxDiskUsage
operator|<=
literal|4
operator|*
name|maxStartFinalDiskUsage
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// print out listing of files and sizes, but recurse into CFS to debug nested files there.
DECL|method|listFiles
specifier|private
name|String
name|listFiles
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentInfos
name|infos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|infos
control|)
block|{
for|for
control|(
name|String
name|file
range|:
name|info
operator|.
name|files
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%-20s%d%n"
argument_list|,
name|file
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
try|try
init|(
name|Directory
name|cfs
init|=
name|info
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|compoundFormat
argument_list|()
operator|.
name|getCompoundReader
argument_list|(
name|dir
argument_list|,
name|info
operator|.
name|info
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|file
range|:
name|cfs
operator|.
name|listAll
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|" |- (inside compound file) %-20s%d%n"
argument_list|,
name|file
argument_list|,
name|cfs
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Test calling forceMerge(1, false) whereby forceMerge is kicked
comment|// off but we don't wait for it to finish (but
comment|// writer.close()) does wait
DECL|method|testBackgroundForceMerge
specifier|public
name|void
name|testBackgroundForceMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|51
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|pass
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Get another segment to flush so we can verify it is
comment|// NOT included in the merging
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|infos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

