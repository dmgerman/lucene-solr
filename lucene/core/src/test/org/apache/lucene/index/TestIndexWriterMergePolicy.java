begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestIndexWriterMergePolicy
specifier|public
class|class
name|TestIndexWriterMergePolicy
extends|extends
name|LuceneTestCase
block|{
comment|// Test the normal case
DECL|method|testNormalCase
specifier|public
name|void
name|testNormalCase
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test to see if there is over merge
DECL|method|testNoOverMerge
specifier|public
name|void
name|testNoOverMerge
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|noOverMerge
init|=
literal|false
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
if|if
condition|(
name|writer
operator|.
name|getNumBufferedDocuments
argument_list|()
operator|+
name|writer
operator|.
name|getSegmentCount
argument_list|()
operator|>=
literal|18
condition|)
block|{
name|noOverMerge
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|noOverMerge
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test the case where flush is forced after every addDoc
DECL|method|testForceFlush
specifier|public
name|void
name|testForceFlush
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
name|LogDocMergePolicy
name|mp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|mp
operator|.
name|setMinMergeDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|mp
operator|.
name|setMergeFactor
argument_list|(
literal|10
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mp
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mp
operator|=
operator|new
name|LogDocMergePolicy
argument_list|()
expr_stmt|;
name|mp
operator|.
name|setMergeFactor
argument_list|(
literal|10
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
name|TEST_VERSION_CURRENT
argument_list|,
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
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
argument_list|)
expr_stmt|;
name|mp
operator|.
name|setMinMergeDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test the case where mergeFactor changes
DECL|method|testMergeFactorChange
specifier|public
name|void
name|testMergeFactorChange
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
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
literal|250
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|5
argument_list|)
expr_stmt|;
comment|// merge policy only fixes segments on levels where merges
comment|// have been triggered, so check invariants after all adds
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test the case where both mergeFactor and maxBufferedDocs change
DECL|method|testMaxBufferedDocsChange
specifier|public
name|void
name|testMaxBufferedDocsChange
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|101
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// leftmost* segment has 1 doc
comment|// rightmost* segment has 100 docs
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
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
name|TEST_VERSION_CURRENT
argument_list|,
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
name|setMaxBufferedDocs
argument_list|(
literal|101
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
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
name|setMergeFactor
argument_list|(
literal|10
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
name|TEST_VERSION_CURRENT
argument_list|,
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
name|setMaxBufferedDocs
argument_list|(
literal|10
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
name|SerialMergeScheduler
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// merge policy only fixes segments on levels where merges
comment|// have been triggered, so check invariants after all adds
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
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|100
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
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
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test the case where a merge results in no doc at all
DECL|method|testMergeDocCount0
specifier|public
name|void
name|testMergeDocCount0
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
name|LogDocMergePolicy
name|ldmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|ldmp
operator|.
name|setMergeFactor
argument_list|(
literal|100
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
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|10
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
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|250
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// delete some docs without merging
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
name|TEST_VERSION_CURRENT
argument_list|,
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
name|setMaxBufferedDocs
argument_list|(
literal|10
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
expr_stmt|;
comment|// merge factor is changed, so check invariants after all adds
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
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
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
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
name|newTextField
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|checkInvariants
specifier|private
name|void
name|checkInvariants
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|waitForMerges
argument_list|()
expr_stmt|;
name|int
name|maxBufferedDocs
init|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMaxBufferedDocs
argument_list|()
decl_stmt|;
name|int
name|mergeFactor
init|=
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|getMergeFactor
argument_list|()
decl_stmt|;
name|int
name|maxMergeDocs
init|=
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|getMaxMergeDocs
argument_list|()
decl_stmt|;
name|int
name|ramSegmentCount
init|=
name|writer
operator|.
name|getNumBufferedDocuments
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ramSegmentCount
operator|<
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|int
name|lowerBound
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|upperBound
init|=
name|maxBufferedDocs
decl_stmt|;
name|int
name|numSegments
init|=
literal|0
decl_stmt|;
name|int
name|segmentCount
init|=
name|writer
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|segmentCount
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|docCount
init|=
name|writer
operator|.
name|getDocCount
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"docCount="
operator|+
name|docCount
operator|+
literal|" lowerBound="
operator|+
name|lowerBound
operator|+
literal|" upperBound="
operator|+
name|upperBound
operator|+
literal|" i="
operator|+
name|i
operator|+
literal|" segmentCount="
operator|+
name|segmentCount
operator|+
literal|" index="
operator|+
name|writer
operator|.
name|segString
argument_list|()
operator|+
literal|" config="
operator|+
name|writer
operator|.
name|getConfig
argument_list|()
argument_list|,
name|docCount
operator|>
name|lowerBound
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCount
operator|<=
name|upperBound
condition|)
block|{
name|numSegments
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|upperBound
operator|*
name|mergeFactor
operator|<=
name|maxMergeDocs
condition|)
block|{
name|assertTrue
argument_list|(
literal|"maxMergeDocs="
operator|+
name|maxMergeDocs
operator|+
literal|"; numSegments="
operator|+
name|numSegments
operator|+
literal|"; upperBound="
operator|+
name|upperBound
operator|+
literal|"; mergeFactor="
operator|+
name|mergeFactor
operator|+
literal|"; segs="
operator|+
name|writer
operator|.
name|segString
argument_list|()
operator|+
literal|" config="
operator|+
name|writer
operator|.
name|getConfig
argument_list|()
argument_list|,
name|numSegments
operator|<
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
do|do
block|{
name|lowerBound
operator|=
name|upperBound
expr_stmt|;
name|upperBound
operator|*=
name|mergeFactor
expr_stmt|;
block|}
do|while
condition|(
name|docCount
operator|>
name|upperBound
condition|)
do|;
name|numSegments
operator|=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|upperBound
operator|*
name|mergeFactor
operator|<=
name|maxMergeDocs
condition|)
block|{
name|assertTrue
argument_list|(
name|numSegments
operator|<
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|EPSILON
specifier|private
specifier|static
specifier|final
name|double
name|EPSILON
init|=
literal|1E
operator|-
literal|14
decl_stmt|;
DECL|method|testSetters
specifier|public
name|void
name|testSetters
parameter_list|()
block|{
name|assertSetters
argument_list|(
operator|new
name|LogByteSizeMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|assertSetters
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSetters
specifier|private
name|void
name|assertSetters
parameter_list|(
name|MergePolicy
name|lmp
parameter_list|)
block|{
name|lmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
literal|2.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|lmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|lmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|lmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
try|try
block|{
name|lmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
operator|-
literal|2.0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
comment|// TODO: Add more checks for other non-double setters!
block|}
block|}
end_class

end_unit

