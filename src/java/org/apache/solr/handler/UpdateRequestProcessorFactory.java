begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|util
operator|.
name|NamedList
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

begin_comment
comment|/**  * A factory to generate UpdateRequestProcessors for each request.  The default  * implementation does nothing except pass the commands directly to the   * UpdateHandler  *   * @author ryan  * @since solr 1.3  */
end_comment

begin_class
DECL|class|UpdateRequestProcessorFactory
specifier|public
class|class
name|UpdateRequestProcessorFactory
block|{
DECL|method|UpdateRequestProcessorFactory
specifier|public
name|UpdateRequestProcessorFactory
parameter_list|()
block|{        }
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|args
parameter_list|)
block|{
comment|// by default nothing...
block|}
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|UpdateRequestProcessor
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
end_class

end_unit

