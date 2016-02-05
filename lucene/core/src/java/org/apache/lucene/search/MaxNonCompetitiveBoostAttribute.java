begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AttributeSource
import|;
end_import

begin_comment
comment|// javadocs only
end_comment

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
name|index
operator|.
name|Terms
import|;
end_import

begin_comment
comment|// javadocs only
end_comment

begin_comment
comment|/** Add this {@link Attribute} to a fresh {@link AttributeSource} before calling  * {@link MultiTermQuery#getTermsEnum(Terms,AttributeSource)}.  * {@link FuzzyQuery} is using this to control its internal behaviour  * to only return competitive terms.  *<p><b>Please note:</b> This attribute is intended to be added by the {@link MultiTermQuery.RewriteMethod}  * to an empty {@link AttributeSource} that is shared for all segments  * during query rewrite. This attribute source is passed to all segment enums  * on {@link MultiTermQuery#getTermsEnum(Terms,AttributeSource)}.  * {@link TopTermsRewrite} uses this attribute to  * inform all enums about the current boost, that is not competitive.  * @lucene.internal  */
end_comment

begin_interface
DECL|interface|MaxNonCompetitiveBoostAttribute
specifier|public
interface|interface
name|MaxNonCompetitiveBoostAttribute
extends|extends
name|Attribute
block|{
comment|/** This is the maximum boost that would not be competitive. */
DECL|method|setMaxNonCompetitiveBoost
specifier|public
name|void
name|setMaxNonCompetitiveBoost
parameter_list|(
name|float
name|maxNonCompetitiveBoost
parameter_list|)
function_decl|;
comment|/** This is the maximum boost that would not be competitive. Default is negative infinity, which means every term is competitive. */
DECL|method|getMaxNonCompetitiveBoost
specifier|public
name|float
name|getMaxNonCompetitiveBoost
parameter_list|()
function_decl|;
comment|/** This is the term or<code>null</code> of the term that triggered the boost change. */
DECL|method|setCompetitiveTerm
specifier|public
name|void
name|setCompetitiveTerm
parameter_list|(
name|BytesRef
name|competitiveTerm
parameter_list|)
function_decl|;
comment|/** This is the term or<code>null</code> of the term that triggered the boost change. Default is<code>null</code>, which means every term is competitoive. */
DECL|method|getCompetitiveTerm
specifier|public
name|BytesRef
name|getCompetitiveTerm
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

