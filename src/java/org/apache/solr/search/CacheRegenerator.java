begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Implementations of<code>CacheRegenerator</code> are used in autowarming to populate a new cache  * based on an old cache.<code>regenerateItem</code> is called for each item that should be inserted into the new cache.  *<p>  * Implementations should have a noarg constructor and be thread safe (a single instance will be  * used for all cache autowarmings).  *  * @author yonik  * @version $Id$  */
end_comment

begin_interface
DECL|interface|CacheRegenerator
specifier|public
interface|interface
name|CacheRegenerator
block|{
comment|/**    * Regenerate an old cache item and insert it into<code>newCache</code>    *    * @param newSearcher the new searcher who's caches are being autowarmed    * @param newCache    where regenerated cache items should be stored. the target of the autowarming    * @param oldCache    the old cache being used as a source for autowarming    * @param oldKey      the key of the old cache item to regenerate in the new cache    * @param oldVal      the old value of the cache item    * @return true to continue with autowarming, false to stop    */
DECL|method|regenerateItem
specifier|public
name|boolean
name|regenerateItem
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrCache
name|newCache
parameter_list|,
name|SolrCache
name|oldCache
parameter_list|,
name|Object
name|oldKey
parameter_list|,
name|Object
name|oldVal
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

