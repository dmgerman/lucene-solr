begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|Locale
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
name|Analyzer
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
name|Tokenizer
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
name|core
operator|.
name|LetterTokenizer
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
name|core
operator|.
name|LowerCaseTokenizer
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
name|OffsetAttribute
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

begin_comment
comment|/**  * Testcase for {@link CharTokenizer} subclasses  */
end_comment

begin_class
DECL|class|TestCharTokenizers
specifier|public
class|class
name|TestCharTokenizers
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/*    * test to read surrogate pairs without loosing the pairing     * if the surrogate pair is at the border of the internal IO buffer    */
DECL|method|testReadSupplementaryChars
specifier|public
name|void
name|testReadSupplementaryChars
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// create random input
name|int
name|num
init|=
literal|1024
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|num
operator|*=
name|RANDOM_MULTIPLIER
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1cabc"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|10
operator|)
operator|==
literal|0
condition|)
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
comment|// internal buffer size is 1024 make sure we have a surrogate pair right at the border
name|builder
operator|.
name|insert
argument_list|(
literal|1023
argument_list|,
literal|"\ud801\udc1c"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * test to extend the buffer TermAttribute buffer internally. If the internal    * alg that extends the size of the char array only extends by 1 char and the    * next char to be filled in is a supplementary codepoint (using 2 chars) an    * index out of bound exception is triggered.    */
DECL|method|testExtendCharBuffer
specifier|public
name|void
name|testExtendCharBuffer
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1
operator|+
name|i
condition|;
name|j
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1cabc"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * tests the max word length of 255 - tokenizer will split at the 255 char no matter what happens    */
DECL|method|testMaxWordLength
specifier|public
name|void
name|testMaxWordLength
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
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
literal|0
init|;
name|i
operator|<
literal|255
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
block|}
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|+
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * tests the max word length of 255 with a surrogate pair at position 255    */
DECL|method|testMaxWordLengthWithSupplementary
specifier|public
name|void
name|testMaxWordLengthWithSupplementary
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
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
literal|0
init|;
name|i
operator|<
literal|254
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1c"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|+
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3642: normalize SMP->BMP and check that offsets are correct
DECL|method|testCrossPlaneNormalization
specifier|public
name|void
name|testCrossPlaneNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|>
literal|0xffff
condition|)
block|{
return|return
literal|'Î´'
return|;
block|}
else|else
block|{
return|return
name|c
return|;
block|}
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|num
init|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
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
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|s
argument_list|)
init|)
block|{
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|highlightedText
init|=
name|s
operator|.
name|substring
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|j
operator|<
name|highlightedText
operator|.
name|length
argument_list|()
condition|;
name|j
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|cp
operator|=
name|highlightedText
operator|.
name|codePointAt
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"non-letter:"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|cp
argument_list|)
argument_list|,
name|Character
operator|.
name|isLetter
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
comment|// just for fun
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3642: normalize BMP->SMP and check that offsets are correct
DECL|method|testCrossPlaneNormalization2
specifier|public
name|void
name|testCrossPlaneNormalization2
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|<=
literal|0xffff
condition|)
block|{
return|return
literal|0x1043C
return|;
block|}
else|else
block|{
return|return
name|c
return|;
block|}
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|num
init|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
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
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|s
argument_list|)
init|)
block|{
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|highlightedText
init|=
name|s
operator|.
name|substring
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|j
operator|<
name|highlightedText
operator|.
name|length
argument_list|()
condition|;
name|j
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|cp
operator|=
name|highlightedText
operator|.
name|codePointAt
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"non-letter:"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|cp
argument_list|)
argument_list|,
name|Character
operator|.
name|isLetter
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
comment|// just for fun
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefinitionUsingMethodReference1
specifier|public
name|void
name|testDefinitionUsingMethodReference1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer Test"
argument_list|)
decl_stmt|;
specifier|final
name|Tokenizer
name|tokenizer
init|=
name|CharTokenizer
operator|.
name|fromSeparatorCharPredicate
argument_list|(
name|Character
operator|::
name|isWhitespace
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Tokenizer"
block|,
literal|"Test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefinitionUsingMethodReference2
specifier|public
name|void
name|testDefinitionUsingMethodReference2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer(Test)"
argument_list|)
decl_stmt|;
specifier|final
name|Tokenizer
name|tokenizer
init|=
name|CharTokenizer
operator|.
name|fromTokenCharPredicate
argument_list|(
name|Character
operator|::
name|isLetter
argument_list|,
name|Character
operator|::
name|toUpperCase
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TOKENIZER"
block|,
literal|"TEST"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefinitionUsingLambda
specifier|public
name|void
name|testDefinitionUsingLambda
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer\u00A0Test Foo"
argument_list|)
decl_stmt|;
specifier|final
name|Tokenizer
name|tokenizer
init|=
name|CharTokenizer
operator|.
name|fromSeparatorCharPredicate
argument_list|(
name|c
lambda|->
name|c
operator|==
literal|'\u00A0'
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
argument_list|)
argument_list|,
name|Character
operator|::
name|toLowerCase
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tokenizer"
block|,
literal|"test"
block|,
literal|"foo"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

