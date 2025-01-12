begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
package|;
end_package

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
name|AttributeImpl
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
name|TestUtil
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Formatter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|TestCharTermAttributeImpl
specifier|public
class|class
name|TestCharTermAttributeImpl
extends|extends
name|LuceneTestCase
block|{
DECL|method|testResize
specifier|public
name|void
name|testResize
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
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
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|.
name|resizeBuffer
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|<=
name|t
operator|.
name|buffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGrow
specifier|public
name|void
name|testGrow
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ab"
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1048576
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// now as a StringBuilder, first variant
name|t
operator|=
operator|new
name|CharTermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"ab"
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
block|{
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1048576
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test for slow growth to a long term
name|t
operator|=
operator|new
name|CharTermAttributeImpl
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"a"
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
literal|20000
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|20000
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|b
init|=
block|{
literal|'a'
block|,
literal|'l'
block|,
literal|'o'
block|,
literal|'h'
block|,
literal|'a'
block|}
decl_stmt|;
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aloha"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"hi there"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hi there"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
throws|throws
name|Exception
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
name|t
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|CharTermAttributeImpl
name|copy
init|=
name|assertCloneIsEqual
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|buffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|CharTermAttributeImpl
name|t1a
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content1a
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t1a
operator|.
name|copyBuffer
argument_list|(
name|content1a
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|CharTermAttributeImpl
name|t1b
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content1b
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t1b
operator|.
name|copyBuffer
argument_list|(
name|content1b
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|CharTermAttributeImpl
name|t2
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|char
index|[]
name|content2
init|=
literal|"hello2"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t2
operator|.
name|copyBuffer
argument_list|(
name|content2
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t1a
operator|.
name|equals
argument_list|(
name|t1b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t1a
operator|.
name|equals
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t2
operator|.
name|equals
argument_list|(
name|t1b
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCopyTo
specifier|public
name|void
name|testCopyTo
parameter_list|()
throws|throws
name|Exception
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|CharTermAttributeImpl
name|copy
init|=
name|assertCopyIsEqual
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|CharTermAttributeImpl
argument_list|()
expr_stmt|;
name|char
index|[]
name|content
init|=
literal|"hello"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|content
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
name|t
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|copy
operator|=
name|assertCopyIsEqual
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
name|copy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|buf
argument_list|,
name|copy
operator|.
name|buffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAttributeReflection
specifier|public
name|void
name|testAttributeReflection
parameter_list|()
throws|throws
name|Exception
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
name|t
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#term"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#bytes"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharSequenceInterface
specifier|public
name|void
name|testCharSequenceInterface
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"0123456789"
decl_stmt|;
specifier|final
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|t
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12"
argument_list|,
name|t
operator|.
name|subSequence
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|t
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Pattern
operator|.
name|matches
argument_list|(
literal|"01\\d+"
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Pattern
operator|.
name|matches
argument_list|(
literal|"34"
argument_list|,
name|t
operator|.
name|subSequence
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|subSequence
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|subSequence
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|)
operator|.
name|toString
argument_list|()
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppendableInterface
specifier|public
name|void
name|testAppendableInterface
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|Formatter
name|formatter
init|=
operator|new
name|Formatter
argument_list|(
name|t
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
literal|1234
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
literal|5678
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12345678"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|'9'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456789"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234567890"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"0123456789"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456789012"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|CharBuffer
operator|.
name|wrap
argument_list|(
literal|"0123456789"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12345678901234"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234567890123412345678901234"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
operator|new
name|StringBuilder
argument_list|(
literal|"0123456789"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456789012341234567890123456"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
operator|new
name|StringBuffer
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456789012341234567890123456123456789012341234567890123456"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// very wierd, to test if a subSlice is wrapped correct :)
name|CharBuffer
name|buf
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
literal|"0123456789"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"34567"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|buf
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|CharTermAttribute
name|t2
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t2
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|t2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4test"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|t2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4teste"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|t2
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|t2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4testenull"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppendableInterfaceWithLongSequences
specifier|public
name|void
name|testAppendableInterfaceWithLongSequences
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"01234567890123456789012345678901234567890123456789"
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
name|CharBuffer
operator|.
name|wrap
argument_list|(
literal|"01234567890123456789012345678901234567890123456789"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0123456789012345678901234567890123456789012345678934567890123456789012345678901234567890123456789"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
operator|new
name|StringBuilder
argument_list|(
literal|"01234567890123456789"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|17
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"567890123456"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"567890123456567890123456"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// very wierd, to test if a subSlice is wrapped correct :)
name|CharBuffer
name|buf
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
literal|"012345678901234567890123456789"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"345678901234567"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|1
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4567890123456"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// finally use a completely custom CharSequence that is not catched by instanceof checks
specifier|final
name|String
name|longTestString
init|=
literal|"012345678901234567890123456789"
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|new
name|CharSequence
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|longTestString
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|longTestString
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|longTestString
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|longTestString
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4567890123456"
operator|+
name|longTestString
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonCharSequenceAppend
specifier|public
name|void
name|testNonCharSequenceAppend
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|"0123456789"
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|"0123456789"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"01234567890123456789"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"0123456789"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"012345678901234567890123456789"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|CharTermAttribute
name|t2
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t2
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"012345678901234567890123456789test"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|StringBuilder
operator|)
literal|null
argument_list|)
expr_stmt|;
name|t
operator|.
name|append
argument_list|(
operator|(
name|CharTermAttribute
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"012345678901234567890123456789testnullnullnull"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|charAt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|charAt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|t
operator|.
name|subSequence
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCloneIsEqual
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|AttributeImpl
parameter_list|>
name|T
name|assertCloneIsEqual
parameter_list|(
name|T
name|att
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|clone
init|=
operator|(
name|T
operator|)
name|att
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Clone must be equal"
argument_list|,
name|att
argument_list|,
name|clone
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Clone's hashcode must be equal"
argument_list|,
name|att
operator|.
name|hashCode
argument_list|()
argument_list|,
name|clone
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|assertCopyIsEqual
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|AttributeImpl
parameter_list|>
name|T
name|assertCopyIsEqual
parameter_list|(
name|T
name|att
parameter_list|)
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|copy
init|=
operator|(
name|T
operator|)
name|att
operator|.
name|getClass
argument_list|()
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|att
operator|.
name|copyTo
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Copied instance must be equal"
argument_list|,
name|att
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Copied instance's hashcode must be equal"
argument_list|,
name|att
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/*      // test speed of the dynamic instanceof checks in append(CharSequence),   // to find the best max length for the generic while (start<end) loop:   public void testAppendPerf() {     CharTermAttributeImpl t = new CharTermAttributeImpl();     final int count = 32;     CharSequence[] csq = new CharSequence[count * 6];     final StringBuilder sb = new StringBuilder();     for (int i=0,j=0; i<count; i++) {       sb.append(i%10);       final String testString = sb.toString();       CharTermAttribute cta = new CharTermAttributeImpl();       cta.append(testString);       csq[j++] = cta;       csq[j++] = testString;       csq[j++] = new StringBuilder(sb);       csq[j++] = new StringBuffer(sb);       csq[j++] = CharBuffer.wrap(testString.toCharArray());       csq[j++] = new CharSequence() {         public char charAt(int i) { return testString.charAt(i); }         public int length() { return testString.length(); }         public CharSequence subSequence(int start, int end) { return testString.subSequence(start, end); }         public String toString() { return testString; }       };     }      Random rnd = newRandom();     long startTime = System.currentTimeMillis();     for (int i=0; i<100000000; i++) {       t.setEmpty().append(csq[rnd.nextInt(csq.length)]);     }     long endTime = System.currentTimeMillis();     System.out.println("Time: " + (endTime-startTime)/1000.0 + " s");   }      */
block|}
end_class

end_unit

