begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ConstValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|RangeMapFloatFunction
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
name|search
operator|.
name|BaseExplanationTestCase
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
name|search
operator|.
name|BoostQuery
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
name|search
operator|.
name|Query
import|;
end_import

begin_class
DECL|class|TestFunctionQueryExplanations
specifier|public
class|class
name|TestFunctionQueryExplanations
extends|extends
name|BaseExplanationTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoost
specifier|public
name|void
name|testBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapFunction
specifier|public
name|void
name|testMapFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|ValueSource
name|rff
init|=
operator|new
name|RangeMapFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
operator|new
name|Float
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|FunctionQuery
argument_list|(
name|rff
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map(const(3.0),0.0,1.0,const(2.0),const(4.0))"
argument_list|,
name|rff
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map(const(3.0),min=0.0,max=1.0,target=const(2.0),defaultVal=const(4.0))"
argument_list|,
name|rff
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|(
literal|123
argument_list|)
argument_list|)
expr_stmt|;
comment|// DefaultValue is null -> defaults to source value
name|rff
operator|=
operator|new
name|RangeMapFloatFunction
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map(const(3.0),0.0,1.0,const(2.0),null)"
argument_list|,
name|rff
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map(const(3.0),min=0.0,max=1.0,target=const(2.0),defaultVal=null)"
argument_list|,
name|rff
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|(
literal|123
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

