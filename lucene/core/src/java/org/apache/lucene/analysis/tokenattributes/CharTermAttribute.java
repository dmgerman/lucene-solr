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
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * The term text of a Token.  */
end_comment

begin_interface
DECL|interface|CharTermAttribute
specifier|public
interface|interface
name|CharTermAttribute
extends|extends
name|Attribute
extends|,
name|CharSequence
extends|,
name|Appendable
block|{
comment|/** Copies the contents of buffer, starting at offset for    *  length characters, into the termBuffer array.    *  @param buffer the buffer to copy    *  @param offset the index in the buffer of the first character to copy    *  @param length the number of characters to copy    */
DECL|method|copyBuffer
specifier|public
name|void
name|copyBuffer
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
comment|/** Returns the internal termBuffer character array which    *  you can then directly alter.  If the array is too    *  small for your token, use {@link    *  #resizeBuffer(int)} to increase it.  After    *  altering the buffer be sure to call {@link    *  #setLength} to record the number of valid    *  characters that were placed into the termBuffer.     *<p>    *<b>NOTE</b>: The returned buffer may be larger than    *  the valid {@link #length()}.    */
DECL|method|buffer
specifier|public
name|char
index|[]
name|buffer
parameter_list|()
function_decl|;
comment|/** Grows the termBuffer to at least size newSize, preserving the    *  existing content.    *  @param newSize minimum size of the new termBuffer    *  @return newly created termBuffer with {@code length>= newSize}    */
DECL|method|resizeBuffer
specifier|public
name|char
index|[]
name|resizeBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
function_decl|;
comment|/** Set number of valid characters (length of the term) in    *  the termBuffer array. Use this to truncate the termBuffer    *  or to synchronize with external manipulation of the termBuffer.    *  Note: to grow the size of the array,    *  use {@link #resizeBuffer(int)} first.    *  @param length the truncated length    */
DECL|method|setLength
specifier|public
name|CharTermAttribute
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
function_decl|;
comment|/** Sets the length of the termBuffer to zero.    * Use this method before appending contents    * using the {@link Appendable} interface.    */
DECL|method|setEmpty
specifier|public
name|CharTermAttribute
name|setEmpty
parameter_list|()
function_decl|;
comment|// the following methods are redefined to get rid of IOException declaration:
annotation|@
name|Override
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|char
name|c
parameter_list|)
function_decl|;
comment|/** Appends the specified {@code String} to this character sequence.     *<p>The characters of the {@code String} argument are appended, in order, increasing the length of    * this sequence by the length of the argument. If argument is {@code null}, then the four    * characters {@code "null"} are appended.     */
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
comment|/** Appends the specified {@code StringBuilder} to this character sequence.     *<p>The characters of the {@code StringBuilder} argument are appended, in order, increasing the length of    * this sequence by the length of the argument. If argument is {@code null}, then the four    * characters {@code "null"} are appended.     */
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
function_decl|;
comment|/** Appends the contents of the other {@code CharTermAttribute} to this character sequence.     *<p>The characters of the {@code CharTermAttribute} argument are appended, in order, increasing the length of    * this sequence by the length of the argument. If argument is {@code null}, then the four    * characters {@code "null"} are appended.     */
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|CharTermAttribute
name|termAtt
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

