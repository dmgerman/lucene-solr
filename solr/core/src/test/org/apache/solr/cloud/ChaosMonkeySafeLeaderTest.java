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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|BadApple
import|;
end_import

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
name|SolrQuery
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
name|CloudSolrServer
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
name|SolrInputDocument
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
name|core
operator|.
name|Diagnostics
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
name|SolrCmdDistributor
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
name|AfterClass
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
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
annotation|@
name|Slow
DECL|class|ChaosMonkeySafeLeaderTest
specifier|public
class|class
name|ChaosMonkeySafeLeaderTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|RUN_LENGTH
specifier|private
specifier|static
specifier|final
name|Integer
name|RUN_LENGTH
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.runlength"
argument_list|,
literal|"-1"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|SolrCmdDistributor
operator|.
name|testing_errorHook
operator|=
operator|new
name|Diagnostics
operator|.
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|Object
modifier|...
name|data
parameter_list|)
block|{
name|Exception
name|e
init|=
operator|(
name|Exception
operator|)
name|data
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Timeout"
argument_list|)
condition|)
block|{
name|Diagnostics
operator|.
name|logThreadDumps
argument_list|(
literal|"REQUESTING THREAD DUMP DUE TO TIMEOUT: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{
name|SolrCmdDistributor
operator|.
name|testing_errorHook
operator|=
literal|null
expr_stmt|;
block|}
DECL|field|fieldNames
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"f_i"
block|,
literal|"f_f"
block|,
literal|"f_d"
block|,
literal|"f_l"
block|,
literal|"f_dt"
block|}
decl_stmt|;
DECL|field|randVals
specifier|protected
specifier|static
specifier|final
name|RandVal
index|[]
name|randVals
init|=
operator|new
name|RandVal
index|[]
block|{
name|rint
block|,
name|rfloat
block|,
name|rdouble
block|,
name|rlong
block|,
name|rdate
block|}
decl_stmt|;
DECL|method|getFieldNames
specifier|public
name|String
index|[]
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
DECL|method|getRandValues
specifier|public
name|RandVal
index|[]
name|getRandValues
parameter_list|()
block|{
return|return
name|randVals
return|;
block|}
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
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|ChaosMonkeySafeLeaderTest
specifier|public
name|ChaosMonkeySafeLeaderTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.slicecount"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|shardCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.shardcount"
argument_list|,
literal|"12"
argument_list|)
argument_list|)
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
comment|// randomly turn on 1 seconds 'soft' commit
name|randomlyEnableAutoSoftCommit
argument_list|()
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StopableIndexingThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|threadCount
init|=
literal|2
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|StopableIndexingThread
name|indexThread
init|=
operator|new
name|StopableIndexingThread
argument_list|(
name|controlClient
argument_list|,
name|cloudClient
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|indexThread
argument_list|)
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|chaosMonkey
operator|.
name|startTheMonkey
argument_list|(
literal|false
argument_list|,
literal|500
argument_list|)
expr_stmt|;
try|try
block|{
name|long
name|runLength
decl_stmt|;
if|if
condition|(
name|RUN_LENGTH
operator|!=
operator|-
literal|1
condition|)
block|{
name|runLength
operator|=
name|RUN_LENGTH
expr_stmt|;
block|}
else|else
block|{
name|int
index|[]
name|runTimes
decl_stmt|;
if|if
condition|(
name|TEST_NIGHTLY
condition|)
block|{
name|runTimes
operator|=
operator|new
name|int
index|[]
block|{
literal|5000
block|,
literal|6000
block|,
literal|10000
block|,
literal|15000
block|,
literal|25000
block|,
literal|30000
block|,
literal|30000
block|,
literal|45000
block|,
literal|90000
block|,
literal|120000
block|}
expr_stmt|;
block|}
else|else
block|{
name|runTimes
operator|=
operator|new
name|int
index|[]
block|{
literal|5000
block|,
literal|7000
block|,
literal|15000
block|}
expr_stmt|;
block|}
name|runLength
operator|=
name|runTimes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|runTimes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|runLength
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|chaosMonkey
operator|.
name|stopTheMonkey
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
block|}
comment|// wait for stop...
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|indexThread
operator|.
name|getFailCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// try and wait for any replications and what not to finish...
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|180000
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control docs:"
operator|+
name|controlClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|+
literal|"\n\n"
argument_list|)
expr_stmt|;
comment|// try and make a collection to make sure the overseer has survived the expiration and session loss
comment|// sometimes we restart zookeeper as well
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkServer
operator|.
name|getZkDir
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|CloudSolrServer
name|client
init|=
name|createCloudClient
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
try|try
block|{
name|createCollection
argument_list|(
literal|null
argument_list|,
literal|"testcollection"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|client
argument_list|,
literal|null
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|numShardsNumReplicas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|numShardsNumReplicas
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|numShardsNumReplicas
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkForCollection
argument_list|(
literal|"testcollection"
argument_list|,
name|numShardsNumReplicas
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// skip the randoms - they can deadlock...
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

