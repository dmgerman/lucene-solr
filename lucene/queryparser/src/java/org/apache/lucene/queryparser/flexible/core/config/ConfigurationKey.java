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

begin_comment
comment|/**  * An instance of this class represents a key that is used to retrieve a value  * from {@link AbstractQueryConfig}. It also holds the value's type, which is  * defined in the generic argument.  *   * @see AbstractQueryConfig  */
end_comment

begin_class
DECL|class|ConfigurationKey
specifier|final
specifier|public
class|class
name|ConfigurationKey
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|ConfigurationKey
specifier|private
name|ConfigurationKey
parameter_list|()
block|{}
comment|/**    * Creates a new instance.    *     * @param<T> the value's type    *     * @return a new instance    */
DECL|method|newInstance
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ConfigurationKey
argument_list|<
name|T
argument_list|>
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ConfigurationKey
argument_list|<>
argument_list|()
return|;
block|}
block|}
end_class

end_unit

