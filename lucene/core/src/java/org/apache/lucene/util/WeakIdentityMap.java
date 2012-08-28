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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|ReferenceQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Implements a combination of {@link java.util.WeakHashMap} and  * {@link java.util.IdentityHashMap}.  * Useful for caches that need to key off of a {@code ==} comparison  * instead of a {@code .equals}.  *   *<p>This class is not a general-purpose {@link java.util.Map}  * implementation! It intentionally violates  * Map's general contract, which mandates the use of the equals method  * when comparing objects. This class is designed for use only in the  * rare cases wherein reference-equality semantics are required.  *   *<p>This implementation was forked from<a href="http://cxf.apache.org/">Apache CXF</a>  * but modified to<b>not</b> implement the {@link java.util.Map} interface and  * without any set views on it, as those are error-prone and inefficient,  * if not implemented carefully. The map only contains {@link Iterator} implementations  * on the values and not-GCed keys. Lucene's implementation also supports {@code null}  * keys, but those are never weak!  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|WeakIdentityMap
specifier|public
specifier|final
class|class
name|WeakIdentityMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|queue
specifier|private
specifier|final
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
name|queue
init|=
operator|new
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|backingStore
specifier|private
specifier|final
name|Map
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
name|backingStore
decl_stmt|;
comment|/** Creates a new {@code WeakIdentityMap} based on a non-synchronized {@link HashMap}. */
DECL|method|newHashMap
specifier|public
specifier|static
specifier|final
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newHashMap
parameter_list|()
block|{
return|return
operator|new
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
operator|new
name|HashMap
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/** Creates a new {@code WeakIdentityMap} based on a {@link ConcurrentHashMap}. */
DECL|method|newConcurrentHashMap
specifier|public
specifier|static
specifier|final
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newConcurrentHashMap
parameter_list|()
block|{
return|return
operator|new
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|WeakIdentityMap
specifier|private
name|WeakIdentityMap
parameter_list|(
name|Map
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
name|backingStore
parameter_list|)
block|{
name|this
operator|.
name|backingStore
operator|=
name|backingStore
expr_stmt|;
block|}
comment|/** Removes all of the mappings from this map. */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|backingStore
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns {@code true} if this map contains a mapping for the specified key. */
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|containsKey
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the value to which the specified key is mapped. */
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|get
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/** Associates the specified value with the specified key in this map.    * If the map previously contained a mapping for this key, the old value    * is replaced. */
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
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|put
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
name|queue
argument_list|)
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** Returns {@code true} if this map contains no key-value mappings. */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/** Removes the mapping for a key from this weak hash map if it is present.    * Returns the value to which this map previously associated the key,    * or {@code null} if the map contained no mapping for the key.    * A return value of {@code null} does not necessarily indicate that    * the map contained.*/
DECL|method|remove
specifier|public
name|V
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|remove
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the number of key-value mappings in this map. This result is a snapshot,    * and may not reflect unprocessed entries that will be removed before next    * attempted access because they are no longer referenced.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|backingStore
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|0
return|;
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Returns an iterator over all weak keys of this map.    * Keys already garbage collected will not be returned.    * This Iterator does not support removals. */
DECL|method|keyIterator
specifier|public
name|Iterator
argument_list|<
name|K
argument_list|>
name|keyIterator
parameter_list|()
block|{
name|reap
argument_list|()
expr_stmt|;
specifier|final
name|Iterator
argument_list|<
name|IdentityWeakReference
argument_list|>
name|iterator
init|=
name|backingStore
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// IMPORTANT: Don't use oal.util.FilterIterator here:
comment|// We need *strong* reference to current key after setNext()!!!
return|return
operator|new
name|Iterator
argument_list|<
name|K
argument_list|>
argument_list|()
block|{
comment|// holds strong reference to next element in backing iterator:
specifier|private
name|Object
name|next
init|=
literal|null
decl_stmt|;
comment|// the backing iterator was already consumed:
specifier|private
name|boolean
name|nextIsSet
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
condition|?
literal|true
else|:
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|K
name|next
parameter_list|()
block|{
if|if
condition|(
name|nextIsSet
operator|||
name|setNext
argument_list|()
condition|)
block|{
try|try
block|{
assert|assert
name|nextIsSet
assert|;
return|return
operator|(
name|K
operator|)
name|next
return|;
block|}
finally|finally
block|{
comment|// release strong reference and invalidate current value:
name|nextIsSet
operator|=
literal|false
expr_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
assert|assert
operator|!
name|nextIsSet
assert|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|next
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
comment|// already garbage collected!
continue|continue;
block|}
comment|// unfold "null" special value
if|if
condition|(
name|next
operator|==
name|NULL
condition|)
block|{
name|next
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|nextIsSet
operator|=
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
comment|/** Returns an iterator over all values of this map.    * This iterator may return values whose key is already    * garbage collected while iterator is consumed. */
DECL|method|valueIterator
specifier|public
name|Iterator
argument_list|<
name|V
argument_list|>
name|valueIterator
parameter_list|()
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|reap
specifier|private
name|void
name|reap
parameter_list|()
block|{
name|Reference
argument_list|<
name|?
argument_list|>
name|zombie
decl_stmt|;
while|while
condition|(
operator|(
name|zombie
operator|=
name|queue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|backingStore
operator|.
name|remove
argument_list|(
name|zombie
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we keep a hard reference to our NULL key, so map supports null keys that never get GCed:
DECL|field|NULL
specifier|static
specifier|final
name|Object
name|NULL
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|class|IdentityWeakReference
specifier|private
specifier|static
specifier|final
class|class
name|IdentityWeakReference
extends|extends
name|WeakReference
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|hash
specifier|private
specifier|final
name|int
name|hash
decl_stmt|;
DECL|method|IdentityWeakReference
name|IdentityWeakReference
parameter_list|(
name|Object
name|obj
parameter_list|,
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
name|queue
parameter_list|)
block|{
name|super
argument_list|(
name|obj
operator|==
literal|null
condition|?
name|NULL
else|:
name|obj
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|hash
operator|=
name|System
operator|.
name|identityHashCode
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|IdentityWeakReference
condition|)
block|{
specifier|final
name|IdentityWeakReference
name|ref
init|=
operator|(
name|IdentityWeakReference
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|get
argument_list|()
operator|==
name|ref
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

