begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Simple concurrent LRU cache, using a "double barrel"  * approach where two ConcurrentHashMaps record entries.  *  *<p>At any given time, one hash is primary and the other  * is secondary.  {@link #get} first checks primary, and if  * that's a miss, checks secondary.  If secondary has the  * entry, it's promoted to primary (<b>NOTE</b>: the key is  * cloned at this point).  Once primary is full, the  * secondary is cleared and the two are swapped.</p>  *  *<p>This is not as space efficient as other possible  * concurrent approaches (see LUCENE-2075): to achieve  * perfect LRU(N) it requires 2*N storage.  But, this  * approach is relatively simple and seems in practice to  * not grow unbounded in size when under hideously high  * load.</p>  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|DoubleBarrelLRUCache
specifier|final
specifier|public
class|class
name|DoubleBarrelLRUCache
parameter_list|<
name|K
extends|extends
name|DoubleBarrelLRUCache
operator|.
name|CloneableKey
parameter_list|,
name|V
parameter_list|>
block|{
comment|/** Object providing clone(); the key class must subclass this. */
DECL|class|CloneableKey
specifier|public
specifier|static
specifier|abstract
class|class
name|CloneableKey
block|{
annotation|@
name|Override
DECL|method|clone
specifier|abstract
specifier|public
name|Object
name|clone
parameter_list|()
function_decl|;
block|}
DECL|field|cache1
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache1
decl_stmt|;
DECL|field|cache2
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache2
decl_stmt|;
DECL|field|countdown
specifier|private
specifier|final
name|AtomicInteger
name|countdown
decl_stmt|;
DECL|field|swapped
specifier|private
specifier|volatile
name|boolean
name|swapped
decl_stmt|;
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|method|DoubleBarrelLRUCache
specifier|public
name|DoubleBarrelLRUCache
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|countdown
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|cache1
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
expr_stmt|;
name|cache2
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|primary
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|secondary
decl_stmt|;
if|if
condition|(
name|swapped
condition|)
block|{
name|primary
operator|=
name|cache2
expr_stmt|;
name|secondary
operator|=
name|cache1
expr_stmt|;
block|}
else|else
block|{
name|primary
operator|=
name|cache1
expr_stmt|;
name|secondary
operator|=
name|cache2
expr_stmt|;
block|}
comment|// Try primary first
name|V
name|result
init|=
name|primary
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// Not found -- try secondary
name|result
operator|=
name|secondary
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// Promote to primary
name|put
argument_list|(
operator|(
name|K
operator|)
name|key
operator|.
name|clone
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
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
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|primary
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|secondary
decl_stmt|;
if|if
condition|(
name|swapped
condition|)
block|{
name|primary
operator|=
name|cache2
expr_stmt|;
name|secondary
operator|=
name|cache1
expr_stmt|;
block|}
else|else
block|{
name|primary
operator|=
name|cache1
expr_stmt|;
name|secondary
operator|=
name|cache2
expr_stmt|;
block|}
name|primary
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|countdown
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Time to swap
comment|// NOTE: there is saturation risk here, that the
comment|// thread that's doing the clear() takes too long to
comment|// do so, while other threads continue to add to
comment|// primary, but in practice this seems not to be an
comment|// issue (see LUCENE-2075 for benchmark& details)
comment|// First, clear secondary
name|secondary
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Second, swap
name|swapped
operator|=
operator|!
name|swapped
expr_stmt|;
comment|// Third, reset countdown
name|countdown
operator|.
name|set
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

