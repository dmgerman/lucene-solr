begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|ClusterState
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|DocRouter
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
name|cloud
operator|.
name|Replica
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
name|cloud
operator|.
name|Slice
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
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|SliceStateTest
specifier|public
class|class
name|SliceStateTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testDefaultSliceState
specifier|public
name|void
name|testDefaultSliceState
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|liveNodes
operator|.
name|add
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|sliceToProps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Replica
name|replica
init|=
operator|new
name|Replica
argument_list|(
literal|"node1"
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|sliceToProps
operator|.
name|put
argument_list|(
literal|"node1"
argument_list|,
name|replica
argument_list|)
expr_stmt|;
name|Slice
name|slice
init|=
operator|new
name|Slice
argument_list|(
literal|"shard1"
argument_list|,
name|sliceToProps
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Default state not set to active"
argument_list|,
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
argument_list|,
name|slice
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|slices
operator|.
name|put
argument_list|(
literal|"shard1"
argument_list|,
name|slice
argument_list|)
expr_stmt|;
name|collectionStates
operator|.
name|put
argument_list|(
literal|"collection1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"collection1"
argument_list|,
name|slices
argument_list|,
literal|null
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
operator|new
name|ClusterState
argument_list|(
operator|-
literal|1
argument_list|,
name|liveNodes
argument_list|,
name|collectionStates
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|ClusterState
name|loadedClusterState
init|=
name|ClusterState
operator|.
name|load
argument_list|(
operator|-
literal|1
argument_list|,
name|bytes
argument_list|,
name|liveNodes
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Default state not set to active"
argument_list|,
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
argument_list|,
name|loadedClusterState
operator|.
name|getSlice
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

