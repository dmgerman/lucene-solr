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
name|DirectoryReader
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
name|IndexWriter
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
comment|/**  * Spawns a BG thread that periodically (defaults to 3.0  * seconds, but accepts param in seconds) wakes up and asks  * IndexWriter for a near real-time reader.  Then runs a  * single query (body: 1) sorted by docdate, and prints  * time to reopen and time to run the search.  *  * @lucene.experimental It's also not generally usable, eg  * you cannot change which query is executed.  */
end_comment

begin_class
DECL|class|NearRealtimeReaderTask
specifier|public
class|class
name|NearRealtimeReaderTask
extends|extends
name|PerfTask
block|{
DECL|field|pauseMSec
name|long
name|pauseMSec
init|=
literal|3000L
decl_stmt|;
DECL|field|reopenCount
name|int
name|reopenCount
decl_stmt|;
DECL|field|reopenTimes
name|int
index|[]
name|reopenTimes
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|method|NearRealtimeReaderTask
specifier|public
name|NearRealtimeReaderTask
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
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PerfRunData
name|runData
init|=
name|getRunData
argument_list|()
decl_stmt|;
comment|// Get initial reader
name|IndexWriter
name|w
init|=
name|runData
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"please open the writer before invoking NearRealtimeReader"
argument_list|)
throw|;
block|}
if|if
condition|(
name|runData
operator|.
name|getIndexReader
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"please close the existing reader before invoking NearRealtimeReader"
argument_list|)
throw|;
block|}
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|runData
operator|.
name|setIndexReader
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|// Transfer our reference to runData
name|r
operator|.
name|decRef
argument_list|()
expr_stmt|;
comment|// TODO: gather basic metrics for reporting -- eg mean,
comment|// stddev, min/max reopen latencies
comment|// Parent sequence sets stopNow
name|reopenCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
operator|!
name|stopNow
condition|)
block|{
name|long
name|waitForMsec
init|=
operator|(
name|pauseMSec
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t
operator|)
operator|)
decl_stmt|;
if|if
condition|(
name|waitForMsec
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitForMsec
argument_list|)
expr_stmt|;
comment|//System.out.println("NRT wait: " + waitForMsec + " msec");
block|}
name|t
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|delay
init|=
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|reopenTimes
operator|.
name|length
operator|==
name|reopenCount
condition|)
block|{
name|reopenTimes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|reopenTimes
argument_list|,
literal|1
operator|+
name|reopenCount
argument_list|)
expr_stmt|;
block|}
name|reopenTimes
index|[
name|reopenCount
operator|++
index|]
operator|=
name|delay
expr_stmt|;
comment|// TODO: somehow we need to enable warming, here
name|runData
operator|.
name|setIndexReader
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
comment|// Transfer our reference to runData
name|newReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|r
operator|=
name|newReader
expr_stmt|;
block|}
block|}
name|stopNow
operator|=
literal|false
expr_stmt|;
return|return
name|reopenCount
return|;
block|}
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|pauseMSec
operator|=
call|(
name|long
call|)
argument_list|(
literal|1000.0
operator|*
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
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
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NRT reopen times:"
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
name|reopenCount
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|reopenTimes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

