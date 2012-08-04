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
name|junit
operator|.
name|Ignore
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

begin_class
annotation|@
name|Slow
annotation|@
name|Ignore
argument_list|(
literal|"ignore while investigating jenkins fails"
argument_list|)
DECL|class|ChaosMonkeyNothingIsSafeTest
specifier|public
class|class
name|ChaosMonkeyNothingIsSafeTest
extends|extends
name|FullSolrCloudTest
block|{
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
DECL|field|BASE_RUN_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|BASE_RUN_LENGTH
init|=
literal|180000
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{   }
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{   }
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
comment|// TODO use @Noisy annotation as we expect lots of exceptions
comment|//ignoreException(".*");
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
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|6
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
comment|// we cannot do delete by query
comment|// as it's not supported for recovery
comment|// del("*:*");
name|List
argument_list|<
name|StopableThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|StopableThread
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|threadCount
init|=
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
name|i
operator|*
literal|50000
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
name|FullThrottleStopableIndexingThread
name|ftIndexThread
init|=
operator|new
name|FullThrottleStopableIndexingThread
argument_list|(
name|clients
argument_list|,
name|i
operator|*
literal|50000
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
name|chaosMonkey
operator|.
name|startTheMonkey
argument_list|(
literal|true
argument_list|,
literal|1500
argument_list|)
expr_stmt|;
name|int
name|runLength
init|=
name|atLeast
argument_list|(
name|BASE_RUN_LENGTH
argument_list|)
decl_stmt|;
try|try
block|{
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
comment|// fails will happen...
comment|// for (StopableIndexingThread indexThread : threads) {
comment|// assertEquals(0, indexThread.getFails());
comment|// }
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
name|Math
operator|.
name|round
argument_list|(
operator|(
name|runLength
operator|/
literal|1000.0f
operator|/
literal|5.0f
operator|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|getLeaderProps
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
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
comment|// skip the randoms - they can deadlock...
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
DECL|method|getDoc
specifier|private
name|SolrInputDocument
name|getDoc
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
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
return|return
name|doc
return|;
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
name|int
name|startI
parameter_list|,
name|boolean
name|doDeletes
parameter_list|)
block|{
name|super
argument_list|(
name|startI
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
name|startI
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
name|Integer
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
name|Integer
operator|.
name|toString
argument_list|(
name|delete
argument_list|)
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
literal|4000
condition|)
continue|continue;
name|SolrInputDocument
name|doc
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
name|i
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
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
name|i
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
DECL|method|getFails
specifier|public
name|int
name|getFails
parameter_list|()
block|{
return|return
name|fails
operator|.
name|get
argument_list|()
return|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

