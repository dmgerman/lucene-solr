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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
operator|.
name|BooleanWeight
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

begin_comment
comment|/* See the description in BooleanScorer.java, comparing  * BooleanScorer& BooleanScorer2 */
end_comment

begin_comment
comment|/** An alternative to BooleanScorer that also allows a minimum number  * of optional scorers that should match.  *<br>Implements skipTo(), and has no limitations on the numbers of added scorers.  *<br>Uses ConjunctionScorer, DisjunctionScorer, ReqOptScorer and ReqExclScorer.  */
end_comment

begin_class
DECL|class|BooleanScorer2
class|class
name|BooleanScorer2
extends|extends
name|Scorer
block|{
DECL|field|requiredScorers
specifier|private
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|requiredScorers
decl_stmt|;
DECL|field|optionalScorers
specifier|private
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|optionalScorers
decl_stmt|;
DECL|field|prohibitedScorers
specifier|private
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|prohibitedScorers
decl_stmt|;
DECL|class|Coordinator
specifier|private
class|class
name|Coordinator
block|{
DECL|field|coordFactors
specifier|final
name|float
name|coordFactors
index|[]
decl_stmt|;
DECL|method|Coordinator
name|Coordinator
parameter_list|(
name|int
name|maxCoord
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
block|{
name|coordFactors
operator|=
operator|new
name|float
index|[
name|optionalScorers
operator|.
name|size
argument_list|()
operator|+
name|requiredScorers
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|coordFactors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|coordFactors
index|[
name|i
index|]
operator|=
name|disableCoord
condition|?
literal|1.0f
else|:
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|nrMatchers
name|int
name|nrMatchers
decl_stmt|;
comment|// to be increased by score() of match counting scorers.
block|}
DECL|field|coordinator
specifier|private
specifier|final
name|Coordinator
name|coordinator
decl_stmt|;
comment|/** The scorer to which all scoring will be delegated,    * except for computing and using the coordination factor.    */
DECL|field|countingSumScorer
specifier|private
specifier|final
name|Scorer
name|countingSumScorer
decl_stmt|;
comment|/** The number of optionalScorers that need to match (if there are any) */
DECL|field|minNrShouldMatch
specifier|private
specifier|final
name|int
name|minNrShouldMatch
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Creates a {@link Scorer} with the given similarity and lists of required,    * prohibited and optional scorers. In no required scorers are added, at least    * one of the optional scorers will have to match during the search.    *     * @param weight    *          The BooleanWeight to be used.    * @param disableCoord    *          If this parameter is true, coordination level matching     *          ({@link Similarity#coord(int, int)}) is not used.    * @param minNrShouldMatch    *          The minimum number of optional added scorers that should match    *          during the search. In case no required scorers are added, at least    *          one of the optional scorers will have to match during the search.    * @param required    *          the list of required scorers.    * @param prohibited    *          the list of prohibited scorers.    * @param optional    *          the list of optional scorers.    */
DECL|method|BooleanScorer2
specifier|public
name|BooleanScorer2
parameter_list|(
name|BooleanWeight
name|weight
parameter_list|,
name|boolean
name|disableCoord
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|required
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|prohibited
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|optional
parameter_list|,
name|int
name|maxCoord
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
if|if
condition|(
name|minNrShouldMatch
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Minimum number of optional scorers should not be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minNrShouldMatch
operator|=
name|minNrShouldMatch
expr_stmt|;
name|optionalScorers
operator|=
name|optional
expr_stmt|;
name|requiredScorers
operator|=
name|required
expr_stmt|;
name|prohibitedScorers
operator|=
name|prohibited
expr_stmt|;
name|coordinator
operator|=
operator|new
name|Coordinator
argument_list|(
name|maxCoord
argument_list|,
name|disableCoord
argument_list|)
expr_stmt|;
name|countingSumScorer
operator|=
name|makeCountingSumScorer
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
block|}
comment|/** Count a scorer as a single match. */
DECL|class|SingleMatchScorer
specifier|private
class|class
name|SingleMatchScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|lastScoredDoc
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// Save the score of lastScoredDoc, so that we don't compute it more than
comment|// once in score().
DECL|field|lastDocScore
specifier|private
name|float
name|lastDocScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|method|SingleMatchScorer
name|SingleMatchScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|super
argument_list|(
name|scorer
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
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
name|int
name|doc
init|=
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|lastScoredDoc
condition|)
block|{
if|if
condition|(
name|doc
operator|>
name|lastScoredDoc
condition|)
block|{
name|lastDocScore
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|doc
expr_stmt|;
block|}
name|coordinator
operator|.
name|nrMatchers
operator|++
expr_stmt|;
block|}
return|return
name|lastDocScore
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
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
return|return
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
DECL|method|countingDisjunctionSumScorer
specifier|private
name|Scorer
name|countingDisjunctionSumScorer
parameter_list|(
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|scorers
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each scorer from the list counted as a single matcher
if|if
condition|(
name|minNrShouldMatch
operator|>
literal|1
condition|)
block|{
return|return
operator|new
name|MinShouldMatchSumScorer
argument_list|(
name|weight
argument_list|,
name|scorers
argument_list|,
name|minNrShouldMatch
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
name|coordinator
operator|.
name|nrMatchers
operator|+=
name|super
operator|.
name|nrMatchers
expr_stmt|;
return|return
name|super
operator|.
name|score
argument_list|()
return|;
block|}
block|}
return|;
block|}
else|else
block|{
comment|// we pass null for coord[] since we coordinate ourselves and override score()
return|return
operator|new
name|DisjunctionSumScorer
argument_list|(
name|weight
argument_list|,
name|scorers
operator|.
name|toArray
argument_list|(
operator|new
name|Scorer
index|[
name|scorers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
literal|null
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
name|coordinator
operator|.
name|nrMatchers
operator|+=
name|super
operator|.
name|nrMatchers
expr_stmt|;
return|return
operator|(
name|float
operator|)
name|super
operator|.
name|score
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|countingConjunctionSumScorer
specifier|private
name|Scorer
name|countingConjunctionSumScorer
parameter_list|(
name|boolean
name|disableCoord
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|requiredScorers
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each scorer from the list counted as a single matcher
specifier|final
name|int
name|requiredNrMatchers
init|=
name|requiredScorers
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|weight
argument_list|,
name|requiredScorers
operator|.
name|toArray
argument_list|(
operator|new
name|Scorer
index|[
name|requiredScorers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
block|{
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// Save the score of lastScoredDoc, so that we don't compute it more than
comment|// once in score().
specifier|private
name|float
name|lastDocScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
annotation|@
name|Override
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
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|lastScoredDoc
condition|)
block|{
if|if
condition|(
name|doc
operator|>
name|lastScoredDoc
condition|)
block|{
name|lastDocScore
operator|=
name|super
operator|.
name|score
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|doc
expr_stmt|;
block|}
name|coordinator
operator|.
name|nrMatchers
operator|+=
name|requiredNrMatchers
expr_stmt|;
block|}
comment|// All scorers match, so defaultSimilarity super.score() always has 1 as
comment|// the coordination factor.
comment|// Therefore the sum of the scores of the requiredScorers
comment|// is used as score.
return|return
name|lastDocScore
return|;
block|}
block|}
return|;
block|}
DECL|method|dualConjunctionSumScorer
specifier|private
name|Scorer
name|dualConjunctionSumScorer
parameter_list|(
name|boolean
name|disableCoord
parameter_list|,
name|Scorer
name|req1
parameter_list|,
name|Scorer
name|req2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// non counting.
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|weight
argument_list|,
operator|new
name|Scorer
index|[]
block|{
name|req1
block|,
name|req2
block|}
argument_list|)
return|;
comment|// All scorers match, so defaultSimilarity always has 1 as
comment|// the coordination factor.
comment|// Therefore the sum of the scores of two scorers
comment|// is used as score.
block|}
comment|/** Returns the scorer to be used for match counting and score summing.    * Uses requiredScorers, optionalScorers and prohibitedScorers.    */
DECL|method|makeCountingSumScorer
specifier|private
name|Scorer
name|makeCountingSumScorer
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each scorer counted as a single matcher
return|return
operator|(
name|requiredScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|makeCountingSumScorerNoReq
argument_list|(
name|disableCoord
argument_list|)
else|:
name|makeCountingSumScorerSomeReq
argument_list|(
name|disableCoord
argument_list|)
return|;
block|}
DECL|method|makeCountingSumScorerNoReq
specifier|private
name|Scorer
name|makeCountingSumScorerNoReq
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
comment|// No required scorers
comment|// minNrShouldMatch optional scorers are required, but at least 1
name|int
name|nrOptRequired
init|=
operator|(
name|minNrShouldMatch
operator|<
literal|1
operator|)
condition|?
literal|1
else|:
name|minNrShouldMatch
decl_stmt|;
name|Scorer
name|requiredCountingSumScorer
decl_stmt|;
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|>
name|nrOptRequired
condition|)
name|requiredCountingSumScorer
operator|=
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
name|nrOptRequired
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
name|requiredCountingSumScorer
operator|=
operator|new
name|SingleMatchScorer
argument_list|(
name|optionalScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
else|else
block|{
name|requiredCountingSumScorer
operator|=
name|countingConjunctionSumScorer
argument_list|(
name|disableCoord
argument_list|,
name|optionalScorers
argument_list|)
expr_stmt|;
block|}
return|return
name|addProhibitedScorers
argument_list|(
name|requiredCountingSumScorer
argument_list|)
return|;
block|}
DECL|method|makeCountingSumScorerSomeReq
specifier|private
name|Scorer
name|makeCountingSumScorerSomeReq
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
comment|// At least one required scorer.
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
name|minNrShouldMatch
condition|)
block|{
comment|// all optional scorers also required.
name|ArrayList
argument_list|<
name|Scorer
argument_list|>
name|allReq
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|requiredScorers
argument_list|)
decl_stmt|;
name|allReq
operator|.
name|addAll
argument_list|(
name|optionalScorers
argument_list|)
expr_stmt|;
return|return
name|addProhibitedScorers
argument_list|(
name|countingConjunctionSumScorer
argument_list|(
name|disableCoord
argument_list|,
name|allReq
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// optionalScorers.size()> minNrShouldMatch, and at least one required scorer
name|Scorer
name|requiredCountingSumScorer
init|=
name|requiredScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|SingleMatchScorer
argument_list|(
name|requiredScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
name|countingConjunctionSumScorer
argument_list|(
name|disableCoord
argument_list|,
name|requiredScorers
argument_list|)
decl_stmt|;
if|if
condition|(
name|minNrShouldMatch
operator|>
literal|0
condition|)
block|{
comment|// use a required disjunction scorer over the optional scorers
return|return
name|addProhibitedScorers
argument_list|(
name|dualConjunctionSumScorer
argument_list|(
comment|// non counting
name|disableCoord
argument_list|,
name|requiredCountingSumScorer
argument_list|,
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
name|minNrShouldMatch
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// minNrShouldMatch == 0
return|return
operator|new
name|ReqOptSumScorer
argument_list|(
name|addProhibitedScorers
argument_list|(
name|requiredCountingSumScorer
argument_list|)
argument_list|,
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|SingleMatchScorer
argument_list|(
name|optionalScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
comment|// require 1 in combined, optional scorer.
else|:
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Returns the scorer to be used for match counting and score summing.    * Uses the given required scorer and the prohibitedScorers.    * @param requiredCountingSumScorer A required scorer already built.    */
DECL|method|addProhibitedScorers
specifier|private
name|Scorer
name|addProhibitedScorers
parameter_list|(
name|Scorer
name|requiredCountingSumScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|requiredCountingSumScorer
comment|// no prohibited
else|:
operator|new
name|ReqExclScorer
argument_list|(
name|requiredCountingSumScorer
argument_list|,
operator|(
operator|(
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
name|prohibitedScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
operator|new
name|MinShouldMatchSumScorer
argument_list|(
name|weight
argument_list|,
name|prohibitedScorers
argument_list|)
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doc
operator|=
name|countingSumScorer
operator|.
name|nextDoc
argument_list|()
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
name|coordinator
operator|.
name|nrMatchers
operator|=
literal|0
expr_stmt|;
name|float
name|sum
init|=
name|countingSumScorer
operator|.
name|score
argument_list|()
decl_stmt|;
return|return
name|sum
operator|*
name|coordinator
operator|.
name|coordFactors
index|[
name|coordinator
operator|.
name|nrMatchers
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|countingSumScorer
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|doc
operator|=
name|countingSumScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
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
return|return
name|countingSumScorer
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
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
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Scorer
name|s
range|:
name|optionalScorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|s
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Scorer
name|s
range|:
name|prohibitedScorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|s
argument_list|,
literal|"MUST_NOT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Scorer
name|s
range|:
name|requiredScorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|s
argument_list|,
literal|"MUST"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

