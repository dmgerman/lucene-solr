begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestAxiomaticSimilarity
specifier|public
class|class
name|TestAxiomaticSimilarity
extends|extends
name|LuceneTestCase
block|{
DECL|method|testIllegalS
specifier|public
name|void
name|testIllegalS
parameter_list|()
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
operator|new
name|AxiomaticF2EXP
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|,
literal|0.1f
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
literal|"illegal s value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
operator|-
literal|1
argument_list|,
literal|0.1f
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal s value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
literal|0.1f
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal s value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalK
specifier|public
name|void
name|testIllegalK
parameter_list|()
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
operator|new
name|AxiomaticF2EXP
argument_list|(
literal|0.35f
argument_list|,
literal|2f
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
literal|"illegal k value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
literal|0.35f
argument_list|,
operator|-
literal|1f
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
literal|0.35f
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
literal|0.35f
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalQL
specifier|public
name|void
name|testIllegalQL
parameter_list|()
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
operator|new
name|AxiomaticF3EXP
argument_list|(
literal|0.35f
argument_list|,
operator|-
literal|1
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
literal|"illegal query length value"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|AxiomaticF2EXP
argument_list|(
literal|0.35f
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

