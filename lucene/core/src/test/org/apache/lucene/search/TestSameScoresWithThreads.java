begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestSameScoresWithThreads
specifier|public
class|class
name|TestSameScoresWithThreads
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
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|analyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
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
name|int
name|charsToIndex
init|=
name|atLeast
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|int
name|charsIndexed
init|=
literal|0
decl_stmt|;
comment|//System.out.println("bytesToIndex=" + charsToIndex);
while|while
condition|(
name|charsIndexed
operator|<
name|charsToIndex
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|charsIndexed
operator|+=
name|doc
operator|.
name|get
argument_list|(
literal|"body"
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//System.out.println("  bytes=" + charsIndexed + " add: " + doc);
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|//System.out.println("numDocs=" + r.numDocs());
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|r
argument_list|)
operator|.
name|terms
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
name|int
name|termCount
init|=
literal|0
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|termCount
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|termCount
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Target ~10 terms to search:
name|double
name|chance
init|=
literal|10.0
operator|/
name|termCount
decl_stmt|;
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|TopDocs
argument_list|>
name|answers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|<=
name|chance
condition|)
block|{
name|BytesRef
name|term
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|answers
operator|.
name|put
argument_list|(
name|term
argument_list|,
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
name|term
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|answers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|numThreads
condition|;
name|threadID
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|TopDocs
argument_list|>
argument_list|>
name|shuffled
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|answers
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffled
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|TopDocs
argument_list|>
name|ent
range|:
name|shuffled
control|)
block|{
name|TopDocs
name|actual
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
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|TopDocs
name|expected
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|totalHits
argument_list|,
name|actual
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query="
operator|+
name|ent
operator|.
name|getKey
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|expected
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
name|actual
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|hit
init|=
literal|0
init|;
name|hit
operator|<
name|expected
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hit
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|doc
argument_list|,
name|actual
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
comment|// Floats really should be identical:
name|assertTrue
argument_list|(
name|expected
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|score
operator|==
name|actual
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
decl_stmt|;
name|threads
index|[
name|threadID
index|]
operator|=
name|thread
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit

