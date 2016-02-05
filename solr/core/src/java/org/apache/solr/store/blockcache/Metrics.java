begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicLong
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
name|core
operator|.
name|SolrInfoMBean
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
name|search
operator|.
name|SolrCacheBase
import|;
end_import

begin_comment
comment|/**  * A {@link SolrInfoMBean} that provides metrics on block cache operations.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Metrics
specifier|public
class|class
name|Metrics
extends|extends
name|SolrCacheBase
implements|implements
name|SolrInfoMBean
block|{
DECL|class|MethodCall
specifier|public
specifier|static
class|class
name|MethodCall
block|{
DECL|field|invokes
specifier|public
name|AtomicLong
name|invokes
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|times
specifier|public
name|AtomicLong
name|times
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
block|}
DECL|field|blockCacheHit
specifier|public
name|AtomicLong
name|blockCacheHit
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|blockCacheMiss
specifier|public
name|AtomicLong
name|blockCacheMiss
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|blockCacheEviction
specifier|public
name|AtomicLong
name|blockCacheEviction
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|blockCacheSize
specifier|public
name|AtomicLong
name|blockCacheSize
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|rowReads
specifier|public
name|AtomicLong
name|rowReads
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|rowWrites
specifier|public
name|AtomicLong
name|rowWrites
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|recordReads
specifier|public
name|AtomicLong
name|recordReads
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|recordWrites
specifier|public
name|AtomicLong
name|recordWrites
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|queriesExternal
specifier|public
name|AtomicLong
name|queriesExternal
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|queriesInternal
specifier|public
name|AtomicLong
name|queriesInternal
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|shardBuffercacheAllocate
specifier|public
name|AtomicLong
name|shardBuffercacheAllocate
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|shardBuffercacheLost
specifier|public
name|AtomicLong
name|shardBuffercacheLost
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|methodCalls
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MethodCall
argument_list|>
name|methodCalls
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|tableCount
specifier|public
name|AtomicLong
name|tableCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|rowCount
specifier|public
name|AtomicLong
name|rowCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|recordCount
specifier|public
name|AtomicLong
name|recordCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|indexCount
specifier|public
name|AtomicLong
name|indexCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|indexMemoryUsage
specifier|public
name|AtomicLong
name|indexMemoryUsage
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|segmentCount
specifier|public
name|AtomicLong
name|segmentCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|previous
specifier|private
name|long
name|previous
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Metrics
name|metrics
init|=
operator|new
name|Metrics
argument_list|()
decl_stmt|;
name|MethodCall
name|methodCall
init|=
operator|new
name|MethodCall
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|methodCalls
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
name|methodCall
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|metrics
operator|.
name|blockCacheHit
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|blockCacheMiss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|methodCall
operator|.
name|invokes
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|methodCall
operator|.
name|times
operator|.
name|addAndGet
argument_list|(
literal|56000000
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
argument_list|<
name|Number
argument_list|>
name|getStatistics
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Number
argument_list|>
name|stats
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|(
literal|21
argument_list|)
decl_stmt|;
comment|// room for one method call before growing
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|float
name|seconds
init|=
operator|(
name|now
operator|-
name|previous
operator|)
operator|/
literal|1000000000.0f
decl_stmt|;
name|long
name|hits
init|=
name|blockCacheHit
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|lookups
init|=
name|hits
operator|+
name|blockCacheMiss
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"lookups"
argument_list|,
name|getPerSecond
argument_list|(
name|lookups
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"hits"
argument_list|,
name|getPerSecond
argument_list|(
name|hits
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|lookups
argument_list|,
name|hits
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"evictions"
argument_list|,
name|getPerSecond
argument_list|(
name|blockCacheEviction
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|blockCacheSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"row.reads"
argument_list|,
name|getPerSecond
argument_list|(
name|rowReads
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"row.writes"
argument_list|,
name|getPerSecond
argument_list|(
name|rowWrites
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"record.reads"
argument_list|,
name|getPerSecond
argument_list|(
name|recordReads
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"record.writes"
argument_list|,
name|getPerSecond
argument_list|(
name|recordWrites
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"query.external"
argument_list|,
name|getPerSecond
argument_list|(
name|queriesExternal
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"query.internal"
argument_list|,
name|getPerSecond
argument_list|(
name|queriesInternal
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"buffercache.allocations"
argument_list|,
name|getPerSecond
argument_list|(
name|shardBuffercacheAllocate
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"buffercache.lost"
argument_list|,
name|getPerSecond
argument_list|(
name|shardBuffercacheLost
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|MethodCall
argument_list|>
name|entry
range|:
name|methodCalls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MethodCall
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|long
name|invokes
init|=
name|value
operator|.
name|invokes
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|times
init|=
name|value
operator|.
name|times
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|float
name|avgTimes
init|=
operator|(
name|times
operator|/
operator|(
name|float
operator|)
name|invokes
operator|)
operator|/
literal|1000000000.0f
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"methodcalls."
operator|+
name|key
operator|+
literal|".count"
argument_list|,
name|getPerSecond
argument_list|(
name|invokes
argument_list|,
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"methodcalls."
operator|+
name|key
operator|+
literal|".time"
argument_list|,
name|avgTimes
argument_list|)
expr_stmt|;
block|}
name|stats
operator|.
name|add
argument_list|(
literal|"tables"
argument_list|,
name|tableCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
name|rowCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"records"
argument_list|,
name|recordCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"index.count"
argument_list|,
name|indexCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"index.memoryusage"
argument_list|,
name|indexMemoryUsage
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"index.segments"
argument_list|,
name|segmentCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|previous
operator|=
name|now
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|method|getPerSecond
specifier|private
name|float
name|getPerSecond
parameter_list|(
name|long
name|value
parameter_list|,
name|float
name|seconds
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|value
operator|/
name|seconds
argument_list|)
return|;
block|}
comment|// SolrInfoMBean methods
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"HdfsBlockCache"
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provides metrics for the HdfsDirectoryFactory BlockCache."
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

