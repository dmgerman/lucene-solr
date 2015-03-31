begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
import|;
end_import

begin_comment
comment|/**  * A base class for writing a very simple UpdateProcessor without worrying too much about the API.  * This is deliberately made to support only the add operation  */
end_comment

begin_class
DECL|class|SimpleUpdateProcessorFactory
specifier|public
specifier|abstract
class|class
name|SimpleUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
block|{
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|UpdateRequestProcessor
argument_list|(
name|next
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|process
argument_list|(
name|cmd
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * This object is reused across requests. So,this method should not store anything in the instance variable.    */
DECL|method|process
specifier|protected
specifier|abstract
name|void
name|process
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
function_decl|;
block|}
end_class

end_unit

