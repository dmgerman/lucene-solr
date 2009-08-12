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
name|Searcher
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
comment|/**  * The BoostingNearQuery is very similar to the {@link org.apache.lucene.search.spans.SpanNearQuery} except  * that it factors in the value of the payloads located at each of the positions where the  * {@link org.apache.lucene.search.spans.TermSpans} occurs.  *<p/>  * In order to take advantage of this, you must override {@link org.apache.lucene.search.Similarity#scorePayload(String, byte[],int,int)}  * which returns 1 by default.  *<p/>  * Payload scores are averaged across term occurrences in the document.  *  * @see org.apache.lucene.search.Similarity#scorePayload(String, byte[], int, int)  */
end_comment

begin_class
DECL|class|BoostingNearQuery
specifier|public
class|class
name|BoostingNearQuery
extends|extends
name|SpanNearQuery
implements|implements
name|PayloadQuery
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
DECL|method|BoostingNearQuery
specifier|public
name|BoostingNearQuery
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
DECL|method|BoostingNearQuery
specifier|public
name|BoostingNearQuery
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
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingSpanWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
DECL|class|BoostingSpanWeight
specifier|public
class|class
name|BoostingSpanWeight
extends|extends
name|SpanWeight
block|{
DECL|method|BoostingSpanWeight
specifier|public
name|BoostingSpanWeight
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|Searcher
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
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingSpanScorer
argument_list|(
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingSpanScorer
argument_list|(
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|BoostingSpanScorer
specifier|public
class|class
name|BoostingSpanScorer
extends|extends
name|SpanScorer
block|{
DECL|field|spans
name|Spans
name|spans
decl_stmt|;
DECL|field|subSpans
name|Spans
index|[]
name|subSpans
init|=
literal|null
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
DECL|field|similarity
name|Similarity
name|similarity
init|=
name|getSimilarity
argument_list|()
decl_stmt|;
DECL|method|BoostingSpanScorer
specifier|protected
name|BoostingSpanScorer
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
name|byte
index|[]
name|norms
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
name|similarity
argument_list|,
name|norms
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
comment|/**      * By default, sums the payloads, but can be overridden to do other things.      *      * @param payLoads The payloads      */
DECL|method|processPayloads
specifier|protected
name|void
name|processPayloads
parameter_list|(
name|Collection
name|payLoads
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iterator
init|=
name|payLoads
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|byte
index|[]
name|thePayload
init|=
operator|(
name|byte
index|[]
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|,
name|similarity
operator|.
name|scorePayload
argument_list|(
name|doc
argument_list|,
name|fieldName
argument_list|,
name|thePayload
argument_list|,
literal|0
argument_list|,
name|thePayload
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|payloadsSeen
expr_stmt|;
block|}
block|}
comment|//
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
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
name|payloadScore
operator|=
literal|0
expr_stmt|;
name|payloadsSeen
operator|=
literal|0
expr_stmt|;
name|getPayloads
argument_list|(
name|spansArr
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|setFreqCurrentDoc
argument_list|()
return|;
block|}
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
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|Explanation
name|nonPayloadExpl
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|nonPayloadExpl
argument_list|)
expr_stmt|;
name|Explanation
name|payloadBoost
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|payloadBoost
argument_list|)
expr_stmt|;
name|float
name|avgPayloadScore
init|=
operator|(
name|payloadsSeen
operator|>
literal|0
condition|?
operator|(
name|payloadScore
operator|/
name|payloadsSeen
operator|)
else|:
literal|1
operator|)
decl_stmt|;
name|payloadBoost
operator|.
name|setValue
argument_list|(
name|avgPayloadScore
argument_list|)
expr_stmt|;
name|payloadBoost
operator|.
name|setDescription
argument_list|(
literal|"scorePayload(...)"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|nonPayloadExpl
operator|.
name|getValue
argument_list|()
operator|*
name|avgPayloadScore
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"bnq, product of:"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

