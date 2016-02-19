begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|MockTokenizer
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

begin_comment
comment|/** basic tests for {@link ICUTransformFilterFactory} */
end_comment

begin_class
DECL|class|TestICUTransformFilterFactory
specifier|public
class|class
name|TestICUTransformFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** ensure the transform is working */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ç°¡åå­"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"Traditional-Simplified"
argument_list|)
expr_stmt|;
name|ICUTransformFilterFactory
name|factory
init|=
operator|new
name|ICUTransformFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ç®åå­"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** test forward and reverse direction */
DECL|method|testForwardDirection
specifier|public
name|void
name|testForwardDirection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// forward
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Ð Ð¾ÑÑÐ¸Ð¹ÑÐºÐ°Ñ Ð¤ÐµÐ´ÐµÑÐ°ÑÐ¸Ñ"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"Cyrillic-Latin"
argument_list|)
expr_stmt|;
name|ICUTransformFilterFactory
name|factory
init|=
operator|new
name|ICUTransformFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"RossijskaÃ¢"
block|,
literal|"FederaciÃ¢"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseDirection
specifier|public
name|void
name|testReverseDirection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// backward (invokes Latin-Cyrillic)
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"RossijskaÃ¢ FederaciÃ¢"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"Cyrillic-Latin"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"direction"
argument_list|,
literal|"reverse"
argument_list|)
expr_stmt|;
name|ICUTransformFilterFactory
name|factory
init|=
operator|new
name|ICUTransformFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð Ð¾ÑÑÐ¸Ð¹ÑÐºÐ°Ñ"
block|,
literal|"Ð¤ÐµÐ´ÐµÑÐ°ÑÐ¸Ñ"
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
name|ICUTransformFilterFactory
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
literal|"id"
argument_list|,
literal|"Null"
argument_list|)
expr_stmt|;
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

