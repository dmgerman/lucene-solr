begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
package|;
end_package

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
name|BytesRef
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Tests the {@link VInt8} class.  */
end_comment

begin_class
DECL|class|Vint8Test
specifier|public
class|class
name|Vint8Test
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEST_VALUES
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|TEST_VALUES
init|=
block|{
operator|-
literal|1000000000
block|,
operator|-
literal|1
block|,
literal|0
block|,
operator|(
literal|1
operator|<<
literal|7
operator|)
operator|-
literal|1
block|,
literal|1
operator|<<
literal|7
block|,
operator|(
literal|1
operator|<<
literal|14
operator|)
operator|-
literal|1
block|,
literal|1
operator|<<
literal|14
block|,
operator|(
literal|1
operator|<<
literal|21
operator|)
operator|-
literal|1
block|,
literal|1
operator|<<
literal|21
block|,
operator|(
literal|1
operator|<<
literal|28
operator|)
operator|-
literal|1
block|,
literal|1
operator|<<
literal|28
block|}
decl_stmt|;
DECL|field|BYTES_NEEDED_TEST_VALUES
specifier|private
specifier|static
name|int
index|[]
name|BYTES_NEEDED_TEST_VALUES
init|=
block|{
literal|5
block|,
literal|5
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testBytesRef
specifier|public
name|void
name|testBytesRef
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|int
name|expectedSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TEST_VALUES
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|VInt8
operator|.
name|encode
argument_list|(
name|TEST_VALUES
index|[
name|j
index|]
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|expectedSize
operator|+=
name|BYTES_NEEDED_TEST_VALUES
index|[
name|j
index|]
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TEST_VALUES
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|TEST_VALUES
index|[
name|j
index|]
argument_list|,
name|VInt8
operator|.
name|decode
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

