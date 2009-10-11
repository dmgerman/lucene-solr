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

begin_comment
comment|/**  * The Scorer for DisjunctionMaxQuery.  The union of all documents generated by the the subquery scorers  * is generated in document number order.  The score for each document is the maximum of the scores computed  * by the subquery scorers that generate that document, plus tieBreakerMultiplier times the sum of the scores  * for the other subqueries that generate the document.  */
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
specifier|final
name|Scorer
index|[]
name|subScorers
decl_stmt|;
DECL|field|numScorers
specifier|private
name|int
name|numScorers
decl_stmt|;
comment|/* Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result. */
DECL|field|tieBreakerMultiplier
specifier|private
specifier|final
name|float
name|tieBreakerMultiplier
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Creates a new instance of DisjunctionMaxScorer    *     * @param tieBreakerMultiplier    *          Multiplier applied to non-maximum-scoring subqueries for a    *          document as they are summed into the result.    * @param similarity    *          -- not used since our definition involves neither coord nor terms    *          directly    * @param subScorers    *          The sub scorers this Scorer should iterate on    * @param numScorers    *          The actual number of scorers to iterate on. Note that the array's    *          length may be larger than the actual number of scorers.    */
DECL|method|DisjunctionMaxScorer
specifier|public
name|DisjunctionMaxScorer
parameter_list|(
name|float
name|tieBreakerMultiplier
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Scorer
index|[]
name|subScorers
parameter_list|,
name|int
name|numScorers
parameter_list|)
throws|throws
name|IOException
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
comment|// The passed subScorers array includes only scorers which have documents
comment|// (DisjunctionMaxQuery takes care of that), and their nextDoc() was already
comment|// called.
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
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|numScorers
operator|==
literal|0
condition|)
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
while|while
condition|(
name|subScorers
index|[
literal|0
index|]
operator|.
name|docID
argument_list|()
operator|==
name|doc
condition|)
block|{
if|if
condition|(
name|subScorers
index|[
literal|0
index|]
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|heapRemoveRoot
argument_list|()
expr_stmt|;
if|if
condition|(
name|numScorers
operator|==
literal|0
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
block|}
return|return
name|doc
operator|=
name|subScorers
index|[
literal|0
index|]
operator|.
name|docID
argument_list|()
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/** Determine the current document score.  Initially invalid, until {@link #next()} is called the first time.    * @return the score of the current generated document    */
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
name|subScorers
index|[
literal|0
index|]
operator|.
name|docID
argument_list|()
decl_stmt|;
name|float
index|[]
name|sum
init|=
block|{
name|subScorers
index|[
literal|0
index|]
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
name|numScorers
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
name|subScorers
index|[
name|root
index|]
operator|.
name|docID
argument_list|()
operator|==
name|doc
condition|)
block|{
name|float
name|sub
init|=
name|subScorers
index|[
name|root
index|]
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
DECL|method|advance
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
if|if
condition|(
name|numScorers
operator|==
literal|0
condition|)
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
while|while
condition|(
name|subScorers
index|[
literal|0
index|]
operator|.
name|docID
argument_list|()
operator|<
name|target
condition|)
block|{
if|if
condition|(
name|subScorers
index|[
literal|0
index|]
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|heapAdjust
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|heapRemoveRoot
argument_list|()
expr_stmt|;
if|if
condition|(
name|numScorers
operator|==
literal|0
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
block|}
return|return
name|doc
operator|=
name|subScorers
index|[
literal|0
index|]
operator|.
name|docID
argument_list|()
return|;
block|}
comment|/** Explain a score that we computed.  UNSUPPORTED -- see explanation capability in DisjunctionMaxQuery.    * @param doc the number of a document we scored    * @return the Explanation for our score    */
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
comment|// Organize subScorers into a min heap with scorers generating the earliest document on top.
DECL|method|heapify
specifier|private
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
comment|/* The subtree of subScorers at root is a min heap except possibly for its root element.    * Bubble the root down as required to make the subtree a heap.    */
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
comment|// Remove the root Scorer from subScorers and re-establish it as a heap
DECL|method|heapRemoveRoot
specifier|private
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
block|}
end_class

end_unit

