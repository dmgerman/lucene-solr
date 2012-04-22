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
name|java
operator|.
name|text
operator|.
name|Collator
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
name|CollatedTermAttributeImpl
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
name|Attribute
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
name|AttributeImpl
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

begin_comment
comment|/**  *<p>  *   Converts each token into its {@link java.text.CollationKey}, and then  *   encodes the bytes as an index term.  *</p>  *<p>  *<strong>WARNING:</strong> Make sure you use exactly the same Collator at  *   index and query time -- CollationKeys are only comparable when produced by  *   the same Collator.  Since {@link java.text.RuleBasedCollator}s are not  *   independently versioned, it is unsafe to search against stored  *   CollationKeys unless the following are exactly the same (best practice is  *   to store this information with the index and check that they remain the  *   same at query time):  *</p>  *<ol>  *<li>JVM vendor</li>  *<li>JVM version, including patch version</li>  *<li>  *     The language (and country and variant, if specified) of the Locale  *     used when constructing the collator via  *     {@link Collator#getInstance(java.util.Locale)}.  *</li>  *<li>  *     The collation strength used - see {@link Collator#setStrength(int)}  *</li>  *</ol>   *<p>  *   The<code>ICUCollationAttributeFactory</code> in the analysis-icu package   *   uses ICU4J's Collator, which makes its  *   version available, thus allowing collation to be versioned independently  *   from the JVM.  ICUCollationAttributeFactory is also significantly faster and  *   generates significantly shorter keys than CollationAttributeFactory.  See  *<a href="http://site.icu-project.org/charts/collation-icu4j-sun"  *>http://site.icu-project.org/charts/collation-icu4j-sun</a> for key  *   generation timing and key length comparisons between ICU4J and  *   java.text.Collator over several languages.  *</p>  *<p>  *   CollationKeys generated by java.text.Collators are not compatible  *   with those those generated by ICU Collators.  Specifically, if you use   *   CollationAttributeFactory to generate index terms, do not use  *   ICUCollationAttributeFactory on the query side, or vice versa.  *</p>  */
end_comment

begin_class
DECL|class|CollationAttributeFactory
specifier|public
class|class
name|CollationAttributeFactory
extends|extends
name|AttributeSource
operator|.
name|AttributeFactory
block|{
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
decl_stmt|;
comment|/**    * Create a CollationAttributeFactory, using     * {@link org.apache.lucene.util.AttributeSource.AttributeFactory#DEFAULT_ATTRIBUTE_FACTORY} as the    * factory for all other attributes.    * @param collator CollationKey generator    */
DECL|method|CollationAttributeFactory
specifier|public
name|CollationAttributeFactory
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
name|this
argument_list|(
name|AttributeSource
operator|.
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|collator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a CollationAttributeFactory, using the supplied Attribute Factory     * as the factory for all other attributes.    * @param delegate Attribute Factory    * @param collator CollationKey generator    */
DECL|method|CollationAttributeFactory
specifier|public
name|CollationAttributeFactory
parameter_list|(
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
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
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
return|return
name|attClass
operator|.
name|isAssignableFrom
argument_list|(
name|CollatedTermAttributeImpl
operator|.
name|class
argument_list|)
condition|?
operator|new
name|CollatedTermAttributeImpl
argument_list|(
name|collator
argument_list|)
else|:
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

