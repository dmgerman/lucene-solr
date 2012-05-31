begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|core
operator|.
name|KeywordTokenizer
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

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  *<p>  *   Configures {@link KeywordTokenizer} with {@link CollationAttributeFactory}.  *</p>  *<p>  *   Converts the token into its {@link java.text.CollationKey}, and then  *   encodes the CollationKey directly to allow   *   it to be stored as an index term.  *</p>  *<p>  *<strong>WARNING:</strong> Make sure you use exactly the same Collator at  *   index and query time -- CollationKeys are only comparable when produced by  *   the same Collator.  Since {@link java.text.RuleBasedCollator}s are not  *   independently versioned, it is unsafe to search against stored  *   CollationKeys unless the following are exactly the same (best practice is  *   to store this information with the index and check that they remain the  *   same at query time):  *</p>  *<ol>  *<li>JVM vendor</li>  *<li>JVM version, including patch version</li>  *<li>  *     The language (and country and variant, if specified) of the Locale  *     used when constructing the collator via  *     {@link Collator#getInstance(java.util.Locale)}.  *</li>  *<li>  *     The collation strength used - see {@link Collator#setStrength(int)}  *</li>  *</ol>   *<p>  *   The<code>ICUCollationKeyAnalyzer</code> in the analysis-icu package   *   uses ICU4J's Collator, which makes its  *   its version available, thus allowing collation to be versioned  *   independently from the JVM.  ICUCollationKeyAnalyzer is also significantly  *   faster and generates significantly shorter keys than CollationKeyAnalyzer.  *   See<a href="http://site.icu-project.org/charts/collation-icu4j-sun"  *>http://site.icu-project.org/charts/collation-icu4j-sun</a> for key  *   generation timing and key length comparisons between ICU4J and  *   java.text.Collator over several languages.  *</p>  *<p>  *   CollationKeys generated by java.text.Collators are not compatible  *   with those those generated by ICU Collators.  Specifically, if you use   *   CollationKeyAnalyzer to generate index terms, do not use  *   ICUCollationKeyAnalyzer on the query side, or vice versa.  *</p>  */
end_comment

begin_class
DECL|class|CollationKeyAnalyzer
specifier|public
specifier|final
class|class
name|CollationKeyAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|factory
specifier|private
specifier|final
name|CollationAttributeFactory
name|factory
decl_stmt|;
comment|/**    * Create a new CollationKeyAnalyzer, using the specified collator.    *     * @param matchVersion compatibility version    * @param collator CollationKey generator    */
DECL|method|CollationKeyAnalyzer
specifier|public
name|CollationKeyAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
operator|new
name|CollationAttributeFactory
argument_list|(
name|collator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|KeywordTokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|factory
argument_list|,
name|reader
argument_list|,
name|KeywordTokenizer
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

