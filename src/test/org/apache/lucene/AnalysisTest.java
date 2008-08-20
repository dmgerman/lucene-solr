begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SimpleAnalyzer
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
name|Token
import|;
end_import

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
name|BufferedReader
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
name|util
operator|.
name|Date
import|;
end_import

begin_class
DECL|class|AnalysisTest
class|class
name|AnalysisTest
block|{
DECL|field|tmpFile
specifier|static
name|File
name|tmpFile
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|test
argument_list|(
literal|"This is a test"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tmpFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"words"
argument_list|,
literal|".txt"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|tmpFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tmpFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|static
name|void
name|test
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|verbose
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|bytes
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Reading test file containing "
operator|+
name|bytes
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedReader
name|ir
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|ir
argument_list|,
name|verbose
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|static
name|void
name|test
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|verbose
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Tokenizing string: "
operator|+
name|text
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|,
name|verbose
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|static
name|void
name|test
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|long
name|bytes
parameter_list|)
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
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
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Text="
operator|+
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" start="
operator|+
name|nextToken
operator|.
name|startOffset
argument_list|()
operator|+
literal|" end="
operator|+
name|nextToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|time
operator|+
literal|" milliseconds to extract "
operator|+
name|count
operator|+
literal|" tokens"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|time
operator|*
literal|1000.0
operator|)
operator|/
name|count
operator|+
literal|" microseconds/token"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|bytes
operator|*
literal|1000.0
operator|*
literal|60.0
operator|*
literal|60.0
operator|)
operator|/
operator|(
name|time
operator|*
literal|1000000.0
operator|)
operator|+
literal|" megabytes/hour"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

