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
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Fieldable
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
name|Similarity
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
name|MockRAMDirectory
import|;
end_import

begin_class
DECL|class|TestSegmentReader
specifier|public
class|class
name|TestSegmentReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|MockRAMDirectory
name|dir
decl_stmt|;
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|reader
specifier|private
name|SegmentReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|method|TestSegmentReader
specifier|public
name|TestSegmentReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|//TODO: Setup the reader w/ multiple documents
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|dir
operator|=
name|newDirectory
argument_list|(
name|newRandom
argument_list|()
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|testDoc
argument_list|)
decl_stmt|;
name|reader
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|testDoc
argument_list|)
operator|==
name|DocHelper
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|Document
name|result
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//There are 2 unstored fields on the document that are not preserved across writing
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|result
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|testDoc
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Fieldable
argument_list|>
name|fields
init|=
name|result
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|containsKey
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDelete
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|Document
name|docToDelete
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|docToDelete
argument_list|)
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|docToDelete
argument_list|)
decl_stmt|;
name|SegmentReader
name|deleteReader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|false
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|deleteReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deleteReader
operator|.
name|numDocs
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|deleteReader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deleteReader
operator|.
name|getDeletedDocs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deleteReader
operator|.
name|hasDeletions
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deleteReader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|deleteReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetFieldNameVariations
specifier|public
name|void
name|testGetFieldNameVariations
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|result
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|result
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//System.out.println("Name: " + s);
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
operator|==
literal|true
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|indexed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|result
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|indexed
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
operator|==
literal|true
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|unindexed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Get all indexed fields that are storing term vectors
name|result
operator|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|termvector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_NO_TERMVECTOR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|notermvector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTerms
specifier|public
name|void
name|testTerms
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldsEnum
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|field
decl_stmt|;
while|while
condition|(
operator|(
name|field
operator|=
name|fields
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|term
init|=
name|terms
operator|.
name|term
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|term
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|fieldValue
init|=
operator|(
name|String
operator|)
name|DocHelper
operator|.
name|nameValues
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldValue
operator|.
name|indexOf
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|DocsEnum
name|termDocs
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|termDocs
operator|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|DocHelper
operator|.
name|NO_NORMS_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|DocsAndPositionsEnum
name|positions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: prior rev of this test was failing to first
comment|// call next here:
name|assertTrue
argument_list|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|docID
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|nextPosition
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|IOException
block|{
comment|//TODO: Not sure how these work/should be tested
comment|/*     try {       byte [] norms = reader.norms(DocHelper.TEXT_FIELD_1_KEY);       System.out.println("Norms: " + norms);       assertTrue(norms != null);     } catch (IOException e) {       e.printStackTrace();       assertTrue(false);     } */
name|checkNorms
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNorms
specifier|public
specifier|static
name|void
name|checkNorms
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// test omit norms
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DocHelper
operator|.
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Fieldable
name|f
init|=
name|DocHelper
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|reader
operator|.
name|hasNorms
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
operator|!
name|f
operator|.
name|getOmitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|hasNorms
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
operator|!
name|DocHelper
operator|.
name|noNorms
operator|.
name|containsKey
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|hasNorms
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
comment|// test for fake norms of 1.0 or null depending on the flag
name|byte
index|[]
name|norms
init|=
name|reader
operator|.
name|norms
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|byte
name|norm1
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
operator|.
name|encodeNormValue
argument_list|(
literal|1.0f
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|norms
argument_list|)
expr_stmt|;
name|norms
operator|=
operator|new
name|byte
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|reader
operator|.
name|norms
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|norms
argument_list|,
literal|0
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|norms
index|[
name|j
index|]
argument_list|,
name|norm1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|testTermVectors
specifier|public
name|void
name|testTermVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|TermFreqVector
name|result
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|BytesRef
index|[]
name|terms
init|=
name|result
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|int
index|[]
name|freqs
init|=
name|result
operator|.
name|getTermFrequencies
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|length
operator|==
literal|3
operator|&&
name|freqs
operator|!=
literal|null
operator|&&
name|freqs
operator|.
name|length
operator|==
literal|3
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
name|freqs
index|[
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_TEXT
operator|.
name|indexOf
argument_list|(
name|term
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|TermFreqVector
index|[]
name|results
init|=
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|results
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"We do not have 3 term freq vectors, we have: "
operator|+
name|results
operator|.
name|length
argument_list|,
name|results
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

