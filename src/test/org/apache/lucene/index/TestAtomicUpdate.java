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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|queryParser
operator|.
name|*
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
name|io
operator|.
name|File
import|;
end_import

begin_class
DECL|class|TestAtomicUpdate
specifier|public
class|class
name|TestAtomicUpdate
extends|extends
name|LuceneTestCase
block|{
DECL|field|ANALYZER
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|class|TimedThread
specifier|private
specifier|static
specifier|abstract
class|class
name|TimedThread
extends|extends
name|Thread
block|{
DECL|field|failed
name|boolean
name|failed
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|RUN_TIME_SEC
specifier|private
specifier|static
name|int
name|RUN_TIME_SEC
init|=
literal|3
decl_stmt|;
DECL|field|allThreads
specifier|private
name|TimedThread
index|[]
name|allThreads
decl_stmt|;
DECL|method|doWork
specifier|abstract
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Throwable
function_decl|;
DECL|method|TimedThread
name|TimedThread
parameter_list|(
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|this
operator|.
name|allThreads
operator|=
name|threads
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
operator|*
name|RUN_TIME_SEC
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
try|try
block|{
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
operator|&&
operator|!
name|anyErrors
argument_list|()
condition|)
block|{
name|doWork
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|anyErrors
specifier|private
name|boolean
name|anyErrors
parameter_list|()
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
name|allThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|allThreads
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|allThreads
index|[
name|i
index|]
operator|.
name|failed
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
block|}
DECL|class|IndexerThread
specifier|private
specifier|static
class|class
name|IndexerThread
extends|extends
name|TimedThread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|super
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
DECL|method|doWork
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Update all 100 docs...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|RANDOM
operator|.
name|nextInt
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
name|i
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
name|UN_TOKENIZED
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
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
operator|+
literal|10
operator|*
name|count
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
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
name|i
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|SearcherThread
specifier|private
specifier|static
class|class
name|SearcherThread
extends|extends
name|TimedThread
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|super
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
DECL|method|doWork
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Throwable
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
name|t
throw|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*     Run one indexer and 2 searchers against single index as     stress test.   */
DECL|method|runTest
specifier|public
name|void
name|runTest
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|Exception
block|{
name|TimedThread
index|[]
name|threads
init|=
operator|new
name|TimedThread
index|[
literal|4
index|]
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|ANALYZER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Establish a base index of 100 docs:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
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
name|Integer
operator|.
name|toString
argument_list|(
name|i
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
name|UN_TOKENIZED
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
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|IndexerThread
name|indexerThread
init|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
literal|0
index|]
operator|=
name|indexerThread
expr_stmt|;
name|indexerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|IndexerThread
name|indexerThread2
init|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
literal|1
index|]
operator|=
name|indexerThread2
expr_stmt|;
name|indexerThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|SearcherThread
name|searcherThread1
init|=
operator|new
name|SearcherThread
argument_list|(
name|directory
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
literal|2
index|]
operator|=
name|searcherThread1
expr_stmt|;
name|searcherThread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|SearcherThread
name|searcherThread2
init|=
operator|new
name|SearcherThread
argument_list|(
name|directory
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
literal|3
index|]
operator|=
name|searcherThread2
expr_stmt|;
name|searcherThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|indexerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|indexerThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|searcherThread1
operator|.
name|join
argument_list|()
expr_stmt|;
name|searcherThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected exception in indexer"
argument_list|,
operator|!
name|indexerThread
operator|.
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected exception in indexer2"
argument_list|,
operator|!
name|indexerThread2
operator|.
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected exception in search1"
argument_list|,
operator|!
name|searcherThread1
operator|.
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected exception in search2"
argument_list|,
operator|!
name|searcherThread2
operator|.
name|failed
argument_list|)
expr_stmt|;
comment|//System.out.println("    Writer: " + indexerThread.count + " iterations");
comment|//System.out.println("Searcher 1: " + searcherThread1.count + " searchers created");
comment|//System.out.println("Searcher 2: " + searcherThread2.count + " searchers created");
block|}
comment|/*     Run above stress test against RAMDirectory and then     FSDirectory.   */
DECL|method|testAtomicUpdates
specifier|public
name|void
name|testAtomicUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
decl_stmt|;
comment|// First in a RAM directory:
name|directory
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Second in an FSDirectory:
name|String
name|tempDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
name|File
name|dirPath
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"lucene.test.atomic"
argument_list|)
decl_stmt|;
name|directory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

