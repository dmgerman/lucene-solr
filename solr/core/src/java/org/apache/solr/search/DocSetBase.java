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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|Filter
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
name|search
operator|.
name|BitsFilteredDocIdSet
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
name|AtomicReaderContext
import|;
end_import

begin_comment
comment|/** A base class that may be usefull for implementing DocSets */
end_comment

begin_class
DECL|class|DocSetBase
specifier|abstract
class|class
name|DocSetBase
implements|implements
name|DocSet
block|{
comment|// Not implemented efficiently... for testing purposes only
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|DocSet
operator|)
condition|)
return|return
literal|false
return|;
name|DocSet
name|other
init|=
operator|(
name|DocSet
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|instanceof
name|DocList
operator|&&
name|other
operator|instanceof
name|DocList
condition|)
block|{
comment|// compare ordering
name|DocIterator
name|i1
init|=
name|this
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIterator
name|i2
init|=
name|other
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|&&
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|i1
operator|.
name|nextDoc
argument_list|()
operator|!=
name|i2
operator|.
name|nextDoc
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
comment|// don't compare matches
block|}
comment|// if (this.size() != other.size()) return false;
return|return
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @throws SolrException Base implementation does not allow modifications    */
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unsupported Operation"
argument_list|)
throw|;
block|}
comment|/**    * @throws SolrException Base implementation does not allow modifications    */
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unsupported Operation"
argument_list|)
throw|;
block|}
comment|/**    * Inefficient base implementation.    *    * @see BitDocSet#getBits    */
annotation|@
name|Override
DECL|method|getBits
specifier|public
name|OpenBitSet
name|getBits
parameter_list|()
block|{
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|DocIterator
name|iter
init|=
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|bits
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
name|bits
return|;
block|}
empty_stmt|;
annotation|@
name|Override
DECL|method|intersection
specifier|public
name|DocSet
name|intersection
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
comment|// intersection is overloaded in the smaller DocSets to be more
comment|// efficient, so dispatch off of it instead.
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|BitDocSet
operator|)
condition|)
block|{
return|return
name|other
operator|.
name|intersection
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// Default... handle with bitsets.
name|OpenBitSet
name|newbits
init|=
call|(
name|OpenBitSet
call|)
argument_list|(
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|newbits
operator|.
name|and
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
comment|// intersection is overloaded in the smaller DocSets to be more
comment|// efficient, so dispatch off of it instead.
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|BitDocSet
operator|)
condition|)
block|{
return|return
name|other
operator|.
name|intersects
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// less efficient way: get the intersection size
return|return
name|intersectionSize
argument_list|(
name|other
argument_list|)
operator|>
literal|0
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
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|newbits
operator|.
name|or
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
comment|// intersection is overloaded in the smaller DocSets to be more
comment|// efficient, so dispatch off of it instead.
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|BitDocSet
operator|)
condition|)
block|{
return|return
name|other
operator|.
name|intersectionSize
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// less efficient way: do the intersection then get it's size
return|return
name|intersection
argument_list|(
name|other
argument_list|)
operator|.
name|size
argument_list|()
return|;
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
return|return
name|this
operator|.
name|size
argument_list|()
operator|+
name|other
operator|.
name|size
argument_list|()
operator|-
name|this
operator|.
name|intersectionSize
argument_list|(
name|other
argument_list|)
return|;
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
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|newbits
operator|.
name|andNot
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|andNotSize
specifier|public
name|int
name|andNotSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|size
argument_list|()
operator|-
name|this
operator|.
name|intersectionSize
argument_list|(
name|other
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
specifier|final
name|OpenBitSet
name|bs
init|=
name|getBits
argument_list|()
decl_stmt|;
return|return
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|AtomicReader
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
name|bs
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
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|base
operator|+
name|maxDoc
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
name|pos
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|adjustedDoc
operator|=
operator|(
name|pos
operator|>=
literal|0
operator|&&
name|pos
operator|<
name|max
operator|)
condition|?
name|pos
operator|-
name|base
else|:
name|NO_MORE_DOCS
return|;
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
name|pos
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|target
operator|+
name|base
argument_list|)
expr_stmt|;
return|return
name|adjustedDoc
operator|=
operator|(
name|pos
operator|>=
literal|0
operator|&&
name|pos
operator|<
name|max
operator|)
condition|?
name|pos
operator|-
name|base
else|:
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|bs
operator|.
name|capacity
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|bits
parameter_list|()
block|{
comment|// sparse filters should not use random access
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|acceptDocs2
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|setBitsOn
specifier|public
name|void
name|setBitsOn
parameter_list|(
name|OpenBitSet
name|target
parameter_list|)
block|{
name|DocIterator
name|iter
init|=
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
name|target
operator|.
name|fastSet
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

