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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  * Report all statistics grouped/aggregated by name and round.  *<br>Other side effects: None.  */
end_comment

begin_class
DECL|class|RepSumByNameRoundTask
specifier|public
class|class
name|RepSumByNameRoundTask
extends|extends
name|ReportTask
block|{
DECL|method|RepSumByNameRoundTask
specifier|public
name|RepSumByNameRoundTask
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
name|Report
name|rp
init|=
name|reportSumByNameRound
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|taskStats
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> Report Sum By (any) Name and Round ("
operator|+
name|rp
operator|.
name|getSize
argument_list|()
operator|+
literal|" about "
operator|+
name|rp
operator|.
name|getReported
argument_list|()
operator|+
literal|" out of "
operator|+
name|rp
operator|.
name|getOutOf
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/**    * Report statistics as a string, aggregate for tasks named the same, and from the same round.    * @return the report    */
DECL|method|reportSumByNameRound
specifier|protected
name|Report
name|reportSumByNameRound
parameter_list|(
name|List
name|taskStats
parameter_list|)
block|{
comment|// aggregate by task name and round
name|LinkedHashMap
name|p2
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|int
name|reported
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|taskStats
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
name|stat1
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
name|stat1
operator|.
name|getElapsed
argument_list|()
operator|>=
literal|0
condition|)
block|{
comment|// consider only tasks that ended
name|reported
operator|++
expr_stmt|;
name|String
name|name
init|=
name|stat1
operator|.
name|getTask
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|rname
init|=
name|stat1
operator|.
name|getRound
argument_list|()
operator|+
literal|"."
operator|+
name|name
decl_stmt|;
comment|// group by round
name|TaskStats
name|stat2
init|=
operator|(
name|TaskStats
operator|)
name|p2
operator|.
name|get
argument_list|(
name|rname
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat2
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|stat2
operator|=
operator|(
name|TaskStats
operator|)
name|stat1
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|p2
operator|.
name|put
argument_list|(
name|rname
argument_list|,
name|stat2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stat2
operator|.
name|add
argument_list|(
name|stat1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now generate report from secondary list p2
return|return
name|genPartialReport
argument_list|(
name|reported
argument_list|,
name|p2
argument_list|,
name|taskStats
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

