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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|index
operator|.
name|AtomicReaderContext
import|;
end_import

begin_comment
comment|/**  * A {@link Rescorer} that re-sorts according to a provided  * Sort.  */
end_comment

begin_class
DECL|class|SortRescorer
specifier|public
class|class
name|SortRescorer
extends|extends
name|Rescorer
block|{
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|SortRescorer
specifier|public
name|SortRescorer
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rescore
specifier|public
name|TopDocs
name|rescore
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|firstPassTopDocs
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Copy ScoreDoc[] and sort by ascending docID:
name|ScoreDoc
index|[]
name|hits
init|=
name|firstPassTopDocs
operator|.
name|scoreDocs
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|hits
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|a
parameter_list|,
name|ScoreDoc
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|doc
operator|-
name|b
operator|.
name|doc
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|topN
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Now merge sort docIDs from hits, with reader's leaves:
name|int
name|hitUpto
init|=
literal|0
decl_stmt|;
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|endDoc
init|=
literal|0
decl_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
name|FakeScorer
name|fakeScorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
while|while
condition|(
name|hitUpto
operator|<
name|hits
operator|.
name|length
condition|)
block|{
name|ScoreDoc
name|hit
init|=
name|hits
index|[
name|hitUpto
index|]
decl_stmt|;
name|int
name|docID
init|=
name|hit
operator|.
name|doc
decl_stmt|;
name|AtomicReaderContext
name|readerContext
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|docID
operator|>=
name|endDoc
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
name|readerContext
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|endDoc
operator|=
name|readerContext
operator|.
name|docBase
operator|+
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|readerContext
operator|!=
literal|null
condition|)
block|{
comment|// We advanced to another segment:
name|collector
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|fakeScorer
argument_list|)
expr_stmt|;
name|docBase
operator|=
name|readerContext
operator|.
name|docBase
expr_stmt|;
block|}
name|fakeScorer
operator|.
name|score
operator|=
name|hit
operator|.
name|score
expr_stmt|;
name|fakeScorer
operator|.
name|doc
operator|=
name|docID
operator|-
name|docBase
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|fakeScorer
operator|.
name|doc
argument_list|)
expr_stmt|;
name|hitUpto
operator|++
expr_stmt|;
block|}
return|return
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Explanation
name|firstPassExplanation
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|oneHit
init|=
operator|new
name|TopDocs
argument_list|(
literal|1
argument_list|,
operator|new
name|ScoreDoc
index|[]
block|{
operator|new
name|ScoreDoc
argument_list|(
name|docID
argument_list|,
name|firstPassExplanation
operator|.
name|getValue
argument_list|()
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|rescore
argument_list|(
name|searcher
argument_list|,
name|oneHit
argument_list|,
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|hits
operator|.
name|totalHits
operator|==
literal|1
assert|;
comment|// TODO: if we could ask the Sort to explain itself then
comment|// we wouldn't need the separate ExpressionRescorer...
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"sort field values for sort="
operator|+
name|sort
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add first pass:
name|Explanation
name|first
init|=
operator|new
name|Explanation
argument_list|(
name|firstPassExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"first pass score"
argument_list|)
decl_stmt|;
name|first
operator|.
name|addDetail
argument_list|(
name|firstPassExplanation
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|// Add sort values:
name|SortField
index|[]
name|sortFields
init|=
name|sort
operator|.
name|getSort
argument_list|()
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"sort field "
operator|+
name|sortFields
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
operator|+
literal|" value="
operator|+
name|fieldDoc
operator|.
name|fields
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

