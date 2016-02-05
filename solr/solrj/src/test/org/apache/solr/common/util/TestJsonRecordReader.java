begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RecordingJSONParser
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_class
DECL|class|TestJsonRecordReader
specifier|public
class|class
name|TestJsonRecordReader
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testOneLevelSplit
specifier|public
name|void
name|testOneLevelSplit
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|" \"a\":\"A\" ,\n"
operator|+
literal|" \"b\":[\n"
operator|+
literal|"     {\"c\":\"C\",\"d\":\"D\" ,\"e\": {\n"
operator|+
literal|"                         \"s\":\"S\",\n"
operator|+
literal|"                         \"t\":3}},\n"
operator|+
literal|"     {\"c\":\"C1\",\"d\":\"D1\"},\n"
operator|+
literal|"     {\"c\":\"C2\",\"d\":\"D2\"}\n"
operator|+
literal|" ]\n"
operator|+
literal|"}"
decl_stmt|;
comment|//    System.out.println(json);
comment|//    All parameters are mapped with field name
name|JsonRecordReader
name|streamer
init|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"e_s:/b/e/s"
argument_list|,
literal|"e_i:/b/e/t"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
init|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"D2"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"d_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"e_i"
argument_list|)
argument_list|)
expr_stmt|;
comment|//    All parameters but /b/c is omitted
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a:/a"
argument_list|,
literal|"d:/b/d"
argument_list|,
literal|"s:/b/e/s"
argument_list|,
literal|"t:/b/e/t"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//one nested /b/e/* object is completely ignored
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a:/a"
argument_list|,
literal|"c:/b/c"
argument_list|,
literal|"d:/b/d"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//nested /b/e/* object is completely ignored even though /b/e is mapped
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"e:/b/e"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a_s:/a"
argument_list|,
literal|"c_s:/b/c"
argument_list|,
literal|"d_s:/b/d"
argument_list|,
literal|"/b/e/*"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveWildCard
specifier|public
name|void
name|testRecursiveWildCard
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|" \"a\":\"A\" ,\n"
operator|+
literal|" \"b\":[\n"
operator|+
literal|"     {\"c\":\"C\",\"d\":\"D\" ,\"e\": {\n"
operator|+
literal|"                         \"s\":\"S\",\n"
operator|+
literal|"                         \"t\":3 ,\"u\":{\"v\":3.1234,\"w\":false}}},\n"
operator|+
literal|"     {\"c\":\"C1\",\"d\":\"D1\"},\n"
operator|+
literal|"     {\"c\":\"C2\",\"d\":\"D2\"}\n"
operator|+
literal|" ]\n"
operator|+
literal|"}"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/b"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/b/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3.1234
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|false
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"w"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertNotNull
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|3l
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"S"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|"A"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"records "
operator|+
name|records
argument_list|,
literal|false
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"w"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveWildcard2
specifier|public
name|void
name|testRecursiveWildcard2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"first\": \"John\",\n"
operator|+
literal|"  \"last\": \"Doe\",\n"
operator|+
literal|"  \"grade\": 8,\n"
operator|+
literal|"  \"exams\": [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"subject\": \"Maths\",\n"
operator|+
literal|"        \"test\"   : \"term1\",\n"
operator|+
literal|"        \"marks\":90},\n"
operator|+
literal|"        {\n"
operator|+
literal|"         \"subject\": \"Biology\",\n"
operator|+
literal|"         \"test\"   : \"term1\",\n"
operator|+
literal|"         \"marks\":86}\n"
operator|+
literal|"      ]\n"
operator|+
literal|"}"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/exams"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|record
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"marks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/exams"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"$FQN:/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|record
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record
operator|.
name|containsKey
argument_list|(
literal|"exams.marks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"txt:/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
operator|(
operator|(
name|List
operator|)
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"txt"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedJsonWithFloats
specifier|public
name|void
name|testNestedJsonWithFloats
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"        \"a_string\" : \"abc\",\n"
operator|+
literal|"        \"a_num\" : 2.0,\n"
operator|+
literal|"        \"a\" : {\n"
operator|+
literal|"                        \"b\" : [\n"
operator|+
literal|"                                {\"id\":\"1\", \"title\" : \"test1\"},\n"
operator|+
literal|"                                {\"id\":\"2\", \"title\" : \"test2\"}\n"
operator|+
literal|"                        ]\n"
operator|+
literal|"                }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/a/b"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"title_s:/a/b/title"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testClearPreviousRecordFields
specifier|public
name|void
name|testClearPreviousRecordFields
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"'first': 'John',\n"
operator|+
literal|"'exams': [\n"
operator|+
literal|"{'subject': 'Maths', 'test'   : 'term1', 'marks':90},\n"
operator|+
literal|"{'subject': 'Biology', 'test'   : 'term1', 'marks':86}\n"
operator|+
literal|"]\n"
operator|+
literal|"}\n"
operator|+
literal|"{\n"
operator|+
literal|"'first': 'Bob',\n"
operator|+
literal|"'exams': [\n"
operator|+
literal|"{'subject': 'Maths', 'test': 'term1', 'marks': 95\n"
operator|+
literal|"}\n"
operator|+
literal|",\n"
operator|+
literal|"{\n"
operator|+
literal|"'subject': 'Biology', 'test'   : 'term1', 'marks': 92}\n"
operator|+
literal|"]\n"
operator|+
literal|"}"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/exams"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
expr_stmt|;
name|records
operator|=
name|streamer
operator|.
name|getAllRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
range|:
name|records
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|record
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testArrayOfRootObjects
specifier|public
name|void
name|testArrayOfRootObjects
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"[{'fieldA':'A1'}, {'fieldB':'B2'}]"
decl_stmt|;
name|JsonRecordReader
name|streamer
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|records
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|WeakReference
argument_list|<
name|String
argument_list|>
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|streamer
operator|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"$FQN:/**"
argument_list|)
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|streamRecords
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|,
operator|new
name|JsonRecordReader
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
if|if
condition|(
name|ref
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"This reference is still intact :"
operator|+
name|ref
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ref
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|fName
init|=
name|record
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|ref
operator|.
name|set
argument_list|(
operator|new
name|WeakReference
argument_list|<
name|String
argument_list|>
argument_list|(
name|fName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAIOOBE
specifier|public
name|void
name|testAIOOBE
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"[   {\n"
operator|+
literal|"      \"taxon_group\" : {\n"
operator|+
literal|"         \"identifiers\" : {\n"
operator|+
literal|"            \"bioentry_id\" : 1876284,\n"
operator|+
literal|"            \"namespace\" : \"NCBI\",\n"
operator|+
literal|"            \"primary_id\" : \"AAAAA_ID_19303\",\n"
operator|+
literal|"            \"version\" : null,\n"
operator|+
literal|"            \"name\" : \"AAAAA_ID_19303\",\n"
operator|+
literal|"            \"description\" : \"Taxon group for NCBI taxon 1286265\",\n"
operator|+
literal|"            \"accession\" : \"AAAAA_ID_19303\"\n"
operator|+
literal|"         },\n"
operator|+
literal|"         \"source\" : [\n"
operator|+
literal|"            {\n"
operator|+
literal|"               \"ID\" : \"AAAAA_ID_19303_0\",\n"
operator|+
literal|"               \"segment_group_serotype\" : \"H3\",\n"
operator|+
literal|"               \"primary_key\" : 11877892,\n"
operator|+
literal|"               \"name\" : \"segment_group\",\n"
operator|+
literal|"               \"segment_group_NA_subtype\" : \"\",\n"
operator|+
literal|"               \"segmentgroup_sequence_count\" : \"1\",\n"
operator|+
literal|"               \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"               \"segment_group_submitter_lab\" : \"Microbiology, Faculty of Medicine Sebelas Maret University, Jl. Ir. Sutami 36A, Surakarta, Jawa Tengah 57126, Indonesia\",\n"
operator|+
literal|"               \"strand\" : \"1\",\n"
operator|+
literal|"               \"segment_group_submission_date\" : \"22-JAN-2013\",\n"
operator|+
literal|"               \"segment_group_HA_subtype\" : \"H3\",\n"
operator|+
literal|"               \"start-end\" : \"1..260\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            {\n"
operator|+
literal|"               \"taxon_group_serotype\" : \"H3\",\n"
operator|+
literal|"               \"taxon_group_sequence_count\" : \"1\",\n"
operator|+
literal|"               \"ID\" : \"AAAAA_ID_19303\",\n"
operator|+
literal|"               \"primary_key\" : 11877893,\n"
operator|+
literal|"               \"name\" : \"source\",\n"
operator|+
literal|"               \"db_xref\" : [\n"
operator|+
literal|"                  \"taxon:1286265\"\n"
operator|+
literal|"               ],\n"
operator|+
literal|"               \"taxon_group_HA_subtype\" : \"H3\",\n"
operator|+
literal|"               \"taxon_group_segment_group_count\" : \"1\",\n"
operator|+
literal|"               \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"               \"strand\" : \"1\",\n"
operator|+
literal|"               \"taxon_group_NA_subtype\" : \"\",\n"
operator|+
literal|"               \"start-end\" : \"1..260\"\n"
operator|+
literal|"            }\n"
operator|+
literal|"         ],\n"
operator|+
literal|"         \"segment_groups\" : [\n"
operator|+
literal|"            {\n"
operator|+
literal|"               \"identifiers\" : {\n"
operator|+
literal|"                  \"bioentry_id\" : 1876283,\n"
operator|+
literal|"                  \"namespace\" : \"NCBI\",\n"
operator|+
literal|"                  \"primary_id\" : \"AAAAA_ID_19303_0\",\n"
operator|+
literal|"                  \"version\" : null,\n"
operator|+
literal|"                  \"name\" : \"AAAAA_ID_19303_0\",\n"
operator|+
literal|"                  \"description\" : \"Segment group 0 for AAAAA_ID_19303 (NCBI taxon 1286265)\",\n"
operator|+
literal|"                  \"accession\" : \"AAAAA_ID_19303_0\"\n"
operator|+
literal|"               },\n"
operator|+
literal|"               \"source\" : [\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"ID\" : \"KC513508\",\n"
operator|+
literal|"                     \"primary_key\" : 22483564,\n"
operator|+
literal|"                     \"nucleotide_gi\" : \"451898947\",\n"
operator|+
literal|"                     \"name\" : \"gene\",\n"
operator|+
literal|"                     \"HA_subtype\" : \"H3\",\n"
operator|+
literal|"                     \"segment\" : \"4\",\n"
operator|+
literal|"                     \"collection_date\" : \"22-Mar-2010\",\n"
operator|+
literal|"                     \"NA_subtype\" : \"N2\",\n"
operator|+
literal|"                     \"subtype\" : \"H3\",\n"
operator|+
literal|"                     \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"                     \"standardized_collection_date\" : \"2010-03-22T00:00:00Z\",\n"
operator|+
literal|"                     \"segment_name\" : \"HA\",\n"
operator|+
literal|"                     \"strand\" : \"1\",\n"
operator|+
literal|"                     \"serotype\" : \"H3N2\",\n"
operator|+
literal|"                     \"ncbi_accession\" : \"KC513508\",\n"
operator|+
literal|"                     \"start-end\" : \"1..260\"\n"
operator|+
literal|"                  },\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"mol_type\" : \"viral cRNA\",\n"
operator|+
literal|"                     \"taxonomy_strain\" : \"A/Surakarta/1/2010\",\n"
operator|+
literal|"                     \"identified_by\" : \"Afiono Agung Prasetyo\",\n"
operator|+
literal|"                     \"HA_subtype\" : \"H3\",\n"
operator|+
literal|"                     \"collection_date\" : \"22-Mar-2010\",\n"
operator|+
literal|"                     \"standardized_collection_date\" : \"2010-03-22T00:00:00Z\",\n"
operator|+
literal|"                     \"strand\" : \"1\",\n"
operator|+
literal|"                     \"serotype\" : \"H3N2\",\n"
operator|+
literal|"                     \"organism\" : \"Influenza A virus (A/Surakarta/1/2010(H3N2))\",\n"
operator|+
literal|"                     \"country\" : \"Indonesia\",\n"
operator|+
literal|"                     \"primary_key\" : 22483566,\n"
operator|+
literal|"                     \"isolation_source\" : \"nasal and throat swab\",\n"
operator|+
literal|"                     \"collected_by\" : \"Afiono Agung Prasetyo\",\n"
operator|+
literal|"                     \"name\" : \"source\",\n"
operator|+
literal|"                     \"flu_type\" : \"A\",\n"
operator|+
literal|"                     \"host\" : \"Homo sapiens\",\n"
operator|+
literal|"                     \"db_xref\" : [\n"
operator|+
literal|"                        \"taxon:1286265\"\n"
operator|+
literal|"                     ],\n"
operator|+
literal|"                     \"NA_subtype\" : \"N2\",\n"
operator|+
literal|"                     \"strain\" : \"A/Surakarta/1/2010\",\n"
operator|+
literal|"                     \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"                     \"start-end\" : \"1..260\"\n"
operator|+
literal|"                  },\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"primary_key\" : 22483572,\n"
operator|+
literal|"                     \"name\" : \"standard_host\",\n"
operator|+
literal|"                     \"Host_NCBI_taxon_id\" : \"9605\",\n"
operator|+
literal|"                     \"curation_status_code\" : \"150\",\n"
operator|+
literal|"                     \"curation_date\" : \"2015-01-12\",\n"
operator|+
literal|"                     \"program_version\" : \"v1.1.7\",\n"
operator|+
literal|"                     \"source_tag\" : \"parse_host_v1\",\n"
operator|+
literal|"                     \"strand\" : \"1\",\n"
operator|+
literal|"                     \"curation_status_message\" : \"success, archive and add on next major program revision\",\n"
operator|+
literal|"                     \"start-end\" : \"1..260\",\n"
operator|+
literal|"                     \"curation_status\" : \"true\"\n"
operator|+
literal|"                  },\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"primary_key\" : 22483576,\n"
operator|+
literal|"                     \"Location_Lat_Long\" : \"-0.7892749906,113.9213256836\",\n"
operator|+
literal|"                     \"name\" : \"standardized_location\",\n"
operator|+
literal|"                     \"Location_Country_Alpha2\" : \"ID\",\n"
operator|+
literal|"                     \"curation_status_code\" : \"150\",\n"
operator|+
literal|"                     \"curation_date\" : \"2015-01-15\",\n"
operator|+
literal|"                     \"program_version\" : \"v0.1\",\n"
operator|+
literal|"                     \"source_tag\" : \"xxxxx_parse_location_v0\",\n"
operator|+
literal|"                     \"strand\" : \"1\",\n"
operator|+
literal|"                     \"curation_status_message\" : \"success, archive and add on next major program revision\",\n"
operator|+
literal|"                     \"start-end\" : 1,\n"
operator|+
literal|"                     \"curation_status\" : \"true\"\n"
operator|+
literal|"                  }\n"
operator|+
literal|"               ],\n"
operator|+
literal|"               \"sequence\" : {\n"
operator|+
literal|"                  \"string\" : \"CCCTTATGATGTGCCGGATTATGCCTCCCTTAGGTCACTAGTTGCCTCATCCGGCACACTGGAGTTTAACAGTGAAAGCTTCAATTGGACTGGAGTCACTCAAAACGGAACAAGCTCTGCTTGCATAAGGAGATCTAATAATAGTTTCTTTAGTAGATTGAATTGGTTGACCCACTTAAACTTCAAATACCCAGCATTGAACGTGACTATGCCAAACAATGAACAATTTGACAAATTGTACATTTGGGGGGTTCACCACC\"\n"
operator|+
literal|"               },\n"
operator|+
literal|"               \"references\" : [\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"authors\" : \"Prasetyo,A.A.\",\n"
operator|+
literal|"                     \"location\" : \"Unpublished\",\n"
operator|+
literal|"                     \"title\" : \"Molecular Epidemiology Study of Human Respiratory Virus in Surakarta Indonesia\"\n"
operator|+
literal|"                  },\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"authors\" : \"Prasetyo,A.A.\",\n"
operator|+
literal|"                     \"location\" : \"Submitted (22-JAN-2013) Microbiology, Faculty of Medicine Sebelas Maret University, Jl. Ir. Sutami 36A, Surakarta, Jawa Tengah 57126, Indonesia\",\n"
operator|+
literal|"                     \"title\" : \"Direct Submission\"\n"
operator|+
literal|"                  }\n"
operator|+
literal|"               ],\n"
operator|+
literal|"               \"name\" : \"AAAAA_ID_19303_0\",\n"
operator|+
literal|"               \"segments\" : [\n"
operator|+
literal|"                  {\n"
operator|+
literal|"                     \"identifiers\" : {\n"
operator|+
literal|"                        \"bioentry_id\" : 1588885,\n"
operator|+
literal|"                        \"namespace\" : \"NCBI\",\n"
operator|+
literal|"                        \"primary_id\" : \"KC513508\",\n"
operator|+
literal|"                        \"version\" : 1,\n"
operator|+
literal|"                        \"name\" : \"KC513508\",\n"
operator|+
literal|"                        \"description\" : \"Influenza A virus (A/Surakarta/1/2010(H3N2)) segment 4 hemagglutinin (HA) gene, partial cds.\",\n"
operator|+
literal|"                        \"accession\" : \"KC513508\"\n"
operator|+
literal|"                     },\n"
operator|+
literal|"                     \"source\" : [\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"mol_type\" : \"viral cRNA\",\n"
operator|+
literal|"                           \"identified_by\" : \"Afiono Agung Prasetyo\",\n"
operator|+
literal|"                           \"HA_subtype\" : \"H3\",\n"
operator|+
literal|"                           \"segment\" : \"4\",\n"
operator|+
literal|"                           \"collection_date\" : \"22-Mar-2010\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"serotype\" : \"H3N2\",\n"
operator|+
literal|"                           \"organism\" : \"Influenza A virus (A/Surakarta/1/2010(H3N2))\",\n"
operator|+
literal|"                           \"country\" : \"Indonesia\",\n"
operator|+
literal|"                           \"primary_key\" : 22511042,\n"
operator|+
literal|"                           \"isolation_source\" : \"nasal and throat swab\",\n"
operator|+
literal|"                           \"collected_by\" : \"Afiono Agung Prasetyo\",\n"
operator|+
literal|"                           \"name\" : \"source\",\n"
operator|+
literal|"                           \"host\" : \"Homo sapiens\",\n"
operator|+
literal|"                           \"NA_subtype\" : \"N2\",\n"
operator|+
literal|"                           \"db_xref\" : [\n"
operator|+
literal|"                              \"taxon:1286265\"\n"
operator|+
literal|"                           ],\n"
operator|+
literal|"                           \"strain\" : \"A/Surakarta/1/2010\",\n"
operator|+
literal|"                           \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511045,\n"
operator|+
literal|"                           \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"                           \"gene\" : \"HA\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"name\" : \"gene\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511046,\n"
operator|+
literal|"                           \"protein_id\" : \"AGF80141.1\",\n"
operator|+
literal|"                           \"gene\" : \"HA\",\n"
operator|+
literal|"                           \"name\" : \"CDS\",\n"
operator|+
literal|"                           \"db_xref\" : [\n"
operator|+
literal|"                              \"GI:451898948\"\n"
operator|+
literal|"                           ],\n"
operator|+
literal|"                           \"codon_start\" : \"2\",\n"
operator|+
literal|"                           \"source_tag\" : \"EMBL/GenBank/SwissProt\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"translation\" : \"PYDVPDYASLRSLVASSGTLEFNSESFNWTGVTQNGTSSACIRRSNNSFFSRLNWLTHLNFKYPALNVTMPNNEQFDKLYIWGVHH\",\n"
operator|+
literal|"                           \"product\" : \"hemagglutinin\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511053,\n"
operator|+
literal|"                           \"name\" : \"asdf_typing\",\n"
operator|+
literal|"                           \"segment\" : \"4\",\n"
operator|+
literal|"                           \"flu_type\" : \"A\",\n"
operator|+
literal|"                           \"bitscore\" : \"277.3\",\n"
operator|+
literal|"                           \"full_lineage\" : \"X_XX_XX_XxxxxXxxxx\",\n"
operator|+
literal|"                           \"lineage\" : \"AAAAAAAAAA\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-07\",\n"
operator|+
literal|"                           \"subtype\" : \"H3\",\n"
operator|+
literal|"                           \"program_version\" : \"v2.8.2\",\n"
operator|+
literal|"                           \"source_tag\" : \"some_text_some\",\n"
operator|+
literal|"                           \"Evalue\" : \"4.6e-85\",\n"
operator|+
literal|"                           \"segment_name\" : \"HA\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"bitscore\" : \"464\",\n"
operator|+
literal|"                           \"subj_location\" : \"342..601\",\n"
operator|+
literal|"                           \"slen\" : \"1701\",\n"
operator|+
literal|"                           \"sseqid\" : \"someID_someID_some\",\n"
operator|+
literal|"                           \"mismatch\" : \"3\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"qcovs\" : \"100\",\n"
operator|+
literal|"                           \"qlen\" : \"260\",\n"
operator|+
literal|"                           \"qcovhsp\" : \"100\",\n"
operator|+
literal|"                           \"primary_key\" : 22511048,\n"
operator|+
literal|"                           \"pident\" : \"98.85\",\n"
operator|+
literal|"                           \"name\" : \"segtypeAlign\",\n"
operator|+
literal|"                           \"qseqid\" : \"KC513508\",\n"
operator|+
literal|"                           \"gaps\" : \"0\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-15\",\n"
operator|+
literal|"                           \"typing\" : \"A_HA_H3\",\n"
operator|+
literal|"                           \"length\" : \"260\",\n"
operator|+
literal|"                           \"evalue\" : \"2e-132\",\n"
operator|+
literal|"                           \"source_tag\" : \"asdf_asdf_asdfv1\",\n"
operator|+
literal|"                           \"program_version\" : \"v1.1.2\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\",\n"
operator|+
literal|"                           \"gapopen\" : \"0\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511060,\n"
operator|+
literal|"                           \"name\" : \"standard_host\",\n"
operator|+
literal|"                           \"Host_NCBI_taxon_id\" : \"9605\",\n"
operator|+
literal|"                           \"curation_status_code\" : \"150\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-12\",\n"
operator|+
literal|"                           \"program_version\" : \"v1.1.7\",\n"
operator|+
literal|"                           \"source_tag\" : \"parse_host_v1\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"curation_status_message\" : \"success, archive and add on next major program revision\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511052,\n"
operator|+
literal|"                           \"name\" : \"flu_segtype\",\n"
operator|+
literal|"                           \"flu_type\" : \"A\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-15\",\n"
operator|+
literal|"                           \"subtype\" : \"H3\",\n"
operator|+
literal|"                           \"program_version\" : \"v1.1.2\",\n"
operator|+
literal|"                           \"source_tag\" : \"asdf_asdf_asd_v1\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"segtype\" : \"HA\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511056,\n"
operator|+
literal|"                           \"prot_coord\" : \"115..87\",\n"
operator|+
literal|"                           \"name\" : \"exon\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-07\",\n"
operator|+
literal|"                           \"source_tag\" : \"asdf_asdfas_v2\",\n"
operator|+
literal|"                           \"program_version\" : \"v2.8.2\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"start-end\" : \"2..259\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"nstart\" : \"2\",\n"
operator|+
literal|"                           \"primary_key\" : 22511058,\n"
operator|+
literal|"                           \"pend\" : \"200\",\n"
operator|+
literal|"                           \"aaseq\" : \"PYDVPDYASLRSLVASSGTLEFNSESFNWTGVTQNGTSSACIRRSNNSFFSRLNWLTHLNFKYPALNVTMPNNEQFDKLYIWGVHH\",\n"
operator|+
literal|"                           \"p_pc_coverage\" : \"15.19\",\n"
operator|+
literal|"                           \"name\" : \"CDS\",\n"
operator|+
literal|"                           \"score\" : \"461\",\n"
operator|+
literal|"                           \"nend\" : \"259\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-07\",\n"
operator|+
literal|"                           \"program_version\" : \"v2.8.2\",\n"
operator|+
literal|"                           \"source_tag\" : \"asdf_asdfas_v2\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"product\" : \"A_HA_H3\",\n"
operator|+
literal|"                           \"ntlength\" : \"258\",\n"
operator|+
literal|"                           \"start-end\" : \"1..260\",\n"
operator|+
literal|"                           \"curation_status\" : \"true\",\n"
operator|+
literal|"                           \"pstart\" : \"115\"\n"
operator|+
literal|"                        },\n"
operator|+
literal|"                        {\n"
operator|+
literal|"                           \"primary_key\" : 22511062,\n"
operator|+
literal|"                           \"Location_Lat_Long\" : \"-0.7892749906,113.9213256836\",\n"
operator|+
literal|"                           \"name\" : \"standardized_location\",\n"
operator|+
literal|"                           \"Location_Country_Alpha2\" : \"ID\",\n"
operator|+
literal|"                           \"curation_status_code\" : \"150\",\n"
operator|+
literal|"                           \"curation_date\" : \"2015-01-15\",\n"
operator|+
literal|"                           \"program_version\" : \"v0.1\",\n"
operator|+
literal|"                           \"source_tag\" : \"asdf_asdf_asdf_asdfn_v0\",\n"
operator|+
literal|"                           \"strand\" : \"1\",\n"
operator|+
literal|"                           \"curation_status_message\" : \"success, archive and add on next major program revision\",\n"
operator|+
literal|"                           \"start-end\" : 1,\n"
operator|+
literal|"                           \"curation_status\" : \"true\"\n"
operator|+
literal|"                        }\n"
operator|+
literal|"                     ],\n"
operator|+
literal|"                     \"sequence\" : {\n"
operator|+
literal|"                        \"length\" : 260,\n"
operator|+
literal|"                        \"string\" : \"CCCTTATGATGTGCCGGATTATGCCTCCCTTAGGTCACTAGTTGCCTCATCCGGCACACTGGAGTTTAACAGTGAAAGCTTCAATTGGACTGGAGTCACTCAAAACGGAACAAGCTCTGCTTGCATAAGGAGATCTAATAATAGTTTCTTTAGTAGATTGAATTGGTTGACCCACTTAAACTTCAAATACCCAGCATTGAACGTGACTATGCCAAACAATGAACAATTTGACAAATTGTACATTTGGGGGGTTCACCACC\"\n"
operator|+
literal|"                     },\n"
operator|+
literal|"                     \"name\" : \"KC513508\",\n"
operator|+
literal|"                     \"annotations\" : {\n"
operator|+
literal|"                        \"keyword\" : \"\",\n"
operator|+
literal|"                        \"curation_date\" : \"2015-01-03\",\n"
operator|+
literal|"                        \"comment\" : [\n"
operator|+
literal|"                           \"##Assembly-Data-START## Assembly Method :: CLC Main Workbench v. 6.8 Sequencing Technology :: Sanger dideoxy1 sequencing ##Assembly-Data-END## \"\n"
operator|+
literal|"                        ],\n"
operator|+
literal|"                        \"date_changed\" : \"25-FEB-2013\",\n"
operator|+
literal|"                        \"curation_status\" : \"false\"\n"
operator|+
literal|"                     }\n"
operator|+
literal|"                  }\n"
operator|+
literal|"               ],\n"
operator|+
literal|"               \"annotations\" : {\n"
operator|+
literal|"                  \"keyword\" : \"\",\n"
operator|+
literal|"                  \"curation_date\" : \"2015-01-05\",\n"
operator|+
literal|"                  \"comment\" : [\n"
operator|+
literal|"                     \"##Assembly-Data-START## Assembly Method :: CLC Main Workbench v. 6.8 Sequencing Technology :: Sanger dideoxy sequencing ##Assembly-Data-END## \"\n"
operator|+
literal|"                  ]\n"
operator|+
literal|"               }}]}}]"
decl_stmt|;
name|RecordingJSONParser
name|parser
init|=
operator|new
name|RecordingJSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
name|JsonRecordReader
name|recordReader
init|=
name|JsonRecordReader
operator|.
name|getInst
argument_list|(
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/**"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|recordReader
operator|.
name|streamRecords
argument_list|(
name|parser
argument_list|,
operator|new
name|JsonRecordReader
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|/*don't care*/
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|parser
operator|.
name|error
argument_list|(
literal|""
argument_list|)
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

