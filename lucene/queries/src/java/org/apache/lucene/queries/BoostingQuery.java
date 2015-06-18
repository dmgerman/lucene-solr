begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|Objects
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
name|LeafReaderContext
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

begin_comment
comment|/**  * The BoostingQuery class can be used to effectively demote results that match a given query.   * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,   * but reduces their overall score:  *  *     Query balancedQuery = new BoostingQuery(positiveQuery, negativeQuery, 0.01f);  * In this scenario the positiveQuery contains the mandatory, desirable criteria which is used to   * select all matching documents, and the negativeQuery contains the undesirable elements which   * are simply used to lessen the scores. Documents that match the negativeQuery have their score   * multiplied by the supplied "boost" parameter, so this should be less than 1 to achieve a   * demoting effect  *   * This code was originally made available here:   *<a href="http://marc.theaimsgroup.com/?l=lucene-user&m=108058407130459&w=2">http://marc.theaimsgroup.com/?l=lucene-user&amp;m=108058407130459&amp;w=2</a>  * and is documented here: http://wiki.apache.org/lucene-java/CommunityContributions  */
end_comment

begin_class
DECL|class|BoostingQuery
specifier|public
class|class
name|BoostingQuery
extends|extends
name|Query
block|{
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
comment|// the amount to boost by
DECL|field|match
specifier|private
specifier|final
name|Query
name|match
decl_stmt|;
comment|// query to match
DECL|field|context
specifier|private
specifier|final
name|Query
name|context
decl_stmt|;
comment|// boost when matches too
DECL|method|BoostingQuery
specifier|public
name|BoostingQuery
parameter_list|(
name|Query
name|match
parameter_list|,
name|Query
name|context
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// clone before boost
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
comment|// ignore context-only matches
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
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
return|return
name|match
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
specifier|final
name|Weight
name|matchWeight
init|=
name|match
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
specifier|final
name|Weight
name|contextWeight
init|=
name|context
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|matchWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|>=
literal|1
condition|)
block|{
name|contextWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Explanation
name|matchExplanation
init|=
name|matchWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|Explanation
name|contextExplanation
init|=
name|contextWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchExplanation
operator|.
name|isMatch
argument_list|()
operator|==
literal|false
operator|||
name|contextExplanation
operator|.
name|isMatch
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
name|matchExplanation
return|;
block|}
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|matchExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|boost
argument_list|,
literal|"product of:"
argument_list|,
name|matchExplanation
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|boost
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|matchWeight
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|matchWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|matchScorer
init|=
name|matchWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Scorer
name|contextScorer
init|=
name|contextWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextScorer
operator|==
literal|null
condition|)
block|{
return|return
name|matchScorer
return|;
block|}
name|TwoPhaseIterator
name|contextTwoPhase
init|=
name|contextScorer
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|contextApproximation
init|=
name|contextTwoPhase
operator|==
literal|null
condition|?
name|contextScorer
else|:
name|contextTwoPhase
operator|.
name|approximation
argument_list|()
decl_stmt|;
return|return
operator|new
name|FilterScorer
argument_list|(
name|matchScorer
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|contextApproximation
operator|.
name|docID
argument_list|()
operator|<
name|docID
argument_list|()
condition|)
block|{
name|contextApproximation
operator|.
name|advance
argument_list|(
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
name|contextApproximation
operator|.
name|docID
argument_list|()
operator|>=
name|docID
argument_list|()
assert|;
name|float
name|score
init|=
name|super
operator|.
name|score
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextApproximation
operator|.
name|docID
argument_list|()
operator|==
name|docID
argument_list|()
operator|&&
operator|(
name|contextTwoPhase
operator|==
literal|null
operator|||
name|contextTwoPhase
operator|.
name|matches
argument_list|()
operator|)
condition|)
block|{
name|score
operator|*=
name|boost
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
return|;
block|}
block|}
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
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|Objects
operator|.
name|hash
argument_list|(
name|match
argument_list|,
name|context
argument_list|,
name|boost
argument_list|)
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
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BoostingQuery
name|that
init|=
operator|(
name|BoostingQuery
operator|)
name|obj
decl_stmt|;
return|return
name|match
operator|.
name|equals
argument_list|(
name|that
operator|.
name|match
argument_list|)
operator|&&
name|context
operator|.
name|equals
argument_list|(
name|that
operator|.
name|context
argument_list|)
operator|&&
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
operator|==
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|that
operator|.
name|boost
argument_list|)
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
return|return
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|"/"
operator|+
name|context
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class

end_unit

