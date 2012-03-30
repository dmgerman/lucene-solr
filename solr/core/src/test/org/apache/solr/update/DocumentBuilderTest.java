begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *   *  */
end_comment

begin_class
DECL|class|DocumentBuilderTest
specifier|public
class|class
name|DocumentBuilderTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildDocument
specifier|public
name|void
name|testBuildDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// undefined field
try|try
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"unknown field"
argument_list|,
literal|12345
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should be bad request"
argument_list|,
literal|400
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNullField
specifier|public
name|void
name|testNullField
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// make sure a null value is not indexed
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// make sure a null value is not indexed
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"123"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"unknown"
argument_list|,
literal|"something"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
try|try
block|{
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"added an unknown field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"should have document ID"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"doc=123"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|remove
argument_list|(
literal|"unknown"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"weight"
argument_list|,
literal|"not a number"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
try|try
block|{
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid 'float' field value"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"should have document ID"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"doc=123"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause is number format"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NumberFormatException
argument_list|)
expr_stmt|;
block|}
comment|// now make sure it is OK
name|doc
operator|.
name|setField
argument_list|(
literal|"weight"
argument_list|,
literal|"1.34"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiField
specifier|public
name|void
name|testMultiField
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// make sure a null value is not indexed
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"home"
argument_list|,
literal|"2.2,3.3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"home"
argument_list|)
argument_list|)
expr_stmt|;
comment|//contains the stored value and term vector, if there is one
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"home_0"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"home_1"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyFieldWithDocumentBoost
specifier|public
name|void
name|testCopyFieldWithDocumentBoost
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setDocumentBoost
argument_list|(
literal|3f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"mytitle"
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"title_stringNoNorms has the omitNorms attribute set to true, if the boost is different than 1.0, it will fail"
argument_list|,
literal|1.0f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"It is OK that title has a boost of 3"
argument_list|,
literal|3.0f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyFieldWithFieldBoost
specifier|public
name|void
name|testCopyFieldWithFieldBoost
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"mytitle"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"title_stringNoNorms has the omitNorms attribute set to true, if the boost is different than 1.0, it will fail"
argument_list|,
literal|1.0f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"title_stringNoNorms"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"It is OK that title has a boost of 3"
argument_list|,
literal|3.0f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithPolyFieldsAndFieldBoost
specifier|public
name|void
name|testWithPolyFieldsAndFieldBoost
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store_1_coordinate"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_currency"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_amount_raw"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"store"
argument_list|,
literal|"40.7143,-74.006"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"amount"
argument_list|,
literal|"10.5"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"amount"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
argument_list|)
expr_stmt|;
comment|//NOTE: As the subtypes have omitNorm=true, they must have boost=1F, otherwise this is going to fail when adding the doc to Lucene.
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"store_1_coordinate"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_currency"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_amount_raw"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithPolyFieldsAndDocumentBoost
specifier|public
name|void
name|testWithPolyFieldsAndDocumentBoost
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"store_1_coordinate"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_currency"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_amount_raw"
argument_list|)
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setDocumentBoost
argument_list|(
literal|3.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"store"
argument_list|,
literal|"40.7143,-74.006"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"amount"
argument_list|,
literal|"10.5"
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
argument_list|)
expr_stmt|;
comment|//NOTE: As the subtypes have omitNorm=true, they must have boost=1F, otherwise this is going to fail when adding the doc to Lucene.
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"store_0_coordinate"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"store_1_coordinate"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_currency"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1f
operator|==
name|out
operator|.
name|getField
argument_list|(
literal|"amount"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"_amount_raw"
argument_list|)
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Its ok to boost a field if it has norms    */
DECL|method|testBoost
specifier|public
name|void
name|testBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|XmlDoc
name|xml
init|=
operator|new
name|XmlDoc
argument_list|()
decl_stmt|;
name|xml
operator|.
name|xml
operator|=
literal|"<doc>"
operator|+
literal|"<field name=\"id\">0</field>"
operator|+
literal|"<field name=\"title\" boost=\"3.0\">mytitle</field>"
operator|+
literal|"</doc>"
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|add
argument_list|(
name|xml
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Its not ok to boost a field if it omits norms    */
DECL|method|testBoostOmitNorms
specifier|public
name|void
name|testBoostOmitNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|XmlDoc
name|xml
init|=
operator|new
name|XmlDoc
argument_list|()
decl_stmt|;
name|xml
operator|.
name|xml
operator|=
literal|"<doc>"
operator|+
literal|"<field name=\"id\">ignore_exception</field>"
operator|+
literal|"<field name=\"title_stringNoNorms\" boost=\"3.0\">mytitle</field>"
operator|+
literal|"</doc>"
expr_stmt|;
try|try
block|{
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|add
argument_list|(
name|xml
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception for boosting omit norms field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
comment|/**    * Its ok to supply a document boost even if a field omits norms    */
DECL|method|testDocumentBoostOmitNorms
specifier|public
name|void
name|testDocumentBoostOmitNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|XmlDoc
name|xml
init|=
operator|new
name|XmlDoc
argument_list|()
decl_stmt|;
name|xml
operator|.
name|xml
operator|=
literal|"<doc boost=\"3.0\">"
operator|+
literal|"<field name=\"id\">2</field>"
operator|+
literal|"<field name=\"title_stringNoNorms\">mytitle</field>"
operator|+
literal|"</doc>"
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|add
argument_list|(
name|xml
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

