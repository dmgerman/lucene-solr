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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Primary API for dealing with Solr's internal caches.  */
end_comment

begin_interface
DECL|interface|SolrCache
specifier|public
interface|interface
name|SolrCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|SolrInfoMBean
block|{
comment|/**    * The initialization routine. Instance specific arguments are passed in    * the<code>args</code> map.    *<p>    * The persistence object will exist across different lifetimes of similar caches.    * For example, all filter caches will share the same persistence object, sometimes    * at the same time (it must be thread-safe).  If null is passed, then the cache    * implementation should create and return a new persistence object.  If not null,    * the passed in object should be returned again.    *<p>    * Since it will exist across the lifetime of many caches, care should be taken to    * not reference any particular cache instance and prevent it from being    * garbage collected (no using inner classes unless they are static).    *<p>    * The persistence object is designed to be used as a way for statistics    * to accumulate across all instances of the same type of cache, however the    * object may be of any type desired by the cache implementation.    *<p>    * The {@link CacheRegenerator} is what the cache uses during auto-warming to    * regenerate an item in the new cache from an entry in the old cache.    *    */
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
function_decl|;
comment|// I don't think we need a factory for faster creation given that these
comment|// will be associated with slow-to-create SolrIndexSearchers.
comment|// change to NamedList when other plugins do?
comment|/**    * Name the Cache can be referenced with by SolrRequestHandlers.    *    * This method must return the identifier that the Cache instance     * expects SolrRequestHandlers to use when requesting access to it     * from the SolrIndexSearcher.  It is<strong>strongly</strong>     * recommended that this method return the value of the "name"     * parameter from the init args.    *    * :TODO: verify this.    */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
function_decl|;
comment|// Should SolrCache just extend the java.util.Map interface?
comment|// Following the conventions of the java.util.Map interface in any case.
comment|/** :TODO: copy from Map */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/** :TODO: copy from Map */
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
function_decl|;
comment|/** :TODO: copy from Map */
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/** :TODO: copy from Map */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**     * Enumeration of possible States for cache instances.    * :TODO: only state that seems to ever be set is LIVE ?   */
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
comment|/** :TODO */
DECL|enum constant|CREATED
name|CREATED
block|,
comment|/** :TODO */
DECL|enum constant|STATICWARMING
name|STATICWARMING
block|,
comment|/** :TODO */
DECL|enum constant|AUTOWARMING
name|AUTOWARMING
block|,
comment|/** :TODO */
DECL|enum constant|LIVE
name|LIVE
block|}
comment|/**    * Set different cache states.    * The state a cache is in can have an effect on how statistics are kept.    * The cache user (SolrIndexSearcher) will take care of switching    * cache states.    */
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|State
name|state
parameter_list|)
function_decl|;
comment|/**    * Returns the last State set on this instance    *    * @see #setState    */
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
function_decl|;
comment|/**    * Warm this cache associated with<code>searcher</code> using the<code>old</code>    * cache object.<code>this</code> and<code>old</code> will have the same concrete type.    */
DECL|method|warm
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
function_decl|;
comment|// Q: an alternative to passing the searcher here would be to pass it in
comment|// init and have the cache implementation save it.
comment|/** Frees any non-memory resources */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

