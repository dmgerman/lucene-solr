begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|StringReader
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
name|EmptyTokenizer
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
name|index
operator|.
name|DirectoryReader
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
name|IndexReader
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
name|IndexWriter
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
name|IndexableField
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
name|RandomIndexWriter
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
name|Term
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
name|Query
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
name|ScoreDoc
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

begin_comment
comment|/**  * Tests {@link Document} class.  */
end_comment

begin_class
DECL|class|TestDocument
specifier|public
class|class
name|TestDocument
extends|extends
name|LuceneTestCase
block|{
DECL|field|binaryVal
name|String
name|binaryVal
init|=
literal|"this text will be stored as a byte array in the index"
decl_stmt|;
DECL|field|binaryVal2
name|String
name|binaryVal2
init|=
literal|"this text will be also stored as a byte array in the index"
decl_stmt|;
DECL|method|testBinaryField
specifier|public
name|void
name|testBinaryField
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
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
name|IndexableField
name|stringFld
init|=
operator|new
name|Field
argument_list|(
literal|"string"
argument_list|,
name|binaryVal
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|IndexableField
name|binaryFld
init|=
operator|new
name|StoredField
argument_list|(
literal|"binary"
argument_list|,
name|binaryVal
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexableField
name|binaryFld2
init|=
operator|new
name|StoredField
argument_list|(
literal|"binary"
argument_list|,
name|binaryVal2
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|stringFld
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|binaryFld
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binaryFld
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binaryFld
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|binaryFld
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|binaryTest
init|=
name|doc
operator|.
name|getBinaryValue
argument_list|(
literal|"binary"
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|binaryTest
operator|.
name|equals
argument_list|(
name|binaryVal
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|stringTest
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"string"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|binaryTest
operator|.
name|equals
argument_list|(
name|stringTest
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|binaryFld2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
index|[]
name|binaryTests
init|=
name|doc
operator|.
name|getBinaryValues
argument_list|(
literal|"binary"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|binaryTests
operator|.
name|length
argument_list|)
expr_stmt|;
name|binaryTest
operator|=
name|binaryTests
index|[
literal|0
index|]
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
name|String
name|binaryTest2
init|=
name|binaryTests
index|[
literal|1
index|]
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|binaryTest
operator|.
name|equals
argument_list|(
name|binaryTest2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binaryTest
operator|.
name|equals
argument_list|(
name|binaryVal
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binaryTest2
operator|.
name|equals
argument_list|(
name|binaryVal2
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"string"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"binary"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests {@link Document#removeField(String)} method for a brand new Document    * that has not been indexed yet.    *     * @throws Exception on error    */
DECL|method|testRemoveForNewDocument
specifier|public
name|void
name|testRemoveForNewDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|makeDocumentWithFields
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"keyword"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is
comment|// siltenlty ignored
name|doc
operator|.
name|removeFields
argument_list|(
literal|"keyword"
argument_list|)
expr_stmt|;
comment|// removing a field more than once
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is
comment|// siltenlty ignored
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"unindexed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"unstored"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeFields
argument_list|(
literal|"doesnotexists"
argument_list|)
expr_stmt|;
comment|// removing non-existing fields is
comment|// siltenlty ignored
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstructorExceptions
specifier|public
name|void
name|testConstructorExceptions
parameter_list|()
block|{
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
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|ft
argument_list|)
expr_stmt|;
comment|// okay
operator|new
name|StringField
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
comment|// okay
try|try
block|{
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
operator|new
name|FieldType
argument_list|()
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
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|ft
argument_list|)
expr_stmt|;
comment|// okay
try|try
block|{
name|FieldType
name|ft2
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft2
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft2
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|,
name|ft2
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
block|}
comment|/**    * Tests {@link Document#getValues(String)} method for a brand new Document    * that has not been indexed yet.    *     * @throws Exception on error    */
DECL|method|testGetValuesForNewDocument
specifier|public
name|void
name|testGetValuesForNewDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|doAssert
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests {@link Document#getValues(String)} method for a Document retrieved    * from an index.    *     * @throws Exception on error    */
DECL|method|testGetValuesForIndexedDocument
specifier|public
name|void
name|testGetValuesForIndexedDocument
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
name|writer
operator|.
name|addDocument
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// search for something that does exists
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|doAssert
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|testGetValues
specifier|public
name|void
name|testGetValues
parameter_list|()
block|{
name|Document
name|doc
init|=
name|makeDocumentWithFields
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test1"
block|,
literal|"test2"
block|}
argument_list|,
name|doc
operator|.
name|getValues
argument_list|(
literal|"keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test1"
block|,
literal|"test2"
block|}
argument_list|,
name|doc
operator|.
name|getValues
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test1"
block|,
literal|"test2"
block|}
argument_list|,
name|doc
operator|.
name|getValues
argument_list|(
literal|"unindexed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|doc
operator|.
name|getValues
argument_list|(
literal|"nope"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeDocumentWithFields
specifier|private
name|Document
name|makeDocumentWithFields
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|stored
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|stored
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"keyword"
argument_list|,
literal|"test2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"text"
argument_list|,
literal|"test1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"text"
argument_list|,
literal|"test2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test1"
argument_list|,
name|stored
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test2"
argument_list|,
name|stored
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"unstored"
argument_list|,
literal|"test1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"unstored"
argument_list|,
literal|"test2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|doAssert
specifier|private
name|void
name|doAssert
parameter_list|(
name|Document
name|doc
parameter_list|,
name|boolean
name|fromIndex
parameter_list|)
block|{
name|IndexableField
index|[]
name|keywordFieldValues
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"keyword"
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|textFieldValues
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|unindexedFieldValues
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"unindexed"
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|unstoredFieldValues
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"unstored"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|1
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|1
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|1
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|1
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFieldSetValue
specifier|public
name|void
name|testFieldSetValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"id1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"keyword"
argument_list|,
literal|"test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"id2"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"id3"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"keyword"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|result
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc2
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|doc2
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"id1"
argument_list|)
condition|)
name|result
operator||=
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"id2"
argument_list|)
condition|)
name|result
operator||=
literal|2
expr_stmt|;
elseif|else
if|if
condition|(
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"id3"
argument_list|)
condition|)
name|result
operator||=
literal|4
expr_stmt|;
else|else
name|fail
argument_list|(
literal|"unexpected id field"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|assertEquals
argument_list|(
literal|"did not see all IDs"
argument_list|,
literal|7
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3616
DECL|method|testInvalidFields
specifier|public
name|void
name|testInvalidFields
parameter_list|()
block|{
try|try
block|{
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|EmptyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

