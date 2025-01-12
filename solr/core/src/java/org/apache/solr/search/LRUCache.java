begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|Collections
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|concurrent
operator|.
name|TimeUnit
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
name|LongAdder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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
name|metrics
operator|.
name|MetricsMap
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
name|metrics
operator|.
name|SolrMetricManager
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LRUCache
specifier|public
class|class
name|LRUCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|SolrCacheBase
implements|implements
name|SolrCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|,
name|Accountable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
DECL|field|BASE_RAM_BYTES_USED
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|LRUCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|///  Copied from Lucene's LRUQueryCache
comment|// memory usage of a simple term query
DECL|field|DEFAULT_RAM_BYTES_USED
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_RAM_BYTES_USED
init|=
literal|192
decl_stmt|;
DECL|field|HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|public
specifier|static
specifier|final
name|long
name|HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// key + value
operator|*
literal|2
decl_stmt|;
comment|// hash tables need to be oversized to avoid collisions, assume 2x capacity
DECL|field|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|static
specifier|final
name|long
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|// previous& next references
comment|/// End copied code
comment|/* An instance of this class will be shared across multiple instances    * of an LRUCache at the same time.  Make sure everything is thread safe.    */
DECL|class|CumulativeStats
specifier|private
specifier|static
class|class
name|CumulativeStats
block|{
DECL|field|lookups
name|LongAdder
name|lookups
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|hits
name|LongAdder
name|hits
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|inserts
name|LongAdder
name|inserts
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|evictions
name|LongAdder
name|evictions
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
DECL|field|evictionsRamUsage
name|LongAdder
name|evictionsRamUsage
init|=
operator|new
name|LongAdder
argument_list|()
decl_stmt|;
block|}
DECL|field|stats
specifier|private
name|CumulativeStats
name|stats
decl_stmt|;
comment|// per instance stats.  The synchronization used for the map will also be
comment|// used for updating these statistics (and hence they are not AtomicLongs
DECL|field|lookups
specifier|private
name|long
name|lookups
decl_stmt|;
DECL|field|hits
specifier|private
name|long
name|hits
decl_stmt|;
DECL|field|inserts
specifier|private
name|long
name|inserts
decl_stmt|;
DECL|field|evictions
specifier|private
name|long
name|evictions
decl_stmt|;
DECL|field|evictionsRamUsage
specifier|private
name|long
name|evictionsRamUsage
decl_stmt|;
DECL|field|warmupTime
specifier|private
name|long
name|warmupTime
init|=
literal|0
decl_stmt|;
DECL|field|map
specifier|private
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
init|=
literal|"LRU Cache"
decl_stmt|;
DECL|field|cacheMap
specifier|private
name|MetricsMap
name|cacheMap
decl_stmt|;
DECL|field|metricNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|metricNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|registry
specifier|private
name|MetricRegistry
name|registry
decl_stmt|;
DECL|field|maxRamBytes
specifier|private
name|long
name|maxRamBytes
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|// The synchronization used for the map will be used to update this,
comment|// hence not an AtomicLong
DECL|field|ramBytesUsed
specifier|private
name|long
name|ramBytesUsed
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|Object
name|init
parameter_list|(
name|Map
name|args
parameter_list|,
name|Object
name|persistence
parameter_list|,
name|CacheRegenerator
name|regenerator
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|,
name|regenerator
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|str
operator|==
literal|null
condition|?
literal|1024
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"initialSize"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|initialSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|str
operator|==
literal|null
condition|?
literal|1024
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxRamMB"
argument_list|)
expr_stmt|;
specifier|final
name|long
name|maxRamBytes
init|=
name|this
operator|.
name|maxRamBytes
operator|=
name|str
operator|==
literal|null
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
argument_list|)
operator|*
literal|1024L
operator|*
literal|1024L
argument_list|)
decl_stmt|;
name|description
operator|=
name|generateDescription
argument_list|(
name|limit
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|initialSize
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
name|eldest
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
name|limit
operator|||
name|ramBytesUsed
operator|>
name|maxRamBytes
condition|)
block|{
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|ramBytesUsed
operator|>
name|maxRamBytes
condition|)
block|{
name|long
name|bytesToDecrement
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|iterator
init|=
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
do|do
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|instanceof
name|Accountable
condition|)
block|{
name|bytesToDecrement
operator|+=
operator|(
operator|(
name|Accountable
operator|)
name|entry
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bytesToDecrement
operator|+=
name|DEFAULT_RAM_BYTES_USED
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|bytesToDecrement
operator|+=
operator|(
operator|(
name|Accountable
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|bytesToDecrement
operator|+=
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
name|ramBytesUsed
operator|-=
name|bytesToDecrement
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|evictions
operator|++
expr_stmt|;
name|evictionsRamUsage
operator|++
expr_stmt|;
name|stats
operator|.
name|evictions
operator|.
name|increment
argument_list|()
expr_stmt|;
name|stats
operator|.
name|evictionsRamUsage
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|ramBytesUsed
operator|>
name|maxRamBytes
condition|)
do|;
comment|// must return false according to javadocs of removeEldestEntry if we're modifying
comment|// the map ourselves
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// increment evictions regardless of state.
comment|// this doesn't need to be synchronized because it will
comment|// only be called in the context of a higher level synchronized block.
name|evictions
operator|++
expr_stmt|;
name|stats
operator|.
name|evictions
operator|.
name|increment
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
expr_stmt|;
if|if
condition|(
name|persistence
operator|==
literal|null
condition|)
block|{
comment|// must be the first time a cache of this type is being created
name|persistence
operator|=
operator|new
name|CumulativeStats
argument_list|()
expr_stmt|;
block|}
name|stats
operator|=
operator|(
name|CumulativeStats
operator|)
name|persistence
expr_stmt|;
return|return
name|persistence
return|;
block|}
comment|/**    *     * @return Returns the description of this cache.     */
DECL|method|generateDescription
specifier|private
name|String
name|generateDescription
parameter_list|(
name|int
name|limit
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|String
name|description
init|=
literal|"LRU Cache(maxSize="
operator|+
name|limit
operator|+
literal|", initialSize="
operator|+
name|initialSize
decl_stmt|;
if|if
condition|(
name|isAutowarmingOn
argument_list|()
condition|)
block|{
name|description
operator|+=
literal|", "
operator|+
name|getAutowarmDescription
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|description
operator|+=
literal|", maxRamMB="
operator|+
operator|(
name|maxRamBytes
operator|/
literal|1024L
operator|/
literal|1024L
operator|)
expr_stmt|;
block|}
name|description
operator|+=
literal|')'
expr_stmt|;
return|return
name|description
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
if|if
condition|(
name|getState
argument_list|()
operator|==
name|State
operator|.
name|LIVE
condition|)
block|{
name|stats
operator|.
name|inserts
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
comment|// increment local inserts regardless of state???
comment|// it does make it more consistent with the current size...
name|inserts
operator|++
expr_stmt|;
comment|// important to calc and add new ram bytes first so that removeEldestEntry can compare correctly
name|long
name|keySize
init|=
name|DEFAULT_RAM_BYTES_USED
decl_stmt|;
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
name|key
operator|instanceof
name|Accountable
condition|)
block|{
name|keySize
operator|=
operator|(
operator|(
name|Accountable
operator|)
name|key
operator|)
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|long
name|valueSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Accountable
condition|)
block|{
name|Accountable
name|accountable
init|=
operator|(
name|Accountable
operator|)
name|value
decl_stmt|;
name|valueSize
operator|=
name|accountable
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cache: "
operator|+
name|getName
argument_list|()
operator|+
literal|" is configured with maxRamBytes="
operator|+
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|maxRamBytes
argument_list|)
operator|+
literal|" but its values do not implement org.apache.lucene.util.Accountable"
argument_list|)
throw|;
block|}
block|}
name|ramBytesUsed
operator|+=
name|keySize
operator|+
name|valueSize
operator|+
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
block|}
name|V
name|old
init|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|old
operator|!=
literal|null
condition|)
block|{
name|long
name|bytesToDecrement
init|=
operator|(
operator|(
name|Accountable
operator|)
name|old
operator|)
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
comment|// the key existed in the map but we added its size before the put, so let's back out
name|bytesToDecrement
operator|+=
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|key
operator|instanceof
name|Accountable
condition|)
block|{
name|Accountable
name|aKey
init|=
operator|(
name|Accountable
operator|)
name|key
decl_stmt|;
name|bytesToDecrement
operator|+=
name|aKey
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bytesToDecrement
operator|+=
name|DEFAULT_RAM_BYTES_USED
expr_stmt|;
block|}
block|}
name|ramBytesUsed
operator|-=
name|bytesToDecrement
expr_stmt|;
block|}
return|return
name|old
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
name|V
name|val
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|getState
argument_list|()
operator|==
name|State
operator|.
name|LIVE
condition|)
block|{
comment|// only increment lookups and hits if we are live.
name|lookups
operator|++
expr_stmt|;
name|stats
operator|.
name|lookups
operator|.
name|increment
argument_list|()
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|hits
operator|++
expr_stmt|;
name|stats
operator|.
name|hits
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|val
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|warm
specifier|public
name|void
name|warm
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|SolrCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|old
parameter_list|)
block|{
if|if
condition|(
name|regenerator
operator|==
literal|null
condition|)
return|return;
name|long
name|warmingStartTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|LRUCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|other
init|=
operator|(
name|LRUCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|old
decl_stmt|;
comment|// warm entries
if|if
condition|(
name|isAutowarmingOn
argument_list|()
condition|)
block|{
name|Object
index|[]
name|keys
decl_stmt|,
name|vals
init|=
literal|null
decl_stmt|;
comment|// Don't do the autowarming in the synchronized block, just pull out the keys and values.
synchronized|synchronized
init|(
name|other
operator|.
name|map
init|)
block|{
name|int
name|sz
init|=
name|autowarm
operator|.
name|getWarmCount
argument_list|(
name|other
operator|.
name|map
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|keys
operator|=
operator|new
name|Object
index|[
name|sz
index|]
expr_stmt|;
name|vals
operator|=
operator|new
name|Object
index|[
name|sz
index|]
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|iter
init|=
name|other
operator|.
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// iteration goes from oldest (least recently used) to most recently used,
comment|// so we need to skip over the oldest entries.
name|int
name|skip
init|=
name|other
operator|.
name|map
operator|.
name|size
argument_list|()
operator|-
name|sz
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
name|skip
condition|;
name|i
operator|++
control|)
name|iter
operator|.
name|next
argument_list|()
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|keys
index|[
name|i
index|]
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|vals
index|[
name|i
index|]
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|// autowarm from the oldest to the newest entries so that the ordering will be
comment|// correct in the new cache.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|boolean
name|continueRegen
init|=
name|regenerator
operator|.
name|regenerateItem
argument_list|(
name|searcher
argument_list|,
name|this
argument_list|,
name|old
argument_list|,
name|keys
index|[
name|i
index|]
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|continueRegen
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error during auto-warming of key:"
operator|+
name|keys
index|[
name|i
index|]
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|warmupTime
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|warmingStartTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|LRUCache
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|description
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMetricNames
parameter_list|()
block|{
return|return
name|metricNames
return|;
block|}
annotation|@
name|Override
DECL|method|initializeMetrics
specifier|public
name|void
name|initializeMetrics
parameter_list|(
name|SolrMetricManager
name|manager
parameter_list|,
name|String
name|registryName
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|registry
operator|=
name|manager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
name|cacheMap
operator|=
operator|new
name|MetricsMap
argument_list|(
parameter_list|(
name|detailed
parameter_list|,
name|res
parameter_list|)
lambda|->
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
name|res
operator|.
name|put
argument_list|(
literal|"lookups"
argument_list|,
name|lookups
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"hits"
argument_list|,
name|hits
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
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
name|res
operator|.
name|put
argument_list|(
literal|"inserts"
argument_list|,
name|inserts
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"evictions"
argument_list|,
name|evictions
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|res
operator|.
name|put
argument_list|(
literal|"maxRamMB"
argument_list|,
name|maxRamBytes
operator|/
literal|1024L
operator|/
literal|1024L
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"ramBytesUsed"
argument_list|,
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"evictionsRamUsage"
argument_list|,
name|evictionsRamUsage
argument_list|)
expr_stmt|;
block|}
block|}
name|res
operator|.
name|put
argument_list|(
literal|"warmupTime"
argument_list|,
name|warmupTime
argument_list|)
expr_stmt|;
name|long
name|clookups
init|=
name|stats
operator|.
name|lookups
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|chits
init|=
name|stats
operator|.
name|hits
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_lookups"
argument_list|,
name|clookups
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_hits"
argument_list|,
name|chits
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|clookups
argument_list|,
name|chits
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_inserts"
argument_list|,
name|stats
operator|.
name|inserts
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_evictions"
argument_list|,
name|stats
operator|.
name|evictions
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|res
operator|.
name|put
argument_list|(
literal|"cumulative_evictionsRamUsage"
argument_list|,
name|stats
operator|.
name|evictionsRamUsage
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|manager
operator|.
name|registerGauge
argument_list|(
name|this
argument_list|,
name|registryName
argument_list|,
name|cacheMap
argument_list|,
literal|true
argument_list|,
name|scope
argument_list|,
name|getCategory
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// for unit tests only
DECL|method|getMetricsMap
name|MetricsMap
name|getMetricsMap
parameter_list|()
block|{
return|return
name|cacheMap
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricRegistry
specifier|public
name|MetricRegistry
name|getMetricRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|+
operator|(
name|cacheMap
operator|!=
literal|null
condition|?
name|cacheMap
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|""
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|ramBytesUsed
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
if|if
condition|(
name|maxRamBytes
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
name|getName
argument_list|()
argument_list|,
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
extends|extends
name|Accountable
argument_list|>
operator|)
name|map
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

