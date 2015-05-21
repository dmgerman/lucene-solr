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
name|PostingsEnum
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|BitDocIdSet
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

begin_comment
comment|/**  * This class also provides the functionality behind  * {@link MultiTermQuery#CONSTANT_SCORE_REWRITE}.  * It tries to rewrite per-segment as a boolean query  * that returns a constant score and otherwise fills a  * bit set with matches and builds a Scorer on top of  * this bit set.  */
end_comment

begin_class
DECL|class|MultiTermQueryConstantScoreWrapper
specifier|final
class|class
name|MultiTermQueryConstantScoreWrapper
parameter_list|<
name|Q
extends|extends
name|MultiTermQuery
parameter_list|>
extends|extends
name|Query
block|{
comment|// mtq that matches 16 terms or less will be executed as a regular disjunction
DECL|field|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
init|=
literal|16
decl_stmt|;
DECL|class|TermAndState
specifier|private
specifier|static
class|class
name|TermAndState
block|{
DECL|field|term
specifier|final
name|BytesRef
name|term
decl_stmt|;
DECL|field|state
specifier|final
name|TermState
name|state
decl_stmt|;
DECL|field|docFreq
specifier|final
name|int
name|docFreq
decl_stmt|;
DECL|field|totalTermFreq
specifier|final
name|long
name|totalTermFreq
decl_stmt|;
DECL|method|TermAndState
name|TermAndState
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|long
name|totalTermFreq
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|this
operator|.
name|totalTermFreq
operator|=
name|totalTermFreq
expr_stmt|;
block|}
block|}
DECL|field|query
specifier|protected
specifier|final
name|Q
name|query
decl_stmt|;
comment|/**    * Wrap a {@link MultiTermQuery} as a Filter.    */
DECL|method|MultiTermQueryConstantScoreWrapper
specifier|protected
name|MultiTermQueryConstantScoreWrapper
parameter_list|(
name|Q
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
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
comment|// query.toString should be ok for the filter, too, if the query boost is 1.0f
return|return
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|MultiTermQueryConstantScoreWrapper
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|MultiTermQueryConstantScoreWrapper
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
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
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the field name for this query */
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
comment|/** Try to collect terms from the given terms enum and return true iff all        *  terms could be collected. If {@code false} is returned, the enum is        *  left positioned on the next term. */
specifier|private
name|boolean
name|collectTerms
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|List
argument_list|<
name|TermAndState
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|threshold
init|=
name|Math
operator|.
name|min
argument_list|(
name|BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
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
name|threshold
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|TermState
name|state
init|=
name|termsEnum
operator|.
name|termState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|isRealTerm
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// TermQuery does not accept fake terms for now
return|return
literal|false
return|;
block|}
name|terms
operator|.
name|add
argument_list|(
operator|new
name|TermAndState
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
argument_list|,
name|state
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|termsEnum
operator|.
name|next
argument_list|()
operator|==
literal|null
return|;
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
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|query
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// field does not exist
return|return
literal|null
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TermAndState
argument_list|>
name|collectedTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectTerms
argument_list|(
name|context
argument_list|,
name|termsEnum
argument_list|,
name|collectedTerms
argument_list|)
condition|)
block|{
comment|// build a boolean query
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|TermAndState
name|t
range|:
name|collectedTerms
control|)
block|{
specifier|final
name|TermContext
name|termContext
init|=
operator|new
name|TermContext
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|)
decl_stmt|;
name|termContext
operator|.
name|register
argument_list|(
name|t
operator|.
name|state
argument_list|,
name|context
operator|.
name|ord
argument_list|,
name|t
operator|.
name|docFreq
argument_list|,
name|t
operator|.
name|totalTermFreq
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|query
operator|.
name|field
argument_list|,
name|t
operator|.
name|term
argument_list|)
argument_list|,
name|termContext
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|score
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|searcher
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
comment|// Too many terms: go back to the terms we already collected and start building the bit set
if|if
condition|(
name|collectedTerms
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|TermsEnum
name|termsEnum2
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|TermAndState
name|t
range|:
name|collectedTerms
control|)
block|{
name|termsEnum2
operator|.
name|seekExact
argument_list|(
name|t
operator|.
name|term
argument_list|,
name|t
operator|.
name|state
argument_list|)
expr_stmt|;
name|docs
operator|=
name|termsEnum2
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Then keep filling the bit set with remaining terms
do|do
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
specifier|final
name|BitDocIdSet
name|set
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
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
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|disi
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

