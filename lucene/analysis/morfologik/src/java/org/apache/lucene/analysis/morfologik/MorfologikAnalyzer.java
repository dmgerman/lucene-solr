begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// -*- c-basic-offset: 2 -*-
end_comment

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|morfologik
operator|.
name|stemming
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|morfologik
operator|.
name|stemming
operator|.
name|polish
operator|.
name|PolishStemmer
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
name|Tokenizer
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.lucene.analysis.Analyzer} using Morfologik library.  * @see<a href="http://morfologik.blogspot.com/">Morfologik project page</a>  */
end_comment

begin_class
DECL|class|MorfologikAnalyzer
specifier|public
class|class
name|MorfologikAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|dictionary
specifier|private
specifier|final
name|Dictionary
name|dictionary
decl_stmt|;
comment|/**    * Builds an analyzer with an explicit {@link Dictionary} resource.    *     * @param dictionary A prebuilt automaton with inflected and base word forms.    * @see<a href="https://github.com/morfologik/">https://github.com/morfologik/</a>    */
DECL|method|MorfologikAnalyzer
specifier|public
name|MorfologikAnalyzer
parameter_list|(
specifier|final
name|Dictionary
name|dictionary
parameter_list|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the default Morfologik's Polish dictionary.    */
DECL|method|MorfologikAnalyzer
specifier|public
name|MorfologikAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|PolishStemmer
argument_list|()
operator|.
name|getDictionary
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a    * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    * which tokenizes all the text in the provided {@link Reader}.    *     * @param field ignored field name    * @return A {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    *         built from an {@link StandardTokenizer} filtered with    *         {@link StandardFilter} and {@link MorfologikFilter}.    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
operator|new
name|MorfologikFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
name|src
argument_list|)
argument_list|,
name|dictionary
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|TokenStream
name|normalize
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStream
name|in
parameter_list|)
block|{
return|return
operator|new
name|StandardFilter
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

