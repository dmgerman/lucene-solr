begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AttributeReflector
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
name|io
operator|.
name|StringReader
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

begin_class
annotation|@
name|Deprecated
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
argument_list|(
literal|"hello"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
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
literal|1
argument_list|,
name|t
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|t
operator|.
name|getPositionLength
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
name|assertNull
argument_list|(
name|t
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* the CharTermAttributeStuff is tested by TestCharTermAttributeImpl */
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
argument_list|()
decl_stmt|;
name|t
operator|.
name|setOffset
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
name|Token
name|copy
init|=
name|TestCharTermAttributeImpl
operator|.
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
name|BytesRef
name|pl
init|=
operator|new
name|BytesRef
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
name|TestCharTermAttributeImpl
operator|.
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
name|TestCharTermAttributeImpl
operator|.
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
argument_list|()
expr_stmt|;
name|t
operator|.
name|setOffset
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
name|TestCharTermAttributeImpl
operator|.
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
name|BytesRef
name|pl
init|=
operator|new
name|BytesRef
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
name|TestCharTermAttributeImpl
operator|.
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
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{}
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
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
name|MockTokenizer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|ts
operator|)
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foo bar"
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|testAttributeReflection
specifier|public
name|void
name|testAttributeReflection
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
literal|"foobar"
argument_list|,
literal|6
argument_list|,
literal|22
argument_list|)
decl_stmt|;
name|t
operator|.
name|setFlags
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionLength
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|t
operator|.
name|setTermFrequency
argument_list|(
literal|42
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
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#startOffset"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#endOffset"
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionIncrement"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionLength"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PayloadAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#payload"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TypeAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#type"
argument_list|,
name|TypeAttribute
operator|.
name|DEFAULT_TYPE
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|FlagsAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#flags"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|TermFrequencyAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#termFrequency"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

