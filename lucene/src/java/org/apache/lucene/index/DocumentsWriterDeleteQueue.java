begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|AtomicReferenceFieldUpdater
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
name|locks
operator|.
name|ReentrantLock
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

begin_comment
comment|/**  * {@link DocumentsWriterDeleteQueue} is a non-blocking linked pending deletes  * queue. In contrast to other queue implementation we only maintain the  * tail of the queue. A delete queue is always used in a context of a set of  * DWPTs and a global delete pool. Each of the DWPT and the global pool need to  * maintain their 'own' head of the queue (as a DeleteSlice instance per DWPT).  * The difference between the DWPT and the global pool is that the DWPT starts  * maintaining a head once it has added its first document since for its segments  * private deletes only the deletes after that document are relevant. The global  * pool instead starts maintaining the head once this instance is created by  * taking the sentinel instance as its initial head.  *<p>  * Since each {@link DeleteSlice} maintains its own head and the list is only  * single linked the garbage collector takes care of pruning the list for us.  * All nodes in the list that are still relevant should be either directly or  * indirectly referenced by one of the DWPT's private {@link DeleteSlice} or by  * the global {@link BufferedDeletes} slice.  *<p>  * Each DWPT as well as the global delete pool maintain their private  * DeleteSlice instance. In the DWPT case updating a slice is equivalent to  * atomically finishing the document. The slice update guarantees a "happens  * before" relationship to all other updates in the same indexing session. When a  * DWPT updates a document it:  *   *<ol>  *<li>consumes a document and finishes its processing</li>  *<li>updates its private {@link DeleteSlice} either by calling  * {@link #updateSlice(DeleteSlice)} or {@link #add(Term, DeleteSlice)} (if the  * document has a delTerm)</li>  *<li>applies all deletes in the slice to its private {@link BufferedDeletes}  * and resets it</li>  *<li>increments its internal document id</li>  *</ol>  *   * The DWPT also doesn't apply its current documents delete term until it has  * updated its delete slice which ensures the consistency of the update. If the  * update fails before the DeleteSlice could have been updated the deleteTerm  * will also not be added to its private deletes neither to the global deletes.  *   */
end_comment

begin_class
DECL|class|DocumentsWriterDeleteQueue
specifier|final
class|class
name|DocumentsWriterDeleteQueue
block|{
DECL|field|tail
specifier|private
specifier|volatile
name|Node
name|tail
decl_stmt|;
DECL|field|tailUpdater
specifier|private
specifier|static
specifier|final
name|AtomicReferenceFieldUpdater
argument_list|<
name|DocumentsWriterDeleteQueue
argument_list|,
name|Node
argument_list|>
name|tailUpdater
init|=
name|AtomicReferenceFieldUpdater
operator|.
name|newUpdater
argument_list|(
name|DocumentsWriterDeleteQueue
operator|.
name|class
argument_list|,
name|Node
operator|.
name|class
argument_list|,
literal|"tail"
argument_list|)
decl_stmt|;
DECL|field|globalSlice
specifier|private
specifier|final
name|DeleteSlice
name|globalSlice
decl_stmt|;
DECL|field|globalBufferedDeletes
specifier|private
specifier|final
name|BufferedDeletes
name|globalBufferedDeletes
decl_stmt|;
comment|/* only acquired to update the global deletes */
DECL|field|globalBufferLock
specifier|private
specifier|final
name|ReentrantLock
name|globalBufferLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|generation
specifier|final
name|long
name|generation
decl_stmt|;
DECL|method|DocumentsWriterDeleteQueue
name|DocumentsWriterDeleteQueue
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|DocumentsWriterDeleteQueue
name|DocumentsWriterDeleteQueue
parameter_list|(
name|long
name|generation
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
argument_list|,
name|generation
argument_list|)
expr_stmt|;
block|}
DECL|method|DocumentsWriterDeleteQueue
name|DocumentsWriterDeleteQueue
parameter_list|(
name|BufferedDeletes
name|globalBufferedDeletes
parameter_list|,
name|long
name|generation
parameter_list|)
block|{
name|this
operator|.
name|globalBufferedDeletes
operator|=
name|globalBufferedDeletes
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
comment|/*      * we use a sentinel instance as our initial tail. No slice will ever try to      * apply this tail since the head is always omitted.      */
name|tail
operator|=
operator|new
name|Node
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// sentinel
name|globalSlice
operator|=
operator|new
name|DeleteSlice
argument_list|(
name|tail
argument_list|)
expr_stmt|;
block|}
DECL|method|addDelete
name|void
name|addDelete
parameter_list|(
name|Query
modifier|...
name|queries
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|QueryArrayNode
argument_list|(
name|queries
argument_list|)
argument_list|)
expr_stmt|;
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
block|}
DECL|method|addDelete
name|void
name|addDelete
parameter_list|(
name|Term
modifier|...
name|terms
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|TermArrayNode
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
block|}
comment|/**    * invariant for document update    */
DECL|method|add
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|DeleteSlice
name|slice
parameter_list|)
block|{
specifier|final
name|TermNode
name|termNode
init|=
operator|new
name|TermNode
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|termNode
argument_list|)
expr_stmt|;
comment|/*      * this is an update request where the term is the updated documents      * delTerm. in that case we need to guarantee that this insert is atomic      * with regards to the given delete slice. This means if two threads try to      * update the same document with in turn the same delTerm one of them must      * win. By taking the node we have created for our del term as the new tail      * it is guaranteed that if another thread adds the same right after us we      * will apply this delete next time we update our slice and one of the two      * competing updates wins!      */
name|slice
operator|.
name|sliceTail
operator|=
name|termNode
expr_stmt|;
assert|assert
name|slice
operator|.
name|sliceHead
operator|!=
name|slice
operator|.
name|sliceTail
operator|:
literal|"slice head and tail must differ after add"
assert|;
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
comment|// TODO doing this each time is not necessary maybe
comment|// we can do it just every n times or so?
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|Node
name|item
parameter_list|)
block|{
comment|/*      * this non-blocking / 'wait-free' linked list add was inspired by Apache      * Harmony's ConcurrentLinkedQueue Implementation.      */
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|Node
name|currentTail
init|=
name|this
operator|.
name|tail
decl_stmt|;
specifier|final
name|Node
name|tailNext
init|=
name|currentTail
operator|.
name|next
decl_stmt|;
if|if
condition|(
name|tail
operator|==
name|currentTail
condition|)
block|{
if|if
condition|(
name|tailNext
operator|!=
literal|null
condition|)
block|{
comment|/*            * we are in intermediate state here. the tails next pointer has been            * advanced but the tail itself might not be updated yet. help to            * advance the tail and try again updating it.            */
name|tailUpdater
operator|.
name|compareAndSet
argument_list|(
name|this
argument_list|,
name|currentTail
argument_list|,
name|tailNext
argument_list|)
expr_stmt|;
comment|// can fail
block|}
else|else
block|{
comment|/*            * we are in quiescent state and can try to insert the item to the            * current tail if we fail to insert we just retry the operation since            * somebody else has already added its item            */
if|if
condition|(
name|currentTail
operator|.
name|casNext
argument_list|(
literal|null
argument_list|,
name|item
argument_list|)
condition|)
block|{
comment|/*              * now that we are done we need to advance the tail while another              * thread could have advanced it already so we can ignore the return              * type of this CAS call              */
name|tailUpdater
operator|.
name|compareAndSet
argument_list|(
name|this
argument_list|,
name|currentTail
argument_list|,
name|item
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
block|}
DECL|method|anyChanges
name|boolean
name|anyChanges
parameter_list|()
block|{
name|globalBufferLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|!
name|globalSlice
operator|.
name|isEmpty
argument_list|()
operator|||
name|globalBufferedDeletes
operator|.
name|any
argument_list|()
return|;
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|tryApplyGlobalSlice
name|void
name|tryApplyGlobalSlice
parameter_list|()
block|{
if|if
condition|(
name|globalBufferLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
comment|/*        * The global buffer must be locked but we don't need to upate them if        * there is an update going on right now. It is sufficient to apply the        * deletes that have been added after the current in-flight global slices        * tail the next time we can get the lock!        */
try|try
block|{
if|if
condition|(
name|updateSlice
argument_list|(
name|globalSlice
argument_list|)
condition|)
block|{
name|globalSlice
operator|.
name|apply
argument_list|(
name|globalBufferedDeletes
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|freezeGlobalBuffer
name|FrozenBufferedDeletes
name|freezeGlobalBuffer
parameter_list|(
name|DeleteSlice
name|callerSlice
parameter_list|)
block|{
name|globalBufferLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|/*      * Here we freeze the global buffer so we need to lock it, apply all      * deletes in the queue and reset the global slice to let the GC prune the      * queue.      */
specifier|final
name|Node
name|currentTail
init|=
name|tail
decl_stmt|;
comment|// take the current tail make this local any
comment|// Changes after this call are applied later
comment|// and not relevant here
if|if
condition|(
name|callerSlice
operator|!=
literal|null
condition|)
block|{
comment|// Update the callers slices so we are on the same page
name|callerSlice
operator|.
name|sliceTail
operator|=
name|currentTail
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|globalSlice
operator|.
name|sliceTail
operator|!=
name|currentTail
condition|)
block|{
name|globalSlice
operator|.
name|sliceTail
operator|=
name|currentTail
expr_stmt|;
name|globalSlice
operator|.
name|apply
argument_list|(
name|globalBufferedDeletes
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FrozenBufferedDeletes
name|packet
init|=
operator|new
name|FrozenBufferedDeletes
argument_list|(
name|globalBufferedDeletes
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|globalBufferedDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|packet
return|;
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newSlice
name|DeleteSlice
name|newSlice
parameter_list|()
block|{
return|return
operator|new
name|DeleteSlice
argument_list|(
name|tail
argument_list|)
return|;
block|}
DECL|method|updateSlice
name|boolean
name|updateSlice
parameter_list|(
name|DeleteSlice
name|slice
parameter_list|)
block|{
if|if
condition|(
name|slice
operator|.
name|sliceTail
operator|!=
name|tail
condition|)
block|{
comment|// If we are the same just
name|slice
operator|.
name|sliceTail
operator|=
name|tail
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|class|DeleteSlice
specifier|static
class|class
name|DeleteSlice
block|{
comment|// No need to be volatile, slices are thread captive (only accessed by one thread)!
DECL|field|sliceHead
name|Node
name|sliceHead
decl_stmt|;
comment|// we don't apply this one
DECL|field|sliceTail
name|Node
name|sliceTail
decl_stmt|;
DECL|method|DeleteSlice
name|DeleteSlice
parameter_list|(
name|Node
name|currentTail
parameter_list|)
block|{
assert|assert
name|currentTail
operator|!=
literal|null
assert|;
comment|/*        * Initially this is a 0 length slice pointing to the 'current' tail of        * the queue. Once we update the slice we only need to assign the tail and        * have a new slice        */
name|sliceHead
operator|=
name|sliceTail
operator|=
name|currentTail
expr_stmt|;
block|}
DECL|method|apply
name|void
name|apply
parameter_list|(
name|BufferedDeletes
name|del
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
if|if
condition|(
name|sliceHead
operator|==
name|sliceTail
condition|)
block|{
comment|// 0 length slice
return|return;
block|}
comment|/*        * When we apply a slice we take the head and get its next as our first        * item to apply and continue until we applied the tail. If the head and        * tail in this slice are not equal then there will be at least one more        * non-null node in the slice!        */
name|Node
name|current
init|=
name|sliceHead
decl_stmt|;
do|do
block|{
name|current
operator|=
name|current
operator|.
name|next
expr_stmt|;
assert|assert
name|current
operator|!=
literal|null
operator|:
literal|"slice property violated between the head on the tail must not be a null node"
assert|;
name|current
operator|.
name|apply
argument_list|(
name|del
argument_list|,
name|docIDUpto
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|current
operator|!=
name|sliceTail
condition|)
do|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
comment|// Reset to a 0 length slice
name|sliceHead
operator|=
name|sliceTail
expr_stmt|;
block|}
comment|/**      * Returns<code>true</code> iff the given item is identical to the item      * hold by the slices tail, otherwise<code>false</code>.      */
DECL|method|isTailItem
name|boolean
name|isTailItem
parameter_list|(
name|Object
name|item
parameter_list|)
block|{
return|return
name|sliceTail
operator|.
name|item
operator|==
name|item
return|;
block|}
DECL|method|isEmpty
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|sliceHead
operator|==
name|sliceTail
return|;
block|}
block|}
DECL|method|numGlobalTermDeletes
specifier|public
name|int
name|numGlobalTermDeletes
parameter_list|()
block|{
return|return
name|globalBufferedDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|globalBufferLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|Node
name|currentTail
init|=
name|tail
decl_stmt|;
name|globalSlice
operator|.
name|sliceHead
operator|=
name|globalSlice
operator|.
name|sliceTail
operator|=
name|currentTail
expr_stmt|;
name|globalBufferedDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Node
specifier|private
specifier|static
class|class
name|Node
block|{
DECL|field|next
specifier|volatile
name|Node
name|next
decl_stmt|;
DECL|field|item
specifier|final
name|Object
name|item
decl_stmt|;
DECL|method|Node
specifier|private
name|Node
parameter_list|(
name|Object
name|item
parameter_list|)
block|{
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
block|}
DECL|field|nextUpdater
specifier|static
specifier|final
name|AtomicReferenceFieldUpdater
argument_list|<
name|Node
argument_list|,
name|Node
argument_list|>
name|nextUpdater
init|=
name|AtomicReferenceFieldUpdater
operator|.
name|newUpdater
argument_list|(
name|Node
operator|.
name|class
argument_list|,
name|Node
operator|.
name|class
argument_list|,
literal|"next"
argument_list|)
decl_stmt|;
DECL|method|apply
name|void
name|apply
parameter_list|(
name|BufferedDeletes
name|bufferedDeletes
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"sentinel item must never be applied"
assert|;
block|}
DECL|method|casNext
name|boolean
name|casNext
parameter_list|(
name|Node
name|cmp
parameter_list|,
name|Node
name|val
parameter_list|)
block|{
return|return
name|nextUpdater
operator|.
name|compareAndSet
argument_list|(
name|this
argument_list|,
name|cmp
argument_list|,
name|val
argument_list|)
return|;
block|}
block|}
DECL|class|TermNode
specifier|private
specifier|static
specifier|final
class|class
name|TermNode
extends|extends
name|Node
block|{
DECL|method|TermNode
name|TermNode
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
name|void
name|apply
parameter_list|(
name|BufferedDeletes
name|bufferedDeletes
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
name|bufferedDeletes
operator|.
name|addTerm
argument_list|(
operator|(
name|Term
operator|)
name|item
argument_list|,
name|docIDUpto
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|QueryArrayNode
specifier|private
specifier|static
specifier|final
class|class
name|QueryArrayNode
extends|extends
name|Node
block|{
DECL|method|QueryArrayNode
name|QueryArrayNode
parameter_list|(
name|Query
index|[]
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
name|void
name|apply
parameter_list|(
name|BufferedDeletes
name|bufferedDeletes
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
specifier|final
name|Query
index|[]
name|queries
init|=
operator|(
name|Query
index|[]
operator|)
name|item
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
name|bufferedDeletes
operator|.
name|addQuery
argument_list|(
name|query
argument_list|,
name|docIDUpto
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TermArrayNode
specifier|private
specifier|static
specifier|final
class|class
name|TermArrayNode
extends|extends
name|Node
block|{
DECL|method|TermArrayNode
name|TermArrayNode
parameter_list|(
name|Term
index|[]
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
name|void
name|apply
parameter_list|(
name|BufferedDeletes
name|bufferedDeletes
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
specifier|final
name|Term
index|[]
name|terms
init|=
operator|(
name|Term
index|[]
operator|)
name|item
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|bufferedDeletes
operator|.
name|addTerm
argument_list|(
name|term
argument_list|,
name|docIDUpto
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|forceApplyGlobalSlice
specifier|private
name|boolean
name|forceApplyGlobalSlice
parameter_list|()
block|{
name|globalBufferLock
operator|.
name|lock
argument_list|()
expr_stmt|;
specifier|final
name|Node
name|currentTail
init|=
name|tail
decl_stmt|;
try|try
block|{
if|if
condition|(
name|globalSlice
operator|.
name|sliceTail
operator|!=
name|currentTail
condition|)
block|{
name|globalSlice
operator|.
name|sliceTail
operator|=
name|currentTail
expr_stmt|;
name|globalSlice
operator|.
name|apply
argument_list|(
name|globalBufferedDeletes
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
return|return
name|globalBufferedDeletes
operator|.
name|any
argument_list|()
return|;
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getBufferedDeleteTermsSize
specifier|public
name|int
name|getBufferedDeleteTermsSize
parameter_list|()
block|{
name|globalBufferLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|forceApplyGlobalSlice
argument_list|()
expr_stmt|;
return|return
name|globalBufferedDeletes
operator|.
name|terms
operator|.
name|size
argument_list|()
return|;
block|}
finally|finally
block|{
name|globalBufferLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|bytesUsed
specifier|public
name|long
name|bytesUsed
parameter_list|()
block|{
return|return
name|globalBufferedDeletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DWDQ: [ generation: "
operator|+
name|generation
operator|+
literal|" ]"
return|;
block|}
block|}
end_class

end_unit

