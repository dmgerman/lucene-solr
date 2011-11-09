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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|CountDownLatch
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
name|document
operator|.
name|TextField
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
name|store
operator|.
name|LockObtainFailedException
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

begin_class
DECL|class|TestIndexWriterNRTIsCurrent
specifier|public
class|class
name|TestIndexWriterNRTIsCurrent
extends|extends
name|LuceneTestCase
block|{
DECL|class|ReaderHolder
specifier|public
specifier|static
class|class
name|ReaderHolder
block|{
DECL|field|reader
specifier|volatile
name|IndexReader
name|reader
decl_stmt|;
DECL|field|stop
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
block|}
DECL|method|testIsCurrentWithThreads
specifier|public
name|void
name|testIsCurrentWithThreads
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
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
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ReaderHolder
name|holder
init|=
operator|new
name|ReaderHolder
argument_list|()
decl_stmt|;
name|ReaderThread
index|[]
name|threads
init|=
operator|new
name|ReaderThread
index|[
name|atLeast
argument_list|(
literal|3
argument_list|)
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|WriterThread
name|writerThread
init|=
operator|new
name|WriterThread
argument_list|(
name|holder
argument_list|,
name|writer
argument_list|,
name|atLeast
argument_list|(
literal|500
argument_list|)
argument_list|,
name|random
argument_list|,
name|latch
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|ReaderThread
argument_list|(
name|holder
argument_list|,
name|latch
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|writerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|writerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|boolean
name|failed
init|=
name|writerThread
operator|.
name|failed
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|failed
condition|)
name|writerThread
operator|.
name|failed
operator|.
name|printStackTrace
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|failed
operator|!=
literal|null
condition|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|failed
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|writer
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
DECL|class|WriterThread
specifier|public
specifier|static
class|class
name|WriterThread
extends|extends
name|Thread
block|{
DECL|field|holder
specifier|private
specifier|final
name|ReaderHolder
name|holder
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|numOps
specifier|private
specifier|final
name|int
name|numOps
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|countdown
specifier|private
name|boolean
name|countdown
init|=
literal|true
decl_stmt|;
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|failed
name|Throwable
name|failed
decl_stmt|;
DECL|method|WriterThread
name|WriterThread
parameter_list|(
name|ReaderHolder
name|holder
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numOps
parameter_list|,
name|Random
name|random
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|holder
operator|=
name|holder
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|numOps
operator|=
name|numOps
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|IndexReader
name|currentReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|holder
operator|.
name|reader
operator|=
name|currentReader
operator|=
name|writer
operator|.
name|getReader
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
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
name|numOps
operator|&&
operator|!
name|holder
operator|.
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|float
name|nextOp
init|=
name|random
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextOp
operator|<
literal|0.3
condition|)
block|{
name|term
operator|.
name|set
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
name|term
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nextOp
operator|<
literal|0.5
condition|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|term
operator|.
name|set
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|holder
operator|.
name|reader
operator|!=
name|currentReader
condition|)
block|{
name|holder
operator|.
name|reader
operator|=
name|currentReader
expr_stmt|;
if|if
condition|(
name|countdown
condition|)
block|{
name|countdown
operator|=
literal|false
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|newReader
init|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|currentReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
name|currentReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|currentReader
operator|=
name|newReader
expr_stmt|;
block|}
if|if
condition|(
name|currentReader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
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
name|failed
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|holder
operator|.
name|reader
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|countdown
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|currentReader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|currentReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{           }
block|}
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
literal|"writer stopped - forced by reader: "
operator|+
name|holder
operator|.
name|stop
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ReaderThread
specifier|public
specifier|static
specifier|final
class|class
name|ReaderThread
extends|extends
name|Thread
block|{
DECL|field|holder
specifier|private
specifier|final
name|ReaderHolder
name|holder
decl_stmt|;
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|failed
name|Throwable
name|failed
decl_stmt|;
DECL|method|ReaderThread
name|ReaderThread
parameter_list|(
name|ReaderHolder
name|holder
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|holder
operator|=
name|holder
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|failed
operator|=
name|e
expr_stmt|;
return|return;
block|}
name|IndexReader
name|reader
decl_stmt|;
while|while
condition|(
operator|(
name|reader
operator|=
name|holder
operator|.
name|reader
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|reader
operator|.
name|tryIncRef
argument_list|()
condition|)
block|{
try|try
block|{
name|boolean
name|current
init|=
name|reader
operator|.
name|isCurrent
argument_list|()
decl_stmt|;
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
literal|"Thread: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|+
literal|" Reader: "
operator|+
name|reader
operator|+
literal|" isCurrent:"
operator|+
name|current
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
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
literal|"FAILED Thread: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|+
literal|" Reader: "
operator|+
name|reader
operator|+
literal|" isCurrent: false"
argument_list|)
expr_stmt|;
block|}
name|failed
operator|=
name|e
expr_stmt|;
name|holder
operator|.
name|stop
operator|=
literal|true
expr_stmt|;
return|return;
block|}
finally|finally
block|{
try|try
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|failed
operator|==
literal|null
condition|)
block|{
name|failed
operator|=
name|e
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

