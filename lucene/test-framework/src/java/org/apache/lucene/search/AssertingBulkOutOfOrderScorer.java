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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|DocsEnum
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
name|VirtualMethod
import|;
end_import

begin_comment
comment|/** A crazy {@link BulkScorer} that wraps a {@link Scorer}  *  but shuffles the order of the collected documents. */
end_comment

begin_class
DECL|class|AssertingBulkOutOfOrderScorer
specifier|public
class|class
name|AssertingBulkOutOfOrderScorer
extends|extends
name|BulkScorer
block|{
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|scorer
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|method|AssertingBulkOutOfOrderScorer
specifier|public
name|AssertingBulkOutOfOrderScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|shuffle
specifier|private
name|void
name|shuffle
parameter_list|(
name|int
index|[]
name|docIDs
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|,
name|int
name|size
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
control|)
block|{
specifier|final
name|int
name|other
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|tmpDoc
init|=
name|docIDs
index|[
name|i
index|]
decl_stmt|;
name|docIDs
index|[
name|i
index|]
operator|=
name|docIDs
index|[
name|other
index|]
expr_stmt|;
name|docIDs
index|[
name|other
index|]
operator|=
name|tmpDoc
expr_stmt|;
specifier|final
name|float
name|tmpScore
init|=
name|scores
index|[
name|i
index|]
decl_stmt|;
name|scores
index|[
name|i
index|]
operator|=
name|scores
index|[
name|other
index|]
expr_stmt|;
name|scores
index|[
name|other
index|]
operator|=
name|tmpScore
expr_stmt|;
specifier|final
name|int
name|tmpFreq
init|=
name|freqs
index|[
name|i
index|]
decl_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|freqs
index|[
name|other
index|]
expr_stmt|;
name|freqs
index|[
name|other
index|]
operator|=
name|tmpFreq
expr_stmt|;
block|}
block|}
DECL|method|flush
specifier|private
specifier|static
name|void
name|flush
parameter_list|(
name|int
index|[]
name|docIDs
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|,
name|int
name|size
parameter_list|,
name|FakeScorer
name|scorer
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
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
name|size
condition|;
operator|++
name|i
control|)
block|{
name|scorer
operator|.
name|doc
operator|=
name|docIDs
index|[
name|i
index|]
expr_stmt|;
name|scorer
operator|.
name|freq
operator|=
name|freqs
index|[
name|i
index|]
expr_stmt|;
name|scorer
operator|.
name|score
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|scorer
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|scorer
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
name|FakeScorer
name|fake
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|fake
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bufferSize
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
name|bufferSize
index|]
decl_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|bufferSize
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|freqs
init|=
operator|new
name|int
index|[
name|bufferSize
index|]
decl_stmt|;
name|int
name|buffered
init|=
literal|0
decl_stmt|;
name|int
name|doc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|max
condition|)
block|{
name|docIDs
index|[
name|buffered
index|]
operator|=
name|doc
expr_stmt|;
name|scores
index|[
name|buffered
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|freqs
index|[
name|buffered
index|]
operator|=
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|buffered
operator|==
name|bufferSize
condition|)
block|{
name|shuffle
argument_list|(
name|docIDs
argument_list|,
name|scores
argument_list|,
name|freqs
argument_list|,
name|buffered
argument_list|)
expr_stmt|;
name|flush
argument_list|(
name|docIDs
argument_list|,
name|scores
argument_list|,
name|freqs
argument_list|,
name|buffered
argument_list|,
name|fake
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|buffered
operator|=
literal|0
expr_stmt|;
block|}
name|doc
operator|=
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
name|shuffle
argument_list|(
name|docIDs
argument_list|,
name|scores
argument_list|,
name|freqs
argument_list|,
name|buffered
argument_list|)
expr_stmt|;
name|flush
argument_list|(
name|docIDs
argument_list|,
name|scores
argument_list|,
name|freqs
argument_list|,
name|buffered
argument_list|,
name|fake
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|doc
operator|!=
name|Scorer
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AssertingBulkOutOfOrderScorer("
operator|+
name|scorer
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

