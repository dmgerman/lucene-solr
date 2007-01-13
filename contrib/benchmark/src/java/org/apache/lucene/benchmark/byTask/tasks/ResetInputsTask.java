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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Reset inputs so that the test run would behave, input wise,   * as if it just started. This affects e.g. the generation of docs and queries.  */
end_comment

begin_class
DECL|class|ResetInputsTask
specifier|public
class|class
name|ResetInputsTask
extends|extends
name|PerfTask
block|{
DECL|method|ResetInputsTask
specifier|public
name|ResetInputsTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|getRunData
argument_list|()
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#shouldNotRecordStats()    */
DECL|method|shouldNotRecordStats
specifier|protected
name|boolean
name|shouldNotRecordStats
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

