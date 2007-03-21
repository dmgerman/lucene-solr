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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|stats
operator|.
name|Report
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
name|stats
operator|.
name|TaskStats
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
name|Format
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Report (abstract) task - all report tasks extend this task.  */
end_comment

begin_class
DECL|class|ReportTask
specifier|public
specifier|abstract
class|class
name|ReportTask
extends|extends
name|PerfTask
block|{
DECL|method|ReportTask
specifier|public
name|ReportTask
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
comment|/* (non-Javadoc)    * @see PerfTask#shouldNeverLogAtStart()    */
DECL|method|shouldNeverLogAtStart
specifier|protected
name|boolean
name|shouldNeverLogAtStart
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc)    * @see PerfTask#shouldNotRecordStats()    */
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
comment|/*    * From here start the code used to generate the reports.     * Subclasses would use this part to generate reports.    */
DECL|field|newline
specifier|protected
specifier|static
specifier|final
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/**    * Get a textual summary of the benchmark results, average from all test runs.    */
DECL|field|OP
specifier|protected
specifier|static
specifier|final
name|String
name|OP
init|=
literal|"Operation  "
decl_stmt|;
DECL|field|ROUND
specifier|protected
specifier|static
specifier|final
name|String
name|ROUND
init|=
literal|" round"
decl_stmt|;
DECL|field|RUNCNT
specifier|protected
specifier|static
specifier|final
name|String
name|RUNCNT
init|=
literal|"   runCnt"
decl_stmt|;
DECL|field|RECCNT
specifier|protected
specifier|static
specifier|final
name|String
name|RECCNT
init|=
literal|"   recsPerRun"
decl_stmt|;
DECL|field|RECSEC
specifier|protected
specifier|static
specifier|final
name|String
name|RECSEC
init|=
literal|"        rec/s"
decl_stmt|;
DECL|field|ELAPSED
specifier|protected
specifier|static
specifier|final
name|String
name|ELAPSED
init|=
literal|"  elapsedSec"
decl_stmt|;
DECL|field|USEDMEM
specifier|protected
specifier|static
specifier|final
name|String
name|USEDMEM
init|=
literal|"    avgUsedMem"
decl_stmt|;
DECL|field|TOTMEM
specifier|protected
specifier|static
specifier|final
name|String
name|TOTMEM
init|=
literal|"    avgTotalMem"
decl_stmt|;
DECL|field|COLS
specifier|protected
specifier|static
specifier|final
name|String
name|COLS
index|[]
init|=
block|{
name|RUNCNT
block|,
name|RECCNT
block|,
name|RECSEC
block|,
name|ELAPSED
block|,
name|USEDMEM
block|,
name|TOTMEM
block|}
decl_stmt|;
comment|/**    * Compute a title line for a report table    * @param longestOp size of longest op name in the table    * @return the table title line.    */
DECL|method|tableTitle
specifier|protected
name|String
name|tableTitle
parameter_list|(
name|String
name|longestOp
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
name|OP
argument_list|,
name|longestOp
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ROUND
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getColsNamesForValsByRound
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COLS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|COLS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * find the longest op name out of completed tasks.      * @param taskStats completed tasks to be considered.    * @return the longest op name out of completed tasks.    */
DECL|method|longestOp
specifier|protected
name|String
name|longestOp
parameter_list|(
name|Iterator
name|taskStats
parameter_list|)
block|{
name|String
name|longest
init|=
name|OP
decl_stmt|;
while|while
condition|(
name|taskStats
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TaskStats
name|stat
init|=
operator|(
name|TaskStats
operator|)
name|taskStats
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|getElapsed
argument_list|()
operator|>=
literal|0
condition|)
block|{
comment|// consider only tasks that ended
name|String
name|name
init|=
name|stat
operator|.
name|getTask
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>
name|longest
operator|.
name|length
argument_list|()
condition|)
block|{
name|longest
operator|=
name|name
expr_stmt|;
block|}
block|}
block|}
return|return
name|longest
return|;
block|}
comment|/**    * Compute a report line for the given task stat.    * @param longestOp size of longest op name in the table.    * @param stat task stat to be printed.    * @return the report line.    */
DECL|method|taskReportLine
specifier|protected
name|String
name|taskReportLine
parameter_list|(
name|String
name|longestOp
parameter_list|,
name|TaskStats
name|stat
parameter_list|)
block|{
name|PerfTask
name|task
init|=
name|stat
operator|.
name|getTask
argument_list|()
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
name|task
operator|.
name|getName
argument_list|()
argument_list|,
name|longestOp
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|round
init|=
operator|(
name|stat
operator|.
name|getRound
argument_list|()
operator|>=
literal|0
condition|?
literal|""
operator|+
name|stat
operator|.
name|getRound
argument_list|()
else|:
literal|"-"
operator|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|formatPaddLeft
argument_list|(
name|round
argument_list|,
name|ROUND
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getColsValuesForValsByRound
argument_list|(
name|stat
operator|.
name|getRound
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
name|stat
operator|.
name|getNumRuns
argument_list|()
argument_list|,
name|RUNCNT
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
name|stat
operator|.
name|getCount
argument_list|()
operator|/
name|stat
operator|.
name|getNumRuns
argument_list|()
argument_list|,
name|RECCNT
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
operator|(
name|stat
operator|.
name|getElapsed
argument_list|()
operator|>
literal|0
condition|?
name|stat
operator|.
name|getElapsed
argument_list|()
else|:
literal|1
operator|)
decl_stmt|;
comment|// assume at least 1ms
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|1
argument_list|,
call|(
name|float
call|)
argument_list|(
name|stat
operator|.
name|getCount
argument_list|()
operator|*
literal|1000.0
operator|/
name|elapsed
argument_list|)
argument_list|,
name|RECSEC
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|2
argument_list|,
operator|(
name|float
operator|)
name|stat
operator|.
name|getElapsed
argument_list|()
operator|/
literal|1000
argument_list|,
name|ELAPSED
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
operator|(
name|float
operator|)
name|stat
operator|.
name|getMaxUsedMem
argument_list|()
operator|/
name|stat
operator|.
name|getNumRuns
argument_list|()
argument_list|,
name|USEDMEM
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
operator|(
name|float
operator|)
name|stat
operator|.
name|getMaxTotMem
argument_list|()
operator|/
name|stat
operator|.
name|getNumRuns
argument_list|()
argument_list|,
name|TOTMEM
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|genPartialReport
specifier|protected
name|Report
name|genPartialReport
parameter_list|(
name|int
name|reported
parameter_list|,
name|LinkedHashMap
name|partOfTasks
parameter_list|,
name|int
name|totalSize
parameter_list|)
block|{
name|String
name|longetOp
init|=
name|longestOp
argument_list|(
name|partOfTasks
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tableTitle
argument_list|(
name|longetOp
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|int
name|lineNum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|partOfTasks
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TaskStats
name|stat
init|=
operator|(
name|TaskStats
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|String
name|line
init|=
name|taskReportLine
argument_list|(
name|longetOp
argument_list|,
name|stat
argument_list|)
decl_stmt|;
name|lineNum
operator|++
expr_stmt|;
if|if
condition|(
name|partOfTasks
operator|.
name|size
argument_list|()
operator|>
literal|2
operator|&&
name|lineNum
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"   "
argument_list|,
literal|" - "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|String
name|reptxt
init|=
operator|(
name|reported
operator|==
literal|0
condition|?
literal|"No Matching Entries Were Found!"
else|:
name|sb
operator|.
name|toString
argument_list|()
operator|)
decl_stmt|;
return|return
operator|new
name|Report
argument_list|(
name|reptxt
argument_list|,
name|partOfTasks
operator|.
name|size
argument_list|()
argument_list|,
name|reported
argument_list|,
name|totalSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit

