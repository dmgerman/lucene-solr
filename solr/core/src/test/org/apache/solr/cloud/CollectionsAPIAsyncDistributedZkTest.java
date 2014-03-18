begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|SolrServer
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
name|SolrServerException
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
name|impl
operator|.
name|HttpSolrServer
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
name|request
operator|.
name|CollectionAdminRequest
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
name|CollectionAdminResponse
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
name|update
operator|.
name|DirectUpdateHandler2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Tests the Cloud Collections API.  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|CollectionsAPIAsyncDistributedZkTest
specifier|public
class|class
name|CollectionsAPIAsyncDistributedZkTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|useJettyDataDir
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|CollectionsAPIAsyncDistributedZkTest
specifier|public
name|CollectionsAPIAsyncDistributedZkTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|4
expr_stmt|;
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
name|testSolrJAPICalls
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSolrJAPICalls
specifier|private
name|void
name|testSolrJAPICalls
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|createNewSolrServer
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"testasynccollectioncreation"
argument_list|,
literal|2
argument_list|,
literal|"conf1"
argument_list|,
name|server
argument_list|,
literal|"1001"
argument_list|)
expr_stmt|;
name|String
name|state
init|=
literal|null
decl_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1001"
argument_list|,
literal|10
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CreateCollection task did not complete!"
argument_list|,
literal|"completed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"testasynccollectioncreation"
argument_list|,
literal|2
argument_list|,
literal|"conf1"
argument_list|,
name|server
argument_list|,
literal|"1002"
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1002"
argument_list|,
literal|3
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Recreating a collection with the same name didn't fail, should have."
argument_list|,
literal|"failed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
literal|"testasynccollectioncreation"
argument_list|,
literal|"shard1"
argument_list|,
name|server
argument_list|,
literal|"1003"
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1003"
argument_list|,
literal|60
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard split did not complete. Last recorded state: "
operator|+
name|state
argument_list|,
literal|"completed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|getRequestStateAfterCompletion
specifier|private
name|String
name|getRequestStateAfterCompletion
parameter_list|(
name|String
name|requestId
parameter_list|,
name|int
name|waitForSeconds
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|state
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|waitForSeconds
operator|--
operator|>
literal|0
condition|)
block|{
name|state
operator|=
name|getRequestState
argument_list|(
name|requestId
argument_list|,
name|server
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|equals
argument_list|(
literal|"completed"
argument_list|)
operator|||
name|state
operator|.
name|equals
argument_list|(
literal|"failed"
argument_list|)
condition|)
return|return
name|state
return|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
return|return
name|state
return|;
block|}
DECL|method|getRequestState
specifier|private
name|String
name|getRequestState
parameter_list|(
name|String
name|requestId
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|CollectionAdminResponse
name|response
init|=
name|CollectionAdminRequest
operator|.
name|requestStatus
argument_list|(
name|requestId
argument_list|,
name|server
argument_list|)
decl_stmt|;
name|NamedList
name|innerResponse
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
return|return
operator|(
name|String
operator|)
name|innerResponse
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
comment|// insurance
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

