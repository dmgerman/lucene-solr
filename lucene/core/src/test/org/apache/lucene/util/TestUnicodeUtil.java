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

begin_comment
comment|/*  * Some of this code came from the excellent Unicode  * conversion examples from:  *  *   http://www.unicode.org/Public/PROGRAMS/CVTUTF  *  * Full Copyright for that code follows: */
end_comment

begin_comment
comment|/*  * Copyright 2001-2004 Unicode, Inc.  *   * Disclaimer  *   * This source code is provided as is by Unicode, Inc. No claims are  * made as to fitness for any particular purpose. No warranties of any  * kind are expressed or implied. The recipient agrees to determine  * applicability of information provided. If this file has been  * purchased on magnetic or optical media from Unicode, Inc., the  * sole remedy for any claim will be exchange of defective media  * within 90 days of receipt.  *   * Limitations on Rights to Redistribute This Code  *   * Unicode, Inc. hereby grants the right to freely use the information  * supplied in this file in the creation of products supporting the  * Unicode Standard, and to make copies of this file in any form  * for internal or external distribution as long as this notice  * remains attached.  */
end_comment

begin_comment
comment|/*  * Additional code came from the IBM ICU library.  *  *  http://www.icu-project.org  *  * Full Copyright for that code follows.  */
end_comment

begin_comment
comment|/*  * Copyright (C) 1999-2010, International Business Machines  * Corporation and others.  All Rights Reserved.  *  * Permission is hereby granted, free of charge, to any person obtaining a copy  * of this software and associated documentation files (the "Software"), to deal  * in the Software without restriction, including without limitation the rights  * to use, copy, modify, merge, publish, distribute, and/or sell copies of the  * Software, and to permit persons to whom the Software is furnished to do so,  * provided that the above copyright notice(s) and this permission notice appear  * in all copies of the Software and that both the above copyright notice(s) and  * this permission notice appear in supporting documentation.  *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS.  * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE  * LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR  * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER  * IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT  * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.  *  * Except as contained in this notice, the name of a copyright holder shall not  * be used in advertising or otherwise to promote the sale, use or other  * dealings in this Software without prior written authorization of the  * copyright holder.  */
end_comment

begin_class
DECL|class|TestUnicodeUtil
specifier|public
class|class
name|TestUnicodeUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCodePointCount
specifier|public
name|void
name|testCodePointCount
parameter_list|()
block|{
comment|// Check invalid codepoints.
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0x80
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xc0
operator|-
literal|1
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check 5-byte and longer sequences.
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xf8
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xfc
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check improperly terminated codepoints.
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xc2
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xe2
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xe2
argument_list|,
literal|0x82
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xf0
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xf0
argument_list|,
literal|0xa4
argument_list|)
argument_list|)
expr_stmt|;
name|assertcodePointCountThrowsAssertionOn
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xf0
argument_list|,
literal|0xa4
argument_list|,
literal|0xad
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check some typical examples (multibyte).
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|asByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|'z'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xc2
argument_list|,
literal|0xa2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xe2
argument_list|,
literal|0x82
argument_list|,
literal|0xac
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|asByteArray
argument_list|(
literal|'z'
argument_list|,
literal|0xf0
argument_list|,
literal|0xa4
argument_list|,
literal|0xad
argument_list|,
literal|0xa2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// And do some random stuff.
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|50000
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
name|num
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|utf8
init|=
operator|new
name|byte
index|[
name|s
operator|.
name|length
argument_list|()
operator|*
name|UnicodeUtil
operator|.
name|MAX_UTF8_BYTES_PER_CHAR
index|]
decl_stmt|;
specifier|final
name|int
name|utf8Len
init|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|utf8
argument_list|,
literal|0
argument_list|,
name|utf8Len
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|asByteArray
specifier|private
name|byte
index|[]
name|asByteArray
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
name|byte
index|[]
name|asByteArray
init|=
operator|new
name|byte
index|[
name|ints
operator|.
name|length
index|]
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|asByteArray
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|asByteArray
return|;
block|}
DECL|method|assertcodePointCountThrowsAssertionOn
specifier|private
name|void
name|assertcodePointCountThrowsAssertionOn
parameter_list|(
name|byte
modifier|...
name|bytes
parameter_list|)
block|{
name|boolean
name|threwAssertion
init|=
literal|false
decl_stmt|;
try|try
block|{
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|threwAssertion
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|threwAssertion
argument_list|)
expr_stmt|;
block|}
DECL|method|testUTF8toUTF32
specifier|public
name|void
name|testUTF8toUTF32
parameter_list|()
block|{
name|int
index|[]
name|utf32
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
name|int
index|[]
name|codePoints
init|=
operator|new
name|int
index|[
literal|20
index|]
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|50000
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
name|num
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|utf8
init|=
operator|new
name|byte
index|[
name|s
operator|.
name|length
argument_list|()
operator|*
name|UnicodeUtil
operator|.
name|MAX_UTF8_BYTES_PER_CHAR
index|]
decl_stmt|;
specifier|final
name|int
name|utf8Len
init|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
argument_list|)
decl_stmt|;
name|utf32
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|utf32
argument_list|,
name|utf8Len
argument_list|)
expr_stmt|;
specifier|final
name|int
name|utf32Len
init|=
name|UnicodeUtil
operator|.
name|UTF8toUTF32
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|utf8
argument_list|,
literal|0
argument_list|,
name|utf8Len
argument_list|)
argument_list|,
name|utf32
argument_list|)
decl_stmt|;
name|int
name|charUpto
init|=
literal|0
decl_stmt|;
name|int
name|intUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|charUpto
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
specifier|final
name|int
name|cp
init|=
name|s
operator|.
name|codePointAt
argument_list|(
name|charUpto
argument_list|)
decl_stmt|;
name|codePoints
index|[
name|intUpto
operator|++
index|]
operator|=
name|cp
expr_stmt|;
name|charUpto
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|codePoints
argument_list|,
literal|0
argument_list|,
name|utf32
argument_list|,
literal|0
argument_list|,
name|intUpto
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAILED"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  char["
operator|+
name|j
operator|+
literal|"]="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|intUpto
argument_list|,
name|utf32Len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|intUpto
condition|;
name|j
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|utf32
index|[
name|j
index|]
argument_list|)
operator|+
literal|" vs "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|codePoints
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"mismatch"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testNewString
specifier|public
name|void
name|testNewString
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|codePoints
init|=
block|{
name|Character
operator|.
name|toCodePoint
argument_list|(
name|Character
operator|.
name|MIN_HIGH_SURROGATE
argument_list|,
name|Character
operator|.
name|MAX_LOW_SURROGATE
argument_list|)
block|,
name|Character
operator|.
name|toCodePoint
argument_list|(
name|Character
operator|.
name|MAX_HIGH_SURROGATE
argument_list|,
name|Character
operator|.
name|MIN_LOW_SURROGATE
argument_list|)
block|,
name|Character
operator|.
name|MAX_HIGH_SURROGATE
block|,
literal|'A'
block|,
operator|-
literal|1
block|,}
decl_stmt|;
specifier|final
name|String
name|cpString
init|=
literal|""
operator|+
name|Character
operator|.
name|MIN_HIGH_SURROGATE
operator|+
name|Character
operator|.
name|MAX_LOW_SURROGATE
operator|+
name|Character
operator|.
name|MAX_HIGH_SURROGATE
operator|+
name|Character
operator|.
name|MIN_LOW_SURROGATE
operator|+
name|Character
operator|.
name|MAX_HIGH_SURROGATE
operator|+
literal|'A'
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|tests
init|=
block|{
block|{
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|2
block|}
block|,
block|{
literal|0
block|,
literal|2
block|,
literal|0
block|,
literal|4
block|}
block|,
block|{
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|}
block|,
block|{
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|}
block|,
block|{
literal|1
block|,
literal|3
block|,
literal|2
block|,
literal|4
block|}
block|,
block|{
literal|2
block|,
literal|2
block|,
literal|4
block|,
literal|2
block|}
block|,
block|{
literal|2
block|,
literal|3
block|,
literal|0
block|,
operator|-
literal|1
block|}
block|,
block|{
literal|4
block|,
literal|5
block|,
literal|0
block|,
operator|-
literal|1
block|}
block|,
block|{
literal|3
block|,
operator|-
literal|1
block|,
literal|0
block|,
operator|-
literal|1
block|}
block|}
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
name|tests
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
index|[]
name|t
init|=
name|tests
index|[
name|i
index|]
decl_stmt|;
name|int
name|s
init|=
name|t
index|[
literal|0
index|]
decl_stmt|;
name|int
name|c
init|=
name|t
index|[
literal|1
index|]
decl_stmt|;
name|int
name|rs
init|=
name|t
index|[
literal|2
index|]
decl_stmt|;
name|int
name|rc
init|=
name|t
index|[
literal|3
index|]
decl_stmt|;
try|try
block|{
name|String
name|str
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|codePoints
argument_list|,
name|s
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rc
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cpString
operator|.
name|substring
argument_list|(
name|rs
argument_list|,
name|rs
operator|+
name|rc
argument_list|)
argument_list|,
name|str
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
decl||
name|IllegalArgumentException
name|e1
parameter_list|)
block|{
comment|// Ignored.
block|}
name|assertTrue
argument_list|(
name|rc
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUTF8UTF16CharsRef
specifier|public
name|void
name|testUTF8UTF16CharsRef
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|3989
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|unicode
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
name|unicode
argument_list|)
decl_stmt|;
name|CharsRefBuilder
name|cRef
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|cRef
operator|.
name|copyUTF8Bytes
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cRef
operator|.
name|toString
argument_list|()
argument_list|,
name|unicode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCalcUTF16toUTF8Length
specifier|public
name|void
name|testCalcUTF16toUTF8Length
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|5000
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|unicode
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|utf8
init|=
operator|new
name|byte
index|[
name|unicode
operator|.
name|length
argument_list|()
operator|*
name|UnicodeUtil
operator|.
name|MAX_UTF8_BYTES_PER_CHAR
index|]
decl_stmt|;
name|int
name|len
init|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|unicode
argument_list|,
literal|0
argument_list|,
name|unicode
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|len
argument_list|,
name|UnicodeUtil
operator|.
name|calcUTF16toUTF8Length
argument_list|(
name|unicode
argument_list|,
literal|0
argument_list|,
name|unicode
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

