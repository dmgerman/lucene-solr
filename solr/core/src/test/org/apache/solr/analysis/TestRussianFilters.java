begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Tokenizer
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the Russian filter factories are working.  */
end_comment

begin_class
DECL|class|TestRussianFilters
specifier|public
class|class
name|TestRussianFilters
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test RussianLetterTokenizerFactory    */
DECL|method|testTokenizer
specifier|public
name|void
name|testTokenizer
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
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ 100"
argument_list|)
decl_stmt|;
name|RussianLetterTokenizerFactory
name|factory
init|=
operator|new
name|RussianLetterTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|DEFAULT_VERSION
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
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
literal|"ÐÐ¼ÐµÑÑÐµ"
block|,
literal|"Ñ"
block|,
literal|"ÑÐµÐ¼"
block|,
literal|"Ð¾"
block|,
literal|"ÑÐ¸Ð»Ðµ"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹"
block|,
literal|"100"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

