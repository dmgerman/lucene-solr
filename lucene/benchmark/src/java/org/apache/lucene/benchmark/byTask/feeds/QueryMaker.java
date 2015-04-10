begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
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
name|feeds
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
name|Query
import|;
end_import

begin_comment
comment|/**  * Create queries for the test.  */
end_comment

begin_interface
DECL|interface|QueryMaker
specifier|public
interface|interface
name|QueryMaker
block|{
comment|/**     * Create the next query, of the given size.    * @param size the size of the query - number of terms, etc.    * @exception Exception if cannot make the query, or if size&gt; 0 was specified but this feature is not supported.    */
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** Create the next query */
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** Set the properties */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** Reset inputs so that the test run would behave, input wise, as if it just started. */
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** Print the queries */
DECL|method|printQueries
specifier|public
name|String
name|printQueries
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

