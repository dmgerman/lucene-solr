begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SimpleSolrResponse
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
name|params
operator|.
name|SolrParams
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
name|ContentStream
import|;
end_import

begin_class
DECL|class|GenericSolrRequest
specifier|public
class|class
name|GenericSolrRequest
extends|extends
name|SolrRequest
argument_list|<
name|SimpleSolrResponse
argument_list|>
block|{
DECL|field|params
specifier|public
name|SolrParams
name|params
decl_stmt|;
DECL|field|response
specifier|public
name|SimpleSolrResponse
name|response
init|=
operator|new
name|SimpleSolrResponse
argument_list|()
decl_stmt|;
DECL|field|contentStreams
specifier|private
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|contentStreams
decl_stmt|;
DECL|method|GenericSolrRequest
specifier|public
name|GenericSolrRequest
parameter_list|(
name|METHOD
name|m
parameter_list|,
name|String
name|path
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|m
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|setContentStreams
specifier|public
name|void
name|setContentStreams
parameter_list|(
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
block|{
name|contentStreams
operator|=
name|streams
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|protected
name|SimpleSolrResponse
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

