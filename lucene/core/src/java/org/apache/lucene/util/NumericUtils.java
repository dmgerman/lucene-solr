begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|math
operator|.
name|BigInteger
import|;
end_import

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
comment|/**  * Helper APIs to encode numeric values as sortable bytes and vice-versa.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|NumericUtils
specifier|public
specifier|final
class|class
name|NumericUtils
block|{
DECL|method|NumericUtils
specifier|private
name|NumericUtils
parameter_list|()
block|{}
comment|// no instance!
comment|/**    * Converts a<code>double</code> value to a sortable signed<code>long</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;double format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as long.    * By this the precision is not reduced, but the value can easily used as a long.    * The sort order (including {@link Double#NaN}) is defined by    * {@link Double#compareTo}; {@code NaN} is greater than positive infinity.    * @see #sortableLongToDouble    */
DECL|method|doubleToSortableLong
specifier|public
specifier|static
name|long
name|doubleToSortableLong
parameter_list|(
name|double
name|val
parameter_list|)
block|{
return|return
name|sortableDoubleBits
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a sortable<code>long</code> back to a<code>double</code>.    * @see #doubleToSortableLong    */
DECL|method|sortableLongToDouble
specifier|public
specifier|static
name|double
name|sortableLongToDouble
parameter_list|(
name|long
name|val
parameter_list|)
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|sortableDoubleBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a<code>float</code> value to a sortable signed<code>int</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;float format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as int.    * By this the precision is not reduced, but the value can easily used as an int.    * The sort order (including {@link Float#NaN}) is defined by    * {@link Float#compareTo}; {@code NaN} is greater than positive infinity.    * @see #sortableIntToFloat    */
DECL|method|floatToSortableInt
specifier|public
specifier|static
name|int
name|floatToSortableInt
parameter_list|(
name|float
name|val
parameter_list|)
block|{
return|return
name|sortableFloatBits
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a sortable<code>int</code> back to a<code>float</code>.    * @see #floatToSortableInt    */
DECL|method|sortableIntToFloat
specifier|public
specifier|static
name|float
name|sortableIntToFloat
parameter_list|(
name|int
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|sortableFloatBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts IEEE 754 representation of a double to sortable order (or back to the original) */
DECL|method|sortableDoubleBits
specifier|public
specifier|static
name|long
name|sortableDoubleBits
parameter_list|(
name|long
name|bits
parameter_list|)
block|{
return|return
name|bits
operator|^
operator|(
name|bits
operator|>>
literal|63
operator|)
operator|&
literal|0x7fffffffffffffffL
return|;
block|}
comment|/** Converts IEEE 754 representation of a float to sortable order (or back to the original) */
DECL|method|sortableFloatBits
specifier|public
specifier|static
name|int
name|sortableFloatBits
parameter_list|(
name|int
name|bits
parameter_list|)
block|{
return|return
name|bits
operator|^
operator|(
name|bits
operator|>>
literal|31
operator|)
operator|&
literal|0x7fffffff
return|;
block|}
comment|/** Result = a - b, where a&gt;= b, else {@code IllegalArgumentException} is thrown.  */
DECL|method|subtract
specifier|public
specifier|static
name|void
name|subtract
parameter_list|(
name|int
name|bytesPerDim
parameter_list|,
name|int
name|dim
parameter_list|,
name|byte
index|[]
name|a
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|byte
index|[]
name|result
parameter_list|)
block|{
name|int
name|start
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|bytesPerDim
decl_stmt|;
name|int
name|borrow
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
block|{
name|int
name|diff
init|=
operator|(
name|a
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
operator|(
name|b
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
name|borrow
decl_stmt|;
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
name|diff
operator|+=
literal|256
expr_stmt|;
name|borrow
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|borrow
operator|=
literal|0
expr_stmt|;
block|}
name|result
index|[
name|i
operator|-
name|start
index|]
operator|=
operator|(
name|byte
operator|)
name|diff
expr_stmt|;
block|}
if|if
condition|(
name|borrow
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"a< b"
argument_list|)
throw|;
block|}
block|}
comment|/** Result = a + b, where a and b are unsigned.  If there is an overflow, {@code IllegalArgumentException} is thrown. */
DECL|method|add
specifier|public
specifier|static
name|void
name|add
parameter_list|(
name|int
name|bytesPerDim
parameter_list|,
name|int
name|dim
parameter_list|,
name|byte
index|[]
name|a
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|byte
index|[]
name|result
parameter_list|)
block|{
name|int
name|start
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|bytesPerDim
decl_stmt|;
name|int
name|carry
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
block|{
name|int
name|digitSum
init|=
operator|(
name|a
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|+
operator|(
name|b
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|+
name|carry
decl_stmt|;
if|if
condition|(
name|digitSum
operator|>
literal|255
condition|)
block|{
name|digitSum
operator|-=
literal|256
expr_stmt|;
name|carry
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|carry
operator|=
literal|0
expr_stmt|;
block|}
name|result
index|[
name|i
operator|-
name|start
index|]
operator|=
operator|(
name|byte
operator|)
name|digitSum
expr_stmt|;
block|}
if|if
condition|(
name|carry
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"a + b overflows bytesPerDim="
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
block|}
comment|/** Returns positive int if a&gt; b, negative int if a&lt; b and 0 if a == b */
DECL|method|compare
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|int
name|bytesPerDim
parameter_list|,
name|byte
index|[]
name|a
parameter_list|,
name|int
name|aIndex
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|bIndex
parameter_list|)
block|{
assert|assert
name|aIndex
operator|>=
literal|0
assert|;
assert|assert
name|bIndex
operator|>=
literal|0
assert|;
name|int
name|aOffset
init|=
name|aIndex
operator|*
name|bytesPerDim
decl_stmt|;
name|int
name|bOffset
init|=
name|bIndex
operator|*
name|bytesPerDim
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
name|bytesPerDim
condition|;
name|i
operator|++
control|)
block|{
name|int
name|cmp
init|=
operator|(
name|a
index|[
name|aOffset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
operator|(
name|b
index|[
name|bOffset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/** Returns true if N-dim rect A contains N-dim rect B */
DECL|method|contains
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|int
name|bytesPerDim
parameter_list|,
name|byte
index|[]
name|minPackedA
parameter_list|,
name|byte
index|[]
name|maxPackedA
parameter_list|,
name|byte
index|[]
name|minPackedB
parameter_list|,
name|byte
index|[]
name|maxPackedB
parameter_list|)
block|{
name|int
name|dims
init|=
name|minPackedA
operator|.
name|length
operator|/
name|bytesPerDim
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|dims
condition|;
name|dim
operator|++
control|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedA
argument_list|,
name|dim
argument_list|,
name|minPackedB
argument_list|,
name|dim
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|maxPackedA
argument_list|,
name|dim
argument_list|,
name|maxPackedB
argument_list|,
name|dim
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|intToBytes
specifier|public
specifier|static
name|void
name|intToBytes
parameter_list|(
name|int
name|x
parameter_list|,
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// Flip the sign bit, so negative ints sort before positive ints correctly:
name|x
operator|^=
literal|0x80000000
expr_stmt|;
name|intToBytesDirect
argument_list|(
name|x
argument_list|,
name|dest
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
DECL|method|intToBytesDirect
specifier|public
specifier|static
name|void
name|intToBytesDirect
parameter_list|(
name|int
name|x
parameter_list|,
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|offset
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|dest
index|[
name|offset
operator|+
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|24
operator|-
name|i
operator|*
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bytesToInt
specifier|public
specifier|static
name|int
name|bytesToInt
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|int
name|x
init|=
name|bytesToIntDirect
argument_list|(
name|src
argument_list|,
name|index
argument_list|)
decl_stmt|;
comment|// Re-flip the sign bit to restore the original value:
return|return
name|x
operator|^
literal|0x80000000
return|;
block|}
DECL|method|bytesToIntDirect
specifier|public
specifier|static
name|int
name|bytesToIntDirect
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|x
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|x
operator||=
operator|(
name|src
index|[
name|offset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
operator|<<
operator|(
literal|24
operator|-
name|i
operator|*
literal|8
operator|)
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
DECL|method|longToBytes
specifier|public
specifier|static
name|void
name|longToBytes
parameter_list|(
name|long
name|v
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// Flip the sign bit so negative longs sort before positive longs:
name|v
operator|^=
literal|0x8000000000000000L
expr_stmt|;
name|longToBytesDirect
argument_list|(
name|v
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
DECL|method|longToBytesDirect
specifier|public
specifier|static
name|void
name|longToBytesDirect
parameter_list|(
name|long
name|v
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|bytes
index|[
name|offset
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|56
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|48
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|40
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|32
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
name|offset
operator|+
literal|7
index|]
operator|=
operator|(
name|byte
operator|)
name|v
expr_stmt|;
block|}
DECL|method|bytesToLong
specifier|public
specifier|static
name|long
name|bytesToLong
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|long
name|v
init|=
name|bytesToLongDirect
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
decl_stmt|;
comment|// Flip the sign bit back
name|v
operator|^=
literal|0x8000000000000000L
expr_stmt|;
return|return
name|v
return|;
block|}
DECL|method|bytesToLongDirect
specifier|public
specifier|static
name|long
name|bytesToLongDirect
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|long
name|v
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|56
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|4
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|5
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|6
index|]
operator|&
literal|0xffL
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|7
index|]
operator|&
literal|0xffL
operator|)
decl_stmt|;
return|return
name|v
return|;
block|}
DECL|method|sortableBigIntBytes
specifier|public
specifier|static
name|void
name|sortableBigIntBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
comment|// Flip the sign bit so negative bigints sort before positive bigints:
name|bytes
index|[
literal|0
index|]
operator|^=
literal|0x80
expr_stmt|;
block|}
DECL|method|bigIntToBytes
specifier|public
specifier|static
name|void
name|bigIntToBytes
parameter_list|(
name|BigInteger
name|bigInt
parameter_list|,
name|int
name|bigIntSize
parameter_list|,
name|byte
index|[]
name|result
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|byte
index|[]
name|bigIntBytes
init|=
name|bigInt
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fullBigIntBytes
decl_stmt|;
if|if
condition|(
name|bigIntBytes
operator|.
name|length
operator|<
name|bigIntSize
condition|)
block|{
name|fullBigIntBytes
operator|=
operator|new
name|byte
index|[
name|bigIntSize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bigIntBytes
argument_list|,
literal|0
argument_list|,
name|fullBigIntBytes
argument_list|,
name|bigIntSize
operator|-
name|bigIntBytes
operator|.
name|length
argument_list|,
name|bigIntBytes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|bigIntBytes
index|[
literal|0
index|]
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// sign extend
name|Arrays
operator|.
name|fill
argument_list|(
name|fullBigIntBytes
argument_list|,
literal|0
argument_list|,
name|bigIntSize
operator|-
name|bigIntBytes
operator|.
name|length
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|bigIntBytes
operator|.
name|length
operator|==
name|bigIntSize
condition|)
block|{
name|fullBigIntBytes
operator|=
name|bigIntBytes
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"BigInteger: "
operator|+
name|bigInt
operator|+
literal|" requires more than "
operator|+
name|bigIntSize
operator|+
literal|" bytes storage"
argument_list|)
throw|;
block|}
name|sortableBigIntBytes
argument_list|(
name|fullBigIntBytes
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fullBigIntBytes
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|offset
argument_list|,
name|bigIntSize
argument_list|)
expr_stmt|;
assert|assert
name|bytesToBigInt
argument_list|(
name|result
argument_list|,
name|offset
argument_list|,
name|bigIntSize
argument_list|)
operator|.
name|equals
argument_list|(
name|bigInt
argument_list|)
operator|:
literal|"bigInt="
operator|+
name|bigInt
operator|+
literal|" converted="
operator|+
name|bytesToBigInt
argument_list|(
name|result
argument_list|,
name|offset
argument_list|,
name|bigIntSize
argument_list|)
assert|;
block|}
DECL|method|bytesToBigInt
specifier|public
specifier|static
name|BigInteger
name|bytesToBigInt
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|byte
index|[]
name|bigIntBytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|bigIntBytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|sortableBigIntBytes
argument_list|(
name|bigIntBytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|BigInteger
argument_list|(
name|bigIntBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

