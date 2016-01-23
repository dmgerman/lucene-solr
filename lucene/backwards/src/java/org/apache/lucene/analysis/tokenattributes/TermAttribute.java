begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * The term text of a Token.  */
end_comment

begin_interface
DECL|interface|TermAttribute
specifier|public
interface|interface
name|TermAttribute
extends|extends
name|Attribute
block|{
comment|/** Returns the Token's term text.    *     * This method has a performance penalty    * because the text is stored internally in a char[].  If    * possible, use {@link #termBuffer()} and {@link    * #termLength()} directly instead.  If you really need a    * String, use this method, which is nothing more than    * a convenience call to<b>new String(token.termBuffer(), 0, token.termLength())</b>    */
DECL|method|term
specifier|public
name|String
name|term
parameter_list|()
function_decl|;
comment|/** Copies the contents of buffer, starting at offset for    *  length characters, into the termBuffer array.    *  @param buffer the buffer to copy    *  @param offset the index in the buffer of the first character to copy    *  @param length the number of characters to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/** Copies the contents of buffer into the termBuffer array.    *  @param buffer the buffer to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|)
function_decl|;
comment|/** Copies the contents of buffer, starting at offset and continuing    *  for length characters, into the termBuffer array.    *  @param buffer the buffer to copy    *  @param offset the index in the buffer of the first character to copy    *  @param length the number of characters to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/** Returns the internal termBuffer character array which    *  you can then directly alter.  If the array is too    *  small for your token, use {@link    *  #resizeTermBuffer(int)} to increase it.  After    *  altering the buffer be sure to call {@link    *  #setTermLength} to record the number of valid    *  characters that were placed into the termBuffer. */
DECL|method|termBuffer
specifier|public
name|char
index|[]
name|termBuffer
parameter_list|()
function_decl|;
comment|/** Grows the termBuffer to at least size newSize, preserving the    *  existing content. Note: If the next operation is to change    *  the contents of the term buffer use    *  {@link #setTermBuffer(char[], int, int)},    *  {@link #setTermBuffer(String)}, or    *  {@link #setTermBuffer(String, int, int)}    *  to optimally combine the resize with the setting of the termBuffer.    *  @param newSize minimum size of the new termBuffer    *  @return newly created termBuffer with length>= newSize    */
DECL|method|resizeTermBuffer
specifier|public
name|char
index|[]
name|resizeTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
function_decl|;
comment|/** Return number of valid characters (length of the term)    *  in the termBuffer array. */
DECL|method|termLength
specifier|public
name|int
name|termLength
parameter_list|()
function_decl|;
comment|/** Set number of valid characters (length of the term) in    *  the termBuffer array. Use this to truncate the termBuffer    *  or to synchronize with external manipulation of the termBuffer.    *  Note: to grow the size of the array,    *  use {@link #resizeTermBuffer(int)} first.    *  @param length the truncated length    */
DECL|method|setTermLength
specifier|public
name|void
name|setTermLength
parameter_list|(
name|int
name|length
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

