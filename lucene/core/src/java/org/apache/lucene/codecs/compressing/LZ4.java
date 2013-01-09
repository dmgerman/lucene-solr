begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|Arrays
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * LZ4 compression and decompression routines.  *  * http://code.google.com/p/lz4/  * http://fastcompression.blogspot.fr/p/lz4.html  */
end_comment

begin_class
DECL|class|LZ4
class|class
name|LZ4
block|{
DECL|method|LZ4
specifier|private
name|LZ4
parameter_list|()
block|{}
DECL|field|MEMORY_USAGE
specifier|static
specifier|final
name|int
name|MEMORY_USAGE
init|=
literal|14
decl_stmt|;
DECL|field|MIN_MATCH
specifier|static
specifier|final
name|int
name|MIN_MATCH
init|=
literal|4
decl_stmt|;
comment|// minimum length of a match
DECL|field|MAX_DISTANCE
specifier|static
specifier|final
name|int
name|MAX_DISTANCE
init|=
literal|1
operator|<<
literal|16
decl_stmt|;
comment|// maximum distance of a reference
DECL|field|LAST_LITERALS
specifier|static
specifier|final
name|int
name|LAST_LITERALS
init|=
literal|5
decl_stmt|;
comment|// the last 5 bytes must be encoded as literals
DECL|field|HASH_LOG_HC
specifier|static
specifier|final
name|int
name|HASH_LOG_HC
init|=
literal|15
decl_stmt|;
comment|// log size of the dictionary for compressHC
DECL|field|HASH_TABLE_SIZE_HC
specifier|static
specifier|final
name|int
name|HASH_TABLE_SIZE_HC
init|=
literal|1
operator|<<
name|HASH_LOG_HC
decl_stmt|;
DECL|field|OPTIMAL_ML
specifier|static
specifier|final
name|int
name|OPTIMAL_ML
init|=
literal|0x0F
operator|+
literal|4
operator|-
literal|1
decl_stmt|;
comment|// match length that doesn't require an additional byte
DECL|method|hash
specifier|private
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|hashBits
parameter_list|)
block|{
return|return
operator|(
name|i
operator|*
operator|-
literal|1640531535
operator|)
operator|>>>
operator|(
literal|32
operator|-
name|hashBits
operator|)
return|;
block|}
DECL|method|hashHC
specifier|private
specifier|static
name|int
name|hashHC
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|i
argument_list|,
name|HASH_LOG_HC
argument_list|)
return|;
block|}
DECL|method|readInt
specifier|private
specifier|static
name|int
name|readInt
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|buf
index|[
name|i
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|buf
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|buf
index|[
name|i
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|buf
index|[
name|i
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
DECL|method|readIntEquals
specifier|private
specifier|static
name|boolean
name|readIntEquals
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|readInt
argument_list|(
name|buf
argument_list|,
name|i
argument_list|)
operator|==
name|readInt
argument_list|(
name|buf
argument_list|,
name|j
argument_list|)
return|;
block|}
DECL|method|commonBytes
specifier|private
specifier|static
name|int
name|commonBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|o1
parameter_list|,
name|int
name|o2
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
assert|assert
name|o1
operator|<
name|o2
assert|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|o2
operator|<
name|limit
operator|&&
name|b
index|[
name|o1
operator|++
index|]
operator|==
name|b
index|[
name|o2
operator|++
index|]
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|commonBytesBackward
specifier|private
specifier|static
name|int
name|commonBytesBackward
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|o1
parameter_list|,
name|int
name|o2
parameter_list|,
name|int
name|l1
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|o1
operator|>
name|l1
operator|&&
name|o2
operator|>
name|l2
operator|&&
name|b
index|[
operator|--
name|o1
index|]
operator|==
name|b
index|[
operator|--
name|o2
index|]
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**    * Decompress at least<code>decompressedLen</code> bytes into    *<code>dest[dOff:]</code>. Please note that<code>dest</code> must be large    * enough to be able to hold<b>all</b> decompressed data (meaning that you    * need to know the total decompressed length).    */
DECL|method|decompress
specifier|public
specifier|static
name|int
name|decompress
parameter_list|(
name|DataInput
name|compressed
parameter_list|,
name|int
name|decompressedLen
parameter_list|,
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|dOff
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|destEnd
init|=
name|dest
operator|.
name|length
decl_stmt|;
do|do
block|{
comment|// literals
specifier|final
name|int
name|token
init|=
name|compressed
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
name|int
name|literalLen
init|=
name|token
operator|>>>
literal|4
decl_stmt|;
if|if
condition|(
name|literalLen
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|literalLen
operator|==
literal|0x0F
condition|)
block|{
name|byte
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|compressed
operator|.
name|readByte
argument_list|()
operator|)
operator|==
operator|(
name|byte
operator|)
literal|0xFF
condition|)
block|{
name|literalLen
operator|+=
literal|0xFF
expr_stmt|;
block|}
name|literalLen
operator|+=
name|len
operator|&
literal|0xFF
expr_stmt|;
block|}
name|compressed
operator|.
name|readBytes
argument_list|(
name|dest
argument_list|,
name|dOff
argument_list|,
name|literalLen
argument_list|)
expr_stmt|;
name|dOff
operator|+=
name|literalLen
expr_stmt|;
block|}
if|if
condition|(
name|dOff
operator|>=
name|decompressedLen
condition|)
block|{
break|break;
block|}
comment|// matchs
specifier|final
name|int
name|matchDec
init|=
operator|(
name|compressed
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator||
operator|(
operator|(
name|compressed
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
decl_stmt|;
assert|assert
name|matchDec
operator|>
literal|0
assert|;
name|int
name|matchLen
init|=
name|token
operator|&
literal|0x0F
decl_stmt|;
if|if
condition|(
name|matchLen
operator|==
literal|0x0F
condition|)
block|{
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|compressed
operator|.
name|readByte
argument_list|()
operator|)
operator|==
operator|(
name|byte
operator|)
literal|0xFF
condition|)
block|{
name|matchLen
operator|+=
literal|0xFF
expr_stmt|;
block|}
name|matchLen
operator|+=
name|len
operator|&
literal|0xFF
expr_stmt|;
block|}
name|matchLen
operator|+=
name|MIN_MATCH
expr_stmt|;
comment|// copying a multiple of 8 bytes can make decompression from 5% to 10% faster
specifier|final
name|int
name|fastLen
init|=
operator|(
name|matchLen
operator|+
literal|7
operator|)
operator|&
literal|0xFFFFFFF8
decl_stmt|;
if|if
condition|(
name|matchDec
argument_list|<
name|matchLen
operator|||
name|dOff
operator|+
name|fastLen
argument_list|>
name|destEnd
condition|)
block|{
comment|// overlap -> naive incremental copy
for|for
control|(
name|int
name|ref
init|=
name|dOff
operator|-
name|matchDec
init|,
name|end
init|=
name|dOff
operator|+
name|matchLen
init|;
name|dOff
operator|<
name|end
condition|;
operator|++
name|ref
operator|,
operator|++
name|dOff
control|)
block|{
name|dest
index|[
name|dOff
index|]
operator|=
name|dest
index|[
name|ref
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// no overlap -> arraycopy
name|System
operator|.
name|arraycopy
argument_list|(
name|dest
argument_list|,
name|dOff
operator|-
name|matchDec
argument_list|,
name|dest
argument_list|,
name|dOff
argument_list|,
name|fastLen
argument_list|)
expr_stmt|;
name|dOff
operator|+=
name|matchLen
expr_stmt|;
block|}
block|}
do|while
condition|(
name|dOff
operator|<
name|decompressedLen
condition|)
do|;
return|return
name|dOff
return|;
block|}
DECL|method|encodeLen
specifier|private
specifier|static
name|void
name|encodeLen
parameter_list|(
name|int
name|l
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|l
operator|>=
literal|0xFF
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0xFF
argument_list|)
expr_stmt|;
name|l
operator|-=
literal|0xFF
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|encodeLiterals
specifier|private
specifier|static
name|void
name|encodeLiterals
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|token
parameter_list|,
name|int
name|anchor
parameter_list|,
name|int
name|literalLen
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|token
argument_list|)
expr_stmt|;
comment|// encode literal length
if|if
condition|(
name|literalLen
operator|>=
literal|0x0F
condition|)
block|{
name|encodeLen
argument_list|(
name|literalLen
operator|-
literal|0x0F
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|// encode literals
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|anchor
argument_list|,
name|literalLen
argument_list|)
expr_stmt|;
block|}
DECL|method|encodeLastLiterals
specifier|private
specifier|static
name|void
name|encodeLastLiterals
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|anchor
parameter_list|,
name|int
name|literalLen
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|token
init|=
name|Math
operator|.
name|min
argument_list|(
name|literalLen
argument_list|,
literal|0x0F
argument_list|)
operator|<<
literal|4
decl_stmt|;
name|encodeLiterals
argument_list|(
name|bytes
argument_list|,
name|token
argument_list|,
name|anchor
argument_list|,
name|literalLen
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|encodeSequence
specifier|private
specifier|static
name|void
name|encodeSequence
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|anchor
parameter_list|,
name|int
name|matchRef
parameter_list|,
name|int
name|matchOff
parameter_list|,
name|int
name|matchLen
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|literalLen
init|=
name|matchOff
operator|-
name|anchor
decl_stmt|;
assert|assert
name|matchLen
operator|>=
literal|4
assert|;
comment|// encode token
specifier|final
name|int
name|token
init|=
operator|(
name|Math
operator|.
name|min
argument_list|(
name|literalLen
argument_list|,
literal|0x0F
argument_list|)
operator|<<
literal|4
operator|)
operator||
name|Math
operator|.
name|min
argument_list|(
name|matchLen
operator|-
literal|4
argument_list|,
literal|0x0F
argument_list|)
decl_stmt|;
name|encodeLiterals
argument_list|(
name|bytes
argument_list|,
name|token
argument_list|,
name|anchor
argument_list|,
name|literalLen
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// encode match dec
specifier|final
name|int
name|matchDec
init|=
name|matchOff
operator|-
name|matchRef
decl_stmt|;
assert|assert
name|matchDec
operator|>
literal|0
operator|&&
name|matchDec
operator|<
literal|1
operator|<<
literal|16
assert|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|matchDec
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|matchDec
operator|>>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|// encode match len
if|if
condition|(
name|matchLen
operator|>=
name|MIN_MATCH
operator|+
literal|0x0F
condition|)
block|{
name|encodeLen
argument_list|(
name|matchLen
operator|-
literal|0x0F
operator|-
name|MIN_MATCH
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compress<code>bytes[off:off+len]</code> into<code>out</code> using    * at most 16KB of memory.    */
DECL|method|compress
specifier|public
specifier|static
name|void
name|compress
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|base
init|=
name|off
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|off
operator|+
name|len
decl_stmt|;
name|int
name|anchor
init|=
name|off
operator|++
decl_stmt|;
if|if
condition|(
name|len
operator|>
name|LAST_LITERALS
operator|+
name|MIN_MATCH
condition|)
block|{
specifier|final
name|int
name|limit
init|=
name|end
operator|-
name|LAST_LITERALS
decl_stmt|;
specifier|final
name|int
name|matchLimit
init|=
name|limit
operator|-
name|MIN_MATCH
decl_stmt|;
specifier|final
name|int
name|bitsPerOffset
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|len
operator|-
name|LAST_LITERALS
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bitsPerOffsetLog
init|=
literal|32
operator|-
name|Integer
operator|.
name|numberOfLeadingZeros
argument_list|(
name|bitsPerOffset
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|hashLog
init|=
name|MEMORY_USAGE
operator|+
literal|3
operator|-
name|bitsPerOffsetLog
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Mutable
name|hashTable
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
literal|1
operator|<<
name|hashLog
argument_list|,
name|bitsPerOffset
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|main
label|:
while|while
condition|(
name|off
operator|<
name|limit
condition|)
block|{
comment|// find a match
name|int
name|ref
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|off
operator|>=
name|matchLimit
condition|)
block|{
break|break
name|main
break|;
block|}
specifier|final
name|int
name|v
init|=
name|readInt
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|hash
argument_list|(
name|v
argument_list|,
name|hashLog
argument_list|)
decl_stmt|;
name|ref
operator|=
name|base
operator|+
operator|(
name|int
operator|)
name|hashTable
operator|.
name|get
argument_list|(
name|h
argument_list|)
expr_stmt|;
assert|assert
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|off
operator|-
name|base
argument_list|)
operator|<=
name|hashTable
operator|.
name|getBitsPerValue
argument_list|()
assert|;
name|hashTable
operator|.
name|set
argument_list|(
name|h
argument_list|,
name|off
operator|-
name|base
argument_list|)
expr_stmt|;
if|if
condition|(
name|off
operator|-
name|ref
operator|<
name|MAX_DISTANCE
operator|&&
name|readInt
argument_list|(
name|bytes
argument_list|,
name|ref
argument_list|)
operator|==
name|v
condition|)
block|{
break|break;
block|}
operator|++
name|off
expr_stmt|;
block|}
comment|// compute match length
specifier|final
name|int
name|matchLen
init|=
name|MIN_MATCH
operator|+
name|commonBytes
argument_list|(
name|bytes
argument_list|,
name|ref
operator|+
name|MIN_MATCH
argument_list|,
name|off
operator|+
name|MIN_MATCH
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|encodeSequence
argument_list|(
name|bytes
argument_list|,
name|anchor
argument_list|,
name|ref
argument_list|,
name|off
argument_list|,
name|matchLen
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|off
operator|+=
name|matchLen
expr_stmt|;
name|anchor
operator|=
name|off
expr_stmt|;
block|}
block|}
comment|// last literals
specifier|final
name|int
name|literalLen
init|=
name|end
operator|-
name|anchor
decl_stmt|;
assert|assert
name|literalLen
operator|>=
name|LAST_LITERALS
operator|||
name|literalLen
operator|==
name|len
assert|;
name|encodeLastLiterals
argument_list|(
name|bytes
argument_list|,
name|anchor
argument_list|,
name|end
operator|-
name|anchor
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|class|Match
specifier|private
specifier|static
class|class
name|Match
block|{
DECL|field|start
DECL|field|ref
DECL|field|len
name|int
name|start
decl_stmt|,
name|ref
decl_stmt|,
name|len
decl_stmt|;
DECL|method|fix
name|void
name|fix
parameter_list|(
name|int
name|correction
parameter_list|)
block|{
name|start
operator|+=
name|correction
expr_stmt|;
name|ref
operator|+=
name|correction
expr_stmt|;
name|len
operator|-=
name|correction
expr_stmt|;
block|}
DECL|method|end
name|int
name|end
parameter_list|()
block|{
return|return
name|start
operator|+
name|len
return|;
block|}
block|}
DECL|method|copyTo
specifier|private
specifier|static
name|void
name|copyTo
parameter_list|(
name|Match
name|m1
parameter_list|,
name|Match
name|m2
parameter_list|)
block|{
name|m2
operator|.
name|len
operator|=
name|m1
operator|.
name|len
expr_stmt|;
name|m2
operator|.
name|start
operator|=
name|m1
operator|.
name|start
expr_stmt|;
name|m2
operator|.
name|ref
operator|=
name|m1
operator|.
name|ref
expr_stmt|;
block|}
DECL|class|HashTable
specifier|private
specifier|static
class|class
name|HashTable
block|{
DECL|field|MAX_ATTEMPTS
specifier|static
specifier|final
name|int
name|MAX_ATTEMPTS
init|=
literal|256
decl_stmt|;
DECL|field|MASK
specifier|static
specifier|final
name|int
name|MASK
init|=
name|MAX_DISTANCE
operator|-
literal|1
decl_stmt|;
DECL|field|nextToUpdate
name|int
name|nextToUpdate
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|int
name|base
decl_stmt|;
DECL|field|hashTable
specifier|private
specifier|final
name|int
index|[]
name|hashTable
decl_stmt|;
DECL|field|chainTable
specifier|private
specifier|final
name|short
index|[]
name|chainTable
decl_stmt|;
DECL|method|HashTable
name|HashTable
parameter_list|(
name|int
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|nextToUpdate
operator|=
name|base
expr_stmt|;
name|hashTable
operator|=
operator|new
name|int
index|[
name|HASH_TABLE_SIZE_HC
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|hashTable
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|chainTable
operator|=
operator|new
name|short
index|[
name|MAX_DISTANCE
index|]
expr_stmt|;
block|}
DECL|method|hashPointer
specifier|private
name|int
name|hashPointer
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|)
block|{
specifier|final
name|int
name|v
init|=
name|readInt
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|hashHC
argument_list|(
name|v
argument_list|)
decl_stmt|;
return|return
name|base
operator|+
name|hashTable
index|[
name|h
index|]
return|;
block|}
DECL|method|next
specifier|private
name|int
name|next
parameter_list|(
name|int
name|off
parameter_list|)
block|{
return|return
name|base
operator|+
name|off
operator|-
operator|(
name|chainTable
index|[
name|off
operator|&
name|MASK
index|]
operator|&
literal|0xFFFF
operator|)
return|;
block|}
DECL|method|addHash
specifier|private
name|void
name|addHash
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|)
block|{
specifier|final
name|int
name|v
init|=
name|readInt
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|hashHC
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|int
name|delta
init|=
name|off
operator|-
name|hashTable
index|[
name|h
index|]
decl_stmt|;
if|if
condition|(
name|delta
operator|>=
name|MAX_DISTANCE
condition|)
block|{
name|delta
operator|=
name|MAX_DISTANCE
operator|-
literal|1
expr_stmt|;
block|}
name|chainTable
index|[
name|off
operator|&
name|MASK
index|]
operator|=
operator|(
name|short
operator|)
name|delta
expr_stmt|;
name|hashTable
index|[
name|h
index|]
operator|=
name|off
operator|-
name|base
expr_stmt|;
block|}
DECL|method|insert
name|void
name|insert
parameter_list|(
name|int
name|off
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
for|for
control|(
init|;
name|nextToUpdate
operator|<
name|off
condition|;
operator|++
name|nextToUpdate
control|)
block|{
name|addHash
argument_list|(
name|bytes
argument_list|,
name|nextToUpdate
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insertAndFindBestMatch
name|boolean
name|insertAndFindBestMatch
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|matchLimit
parameter_list|,
name|Match
name|match
parameter_list|)
block|{
name|match
operator|.
name|start
operator|=
name|off
expr_stmt|;
name|match
operator|.
name|len
operator|=
literal|0
expr_stmt|;
name|insert
argument_list|(
name|off
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|int
name|ref
init|=
name|hashPointer
argument_list|(
name|buf
argument_list|,
name|off
argument_list|)
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
name|MAX_ATTEMPTS
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|ref
operator|<
name|Math
operator|.
name|max
argument_list|(
name|base
argument_list|,
name|off
operator|-
name|MAX_DISTANCE
operator|+
literal|1
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|buf
index|[
name|ref
operator|+
name|match
operator|.
name|len
index|]
operator|==
name|buf
index|[
name|off
operator|+
name|match
operator|.
name|len
index|]
operator|&&
name|readIntEquals
argument_list|(
name|buf
argument_list|,
name|ref
argument_list|,
name|off
argument_list|)
condition|)
block|{
specifier|final
name|int
name|matchLen
init|=
name|MIN_MATCH
operator|+
name|commonBytes
argument_list|(
name|buf
argument_list|,
name|ref
operator|+
name|MIN_MATCH
argument_list|,
name|off
operator|+
name|MIN_MATCH
argument_list|,
name|matchLimit
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchLen
operator|>
name|match
operator|.
name|len
condition|)
block|{
name|match
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|match
operator|.
name|len
operator|=
name|matchLen
expr_stmt|;
block|}
block|}
name|ref
operator|=
name|next
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
return|return
name|match
operator|.
name|len
operator|!=
literal|0
return|;
block|}
DECL|method|insertAndFindWiderMatch
name|boolean
name|insertAndFindWiderMatch
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|startLimit
parameter_list|,
name|int
name|matchLimit
parameter_list|,
name|int
name|minLen
parameter_list|,
name|Match
name|match
parameter_list|)
block|{
name|match
operator|.
name|len
operator|=
name|minLen
expr_stmt|;
name|insert
argument_list|(
name|off
argument_list|,
name|buf
argument_list|)
expr_stmt|;
specifier|final
name|int
name|delta
init|=
name|off
operator|-
name|startLimit
decl_stmt|;
name|int
name|ref
init|=
name|hashPointer
argument_list|(
name|buf
argument_list|,
name|off
argument_list|)
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
name|MAX_ATTEMPTS
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|ref
operator|<
name|Math
operator|.
name|max
argument_list|(
name|base
argument_list|,
name|off
operator|-
name|MAX_DISTANCE
operator|+
literal|1
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|buf
index|[
name|ref
operator|-
name|delta
operator|+
name|match
operator|.
name|len
index|]
operator|==
name|buf
index|[
name|startLimit
operator|+
name|match
operator|.
name|len
index|]
operator|&&
name|readIntEquals
argument_list|(
name|buf
argument_list|,
name|ref
argument_list|,
name|off
argument_list|)
condition|)
block|{
specifier|final
name|int
name|matchLenForward
init|=
name|MIN_MATCH
operator|+
name|commonBytes
argument_list|(
name|buf
argument_list|,
name|ref
operator|+
name|MIN_MATCH
argument_list|,
name|off
operator|+
name|MIN_MATCH
argument_list|,
name|matchLimit
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchLenBackward
init|=
name|commonBytesBackward
argument_list|(
name|buf
argument_list|,
name|ref
argument_list|,
name|off
argument_list|,
name|base
argument_list|,
name|startLimit
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchLen
init|=
name|matchLenBackward
operator|+
name|matchLenForward
decl_stmt|;
if|if
condition|(
name|matchLen
operator|>
name|match
operator|.
name|len
condition|)
block|{
name|match
operator|.
name|len
operator|=
name|matchLen
expr_stmt|;
name|match
operator|.
name|ref
operator|=
name|ref
operator|-
name|matchLenBackward
expr_stmt|;
name|match
operator|.
name|start
operator|=
name|off
operator|-
name|matchLenBackward
expr_stmt|;
block|}
block|}
name|ref
operator|=
name|next
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
return|return
name|match
operator|.
name|len
operator|>
name|minLen
return|;
block|}
block|}
comment|/**    * Compress<code>bytes[off:off+len]</code> into<code>out</code>. Compared to    * {@link LZ4#compress(byte[], int, int, DataOutput)}, this method is slower,    * uses more memory (~ 256KB), but should provide better compression ratios    * (especially on large inputs) because it chooses the best match among up to    * 256 candidates and then performs trade-offs to fix overlapping matches.    */
DECL|method|compressHC
specifier|public
specifier|static
name|void
name|compressHC
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|srcOff
parameter_list|,
name|int
name|srcLen
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|srcEnd
init|=
name|srcOff
operator|+
name|srcLen
decl_stmt|;
specifier|final
name|int
name|matchLimit
init|=
name|srcEnd
operator|-
name|LAST_LITERALS
decl_stmt|;
name|int
name|sOff
init|=
name|srcOff
decl_stmt|;
name|int
name|anchor
init|=
name|sOff
operator|++
decl_stmt|;
specifier|final
name|HashTable
name|ht
init|=
operator|new
name|HashTable
argument_list|(
name|srcOff
argument_list|)
decl_stmt|;
specifier|final
name|Match
name|match0
init|=
operator|new
name|Match
argument_list|()
decl_stmt|;
specifier|final
name|Match
name|match1
init|=
operator|new
name|Match
argument_list|()
decl_stmt|;
specifier|final
name|Match
name|match2
init|=
operator|new
name|Match
argument_list|()
decl_stmt|;
specifier|final
name|Match
name|match3
init|=
operator|new
name|Match
argument_list|()
decl_stmt|;
name|main
label|:
while|while
condition|(
name|sOff
operator|<
name|matchLimit
condition|)
block|{
if|if
condition|(
operator|!
name|ht
operator|.
name|insertAndFindBestMatch
argument_list|(
name|src
argument_list|,
name|sOff
argument_list|,
name|matchLimit
argument_list|,
name|match1
argument_list|)
condition|)
block|{
operator|++
name|sOff
expr_stmt|;
continue|continue;
block|}
comment|// saved, in case we would skip too much
name|copyTo
argument_list|(
name|match1
argument_list|,
name|match0
argument_list|)
expr_stmt|;
name|search2
label|:
while|while
condition|(
literal|true
condition|)
block|{
assert|assert
name|match1
operator|.
name|start
operator|>=
name|anchor
assert|;
if|if
condition|(
name|match1
operator|.
name|end
argument_list|()
operator|>=
name|matchLimit
operator|||
operator|!
name|ht
operator|.
name|insertAndFindWiderMatch
argument_list|(
name|src
argument_list|,
name|match1
operator|.
name|end
argument_list|()
operator|-
literal|2
argument_list|,
name|match1
operator|.
name|start
operator|+
literal|1
argument_list|,
name|matchLimit
argument_list|,
name|match1
operator|.
name|len
argument_list|,
name|match2
argument_list|)
condition|)
block|{
comment|// no better match
name|encodeSequence
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|match1
operator|.
name|ref
argument_list|,
name|match1
operator|.
name|start
argument_list|,
name|match1
operator|.
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|anchor
operator|=
name|sOff
operator|=
name|match1
operator|.
name|end
argument_list|()
expr_stmt|;
continue|continue
name|main
continue|;
block|}
if|if
condition|(
name|match0
operator|.
name|start
operator|<
name|match1
operator|.
name|start
condition|)
block|{
if|if
condition|(
name|match2
operator|.
name|start
operator|<
name|match1
operator|.
name|start
operator|+
name|match0
operator|.
name|len
condition|)
block|{
comment|// empirical
name|copyTo
argument_list|(
name|match0
argument_list|,
name|match1
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|match2
operator|.
name|start
operator|>
name|match1
operator|.
name|start
assert|;
if|if
condition|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|<
literal|3
condition|)
block|{
comment|// First Match too small : removed
name|copyTo
argument_list|(
name|match2
argument_list|,
name|match1
argument_list|)
expr_stmt|;
continue|continue
name|search2
continue|;
block|}
name|search3
label|:
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|<
name|OPTIMAL_ML
condition|)
block|{
name|int
name|newMatchLen
init|=
name|match1
operator|.
name|len
decl_stmt|;
if|if
condition|(
name|newMatchLen
operator|>
name|OPTIMAL_ML
condition|)
block|{
name|newMatchLen
operator|=
name|OPTIMAL_ML
expr_stmt|;
block|}
if|if
condition|(
name|match1
operator|.
name|start
operator|+
name|newMatchLen
operator|>
name|match2
operator|.
name|end
argument_list|()
operator|-
name|MIN_MATCH
condition|)
block|{
name|newMatchLen
operator|=
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|+
name|match2
operator|.
name|len
operator|-
name|MIN_MATCH
expr_stmt|;
block|}
specifier|final
name|int
name|correction
init|=
name|newMatchLen
operator|-
operator|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|)
decl_stmt|;
if|if
condition|(
name|correction
operator|>
literal|0
condition|)
block|{
name|match2
operator|.
name|fix
argument_list|(
name|correction
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|match2
operator|.
name|start
operator|+
name|match2
operator|.
name|len
operator|>=
name|matchLimit
operator|||
operator|!
name|ht
operator|.
name|insertAndFindWiderMatch
argument_list|(
name|src
argument_list|,
name|match2
operator|.
name|end
argument_list|()
operator|-
literal|3
argument_list|,
name|match2
operator|.
name|start
argument_list|,
name|matchLimit
argument_list|,
name|match2
operator|.
name|len
argument_list|,
name|match3
argument_list|)
condition|)
block|{
comment|// no better match -> 2 sequences to encode
if|if
condition|(
name|match2
operator|.
name|start
operator|<
name|match1
operator|.
name|end
argument_list|()
condition|)
block|{
if|if
condition|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|<
name|OPTIMAL_ML
condition|)
block|{
if|if
condition|(
name|match1
operator|.
name|len
operator|>
name|OPTIMAL_ML
condition|)
block|{
name|match1
operator|.
name|len
operator|=
name|OPTIMAL_ML
expr_stmt|;
block|}
if|if
condition|(
name|match1
operator|.
name|end
argument_list|()
operator|>
name|match2
operator|.
name|end
argument_list|()
operator|-
name|MIN_MATCH
condition|)
block|{
name|match1
operator|.
name|len
operator|=
name|match2
operator|.
name|end
argument_list|()
operator|-
name|match1
operator|.
name|start
operator|-
name|MIN_MATCH
expr_stmt|;
block|}
specifier|final
name|int
name|correction
init|=
name|match1
operator|.
name|len
operator|-
operator|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|)
decl_stmt|;
if|if
condition|(
name|correction
operator|>
literal|0
condition|)
block|{
name|match2
operator|.
name|fix
argument_list|(
name|correction
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|match1
operator|.
name|len
operator|=
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
expr_stmt|;
block|}
block|}
comment|// encode seq 1
name|encodeSequence
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|match1
operator|.
name|ref
argument_list|,
name|match1
operator|.
name|start
argument_list|,
name|match1
operator|.
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|anchor
operator|=
name|sOff
operator|=
name|match1
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// encode seq 2
name|encodeSequence
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|match2
operator|.
name|ref
argument_list|,
name|match2
operator|.
name|start
argument_list|,
name|match2
operator|.
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|anchor
operator|=
name|sOff
operator|=
name|match2
operator|.
name|end
argument_list|()
expr_stmt|;
continue|continue
name|main
continue|;
block|}
if|if
condition|(
name|match3
operator|.
name|start
operator|<
name|match1
operator|.
name|end
argument_list|()
operator|+
literal|3
condition|)
block|{
comment|// Not enough space for match 2 : remove it
if|if
condition|(
name|match3
operator|.
name|start
operator|>=
name|match1
operator|.
name|end
argument_list|()
condition|)
block|{
comment|// // can write Seq1 immediately ==> Seq2 is removed, so Seq3 becomes Seq1
if|if
condition|(
name|match2
operator|.
name|start
operator|<
name|match1
operator|.
name|end
argument_list|()
condition|)
block|{
specifier|final
name|int
name|correction
init|=
name|match1
operator|.
name|end
argument_list|()
operator|-
name|match2
operator|.
name|start
decl_stmt|;
name|match2
operator|.
name|fix
argument_list|(
name|correction
argument_list|)
expr_stmt|;
if|if
condition|(
name|match2
operator|.
name|len
operator|<
name|MIN_MATCH
condition|)
block|{
name|copyTo
argument_list|(
name|match3
argument_list|,
name|match2
argument_list|)
expr_stmt|;
block|}
block|}
name|encodeSequence
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|match1
operator|.
name|ref
argument_list|,
name|match1
operator|.
name|start
argument_list|,
name|match1
operator|.
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|anchor
operator|=
name|sOff
operator|=
name|match1
operator|.
name|end
argument_list|()
expr_stmt|;
name|copyTo
argument_list|(
name|match3
argument_list|,
name|match1
argument_list|)
expr_stmt|;
name|copyTo
argument_list|(
name|match2
argument_list|,
name|match0
argument_list|)
expr_stmt|;
continue|continue
name|search2
continue|;
block|}
name|copyTo
argument_list|(
name|match3
argument_list|,
name|match2
argument_list|)
expr_stmt|;
continue|continue
name|search3
continue|;
block|}
comment|// OK, now we have 3 ascending matches; let's write at least the first one
if|if
condition|(
name|match2
operator|.
name|start
operator|<
name|match1
operator|.
name|end
argument_list|()
condition|)
block|{
if|if
condition|(
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
operator|<
literal|0x0F
condition|)
block|{
if|if
condition|(
name|match1
operator|.
name|len
operator|>
name|OPTIMAL_ML
condition|)
block|{
name|match1
operator|.
name|len
operator|=
name|OPTIMAL_ML
expr_stmt|;
block|}
if|if
condition|(
name|match1
operator|.
name|end
argument_list|()
operator|>
name|match2
operator|.
name|end
argument_list|()
operator|-
name|MIN_MATCH
condition|)
block|{
name|match1
operator|.
name|len
operator|=
name|match2
operator|.
name|end
argument_list|()
operator|-
name|match1
operator|.
name|start
operator|-
name|MIN_MATCH
expr_stmt|;
block|}
specifier|final
name|int
name|correction
init|=
name|match1
operator|.
name|end
argument_list|()
operator|-
name|match2
operator|.
name|start
decl_stmt|;
name|match2
operator|.
name|fix
argument_list|(
name|correction
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|match1
operator|.
name|len
operator|=
name|match2
operator|.
name|start
operator|-
name|match1
operator|.
name|start
expr_stmt|;
block|}
block|}
name|encodeSequence
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|match1
operator|.
name|ref
argument_list|,
name|match1
operator|.
name|start
argument_list|,
name|match1
operator|.
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|anchor
operator|=
name|sOff
operator|=
name|match1
operator|.
name|end
argument_list|()
expr_stmt|;
name|copyTo
argument_list|(
name|match2
argument_list|,
name|match1
argument_list|)
expr_stmt|;
name|copyTo
argument_list|(
name|match3
argument_list|,
name|match2
argument_list|)
expr_stmt|;
continue|continue
name|search3
continue|;
block|}
block|}
block|}
name|encodeLastLiterals
argument_list|(
name|src
argument_list|,
name|anchor
argument_list|,
name|srcEnd
operator|-
name|anchor
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

