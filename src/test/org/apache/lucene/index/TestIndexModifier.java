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
name|Analyzer
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
name|SimpleAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
name|FSDirectory
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
name|java
operator|.
name|io
operator|.
name|File
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EmptyStackException
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
name|Stack
import|;
end_import

begin_comment
comment|/**  * Tests for the "IndexModifier" class, including accesses from two threads at the  * same time.  *   * @author Daniel Naber  * @deprecated  */
end_comment

begin_class
DECL|class|TestIndexModifier
specifier|public
class|class
name|TestIndexModifier
extends|extends
name|LuceneTestCase
block|{
DECL|field|docCount
specifier|private
name|int
name|docCount
init|=
literal|0
decl_stmt|;
DECL|field|allDocTerm
specifier|private
specifier|final
name|Term
name|allDocTerm
init|=
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
DECL|method|testIndex
specifier|public
name|void
name|testIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|ramDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexModifier
name|i
init|=
operator|new
name|IndexModifier
argument_list|(
name|ramDir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|flush
argument_list|()
expr_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|flush
argument_list|()
expr_stmt|;
name|i
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// depend on merge policy - assertEquals(3, i.docCount());
name|i
operator|.
name|deleteDocuments
argument_list|(
name|allDocTerm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|i
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
comment|//  Lucene defaults:
name|assertNull
argument_list|(
name|i
operator|.
name|getInfoStream
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|,
name|i
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10000
argument_list|,
name|i
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|i
operator|.
name|getMergeFactor
argument_list|()
argument_list|)
expr_stmt|;
comment|// test setting properties:
name|i
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|i
operator|.
name|setMergeFactor
argument_list|(
literal|25
argument_list|)
expr_stmt|;
name|i
operator|.
name|setMaxFieldLength
argument_list|(
literal|250000
argument_list|)
expr_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|i
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|i
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|i
operator|.
name|getMergeFactor
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|250000
argument_list|,
name|i
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// test setting properties when internally the reader is opened:
name|i
operator|.
name|deleteDocuments
argument_list|(
name|allDocTerm
argument_list|)
expr_stmt|;
name|i
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|i
operator|.
name|setMergeFactor
argument_list|(
literal|25
argument_list|)
expr_stmt|;
name|i
operator|.
name|setMaxFieldLength
argument_list|(
literal|250000
argument_list|)
expr_stmt|;
name|i
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|i
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|i
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|i
operator|.
name|getMergeFactor
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|250000
argument_list|,
name|i
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|i
operator|.
name|docCount
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
DECL|method|testExtendedIndex
specifier|public
name|void
name|testExtendedIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|ramDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|PowerIndex
name|powerIndex
init|=
operator|new
name|PowerIndex
argument_list|(
name|ramDir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|powerIndex
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|addDocument
argument_list|(
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|powerIndex
operator|.
name|docFreq
argument_list|(
name|allDocTerm
argument_list|)
argument_list|)
expr_stmt|;
name|powerIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDoc
specifier|private
name|Document
name|getDoc
parameter_list|()
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
literal|"body"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docCount
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
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"all"
argument_list|,
literal|"x"
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
argument_list|)
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|testIndexWithThreads
specifier|public
name|void
name|testIndexWithThreads
parameter_list|()
throws|throws
name|IOException
block|{
name|testIndexInternal
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testIndexInternal
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|testIndexInternal
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexInternal
specifier|private
name|void
name|testIndexInternal
parameter_list|(
name|int
name|maxWait
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|create
init|=
literal|true
decl_stmt|;
comment|//Directory rd = new RAMDirectory();
comment|// work on disk to make sure potential lock problems are tested:
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
if|if
condition|(
name|tempDir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"java.io.tmpdir undefined, cannot run test"
argument_list|)
throw|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"lucenetestindex"
argument_list|)
decl_stmt|;
name|Directory
name|rd
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|IndexThread
operator|.
name|id
operator|=
literal|0
expr_stmt|;
name|IndexThread
operator|.
name|idStack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|IndexModifier
name|index
init|=
operator|new
name|IndexModifier
argument_list|(
name|rd
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
decl_stmt|;
name|IndexThread
name|thread1
init|=
operator|new
name|IndexThread
argument_list|(
name|index
argument_list|,
name|maxWait
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|thread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|IndexThread
name|thread2
init|=
operator|new
name|IndexThread
argument_list|(
name|index
argument_list|,
name|maxWait
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|thread2
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|thread1
operator|.
name|isAlive
argument_list|()
operator|||
name|thread2
operator|.
name|isAlive
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|index
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|int
name|added
init|=
name|thread1
operator|.
name|added
operator|+
name|thread2
operator|.
name|added
decl_stmt|;
name|int
name|deleted
init|=
name|thread1
operator|.
name|deleted
operator|+
name|thread2
operator|.
name|deleted
decl_stmt|;
name|assertEquals
argument_list|(
name|added
operator|-
name|deleted
argument_list|,
name|index
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
name|rmDir
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
DECL|method|rmDir
specifier|private
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|class|PowerIndex
specifier|private
class|class
name|PowerIndex
extends|extends
name|IndexModifier
block|{
DECL|method|PowerIndex
specifier|public
name|PowerIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexReader
argument_list|()
expr_stmt|;
return|return
name|indexReader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|IndexThread
class|class
name|IndexThread
extends|extends
name|Thread
block|{
DECL|field|TEST_SECONDS
specifier|private
specifier|final
specifier|static
name|int
name|TEST_SECONDS
init|=
literal|3
decl_stmt|;
comment|// how many seconds to run each test
DECL|field|id
specifier|static
name|int
name|id
init|=
literal|0
decl_stmt|;
DECL|field|idStack
specifier|static
name|Stack
name|idStack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
DECL|field|added
name|int
name|added
init|=
literal|0
decl_stmt|;
DECL|field|deleted
name|int
name|deleted
init|=
literal|0
decl_stmt|;
DECL|field|maxWait
specifier|private
name|int
name|maxWait
init|=
literal|10
decl_stmt|;
DECL|field|index
specifier|private
name|IndexModifier
name|index
decl_stmt|;
DECL|field|threadNumber
specifier|private
name|int
name|threadNumber
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|method|IndexThread
name|IndexThread
parameter_list|(
name|IndexModifier
name|index
parameter_list|,
name|int
name|maxWait
parameter_list|,
name|int
name|threadNumber
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|maxWait
operator|=
name|maxWait
expr_stmt|;
name|this
operator|.
name|threadNumber
operator|=
name|threadNumber
expr_stmt|;
comment|// TODO: test case is not reproducible despite pseudo-random numbers:
name|random
operator|=
operator|new
name|Random
argument_list|(
literal|101
operator|+
name|threadNumber
argument_list|)
expr_stmt|;
comment|// constant seed for better reproducability
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
operator|*
name|TEST_SECONDS
decl_stmt|;
try|try
block|{
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
name|int
name|rand
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|101
argument_list|)
decl_stmt|;
if|if
condition|(
name|rand
operator|<
literal|5
condition|)
block|{
name|index
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rand
operator|<
literal|60
condition|)
block|{
name|Document
name|doc
init|=
name|getDocument
argument_list|()
decl_stmt|;
name|index
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|idStack
operator|.
name|push
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|added
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// we just delete the last document added and remove it
comment|// from the id stack so that it won't be removed twice:
name|String
name|delId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|delId
operator|=
operator|(
name|String
operator|)
name|idStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EmptyStackException
name|e
parameter_list|)
block|{
continue|continue;
block|}
name|Term
name|delTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
operator|new
name|Integer
argument_list|(
name|delId
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|index
operator|.
name|deleteDocuments
argument_list|(
name|delTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|delCount
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Internal error: "
operator|+
name|threadNumber
operator|+
literal|" deleted "
operator|+
name|delCount
operator|+
literal|" documents, term="
operator|+
name|delTerm
argument_list|)
throw|;
block|}
name|deleted
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|maxWait
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|rand
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
comment|//System.out.println("waiting " + rand + "ms");
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getDocument
specifier|private
name|Document
name|getDocument
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|getClass
argument_list|()
init|)
block|{
name|doc
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|id
operator|++
expr_stmt|;
block|}
comment|// add random stuff:
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"all"
argument_list|,
literal|"x"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

