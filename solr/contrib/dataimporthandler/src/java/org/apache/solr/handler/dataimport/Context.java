begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  *<p>  * This abstract class gives access to all available objects. So any  * component implemented by a user can have the full power of DataImportHandler  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|Context
specifier|public
specifier|abstract
class|class
name|Context
block|{
DECL|field|FULL_DUMP
DECL|field|DELTA_DUMP
DECL|field|FIND_DELTA
specifier|public
specifier|static
specifier|final
name|String
name|FULL_DUMP
init|=
literal|"FULL_DUMP"
decl_stmt|,
name|DELTA_DUMP
init|=
literal|"DELTA_DUMP"
decl_stmt|,
name|FIND_DELTA
init|=
literal|"FIND_DELTA"
decl_stmt|;
comment|/**    * An object stored in entity scope is valid only for the current entity for the current document only.    */
DECL|field|SCOPE_ENTITY
specifier|public
specifier|static
specifier|final
name|String
name|SCOPE_ENTITY
init|=
literal|"entity"
decl_stmt|;
comment|/**    * An object stored in global scope is available for the current import only but across entities and documents.    */
DECL|field|SCOPE_GLOBAL
specifier|public
specifier|static
specifier|final
name|String
name|SCOPE_GLOBAL
init|=
literal|"global"
decl_stmt|;
comment|/**    * An object stored in document scope is available for the current document only but across entities.    */
DECL|field|SCOPE_DOC
specifier|public
specifier|static
specifier|final
name|String
name|SCOPE_DOC
init|=
literal|"document"
decl_stmt|;
comment|/**    * An object stored in 'solrcore' scope is available across imports, entities and documents throughout the life of    * a solr core. A solr core unload or reload will destroy this data.    */
DECL|field|SCOPE_SOLR_CORE
specifier|public
specifier|static
specifier|final
name|String
name|SCOPE_SOLR_CORE
init|=
literal|"solrcore"
decl_stmt|;
comment|/**    * Get the value of any attribute put into this entity    *    * @param name name of the attribute eg: 'name'    * @return value of named attribute in entity    */
DECL|method|getEntityAttribute
specifier|public
specifier|abstract
name|String
name|getEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Get the value of any attribute put into this entity after resolving all variables found in the attribute value    * @param name name of the attribute    * @return value of the named attribute after resolving all variables    */
DECL|method|getResolvedEntityAttribute
specifier|public
specifier|abstract
name|String
name|getResolvedEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns all the fields put into an entity. each item (which is a map ) in    * the list corresponds to one field. each if the map contains the attribute    * names and values in a field    *    * @return all fields in an entity    */
DECL|method|getAllEntityFields
specifier|public
specifier|abstract
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getAllEntityFields
parameter_list|()
function_decl|;
comment|/**    * Returns the VariableResolver used in this entity which can be used to    * resolve the tokens in ${&lt;namespce.name&gt;}    *    * @return a VariableResolver instance    * @see org.apache.solr.handler.dataimport.VariableResolver    */
DECL|method|getVariableResolver
specifier|public
specifier|abstract
name|VariableResolver
name|getVariableResolver
parameter_list|()
function_decl|;
comment|/**    * Gets the datasource instance defined for this entity. Do not close() this instance.    * Transformers should use the getDataSource(String name) method.    *    * @return a new DataSource instance as configured for the current entity    * @see org.apache.solr.handler.dataimport.DataSource    * @see #getDataSource(String)    */
DECL|method|getDataSource
specifier|public
specifier|abstract
name|DataSource
name|getDataSource
parameter_list|()
function_decl|;
comment|/**    * Gets a new DataSource instance with a name. Ensure that you close() this after use    * because this is created just for this method call.    *    * @param name Name of the dataSource as defined in the dataSource tag    * @return a new DataSource instance    * @see org.apache.solr.handler.dataimport.DataSource    */
DECL|method|getDataSource
specifier|public
specifier|abstract
name|DataSource
name|getDataSource
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the instance of EntityProcessor used for this entity    *    * @return instance of EntityProcessor used for the current entity    * @see org.apache.solr.handler.dataimport.EntityProcessor    */
DECL|method|getEntityProcessor
specifier|public
specifier|abstract
name|EntityProcessor
name|getEntityProcessor
parameter_list|()
function_decl|;
comment|/**    * Store values in a certain name and scope (entity, document,global)    *    * @param name  the key    * @param val   the value    * @param scope the scope in which the given key, value pair is to be stored    */
DECL|method|setSessionAttribute
specifier|public
specifier|abstract
name|void
name|setSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|,
name|String
name|scope
parameter_list|)
function_decl|;
comment|/**    * get a value by name in the given scope (entity, document,global)    *    * @param name  the key    * @param scope the scope from which the value is to be retrieved    * @return the object stored in the given scope with the given key    */
DECL|method|getSessionAttribute
specifier|public
specifier|abstract
name|Object
name|getSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|scope
parameter_list|)
function_decl|;
comment|/**    * Get the context instance for the parent entity. works only in the full dump    * If the current entity is rootmost a null is returned    *    * @return parent entity's Context    */
DECL|method|getParentContext
specifier|public
specifier|abstract
name|Context
name|getParentContext
parameter_list|()
function_decl|;
comment|/**    * The request parameters passed over HTTP for this command the values in the    * map are either String(for single valued parameters) or List&lt;String&gt; (for    * multi-valued parameters)    *    * @return the request parameters passed in the URL to initiate this process    */
DECL|method|getRequestParameters
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRequestParameters
parameter_list|()
function_decl|;
comment|/**    * Returns if the current entity is the root entity    *    * @return true if current entity is the root entity, false otherwise    */
DECL|method|isRootEntity
specifier|public
specifier|abstract
name|boolean
name|isRootEntity
parameter_list|()
function_decl|;
comment|/**    * Returns the current process FULL_DUMP, DELTA_DUMP, FIND_DELTA    *    * @return the type of the current running process    */
DECL|method|currentProcess
specifier|public
specifier|abstract
name|String
name|currentProcess
parameter_list|()
function_decl|;
comment|/**    * Exposing the actual SolrCore to the components    *    * @return the core    */
DECL|method|getSolrCore
specifier|public
specifier|abstract
name|SolrCore
name|getSolrCore
parameter_list|()
function_decl|;
comment|/**    * Makes available some basic running statistics such as "docCount",    * "deletedDocCount", "rowCount", "queryCount" and "skipDocCount"    *    * @return a Map containing running statistics of the current import    */
DECL|method|getStats
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getStats
parameter_list|()
function_decl|;
comment|/**    * Returns the text specified in the script tag in the data-config.xml     */
DECL|method|getScript
specifier|public
specifier|abstract
name|String
name|getScript
parameter_list|()
function_decl|;
comment|/**    * Returns the language of the script as specified in the script tag in data-config.xml    */
DECL|method|getScriptLanguage
specifier|public
specifier|abstract
name|String
name|getScriptLanguage
parameter_list|()
function_decl|;
comment|/**delete a document by id    */
DECL|method|deleteDoc
specifier|public
specifier|abstract
name|void
name|deleteDoc
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
comment|/**delete documents by query    */
DECL|method|deleteDocByQuery
specifier|public
specifier|abstract
name|void
name|deleteDocByQuery
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
comment|/**Use this directly to  resolve variable    * @param var the variable name     * @return the resolved value    */
DECL|method|resolve
specifier|public
specifier|abstract
name|Object
name|resolve
parameter_list|(
name|String
name|var
parameter_list|)
function_decl|;
comment|/** Resolve variables in a template    *    * @return The string w/ variables resolved    */
DECL|method|replaceTokens
specifier|public
specifier|abstract
name|String
name|replaceTokens
parameter_list|(
name|String
name|template
parameter_list|)
function_decl|;
block|}
end_class

end_unit

