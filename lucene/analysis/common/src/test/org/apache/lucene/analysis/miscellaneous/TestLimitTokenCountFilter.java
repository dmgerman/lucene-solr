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
name|BaseTokenStreamTestCase
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
name|MockTokenizer
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestLimitTokenCountFilter
specifier|public
class|class
name|TestLimitTokenCountFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
specifier|final
name|boolean
name|consumeAll
range|:
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|false
block|}
control|)
block|{
name|MockTokenizer
name|tokenizer
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"A1 B2 C3 D4 E5 F6"
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
name|consumeAll
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|LimitTokenCountFilter
argument_list|(
name|tokenizer
argument_list|,
literal|3
argument_list|,
name|consumeAll
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A1"
block|,
literal|"B2"
block|,
literal|"C3"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|LimitTokenCountFilter
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"A1 B2 C3 D4 E5 F6"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

