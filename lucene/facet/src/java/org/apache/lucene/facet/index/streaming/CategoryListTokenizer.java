begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A base class for category list tokenizers, which add category list tokens to  * category streams.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryListTokenizer
specifier|public
specifier|abstract
class|class
name|CategoryListTokenizer
extends|extends
name|CategoryTokenizerBase
block|{
comment|/**    * @see CategoryTokenizerBase#CategoryTokenizerBase(TokenStream, FacetIndexingParams)    */
DECL|method|CategoryListTokenizer
specifier|public
name|CategoryListTokenizer
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|indexingParams
argument_list|)
expr_stmt|;
block|}
comment|/**    * A method invoked once when the input stream begins, for subclass-specific    * processing. Subclass implementations must invoke this one, too!    */
DECL|method|handleStartOfInput
specifier|protected
name|void
name|handleStartOfInput
parameter_list|()
throws|throws
name|IOException
block|{
comment|// In this class, we do nothing.
block|}
comment|/**    * A method invoked once when the input stream ends, for subclass-specific    * processing.    */
DECL|method|handleEndOfInput
specifier|protected
name|void
name|handleEndOfInput
parameter_list|()
throws|throws
name|IOException
block|{
comment|// In this class, we do nothing.
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|handleStartOfInput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|abstract
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

