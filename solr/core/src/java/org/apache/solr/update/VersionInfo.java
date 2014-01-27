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
name|Map
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|BitUtil
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
name|BytesRef
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|SolrIndexSearcher
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
name|RefCounted
import|;
end_import

begin_class
DECL|class|VersionInfo
specifier|public
class|class
name|VersionInfo
block|{
DECL|field|VERSION_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|VERSION_FIELD
init|=
literal|"_version_"
decl_stmt|;
DECL|field|ulog
specifier|private
specifier|final
name|UpdateLog
name|ulog
decl_stmt|;
DECL|field|buckets
specifier|private
specifier|final
name|VersionBucket
index|[]
name|buckets
decl_stmt|;
DECL|field|versionField
specifier|private
name|SchemaField
name|versionField
decl_stmt|;
DECL|field|idField
specifier|private
name|SchemaField
name|idField
decl_stmt|;
DECL|field|lock
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Gets and returns the {@link #VERSION_FIELD} from the specified     * schema, after verifying that it is indexed, stored, and single-valued.      * If any of these pre-conditions are not met, it throws a SolrException     * with a user suitable message indicating the problem.    */
DECL|method|getAndCheckVersionField
specifier|public
specifier|static
name|SchemaField
name|getAndCheckVersionField
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|SolrException
block|{
specifier|final
name|String
name|errPrefix
init|=
name|VERSION_FIELD
operator|+
literal|" field must exist in schema, using indexed=\"true\" or docValues=\"true\", stored=\"true\" and multiValued=\"false\""
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|VERSION_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|sf
condition|)
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
name|errPrefix
operator|+
literal|" ("
operator|+
name|VERSION_FIELD
operator|+
literal|" does not exist)"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|sf
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|sf
operator|.
name|hasDocValues
argument_list|()
condition|)
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
name|errPrefix
operator|+
literal|" ("
operator|+
name|VERSION_FIELD
operator|+
literal|" must be either indexed or have docValues"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|sf
operator|.
name|stored
argument_list|()
condition|)
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
name|errPrefix
operator|+
literal|" ("
operator|+
name|VERSION_FIELD
operator|+
literal|" is not stored"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
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
name|errPrefix
operator|+
literal|" ("
operator|+
name|VERSION_FIELD
operator|+
literal|" is multiValued"
argument_list|)
throw|;
block|}
return|return
name|sf
return|;
block|}
DECL|method|VersionInfo
specifier|public
name|VersionInfo
parameter_list|(
name|UpdateLog
name|ulog
parameter_list|,
name|int
name|nBuckets
parameter_list|)
block|{
name|this
operator|.
name|ulog
operator|=
name|ulog
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|ulog
operator|.
name|uhandler
operator|.
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|versionField
operator|=
name|getAndCheckVersionField
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|idField
operator|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|buckets
operator|=
operator|new
name|VersionBucket
index|[
name|BitUtil
operator|.
name|nextHighestPowerOfTwo
argument_list|(
name|nBuckets
argument_list|)
index|]
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
name|buckets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buckets
index|[
name|i
index|]
operator|=
operator|new
name|VersionBucket
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|()
block|{    }
DECL|method|getVersionField
specifier|public
name|SchemaField
name|getVersionField
parameter_list|()
block|{
return|return
name|versionField
return|;
block|}
DECL|method|lockForUpdate
specifier|public
name|void
name|lockForUpdate
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
DECL|method|unlockForUpdate
specifier|public
name|void
name|unlockForUpdate
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
DECL|method|blockUpdates
specifier|public
name|void
name|blockUpdates
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
DECL|method|unblockUpdates
specifier|public
name|void
name|unblockUpdates
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|/***   // todo: initialize... use current time to start?   // a clock that increments by 1 for every operation makes it easier to detect missing   // messages, but raises other issues:   // - need to initialize to largest thing in index or tlog   // - when becoming leader, need to make sure it's greater than   // - using to detect missing messages means we need to keep track per-leader, or make   //   sure a new leader starts off with 1 greater than the last leader.   private final AtomicLong clock = new AtomicLong();    public long getNewClock() {     return clock.incrementAndGet();   }    // Named *old* to prevent accidental calling getClock and expecting a new updated clock.   public long getOldClock() {     return clock.get();   }   ***/
comment|/** We are currently using this time-based clock to avoid going back in time on a    * server restart (i.e. we don't want version numbers to start at 1 again).    */
comment|// Time-based lamport clock.  Good for introducing some reality into clocks (to the degree
comment|// that times are somewhat synchronized in the cluster).
comment|// Good if we want to relax some constraints to scale down to where only one node may be
comment|// up at a time.  Possibly harder to detect missing messages (because versions are not contiguous.
DECL|field|vclock
name|long
name|vclock
decl_stmt|;
DECL|field|time
name|long
name|time
decl_stmt|;
DECL|field|clockSync
specifier|private
specifier|final
name|Object
name|clockSync
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|getNewClock
specifier|public
name|long
name|getNewClock
parameter_list|()
block|{
synchronized|synchronized
init|(
name|clockSync
init|)
block|{
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|long
name|result
init|=
name|time
operator|<<
literal|20
decl_stmt|;
if|if
condition|(
name|result
operator|<=
name|vclock
condition|)
block|{
name|result
operator|=
name|vclock
operator|+
literal|1
expr_stmt|;
block|}
name|vclock
operator|=
name|result
expr_stmt|;
return|return
name|vclock
return|;
block|}
block|}
DECL|method|getOldClock
specifier|public
name|long
name|getOldClock
parameter_list|()
block|{
synchronized|synchronized
init|(
name|clockSync
init|)
block|{
return|return
name|vclock
return|;
block|}
block|}
DECL|method|updateClock
specifier|public
name|void
name|updateClock
parameter_list|(
name|long
name|clock
parameter_list|)
block|{
synchronized|synchronized
init|(
name|clockSync
init|)
block|{
name|vclock
operator|=
name|Math
operator|.
name|max
argument_list|(
name|vclock
argument_list|,
name|clock
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bucket
specifier|public
name|VersionBucket
name|bucket
parameter_list|(
name|int
name|hash
parameter_list|)
block|{
comment|// If this is a user provided hash, it may be poor in the right-hand bits.
comment|// Make sure high bits are moved down, since only the low bits will matter.
comment|// int h = hash + (hash>>> 8) + (hash>>> 16) + (hash>>> 24);
comment|// Assume good hash codes for now.
name|int
name|slot
init|=
name|hash
operator|&
operator|(
name|buckets
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
return|return
name|buckets
index|[
name|slot
index|]
return|;
block|}
DECL|method|lookupVersion
specifier|public
name|Long
name|lookupVersion
parameter_list|(
name|BytesRef
name|idBytes
parameter_list|)
block|{
return|return
name|ulog
operator|.
name|lookupVersion
argument_list|(
name|idBytes
argument_list|)
return|;
block|}
DECL|method|getVersionFromIndex
specifier|public
name|Long
name|getVersionFromIndex
parameter_list|(
name|BytesRef
name|idBytes
parameter_list|)
block|{
comment|// TODO: we could cache much of this and invalidate during a commit.
comment|// TODO: most DocValues classes are threadsafe - expose which.
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|newestSearcher
init|=
name|ulog
operator|.
name|uhandler
operator|.
name|core
operator|.
name|getRealtimeSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|newestSearcher
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|lookup
init|=
name|searcher
operator|.
name|lookupId
argument_list|(
name|idBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookup
operator|<
literal|0
condition|)
return|return
literal|null
return|;
name|ValueSource
name|vs
init|=
name|versionField
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|versionField
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
name|context
init|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|vs
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|FunctionValues
name|fv
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
call|(
name|int
call|)
argument_list|(
name|lookup
operator|>>
literal|32
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|ver
init|=
name|fv
operator|.
name|longVal
argument_list|(
operator|(
name|int
operator|)
name|lookup
argument_list|)
decl_stmt|;
return|return
name|ver
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Error reading version from index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|newestSearcher
operator|!=
literal|null
condition|)
block|{
name|newestSearcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

