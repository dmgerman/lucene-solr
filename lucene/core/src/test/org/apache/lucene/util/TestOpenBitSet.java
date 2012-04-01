begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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

begin_class
DECL|class|TestOpenBitSet
specifier|public
class|class
name|TestOpenBitSet
extends|extends
name|LuceneTestCase
block|{
DECL|method|doGet
name|void
name|doGet
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|max
init|=
name|a
operator|.
name|size
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
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|get
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doGetFast
name|void
name|doGetFast
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|,
name|int
name|max
parameter_list|)
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
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|fastGet
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|fastGet
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doNextSetBit
name|void
name|doNextSetBit
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|nextSetBit
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doNextSetBitLong
name|void
name|doNextSetBitLong
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
operator|(
name|int
operator|)
name|b
operator|.
name|nextSetBit
argument_list|(
call|(
name|long
call|)
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doPrevSetBit
name|void
name|doPrevSetBit
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
name|a
operator|.
name|size
argument_list|()
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|int
name|bb
init|=
name|aa
decl_stmt|;
do|do
block|{
comment|// aa = a.prevSetBit(aa-1);
name|aa
operator|--
expr_stmt|;
while|while
condition|(
operator|(
name|aa
operator|>=
literal|0
operator|)
operator|&&
operator|(
operator|!
name|a
operator|.
name|get
argument_list|(
name|aa
argument_list|)
operator|)
condition|)
block|{
name|aa
operator|--
expr_stmt|;
block|}
name|bb
operator|=
name|b
operator|.
name|prevSetBit
argument_list|(
name|bb
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doPrevSetBitLong
name|void
name|doPrevSetBitLong
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
name|a
operator|.
name|size
argument_list|()
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|int
name|bb
init|=
name|aa
decl_stmt|;
do|do
block|{
comment|// aa = a.prevSetBit(aa-1);
name|aa
operator|--
expr_stmt|;
while|while
condition|(
operator|(
name|aa
operator|>=
literal|0
operator|)
operator|&&
operator|(
operator|!
name|a
operator|.
name|get
argument_list|(
name|aa
argument_list|)
operator|)
condition|)
block|{
name|aa
operator|--
expr_stmt|;
block|}
name|bb
operator|=
operator|(
name|int
operator|)
name|b
operator|.
name|prevSetBit
argument_list|(
call|(
name|long
call|)
argument_list|(
name|bb
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
comment|// test interleaving different OpenBitSetIterator.next()/skipTo()
DECL|method|doIterate
name|void
name|doIterate
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|mode
operator|==
literal|1
condition|)
name|doIterate1
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
literal|2
condition|)
name|doIterate2
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|doIterate1
name|void
name|doIterate1
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
name|OpenBitSetIterator
name|iterator
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|b
argument_list|)
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|iterator
operator|.
name|nextDoc
argument_list|()
else|:
name|iterator
operator|.
name|advance
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
operator|==
operator|-
literal|1
condition|?
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
else|:
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doIterate2
name|void
name|doIterate2
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
name|OpenBitSetIterator
name|iterator
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|b
argument_list|)
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|iterator
operator|.
name|nextDoc
argument_list|()
else|:
name|iterator
operator|.
name|advance
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
operator|==
operator|-
literal|1
condition|?
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
else|:
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doRandomSets
name|void
name|doRandomSets
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|int
name|iter
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|BitSet
name|a0
init|=
literal|null
decl_stmt|;
name|OpenBitSet
name|b0
init|=
literal|null
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|sz
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|BitSet
name|a
init|=
operator|new
name|BitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|OpenBitSet
name|b
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
comment|// test the various ways of setting bits
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
name|int
name|nOper
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nOper
condition|;
name|j
operator|++
control|)
block|{
name|int
name|idx
decl_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastSet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastSet
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastClear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastClear
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|flip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastFlip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|boolean
name|val
init|=
name|b
operator|.
name|flipAndGet
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|boolean
name|val2
init|=
name|b
operator|.
name|flipAndGet
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|val
operator|!=
name|val2
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|flip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastFlip
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
expr_stmt|;
name|val
operator|=
name|b
operator|.
name|flipAndGet
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
expr_stmt|;
name|val2
operator|=
name|b
operator|.
name|flipAndGet
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|val
operator|!=
name|val2
argument_list|)
expr_stmt|;
name|val
operator|=
name|b
operator|.
name|getAndSet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|val2
operator|==
name|val
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val
condition|)
name|b
operator|.
name|fastClear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|==
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test that the various ways of accessing the bits are equivalent
name|doGet
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|doGetFast
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|sz
argument_list|)
expr_stmt|;
comment|// test ranges, including possible extension
name|int
name|fromIndex
decl_stmt|,
name|toIndex
decl_stmt|;
name|fromIndex
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|BitSet
name|aa
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|aa
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|OpenBitSet
name|bb
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bb
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|,
name|mode
argument_list|)
expr_stmt|;
comment|// a problem here is from flip or doIterate
name|fromIndex
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from clear() or nextSetBit
name|doNextSetBitLong
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|doPrevSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|doPrevSetBitLong
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|fromIndex
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from set() or nextSetBit
name|doNextSetBitLong
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|doPrevSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|doPrevSetBitLong
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
if|if
condition|(
name|a0
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|equals
argument_list|(
name|a0
argument_list|)
argument_list|,
name|b
operator|.
name|equals
argument_list|(
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|BitSet
name|a_and
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_or
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_xor
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_xor
operator|.
name|xor
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_andn
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_and
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b_and
argument_list|)
expr_stmt|;
name|b_and
operator|.
name|and
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_or
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_or
operator|.
name|or
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_xor
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_xor
operator|.
name|xor
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_andn
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_andn
operator|.
name|andNot
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_and
argument_list|,
name|b_and
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_or
argument_list|,
name|b_or
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_xor
argument_list|,
name|b_xor
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_andn
argument_list|,
name|b_andn
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_and
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_xor
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_xor
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_andn
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
comment|// test non-mutating popcounts
name|assertEquals
argument_list|(
name|b_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|intersectionCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|unionCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_xor
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|xorCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|andNotCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|a0
operator|=
name|a
expr_stmt|;
name|b0
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|// large enough to flush obvious bugs, small enough to run in<.5 sec as part of a
comment|// larger testsuite.
DECL|method|testSmall
specifier|public
name|void
name|testSmall
parameter_list|()
block|{
name|doRandomSets
argument_list|(
name|atLeast
argument_list|(
literal|1200
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doRandomSets
argument_list|(
name|atLeast
argument_list|(
literal|1200
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// uncomment to run a bigger test (~2 minutes).
comment|/*   public void testBig() {     doRandomSets(2000,200000, 1);     doRandomSets(2000,200000, 2);   }   */
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|OpenBitSet
name|b1
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|1111
argument_list|)
decl_stmt|;
name|OpenBitSet
name|b2
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|2222
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b1
operator|.
name|set
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
literal|2221
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b1
operator|.
name|set
argument_list|(
literal|2221
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
comment|// try different type of object
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCodeEquals
specifier|public
name|void
name|testHashCodeEquals
parameter_list|()
block|{
name|OpenBitSet
name|bs1
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|OpenBitSet
name|bs2
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|64
argument_list|)
decl_stmt|;
name|bs1
operator|.
name|set
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|bs2
operator|.
name|set
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bs1
argument_list|,
name|bs2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bs1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|bs2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeOpenBitSet
specifier|private
name|OpenBitSet
name|makeOpenBitSet
parameter_list|(
name|int
index|[]
name|a
parameter_list|)
block|{
name|OpenBitSet
name|bs
init|=
operator|new
name|OpenBitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|e
range|:
name|a
control|)
block|{
name|bs
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|makeBitSet
specifier|private
name|BitSet
name|makeBitSet
parameter_list|(
name|int
index|[]
name|a
parameter_list|)
block|{
name|BitSet
name|bs
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|e
range|:
name|a
control|)
block|{
name|bs
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|checkPrevSetBitArray
specifier|private
name|void
name|checkPrevSetBitArray
parameter_list|(
name|int
index|[]
name|a
parameter_list|)
block|{
name|OpenBitSet
name|obs
init|=
name|makeOpenBitSet
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|BitSet
name|bs
init|=
name|makeBitSet
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|doPrevSetBit
argument_list|(
name|bs
argument_list|,
name|obs
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrevSetBit
specifier|public
name|void
name|testPrevSetBit
parameter_list|()
block|{
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

