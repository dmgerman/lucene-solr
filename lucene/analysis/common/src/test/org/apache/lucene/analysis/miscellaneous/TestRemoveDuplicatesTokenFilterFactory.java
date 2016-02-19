begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|CannedTokenStream
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import

begin_comment
comment|/** Simple tests to ensure this factory is working */
end_comment

begin_class
DECL|class|TestRemoveDuplicatesTokenFilterFactory
specifier|public
class|class
name|TestRemoveDuplicatesTokenFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|tok
specifier|public
specifier|static
name|Token
name|tok
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|t
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|tok
return|;
block|}
DECL|method|testDups
specifier|public
name|void
name|testDups
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
specifier|final
name|Token
modifier|...
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|CannedTokenStream
argument_list|(
name|tokens
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"RemoveDuplicates"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|expected
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleDups
specifier|public
name|void
name|testSimpleDups
parameter_list|()
throws|throws
name|Exception
block|{
name|testDups
argument_list|(
literal|"A B C D E"
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"B"
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"C"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"E"
argument_list|,
literal|21
argument_list|,
literal|25
argument_list|)
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
name|tokenFilterFactory
argument_list|(
literal|"RemoveDuplicates"
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

