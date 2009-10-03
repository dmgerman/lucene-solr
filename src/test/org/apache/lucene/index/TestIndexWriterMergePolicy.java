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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|WhitespaceAnalyzer
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
name|RAMDirectory
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
name|_TestUtil
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
operator|new
name|RAMDirectory
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
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
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
operator|new
name|RAMDirectory
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
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
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
operator|new
name|RAMDirectory
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
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|LogDocMergePolicy
name|mp
init|=
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|mp
operator|.
name|setMinMergeDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
name|mp
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
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
expr_stmt|;
name|mp
operator|.
name|setMinMergeDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|10
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
operator|new
name|RAMDirectory
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
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
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
operator|new
name|RAMDirectory
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
literal|true
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
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
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|10
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
name|checkInvariants
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
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
operator|new
name|RAMDirectory
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
literal|true
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|100
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
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|5
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
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
throws|throws
name|IOException
block|{
name|_TestUtil
operator|.
name|syncConcurrentMerges
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|int
name|maxBufferedDocs
init|=
name|writer
operator|.
name|getMaxBufferedDocs
argument_list|()
decl_stmt|;
name|int
name|mergeFactor
init|=
name|writer
operator|.
name|getMergeFactor
argument_list|()
decl_stmt|;
name|int
name|maxMergeDocs
init|=
name|writer
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
name|String
index|[]
name|files
init|=
name|writer
operator|.
name|getDirectory
argument_list|()
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|int
name|segmentCfsCount
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".cfs"
argument_list|)
condition|)
block|{
name|segmentCfsCount
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|segmentCount
argument_list|,
name|segmentCfsCount
argument_list|)
expr_stmt|;
block|}
comment|/*   private void printSegmentDocCounts(IndexWriter writer) {     int segmentCount = writer.getSegmentCount();     System.out.println("" + segmentCount + " segments total");     for (int i = 0; i< segmentCount; i++) {       System.out.println("  segment " + i + " has " + writer.getDocCount(i)           + " docs");     }   }   */
block|}
end_class

end_unit

