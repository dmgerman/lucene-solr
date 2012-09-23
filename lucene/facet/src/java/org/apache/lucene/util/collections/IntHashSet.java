begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.collections
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A Set or primitive int. Implemented as a HashMap of int->int. *  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntHashSet
specifier|public
class|class
name|IntHashSet
block|{
comment|// TODO (Facet): This is wasteful as the "values" are actually the "keys" and
comment|// we could spare this amount of space (capacity * sizeof(int)). Perhaps even
comment|// though it is not OOP, we should re-implement the hash for just that cause.
comment|/**    * Implements an IntIterator which iterates over all the allocated indexes.    */
DECL|class|IndexIterator
specifier|private
specifier|final
class|class
name|IndexIterator
implements|implements
name|IntIterator
block|{
comment|/**      * The last used baseHashIndex. Needed for "jumping" from one hash entry      * to another.      */
DECL|field|baseHashIndex
specifier|private
name|int
name|baseHashIndex
init|=
literal|0
decl_stmt|;
comment|/**      * The next not-yet-visited index.      */
DECL|field|index
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|/**      * Index of the last visited pair. Used in {@link #remove()}.      */
DECL|field|lastIndex
specifier|private
name|int
name|lastIndex
init|=
literal|0
decl_stmt|;
comment|/**      * Create the Iterator, make<code>index</code> point to the "first"      * index which is not empty. If such does not exist (eg. the map is      * empty) it would be zero.      */
DECL|method|IndexIterator
specifier|public
name|IndexIterator
parameter_list|()
block|{
for|for
control|(
name|baseHashIndex
operator|=
literal|0
init|;
name|baseHashIndex
operator|<
name|baseHash
operator|.
name|length
condition|;
operator|++
name|baseHashIndex
control|)
block|{
name|index
operator|=
name|baseHash
index|[
name|baseHashIndex
index|]
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|index
operator|!=
literal|0
operator|)
return|;
block|}
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
comment|// Save the last index visited
name|lastIndex
operator|=
name|index
expr_stmt|;
comment|// next the index
name|index
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
comment|// if the next index points to the 'Ground' it means we're done with
comment|// the current hash entry and we need to jump to the next one. This
comment|// is done until all the hash entries had been visited.
while|while
condition|(
name|index
operator|==
literal|0
operator|&&
operator|++
name|baseHashIndex
operator|<
name|baseHash
operator|.
name|length
condition|)
block|{
name|index
operator|=
name|baseHash
index|[
name|baseHashIndex
index|]
expr_stmt|;
block|}
return|return
name|lastIndex
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|IntHashSet
operator|.
name|this
operator|.
name|remove
argument_list|(
name|keys
index|[
name|lastIndex
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implements an IntIterator, used for iteration over the map's keys.    */
DECL|class|KeyIterator
specifier|private
specifier|final
class|class
name|KeyIterator
implements|implements
name|IntIterator
block|{
DECL|field|iterator
specifier|private
name|IntIterator
name|iterator
init|=
operator|new
name|IndexIterator
argument_list|()
decl_stmt|;
DECL|method|KeyIterator
name|KeyIterator
parameter_list|()
block|{ }
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
return|return
name|keys
index|[
name|iterator
operator|.
name|next
argument_list|()
index|]
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Default capacity - in case no capacity was specified in the constructor    */
DECL|field|defaultCapacity
specifier|private
specifier|static
name|int
name|defaultCapacity
init|=
literal|16
decl_stmt|;
comment|/**    * Holds the base hash entries. if the capacity is 2^N, than the base hash    * holds 2^(N+1). It can hold    */
DECL|field|baseHash
name|int
index|[]
name|baseHash
decl_stmt|;
comment|/**    * The current capacity of the map. Always 2^N and never less than 16. We    * never use the zero index. It is needed to improve performance and is also    * used as "ground".    */
DECL|field|capacity
specifier|private
name|int
name|capacity
decl_stmt|;
comment|/**    * All objects are being allocated at map creation. Those objects are "free"    * or empty. Whenever a new pair comes along, a pair is being "allocated" or    * taken from the free-linked list. as this is just a free list.    */
DECL|field|firstEmpty
specifier|private
name|int
name|firstEmpty
decl_stmt|;
comment|/**    * hashFactor is always (2^(N+1)) - 1. Used for faster hashing.    */
DECL|field|hashFactor
specifier|private
name|int
name|hashFactor
decl_stmt|;
comment|/**    * This array holds the unique keys    */
DECL|field|keys
name|int
index|[]
name|keys
decl_stmt|;
comment|/**    * In case of collisions, we implement a double linked list of the colliding    * hash's with the following next[] and prev[]. Those are also used to store    * the "empty" list.    */
DECL|field|next
name|int
index|[]
name|next
decl_stmt|;
DECL|field|prev
specifier|private
name|int
name|prev
decl_stmt|;
comment|/**    * Number of currently objects in the map.    */
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/**    * Constructs a map with default capacity.    */
DECL|method|IntHashSet
specifier|public
name|IntHashSet
parameter_list|()
block|{
name|this
argument_list|(
name|defaultCapacity
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a map with given capacity. Capacity is adjusted to a native    * power of 2, with minimum of 16.    *     * @param capacity    *            minimum capacity for the map.    */
DECL|method|IntHashSet
specifier|public
name|IntHashSet
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
literal|16
expr_stmt|;
comment|// Minimum capacity is 16..
while|while
condition|(
name|this
operator|.
name|capacity
operator|<
name|capacity
condition|)
block|{
comment|// Multiply by 2 as long as we're still under the requested capacity
name|this
operator|.
name|capacity
operator|<<=
literal|1
expr_stmt|;
block|}
comment|// As mentioned, we use the first index (0) as 'Ground', so we need the
comment|// length of the arrays to be one more than the capacity
name|int
name|arrayLength
init|=
name|this
operator|.
name|capacity
operator|+
literal|1
decl_stmt|;
name|this
operator|.
name|keys
operator|=
operator|new
name|int
index|[
name|arrayLength
index|]
expr_stmt|;
name|this
operator|.
name|next
operator|=
operator|new
name|int
index|[
name|arrayLength
index|]
expr_stmt|;
comment|// Hash entries are twice as big as the capacity.
name|int
name|baseHashSize
init|=
name|this
operator|.
name|capacity
operator|<<
literal|1
decl_stmt|;
name|this
operator|.
name|baseHash
operator|=
operator|new
name|int
index|[
name|baseHashSize
index|]
expr_stmt|;
comment|// The has factor is 2^M - 1 which is used as an "AND" hashing operator.
comment|// {@link #calcBaseHash()}
name|this
operator|.
name|hashFactor
operator|=
name|baseHashSize
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|size
operator|=
literal|0
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a pair to the map. Takes the first empty position from the    * empty-linked-list's head - {@link #firstEmpty}.    *     * New pairs are always inserted to baseHash, and are followed by the old    * colliding pair.    *     * @param key    *            integer which maps the given value    */
DECL|method|prvt_add
specifier|private
name|void
name|prvt_add
parameter_list|(
name|int
name|key
parameter_list|)
block|{
comment|// Hash entry to which the new pair would be inserted
name|int
name|hashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// 'Allocating' a pair from the "Empty" list.
name|int
name|objectIndex
init|=
name|firstEmpty
decl_stmt|;
comment|// Setting data
name|firstEmpty
operator|=
name|next
index|[
name|firstEmpty
index|]
expr_stmt|;
name|keys
index|[
name|objectIndex
index|]
operator|=
name|key
expr_stmt|;
comment|// Inserting the new pair as the first node in the specific hash entry
name|next
index|[
name|objectIndex
index|]
operator|=
name|baseHash
index|[
name|hashIndex
index|]
expr_stmt|;
name|baseHash
index|[
name|hashIndex
index|]
operator|=
name|objectIndex
expr_stmt|;
comment|// Announcing a new pair was added!
operator|++
name|size
expr_stmt|;
block|}
comment|/**    * Calculating the baseHash index using the internal<code>hashFactor</code>    * .    */
DECL|method|calcBaseHashIndex
specifier|protected
name|int
name|calcBaseHashIndex
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|key
operator|&
name|hashFactor
return|;
block|}
comment|/**    * Empties the map. Generates the "Empty" space list for later allocation.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// Clears the hash entries
name|Arrays
operator|.
name|fill
argument_list|(
name|this
operator|.
name|baseHash
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Set size to zero
name|size
operator|=
literal|0
expr_stmt|;
comment|// Mark all array entries as empty. This is done with
comment|//<code>firstEmpty</code> pointing to the first valid index (1 as 0 is
comment|// used as 'Ground').
name|firstEmpty
operator|=
literal|1
expr_stmt|;
comment|// And setting all the<code>next[i]</code> to point at
comment|//<code>i+1</code>.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|this
operator|.
name|capacity
condition|;
control|)
block|{
name|next
index|[
name|i
index|]
operator|=
operator|++
name|i
expr_stmt|;
block|}
comment|// Surly, the last one should point to the 'Ground'.
name|next
index|[
name|this
operator|.
name|capacity
index|]
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Checks if a given key exists in the map.    *     * @param value    *            that is checked against the map data.    * @return true if the key exists in the map. false otherwise.    */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|value
argument_list|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Find the actual index of a given key.    *     * @return index of the key. zero if the key wasn't found.    */
DECL|method|find
specifier|protected
name|int
name|find
parameter_list|(
name|int
name|key
parameter_list|)
block|{
comment|// Calculate the hash entry.
name|int
name|baseHashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Start from the hash entry.
name|int
name|localIndex
init|=
name|baseHash
index|[
name|baseHashIndex
index|]
decl_stmt|;
comment|// while the index does not point to the 'Ground'
while|while
condition|(
name|localIndex
operator|!=
literal|0
condition|)
block|{
comment|// returns the index found in case of of a matching key.
if|if
condition|(
name|keys
index|[
name|localIndex
index|]
operator|==
name|key
condition|)
block|{
return|return
name|localIndex
return|;
block|}
comment|// next the local index
name|localIndex
operator|=
name|next
index|[
name|localIndex
index|]
expr_stmt|;
block|}
comment|// If we got this far, it could only mean we did not find the key we
comment|// were asked for. return 'Ground' index.
return|return
literal|0
return|;
block|}
comment|/**    * Find the actual index of a given key with it's baseHashIndex.<br>    * Some methods use the baseHashIndex. If those call {@link #find} there's    * no need to re-calculate that hash.    *     * @return the index of the given key, or 0 as 'Ground' if the key wasn't    *         found.    */
DECL|method|findForRemove
specifier|private
name|int
name|findForRemove
parameter_list|(
name|int
name|key
parameter_list|,
name|int
name|baseHashIndex
parameter_list|)
block|{
comment|// Start from the hash entry.
name|this
operator|.
name|prev
operator|=
literal|0
expr_stmt|;
name|int
name|index
init|=
name|baseHash
index|[
name|baseHashIndex
index|]
decl_stmt|;
comment|// while the index does not point to the 'Ground'
while|while
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
comment|// returns the index found in case of of a matching key.
if|if
condition|(
name|keys
index|[
name|index
index|]
operator|==
name|key
condition|)
block|{
return|return
name|index
return|;
block|}
comment|// next the local index
name|prev
operator|=
name|index
expr_stmt|;
name|index
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
block|}
comment|// If we got this far, it could only mean we did not find the key we
comment|// were asked for. return 'Ground' index.
name|this
operator|.
name|prev
operator|=
literal|0
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/**    * Grows the map. Allocates a new map of double the capacity, and    * fast-insert the old key-value pairs.    */
DECL|method|grow
specifier|protected
name|void
name|grow
parameter_list|()
block|{
name|IntHashSet
name|that
init|=
operator|new
name|IntHashSet
argument_list|(
name|this
operator|.
name|capacity
operator|*
literal|2
argument_list|)
decl_stmt|;
comment|// Iterates fast over the collection. Any valid pair is put into the new
comment|// map without checking for duplicates or if there's enough space for
comment|// it.
for|for
control|(
name|IndexIterator
name|iterator
init|=
operator|new
name|IndexIterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|int
name|index
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|that
operator|.
name|prvt_add
argument_list|(
name|this
operator|.
name|keys
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
comment|// for (int i = capacity; i> 0; --i) {
comment|//
comment|// that._add(this.keys[i]);
comment|//
comment|// }
comment|// Copy that's data into this.
name|this
operator|.
name|capacity
operator|=
name|that
operator|.
name|capacity
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|that
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|firstEmpty
operator|=
name|that
operator|.
name|firstEmpty
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|that
operator|.
name|keys
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|that
operator|.
name|next
expr_stmt|;
name|this
operator|.
name|baseHash
operator|=
name|that
operator|.
name|baseHash
expr_stmt|;
name|this
operator|.
name|hashFactor
operator|=
name|that
operator|.
name|hashFactor
expr_stmt|;
block|}
comment|/**    *     * @return true if the map is empty. false otherwise.    */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|==
literal|0
return|;
block|}
comment|/**    * Returns a new iterator for the mapped objects.    */
DECL|method|iterator
specifier|public
name|IntIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|KeyIterator
argument_list|()
return|;
block|}
comment|/**    * Prints the baseHash array, used for debug purposes.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|getBaseHashAsString
specifier|private
name|String
name|getBaseHashAsString
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|this
operator|.
name|baseHash
argument_list|)
return|;
block|}
comment|/**    * Add a mapping int key -> int value.    *<p>    * If the key was already inside just    * updating the value it refers to as the given object.    *<p>    * Otherwise if the map is full, first {@link #grow()} the map.    *     * @param value    *            integer which maps the given value    * @return true always.    */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|int
name|value
parameter_list|)
block|{
comment|// Does key exists?
name|int
name|index
init|=
name|find
argument_list|(
name|value
argument_list|)
decl_stmt|;
comment|// Yes!
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Is there enough room for a new pair?
if|if
condition|(
name|size
operator|==
name|capacity
condition|)
block|{
comment|// No? Than grow up!
name|grow
argument_list|()
expr_stmt|;
block|}
comment|// Now that everything is set, the pair can be just put inside with no
comment|// worries.
name|prvt_add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Remove a pair from the map, specified by it's key.    *     * @param value    *            specify the value to be removed    *     * @return true if the map was changed (the key was found and removed).    *         false otherwise.    */
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|int
name|baseHashIndex
init|=
name|calcBaseHashIndex
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|findForRemove
argument_list|(
name|value
argument_list|,
name|baseHashIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
comment|// If it is the first in the collision list, we should promote its
comment|// next colliding element.
if|if
condition|(
name|prev
operator|==
literal|0
condition|)
block|{
name|baseHash
index|[
name|baseHashIndex
index|]
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
block|}
name|next
index|[
name|prev
index|]
operator|=
name|next
index|[
name|index
index|]
expr_stmt|;
name|next
index|[
name|index
index|]
operator|=
name|firstEmpty
expr_stmt|;
name|firstEmpty
operator|=
name|index
expr_stmt|;
operator|--
name|size
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return number of pairs currently in the map    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|size
return|;
block|}
comment|/**    * Translates the mapped pairs' values into an array of Objects    *     * @return an object array of all the values currently in the map.    */
DECL|method|toArray
specifier|public
name|int
index|[]
name|toArray
parameter_list|()
block|{
name|int
name|j
init|=
operator|-
literal|1
decl_stmt|;
name|int
index|[]
name|array
init|=
operator|new
name|int
index|[
name|size
index|]
decl_stmt|;
comment|// Iterates over the values, adding them to the array.
for|for
control|(
name|IntIterator
name|iterator
init|=
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|array
index|[
operator|++
name|j
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
comment|/**    * Translates the mapped pairs' values into an array of ints    *     * @param a    *            the array into which the elements of the map are to be stored,    *            if it is big enough; otherwise, a new array of the same    *            runtime type is allocated for this purpose.    *     * @return an array containing the values stored in the map    *     */
DECL|method|toArray
specifier|public
name|int
index|[]
name|toArray
parameter_list|(
name|int
index|[]
name|a
parameter_list|)
block|{
name|int
name|j
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|size
condition|)
block|{
name|a
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
comment|// Iterates over the values, adding them to the array.
for|for
control|(
name|IntIterator
name|iterator
init|=
name|iterator
argument_list|()
init|;
name|j
operator|<
name|a
operator|.
name|length
operator|&&
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|a
index|[
name|j
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
comment|/**    * I have no idea why would anyone call it - but for debug purposes.<br>    * Prints the entire map, including the index, key, object, next and prev.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|IntIterator
name|iterator
init|=
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toHashString
specifier|public
name|String
name|toHashString
parameter_list|()
block|{
name|String
name|string
init|=
literal|"\n"
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|baseHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|StringBuffer
name|sb2
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|boolean
name|shouldAppend
init|=
literal|false
decl_stmt|;
name|sb2
operator|.
name|append
argument_list|(
name|i
operator|+
literal|".\t"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|index
init|=
name|baseHash
index|[
name|i
index|]
init|;
name|index
operator|!=
literal|0
condition|;
name|index
operator|=
name|next
index|[
name|index
index|]
control|)
block|{
name|sb2
operator|.
name|append
argument_list|(
literal|" -> "
operator|+
name|keys
index|[
name|index
index|]
operator|+
literal|"@"
operator|+
name|index
argument_list|)
expr_stmt|;
name|shouldAppend
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|shouldAppend
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sb2
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

