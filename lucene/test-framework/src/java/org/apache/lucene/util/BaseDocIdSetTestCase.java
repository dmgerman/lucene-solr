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
name|io
operator|.
name|IOException
import|;
end_import

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

begin_comment
comment|/** Base test class for {@link DocIdSet}s. */
end_comment

begin_class
DECL|class|BaseDocIdSetTestCase
specifier|public
specifier|abstract
class|class
name|BaseDocIdSetTestCase
parameter_list|<
name|T
extends|extends
name|DocIdSet
parameter_list|>
extends|extends
name|LuceneTestCase
block|{
comment|/** Create a copy of the given {@link BitSet} which has<code>length</code> bits. */
DECL|method|copyOf
specifier|public
specifier|abstract
name|T
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Create a random set which has<code>numBitsSet</code> of its<code>numBits</code> bits set. */
DECL|method|randomSet
specifier|protected
specifier|static
name|BitSet
name|randomSet
parameter_list|(
name|int
name|numBits
parameter_list|,
name|int
name|numBitsSet
parameter_list|)
block|{
assert|assert
name|numBitsSet
operator|<=
name|numBits
assert|;
specifier|final
name|BitSet
name|set
init|=
operator|new
name|BitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
name|numBitsSet
operator|==
name|numBits
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
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
name|numBitsSet
condition|;
operator|++
name|i
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|o
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|get
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
name|o
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|set
return|;
block|}
comment|/** Same as {@link #randomSet(int, int)} but given a load factor. */
DECL|method|randomSet
specifier|protected
specifier|static
name|BitSet
name|randomSet
parameter_list|(
name|int
name|numBits
parameter_list|,
name|float
name|percentSet
parameter_list|)
block|{
return|return
name|randomSet
argument_list|(
name|numBits
argument_list|,
call|(
name|int
call|)
argument_list|(
name|percentSet
operator|*
name|numBits
argument_list|)
argument_list|)
return|;
block|}
comment|/** Test length=0. */
DECL|method|testNoBit
specifier|public
name|void
name|testNoBit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BitSet
name|bs
init|=
operator|new
name|BitSet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|T
name|copy
init|=
name|copyOf
argument_list|(
name|bs
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bs
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
comment|/** Test length=1. */
DECL|method|test1Bit
specifier|public
name|void
name|test1Bit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BitSet
name|bs
init|=
operator|new
name|BitSet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|bs
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|final
name|T
name|copy
init|=
name|copyOf
argument_list|(
name|bs
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bs
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
comment|/** Test length=2. */
DECL|method|test2Bits
specifier|public
name|void
name|test2Bits
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BitSet
name|bs
init|=
operator|new
name|BitSet
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|bs
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|bs
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|T
name|copy
init|=
name|copyOf
argument_list|(
name|bs
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bs
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
comment|/** Compare the content of the set against a {@link BitSet}. */
DECL|method|testAgainstBitSet
specifier|public
name|void
name|testAgainstBitSet
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|1
operator|<<
literal|20
argument_list|)
decl_stmt|;
comment|// test various random sets with various load factors
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0f
block|,
literal|0.0001f
block|,
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|/
literal|2
block|,
literal|0.9f
block|,
literal|1f
block|}
control|)
block|{
specifier|final
name|BitSet
name|set
init|=
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
decl_stmt|;
specifier|final
name|T
name|copy
init|=
name|copyOf
argument_list|(
name|set
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|set
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
comment|// test one doc
name|BitSet
name|set
init|=
operator|new
name|BitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|set
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// 0 first
name|T
name|copy
init|=
name|copyOf
argument_list|(
name|set
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|set
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|set
operator|.
name|clear
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|set
operator|.
name|set
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
argument_list|)
expr_stmt|;
name|copy
operator|=
name|copyOf
argument_list|(
name|set
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
comment|// then random index
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|set
argument_list|,
name|copy
argument_list|)
expr_stmt|;
comment|// test regular increments
for|for
control|(
name|int
name|inc
init|=
literal|2
init|;
name|inc
operator|<
literal|1000
condition|;
name|inc
operator|+=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
control|)
block|{
name|set
operator|=
operator|new
name|BitSet
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
init|;
name|d
operator|<
name|numBits
condition|;
name|d
operator|+=
name|inc
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|copy
operator|=
name|copyOf
argument_list|(
name|set
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|set
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Assert that the content of the {@link DocIdSet} is the same as the content of the {@link BitSet}. */
DECL|method|assertEquals
specifier|public
name|void
name|assertEquals
parameter_list|(
name|int
name|numBits
parameter_list|,
name|BitSet
name|ds1
parameter_list|,
name|T
name|ds2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nextDoc
name|DocIdSetIterator
name|it2
init|=
name|ds2
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it2
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|ds1
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|it2
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|ds1
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|doc
operator|!=
operator|-
literal|1
condition|;
name|doc
operator|=
name|ds1
operator|.
name|nextSetBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it2
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// nextDoc / advance
name|it2
operator|=
name|ds2
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|it2
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|ds1
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|doc
init|=
operator|-
literal|1
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|doc
operator|=
name|ds1
operator|.
name|nextSetBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
operator|-
literal|1
condition|)
block|{
name|doc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|target
init|=
name|doc
operator|+
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|64
else|:
name|Math
operator|.
name|max
argument_list|(
name|numBits
operator|/
literal|8
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|=
name|ds1
operator|.
name|nextSetBit
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
operator|-
literal|1
condition|)
block|{
name|doc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|advance
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// bits()
specifier|final
name|Bits
name|bits
init|=
name|ds2
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
comment|// test consistency between bits and iterator
name|it2
operator|=
name|ds2
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|previousDoc
init|=
operator|-
literal|1
init|,
name|doc
init|=
name|it2
operator|.
name|nextDoc
argument_list|()
init|;
condition|;
name|previousDoc
operator|=
name|doc
operator|,
name|doc
operator|=
name|it2
operator|.
name|nextDoc
argument_list|()
control|)
block|{
specifier|final
name|int
name|max
init|=
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|?
name|bits
operator|.
name|length
argument_list|()
else|:
name|doc
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|previousDoc
operator|+
literal|1
init|;
name|i
operator|<
name|max
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|bits
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
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

