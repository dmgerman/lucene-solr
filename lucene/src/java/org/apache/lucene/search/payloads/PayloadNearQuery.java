begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
operator|.
name|AtomicReaderContext
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
name|ComplexExplanation
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
name|Explanation
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
name|Scorer
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
name|IndexSearcher
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
name|Weight
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|similarities
operator|.
name|Similarity
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
name|similarities
operator|.
name|Similarity
operator|.
name|SloppyDocScorer
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
name|spans
operator|.
name|NearSpansOrdered
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
name|spans
operator|.
name|NearSpansUnordered
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanScorer
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
name|spans
operator|.
name|SpanWeight
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
name|spans
operator|.
name|Spans
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
name|ToStringUtils
import|;
end_import

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
name|Collection
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

begin_comment
comment|/**  * This class is very similar to  * {@link org.apache.lucene.search.spans.SpanNearQuery} except that it factors  * in the value of the payloads located at each of the positions where the  * {@link org.apache.lucene.search.spans.TermSpans} occurs.  *<p/>  * NOTE: In order to take advantage of this with the default scoring implementation  * ({@link DefaultSimilarity}), you must override {@link DefaultSimilarity#scorePayload(int, int, int, BytesRef)},  * which returns 1 by default.  *<p/>  * Payload scores are aggregated using a pluggable {@link PayloadFunction}.  *   * @see org.apache.lucene.search.similarities.Similarity.SloppyDocScorer#computePayloadFactor(int, int, int, BytesRef)  */
end_comment

begin_class
DECL|class|PayloadNearQuery
specifier|public
class|class
name|PayloadNearQuery
extends|extends
name|SpanNearQuery
block|{
DECL|field|fieldName
specifier|protected
name|String
name|fieldName
decl_stmt|;
DECL|field|function
specifier|protected
name|PayloadFunction
name|function
decl_stmt|;
DECL|method|PayloadNearQuery
specifier|public
name|PayloadNearQuery
parameter_list|(
name|SpanQuery
index|[]
name|clauses
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
argument_list|(
name|clauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|PayloadNearQuery
specifier|public
name|PayloadNearQuery
parameter_list|(
name|SpanQuery
index|[]
name|clauses
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|,
name|PayloadFunction
name|function
parameter_list|)
block|{
name|super
argument_list|(
name|clauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|clauses
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
expr_stmt|;
comment|// all clauses must have same field
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PayloadNearSpanWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|int
name|sz
init|=
name|clauses
operator|.
name|size
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|newClauses
init|=
operator|new
name|SpanQuery
index|[
name|sz
index|]
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|newClauses
index|[
name|i
index|]
operator|=
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|PayloadNearQuery
name|boostingNearQuery
init|=
operator|new
name|PayloadNearQuery
argument_list|(
name|newClauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|,
name|function
argument_list|)
decl_stmt|;
name|boostingNearQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|boostingNearQuery
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"payloadNear(["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SpanQuery
argument_list|>
name|i
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanQuery
name|clause
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|clause
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|fieldName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fieldName
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|function
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|function
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PayloadNearQuery
name|other
init|=
operator|(
name|PayloadNearQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|fieldName
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|function
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|function
operator|.
name|equals
argument_list|(
name|other
operator|.
name|function
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|class|PayloadNearSpanWeight
specifier|public
class|class
name|PayloadNearSpanWeight
extends|extends
name|SpanWeight
block|{
DECL|method|PayloadNearSpanWeight
specifier|public
name|PayloadNearSpanWeight
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|ScorerContext
name|scorerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PayloadNearSpanScorer
argument_list|(
name|query
operator|.
name|getSpans
argument_list|(
name|context
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|similarity
operator|.
name|sloppyDocScorer
argument_list|(
name|stats
argument_list|,
name|query
operator|.
name|getField
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|PayloadNearSpanScorer
name|scorer
init|=
operator|(
name|PayloadNearSpanScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
name|ScorerContext
operator|.
name|def
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
name|float
name|freq
init|=
name|scorer
operator|.
name|freq
argument_list|()
decl_stmt|;
name|SloppyDocScorer
name|docScorer
init|=
name|similarity
operator|.
name|sloppyDocScorer
argument_list|(
name|stats
argument_list|,
name|query
operator|.
name|getField
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|expl
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|)
expr_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
operator|new
name|Explanation
argument_list|(
name|freq
argument_list|,
literal|"phraseFreq="
operator|+
name|freq
argument_list|)
argument_list|)
decl_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|scoreExplanation
argument_list|)
expr_stmt|;
name|expl
operator|.
name|setValue
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// now the payloads part
name|Explanation
name|payloadExpl
init|=
name|function
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|scorer
operator|.
name|payloadsSeen
argument_list|,
name|scorer
operator|.
name|payloadScore
argument_list|)
decl_stmt|;
comment|// combined
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|expl
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|payloadExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|expl
operator|.
name|getValue
argument_list|()
operator|*
name|payloadExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"PayloadNearQuery, product of:"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"no matching term"
argument_list|)
return|;
block|}
block|}
DECL|class|PayloadNearSpanScorer
specifier|public
class|class
name|PayloadNearSpanScorer
extends|extends
name|SpanScorer
block|{
DECL|field|spans
name|Spans
name|spans
decl_stmt|;
DECL|field|payloadScore
specifier|protected
name|float
name|payloadScore
decl_stmt|;
DECL|field|payloadsSeen
specifier|private
name|int
name|payloadsSeen
decl_stmt|;
DECL|method|PayloadNearSpanScorer
specifier|protected
name|PayloadNearSpanScorer
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Similarity
operator|.
name|SloppyDocScorer
name|docScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spans
argument_list|,
name|weight
argument_list|,
name|docScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|spans
operator|=
name|spans
expr_stmt|;
block|}
comment|// Get the payloads associated with all underlying subspans
DECL|method|getPayloads
specifier|public
name|void
name|getPayloads
parameter_list|(
name|Spans
index|[]
name|subSpans
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
name|subSpans
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|subSpans
index|[
name|i
index|]
operator|instanceof
name|NearSpansOrdered
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NearSpansOrdered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|processPayloads
argument_list|(
operator|(
operator|(
name|NearSpansOrdered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|getPayload
argument_list|()
argument_list|,
name|subSpans
index|[
name|i
index|]
operator|.
name|start
argument_list|()
argument_list|,
name|subSpans
index|[
name|i
index|]
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getPayloads
argument_list|(
operator|(
operator|(
name|NearSpansOrdered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|getSubSpans
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subSpans
index|[
name|i
index|]
operator|instanceof
name|NearSpansUnordered
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NearSpansUnordered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|processPayloads
argument_list|(
operator|(
operator|(
name|NearSpansUnordered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|getPayload
argument_list|()
argument_list|,
name|subSpans
index|[
name|i
index|]
operator|.
name|start
argument_list|()
argument_list|,
name|subSpans
index|[
name|i
index|]
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getPayloads
argument_list|(
operator|(
operator|(
name|NearSpansUnordered
operator|)
name|subSpans
index|[
name|i
index|]
operator|)
operator|.
name|getSubSpans
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO change the whole spans api to use bytesRef, or nuke spans
DECL|field|scratch
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|/**      * By default, uses the {@link PayloadFunction} to score the payloads, but      * can be overridden to do other things.      *       * @param payLoads The payloads      * @param start The start position of the span being scored      * @param end The end position of the span being scored      *       * @see Spans      */
DECL|method|processPayloads
specifier|protected
name|void
name|processPayloads
parameter_list|(
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payLoads
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
for|for
control|(
specifier|final
name|byte
index|[]
name|thePayload
range|:
name|payLoads
control|)
block|{
name|scratch
operator|.
name|bytes
operator|=
name|thePayload
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|thePayload
operator|.
name|length
expr_stmt|;
name|payloadScore
operator|=
name|function
operator|.
name|currentScore
argument_list|(
name|doc
argument_list|,
name|fieldName
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|,
name|docScorer
operator|.
name|computePayloadFactor
argument_list|(
name|doc
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|payloadsSeen
expr_stmt|;
block|}
block|}
comment|//
annotation|@
name|Override
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
block|{
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|spans
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
literal|0.0f
expr_stmt|;
name|payloadScore
operator|=
literal|0
expr_stmt|;
name|payloadsSeen
operator|=
literal|0
expr_stmt|;
do|do
block|{
name|int
name|matchLength
init|=
name|spans
operator|.
name|end
argument_list|()
operator|-
name|spans
operator|.
name|start
argument_list|()
decl_stmt|;
name|freq
operator|+=
name|docScorer
operator|.
name|computeSlopFactor
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
name|Spans
index|[]
name|spansArr
init|=
operator|new
name|Spans
index|[
literal|1
index|]
decl_stmt|;
name|spansArr
index|[
literal|0
index|]
operator|=
name|spans
expr_stmt|;
name|getPayloads
argument_list|(
name|spansArr
argument_list|)
expr_stmt|;
name|more
operator|=
name|spans
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|more
operator|&&
operator|(
name|doc
operator|==
name|spans
operator|.
name|doc
argument_list|()
operator|)
condition|)
do|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|score
argument_list|()
operator|*
name|function
operator|.
name|docScore
argument_list|(
name|doc
argument_list|,
name|fieldName
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

