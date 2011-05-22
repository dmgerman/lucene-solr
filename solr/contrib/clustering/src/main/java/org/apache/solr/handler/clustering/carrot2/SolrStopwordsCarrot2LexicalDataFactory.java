begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|CommonGramsFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|StopFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|TokenFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|TokenizerChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|LanguageCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|Init
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|Processing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|DefaultLexicalDataFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|ILexicalData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|ILexicalDataFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|util
operator|.
name|MutableCharArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Bindable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashMultimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_comment
comment|/**  * An implementation of Carrot2's {@link ILexicalDataFactory} that adds stop  * words from a field's StopFilter to the default stop words used in Carrot2,  * for all languages Carrot2 supports. Completely replacing Carrot2 stop words  * with Solr's wouldn't make much sense because clustering needs more aggressive  * stop words removal. In other words, if something is a stop word during  * indexing, then it should also be a stop word during clustering, but not the  * other way round.  */
end_comment

begin_class
annotation|@
name|Bindable
DECL|class|SolrStopwordsCarrot2LexicalDataFactory
specifier|public
class|class
name|SolrStopwordsCarrot2LexicalDataFactory
implements|implements
name|ILexicalDataFactory
block|{
DECL|field|logger
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrStopwordsCarrot2LexicalDataFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Init
annotation|@
name|Input
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
literal|"solrIndexSchema"
argument_list|)
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
annotation|@
name|Processing
annotation|@
name|Input
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
literal|"solrFieldNames"
argument_list|)
DECL|field|fieldNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
comment|/** 	 * A lazily-built cache of stop words per field. 	 */
DECL|field|solrStopWords
specifier|private
name|Multimap
argument_list|<
name|String
argument_list|,
name|CharArraySet
argument_list|>
name|solrStopWords
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/** 	 * Carrot2's default lexical resources to use in addition to Solr's stop 	 * words. 	 */
DECL|field|carrot2LexicalDataFactory
specifier|private
name|DefaultLexicalDataFactory
name|carrot2LexicalDataFactory
init|=
operator|new
name|DefaultLexicalDataFactory
argument_list|()
decl_stmt|;
comment|/** 	 * Obtains stop words for a field from the associated 	 * {@link StopFilterFactory}, if any. 	 */
DECL|method|getSolrStopWordsForField
specifier|private
name|Collection
argument_list|<
name|CharArraySet
argument_list|>
name|getSolrStopWordsForField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// No need to synchronize here, Carrot2 ensures that instances
comment|// of this class are not used by multiple threads at a time.
if|if
condition|(
operator|!
name|solrStopWords
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
specifier|final
name|Analyzer
name|fieldAnalyzer
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldAnalyzer
operator|instanceof
name|TokenizerChain
condition|)
block|{
specifier|final
name|TokenFilterFactory
index|[]
name|filterFactories
init|=
operator|(
operator|(
name|TokenizerChain
operator|)
name|fieldAnalyzer
operator|)
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|factory
range|:
name|filterFactories
control|)
block|{
if|if
condition|(
name|factory
operator|instanceof
name|StopFilterFactory
condition|)
block|{
comment|// StopFilterFactory holds the stop words in a CharArraySet, but
comment|// the getStopWords() method returns a Set<?>, so we need to cast.
name|solrStopWords
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
call|(
name|CharArraySet
call|)
argument_list|(
operator|(
name|StopFilterFactory
operator|)
name|factory
argument_list|)
operator|.
name|getStopWords
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|factory
operator|instanceof
name|CommonGramsFilterFactory
condition|)
block|{
name|solrStopWords
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
call|(
name|CharArraySet
call|)
argument_list|(
operator|(
name|CommonGramsFilterFactory
operator|)
name|factory
argument_list|)
operator|.
name|getCommonWords
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|solrStopWords
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLexicalData
specifier|public
name|ILexicalData
name|getLexicalData
parameter_list|(
name|LanguageCode
name|languageCode
parameter_list|)
block|{
specifier|final
name|ILexicalData
name|carrot2LexicalData
init|=
name|carrot2LexicalDataFactory
operator|.
name|getLexicalData
argument_list|(
name|languageCode
argument_list|)
decl_stmt|;
return|return
operator|new
name|ILexicalData
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isStopLabel
parameter_list|(
name|CharSequence
name|word
parameter_list|)
block|{
comment|// Nothing in Solr maps to the concept of a stop label,
comment|// so return Carrot2's default here.
return|return
name|carrot2LexicalData
operator|.
name|isStopLabel
argument_list|(
name|word
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCommonWord
parameter_list|(
name|MutableCharArray
name|word
parameter_list|)
block|{
comment|// Loop over the fields involved in clustering first
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
for|for
control|(
name|CharArraySet
name|stopWords
range|:
name|getSolrStopWordsForField
argument_list|(
name|fieldName
argument_list|)
control|)
block|{
if|if
condition|(
name|stopWords
operator|.
name|contains
argument_list|(
name|word
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|// Check default Carrot2 stop words too
return|return
name|carrot2LexicalData
operator|.
name|isCommonWord
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

