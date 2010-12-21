begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BytesRef
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
name|TermFreqVector
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
name|English
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestMultiThreadTermVectors
specifier|public
class|class
name|TestMultiThreadTermVectors
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|numDocs
specifier|public
name|int
name|numDocs
init|=
literal|100
decl_stmt|;
DECL|field|numThreads
specifier|public
name|int
name|numThreads
init|=
literal|3
decl_stmt|;
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//writer.setUseCompoundFile(false);
comment|//writer.infoStream = System.out;
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
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Fieldable
name|fld
init|=
name|newField
argument_list|(
literal|"field"
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
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|fld
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
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
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
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
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numThreads
condition|;
name|i
operator|++
control|)
name|testTermPositionVectors
argument_list|(
name|reader
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|/** close the opened reader */
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testTermPositionVectors
specifier|public
name|void
name|testTermPositionVectors
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
name|int
name|threadCount
parameter_list|)
throws|throws
name|Exception
block|{
name|MultiThreadTermVectorsReader
index|[]
name|mtr
init|=
operator|new
name|MultiThreadTermVectorsReader
index|[
name|threadCount
index|]
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|mtr
index|[
name|i
index|]
operator|=
operator|new
name|MultiThreadTermVectorsReader
argument_list|()
expr_stmt|;
name|mtr
index|[
name|i
index|]
operator|.
name|init
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/** run until all threads finished */
name|int
name|threadsAlive
init|=
name|mtr
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|threadsAlive
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("Threads alive");
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|threadsAlive
operator|=
name|mtr
operator|.
name|length
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
name|mtr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|mtr
index|[
name|i
index|]
operator|.
name|isAlive
argument_list|()
operator|==
literal|true
condition|)
block|{
break|break;
block|}
name|threadsAlive
operator|--
expr_stmt|;
block|}
block|}
name|long
name|totalTime
init|=
literal|0L
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
name|mtr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|totalTime
operator|+=
name|mtr
index|[
name|i
index|]
operator|.
name|timeElapsed
expr_stmt|;
name|mtr
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|//System.out.println("threadcount: " + mtr.length + " average term vector time: " + totalTime/mtr.length);
block|}
block|}
end_class

begin_class
DECL|class|MultiThreadTermVectorsReader
class|class
name|MultiThreadTermVectorsReader
implements|implements
name|Runnable
block|{
DECL|field|reader
specifier|private
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|t
specifier|private
name|Thread
name|t
init|=
literal|null
decl_stmt|;
DECL|field|runsToDo
specifier|private
specifier|final
name|int
name|runsToDo
init|=
literal|100
decl_stmt|;
DECL|field|timeElapsed
name|long
name|timeElapsed
init|=
literal|0
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|timeElapsed
operator|=
literal|0
expr_stmt|;
name|t
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|isAlive
specifier|public
name|boolean
name|isAlive
parameter_list|()
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|t
operator|.
name|isAlive
argument_list|()
return|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// run the test 100 times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|runsToDo
condition|;
name|i
operator|++
control|)
name|testTermVectors
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
block|}
return|return;
block|}
DECL|method|testTermVectors
specifier|private
name|void
name|testTermVectors
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check:
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|long
name|start
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|docId
init|=
literal|0
init|;
name|docId
operator|<
name|numDocs
condition|;
name|docId
operator|++
control|)
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|TermFreqVector
index|[]
name|vectors
init|=
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|timeElapsed
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
comment|// verify vectors result
name|verifyVectors
argument_list|(
name|vectors
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|timeElapsed
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
name|vectors
operator|=
operator|new
name|TermFreqVector
index|[
literal|1
index|]
expr_stmt|;
name|vectors
index|[
literal|0
index|]
operator|=
name|vector
expr_stmt|;
name|verifyVectors
argument_list|(
name|vectors
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyVectors
specifier|private
name|void
name|verifyVectors
parameter_list|(
name|TermFreqVector
index|[]
name|vectors
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|StringBuilder
name|temp
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|BytesRef
index|[]
name|terms
init|=
literal|null
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
name|vectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
operator|=
name|vectors
index|[
name|i
index|]
operator|.
name|getTerms
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|z
init|=
literal|0
init|;
name|z
operator|<
name|terms
operator|.
name|length
condition|;
name|z
operator|++
control|)
block|{
name|temp
operator|.
name|append
argument_list|(
name|terms
index|[
name|z
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|English
operator|.
name|intToEnglish
argument_list|(
name|num
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|temp
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"wrong term result"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

