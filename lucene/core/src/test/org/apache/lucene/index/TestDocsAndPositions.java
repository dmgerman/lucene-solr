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
name|document
operator|.
name|TextField
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
name|DocIdSetIterator
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
name|Bits
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestDocsAndPositions
specifier|public
class|class
name|TestDocsAndPositions
extends|extends
name|LuceneTestCase
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
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
name|fieldName
operator|=
literal|"field"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
comment|/**    * Simple testcase for {@link DocsAndPositionsEnum}    */
DECL|method|testPositionsSimple
specifier|public
name|void
name|testPositionsSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
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
literal|39
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
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|fieldName
argument_list|,
literal|"1 2 3 4 5 6 7 8 9 10 "
operator|+
literal|"1 2 3 4 5 6 7 8 9 10 "
operator|+
literal|"1 2 3 4 5 6 7 8 9 10 "
operator|+
literal|"1 2 3 4 5 6 7 8 9 10"
argument_list|,
name|customType
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|13
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomicReaderContext
range|:
name|topReaderContext
operator|.
name|leaves
argument_list|()
control|)
block|{
name|DocsAndPositionsEnum
name|docsAndPosEnum
init|=
name|getDocsAndPositions
argument_list|(
name|atomicReaderContext
operator|.
name|reader
argument_list|()
argument_list|,
name|bytes
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsAndPosEnum
argument_list|)
expr_stmt|;
if|if
condition|(
name|atomicReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
specifier|final
name|int
name|advance
init|=
name|docsAndPosEnum
operator|.
name|advance
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|atomicReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
do|do
block|{
name|String
name|msg
init|=
literal|"Advanced to: "
operator|+
name|advance
operator|+
literal|" current doc: "
operator|+
name|docsAndPosEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
comment|// TODO: + " usePayloads: " + usePayload;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|4
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|0
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|4
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|10
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|4
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|20
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|4
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|30
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|docsAndPosEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
do|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDocsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|getDocsAndPositions
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|termPositionsEnum
argument_list|(
literal|null
argument_list|,
name|fieldName
argument_list|,
name|bytes
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * this test indexes random numbers within a range into a field and checks    * their occurrences by searching for a number from that range selected at    * random. All positions for that number are saved up front and compared to    * the enums positions.    */
DECL|method|testRandomPositions
specifier|public
name|void
name|testRandomPositions
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
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
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
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|47
argument_list|)
decl_stmt|;
name|int
name|max
init|=
literal|1051
decl_stmt|;
name|int
name|term
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|Integer
index|[]
index|[]
name|positionsInDoc
init|=
operator|new
name|Integer
index|[
name|numDocs
index|]
index|[]
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
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
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|positions
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|131
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
name|num
condition|;
name|j
operator|++
control|)
block|{
name|int
name|nextInt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|nextInt
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextInt
operator|==
name|term
condition|)
block|{
name|positions
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|positions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|positions
operator|.
name|add
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|fieldName
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|positionsInDoc
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
literal|0
index|]
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|13
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|term
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomicReaderContext
range|:
name|topReaderContext
operator|.
name|leaves
argument_list|()
control|)
block|{
name|DocsAndPositionsEnum
name|docsAndPosEnum
init|=
name|getDocsAndPositions
argument_list|(
name|atomicReaderContext
operator|.
name|reader
argument_list|()
argument_list|,
name|bytes
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsAndPosEnum
argument_list|)
expr_stmt|;
name|int
name|initDoc
init|=
literal|0
decl_stmt|;
name|int
name|maxDoc
init|=
name|atomicReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
comment|// initially advance or do next doc
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|initDoc
operator|=
name|docsAndPosEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|initDoc
operator|=
name|docsAndPosEnum
operator|.
name|advance
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now run through the scorer and check if all positions are there...
do|do
block|{
name|int
name|docID
init|=
name|docsAndPosEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|Integer
index|[]
name|pos
init|=
name|positionsInDoc
index|[
name|atomicReaderContext
operator|.
name|docBase
operator|+
name|docID
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|pos
operator|.
name|length
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
comment|// number of positions read should be random - don't read all of them
comment|// allways
specifier|final
name|int
name|howMany
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
condition|?
name|pos
operator|.
name|length
operator|-
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
operator|.
name|length
argument_list|)
else|:
name|pos
operator|.
name|length
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
name|howMany
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"iteration: "
operator|+
name|i
operator|+
literal|" initDoc: "
operator|+
name|initDoc
operator|+
literal|" doc: "
operator|+
name|docID
operator|+
literal|" base: "
operator|+
name|atomicReaderContext
operator|.
name|docBase
operator|+
literal|" positions: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|pos
argument_list|)
comment|/* TODO: + " usePayloads: "                 + usePayload*/
argument_list|,
name|pos
index|[
name|j
index|]
operator|.
name|intValue
argument_list|()
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// once is a while advance
name|docsAndPosEnum
operator|.
name|advance
argument_list|(
name|docID
operator|+
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
operator|(
name|maxDoc
operator|-
name|docID
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|docsAndPosEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
do|;
block|}
block|}
name|reader
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
DECL|method|testRandomDocs
specifier|public
name|void
name|testRandomDocs
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
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
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
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|49
argument_list|)
decl_stmt|;
name|int
name|max
init|=
literal|15678
decl_stmt|;
name|int
name|term
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|int
index|[]
name|freqInDoc
init|=
operator|new
name|int
index|[
name|numDocs
index|]
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|199
condition|;
name|j
operator|++
control|)
block|{
name|int
name|nextInt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|nextInt
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextInt
operator|==
name|term
condition|)
block|{
name|freqInDoc
index|[
name|i
index|]
operator|++
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|fieldName
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|customType
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|13
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|term
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|topReaderContext
operator|.
name|leaves
argument_list|()
control|)
block|{
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|bytes
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|findNext
argument_list|(
name|freqInDoc
argument_list|,
name|context
operator|.
name|docBase
argument_list|,
name|context
operator|.
name|docBase
operator|+
name|maxDoc
argument_list|)
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|assertNull
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|assertNotNull
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|freqInDoc
index|[
name|context
operator|.
name|docBase
operator|+
name|j
index|]
operator|!=
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|j
argument_list|,
name|docsEnum
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docsEnum
operator|.
name|freq
argument_list|()
argument_list|,
name|freqInDoc
index|[
name|context
operator|.
name|docBase
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|int
name|next
init|=
name|findNext
argument_list|(
name|freqInDoc
argument_list|,
name|context
operator|.
name|docBase
operator|+
name|j
operator|+
literal|1
argument_list|,
name|context
operator|.
name|docBase
operator|+
name|maxDoc
argument_list|)
operator|-
name|context
operator|.
name|docBase
decl_stmt|;
name|int
name|advancedTo
init|=
name|docsEnum
operator|.
name|advance
argument_list|(
name|next
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|>=
name|maxDoc
condition|)
block|{
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|advancedTo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"advanced to: "
operator|+
name|advancedTo
operator|+
literal|" but should be<= "
operator|+
name|next
argument_list|,
name|next
operator|>=
name|advancedTo
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"docBase: "
operator|+
name|context
operator|.
name|docBase
operator|+
literal|" maxDoc: "
operator|+
name|maxDoc
operator|+
literal|" "
operator|+
name|docsEnum
operator|.
name|getClass
argument_list|()
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|docsEnum
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
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
DECL|method|findNext
specifier|private
specifier|static
name|int
name|findNext
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|max
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|pos
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docs
index|[
name|i
index|]
operator|!=
literal|0
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**    * tests retrieval of positions for terms that have a large number of    * occurrences to force test of buffer refill during positions iteration.    */
DECL|method|testLargeNumberOfPositions
specifier|public
name|void
name|testLargeNumberOfPositions
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
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
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
argument_list|)
decl_stmt|;
name|int
name|howMany
init|=
literal|1000
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
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
literal|39
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
name|howMany
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"even "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"odd "
argument_list|)
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|fieldName
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|customType
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
comment|// now do searches
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|13
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|"even"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomicReaderContext
range|:
name|topReaderContext
operator|.
name|leaves
argument_list|()
control|)
block|{
name|DocsAndPositionsEnum
name|docsAndPosEnum
init|=
name|getDocsAndPositions
argument_list|(
name|atomicReaderContext
operator|.
name|reader
argument_list|()
argument_list|,
name|bytes
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsAndPosEnum
argument_list|)
expr_stmt|;
name|int
name|initDoc
init|=
literal|0
decl_stmt|;
name|int
name|maxDoc
init|=
name|atomicReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
comment|// initially advance or do next doc
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|initDoc
operator|=
name|docsAndPosEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|initDoc
operator|=
name|docsAndPosEnum
operator|.
name|advance
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|msg
init|=
literal|"Iteration: "
operator|+
name|i
operator|+
literal|" initDoc: "
operator|+
name|initDoc
decl_stmt|;
comment|// TODO: + " payloads: " + usePayload;
name|assertEquals
argument_list|(
name|howMany
operator|/
literal|2
argument_list|,
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|howMany
condition|;
name|j
operator|+=
literal|2
control|)
block|{
name|assertEquals
argument_list|(
literal|"position missmatch index: "
operator|+
name|j
operator|+
literal|" with freq: "
operator|+
name|docsAndPosEnum
operator|.
name|freq
argument_list|()
operator|+
literal|" -- "
operator|+
name|msg
argument_list|,
name|j
argument_list|,
name|docsAndPosEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|reader
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
DECL|method|testDocsEnumStart
specifier|public
name|void
name|testDocsEnumStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
literal|"foo"
argument_list|,
literal|"bar"
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
name|DirectoryReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DocsEnum
name|disi
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|r
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|docid
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// now reuse and check again
name|TermsEnum
name|te
init|=
name|r
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|disi
operator|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
literal|null
argument_list|,
name|disi
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docid
operator|=
name|disi
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
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
DECL|method|testDocsAndPositionsEnumStart
specifier|public
name|void
name|testDocsAndPositionsEnumStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|newTextField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
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
name|DirectoryReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|disi
init|=
name|r
operator|.
name|termPositionsEnum
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|docid
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// now reuse and check again
name|TermsEnum
name|te
init|=
name|r
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|disi
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|disi
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docid
operator|=
name|disi
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
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

