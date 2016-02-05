begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
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
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * Overall grouping result. Contains a list of {@link GroupCommand} instances that is the result of  * one the following parameters:  *<ul>  *<li>group.field  *<li>group.func  *<li>group.query  *</ul>  *  * @since solr 3.4  */
end_comment

begin_class
DECL|class|GroupResponse
specifier|public
class|class
name|GroupResponse
implements|implements
name|Serializable
block|{
DECL|field|_values
specifier|private
specifier|final
name|List
argument_list|<
name|GroupCommand
argument_list|>
name|_values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Adds a grouping command to the response.    *    * @param command The grouping command to add    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|GroupCommand
name|command
parameter_list|)
block|{
name|_values
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns all grouping commands.    *    * @return all grouping commands    */
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|GroupCommand
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|_values
return|;
block|}
block|}
end_class

end_unit

