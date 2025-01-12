begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Timeout
import|;
end_import

begin_class
DECL|class|TestWorstCaseTestBehavior
specifier|public
class|class
name|TestWorstCaseTestBehavior
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Ignore
DECL|method|testThreadLeak
specifier|public
name|void
name|testThreadLeak
parameter_list|()
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
comment|// once alive, leave it to run outside of the test scope.
block|}
annotation|@
name|Ignore
DECL|method|testLaaaaaargeOutput
specifier|public
name|void
name|testLaaaaaargeOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|message
init|=
literal|"I will not OOM on large output"
decl_stmt|;
name|int
name|howMuch
init|=
literal|250
operator|*
literal|1024
operator|*
literal|1024
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
name|howMuch
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|howMuch
operator|-=
name|message
operator|.
name|length
argument_list|()
expr_stmt|;
comment|// approximately.
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
DECL|method|testProgressiveOutput
specifier|public
name|void
name|testProgressiveOutput
parameter_list|()
throws|throws
name|Exception
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Emitting sysout line: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Emitting syserr line: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RandomizedTest
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
DECL|method|testUncaughtException
specifier|public
name|void
name|testUncaughtException
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Timeout
argument_list|(
name|millis
operator|=
literal|500
argument_list|)
DECL|method|testTimeout
specifier|public
name|void
name|testTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Timeout
argument_list|(
name|millis
operator|=
literal|1000
argument_list|)
DECL|method|testZombie
specifier|public
name|void
name|testZombie
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
end_class

end_unit

