begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessor
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * This class can be used to hold any query configuration and no field  * configuration. For field configuration, it creates an empty  * {@link FieldConfig} object and delegate it to field config listeners,   * these are responsible for setting up all the field configuration.  *   * {@link QueryConfigHandler} should be extended by classes that intends to  * provide configuration to {@link QueryNodeProcessor} objects.  *   * The class that extends {@link QueryConfigHandler} should also provide  * {@link FieldConfig} objects for each collection field.  *   * @see FieldConfig  * @see FieldConfigListener  * @see QueryConfigHandler  */
end_comment

begin_class
DECL|class|QueryConfigHandler
specifier|public
specifier|abstract
class|class
name|QueryConfigHandler
extends|extends
name|AbstractQueryConfig
block|{
DECL|field|listeners
specifier|final
specifier|private
name|LinkedList
argument_list|<
name|FieldConfigListener
argument_list|>
name|listeners
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Returns an implementation of    * {@link FieldConfig} for a specific field name. If the implemented    * {@link QueryConfigHandler} does not know a specific field name, it may    * return<code>null</code>, indicating there is no configuration for that    * field.    *     * @param fieldName    *          the field name    * @return a {@link FieldConfig} object containing the field name    *         configuration or<code>null</code>, if the implemented    *         {@link QueryConfigHandler} has no configuration for that field    */
DECL|method|getFieldConfig
specifier|public
name|FieldConfig
name|getFieldConfig
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldConfig
name|fieldConfig
init|=
operator|new
name|FieldConfig
argument_list|(
name|StringUtils
operator|.
name|toString
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|FieldConfigListener
name|listener
range|:
name|this
operator|.
name|listeners
control|)
block|{
name|listener
operator|.
name|buildFieldConfig
argument_list|(
name|fieldConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldConfig
return|;
block|}
comment|/**    * Adds a listener. The added listeners are called in the order they are    * added.    *     * @param listener    *          the listener to be added    */
DECL|method|addFieldConfigListener
specifier|public
name|void
name|addFieldConfigListener
parameter_list|(
name|FieldConfigListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

