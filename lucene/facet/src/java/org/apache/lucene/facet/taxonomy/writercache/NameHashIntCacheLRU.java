begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * An an LRU cache of mapping from name to int.  * Used to cache Ordinals of category paths.  * It uses as key, hash of the path instead of the path.  * This way the cache takes less RAM, but correctness depends on  * assuming no collisions.   *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NameHashIntCacheLRU
specifier|public
class|class
name|NameHashIntCacheLRU
extends|extends
name|NameIntCacheLRU
block|{
DECL|method|NameHashIntCacheLRU
name|NameHashIntCacheLRU
parameter_list|(
name|int
name|maxCacheSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxCacheSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|key
name|Object
name|key
parameter_list|(
name|FacetLabel
name|name
parameter_list|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|name
operator|.
name|longHashCode
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
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
operator|new
name|Long
argument_list|(
name|name
operator|.
name|subpath
argument_list|(
name|prefixLen
argument_list|)
operator|.
name|longHashCode
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

