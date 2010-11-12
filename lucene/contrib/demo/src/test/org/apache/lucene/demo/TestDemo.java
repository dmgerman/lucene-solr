begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
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
name|ByteArrayOutputStream
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
name|PrintStream
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestDemo
specifier|public
class|class
name|TestDemo
extends|extends
name|LuceneTestCase
block|{
comment|// LUCENE-589
DECL|method|testUnicodeHtml
specifier|public
name|void
name|testUnicodeHtml
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
name|getDataFile
argument_list|(
literal|"test-files/html"
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"demoIndex"
argument_list|)
decl_stmt|;
name|IndexHTML
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-create"
block|,
literal|"-index"
block|,
name|indexDir
operator|.
name|getPath
argument_list|()
block|,
name|dir
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|File
name|queries
init|=
name|getDataFile
argument_list|(
literal|"test-files/queries.txt"
argument_list|)
decl_stmt|;
name|PrintStream
name|outSave
init|=
name|System
operator|.
name|out
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|fakeSystemOut
init|=
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|fakeSystemOut
argument_list|)
expr_stmt|;
name|SearchFiles
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-index"
block|,
name|indexDir
operator|.
name|getPath
argument_list|()
block|,
literal|"-queries"
block|,
name|queries
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|fakeSystemOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|bytes
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// intentionally use default encoding
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"1 total matching documents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|outSave
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-591
DECL|method|testIndexKeywords
specifier|public
name|void
name|testIndexKeywords
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
name|getDataFile
argument_list|(
literal|"test-files/html"
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"demoIndex2"
argument_list|)
decl_stmt|;
name|IndexHTML
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-create"
block|,
literal|"-index"
block|,
name|indexDir
operator|.
name|getPath
argument_list|()
block|,
name|dir
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|File
name|queries
init|=
name|getDataFile
argument_list|(
literal|"test-files/queries2.txt"
argument_list|)
decl_stmt|;
name|PrintStream
name|outSave
init|=
name|System
operator|.
name|out
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|fakeSystemOut
init|=
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|fakeSystemOut
argument_list|)
expr_stmt|;
name|SearchFiles
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-index"
block|,
name|indexDir
operator|.
name|getPath
argument_list|()
block|,
literal|"-queries"
block|,
name|queries
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|fakeSystemOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|bytes
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// intentionally use default encoding
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"1 total matching documents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|outSave
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

