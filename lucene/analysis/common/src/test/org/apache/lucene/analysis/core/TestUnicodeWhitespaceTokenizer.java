begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|AttributeFactory
import|;
end_import

begin_class
DECL|class|TestUnicodeWhitespaceTokenizer
specifier|public
class|class
name|TestUnicodeWhitespaceTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|// clone of test from WhitespaceTokenizer
DECL|method|testSimple
specifier|public
name|void
name|testSimple
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
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|UnicodeWhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|UnicodeWhitespaceTokenizer
argument_list|()
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
literal|"\ud801\udc1ctest"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNBSP
specifier|public
name|void
name|testNBSP
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
literal|"Tokenizer\u00A0test"
argument_list|)
decl_stmt|;
name|UnicodeWhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|UnicodeWhitespaceTokenizer
argument_list|()
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
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFactory
specifier|public
name|void
name|testFactory
parameter_list|()
block|{
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
literal|"rule"
argument_list|,
literal|"unicode"
argument_list|)
expr_stmt|;
name|WhitespaceTokenizerFactory
name|factory
init|=
operator|new
name|WhitespaceTokenizerFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|AttributeFactory
name|attributeFactory
init|=
name|newAttributeFactory
argument_list|()
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|.
name|create
argument_list|(
name|attributeFactory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UnicodeWhitespaceTokenizer
operator|.
name|class
argument_list|,
name|tokenizer
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

