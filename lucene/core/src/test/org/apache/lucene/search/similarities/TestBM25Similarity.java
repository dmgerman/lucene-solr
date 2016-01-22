begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_class
DECL|class|TestBM25Similarity
specifier|public
class|class
name|TestBM25Similarity
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSaneNormValues
specifier|public
name|void
name|testSaneNormValues
parameter_list|()
block|{
name|BM25Similarity
name|sim
init|=
operator|new
name|BM25Similarity
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|float
name|len
init|=
name|sim
operator|.
name|decodeNormValue
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"negative len: "
operator|+
name|len
operator|+
literal|", byte="
operator|+
name|i
argument_list|,
name|len
operator|<
literal|0.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"inf len: "
operator|+
name|len
operator|+
literal|", byte="
operator|+
name|i
argument_list|,
name|Float
operator|.
name|isInfinite
argument_list|(
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nan len for byte="
operator|+
name|i
argument_list|,
name|Float
operator|.
name|isNaN
argument_list|(
name|len
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"len is not decreasing: "
operator|+
name|len
operator|+
literal|",byte="
operator|+
name|i
argument_list|,
name|len
operator|<
name|sim
operator|.
name|decodeNormValue
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testIllegalK1
specifier|public
name|void
name|testIllegalK1
parameter_list|()
block|{
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|,
literal|0.75f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k1 value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
operator|-
literal|1
argument_list|,
literal|0.75f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k1 value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
literal|0.75f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal k1 value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalB
specifier|public
name|void
name|testIllegalB
parameter_list|()
block|{
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
literal|1.2f
argument_list|,
literal|2f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal b value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
literal|1.2f
argument_list|,
operator|-
literal|1f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal b value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
literal|1.2f
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal b value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|BM25Similarity
argument_list|(
literal|1.2f
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"illegal b value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
