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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|nio
operator|.
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_class
DECL|class|TestIndexableBinaryStringTools
specifier|public
class|class
name|TestIndexableBinaryStringTools
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUM_RANDOM_TESTS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_RANDOM_TESTS
init|=
literal|20000
decl_stmt|;
DECL|field|MAX_RANDOM_BINARY_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RANDOM_BINARY_LENGTH
init|=
literal|300
decl_stmt|;
DECL|method|testSingleBinaryRoundTrip
specifier|public
name|void
name|testSingleBinaryRoundTrip
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x23
block|,
operator|(
name|byte
operator|)
literal|0x98
block|,
operator|(
name|byte
operator|)
literal|0x13
block|,
operator|(
name|byte
operator|)
literal|0xE4
block|,
operator|(
name|byte
operator|)
literal|0x76
block|,
operator|(
name|byte
operator|)
literal|0x41
block|,
operator|(
name|byte
operator|)
literal|0xB2
block|,
operator|(
name|byte
operator|)
literal|0xC9
block|,
operator|(
name|byte
operator|)
literal|0x7F
block|,
operator|(
name|byte
operator|)
literal|0x0A
block|,
operator|(
name|byte
operator|)
literal|0xA6
block|,
operator|(
name|byte
operator|)
literal|0xD8
block|}
decl_stmt|;
name|ByteBuffer
name|binaryBuf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|binary
argument_list|)
decl_stmt|;
name|CharBuffer
name|encoded
init|=
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binaryBuf
argument_list|)
decl_stmt|;
name|ByteBuffer
name|decoded
init|=
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"original: "
operator|+
name|binaryDump
argument_list|(
name|binaryBuf
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" encoded: "
operator|+
name|charArrayDump
argument_list|(
name|encoded
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" decoded: "
operator|+
name|binaryDump
argument_list|(
name|decoded
argument_list|)
argument_list|,
name|binaryBuf
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncodedSortability
specifier|public
name|void
name|testEncodedSortability
parameter_list|()
block|{
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|byte
index|[]
name|originalArray1
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|ByteBuffer
name|originalBuf1
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|originalArray1
argument_list|)
decl_stmt|;
name|char
index|[]
name|originalString1
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|CharBuffer
name|originalStringBuf1
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|originalString1
argument_list|)
decl_stmt|;
name|char
index|[]
name|encoded1
init|=
operator|new
name|char
index|[
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|originalBuf1
argument_list|)
index|]
decl_stmt|;
name|CharBuffer
name|encodedBuf1
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|encoded1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|original2
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|ByteBuffer
name|originalBuf2
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|original2
argument_list|)
decl_stmt|;
name|char
index|[]
name|originalString2
init|=
operator|new
name|char
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|CharBuffer
name|originalStringBuf2
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|originalString2
argument_list|)
decl_stmt|;
name|char
index|[]
name|encoded2
init|=
operator|new
name|char
index|[
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|originalBuf2
argument_list|)
index|]
decl_stmt|;
name|CharBuffer
name|encodedBuf2
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|encoded2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|testNum
init|=
literal|0
init|;
name|testNum
operator|<
name|NUM_RANDOM_TESTS
condition|;
operator|++
name|testNum
control|)
block|{
name|int
name|numBytes1
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
name|originalBuf1
operator|.
name|limit
argument_list|(
name|numBytes1
argument_list|)
expr_stmt|;
name|originalStringBuf1
operator|.
name|limit
argument_list|(
name|numBytes1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes1
condition|;
operator|++
name|byteNum
control|)
block|{
name|int
name|randomInt
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
decl_stmt|;
name|originalArray1
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|randomInt
expr_stmt|;
name|originalString1
index|[
name|byteNum
index|]
operator|=
operator|(
name|char
operator|)
name|randomInt
expr_stmt|;
block|}
name|int
name|numBytes2
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
name|originalBuf2
operator|.
name|limit
argument_list|(
name|numBytes2
argument_list|)
expr_stmt|;
name|originalStringBuf2
operator|.
name|limit
argument_list|(
name|numBytes2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes2
condition|;
operator|++
name|byteNum
control|)
block|{
name|int
name|randomInt
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
decl_stmt|;
name|original2
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|randomInt
expr_stmt|;
name|originalString2
index|[
name|byteNum
index|]
operator|=
operator|(
name|char
operator|)
name|randomInt
expr_stmt|;
block|}
name|int
name|originalComparison
init|=
name|originalStringBuf1
operator|.
name|compareTo
argument_list|(
name|originalStringBuf2
argument_list|)
decl_stmt|;
name|originalComparison
operator|=
name|originalComparison
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
name|originalComparison
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|originalBuf1
argument_list|,
name|encodedBuf1
argument_list|)
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|originalBuf2
argument_list|,
name|encodedBuf2
argument_list|)
expr_stmt|;
name|int
name|encodedComparison
init|=
name|encodedBuf1
operator|.
name|compareTo
argument_list|(
name|encodedBuf2
argument_list|)
decl_stmt|;
name|encodedComparison
operator|=
name|encodedComparison
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
name|encodedComparison
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test #"
operator|+
operator|(
name|testNum
operator|+
literal|1
operator|)
operator|+
literal|": Original bytes and encoded chars compare differently:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" binary 1: "
operator|+
name|binaryDump
argument_list|(
name|originalBuf1
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|" binary 2: "
operator|+
name|binaryDump
argument_list|(
name|originalBuf2
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encoded 1: "
operator|+
name|charArrayDump
argument_list|(
name|encodedBuf1
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encoded 2: "
operator|+
name|charArrayDump
argument_list|(
name|encodedBuf2
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|,
name|originalComparison
argument_list|,
name|encodedComparison
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyInput
specifier|public
name|void
name|testEmptyInput
parameter_list|()
block|{
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|CharBuffer
name|encoded
init|=
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|binary
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBuffer
name|decoded
init|=
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"decode() returned null"
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"decoded empty input was not empty"
argument_list|,
name|decoded
operator|.
name|limit
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllNullInput
specifier|public
name|void
name|testAllNullInput
parameter_list|()
block|{
name|byte
index|[]
name|binary
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
literal|0
block|}
decl_stmt|;
name|ByteBuffer
name|binaryBuf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|binary
argument_list|)
decl_stmt|;
name|CharBuffer
name|encoded
init|=
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binaryBuf
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"encode() returned null"
argument_list|,
name|encoded
argument_list|)
expr_stmt|;
name|ByteBuffer
name|decodedBuf
init|=
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"decode() returned null"
argument_list|,
name|decodedBuf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"  original: "
operator|+
name|binaryDump
argument_list|(
name|binaryBuf
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"decodedBuf: "
operator|+
name|binaryDump
argument_list|(
name|decodedBuf
argument_list|)
argument_list|,
name|binaryBuf
argument_list|,
name|decodedBuf
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomBinaryRoundTrip
specifier|public
name|void
name|testRandomBinaryRoundTrip
parameter_list|()
block|{
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|byte
index|[]
name|binary
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|ByteBuffer
name|binaryBuf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|binary
argument_list|)
decl_stmt|;
name|char
index|[]
name|encoded
init|=
operator|new
name|char
index|[
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|binaryBuf
argument_list|)
index|]
decl_stmt|;
name|CharBuffer
name|encodedBuf
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|byte
index|[]
name|decoded
init|=
operator|new
name|byte
index|[
name|MAX_RANDOM_BINARY_LENGTH
index|]
decl_stmt|;
name|ByteBuffer
name|decodedBuf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|decoded
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|testNum
init|=
literal|0
init|;
name|testNum
operator|<
name|NUM_RANDOM_TESTS
condition|;
operator|++
name|testNum
control|)
block|{
name|int
name|numBytes
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_RANDOM_BINARY_LENGTH
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Min == 1
name|binaryBuf
operator|.
name|limit
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes
condition|;
operator|++
name|byteNum
control|)
block|{
name|binary
index|[
name|byteNum
index|]
operator|=
operator|(
name|byte
operator|)
name|random
operator|.
name|nextInt
argument_list|(
literal|0x100
argument_list|)
expr_stmt|;
block|}
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|binaryBuf
argument_list|,
name|encodedBuf
argument_list|)
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|decode
argument_list|(
name|encodedBuf
argument_list|,
name|decodedBuf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test #"
operator|+
operator|(
name|testNum
operator|+
literal|1
operator|)
operator|+
literal|": Round trip decode/decode returned different results:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"  original: "
operator|+
name|binaryDump
argument_list|(
name|binaryBuf
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"encodedBuf: "
operator|+
name|charArrayDump
argument_list|(
name|encodedBuf
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"decodedBuf: "
operator|+
name|binaryDump
argument_list|(
name|decodedBuf
argument_list|)
argument_list|,
name|binaryBuf
argument_list|,
name|decodedBuf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|binaryDump
specifier|public
name|String
name|binaryDump
parameter_list|(
name|ByteBuffer
name|binaryBuf
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numBytes
init|=
name|binaryBuf
operator|.
name|limit
argument_list|()
operator|-
name|binaryBuf
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
name|byte
index|[]
name|binary
init|=
name|binaryBuf
operator|.
name|array
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|byteNum
init|=
literal|0
init|;
name|byteNum
operator|<
name|numBytes
condition|;
operator|++
name|byteNum
control|)
block|{
name|String
name|hex
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|binary
index|[
name|byteNum
index|]
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
if|if
condition|(
name|hex
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|hex
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|byteNum
operator|<
name|numBytes
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|charArrayDump
specifier|public
name|String
name|charArrayDump
parameter_list|(
name|CharBuffer
name|charBuf
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numBytes
init|=
name|charBuf
operator|.
name|limit
argument_list|()
operator|-
name|charBuf
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
name|char
index|[]
name|charArray
init|=
name|charBuf
operator|.
name|array
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|charNum
init|=
literal|0
init|;
name|charNum
operator|<
name|numBytes
condition|;
operator|++
name|charNum
control|)
block|{
name|String
name|hex
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|charArray
index|[
name|charNum
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|digit
init|=
literal|0
init|;
name|digit
operator|<
literal|4
operator|-
name|hex
operator|.
name|length
argument_list|()
condition|;
operator|++
name|digit
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|hex
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|charNum
operator|<
name|numBytes
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

