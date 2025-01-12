begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|Field
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
name|LeafReaderContext
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
name|RandomIndexWriter
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
name|Term
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
name|TimeLimitingCollector
operator|.
name|TimeExceededException
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
name|TimeLimitingCollector
operator|.
name|TimerThread
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
name|store
operator|.
name|Directory
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
name|Counter
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
name|LuceneTestCase
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
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
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
name|TestUtil
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
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * Tests the {@link TimeLimitingCollector}.  This test checks (1) search  * correctness (regardless of timeout), (2) expected timeout behavior,  * and (3) a sanity test with multiple searching threads.  */
end_comment

begin_class
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"http://test.is.timing.sensitive.so.it.prints.instead.of.failing"
argument_list|)
DECL|class|TestTimeLimitingCollector
specifier|public
class|class
name|TestTimeLimitingCollector
extends|extends
name|LuceneTestCase
block|{
DECL|field|SLOW_DOWN
specifier|private
specifier|static
specifier|final
name|int
name|SLOW_DOWN
init|=
literal|3
decl_stmt|;
DECL|field|TIME_ALLOWED
specifier|private
specifier|static
specifier|final
name|long
name|TIME_ALLOWED
init|=
literal|17
operator|*
name|SLOW_DOWN
decl_stmt|;
comment|// so searches can find about 17 docs.
comment|// max time allowed is relaxed for multithreading tests.
comment|// the multithread case fails when setting this to 1 (no slack) and launching many threads (>2000).
comment|// but this is not a real failure, just noise.
DECL|field|MULTI_THREAD_SLACK
specifier|private
specifier|static
specifier|final
name|double
name|MULTI_THREAD_SLACK
init|=
literal|7
decl_stmt|;
DECL|field|N_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|N_DOCS
init|=
literal|3000
decl_stmt|;
DECL|field|N_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|N_THREADS
init|=
literal|50
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|FIELD_NAME
specifier|private
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"body"
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|counter
specifier|private
name|Counter
name|counter
decl_stmt|;
DECL|field|counterThread
specifier|private
name|TimerThread
name|counterThread
decl_stmt|;
comment|/**    * initializes searcher with a document set    */
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|counter
operator|=
name|Counter
operator|.
name|newCounter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|counterThread
operator|=
operator|new
name|TimerThread
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|counterThread
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|String
name|docText
index|[]
init|=
block|{
literal|"docThatNeverMatchesSoWeCanRequireLastDocCollectedToBeGreaterThanZero"
block|,
literal|"one blah three"
block|,
literal|"one foo three multiOne"
block|,
literal|"one foobar three multiThree"
block|,
literal|"blueberry pancakes"
block|,
literal|"blueberry pie"
block|,
literal|"blueberry strudel"
block|,
literal|"blueberry pizza"
block|,     }
decl_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
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
name|N_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|docText
index|[
name|i
operator|%
name|docText
operator|.
name|length
index|]
argument_list|,
name|iw
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// start from 1, so that the 0th doc never matches
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|docText
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|docTextParts
init|=
name|docText
index|[
name|i
index|]
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|docTextPart
range|:
name|docTextParts
control|)
block|{
comment|// large query so that search will be longer
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
name|docTextPart
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
name|query
operator|=
name|booleanQuery
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// warm the searcher
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|counterThread
operator|.
name|stopTimer
argument_list|()
expr_stmt|;
name|counterThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|value
parameter_list|,
name|RandomIndexWriter
name|iw
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|FIELD_NAME
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|private
name|void
name|search
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
comment|/**    * test search correctness with no timeout    */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
block|{
name|doTestSearch
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestSearch
specifier|private
name|void
name|doTestSearch
parameter_list|()
block|{
name|int
name|totalResults
init|=
literal|0
decl_stmt|;
name|int
name|totalTLCResults
init|=
literal|0
decl_stmt|;
try|try
block|{
name|MyHitCollector
name|myHc
init|=
operator|new
name|MyHitCollector
argument_list|()
decl_stmt|;
name|search
argument_list|(
name|myHc
argument_list|)
expr_stmt|;
name|totalResults
operator|=
name|myHc
operator|.
name|hitCount
argument_list|()
expr_stmt|;
name|myHc
operator|=
operator|new
name|MyHitCollector
argument_list|()
expr_stmt|;
name|long
name|oneHour
init|=
literal|3600000
decl_stmt|;
name|long
name|duration
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
name|oneHour
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|Collector
name|tlCollector
init|=
name|createTimedCollector
argument_list|(
name|myHc
argument_list|,
name|duration
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|tlCollector
argument_list|)
expr_stmt|;
name|totalTLCResults
operator|=
name|myHc
operator|.
name|hitCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//==fail
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of results!"
argument_list|,
name|totalResults
argument_list|,
name|totalTLCResults
argument_list|)
expr_stmt|;
block|}
DECL|method|createTimedCollector
specifier|private
name|Collector
name|createTimedCollector
parameter_list|(
name|MyHitCollector
name|hc
parameter_list|,
name|long
name|timeAllowed
parameter_list|,
name|boolean
name|greedy
parameter_list|)
block|{
name|TimeLimitingCollector
name|res
init|=
operator|new
name|TimeLimitingCollector
argument_list|(
name|hc
argument_list|,
name|counter
argument_list|,
name|timeAllowed
argument_list|)
decl_stmt|;
name|res
operator|.
name|setGreedy
argument_list|(
name|greedy
argument_list|)
expr_stmt|;
comment|// set to true to make sure at least one doc is collected.
return|return
name|res
return|;
block|}
comment|/**    * Test that timeout is obtained, and soon enough!    */
DECL|method|testTimeoutGreedy
specifier|public
name|void
name|testTimeoutGreedy
parameter_list|()
block|{
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that timeout is obtained, and soon enough!    */
DECL|method|testTimeoutNotGreedy
specifier|public
name|void
name|testTimeoutNotGreedy
parameter_list|()
block|{
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestTimeout
specifier|private
name|void
name|doTestTimeout
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|,
name|boolean
name|greedy
parameter_list|)
block|{
comment|// setup
name|MyHitCollector
name|myHc
init|=
operator|new
name|MyHitCollector
argument_list|()
decl_stmt|;
name|myHc
operator|.
name|setSlowDown
argument_list|(
name|SLOW_DOWN
argument_list|)
expr_stmt|;
name|Collector
name|tlCollector
init|=
name|createTimedCollector
argument_list|(
name|myHc
argument_list|,
name|TIME_ALLOWED
argument_list|,
name|greedy
argument_list|)
decl_stmt|;
comment|// search: must get exception
name|TimeExceededException
name|timeoutException
init|=
name|expectThrows
argument_list|(
name|TimeExceededException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|search
argument_list|(
name|tlCollector
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
comment|// greediness affect last doc collected
name|int
name|exceptionDoc
init|=
name|timeoutException
operator|.
name|getLastDocCollected
argument_list|()
decl_stmt|;
name|int
name|lastCollected
init|=
name|myHc
operator|.
name|getLastDocCollected
argument_list|()
decl_stmt|;
comment|// exceptionDoc == -1 means we hit the timeout in getLeafCollector:
if|if
condition|(
name|exceptionDoc
operator|!=
operator|-
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
literal|"doc collected at timeout must be> 0! or == -1 but was: "
operator|+
name|exceptionDoc
argument_list|,
name|exceptionDoc
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|greedy
condition|)
block|{
name|assertTrue
argument_list|(
literal|"greedy="
operator|+
name|greedy
operator|+
literal|" exceptionDoc="
operator|+
name|exceptionDoc
operator|+
literal|" != lastCollected="
operator|+
name|lastCollected
argument_list|,
name|exceptionDoc
operator|==
name|lastCollected
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"greedy, but no hits found!"
argument_list|,
name|myHc
operator|.
name|hitCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"greedy="
operator|+
name|greedy
operator|+
literal|" exceptionDoc="
operator|+
name|exceptionDoc
operator|+
literal|" not> lastCollected="
operator|+
name|lastCollected
argument_list|,
name|exceptionDoc
operator|>
name|lastCollected
argument_list|)
expr_stmt|;
block|}
block|}
comment|// verify that elapsed time at exception is within valid limits
name|assertEquals
argument_list|(
name|timeoutException
operator|.
name|getTimeAllowed
argument_list|()
argument_list|,
name|TIME_ALLOWED
argument_list|)
expr_stmt|;
comment|// a) Not too early
name|assertTrue
argument_list|(
literal|"elapsed="
operator|+
name|timeoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|+
literal|"<= (allowed-resolution)="
operator|+
operator|(
name|TIME_ALLOWED
operator|-
name|counterThread
operator|.
name|getResolution
argument_list|()
operator|)
argument_list|,
name|timeoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|>
name|TIME_ALLOWED
operator|-
name|counterThread
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
comment|// b) Not too late.
comment|//    This part is problematic in a busy test system, so we just print a warning.
comment|//    We already verified that a timeout occurred, we just can't be picky about how long it took.
if|if
condition|(
name|timeoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|>
name|maxTime
argument_list|(
name|multiThreaded
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Informative: timeout exceeded (no action required: most probably just "
operator|+
literal|" because the test machine is slower than usual):  "
operator|+
literal|"lastDoc="
operator|+
name|exceptionDoc
operator|+
literal|" ,&& allowed="
operator|+
name|timeoutException
operator|.
name|getTimeAllowed
argument_list|()
operator|+
literal|" ,&& elapsed="
operator|+
name|timeoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|+
literal|">= "
operator|+
name|maxTimeStr
argument_list|(
name|multiThreaded
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maxTime
specifier|private
name|long
name|maxTime
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|)
block|{
name|long
name|res
init|=
literal|2
operator|*
name|counterThread
operator|.
name|getResolution
argument_list|()
operator|+
name|TIME_ALLOWED
operator|+
name|SLOW_DOWN
decl_stmt|;
comment|// some slack for less noise in this test
if|if
condition|(
name|multiThreaded
condition|)
block|{
name|res
operator|*=
name|MULTI_THREAD_SLACK
expr_stmt|;
comment|// larger slack
block|}
return|return
name|res
return|;
block|}
DECL|method|maxTimeStr
specifier|private
name|String
name|maxTimeStr
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|)
block|{
name|String
name|s
init|=
literal|"( "
operator|+
literal|"2*resolution +  TIME_ALLOWED + SLOW_DOWN = "
operator|+
literal|"2*"
operator|+
name|counterThread
operator|.
name|getResolution
argument_list|()
operator|+
literal|" + "
operator|+
name|TIME_ALLOWED
operator|+
literal|" + "
operator|+
name|SLOW_DOWN
operator|+
literal|")"
decl_stmt|;
if|if
condition|(
name|multiThreaded
condition|)
block|{
name|s
operator|=
name|MULTI_THREAD_SLACK
operator|+
literal|" * "
operator|+
name|s
expr_stmt|;
block|}
return|return
name|maxTime
argument_list|(
name|multiThreaded
argument_list|)
operator|+
literal|" = "
operator|+
name|s
return|;
block|}
comment|/**    * Test timeout behavior when resolution is modified.     */
DECL|method|testModifyResolution
specifier|public
name|void
name|testModifyResolution
parameter_list|()
block|{
try|try
block|{
comment|// increase and test
name|long
name|resolution
init|=
literal|20
operator|*
name|TimerThread
operator|.
name|DEFAULT_RESOLUTION
decl_stmt|;
comment|//400
name|counterThread
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|counterThread
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// decrease much and test
name|resolution
operator|=
literal|5
expr_stmt|;
name|counterThread
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|counterThread
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// return to default and test
name|resolution
operator|=
name|TimerThread
operator|.
name|DEFAULT_RESOLUTION
expr_stmt|;
name|counterThread
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|counterThread
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|counterThread
operator|.
name|setResolution
argument_list|(
name|TimerThread
operator|.
name|DEFAULT_RESOLUTION
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoHits
specifier|public
name|void
name|testNoHits
parameter_list|()
throws|throws
name|IOException
block|{
name|MyHitCollector
name|myHc
init|=
operator|new
name|MyHitCollector
argument_list|()
decl_stmt|;
name|Collector
name|collector
init|=
name|createTimedCollector
argument_list|(
name|myHc
argument_list|,
operator|-
literal|1
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
comment|// search: must get exception
name|expectThrows
argument_list|(
name|TimeExceededException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// won't match - we only test if we check timeout when collectors are pulled
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"blueberry"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|booleanQuery
operator|.
name|build
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|myHc
operator|.
name|getLastDocCollected
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test correctness with multiple searching threads.    */
DECL|method|testSearchMultiThreaded
specifier|public
name|void
name|testSearchMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultiThreads
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test correctness with multiple searching threads.    */
DECL|method|testTimeoutMultiThreaded
specifier|public
name|void
name|testTimeoutMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultiThreads
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMultiThreads
specifier|private
name|void
name|doTestMultiThreads
parameter_list|(
specifier|final
name|boolean
name|withTimeout
parameter_list|)
throws|throws
name|Exception
block|{
name|Thread
index|[]
name|threadArray
init|=
operator|new
name|Thread
index|[
name|N_THREADS
index|]
decl_stmt|;
specifier|final
name|BitSet
name|success
init|=
operator|new
name|BitSet
argument_list|(
name|N_THREADS
argument_list|)
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|num
init|=
name|i
decl_stmt|;
name|threadArray
index|[
name|num
index|]
operator|=
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
if|if
condition|(
name|withTimeout
condition|)
block|{
name|doTestTimeout
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doTestSearch
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|success
init|)
block|{
name|success
operator|.
name|set
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|threadArray
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|threadArray
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"some threads failed!"
argument_list|,
name|N_THREADS
argument_list|,
name|success
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// counting collector that can slow down at collect().
DECL|class|MyHitCollector
specifier|private
specifier|static
class|class
name|MyHitCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|bits
specifier|private
specifier|final
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
DECL|field|slowdown
specifier|private
name|int
name|slowdown
init|=
literal|0
decl_stmt|;
DECL|field|lastDocCollected
specifier|private
name|int
name|lastDocCollected
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
init|=
literal|0
decl_stmt|;
comment|/**      * amount of time to wait on each collect to simulate a long iteration      */
DECL|method|setSlowDown
specifier|public
name|void
name|setSlowDown
parameter_list|(
name|int
name|milliseconds
parameter_list|)
block|{
name|slowdown
operator|=
name|milliseconds
expr_stmt|;
block|}
DECL|method|hitCount
specifier|public
name|int
name|hitCount
parameter_list|()
block|{
return|return
name|bits
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|getLastDocCollected
specifier|public
name|int
name|getLastDocCollected
parameter_list|()
block|{
return|return
name|lastDocCollected
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// scorer is not needed
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docId
init|=
name|doc
operator|+
name|docBase
decl_stmt|;
if|if
condition|(
name|slowdown
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|slowdown
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
assert|assert
name|docId
operator|>=
literal|0
operator|:
literal|" base="
operator|+
name|docBase
operator|+
literal|" doc="
operator|+
name|doc
assert|;
name|bits
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|lastDocCollected
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

