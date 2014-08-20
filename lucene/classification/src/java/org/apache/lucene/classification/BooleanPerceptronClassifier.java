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
name|StorableField
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
name|StoredDocument
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
name|ScoreDoc
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
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
name|IntsRef
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
name|IntsRefBuilder
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * A perceptron (see<code>http://en.wikipedia.org/wiki/Perceptron</code>) based  *<code>Boolean</code> {@link org.apache.lucene.classification.Classifier}. The  * weights are calculated using  * {@link org.apache.lucene.index.TermsEnum#totalTermFreq} both on a per field  * and a per document basis and then a corresponding  * {@link org.apache.lucene.util.fst.FST} is used for class assignment.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|BooleanPerceptronClassifier
specifier|public
class|class
name|BooleanPerceptronClassifier
implements|implements
name|Classifier
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|threshold
specifier|private
name|Double
name|threshold
decl_stmt|;
DECL|field|batchSize
specifier|private
specifier|final
name|Integer
name|batchSize
decl_stmt|;
DECL|field|textTerms
specifier|private
name|Terms
name|textTerms
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|textFieldName
specifier|private
name|String
name|textFieldName
decl_stmt|;
DECL|field|fst
specifier|private
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
comment|/**    * Create a {@link BooleanPerceptronClassifier}    *     * @param threshold    *          the binary threshold for perceptron output evaluation    */
DECL|method|BooleanPerceptronClassifier
specifier|public
name|BooleanPerceptronClassifier
parameter_list|(
name|Double
name|threshold
parameter_list|,
name|Integer
name|batchSize
parameter_list|)
block|{
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
comment|/**    * Default constructor, no batch updates of FST, perceptron threshold is    * calculated via underlying index metrics during    * {@link #train(org.apache.lucene.index.AtomicReader, String, String, org.apache.lucene.analysis.Analyzer)    * training}    */
DECL|method|BooleanPerceptronClassifier
specifier|public
name|BooleanPerceptronClassifier
parameter_list|()
block|{
name|batchSize
operator|=
literal|1
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|assignClass
specifier|public
name|ClassificationResult
argument_list|<
name|Boolean
argument_list|>
name|assignClass
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|textTerms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"You must first call Classifier#train"
argument_list|)
throw|;
block|}
name|Long
name|output
init|=
literal|0l
decl_stmt|;
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
name|text
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
name|String
name|s
init|=
name|charTermAttribute
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Long
name|d
init|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|output
operator|+=
name|d
expr_stmt|;
block|}
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|output
operator|>=
name|threshold
argument_list|,
name|output
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
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
name|train
argument_list|(
name|atomicReader
argument_list|,
name|textFieldName
argument_list|,
name|classFieldName
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|textTerms
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|atomicReader
argument_list|,
name|textFieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|textTerms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"term vectors need to be available for field "
operator|+
name|textFieldName
argument_list|)
throw|;
block|}
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|textFieldName
operator|=
name|textFieldName
expr_stmt|;
if|if
condition|(
name|threshold
operator|==
literal|null
operator|||
name|threshold
operator|==
literal|0d
condition|)
block|{
comment|// automatic assign a threshold
name|long
name|sumDocFreq
init|=
name|atomicReader
operator|.
name|getSumDocFreq
argument_list|(
name|textFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sumDocFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|threshold
operator|=
operator|(
name|double
operator|)
name|sumDocFreq
operator|/
literal|2d
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"threshold cannot be assigned since term vectors for field "
operator|+
name|textFieldName
operator|+
literal|" do not exist"
argument_list|)
throw|;
block|}
block|}
comment|// TODO : remove this map as soon as we have a writable FST
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|weights
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|TermsEnum
name|reuse
init|=
name|textTerms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|textTerm
decl_stmt|;
while|while
condition|(
operator|(
name|textTerm
operator|=
name|reuse
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|weights
operator|.
name|put
argument_list|(
name|textTerm
operator|.
name|utf8ToString
argument_list|()
argument_list|,
operator|(
name|double
operator|)
name|reuse
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|updateFST
argument_list|(
name|weights
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|atomicReader
argument_list|)
decl_stmt|;
name|int
name|batchCount
init|=
literal|0
decl_stmt|;
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
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
literal|"*"
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
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// run the search and use stored field values
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|indexSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|scoreDocs
control|)
block|{
name|StoredDocument
name|doc
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
comment|// assign class to the doc
name|ClassificationResult
argument_list|<
name|Boolean
argument_list|>
name|classificationResult
init|=
name|assignClass
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|textFieldName
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
name|Boolean
name|assignedClass
init|=
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
decl_stmt|;
comment|// get the expected result
name|StorableField
name|field
init|=
name|doc
operator|.
name|getField
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
name|Boolean
name|correctClass
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|modifier
init|=
name|correctClass
operator|.
name|compareTo
argument_list|(
name|assignedClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|modifier
operator|!=
literal|0
condition|)
block|{
name|reuse
operator|=
name|updateWeights
argument_list|(
name|atomicReader
argument_list|,
name|reuse
argument_list|,
name|scoreDoc
operator|.
name|doc
argument_list|,
name|assignedClass
argument_list|,
name|weights
argument_list|,
name|modifier
argument_list|,
name|batchCount
operator|%
name|batchSize
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|batchCount
operator|++
expr_stmt|;
block|}
name|weights
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// free memory while waiting for GC
block|}
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
index|[]
name|textFieldNames
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"training with multiple fields not supported by boolean perceptron classifier"
argument_list|)
throw|;
block|}
DECL|method|updateWeights
specifier|private
name|TermsEnum
name|updateWeights
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|TermsEnum
name|reuse
parameter_list|,
name|int
name|docId
parameter_list|,
name|Boolean
name|assignedClass
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|weights
parameter_list|,
name|double
name|modifier
parameter_list|,
name|boolean
name|updateFST
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|cte
init|=
name|textTerms
operator|.
name|iterator
argument_list|(
name|reuse
argument_list|)
decl_stmt|;
comment|// get the doc term vectors
name|Terms
name|terms
init|=
name|atomicReader
operator|.
name|getTermVector
argument_list|(
name|docId
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"term vectors must be stored for field "
operator|+
name|textFieldName
argument_list|)
throw|;
block|}
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
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
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
name|cte
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|assignedClass
operator|!=
literal|null
condition|)
block|{
name|long
name|termFreqLocal
init|=
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
comment|// update weights
name|Long
name|previousValue
init|=
name|Util
operator|.
name|get
argument_list|(
name|fst
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|String
name|termString
init|=
name|term
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
name|termString
argument_list|,
name|previousValue
operator|+
name|modifier
operator|*
name|termFreqLocal
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|updateFST
condition|)
block|{
name|updateFST
argument_list|(
name|weights
argument_list|)
expr_stmt|;
block|}
name|reuse
operator|=
name|cte
expr_stmt|;
return|return
name|reuse
return|;
block|}
DECL|method|updateFST
specifier|private
name|void
name|updateFST
parameter_list|(
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|weights
parameter_list|)
throws|throws
name|IOException
block|{
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|Builder
argument_list|<
name|Long
argument_list|>
name|fstBuilder
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratchBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|IntsRefBuilder
name|scratchInts
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|entry
range|:
name|weights
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|scratchBytes
operator|.
name|copyChars
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|fstBuilder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|scratchBytes
operator|.
name|get
argument_list|()
argument_list|,
name|scratchInts
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fst
operator|=
name|fstBuilder
operator|.
name|finish
argument_list|()
expr_stmt|;
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
return|return
literal|null
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

