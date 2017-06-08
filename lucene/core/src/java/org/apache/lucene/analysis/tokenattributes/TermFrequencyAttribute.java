begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|index
operator|.
name|IndexOptions
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
comment|/** Sets the custom term frequency of a term within one document.  If this attribute  *  is present in your analysis chain for a given field, that field must be indexed with  *  {@link IndexOptions#DOCS_AND_FREQS}. */
end_comment

begin_interface
DECL|interface|TermFrequencyAttribute
specifier|public
interface|interface
name|TermFrequencyAttribute
extends|extends
name|Attribute
block|{
comment|/** Set the custom term frequency of the current term within one document. */
DECL|method|setTermFrequency
specifier|public
name|void
name|setTermFrequency
parameter_list|(
name|int
name|termFrequency
parameter_list|)
function_decl|;
comment|/** Returns the custom term frequencey. */
DECL|method|getTermFrequency
specifier|public
name|int
name|getTermFrequency
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

