begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BaseTokenStreamTestCase
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
name|TokenStream
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
name|standard
operator|.
name|StandardTokenizer
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
name|CharTermAttribute
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
name|PositionIncrementAttribute
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
name|TypeAttribute
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
name|English
import|;
end_import

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
name|Collections
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

begin_class
DECL|class|TestTypeTokenFilter
specifier|public
class|class
name|TestTypeTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testTypeFilter
specifier|public
name|void
name|testTypeFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"121 is palindrome, while 123 is not"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
init|=
name|asSet
argument_list|(
literal|"<NUM>"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|TypeTokenFilter
argument_list|(
literal|true
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
argument_list|,
name|stopTypes
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"is"
block|,
literal|"palindrome"
block|,
literal|"while"
block|,
literal|"is"
block|,
literal|"not"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test Position increments applied by TypeTokenFilter with and without enabling this option.    */
DECL|method|testStopPositons
specifier|public
name|void
name|testStopPositons
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|3
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|w
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|w
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|log
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|stopTypes
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stopSet
init|=
name|asSet
argument_list|(
name|stopTypes
argument_list|)
decl_stmt|;
comment|// with increments
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TypeTokenFilter
name|typeTokenFilter
init|=
operator|new
name|TypeTokenFilter
argument_list|(
literal|true
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
argument_list|,
name|stopSet
argument_list|)
decl_stmt|;
name|testPositons
argument_list|(
name|typeTokenFilter
argument_list|)
expr_stmt|;
comment|// without increments
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|typeTokenFilter
operator|=
operator|new
name|TypeTokenFilter
argument_list|(
literal|false
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|testPositons
argument_list|(
name|typeTokenFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testPositons
specifier|private
name|void
name|testPositons
parameter_list|(
name|TypeTokenFilter
name|stpf
parameter_list|)
throws|throws
name|IOException
block|{
name|TypeAttribute
name|typeAtt
init|=
name|stpf
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAttribute
init|=
name|stpf
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stpf
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stpf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|boolean
name|enablePositionIncrements
init|=
name|stpf
operator|.
name|getEnablePositionIncrements
argument_list|()
decl_stmt|;
while|while
condition|(
name|stpf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|log
argument_list|(
literal|"Token: "
operator|+
name|termAttribute
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|typeAtt
operator|.
name|type
argument_list|()
operator|+
literal|" - "
operator|+
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"if position increment is enabled the positionIncrementAttribute value should be 3, otherwise 1"
argument_list|,
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|enablePositionIncrements
condition|?
literal|3
else|:
literal|1
argument_list|)
expr_stmt|;
block|}
name|stpf
operator|.
name|end
argument_list|()
expr_stmt|;
name|stpf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTypeFilterWhitelist
specifier|public
name|void
name|testTypeFilterWhitelist
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"121 is palindrome, while 123 is not"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
init|=
name|Collections
operator|.
name|singleton
argument_list|(
literal|"<NUM>"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|TypeTokenFilter
argument_list|(
literal|true
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
argument_list|,
name|stopTypes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"121"
block|,
literal|"123"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// print debug info depending on VERBOSE
DECL|method|log
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

