begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|BaseDistributedSearchTestCase
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
name|CommonParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"FIXME: test fails on hudson"
argument_list|)
DECL|class|DistributedClusteringComponentTest
specifier|public
class|class
name|DistributedClusteringComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
comment|// TODO: this should work with just "solr-clustering"...
return|return
name|getFile
argument_list|(
literal|"solr-clustering"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|int
name|numberOfDocs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
index|[]
name|doc
range|:
name|AbstractClusteringTestCase
operator|.
name|DOCUMENTS
control|)
block|{
name|index
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numberOfDocs
operator|++
argument_list|)
argument_list|,
literal|"url"
argument_list|,
name|doc
index|[
literal|0
index|]
argument_list|,
literal|"title"
argument_list|,
name|doc
index|[
literal|1
index|]
argument_list|,
literal|"snippet"
argument_list|,
name|doc
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Only really care about the clusters for this test case, so drop the header and response
name|handle
operator|.
name|put
argument_list|(
literal|"responseHeader"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|ClusteringComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"*:*"
argument_list|,
name|CommonParams
operator|.
name|SORT
argument_list|,
name|id
operator|+
literal|" desc"
argument_list|,
name|ClusteringParams
operator|.
name|USE_SEARCH_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// destroy is not needed because tearDown method of base class does it.
comment|//destroyServers();
block|}
block|}
end_class

end_unit

