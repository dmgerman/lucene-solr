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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|atomic
operator|.
name|AtomicBoolean
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestMultiCollector
specifier|public
class|class
name|TestMultiCollector
extends|extends
name|LuceneTestCase
block|{
DECL|class|TerminateAfterCollector
specifier|private
specifier|static
class|class
name|TerminateAfterCollector
extends|extends
name|FilterCollector
block|{
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|terminateAfter
specifier|private
specifier|final
name|int
name|terminateAfter
decl_stmt|;
DECL|method|TerminateAfterCollector
specifier|public
name|TerminateAfterCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|int
name|terminateAfter
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|terminateAfter
operator|=
name|terminateAfter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>=
name|terminateAfter
condition|)
block|{
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
specifier|final
name|LeafCollector
name|in
init|=
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|in
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>=
name|terminateAfter
condition|)
block|{
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|class|SetScorerCollector
specifier|private
specifier|static
class|class
name|SetScorerCollector
extends|extends
name|FilterCollector
block|{
DECL|field|setScorerCalled
specifier|private
specifier|final
name|AtomicBoolean
name|setScorerCalled
decl_stmt|;
DECL|method|SetScorerCollector
specifier|public
name|SetScorerCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|AtomicBoolean
name|setScorerCalled
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|setScorerCalled
operator|=
name|setScorerCalled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
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
name|super
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|setScorerCalled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|method|testCollectionTerminatedExceptionHandling
specifier|public
name|void
name|testCollectionTerminatedExceptionHandling
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|TotalHitCountCollector
argument_list|,
name|Integer
argument_list|>
name|expectedCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numCollectors
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
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
name|numCollectors
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|terminateAfter
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
operator|+
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|expectedCount
init|=
name|terminateAfter
operator|>
name|numDocs
condition|?
name|numDocs
else|:
name|terminateAfter
decl_stmt|;
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
name|collector
argument_list|,
name|expectedCount
argument_list|)
expr_stmt|;
name|collectors
operator|.
name|add
argument_list|(
operator|new
name|TerminateAfterCollector
argument_list|(
name|collector
argument_list|,
name|terminateAfter
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TotalHitCountCollector
argument_list|,
name|Integer
argument_list|>
name|expectedCount
range|:
name|expectedCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|expectedCount
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|expectedCount
operator|.
name|getKey
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSetScorerAfterCollectionTerminated
specifier|public
name|void
name|testSetScorerAfterCollectionTerminated
parameter_list|()
throws|throws
name|IOException
block|{
name|Collector
name|collector1
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|Collector
name|collector2
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|setScorerCalled1
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|collector1
operator|=
operator|new
name|SetScorerCollector
argument_list|(
name|collector1
argument_list|,
name|setScorerCalled1
argument_list|)
expr_stmt|;
name|AtomicBoolean
name|setScorerCalled2
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|collector2
operator|=
operator|new
name|SetScorerCollector
argument_list|(
name|collector2
argument_list|,
name|setScorerCalled2
argument_list|)
expr_stmt|;
name|collector1
operator|=
operator|new
name|TerminateAfterCollector
argument_list|(
name|collector1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|collector2
operator|=
operator|new
name|TerminateAfterCollector
argument_list|(
name|collector2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Scorer
name|scorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|collector1
argument_list|,
name|collector2
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|collectors
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|Collector
name|collector
init|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
argument_list|)
decl_stmt|;
name|LeafCollector
name|leafCollector
init|=
name|collector
operator|.
name|getLeafCollector
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|setScorerCalled1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|setScorerCalled2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|leafCollector
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|leafCollector
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|setScorerCalled1
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setScorerCalled2
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|setScorerCalled1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|setScorerCalled2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|CollectionTerminatedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|leafCollector
operator|.
name|collect
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|setScorerCalled1
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setScorerCalled2
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|setScorerCalled1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|setScorerCalled2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

