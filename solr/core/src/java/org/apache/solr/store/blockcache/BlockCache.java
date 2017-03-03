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
name|nio
operator|.
name|ByteBuffer
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Caffeine
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|RemovalCause
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockCache
specifier|public
class|class
name|BlockCache
block|{
DECL|field|_128M
specifier|public
specifier|static
specifier|final
name|int
name|_128M
init|=
literal|134217728
decl_stmt|;
DECL|field|_32K
specifier|public
specifier|static
specifier|final
name|int
name|_32K
init|=
literal|32768
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
name|cache
decl_stmt|;
DECL|field|banks
specifier|private
specifier|final
name|ByteBuffer
index|[]
name|banks
decl_stmt|;
DECL|field|locks
specifier|private
specifier|final
name|BlockLocks
index|[]
name|locks
decl_stmt|;
DECL|field|lockCounters
specifier|private
specifier|final
name|AtomicInteger
index|[]
name|lockCounters
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|numberOfBlocksPerBank
specifier|private
specifier|final
name|int
name|numberOfBlocksPerBank
decl_stmt|;
DECL|field|maxEntries
specifier|private
specifier|final
name|int
name|maxEntries
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|Metrics
name|metrics
decl_stmt|;
DECL|field|onReleases
specifier|private
specifier|final
name|List
argument_list|<
name|OnRelease
argument_list|>
name|onReleases
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|interface|OnRelease
specifier|public
specifier|static
interface|interface
name|OnRelease
block|{
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|)
function_decl|;
block|}
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|)
block|{
name|this
argument_list|(
name|metrics
argument_list|,
name|directAllocation
argument_list|,
name|totalMemory
argument_list|,
name|_128M
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|,
name|int
name|slabSize
parameter_list|)
block|{
name|this
argument_list|(
name|metrics
argument_list|,
name|directAllocation
argument_list|,
name|totalMemory
argument_list|,
name|slabSize
argument_list|,
name|_32K
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockCache
specifier|public
name|BlockCache
parameter_list|(
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|long
name|totalMemory
parameter_list|,
name|int
name|slabSize
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|numberOfBlocksPerBank
operator|=
name|slabSize
operator|/
name|blockSize
expr_stmt|;
name|int
name|numberOfBanks
init|=
call|(
name|int
call|)
argument_list|(
name|totalMemory
operator|/
name|slabSize
argument_list|)
decl_stmt|;
name|banks
operator|=
operator|new
name|ByteBuffer
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|locks
operator|=
operator|new
name|BlockLocks
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|lockCounters
operator|=
operator|new
name|AtomicInteger
index|[
name|numberOfBanks
index|]
expr_stmt|;
name|maxEntries
operator|=
operator|(
name|numberOfBlocksPerBank
operator|*
name|numberOfBanks
operator|)
operator|-
literal|1
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
name|numberOfBanks
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|directAllocation
condition|)
block|{
name|banks
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|numberOfBlocksPerBank
operator|*
name|blockSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|banks
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|numberOfBlocksPerBank
operator|*
name|blockSize
argument_list|)
expr_stmt|;
block|}
name|locks
index|[
name|i
index|]
operator|=
operator|new
name|BlockLocks
argument_list|(
name|numberOfBlocksPerBank
argument_list|)
expr_stmt|;
name|lockCounters
index|[
name|i
index|]
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
block|}
name|RemovalListener
argument_list|<
name|BlockCacheKey
argument_list|,
name|BlockCacheLocation
argument_list|>
name|listener
init|=
parameter_list|(
name|blockCacheKey
parameter_list|,
name|blockCacheLocation
parameter_list|,
name|removalCause
parameter_list|)
lambda|->
name|releaseLocation
argument_list|(
name|blockCacheKey
argument_list|,
name|blockCacheLocation
argument_list|,
name|removalCause
argument_list|)
decl_stmt|;
name|cache
operator|=
name|Caffeine
operator|.
name|newBuilder
argument_list|()
operator|.
name|removalListener
argument_list|(
name|listener
argument_list|)
operator|.
name|maximumSize
argument_list|(
name|maxEntries
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|BlockCacheKey
name|key
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|releaseLocation
specifier|private
name|void
name|releaseLocation
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|BlockCacheLocation
name|location
parameter_list|,
name|RemovalCause
name|removalCause
parameter_list|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|block
init|=
name|location
operator|.
name|getBlock
argument_list|()
decl_stmt|;
comment|// mark the block removed before we release the lock to allow it to be reused
name|location
operator|.
name|setRemoved
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|locks
index|[
name|bankId
index|]
operator|.
name|clear
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|lockCounters
index|[
name|bankId
index|]
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
for|for
control|(
name|OnRelease
name|onRelease
range|:
name|onReleases
control|)
block|{
name|onRelease
operator|.
name|release
argument_list|(
name|blockCacheKey
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|removalCause
operator|.
name|wasEvicted
argument_list|()
condition|)
block|{
name|metrics
operator|.
name|blockCacheEviction
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|metrics
operator|.
name|blockCacheSize
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * This is only best-effort... it's possible for false to be returned, meaning the block was not able to be cached.    * NOTE: blocks may not currently be updated (false will be returned if the block is already cached)    * The blockCacheKey is cloned before it is inserted into the map, so it may be reused by clients if desired.    *    * @param blockCacheKey the key for the block    * @param blockOffset the offset within the block    * @param data source data to write to the block    * @param offset offset within the source data array    * @param length the number of bytes to write.    * @return true if the block was cached/updated    */
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|+
name|blockOffset
operator|>
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer size exceeded, expecting max ["
operator|+
name|blockSize
operator|+
literal|"] got length ["
operator|+
name|length
operator|+
literal|"] with blockOffset ["
operator|+
name|blockOffset
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|BlockCacheLocation
name|location
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|blockCacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|location
operator|=
operator|new
name|BlockCacheLocation
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|findEmptyLocation
argument_list|(
name|location
argument_list|)
condition|)
block|{
comment|// YCS: it looks like when the cache is full (a normal scenario), then two concurrent writes will result in one of them failing
comment|// because no eviction is done first.  The code seems to rely on leaving just a single block empty.
comment|// TODO: simplest fix would be to leave more than one block empty
name|metrics
operator|.
name|blockCacheStoreFail
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// If we allocated a new block, then it has never been published and is thus never in danger of being concurrently removed.
comment|// On the other hand, if this is an existing block we are updating, it may concurrently be removed and reused for another
comment|// purpose (and then our write may overwrite that).  This can happen even if clients never try to update existing blocks,
comment|// since two clients can try to cache the same block concurrently.  Because of this, the ability to update an existing
comment|// block has been removed for the time being (see SOLR-10121).
comment|// No metrics to update: we don't count a redundant store as a store fail.
return|return
literal|false
return|;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|bankOffset
init|=
name|location
operator|.
name|getBlock
argument_list|()
operator|*
name|blockSize
decl_stmt|;
name|ByteBuffer
name|bank
init|=
name|getBank
argument_list|(
name|bankId
argument_list|)
decl_stmt|;
name|bank
operator|.
name|position
argument_list|(
name|bankOffset
operator|+
name|blockOffset
argument_list|)
expr_stmt|;
name|bank
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// make sure all modifications to the block have been completed before we publish it.
name|cache
operator|.
name|put
argument_list|(
name|blockCacheKey
operator|.
name|clone
argument_list|()
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|blockCacheSize
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * @param blockCacheKey the key for the block    * @param buffer the target buffer for the read result    * @param blockOffset offset within the block    * @param off offset within the target buffer    * @param length the number of bytes to read    * @return true if the block was cached and the bytes were read    */
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|BlockCacheLocation
name|location
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|blockCacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|metrics
operator|.
name|blockCacheMiss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|int
name|bankId
init|=
name|location
operator|.
name|getBankId
argument_list|()
decl_stmt|;
name|int
name|bankOffset
init|=
name|location
operator|.
name|getBlock
argument_list|()
operator|*
name|blockSize
decl_stmt|;
name|location
operator|.
name|touch
argument_list|()
expr_stmt|;
name|ByteBuffer
name|bank
init|=
name|getBank
argument_list|(
name|bankId
argument_list|)
decl_stmt|;
name|bank
operator|.
name|position
argument_list|(
name|bankOffset
operator|+
name|blockOffset
argument_list|)
expr_stmt|;
name|bank
operator|.
name|get
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|location
operator|.
name|isRemoved
argument_list|()
condition|)
block|{
comment|// must check *after* the read is done since the bank may have been reused for another block
comment|// before or during the read.
name|metrics
operator|.
name|blockCacheMiss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|metrics
operator|.
name|blockCacheHit
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|BlockCacheKey
name|blockCacheKey
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|checkLength
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
DECL|method|findEmptyLocation
specifier|private
name|boolean
name|findEmptyLocation
parameter_list|(
name|BlockCacheLocation
name|location
parameter_list|)
block|{
comment|// This is a tight loop that will try and find a location to
comment|// place the block before giving up
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|OUTER
label|:
for|for
control|(
name|int
name|bankId
init|=
literal|0
init|;
name|bankId
operator|<
name|banks
operator|.
name|length
condition|;
name|bankId
operator|++
control|)
block|{
name|AtomicInteger
name|bitSetCounter
init|=
name|lockCounters
index|[
name|bankId
index|]
decl_stmt|;
name|BlockLocks
name|bitSet
init|=
name|locks
index|[
name|bankId
index|]
decl_stmt|;
if|if
condition|(
name|bitSetCounter
operator|.
name|get
argument_list|()
operator|==
name|numberOfBlocksPerBank
condition|)
block|{
comment|// if bitset is full
continue|continue
name|OUTER
continue|;
block|}
comment|// this check needs to spin, if a lock was attempted but not obtained
comment|// the rest of the bank should not be skipped
name|int
name|bit
init|=
name|bitSet
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|INNER
label|:
while|while
condition|(
name|bit
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bit
operator|>=
name|numberOfBlocksPerBank
condition|)
block|{
comment|// bit set is full
continue|continue
name|OUTER
continue|;
block|}
if|if
condition|(
operator|!
name|bitSet
operator|.
name|set
argument_list|(
name|bit
argument_list|)
condition|)
block|{
comment|// lock was not obtained
comment|// this restarts at 0 because another block could have been unlocked
comment|// while this was executing
name|bit
operator|=
name|bitSet
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
continue|continue
name|INNER
continue|;
block|}
else|else
block|{
comment|// lock obtained
name|location
operator|.
name|setBankId
argument_list|(
name|bankId
argument_list|)
expr_stmt|;
name|location
operator|.
name|setBlock
argument_list|(
name|bit
argument_list|)
expr_stmt|;
name|bitSetCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|checkLength
specifier|private
name|void
name|checkLength
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
operator|!=
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer wrong size, expecting ["
operator|+
name|blockSize
operator|+
literal|"] got ["
operator|+
name|buffer
operator|.
name|length
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/** Returns a new copy of the ByteBuffer for the given bank, so it's safe to call position() on w/o additional synchronization */
DECL|method|getBank
specifier|private
name|ByteBuffer
name|getBank
parameter_list|(
name|int
name|bankId
parameter_list|)
block|{
return|return
name|banks
index|[
name|bankId
index|]
operator|.
name|duplicate
argument_list|()
return|;
block|}
comment|/** returns the number of elements in the cache */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|cache
operator|.
name|asMap
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|setOnRelease
name|void
name|setOnRelease
parameter_list|(
name|OnRelease
name|onRelease
parameter_list|)
block|{
name|this
operator|.
name|onReleases
operator|.
name|add
argument_list|(
name|onRelease
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

