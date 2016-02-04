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
name|io
operator|.
name|EOFException
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|IOContext
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
name|MockDirectoryWrapper
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
operator|.
name|SuppressFileSystems
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

begin_comment
comment|/**  * Test that the same file name, but from a different index, is detected as foreign.  */
end_comment

begin_class
annotation|@
name|SuppressFileSystems
argument_list|(
literal|"ExtrasFS"
argument_list|)
DECL|class|TestSwappedIndexFiles
specifier|public
class|class
name|TestSwappedIndexFiles
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir1
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// otherwise we can have unref'd files left in the index that won't be visited when opening a reader and lead to scary looking false failures:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir1
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dir2
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// otherwise we can have unref'd files left in the index that won't be visited when opening a reader and lead to scary looking false failures:
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir2
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Disable CFS 80% of the time so we can truncate individual files, but the other 20% of the time we test truncation of .cfs/.cfe too:
name|boolean
name|useCFS
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|1
decl_stmt|;
comment|// Use LineFileDocs so we (hopefully) get most Lucene features
comment|// tested, e.g. IntPoint was recently added to it:
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|indexOneDoc
argument_list|(
name|seed
argument_list|,
name|dir1
argument_list|,
name|doc
argument_list|,
name|useCFS
argument_list|)
expr_stmt|;
name|indexOneDoc
argument_list|(
name|seed
argument_list|,
name|dir2
argument_list|,
name|doc
argument_list|,
name|useCFS
argument_list|)
expr_stmt|;
name|swapFiles
argument_list|(
name|dir1
argument_list|,
name|dir2
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
DECL|method|indexOneDoc
specifier|private
name|void
name|indexOneDoc
parameter_list|(
name|long
name|seed
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Document
name|doc
parameter_list|,
name|boolean
name|useCFS
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|useCFS
operator|==
literal|false
condition|)
block|{
name|conf
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|setNoCFSRatio
argument_list|(
literal|1.0
argument_list|)
expr_stmt|;
block|}
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|swapFiles
specifier|private
name|void
name|swapFiles
parameter_list|(
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|)
throws|throws
name|IOException
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
literal|"TEST: dir1 files: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dir1
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: dir2 files: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dir2
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|dir1
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|swapOneFile
argument_list|(
name|dir1
argument_list|,
name|dir2
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|swapOneFile
specifier|private
name|void
name|swapOneFile
parameter_list|(
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|String
name|victim
parameter_list|)
throws|throws
name|IOException
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
literal|"TEST: swap file "
operator|+
name|victim
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|BaseDirectoryWrapper
name|dirCopy
init|=
name|newDirectory
argument_list|()
init|)
block|{
name|dirCopy
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Copy all files from dir1 to dirCopy, except victim which we copy from dir2:
for|for
control|(
name|String
name|name
range|:
name|dir1
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|victim
argument_list|)
operator|==
literal|false
condition|)
block|{
name|dirCopy
operator|.
name|copyFrom
argument_list|(
name|dir1
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dirCopy
operator|.
name|copyFrom
argument_list|(
name|dir2
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|dirCopy
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// NOTE: we .close so that if the test fails (truncation not detected) we don't also get all these confusing errors about open files:
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dirCopy
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"wrong file "
operator|+
name|victim
operator|+
literal|" not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
decl||
name|EOFException
decl||
name|IndexFormatTooOldException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// CheckIndex should also fail:
try|try
block|{
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dirCopy
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"wrong file "
operator|+
name|victim
operator|+
literal|" not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
decl||
name|EOFException
decl||
name|IndexFormatTooOldException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
block|}
end_class

end_unit

