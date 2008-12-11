begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|*
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p> Base class for all implementations of EntityProcessor</p><p/><p> Most implementations of EntityProcessor  * extend this base class which provides common functionality.</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|EntityProcessorBase
specifier|public
class|class
name|EntityProcessorBase
extends|extends
name|EntityProcessor
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
name|EntityProcessorBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|isFirstInit
specifier|protected
name|boolean
name|isFirstInit
init|=
literal|true
decl_stmt|;
DECL|field|entityName
specifier|protected
name|String
name|entityName
decl_stmt|;
DECL|field|context
specifier|protected
name|Context
name|context
decl_stmt|;
DECL|field|resolver
specifier|protected
name|VariableResolverImpl
name|resolver
decl_stmt|;
DECL|field|rowIterator
specifier|protected
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
decl_stmt|;
DECL|field|transformers
specifier|protected
name|List
argument_list|<
name|Transformer
argument_list|>
name|transformers
decl_stmt|;
DECL|field|rowcache
specifier|protected
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowcache
decl_stmt|;
DECL|field|query
specifier|protected
name|String
name|query
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|session
specifier|private
name|Map
name|session
decl_stmt|;
DECL|field|onError
specifier|protected
name|String
name|onError
init|=
name|ABORT
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|rowIterator
operator|=
literal|null
expr_stmt|;
name|rowcache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
if|if
condition|(
name|isFirstInit
condition|)
block|{
name|entityName
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|ON_ERROR
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|onError
operator|=
name|s
expr_stmt|;
block|}
name|resolver
operator|=
operator|(
name|VariableResolverImpl
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
name|isFirstInit
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|loadTransformers
name|void
name|loadTransformers
parameter_list|()
block|{
name|String
name|transClasses
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|TRANSFORMER
argument_list|)
decl_stmt|;
if|if
condition|(
name|transClasses
operator|==
literal|null
condition|)
block|{
name|transformers
operator|=
name|Collections
operator|.
name|EMPTY_LIST
expr_stmt|;
return|return;
block|}
name|String
index|[]
name|transArr
init|=
name|transClasses
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|transformers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Transformer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|add
parameter_list|(
name|Transformer
name|transformer
parameter_list|)
block|{
return|return
name|super
operator|.
name|add
argument_list|(
name|DebugLogger
operator|.
name|wrapTransformer
argument_list|(
name|transformer
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
for|for
control|(
name|String
name|aTransArr
range|:
name|transArr
control|)
block|{
name|String
name|trans
init|=
name|aTransArr
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|trans
operator|.
name|startsWith
argument_list|(
literal|"script:"
argument_list|)
condition|)
block|{
name|String
name|functionName
init|=
name|trans
operator|.
name|substring
argument_list|(
literal|"script:"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|ScriptTransformer
name|scriptTransformer
init|=
operator|new
name|ScriptTransformer
argument_list|()
decl_stmt|;
name|scriptTransformer
operator|.
name|setFunctionName
argument_list|(
name|functionName
argument_list|)
expr_stmt|;
name|transformers
operator|.
name|add
argument_list|(
name|scriptTransformer
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|Class
name|clazz
init|=
name|DocBuilder
operator|.
name|loadClass
argument_list|(
name|trans
argument_list|,
name|context
operator|.
name|getSolrCore
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|newInstance
argument_list|()
operator|instanceof
name|Transformer
condition|)
block|{
name|transformers
operator|.
name|add
argument_list|(
operator|(
name|Transformer
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Method
name|meth
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
name|TRANSFORM_ROW
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|meth
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Transformer :"
operator|+
name|trans
operator|+
literal|"does not implement Transformer interface or does not have a transformRow(Map m)method"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
name|msg
argument_list|)
throw|;
block|}
name|transformers
operator|.
name|add
argument_list|(
operator|new
name|ReflectionTransformer
argument_list|(
name|meth
argument_list|,
name|clazz
argument_list|,
name|trans
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to load Transformer: "
operator|+
name|aTransArr
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|ReflectionTransformer
specifier|static
class|class
name|ReflectionTransformer
extends|extends
name|Transformer
block|{
DECL|field|meth
specifier|final
name|Method
name|meth
decl_stmt|;
DECL|field|clazz
specifier|final
name|Class
name|clazz
decl_stmt|;
DECL|field|trans
specifier|final
name|String
name|trans
decl_stmt|;
DECL|field|o
specifier|final
name|Object
name|o
decl_stmt|;
DECL|method|ReflectionTransformer
specifier|public
name|ReflectionTransformer
parameter_list|(
name|Method
name|meth
parameter_list|,
name|Class
name|clazz
parameter_list|,
name|String
name|trans
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|meth
operator|=
name|meth
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|trans
operator|=
name|trans
expr_stmt|;
name|o
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
return|return
name|meth
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
name|aRow
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"method invocation failed on transformer : "
operator|+
name|trans
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|WARN
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getFromRowCache
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFromRowCache
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|rowcache
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowcache
operator|.
name|isEmpty
argument_list|()
condition|)
name|rowcache
operator|=
literal|null
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applyTransformer
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|applyTransformer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|)
block|{
if|if
condition|(
name|transformers
operator|==
literal|null
condition|)
name|loadTransformers
argument_list|()
expr_stmt|;
if|if
condition|(
name|transformers
operator|==
name|Collections
operator|.
name|EMPTY_LIST
condition|)
return|return
name|row
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transformedRow
init|=
name|row
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Transformer
name|t
range|:
name|transformers
control|)
block|{
try|try
block|{
if|if
condition|(
name|rows
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|tmpRows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|rows
control|)
block|{
name|Object
name|o
init|=
name|t
operator|.
name|transformRow
argument_list|(
name|map
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|oMap
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|checkSkipDoc
argument_list|(
name|oMap
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|tmpRows
operator|.
name|add
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|tmpRows
operator|.
name|addAll
argument_list|(
operator|(
name|List
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Transformer must return Map<String, Object> or a List<Map<String, Object>>"
argument_list|)
expr_stmt|;
block|}
block|}
name|rows
operator|=
name|tmpRows
expr_stmt|;
block|}
else|else
block|{
name|Object
name|o
init|=
name|t
operator|.
name|transformRow
argument_list|(
name|transformedRow
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|oMap
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|checkSkipDoc
argument_list|(
name|oMap
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|transformedRow
operator|=
operator|(
name|Map
operator|)
name|o
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|rows
operator|=
operator|(
name|List
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Transformer must return Map<String, Object> or a List<Map<String, Object>>"
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
name|log
operator|.
name|warn
argument_list|(
literal|"transformer threw error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// onError = continue
block|}
block|}
if|if
condition|(
name|rows
operator|==
literal|null
condition|)
block|{
return|return
name|transformedRow
return|;
block|}
else|else
block|{
name|rowcache
operator|=
name|rows
expr_stmt|;
return|return
name|getFromRowCache
argument_list|()
return|;
block|}
block|}
DECL|method|checkSkipDoc
specifier|private
name|void
name|checkSkipDoc
parameter_list|(
name|Map
name|oMap
parameter_list|,
name|Transformer
name|t
parameter_list|)
block|{
if|if
condition|(
name|oMap
operator|.
name|get
argument_list|(
name|SKIP_DOC
argument_list|)
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|oMap
operator|.
name|get
argument_list|(
name|SKIP_DOC
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
literal|"Document skipped by: "
operator|+
name|DebugLogger
operator|.
name|getTransformerName
argument_list|(
name|t
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|getNext
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|rowIterator
operator|.
name|hasNext
argument_list|()
condition|)
return|return
name|rowIterator
operator|.
name|next
argument_list|()
return|;
name|query
operator|=
literal|null
expr_stmt|;
name|rowIterator
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"getNext() failed for query '"
operator|+
name|query
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|rowIterator
operator|=
literal|null
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|nextModifiedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|nextDeletedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextDeletedRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|nextModifiedParentRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedParentRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|setSessionAttribute
specifier|public
name|void
name|setSessionAttribute
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
name|session
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|getSessionAttribute
specifier|public
name|Object
name|getSessionAttribute
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|session
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * For a simple implementation, this is the only method that the sub-class should implement. This is intended to    * stream rows one-by-one. Return null to signal end of rows    *    * @return a row where the key is the name of the field and value can be any Object or a Collection of objects. Return    *         null to signal end of rows    */
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// do not do anything
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|/*no op*/
block|}
comment|/**    * Clears the internal session maintained by this EntityProcessor    */
DECL|method|clearSession
specifier|public
name|void
name|clearSession
parameter_list|()
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
name|session
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Only used by cache implementations    */
DECL|field|cachePk
specifier|protected
name|String
name|cachePk
decl_stmt|;
comment|/**    * Only used by cache implementations    */
DECL|field|cacheVariableName
specifier|protected
name|String
name|cacheVariableName
decl_stmt|;
comment|/**    * Only used by cache implementations    */
DECL|field|simpleCache
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|simpleCache
decl_stmt|;
comment|/**    * Only used by cache implementations    */
DECL|field|cacheWithWhereClause
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|cacheWithWhereClause
decl_stmt|;
DECL|field|dataSourceRowCache
specifier|protected
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|dataSourceRowCache
decl_stmt|;
comment|/**    * Only used by cache implementations    */
DECL|method|cacheInit
specifier|protected
name|void
name|cacheInit
parameter_list|()
block|{
if|if
condition|(
name|simpleCache
operator|!=
literal|null
operator|||
name|cacheWithWhereClause
operator|!=
literal|null
condition|)
return|return;
name|String
name|where
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"where"
argument_list|)
decl_stmt|;
if|if
condition|(
name|where
operator|==
literal|null
condition|)
block|{
name|simpleCache
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|splits
init|=
name|where
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|cachePk
operator|=
name|splits
index|[
literal|0
index|]
expr_stmt|;
name|cacheVariableName
operator|=
name|splits
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
name|cacheWithWhereClause
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * If the where clause is present the cache is sql Vs Map of key Vs List of Rows. Only used by cache implementations.    *    * @param query the query string for which cached data is to be returned    *    * @return the cached row corresponding to the given query after all variables have been resolved    */
DECL|method|getIdCacheData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getIdCacheData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|rowIdVsRows
init|=
name|cacheWithWhereClause
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
literal|null
decl_stmt|;
name|Object
name|key
init|=
name|resolver
operator|.
name|resolve
argument_list|(
name|cacheVariableName
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
literal|"The cache lookup value : "
operator|+
name|cacheVariableName
operator|+
literal|" is resolved to be null in the entity :"
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|rowIdVsRows
operator|!=
literal|null
condition|)
block|{
name|rows
operator|=
name|rowIdVsRows
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|rows
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|dataSourceRowCache
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|rows
argument_list|)
expr_stmt|;
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
else|else
block|{
name|rows
operator|=
name|getAllNonCachedRows
argument_list|()
expr_stmt|;
if|if
condition|(
name|rows
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|rowIdVsRows
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
range|:
name|rows
control|)
block|{
name|Object
name|k
init|=
name|row
operator|.
name|get
argument_list|(
name|cachePk
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
literal|"No value available for the cache key : "
operator|+
name|cachePk
operator|+
literal|" in the entity : "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|k
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|key
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
literal|"The key in the cache type : "
operator|+
name|k
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"is not same as the lookup value type "
operator|+
name|key
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" in the entity "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|rowIdVsRows
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|==
literal|null
condition|)
name|rowIdVsRows
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|rowIdVsRows
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|cacheWithWhereClause
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|rowIdVsRows
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rowIdVsRows
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
return|return
literal|null
return|;
name|dataSourceRowCache
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|rowIdVsRows
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataSourceRowCache
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|dataSourceRowCache
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
block|}
block|}
comment|/**    *<p> Get all the rows from the the datasource for the given query. Only used by cache implementations.</p> This    *<b>must</b> be implemented by sub-classes which intend to provide a cached implementation    *    * @return the list of all rows fetched from the datasource.    */
DECL|method|getAllNonCachedRows
specifier|protected
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getAllNonCachedRows
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
comment|/**    * If where clause is not present the cache is a Map of query vs List of Rows. Only used by cache implementations.    *    * @param query string for which cached row is to be returned    *    * @return the cached row corresponding to the given query    */
DECL|method|getSimpleCacheData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSimpleCacheData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
name|simpleCache
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|rows
operator|!=
literal|null
condition|)
block|{
name|dataSourceRowCache
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|rows
argument_list|)
expr_stmt|;
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
else|else
block|{
name|rows
operator|=
name|getAllNonCachedRows
argument_list|()
expr_stmt|;
if|if
condition|(
name|rows
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|dataSourceRowCache
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|rows
argument_list|)
expr_stmt|;
name|simpleCache
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|rows
argument_list|)
expr_stmt|;
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|getFromRowCacheTransformed
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFromRowCacheTransformed
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|dataSourceRowCache
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataSourceRowCache
operator|.
name|isEmpty
argument_list|()
condition|)
name|dataSourceRowCache
operator|=
literal|null
expr_stmt|;
return|return
name|r
operator|==
literal|null
condition|?
literal|null
else|:
name|applyTransformer
argument_list|(
name|r
argument_list|)
return|;
block|}
DECL|field|TRANSFORMER
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORMER
init|=
literal|"transformer"
decl_stmt|;
DECL|field|TRANSFORM_ROW
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORM_ROW
init|=
literal|"transformRow"
decl_stmt|;
DECL|field|ON_ERROR
specifier|public
specifier|static
specifier|final
name|String
name|ON_ERROR
init|=
literal|"onError"
decl_stmt|;
DECL|field|ABORT
specifier|public
specifier|static
specifier|final
name|String
name|ABORT
init|=
literal|"abort"
decl_stmt|;
DECL|field|CONTINUE
specifier|public
specifier|static
specifier|final
name|String
name|CONTINUE
init|=
literal|"continue"
decl_stmt|;
DECL|field|SKIP
specifier|public
specifier|static
specifier|final
name|String
name|SKIP
init|=
literal|"skip"
decl_stmt|;
DECL|field|SKIP_DOC
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_DOC
init|=
literal|"$skipDoc"
decl_stmt|;
block|}
end_class

end_unit

