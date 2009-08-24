begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|search
operator|.
name|*
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Expert-only.  Public for use by other weight implementations  */
end_comment

begin_class
DECL|class|SpanWeight
specifier|public
class|class
name|SpanWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|protected
name|Similarity
name|similarity
decl_stmt|;
DECL|field|value
specifier|protected
name|float
name|value
decl_stmt|;
DECL|field|idf
specifier|protected
name|float
name|idf
decl_stmt|;
DECL|field|queryNorm
specifier|protected
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|protected
name|float
name|queryWeight
decl_stmt|;
DECL|field|terms
specifier|protected
name|Set
name|terms
decl_stmt|;
DECL|field|query
specifier|protected
name|SpanQuery
name|query
decl_stmt|;
DECL|method|SpanWeight
specifier|public
name|SpanWeight
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
name|this
operator|.
name|similarity
operator|=
name|query
operator|.
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|terms
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|query
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|idf
operator|=
name|this
operator|.
name|query
operator|.
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
operator|.
name|idf
argument_list|(
name|terms
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|idf
operator|*
name|query
operator|.
name|getBoost
argument_list|()
expr_stmt|;
comment|// compute query weight
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
comment|// square it
block|}
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|queryNorm
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
expr_stmt|;
comment|// normalize query weight
name|value
operator|=
name|queryWeight
operator|*
name|idf
expr_stmt|;
comment|// idf for document
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
name|SpanScorer
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
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|result
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
literal|"), product of:"
argument_list|)
expr_stmt|;
name|String
name|field
init|=
operator|(
operator|(
name|SpanQuery
operator|)
name|getQuery
argument_list|()
operator|)
operator|.
name|getField
argument_list|()
decl_stmt|;
name|StringBuffer
name|docFreqs
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Iterator
name|i
init|=
name|terms
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
name|Term
name|term
init|=
operator|(
name|Term
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|docFreqs
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|docFreqs
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|docFreqs
operator|.
name|append
argument_list|(
name|reader
operator|.
name|docFreq
argument_list|(
name|term
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
name|docFreqs
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|Explanation
name|idfExpl
init|=
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
literal|"idf("
operator|+
name|field
operator|+
literal|": "
operator|+
name|docFreqs
operator|+
literal|")"
argument_list|)
decl_stmt|;
comment|// explain query weight
name|Explanation
name|queryExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|queryExpl
operator|.
name|setDescription
argument_list|(
literal|"queryWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|boostExpl
init|=
operator|new
name|Explanation
argument_list|(
name|getQuery
argument_list|()
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getQuery
argument_list|()
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|boostExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|queryNormExpl
init|=
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
decl_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|queryNormExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|setValue
argument_list|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|queryNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|queryExpl
argument_list|)
expr_stmt|;
comment|// explain field weight
name|ComplexExplanation
name|fieldExpl
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|fieldExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldWeight("
operator|+
name|field
operator|+
literal|":"
operator|+
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|tfExpl
init|=
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|tfExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|fieldNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fieldNorms
init|=
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|float
name|fieldNorm
init|=
name|fieldNorms
operator|!=
literal|null
condition|?
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|fieldNorms
index|[
name|doc
index|]
argument_list|)
else|:
literal|1.0f
decl_stmt|;
name|fieldNormExpl
operator|.
name|setValue
argument_list|(
name|fieldNorm
argument_list|)
expr_stmt|;
name|fieldNormExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldNorm(field="
operator|+
name|field
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|fieldNormExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|tfExpl
operator|.
name|isMatch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setValue
argument_list|(
name|tfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|fieldExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|fieldExpl
operator|.
name|getMatch
argument_list|()
argument_list|)
expr_stmt|;
comment|// combine them
name|result
operator|.
name|setValue
argument_list|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|1.0f
condition|)
return|return
name|fieldExpl
return|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

