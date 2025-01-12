begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.eval
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|eval
package|;
end_package

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
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|map
operator|.
name|HashedMap
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|ConversionEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|RawValueEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|StreamEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamContext
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test ConversionEvaluators  */
end_comment

begin_class
DECL|class|ConversionEvaluatorsTest
specifier|public
class|class
name|ConversionEvaluatorsTest
block|{
DECL|field|factory
name|StreamFactory
name|factory
decl_stmt|;
DECL|field|values
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|method|ConversionEvaluatorsTest
specifier|public
name|ConversionEvaluatorsTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
expr_stmt|;
name|factory
operator|.
name|withFunctionName
argument_list|(
literal|"convert"
argument_list|,
name|ConversionEvaluator
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"raw"
argument_list|,
name|RawValueEvaluator
operator|.
name|class
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|HashedMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidExpression
specifier|public
name|void
name|testInvalidExpression
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
decl_stmt|;
try|try
block|{
name|evaluator
operator|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"convert(inches)"
argument_list|)
expr_stmt|;
name|StreamContext
name|streamContext
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid expression convert(inches) - expecting 3 value but found 1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|evaluator
operator|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"convert(inches, yards, 3)"
argument_list|)
expr_stmt|;
name|StreamContext
name|streamContext
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
decl_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No conversion available from INCHES to YARDS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInches
specifier|public
name|void
name|testInches
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(inches, centimeters, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|2.54
argument_list|)
argument_list|)
expr_stmt|;
name|testFunction
argument_list|(
literal|"convert(inches, meters, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|0.0254
argument_list|)
argument_list|)
expr_stmt|;
name|testFunction
argument_list|(
literal|"convert(inches, millimeters, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|25.40
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testYards
specifier|public
name|void
name|testYards
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(yards, meters, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|.91
argument_list|)
argument_list|)
expr_stmt|;
name|testFunction
argument_list|(
literal|"convert(yards, kilometers, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|.00091
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMiles
specifier|public
name|void
name|testMiles
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(miles, kilometers, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|1.61
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMillimeters
specifier|public
name|void
name|testMillimeters
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(millimeters, inches, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|.039
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCentimeters
specifier|public
name|void
name|testCentimeters
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(centimeters, inches, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|.39
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMeters
specifier|public
name|void
name|testMeters
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(meters, feet, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|3.28
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKiloMeters
specifier|public
name|void
name|testKiloMeters
parameter_list|()
throws|throws
name|Exception
block|{
name|testFunction
argument_list|(
literal|"convert(kilometers, feet, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|3280.8
argument_list|)
argument_list|)
expr_stmt|;
name|testFunction
argument_list|(
literal|"convert(kilometers, miles, 2)"
argument_list|,
call|(
name|double
call|)
argument_list|(
literal|2
operator|*
literal|.62
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFunction
specifier|public
name|void
name|testFunction
parameter_list|(
name|String
name|expression
parameter_list|,
name|Number
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|StreamContext
name|streamContext
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Number
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

