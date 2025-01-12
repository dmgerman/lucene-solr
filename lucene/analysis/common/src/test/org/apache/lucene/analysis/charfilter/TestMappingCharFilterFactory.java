begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
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
name|analysis
operator|.
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import

begin_class
DECL|class|TestMappingCharFilterFactory
specifier|public
class|class
name|TestMappingCharFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testParseString
specifier|public
name|void
name|testParseString
parameter_list|()
throws|throws
name|Exception
block|{
name|MappingCharFilterFactory
name|f
init|=
operator|(
name|MappingCharFilterFactory
operator|)
name|charFilterFactory
argument_list|(
literal|"Mapping"
argument_list|)
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|f
operator|.
name|parseString
argument_list|(
literal|"\\"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected escaped characters"
argument_list|,
literal|"\\\"\n\t\r\b\f"
argument_list|,
name|f
operator|.
name|parseString
argument_list|(
literal|"\\\\\\\"\\n\\t\\r\\b\\f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected escaped characters"
argument_list|,
literal|"A"
argument_list|,
name|f
operator|.
name|parseString
argument_list|(
literal|"\\u0041"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected escaped characters"
argument_list|,
literal|"AB"
argument_list|,
name|f
operator|.
name|parseString
argument_list|(
literal|"\\u0041\\u0042"
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|f
operator|.
name|parseString
argument_list|(
literal|"\\u000"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// invalid hex number
name|expectThrows
argument_list|(
name|NumberFormatException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|f
operator|.
name|parseString
argument_list|(
literal|"\\u123x"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|charFilterFactory
argument_list|(
literal|"Mapping"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

