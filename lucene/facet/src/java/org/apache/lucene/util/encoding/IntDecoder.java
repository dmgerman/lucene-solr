begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|util
operator|.
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Decodes integers from a set {@link BytesRef}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntDecoder
specifier|public
specifier|abstract
class|class
name|IntDecoder
block|{
comment|/**    * Performs the actual decoding. Values should be read from    * {@link BytesRef#offset} up to {@code upto}. Also, {@code values} offset and    * length are set to 0 and the encoder is expected to update    * {@link IntsRef#length}, but not {@link IntsRef#offset}.    *     *<p>    *<b>NOTE:</b> it is ok to use the buffer's offset as the current position in    * the buffer (and modify it), it will be reset by    * {@link #decode(BytesRef, IntsRef)}.    */
DECL|method|doDecode
specifier|protected
specifier|abstract
name|void
name|doDecode
parameter_list|(
name|BytesRef
name|buf
parameter_list|,
name|IntsRef
name|values
parameter_list|,
name|int
name|upto
parameter_list|)
function_decl|;
comment|/**    * Called before {@link #doDecode(BytesRef, IntsRef, int)} so that decoders    * can reset their state.    */
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|()
block|{
comment|// do nothing by default
block|}
comment|/**    * Decodes the values from the buffer into the given {@link IntsRef}. Note    * that {@code values.offset} and {@code values.length} are set to 0.    */
DECL|method|decode
specifier|public
specifier|final
name|void
name|decode
parameter_list|(
name|BytesRef
name|buf
parameter_list|,
name|IntsRef
name|values
parameter_list|)
block|{
name|values
operator|.
name|offset
operator|=
name|values
operator|.
name|length
operator|=
literal|0
expr_stmt|;
comment|// must do that because we cannot grow() them otherwise
comment|// some decoders may use the buffer's offset as a position index, so save
comment|// current offset.
name|int
name|bufOffset
init|=
name|buf
operator|.
name|offset
decl_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|doDecode
argument_list|(
name|buf
argument_list|,
name|values
argument_list|,
name|buf
operator|.
name|offset
operator|+
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
assert|assert
name|values
operator|.
name|offset
operator|==
literal|0
operator|:
literal|"offset should not have been modified by the decoder."
assert|;
comment|// fix offset
name|buf
operator|.
name|offset
operator|=
name|bufOffset
expr_stmt|;
block|}
block|}
end_class

end_unit

