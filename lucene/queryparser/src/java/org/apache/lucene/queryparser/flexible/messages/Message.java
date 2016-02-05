begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.messages
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
name|messages
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Message Interface for a lazy loading.  * For Native Language Support (NLS), system of software internationalization.  */
end_comment

begin_interface
DECL|interface|Message
specifier|public
interface|interface
name|Message
block|{
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
function_decl|;
DECL|method|getArguments
specifier|public
name|Object
index|[]
name|getArguments
parameter_list|()
function_decl|;
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|()
function_decl|;
DECL|method|getLocalizedMessage
specifier|public
name|String
name|getLocalizedMessage
parameter_list|(
name|Locale
name|locale
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

