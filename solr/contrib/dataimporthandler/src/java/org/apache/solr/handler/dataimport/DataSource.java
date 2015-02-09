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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  *<p>  * Provides data from a source with a given query.  *</p>  *<p>  * Implementation of this abstract class must provide a default no-arg constructor  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DataSource
specifier|public
specifier|abstract
class|class
name|DataSource
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Initializes the DataSource with the<code>Context</code> and    * initialization properties.    *<p>    * This is invoked by the<code>DataImporter</code> after creating an    * instance of this class.    */
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
function_decl|;
comment|/**    * Get records for the given query.The return type depends on the    * implementation .    *    * @param query The query string. It can be a SQL for JdbcDataSource or a URL    *              for HttpDataSource or a file location for FileDataSource or a custom    *              format for your own custom DataSource.    * @return Depends on the implementation. For instance JdbcDataSource returns    *         an Iterator&lt;Map&lt;String,Object&gt;&gt;    */
DECL|method|getData
specifier|public
specifier|abstract
name|T
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
comment|/**    * Cleans up resources of this DataSource after use.    */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_class

end_unit

