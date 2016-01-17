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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
DECL|class|TestParallelLeafReader
specifier|public
class|class
name|TestParallelLeafReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|parallel
DECL|field|single
specifier|private
name|IndexSearcher
name|parallel
decl_stmt|,
name|single
decl_stmt|;
DECL|field|dir
DECL|field|dir1
DECL|field|dir2
specifier|private
name|Directory
name|dir
decl_stmt|,
name|dir1
decl_stmt|,
name|dir2
decl_stmt|;
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|single
operator|=
name|single
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|parallel
operator|=
name|parallel
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|queryTest
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|single
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|single
operator|=
literal|null
expr_stmt|;
name|parallel
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|parallel
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|=
literal|null
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testFieldNames
specifier|public
name|void
name|testFieldNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
argument_list|,
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|pr
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|fieldInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRefCounts1
specifier|public
name|void
name|testRefCounts1
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReader
name|ir1
decl_stmt|,
name|ir2
decl_stmt|;
comment|// close subreaders, ParallelReader will not change refCounts, but close on its own close
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
name|ir1
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
argument_list|,
name|ir2
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// check RefCounts
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRefCounts2
specifier|public
name|void
name|testRefCounts2
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReader
name|ir1
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
decl_stmt|;
name|LeafReader
name|ir2
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
decl_stmt|;
comment|// don't close subreaders, so ParallelReader will increment refcounts
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|false
argument_list|,
name|ir1
argument_list|,
name|ir2
argument_list|)
decl_stmt|;
comment|// check RefCounts
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|ir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCloseInnerReader
specifier|public
name|void
name|testCloseInnerReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReader
name|ir1
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
decl_stmt|;
comment|// with overlapping
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|true
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|}
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|}
argument_list|)
decl_stmt|;
name|ir1
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ParallelLeafReader should be already closed because inner reader was closed!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// pass
block|}
comment|// noop:
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIncompatibleIndexes
specifier|public
name|void
name|testIncompatibleIndexes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// two documents:
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// one document only:
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
name|LeafReader
name|ir1
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
decl_stmt|;
name|LeafReader
name|ir2
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|ParallelLeafReader
argument_list|(
name|ir1
argument_list|,
name|ir2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get exptected exception: indexes don't have same number of documents"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
operator|new
name|ParallelLeafReader
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|,
name|ir2
block|}
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|,
name|ir2
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception: indexes don't have same number of documents"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
comment|// check RefCounts
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|ir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testIgnoreStoredFields
specifier|public
name|void
name|testIgnoreStoredFields
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReader
name|ir1
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
decl_stmt|;
name|LeafReader
name|ir2
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
decl_stmt|;
comment|// with overlapping
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|false
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|,
name|ir2
block|}
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that fields are there
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// no stored fields at all
name|pr
operator|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|false
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir2
block|}
argument_list|,
operator|new
name|LeafReader
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that fields are there
name|assertNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// without overlapping
name|pr
operator|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|true
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir2
block|}
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|document
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that fields are there
name|assertNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
operator|.
name|terms
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// no main readers
try|try
block|{
operator|new
name|ParallelLeafReader
argument_list|(
literal|true
argument_list|,
operator|new
name|LeafReader
index|[
literal|0
index|]
argument_list|,
operator|new
name|LeafReader
index|[]
block|{
name|ir1
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception: need a non-empty main-reader array"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|queryTest
specifier|private
name|void
name|queryTest
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|parallelHits
init|=
name|parallel
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|singleHits
init|=
name|single
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|parallelHits
operator|.
name|length
argument_list|,
name|singleHits
operator|.
name|length
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
name|parallelHits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|parallelHits
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|singleHits
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.001f
argument_list|)
expr_stmt|;
name|Document
name|docParallel
init|=
name|parallel
operator|.
name|doc
argument_list|(
name|parallelHits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Document
name|docSingle
init|=
name|single
operator|.
name|doc
argument_list|(
name|singleHits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docParallel
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fields 1-4 indexed together:
DECL|method|single
specifier|private
name|IndexSearcher
name|single
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
return|return
name|newSearcher
argument_list|(
name|ir
argument_list|)
return|;
block|}
comment|// Fields 1& 2 in one index, 3& 4 in other, with ParallelReader:
DECL|method|parallel
specifier|private
name|IndexSearcher
name|parallel
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir1
operator|=
name|getDir1
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|dir2
operator|=
name|getDir2
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
argument_list|)
argument_list|,
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|pr
argument_list|)
expr_stmt|;
return|return
name|newSearcher
argument_list|(
name|pr
argument_list|)
return|;
block|}
DECL|method|getDir1
specifier|private
name|Directory
name|getDir1
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w1
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w1
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir1
return|;
block|}
DECL|method|getDir2
specifier|private
name|Directory
name|getDir2
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|Document
name|d4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d4
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir2
return|;
block|}
block|}
end_class

end_unit

