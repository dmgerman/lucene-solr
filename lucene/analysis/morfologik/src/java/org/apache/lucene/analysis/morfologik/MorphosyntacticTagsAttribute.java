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
name|util
operator|.
name|List
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

begin_comment
comment|/**   * Morfologik provides morphosyntactic annotations for  * surface forms. For the exact format and description of these,  * see the project's documentation.  */
end_comment

begin_interface
DECL|interface|MorphosyntacticTagsAttribute
specifier|public
interface|interface
name|MorphosyntacticTagsAttribute
extends|extends
name|Attribute
block|{
comment|/**     * Set the POS tag. The default value (no-value) is null.    *     * @param tags A list of POS tags corresponding to current lemma.    */
DECL|method|setTags
specifier|public
name|void
name|setTags
parameter_list|(
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|tags
parameter_list|)
function_decl|;
comment|/**     * Returns the POS tag of the term. A single word may have multiple POS tags,     * depending on the interpretation (context disambiguation is typically needed    * to determine which particular tag is appropriate).      */
DECL|method|getTags
specifier|public
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|getTags
parameter_list|()
function_decl|;
comment|/** Clear to default value. */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

