begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|zip
operator|.
name|ZipFile
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
comment|/**  * Test the PorterStemFilter with Martin Porter's test data.  */
end_comment

begin_class
DECL|class|TestPorterStemFilter
specifier|public
class|class
name|TestPorterStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Run the stemmer against all strings in voc.txt    * The output should be the same as the string in output.txt    */
DECL|method|testPorterStemFilter
specifier|public
name|void
name|testPorterStemFilter
parameter_list|()
throws|throws
name|Exception
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
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|PorterStemFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|ZipFile
name|zipFile
init|=
operator|new
name|ZipFile
argument_list|(
operator|new
name|File
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"porterTestData.zip"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|voc
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zipFile
operator|.
name|getEntry
argument_list|(
literal|"voc.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|out
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zipFile
operator|.
name|getEntry
argument_list|(
literal|"output.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|vocReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|voc
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|outputReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|inputWord
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|inputWord
operator|=
name|vocReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|expectedWord
init|=
name|outputReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|expectedWord
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|inputWord
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
name|expectedWord
block|}
argument_list|)
expr_stmt|;
block|}
name|vocReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|outputReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|zipFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testWithKeywordAttribute
specifier|public
name|void
name|testWithKeywordAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"yourselves"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"yourselves yours"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|PorterStemFilter
argument_list|(
operator|new
name|KeywordMarkerTokenFilter
argument_list|(
name|tokenizer
argument_list|,
name|set
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"yourselves"
block|,
literal|"your"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

