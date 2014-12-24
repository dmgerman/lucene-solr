begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|NamedList
import|;
end_import

begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|ResponseParser
specifier|public
specifier|abstract
class|class
name|ResponseParser
block|{
DECL|method|getWriterType
specifier|public
specifier|abstract
name|String
name|getWriterType
parameter_list|()
function_decl|;
comment|// for example: wt=XML, JSON, etc
DECL|method|processResponse
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
function_decl|;
DECL|method|processResponse
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/**    * A well behaved ResponseParser will return its content-type.    *     * @return the content-type this parser expects to parse    */
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * @return the version param passed to solr    */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"2.2"
return|;
block|}
block|}
end_class

end_unit

