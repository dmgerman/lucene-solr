begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link RussianLetterTokenizer}  * @deprecated Remove this test class in Lucene 4.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|TestRussianLetterTokenizer
specifier|public
class|class
name|TestRussianLetterTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testRussianLetterTokenizer
specifier|public
name|void
name|testRussianLetterTokenizer
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
literal|"1234567890 ÐÐ¼ÐµÑÑÐµ \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|tokenizer
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1234567890"
block|,
literal|"ÐÐ¼ÐµÑÑÐµ"
block|,
literal|"\ud801\udc1ctest"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRussianLetterTokenizerBWCompat
specifier|public
name|void
name|testRussianLetterTokenizerBWCompat
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
literal|"1234567890 ÐÐ¼ÐµÑÑÐµ \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|tokenizer
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1234567890"
block|,
literal|"ÐÐ¼ÐµÑÑÐµ"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

