begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrException
operator|.
name|ErrorCode
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
name|rest
operator|.
name|GETable
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
name|rest
operator|.
name|PUTable
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
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|ManagedIndexSchema
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
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/fields/(fieldname)  * where "fieldname" is the name of a field.  *<p/>  * The GET method returns properties for the given fieldname.  * The "includeDynamic" query parameter, if specified, will cause the  * dynamic field matching the given fieldname to be returned if fieldname  * is not explicitly declared in the schema.  *<p/>  * The PUT method accepts field addition requests in JSON format.  */
end_comment

begin_class
DECL|class|FieldResource
specifier|public
class|class
name|FieldResource
extends|extends
name|BaseFieldResource
implements|implements
name|GETable
implements|,
name|PUTable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|includeDynamic
specifier|private
name|boolean
name|includeDynamic
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|FieldResource
specifier|public
name|FieldResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
name|includeDynamic
operator|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|INCLUDE_DYNAMIC_PARAM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fieldName
operator|=
operator|(
name|String
operator|)
name|getRequestAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|)
expr_stmt|;
try|try
block|{
name|fieldName
operator|=
literal|null
operator|==
name|fieldName
condition|?
literal|""
else|:
name|urlDecode
argument_list|(
name|fieldName
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|fieldName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field name is missing"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|SchemaField
name|field
decl_stmt|;
if|if
condition|(
name|includeDynamic
condition|)
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|field
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"' not found."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|FIELD
argument_list|,
name|getFieldProperties
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
comment|/**    * Accepts JSON add field request, to URL    */
annotation|@
name|Override
DECL|method|put
specifier|public
name|Representation
name|put
parameter_list|(
name|Representation
name|entity
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|getSchema
argument_list|()
operator|.
name|isMutable
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"This IndexSchema is not mutable."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
literal|null
operator|==
name|entity
operator|.
name|getMediaType
argument_list|()
condition|)
block|{
name|entity
operator|.
name|setMediaType
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Only media type "
operator|+
name|MediaType
operator|.
name|APPLICATION_JSON
operator|.
name|toString
argument_list|()
operator|+
literal|" is accepted."
operator|+
literal|"  Request has media type "
operator|+
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|Object
name|object
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|entity
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|object
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid JSON type "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected Map of the form"
operator|+
literal|" (ignore the backslashes): {\"type\":\"text_general\", ...}, either with or"
operator|+
literal|" without a \"name\" mapping.  If the \"name\" is specified, it must match the"
operator|+
literal|" name given in the request URL: /schema/fields/(name)"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
decl_stmt|;
if|if
condition|(
literal|1
operator|==
name|map
operator|.
name|size
argument_list|()
operator|&&
name|map
operator|.
name|containsKey
argument_list|(
name|IndexSchema
operator|.
name|FIELD
argument_list|)
condition|)
block|{
name|map
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|map
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|FIELD
argument_list|)
expr_stmt|;
block|}
name|String
name|bodyFieldName
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
operator|(
name|bodyFieldName
operator|=
operator|(
name|String
operator|)
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|)
operator|)
operator|&&
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|bodyFieldName
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Field name in the request body '"
operator|+
name|bodyFieldName
operator|+
literal|"' doesn't match field name in the request URL '"
operator|+
name|fieldName
operator|+
literal|"'"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|String
name|fieldType
decl_stmt|;
if|if
condition|(
literal|null
operator|==
operator|(
name|fieldType
operator|=
operator|(
name|String
operator|)
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|TYPE
argument_list|)
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"Missing '"
operator|+
name|IndexSchema
operator|.
name|TYPE
operator|+
literal|"' mapping."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|ManagedIndexSchema
name|oldSchema
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|getSchema
argument_list|()
decl_stmt|;
name|String
name|copyTo
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|copyFieldNames
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
if|if
condition|(
name|copyTo
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|)
expr_stmt|;
name|String
index|[]
name|tmp
init|=
name|copyTo
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|copyFieldNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|tmp
operator|.
name|length
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|copyFieldNames
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
name|SchemaField
name|newField
init|=
name|oldSchema
operator|.
name|newField
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addField
argument_list|(
name|newField
argument_list|,
name|copyFieldNames
argument_list|)
decl_stmt|;
name|getSolrCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
block|}
end_class

end_unit

