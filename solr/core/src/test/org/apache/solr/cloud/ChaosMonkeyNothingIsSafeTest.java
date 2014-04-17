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
name|net
operator|.
name|ConnectException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|ConcurrentUpdateSolrServer
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
name|HttpClientUtil
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakLingering
import|;
end_import

begin_class
annotation|@
name|Slow
annotation|@
name|ThreadLeakLingering
argument_list|(
name|linger
operator|=
literal|60000
argument_list|)
DECL|class|ChaosMonkeyNothingIsSafeTest
specifier|public
class|class
name|ChaosMonkeyNothingIsSafeTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|FAIL_TOLERANCE
specifier|private
specifier|static
specifier|final
name|int
name|FAIL_TOLERANCE
init|=
literal|20
decl_stmt|;
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChaosMonkeyNothingIsSafeTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// can help to hide this when testing and looking at logs
comment|//ignoreException("shard update error");
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
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
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
DECL|method|ChaosMonkeyNothingIsSafeTest
specifier|public
name|ChaosMonkeyNothingIsSafeTest
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
literal|"2"
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
literal|"7"
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
name|boolean
name|testsSuccesful
init|=
literal|false
decl_stmt|;
try|try
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
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
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
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
comment|// make sure we have leaders for each shard
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|sliceCount
condition|;
name|j
operator|++
control|)
block|{
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard"
operator|+
name|j
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
comment|// make sure we again have leaders for each shard
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we cannot do delete by query
comment|// as it's not supported for recovery
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StopableThread
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
name|TEST_NIGHTLY
condition|?
literal|3
else|:
literal|1
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|i
operator|=
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
name|threadCount
operator|=
literal|1
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|i
operator|=
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
name|StopableSearchThread
name|searchThread
init|=
operator|new
name|StopableSearchThread
argument_list|()
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|searchThread
argument_list|)
expr_stmt|;
name|searchThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// TODO: we only do this sometimes so that we can sometimes compare against control,
comment|// it's currently hard to know what requests failed when using ConcurrentSolrUpdateServer
name|boolean
name|runFullThrottle
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|runFullThrottle
condition|)
block|{
name|FullThrottleStopableIndexingThread
name|ftIndexThread
init|=
operator|new
name|FullThrottleStopableIndexingThread
argument_list|(
name|clients
argument_list|,
literal|"ft1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|ftIndexThread
argument_list|)
expr_stmt|;
name|ftIndexThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|chaosMonkey
operator|.
name|startTheMonkey
argument_list|(
literal|true
argument_list|,
literal|10000
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
name|StopableThread
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
comment|// start any downed jetties to be sure we still will end up with a leader per shard...
comment|// wait for stop...
for|for
control|(
name|StopableThread
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
comment|// try and wait for any replications and what not to finish...
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// wait until there are no recoveries...
name|waitForThingsToLevelOut
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|//Math.round((runLength / 1000.0f / 3.0f)));
comment|// make sure we again have leaders for each shard
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|sliceCount
condition|;
name|j
operator|++
control|)
block|{
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard"
operator|+
name|j
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
comment|// TODO: assert we didnt kill everyone
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// we expect full throttle fails, but cloud client should not easily fail
for|for
control|(
name|StopableThread
name|indexThread
range|:
name|threads
control|)
block|{
if|if
condition|(
name|indexThread
operator|instanceof
name|StopableIndexingThread
operator|&&
operator|!
operator|(
name|indexThread
operator|instanceof
name|FullThrottleStopableIndexingThread
operator|)
condition|)
block|{
name|assertFalse
argument_list|(
literal|"There were too many update fails - we expect it can happen, but shouldn't easily"
argument_list|,
operator|(
operator|(
name|StopableIndexingThread
operator|)
name|indexThread
operator|)
operator|.
name|getFailCount
argument_list|()
operator|>
name|FAIL_TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
comment|// full throttle thread can
comment|// have request fails
name|checkShardConsistency
argument_list|(
operator|!
name|runFullThrottle
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|ctrlDocs
init|=
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
decl_stmt|;
comment|// ensure we have added more than 0 docs
name|long
name|cloudClientDocs
init|=
name|cloudClient
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
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Found "
operator|+
name|ctrlDocs
operator|+
literal|" control docs"
argument_list|,
name|cloudClientDocs
operator|>
literal|0
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
name|testsSuccesful
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testsSuccesful
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|FullThrottleStopableIndexingThread
class|class
name|FullThrottleStopableIndexingThread
extends|extends
name|StopableIndexingThread
block|{
DECL|field|httpClient
specifier|private
name|HttpClient
name|httpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|field|clientIndex
name|int
name|clientIndex
init|=
literal|0
decl_stmt|;
DECL|field|suss
specifier|private
name|ConcurrentUpdateSolrServer
name|suss
decl_stmt|;
DECL|field|clients
specifier|private
name|List
argument_list|<
name|SolrServer
argument_list|>
name|clients
decl_stmt|;
DECL|field|fails
specifier|private
name|AtomicInteger
name|fails
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|FullThrottleStopableIndexingThread
specifier|public
name|FullThrottleStopableIndexingThread
parameter_list|(
name|List
argument_list|<
name|SolrServer
argument_list|>
name|clients
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|doDeletes
parameter_list|)
block|{
name|super
argument_list|(
name|controlClient
argument_list|,
name|cloudClient
argument_list|,
name|id
argument_list|,
name|doDeletes
argument_list|)
expr_stmt|;
name|setName
argument_list|(
literal|"FullThrottleStopableIndexingThread"
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|clients
operator|=
name|clients
expr_stmt|;
name|HttpClientUtil
operator|.
name|setConnectionTimeout
argument_list|(
name|httpClient
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
name|HttpClientUtil
operator|.
name|setSoTimeout
argument_list|(
name|httpClient
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
name|suss
operator|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
operator|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBaseURL
argument_list|()
argument_list|,
name|httpClient
argument_list|,
literal|8
argument_list|,
literal|2
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"suss error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|numDeletes
init|=
literal|0
decl_stmt|;
name|int
name|numAdds
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
operator|&&
operator|!
name|stop
condition|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|id
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
operator|++
name|i
expr_stmt|;
if|if
condition|(
name|doDeletes
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|deletes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|delete
init|=
name|deletes
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|numDeletes
operator|++
expr_stmt|;
name|suss
operator|.
name|deleteById
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|changeUrlOnError
argument_list|(
name|e
argument_list|)
expr_stmt|;
comment|//System.err.println("REQUEST FAILED:");
comment|//e.printStackTrace();
name|fails
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|numAdds
operator|++
expr_stmt|;
if|if
condition|(
name|numAdds
operator|>
operator|(
name|TEST_NIGHTLY
condition|?
literal|4002
else|:
literal|197
operator|)
condition|)
continue|continue;
name|SolrInputDocument
name|doc
init|=
name|getDoc
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"Saxon heptarchies that used to rip around so in old times and raise Cain.  My, you ought to seen old Henry the Eight when he was in bloom.  He WAS a blossom.  He used to marry a new wife every day, and chop off her head next morning.  And he would do it just as indifferent as if "
argument_list|)
decl_stmt|;
name|suss
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|changeUrlOnError
argument_list|(
name|e
argument_list|)
expr_stmt|;
comment|//System.err.println("REQUEST FAILED:");
comment|//e.printStackTrace();
name|fails
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doDeletes
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|deletes
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"FT added docs:"
operator|+
name|numAdds
operator|+
literal|" with "
operator|+
name|fails
operator|+
literal|" fails"
operator|+
literal|" deletes:"
operator|+
name|numDeletes
argument_list|)
expr_stmt|;
block|}
DECL|method|changeUrlOnError
specifier|private
name|void
name|changeUrlOnError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ConnectException
condition|)
block|{
name|clientIndex
operator|++
expr_stmt|;
if|if
condition|(
name|clientIndex
operator|>
name|clients
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|clientIndex
operator|=
literal|0
expr_stmt|;
block|}
name|suss
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|suss
operator|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
operator|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
name|clientIndex
argument_list|)
operator|)
operator|.
name|getBaseURL
argument_list|()
argument_list|,
name|httpClient
argument_list|,
literal|30
argument_list|,
literal|3
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"suss error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|safeStop
specifier|public
name|void
name|safeStop
parameter_list|()
block|{
name|stop
operator|=
literal|true
expr_stmt|;
name|suss
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|suss
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFailCount
specifier|public
name|int
name|getFailCount
parameter_list|()
block|{
return|return
name|fails
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAddFails
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAddFails
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getDeleteFails
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDeleteFails
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
empty_stmt|;
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
name|getDoc
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

