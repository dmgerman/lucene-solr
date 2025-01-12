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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|LeafReader
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|util
operator|.
name|Accountable
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
name|util
operator|.
name|BitDocIdSet
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
name|util
operator|.
name|BitSetIterator
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|FixedBitSet
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
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  *<code>BitDocSet</code> represents an unordered set of Lucene Document Ids  * using a BitSet.  A set bit represents inclusion in the set for that document.  *  * @since solr 0.9  */
end_comment

begin_class
DECL|class|BitDocSet
specifier|public
class|class
name|BitDocSet
extends|extends
name|DocSetBase
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|BitDocSet
operator|.
name|class
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|FixedBitSet
operator|.
name|class
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
comment|// for the array object inside the FixedBitSet. long[] array won't change alignment, so no need to calculate it.
DECL|field|bits
specifier|final
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
comment|// number of docs in the set (cached for perf)
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|()
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
literal|64
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a BitDocSet. The capacity of the {@link FixedBitSet} should be at    * least maxDoc()    */
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|FixedBitSet
name|bits
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Construct a BitDocSet, and provides the number of set bits. The capacity of    * the {@link FixedBitSet} should be at least maxDoc()    */
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|FixedBitSet
name|bits
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/* DocIterator using nextSetBit()   public DocIterator iterator() {     return new DocIterator() {       int pos=bits.nextSetBit(0);       public boolean hasNext() {         return pos>=0;       }        public Integer next() {         return nextDoc();       }        public void remove() {         bits.clear(pos);       }        public int nextDoc() {         int old=pos;         pos=bits.nextSetBit(old+1);         return old;       }        public float score() {         return 0.0f;       }     };   }   ***/
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIterator
argument_list|()
block|{
specifier|private
specifier|final
name|BitSetIterator
name|iter
init|=
operator|new
name|BitSetIterator
argument_list|(
name|bits
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
comment|// cost is not useful here
specifier|private
name|int
name|pos
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|next
parameter_list|()
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|bits
operator|.
name|clear
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|int
name|old
init|=
name|pos
decl_stmt|;
name|pos
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
return|return
name|old
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
literal|0.0f
return|;
block|}
block|}
return|;
block|}
comment|/**    * @return the<b>internal</b> {@link FixedBitSet} that should<b>not</b> be modified.    */
annotation|@
name|Override
DECL|method|getBits
specifier|public
name|FixedBitSet
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate size
block|}
annotation|@
name|Override
DECL|method|addUnique
specifier|public
name|void
name|addUnique
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate size
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
return|return
name|size
return|;
return|return
name|size
operator|=
name|bits
operator|.
name|cardinality
argument_list|()
return|;
block|}
comment|/**    * The number of set bits - size - is cached.  If the bitset is changed externally,    * this method should be used to invalidate the previously cached size.    */
DECL|method|invalidateSize
specifier|public
name|void
name|invalidateSize
parameter_list|()
block|{
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Returns true of the doc exists in the set. Should only be called when doc&lt;    * {@link FixedBitSet#length()}.    */
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|FixedBitSet
operator|.
name|intersectionCount
argument_list|(
name|this
operator|.
name|bits
argument_list|,
operator|(
operator|(
name|BitDocSet
operator|)
name|other
operator|)
operator|.
name|bits
argument_list|)
return|;
block|}
else|else
block|{
comment|// they had better not call us back!
return|return
name|other
operator|.
name|intersectionSize
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
return|return
name|bits
operator|.
name|intersects
argument_list|(
operator|(
operator|(
name|BitDocSet
operator|)
name|other
operator|)
operator|.
name|bits
argument_list|)
return|;
block|}
else|else
block|{
comment|// they had better not call us back!
return|return
name|other
operator|.
name|intersects
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|unionSize
specifier|public
name|int
name|unionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
comment|// if we don't know our current size, this is faster than
comment|// size + other.size - intersection_size
return|return
operator|(
name|int
operator|)
name|FixedBitSet
operator|.
name|unionCount
argument_list|(
name|this
operator|.
name|bits
argument_list|,
operator|(
operator|(
name|BitDocSet
operator|)
name|other
operator|)
operator|.
name|bits
argument_list|)
return|;
block|}
else|else
block|{
comment|// they had better not call us back!
return|return
name|other
operator|.
name|unionSize
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|andNotSize
specifier|public
name|int
name|andNotSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
comment|// if we don't know our current size, this is faster than
comment|// size - intersection_size
return|return
operator|(
name|int
operator|)
name|FixedBitSet
operator|.
name|andNotCount
argument_list|(
name|this
operator|.
name|bits
argument_list|,
operator|(
operator|(
name|BitDocSet
operator|)
name|other
operator|)
operator|.
name|bits
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|andNotSize
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|addAllTo
specifier|public
name|void
name|addAllTo
parameter_list|(
name|DocSet
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|instanceof
name|BitDocSet
condition|)
block|{
operator|(
operator|(
name|BitDocSet
operator|)
name|target
operator|)
operator|.
name|bits
operator|.
name|or
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|addAllTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|andNot
specifier|public
name|DocSet
name|andNot
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
name|FixedBitSet
name|newbits
init|=
name|bits
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
name|newbits
operator|.
name|andNot
argument_list|(
operator|(
operator|(
name|BitDocSet
operator|)
name|other
operator|)
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocIterator
name|iter
init|=
name|other
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|<
name|newbits
operator|.
name|length
argument_list|()
condition|)
block|{
name|newbits
operator|.
name|clear
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
name|newbits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|union
specifier|public
name|DocSet
name|union
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
name|FixedBitSet
name|newbits
init|=
name|bits
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|BitDocSet
condition|)
block|{
name|BitDocSet
name|otherDocSet
init|=
operator|(
name|BitDocSet
operator|)
name|other
decl_stmt|;
name|newbits
operator|=
name|FixedBitSet
operator|.
name|ensureCapacity
argument_list|(
name|newbits
argument_list|,
name|otherDocSet
operator|.
name|bits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|newbits
operator|.
name|or
argument_list|(
name|otherDocSet
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocIterator
name|iter
init|=
name|other
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|newbits
operator|=
name|FixedBitSet
operator|.
name|ensureCapacity
argument_list|(
name|newbits
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|newbits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
name|newbits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|BitDocSet
name|clone
parameter_list|()
block|{
return|return
operator|new
name|BitDocSet
argument_list|(
name|bits
operator|.
name|clone
argument_list|()
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTopFilter
specifier|public
name|Filter
name|getTopFilter
parameter_list|()
block|{
comment|// TODO: if cardinality isn't cached, do a quick measure of sparseness
comment|// and return null from bits() if too sparse.
return|return
operator|new
name|Filter
argument_list|()
block|{
specifier|final
name|FixedBitSet
name|bs
init|=
name|bits
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// all Solr DocSets that are used as filters only include live docs
specifier|final
name|Bits
name|acceptDocs2
init|=
name|acceptDocs
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
operator|==
name|acceptDocs
condition|?
literal|null
else|:
name|acceptDocs
operator|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|isTopLevel
condition|)
block|{
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|bs
argument_list|)
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
specifier|final
name|int
name|base
init|=
name|context
operator|.
name|docBase
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|base
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
comment|// one past the max doc in this segment.
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|pos
init|=
name|base
operator|-
literal|1
decl_stmt|;
name|int
name|adjustedDoc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|adjustedDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|int
name|next
init|=
name|pos
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|next
operator|>=
name|max
condition|)
block|{
return|return
name|adjustedDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|pos
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|next
argument_list|)
expr_stmt|;
return|return
name|adjustedDoc
operator|=
name|pos
operator|<
name|max
condition|?
name|pos
operator|-
name|base
else|:
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|adjustedDoc
operator|=
name|NO_MORE_DOCS
return|;
name|int
name|adjusted
init|=
name|target
operator|+
name|base
decl_stmt|;
if|if
condition|(
name|adjusted
operator|>=
name|max
condition|)
block|{
return|return
name|adjustedDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|pos
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|adjusted
argument_list|)
expr_stmt|;
return|return
name|adjustedDoc
operator|=
name|pos
operator|<
name|max
condition|?
name|pos
operator|-
name|base
else|:
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
comment|// we don't want to actually compute cardinality, but
comment|// if it's already been computed, we use it (pro-rated for the segment)
name|int
name|maxDoc
init|=
name|max
operator|-
name|base
decl_stmt|;
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
call|(
name|long
call|)
argument_list|(
name|size
operator|*
operator|(
operator|(
name|FixedBitSet
operator|.
name|bits2words
argument_list|(
name|maxDoc
argument_list|)
operator|<<
literal|6
operator|)
operator|/
operator|(
name|float
operator|)
name|bs
operator|.
name|length
argument_list|()
operator|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|maxDoc
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|bs
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|bits
parameter_list|()
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bs
operator|.
name|get
argument_list|(
name|index
operator|+
name|base
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|max
operator|-
name|base
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
name|acceptDocs2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"BitSetDocTopFilter"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|bs
argument_list|,
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|bs
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|classHash
argument_list|()
operator|*
literal|31
operator|+
name|bs
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
operator|(
operator|(
name|long
operator|)
name|bits
operator|.
name|getBits
argument_list|()
operator|.
name|length
operator|<<
literal|3
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

