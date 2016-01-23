begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

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
name|SchemaField
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|RequiredFieldsTest
specifier|public
class|class
name|RequiredFieldsTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema-required-fields.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testRequiredFieldsConfig
specifier|public
name|void
name|testRequiredFieldsConfig
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
name|SchemaField
name|uniqueKey
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
comment|// Make sure the uniqueKey is required
name|assertTrue
argument_list|(
name|uniqueKey
operator|.
name|isRequired
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getRequiredFields
argument_list|()
operator|.
name|contains
argument_list|(
name|uniqueKey
argument_list|)
argument_list|)
expr_stmt|;
comment|// we specified one required field, but all devault valued fields are also required
name|Collection
argument_list|<
name|SchemaField
argument_list|>
name|requiredFields
init|=
name|schema
operator|.
name|getRequiredFields
argument_list|()
decl_stmt|;
name|int
name|numDefaultFields
init|=
name|schema
operator|.
name|getFieldsWithDefaultValue
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numDefaultFields
operator|+
literal|1
operator|+
literal|1
argument_list|,
name|requiredFields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// also the uniqueKey
block|}
DECL|method|testRequiredFieldsSingleAdd
specifier|public
name|void
name|testRequiredFieldsSingleAdd
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
comment|// Add a single document
name|assertU
argument_list|(
literal|"adding document"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"529"
argument_list|,
literal|"name"
argument_list|,
literal|"document with id, name, and subject"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check it it is in the index
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// Add another document without the required subject field, which
comment|// has a configured defaultValue of "Stuff"
name|assertU
argument_list|(
literal|"adding a doc without field w/ configured default"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"530"
argument_list|,
literal|"name"
argument_list|,
literal|"document with id and name"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add another document without a subject, which has a default in schema
name|String
name|subjectDefault
init|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"subject"
argument_list|)
operator|.
name|getDefaultValue
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"subject has no default value"
argument_list|,
name|subjectDefault
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should find one with subject="
operator|+
name|subjectDefault
argument_list|,
name|req
argument_list|(
literal|"id:530 subject:"
operator|+
name|subjectDefault
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// Add another document without a required name, which has no default
name|assertNull
argument_list|(
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFailedU
argument_list|(
literal|"adding doc without required field"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"531"
argument_list|,
literal|"subject"
argument_list|,
literal|"no name document"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check to make sure this submission did not succeed
name|assertQ
argument_list|(
literal|"should not find any"
argument_list|,
name|req
argument_list|(
literal|"id:531"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddMultipleDocumentsWithErrors
specifier|public
name|void
name|testAddMultipleDocumentsWithErrors
parameter_list|()
block|{
comment|//Add three documents at once to make sure the baseline succeeds
name|assertU
argument_list|(
literal|"adding 3 documents"
argument_list|,
literal|"<add>"
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"601"
argument_list|,
literal|"name"
argument_list|,
literal|"multiad one"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"602"
argument_list|,
literal|"name"
argument_list|,
literal|"multiad two"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"603"
argument_list|,
literal|"name"
argument_list|,
literal|"multiad three"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that they are in the index
name|assertQ
argument_list|(
literal|"should find three"
argument_list|,
name|req
argument_list|(
literal|"name:multiad"
argument_list|)
argument_list|,
literal|"//result[@numFound=3]"
argument_list|)
expr_stmt|;
comment|// Add three documents at once, with the middle one missing a field that has a default
name|assertU
argument_list|(
literal|"adding 3 docs, with 2nd one missing a field that has a default value"
argument_list|,
literal|"<add>"
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"601"
argument_list|,
literal|"name"
argument_list|,
literal|"nosubject batch one"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"602"
argument_list|,
literal|"name"
argument_list|,
literal|"nosubject batch two"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"603"
argument_list|,
literal|"name"
argument_list|,
literal|"nosubject batch three"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Since the missing field had a devault value,
comment|// All three should have made it into the index
name|assertQ
argument_list|(
literal|"should find three"
argument_list|,
name|req
argument_list|(
literal|"name:nosubject"
argument_list|)
argument_list|,
literal|"//result[@numFound=3]"
argument_list|)
expr_stmt|;
comment|// Add three documents at once, with the middle with a bad field definition,
comment|// to establish the baselinie behavior for errors in a multi-ad submission
name|assertFailedU
argument_list|(
literal|"adding 3 documents, with 2nd one with undefined field"
argument_list|,
literal|"<add>"
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"801"
argument_list|,
literal|"name"
argument_list|,
literal|"baddef batch one"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"802"
argument_list|,
literal|"field_t"
argument_list|,
literal|"name"
argument_list|,
literal|"baddef batch two"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|,
literal|"GaRbAgeFiElD"
argument_list|,
literal|"garbage"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"803"
argument_list|,
literal|"name"
argument_list|,
literal|"baddef batch three"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that only docs before the error should be in the index
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"name:baddef"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// Add three documents at once, with the middle one missing a required field that has no default
name|assertFailedU
argument_list|(
literal|"adding 3 docs, with 2nd one missing required field"
argument_list|,
literal|"<add>"
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"701"
argument_list|,
literal|"name"
argument_list|,
literal|"noname batch one"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"702"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"703"
argument_list|,
literal|"name"
argument_list|,
literal|"noname batch batch three"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that only docs before the error should be in the index
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"name:noname"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

