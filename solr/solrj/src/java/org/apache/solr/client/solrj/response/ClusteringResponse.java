begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * Encapsulates responses from ClusteringComponent  */
end_comment

begin_class
DECL|class|ClusteringResponse
specifier|public
class|class
name|ClusteringResponse
block|{
DECL|field|LABELS_NODE
specifier|private
specifier|static
specifier|final
name|String
name|LABELS_NODE
init|=
literal|"labels"
decl_stmt|;
DECL|field|DOCS_NODE
specifier|private
specifier|static
specifier|final
name|String
name|DOCS_NODE
init|=
literal|"docs"
decl_stmt|;
DECL|field|SCORE_NODE
specifier|private
specifier|static
specifier|final
name|String
name|SCORE_NODE
init|=
literal|"score"
decl_stmt|;
DECL|field|clusters
specifier|private
name|List
argument_list|<
name|Cluster
argument_list|>
name|clusters
init|=
operator|new
name|LinkedList
argument_list|<
name|Cluster
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ClusteringResponse
specifier|public
name|ClusteringResponse
parameter_list|(
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|clusterInfo
parameter_list|)
block|{
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|clusterNode
range|:
name|clusterInfo
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|labelList
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|docIdList
decl_stmt|;
name|labelList
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|clusterNode
operator|.
name|get
argument_list|(
name|LABELS_NODE
argument_list|)
expr_stmt|;
name|double
name|score
init|=
operator|(
name|double
operator|)
name|clusterNode
operator|.
name|get
argument_list|(
name|SCORE_NODE
argument_list|)
decl_stmt|;
name|docIdList
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|clusterNode
operator|.
name|get
argument_list|(
name|DOCS_NODE
argument_list|)
expr_stmt|;
name|Cluster
name|currentCluster
init|=
operator|new
name|Cluster
argument_list|(
name|labelList
argument_list|,
name|score
argument_list|,
name|docIdList
argument_list|)
decl_stmt|;
name|clusters
operator|.
name|add
argument_list|(
name|currentCluster
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getClusters
specifier|public
name|List
argument_list|<
name|Cluster
argument_list|>
name|getClusters
parameter_list|()
block|{
return|return
name|clusters
return|;
block|}
block|}
end_class

end_unit
