begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the English minimal stem factory is working.  */
end_comment

begin_class
DECL|class|TestEnglishMinimalStemFilterFactory
specifier|public
class|class
name|TestEnglishMinimalStemFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testStemming
specifier|public
name|void
name|testStemming
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
literal|"bricks"
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
name|tokenFilterFactory
argument_list|(
literal|"EnglishMinimalStem"
argument_list|)
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
literal|"brick"
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
name|tokenFilterFactory
argument_list|(
literal|"EnglishMinimalStem"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
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

