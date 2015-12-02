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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/dynamicfields/(pattern)  * where pattern is a field name pattern (with an asterisk at the beginning or the end).  */
end_comment

begin_class
DECL|class|DynamicFieldResource
specifier|public
class|class
name|DynamicFieldResource
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|fieldNamePattern
specifier|private
name|String
name|fieldNamePattern
decl_stmt|;
DECL|method|DynamicFieldResource
specifier|public
name|DynamicFieldResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Gets the field name pattern from the request attribute where it's stored by Restlet.     */
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
name|fieldNamePattern
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
name|fieldNamePattern
operator|=
literal|null
operator|==
name|fieldNamePattern
condition|?
literal|""
else|:
name|urlDecode
argument_list|(
name|fieldNamePattern
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
name|fieldNamePattern
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Dynamic field name is missing"
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
name|SchemaField
name|field
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SchemaField
name|prototype
range|:
name|getSchema
argument_list|()
operator|.
name|getDynamicFieldPrototypes
argument_list|()
control|)
block|{
if|if
condition|(
name|prototype
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldNamePattern
argument_list|)
condition|)
block|{
name|field
operator|=
name|prototype
expr_stmt|;
break|break;
block|}
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
literal|"Dynamic field '"
operator|+
name|fieldNamePattern
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
name|DYNAMIC_FIELD
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
comment|/**    * Accepts JSON add dynamic field request    */
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
literal|" name given in the request URL: /schema/dynamicfields/(name)"
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
name|DYNAMIC_FIELD
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
name|DYNAMIC_FIELD
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
name|fieldNamePattern
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
literal|"Dynamic field name in the request body '"
operator|+
name|bodyFieldName
operator|+
literal|"' doesn't match dynamic field name in the request URL '"
operator|+
name|fieldNamePattern
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
name|Object
name|copies
init|=
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
literal|null
decl_stmt|;
if|if
condition|(
name|copies
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|copies
operator|instanceof
name|List
condition|)
block|{
name|copyFieldNames
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|copies
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|copies
operator|instanceof
name|String
condition|)
block|{
name|copyFieldNames
operator|=
name|singletonList
argument_list|(
name|copies
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"Invalid '"
operator|+
name|IndexSchema
operator|.
name|COPY_FIELDS
operator|+
literal|"' type."
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
block|}
if|if
condition|(
name|copyFieldNames
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
block|}
name|IndexSchema
name|newSchema
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|SchemaField
name|newDynamicField
init|=
name|oldSchema
operator|.
name|newDynamicField
argument_list|(
name|fieldNamePattern
argument_list|,
name|fieldType
argument_list|,
name|map
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|oldSchema
operator|.
name|getSchemaUpdateLock
argument_list|()
init|)
block|{
name|newSchema
operator|=
name|oldSchema
operator|.
name|addDynamicFields
argument_list|(
name|singletonList
argument_list|(
name|newDynamicField
argument_list|)
argument_list|,
name|singletonMap
argument_list|(
name|newDynamicField
operator|.
name|getName
argument_list|()
argument_list|,
name|copyFieldNames
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|newSchema
condition|)
block|{
name|getSolrCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed to add dynamic field."
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ManagedIndexSchema
operator|.
name|SchemaChangedInZkException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Schema changed while processing request, retrying"
argument_list|)
expr_stmt|;
name|oldSchema
operator|=
operator|(
name|ManagedIndexSchema
operator|)
name|getSolrCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
block|}
block|}
comment|// if in cloud mode, wait for schema updates to propagate to all replicas
name|waitForSchemaUpdateToPropagate
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

