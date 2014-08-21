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
name|SolrZkClient
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
name|ZkNodeProps
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
name|CoreContainer
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
name|BeforeClass
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
annotation|@
name|Slow
DECL|class|LeaderElectionIntegrationTest
specifier|public
class|class
name|LeaderElectionIntegrationTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractZkTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_SHARD_REPLICAS
specifier|private
specifier|final
specifier|static
name|int
name|NUM_SHARD_REPLICAS
init|=
literal|5
decl_stmt|;
DECL|field|VERBOSE
specifier|private
specifier|static
specifier|final
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
DECL|field|HOST
specifier|private
specifier|static
specifier|final
name|Pattern
name|HOST
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?\\:(\\d\\d\\d\\d)_.*"
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
DECL|field|containerMap
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|CoreContainer
argument_list|>
name|containerMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|shardPorts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|shardPorts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|reader
specifier|private
name|ZkStateReader
name|reader
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
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
name|ignoreException
argument_list|(
literal|"No UpdateLog found - cannot sync"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"No UpdateLog found - cannot recover"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|"8000"
argument_list|)
expr_stmt|;
name|zkDir
operator|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|7000
init|;
name|i
operator|<
literal|7000
operator|+
name|NUM_SHARD_REPLICAS
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|setupContainer
argument_list|(
name|i
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"!!!Could not start container:"
operator|+
name|i
operator|+
literal|" The exception thrown was: "
operator|+
name|t
operator|.
name|getClass
argument_list|()
operator|+
literal|" "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Could not start container:"
operator|+
name|i
operator|+
literal|". Reason:"
operator|+
name|t
operator|.
name|getClass
argument_list|()
operator|+
literal|" "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|setupContainer
argument_list|(
literal|3333
argument_list|,
literal|"shard2"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"!!!Could not start container 3333. The exception thrown was: "
operator|+
name|t
operator|.
name|getClass
argument_list|()
operator|+
literal|" "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Could not start container: 3333"
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|boolean
name|initSuccessful
init|=
literal|false
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/live_nodes"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|liveNodes
operator|.
name|size
argument_list|()
operator|==
name|NUM_SHARD_REPLICAS
operator|+
literal|1
condition|)
block|{
comment|// all nodes up
name|initSuccessful
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for more nodes to come up, now: "
operator|+
name|liveNodes
operator|.
name|size
argument_list|()
operator|+
literal|"/"
operator|+
operator|(
name|NUM_SHARD_REPLICAS
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|initSuccessful
condition|)
block|{
name|fail
argument_list|(
literal|"Init was not successful!"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupContainer
specifier|private
name|void
name|setupContainer
parameter_list|(
name|int
name|port
parameter_list|,
name|String
name|shard
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
name|File
name|data
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"shard"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|TEST_HOME
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|ports
init|=
name|shardPorts
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|ports
operator|==
literal|null
condition|)
block|{
name|ports
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|shardPorts
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|ports
argument_list|)
expr_stmt|;
block|}
name|ports
operator|.
name|add
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|CoreContainer
name|container
init|=
operator|new
name|CoreContainer
argument_list|()
decl_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Container "
operator|+
name|port
operator|+
literal|" has no cores!"
argument_list|,
name|container
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|containerMap
operator|.
name|put
argument_list|(
name|port
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleSliceLeaderElection
specifier|public
name|void
name|testSimpleSliceLeaderElection
parameter_list|()
throws|throws
name|Exception
block|{
comment|//printLayout(zkServer.getZkAddress());
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
comment|// who is the leader?
name|String
name|leader
init|=
name|getLeader
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|shard1Ports
init|=
name|shardPorts
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|int
name|leaderPort
init|=
name|getLeaderPort
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|shard1Ports
operator|.
name|toString
argument_list|()
argument_list|,
name|shard1Ports
operator|.
name|contains
argument_list|(
name|leaderPort
argument_list|)
argument_list|)
expr_stmt|;
name|shard1Ports
operator|.
name|remove
argument_list|(
name|leaderPort
argument_list|)
expr_stmt|;
comment|// kill the leader
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
literal|"Killing "
operator|+
name|leaderPort
argument_list|)
expr_stmt|;
name|containerMap
operator|.
name|get
argument_list|(
name|leaderPort
argument_list|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|//printLayout(zkServer.getZkAddress());
comment|// poll until leader change is visible
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|90
condition|;
name|j
operator|++
control|)
block|{
name|String
name|currentLeader
init|=
name|getLeader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|leader
operator|.
name|equals
argument_list|(
name|currentLeader
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|leader
operator|=
name|getLeader
argument_list|()
expr_stmt|;
name|int
name|newLeaderPort
init|=
name|getLeaderPort
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|int
name|retry
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|leaderPort
operator|==
name|newLeaderPort
condition|)
block|{
if|if
condition|(
name|retry
operator|++
operator|==
literal|60
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|leaderPort
operator|==
name|newLeaderPort
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"We didn't find a new leader! "
operator|+
name|leaderPort
operator|+
literal|" was close, but it's still showing as the leader"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Could not find leader "
operator|+
name|newLeaderPort
operator|+
literal|" in "
operator|+
name|shard1Ports
argument_list|,
name|shard1Ports
operator|.
name|contains
argument_list|(
name|newLeaderPort
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLeaderElectionAfterClientTimeout
specifier|public
name|void
name|testLeaderElectionAfterClientTimeout
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: work out the best timing here...
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|ZkTestServer
operator|.
name|TICK_TIME
operator|*
literal|2
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// timeout the leader
name|String
name|leader
init|=
name|getLeader
argument_list|()
decl_stmt|;
name|int
name|leaderPort
init|=
name|getLeaderPort
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|ZkController
name|zkController
init|=
name|containerMap
operator|.
name|get
argument_list|(
name|leaderPort
argument_list|)
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|closeCnxn
argument_list|()
expr_stmt|;
name|long
name|sessionId
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|zkServer
operator|.
name|expire
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
comment|// wait till leader is changed
if|if
condition|(
name|leaderPort
operator|!=
name|getLeaderPort
argument_list|(
name|getLeader
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// make sure we have waited long enough for the first leader to have come back
name|Thread
operator|.
name|sleep
argument_list|(
name|ZkTestServer
operator|.
name|TICK_TIME
operator|*
literal|2
operator|+
literal|100
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
literal|"kill everyone"
argument_list|)
expr_stmt|;
comment|// kill everyone but the first leader that should have reconnected by now
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|CoreContainer
argument_list|>
name|entry
range|:
name|containerMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|!=
name|leaderPort
condition|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|320
condition|;
name|i
operator|++
control|)
block|{
comment|// wait till leader is changed
try|try
block|{
if|if
condition|(
name|leaderPort
operator|==
name|getLeaderPort
argument_list|(
name|getLeader
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
continue|continue;
block|}
block|}
comment|// the original leader should be leader again now - everyone else is down
comment|// TODO: I saw this fail once...expected:<7000> but was:<7004>
name|assertEquals
argument_list|(
name|leaderPort
argument_list|,
name|getLeaderPort
argument_list|(
name|getLeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//printLayout(zkServer.getZkAddress());
comment|//Thread.sleep(100000);
block|}
DECL|method|getLeader
specifier|private
name|String
name|getLeader
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ZkNodeProps
name|props
init|=
name|reader
operator|.
name|getLeaderRetry
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|30000
argument_list|)
decl_stmt|;
name|String
name|leader
init|=
name|props
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
return|return
name|leader
return|;
block|}
DECL|method|getLeaderPort
specifier|private
name|int
name|getLeaderPort
parameter_list|(
name|String
name|leader
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|HOST
operator|.
name|matcher
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|int
name|leaderPort
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|leaderPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
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
literal|"The leader is:"
operator|+
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
return|return
name|leaderPort
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|printLayout
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|CoreContainer
name|cc
range|:
name|containerMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkClientTimeout"
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
literal|"hostPort"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"shard"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.update.delay"
argument_list|)
expr_stmt|;
block|}
DECL|method|printLayout
specifier|private
name|void
name|printLayout
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkClientTimeout"
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
literal|"shard"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.data.dir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
comment|// wait just a bit for any zk client threads to outlast timeout
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

