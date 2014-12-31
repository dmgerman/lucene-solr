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
name|HttpSolrClient
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
name|QueryRequest
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
name|params
operator|.
name|CollectionParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
DECL|class|OverseerStatusTest
specifier|public
class|class
name|OverseerStatusTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|method|OverseerStatusTest
specifier|public
name|OverseerStatusTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
block|}
annotation|@
name|Override
annotation|@
name|Before
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
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|VERBOSE
operator|||
name|printLayoutOnTearDown
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClient
operator|!=
literal|null
condition|)
block|{
name|controlClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cloudClient
operator|!=
literal|null
condition|)
block|{
name|cloudClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClientCloud
operator|!=
literal|null
condition|)
block|{
name|controlClientCloud
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
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
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
comment|// find existing command counts because collection may be created by base test class too
name|int
name|numCollectionCreates
init|=
literal|0
decl_stmt|,
name|numOverseerCreates
init|=
literal|0
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|OVERSEERSTATUS
operator|.
name|toLower
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|!=
literal|null
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|collection_operations
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"collection_operations"
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection_operations
operator|!=
literal|null
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|createcollection
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|collection_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
operator|.
name|toLower
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|createcollection
operator|!=
literal|null
operator|&&
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|numCollectionCreates
operator|=
operator|(
name|Integer
operator|)
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|overseer_operations
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"overseer_operations"
argument_list|)
decl_stmt|;
if|if
condition|(
name|overseer_operations
operator|!=
literal|null
condition|)
block|{
name|createcollection
operator|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|overseer_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|createcollection
operator|!=
literal|null
operator|&&
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|numOverseerCreates
operator|=
operator|(
name|Integer
operator|)
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|String
name|collectionName
init|=
literal|"overseer_status_test"
decl_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|resp
operator|=
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|OVERSEERSTATUS
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|collection_operations
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"collection_operations"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|overseer_operations
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"overseer_operations"
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|createcollection
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|collection_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
operator|.
name|toLower
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No stats for create in OverseerCollectionProcessor"
argument_list|,
name|numCollectionCreates
operator|+
literal|1
argument_list|,
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
name|createcollection
operator|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|overseer_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No stats for create in Overseer"
argument_list|,
name|numOverseerCreates
operator|+
literal|1
argument_list|,
name|createcollection
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|RELOAD
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|resp
operator|=
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|OVERSEERSTATUS
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|collection_operations
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"collection_operations"
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|reload
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|collection_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|RELOAD
operator|.
name|toLower
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No stats for reload in OverseerCollectionProcessor"
argument_list|,
literal|1
argument_list|,
name|reload
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|SPLITSHARD
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
literal|"non_existent_collection"
argument_list|,
literal|"shard"
argument_list|,
literal|"non_existent_shard"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected because we did not correctly specify required params for split
block|}
name|resp
operator|=
name|invokeCollectionApi
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|OVERSEERSTATUS
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|collection_operations
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"collection_operations"
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|split
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|collection_operations
operator|.
name|get
argument_list|(
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|SPLITSHARD
operator|.
name|toLower
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No stats for split in OverseerCollectionProcessor"
argument_list|,
literal|1
argument_list|,
name|split
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|split
operator|.
name|get
argument_list|(
literal|"recent_failures"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|amIleader
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|collection_operations
operator|.
name|get
argument_list|(
literal|"am_i_leader"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"OverseerCollectionProcessor amILeader stats should not be null"
argument_list|,
name|amIleader
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"avgTimePerRequest"
argument_list|)
argument_list|)
expr_stmt|;
name|amIleader
operator|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|overseer_operations
operator|.
name|get
argument_list|(
literal|"am_i_leader"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Overseer amILeader stats should not be null"
argument_list|,
name|amIleader
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|amIleader
operator|.
name|get
argument_list|(
literal|"avgTimePerRequest"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|updateState
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|overseer_operations
operator|.
name|get
argument_list|(
literal|"update_state"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Overseer update_state stats should not be null"
argument_list|,
name|updateState
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|updateState
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|updateState
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|updateState
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|updateState
operator|.
name|get
argument_list|(
literal|"avgTimePerRequest"
argument_list|)
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

