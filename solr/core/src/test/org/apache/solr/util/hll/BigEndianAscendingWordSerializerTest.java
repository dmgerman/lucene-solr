begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link BigEndianAscendingWordSerializer}.  */
end_comment

begin_class
DECL|class|BigEndianAscendingWordSerializerTest
specifier|public
class|class
name|BigEndianAscendingWordSerializerTest
extends|extends
name|LuceneTestCase
block|{
comment|/**      * Error checking tests for constructor.      */
annotation|@
name|Test
DECL|method|constructorErrorTest
specifier|public
name|void
name|constructorErrorTest
parameter_list|()
block|{
comment|// word length too small
try|try
block|{
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|0
comment|/*wordLength, below minimum of 1*/
argument_list|,
literal|1
comment|/*wordCount, arbitrary*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should complain about too-short words."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Word length must be"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// word length too large
try|try
block|{
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|65
comment|/*wordLength, above max of 64*/
argument_list|,
literal|1
comment|/*wordCount, arbitrary*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should complain about too-long words."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Word length must be"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// word count negative
try|try
block|{
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|5
comment|/*wordLength, arbitrary*/
argument_list|,
operator|-
literal|1
comment|/*wordCount, too small*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should complain about negative word count."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Word count must be"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// byte padding negative
try|try
block|{
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|5
comment|/*wordLength, arbitrary*/
argument_list|,
literal|1
comment|/*wordCount, arbitrary*/
argument_list|,
operator|-
literal|1
comment|/*bytePadding, too small*/
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should complain about negative byte padding."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Byte padding must be"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests runtime exception thrown at premature call to {@link BigEndianAscendingWordSerializer#getBytes()}.      */
annotation|@
name|Test
DECL|method|earlyGetBytesTest
specifier|public
name|void
name|earlyGetBytesTest
parameter_list|()
block|{
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|5
comment|/*wordLength, arbitrary*/
argument_list|,
literal|1
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|)
decl_stmt|;
comment|// getBytes without enough writeWord should throw
try|try
block|{
name|serializer
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Not all words"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      */
annotation|@
name|Test
DECL|method|smokeTestExplicitParams
specifier|public
name|void
name|smokeTestExplicitParams
parameter_list|()
block|{
specifier|final
name|int
name|shortWordLength
init|=
literal|64
comment|/*longs used in LongSetSlab*/
decl_stmt|;
block|{
comment|// Should work on an empty sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|0
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|serializer
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
operator|)
assert|;
block|}
block|{
comment|// Should work on a byte-divisible sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|2
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|0xBAAAAAAAAAAAAAACL
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|0x8FFFFFFFFFFFFFF1L
argument_list|)
expr_stmt|;
comment|// Bytes:
comment|// ======
comment|// 0xBA 0xAA 0xAA 0xAA 0xAA 0xAA 0xAA 0xAC
comment|// 0x8F 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xF1
comment|//
comment|// -70 -86 ...                        -84
comment|// -113 -1 ...                        -15
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|70
block|,
operator|-
literal|86
block|,
operator|-
literal|86
block|,
operator|-
literal|86
block|,
operator|-
literal|86
block|,
operator|-
literal|86
block|,
operator|-
literal|86
block|,
operator|-
literal|84
block|,
operator|-
literal|113
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|15
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should pad the array correctly.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|1
comment|/*wordCount*/
argument_list|,
literal|1
comment|/*bytePadding*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 1 byte leading padding | value 1 | trailing padding
comment|// 0000 0000 | 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0001
comment|// 0x00 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x01
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Smoke test for typical parameters used in practice.      */
annotation|@
name|Test
DECL|method|smokeTestProbabilisticParams
specifier|public
name|void
name|smokeTestProbabilisticParams
parameter_list|()
block|{
comment|// XXX: revisit this
specifier|final
name|int
name|shortWordLength
init|=
literal|5
decl_stmt|;
block|{
comment|// Should work on an empty sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|0
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|serializer
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
operator|)
assert|;
block|}
block|{
comment|// Should work on a non-byte-divisible sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|3
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|31
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// The values:
comment|// -----------
comment|// 9     |31    |1     |padding
comment|// Corresponding bits:
comment|// ------------------
comment|// 0100 1|111 11|00 001|0
comment|// And the hex/decimal (remember Java bytes are signed):
comment|// -----------------------------------------------------
comment|// 0100 1111 -> 0x4F -> 79
comment|// 1100 0010 -> 0xC2 -> -62
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|79
block|,
operator|-
literal|62
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should work on a byte-divisible sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|8
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|9
condition|;
name|i
operator|++
control|)
block|{
name|serializer
operator|.
name|writeWord
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Values: 1-8
comment|// Corresponding bits:
comment|// ------------------
comment|// 00001
comment|// 00010
comment|// 00011
comment|// 00100
comment|// 00101
comment|// 00110
comment|// 00111
comment|// 01000
comment|// And the hex:
comment|// ------------
comment|// 0000 1000 => 0x08 => 8
comment|// 1000 0110 => 0x86 => -122
comment|// 0100 0010 => 0x62 => 66
comment|// 1001 1000 => 0x98 => -104
comment|// 1110 1000 => 0xE8 => -24
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|8
block|,
operator|-
literal|122
block|,
literal|66
block|,
operator|-
literal|104
block|,
operator|-
literal|24
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should pad the array correctly.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|1
comment|/*wordCount*/
argument_list|,
literal|1
comment|/*bytePadding*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 1 byte leading padding | value 1 | trailing padding
comment|// 0000 0000 | 0000 1|000
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|8
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Smoke test for typical parameters used in practice.      */
annotation|@
name|Test
DECL|method|smokeTestSparseParams
specifier|public
name|void
name|smokeTestSparseParams
parameter_list|()
block|{
comment|// XXX: revisit
specifier|final
name|int
name|shortWordLength
init|=
literal|17
decl_stmt|;
block|{
comment|// Should work on an empty sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|0
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|serializer
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
operator|)
assert|;
block|}
block|{
comment|// Should work on a non-byte-divisible sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|3
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|42
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|75
argument_list|)
expr_stmt|;
comment|// The values:
comment|// -----------
comment|// 9                    |42                   |75                   |padding
comment|// Corresponding bits:
comment|// ------------------
comment|// 0000 0000 0000 0100 1|000 0000 0000 1010 10|00 0000 0000 1001 011|0 0000
comment|// And the hex/decimal (remember Java bytes are signed):
comment|// -----------------------------------------------------
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0000 0100 -> 0x04 -> 4
comment|// 1000 0000 -> 0x80 -> -128
comment|// 0000 1010 -> 0x0A -> 10
comment|// 1000 0000 -> 0x80 -> -128
comment|// 0000 1001 -> 0x09 -> 9
comment|// 0110 0000 -> 0x60 -> 96
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|4
block|,
operator|-
literal|128
block|,
literal|10
block|,
operator|-
literal|128
block|,
literal|9
block|,
literal|96
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should work on a byte-divisible sequence, with no padding.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|8
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, none*/
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|9
condition|;
name|i
operator|++
control|)
block|{
name|serializer
operator|.
name|writeWord
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Values: 1-8
comment|// Corresponding bits:
comment|// ------------------
comment|// 0000 0000 0000 0000 1
comment|// 000 0000 0000 0000 10
comment|// 00 0000 0000 0000 011
comment|// 0 0000 0000 0000 0100
comment|// 0000 0000 0000 0010 1
comment|// 000 0000 0000 0001 10
comment|// 00 0000 0000 0000 111
comment|// 0 0000 0000 0000 1000
comment|// And the hex:
comment|// ------------
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0000 0000 -> 0x00 -> 0
comment|// 1000 0000 -> 0x80 -> -128
comment|// 0000 0000 -> 0x00 -> 0
comment|// 1000 0000 -> 0x80 -> -128
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0110 0000 -> 0x60 -> 96
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0100 0000 -> 0x40 -> 64
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0010 1000 -> 0x28 -> 40
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0001 1000 -> 0x18 -> 24
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0000 1110 -> 0x0D -> 14
comment|// 0000 0000 -> 0x00 -> 0
comment|// 0000 1000 -> 0x08 -> 8
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
operator|-
literal|128
block|,
literal|0
block|,
operator|-
literal|128
block|,
literal|0
block|,
literal|96
block|,
literal|0
block|,
literal|64
block|,
literal|0
block|,
literal|40
block|,
literal|0
block|,
literal|24
block|,
literal|0
block|,
literal|14
block|,
literal|0
block|,
literal|8
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should pad the array correctly.
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|shortWordLength
argument_list|,
literal|1
comment|/*wordCount*/
argument_list|,
literal|1
comment|/*bytePadding*/
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 1 byte leading padding | value 1 | trailing padding
comment|// 0000 0000 | 0000 0000 0000 0000 1|000 0000
comment|// 0x00 0x00 0x00 0x80
specifier|final
name|byte
index|[]
name|bytes
init|=
name|serializer
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|expectedBytes
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
operator|-
literal|128
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
name|expectedBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

