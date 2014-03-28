begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for FileListEntityProcessor  *</p>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestFileListEntityProcessor
specifier|public
class|class
name|TestFileListEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpdir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolver
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|FileListEntityProcessor
name|fileListEntityProcessor
init|=
operator|new
name|FileListEntityProcessor
argument_list|()
decl_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBiggerSmallerFiles
specifier|public
name|void
name|testBiggerSmallerFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpdir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|tmpdir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|long
name|minLength
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|String
name|smallestFile
init|=
literal|""
decl_stmt|;
name|byte
index|[]
name|content
init|=
literal|"abcdefgij"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
name|content
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|minLength
operator|>
name|content
operator|.
name|length
condition|)
block|{
name|minLength
operator|=
name|content
operator|.
name|length
expr_stmt|;
name|smallestFile
operator|=
literal|"a.xml"
expr_stmt|;
block|}
name|content
operator|=
literal|"abcdefgij"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
name|content
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|minLength
operator|>
name|content
operator|.
name|length
condition|)
block|{
name|minLength
operator|=
name|content
operator|.
name|length
expr_stmt|;
name|smallestFile
operator|=
literal|"b.xml"
expr_stmt|;
block|}
name|content
operator|=
literal|"abc"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
name|content
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|minLength
operator|>
name|content
operator|.
name|length
condition|)
block|{
name|minLength
operator|=
name|content
operator|.
name|length
expr_stmt|;
name|smallestFile
operator|=
literal|"c.props"
expr_stmt|;
block|}
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".*"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|BIGGER_THAN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|minLength
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
name|getFiles
argument_list|(
literal|null
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|fList
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".*"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|SMALLER_THAN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|minLength
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fList
operator|=
name|getFiles
argument_list|(
literal|null
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|l
operator|.
name|clear
argument_list|()
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
name|smallestFile
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|fList
argument_list|)
argument_list|)
expr_stmt|;
name|attrs
operator|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".*"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|SMALLER_THAN
argument_list|,
literal|"${a.x}"
argument_list|)
expr_stmt|;
name|VariableResolver
name|resolver
init|=
operator|new
name|VariableResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"a"
argument_list|,
name|createMap
argument_list|(
literal|"x"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|fList
operator|=
name|getFiles
argument_list|(
name|resolver
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|fList
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getFiles
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getFiles
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|Map
name|attrs
parameter_list|)
block|{
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|FileListEntityProcessor
name|fileListEntityProcessor
init|=
operator|new
name|FileListEntityProcessor
argument_list|()
decl_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fList
return|;
block|}
annotation|@
name|Test
DECL|method|testNTOT
specifier|public
name|void
name|testNTOT
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpdir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|OLDER_THAN
argument_list|,
literal|"'NOW'"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
name|getFiles
argument_list|(
literal|null
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|NEWER_THAN
argument_list|,
literal|"'NOW-2HOURS'"
argument_list|)
expr_stmt|;
name|fList
operator|=
name|getFiles
argument_list|(
literal|null
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use a variable for newerThan
name|attrs
operator|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|NEWER_THAN
argument_list|,
literal|"${a.x}"
argument_list|)
expr_stmt|;
name|VariableResolver
name|resolver
init|=
operator|new
name|VariableResolver
argument_list|()
decl_stmt|;
name|String
name|lastMod
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|50000
argument_list|)
argument_list|)
decl_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"a"
argument_list|,
name|createMap
argument_list|(
literal|"x"
argument_list|,
name|lastMod
argument_list|)
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"t.xml"
argument_list|,
literal|"t.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fList
operator|=
name|getFiles
argument_list|(
name|resolver
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"File name must be t.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
literal|"t.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|fList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRECURSION
specifier|public
name|void
name|testRECURSION
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpdir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|File
name|childdir
init|=
operator|new
name|File
argument_list|(
name|tmpdir
operator|+
literal|"/child"
argument_list|)
decl_stmt|;
name|childdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"^.*\\.xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|childdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|RECURSIVE
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
name|getFiles
argument_list|(
literal|null
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

