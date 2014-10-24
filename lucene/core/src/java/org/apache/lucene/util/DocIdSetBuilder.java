begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
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
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/**  * A builder of {@link DocIdSet}s that supports random access.  * @lucene.internal  */
end_comment

begin_class
DECL|class|DocIdSetBuilder
specifier|public
specifier|final
class|class
name|DocIdSetBuilder
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|threshold
specifier|private
specifier|final
name|int
name|threshold
decl_stmt|;
DECL|field|sparseSet
specifier|private
name|SparseFixedBitSet
name|sparseSet
decl_stmt|;
DECL|field|denseSet
specifier|private
name|FixedBitSet
name|denseSet
decl_stmt|;
comment|// we cache an upper bound of the cost of this builder so that we don't have
comment|// to re-compute approximateCardinality on the sparse set every time
DECL|field|costUpperBound
specifier|private
name|long
name|costUpperBound
decl_stmt|;
comment|/** Create a new instance that can hold<code>maxDoc</code> documents and is optionally<code>full</code>. */
DECL|method|DocIdSetBuilder
specifier|public
name|DocIdSetBuilder
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|boolean
name|full
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|threshold
operator|=
name|maxDoc
operator|>>>
literal|10
expr_stmt|;
if|if
condition|(
name|full
condition|)
block|{
name|denseSet
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|denseSet
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Create a new empty instance. */
DECL|method|DocIdSetBuilder
specifier|public
name|DocIdSetBuilder
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
argument_list|(
name|maxDoc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the content of the provided {@link DocIdSetIterator} to this builder.    */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|denseSet
operator|!=
literal|null
condition|)
block|{
comment|// already upgraded
name|denseSet
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|long
name|itCost
init|=
name|it
operator|.
name|cost
argument_list|()
decl_stmt|;
name|costUpperBound
operator|+=
name|itCost
expr_stmt|;
if|if
condition|(
name|costUpperBound
operator|>=
name|threshold
condition|)
block|{
name|costUpperBound
operator|=
operator|(
name|sparseSet
operator|==
literal|null
condition|?
literal|0
else|:
name|sparseSet
operator|.
name|approximateCardinality
argument_list|()
operator|)
operator|+
name|itCost
expr_stmt|;
if|if
condition|(
name|costUpperBound
operator|>=
name|threshold
condition|)
block|{
comment|// upgrade
name|denseSet
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|denseSet
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
if|if
condition|(
name|sparseSet
operator|!=
literal|null
condition|)
block|{
name|denseSet
operator|.
name|or
argument_list|(
operator|new
name|BitSetIterator
argument_list|(
name|sparseSet
argument_list|,
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
comment|// we are still sparse
if|if
condition|(
name|sparseSet
operator|==
literal|null
condition|)
block|{
name|sparseSet
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
name|sparseSet
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes from this builder documents that are not contained in<code>it</code>.    */
DECL|method|and
specifier|public
name|void
name|and
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|denseSet
operator|!=
literal|null
condition|)
block|{
name|denseSet
operator|.
name|and
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sparseSet
operator|!=
literal|null
condition|)
block|{
name|sparseSet
operator|.
name|and
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Removes from this builder documents that are contained in<code>it</code>.    */
DECL|method|andNot
specifier|public
name|void
name|andNot
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|denseSet
operator|!=
literal|null
condition|)
block|{
name|denseSet
operator|.
name|andNot
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|denseSet
operator|!=
literal|null
condition|)
block|{
name|denseSet
operator|.
name|andNot
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Build a {@link DocIdSet} that contains all doc ids that have been added.    * This method may return<tt>null</tt> if no documents were addded to this    * builder.    * NOTE: this is a destructive operation, the builder should not be used    * anymore after this method has been called.    */
DECL|method|build
specifier|public
name|DocIdSet
name|build
parameter_list|()
block|{
specifier|final
name|DocIdSet
name|result
decl_stmt|;
if|if
condition|(
name|denseSet
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|BitDocIdSet
argument_list|(
name|denseSet
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sparseSet
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|BitDocIdSet
argument_list|(
name|sparseSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
name|denseSet
operator|=
literal|null
expr_stmt|;
name|sparseSet
operator|=
literal|null
expr_stmt|;
name|costUpperBound
operator|=
literal|0
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

