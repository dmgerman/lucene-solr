begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|List
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
name|LineFileDocs
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
name|Version
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
DECL|class|TestNRTCachingDirectory
specifier|public
class|class
name|TestNRTCachingDirectory
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNRTAndCommit
specifier|public
name|void
name|testNRTAndCommit
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
name|NRTCachingDirectory
name|cachedDir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|dir
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
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
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|cachedDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|w
operator|.
name|w
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|100
argument_list|,
literal|400
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
literal|"TEST: numDocs="
operator|+
name|numDocs
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|docCount
init|=
literal|0
init|;
name|docCount
operator|<
name|numDocs
condition|;
name|docCount
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"docid"
argument_list|)
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
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|17
condition|)
block|{
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|w
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|IndexReader
name|r2
init|=
name|r
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
name|r
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
block|}
block|}
name|assertEquals
argument_list|(
literal|1
operator|+
name|docCount
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
comment|// Just make sure search can run; we can't assert
comment|// totHits since it could be 0
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"the"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// System.out.println("tot hits " + hits.totalHits);
block|}
block|}
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Close should force cache to clear since all files are sync'd
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|String
index|[]
name|cachedFiles
init|=
name|cachedDir
operator|.
name|listCachedFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|cachedFiles
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: cached file "
operator|+
name|file
operator|+
literal|" remains after sync"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cachedFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|r
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
name|BytesRef
name|id
range|:
name|ids
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|docFreq
argument_list|(
literal|"docid"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|cachedDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// NOTE: not a test; just here to make sure the code frag
comment|// in the javadocs is correct!
DECL|method|verifyCompiles
specifier|public
name|void
name|verifyCompiles
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
name|Directory
name|fsDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
literal|"/path/to/index"
argument_list|)
argument_list|)
decl_stmt|;
name|NRTCachingDirectory
name|cachedFSDir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|fsDir
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_32
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|cachedFSDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
block|}
DECL|method|testDeleteFile
specifier|public
name|void
name|testDeleteFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|newDirectory
argument_list|()
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
decl_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo.txt"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"foo.txt"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dir
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3382 -- make sure we get exception if the directory really does not exist.
DECL|method|testNoDir
specifier|public
name|void
name|testNoDir
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|dir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"doesnotexist"
argument_list|)
argument_list|)
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchDirectoryException
name|nsde
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-3382 test that we can add a file, and then when we call list() we get it back
DECL|method|testDirectoryFilter
specifier|public
name|void
name|testDirectoryFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"file"
decl_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-3382 test that delegate compound files correctly.
DECL|method|testCompoundFileAppendTwice
specifier|public
name|void
name|testCompoundFileAppendTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|newDir
init|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|newDirectory
argument_list|()
argument_list|,
literal|2.0
argument_list|,
literal|25.0
argument_list|)
decl_stmt|;
name|CompoundFileDirectory
name|csw
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|createSequenceFile
argument_list|(
name|newDir
argument_list|,
literal|"d1"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|csw
operator|.
name|createOutput
argument_list|(
literal|"d.xyz"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|newDir
operator|.
name|copy
argument_list|(
name|csw
argument_list|,
literal|"d1"
argument_list|,
literal|"d1"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"file does already exist"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|csw
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|csw
operator|.
name|close
argument_list|()
expr_stmt|;
name|CompoundFileDirectory
name|cfr
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|newDir
argument_list|,
literal|"d.cfs"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d.xyz"
argument_list|,
name|cfr
operator|.
name|listAll
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cfr
operator|.
name|close
argument_list|()
expr_stmt|;
name|newDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a file of the specified size with sequential data. The first    *  byte is written as the start byte provided. All subsequent bytes are    *  computed as start + offset where offset is the number of the byte.    */
DECL|method|createSequenceFile
specifier|private
name|void
name|createSequenceFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
name|start
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|os
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|newIOContext
argument_list|(
name|random
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|writeByte
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|start
operator|++
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

