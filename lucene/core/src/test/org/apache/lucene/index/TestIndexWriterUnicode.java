begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

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
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|util
operator|.
name|Set
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
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|Directory
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
name|BytesRef
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
name|BytesRefBuilder
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
name|CharsRefBuilder
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_class
DECL|class|TestIndexWriterUnicode
specifier|public
class|class
name|TestIndexWriterUnicode
extends|extends
name|LuceneTestCase
block|{
DECL|field|utf8Data
specifier|final
name|String
index|[]
name|utf8Data
init|=
operator|new
name|String
index|[]
block|{
comment|// unpaired low surrogate
literal|"ab\udc17cd"
block|,
literal|"ab\ufffdcd"
block|,
literal|"\udc17abcd"
block|,
literal|"\ufffdabcd"
block|,
literal|"\udc17"
block|,
literal|"\ufffd"
block|,
literal|"ab\udc17\udc17cd"
block|,
literal|"ab\ufffd\ufffdcd"
block|,
literal|"\udc17\udc17abcd"
block|,
literal|"\ufffd\ufffdabcd"
block|,
literal|"\udc17\udc17"
block|,
literal|"\ufffd\ufffd"
block|,
comment|// unpaired high surrogate
literal|"ab\ud917cd"
block|,
literal|"ab\ufffdcd"
block|,
literal|"\ud917abcd"
block|,
literal|"\ufffdabcd"
block|,
literal|"\ud917"
block|,
literal|"\ufffd"
block|,
literal|"ab\ud917\ud917cd"
block|,
literal|"ab\ufffd\ufffdcd"
block|,
literal|"\ud917\ud917abcd"
block|,
literal|"\ufffd\ufffdabcd"
block|,
literal|"\ud917\ud917"
block|,
literal|"\ufffd\ufffd"
block|,
comment|// backwards surrogates
literal|"ab\udc17\ud917cd"
block|,
literal|"ab\ufffd\ufffdcd"
block|,
literal|"\udc17\ud917abcd"
block|,
literal|"\ufffd\ufffdabcd"
block|,
literal|"\udc17\ud917"
block|,
literal|"\ufffd\ufffd"
block|,
literal|"ab\udc17\ud917\udc17\ud917cd"
block|,
literal|"ab\ufffd\ud917\udc17\ufffdcd"
block|,
literal|"\udc17\ud917\udc17\ud917abcd"
block|,
literal|"\ufffd\ud917\udc17\ufffdabcd"
block|,
literal|"\udc17\ud917\udc17\ud917"
block|,
literal|"\ufffd\ud917\udc17\ufffd"
block|}
decl_stmt|;
DECL|method|nextInt
specifier|private
name|int
name|nextInt
parameter_list|(
name|int
name|lim
parameter_list|)
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|lim
argument_list|)
return|;
block|}
DECL|method|nextInt
specifier|private
name|int
name|nextInt
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|start
operator|+
name|nextInt
argument_list|(
name|end
operator|-
name|start
argument_list|)
return|;
block|}
DECL|method|fillUnicode
specifier|private
name|boolean
name|fillUnicode
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|char
index|[]
name|expected
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|offset
operator|+
name|count
decl_stmt|;
name|boolean
name|hasIllegal
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|offset
operator|>
literal|0
operator|&&
name|buffer
index|[
name|offset
index|]
operator|>=
literal|0xdc00
operator|&&
name|buffer
index|[
name|offset
index|]
operator|<
literal|0xe000
condition|)
comment|// Don't start in the middle of a valid surrogate pair
name|offset
operator|--
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|t
init|=
name|nextInt
argument_list|(
literal|6
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|t
operator|&&
name|i
operator|<
name|len
operator|-
literal|1
condition|)
block|{
comment|// Make a surrogate pair
comment|// High surrogate
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0xd800
argument_list|,
literal|0xdc00
argument_list|)
expr_stmt|;
comment|// Low surrogate
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0xdc00
argument_list|,
literal|0xe000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|<=
literal|1
condition|)
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0x80
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|2
operator|==
name|t
condition|)
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0x80
argument_list|,
literal|0x800
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|3
operator|==
name|t
condition|)
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0x800
argument_list|,
literal|0xd800
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|4
operator|==
name|t
condition|)
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0xe000
argument_list|,
literal|0xffff
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|5
operator|==
name|t
operator|&&
name|i
operator|<
name|len
operator|-
literal|1
condition|)
block|{
comment|// Illegal unpaired surrogate
if|if
condition|(
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0xd800
argument_list|,
literal|0xdc00
argument_list|)
expr_stmt|;
else|else
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0xdc00
argument_list|,
literal|0xe000
argument_list|)
expr_stmt|;
name|expected
index|[
name|i
operator|++
index|]
operator|=
literal|0xfffd
expr_stmt|;
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0x800
argument_list|,
literal|0xd800
argument_list|)
expr_stmt|;
name|hasIllegal
operator|=
literal|true
expr_stmt|;
block|}
else|else
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|nextInt
argument_list|(
literal|0x800
argument_list|,
literal|0xd800
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expected
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
index|]
operator|=
literal|' '
expr_stmt|;
block|}
block|}
return|return
name|hasIllegal
return|;
block|}
comment|// both start& end are inclusive
DECL|method|getInt
specifier|private
specifier|final
name|int
name|getInt
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|start
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|1
operator|+
name|end
operator|-
name|start
argument_list|)
return|;
block|}
DECL|method|asUnicodeChar
specifier|private
specifier|final
name|String
name|asUnicodeChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
literal|"U+"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|c
argument_list|)
return|;
block|}
DECL|method|termDesc
specifier|private
specifier|final
name|String
name|termDesc
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|String
name|s0
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|<=
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|s0
operator|=
name|asUnicodeChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s0
operator|=
name|asUnicodeChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
name|asUnicodeChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s0
return|;
block|}
DECL|method|checkTermsOrder
specifier|private
name|void
name|checkTermsOrder
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allTerms
parameter_list|,
name|boolean
name|isTop
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"f"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRefBuilder
name|last
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|seenTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|terms
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|assertTrue
argument_list|(
name|last
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|last
operator|.
name|copyBytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"term "
operator|+
name|termDesc
argument_list|(
name|s
argument_list|)
operator|+
literal|" was not added to index (count="
operator|+
name|allTerms
operator|.
name|size
argument_list|()
operator|+
literal|")"
argument_list|,
name|allTerms
operator|.
name|contains
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|seenTerms
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isTop
condition|)
block|{
name|assertTrue
argument_list|(
name|allTerms
operator|.
name|equals
argument_list|(
name|seenTerms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Test seeking:
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|seenTerms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BytesRef
name|tr
init|=
operator|new
name|BytesRef
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"seek failed for term="
operator|+
name|termDesc
argument_list|(
name|tr
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|,
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|terms
operator|.
name|seekCeil
argument_list|(
name|tr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-510
DECL|method|testRandomUnicodeStrings
specifier|public
name|void
name|testRandomUnicodeStrings
parameter_list|()
throws|throws
name|Throwable
block|{
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
name|char
index|[]
name|expected
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
name|CharsRefBuilder
name|utf16
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
name|boolean
name|hasIllegal
init|=
name|fillUnicode
argument_list|(
name|buffer
argument_list|,
name|expected
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|BytesRef
name|utf8
init|=
operator|new
name|BytesRef
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasIllegal
condition|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b
operator|.
name|length
argument_list|,
name|utf8
operator|.
name|length
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
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|assertEquals
argument_list|(
name|b
index|[
name|i
index|]
argument_list|,
name|utf8
operator|.
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|utf16
operator|.
name|copyUTF8Bytes
argument_list|(
name|utf8
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|utf16
operator|.
name|length
argument_list|()
argument_list|,
literal|20
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
literal|20
condition|;
name|i
operator|++
control|)
name|assertEquals
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|utf16
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-510
DECL|method|testAllUnicodeChars
specifier|public
name|void
name|testAllUnicodeChars
parameter_list|()
throws|throws
name|Throwable
block|{
name|CharsRefBuilder
name|utf16
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|ch
init|=
literal|0
init|;
name|ch
operator|<
literal|0x0010FFFF
condition|;
name|ch
operator|++
control|)
block|{
if|if
condition|(
name|ch
operator|==
literal|0xd800
condition|)
comment|// Skip invalid code points
name|ch
operator|=
literal|0xe000
expr_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|ch
operator|<=
literal|0xffff
condition|)
block|{
name|chars
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|ch
expr_stmt|;
block|}
else|else
block|{
name|chars
index|[
name|len
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|ch
operator|-
literal|0x0010000
operator|)
operator|>>
literal|10
operator|)
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
argument_list|)
expr_stmt|;
name|chars
index|[
name|len
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|ch
operator|-
literal|0x0010000
operator|)
operator|&
literal|0x3FFL
operator|)
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_START
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|utf8
init|=
operator|new
name|BytesRef
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
operator|new
name|String
argument_list|(
name|utf8
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"codepoint "
operator|+
name|ch
argument_list|,
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|utf16
operator|.
name|copyUTF8Bytes
argument_list|(
name|utf8
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"codepoint "
operator|+
name|ch
argument_list|,
name|s1
argument_list|,
name|utf16
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
name|s1
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|utf8
operator|.
name|length
argument_list|,
name|b
operator|.
name|length
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
name|utf8
operator|.
name|length
condition|;
name|j
operator|++
control|)
name|assertEquals
argument_list|(
name|utf8
operator|.
name|bytes
index|[
name|j
index|]
argument_list|,
name|b
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmbeddedFFFF
specifier|public
name|void
name|testEmbeddedFFFF
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"a a\uffffb"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a\uffffb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-510
DECL|method|testInvalidUTF16
specifier|public
name|void
name|testInvalidUTF16
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|TestIndexWriter
operator|.
name|StringSplitAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|utf8Data
operator|.
name|length
operator|/
literal|2
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
name|count
condition|;
name|i
operator|++
control|)
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f"
operator|+
name|i
argument_list|,
name|utf8Data
index|[
literal|2
operator|*
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc2
init|=
name|ir
operator|.
name|document
argument_list|(
literal|0
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"field "
operator|+
name|i
operator|+
literal|" was not indexed correctly"
argument_list|,
literal|1
argument_list|,
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
operator|+
name|i
argument_list|,
name|utf8Data
index|[
literal|2
operator|*
name|i
operator|+
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field "
operator|+
name|i
operator|+
literal|" is incorrect"
argument_list|,
name|utf8Data
index|[
literal|2
operator|*
name|i
operator|+
literal|1
index|]
argument_list|,
name|doc2
operator|.
name|getField
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure terms, including ones with surrogate pairs,
comment|// sort in codepoint sort order by default
DECL|method|testTermUTF16SortOrder
specifier|public
name|void
name|testTermUTF16SortOrder
parameter_list|()
throws|throws
name|Throwable
block|{
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|rnd
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Single segment
name|Field
name|f
init|=
name|newStringField
argument_list|(
literal|"f"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
decl_stmt|;
if|if
condition|(
name|rnd
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Single char
if|if
condition|(
name|rnd
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Above surrogates
name|chars
index|[
literal|0
index|]
operator|=
operator|(
name|char
operator|)
name|getInt
argument_list|(
name|rnd
argument_list|,
literal|1
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
argument_list|,
literal|0xffff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Below surrogates
name|chars
index|[
literal|0
index|]
operator|=
operator|(
name|char
operator|)
name|getInt
argument_list|(
name|rnd
argument_list|,
literal|0
argument_list|,
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|s
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Surrogate pair
name|chars
index|[
literal|0
index|]
operator|=
operator|(
name|char
operator|)
name|getInt
argument_list|(
name|rnd
argument_list|,
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
argument_list|,
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_END
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|int
operator|)
name|chars
index|[
literal|0
index|]
operator|)
operator|>=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|&&
operator|(
operator|(
name|int
operator|)
name|chars
index|[
literal|0
index|]
operator|)
operator|<=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_END
argument_list|)
expr_stmt|;
name|chars
index|[
literal|1
index|]
operator|=
operator|(
name|char
operator|)
name|getInt
argument_list|(
name|rnd
argument_list|,
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_START
argument_list|,
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
name|allTerms
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
literal|1
operator|+
name|i
operator|)
operator|%
literal|42
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// Test each sub-segment
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|r
operator|.
name|leaves
argument_list|()
control|)
block|{
name|checkTermsOrder
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|allTerms
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|checkTermsOrder
argument_list|(
name|r
argument_list|,
name|allTerms
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Test multi segment
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Test single segment
name|r
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|checkTermsOrder
argument_list|(
name|r
argument_list|,
name|allTerms
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

