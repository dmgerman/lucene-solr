begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|WhitespaceAnalyzer
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
name|store
operator|.
name|RAMDirectory
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
name|OpenBitSet
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TestFilteredSearch
specifier|public
class|class
name|TestFilteredSearch
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestFilteredSearch
specifier|public
name|TestFilteredSearch
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"category"
decl_stmt|;
DECL|method|testFilteredSearch
specifier|public
name|void
name|testFilteredSearch
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|boolean
name|enforceSingleSegment
init|=
literal|true
decl_stmt|;
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|int
index|[]
name|filterBits
init|=
block|{
literal|1
block|,
literal|36
block|}
decl_stmt|;
name|SimpleDocIdSetFilter
name|filter
init|=
operator|new
name|SimpleDocIdSetFilter
argument_list|(
name|filterBits
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|searchFiltered
argument_list|(
name|writer
argument_list|,
name|directory
argument_list|,
name|filter
argument_list|,
name|enforceSingleSegment
argument_list|)
expr_stmt|;
comment|// run the test on more than one segment
name|enforceSingleSegment
operator|=
literal|false
expr_stmt|;
comment|// reset - it is stateful
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
comment|// we index 60 docs - this will create 6 segments
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|searchFiltered
argument_list|(
name|writer
argument_list|,
name|directory
argument_list|,
name|filter
argument_list|,
name|enforceSingleSegment
argument_list|)
expr_stmt|;
block|}
DECL|method|searchFiltered
specifier|public
name|void
name|searchFiltered
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
try|try
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
literal|60
condition|;
name|i
operator|++
control|)
block|{
comment|//Simple docs
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
name|FIELD
argument_list|,
name|Integer
operator|.
name|toString
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
block|}
if|if
condition|(
name|optimize
condition|)
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"36"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|booleanQuery
argument_list|,
name|filter
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of matched documents"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SimpleDocIdSetFilter
specifier|public
specifier|static
specifier|final
class|class
name|SimpleDocIdSetFilter
extends|extends
name|Filter
block|{
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|int
index|[]
name|docs
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|method|SimpleDocIdSetFilter
specifier|public
name|SimpleDocIdSetFilter
parameter_list|(
name|int
index|[]
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
specifier|final
name|OpenBitSet
name|set
init|=
operator|new
name|OpenBitSet
argument_list|()
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|docBase
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|index
operator|<
name|docs
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
specifier|final
name|int
name|docId
init|=
name|docs
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|docId
operator|>
name|limit
condition|)
break|break;
name|set
operator|.
name|set
argument_list|(
name|docId
operator|-
name|docBase
argument_list|)
expr_stmt|;
block|}
name|docBase
operator|=
name|limit
expr_stmt|;
return|return
name|set
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|set
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|index
operator|=
literal|0
expr_stmt|;
name|docBase
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

