begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks.alt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
operator|.
name|alt
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
name|benchmark
operator|.
name|BenchmarkTestCase
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
name|benchmark
operator|.
name|byTask
operator|.
name|Benchmark
import|;
end_import

begin_comment
comment|/** Tests that tasks in alternate packages are found. */
end_comment

begin_class
DECL|class|AltPackageTaskTest
specifier|public
class|class
name|AltPackageTaskTest
extends|extends
name|BenchmarkTestCase
block|{
comment|/** Benchmark should fail loading the algorithm when alt is not specified */
DECL|method|testWithoutAlt
specifier|public
name|void
name|testWithoutAlt
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|execBenchmark
argument_list|(
name|altAlg
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Benchmark should be able to load the algorithm when alt is specified */
DECL|method|testWithAlt
specifier|public
name|void
name|testWithAlt
parameter_list|()
throws|throws
name|Exception
block|{
name|Benchmark
name|bm
init|=
name|execBenchmark
argument_list|(
name|altAlg
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|bm
operator|.
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|altAlg
specifier|private
name|String
index|[]
name|altAlg
parameter_list|(
name|boolean
name|allowAlt
parameter_list|)
block|{
name|String
name|altTask
init|=
literal|"{ AltTest }"
decl_stmt|;
if|if
condition|(
name|allowAlt
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"alt.tasks.packages = "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
block|,
name|altTask
block|}
return|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|altTask
block|}
return|;
block|}
block|}
end_class

end_unit

