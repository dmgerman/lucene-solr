begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
operator|.
name|util
operator|.
name|OpenBitSet
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
name|OpenBitSetIterator
import|;
end_import

begin_comment
comment|/**  *<code>BitDocSet</code> represents an unordered set of Lucene Document Ids  * using a BitSet.  A set bit represents inclusion in the set for that document.  *  * @version $Id$  * @since solr 0.9  */
end_comment

begin_class
DECL|class|BitDocSet
specifier|public
class|class
name|BitDocSet
extends|extends
name|DocSetBase
block|{
DECL|field|bits
specifier|final
name|OpenBitSet
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
name|OpenBitSet
argument_list|()
expr_stmt|;
block|}
comment|/** Construct a BitDocSet.    * The capacity of the OpenBitSet should be at least maxDoc() */
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|OpenBitSet
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
comment|/** Construct a BitDocSet, and provides the number of set bits.    * The capacity of the OpenBitSet should be at least maxDoc()    */
DECL|method|BitDocSet
specifier|public
name|BitDocSet
parameter_list|(
name|OpenBitSet
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
comment|/*** DocIterator using nextSetBit()   public DocIterator iterator() {     return new DocIterator() {       int pos=bits.nextSetBit(0);       public boolean hasNext() {         return pos>=0;       }        public Integer next() {         return nextDoc();       }        public void remove() {         bits.clear(pos);       }        public int nextDoc() {         int old=pos;         pos=bits.nextSetBit(old+1);         return old;       }        public float score() {         return 0.0f;       }     };   }   ***/
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
name|OpenBitSetIterator
name|iter
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|bits
argument_list|)
decl_stmt|;
specifier|private
name|int
name|pos
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|>=
literal|0
return|;
block|}
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
comment|/**    *    * @return the<b>internal</b> OpenBitSet that should<b>not</b> be modified.    */
DECL|method|getBits
specifier|public
name|OpenBitSet
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
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
operator|(
name|int
operator|)
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
name|OpenBitSet
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
name|OpenBitSet
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
name|OpenBitSet
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
DECL|method|andNot
specifier|public
name|DocSet
name|andNot
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
name|OpenBitSet
name|newbits
init|=
call|(
name|OpenBitSet
call|)
argument_list|(
name|bits
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|OpenBitSet
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
name|newbits
operator|.
name|clear
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
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
name|OpenBitSet
name|newbits
init|=
call|(
name|OpenBitSet
call|)
argument_list|(
name|bits
operator|.
name|clone
argument_list|()
argument_list|)
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
name|union
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
name|newbits
operator|.
name|set
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
name|newbits
argument_list|)
return|;
block|}
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
block|{
return|return
operator|(
name|bits
operator|.
name|getBits
argument_list|()
operator|.
name|length
operator|<<
literal|3
operator|)
operator|+
literal|16
return|;
block|}
block|}
end_class

end_unit

