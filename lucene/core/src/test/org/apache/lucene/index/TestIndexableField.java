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
name|io
operator|.
name|StringReader
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
name|TokenStream
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|search
operator|.
name|TermQuery
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
name|TopDocs
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
DECL|class|TestIndexableField
specifier|public
class|class
name|TestIndexableField
extends|extends
name|LuceneTestCase
block|{
DECL|class|MyField
specifier|private
class|class
name|MyField
implements|implements
name|IndexableField
block|{
DECL|field|counter
specifier|private
specifier|final
name|int
name|counter
decl_stmt|;
DECL|field|fieldType
specifier|private
specifier|final
name|IndexableFieldType
name|fieldType
init|=
operator|new
name|IndexableFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|indexed
parameter_list|()
block|{
return|return
operator|(
name|counter
operator|%
literal|10
operator|)
operator|!=
literal|3
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
operator|(
name|counter
operator|&
literal|1
operator|)
operator|==
literal|0
operator|||
operator|(
name|counter
operator|%
literal|10
operator|)
operator|==
literal|3
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|tokenized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|storeTermVectors
parameter_list|()
block|{
return|return
name|counter
operator|%
literal|2
operator|==
literal|1
operator|&&
name|counter
operator|%
literal|10
operator|!=
literal|9
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|storeTermVectorOffsets
parameter_list|()
block|{
return|return
name|counter
operator|%
literal|2
operator|==
literal|1
operator|&&
name|counter
operator|%
literal|10
operator|!=
literal|9
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|storeTermVectorPositions
parameter_list|()
block|{
return|return
name|counter
operator|%
literal|2
operator|==
literal|1
operator|&&
name|counter
operator|%
literal|10
operator|!=
literal|9
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfo
operator|.
name|IndexOptions
name|indexOptions
parameter_list|()
block|{
return|return
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocValues
operator|.
name|Type
name|docValueType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|method|MyField
specifier|public
name|MyField
parameter_list|(
name|int
name|counter
parameter_list|)
block|{
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"f"
operator|+
name|counter
return|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
literal|1.0f
operator|+
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
if|if
condition|(
operator|(
name|counter
operator|%
literal|10
operator|)
operator|==
literal|3
condition|)
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|bytes
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|bytes
index|[
name|idx
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|counter
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
specifier|final
name|int
name|fieldID
init|=
name|counter
operator|%
literal|10
decl_stmt|;
if|if
condition|(
name|fieldID
operator|!=
literal|3
operator|&&
name|fieldID
operator|!=
literal|7
condition|)
block|{
return|return
literal|"text "
operator|+
name|counter
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|%
literal|10
operator|==
literal|7
condition|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
literal|"text "
operator|+
name|counter
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
block|{
return|return
name|fieldType
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readerValue
argument_list|()
operator|!=
literal|null
condition|?
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|name
argument_list|()
argument_list|,
name|readerValue
argument_list|()
argument_list|)
else|:
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|name
argument_list|()
argument_list|,
operator|new
name|StringReader
argument_list|(
name|stringValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// Silly test showing how to index documents w/o using Lucene's core
comment|// Document nor Field class
DECL|method|testArbitraryFields
specifier|public
name|void
name|testArbitraryFields
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|w
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
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|27
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
literal|"TEST: "
operator|+
name|NUM_DOCS
operator|+
literal|" docs"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|fieldsPerDoc
init|=
operator|new
name|int
index|[
name|NUM_DOCS
index|]
decl_stmt|;
name|int
name|baseCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|docCount
init|=
literal|0
init|;
name|docCount
operator|<
name|NUM_DOCS
condition|;
name|docCount
operator|++
control|)
block|{
specifier|final
name|int
name|fieldCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
decl_stmt|;
name|fieldsPerDoc
index|[
name|docCount
index|]
operator|=
name|fieldCount
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|finalDocCount
init|=
name|docCount
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
literal|"TEST: "
operator|+
name|fieldCount
operator|+
literal|" fields in doc "
operator|+
name|docCount
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|finalBaseCount
init|=
name|baseCount
decl_stmt|;
name|baseCount
operator|+=
name|fieldCount
operator|-
literal|1
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Iterable
argument_list|<
name|IndexableField
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexableField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IndexableField
argument_list|>
argument_list|()
block|{
name|int
name|fieldUpto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|fieldUpto
operator|<
name|fieldCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexableField
name|next
parameter_list|()
block|{
assert|assert
name|fieldUpto
operator|<
name|fieldCount
assert|;
if|if
condition|(
name|fieldUpto
operator|==
literal|0
condition|)
block|{
name|fieldUpto
operator|=
literal|1
expr_stmt|;
return|return
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|finalDocCount
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MyField
argument_list|(
name|finalBaseCount
operator|+
operator|(
name|fieldUpto
operator|++
operator|-
literal|1
operator|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|NUM_DOCS
condition|;
name|id
operator|++
control|)
block|{
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
literal|"TEST: verify doc id="
operator|+
name|id
operator|+
literal|" ("
operator|+
name|fieldsPerDoc
index|[
name|id
index|]
operator|+
literal|" fields) counter="
operator|+
name|counter
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|int
name|docID
init|=
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
name|s
operator|.
name|doc
argument_list|(
name|docID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endCounter
init|=
name|counter
operator|+
name|fieldsPerDoc
index|[
name|id
index|]
decl_stmt|;
while|while
condition|(
name|counter
operator|<
name|endCounter
condition|)
block|{
specifier|final
name|String
name|name
init|=
literal|"f"
operator|+
name|counter
decl_stmt|;
specifier|final
name|int
name|fieldID
init|=
name|counter
operator|%
literal|10
decl_stmt|;
specifier|final
name|boolean
name|stored
init|=
operator|(
name|counter
operator|&
literal|1
operator|)
operator|==
literal|0
operator|||
name|fieldID
operator|==
literal|3
decl_stmt|;
specifier|final
name|boolean
name|binary
init|=
name|fieldID
operator|==
literal|3
decl_stmt|;
specifier|final
name|boolean
name|indexed
init|=
name|fieldID
operator|!=
literal|3
decl_stmt|;
specifier|final
name|String
name|stringValue
decl_stmt|;
if|if
condition|(
name|fieldID
operator|!=
literal|3
operator|&&
name|fieldID
operator|!=
literal|9
condition|)
block|{
name|stringValue
operator|=
literal|"text "
operator|+
name|counter
expr_stmt|;
block|}
else|else
block|{
name|stringValue
operator|=
literal|null
expr_stmt|;
block|}
comment|// stored:
if|if
condition|(
name|stored
condition|)
block|{
name|IndexableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"doc "
operator|+
name|id
operator|+
literal|" doesn't have field f"
operator|+
name|counter
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|binary
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"doc "
operator|+
name|id
operator|+
literal|" doesn't have field f"
operator|+
name|counter
argument_list|,
name|f
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|b
init|=
name|f
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|10
condition|;
name|idx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|idx
operator|+
name|counter
argument_list|)
argument_list|,
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|stringValue
operator|!=
literal|null
assert|;
name|assertEquals
argument_list|(
name|stringValue
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexed
condition|)
block|{
specifier|final
name|boolean
name|tv
init|=
name|counter
operator|%
literal|2
operator|==
literal|1
operator|&&
name|fieldID
operator|!=
literal|9
decl_stmt|;
if|if
condition|(
name|tv
condition|)
block|{
specifier|final
name|Terms
name|tfv
init|=
name|r
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
operator|.
name|terms
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tfv
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|tfv
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|counter
argument_list|)
argument_list|,
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
literal|1
argument_list|,
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"text"
argument_list|)
argument_list|,
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
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
literal|false
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
literal|1
argument_list|,
name|dpEnum
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dpEnum
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: offsets
block|}
else|else
block|{
name|Fields
name|vectors
init|=
name|r
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vectors
operator|==
literal|null
operator|||
name|vectors
operator|.
name|terms
argument_list|(
name|name
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
specifier|final
name|TopDocs
name|hits2
init|=
name|s
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits2
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|hits2
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
literal|""
operator|+
name|counter
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
specifier|final
name|TopDocs
name|hits3
init|=
name|s
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits3
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|hits3
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
name|counter
operator|++
expr_stmt|;
block|}
block|}
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

