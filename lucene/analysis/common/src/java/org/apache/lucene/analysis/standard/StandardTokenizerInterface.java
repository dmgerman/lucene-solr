begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|tokenattributes
operator|.
name|CharTermAttribute
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**   * Internal interface for supporting versioned grammars.  * @lucene.internal   */
end_comment

begin_interface
DECL|interface|StandardTokenizerInterface
specifier|public
interface|interface
name|StandardTokenizerInterface
block|{
comment|/** This token type, as returned from {#link #getNextToken()}, denotes the end of file */
DECL|field|YYEOF
specifier|public
specifier|static
specifier|final
name|int
name|YYEOF
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Copies the matched text into the CharTermAttribute    */
DECL|method|getText
specifier|public
name|void
name|getText
parameter_list|(
name|CharTermAttribute
name|t
parameter_list|)
function_decl|;
comment|/**    * Returns the current position.    */
DECL|method|yychar
specifier|public
name|int
name|yychar
parameter_list|()
function_decl|;
comment|/**    * Resets the scanner to read from a new input stream.    * Does not close the old reader.    *    * All internal variables are reset, the old input stream     *<b>cannot</b> be reused (internal buffer is discarded and lost).    * Lexical state is set to<tt>ZZ_INITIAL</tt>.    *    * @param reader   the new input stream     */
DECL|method|yyreset
specifier|public
name|void
name|yyreset
parameter_list|(
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/**    * Returns the length of the matched text region.    */
DECL|method|yylength
specifier|public
name|int
name|yylength
parameter_list|()
function_decl|;
comment|/**    * Resumes scanning until the next regular expression is matched,    * the end of input is encountered or an I/O-Error occurs.    *    * @return      the next token, {@link #YYEOF} on end of stream    * @exception   IOException  if any I/O-Error occurs    */
DECL|method|getNextToken
specifier|public
name|int
name|getNextToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the scanner buffer size in chars    */
DECL|method|setBufferSize
specifier|public
name|void
name|setBufferSize
parameter_list|(
name|int
name|numChars
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

