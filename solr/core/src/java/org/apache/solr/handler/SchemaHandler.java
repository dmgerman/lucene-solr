begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|Arrays
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
operator|.
name|Api
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
name|api
operator|.
name|ApiBag
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|SimpleOrderedMap
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
name|util
operator|.
name|StrUtils
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
name|util
operator|.
name|Utils
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|SchemaManager
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
name|ZkIndexSchemaReader
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|PermissionNameProvider
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
name|plugin
operator|.
name|SolrCoreAware
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|JSON
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
operator|.
name|SchemaProps
operator|.
name|Handler
operator|.
name|COPY_FIELDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
operator|.
name|SchemaProps
operator|.
name|Handler
operator|.
name|DYNAMIC_FIELDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
operator|.
name|SchemaProps
operator|.
name|Handler
operator|.
name|FIELDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
operator|.
name|SchemaProps
operator|.
name|Handler
operator|.
name|FIELD_TYPES
import|;
end_import

begin_class
DECL|class|SchemaHandler
specifier|public
class|class
name|SchemaHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
implements|,
name|PermissionNameProvider
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
DECL|field|isImmutableConfigSet
specifier|private
name|boolean
name|isImmutableConfigSet
init|=
literal|false
decl_stmt|;
DECL|field|level2
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|level2
decl_stmt|;
static|static
block|{
name|Map
name|s
init|=
name|Utils
operator|.
name|makeMap
argument_list|(
name|FIELD_TYPES
operator|.
name|nameLower
argument_list|,
literal|null
argument_list|,
name|FIELDS
operator|.
name|nameLower
argument_list|,
literal|"fl"
argument_list|,
name|DYNAMIC_FIELDS
operator|.
name|nameLower
argument_list|,
literal|"fl"
argument_list|,
name|COPY_FIELDS
operator|.
name|nameLower
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|level2
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|RequestHandlerUtils
operator|.
name|setWt
argument_list|(
name|req
argument_list|,
name|JSON
argument_list|)
expr_stmt|;
name|String
name|httpMethod
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"httpMethod"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|httpMethod
argument_list|)
condition|)
block|{
if|if
condition|(
name|isImmutableConfigSet
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
literal|"ConfigSet is immutable"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|req
operator|.
name|getContentStreams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
literal|"no stream"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|List
name|errs
init|=
operator|new
name|SchemaManager
argument_list|(
name|req
argument_list|)
operator|.
name|performOperations
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"Error reading input String "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|handleGET
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|PermissionNameProvider
operator|.
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|ctx
parameter_list|)
block|{
switch|switch
condition|(
name|ctx
operator|.
name|getHttpMethod
argument_list|()
condition|)
block|{
case|case
literal|"GET"
case|:
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|SCHEMA_READ_PERM
return|;
case|case
literal|"POST"
case|:
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|SCHEMA_EDIT_PERM
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|handleGET
specifier|private
name|void
name|handleGET
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|String
name|path
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|path
condition|)
block|{
case|case
literal|"/schema"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|SCHEMA
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/version"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|VERSION
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/uniquekey"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|UNIQUE_KEY
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/similarity"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|SIMILARITY
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getSimilarityFactory
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/name"
case|:
block|{
specifier|final
name|String
name|schemaName
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getSchemaName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|schemaName
condition|)
block|{
name|String
name|message
init|=
literal|"Schema has no name"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|,
name|schemaName
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|"/schema/zkversion"
case|:
block|{
name|int
name|refreshIfBelowVersion
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
literal|"refreshIfBelowVersion"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|zkVersion
init|=
operator|-
literal|1
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|ManagedIndexSchema
condition|)
block|{
name|ManagedIndexSchema
name|managed
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|schema
decl_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|refreshIfBelowVersion
operator|!=
operator|-
literal|1
operator|&&
name|zkVersion
operator|<
name|refreshIfBelowVersion
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"REFRESHING SCHEMA (refreshIfBelowVersion="
operator|+
name|refreshIfBelowVersion
operator|+
literal|", currentVersion="
operator|+
name|zkVersion
operator|+
literal|") before returning version!"
argument_list|)
expr_stmt|;
name|ZkSolrResourceLoader
name|zkSolrResourceLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|ZkIndexSchemaReader
name|zkIndexSchemaReader
init|=
name|zkSolrResourceLoader
operator|.
name|getZkIndexSchemaReader
argument_list|()
decl_stmt|;
name|managed
operator|=
name|zkIndexSchemaReader
operator|.
name|refreshSchemaFromZk
argument_list|(
name|refreshIfBelowVersion
argument_list|)
expr_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"zkversion"
argument_list|,
name|zkVersion
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
name|parts
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
name|level2
operator|.
name|containsKey
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|realName
init|=
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|IndexSchema
operator|.
name|nameMapping
operator|.
name|get
argument_list|(
name|realName
argument_list|)
decl_stmt|;
name|String
name|pathParam
init|=
name|level2
operator|.
name|get
argument_list|(
name|realName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|req
operator|.
name|setParams
argument_list|(
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
name|pathParam
argument_list|,
name|parts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
name|propertyValues
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|(
name|realName
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|propertyValues
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|String
name|name
init|=
name|parts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|list
control|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|SimpleOrderedMap
condition|)
block|{
name|SimpleOrderedMap
name|simpleOrderedMap
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|simpleOrderedMap
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
name|fieldName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|realName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|simpleOrderedMap
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No such path "
operator|+
name|path
argument_list|)
throw|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No such path "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|subPaths
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|subPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"version"
argument_list|,
literal|"uniquekey"
argument_list|,
literal|"name"
argument_list|,
literal|"similarity"
argument_list|,
literal|"defaultsearchfield"
argument_list|,
literal|"solrqueryparser"
argument_list|,
literal|"zkversion"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|subPaths
operator|.
name|addAll
argument_list|(
name|level2
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|subPath
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|subPath
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
name|parts
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|subPaths
operator|.
name|contains
argument_list|(
name|prefix
argument_list|)
condition|)
return|return
name|this
return|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"CRUD operations over the Solr schema"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|isImmutableConfigSet
operator|=
name|SolrConfigHandler
operator|.
name|getImmutable
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApis
specifier|public
name|Collection
argument_list|<
name|Api
argument_list|>
name|getApis
parameter_list|()
block|{
return|return
name|ApiBag
operator|.
name|wrapRequestHandlers
argument_list|(
name|this
argument_list|,
literal|"core.SchemaRead"
argument_list|,
literal|"core.SchemaRead.fields"
argument_list|,
literal|"core.SchemaRead.copyFields"
argument_list|,
literal|"core.SchemaEdit"
argument_list|,
literal|"core.SchemaRead.dynamicFields_fieldTypes"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|registerV2
specifier|public
name|Boolean
name|registerV2
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

