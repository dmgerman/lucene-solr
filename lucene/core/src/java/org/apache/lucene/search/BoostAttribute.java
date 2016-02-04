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
name|index
operator|.
name|TermsEnum
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
name|index
operator|.
name|Terms
import|;
end_import

begin_comment
comment|// javadocs only
end_comment

begin_comment
comment|/** Add this {@link Attribute} to a {@link TermsEnum} returned by {@link MultiTermQuery#getTermsEnum(Terms,AttributeSource)}  * and update the boost on each returned term. This enables to control the boost factor  * for each matching term in {@link MultiTermQuery#SCORING_BOOLEAN_REWRITE} or  * {@link TopTermsRewrite} mode.  * {@link FuzzyQuery} is using this to take the edit distance into account.  *<p><b>Please note:</b> This attribute is intended to be added only by the TermsEnum  * to itself in its constructor and consumed by the {@link MultiTermQuery.RewriteMethod}.  * @lucene.internal  */
end_comment

begin_interface
DECL|interface|BoostAttribute
specifier|public
interface|interface
name|BoostAttribute
extends|extends
name|Attribute
block|{
comment|/** Sets the boost in this attribute */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
function_decl|;
comment|/** Retrieves the boost, default is {@code 1.0f}. */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

