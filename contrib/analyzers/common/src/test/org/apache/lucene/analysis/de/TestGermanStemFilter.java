begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
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
name|FileInputStream
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
name|LowerCaseFilter
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
name|TokenFilter
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Test the German stemmer. The stemming algorithm is known to work less   * than perfect, as it doesn't use any word lists with exceptions. We   * also check some of the cases where the algorithm is wrong.  *  */
end_comment

begin_class
DECL|class|TestGermanStemFilter
specifier|public
class|class
name|TestGermanStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testStemming
specifier|public
name|void
name|testStemming
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
name|TokenFilter
name|filter
init|=
operator|new
name|GermanStemFilter
argument_list|(
operator|new
name|LowerCaseFilter
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|tokenizer
argument_list|)
argument_list|)
decl_stmt|;
comment|// read test cases from external file:
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"org/apache/lucene/analysis/de/data.txt"
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
literal|"iso-8859-1"
argument_list|)
decl_stmt|;
name|BufferedReader
name|breader
init|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|breader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
break|break;
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|line
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
continue|continue;
comment|// ignore comments and empty lines
name|String
index|[]
name|parts
init|=
name|line
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
comment|//System.out.println(parts[0] + " -- " + parts[1]);
name|tokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|parts
index|[
literal|0
index|]
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
name|parts
index|[
literal|1
index|]
block|}
argument_list|)
expr_stmt|;
block|}
name|breader
operator|.
name|close
argument_list|()
expr_stmt|;
name|isr
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

