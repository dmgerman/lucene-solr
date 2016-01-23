begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|standard
operator|.
name|processors
operator|.
name|MultiFieldQueryNodeProcessor
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
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * This attribute is used by {@link MultiFieldQueryNodeProcessor} processor and  * must be defined in the {@link QueryConfigHandler}. This attribute tells the  * processor to which fields the terms in the query should be expanded.<br/>  *   */
end_comment

begin_interface
DECL|interface|MultiFieldAttribute
specifier|public
interface|interface
name|MultiFieldAttribute
extends|extends
name|Attribute
block|{
DECL|method|setFields
specifier|public
name|void
name|setFields
parameter_list|(
name|CharSequence
index|[]
name|fields
parameter_list|)
function_decl|;
DECL|method|getFields
specifier|public
name|CharSequence
index|[]
name|getFields
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

