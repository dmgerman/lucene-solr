begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
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
name|Arrays
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
name|MultiFields
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
name|MockRAMDirectory
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
DECL|class|TestEmptyIndex
specifier|public
class|class
name|TestEmptyIndex
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|TopDocs
name|td
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|testNorms
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure a Directory acts the same
name|Directory
name|d
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testNorms
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNorms
specifier|private
name|void
name|testNorms
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|norms
decl_stmt|;
name|norms
operator|=
name|r
operator|.
name|norms
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
if|if
condition|(
name|norms
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|norms
operator|.
name|length
argument_list|)
expr_stmt|;
name|norms
operator|=
operator|new
name|byte
index|[
literal|10
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|norms
argument_list|,
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
name|r
operator|.
name|norms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|norms
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testTermsEnum
specifier|public
name|void
name|testTermsEnum
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|termsEnumTest
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure a Directory acts the same
name|Directory
name|d
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|termsEnumTest
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|termsEnumTest
specifier|public
name|void
name|termsEnumTest
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|MultiFields
operator|.
name|getFields
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

