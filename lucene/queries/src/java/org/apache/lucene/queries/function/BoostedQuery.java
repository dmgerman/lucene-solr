begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package

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
name|FilterScorer
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
name|Query
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
name|Weight
import|;
end_import

begin_comment
comment|/**  * Query that is boosted by a ValueSource  */
end_comment

begin_comment
comment|// TODO: BoostedQuery and BoostingQuery in the same module?
end_comment

begin_comment
comment|// something has to give
end_comment

begin_class
DECL|class|BoostedQuery
specifier|public
specifier|final
class|class
name|BoostedQuery
extends|extends
name|Query
block|{
DECL|field|q
specifier|private
specifier|final
name|Query
name|q
decl_stmt|;
DECL|field|boostVal
specifier|private
specifier|final
name|ValueSource
name|boostVal
decl_stmt|;
comment|// optional, can be null
DECL|method|BoostedQuery
specifier|public
name|BoostedQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|ValueSource
name|boostVal
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|subQuery
expr_stmt|;
name|this
operator|.
name|boostVal
operator|=
name|boostVal
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|q
return|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|boostVal
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|newQ
init|=
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newQ
operator|!=
name|q
condition|)
block|{
return|return
operator|new
name|BoostedQuery
argument_list|(
name|newQ
argument_list|,
name|boostVal
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
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
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostedQuery
operator|.
name|BoostedWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
return|;
block|}
DECL|class|BoostedWeight
specifier|private
class|class
name|BoostedWeight
extends|extends
name|Weight
block|{
DECL|field|qWeight
name|Weight
name|qWeight
decl_stmt|;
DECL|field|fcontext
name|Map
name|fcontext
decl_stmt|;
DECL|method|BoostedWeight
specifier|public
name|BoostedWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|BoostedQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|qWeight
operator|=
name|searcher
operator|.
name|createWeight
argument_list|(
name|q
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|this
operator|.
name|fcontext
operator|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|boostVal
operator|.
name|createWeight
argument_list|(
name|fcontext
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
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
name|qWeight
operator|.
name|extractTerms
argument_list|(
name|terms
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
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|subQueryScorer
init|=
name|qWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|subQueryScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|BoostedQuery
operator|.
name|CustomScorer
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|subQueryScorer
argument_list|,
name|boostVal
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
name|LeafReaderContext
name|readerContext
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|subQueryExpl
init|=
name|qWeight
operator|.
name|explain
argument_list|(
name|readerContext
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subQueryExpl
operator|.
name|isMatch
argument_list|()
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
name|FunctionValues
name|vals
init|=
name|boostVal
operator|.
name|getValues
argument_list|(
name|fcontext
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
name|float
name|sc
init|=
name|subQueryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|sc
argument_list|,
name|BoostedQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|,
name|subQueryExpl
argument_list|,
name|vals
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|CustomScorer
specifier|private
class|class
name|CustomScorer
extends|extends
name|FilterScorer
block|{
DECL|field|weight
specifier|private
specifier|final
name|BoostedQuery
operator|.
name|BoostedWeight
name|weight
decl_stmt|;
DECL|field|vals
specifier|private
specifier|final
name|FunctionValues
name|vals
decl_stmt|;
DECL|field|readerContext
specifier|private
specifier|final
name|LeafReaderContext
name|readerContext
decl_stmt|;
DECL|method|CustomScorer
specifier|private
name|CustomScorer
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|,
name|BoostedQuery
operator|.
name|BoostedWeight
name|w
parameter_list|,
name|Scorer
name|scorer
parameter_list|,
name|ValueSource
name|vs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|vals
operator|=
name|vs
operator|.
name|getValues
argument_list|(
name|weight
operator|.
name|fcontext
argument_list|,
name|readerContext
argument_list|)
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
name|float
name|score
init|=
name|in
operator|.
name|score
argument_list|()
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|in
operator|.
name|docID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Current Lucene priority queues can't handle NaN and -Infinity, so
comment|// map to -Float.MAX_VALUE. This conditional handles both -infinity
comment|// and NaN since comparisons with NaN are always false.
return|return
name|score
operator|>
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|?
name|score
else|:
operator|-
name|Float
operator|.
name|MAX_VALUE
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|in
argument_list|,
literal|"CUSTOM"
argument_list|)
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
name|subQueryExpl
init|=
name|weight
operator|.
name|qWeight
operator|.
name|explain
argument_list|(
name|readerContext
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subQueryExpl
operator|.
name|isMatch
argument_list|()
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
name|float
name|sc
init|=
name|subQueryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|sc
argument_list|,
name|BoostedQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|,
name|subQueryExpl
argument_list|,
name|vals
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
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
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"boost("
argument_list|)
operator|.
name|append
argument_list|(
name|q
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|boostVal
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|BoostedQuery
name|other
parameter_list|)
block|{
return|return
name|q
operator|.
name|equals
argument_list|(
name|other
operator|.
name|q
argument_list|)
operator|&&
name|boostVal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|boostVal
argument_list|)
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
name|int
name|h
init|=
name|classHash
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|q
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|boostVal
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

