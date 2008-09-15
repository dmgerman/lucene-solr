begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
package|;
end_package

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
name|lucene
operator|.
name|search
operator|.
name|CachingWrapperFilter
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
name|search
operator|.
name|Filter
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|QueryWrapperFilter
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
name|xmlparser
operator|.
name|DOMUtils
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
name|xmlparser
operator|.
name|FilterBuilder
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
name|xmlparser
operator|.
name|FilterBuilderFactory
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
name|xmlparser
operator|.
name|ParserException
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
name|xmlparser
operator|.
name|QueryBuilder
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
name|xmlparser
operator|.
name|QueryBuilderFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Filters are cached in an LRU Cache keyed on the contained query or filter object. Using this will   * speed up overall performance for repeated uses of the same expensive query/filter. The sorts of   * queries/filters likely to benefit from caching need not necessarily be complex - e.g. simple   * TermQuerys with a large DF (document frequency) can be expensive	on large indexes.   * A good example of this might be a term query on a field with only 2 possible	values -   * "true" or "false". In a large index, querying or filtering on this field requires reading   * millions	of document ids from disk which can more usefully be cached as a filter bitset.  *   * For Queries/Filters to be cached and reused the object must implement hashcode and  * equals methods correctly so that duplicate queries/filters can be detected in the cache.  *   * The CoreParser.maxNumCachedFilters property can be used to control the size of the LRU   * Cache established during the construction of CoreParser instances.  *  */
end_comment

begin_class
DECL|class|CachedFilterBuilder
specifier|public
class|class
name|CachedFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|field|queryFactory
specifier|private
name|QueryBuilderFactory
name|queryFactory
decl_stmt|;
DECL|field|filterFactory
specifier|private
name|FilterBuilderFactory
name|filterFactory
decl_stmt|;
DECL|field|filterCache
specifier|private
name|LRUCache
name|filterCache
init|=
literal|null
decl_stmt|;
DECL|field|cacheSize
specifier|private
name|int
name|cacheSize
decl_stmt|;
DECL|method|CachedFilterBuilder
specifier|public
name|CachedFilterBuilder
parameter_list|(
name|QueryBuilderFactory
name|queryFactory
parameter_list|,
name|FilterBuilderFactory
name|filterFactory
parameter_list|,
name|int
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|queryFactory
operator|=
name|queryFactory
expr_stmt|;
name|this
operator|.
name|filterFactory
operator|=
name|filterFactory
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
block|}
DECL|method|getFilter
specifier|public
specifier|synchronized
name|Filter
name|getFilter
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|childElement
init|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterCache
operator|==
literal|null
condition|)
block|{
name|filterCache
operator|=
operator|new
name|LRUCache
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
comment|// Test to see if child Element is a query or filter that needs to be
comment|// cached
name|QueryBuilder
name|qb
init|=
name|queryFactory
operator|.
name|getQueryBuilder
argument_list|(
name|childElement
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|cacheKey
init|=
literal|null
decl_stmt|;
name|Query
name|q
init|=
literal|null
decl_stmt|;
name|Filter
name|f
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|qb
operator|!=
literal|null
condition|)
block|{
name|q
operator|=
name|qb
operator|.
name|getQuery
argument_list|(
name|childElement
argument_list|)
expr_stmt|;
name|cacheKey
operator|=
name|q
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
name|filterFactory
operator|.
name|getFilter
argument_list|(
name|childElement
argument_list|)
expr_stmt|;
name|cacheKey
operator|=
name|f
expr_stmt|;
block|}
name|Filter
name|cachedFilter
init|=
operator|(
name|Filter
operator|)
name|filterCache
operator|.
name|get
argument_list|(
name|cacheKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedFilter
operator|!=
literal|null
condition|)
block|{
return|return
name|cachedFilter
return|;
comment|// cache hit
block|}
comment|//cache miss
if|if
condition|(
name|qb
operator|!=
literal|null
condition|)
block|{
name|cachedFilter
operator|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cachedFilter
operator|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|filterCache
operator|.
name|put
argument_list|(
name|cacheKey
argument_list|,
name|cachedFilter
argument_list|)
expr_stmt|;
return|return
name|cachedFilter
return|;
block|}
DECL|class|LRUCache
specifier|static
class|class
name|LRUCache
extends|extends
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
block|{
DECL|method|LRUCache
specifier|public
name|LRUCache
parameter_list|(
name|int
name|maxsize
parameter_list|)
block|{
name|super
argument_list|(
name|maxsize
operator|*
literal|4
operator|/
literal|3
operator|+
literal|1
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxsize
operator|=
name|maxsize
expr_stmt|;
block|}
DECL|field|maxsize
specifier|protected
name|int
name|maxsize
decl_stmt|;
DECL|method|removeEldestEntry
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Entry
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|maxsize
return|;
block|}
block|}
block|}
end_class

end_unit

