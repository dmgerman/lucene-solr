begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|LeafReader
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
name|MultiFields
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
name|TotalHitCountCollector
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
name|WildcardQuery
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
comment|/**  * A simplistic Lucene based NaiveBayes classifier, see<code>http://en.wikipedia.org/wiki/Naive_Bayes_classifier</code>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleNaiveBayesClassifier
specifier|public
class|class
name|SimpleNaiveBayesClassifier
implements|implements
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|/**    * {@link org.apache.lucene.index.LeafReader} used to access the {@link org.apache.lucene.classification.Classifier}'s    * index    */
DECL|field|leafReader
specifier|protected
specifier|final
name|LeafReader
name|leafReader
decl_stmt|;
comment|/**    * names of the fields to be used as input text    */
DECL|field|textFieldNames
specifier|protected
specifier|final
name|String
index|[]
name|textFieldNames
decl_stmt|;
comment|/**    * name of the field to be used as a class / category output    */
DECL|field|classFieldName
specifier|protected
specifier|final
name|String
name|classFieldName
decl_stmt|;
comment|/**    * {@link org.apache.lucene.analysis.Analyzer} to be used for tokenizing unseen input text    */
DECL|field|analyzer
specifier|protected
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
comment|/**    * {@link org.apache.lucene.search.IndexSearcher} to run searches on the index for retrieving frequencies    */
DECL|field|indexSearcher
specifier|protected
specifier|final
name|IndexSearcher
name|indexSearcher
decl_stmt|;
comment|/**    * {@link org.apache.lucene.search.Query} used to eventually filter the document set to be used to classify    */
DECL|field|query
specifier|protected
specifier|final
name|Query
name|query
decl_stmt|;
comment|/**    * Creates a new NaiveBayes classifier.    *    * @param leafReader     the reader on the index to be used for classification    * @param analyzer       an {@link Analyzer} used to analyze unseen text    * @param query          a {@link Query} to eventually filter the docs used for training the classifier, or {@code null}    *                       if all the indexed docs should be used    * @param classFieldName the name of the field used as the output for the classifier    * @param textFieldNames the name of the fields used as the inputs for the classifier    */
DECL|method|SimpleNaiveBayesClassifier
specifier|public
name|SimpleNaiveBayesClassifier
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|String
modifier|...
name|textFieldNames
parameter_list|)
block|{
name|this
operator|.
name|leafReader
operator|=
name|leafReader
expr_stmt|;
name|this
operator|.
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|this
operator|.
name|leafReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|textFieldNames
operator|=
name|textFieldNames
expr_stmt|;
name|this
operator|.
name|classFieldName
operator|=
name|classFieldName
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|assignClass
specifier|public
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|assignClass
parameter_list|(
name|String
name|inputDocument
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|assignClassNormalizedList
argument_list|(
name|inputDocument
argument_list|)
decl_stmt|;
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|retval
init|=
literal|null
decl_stmt|;
name|double
name|maxscore
init|=
operator|-
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|element
range|:
name|doclist
control|)
block|{
if|if
condition|(
name|element
operator|.
name|getScore
argument_list|()
operator|>
name|maxscore
condition|)
block|{
name|retval
operator|=
name|element
expr_stmt|;
name|maxscore
operator|=
name|element
operator|.
name|getScore
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|retval
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|assignClassNormalizedList
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|doclist
argument_list|)
expr_stmt|;
return|return
name|doclist
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|doclist
init|=
name|assignClassNormalizedList
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|doclist
argument_list|)
expr_stmt|;
return|return
name|doclist
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|assignClassNormalizedList
specifier|private
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignClassNormalizedList
parameter_list|(
name|String
name|inputDocument
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|dataList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|classFieldName
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|next
decl_stmt|;
name|String
index|[]
name|tokenizedDoc
init|=
name|tokenizeDoc
argument_list|(
name|inputDocument
argument_list|)
decl_stmt|;
name|int
name|docsWithClassSize
init|=
name|countDocsWithClass
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|double
name|clVal
init|=
name|calculateLogPrior
argument_list|(
name|next
argument_list|,
name|docsWithClassSize
argument_list|)
operator|+
name|calculateLogLikelihood
argument_list|(
name|tokenizedDoc
argument_list|,
name|next
argument_list|,
name|docsWithClassSize
argument_list|)
decl_stmt|;
name|dataList
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|next
argument_list|)
argument_list|,
name|clVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// normalization; the values transforms to a 0-1 range
name|ArrayList
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dataList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|dataList
argument_list|)
expr_stmt|;
comment|// this is a negative number closest to 0 = a
name|double
name|smax
init|=
name|dataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getScore
argument_list|()
decl_stmt|;
name|double
name|sumLog
init|=
literal|0
decl_stmt|;
comment|// log(sum(exp(x_n-a)))
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
range|:
name|dataList
control|)
block|{
comment|// getScore-smax<=0 (both negative, smax is the smallest abs()
name|sumLog
operator|+=
name|Math
operator|.
name|exp
argument_list|(
name|cr
operator|.
name|getScore
argument_list|()
operator|-
name|smax
argument_list|)
expr_stmt|;
block|}
comment|// loga=a+log(sum(exp(x_n-a))) = log(sum(exp(x_n)))
name|double
name|loga
init|=
name|smax
decl_stmt|;
name|loga
operator|+=
name|Math
operator|.
name|log
argument_list|(
name|sumLog
argument_list|)
expr_stmt|;
comment|// 1/sum*x = exp(log(x))*1/sum = exp(log(x)-log(sum))
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
range|:
name|dataList
control|)
block|{
name|returnList
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|cr
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|Math
operator|.
name|exp
argument_list|(
name|cr
operator|.
name|getScore
argument_list|()
operator|-
name|loga
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|returnList
return|;
block|}
comment|/**    * count the number of documents in the index having at least a value for the 'class' field    *    * @return the no. of documents having a value for the 'class' field    * @throws IOException if accessing to term vectors or search fails    */
DECL|method|countDocsWithClass
specifier|protected
name|int
name|countDocsWithClass
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|this
operator|.
name|leafReader
argument_list|,
name|this
operator|.
name|classFieldName
argument_list|)
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|docCount
operator|==
operator|-
literal|1
condition|)
block|{
comment|// in case codec doesn't support getDocCount
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|WildcardQuery
operator|.
name|WILDCARD_STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|indexSearcher
operator|.
name|search
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|,
name|totalHitCountCollector
argument_list|)
expr_stmt|;
name|docCount
operator|=
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
block|}
return|return
name|docCount
return|;
block|}
comment|/**    * tokenize a<code>String</code> on this classifier's text fields and analyzer    *    * @param doc the<code>String</code> representing an input text (to be classified)    * @return a<code>String</code> array of the resulting tokens    * @throws IOException if tokenization fails    */
DECL|method|tokenizeDoc
specifier|protected
name|String
index|[]
name|tokenizeDoc
parameter_list|(
name|String
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
control|)
block|{
try|try
init|(
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|textFieldName
argument_list|,
name|doc
argument_list|)
init|)
block|{
name|CharTermAttribute
name|charTermAttribute
init|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|charTermAttribute
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|calculateLogLikelihood
specifier|private
name|double
name|calculateLogLikelihood
parameter_list|(
name|String
index|[]
name|tokenizedDoc
parameter_list|,
name|BytesRef
name|c
parameter_list|,
name|int
name|docsWithClassSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// for each word
name|double
name|result
init|=
literal|0d
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|tokenizedDoc
control|)
block|{
comment|// search with text:word AND class:c
name|int
name|hits
init|=
name|getWordFreqForClass
argument_list|(
name|word
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// num : count the no of times the word appears in documents of class c (+1)
name|double
name|num
init|=
name|hits
operator|+
literal|1
decl_stmt|;
comment|// +1 is added because of add 1 smoothing
comment|// den : for the whole dictionary, count the no of times a word appears in documents of class c (+|V|)
name|double
name|den
init|=
name|getTextTermFreqForClass
argument_list|(
name|c
argument_list|)
operator|+
name|docsWithClassSize
decl_stmt|;
comment|// P(w|c) = num/den
name|double
name|wordProbability
init|=
name|num
operator|/
name|den
decl_stmt|;
name|result
operator|+=
name|Math
operator|.
name|log
argument_list|(
name|wordProbability
argument_list|)
expr_stmt|;
block|}
comment|// log(P(d|c)) = log(P(w1|c))+...+log(P(wn|c))
return|return
name|result
return|;
block|}
DECL|method|getTextTermFreqForClass
specifier|private
name|double
name|getTextTermFreqForClass
parameter_list|(
name|BytesRef
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|avgNumberOfUniqueTerms
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
control|)
block|{
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|long
name|numPostings
init|=
name|terms
operator|.
name|getSumDocFreq
argument_list|()
decl_stmt|;
comment|// number of term/doc pairs
name|avgNumberOfUniqueTerms
operator|+=
name|numPostings
operator|/
operator|(
name|double
operator|)
name|terms
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
comment|// avg # of unique terms per doc
block|}
name|int
name|docsWithC
init|=
name|leafReader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|c
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|avgNumberOfUniqueTerms
operator|*
name|docsWithC
return|;
comment|// avg # of unique terms in text fields per doc * # docs with c
block|}
DECL|method|getWordFreqForClass
specifier|private
name|int
name|getWordFreqForClass
parameter_list|(
name|String
name|word
parameter_list|,
name|BytesRef
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|subQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
control|)
block|{
name|subQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|textFieldName
argument_list|,
name|word
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|subQuery
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|c
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|booleanQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|booleanQuery
operator|.
name|build
argument_list|()
argument_list|,
name|totalHitCountCollector
argument_list|)
expr_stmt|;
return|return
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
return|;
block|}
DECL|method|calculateLogPrior
specifier|private
name|double
name|calculateLogPrior
parameter_list|(
name|BytesRef
name|currentClass
parameter_list|,
name|int
name|docsWithClassSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|log
argument_list|(
operator|(
name|double
operator|)
name|docCount
argument_list|(
name|currentClass
argument_list|)
argument_list|)
operator|-
name|Math
operator|.
name|log
argument_list|(
name|docsWithClassSize
argument_list|)
return|;
block|}
DECL|method|docCount
specifier|private
name|int
name|docCount
parameter_list|(
name|BytesRef
name|countedClass
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|leafReader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|countedClass
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

