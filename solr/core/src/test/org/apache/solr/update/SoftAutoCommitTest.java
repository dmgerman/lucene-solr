begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|Constants
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
name|Slow
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrEventListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Test auto commit functionality in a way that doesn't suck.  *<p>  * AutoCommitTest is an abomination that is way to brittle in how it   * tries to check that commits happened, and when they happened.  * The goal of this test class is to (ultimately) completely replace all   * of the functionality of that test class using:  *</p>  *<ul>  *<li>A more robust monitor of commit/newSearcher events that records   *       the times of those events in a queue that can be polled.    *       Multiple events in rapid succession are not lost.  *</li>  *<li>Timing checks that are forgiving of slow machines and use   *       knowledge of how slow A-&gt;B was to affect the expectation of   *       how slow B-&gt;C will be  *</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|SoftAutoCommitTest
specifier|public
class|class
name|SoftAutoCommitTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|monitor
specifier|private
name|MockEventListener
name|monitor
decl_stmt|;
DECL|field|updater
specifier|private
name|DirectUpdateHandler2
name|updater
decl_stmt|;
annotation|@
name|Before
DECL|method|createMonitor
specifier|public
name|void
name|createMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"This test is not working on Windows (or maybe machines with only 2 CPUs)"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|updater
operator|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
expr_stmt|;
name|monitor
operator|=
operator|new
name|MockEventListener
argument_list|()
expr_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|updater
operator|.
name|registerSoftCommitCallback
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|updater
operator|.
name|registerCommitCallback
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
comment|// isolate searcher getting ready from this test
name|monitor
operator|.
name|searcher
operator|.
name|poll
argument_list|(
literal|5000
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
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
block|}
DECL|method|testSoftAndHardCommitMaxTimeMixedAdds
specifier|public
name|void
name|testSoftAndHardCommitMaxTimeMixedAdds
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|softCommitWaitMillis
init|=
literal|500
decl_stmt|;
specifier|final
name|int
name|hardCommitWaitMillis
init|=
literal|1200
decl_stmt|;
name|CommitTracker
name|hardTracker
init|=
name|updater
operator|.
name|commitTracker
decl_stmt|;
name|CommitTracker
name|softTracker
init|=
name|updater
operator|.
name|softCommitTracker
decl_stmt|;
comment|// wait out any leaked commits
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
operator|*
literal|2
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
operator|*
literal|2
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|int
name|startingHardCommits
init|=
name|hardTracker
operator|.
name|getCommitCount
argument_list|()
decl_stmt|;
name|int
name|startingSoftCommits
init|=
name|softTracker
operator|.
name|getCommitCount
argument_list|()
decl_stmt|;
name|softTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|softCommitWaitMillis
argument_list|)
expr_stmt|;
name|softTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|hardCommitWaitMillis
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// simplify whats going on by only having soft auto commits trigger new searchers
name|hardTracker
operator|.
name|setOpenSearcher
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Add a single document
name|long
name|add529
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"529"
argument_list|,
literal|"subject"
argument_list|,
literal|"the doc we care about in this test"
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// Wait for the soft commit with some fudge
name|Long
name|soft529
init|=
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
operator|*
literal|5
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"soft529 wasn't fast enough"
argument_list|,
name|soft529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// wait for the hard commit
name|Long
name|hard529
init|=
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
operator|*
literal|5
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"hard529 wasn't fast enough"
argument_list|,
name|hard529
argument_list|)
expr_stmt|;
comment|// check for the searcher, should have happened right after soft commit
name|Long
name|searcher529
init|=
name|monitor
operator|.
name|searcher
operator|.
name|poll
argument_list|(
literal|5000
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"searcher529 wasn't fast enough"
argument_list|,
name|searcher529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// toss in another doc, shouldn't affect first hard commit time we poll
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"530"
argument_list|,
literal|"subject"
argument_list|,
literal|"just for noise/activity"
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
specifier|final
name|long
name|soft529Ms
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|soft529
operator|-
name|add529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"soft529 occurred too fast, in "
operator|+
name|soft529Ms
operator|+
literal|"ms, less than soft commit interval "
operator|+
name|softCommitWaitMillis
argument_list|,
name|soft529Ms
operator|>=
name|softCommitWaitMillis
argument_list|)
expr_stmt|;
specifier|final
name|long
name|hard529Ms
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|hard529
operator|-
name|add529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hard529 occurred too fast, in "
operator|+
name|hard529Ms
operator|+
literal|"ms, less than hard commit interval "
operator|+
name|hardCommitWaitMillis
argument_list|,
name|hard529Ms
operator|>=
name|hardCommitWaitMillis
argument_list|)
expr_stmt|;
comment|// however slow the machine was to do the soft commit compared to expected,
comment|// assume newSearcher had some magnitude of that much overhead as well
name|long
name|slowTestFudge
init|=
name|Math
operator|.
name|max
argument_list|(
literal|300
argument_list|,
literal|12
operator|*
operator|(
name|soft529Ms
operator|-
name|softCommitWaitMillis
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|long
name|softCommitToSearcherOpenMs
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|searcher529
operator|-
name|soft529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"searcher529 wasn't soon enough after soft529: Took "
operator|+
name|softCommitToSearcherOpenMs
operator|+
literal|"ms,>= acceptable "
operator|+
name|slowTestFudge
operator|+
literal|"ms (fudge)"
argument_list|,
name|softCommitToSearcherOpenMs
operator|<
name|slowTestFudge
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hard529 was before searcher529: "
operator|+
name|searcher529
operator|+
literal|" !<= "
operator|+
name|hard529
argument_list|,
name|searcher529
operator|<=
name|hard529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// there may have been (or will be) a second hard commit for 530
name|Long
name|hard530
init|=
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
operator|*
literal|5
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Tracker reports too many hard commits"
argument_list|,
operator|(
literal|null
operator|==
name|hard530
condition|?
literal|1
else|:
literal|2
operator|)
argument_list|,
name|hardTracker
operator|.
name|getCommitCount
argument_list|()
operator|-
name|startingHardCommits
argument_list|)
expr_stmt|;
comment|// there may have been a second soft commit for 530,
comment|// but if so it must have already happend
name|Long
name|soft530
init|=
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|soft530
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Tracker reports too many soft commits"
argument_list|,
literal|2
argument_list|,
name|softTracker
operator|.
name|getCommitCount
argument_list|()
operator|-
name|startingSoftCommits
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|hard530
condition|)
block|{
name|assertTrue
argument_list|(
literal|"soft530 after hard530: "
operator|+
name|soft530
operator|+
literal|" !<= "
operator|+
name|hard530
argument_list|,
name|soft530
operator|<=
name|hard530
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"soft530 after hard529 but no hard530: "
operator|+
name|soft530
operator|+
literal|" !<= "
operator|+
name|hard529
argument_list|,
name|soft530
operator|<=
name|hard529
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Tracker reports too many soft commits"
argument_list|,
literal|1
argument_list|,
name|softTracker
operator|.
name|getCommitCount
argument_list|()
operator|-
name|startingSoftCommits
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|soft530
operator|||
literal|null
operator|!=
name|hard530
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"at least one extra commit for 530, but no searcher"
argument_list|,
name|monitor
operator|.
name|searcher
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// clear commits
name|monitor
operator|.
name|hard
operator|.
name|clear
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|soft
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// wait a bit, w/o other action we shouldn't see any
comment|// new hard/soft commits
name|assertNull
argument_list|(
literal|"Got a hard commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got a soft commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|searcher
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|testSoftAndHardCommitMaxTimeDelete
specifier|public
name|void
name|testSoftAndHardCommitMaxTimeDelete
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|softCommitWaitMillis
init|=
literal|500
decl_stmt|;
specifier|final
name|int
name|hardCommitWaitMillis
init|=
literal|1200
decl_stmt|;
name|CommitTracker
name|hardTracker
init|=
name|updater
operator|.
name|commitTracker
decl_stmt|;
name|CommitTracker
name|softTracker
init|=
name|updater
operator|.
name|softCommitTracker
decl_stmt|;
name|int
name|startingHardCommits
init|=
name|hardTracker
operator|.
name|getCommitCount
argument_list|()
decl_stmt|;
name|int
name|startingSoftCommits
init|=
name|softTracker
operator|.
name|getCommitCount
argument_list|()
decl_stmt|;
name|softTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|softCommitWaitMillis
argument_list|)
expr_stmt|;
name|softTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|hardCommitWaitMillis
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// we don't want to overlap soft and hard opening searchers - this now blocks commits and we
comment|// are looking for prompt timings
name|hardTracker
operator|.
name|setOpenSearcher
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// add a doc and force a commit
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"529"
argument_list|,
literal|"subject"
argument_list|,
literal|"the doc we care about in this test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Long
name|soft529
decl_stmt|;
name|Long
name|hard529
decl_stmt|;
comment|/*** an explicit commit can (and should) clear pending auto-commits     long postAdd529 = System.currentTimeMillis();      // wait for first hard/soft commit     Long soft529 = monitor.soft.poll(softCommitWaitMillis * 3, MILLISECONDS);     assertNotNull("soft529 wasn't fast enough", soft529);     Long manCommit = monitor.hard.poll(0, MILLISECONDS);      assertNotNull("manCommit wasn't fast enough", manCommit);     assertTrue("forced manCommit didn't happen when it should have: " +          manCommit + " !<= " + postAdd529,          manCommit<= postAdd529);          Long hard529 = monitor.hard.poll(hardCommitWaitMillis * 2, MILLISECONDS);     assertNotNull("hard529 wasn't fast enough", hard529);      monitor.assertSaneOffers();  ***/
name|monitor
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Delete the document
name|long
name|del529
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"529"
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// Wait for the soft commit with some fudge
name|soft529
operator|=
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
operator|*
literal|5
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"soft529 wasn't fast enough"
argument_list|,
name|soft529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// check for the searcher, should have happened right after soft commit
name|Long
name|searcher529
init|=
name|monitor
operator|.
name|searcher
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"searcher529 wasn't fast enough"
argument_list|,
name|searcher529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// toss in another doc, shouldn't affect first hard commit time we poll
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"550"
argument_list|,
literal|"subject"
argument_list|,
literal|"just for noise/activity"
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait for the hard commit
name|hard529
operator|=
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
operator|*
literal|5
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"hard529 wasn't fast enough"
argument_list|,
name|hard529
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
specifier|final
name|long
name|soft529Ms
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|soft529
operator|-
name|del529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"soft529 occurred too fast, in "
operator|+
name|soft529Ms
operator|+
literal|"ms, less than soft commit interval "
operator|+
name|softCommitWaitMillis
argument_list|,
name|soft529Ms
operator|>=
name|softCommitWaitMillis
argument_list|)
expr_stmt|;
specifier|final
name|long
name|hard529Ms
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|hard529
operator|-
name|del529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hard529 occurred too fast, in "
operator|+
name|hard529Ms
operator|+
literal|"ms, less than hard commit interval "
operator|+
name|hardCommitWaitMillis
argument_list|,
name|hard529Ms
operator|>=
name|hardCommitWaitMillis
argument_list|)
expr_stmt|;
comment|// however slow the machine was to do the soft commit compared to expected,
comment|// assume newSearcher had some magnitude of that much overhead as well
name|long
name|slowTestFudge
init|=
name|Math
operator|.
name|max
argument_list|(
literal|300
argument_list|,
literal|12
operator|*
operator|(
name|soft529Ms
operator|-
name|softCommitWaitMillis
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|long
name|softCommitToSearcherOpenMs
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|searcher529
operator|-
name|soft529
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"searcher529 wasn't soon enough after soft529: Took "
operator|+
name|softCommitToSearcherOpenMs
operator|+
literal|"ms,>= acceptable "
operator|+
name|slowTestFudge
operator|+
literal|"ms (fudge)"
argument_list|,
name|softCommitToSearcherOpenMs
operator|<
name|slowTestFudge
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hard529 was before searcher529: "
operator|+
name|searcher529
operator|+
literal|" !<= "
operator|+
name|hard529
argument_list|,
name|searcher529
operator|<=
name|hard529
argument_list|)
expr_stmt|;
comment|// ensure we wait for the last searcher we triggered with 550
name|monitor
operator|.
name|searcher
operator|.
name|poll
argument_list|(
literal|5000
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// ensure we wait for the commits on 550
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
literal|5000
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|5000
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// clear commits
name|monitor
operator|.
name|hard
operator|.
name|clear
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|soft
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// wait a bit, w/o other action we shouldn't see any
comment|// new hard/soft commits
name|assertNull
argument_list|(
literal|"Got a hard commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got a soft commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|searcher
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|testSoftAndHardCommitMaxTimeRapidAdds
specifier|public
name|void
name|testSoftAndHardCommitMaxTimeRapidAdds
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|softCommitWaitMillis
init|=
literal|500
decl_stmt|;
specifier|final
name|int
name|hardCommitWaitMillis
init|=
literal|1200
decl_stmt|;
name|CommitTracker
name|hardTracker
init|=
name|updater
operator|.
name|commitTracker
decl_stmt|;
name|CommitTracker
name|softTracker
init|=
name|updater
operator|.
name|softCommitTracker
decl_stmt|;
name|softTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|softCommitWaitMillis
argument_list|)
expr_stmt|;
name|softTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setTimeUpperBound
argument_list|(
name|hardCommitWaitMillis
argument_list|)
expr_stmt|;
name|hardTracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// we don't want to overlap soft and hard opening searchers - this now blocks commits and we
comment|// are looking for prompt timings
name|hardTracker
operator|.
name|setOpenSearcher
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// try to add 5 docs really fast
name|long
name|fast5start
init|=
name|System
operator|.
name|nanoTime
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|500
operator|+
name|i
argument_list|,
literal|"subject"
argument_list|,
literal|"five fast docs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|fast5end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|300
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
comment|// minus a tad of slop
name|long
name|fast5time
init|=
literal|1
operator|+
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|fast5end
operator|-
name|fast5start
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
comment|// total time for all 5 adds determines the number of soft to expect
name|long
name|expectedSoft
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|fast5time
operator|/
name|softCommitWaitMillis
argument_list|)
decl_stmt|;
name|long
name|expectedHard
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|fast5time
operator|/
name|hardCommitWaitMillis
argument_list|)
decl_stmt|;
name|expectedSoft
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|expectedSoft
argument_list|)
expr_stmt|;
name|expectedHard
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|expectedHard
argument_list|)
expr_stmt|;
comment|// note: counting from 1 for multiplication
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|expectedSoft
condition|;
name|i
operator|++
control|)
block|{
comment|// Wait for the soft commit with plenty of fudge to survive nasty envs
name|Long
name|soft
init|=
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
operator|*
literal|3
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|soft
operator|!=
literal|null
operator|||
name|i
operator|==
literal|1
condition|)
block|{
name|assertNotNull
argument_list|(
name|i
operator|+
literal|": soft wasn't fast enough"
argument_list|,
name|soft
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// have to assume none of the docs were added until
comment|// very end of the add window
name|long
name|softMs
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|soft
operator|-
name|fast5end
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i
operator|+
literal|": soft occurred too fast: "
operator|+
name|softMs
operator|+
literal|"< ("
operator|+
name|softCommitWaitMillis
operator|+
literal|" * "
operator|+
name|i
operator|+
literal|")"
argument_list|,
name|softMs
operator|>=
operator|(
name|softCommitWaitMillis
operator|*
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we may have guessed wrong and there were fewer commits
name|assertNull
argument_list|(
literal|"Got a soft commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|2000
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// note: counting from 1 for multiplication
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|expectedHard
condition|;
name|i
operator|++
control|)
block|{
comment|// wait for the hard commit, shouldn't need any fudge given
comment|// other actions already taken
name|Long
name|hard
init|=
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
argument_list|,
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|i
operator|+
literal|": hard wasn't fast enough"
argument_list|,
name|hard
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
comment|// have to assume none of the docs were added until
comment|// very end of the add window
name|long
name|hardMs
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|hard
operator|-
name|fast5end
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i
operator|+
literal|": hard occurred too fast: "
operator|+
name|hardMs
operator|+
literal|"< ("
operator|+
name|hardCommitWaitMillis
operator|+
literal|" * "
operator|+
name|i
operator|+
literal|")"
argument_list|,
name|hardMs
operator|>=
operator|(
name|hardCommitWaitMillis
operator|*
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// we are only guessing how many commits we may see, allow one extra of each
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
name|softCommitWaitMillis
operator|+
literal|200
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
name|hardCommitWaitMillis
operator|+
literal|200
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// clear commits
name|monitor
operator|.
name|hard
operator|.
name|clear
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|soft
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// wait a bit, w/o other action we shouldn't see any
comment|// new hard/soft commits
name|assertNull
argument_list|(
literal|"Got a hard commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|hard
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got a soft commit we weren't expecting"
argument_list|,
name|monitor
operator|.
name|soft
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|assertSaneOffers
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|MockEventListener
class|class
name|MockEventListener
implements|implements
name|SolrEventListener
block|{
comment|// use capacity bound Queues just so we're sure we don't OOM
DECL|field|soft
specifier|public
specifier|final
name|BlockingQueue
argument_list|<
name|Long
argument_list|>
name|soft
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
DECL|field|hard
specifier|public
specifier|final
name|BlockingQueue
argument_list|<
name|Long
argument_list|>
name|hard
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|public
specifier|final
name|BlockingQueue
argument_list|<
name|Long
argument_list|>
name|searcher
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// if non enpty, then at least one offer failed (queues full)
DECL|field|fail
specifier|private
name|StringBuffer
name|fail
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
DECL|method|MockEventListener
specifier|public
name|MockEventListener
parameter_list|()
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
name|Long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|searcher
operator|.
name|offer
argument_list|(
name|now
argument_list|)
condition|)
name|fail
operator|.
name|append
argument_list|(
literal|", newSearcher @ "
operator|+
name|now
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{
name|Long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hard
operator|.
name|offer
argument_list|(
name|now
argument_list|)
condition|)
name|fail
operator|.
name|append
argument_list|(
literal|", hardCommit @ "
operator|+
name|now
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postSoftCommit
specifier|public
name|void
name|postSoftCommit
parameter_list|()
block|{
name|Long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|soft
operator|.
name|offer
argument_list|(
name|now
argument_list|)
condition|)
name|fail
operator|.
name|append
argument_list|(
literal|", softCommit @ "
operator|+
name|now
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|soft
operator|.
name|clear
argument_list|()
expr_stmt|;
name|hard
operator|.
name|clear
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSaneOffers
specifier|public
name|void
name|assertSaneOffers
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Failure of MockEventListener"
operator|+
name|fail
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fail
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

