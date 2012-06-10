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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|*
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|codecs
operator|.
name|TermVectorsReader
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
DECL|class|TestTermVectorsReader
specifier|public
class|class
name|TestTermVectorsReader
extends|extends
name|LuceneTestCase
block|{
comment|//Must be lexicographically sorted, will do in setup, versus trying to maintain here
DECL|field|testFields
specifier|private
name|String
index|[]
name|testFields
init|=
block|{
literal|"f1"
block|,
literal|"f2"
block|,
literal|"f3"
block|,
literal|"f4"
block|}
decl_stmt|;
DECL|field|testFieldsStorePos
specifier|private
name|boolean
index|[]
name|testFieldsStorePos
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|true
block|,
literal|false
block|}
decl_stmt|;
DECL|field|testFieldsStoreOff
specifier|private
name|boolean
index|[]
name|testFieldsStoreOff
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|false
block|,
literal|true
block|}
decl_stmt|;
DECL|field|testTerms
specifier|private
name|String
index|[]
name|testTerms
init|=
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|}
decl_stmt|;
DECL|field|positions
specifier|private
name|int
index|[]
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|testTerms
operator|.
name|length
index|]
index|[]
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|seg
specifier|private
name|SegmentInfoPerCommit
name|seg
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
operator|new
name|FieldInfo
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
DECL|field|TERM_FREQ
specifier|private
specifier|static
name|int
name|TERM_FREQ
init|=
literal|3
decl_stmt|;
DECL|class|TestToken
specifier|private
class|class
name|TestToken
implements|implements
name|Comparable
argument_list|<
name|TestToken
argument_list|>
block|{
DECL|field|text
name|String
name|text
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TestToken
name|other
parameter_list|)
block|{
return|return
name|pos
operator|-
name|other
operator|.
name|pos
return|;
block|}
block|}
DECL|field|tokens
name|TestToken
index|[]
name|tokens
init|=
operator|new
name|TestToken
index|[
name|testTerms
operator|.
name|length
operator|*
name|TERM_FREQ
index|]
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
comment|/*     for (int i = 0; i< testFields.length; i++) {       fieldInfos.add(testFields[i], true, true, testFieldsStorePos[i], testFieldsStoreOff[i]);     }     */
name|Arrays
operator|.
name|sort
argument_list|(
name|testTerms
argument_list|)
expr_stmt|;
name|int
name|tokenUpto
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|TERM_FREQ
index|]
expr_stmt|;
comment|// first position must be 0
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TERM_FREQ
condition|;
name|j
operator|++
control|)
block|{
comment|// positions are always sorted in increasing order
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|j
operator|*
literal|10
operator|+
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|10
argument_list|)
expr_stmt|;
name|TestToken
name|token
init|=
name|tokens
index|[
name|tokenUpto
operator|++
index|]
operator|=
operator|new
name|TestToken
argument_list|()
decl_stmt|;
name|token
operator|.
name|text
operator|=
name|testTerms
index|[
name|i
index|]
expr_stmt|;
name|token
operator|.
name|pos
operator|=
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|token
operator|.
name|startOffset
operator|=
name|j
operator|*
literal|10
expr_stmt|;
name|token
operator|.
name|endOffset
operator|=
name|j
operator|*
literal|10
operator|+
name|testTerms
index|[
name|i
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
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
name|MyAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
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
argument_list|)
decl_stmt|;
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
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
if|if
condition|(
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
block|{
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
operator|!
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
block|{
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
block|{
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|testFields
index|[
name|i
index|]
argument_list|,
literal|""
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Create 5 documents for testing, they all have the same
comment|//terms
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|seg
operator|=
name|writer
operator|.
name|newestSegment
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fieldInfos
operator|=
name|_TestUtil
operator|.
name|getFieldInfos
argument_list|(
name|seg
operator|.
name|info
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|class|MyTokenizer
specifier|private
class|class
name|MyTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|tokenUpto
specifier|private
name|int
name|tokenUpto
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|method|MyTokenizer
specifier|public
name|MyTokenizer
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|tokenUpto
operator|>=
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
specifier|final
name|TestToken
name|testToken
init|=
name|tokens
index|[
name|tokenUpto
operator|++
index|]
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
name|testToken
operator|.
name|text
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|testToken
operator|.
name|startOffset
argument_list|,
name|testToken
operator|.
name|endOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenUpto
operator|>
literal|1
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|testToken
operator|.
name|pos
operator|-
name|tokens
index|[
name|tokenUpto
operator|-
literal|2
index|]
operator|.
name|pos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|testToken
operator|.
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|class|MyAnalyzer
specifier|private
class|class
name|MyAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MyTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Check to see the files were created properly in setup
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
for|for
control|(
name|IndexReader
name|r
range|:
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
control|)
block|{
name|SegmentInfoPerCommit
name|s
init|=
operator|(
operator|(
name|SegmentReader
operator|)
name|r
operator|)
operator|.
name|getSegmentInfo
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|SegmentReader
operator|)
name|r
operator|)
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|hasVectors
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReader
specifier|public
name|void
name|testReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
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
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Terms
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|terms
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testTerms
operator|.
name|length
argument_list|,
name|vector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|text
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertEquals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDocsEnum
specifier|public
name|void
name|testDocsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
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
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|Terms
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|terms
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testTerms
operator|.
name|length
argument_list|,
name|vector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|text
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertEquals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|termsEnum
argument_list|,
literal|null
argument_list|,
name|docsEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|==
operator|-
literal|1
operator|||
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|docsEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPositionReader
specifier|public
name|void
name|testPositionReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
index|[]
name|terms
decl_stmt|;
name|Terms
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testTerms
operator|.
name|length
argument_list|,
name|vector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
literal|null
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|text
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertEquals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dpEnum
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|dpEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|==
operator|-
literal|1
operator|||
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dpEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|,
name|positions
index|[
name|i
index|]
operator|.
name|length
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
name|positions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dpEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|=
name|dpEnum
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|==
operator|-
literal|1
operator|||
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dpEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dpEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|,
name|positions
index|[
name|i
index|]
operator|.
name|length
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
name|positions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
operator|*
literal|10
argument_list|,
name|dpEnum
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
operator|*
literal|10
operator|+
name|testTerms
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|dpEnum
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dpEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Terms
name|freqVector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
name|testFields
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|//no pos, no offset
name|assertNotNull
argument_list|(
name|freqVector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testTerms
operator|.
name|length
argument_list|,
name|freqVector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|termsEnum
operator|=
name|freqVector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|termsEnum
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|text
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertEquals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testOffsetReader
specifier|public
name|void
name|testOffsetReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Terms
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testTerms
operator|.
name|length
argument_list|,
name|vector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
literal|null
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|text
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dpEnum
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dpEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|,
name|positions
index|[
name|i
index|]
operator|.
name|length
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
name|positions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dpEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dpEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dpEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|,
name|positions
index|[
name|i
index|]
operator|.
name|length
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
name|positions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
operator|*
literal|10
argument_list|,
name|dpEnum
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
operator|*
literal|10
operator|+
name|testTerms
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|,
name|dpEnum
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dpEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Make sure exceptions and bad params are handled appropriately    */
DECL|method|testBadParams
specifier|public
name|void
name|testBadParams
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Bad document number, good field number
name|reader
operator|.
name|get
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//good document number, bad field
name|Terms
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
literal|"f50"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

