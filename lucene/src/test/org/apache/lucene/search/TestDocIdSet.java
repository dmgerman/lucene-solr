begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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

begin_class
DECL|class|TestDocIdSet
specifier|public
class|class
name|TestDocIdSet
extends|extends
name|LuceneTestCase
block|{
DECL|method|testFilteredDocIdSet
specifier|public
name|void
name|testFilteredDocIdSet
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxdoc
init|=
literal|10
decl_stmt|;
specifier|final
name|DocIdSet
name|innerSet
init|=
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|docid
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docid
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|docid
operator|++
expr_stmt|;
return|return
name|docid
operator|<
name|maxdoc
condition|?
name|docid
else|:
operator|(
name|docid
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|nextDoc
argument_list|()
operator|<
name|target
condition|)
block|{}
return|return
name|docid
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|DocIdSet
name|filteredSet
init|=
operator|new
name|FilteredDocIdSet
argument_list|(
name|innerSet
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
return|return
name|docid
operator|%
literal|2
operator|==
literal|0
return|;
comment|//validate only even docids
block|}
block|}
decl_stmt|;
name|DocIdSetIterator
name|iter
init|=
name|filteredSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
name|iter
operator|.
name|advance
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|intIter
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|intIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|docs
index|[
name|c
operator|++
index|]
operator|=
name|intIter
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|int
index|[]
name|answer
init|=
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|6
block|,
literal|8
block|}
decl_stmt|;
name|boolean
name|same
init|=
name|Arrays
operator|.
name|equals
argument_list|(
name|answer
argument_list|,
name|docs
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|same
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"answer: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|answer
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"gotten: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|docs
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testNullDocIdSet
specifier|public
name|void
name|testNullDocIdSet
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that if a Filter produces a null DocIdSet, which is given to
comment|// IndexSearcher, everything works fine. This came up in LUCENE-1754.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
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
literal|"c"
argument_list|,
literal|"val"
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// First verify the document is searchable.
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Now search w/ a Filter which returns a null DocIdSet
name|Filter
name|f
init|=
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|f
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
block|}
end_class

end_unit

