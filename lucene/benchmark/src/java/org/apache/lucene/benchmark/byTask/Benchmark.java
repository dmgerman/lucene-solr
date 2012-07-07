begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask
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
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Algorithm
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Run the benchmark algorithm.  *<p>Usage: java Benchmark  algorithm-file  *<ol>  *<li>Read algorithm.</li>  *<li> Run the algorithm.</li>  *</ol>  * Things to be added/fixed in "Benchmarking by tasks":  *<ol>  *<li>TODO - report into Excel and/or graphed view.</li>  *<li>TODO - perf comparison between Lucene releases over the years.</li>  *<li>TODO - perf report adequate to include in Lucene nightly build site? (so we can easily track performance changes.)</li>  *<li>TODO - add overall time control for repeated execution (vs. current by-count only).</li>  *<li>TODO - query maker that is based on index statistics.</li>  *</ol>  */
end_comment

begin_class
DECL|class|Benchmark
specifier|public
class|class
name|Benchmark
block|{
DECL|field|runData
specifier|private
name|PerfRunData
name|runData
decl_stmt|;
DECL|field|algorithm
specifier|private
name|Algorithm
name|algorithm
decl_stmt|;
DECL|field|executed
specifier|private
name|boolean
name|executed
decl_stmt|;
DECL|method|Benchmark
specifier|public
name|Benchmark
parameter_list|(
name|Reader
name|algReader
parameter_list|)
throws|throws
name|Exception
block|{
comment|// prepare run data
try|try
block|{
name|runData
operator|=
operator|new
name|PerfRunData
argument_list|(
operator|new
name|Config
argument_list|(
name|algReader
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Error: cannot init PerfRunData!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// parse algorithm
try|try
block|{
name|algorithm
operator|=
operator|new
name|Algorithm
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Error: cannot understand algorithm!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Execute this benchmark     */
DECL|method|execute
specifier|public
specifier|synchronized
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|executed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Benchmark was already executed"
argument_list|)
throw|;
block|}
name|executed
operator|=
literal|true
expr_stmt|;
name|runData
operator|.
name|setStartTimeMillis
argument_list|()
expr_stmt|;
name|algorithm
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
comment|/**    * Run the benchmark algorithm.    * @param args benchmark config and algorithm files    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|exec
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility: execute benchmark from command line    * @param args single argument is expected: algorithm-file    */
DECL|method|exec
specifier|public
specifier|static
name|void
name|exec
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// verify command line args
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java Benchmark<algorithm file>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// verify input files
name|File
name|algFile
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|algFile
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|algFile
operator|.
name|isFile
argument_list|()
operator|||
operator|!
name|algFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"cannot find/read algorithm file: "
operator|+
name|algFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running algorithm from: "
operator|+
name|algFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Benchmark
name|benchmark
init|=
literal|null
decl_stmt|;
try|try
block|{
name|benchmark
operator|=
operator|new
name|Benchmark
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|algFile
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> algorithm:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|benchmark
operator|.
name|getAlgorithm
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// execute
try|try
block|{
name|benchmark
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: cannot execute the algorithm! "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"####################"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"###  D O N E !!! ###"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"####################"
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return Returns the algorithm.    */
DECL|method|getAlgorithm
specifier|public
name|Algorithm
name|getAlgorithm
parameter_list|()
block|{
return|return
name|algorithm
return|;
block|}
comment|/**    * @return Returns the runData.    */
DECL|method|getRunData
specifier|public
name|PerfRunData
name|getRunData
parameter_list|()
block|{
return|return
name|runData
return|;
block|}
block|}
end_class

end_unit

