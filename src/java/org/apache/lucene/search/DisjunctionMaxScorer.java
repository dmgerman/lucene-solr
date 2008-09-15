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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * The Scorer for DisjunctionMaxQuery's.  The union of all documents generated by the the subquery scorers  * is generated in document number order.  The score for each document is the maximum of the scores computed  * by the subquery scorers that generate that document, plus tieBreakerMultiplier times the sum of the scores  * for the other subqueries that generate the document.  */
end_comment

begin_class
DECL|class|DisjunctionMaxScorer
class|class
name|DisjunctionMaxScorer
extends|extends
name|Scorer
block|{
comment|/* The scorers for subqueries that have remaining docs, kept as a min heap by number of next doc. */
DECL|field|subScorers
specifier|private
name|ArrayList
name|subScorers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/* Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result. */
DECL|field|tieBreakerMultiplier
specifier|private
name|float
name|tieBreakerMultiplier
decl_stmt|;
DECL|field|more
specifier|private
name|boolean
name|more
init|=
literal|false
decl_stmt|;
comment|// True iff there is a next document
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
comment|// True iff next() has not yet been called
comment|/** Creates a new instance of DisjunctionMaxScorer      * @param tieBreakerMultiplier Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result.      * @param similarity -- not used since our definition involves neither coord nor terms directly */
DECL|method|DisjunctionMaxScorer
specifier|public
name|DisjunctionMaxScorer
parameter_list|(
name|float
name|tieBreakerMultiplier
parameter_list|,
name|Similarity
name|similarity
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
block|}
comment|/** Add the scorer for a subquery      * @param scorer the scorer of a subquery of our associated DisjunctionMaxQuery      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|scorer
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// Initialize and retain only if it produces docs
name|subScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|more
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Generate the next document matching our associated DisjunctionMaxQuery.      * @return true iff there is a next document      */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|firstTime
condition|)
block|{
name|heapify
argument_list|()
expr_stmt|;
name|firstTime
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
comment|// more would have been false if no subScorers had any docs
block|}
comment|// Increment all generators that generated the last doc and adjust the heap.
name|int
name|lastdoc
init|=
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
decl_stmt|;
do|do
block|{
if|if
condition|(
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|next
argument_list|()
condition|)
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
block|{
name|heapRemoveRoot
argument_list|()
expr_stmt|;
if|if
condition|(
name|subScorers
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
operator|(
name|more
operator|=
literal|false
operator|)
return|;
block|}
block|}
do|while
condition|(
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
operator|==
name|lastdoc
condition|)
do|;
return|return
literal|true
return|;
block|}
comment|/** Determine the current document number.  Initially invalid, until {@link #next()} is called the first time.      * @return the document number of the currently generated document      */
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
comment|/** Determine the current document score.  Initially invalid, until {@link #next()} is called the first time.      * @return the score of the current generated document      */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
decl_stmt|;
name|float
index|[]
name|sum
init|=
block|{
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|score
argument_list|()
block|}
decl_stmt|,
name|max
init|=
block|{
name|sum
index|[
literal|0
index|]
block|}
decl_stmt|;
name|int
name|size
init|=
name|subScorers
operator|.
name|size
argument_list|()
decl_stmt|;
name|scoreAll
argument_list|(
literal|1
argument_list|,
name|size
argument_list|,
name|doc
argument_list|,
name|sum
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|scoreAll
argument_list|(
literal|2
argument_list|,
name|size
argument_list|,
name|doc
argument_list|,
name|sum
argument_list|,
name|max
argument_list|)
expr_stmt|;
return|return
name|max
index|[
literal|0
index|]
operator|+
operator|(
name|sum
index|[
literal|0
index|]
operator|-
name|max
index|[
literal|0
index|]
operator|)
operator|*
name|tieBreakerMultiplier
return|;
block|}
comment|// Recursively iterate all subScorers that generated last doc computing sum and max
DECL|method|scoreAll
specifier|private
name|void
name|scoreAll
parameter_list|(
name|int
name|root
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
index|[]
name|sum
parameter_list|,
name|float
index|[]
name|max
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|root
operator|<
name|size
operator|&&
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
name|root
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
operator|==
name|doc
condition|)
block|{
name|float
name|sub
init|=
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
name|root
argument_list|)
operator|)
operator|.
name|score
argument_list|()
decl_stmt|;
name|sum
index|[
literal|0
index|]
operator|+=
name|sub
expr_stmt|;
name|max
index|[
literal|0
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
index|[
literal|0
index|]
argument_list|,
name|sub
argument_list|)
expr_stmt|;
name|scoreAll
argument_list|(
operator|(
name|root
operator|<<
literal|1
operator|)
operator|+
literal|1
argument_list|,
name|size
argument_list|,
name|doc
argument_list|,
name|sum
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|scoreAll
argument_list|(
operator|(
name|root
operator|<<
literal|1
operator|)
operator|+
literal|2
argument_list|,
name|size
argument_list|,
name|doc
argument_list|,
name|sum
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Advance to the first document beyond the current whose number is greater than or equal to target.      * @param target the minimum number of the next desired document      * @return true iff there is a document to be generated whose number is at least target      */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
if|if
condition|(
operator|!
name|more
condition|)
return|return
literal|false
return|;
name|heapify
argument_list|()
expr_stmt|;
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
while|while
condition|(
name|subScorers
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
operator|<
name|target
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
name|heapRemoveRoot
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|subScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
return|return
operator|(
name|more
operator|=
literal|false
operator|)
return|;
return|return
literal|true
return|;
block|}
comment|/** Explain a score that we computed.  UNSUPPORTED -- see explanation capability in DisjunctionMaxQuery.      * @param doc the number of a document we scored      * @return the Explanation for our score      */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// Organize subScorers into a min heap with scorers generating the earlest document on top.
DECL|method|heapify
specifier|private
name|void
name|heapify
parameter_list|()
block|{
name|int
name|size
init|=
name|subScorers
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|(
name|size
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
name|heapAdjust
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* The subtree of subScorers at root is a min heap except possibly for its root element.      * Bubble the root down as required to make the subtree a heap.      */
DECL|method|heapAdjust
specifier|private
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
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|int
name|doc
init|=
name|scorer
operator|.
name|doc
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|root
decl_stmt|,
name|size
init|=
name|subScorers
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<=
operator|(
name|size
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
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
name|lchild
argument_list|)
decl_stmt|;
name|int
name|ldoc
init|=
name|lscorer
operator|.
name|doc
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
name|size
condition|)
block|{
name|rscorer
operator|=
operator|(
name|Scorer
operator|)
name|subScorers
operator|.
name|get
argument_list|(
name|rchild
argument_list|)
expr_stmt|;
name|rdoc
operator|=
name|rscorer
operator|.
name|doc
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
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|rscorer
argument_list|)
expr_stmt|;
name|subScorers
operator|.
name|set
argument_list|(
name|rchild
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
name|i
operator|=
name|rchild
expr_stmt|;
block|}
else|else
block|{
name|subScorers
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|lscorer
argument_list|)
expr_stmt|;
name|subScorers
operator|.
name|set
argument_list|(
name|lchild
argument_list|,
name|scorer
argument_list|)
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
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|rscorer
argument_list|)
expr_stmt|;
name|subScorers
operator|.
name|set
argument_list|(
name|rchild
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
name|i
operator|=
name|rchild
expr_stmt|;
block|}
else|else
return|return;
block|}
block|}
comment|// Remove the root Scorer from subScorers and re-establish it as a heap
DECL|method|heapRemoveRoot
specifier|private
name|void
name|heapRemoveRoot
parameter_list|()
block|{
name|int
name|size
init|=
name|subScorers
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|1
condition|)
name|subScorers
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
block|{
name|subScorers
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|subScorers
operator|.
name|get
argument_list|(
name|size
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|subScorers
operator|.
name|remove
argument_list|(
name|size
operator|-
literal|1
argument_list|)
expr_stmt|;
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

