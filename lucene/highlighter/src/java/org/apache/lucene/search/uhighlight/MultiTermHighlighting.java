begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
package|;
end_package

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
name|Arrays
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
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|AutomatonQuery
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
name|BoostQuery
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
name|ConstantScoreQuery
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
name|DisjunctionMaxQuery
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
name|FuzzyQuery
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
name|PrefixQuery
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
name|SpanBoostQuery
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
name|SpanMultiTermQueryWrapper
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
name|SpanPositionCheckQuery
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
name|CharsRef
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
name|UnicodeUtil
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
name|automaton
operator|.
name|Automata
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|LevenshteinAutomata
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
name|automaton
operator|.
name|Operations
import|;
end_import

begin_comment
comment|/**  * Support for highlighting multi-term queries.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|MultiTermHighlighting
class|class
name|MultiTermHighlighting
block|{
DECL|method|MultiTermHighlighting
specifier|private
name|MultiTermHighlighting
parameter_list|()
block|{   }
comment|/**    * Extracts MultiTermQueries that match the provided field predicate.    * Returns equivalent automata that will match terms.    */
DECL|method|extractAutomata
specifier|public
specifier|static
name|CharacterRunAutomaton
index|[]
name|extractAutomata
parameter_list|(
name|Query
name|query
parameter_list|,
name|Predicate
argument_list|<
name|String
argument_list|>
name|fieldMatcher
parameter_list|,
name|boolean
name|lookInSpan
parameter_list|,
name|Function
argument_list|<
name|Query
argument_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
argument_list|>
name|preRewriteFunc
parameter_list|)
block|{
comment|// TODO Lucene needs a Query visitor API!  LUCENE-3041
name|List
argument_list|<
name|CharacterRunAutomaton
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Query
argument_list|>
name|customSubQueries
init|=
name|preRewriteFunc
operator|.
name|apply
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|customSubQueries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|sub
range|:
name|customSubQueries
control|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
name|sub
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
operator|(
name|BooleanQuery
operator|)
name|query
control|)
block|{
if|if
condition|(
operator|!
name|clause
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BoostQuery
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|BoostQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|DisjunctionMaxQuery
condition|)
block|{
for|for
control|(
name|Query
name|sub
range|:
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
operator|)
operator|.
name|getDisjuncts
argument_list|()
control|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
name|sub
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanOrQuery
condition|)
block|{
for|for
control|(
name|Query
name|sub
range|:
operator|(
operator|(
name|SpanOrQuery
operator|)
name|query
operator|)
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
name|sub
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanNearQuery
condition|)
block|{
for|for
control|(
name|Query
name|sub
range|:
operator|(
operator|(
name|SpanNearQuery
operator|)
name|query
operator|)
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
name|sub
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanNotQuery
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|SpanNotQuery
operator|)
name|query
operator|)
operator|.
name|getInclude
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanPositionCheckQuery
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|SpanPositionCheckQuery
operator|)
name|query
operator|)
operator|.
name|getMatch
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanBoostQuery
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|SpanBoostQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lookInSpan
operator|&&
name|query
operator|instanceof
name|SpanMultiTermQueryWrapper
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|extractAutomata
argument_list|(
operator|(
operator|(
name|SpanMultiTermQueryWrapper
argument_list|<
name|?
argument_list|>
operator|)
name|query
operator|)
operator|.
name|getWrappedQuery
argument_list|()
argument_list|,
name|fieldMatcher
argument_list|,
name|lookInSpan
argument_list|,
name|preRewriteFunc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PrefixQuery
condition|)
block|{
specifier|final
name|PrefixQuery
name|pq
init|=
operator|(
name|PrefixQuery
operator|)
name|query
decl_stmt|;
name|Term
name|prefix
init|=
name|pq
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMatcher
operator|.
name|test
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Operations
operator|.
name|concatenate
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|pq
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
specifier|final
name|FuzzyQuery
name|fq
init|=
operator|(
name|FuzzyQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|fieldMatcher
operator|.
name|test
argument_list|(
name|fq
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|utf16
init|=
name|fq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
decl_stmt|;
name|int
name|termText
index|[]
init|=
operator|new
name|int
index|[
name|utf16
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|utf16
operator|.
name|length
argument_list|()
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|cp
init|,
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|utf16
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|termText
index|[
name|j
operator|++
index|]
operator|=
name|cp
operator|=
name|utf16
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|termLength
init|=
name|termText
operator|.
name|length
decl_stmt|;
name|int
name|prefixLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|fq
operator|.
name|getPrefixLength
argument_list|()
argument_list|,
name|termLength
argument_list|)
decl_stmt|;
name|String
name|suffix
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|termText
argument_list|,
name|prefixLength
argument_list|,
name|termText
operator|.
name|length
operator|-
name|prefixLength
argument_list|)
decl_stmt|;
name|LevenshteinAutomata
name|builder
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|suffix
argument_list|,
name|fq
operator|.
name|getTranspositions
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|termText
argument_list|,
literal|0
argument_list|,
name|prefixLength
argument_list|)
decl_stmt|;
name|Automaton
name|automaton
init|=
name|builder
operator|.
name|toAutomaton
argument_list|(
name|fq
operator|.
name|getMaxEdits
argument_list|()
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fq
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|TermRangeQuery
condition|)
block|{
specifier|final
name|TermRangeQuery
name|tq
init|=
operator|(
name|TermRangeQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|fieldMatcher
operator|.
name|test
argument_list|(
name|tq
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|CharsRef
name|lowerBound
decl_stmt|;
if|if
condition|(
name|tq
operator|.
name|getLowerTerm
argument_list|()
operator|==
literal|null
condition|)
block|{
name|lowerBound
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|lowerBound
operator|=
operator|new
name|CharsRef
argument_list|(
name|tq
operator|.
name|getLowerTerm
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CharsRef
name|upperBound
decl_stmt|;
if|if
condition|(
name|tq
operator|.
name|getUpperTerm
argument_list|()
operator|==
literal|null
condition|)
block|{
name|upperBound
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|upperBound
operator|=
operator|new
name|CharsRef
argument_list|(
name|tq
operator|.
name|getUpperTerm
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|includeLower
init|=
name|tq
operator|.
name|includesLower
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|includeUpper
init|=
name|tq
operator|.
name|includesUpper
argument_list|()
decl_stmt|;
specifier|final
name|CharsRef
name|scratch
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|final
name|Comparator
argument_list|<
name|CharsRef
argument_list|>
name|comparator
init|=
name|CharsRef
operator|.
name|getUTF16SortedAsUTF8Comparator
argument_list|()
decl_stmt|;
comment|// this is *not* an automaton, but its very simple
name|list
operator|.
name|add
argument_list|(
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Automata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|run
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|scratch
operator|.
name|chars
operator|=
name|s
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|lowerBound
operator|!=
literal|null
condition|)
block|{
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|scratch
argument_list|,
name|lowerBound
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
operator|||
operator|(
operator|!
name|includeLower
operator|&&
name|cmp
operator|==
literal|0
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|upperBound
operator|!=
literal|null
condition|)
block|{
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|scratch
argument_list|,
name|upperBound
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|>
literal|0
operator|||
operator|(
operator|!
name|includeUpper
operator|&&
name|cmp
operator|==
literal|0
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|tq
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|AutomatonQuery
condition|)
block|{
specifier|final
name|AutomatonQuery
name|aq
init|=
operator|(
name|AutomatonQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|fieldMatcher
operator|.
name|test
argument_list|(
name|aq
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|CharacterRunAutomaton
argument_list|(
name|aq
operator|.
name|getAutomaton
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|aq
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|CharacterRunAutomaton
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

