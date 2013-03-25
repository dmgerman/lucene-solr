begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|KeywordTokenizer
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
name|WhitespaceTokenizer
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
name|en
operator|.
name|PorterStemFilter
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
name|miscellaneous
operator|.
name|StemmerOverrideFilter
operator|.
name|StemmerOverrideMap
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|TestStemmerOverrideFilter
specifier|public
class|class
name|TestStemmerOverrideFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testOverride
specifier|public
name|void
name|testOverride
parameter_list|()
throws|throws
name|IOException
block|{
comment|// lets make booked stem to books
comment|// the override filter will convert "booked" to "books",
comment|// but also mark it with KeywordAttribute so Porter will not change it.
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"booked"
argument_list|,
literal|"books"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"booked"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|StemmerOverrideFilter
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
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
literal|"books"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreCase
specifier|public
name|void
name|testIgnoreCase
parameter_list|()
throws|throws
name|IOException
block|{
comment|// lets make booked stem to books
comment|// the override filter will convert "booked" to "books",
comment|// but also mark it with KeywordAttribute so Porter will not change it.
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"boOkEd"
argument_list|,
literal|"books"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BooKeD"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|StemmerOverrideFilter
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
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
literal|"books"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoOverrides
specifier|public
name|void
name|testNoOverrides
parameter_list|()
throws|throws
name|IOException
block|{
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"book"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|StemmerOverrideFilter
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
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
literal|"book"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomRealisticWhiteSpace
specifier|public
name|void
name|testRandomRealisticWhiteSpace
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|atLeast
argument_list|(
literal|50
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|randomRealisticUnicodeString
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|char
index|[]
name|charArray
init|=
name|randomRealisticUnicodeString
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
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
name|charArray
operator|.
name|length
condition|;
control|)
block|{
name|int
name|cp
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|charArray
argument_list|,
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Character
operator|.
name|isWhitespace
argument_list|(
name|cp
argument_list|)
condition|)
block|{
name|builder
operator|.
name|appendCodePoint
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
name|j
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
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|value
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|value
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"a"
else|:
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"booked"
argument_list|,
literal|"books"
argument_list|)
expr_stmt|;
block|}
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
init|=
name|map
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|StringBuilder
name|input
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|output
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|||
name|output
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|input
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|StemmerOverrideFilter
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|output
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomRealisticKeyword
specifier|public
name|void
name|testRandomRealisticKeyword
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|atLeast
argument_list|(
literal|50
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|randomRealisticUnicodeString
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomRealisticUnicodeString
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|value
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|randomRealisticUnicodeString
argument_list|,
name|value
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"a"
else|:
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"booked"
argument_list|,
literal|"books"
argument_list|)
expr_stmt|;
block|}
name|StemmerOverrideFilter
operator|.
name|Builder
name|builder
init|=
operator|new
name|StemmerOverrideFilter
operator|.
name|Builder
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
init|=
name|map
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StemmerOverrideMap
name|build
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|StemmerOverrideFilter
argument_list|(
name|tokenizer
argument_list|,
name|build
argument_list|)
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
name|entry
operator|.
name|getValue
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

