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
name|AtomicReader
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
name|Collection
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
DECL|field|atomicReader
specifier|private
name|AtomicReader
name|atomicReader
decl_stmt|;
DECL|field|textFieldName
specifier|private
name|String
name|textFieldName
decl_stmt|;
DECL|field|classFieldName
specifier|private
name|String
name|classFieldName
decl_stmt|;
DECL|field|docsWithClassSize
specifier|private
name|int
name|docsWithClassSize
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|indexSearcher
specifier|private
name|IndexSearcher
name|indexSearcher
decl_stmt|;
comment|/**    * Creates a new NaiveBayes classifier.    * Note that you must call {@link #train(AtomicReader, String, String, Analyzer) train()} before you can    * classify any documents.    */
DECL|method|SimpleNaiveBayesClassifier
specifier|public
name|SimpleNaiveBayesClassifier
parameter_list|()
block|{   }
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|train
specifier|public
name|void
name|train
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|atomicReader
operator|=
name|atomicReader
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
name|atomicReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|textFieldName
operator|=
name|textFieldName
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
name|docsWithClassSize
operator|=
name|countDocsWithClass
argument_list|()
expr_stmt|;
block|}
DECL|method|countDocsWithClass
specifier|private
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
name|atomicReader
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
name|indexSearcher
operator|.
name|search
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
DECL|method|tokenizeDoc
specifier|private
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
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|textFieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
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
name|tokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|atomicReader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"You must first call Classifier#train first"
argument_list|)
throw|;
block|}
name|double
name|max
init|=
literal|0d
decl_stmt|;
name|BytesRef
name|foundClass
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|atomicReader
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
argument_list|(
literal|null
argument_list|)
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
comment|// TODO : turn it to be in log scale
name|double
name|clVal
init|=
name|calculatePrior
argument_list|(
name|next
argument_list|)
operator|*
name|calculateLikelihood
argument_list|(
name|tokenizedDoc
argument_list|,
name|next
argument_list|)
decl_stmt|;
if|if
condition|(
name|clVal
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|clVal
expr_stmt|;
name|foundClass
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|foundClass
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|calculateLikelihood
specifier|private
name|double
name|calculateLikelihood
parameter_list|(
name|String
index|[]
name|tokenizedDoc
parameter_list|,
name|BytesRef
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// for each word
name|double
name|result
init|=
literal|1d
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
operator|*=
name|wordProbability
expr_stmt|;
block|}
comment|// P(d|c) = P(w1|c)*...*P(wn|c)
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
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|atomicReader
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
name|double
name|avgNumberOfUniqueTerms
init|=
name|numPostings
operator|/
operator|(
name|double
operator|)
name|terms
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
comment|// avg # of unique terms per doc
name|int
name|docsWithC
init|=
name|atomicReader
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
comment|// avg # of unique terms in text field per doc * # docs with c
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
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
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
DECL|method|calculatePrior
specifier|private
name|double
name|calculatePrior
parameter_list|(
name|BytesRef
name|currentClass
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|double
operator|)
name|docCount
argument_list|(
name|currentClass
argument_list|)
operator|/
name|docsWithClassSize
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
name|atomicReader
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

