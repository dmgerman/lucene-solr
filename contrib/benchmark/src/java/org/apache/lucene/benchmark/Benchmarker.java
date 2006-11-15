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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|stats
operator|.
name|TestData
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|Benchmarker
specifier|public
interface|interface
name|Benchmarker
block|{
comment|/**      * Benchmark according to the implementation, using the workingDir as the place to store things.      *      * @param workingDir The {@link java.io.File} directory to store temporary data in for running the benchmark      * @param options Any {@link BenchmarkOptions} that are needed for this benchmark.  This      * @return The {@link org.apache.lucene.benchmark.stats.TestData} used to run the benchmark.      */
DECL|method|benchmark
name|TestData
index|[]
name|benchmark
parameter_list|(
name|File
name|workingDir
parameter_list|,
name|BenchmarkOptions
name|options
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

