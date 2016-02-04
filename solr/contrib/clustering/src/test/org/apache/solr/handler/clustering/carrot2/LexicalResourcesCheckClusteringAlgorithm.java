begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|carrot2
operator|.
name|core
operator|.
name|Cluster
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
name|IClusteringAlgorithm
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
name|ProcessingComponentBase
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
name|ProcessingException
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
name|AttributeNames
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
name|preprocessing
operator|.
name|pipeline
operator|.
name|BasicPreprocessingPipeline
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
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Output
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
name|Lists
import|;
end_import

begin_comment
comment|/**  * A mock implementation of Carrot2 clustering algorithm for testing whether the  * customized lexical resource lookup works correctly. This algorithm ignores  * the input documents and instead for each word from {@link #wordsToCheck}, it  * outputs a cluster labeled with the word only if the word is neither a stop  * word nor a stop label.  */
end_comment

begin_class
annotation|@
name|Bindable
argument_list|(
name|prefix
operator|=
literal|"LexicalResourcesCheckClusteringAlgorithm"
argument_list|)
DECL|class|LexicalResourcesCheckClusteringAlgorithm
specifier|public
class|class
name|LexicalResourcesCheckClusteringAlgorithm
extends|extends
name|ProcessingComponentBase
implements|implements
name|IClusteringAlgorithm
block|{
annotation|@
name|Output
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|CLUSTERS
argument_list|)
DECL|field|clusters
specifier|private
name|List
argument_list|<
name|Cluster
argument_list|>
name|clusters
decl_stmt|;
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
DECL|field|wordsToCheck
specifier|private
name|String
name|wordsToCheck
decl_stmt|;
DECL|field|preprocessing
specifier|private
name|BasicPreprocessingPipeline
name|preprocessing
init|=
operator|new
name|BasicPreprocessingPipeline
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|ProcessingException
block|{
name|clusters
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
if|if
condition|(
name|wordsToCheck
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Test with Maltese so that the English clustering performed in other tests
comment|// is not affected by the test stopwords and stoplabels.
name|ILexicalData
name|lexicalData
init|=
name|preprocessing
operator|.
name|lexicalDataFactory
operator|.
name|getLexicalData
argument_list|(
name|LanguageCode
operator|.
name|MALTESE
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|wordsToCheck
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|lexicalData
operator|.
name|isCommonWord
argument_list|(
operator|new
name|MutableCharArray
argument_list|(
name|word
argument_list|)
argument_list|)
operator|&&
operator|!
name|lexicalData
operator|.
name|isStopLabel
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|clusters
operator|.
name|add
argument_list|(
operator|new
name|Cluster
argument_list|(
name|word
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

