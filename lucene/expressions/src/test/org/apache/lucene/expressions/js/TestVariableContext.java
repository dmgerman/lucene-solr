begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
operator|.
name|VariableContext
operator|.
name|Type
operator|.
name|MEMBER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
operator|.
name|VariableContext
operator|.
name|Type
operator|.
name|STR_INDEX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
operator|.
name|VariableContext
operator|.
name|Type
operator|.
name|INT_INDEX
import|;
end_import

begin_class
DECL|class|TestVariableContext
specifier|public
class|class
name|TestVariableContext
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleVar
specifier|public
name|void
name|testSimpleVar
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|0
index|]
operator|.
name|type
argument_list|,
name|MEMBER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|0
index|]
operator|.
name|text
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyString
specifier|public
name|void
name|testEmptyString
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo['']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|type
argument_list|,
name|STR_INDEX
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|text
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnescapeString
specifier|public
name|void
name|testUnescapeString
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo['\\'\\\\']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|type
argument_list|,
name|STR_INDEX
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|text
argument_list|,
literal|"'\\"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMember
specifier|public
name|void
name|testMember
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo.bar"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|type
argument_list|,
name|MEMBER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|1
index|]
operator|.
name|text
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMemberFollowedByMember
specifier|public
name|void
name|testMemberFollowedByMember
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo.bar.baz"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|2
index|]
operator|.
name|type
argument_list|,
name|MEMBER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|2
index|]
operator|.
name|text
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMemberFollowedByIntArray
specifier|public
name|void
name|testMemberFollowedByIntArray
parameter_list|()
block|{
name|VariableContext
index|[]
name|x
init|=
name|VariableContext
operator|.
name|parse
argument_list|(
literal|"foo.bar[1]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|2
index|]
operator|.
name|type
argument_list|,
name|INT_INDEX
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
index|[
literal|2
index|]
operator|.
name|integer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

