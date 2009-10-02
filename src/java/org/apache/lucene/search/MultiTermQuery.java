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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Serializable
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
name|Iterator
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
name|util
operator|.
name|ToStringUtils
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
name|queryParser
operator|.
name|QueryParser
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

begin_comment
comment|/**  * An abstract {@link Query} that matches documents  * containing a subset of terms provided by a {@link  * FilteredTermEnum} enumeration.  *  *<p>This query cannot be used directly; you must subclass  * it and define {@link #getEnum} to provide a {@link  * FilteredTermEnum} that iterates through the terms to be  * matched.  *  *<p><b>NOTE</b>: if {@link #setRewriteMethod} is either  * {@link #CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE} or {@link  * #SCORING_BOOLEAN_QUERY_REWRITE}, you may encounter a  * {@link BooleanQuery.TooManyClauses} exception during  * searching, which happens when the number of terms to be  * searched exceeds {@link  * BooleanQuery#getMaxClauseCount()}.  Setting {@link  * #setRewriteMethod} to {@link #CONSTANT_SCORE_FILTER_REWRITE}  * prevents this.  *  *<p>The recommended rewrite method is {@link  * #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT}: it doesn't spend CPU  * computing unhelpful scores, and it tries to pick the most  * performant rewrite method given the query.  *  * Note that {@link QueryParser} produces  * MultiTermQueries using {@link  * #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} by default.  */
end_comment

begin_class
DECL|class|MultiTermQuery
specifier|public
specifier|abstract
class|class
name|MultiTermQuery
extends|extends
name|Query
block|{
comment|/* @deprecated move to sub class */
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
DECL|field|rewriteMethod
specifier|protected
name|RewriteMethod
name|rewriteMethod
init|=
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
decl_stmt|;
DECL|field|numberOfTerms
specifier|transient
name|int
name|numberOfTerms
init|=
literal|0
decl_stmt|;
comment|/** Abstract class that defines how the query is rewritten. */
DECL|class|RewriteMethod
specifier|public
specifier|static
specifier|abstract
class|class
name|RewriteMethod
implements|implements
name|Serializable
block|{
DECL|method|rewrite
specifier|public
specifier|abstract
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|ConstantScoreFilterRewrite
specifier|private
specifier|static
specifier|final
class|class
name|ConstantScoreFilterRewrite
extends|extends
name|RewriteMethod
implements|implements
name|Serializable
block|{
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
block|{
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|MultiTermQueryWrapperFilter
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// Make sure we are still a singleton even after deserializing
DECL|method|readResolve
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|CONSTANT_SCORE_FILTER_REWRITE
return|;
block|}
block|}
comment|/** A rewrite method that first creates a private Filter,    *  by visiting each term in sequence and marking all docs    *  for that term.  Matching documents are assigned a    *  constant score equal to the query's boost.    *     *<p> This method is faster than the BooleanQuery    *  rewrite methods when the number of matched terms or    *  matched documents is non-trivial. Also, it will never    *  hit an errant {@link BooleanQuery.TooManyClauses}    *  exception.    *    *  @see #setRewriteMethod */
DECL|field|CONSTANT_SCORE_FILTER_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_FILTER_REWRITE
init|=
operator|new
name|ConstantScoreFilterRewrite
argument_list|()
decl_stmt|;
DECL|class|ScoringBooleanQueryRewrite
specifier|private
specifier|static
class|class
name|ScoringBooleanQueryRewrite
extends|extends
name|RewriteMethod
implements|implements
name|Serializable
block|{
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|FilteredTermEnum
name|enumerator
init|=
name|query
operator|.
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|t
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
operator|*
name|enumerator
operator|.
name|difference
argument_list|()
argument_list|)
expr_stmt|;
comment|// set the boost
name|result
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// add to query
name|count
operator|++
expr_stmt|;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|query
operator|.
name|incTotalNumberOfTerms
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// Make sure we are still a singleton even after deserializing
DECL|method|readResolve
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|SCORING_BOOLEAN_QUERY_REWRITE
return|;
block|}
block|}
comment|/** A rewrite method that first translates each term into    *  {@link BooleanClause.Occur#SHOULD} clause in a    *  BooleanQuery, and keeps the scores as computed by the    *  query.  Note that typically such scores are    *  meaningless to the user, and require non-trivial CPU    *  to compute, so it's almost always better to use {@link    *  #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} instead.    *    *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see #setRewriteMethod */
DECL|field|SCORING_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|SCORING_BOOLEAN_QUERY_REWRITE
init|=
operator|new
name|ScoringBooleanQueryRewrite
argument_list|()
decl_stmt|;
DECL|class|ConstantScoreBooleanQueryRewrite
specifier|private
specifier|static
class|class
name|ConstantScoreBooleanQueryRewrite
extends|extends
name|ScoringBooleanQueryRewrite
implements|implements
name|Serializable
block|{
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
comment|// strip the scores off
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|query
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// Make sure we are still a singleton even after deserializing
DECL|method|readResolve
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
return|;
block|}
block|}
comment|/** Like {@link #SCORING_BOOLEAN_QUERY_REWRITE} except    *  scores are not computed.  Instead, each matching    *  document receives a constant score equal to the    *  query's boost.    *     *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see #setRewriteMethod */
DECL|field|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
init|=
operator|new
name|ConstantScoreBooleanQueryRewrite
argument_list|()
decl_stmt|;
comment|/** A rewrite method that tries to pick the best    *  constant-score rewrite method based on term and    *  document counts from the query.  If both the number of    *  terms and documents is small enough, then {@link    *  #CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE} is used.    *  Otherwise, {@link #CONSTANT_SCORE_FILTER_REWRITE} is    *  used.    */
DECL|class|ConstantScoreAutoRewrite
specifier|public
specifier|static
class|class
name|ConstantScoreAutoRewrite
extends|extends
name|RewriteMethod
implements|implements
name|Serializable
block|{
comment|// Defaults derived from rough tests with a 20.0 million
comment|// doc Wikipedia index.  With more than 350 terms in the
comment|// query, the filter method is fastest:
DECL|field|DEFAULT_TERM_COUNT_CUTOFF
specifier|public
specifier|static
name|int
name|DEFAULT_TERM_COUNT_CUTOFF
init|=
literal|350
decl_stmt|;
comment|// If the query will hit more than 1 in 1000 of the docs
comment|// in the index (0.1%), the filter method is fastest:
DECL|field|DEFAULT_DOC_COUNT_PERCENT
specifier|public
specifier|static
name|double
name|DEFAULT_DOC_COUNT_PERCENT
init|=
literal|0.1
decl_stmt|;
DECL|field|termCountCutoff
specifier|private
name|int
name|termCountCutoff
init|=
name|DEFAULT_TERM_COUNT_CUTOFF
decl_stmt|;
DECL|field|docCountPercent
specifier|private
name|double
name|docCountPercent
init|=
name|DEFAULT_DOC_COUNT_PERCENT
decl_stmt|;
comment|/** If the number of terms in this query is equal to or      *  larger than this setting then {@link      *  #CONSTANT_SCORE_FILTER_REWRITE} is used. */
DECL|method|setTermCountCutoff
specifier|public
name|void
name|setTermCountCutoff
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|termCountCutoff
operator|=
name|count
expr_stmt|;
block|}
comment|/** @see #setTermCountCutoff */
DECL|method|getTermCountCutoff
specifier|public
name|int
name|getTermCountCutoff
parameter_list|()
block|{
return|return
name|termCountCutoff
return|;
block|}
comment|/** If the number of documents to be visited in the      *  postings exceeds this specified percentage of the      *  maxDoc() for the index, then {@link      *  #CONSTANT_SCORE_FILTER_REWRITE} is used.      *  @param percent 0.0 to 100.0 */
DECL|method|setDocCountPercent
specifier|public
name|void
name|setDocCountPercent
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
name|docCountPercent
operator|=
name|percent
expr_stmt|;
block|}
comment|/** @see #setDocCountPercent */
DECL|method|getDocCountPercent
specifier|public
name|double
name|getDocCountPercent
parameter_list|()
block|{
return|return
name|docCountPercent
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get the enum and start visiting terms.  If we
comment|// exhaust the enum before hitting either of the
comment|// cutoffs, we use ConstantBooleanQueryRewrite; else,
comment|// ConstantFilterRewrite:
specifier|final
name|Collection
name|pendingTerms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCountCutoff
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|docCountPercent
operator|/
literal|100.
operator|)
operator|*
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|termCountLimit
init|=
name|Math
operator|.
name|min
argument_list|(
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|,
name|termCountCutoff
argument_list|)
decl_stmt|;
name|int
name|docVisitCount
init|=
literal|0
decl_stmt|;
name|FilteredTermEnum
name|enumerator
init|=
name|query
operator|.
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Term
name|t
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|pendingTerms
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// Loading the TermInfo from the terms dict here
comment|// should not be costly, because 1) the
comment|// query/filter will load the TermInfo when it
comment|// runs, and 2) the terms dict has a cache:
name|docVisitCount
operator|+=
name|reader
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pendingTerms
operator|.
name|size
argument_list|()
operator|>=
name|termCountLimit
operator|||
name|docVisitCount
operator|>=
name|docCountCutoff
condition|)
block|{
comment|// Too many terms -- make a filter.
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|MultiTermQueryWrapperFilter
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|enumerator
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// Enumeration is done, and we hit a small
comment|// enough number of terms& docs -- just make a
comment|// BooleanQuery, now
name|Iterator
name|it
init|=
name|pendingTerms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|(
name|Term
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
comment|// Strip scores
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|bq
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|.
name|incTotalNumberOfTerms
argument_list|(
name|pendingTerms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
literal|1279
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|prime
operator|*
name|termCountCutoff
operator|+
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|docCountPercent
argument_list|)
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
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
name|ConstantScoreAutoRewrite
name|other
init|=
operator|(
name|ConstantScoreAutoRewrite
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|termCountCutoff
operator|!=
name|termCountCutoff
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|docCountPercent
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|docCountPercent
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/** Read-only default instance of {@link    *  ConstantScoreAutoRewrite}, with {@link    *  ConstantScoreAutoRewrite#setTermCountCutoff} set to    *  {@link    *  ConstantScoreAutoRewrite#DEFAULT_TERM_COUNT_CUTOFF}    *  and {@link    *  ConstantScoreAutoRewrite#setDocCountPercent} set to    *  {@link    *  ConstantScoreAutoRewrite#DEFAULT_DOC_COUNT_PERCENT}.    *  Note that you cannot alter the configuration of this    *  instance; you'll need to create a private instance    *  instead. */
DECL|field|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
init|=
operator|new
name|ConstantScoreAutoRewrite
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setTermCountCutoff
parameter_list|(
name|int
name|count
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Please create a private instance"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocCountPercent
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Please create a private instance"
argument_list|)
throw|;
block|}
comment|// Make sure we are still a singleton even after deserializing
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Constructs a query for terms matching<code>term</code>.    * @deprecated check sub class for possible term access - the Term does not    * make sense for all MultiTermQuerys and will be removed.    */
DECL|method|MultiTermQuery
specifier|public
name|MultiTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|/**    * Constructs a query matching terms that cannot be represented with a single    * Term.    */
DECL|method|MultiTermQuery
specifier|public
name|MultiTermQuery
parameter_list|()
block|{   }
comment|/**    * Returns the pattern term.    * @deprecated check sub class for possible term access - getTerm does not    * make sense for all MultiTermQuerys and will be removed.    */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
comment|/** Construct the enumeration to be used, expanding the pattern term. */
DECL|method|getEnum
specifier|protected
specifier|abstract
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Return the number of unique terms visited during execution of the query.    * If there are many of them, you may consider using another query type    * or optimize your total term count in index.    *<p>This method is not thread safe, be sure to only call it when no query is running!    * If you re-use the same query instance for another    * search, be sure to first reset the term counter    * with {@link #clearTotalNumberOfTerms}.    *<p>On optimized indexes / no MultiReaders, you get the correct number of    * unique terms for the whole index. Use this number to compare different queries.    * For non-optimized indexes this number can also be achieved in    * non-constant-score mode. In constant-score mode you get the total number of    * terms seeked for all segments / sub-readers.    * @see #clearTotalNumberOfTerms    */
DECL|method|getTotalNumberOfTerms
specifier|public
name|int
name|getTotalNumberOfTerms
parameter_list|()
block|{
return|return
name|numberOfTerms
return|;
block|}
comment|/**    * Expert: Resets the counting of unique terms.    * Do this before executing the query/filter.    * @see #getTotalNumberOfTerms    */
DECL|method|clearTotalNumberOfTerms
specifier|public
name|void
name|clearTotalNumberOfTerms
parameter_list|()
block|{
name|numberOfTerms
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|incTotalNumberOfTerms
specifier|protected
name|void
name|incTotalNumberOfTerms
parameter_list|(
name|int
name|inc
parameter_list|)
block|{
name|numberOfTerms
operator|+=
name|inc
expr_stmt|;
block|}
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
return|return
name|rewriteMethod
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/* Prints a user-readable version of this query.    * Implemented for back compat in case MultiTermQuery    * subclasses do no implement.    */
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
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"termPattern:unknown"
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * @see #setRewriteMethod    */
DECL|method|getRewriteMethod
specifier|public
name|RewriteMethod
name|getRewriteMethod
parameter_list|()
block|{
return|return
name|rewriteMethod
return|;
block|}
comment|/**    * Sets the rewrite method to be used when executing the    * query.  You can use one of the four core methods, or    * implement your own subclass of {@link RewriteMethod}. */
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|RewriteMethod
name|method
parameter_list|)
block|{
name|rewriteMethod
operator|=
name|method
expr_stmt|;
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
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
expr_stmt|;
name|result
operator|+=
name|rewriteMethod
operator|.
name|hashCode
argument_list|()
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
name|obj
operator|==
literal|null
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
name|MultiTermQuery
name|other
init|=
operator|(
name|MultiTermQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|getBoost
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|rewriteMethod
operator|.
name|equals
argument_list|(
name|other
operator|.
name|rewriteMethod
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

