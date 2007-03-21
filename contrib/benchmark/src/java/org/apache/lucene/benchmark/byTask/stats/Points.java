begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.stats
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
name|stats
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|tasks
operator|.
name|PerfTask
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

begin_comment
comment|/**  * Test run data points collected as the test proceeds.  */
end_comment

begin_class
DECL|class|Points
specifier|public
class|class
name|Points
block|{
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
comment|// stat points ordered by their start time.
comment|// for now we collect points as TaskStats objects.
comment|// later might optimize to collect only native data.
DECL|field|points
specifier|private
name|ArrayList
name|points
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|nextTaskRunNum
specifier|private
name|int
name|nextTaskRunNum
init|=
literal|0
decl_stmt|;
comment|/**    * Create a Points statistics object.     */
DECL|method|Points
specifier|public
name|Points
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/**    * Return the current task stats.    * the actual task stats are returned, so caller should not modify this task stats.     * @return current {@link TaskStats}.    */
DECL|method|taskStats
specifier|public
name|List
name|taskStats
parameter_list|()
block|{
return|return
name|points
return|;
block|}
comment|/**    * Mark that a task is starting.     * Create a task stats for it and store it as a point.    * @param task the starting task.    * @return the new task stats created for the starting task.    */
DECL|method|markTaskStart
specifier|public
specifier|synchronized
name|TaskStats
name|markTaskStart
parameter_list|(
name|PerfTask
name|task
parameter_list|,
name|int
name|round
parameter_list|)
block|{
name|TaskStats
name|stats
init|=
operator|new
name|TaskStats
argument_list|(
name|task
argument_list|,
name|nextTaskRunNum
argument_list|()
argument_list|,
name|round
argument_list|)
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
name|stats
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
comment|// return next task num
DECL|method|nextTaskRunNum
specifier|private
specifier|synchronized
name|int
name|nextTaskRunNum
parameter_list|()
block|{
return|return
name|nextTaskRunNum
operator|++
return|;
block|}
comment|/**    * mark the end of a task    */
DECL|method|markTaskEnd
specifier|public
specifier|synchronized
name|void
name|markTaskEnd
parameter_list|(
name|TaskStats
name|stats
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|int
name|numParallelTasks
init|=
name|nextTaskRunNum
operator|-
literal|1
operator|-
name|stats
operator|.
name|getTaskRunNum
argument_list|()
decl_stmt|;
comment|// note: if the stats were cleared, might be that this stats object is
comment|// no longer in points, but this is just ok.
name|stats
operator|.
name|markEnd
argument_list|(
name|numParallelTasks
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear all data, prepare for more tests.    */
DECL|method|clearData
specifier|public
name|void
name|clearData
parameter_list|()
block|{
name|points
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

