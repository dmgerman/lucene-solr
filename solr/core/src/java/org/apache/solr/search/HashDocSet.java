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
name|BitUtil
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
comment|/**  *<code>HashDocSet</code> represents an unordered set of Lucene Document Ids  * using a primitive int hash table.  It can be a better choice if there are few docs  * in the set because it takes up less memory and is faster to iterate and take  * set intersections.  *  *  * @since solr 0.9  */
end_comment

begin_class
DECL|class|HashDocSet
specifier|public
specifier|final
class|class
name|HashDocSet
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
name|HashDocSet
operator|.
name|class
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
comment|/** Default load factor to use for HashDocSets.  We keep track of the inverse    *  since multiplication is so much faster than division.  The default    *  is 1.0f / 0.75f    */
DECL|field|DEFAULT_INVERSE_LOAD_FACTOR
specifier|static
name|float
name|DEFAULT_INVERSE_LOAD_FACTOR
init|=
literal|1.0f
operator|/
literal|0.75f
decl_stmt|;
comment|// public final static int MAX_SIZE = SolrConfig.config.getInt("//HashDocSet/@maxSize",-1);
comment|// lucene docs are numbered from 0, so a neg number must be used for missing.
comment|// an alternative to having to init the array to EMPTY at the start is
comment|//
DECL|field|EMPTY
specifier|private
specifier|final
specifier|static
name|int
name|EMPTY
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|table
specifier|private
specifier|final
name|int
index|[]
name|table
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|int
name|mask
decl_stmt|;
DECL|method|HashDocSet
specifier|public
name|HashDocSet
parameter_list|(
name|HashDocSet
name|set
parameter_list|)
block|{
name|this
operator|.
name|table
operator|=
name|set
operator|.
name|table
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|set
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|mask
operator|=
name|set
operator|.
name|mask
expr_stmt|;
block|}
comment|/** Create a HashDocSet from a list of *unique* ids */
DECL|method|HashDocSet
specifier|public
name|HashDocSet
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
argument_list|(
name|docs
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|DEFAULT_INVERSE_LOAD_FACTOR
argument_list|)
expr_stmt|;
block|}
comment|/** Create a HashDocSet from a list of *unique* ids */
DECL|method|HashDocSet
specifier|public
name|HashDocSet
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|float
name|inverseLoadFactor
parameter_list|)
block|{
name|int
name|tsize
init|=
name|Math
operator|.
name|max
argument_list|(
name|BitUtil
operator|.
name|nextHighestPowerOfTwo
argument_list|(
name|len
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|tsize
operator|<
name|len
operator|*
name|inverseLoadFactor
condition|)
block|{
name|tsize
operator|<<=
literal|1
expr_stmt|;
block|}
name|mask
operator|=
name|tsize
operator|-
literal|1
expr_stmt|;
name|table
operator|=
operator|new
name|int
index|[
name|tsize
index|]
expr_stmt|;
comment|// (for now) better then: Arrays.fill(table, EMPTY);
comment|// https://issues.apache.org/jira/browse/SOLR-390
for|for
control|(
name|int
name|i
init|=
name|tsize
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
name|table
index|[
name|i
index|]
operator|=
name|EMPTY
expr_stmt|;
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|put
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|len
expr_stmt|;
block|}
DECL|method|put
name|void
name|put
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|s
init|=
name|doc
operator|&
name|mask
decl_stmt|;
while|while
condition|(
name|table
index|[
name|s
index|]
operator|!=
name|EMPTY
condition|)
block|{
comment|// Adding an odd number to this power-of-two hash table is
comment|// guaranteed to do a full traversal, so instead of re-hashing
comment|// we jump straight to a "linear" traversal.
comment|// The key is that we provide many different ways to do the
comment|// traversal (tablesize/2) based on the last hash code (the doc).
comment|// Rely on loop invariant code motion to eval ((doc>>7)|1) only once.
comment|// otherwise, we would need to pull the first case out of the loop.
name|s
operator|=
operator|(
name|s
operator|+
operator|(
operator|(
name|doc
operator|>>
literal|7
operator|)
operator||
literal|1
operator|)
operator|)
operator|&
name|mask
expr_stmt|;
block|}
name|table
index|[
name|s
index|]
operator|=
name|doc
expr_stmt|;
block|}
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
name|int
name|s
init|=
name|doc
operator|&
name|mask
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|v
init|=
name|table
index|[
name|s
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|==
name|EMPTY
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|v
operator|==
name|doc
condition|)
return|return
literal|true
return|;
comment|// see put() for algorithm details.
name|s
operator|=
operator|(
name|s
operator|+
operator|(
operator|(
name|doc
operator|>>
literal|7
operator|)
operator||
literal|1
operator|)
operator|)
operator|&
name|mask
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
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
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|doc
decl_stmt|;
block|{
name|goNext
parameter_list|()
constructor_decl|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|table
operator|.
name|length
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
block|{       }
name|void
name|goNext
parameter_list|()
block|{
while|while
condition|(
name|pos
operator|<
name|table
operator|.
name|length
operator|&&
name|table
index|[
name|pos
index|]
operator|==
name|EMPTY
condition|)
name|pos
operator|++
expr_stmt|;
block|}
comment|// modify to return -1 at end of iteration?
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|int
name|doc
init|=
name|table
index|[
name|pos
index|]
decl_stmt|;
name|pos
operator|++
expr_stmt|;
name|goNext
argument_list|()
expr_stmt|;
return|return
name|doc
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
if|if
condition|(
name|other
operator|instanceof
name|HashDocSet
condition|)
block|{
comment|// set "a" to the smallest doc set for the most efficient
comment|// intersection.
specifier|final
name|HashDocSet
name|a
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
name|this
else|:
operator|(
name|HashDocSet
operator|)
name|other
decl_stmt|;
specifier|final
name|HashDocSet
name|b
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
operator|(
name|HashDocSet
operator|)
name|other
else|:
name|this
decl_stmt|;
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|a
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|resultCount
init|=
literal|0
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
name|a
operator|.
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|a
operator|.
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|b
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|result
index|[
name|resultCount
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|resultCount
argument_list|)
return|;
block|}
else|else
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|resultCount
init|=
literal|0
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
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|other
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|result
index|[
name|resultCount
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|resultCount
argument_list|)
return|;
block|}
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
name|HashDocSet
condition|)
block|{
comment|// set "a" to the smallest doc set for the most efficient
comment|// intersection.
specifier|final
name|HashDocSet
name|a
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
name|this
else|:
operator|(
name|HashDocSet
operator|)
name|other
decl_stmt|;
specifier|final
name|HashDocSet
name|b
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
operator|(
name|HashDocSet
operator|)
name|other
else|:
name|this
decl_stmt|;
name|int
name|resultCount
init|=
literal|0
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
name|a
operator|.
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|a
operator|.
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|b
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|resultCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|resultCount
return|;
block|}
else|else
block|{
name|int
name|resultCount
init|=
literal|0
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
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|other
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|resultCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|resultCount
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
name|HashDocSet
condition|)
block|{
comment|// set "a" to the smallest doc set for the most efficient
comment|// intersection.
specifier|final
name|HashDocSet
name|a
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
name|this
else|:
operator|(
name|HashDocSet
operator|)
name|other
decl_stmt|;
specifier|final
name|HashDocSet
name|b
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
operator|(
name|HashDocSet
operator|)
name|other
else|:
name|this
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
name|a
operator|.
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|a
operator|.
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|b
operator|.
name|exists
argument_list|(
name|id
argument_list|)
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
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
name|other
operator|.
name|exists
argument_list|(
name|id
argument_list|)
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
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|resultCount
init|=
literal|0
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
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
operator|!
name|other
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|result
index|[
name|resultCount
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|resultCount
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
if|if
condition|(
name|other
operator|instanceof
name|HashDocSet
condition|)
block|{
comment|// set "a" to the smallest doc set
specifier|final
name|HashDocSet
name|a
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
name|this
else|:
operator|(
name|HashDocSet
operator|)
name|other
decl_stmt|;
specifier|final
name|HashDocSet
name|b
init|=
name|size
argument_list|()
operator|<=
name|other
operator|.
name|size
argument_list|()
condition|?
operator|(
name|HashDocSet
operator|)
name|other
else|:
name|this
decl_stmt|;
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|a
operator|.
name|size
argument_list|()
operator|+
name|b
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|resultCount
init|=
literal|0
decl_stmt|;
comment|// iterate over the largest table first, adding w/o checking.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b
operator|.
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|b
operator|.
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
condition|)
name|result
index|[
name|resultCount
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
comment|// now iterate over smaller set, adding all not already in larger set.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|a
operator|.
name|table
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|a
operator|.
name|table
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
operator|&&
operator|!
name|b
operator|.
name|exists
argument_list|(
name|id
argument_list|)
condition|)
name|result
index|[
name|resultCount
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|resultCount
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|other
operator|.
name|union
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|HashDocSet
name|clone
parameter_list|()
block|{
return|return
operator|new
name|HashDocSet
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// don't implement andNotSize() and unionSize() on purpose... they are implemented
comment|// in BaseDocSet in terms of intersectionSize().
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
name|table
operator|.
name|length
operator|<<
literal|2
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

