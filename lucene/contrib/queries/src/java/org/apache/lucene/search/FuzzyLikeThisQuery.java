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
name|StringReader
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
name|HashMap
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
name|analysis
operator|.
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|AttributeSource
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * Fuzzifies ALL terms provided as strings and then picks the best n differentiating terms.  * In effect this mixes the behaviour of FuzzyQuery and MoreLikeThis but with special consideration  * of fuzzy scoring factors.  * This generally produces good results for queries where users may provide details in a number of   * fields and have no knowledge of boolean query syntax and also want a degree of fuzzy matching and  * a fast query.  *   * For each source term the fuzzy variants are held in a BooleanQuery with no coord factor (because  * we are not looking for matches on multiple variants in any one doc). Additionally, a specialized  * TermQuery is used for variants and does not use that variant term's IDF because this would favour rarer   * terms eg misspellings. Instead, all variants use the same IDF ranking (the one for the source query   * term) and this is factored into the variant's boost. If the source query term does not exist in the  * index the average IDF of the variants is used.  */
end_comment

begin_class
DECL|class|FuzzyLikeThisQuery
specifier|public
class|class
name|FuzzyLikeThisQuery
extends|extends
name|Query
block|{
DECL|field|sim
specifier|static
name|Similarity
name|sim
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
DECL|field|rewrittenQuery
name|Query
name|rewrittenQuery
init|=
literal|null
decl_stmt|;
DECL|field|fieldVals
name|ArrayList
argument_list|<
name|FieldVals
argument_list|>
name|fieldVals
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldVals
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|q
name|ScoreTermQueue
name|q
decl_stmt|;
DECL|field|MAX_VARIANTS_PER_TERM
name|int
name|MAX_VARIANTS_PER_TERM
init|=
literal|50
decl_stmt|;
DECL|field|ignoreTF
name|boolean
name|ignoreTF
init|=
literal|false
decl_stmt|;
DECL|field|maxNumTerms
specifier|private
name|int
name|maxNumTerms
decl_stmt|;
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
operator|(
operator|(
name|analyzer
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|analyzer
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
name|fieldVals
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fieldVals
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
name|ignoreTF
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|maxNumTerms
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
name|FuzzyLikeThisQuery
name|other
init|=
operator|(
name|FuzzyLikeThisQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|analyzer
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
name|analyzer
operator|.
name|equals
argument_list|(
name|other
operator|.
name|analyzer
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|fieldVals
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|fieldVals
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
name|fieldVals
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldVals
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|ignoreTF
operator|!=
name|other
operator|.
name|ignoreTF
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|maxNumTerms
operator|!=
name|other
operator|.
name|maxNumTerms
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/**      *       * @param maxNumTerms The total number of terms clauses that will appear once rewritten as a BooleanQuery      * @param analyzer      */
DECL|method|FuzzyLikeThisQuery
specifier|public
name|FuzzyLikeThisQuery
parameter_list|(
name|int
name|maxNumTerms
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|q
operator|=
operator|new
name|ScoreTermQueue
argument_list|(
name|maxNumTerms
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|maxNumTerms
operator|=
name|maxNumTerms
expr_stmt|;
block|}
DECL|class|FieldVals
class|class
name|FieldVals
block|{
DECL|field|queryString
name|String
name|queryString
decl_stmt|;
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|minSimilarity
name|float
name|minSimilarity
decl_stmt|;
DECL|field|prefixLength
name|int
name|prefixLength
decl_stmt|;
DECL|method|FieldVals
specifier|public
name|FieldVals
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|similarity
parameter_list|,
name|int
name|length
parameter_list|,
name|String
name|queryString
parameter_list|)
block|{
name|fieldName
operator|=
name|name
expr_stmt|;
name|minSimilarity
operator|=
name|similarity
expr_stmt|;
name|prefixLength
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|queryString
operator|=
name|queryString
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
operator|(
operator|(
name|fieldName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fieldName
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
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minSimilarity
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|prefixLength
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|queryString
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|queryString
operator|.
name|hashCode
argument_list|()
operator|)
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
name|FieldVals
name|other
init|=
operator|(
name|FieldVals
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|fieldName
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
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minSimilarity
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|minSimilarity
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|prefixLength
operator|!=
name|other
operator|.
name|prefixLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|queryString
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|queryString
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
name|queryString
operator|.
name|equals
argument_list|(
name|other
operator|.
name|queryString
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Adds user input for "fuzzification"       * @param queryString The string which will be parsed by the analyzer and for which fuzzy variants will be parsed      * @param fieldName      * @param minSimilarity The minimum similarity of the term variants (see FuzzyTermsEnum)      * @param prefixLength Length of required common prefix on variant terms (see FuzzyTermsEnum)      */
DECL|method|addTerms
specifier|public
name|void
name|addTerms
parameter_list|(
name|String
name|queryString
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|float
name|minSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|)
block|{
name|fieldVals
operator|.
name|add
argument_list|(
operator|new
name|FieldVals
argument_list|(
name|fieldName
argument_list|,
name|minSimilarity
argument_list|,
name|prefixLength
argument_list|,
name|queryString
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addTerms
specifier|private
name|void
name|addTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|FieldVals
name|f
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|f
operator|.
name|queryString
operator|==
literal|null
condition|)
return|return;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|f
operator|.
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|f
operator|.
name|queryString
argument_list|)
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|corpusNumDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|Term
name|internSavingTemplateTerm
init|=
operator|new
name|Term
argument_list|(
name|f
operator|.
name|fieldName
argument_list|)
decl_stmt|;
comment|//optimization to avoid constructing new Term() objects
name|HashSet
argument_list|<
name|String
argument_list|>
name|processedTerms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|term
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|processedTerms
operator|.
name|contains
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|processedTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|ScoreTermQueue
name|variantsQ
init|=
operator|new
name|ScoreTermQueue
argument_list|(
name|MAX_VARIANTS_PER_TERM
argument_list|)
decl_stmt|;
comment|//maxNum variants considered for any one term
name|float
name|minScore
init|=
literal|0
decl_stmt|;
name|Term
name|startTerm
init|=
name|internSavingTemplateTerm
operator|.
name|createTerm
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|AttributeSource
name|atts
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|MaxNonCompetitiveBoostAttribute
name|maxBoostAtt
init|=
name|atts
operator|.
name|addAttribute
argument_list|(
name|MaxNonCompetitiveBoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|FuzzyTermsEnum
name|fe
init|=
operator|new
name|FuzzyTermsEnum
argument_list|(
name|reader
argument_list|,
name|atts
argument_list|,
name|startTerm
argument_list|,
name|f
operator|.
name|minSimilarity
argument_list|,
name|f
operator|.
name|prefixLength
argument_list|)
decl_stmt|;
comment|//store the df so all variants use same idf
name|int
name|df
init|=
name|reader
operator|.
name|docFreq
argument_list|(
name|startTerm
argument_list|)
decl_stmt|;
name|int
name|numVariants
init|=
literal|0
decl_stmt|;
name|int
name|totalVariantDocFreqs
init|=
literal|0
decl_stmt|;
name|BytesRef
name|possibleMatch
decl_stmt|;
name|BoostAttribute
name|boostAtt
init|=
name|fe
operator|.
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|possibleMatch
operator|=
name|fe
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|possibleMatch
operator|!=
literal|null
condition|)
block|{
name|numVariants
operator|++
expr_stmt|;
name|totalVariantDocFreqs
operator|+=
name|fe
operator|.
name|docFreq
argument_list|()
expr_stmt|;
name|float
name|score
init|=
name|boostAtt
operator|.
name|getBoost
argument_list|()
decl_stmt|;
if|if
condition|(
name|variantsQ
operator|.
name|size
argument_list|()
operator|<
name|MAX_VARIANTS_PER_TERM
operator|||
name|score
operator|>
name|minScore
condition|)
block|{
name|ScoreTerm
name|st
init|=
operator|new
name|ScoreTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|startTerm
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|possibleMatch
argument_list|)
argument_list|)
argument_list|,
name|score
argument_list|,
name|startTerm
argument_list|)
decl_stmt|;
name|variantsQ
operator|.
name|insertWithOverflow
argument_list|(
name|st
argument_list|)
expr_stmt|;
name|minScore
operator|=
name|variantsQ
operator|.
name|top
argument_list|()
operator|.
name|score
expr_stmt|;
comment|// maintain minScore
block|}
name|maxBoostAtt
operator|.
name|setMaxNonCompetitiveBoost
argument_list|(
name|variantsQ
operator|.
name|size
argument_list|()
operator|>=
name|MAX_VARIANTS_PER_TERM
condition|?
name|minScore
else|:
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numVariants
operator|>
literal|0
condition|)
block|{
name|int
name|avgDf
init|=
name|totalVariantDocFreqs
operator|/
name|numVariants
decl_stmt|;
if|if
condition|(
name|df
operator|==
literal|0
condition|)
comment|//no direct match we can use as df for all variants
block|{
name|df
operator|=
name|avgDf
expr_stmt|;
comment|//use avg df of all variants
block|}
comment|// take the top variants (scored by edit distance) and reset the score
comment|// to include an IDF factor then add to the global queue for ranking
comment|// overall top query terms
name|int
name|size
init|=
name|variantsQ
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ScoreTerm
name|st
init|=
name|variantsQ
operator|.
name|pop
argument_list|()
decl_stmt|;
name|st
operator|.
name|score
operator|=
operator|(
name|st
operator|.
name|score
operator|*
name|st
operator|.
name|score
operator|)
operator|*
name|sim
operator|.
name|idf
argument_list|(
name|df
argument_list|,
name|corpusNumDocs
argument_list|)
expr_stmt|;
name|q
operator|.
name|insertWithOverflow
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
if|if
condition|(
name|rewrittenQuery
operator|!=
literal|null
condition|)
block|{
return|return
name|rewrittenQuery
return|;
block|}
comment|//load up the list of possible terms
for|for
control|(
name|Iterator
argument_list|<
name|FieldVals
argument_list|>
name|iter
init|=
name|fieldVals
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|FieldVals
name|f
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|addTerms
argument_list|(
name|reader
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
comment|//clear the list of fields
name|fieldVals
operator|.
name|clear
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|//create BooleanQueries to hold the variants for each token/field pair and ensure it
comment|// has no coord factor
comment|//Step 1: sort the termqueries by term/field
name|HashMap
argument_list|<
name|Term
argument_list|,
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|>
name|variantQueries
init|=
operator|new
name|HashMap
argument_list|<
name|Term
argument_list|,
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|q
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ScoreTerm
name|st
init|=
name|q
operator|.
name|pop
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
name|l
init|=
name|variantQueries
operator|.
name|get
argument_list|(
name|st
operator|.
name|fuzziedSourceTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|()
expr_stmt|;
name|variantQueries
operator|.
name|put
argument_list|(
name|st
operator|.
name|fuzziedSourceTerm
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
comment|//Step 2: Organize the sorted termqueries into zero-coord scoring boolean queries
for|for
control|(
name|Iterator
argument_list|<
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|>
name|iter
init|=
name|variantQueries
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ArrayList
argument_list|<
name|ScoreTerm
argument_list|>
name|variants
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|variants
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|//optimize where only one selected variant
name|ScoreTerm
name|st
init|=
name|variants
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|FuzzyTermQuery
argument_list|(
name|st
operator|.
name|term
argument_list|,
name|ignoreTF
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|st
operator|.
name|score
argument_list|)
expr_stmt|;
comment|// set the boost to a mix of IDF and score
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
else|else
block|{
name|BooleanQuery
name|termVariants
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|//disable coord and IDF for these term variants
for|for
control|(
name|Iterator
argument_list|<
name|ScoreTerm
argument_list|>
name|iterator2
init|=
name|variants
operator|.
name|iterator
argument_list|()
init|;
name|iterator2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ScoreTerm
name|st
init|=
name|iterator2
operator|.
name|next
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|FuzzyTermQuery
argument_list|(
name|st
operator|.
name|term
argument_list|,
name|ignoreTF
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|st
operator|.
name|score
argument_list|)
expr_stmt|;
comment|// set the boost using the ScoreTerm's score
name|termVariants
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
block|}
name|bq
operator|.
name|add
argument_list|(
name|termVariants
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// add to query
block|}
block|}
comment|//TODO possible alternative step 3 - organize above booleans into a new layer of field-based
comment|// booleans with a minimum-should-match of NumFields-1?
name|bq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rewrittenQuery
operator|=
name|bq
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|//Holds info for a fuzzy term variant - initially score is set to edit distance (for ranking best
comment|// term variants) then is reset with IDF for use in ranking against all other
comment|// terms/fields
DECL|class|ScoreTerm
specifier|private
specifier|static
class|class
name|ScoreTerm
block|{
DECL|field|term
specifier|public
name|Term
name|term
decl_stmt|;
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|field|fuzziedSourceTerm
name|Term
name|fuzziedSourceTerm
decl_stmt|;
DECL|method|ScoreTerm
specifier|public
name|ScoreTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|score
parameter_list|,
name|Term
name|fuzziedSourceTerm
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
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|fuzziedSourceTerm
operator|=
name|fuzziedSourceTerm
expr_stmt|;
block|}
block|}
DECL|class|ScoreTermQueue
specifier|private
specifier|static
class|class
name|ScoreTermQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
block|{
DECL|method|ScoreTermQueue
specifier|public
name|ScoreTermQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see org.apache.lucene.util.PriorityQueue#lessThan(java.lang.Object, java.lang.Object)          */
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|ScoreTerm
name|termA
parameter_list|,
name|ScoreTerm
name|termB
parameter_list|)
block|{
if|if
condition|(
name|termA
operator|.
name|score
operator|==
name|termB
operator|.
name|score
condition|)
return|return
name|termA
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|termB
operator|.
name|term
argument_list|)
operator|>
literal|0
return|;
else|else
return|return
name|termA
operator|.
name|score
operator|<
name|termB
operator|.
name|score
return|;
block|}
block|}
comment|//overrides basic TermQuery to negate effects of IDF (idf is factored into boost of containing BooleanQuery)
DECL|class|FuzzyTermQuery
specifier|private
specifier|static
class|class
name|FuzzyTermQuery
extends|extends
name|TermQuery
block|{
DECL|field|ignoreTF
name|boolean
name|ignoreTF
decl_stmt|;
DECL|method|FuzzyTermQuery
specifier|public
name|FuzzyTermQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|boolean
name|ignoreTF
parameter_list|)
block|{
name|super
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|this
operator|.
name|ignoreTF
operator|=
name|ignoreTF
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
name|Similarity
name|result
init|=
name|super
operator|.
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|SimilarityDelegator
argument_list|(
name|result
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
if|if
condition|(
name|ignoreTF
condition|)
block|{
return|return
literal|1
return|;
comment|//ignore tf
block|}
return|return
name|super
operator|.
name|tf
argument_list|(
name|freq
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
comment|//IDF is already factored into individual term boosts
return|return
literal|1
return|;
block|}
block|}
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.apache.lucene.search.Query#toString(java.lang.String)      */
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
literal|null
return|;
block|}
DECL|method|isIgnoreTF
specifier|public
name|boolean
name|isIgnoreTF
parameter_list|()
block|{
return|return
name|ignoreTF
return|;
block|}
DECL|method|setIgnoreTF
specifier|public
name|void
name|setIgnoreTF
parameter_list|(
name|boolean
name|ignoreTF
parameter_list|)
block|{
name|this
operator|.
name|ignoreTF
operator|=
name|ignoreTF
expr_stmt|;
block|}
block|}
end_class

end_unit

