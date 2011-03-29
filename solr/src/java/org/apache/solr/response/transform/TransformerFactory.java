begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_comment
comment|/**  * New instance for each request  *  * @version $Id$  */
end_comment

begin_class
DECL|class|TransformerFactory
specifier|public
specifier|abstract
class|class
name|TransformerFactory
implements|implements
name|NamedListInitializedPlugin
block|{
DECL|field|defaultUserArgs
specifier|protected
name|String
name|defaultUserArgs
init|=
literal|null
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|defaultUserArgs
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"args"
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
specifier|abstract
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|args
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
function_decl|;
DECL|field|defaultFactories
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TransformerFactory
argument_list|>
name|defaultFactories
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TransformerFactory
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultFactories
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
operator|new
name|ExplainAugmenterFactory
argument_list|()
argument_list|)
expr_stmt|;
name|defaultFactories
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
operator|new
name|ValueAugmenterFactory
argument_list|()
argument_list|)
expr_stmt|;
name|defaultFactories
operator|.
name|put
argument_list|(
literal|"docid"
argument_list|,
operator|new
name|DocIdAugmenterFactory
argument_list|()
argument_list|)
expr_stmt|;
name|defaultFactories
operator|.
name|put
argument_list|(
literal|"shard"
argument_list|,
operator|new
name|ShardAugmenterFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

