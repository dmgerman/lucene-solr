begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|LetterTokenizer
import|;
end_import

begin_comment
comment|/**  * The problem with the standard Letter tokenizer is that it fails on diacritics.  * Handling similar to this is necessary for Indic Scripts, Hebrew, Thaana, etc.  *   *  */
end_comment

begin_class
DECL|class|ArabicLetterTokenizer
specifier|public
class|class
name|ArabicLetterTokenizer
extends|extends
name|LetterTokenizer
block|{
DECL|method|ArabicLetterTokenizer
specifier|public
name|ArabicLetterTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**     * Allows for Letter category or NonspacingMark category    * @see org.apache.lucene.analysis.LetterTokenizer#isTokenChar(char)    */
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|super
operator|.
name|isTokenChar
argument_list|(
name|c
argument_list|)
operator|||
name|Character
operator|.
name|getType
argument_list|(
name|c
argument_list|)
operator|==
name|Character
operator|.
name|NON_SPACING_MARK
return|;
block|}
block|}
end_class

end_unit

