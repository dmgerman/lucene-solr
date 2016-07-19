begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|SolrDocument
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  * Augments the document with a<code>[docid]</code> integer containing it's current  * (internal) id in the lucene index.  May be<code>-1</code> if this document did not come from the   * index (ie: a RealTimeGet from  the transaction log)  *   * @since solr 4.0  */
end_comment

begin_class
DECL|class|DocIdAugmenterFactory
specifier|public
class|class
name|DocIdAugmenterFactory
extends|extends
name|TransformerFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|DocIdAugmenter
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|DocIdAugmenter
class|class
name|DocIdAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DocIdAugmenter
specifier|public
name|DocIdAugmenter
parameter_list|(
name|String
name|display
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|display
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
block|{
assert|assert
operator|-
literal|1
operator|<=
name|docid
assert|;
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|docid
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

