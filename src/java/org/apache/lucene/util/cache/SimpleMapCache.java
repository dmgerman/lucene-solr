begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|cache
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  * Simple cache implementation that uses a HashMap to store (key, value) pairs.  * This cache is not synchronized, use {@link Cache#synchronizedCache(Cache)}  * if needed.  *  * @deprecated Lucene's internal use of this class has now  * switched to {@link DoubleBarrelLRUCache}.  */
end_comment

begin_class
DECL|class|SimpleMapCache
specifier|public
class|class
name|SimpleMapCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|map
specifier|protected
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
decl_stmt|;
DECL|method|SimpleMapCache
specifier|public
name|SimpleMapCache
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleMapCache
specifier|public
name|SimpleMapCache
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
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
block|{
comment|// NOOP
block|}
annotation|@
name|Override
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns a Set containing all keys in this cache.    */
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSynchronizedCache
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getSynchronizedCache
parameter_list|()
block|{
return|return
operator|new
name|SynchronizedSimpleMapCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|SynchronizedSimpleMapCache
specifier|private
specifier|static
class|class
name|SynchronizedSimpleMapCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|SimpleMapCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|mutex
specifier|private
name|Object
name|mutex
decl_stmt|;
DECL|field|cache
specifier|private
name|SimpleMapCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
decl_stmt|;
DECL|method|SynchronizedSimpleMapCache
name|SynchronizedSimpleMapCache
parameter_list|(
name|SimpleMapCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|mutex
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|void
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
name|mutex
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|cache
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|keySet
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|cache
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSynchronizedCache
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getSynchronizedCache
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

