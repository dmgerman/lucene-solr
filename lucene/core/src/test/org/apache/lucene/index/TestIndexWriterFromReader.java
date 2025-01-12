begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|store
operator|.
name|AlreadyClosedException
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
name|IOUtils
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
DECL|class|TestIndexWriterFromReader
specifier|public
class|class
name|TestIndexWriterFromReader
extends|extends
name|LuceneTestCase
block|{
comment|// Pull NRT reader immediately after writer has committed
DECL|method|testRightAfterCommit
specifier|public
name|void
name|testRightAfterCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|r2
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
comment|// Open from non-NRT reader
DECL|method|testFromNonNRTReader
specifier|public
name|void
name|testFromNonNRTReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|r2
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
comment|// Pull NRT reader from a writer on a new index with no commit:
DECL|method|testWithNoFirstCommit
specifier|public
name|void
name|testWithNoFirstCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cannot use IndexWriterConfig.setIndexCommit() when index has no commit"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|r
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
comment|// Pull NRT reader after writer has committed and then indexed another doc:
DECL|method|testAfterCommitThenIndex
specifier|public
name|void
name|testAfterCommitThenIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"the provided reader is stale: its prior commit file"
argument_list|)
argument_list|)
expr_stmt|;
name|r
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
comment|// NRT rollback: pull NRT reader after writer has committed and then before indexing another doc
DECL|method|testNRTRollback
specifier|public
name|void
name|testNRTRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add another doc
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"the provided reader is stale: its prior commit file"
argument_list|)
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|int
name|numOps
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
comment|// We must have a starting commit for this test because whenever we rollback with
comment|// an NRT reader, the commit before that NRT reader must exist
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|int
name|nrtReaderNumDocs
init|=
literal|0
decl_stmt|;
name|int
name|writerNumDocs
init|=
literal|0
decl_stmt|;
name|boolean
name|commitAfterNRT
init|=
literal|false
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|liveIDs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|nrtLiveIDs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|op
init|=
literal|0
init|;
name|op
operator|<
name|numOps
condition|;
name|op
operator|++
control|)
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
literal|"\nITER op="
operator|+
name|op
operator|+
literal|" nrtReaderNumDocs="
operator|+
name|nrtReaderNumDocs
operator|+
literal|" writerNumDocs="
operator|+
name|writerNumDocs
operator|+
literal|" r="
operator|+
name|r
operator|+
literal|" r.numDocs()="
operator|+
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|nrtReaderNumDocs
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|x
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|x
condition|)
block|{
case|case
literal|0
case|:
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
literal|"  add doc id="
operator|+
name|op
argument_list|)
expr_stmt|;
block|}
comment|// add doc
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|op
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|liveIDs
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|writerNumDocs
operator|++
expr_stmt|;
break|break;
case|case
literal|1
case|:
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
literal|"  delete doc"
argument_list|)
expr_stmt|;
block|}
comment|// delete docs
if|if
condition|(
name|liveIDs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|id
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|op
argument_list|)
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
literal|"    id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveIDs
operator|.
name|remove
argument_list|(
name|id
argument_list|)
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
literal|"    really deleted"
argument_list|)
expr_stmt|;
block|}
name|writerNumDocs
operator|--
expr_stmt|;
block|}
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
literal|"    nothing to delete yet"
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|2
case|:
comment|// reopen NRT reader
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
literal|"  reopen NRT reader"
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|r2
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
literal|"    got new reader oldNumDocs="
operator|+
name|nrtReaderNumDocs
operator|+
literal|" newNumDocs="
operator|+
name|writerNumDocs
argument_list|)
expr_stmt|;
block|}
name|nrtReaderNumDocs
operator|=
name|writerNumDocs
expr_stmt|;
name|nrtLiveIDs
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|liveIDs
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
literal|"    reader is unchanged"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|nrtReaderNumDocs
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|commitAfterNRT
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|3
case|:
if|if
condition|(
name|commitAfterNRT
operator|==
literal|false
condition|)
block|{
comment|// rollback writer to last nrt reader
if|if
condition|(
name|random
argument_list|()
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
literal|"  close writer and open new writer from non-NRT reader numDocs="
operator|+
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writerNumDocs
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|nrtReaderNumDocs
operator|=
name|writerNumDocs
expr_stmt|;
name|nrtLiveIDs
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|liveIDs
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
literal|"  rollback writer and open new writer from NRT reader numDocs="
operator|+
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|writerNumDocs
operator|=
name|nrtReaderNumDocs
expr_stmt|;
name|liveIDs
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nrtLiveIDs
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|4
case|:
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
literal|"    commit"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|commitAfterNRT
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|w
argument_list|,
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testConsistentFieldNumbers
specifier|public
name|void
name|testConsistentFieldNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
comment|// Empty first commit:
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|newStringField
argument_list|(
literal|"f0"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"f1"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r2
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"f1"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"f0"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w2
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
DECL|method|testInvalidOpenMode
specifier|public
name|void
name|testInvalidOpenMode
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cannot use IndexWriterConfig.setIndexCommit() with OpenMode.CREATE"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnClosedReader
specifier|public
name|void
name|testOnClosedReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|IndexCommit
name|commit
init|=
name|r
operator|.
name|getIndexCommit
argument_list|()
decl_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|AlreadyClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaleNRTReader
specifier|public
name|void
name|testStaleNRTReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r3
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r3
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r4
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r3
argument_list|)
decl_stmt|;
name|r3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r4
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r4
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testAfterRollback
specifier|public
name|void
name|testAfterRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r2
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// Pull NRT reader after writer has committed and then indexed another doc:
DECL|method|testAfterCommitThenIndexKeepCommits
specifier|public
name|void
name|testAfterCommitThenIndexKeepCommits
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
comment|// Keep all commits:
name|iwc
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|IndexDeletionPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{         }
block|}
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|r2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r2
argument_list|,
name|w
argument_list|)
expr_stmt|;
comment|// r is not stale because, even though we've committed the original writer since it was open, we are keeping all commit points:
name|iwc
operator|=
name|newIndexWriterConfig
argument_list|()
expr_stmt|;
name|iwc
operator|.
name|setIndexCommit
argument_list|(
name|r
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|w2
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

