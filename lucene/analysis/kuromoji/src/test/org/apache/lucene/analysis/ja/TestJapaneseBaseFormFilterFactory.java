begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
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

begin_comment
comment|/**  * Simple tests for {@link JapaneseBaseFormFilterFactory}  */
end_comment

begin_class
DECL|class|TestJapaneseBaseFormFilterFactory
specifier|public
class|class
name|TestJapaneseBaseFormFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizerFactory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
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
literal|"ããã¯ã¾ã å®é¨æ®µéã«ããã¾ã"
argument_list|)
argument_list|)
expr_stmt|;
name|JapaneseBaseFormFilterFactory
name|factory
init|=
operator|new
name|JapaneseBaseFormFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|ts
operator|=
name|factory
operator|.
name|create
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ã¯"
block|,
literal|"ã¾ã "
block|,
literal|"å®é¨"
block|,
literal|"æ®µé"
block|,
literal|"ã«"
block|,
literal|"ãã"
block|,
literal|"ã¾ã"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|JapaneseBaseFormFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

