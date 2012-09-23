begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  *<p>  * This class is the base of {@link QueryConfigHandler} and {@link FieldConfig}.  * It has operations to set, unset and get configuration values.  *</p>  *<p>  * Each configuration is is a key->value pair. The key should be an unique  * {@link ConfigurationKey} instance and it also holds the value's type.  *</p>  *   * @see ConfigurationKey  */
end_comment

begin_class
DECL|class|AbstractQueryConfig
specifier|public
specifier|abstract
class|class
name|AbstractQueryConfig
block|{
DECL|field|configMap
specifier|final
specifier|private
name|HashMap
argument_list|<
name|ConfigurationKey
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|configMap
init|=
operator|new
name|HashMap
argument_list|<
name|ConfigurationKey
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AbstractQueryConfig
name|AbstractQueryConfig
parameter_list|()
block|{
comment|// although this class is public, it can only be constructed from package
block|}
comment|/**    * Returns the value held by the given key.    *     * @param<T> the value's type    *     * @param key the key, cannot be<code>null</code>    *     * @return the value held by the given key    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|ConfigurationKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key cannot be null!"
argument_list|)
throw|;
block|}
return|return
operator|(
name|T
operator|)
name|this
operator|.
name|configMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns true if there is a value set with the given key, otherwise false.    *     * @param<T> the value's type    * @param key the key, cannot be<code>null</code>    * @return true if there is a value set with the given key, otherwise false    */
DECL|method|has
specifier|public
parameter_list|<
name|T
parameter_list|>
name|boolean
name|has
parameter_list|(
name|ConfigurationKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key cannot be null!"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|configMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Sets a key and its value.    *     * @param<T> the value's type    * @param key the key, cannot be<code>null</code>    * @param value value to set    */
DECL|method|set
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|set
parameter_list|(
name|ConfigurationKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|T
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key cannot be null!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|unset
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|configMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Unsets the given key and its value.    *     * @param<T> the value's type    * @param key the key    * @return true if the key and value was set and removed, otherwise false    */
DECL|method|unset
specifier|public
parameter_list|<
name|T
parameter_list|>
name|boolean
name|unset
parameter_list|(
name|ConfigurationKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key cannot be null!"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|configMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

