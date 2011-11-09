begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|Random
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
name|FieldType
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
name|StringField
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
name|ScoreDoc
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestStressNRT
specifier|public
class|class
name|TestStressNRT
extends|extends
name|LuceneTestCase
block|{
DECL|field|reader
specifier|volatile
name|IndexReader
name|reader
decl_stmt|;
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
comment|// update variables
specifier|final
name|int
name|commitPercent
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|softCommitPercent
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// what percent of the commits are soft
specifier|final
name|int
name|deletePercent
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|int
name|deleteByQueryPercent
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ndocs
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nWriteThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|10
else|:
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxConcurrentCommits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|10
else|:
literal|5
argument_list|)
decl_stmt|;
comment|// number of committers at a time... needed if we want to avoid commit errors due to exceeding the max
specifier|final
name|boolean
name|tombstones
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// query variables
specifier|final
name|AtomicLong
name|operations
init|=
operator|new
name|AtomicLong
argument_list|(
name|atLeast
argument_list|(
literal|50000
argument_list|)
argument_list|)
decl_stmt|;
comment|// number of query operations to perform in total
specifier|final
name|int
name|nReadThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|10
else|:
literal|5
argument_list|)
decl_stmt|;
name|initModel
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|storedOnlyType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|storedOnlyType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: commitPercent="
operator|+
name|commitPercent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: softCommitPercent="
operator|+
name|softCommitPercent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: deletePercent="
operator|+
name|deletePercent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: deleteByQueryPercent="
operator|+
name|deleteByQueryPercent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: ndocs="
operator|+
name|ndocs
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: nWriteThreads="
operator|+
name|nWriteThreads
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: nReadThreads="
operator|+
name|nReadThreads
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: maxConcurrentCommits="
operator|+
name|maxConcurrentCommits
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: tombstones="
operator|+
name|tombstones
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: operations="
operator|+
name|operations
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
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
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setDoRandomOptimizeAssert
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|TestStressNRT
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
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": call writer.getReader"
argument_list|)
expr_stmt|;
block|}
name|newReader
operator|=
name|writer
operator|.
name|getReader
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": reopen reader="
operator|+
name|oldReader
operator|+
literal|" version="
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
name|newReader
operator|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|,
name|writer
operator|.
name|w
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// assertU(commit());
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": commit+reopen reader="
operator|+
name|oldReader
operator|+
literal|" version="
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": now reopen after commit"
argument_list|)
expr_stmt|;
block|}
name|newReader
operator|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|)
expr_stmt|;
block|}
comment|// Code below assumes newReader comes w/
comment|// extra ref:
if|if
condition|(
name|newReader
operator|==
literal|null
condition|)
block|{
name|oldReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|newReader
operator|=
name|oldReader
expr_stmt|;
block|}
name|oldReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|TestStressNRT
operator|.
name|this
init|)
block|{
comment|// install the new reader if it's newest (and check the current version since another reader may have already been installed)
comment|//System.out.println(Thread.currentThread().getName() + ": newVersion=" + newReader.getVersion());
assert|assert
name|newReader
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
assert|;
assert|assert
name|reader
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
assert|;
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": install new reader="
operator|+
name|newReader
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
comment|// Silly: forces fieldInfos to be
comment|// loaded so we don't hit IOE on later
comment|// reader.toString
name|newReader
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// install this snapshot only if it's newer than the current one
if|if
condition|(
name|version
operator|>=
name|committedModelClock
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": install new model version="
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
name|committedModel
operator|=
name|newCommittedModel
expr_stmt|;
name|committedModelClock
operator|=
name|version
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": skip install new model version="
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// if the same reader, don't decRef.
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": skip install new reader="
operator|+
name|newReader
argument_list|)
expr_stmt|;
block|}
name|newReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|numCommitting
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
name|random
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
name|newField
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
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|storedOnlyType
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": term delDocs id:"
operator|+
name|id
operator|+
literal|" nextVal="
operator|+
name|nextVal
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
name|newField
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
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|storedOnlyType
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": query delDocs id:"
operator|+
name|id
operator|+
literal|" nextVal="
operator|+
name|nextVal
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
name|newField
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
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|storedOnlyType
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": u id:"
operator|+
name|id
operator|+
literal|" val="
operator|+
name|nextVal
argument_list|)
expr_stmt|;
block|}
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
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": FAILED: unexpected exception"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
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
name|IndexReader
name|r
decl_stmt|;
synchronized|synchronized
init|(
name|TestStressNRT
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": s id="
operator|+
name|id
operator|+
literal|" val="
operator|+
name|val
operator|+
literal|" r="
operator|+
name|r
operator|.
name|getVersion
argument_list|()
argument_list|)
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
literal|10
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
comment|// if we couldn't find the doc, look for its tombstone
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
name|r
operator|.
name|decRef
argument_list|()
expr_stmt|;
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
operator|+
literal|" reader="
operator|+
name|r
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
comment|// we should have found the document, or its tombstone
if|if
condition|(
name|results
operator|.
name|totalHits
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: hits id:"
operator|+
name|id
operator|+
literal|" val="
operator|+
name|val
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|sd
range|:
name|results
operator|.
name|scoreDocs
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|sd
operator|.
name|doc
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  docID="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" id:"
operator|+
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" foundVal="
operator|+
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"id="
operator|+
name|id
operator|+
literal|" reader="
operator|+
name|r
operator|+
literal|" totalHits="
operator|+
name|results
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
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
name|fail
argument_list|(
literal|"foundVal="
operator|+
name|foundVal
operator|+
literal|" val="
operator|+
name|val
operator|+
literal|" id="
operator|+
name|id
operator|+
literal|" reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": FAILED: unexpected exception"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: close reader="
operator|+
name|reader
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
end_class

end_unit

