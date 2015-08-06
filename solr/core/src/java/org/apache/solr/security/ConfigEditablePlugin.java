begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|CommandOperation
import|;
end_import

begin_comment
comment|/**An interface to be implemented by a Plugin whose Configuration is runtime editable  *  */
end_comment

begin_interface
DECL|interface|ConfigEditablePlugin
specifier|public
interface|interface
name|ConfigEditablePlugin
block|{
comment|/** Operate the commands on the latest conf and return a new conf object    * If there are errors in the commands , throw a SolrException. return a null    * if no changes are to be made as a result of this edit. It is the responsibility    * of the implementation to ensure that the returned config is valid . The framework    * does no validation of the data    */
DECL|method|edit
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|edit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|latestConf
parameter_list|,
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|commands
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

