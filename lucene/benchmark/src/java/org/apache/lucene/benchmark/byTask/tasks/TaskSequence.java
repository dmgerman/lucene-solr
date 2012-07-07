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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|feeds
operator|.
name|NoMoreDataException
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
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_comment
comment|/**  * Sequence of parallel or sequential tasks.  */
end_comment

begin_class
DECL|class|TaskSequence
specifier|public
class|class
name|TaskSequence
extends|extends
name|PerfTask
block|{
DECL|field|REPEAT_EXHAUST
specifier|public
specifier|static
name|int
name|REPEAT_EXHAUST
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|tasks
specifier|private
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
name|tasks
decl_stmt|;
DECL|field|repetitions
specifier|private
name|int
name|repetitions
init|=
literal|1
decl_stmt|;
DECL|field|parallel
specifier|private
name|boolean
name|parallel
decl_stmt|;
DECL|field|parent
specifier|private
name|TaskSequence
name|parent
decl_stmt|;
DECL|field|letChildReport
specifier|private
name|boolean
name|letChildReport
init|=
literal|true
decl_stmt|;
DECL|field|rate
specifier|private
name|int
name|rate
init|=
literal|0
decl_stmt|;
DECL|field|perMin
specifier|private
name|boolean
name|perMin
init|=
literal|false
decl_stmt|;
comment|// rate, if set, is, by default, be sec.
DECL|field|seqName
specifier|private
name|String
name|seqName
decl_stmt|;
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
init|=
literal|false
decl_stmt|;
DECL|field|resetExhausted
specifier|private
name|boolean
name|resetExhausted
init|=
literal|false
decl_stmt|;
DECL|field|tasksArray
specifier|private
name|PerfTask
index|[]
name|tasksArray
decl_stmt|;
DECL|field|anyExhaustibleTasks
specifier|private
name|boolean
name|anyExhaustibleTasks
decl_stmt|;
DECL|field|collapsable
specifier|private
name|boolean
name|collapsable
init|=
literal|false
decl_stmt|;
comment|// to not collapse external sequence named in alg.
DECL|field|fixedTime
specifier|private
name|boolean
name|fixedTime
decl_stmt|;
comment|// true if we run for fixed time
DECL|field|runTimeSec
specifier|private
name|double
name|runTimeSec
decl_stmt|;
comment|// how long to run for
DECL|field|logByTimeMsec
specifier|private
specifier|final
name|long
name|logByTimeMsec
decl_stmt|;
DECL|method|TaskSequence
specifier|public
name|TaskSequence
parameter_list|(
name|PerfRunData
name|runData
parameter_list|,
name|String
name|name
parameter_list|,
name|TaskSequence
name|parent
parameter_list|,
name|boolean
name|parallel
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|collapsable
operator|=
operator|(
name|name
operator|==
literal|null
operator|)
expr_stmt|;
name|name
operator|=
operator|(
name|name
operator|!=
literal|null
condition|?
name|name
else|:
operator|(
name|parallel
condition|?
literal|"Par"
else|:
literal|"Seq"
operator|)
operator|)
expr_stmt|;
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|setSequenceName
argument_list|()
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|parallel
operator|=
name|parallel
expr_stmt|;
name|tasks
operator|=
operator|new
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
argument_list|()
expr_stmt|;
name|logByTimeMsec
operator|=
name|runData
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"report.time.step.msec"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|initTasksArray
argument_list|()
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
name|tasksArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tasksArray
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|getRunData
argument_list|()
operator|.
name|getDocMaker
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|initTasksArray
specifier|private
name|void
name|initTasksArray
parameter_list|()
block|{
if|if
condition|(
name|tasksArray
operator|==
literal|null
condition|)
block|{
specifier|final
name|int
name|numTasks
init|=
name|tasks
operator|.
name|size
argument_list|()
decl_stmt|;
name|tasksArray
operator|=
operator|new
name|PerfTask
index|[
name|numTasks
index|]
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|numTasks
condition|;
name|k
operator|++
control|)
block|{
name|tasksArray
index|[
name|k
index|]
operator|=
name|tasks
operator|.
name|get
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|anyExhaustibleTasks
operator||=
name|tasksArray
index|[
name|k
index|]
operator|instanceof
name|ResetInputsTask
expr_stmt|;
name|anyExhaustibleTasks
operator||=
name|tasksArray
index|[
name|k
index|]
operator|instanceof
name|TaskSequence
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|parallel
operator|&&
name|logByTimeMsec
operator|!=
literal|0
operator|&&
operator|!
name|letChildReport
condition|)
block|{
name|countsByTime
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
comment|/**    * @return Returns the parallel.    */
DECL|method|isParallel
specifier|public
name|boolean
name|isParallel
parameter_list|()
block|{
return|return
name|parallel
return|;
block|}
comment|/**    * @return Returns the repetitions.    */
DECL|method|getRepetitions
specifier|public
name|int
name|getRepetitions
parameter_list|()
block|{
return|return
name|repetitions
return|;
block|}
DECL|field|countsByTime
specifier|private
name|int
index|[]
name|countsByTime
decl_stmt|;
DECL|method|setRunTime
specifier|public
name|void
name|setRunTime
parameter_list|(
name|double
name|sec
parameter_list|)
throws|throws
name|Exception
block|{
name|runTimeSec
operator|=
name|sec
expr_stmt|;
name|fixedTime
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * @param repetitions The repetitions to set.    * @throws Exception     */
DECL|method|setRepetitions
specifier|public
name|void
name|setRepetitions
parameter_list|(
name|int
name|repetitions
parameter_list|)
throws|throws
name|Exception
block|{
name|fixedTime
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|repetitions
operator|=
name|repetitions
expr_stmt|;
if|if
condition|(
name|repetitions
operator|==
name|REPEAT_EXHAUST
condition|)
block|{
if|if
condition|(
name|isParallel
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"REPEAT_EXHAUST is not allowed for parallel tasks"
argument_list|)
throw|;
block|}
block|}
name|setSequenceName
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return Returns the parent.    */
DECL|method|getParent
specifier|public
name|TaskSequence
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#doLogic()    */
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|exhausted
operator|=
name|resetExhausted
operator|=
literal|false
expr_stmt|;
return|return
operator|(
name|parallel
condition|?
name|doParallelTasks
argument_list|()
else|:
name|doSerialTasks
argument_list|()
operator|)
return|;
block|}
DECL|class|RunBackgroundTask
specifier|private
specifier|static
class|class
name|RunBackgroundTask
extends|extends
name|Thread
block|{
DECL|field|task
specifier|private
specifier|final
name|PerfTask
name|task
decl_stmt|;
DECL|field|letChildReport
specifier|private
specifier|final
name|boolean
name|letChildReport
decl_stmt|;
DECL|field|count
specifier|private
specifier|volatile
name|int
name|count
decl_stmt|;
DECL|method|RunBackgroundTask
specifier|public
name|RunBackgroundTask
parameter_list|(
name|PerfTask
name|task
parameter_list|,
name|boolean
name|letChildReport
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|letChildReport
operator|=
name|letChildReport
expr_stmt|;
block|}
DECL|method|stopNow
specifier|public
name|void
name|stopNow
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|task
operator|.
name|stopNow
argument_list|()
expr_stmt|;
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|count
operator|=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|doSerialTasks
specifier|private
name|int
name|doSerialTasks
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
return|return
name|doSerialTasksWithRate
argument_list|()
return|;
block|}
name|initTasksArray
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|long
name|runTime
init|=
call|(
name|long
call|)
argument_list|(
name|runTimeSec
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RunBackgroundTask
argument_list|>
name|bgTasks
init|=
literal|null
decl_stmt|;
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|fixedTime
operator|||
operator|(
name|repetitions
operator|==
name|REPEAT_EXHAUST
operator|&&
operator|!
name|exhausted
operator|)
operator|||
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|stopNow
condition|)
block|{
break|break;
block|}
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|tasksArray
operator|.
name|length
condition|;
name|l
operator|++
control|)
block|{
specifier|final
name|PerfTask
name|task
init|=
name|tasksArray
index|[
name|l
index|]
decl_stmt|;
if|if
condition|(
name|task
operator|.
name|getRunInBackground
argument_list|()
condition|)
block|{
if|if
condition|(
name|bgTasks
operator|==
literal|null
condition|)
block|{
name|bgTasks
operator|=
operator|new
name|ArrayList
argument_list|<
name|RunBackgroundTask
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|RunBackgroundTask
name|bgTask
init|=
operator|new
name|RunBackgroundTask
argument_list|(
name|task
argument_list|,
name|letChildReport
argument_list|)
decl_stmt|;
name|bgTask
operator|.
name|setPriority
argument_list|(
name|task
operator|.
name|getBackgroundDeltaPriority
argument_list|()
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|bgTask
operator|.
name|start
argument_list|()
expr_stmt|;
name|bgTasks
operator|.
name|add
argument_list|(
name|bgTask
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
specifier|final
name|int
name|inc
init|=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
decl_stmt|;
name|count
operator|+=
name|inc
expr_stmt|;
if|if
condition|(
name|countsByTime
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|slot
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|/
name|logByTimeMsec
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|>=
name|countsByTime
operator|.
name|length
condition|)
block|{
name|countsByTime
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|countsByTime
argument_list|,
literal|1
operator|+
name|slot
argument_list|)
expr_stmt|;
block|}
name|countsByTime
index|[
name|slot
index|]
operator|+=
name|inc
expr_stmt|;
block|}
if|if
condition|(
name|anyExhaustibleTasks
condition|)
name|updateExhausted
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
name|exhausted
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fixedTime
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|>
name|runTime
condition|)
block|{
name|repetitions
operator|=
name|k
operator|+
literal|1
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|bgTasks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|RunBackgroundTask
name|bgTask
range|:
name|bgTasks
control|)
block|{
name|bgTask
operator|.
name|stopNow
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|RunBackgroundTask
name|bgTask
range|:
name|bgTasks
control|)
block|{
name|bgTask
operator|.
name|join
argument_list|()
expr_stmt|;
name|count
operator|+=
name|bgTask
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|countsByTime
operator|!=
literal|null
condition|)
block|{
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|getCurrentStats
argument_list|()
operator|.
name|setCountsByTime
argument_list|(
name|countsByTime
argument_list|,
name|logByTimeMsec
argument_list|)
expr_stmt|;
block|}
name|stopNow
operator|=
literal|false
expr_stmt|;
return|return
name|count
return|;
block|}
DECL|method|doSerialTasksWithRate
specifier|private
name|int
name|doSerialTasksWithRate
parameter_list|()
throws|throws
name|Exception
block|{
name|initTasksArray
argument_list|()
expr_stmt|;
name|long
name|delayStep
init|=
operator|(
name|perMin
condition|?
literal|60000
else|:
literal|1000
operator|)
operator|/
name|rate
decl_stmt|;
name|long
name|nextStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
operator|(
name|repetitions
operator|==
name|REPEAT_EXHAUST
operator|&&
operator|!
name|exhausted
operator|)
operator|||
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|stopNow
condition|)
block|{
break|break;
block|}
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|tasksArray
operator|.
name|length
condition|;
name|l
operator|++
control|)
block|{
specifier|final
name|PerfTask
name|task
init|=
name|tasksArray
index|[
name|l
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|stopNow
condition|)
block|{
name|long
name|waitMore
init|=
name|nextStartTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitMore
operator|>
literal|0
condition|)
block|{
comment|// TODO: better to use condition to notify
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|stopNow
condition|)
block|{
break|break;
block|}
name|nextStartTime
operator|+=
name|delayStep
expr_stmt|;
comment|// this aims at avarage rate.
try|try
block|{
specifier|final
name|int
name|inc
init|=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
decl_stmt|;
name|count
operator|+=
name|inc
expr_stmt|;
if|if
condition|(
name|countsByTime
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|slot
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|/
name|logByTimeMsec
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|>=
name|countsByTime
operator|.
name|length
condition|)
block|{
name|countsByTime
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|countsByTime
argument_list|,
literal|1
operator|+
name|slot
argument_list|)
expr_stmt|;
block|}
name|countsByTime
index|[
name|slot
index|]
operator|+=
name|inc
expr_stmt|;
block|}
if|if
condition|(
name|anyExhaustibleTasks
condition|)
name|updateExhausted
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
name|exhausted
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|stopNow
operator|=
literal|false
expr_stmt|;
return|return
name|count
return|;
block|}
comment|// update state regarding exhaustion.
DECL|method|updateExhausted
specifier|private
name|void
name|updateExhausted
parameter_list|(
name|PerfTask
name|task
parameter_list|)
block|{
if|if
condition|(
name|task
operator|instanceof
name|ResetInputsTask
condition|)
block|{
name|exhausted
operator|=
literal|false
expr_stmt|;
name|resetExhausted
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
name|TaskSequence
name|t
init|=
operator|(
name|TaskSequence
operator|)
name|task
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|resetExhausted
condition|)
block|{
name|exhausted
operator|=
literal|false
expr_stmt|;
name|resetExhausted
operator|=
literal|true
expr_stmt|;
name|t
operator|.
name|resetExhausted
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|exhausted
operator||=
name|t
operator|.
name|exhausted
expr_stmt|;
block|}
block|}
block|}
DECL|class|ParallelTask
specifier|private
class|class
name|ParallelTask
extends|extends
name|Thread
block|{
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|field|task
specifier|public
specifier|final
name|PerfTask
name|task
decl_stmt|;
DECL|method|ParallelTask
specifier|public
name|ParallelTask
parameter_list|(
name|PerfTask
name|task
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|n
init|=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
decl_stmt|;
if|if
condition|(
name|anyExhaustibleTasks
condition|)
block|{
name|updateExhausted
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
name|count
operator|+=
name|n
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
name|exhausted
operator|=
literal|true
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|stopNow
specifier|public
name|void
name|stopNow
parameter_list|()
block|{
name|super
operator|.
name|stopNow
argument_list|()
expr_stmt|;
comment|// Forwards top request to children
if|if
condition|(
name|runningParallelTasks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ParallelTask
name|t
range|:
name|runningParallelTasks
control|)
block|{
name|t
operator|.
name|task
operator|.
name|stopNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|runningParallelTasks
name|ParallelTask
index|[]
name|runningParallelTasks
decl_stmt|;
DECL|method|doParallelTasks
specifier|private
name|int
name|doParallelTasks
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TaskStats
name|stats
init|=
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|getCurrentStats
argument_list|()
decl_stmt|;
name|initTasksArray
argument_list|()
expr_stmt|;
name|ParallelTask
name|t
index|[]
init|=
name|runningParallelTasks
operator|=
operator|new
name|ParallelTask
index|[
name|repetitions
operator|*
name|tasks
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// prepare threads
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tasksArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PerfTask
name|task
init|=
name|tasksArray
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
decl_stmt|;
name|t
index|[
name|index
operator|++
index|]
operator|=
operator|new
name|ParallelTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
comment|// run threads
name|startThreads
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// wait for all threads to complete
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|count
operator|+=
name|t
index|[
name|i
index|]
operator|.
name|count
expr_stmt|;
if|if
condition|(
name|t
index|[
name|i
index|]
operator|.
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
name|TaskSequence
name|sub
init|=
operator|(
name|TaskSequence
operator|)
name|t
index|[
name|i
index|]
operator|.
name|task
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|countsByTime
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|countsByTime
operator|==
literal|null
condition|)
block|{
name|countsByTime
operator|=
operator|new
name|int
index|[
name|sub
operator|.
name|countsByTime
operator|.
name|length
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|countsByTime
operator|.
name|length
operator|<
name|sub
operator|.
name|countsByTime
operator|.
name|length
condition|)
block|{
name|countsByTime
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|countsByTime
argument_list|,
name|sub
operator|.
name|countsByTime
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sub
operator|.
name|countsByTime
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|countsByTime
index|[
name|j
index|]
operator|+=
name|sub
operator|.
name|countsByTime
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|countsByTime
operator|!=
literal|null
condition|)
block|{
name|stats
operator|.
name|setCountsByTime
argument_list|(
name|countsByTime
argument_list|,
name|logByTimeMsec
argument_list|)
expr_stmt|;
block|}
comment|// return total count
return|return
name|count
return|;
block|}
comment|// run threads
DECL|method|startThreads
specifier|private
name|void
name|startThreads
parameter_list|(
name|ParallelTask
index|[]
name|t
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|startlThreadsWithRate
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|// run threads with rate
DECL|method|startlThreadsWithRate
specifier|private
name|void
name|startlThreadsWithRate
parameter_list|(
name|ParallelTask
index|[]
name|t
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|delayStep
init|=
operator|(
name|perMin
condition|?
literal|60000
else|:
literal|1000
operator|)
operator|/
name|rate
decl_stmt|;
name|long
name|nextStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|waitMore
init|=
name|nextStartTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitMore
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitMore
argument_list|)
expr_stmt|;
block|}
name|nextStartTime
operator|+=
name|delayStep
expr_stmt|;
comment|// this aims at average rate of starting threads.
name|t
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addTask
specifier|public
name|void
name|addTask
parameter_list|(
name|PerfTask
name|task
parameter_list|)
block|{
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|task
operator|.
name|setDepth
argument_list|(
name|getDepth
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#toString()    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|padd
init|=
name|getPadding
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|parallel
condition|?
literal|" ["
else|:
literal|" {"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|PerfTask
name|task
range|:
name|tasks
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|task
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|padd
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|!
name|letChildReport
condition|?
literal|">"
else|:
operator|(
name|parallel
condition|?
literal|"]"
else|:
literal|"}"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fixedTime
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
operator|.
name|format
argument_list|(
name|runTimeSec
argument_list|)
operator|+
literal|"s"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|repetitions
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" * "
operator|+
name|repetitions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|repetitions
operator|==
name|REPEAT_EXHAUST
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" * EXHAUST"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",  rate: "
operator|+
name|rate
operator|+
literal|"/"
operator|+
operator|(
name|perMin
condition|?
literal|"min"
else|:
literal|"sec"
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getRunInBackground
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
name|int
name|x
init|=
name|getBackgroundDeltaPriority
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Execute child tasks in a way that they do not report their time separately.    */
DECL|method|setNoChildReport
specifier|public
name|void
name|setNoChildReport
parameter_list|()
block|{
name|letChildReport
operator|=
literal|false
expr_stmt|;
for|for
control|(
specifier|final
name|PerfTask
name|task
range|:
name|tasks
control|)
block|{
if|if
condition|(
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|setNoChildReport
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns the rate per minute: how many operations should be performed in a minute.    * If 0 this has no effect.    * @return the rate per min: how many operations should be performed in a minute.    */
DECL|method|getRate
specifier|public
name|int
name|getRate
parameter_list|()
block|{
return|return
operator|(
name|perMin
condition|?
name|rate
else|:
literal|60
operator|*
name|rate
operator|)
return|;
block|}
comment|/**    * @param rate The rate to set.    */
DECL|method|setRate
specifier|public
name|void
name|setRate
parameter_list|(
name|int
name|rate
parameter_list|,
name|boolean
name|perMin
parameter_list|)
block|{
name|this
operator|.
name|rate
operator|=
name|rate
expr_stmt|;
name|this
operator|.
name|perMin
operator|=
name|perMin
expr_stmt|;
name|setSequenceName
argument_list|()
expr_stmt|;
block|}
DECL|method|setSequenceName
specifier|private
name|void
name|setSequenceName
parameter_list|()
block|{
name|seqName
operator|=
name|super
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|repetitions
operator|==
name|REPEAT_EXHAUST
condition|)
block|{
name|seqName
operator|+=
literal|"_Exhaust"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|repetitions
operator|>
literal|1
condition|)
block|{
name|seqName
operator|+=
literal|"_"
operator|+
name|repetitions
expr_stmt|;
block|}
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|seqName
operator|+=
literal|"_"
operator|+
name|rate
operator|+
operator|(
name|perMin
condition|?
literal|"/min"
else|:
literal|"/sec"
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|parallel
operator|&&
name|seqName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|indexOf
argument_list|(
literal|"par"
argument_list|)
operator|<
literal|0
condition|)
block|{
name|seqName
operator|+=
literal|"_Par"
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|seqName
return|;
comment|// override to include more info
block|}
comment|/**    * @return Returns the tasks.    */
DECL|method|getTasks
specifier|public
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|tasks
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#clone()    */
annotation|@
name|Override
DECL|method|clone
specifier|protected
name|TaskSequence
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|TaskSequence
name|res
init|=
operator|(
name|TaskSequence
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|res
operator|.
name|tasks
operator|=
operator|new
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
argument_list|()
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
name|tasks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|res
operator|.
name|tasks
operator|.
name|add
argument_list|(
name|tasks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Return true if can be collapsed in case it is outermost sequence    */
DECL|method|isCollapsable
specifier|public
name|boolean
name|isCollapsable
parameter_list|()
block|{
return|return
name|collapsable
return|;
block|}
block|}
end_class

end_unit

