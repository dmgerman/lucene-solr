begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Payload
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
name|tokenattributes
operator|.
name|*
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
name|Attribute
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
name|AttributeImpl
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_class
DECL|class|TestToken
specifier|public
class|class
name|TestToken
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCtor
specifier|public
name|void
name|testCtor
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
name|t
init|=
operator|new
name|Token
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
name|assertNotSame
argument_list|(
name|t
operator|.
name|buffer
argument_list|()
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
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
name|assertEquals
argument_list|(
literal|"word"
argument_list|,
name|t
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|6
argument_list|,
literal|22
argument_list|)
expr_stmt|;
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
literal|"hello"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"word"
argument_list|,
name|t
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|6
argument_list|,
literal|22
argument_list|,
literal|7
argument_list|)
expr_stmt|;
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
literal|"hello"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"word"
argument_list|,
name|t
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|t
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|6
argument_list|,
literal|22
argument_list|,
literal|"junk"
argument_list|)
expr_stmt|;
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
literal|"hello"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"junk"
argument_list|,
name|t
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testResize
specifier|public
name|void
name|testResize
parameter_list|()
block|{
name|Token
name|t
init|=
operator|new
name|Token
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
name|Token
name|t
init|=
operator|new
name|Token
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
comment|// now as a string, second variant
name|t
operator|=
operator|new
name|Token
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
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|content
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
name|content
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
name|content
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
name|Token
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
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|content
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
name|content
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
comment|// Test for slow growth to a long term
name|t
operator|=
operator|new
name|Token
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
name|String
name|content
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|content
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
name|content
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
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
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
DECL|method|testTermBufferEquals
specifier|public
name|void
name|testTermBufferEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
name|t1a
init|=
operator|new
name|Token
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
name|Token
name|t1b
init|=
operator|new
name|Token
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
name|Token
name|t2
init|=
operator|new
name|Token
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
DECL|method|testMixedStringArray
specifier|public
name|void
name|testMixedStringArray
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
literal|"hello"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|length
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"hello2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|length
argument_list|()
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
literal|"hello3"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
literal|"hello3"
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
name|t
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
literal|'o'
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|,
literal|"hollo3"
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
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|Token
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
name|Payload
name|pl
init|=
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPayload
argument_list|(
name|pl
argument_list|)
expr_stmt|;
name|copy
operator|=
name|assertCloneIsEqual
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pl
argument_list|,
name|copy
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|pl
argument_list|,
name|copy
operator|.
name|getPayload
argument_list|()
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
name|Token
name|t
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|Token
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
name|Token
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
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
name|Payload
name|pl
init|=
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPayload
argument_list|(
name|pl
argument_list|)
expr_stmt|;
name|copy
operator|=
name|assertCopyIsEqual
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pl
argument_list|,
name|copy
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|pl
argument_list|,
name|copy
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|interface|SenselessAttribute
specifier|public
interface|interface
name|SenselessAttribute
extends|extends
name|Attribute
block|{}
DECL|class|SenselessAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|SenselessAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|SenselessAttribute
block|{
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|SenselessAttributeImpl
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|testTokenAttributeFactory
specifier|public
name|void
name|testTokenAttributeFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|Token
operator|.
name|TOKEN_ATTRIBUTE_FACTORY
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"foo bar"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"SenselessAttribute is not implemented by SenselessAttributeImpl"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|SenselessAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|SenselessAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CharTermAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OffsetAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FlagsAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PayloadAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PositionIncrementAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TypeAttribute is not implemented by Token"
argument_list|,
name|ts
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|Token
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
block|}
end_class

end_unit

