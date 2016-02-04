begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|StrUtils
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
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import

begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|PeerSyncTest
specifier|public
class|class
name|PeerSyncTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|numVersions
specifier|private
specifier|static
name|int
name|numVersions
init|=
literal|100
decl_stmt|;
comment|// number of versions to use when syncing
DECL|field|FROM_LEADER
specifier|private
specifier|final
name|String
name|FROM_LEADER
init|=
name|DistribPhase
operator|.
name|FROMLEADER
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|seenLeader
specifier|private
name|ModifiableSolrParams
name|seenLeader
init|=
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|)
decl_stmt|;
DECL|method|PeerSyncTest
specifier|public
name|PeerSyncTest
parameter_list|()
block|{
name|stress
operator|=
literal|0
expr_stmt|;
comment|// TODO: a better way to do this?
name|configString
operator|=
literal|"solrconfig-tlog.xml"
expr_stmt|;
name|schemaString
operator|=
literal|"schema.xml"
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|3
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|SolrClient
name|client0
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SolrClient
name|client1
init|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SolrClient
name|client2
init|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|long
name|v
init|=
literal|0
decl_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
comment|// this fails because client0 has no context (i.e. no updates of its own to judge if applying the updates
comment|// from client1 will bring it into sync with client1)
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|false
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// bring client1 back into sync with client0 by adding the doc
name|add
argument_list|(
name|client1
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"_version_"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
comment|// both have the same version list, so sync should now return true
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// TODO: test that updates weren't necessary
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// now client1 has the context to sync
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|addRandFields
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
name|int
name|toAdd
init|=
call|(
name|int
call|)
argument_list|(
name|numVersions
operator|*
literal|.95
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toAdd
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|+
literal|11
argument_list|)
argument_list|,
literal|"_version_"
argument_list|,
name|v
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sync should fail since there's not enough overlap to give us confidence
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|false
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// add some of the docs that were missing... just enough to give enough overlap
name|int
name|toAdd2
init|=
call|(
name|int
call|)
argument_list|(
name|numVersions
operator|*
literal|.25
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toAdd2
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|client1
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|+
literal|11
argument_list|)
argument_list|,
literal|"_version_"
argument_list|,
name|v
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"_version_ desc"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
comment|// test delete and deleteByQuery
name|v
operator|=
literal|1000
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1001"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|delQ
argument_list|(
name|client0
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"id:1001 OR id:1002"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1002"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client0
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"_version_ desc"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
comment|// test that delete by query is returned even if not requested, and that it doesn't delete newer stuff than it should
name|v
operator|=
literal|2000
expr_stmt|;
name|SolrClient
name|client
init|=
name|client0
decl_stmt|;
name|add
argument_list|(
name|client
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2000"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2001"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|delQ
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"id:2001 OR id:2002"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2002"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|v
operator|=
literal|2000
expr_stmt|;
name|client
operator|=
name|client1
expr_stmt|;
name|add
argument_list|(
name|client
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2000"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|v
expr_stmt|;
comment|// pretend we missed the add of 2001.  peersync should retrieve it, but should also retrieve any deleteByQuery objects after it
comment|// add(client, seenLeader, sdoc("id","2001","_version_",++v));
name|delQ
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"id:2001 OR id:2002"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2002"
argument_list|,
literal|"_version_"
argument_list|,
operator|++
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
operator|++
name|v
argument_list|)
argument_list|)
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"_version_ desc"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
comment|//
comment|// Test that handling reorders work when applying docs retrieved from peer
comment|//
comment|// this should cause us to retrieve the delete (but not the following add)
comment|// the reorder in application shouldn't affect anything
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3000"
argument_list|,
literal|"_version_"
argument_list|,
literal|3001
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client1
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3000"
argument_list|,
literal|"_version_"
argument_list|,
literal|3001
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client0
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
literal|"3000"
argument_list|)
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
comment|// this should cause us to retrieve an add tha was previously deleted
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3001"
argument_list|,
literal|"_version_"
argument_list|,
literal|3003
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client0
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
literal|"3001"
argument_list|)
argument_list|,
literal|"3004"
argument_list|)
expr_stmt|;
name|del
argument_list|(
name|client1
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|,
literal|"_version_"
argument_list|,
literal|"3001"
argument_list|)
argument_list|,
literal|"3004"
argument_list|)
expr_stmt|;
comment|// this should cause us to retrieve an older add that was overwritten
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3002"
argument_list|,
literal|"_version_"
argument_list|,
literal|3004
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client0
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3002"
argument_list|,
literal|"_version_"
argument_list|,
literal|3005
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|client1
argument_list|,
name|seenLeader
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3002"
argument_list|,
literal|"_version_"
argument_list|,
literal|3005
argument_list|)
argument_list|)
expr_stmt|;
name|assertSync
argument_list|(
name|client1
argument_list|,
name|numVersions
argument_list|,
literal|true
argument_list|,
name|shardsArr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client0
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|queryAndCompare
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"_version_ desc"
argument_list|)
argument_list|,
name|client0
argument_list|,
name|client1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSync
name|void
name|assertSync
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|int
name|numVersions
parameter_list|,
name|boolean
name|expectedResult
parameter_list|,
name|String
modifier|...
name|syncWith
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"getVersions"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numVersions
argument_list|)
argument_list|,
literal|"sync"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syncWith
argument_list|)
argument_list|,
literal|','
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|NamedList
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|qr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
operator|(
name|Boolean
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"sync"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

