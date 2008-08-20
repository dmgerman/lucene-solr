begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
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
name|StringReader
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Token
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
comment|/**  * Test case for ThaiAnalyzer, modified from TestFrenchAnalyzer  *  * @author    Samphan Raruenrom<samphan@osdev.co.th>  * @version   0.1  */
end_comment

begin_class
DECL|class|TestThaiAnalyzer
specifier|public
class|class
name|TestThaiAnalyzer
extends|extends
name|TestCase
block|{
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|nextToken
init|=
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
argument_list|,
name|output
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸´à¸©à¸±à¸"
block|,
literal|"à¸à¸·à¹à¸­"
block|,
literal|"xy&z"
block|,
literal|"à¸à¸¸à¸¢"
block|,
literal|"à¸à¸±à¸"
block|,
literal|"xyz@demo.com"
block|}
argument_list|)
expr_stmt|;
comment|// English stop words
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸°à¹à¸¢à¸à¸§à¹à¸² The quick brown fox jumped over the lazy dogs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸°à¹à¸¢à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"lazy"
block|,
literal|"dogs"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

