begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_comment
comment|/**  * A policy defining which filters should be cached.  *  * Implementations of this class must be thread-safe.  *  * @see UsageTrackingQueryCachingPolicy  * @see LRUQueryCache  * @lucene.experimental  */
end_comment

begin_comment
comment|// TODO: add APIs for integration with IndexWriter.IndexReaderWarmer
end_comment

begin_interface
DECL|interface|QueryCachingPolicy
specifier|public
interface|interface
name|QueryCachingPolicy
block|{
comment|/** A simple policy that caches all the provided filters on all segments. */
DECL|field|ALWAYS_CACHE
specifier|public
specifier|static
specifier|final
name|QueryCachingPolicy
name|ALWAYS_CACHE
init|=
operator|new
name|QueryCachingPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onUse
parameter_list|(
name|Query
name|query
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Query
name|query
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/** Callback that is called every time that a cached filter is used.    *  This is typically useful if the policy wants to track usage statistics    *  in order to make decisions. */
DECL|method|onUse
name|void
name|onUse
parameter_list|(
name|Query
name|query
parameter_list|)
function_decl|;
comment|/** Whether the given {@link DocIdSet} should be cached on a given segment.    *  This method will be called on each leaf context to know if the filter    *  should be cached on this particular leaf. The filter cache will first    *  attempt to load a {@link DocIdSet} from the cache. If it is not cached    *  yet and this method returns<tt>true</tt> then a cache entry will be    *  generated. Otherwise an uncached set will be returned. */
DECL|method|shouldCache
name|boolean
name|shouldCache
parameter_list|(
name|Query
name|query
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

