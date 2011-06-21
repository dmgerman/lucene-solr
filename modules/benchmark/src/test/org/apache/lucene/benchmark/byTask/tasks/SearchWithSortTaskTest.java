begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|PerfRunData
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
name|utils
operator|.
name|Config
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
name|SortField
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
DECL|class|SearchWithSortTaskTest
specifier|public
class|class
name|SearchWithSortTaskTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|method|testSetParams_docField
specifier|public
name|void
name|testSetParams_docField
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchWithSortTask
name|task
init|=
operator|new
name|SearchWithSortTask
argument_list|(
operator|new
name|PerfRunData
argument_list|(
operator|new
name|Config
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|task
operator|.
name|setParams
argument_list|(
literal|"doc"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortField
operator|.
name|DOC
argument_list|,
name|task
operator|.
name|getSort
argument_list|()
operator|.
name|getSort
argument_list|()
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

