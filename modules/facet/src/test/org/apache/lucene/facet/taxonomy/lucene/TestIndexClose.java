begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
package|;
end_package

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
name|HashSet
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
name|index
operator|.
name|CorruptIndexException
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
name|FilterIndexReader
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|junit
operator|.
name|Test
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
name|analysis
operator|.
name|MockTokenizer
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This test case attempts to catch index "leaks" in LuceneTaxonomyReader/Writer,  * i.e., cases where an index has been opened, but never closed; In that case,  * Java would eventually collect this object and close the index, but leaving  * the index open might nevertheless cause problems - e.g., on Windows it prevents  * deleting it.  */
end_comment

begin_class
DECL|class|TestIndexClose
specifier|public
class|class
name|TestIndexClose
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testLeaks
specifier|public
name|void
name|testLeaks
parameter_list|()
throws|throws
name|Exception
block|{
name|LeakChecker
name|checker
init|=
operator|new
name|LeakChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|LuceneTaxonomyWriter
name|tw
init|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"animal"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneTaxonomyReader
name|tr
init|=
name|checker
operator|.
name|openReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|tr
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tr
operator|=
name|checker
operator|.
name|openReader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"animal"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"number"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"number"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|LeakChecker
specifier|private
specifier|static
class|class
name|LeakChecker
block|{
DECL|field|ireader
name|int
name|ireader
init|=
literal|0
decl_stmt|;
DECL|field|openReaders
name|Set
argument_list|<
name|Integer
argument_list|>
name|openReaders
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|iwriter
name|int
name|iwriter
init|=
literal|0
decl_stmt|;
DECL|field|openWriters
name|Set
argument_list|<
name|Integer
argument_list|>
name|openWriters
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LeakChecker
name|LeakChecker
parameter_list|()
block|{ }
DECL|method|openWriter
specifier|public
name|LuceneTaxonomyWriter
name|openWriter
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
operator|new
name|InstrumentedTaxonomyWriter
argument_list|(
name|dir
argument_list|)
return|;
block|}
DECL|method|openReader
specifier|public
name|LuceneTaxonomyReader
name|openReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
operator|new
name|InstrumentedTaxonomyReader
argument_list|(
name|dir
argument_list|)
return|;
block|}
DECL|method|nopen
specifier|public
name|int
name|nopen
parameter_list|()
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|openReaders
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"reader "
operator|+
name|i
operator|+
literal|" still open"
argument_list|)
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
range|:
name|openWriters
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"writer "
operator|+
name|i
operator|+
literal|" still open"
argument_list|)
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|class|InstrumentedTaxonomyWriter
specifier|private
class|class
name|InstrumentedTaxonomyWriter
extends|extends
name|LuceneTaxonomyWriter
block|{
DECL|method|InstrumentedTaxonomyWriter
specifier|public
name|InstrumentedTaxonomyWriter
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openReader
specifier|protected
name|IndexReader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstrumentedIndexReader
argument_list|(
name|super
operator|.
name|openReader
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openLuceneIndex
specifier|protected
name|void
name|openLuceneIndex
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|OpenMode
name|openMode
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|indexWriter
operator|=
operator|new
name|InstrumentedIndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|openMode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InstrumentedTaxonomyReader
specifier|private
class|class
name|InstrumentedTaxonomyReader
extends|extends
name|LuceneTaxonomyReader
block|{
DECL|method|InstrumentedTaxonomyReader
specifier|public
name|InstrumentedTaxonomyReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openIndexReader
specifier|protected
name|IndexReader
name|openIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
operator|new
name|InstrumentedIndexReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|InstrumentedIndexReader
specifier|private
class|class
name|InstrumentedIndexReader
extends|extends
name|FilterIndexReader
block|{
DECL|field|mynum
name|int
name|mynum
decl_stmt|;
DECL|method|InstrumentedIndexReader
specifier|public
name|InstrumentedIndexReader
parameter_list|(
name|IndexReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|mynum
operator|=
name|ireader
operator|++
expr_stmt|;
name|openReaders
operator|.
name|add
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("opened "+mynum);
block|}
annotation|@
name|Override
DECL|method|reopen
specifier|public
specifier|synchronized
name|IndexReader
name|reopen
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|n
init|=
name|in
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
name|in
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|InstrumentedIndexReader
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|// Unfortunately, IndexReader.close() is marked final so we can't
comment|// change it! Fortunately, close() calls (if the object wasn't
comment|// already closed) doClose() so we can override it to do our thing -
comment|// just like FilterIndexReader does.
annotation|@
name|Override
DECL|method|doClose
specifier|public
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|openReaders
operator|.
name|contains
argument_list|(
name|mynum
argument_list|)
condition|)
block|{
comment|// probably can't happen...
name|fail
argument_list|(
literal|"Reader #"
operator|+
name|mynum
operator|+
literal|" was closed twice!"
argument_list|)
expr_stmt|;
block|}
name|openReaders
operator|.
name|remove
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("closed "+mynum);
block|}
block|}
DECL|class|InstrumentedIndexWriter
specifier|private
class|class
name|InstrumentedIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|mynum
name|int
name|mynum
decl_stmt|;
DECL|method|InstrumentedIndexWriter
specifier|public
name|InstrumentedIndexWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|d
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|mynum
operator|=
name|iwriter
operator|++
expr_stmt|;
name|openWriters
operator|.
name|add
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("openedw "+mynum);
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|openWriters
operator|.
name|contains
argument_list|(
name|mynum
argument_list|)
condition|)
block|{
comment|// probably can't happen...
name|fail
argument_list|(
literal|"Writer #"
operator|+
name|mynum
operator|+
literal|" was closed twice!"
argument_list|)
expr_stmt|;
block|}
name|openWriters
operator|.
name|remove
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("closedw "+mynum);
block|}
block|}
block|}
block|}
end_class

end_unit

