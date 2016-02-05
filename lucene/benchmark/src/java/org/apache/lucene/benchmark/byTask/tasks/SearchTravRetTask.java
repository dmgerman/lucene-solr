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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
import|;
end_import

begin_comment
comment|/**  * Search and Traverse and Retrieve docs task.  *   *<p>Note: This task reuses the reader if it is already open.   * Otherwise a reader is opened at start and closed at the end.  *</p>  *   *<p>Takes optional param: traversal size (otherwise all results are traversed).</p>  *   *<p>Other side effects: counts additional 1 (record) for each traversed hit,   * and 1 more for each retrieved (non null) document.</p>  */
end_comment

begin_class
DECL|class|SearchTravRetTask
specifier|public
class|class
name|SearchTravRetTask
extends|extends
name|SearchTravTask
block|{
DECL|method|SearchTravRetTask
specifier|public
name|SearchTravRetTask
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
annotation|@
name|Override
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

