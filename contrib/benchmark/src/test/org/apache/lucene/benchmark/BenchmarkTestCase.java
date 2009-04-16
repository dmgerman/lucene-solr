begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** Base class for all Benchmark unit tests. */
end_comment

begin_class
DECL|class|BenchmarkTestCase
specifier|public
class|class
name|BenchmarkTestCase
extends|extends
name|TestCase
block|{
DECL|field|workDir
specifier|private
specifier|static
specifier|final
name|File
name|workDir
decl_stmt|;
static|static
block|{
name|workDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"benchmark.work.dir"
argument_list|,
literal|"test/benchmark"
argument_list|)
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
DECL|method|getWorkDir
specifier|public
name|File
name|getWorkDir
parameter_list|()
block|{
return|return
name|workDir
return|;
block|}
block|}
end_class

end_unit

