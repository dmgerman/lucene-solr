begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|collation
operator|.
name|tokenattributes
operator|.
name|ICUCollatedTermAttributeImpl
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
name|AttributeFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_comment
comment|/**  *<p>  *   Converts each token into its {@link com.ibm.icu.text.CollationKey}, and  *   then encodes bytes as an index term.  *</p>  *<p>  *<strong>WARNING:</strong> Make sure you use exactly the same Collator at  *   index and query time -- CollationKeys are only comparable when produced by  *   the same Collator.  {@link com.ibm.icu.text.RuleBasedCollator}s are   *   independently versioned, so it is safe to search against stored  *   CollationKeys if the following are exactly the same (best practice is  *   to store this information with the index and check that they remain the  *   same at query time):  *</p>  *<ol>  *<li>  *     Collator version - see {@link Collator#getVersion()}  *</li>  *<li>  *     The collation strength used - see {@link Collator#setStrength(int)}  *</li>  *</ol>   *<p>  *   CollationKeys generated by ICU Collators are not compatible with those  *   generated by java.text.Collators.  Specifically, if you use   *   ICUCollationAttributeFactory to generate index terms, do not use   *   {@link CollationAttributeFactory} on the query side, or vice versa.  *</p>  *<p>  *   ICUCollationAttributeFactory is significantly faster and generates significantly  *   shorter keys than CollationAttributeFactory.  See  *<a href="http://site.icu-project.org/charts/collation-icu4j-sun"  *>http://site.icu-project.org/charts/collation-icu4j-sun</a> for key  *   generation timing and key length comparisons between ICU4J and  *   java.text.Collator over several languages.  *</p>  */
end_comment

begin_class
DECL|class|ICUCollationAttributeFactory
specifier|public
class|class
name|ICUCollationAttributeFactory
extends|extends
name|AttributeFactory
operator|.
name|StaticImplementationAttributeFactory
argument_list|<
name|ICUCollatedTermAttributeImpl
argument_list|>
block|{
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
comment|/**    * Create an ICUCollationAttributeFactory, using     * {@link TokenStream#DEFAULT_TOKEN_ATTRIBUTE_FACTORY} as the    * factory for all other attributes.    * @param collator CollationKey generator    */
DECL|method|ICUCollationAttributeFactory
specifier|public
name|ICUCollationAttributeFactory
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
name|this
argument_list|(
name|TokenStream
operator|.
name|DEFAULT_TOKEN_ATTRIBUTE_FACTORY
argument_list|,
name|collator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an ICUCollationAttributeFactory, using the supplied Attribute     * Factory as the factory for all other attributes.    * @param delegate Attribute Factory    * @param collator CollationKey generator    */
DECL|method|ICUCollationAttributeFactory
specifier|public
name|ICUCollationAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
name|ICUCollatedTermAttributeImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInstance
specifier|public
name|ICUCollatedTermAttributeImpl
name|createInstance
parameter_list|()
block|{
return|return
operator|new
name|ICUCollatedTermAttributeImpl
argument_list|(
name|collator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

