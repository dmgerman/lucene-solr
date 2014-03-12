begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.complexPhrase
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|complexPhrase
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
name|Iterator
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
name|analysis
operator|.
name|Analyzer
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|MultiTermQuery
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
name|TermQuery
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
name|TermRangeQuery
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
name|SpanNotQuery
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
name|SpanOrQuery
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
name|SpanTermQuery
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
name|Version
import|;
end_import

begin_comment
comment|/**  * QueryParser which permits complex phrase query syntax eg "(john jon  * jonathan~) peters*".  *<p>  * Performs potentially multiple passes over Query text to parse any nested  * logic in PhraseQueries. - First pass takes any PhraseQuery content between  * quotes and stores for subsequent pass. All other query content is parsed as  * normal - Second pass parses any stored PhraseQuery content, checking all  * embedded clauses are referring to the same field and therefore can be  * rewritten as Span queries. All PhraseQuery clauses are expressed as  * ComplexPhraseQuery objects  *</p>  *<p>  * This could arguably be done in one pass using a new QueryParser but here I am  * working within the constraints of the existing parser as a base class. This  * currently simply feeds all phrase content through an analyzer to select  * phrase terms - any "special" syntax such as * ~ * etc are not given special  * status  *</p>  *   */
end_comment

begin_class
DECL|class|ComplexPhraseQueryParser
specifier|public
class|class
name|ComplexPhraseQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|complexPhrases
specifier|private
name|ArrayList
argument_list|<
name|ComplexPhraseQuery
argument_list|>
name|complexPhrases
init|=
literal|null
decl_stmt|;
DECL|field|isPass2ResolvingPhrases
specifier|private
name|boolean
name|isPass2ResolvingPhrases
decl_stmt|;
DECL|field|currentPhraseQuery
specifier|private
name|ComplexPhraseQuery
name|currentPhraseQuery
init|=
literal|null
decl_stmt|;
DECL|method|ComplexPhraseQueryParser
specifier|public
name|ComplexPhraseQueryParser
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
name|f
parameter_list|,
name|Analyzer
name|a
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|f
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|int
name|slop
parameter_list|)
block|{
name|ComplexPhraseQuery
name|cpq
init|=
operator|new
name|ComplexPhraseQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|slop
argument_list|)
decl_stmt|;
name|complexPhrases
operator|.
name|add
argument_list|(
name|cpq
argument_list|)
expr_stmt|;
comment|// add to list of phrases to be parsed once
comment|// we
comment|// are through with this pass
return|return
name|cpq
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
name|MultiTermQuery
operator|.
name|RewriteMethod
name|oldMethod
init|=
name|getMultiTermRewriteMethod
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Temporarily force BooleanQuery rewrite so that Parser will
comment|// generate visible
comment|// collection of terms which we can convert into SpanQueries.
comment|// ConstantScoreRewrite mode produces an
comment|// opaque ConstantScoreQuery object which cannot be interrogated for
comment|// terms in the same way a BooleanQuery can.
comment|// QueryParser is not guaranteed threadsafe anyway so this temporary
comment|// state change should not
comment|// present an issue
name|setMultiTermRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|parse
argument_list|(
name|query
argument_list|)
return|;
block|}
finally|finally
block|{
name|setMultiTermRewriteMethod
argument_list|(
name|oldMethod
argument_list|)
expr_stmt|;
block|}
block|}
comment|// First pass - parse the top-level query recording any PhraseQuerys
comment|// which will need to be resolved
name|complexPhrases
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|Query
name|q
init|=
name|super
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// Perform second pass, using this QueryParser to parse any nested
comment|// PhraseQueries with different
comment|// set of syntax restrictions (i.e. all fields must be same)
name|isPass2ResolvingPhrases
operator|=
literal|true
expr_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
argument_list|<
name|ComplexPhraseQuery
argument_list|>
name|iterator
init|=
name|complexPhrases
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
name|currentPhraseQuery
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// in each phrase, now parse the contents between quotes as a
comment|// separate parse operation
name|currentPhraseQuery
operator|.
name|parsePhraseElements
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isPass2ResolvingPhrases
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
comment|// There is No "getTermQuery throws ParseException" method to override so
comment|// unfortunately need
comment|// to throw a runtime exception here if a term for another field is embedded
comment|// in phrase query
annotation|@
name|Override
DECL|method|newTermQuery
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
try|try
block|{
name|checkPhraseClauseIsForSameField
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error parsing complex phrase"
argument_list|,
name|pe
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|// Helper method used to report on any clauses that appear in query syntax
DECL|method|checkPhraseClauseIsForSameField
specifier|private
name|void
name|checkPhraseClauseIsForSameField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|currentPhraseQuery
operator|.
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Cannot have clause for field \""
operator|+
name|field
operator|+
literal|"\" nested in phrase "
operator|+
literal|" for field \""
operator|+
name|currentPhraseQuery
operator|.
name|field
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getWildcardQuery
specifier|protected
name|Query
name|getWildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
name|checkPhraseClauseIsForSameField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|protected
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
name|checkPhraseClauseIsForSameField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newRangeQuery
specifier|protected
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
comment|// Must use old-style RangeQuery in order to produce a BooleanQuery
comment|// that can be turned into SpanOr clause
name|TermRangeQuery
name|rangeQuery
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
decl_stmt|;
name|rangeQuery
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
return|return
name|rangeQuery
return|;
block|}
return|return
name|super
operator|.
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFuzzyQuery
specifier|protected
name|Query
name|getFuzzyQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|isPass2ResolvingPhrases
condition|)
block|{
name|checkPhraseClauseIsForSameField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
comment|/*    * Used to handle the query content in between quotes and produced Span-based    * interpretations of the clauses.    */
DECL|class|ComplexPhraseQuery
specifier|static
class|class
name|ComplexPhraseQuery
extends|extends
name|Query
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|phrasedQueryStringContents
name|String
name|phrasedQueryStringContents
decl_stmt|;
DECL|field|slopFactor
name|int
name|slopFactor
decl_stmt|;
DECL|field|contents
specifier|private
name|Query
name|contents
decl_stmt|;
DECL|method|ComplexPhraseQuery
specifier|public
name|ComplexPhraseQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|phrasedQueryStringContents
parameter_list|,
name|int
name|slopFactor
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|phrasedQueryStringContents
operator|=
name|phrasedQueryStringContents
expr_stmt|;
name|this
operator|.
name|slopFactor
operator|=
name|slopFactor
expr_stmt|;
block|}
comment|// Called by ComplexPhraseQueryParser for each phrase after the main
comment|// parse
comment|// thread is through
DECL|method|parsePhraseElements
specifier|protected
name|void
name|parsePhraseElements
parameter_list|(
name|QueryParser
name|qp
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// TODO ensure that field-sensitivity is preserved ie the query
comment|// string below is parsed as
comment|// field+":("+phrasedQueryStringContents+")"
comment|// but this will need code in rewrite to unwrap the first layer of
comment|// boolean query
name|contents
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|phrasedQueryStringContents
argument_list|)
expr_stmt|;
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
comment|// ArrayList spanClauses = new ArrayList();
if|if
condition|(
name|contents
operator|instanceof
name|TermQuery
condition|)
block|{
return|return
name|contents
return|;
block|}
comment|// Build a sequence of Span clauses arranged in a SpanNear - child
comment|// clauses can be complex
comment|// Booleans e.g. nots and ors etc
name|int
name|numNegatives
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|contents
operator|instanceof
name|BooleanQuery
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown query type \""
operator|+
name|contents
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"\" found in phrase query string \""
operator|+
name|phrasedQueryStringContents
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|contents
decl_stmt|;
name|BooleanClause
index|[]
name|bclauses
init|=
name|bq
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|allSpanClauses
init|=
operator|new
name|SpanQuery
index|[
name|bclauses
operator|.
name|length
index|]
decl_stmt|;
comment|// For all clauses e.g. one* two~
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bclauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// HashSet bclauseterms=new HashSet();
name|Query
name|qc
init|=
name|bclauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// Rewrite this clause e.g one* becomes (one OR onerous)
name|qc
operator|=
name|qc
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|bclauses
index|[
name|i
index|]
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
condition|)
block|{
name|numNegatives
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|qc
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
name|sc
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|addComplexPhraseClause
argument_list|(
name|sc
argument_list|,
operator|(
name|BooleanQuery
operator|)
name|qc
argument_list|)
expr_stmt|;
if|if
condition|(
name|sc
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|allSpanClauses
index|[
name|i
index|]
operator|=
name|sc
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Insert fake term e.g. phrase query was for "Fred Smithe*" and
comment|// there were no "Smithe*" terms - need to
comment|// prevent match on just "Fred".
name|allSpanClauses
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"Dummy clause because no terms found - must match nothing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|qc
operator|instanceof
name|TermQuery
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|qc
decl_stmt|;
name|allSpanClauses
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown query type \""
operator|+
name|qc
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"\" found in phrase query string \""
operator|+
name|phrasedQueryStringContents
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|numNegatives
operator|==
literal|0
condition|)
block|{
comment|// The simple case - no negative elements in phrase
return|return
operator|new
name|SpanNearQuery
argument_list|(
name|allSpanClauses
argument_list|,
name|slopFactor
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|// Complex case - we have mixed positives and negatives in the
comment|// sequence.
comment|// Need to return a SpanNotQuery
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
name|positiveClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|allSpanClauses
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|bclauses
index|[
name|j
index|]
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
condition|)
block|{
name|positiveClauses
operator|.
name|add
argument_list|(
name|allSpanClauses
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|SpanQuery
index|[]
name|includeClauses
init|=
name|positiveClauses
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|positiveClauses
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|SpanQuery
name|include
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|includeClauses
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|include
operator|=
name|includeClauses
index|[
literal|0
index|]
expr_stmt|;
comment|// only one positive clause
block|}
else|else
block|{
comment|// need to increase slop factor based on gaps introduced by
comment|// negatives
name|include
operator|=
operator|new
name|SpanNearQuery
argument_list|(
name|includeClauses
argument_list|,
name|slopFactor
operator|+
name|numNegatives
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Use sequence of positive and negative values as the exclude.
name|SpanNearQuery
name|exclude
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|allSpanClauses
argument_list|,
name|slopFactor
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanNotQuery
name|snot
init|=
operator|new
name|SpanNotQuery
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|)
decl_stmt|;
return|return
name|snot
return|;
block|}
DECL|method|addComplexPhraseClause
specifier|private
name|void
name|addComplexPhraseClause
parameter_list|(
name|List
argument_list|<
name|SpanQuery
argument_list|>
name|spanClauses
parameter_list|,
name|BooleanQuery
name|qc
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
name|ors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
name|nots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BooleanClause
index|[]
name|bclauses
init|=
name|qc
operator|.
name|getClauses
argument_list|()
decl_stmt|;
comment|// For all clauses e.g. one* two~
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bclauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|childQuery
init|=
name|bclauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// select the list to which we will add these options
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
name|chosenList
init|=
name|ors
decl_stmt|;
if|if
condition|(
name|bclauses
index|[
name|i
index|]
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
condition|)
block|{
name|chosenList
operator|=
name|nots
expr_stmt|;
block|}
if|if
condition|(
name|childQuery
operator|instanceof
name|TermQuery
condition|)
block|{
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|childQuery
decl_stmt|;
name|SpanTermQuery
name|stq
init|=
operator|new
name|SpanTermQuery
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
argument_list|)
decl_stmt|;
name|stq
operator|.
name|setBoost
argument_list|(
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|chosenList
operator|.
name|add
argument_list|(
name|stq
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|childQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|cbq
init|=
operator|(
name|BooleanQuery
operator|)
name|childQuery
decl_stmt|;
name|addComplexPhraseClause
argument_list|(
name|chosenList
argument_list|,
name|cbq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO alternatively could call extract terms here?
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown query type:"
operator|+
name|childQuery
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ors
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|SpanOrQuery
name|soq
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|ors
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|ors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|nots
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|spanClauses
operator|.
name|add
argument_list|(
name|soq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SpanOrQuery
name|snqs
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|nots
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|nots
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNotQuery
name|snq
init|=
operator|new
name|SpanNotQuery
argument_list|(
name|soq
argument_list|,
name|snqs
argument_list|)
decl_stmt|;
name|spanClauses
operator|.
name|add
argument_list|(
name|snq
argument_list|)
expr_stmt|;
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
return|return
literal|"\""
operator|+
name|phrasedQueryStringContents
operator|+
literal|"\""
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
name|field
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|field
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
name|phrasedQueryStringContents
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|phrasedQueryStringContents
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
name|slopFactor
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
block|{
return|return
literal|false
return|;
block|}
name|ComplexPhraseQuery
name|other
init|=
operator|(
name|ComplexPhraseQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|field
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
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|phrasedQueryStringContents
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|phrasedQueryStringContents
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
name|phrasedQueryStringContents
operator|.
name|equals
argument_list|(
name|other
operator|.
name|phrasedQueryStringContents
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|slopFactor
operator|!=
name|other
operator|.
name|slopFactor
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

