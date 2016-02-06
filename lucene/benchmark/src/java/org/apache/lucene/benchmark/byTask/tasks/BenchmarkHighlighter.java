begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Analyzer
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
name|document
operator|.
name|Document
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_comment
comment|/**  * Abstract class for benchmarking highlighting performance  */
end_comment

begin_class
DECL|class|BenchmarkHighlighter
specifier|public
specifier|abstract
class|class
name|BenchmarkHighlighter
block|{
DECL|method|doHighlight
specifier|public
specifier|abstract
name|int
name|doHighlight
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|Document
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

