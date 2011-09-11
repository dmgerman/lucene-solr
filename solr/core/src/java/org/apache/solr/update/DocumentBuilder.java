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
name|HashMap
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
name|solr
operator|.
name|common
operator|.
name|SolrDocument
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
name|common
operator|.
name|SolrInputField
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_comment
comment|// Not thread safe - by design.  Create a new builder for each thread.
end_comment

begin_class
DECL|class|DocumentBuilder
specifier|public
class|class
name|DocumentBuilder
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|map
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
DECL|method|DocumentBuilder
specifier|public
name|DocumentBuilder
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|addSingleField
specifier|protected
name|void
name|addSingleField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|//System.out.println("###################ADDING FIELD "+sfield+"="+val);
comment|// we don't check for a null val ourselves because a solr.FieldType
comment|// might actually want to map it to something.  If createField()
comment|// returns null, then we don't store the field.
if|if
condition|(
name|sfield
operator|.
name|isPolyField
argument_list|()
condition|)
block|{
name|IndexableField
index|[]
name|fields
init|=
name|sfield
operator|.
name|createFields
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|String
name|oldValue
init|=
name|map
operator|.
name|put
argument_list|(
name|sfield
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": first='"
operator|+
name|oldValue
operator|+
literal|"' second='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// Add each field
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|IndexableField
name|field
init|=
name|sfield
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|String
name|oldValue
init|=
name|map
operator|.
name|put
argument_list|(
name|sfield
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": first='"
operator|+
name|oldValue
operator|+
literal|"' second='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add the specified {@link org.apache.solr.schema.SchemaField} to the document.  Does not invoke the copyField mechanism.    * @param sfield The {@link org.apache.solr.schema.SchemaField} to add    * @param val The value to add    * @param boost The boost factor    *    * @see #addField(String, String)    * @see #addField(String, String, float)    * @see #addSingleField(org.apache.solr.schema.SchemaField, String, float)    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|addSingleField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the Field and value to the document, invoking the copyField mechanism    * @param name The name of the field    * @param val The value to add    *    * @see #addField(String, String, float)    * @see #addField(org.apache.solr.schema.SchemaField, String, float)    * @see #addSingleField(org.apache.solr.schema.SchemaField, String, float)    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the Field and value to the document with the specified boost, invoking the copyField mechanism    * @param name The name of the field.    * @param val The value to add    * @param boost The boost    *    * @see #addField(String, String)    * @see #addField(org.apache.solr.schema.SchemaField, String, float)    * @see #addSingleField(org.apache.solr.schema.SchemaField, String, float)    *    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|SchemaField
name|sfield
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|// Check if we should copy this field to any other fields.
comment|// This could happen whether it is explicit or not.
specifier|final
name|List
argument_list|<
name|CopyField
argument_list|>
name|copyFields
init|=
name|schema
operator|.
name|getCopyFieldsList
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|copyFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CopyField
name|cf
range|:
name|copyFields
control|)
block|{
name|addSingleField
argument_list|(
name|cf
operator|.
name|getDestination
argument_list|()
argument_list|,
name|cf
operator|.
name|getLimitedValue
argument_list|(
name|val
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|// error if this field name doesn't match anything
if|if
condition|(
name|sfield
operator|==
literal|null
operator|&&
operator|(
name|copyFields
operator|==
literal|null
operator|||
name|copyFields
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR:unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|endDoc
specifier|public
name|void
name|endDoc
parameter_list|()
block|{   }
comment|// specific to this type of document builder
DECL|method|getDoc
specifier|public
name|Document
name|getDoc
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
comment|// Check for all required fields -- Note, all fields with a
comment|// default value are defacto 'required' fields.
name|List
argument_list|<
name|String
argument_list|>
name|missingFields
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getRequiredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|getField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|field
operator|.
name|getDefaultValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|missingFields
operator|==
literal|null
condition|)
block|{
name|missingFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|missingFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|missingFields
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// add the uniqueKey if possible
if|if
condition|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|n
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|v
init|=
name|doc
operator|.
name|getField
argument_list|(
name|n
argument_list|)
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Document ["
operator|+
name|n
operator|+
literal|"="
operator|+
name|v
operator|+
literal|"] "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"missing required fields: "
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|missingFields
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Document
name|ret
init|=
name|doc
decl_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|addField
specifier|private
specifier|static
name|void
name|addField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|isPolyField
argument_list|()
condition|)
block|{
name|IndexableField
index|[]
name|farr
init|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|createFields
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|farr
control|)
block|{
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// null fields are not added
block|}
block|}
else|else
block|{
name|IndexableField
name|f
init|=
name|field
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// null fields are not added
block|}
block|}
DECL|method|getID
specifier|private
specifier|static
name|String
name|getID
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|String
name|id
init|=
literal|""
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
literal|"[doc="
operator|+
name|doc
operator|.
name|getFieldValue
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
literal|"] "
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/**    * Convert a SolrInputDocument to a lucene Document.    *     * This function should go elsewhere.  This builds the Document without an    * extra Map<> checking for multiple values.  For more discussion, see:    * http://www.nabble.com/Re%3A-svn-commit%3A-r547493---in--lucene-solr-trunk%3A-.--src-java-org-apache-solr-common--src-java-org-apache-solr-schema--src-java-org-apache-solr-update--src-test-org-apache-solr-common--tf3931539.html    *     * TODO: /!\ NOTE /!\ This semantics of this function are still in flux.      * Something somewhere needs to be able to fill up a SolrDocument from    * a lucene document - this is one place that may happen.  It may also be    * moved to an independent function    *     * @since solr 1.3    */
DECL|method|toDocument
specifier|public
specifier|static
name|Document
name|toDocument
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|Document
name|out
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|float
name|docBoost
init|=
name|doc
operator|.
name|getDocumentBoost
argument_list|()
decl_stmt|;
comment|// Load fields from SolrDocument to Document
for|for
control|(
name|SolrInputField
name|field
range|:
name|doc
control|)
block|{
name|String
name|name
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
name|SchemaField
name|sfield
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|boolean
name|used
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
name|field
operator|.
name|getBoost
argument_list|()
decl_stmt|;
comment|// Make sure it has the correct number
if|if
condition|(
name|sfield
operator|!=
literal|null
operator|&&
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
operator|&&
name|field
operator|.
name|getValueCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
comment|// load each field value
name|boolean
name|hasField
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|Object
name|v
range|:
name|field
control|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|hasField
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|used
operator|=
literal|true
expr_stmt|;
name|addField
argument_list|(
name|out
argument_list|,
name|sfield
argument_list|,
name|v
argument_list|,
name|docBoost
operator|*
name|boost
argument_list|)
expr_stmt|;
block|}
comment|// Check if we should copy this field to any other fields.
comment|// This could happen whether it is explicit or not.
name|List
argument_list|<
name|CopyField
argument_list|>
name|copyFields
init|=
name|schema
operator|.
name|getCopyFieldsList
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|CopyField
name|cf
range|:
name|copyFields
control|)
block|{
name|SchemaField
name|destinationField
init|=
name|cf
operator|.
name|getDestination
argument_list|()
decl_stmt|;
comment|// check if the copy field is a multivalued or not
if|if
condition|(
operator|!
name|destinationField
operator|.
name|multiValued
argument_list|()
operator|&&
name|out
operator|.
name|getField
argument_list|(
name|destinationField
operator|.
name|getName
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"multiple values encountered for non multiValued copy field "
operator|+
name|destinationField
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|v
argument_list|)
throw|;
block|}
name|used
operator|=
literal|true
expr_stmt|;
comment|// Perhaps trim the length of a copy field
name|Object
name|val
init|=
name|v
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|String
operator|&&
name|cf
operator|.
name|getMaxChars
argument_list|()
operator|>
literal|0
condition|)
block|{
name|val
operator|=
name|cf
operator|.
name|getLimitedValue
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
name|IndexableField
index|[]
name|fields
init|=
name|destinationField
operator|.
name|createFields
argument_list|(
name|val
argument_list|,
name|docBoost
operator|*
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
comment|// null fields are not added
for|for
control|(
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
name|out
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// In lucene, the boost for a given field is the product of the
comment|// document boost and *all* boosts on values of that field.
comment|// For multi-valued fields, we only want to set the boost on the
comment|// first field.
name|boost
operator|=
name|docBoost
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"Error adding field '"
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|"'='"
operator|+
name|field
operator|.
name|getValue
argument_list|()
operator|+
literal|"'"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
comment|// make sure the field was used somehow...
if|if
condition|(
operator|!
name|used
operator|&&
name|hasField
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// Now validate required fields or add default values
comment|// fields with default values are defacto 'required'
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getRequiredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|out
operator|.
name|getField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|out
argument_list|,
name|field
argument_list|,
name|field
operator|.
name|getDefaultValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"missing required field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|out
return|;
block|}
comment|/**    * Add fields from the solr document    *     * TODO: /!\ NOTE /!\ This semantics of this function are still in flux.      * Something somewhere needs to be able to fill up a SolrDocument from    * a lucene document - this is one place that may happen.  It may also be    * moved to an independent function    *     * @since solr 1.3    */
DECL|method|loadStoredFields
specifier|public
name|SolrDocument
name|loadStoredFields
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|Document
name|luceneDoc
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|luceneDoc
control|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|schema
operator|.
name|isCopyFieldTarget
argument_list|(
name|sf
argument_list|)
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toObject
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

