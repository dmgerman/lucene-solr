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
name|Random
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

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit and smoke tests for {@link BigEndianAscendingWordDeserializer}.  */
end_comment

begin_class
DECL|class|BigEndianAscendingWordDeserializerTest
specifier|public
class|class
name|BigEndianAscendingWordDeserializerTest
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
name|BigEndianAscendingWordDeserializer
argument_list|(
literal|0
comment|/*wordLength, below minimum of 1*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|,
operator|new
name|byte
index|[
literal|1
index|]
comment|/*bytes, arbitrary, not used here*/
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
name|BigEndianAscendingWordDeserializer
argument_list|(
literal|65
comment|/*wordLength, above maximum of 64*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|,
operator|new
name|byte
index|[
literal|1
index|]
comment|/*bytes, arbitrary, not used here*/
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
comment|// byte padding negative
try|try
block|{
operator|new
name|BigEndianAscendingWordDeserializer
argument_list|(
literal|5
comment|/*wordLength, arbitrary*/
argument_list|,
operator|-
literal|1
comment|/*bytePadding, too small*/
argument_list|,
operator|new
name|byte
index|[
literal|1
index|]
comment|/*bytes, arbitrary, not used here*/
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
comment|/**      * Smoke test using 64-bit short words and special word values.      */
annotation|@
name|Test
DECL|method|smokeTest64BitWord
specifier|public
name|void
name|smokeTest64BitWord
parameter_list|()
block|{
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
literal|64
comment|/*wordLength*/
argument_list|,
literal|5
comment|/*wordCount*/
argument_list|,
literal|0
comment|/*bytePadding, arbitrary*/
argument_list|)
decl_stmt|;
comment|// Check that the sign bit is being preserved.
name|serializer
operator|.
name|writeWord
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
operator|-
literal|112894714L
argument_list|)
expr_stmt|;
comment|// Check "special" values
name|serializer
operator|.
name|writeWord
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
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
name|BigEndianAscendingWordDeserializer
name|deserializer
init|=
operator|new
name|BigEndianAscendingWordDeserializer
argument_list|(
literal|64
comment|/*wordLength*/
argument_list|,
literal|0
comment|/*bytePadding*/
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|totalWordCount
argument_list|()
argument_list|,
literal|5
comment|/*wordCount*/
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
operator|-
literal|112894714L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * A smoke/fuzz test for ascending (from zero) word values.      */
annotation|@
name|Test
DECL|method|ascendingSmokeTest
specifier|public
name|void
name|ascendingSmokeTest
parameter_list|()
block|{
for|for
control|(
name|int
name|wordLength
init|=
literal|5
init|;
name|wordLength
operator|<
literal|65
condition|;
name|wordLength
operator|++
control|)
block|{
name|runAscendingTest
argument_list|(
name|wordLength
argument_list|,
literal|3
comment|/*bytePadding, arbitrary*/
argument_list|,
literal|100000
comment|/*wordCount, arbitrary*/
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A smoke/fuzz test for random word values.      */
annotation|@
name|Test
DECL|method|randomSmokeTest
specifier|public
name|void
name|randomSmokeTest
parameter_list|()
block|{
for|for
control|(
name|int
name|wordLength
init|=
literal|5
init|;
name|wordLength
operator|<
literal|65
condition|;
name|wordLength
operator|++
control|)
block|{
name|runRandomTest
argument_list|(
name|wordLength
argument_list|,
literal|3
comment|/*bytePadding, arbitrary*/
argument_list|,
literal|100000
comment|/*wordCount, arbitrary*/
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * Runs a test which serializes and deserializes random word values.      *      * @param wordLength the length of words to test      * @param bytePadding the number of bytes padding the byte array      * @param wordCount the number of word values to test      */
DECL|method|runRandomTest
specifier|private
specifier|static
name|void
name|runRandomTest
parameter_list|(
specifier|final
name|int
name|wordLength
parameter_list|,
specifier|final
name|int
name|bytePadding
parameter_list|,
specifier|final
name|int
name|wordCount
parameter_list|)
block|{
specifier|final
name|long
name|seed
init|=
name|randomLong
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|verificationRandom
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|final
name|long
name|wordMask
decl_stmt|;
if|if
condition|(
name|wordLength
operator|==
literal|64
condition|)
block|{
name|wordMask
operator|=
operator|~
literal|0L
expr_stmt|;
block|}
else|else
block|{
name|wordMask
operator|=
operator|(
literal|1L
operator|<<
name|wordLength
operator|)
operator|-
literal|1L
expr_stmt|;
block|}
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|wordLength
comment|/*wordLength, arbitrary*/
argument_list|,
name|wordCount
argument_list|,
name|bytePadding
comment|/*bytePadding, arbitrary*/
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
name|wordCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|value
init|=
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|wordMask
decl_stmt|;
name|serializer
operator|.
name|writeWord
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
name|BigEndianAscendingWordDeserializer
name|deserializer
init|=
operator|new
name|BigEndianAscendingWordDeserializer
argument_list|(
name|wordLength
argument_list|,
name|bytePadding
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|totalWordCount
argument_list|()
argument_list|,
name|wordCount
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wordCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
operator|(
name|verificationRandom
operator|.
name|nextLong
argument_list|()
operator|&
name|wordMask
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Runs a test which serializes and deserializes ascending (from zero) word values.      *      * @param wordLength the length of words to test      * @param bytePadding the number of bytes padding the byte array      * @param wordCount the number of word values to test      */
DECL|method|runAscendingTest
specifier|private
specifier|static
name|void
name|runAscendingTest
parameter_list|(
specifier|final
name|int
name|wordLength
parameter_list|,
specifier|final
name|int
name|bytePadding
parameter_list|,
specifier|final
name|int
name|wordCount
parameter_list|)
block|{
specifier|final
name|long
name|wordMask
decl_stmt|;
if|if
condition|(
name|wordLength
operator|==
literal|64
condition|)
block|{
name|wordMask
operator|=
operator|~
literal|0L
expr_stmt|;
block|}
else|else
block|{
name|wordMask
operator|=
operator|(
literal|1L
operator|<<
name|wordLength
operator|)
operator|-
literal|1L
expr_stmt|;
block|}
specifier|final
name|BigEndianAscendingWordSerializer
name|serializer
init|=
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|wordLength
comment|/*wordLength, arbitrary*/
argument_list|,
name|wordCount
argument_list|,
name|bytePadding
comment|/*bytePadding, arbitrary*/
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wordCount
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
operator|&
name|wordMask
argument_list|)
expr_stmt|;
block|}
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
name|BigEndianAscendingWordDeserializer
name|deserializer
init|=
operator|new
name|BigEndianAscendingWordDeserializer
argument_list|(
name|wordLength
argument_list|,
name|bytePadding
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|totalWordCount
argument_list|()
argument_list|,
name|wordCount
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wordCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|deserializer
operator|.
name|readWord
argument_list|()
argument_list|,
name|i
operator|&
name|wordMask
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

