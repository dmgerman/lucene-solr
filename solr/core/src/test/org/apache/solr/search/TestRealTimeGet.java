begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|index
operator|.
name|IndexWriterConfig
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
name|IndexSearcher
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
name|Query
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
name|TermQuery
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
name|TopDocs
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
name|RAMDirectory
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
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|SolrTestCaseJ4
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
name|SolrException
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
name|request
operator|.
name|SolrQueryRequest
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|ConcurrentHashMap
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
name|AtomicInteger
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
name|AtomicLong
import|;
end_import

begin_class
DECL|class|TestRealTimeGet
specifier|public
class|class
name|TestRealTimeGet
extends|extends
name|SolrTestCaseJ4
block|{
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
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
comment|/***   @Test   public void testGetRealtime() throws Exception {     SolrQueryRequest sr1 = req("q","foo");     IndexReader r1 = sr1.getCore().getRealtimeReader();      assertU(adoc("id","1"));      IndexReader r2 = sr1.getCore().getRealtimeReader();     assertNotSame(r1, r2);     int refcount = r2.getRefCount();      // make sure a new reader wasn't opened     IndexReader r3 = sr1.getCore().getRealtimeReader();     assertSame(r2, r3);     assertEquals(refcount+1, r3.getRefCount());      assertU(commit());      // this is not critical, but currently a commit does not refresh the reader     // if nothing has changed     IndexReader r4 = sr1.getCore().getRealtimeReader();     assertEquals(refcount+2, r4.getRefCount());       r1.decRef();     r2.decRef();     r3.decRef();     r4.decRef();     sr1.close();   }   ***/
DECL|field|model
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|model
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|committedModel
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|committedModel
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|snapshotCount
name|long
name|snapshotCount
decl_stmt|;
DECL|field|committedModelClock
name|long
name|committedModelClock
decl_stmt|;
DECL|field|lastId
specifier|volatile
name|int
name|lastId
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
init|=
literal|"val_l"
decl_stmt|;
DECL|field|syncArr
name|Object
index|[]
name|syncArr
decl_stmt|;
DECL|field|sanityModel
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|sanityModel
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|initModel
specifier|private
name|void
name|initModel
parameter_list|(
name|int
name|ndocs
parameter_list|)
block|{
name|snapshotCount
operator|=
literal|0
expr_stmt|;
name|committedModelClock
operator|=
literal|0
expr_stmt|;
name|lastId
operator|=
literal|0
expr_stmt|;
name|syncArr
operator|=
operator|new
name|Object
index|[
name|ndocs
index|]
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|model
operator|.
name|put
argument_list|(
name|i
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|syncArr
index|[
name|i
index|]
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
name|committedModel
operator|.
name|putAll
argument_list|(
name|model
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStressGetRealtime
specifier|public
name|void
name|testStressGetRealtime
parameter_list|()
throws|throws
name|Exception
block|{
comment|// update variables
specifier|final
name|int
name|commitPercent
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|softCommitPercent
init|=
literal|50
decl_stmt|;
comment|// what percent of the commits are soft
specifier|final
name|int
name|deletePercent
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|deleteByQueryPercent
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|ndocs
init|=
literal|100
decl_stmt|;
name|int
name|nWriteThreads
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|maxConcurrentCommits
init|=
literal|2
decl_stmt|;
comment|// number of committers at a time... needed if we want to avoid commit errors due to exceeding the max
comment|// query variables
specifier|final
name|int
name|percentRealtimeQuery
init|=
literal|0
decl_stmt|;
comment|// realtime get is not implemented yet
specifier|final
name|AtomicLong
name|operations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// number of query operations to perform in total       // TODO: once lucene level passes, we can move on to the solr level
name|int
name|nReadThreads
init|=
literal|10
decl_stmt|;
name|initModel
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|numCommitting
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
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
name|nWriteThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"WRITER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|operations
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|oper
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
condition|)
block|{
if|if
condition|(
name|numCommitting
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|maxConcurrentCommits
condition|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|newCommittedModel
decl_stmt|;
name|long
name|version
decl_stmt|;
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
name|newCommittedModel
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|(
name|model
argument_list|)
expr_stmt|;
comment|// take a snapshot
name|version
operator|=
name|snapshotCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|softCommitPercent
condition|)
name|assertU
argument_list|(
name|h
operator|.
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
comment|// install this snapshot only if it's newer than the current one
if|if
condition|(
name|version
operator|>=
name|committedModelClock
condition|)
block|{
name|committedModel
operator|=
name|newCommittedModel
expr_stmt|;
name|committedModelClock
operator|=
name|version
expr_stmt|;
block|}
block|}
block|}
name|numCommitting
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
name|Object
name|sync
init|=
name|syncArr
index|[
name|id
index|]
decl_stmt|;
comment|// set the lastId before we actually change it sometimes to try and
comment|// uncover more race conditions between writing and reading
name|boolean
name|before
init|=
name|rand
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
comment|// We can't concurrently update the same document and retain our invariants of increasing values
comment|// since we can't guarantee what order the updates will be executed.
synchronized|synchronized
init|(
name|sync
init|)
block|{
name|Long
name|val
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|long
name|nextVal
init|=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
condition|)
block|{
name|assertU
argument_list|(
literal|"<delete><id>"
operator|+
name|id
operator|+
literal|"</id></delete>"
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|-
name|nextVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
operator|+
name|deleteByQueryPercent
condition|)
block|{
name|assertU
argument_list|(
literal|"<delete><query>id:"
operator|+
name|id
operator|+
literal|"</query></delete>"
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|-
name|nextVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
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
name|nReadThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"READER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|operations
operator|.
name|decrementAndGet
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|int
name|oper
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// bias toward a recently changed doc
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|25
condition|?
name|lastId
else|:
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
comment|// when indexing, we update the index, then the model
comment|// so when querying, we should first check the model, and then the index
name|boolean
name|realTime
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|percentRealtimeQuery
decl_stmt|;
name|long
name|val
decl_stmt|;
if|if
condition|(
name|realTime
condition|)
block|{
name|val
operator|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
name|val
operator|=
name|committedModel
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|SolrQueryRequest
name|sreq
decl_stmt|;
if|if
condition|(
name|realTime
condition|)
block|{
name|sreq
operator|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|sreq
argument_list|)
decl_stmt|;
name|Map
name|rsp
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|List
name|doclist
init|=
call|(
name|List
call|)
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|doclist
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// there's no info we can get back with a delete, so not much we can check without further synchronization
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doclist
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|foundVal
init|=
call|(
name|Long
call|)
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|doclist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|foundVal
operator|>=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|operations
operator|.
name|set
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
annotation|@
name|Test
DECL|method|testStressLuceneNRT
specifier|public
name|void
name|testStressLuceneNRT
parameter_list|()
throws|throws
name|Exception
block|{
comment|// update variables
specifier|final
name|int
name|commitPercent
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|softCommitPercent
init|=
literal|50
decl_stmt|;
comment|// what percent of the commits are soft
specifier|final
name|int
name|deletePercent
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|deleteByQueryPercent
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|ndocs
init|=
literal|100
decl_stmt|;
name|int
name|nWriteThreads
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|maxConcurrentCommits
init|=
literal|2
decl_stmt|;
comment|// number of committers at a time... needed if we want to avoid commit errors due to exceeding the max
specifier|final
name|boolean
name|tombstones
init|=
literal|false
decl_stmt|;
comment|// query variables
specifier|final
name|AtomicLong
name|operations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
comment|// number of query operations to perform in total       // TODO: temporarily high due to lack of stability
name|int
name|nReadThreads
init|=
literal|10
decl_stmt|;
name|initModel
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|numCommitting
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
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
name|nWriteThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"WRITER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|operations
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|oper
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
condition|)
block|{
if|if
condition|(
name|numCommitting
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|maxConcurrentCommits
condition|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|newCommittedModel
decl_stmt|;
name|long
name|version
decl_stmt|;
name|IndexReader
name|oldReader
decl_stmt|;
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
name|newCommittedModel
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|(
name|model
argument_list|)
expr_stmt|;
comment|// take a snapshot
name|version
operator|=
name|snapshotCount
operator|++
expr_stmt|;
name|oldReader
operator|=
name|reader
expr_stmt|;
name|oldReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|// increment the reference since we will use this for reopening
block|}
name|IndexReader
name|newReader
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|softCommitPercent
condition|)
block|{
comment|// assertU(h.commit("softCommit","true"));
name|newReader
operator|=
name|oldReader
operator|.
name|reopen
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// assertU(commit());
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|newReader
operator|=
name|oldReader
operator|.
name|reopen
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
comment|// install the new reader if it's newest (and check the current version since another reader may have already been installed)
if|if
condition|(
name|newReader
operator|.
name|getVersion
argument_list|()
operator|>
name|reader
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
comment|// install this snapshot only if it's newer than the current one
if|if
condition|(
name|version
operator|>=
name|committedModelClock
condition|)
block|{
name|committedModel
operator|=
name|newCommittedModel
expr_stmt|;
name|committedModelClock
operator|=
name|version
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|newReader
operator|!=
name|oldReader
condition|)
block|{
comment|// if the same reader, don't decRef.
name|newReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
name|oldReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
name|numCommitting
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
name|Object
name|sync
init|=
name|syncArr
index|[
name|id
index|]
decl_stmt|;
comment|// set the lastId before we actually change it sometimes to try and
comment|// uncover more race conditions between writing and reading
name|boolean
name|before
init|=
name|rand
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
comment|// We can't concurrently update the same document and retain our invariants of increasing values
comment|// since we can't guarantee what order the updates will be executed.
synchronized|synchronized
init|(
name|sync
init|)
block|{
name|Long
name|val
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|long
name|nextVal
init|=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
condition|)
block|{
comment|// assertU("<delete><id>" + id + "</id></delete>");
comment|// add tombstone first
if|if
condition|(
name|tombstones
condition|)
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
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|-
name|nextVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
operator|+
name|deleteByQueryPercent
condition|)
block|{
comment|//assertU("<delete><query>id:" + id + "</query></delete>");
comment|// add tombstone first
if|if
condition|(
name|tombstones
condition|)
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
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|-
name|nextVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// assertU(adoc("id",Integer.toString(id), field, Long.toString(nextVal)));
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
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|tombstones
condition|)
block|{
comment|// remove tombstone after new addition (this should be optional?)
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
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
name|nReadThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"READER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|operations
operator|.
name|decrementAndGet
argument_list|()
operator|>=
literal|0
condition|)
block|{
comment|// bias toward a recently changed doc
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|25
condition|?
name|lastId
else|:
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
comment|// when indexing, we update the index, then the model
comment|// so when querying, we should first check the model, and then the index
name|long
name|val
decl_stmt|;
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
name|val
operator|=
name|committedModel
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
decl_stmt|;
synchronized|synchronized
init|(
name|TestRealTimeGet
operator|.
name|this
init|)
block|{
name|r
operator|=
name|reader
expr_stmt|;
name|r
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
comment|//  sreq = req("wt","json", "q","id:"+Integer.toString(id), "omitHeader","true");
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|results
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|totalHits
operator|==
literal|0
operator|&&
name|tombstones
condition|)
block|{
comment|// if we couldn't find the doc, look for it's tombstone
name|q
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|results
operator|.
name|totalHits
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|val
operator|==
operator|-
literal|1L
condition|)
block|{
comment|// expected... no doc was added yet
continue|continue;
block|}
name|fail
argument_list|(
literal|"No documents or tombstones found for id "
operator|+
name|id
operator|+
literal|", expected at least "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|results
operator|.
name|totalHits
operator|==
literal|0
operator|&&
operator|!
name|tombstones
condition|)
block|{
comment|// nothing to do - we can't tell anything from a deleted doc without tombstones
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// we should have found the document, or it's tombstone
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|results
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|long
name|foundVal
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundVal
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"model_val="
operator|+
name|val
operator|+
literal|" foundVal="
operator|+
name|foundVal
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|foundVal
operator|>=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|operations
operator|.
name|set
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

