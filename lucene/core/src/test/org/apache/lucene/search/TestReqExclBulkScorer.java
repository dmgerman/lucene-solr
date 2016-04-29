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
name|Bits
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
name|DocIdSetBuilder
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
name|FixedBitSet
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
DECL|class|TestReqExclBulkScorer
specifier|public
class|class
name|TestReqExclBulkScorer
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
name|doTestRandom
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTestRandom
specifier|public
name|void
name|doTestRandom
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|reqBuilder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|exclBuilder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIncludedDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numExcludedDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|reqAdder
init|=
name|reqBuilder
operator|.
name|grow
argument_list|(
name|numIncludedDocs
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
name|numIncludedDocs
condition|;
operator|++
name|i
control|)
block|{
name|reqAdder
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|exclAdder
init|=
name|exclBuilder
operator|.
name|grow
argument_list|(
name|numExcludedDocs
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
name|numExcludedDocs
condition|;
operator|++
name|i
control|)
block|{
name|exclAdder
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocIdSet
name|req
init|=
name|reqBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSet
name|excl
init|=
name|exclBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|BulkScorer
name|reqBulkScorer
init|=
operator|new
name|BulkScorer
argument_list|()
block|{
specifier|final
name|DocIdSetIterator
name|iterator
init|=
name|req
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|iterator
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|docID
argument_list|()
operator|<
name|min
condition|)
block|{
name|doc
operator|=
name|iterator
operator|.
name|advance
argument_list|(
name|min
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|doc
operator|<
name|max
condition|)
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|ReqExclBulkScorer
name|reqExcl
init|=
operator|new
name|ReqExclBulkScorer
argument_list|(
name|reqBulkScorer
argument_list|,
name|excl
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|actualMatches
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|reqExcl
operator|.
name|score
argument_list|(
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|actualMatches
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|next
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|next
operator|<
name|maxDoc
condition|)
block|{
specifier|final
name|int
name|min
init|=
name|next
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|min
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|next
operator|=
name|reqExcl
operator|.
name|score
argument_list|(
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|actualMatches
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|null
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|next
operator|>=
name|max
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|FixedBitSet
name|expectedMatches
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|expectedMatches
operator|.
name|or
argument_list|(
name|req
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|FixedBitSet
name|excludedSet
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|excludedSet
operator|.
name|or
argument_list|(
name|excl
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|expectedMatches
operator|.
name|andNot
argument_list|(
name|excludedSet
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedMatches
operator|.
name|getBits
argument_list|()
argument_list|,
name|actualMatches
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

