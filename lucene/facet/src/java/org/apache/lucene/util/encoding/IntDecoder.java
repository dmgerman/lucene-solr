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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Decodes integers from a set {@link InputStream}. For re-usability, the  * decoder's input stream can be set by ({@link #reInit(InputStream)}).  * By design, Decoders are NOT thread-safe.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntDecoder
specifier|public
specifier|abstract
class|class
name|IntDecoder
block|{
comment|/** A special long value which is used to indicate end-of-stream has reached. */
DECL|field|EOS
specifier|public
specifier|static
specifier|final
name|long
name|EOS
init|=
literal|0x100000000L
decl_stmt|;
comment|/** Input stream from which the encoded bytes are read */
DECL|field|in
specifier|protected
name|InputStream
name|in
decl_stmt|;
comment|/** Sets the input stream from which the encoded data is read. */
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Decodes data received from the input stream, and returns one decoded    * integer. If end of stream is reached, {@link #EOS} is returned.    *     * @return one decoded integer as long or {@link #EOS} if end-of-stream    *         reached.    * @throws IOException if an I/O error occurs    */
DECL|method|decode
specifier|public
specifier|abstract
name|long
name|decode
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

