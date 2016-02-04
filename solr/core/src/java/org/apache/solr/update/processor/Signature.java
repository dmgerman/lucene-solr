begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_class
DECL|class|Signature
specifier|public
specifier|abstract
class|class
name|Signature
block|{
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|nl
parameter_list|)
block|{   }
DECL|method|add
specifier|abstract
specifier|public
name|void
name|add
parameter_list|(
name|String
name|content
parameter_list|)
function_decl|;
DECL|method|getSignature
specifier|abstract
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
function_decl|;
block|}
end_class

end_unit

