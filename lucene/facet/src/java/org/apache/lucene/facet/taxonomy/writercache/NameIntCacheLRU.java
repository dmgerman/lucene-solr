begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
package|;
end_package

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
name|LinkedHashMap
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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An an LRU cache of mapping from name to int.  * Used to cache Ordinals of category paths.  *   * @lucene.experimental  */
end_comment

begin_comment
comment|// Note: Nothing in this class is synchronized. The caller is assumed to be
end_comment

begin_comment
comment|// synchronized so that no two methods of this class are called concurrently.
end_comment

begin_class
DECL|class|NameIntCacheLRU
class|class
name|NameIntCacheLRU
block|{
DECL|field|cache
specifier|private
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Integer
argument_list|>
name|cache
decl_stmt|;
DECL|field|nMisses
name|long
name|nMisses
init|=
literal|0
decl_stmt|;
comment|// for debug
DECL|field|nHits
name|long
name|nHits
init|=
literal|0
decl_stmt|;
comment|// for debug
DECL|field|maxCacheSize
specifier|private
name|int
name|maxCacheSize
decl_stmt|;
DECL|method|NameIntCacheLRU
name|NameIntCacheLRU
parameter_list|(
name|int
name|maxCacheSize
parameter_list|)
block|{
name|this
operator|.
name|maxCacheSize
operator|=
name|maxCacheSize
expr_stmt|;
name|createCache
argument_list|(
name|maxCacheSize
argument_list|)
expr_stmt|;
block|}
comment|/** Maximum number of cache entries before eviction. */
DECL|method|getMaxSize
specifier|public
name|int
name|getMaxSize
parameter_list|()
block|{
return|return
name|maxCacheSize
return|;
block|}
comment|/** Number of entries currently in the cache. */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|createCache
specifier|private
name|void
name|createCache
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
if|if
condition|(
name|maxSize
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|cache
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|1000
argument_list|,
operator|(
name|float
operator|)
literal|0.7
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//for LRU
block|}
else|else
block|{
name|cache
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1000
argument_list|,
operator|(
name|float
operator|)
literal|0.7
argument_list|)
expr_stmt|;
comment|//no need for LRU
block|}
block|}
DECL|method|get
name|Integer
name|get
parameter_list|(
name|FacetLabel
name|name
parameter_list|)
block|{
name|Integer
name|res
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|nMisses
operator|++
expr_stmt|;
block|}
else|else
block|{
name|nHits
operator|++
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/** Subclasses can override this to provide caching by e.g. hash of the string. */
DECL|method|key
name|Object
name|key
parameter_list|(
name|FacetLabel
name|name
parameter_list|)
block|{
return|return
name|name
return|;
block|}
DECL|method|key
name|Object
name|key
parameter_list|(
name|FacetLabel
name|name
parameter_list|,
name|int
name|prefixLen
parameter_list|)
block|{
return|return
name|name
operator|.
name|subpath
argument_list|(
name|prefixLen
argument_list|)
return|;
block|}
comment|/**    * Add a new value to cache.    * Return true if cache became full and some room need to be made.     */
DECL|method|put
name|boolean
name|put
parameter_list|(
name|FacetLabel
name|name
parameter_list|,
name|Integer
name|val
parameter_list|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|(
name|name
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|isCacheFull
argument_list|()
return|;
block|}
DECL|method|put
name|boolean
name|put
parameter_list|(
name|FacetLabel
name|name
parameter_list|,
name|int
name|prefixLen
parameter_list|,
name|Integer
name|val
parameter_list|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|(
name|name
argument_list|,
name|prefixLen
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|isCacheFull
argument_list|()
return|;
block|}
DECL|method|isCacheFull
specifier|private
name|boolean
name|isCacheFull
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
operator|>
name|maxCacheSize
return|;
block|}
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|stats
name|String
name|stats
parameter_list|()
block|{
return|return
literal|"#miss="
operator|+
name|nMisses
operator|+
literal|" #hit="
operator|+
name|nHits
return|;
block|}
comment|/**    * If cache is full remove least recently used entries from cache. Return true    * if anything was removed, false otherwise.    *     * See comment in DirectoryTaxonomyWriter.addToCache(CategoryPath, int) for an    * explanation why we clean 2/3rds of the cache, and not just one entry.    */
DECL|method|makeRoomLRU
name|boolean
name|makeRoomLRU
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isCacheFull
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|n
init|=
name|cache
operator|.
name|size
argument_list|()
operator|-
operator|(
literal|2
operator|*
name|maxCacheSize
operator|)
operator|/
literal|3
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Iterator
argument_list|<
name|Object
argument_list|>
name|it
init|=
name|cache
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|n
operator|&&
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

