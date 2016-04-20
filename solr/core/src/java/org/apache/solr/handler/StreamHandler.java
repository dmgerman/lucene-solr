begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Iterator
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
name|Map
operator|.
name|Entry
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
name|io
operator|.
name|SolrClientCache
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
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|comp
operator|.
name|StreamComparator
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
name|io
operator|.
name|graph
operator|.
name|GatherNodesStream
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
name|io
operator|.
name|graph
operator|.
name|ShortestPathStream
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
name|io
operator|.
name|ops
operator|.
name|ConcatOperation
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
name|io
operator|.
name|ops
operator|.
name|DistinctOperation
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
name|io
operator|.
name|ops
operator|.
name|GroupOperation
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
name|io
operator|.
name|ops
operator|.
name|ReplaceOperation
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
name|io
operator|.
name|stream
operator|.
name|*
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
operator|.
name|ExpressionType
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
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|CountMetric
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
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MaxMetric
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
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MeanMetric
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
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MinMetric
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
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|SumMetric
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
name|SolrException
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
name|params
operator|.
name|SolrParams
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
name|core
operator|.
name|CloseHook
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
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|PermissionNameProvider
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
DECL|class|StreamHandler
specifier|public
class|class
name|StreamHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
implements|,
name|PermissionNameProvider
block|{
DECL|field|clientCache
specifier|static
name|SolrClientCache
name|clientCache
init|=
operator|new
name|SolrClientCache
argument_list|()
decl_stmt|;
DECL|field|streamFactory
specifier|private
name|StreamFactory
name|streamFactory
init|=
operator|new
name|StreamFactory
argument_list|()
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|coreName
specifier|private
name|String
name|coreName
decl_stmt|;
DECL|field|daemons
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DaemonStream
argument_list|>
name|daemons
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|PermissionNameProvider
operator|.
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|request
parameter_list|)
block|{
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|READ_PERM
return|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
comment|/* The stream factory will always contain the zkUrl for the given collection      * Adds default streams with their corresponding function names. These       * defaults can be overridden or added to in the solrConfig in the stream       * RequestHandler def. Example config override      *<lst name="streamFunctions">      *<str name="group">org.apache.solr.client.solrj.io.stream.ReducerStream</str>      *<str name="count">org.apache.solr.client.solrj.io.stream.RecordCountStream</str>      *</lst>      * */
name|String
name|defaultCollection
init|=
literal|null
decl_stmt|;
name|String
name|defaultZkhost
init|=
literal|null
decl_stmt|;
name|CoreContainer
name|coreContainer
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|this
operator|.
name|coreName
operator|=
name|core
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|defaultCollection
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
expr_stmt|;
name|defaultZkhost
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkServerAddress
argument_list|()
expr_stmt|;
name|streamFactory
operator|.
name|withCollectionZkHost
argument_list|(
name|defaultCollection
argument_list|,
name|defaultZkhost
argument_list|)
expr_stmt|;
name|streamFactory
operator|.
name|withDefaultZkHost
argument_list|(
name|defaultZkhost
argument_list|)
expr_stmt|;
block|}
name|streamFactory
comment|// source streams
operator|.
name|withFunctionName
argument_list|(
literal|"search"
argument_list|,
name|CloudSolrStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"facet"
argument_list|,
name|FacetStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"update"
argument_list|,
name|UpdateStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"jdbc"
argument_list|,
name|JDBCStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"topic"
argument_list|,
name|TopicStream
operator|.
name|class
argument_list|)
comment|// decorator streams
operator|.
name|withFunctionName
argument_list|(
literal|"merge"
argument_list|,
name|MergeStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"unique"
argument_list|,
name|UniqueStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"top"
argument_list|,
name|RankStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"group"
argument_list|,
name|GroupOperation
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"reduce"
argument_list|,
name|ReducerStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"parallel"
argument_list|,
name|ParallelStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"rollup"
argument_list|,
name|RollupStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"stats"
argument_list|,
name|StatsStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"innerJoin"
argument_list|,
name|InnerJoinStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"leftOuterJoin"
argument_list|,
name|LeftOuterJoinStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"hashJoin"
argument_list|,
name|HashJoinStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"outerHashJoin"
argument_list|,
name|OuterHashJoinStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"intersect"
argument_list|,
name|IntersectStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"complement"
argument_list|,
name|ComplementStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"sort"
argument_list|,
name|SortStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"daemon"
argument_list|,
name|DaemonStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"shortestPath"
argument_list|,
name|ShortestPathStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"gatherNodes"
argument_list|,
name|GatherNodesStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"select"
argument_list|,
name|SelectStream
operator|.
name|class
argument_list|)
comment|// metrics
operator|.
name|withFunctionName
argument_list|(
literal|"min"
argument_list|,
name|MinMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"max"
argument_list|,
name|MaxMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"avg"
argument_list|,
name|MeanMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"sum"
argument_list|,
name|SumMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"count"
argument_list|,
name|CountMetric
operator|.
name|class
argument_list|)
comment|// tuple manipulation operations
operator|.
name|withFunctionName
argument_list|(
literal|"replace"
argument_list|,
name|ReplaceOperation
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"concat"
argument_list|,
name|ConcatOperation
operator|.
name|class
argument_list|)
comment|// stream reduction operations
operator|.
name|withFunctionName
argument_list|(
literal|"group"
argument_list|,
name|GroupOperation
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"distinct"
argument_list|,
name|DistinctOperation
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// This pulls all the overrides and additions from the config
name|Object
name|functionMappingsObj
init|=
name|initArgs
operator|.
name|get
argument_list|(
literal|"streamFunctions"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|functionMappingsObj
condition|)
block|{
name|NamedList
argument_list|<
name|?
argument_list|>
name|functionMappings
init|=
operator|(
name|NamedList
argument_list|<
name|?
argument_list|>
operator|)
name|functionMappingsObj
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|functionMapping
range|:
name|functionMappings
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|findClass
argument_list|(
operator|(
name|String
operator|)
name|functionMapping
operator|.
name|getValue
argument_list|()
argument_list|,
name|Expressible
operator|.
name|class
argument_list|)
decl_stmt|;
name|streamFactory
operator|.
name|withFunctionName
argument_list|(
name|functionMapping
operator|.
name|getKey
argument_list|()
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
block|}
name|core
operator|.
name|addCloseHook
argument_list|(
operator|new
name|CloseHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
annotation|@
name|Override
specifier|public
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|clientCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|params
operator|=
name|adjustParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
literal|"action"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|handleAdmin
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return;
block|}
name|TupleStream
name|tupleStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tupleStream
operator|=
name|this
operator|.
name|streamFactory
operator|.
name|constructStream
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"expr"
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
comment|//Catch exceptions that occur while the stream is being created. This will include streaming expression parse rules.
name|SolrException
operator|.
name|log
argument_list|(
name|logger
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DummyErrorStream
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|worker
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"workerID"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numWorkers
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"numWorkers"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|workerID
operator|=
name|worker
expr_stmt|;
name|context
operator|.
name|numWorkers
operator|=
name|numWorkers
expr_stmt|;
name|context
operator|.
name|setSolrClientCache
argument_list|(
name|clientCache
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"core"
argument_list|,
name|this
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|tupleStream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// if asking for explanation then go get it
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
literal|"explain"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"explanation"
argument_list|,
name|tupleStream
operator|.
name|toExplanation
argument_list|(
name|this
operator|.
name|streamFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tupleStream
operator|instanceof
name|DaemonStream
condition|)
block|{
name|DaemonStream
name|daemonStream
init|=
operator|(
name|DaemonStream
operator|)
name|tupleStream
decl_stmt|;
if|if
condition|(
name|daemons
operator|.
name|containsKey
argument_list|(
name|daemonStream
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|daemons
operator|.
name|remove
argument_list|(
name|daemonStream
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|daemonStream
operator|.
name|open
argument_list|()
expr_stmt|;
comment|//This will start the deamonStream
name|daemons
operator|.
name|put
argument_list|(
name|daemonStream
operator|.
name|getId
argument_list|()
argument_list|,
name|daemonStream
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonResponseStream
argument_list|(
literal|"Deamon:"
operator|+
name|daemonStream
operator|.
name|getId
argument_list|()
operator|+
literal|" started on "
operator|+
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|TimerStream
argument_list|(
operator|new
name|ExceptionStream
argument_list|(
name|tupleStream
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleAdmin
specifier|private
name|void
name|handleAdmin
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|String
name|action
init|=
name|params
operator|.
name|get
argument_list|(
literal|"action"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"stop"
operator|.
name|equalsIgnoreCase
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|params
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|DaemonStream
name|d
init|=
name|daemons
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonResponseStream
argument_list|(
literal|"Deamon:"
operator|+
name|id
operator|+
literal|" stopped on "
operator|+
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonResponseStream
argument_list|(
literal|"Deamon:"
operator|+
name|id
operator|+
literal|" not found on "
operator|+
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"start"
operator|.
name|equalsIgnoreCase
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|params
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|DaemonStream
name|d
init|=
name|daemons
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|d
operator|.
name|open
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonResponseStream
argument_list|(
literal|"Deamon:"
operator|+
name|id
operator|+
literal|" started on "
operator|+
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"list"
operator|.
name|equalsIgnoreCase
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|Collection
argument_list|<
name|DaemonStream
argument_list|>
name|vals
init|=
name|daemons
operator|.
name|values
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonCollectionStream
argument_list|(
name|vals
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"kill"
operator|.
name|equalsIgnoreCase
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|params
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|DaemonStream
name|d
init|=
name|daemons
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"result-set"
argument_list|,
operator|new
name|DaemonResponseStream
argument_list|(
literal|"Deamon:"
operator|+
name|id
operator|+
literal|" killed on "
operator|+
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|adjustParams
specifier|private
name|SolrParams
name|adjustParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|ModifiableSolrParams
name|adjustedParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|adjustedParams
operator|.
name|add
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|adjustedParams
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|adjustedParams
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"StreamHandler"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|class|DummyErrorStream
specifier|public
specifier|static
class|class
name|DummyErrorStream
extends|extends
name|TupleStream
block|{
DECL|field|e
specifier|private
name|Exception
name|e
decl_stmt|;
DECL|method|DummyErrorStream
specifier|public
name|DummyErrorStream
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{     }
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{     }
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"error"
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
while|while
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|msg
operator|=
name|t
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|t
operator|=
name|t
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EXCEPTION"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
block|}
DECL|class|DaemonCollectionStream
specifier|public
specifier|static
class|class
name|DaemonCollectionStream
extends|extends
name|TupleStream
block|{
DECL|field|it
specifier|private
name|Iterator
argument_list|<
name|DaemonStream
argument_list|>
name|it
decl_stmt|;
DECL|method|DaemonCollectionStream
specifier|public
name|DaemonCollectionStream
parameter_list|(
name|Collection
argument_list|<
name|DaemonStream
argument_list|>
name|col
parameter_list|)
block|{
name|this
operator|.
name|it
operator|=
name|col
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{     }
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{     }
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"daemon-collection"
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
block|{
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|it
operator|.
name|next
argument_list|()
operator|.
name|getInfo
argument_list|()
return|;
block|}
else|else
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|DaemonResponseStream
specifier|public
specifier|static
class|class
name|DaemonResponseStream
extends|extends
name|TupleStream
block|{
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|field|sendEOF
specifier|private
name|boolean
name|sendEOF
init|=
literal|false
decl_stmt|;
DECL|method|DaemonResponseStream
specifier|public
name|DaemonResponseStream
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{     }
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{     }
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"daemon-response"
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
block|{
if|if
condition|(
name|sendEOF
condition|)
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
else|else
block|{
name|sendEOF
operator|=
literal|true
expr_stmt|;
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"DaemonOp"
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|TimerStream
specifier|public
specifier|static
class|class
name|TimerStream
extends|extends
name|TupleStream
block|{
DECL|field|begin
specifier|private
name|long
name|begin
decl_stmt|;
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|method|TimerStream
specifier|public
name|TimerStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|)
block|{
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|this
operator|.
name|tupleStream
operator|.
name|getStreamSort
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|begin
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|tupleStream
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|tupleStream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
name|this
operator|.
name|tupleStream
operator|.
name|children
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"timer"
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
literal|"--non-expressible--"
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|tuple
init|=
name|this
operator|.
name|tupleStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|long
name|totalTime
init|=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|begin
operator|)
operator|/
literal|1000000
decl_stmt|;
name|tuple
operator|.
name|fields
operator|.
name|put
argument_list|(
literal|"RESPONSE_TIME"
argument_list|,
name|totalTime
argument_list|)
expr_stmt|;
block|}
return|return
name|tuple
return|;
block|}
block|}
block|}
end_class

end_unit

