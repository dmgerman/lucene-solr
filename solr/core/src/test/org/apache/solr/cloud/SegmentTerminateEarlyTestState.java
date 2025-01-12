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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|CloudSolrClient
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
name|QueryResponse
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
name|SolrDocument
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
name|ShardParams
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
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_class
DECL|class|SegmentTerminateEarlyTestState
class|class
name|SegmentTerminateEarlyTestState
block|{
DECL|field|KEY_FIELD
specifier|static
specifier|final
name|String
name|KEY_FIELD
init|=
literal|"id"
decl_stmt|;
comment|// for historic reasons, this is refered to as a "timestamp" field, but in actuallity is just an int
comment|// value representing a number of "minutes" between 0-60.
comment|// aka: I decided not to rename a million things while refactoring this test
DECL|field|TIMESTAMP_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|TIMESTAMP_FIELD
init|=
literal|"timestamp_i_dvo"
decl_stmt|;
DECL|field|ODD_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ODD_FIELD
init|=
literal|"odd_l1"
decl_stmt|;
comment|//<dynamicField name="*_l1"  type="long"   indexed="true"  stored="true" multiValued="false"/>
DECL|field|QUAD_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|QUAD_FIELD
init|=
literal|"quad_l1"
decl_stmt|;
comment|//<dynamicField name="*_l1"  type="long"   indexed="true"  stored="true" multiValued="false"/>
DECL|field|minTimestampDocKeys
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|minTimestampDocKeys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxTimestampDocKeys
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|maxTimestampDocKeys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|minTimestampMM
name|Integer
name|minTimestampMM
init|=
literal|null
decl_stmt|;
DECL|field|maxTimestampMM
name|Integer
name|maxTimestampMM
init|=
literal|null
decl_stmt|;
DECL|field|numDocs
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
DECL|field|rand
specifier|final
name|Random
name|rand
decl_stmt|;
DECL|method|SegmentTerminateEarlyTestState
specifier|public
name|SegmentTerminateEarlyTestState
parameter_list|(
name|Random
name|rand
parameter_list|)
block|{
name|this
operator|.
name|rand
operator|=
name|rand
expr_stmt|;
block|}
DECL|method|addDocuments
name|void
name|addDocuments
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|,
name|int
name|numCommits
parameter_list|,
name|int
name|numDocsPerCommit
parameter_list|,
name|boolean
name|optimize
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|cc
init|=
literal|1
init|;
name|cc
operator|<=
name|numCommits
condition|;
operator|++
name|cc
control|)
block|{
for|for
control|(
name|int
name|nn
init|=
literal|1
init|;
name|nn
operator|<=
name|numDocsPerCommit
condition|;
operator|++
name|nn
control|)
block|{
operator|++
name|numDocs
expr_stmt|;
specifier|final
name|Integer
name|docKey
init|=
operator|new
name|Integer
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|KEY_FIELD
argument_list|,
literal|""
operator|+
name|docKey
argument_list|)
expr_stmt|;
specifier|final
name|int
name|MM
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|60
argument_list|)
decl_stmt|;
comment|// minutes
if|if
condition|(
name|minTimestampMM
operator|==
literal|null
operator|||
name|MM
operator|<=
name|minTimestampMM
operator|.
name|intValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|minTimestampMM
operator|!=
literal|null
operator|&&
name|MM
operator|<
name|minTimestampMM
operator|.
name|intValue
argument_list|()
condition|)
block|{
name|minTimestampDocKeys
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|minTimestampMM
operator|=
operator|new
name|Integer
argument_list|(
name|MM
argument_list|)
expr_stmt|;
name|minTimestampDocKeys
operator|.
name|add
argument_list|(
name|docKey
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxTimestampMM
operator|==
literal|null
operator|||
name|maxTimestampMM
operator|.
name|intValue
argument_list|()
operator|<=
name|MM
condition|)
block|{
if|if
condition|(
name|maxTimestampMM
operator|!=
literal|null
operator|&&
name|maxTimestampMM
operator|.
name|intValue
argument_list|()
operator|<
name|MM
condition|)
block|{
name|maxTimestampDocKeys
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|maxTimestampMM
operator|=
operator|new
name|Integer
argument_list|(
name|MM
argument_list|)
expr_stmt|;
name|maxTimestampDocKeys
operator|.
name|add
argument_list|(
name|docKey
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|setField
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
operator|(
name|Integer
operator|)
name|MM
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|ODD_FIELD
argument_list|,
literal|""
operator|+
operator|(
name|numDocs
operator|%
literal|2
operator|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|QUAD_FIELD
argument_list|,
literal|""
operator|+
operator|(
name|numDocs
operator|%
literal|4
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|cloudSolrClient
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|optimize
condition|)
block|{
name|cloudSolrClient
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|queryTimestampDescending
name|void
name|queryTimestampDescending
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
name|maxTimestampDocKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not even"
argument_list|,
operator|(
name|numDocs
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|oddFieldValue
init|=
operator|new
name|Long
argument_list|(
name|maxTimestampDocKeys
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
operator|%
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|ODD_FIELD
operator|+
literal|":"
operator|+
name|oddFieldValue
argument_list|)
decl_stmt|;
name|query
operator|.
name|setSort
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
name|KEY_FIELD
argument_list|,
name|ODD_FIELD
argument_list|,
name|TIMESTAMP_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// CommonParams.SEGMENT_TERMINATE_EARLY parameter intentionally absent
specifier|final
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// check correctness of the results count
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
literal|"numFound"
argument_list|,
name|numDocs
operator|/
literal|2
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// check correctness of the first result
if|if
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|SolrDocument
name|solrDocument0
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|idAsInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|KEY_FIELD
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|KEY_FIELD
operator|+
literal|"="
operator|+
name|idAsInt
operator|+
literal|" of ("
operator|+
name|solrDocument0
operator|+
literal|") is not in maxTimestampDocKeys("
operator|+
name|maxTimestampDocKeys
operator|+
literal|")"
argument_list|,
name|maxTimestampDocKeys
operator|.
name|contains
argument_list|(
name|idAsInt
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
name|ODD_FIELD
argument_list|,
name|oddFieldValue
argument_list|,
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|ODD_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check segmentTerminatedEarly flag
name|TestMiniSolrCloudCluster
operator|.
name|assertNull
argument_list|(
literal|"responseHeader.segmentTerminatedEarly present in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|queryTimestampDescendingSegmentTerminateEarlyYes
name|void
name|queryTimestampDescendingSegmentTerminateEarlyYes
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
name|maxTimestampDocKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not even"
argument_list|,
operator|(
name|numDocs
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|oddFieldValue
init|=
operator|new
name|Long
argument_list|(
name|maxTimestampDocKeys
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
operator|%
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|ODD_FIELD
operator|+
literal|":"
operator|+
name|oddFieldValue
argument_list|)
decl_stmt|;
name|query
operator|.
name|setSort
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
name|KEY_FIELD
argument_list|,
name|ODD_FIELD
argument_list|,
name|TIMESTAMP_FIELD
argument_list|)
expr_stmt|;
specifier|final
name|int
name|rowsWanted
init|=
literal|1
decl_stmt|;
name|query
operator|.
name|setRows
argument_list|(
name|rowsWanted
argument_list|)
expr_stmt|;
specifier|final
name|Boolean
name|shardsInfoWanted
init|=
operator|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|Boolean
argument_list|(
name|rand
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|shardsInfoWanted
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfoWanted
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SEGMENT_TERMINATE_EARLY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// check correctness of the results count
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numFound"
argument_list|,
name|rowsWanted
operator|<=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numFound"
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|<=
name|numDocs
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|// check correctness of the first result
if|if
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|SolrDocument
name|solrDocument0
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|idAsInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|KEY_FIELD
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|KEY_FIELD
operator|+
literal|"="
operator|+
name|idAsInt
operator|+
literal|" of ("
operator|+
name|solrDocument0
operator|+
literal|") is not in maxTimestampDocKeys("
operator|+
name|maxTimestampDocKeys
operator|+
literal|")"
argument_list|,
name|maxTimestampDocKeys
operator|.
name|contains
argument_list|(
name|idAsInt
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
name|ODD_FIELD
argument_list|,
name|oddFieldValue
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
name|ODD_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check segmentTerminatedEarly flag
name|TestMiniSolrCloudCluster
operator|.
name|assertNotNull
argument_list|(
literal|"responseHeader.segmentTerminatedEarly missing in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"responseHeader.segmentTerminatedEarly missing/false in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check shards info
specifier|final
name|Object
name|shardsInfo
init|=
name|rsp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|shardsInfoWanted
argument_list|)
condition|)
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertNull
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertNotNull
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfo
argument_list|)
expr_stmt|;
name|int
name|segmentTerminatedEarlyShardsCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|si
range|:
operator|(
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
operator|)
name|shardsInfo
control|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SimpleOrderedMap
operator|)
name|si
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
condition|)
block|{
name|segmentTerminatedEarlyShardsCount
operator|+=
literal|1
expr_stmt|;
block|}
block|}
comment|// check segmentTerminatedEarly flag within shards info
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|segmentTerminatedEarlyShardsCount
operator|+
literal|" shards reported "
operator|+
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|,
operator|(
literal|0
operator|<
name|segmentTerminatedEarlyShardsCount
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|queryTimestampDescendingSegmentTerminateEarlyNo
name|void
name|queryTimestampDescendingSegmentTerminateEarlyNo
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
name|maxTimestampDocKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not even"
argument_list|,
operator|(
name|numDocs
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|oddFieldValue
init|=
operator|new
name|Long
argument_list|(
name|maxTimestampDocKeys
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
operator|%
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|ODD_FIELD
operator|+
literal|":"
operator|+
name|oddFieldValue
argument_list|)
decl_stmt|;
name|query
operator|.
name|setSort
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
name|KEY_FIELD
argument_list|,
name|ODD_FIELD
argument_list|,
name|TIMESTAMP_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Boolean
name|shardsInfoWanted
init|=
operator|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|Boolean
argument_list|(
name|rand
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|shardsInfoWanted
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfoWanted
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SEGMENT_TERMINATE_EARLY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// check correctness of the results count
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
literal|"numFound"
argument_list|,
name|numDocs
operator|/
literal|2
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// check correctness of the first result
if|if
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|SolrDocument
name|solrDocument0
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|idAsInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|KEY_FIELD
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|KEY_FIELD
operator|+
literal|"="
operator|+
name|idAsInt
operator|+
literal|" of ("
operator|+
name|solrDocument0
operator|+
literal|") is not in maxTimestampDocKeys("
operator|+
name|maxTimestampDocKeys
operator|+
literal|")"
argument_list|,
name|maxTimestampDocKeys
operator|.
name|contains
argument_list|(
name|idAsInt
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
name|ODD_FIELD
argument_list|,
name|oddFieldValue
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
name|ODD_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check segmentTerminatedEarly flag
name|TestMiniSolrCloudCluster
operator|.
name|assertNull
argument_list|(
literal|"responseHeader.segmentTerminatedEarly present in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
literal|"responseHeader.segmentTerminatedEarly present/true in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check shards info
specifier|final
name|Object
name|shardsInfo
init|=
name|rsp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|shardsInfoWanted
argument_list|)
condition|)
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertNull
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertNotNull
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardsInfo
argument_list|)
expr_stmt|;
name|int
name|segmentTerminatedEarlyShardsCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|si
range|:
operator|(
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
operator|)
name|shardsInfo
control|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SimpleOrderedMap
operator|)
name|si
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
condition|)
block|{
name|segmentTerminatedEarlyShardsCount
operator|+=
literal|1
expr_stmt|;
block|}
block|}
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
literal|"shards reporting "
operator|+
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|,
literal|0
argument_list|,
name|segmentTerminatedEarlyShardsCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|queryTimestampDescendingSegmentTerminateEarlyYesGrouped
name|void
name|queryTimestampDescendingSegmentTerminateEarlyYesGrouped
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
name|maxTimestampDocKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not even"
argument_list|,
operator|(
name|numDocs
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|oddFieldValue
init|=
operator|new
name|Long
argument_list|(
name|maxTimestampDocKeys
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
operator|%
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|ODD_FIELD
operator|+
literal|":"
operator|+
name|oddFieldValue
argument_list|)
decl_stmt|;
name|query
operator|.
name|setSort
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
name|KEY_FIELD
argument_list|,
name|ODD_FIELD
argument_list|,
name|TIMESTAMP_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SEGMENT_TERMINATE_EARLY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not quad-able"
argument_list|,
operator|(
name|numDocs
operator|%
literal|4
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"group.field"
argument_list|,
name|QUAD_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"group"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// check correctness of the results count
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
literal|"matches"
argument_list|,
name|numDocs
operator|/
literal|2
argument_list|,
name|rsp
operator|.
name|getGroupResponse
argument_list|()
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
comment|// check correctness of the first result
if|if
condition|(
name|rsp
operator|.
name|getGroupResponse
argument_list|()
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatches
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|SolrDocument
name|solrDocument
init|=
name|rsp
operator|.
name|getGroupResponse
argument_list|()
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|idAsInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
name|KEY_FIELD
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|KEY_FIELD
operator|+
literal|"="
operator|+
name|idAsInt
operator|+
literal|" of ("
operator|+
name|solrDocument
operator|+
literal|") is not in maxTimestampDocKeys("
operator|+
name|maxTimestampDocKeys
operator|+
literal|")"
argument_list|,
name|maxTimestampDocKeys
operator|.
name|contains
argument_list|(
name|idAsInt
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
name|ODD_FIELD
argument_list|,
name|oddFieldValue
argument_list|,
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
name|ODD_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check segmentTerminatedEarly flag
comment|// at present segmentTerminateEarly cannot be used with grouped queries
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
literal|"responseHeader.segmentTerminatedEarly present/true in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|queryTimestampAscendingSegmentTerminateEarlyYes
name|void
name|queryTimestampAscendingSegmentTerminateEarlyYes
parameter_list|(
name|CloudSolrClient
name|cloudSolrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMiniSolrCloudCluster
operator|.
name|assertFalse
argument_list|(
name|minTimestampDocKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"numDocs="
operator|+
name|numDocs
operator|+
literal|" is not even"
argument_list|,
operator|(
name|numDocs
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|oddFieldValue
init|=
operator|new
name|Long
argument_list|(
name|minTimestampDocKeys
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
operator|%
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|ODD_FIELD
operator|+
literal|":"
operator|+
name|oddFieldValue
argument_list|)
decl_stmt|;
name|query
operator|.
name|setSort
argument_list|(
name|TIMESTAMP_FIELD
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|)
expr_stmt|;
comment|// a sort order that is _not_ compatible with the merge sort order
name|query
operator|.
name|setFields
argument_list|(
name|KEY_FIELD
argument_list|,
name|ODD_FIELD
argument_list|,
name|TIMESTAMP_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SEGMENT_TERMINATE_EARLY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// check correctness of the results count
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
literal|"numFound"
argument_list|,
name|numDocs
operator|/
literal|2
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// check correctness of the first result
if|if
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|SolrDocument
name|solrDocument0
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|idAsInt
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|KEY_FIELD
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
name|KEY_FIELD
operator|+
literal|"="
operator|+
name|idAsInt
operator|+
literal|" of ("
operator|+
name|solrDocument0
operator|+
literal|") is not in minTimestampDocKeys("
operator|+
name|minTimestampDocKeys
operator|+
literal|")"
argument_list|,
name|minTimestampDocKeys
operator|.
name|contains
argument_list|(
name|idAsInt
argument_list|)
argument_list|)
expr_stmt|;
name|TestMiniSolrCloudCluster
operator|.
name|assertEquals
argument_list|(
name|ODD_FIELD
argument_list|,
name|oddFieldValue
argument_list|,
name|solrDocument0
operator|.
name|getFieldValue
argument_list|(
name|ODD_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check segmentTerminatedEarly flag
name|TestMiniSolrCloudCluster
operator|.
name|assertNotNull
argument_list|(
literal|"responseHeader.segmentTerminatedEarly missing in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
expr_stmt|;
comment|// segmentTerminateEarly cannot be used with incompatible sort orders
name|TestMiniSolrCloudCluster
operator|.
name|assertTrue
argument_list|(
literal|"responseHeader.segmentTerminatedEarly missing/true in "
operator|+
name|rsp
operator|.
name|getResponseHeader
argument_list|()
argument_list|,
name|Boolean
operator|.
name|FALSE
operator|.
name|equals
argument_list|(
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_SEGMENT_TERMINATED_EARLY_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

