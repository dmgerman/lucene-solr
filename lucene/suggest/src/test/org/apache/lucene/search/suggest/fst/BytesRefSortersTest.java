begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package

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
name|suggest
operator|.
name|InMemorySorter
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
name|BytesRefIterator
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
name|OfflineSorter
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

begin_class
DECL|class|BytesRefSortersTest
specifier|public
class|class
name|BytesRefSortersTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testExternalRefSorter
specifier|public
name|void
name|testExternalRefSorter
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|tempDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|tempDir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|tempDir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|ExternalRefSorter
name|s
init|=
operator|new
name|ExternalRefSorter
argument_list|(
operator|new
name|OfflineSorter
argument_list|(
name|tempDir
argument_list|,
literal|"temp"
argument_list|)
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|s
argument_list|,
name|tempDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInMemorySorter
specifier|public
name|void
name|testInMemorySorter
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|InMemorySorter
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|BytesRefSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|current
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create two iterators and check that they're aligned with each other.
name|BytesRefIterator
name|i1
init|=
name|sorter
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRefIterator
name|i2
init|=
name|sorter
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// Verify sorter contract.
try|try
block|{
name|sorter
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected contract violation."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// Expected.
block|}
name|BytesRef
name|spare1
decl_stmt|;
name|BytesRef
name|spare2
decl_stmt|;
while|while
condition|(
operator|(
name|spare1
operator|=
name|i1
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
operator|(
name|spare2
operator|=
name|i2
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|spare1
argument_list|,
name|spare2
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

