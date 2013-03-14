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
name|Collection
import|;
end_import

begin_comment
comment|/**  * Base class for Scorers that score disjunctions.  * Currently this just provides helper methods to manage the heap.  */
end_comment

begin_class
DECL|class|DisjunctionScorer
specifier|abstract
class|class
name|DisjunctionScorer
extends|extends
name|Scorer
block|{
DECL|field|subScorers
specifier|protected
specifier|final
name|Scorer
name|subScorers
index|[]
decl_stmt|;
DECL|field|numScorers
specifier|protected
name|int
name|numScorers
decl_stmt|;
DECL|method|DisjunctionScorer
specifier|protected
name|DisjunctionScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|subScorers
index|[]
parameter_list|,
name|int
name|numScorers
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|subScorers
operator|=
name|subScorers
expr_stmt|;
name|this
operator|.
name|numScorers
operator|=
name|numScorers
expr_stmt|;
name|heapify
argument_list|()
expr_stmt|;
block|}
comment|/**     * Organize subScorers into a min heap with scorers generating the earliest document on top.    */
DECL|method|heapify
specifier|protected
specifier|final
name|void
name|heapify
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
operator|(
name|numScorers
operator|>>
literal|1
operator|)
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|heapAdjust
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * The subtree of subScorers at root is a min heap except possibly for its root element.    * Bubble the root down as required to make the subtree a heap.    */
DECL|method|heapAdjust
specifier|protected
specifier|final
name|void
name|heapAdjust
parameter_list|(
name|int
name|root
parameter_list|)
block|{
name|Scorer
name|scorer
init|=
name|subScorers
index|[
name|root
index|]
decl_stmt|;
name|int
name|doc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|root
decl_stmt|;
while|while
condition|(
name|i
operator|<=
operator|(
name|numScorers
operator|>>
literal|1
operator|)
operator|-
literal|1
condition|)
block|{
name|int
name|lchild
init|=
operator|(
name|i
operator|<<
literal|1
operator|)
operator|+
literal|1
decl_stmt|;
name|Scorer
name|lscorer
init|=
name|subScorers
index|[
name|lchild
index|]
decl_stmt|;
name|int
name|ldoc
init|=
name|lscorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|int
name|rdoc
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|,
name|rchild
init|=
operator|(
name|i
operator|<<
literal|1
operator|)
operator|+
literal|2
decl_stmt|;
name|Scorer
name|rscorer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rchild
operator|<
name|numScorers
condition|)
block|{
name|rscorer
operator|=
name|subScorers
index|[
name|rchild
index|]
expr_stmt|;
name|rdoc
operator|=
name|rscorer
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ldoc
operator|<
name|doc
condition|)
block|{
if|if
condition|(
name|rdoc
operator|<
name|ldoc
condition|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
name|rscorer
expr_stmt|;
name|subScorers
index|[
name|rchild
index|]
operator|=
name|scorer
expr_stmt|;
name|i
operator|=
name|rchild
expr_stmt|;
block|}
else|else
block|{
name|subScorers
index|[
name|i
index|]
operator|=
name|lscorer
expr_stmt|;
name|subScorers
index|[
name|lchild
index|]
operator|=
name|scorer
expr_stmt|;
name|i
operator|=
name|lchild
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|rdoc
operator|<
name|doc
condition|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
name|rscorer
expr_stmt|;
name|subScorers
index|[
name|rchild
index|]
operator|=
name|scorer
expr_stmt|;
name|i
operator|=
name|rchild
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
block|}
comment|/**     * Remove the root Scorer from subScorers and re-establish it as a heap    */
DECL|method|heapRemoveRoot
specifier|protected
specifier|final
name|void
name|heapRemoveRoot
parameter_list|()
block|{
if|if
condition|(
name|numScorers
operator|==
literal|1
condition|)
block|{
name|subScorers
index|[
literal|0
index|]
operator|=
literal|null
expr_stmt|;
name|numScorers
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|subScorers
index|[
literal|0
index|]
operator|=
name|subScorers
index|[
name|numScorers
operator|-
literal|1
index|]
expr_stmt|;
name|subScorers
index|[
name|numScorers
operator|-
literal|1
index|]
operator|=
literal|null
expr_stmt|;
operator|--
name|numScorers
expr_stmt|;
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
specifier|final
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ChildScorer
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|ChildScorer
argument_list|>
argument_list|(
name|numScorers
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
name|numScorers
condition|;
name|i
operator|++
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|subScorers
index|[
name|i
index|]
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
name|long
name|sum
init|=
literal|0
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
name|numScorers
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|subScorers
index|[
name|i
index|]
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
block|}
end_class

end_unit

