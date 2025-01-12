begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
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
name|search
operator|.
name|DocSet
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocumentClusteringEngine
specifier|public
specifier|abstract
class|class
name|DocumentClusteringEngine
extends|extends
name|ClusteringEngine
block|{
comment|/**    * Experimental.  Subject to change before the next release    *    * Cluster all the documents in the index.  Clustering is often an expensive task that can take a long time.    * @param solrParams The params controlling clustering    * @return The clustering results    */
DECL|method|cluster
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|?
argument_list|>
name|cluster
parameter_list|(
name|SolrParams
name|solrParams
parameter_list|)
function_decl|;
comment|/**    * Experimental.  Subject to change before the next release    *    * Cluster the set of docs.  Clustering of documents is often an expensive task that can take a long time.    * @param docs The docs to cluster.  If null, cluster all docs as in {@link #cluster(org.apache.solr.common.params.SolrParams)}    * @param solrParams The params controlling the clustering    * @return The results.    */
DECL|method|cluster
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|?
argument_list|>
name|cluster
parameter_list|(
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
function_decl|;
block|}
end_class

end_unit

