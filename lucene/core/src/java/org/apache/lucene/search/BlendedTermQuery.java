begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Arrays
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
name|IndexReaderContext
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
name|index
operator|.
name|TermContext
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
name|TermState
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
name|util
operator|.
name|ArrayUtil
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
name|InPlaceMergeSorter
import|;
end_import

begin_comment
comment|/**  * A {@link Query} that blends index statistics across multiple terms.  * This is particularly useful when several terms should produce identical  * scores, regardless of their index statistics.  *<p>For instance imagine that you are resolving synonyms at search time,  * all terms should produce identical scores instead of the default behavior,  * which tends to give higher scores to rare terms.  *<p>An other useful use-case is cross-field search: imagine that you would  * like to search for {@code john} on two fields: {@code first_name} and  * {@code last_name}. You might not want to give a higher weight to matches  * on the field where {@code john} is rarer, in which case  * {@link BlendedTermQuery} would help as well.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlendedTermQuery
specifier|public
specifier|final
class|class
name|BlendedTermQuery
extends|extends
name|Query
block|{
comment|/** A Builder for {@link BlendedTermQuery}. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|numTerms
specifier|private
name|int
name|numTerms
init|=
literal|0
decl_stmt|;
DECL|field|terms
specifier|private
name|Term
index|[]
name|terms
init|=
operator|new
name|Term
index|[
literal|0
index|]
decl_stmt|;
DECL|field|boosts
specifier|private
name|float
index|[]
name|boosts
init|=
operator|new
name|float
index|[
literal|0
index|]
decl_stmt|;
DECL|field|contexts
specifier|private
name|TermContext
index|[]
name|contexts
init|=
operator|new
name|TermContext
index|[
literal|0
index|]
decl_stmt|;
DECL|field|rewriteMethod
specifier|private
name|RewriteMethod
name|rewriteMethod
init|=
name|DISJUNCTION_MAX_REWRITE
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{}
comment|/** Set the {@link RewriteMethod}. Default is to use      *  {@link BlendedTermQuery#DISJUNCTION_MAX_REWRITE}.      *  @see RewriteMethod */
DECL|method|setRewriteMethod
specifier|public
name|Builder
name|setRewriteMethod
parameter_list|(
name|RewriteMethod
name|rewiteMethod
parameter_list|)
block|{
name|this
operator|.
name|rewriteMethod
operator|=
name|rewiteMethod
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add a new {@link Term} to this builder, with a default boost of {@code 1}.      *  @see #add(Term, float) */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|term
argument_list|,
literal|1f
argument_list|)
return|;
block|}
comment|/** Add a {@link Term} with the provided boost. The higher the boost, the      *  more this term will contribute to the overall score of the      *  {@link BlendedTermQuery}. */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|term
argument_list|,
name|boost
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Expert: Add a {@link Term} with the provided boost and context.      * This method is useful if you already have a {@link TermContext}      * object constructed for the given term.      */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|boost
parameter_list|,
name|TermContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|numTerms
operator|>=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BooleanQuery
operator|.
name|TooManyClauses
argument_list|()
throw|;
block|}
name|terms
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|terms
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|boosts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|boosts
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|contexts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|contexts
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|terms
index|[
name|numTerms
index|]
operator|=
name|term
expr_stmt|;
name|boosts
index|[
name|numTerms
index|]
operator|=
name|boost
expr_stmt|;
name|contexts
index|[
name|numTerms
index|]
operator|=
name|context
expr_stmt|;
name|numTerms
operator|+=
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Build the {@link BlendedTermQuery}. */
DECL|method|build
specifier|public
name|BlendedTermQuery
name|build
parameter_list|()
block|{
return|return
operator|new
name|BlendedTermQuery
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|terms
argument_list|,
name|numTerms
argument_list|)
argument_list|,
name|Arrays
operator|.
name|copyOf
argument_list|(
name|boosts
argument_list|,
name|numTerms
argument_list|)
argument_list|,
name|Arrays
operator|.
name|copyOf
argument_list|(
name|contexts
argument_list|,
name|numTerms
argument_list|)
argument_list|,
name|rewriteMethod
argument_list|)
return|;
block|}
block|}
comment|/** A {@link RewriteMethod} defines how queries for individual terms should    *  be merged.    *  @lucene.experimental    *  @see BlendedTermQuery#BOOLEAN_REWRITE    *  @see BlendedTermQuery.DisjunctionMaxRewrite */
DECL|class|RewriteMethod
specifier|public
specifier|static
specifier|abstract
class|class
name|RewriteMethod
block|{
comment|/** Sole constructor */
DECL|method|RewriteMethod
specifier|protected
name|RewriteMethod
parameter_list|()
block|{}
comment|/** Merge the provided sub queries into a single {@link Query} object. */
DECL|method|rewrite
specifier|public
specifier|abstract
name|Query
name|rewrite
parameter_list|(
name|Query
index|[]
name|subQueries
parameter_list|)
function_decl|;
block|}
comment|/**    * A {@link RewriteMethod} that adds all sub queries to a {@link BooleanQuery}.    * This {@link RewriteMethod} is useful when matching on several fields is    * considered better than having a good match on a single field.    */
DECL|field|BOOLEAN_REWRITE
specifier|public
specifier|static
specifier|final
name|RewriteMethod
name|BOOLEAN_REWRITE
init|=
operator|new
name|RewriteMethod
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
index|[]
name|subQueries
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|merged
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|subQueries
control|)
block|{
name|merged
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|merged
operator|.
name|build
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A {@link RewriteMethod} that creates a {@link DisjunctionMaxQuery} out    * of the sub queries. This {@link RewriteMethod} is useful when having a    * good match on a single field is considered better than having average    * matches on several fields.    */
DECL|class|DisjunctionMaxRewrite
specifier|public
specifier|static
class|class
name|DisjunctionMaxRewrite
extends|extends
name|RewriteMethod
block|{
DECL|field|tieBreakerMultiplier
specifier|private
specifier|final
name|float
name|tieBreakerMultiplier
decl_stmt|;
comment|/** This {@link RewriteMethod} will create {@link DisjunctionMaxQuery}      *  instances that have the provided tie breaker.      *  @see DisjunctionMaxQuery */
DECL|method|DisjunctionMaxRewrite
specifier|public
name|DisjunctionMaxRewrite
parameter_list|(
name|float
name|tieBreakerMultiplier
parameter_list|)
block|{
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
index|[]
name|subQueries
parameter_list|)
block|{
return|return
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|subQueries
argument_list|)
argument_list|,
name|tieBreakerMultiplier
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
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DisjunctionMaxRewrite
name|that
init|=
operator|(
name|DisjunctionMaxRewrite
operator|)
name|obj
decl_stmt|;
return|return
name|tieBreakerMultiplier
operator|==
name|that
operator|.
name|tieBreakerMultiplier
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
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|tieBreakerMultiplier
argument_list|)
return|;
block|}
block|}
comment|/** {@link DisjunctionMaxRewrite} instance with a tie-breaker of {@code 0.01}. */
DECL|field|DISJUNCTION_MAX_REWRITE
specifier|public
specifier|static
specifier|final
name|RewriteMethod
name|DISJUNCTION_MAX_REWRITE
init|=
operator|new
name|DisjunctionMaxRewrite
argument_list|(
literal|0.01f
argument_list|)
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|Term
index|[]
name|terms
decl_stmt|;
DECL|field|boosts
specifier|private
specifier|final
name|float
index|[]
name|boosts
decl_stmt|;
DECL|field|contexts
specifier|private
specifier|final
name|TermContext
index|[]
name|contexts
decl_stmt|;
DECL|field|rewriteMethod
specifier|private
specifier|final
name|RewriteMethod
name|rewriteMethod
decl_stmt|;
DECL|method|BlendedTermQuery
specifier|private
name|BlendedTermQuery
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|float
index|[]
name|boosts
parameter_list|,
name|TermContext
index|[]
name|contexts
parameter_list|,
name|RewriteMethod
name|rewriteMethod
parameter_list|)
block|{
assert|assert
name|terms
operator|.
name|length
operator|==
name|boosts
operator|.
name|length
assert|;
assert|assert
name|terms
operator|.
name|length
operator|==
name|contexts
operator|.
name|length
assert|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|boosts
operator|=
name|boosts
expr_stmt|;
name|this
operator|.
name|contexts
operator|=
name|contexts
expr_stmt|;
name|this
operator|.
name|rewriteMethod
operator|=
name|rewriteMethod
expr_stmt|;
comment|// we sort terms so that equals/hashcode does not rely on the order
operator|new
name|InPlaceMergeSorter
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|Term
name|tmpTerm
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
name|terms
index|[
name|i
index|]
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
name|terms
index|[
name|j
index|]
operator|=
name|tmpTerm
expr_stmt|;
name|TermContext
name|tmpContext
init|=
name|contexts
index|[
name|i
index|]
decl_stmt|;
name|contexts
index|[
name|i
index|]
operator|=
name|contexts
index|[
name|j
index|]
expr_stmt|;
name|contexts
index|[
name|j
index|]
operator|=
name|tmpContext
expr_stmt|;
name|float
name|tmpBoost
init|=
name|boosts
index|[
name|i
index|]
decl_stmt|;
name|boosts
index|[
name|i
index|]
operator|=
name|boosts
index|[
name|j
index|]
expr_stmt|;
name|boosts
index|[
name|j
index|]
operator|=
name|tmpBoost
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|terms
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|terms
index|[
name|j
index|]
argument_list|)
return|;
block|}
block|}
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|terms
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|BlendedTermQuery
name|other
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|terms
argument_list|,
name|other
operator|.
name|terms
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|contexts
argument_list|,
name|other
operator|.
name|contexts
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|boosts
argument_list|,
name|other
operator|.
name|boosts
argument_list|)
operator|&&
name|rewriteMethod
operator|.
name|equals
argument_list|(
name|other
operator|.
name|rewriteMethod
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|boosts
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|rewriteMethod
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
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
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Blended("
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
name|terms
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|Query
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|boosts
index|[
name|i
index|]
operator|!=
literal|1f
condition|)
block|{
name|termQuery
operator|=
operator|new
name|BoostQuery
argument_list|(
name|termQuery
argument_list|,
name|boosts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|termQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|final
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermContext
index|[]
name|contexts
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|contexts
argument_list|,
name|this
operator|.
name|contexts
operator|.
name|length
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
name|contexts
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|contexts
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|contexts
index|[
name|i
index|]
operator|.
name|wasBuiltFor
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|contexts
index|[
name|i
index|]
operator|=
name|TermContext
operator|.
name|build
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Compute aggregated doc freq and total term freq
comment|// df will be the max of all doc freqs
comment|// ttf will be the sum of all total term freqs
name|int
name|df
init|=
literal|0
decl_stmt|;
name|long
name|ttf
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TermContext
name|ctx
range|:
name|contexts
control|)
block|{
name|df
operator|=
name|Math
operator|.
name|max
argument_list|(
name|df
argument_list|,
name|ctx
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ctx
operator|.
name|totalTermFreq
argument_list|()
operator|==
operator|-
literal|1L
condition|)
block|{
name|ttf
operator|=
operator|-
literal|1L
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ttf
operator|!=
operator|-
literal|1L
condition|)
block|{
name|ttf
operator|+=
name|ctx
operator|.
name|totalTermFreq
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contexts
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|contexts
index|[
name|i
index|]
operator|=
name|adjustFrequencies
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|contexts
index|[
name|i
index|]
argument_list|,
name|df
argument_list|,
name|ttf
argument_list|)
expr_stmt|;
block|}
name|Query
index|[]
name|termQueries
init|=
operator|new
name|Query
index|[
name|terms
operator|.
name|length
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
name|terms
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|termQueries
index|[
name|i
index|]
operator|=
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|contexts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|boosts
index|[
name|i
index|]
operator|!=
literal|1f
condition|)
block|{
name|termQueries
index|[
name|i
index|]
operator|=
operator|new
name|BoostQuery
argument_list|(
name|termQueries
index|[
name|i
index|]
argument_list|,
name|boosts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rewriteMethod
operator|.
name|rewrite
argument_list|(
name|termQueries
argument_list|)
return|;
block|}
DECL|method|adjustFrequencies
specifier|private
specifier|static
name|TermContext
name|adjustFrequencies
parameter_list|(
name|IndexReaderContext
name|readerContext
parameter_list|,
name|TermContext
name|ctx
parameter_list|,
name|int
name|artificialDf
parameter_list|,
name|long
name|artificialTtf
parameter_list|)
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|readerContext
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
if|if
condition|(
name|leaves
operator|==
literal|null
condition|)
block|{
name|len
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|len
operator|=
name|leaves
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|TermContext
name|newCtx
init|=
operator|new
name|TermContext
argument_list|(
name|readerContext
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|TermState
name|termState
init|=
name|ctx
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|termState
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|newCtx
operator|.
name|register
argument_list|(
name|termState
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|newCtx
operator|.
name|accumulateStatistics
argument_list|(
name|artificialDf
argument_list|,
name|artificialTtf
argument_list|)
expr_stmt|;
return|return
name|newCtx
return|;
block|}
block|}
end_class

end_unit

