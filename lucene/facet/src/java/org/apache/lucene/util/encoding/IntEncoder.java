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
name|OutputStream
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Encodes integers to a set {@link OutputStream}. Extending classes need to  * override {@link #encode(int)} to encode the value using their encoding  * algorithm. The default implementation of {@link #close()} closes the set  * {@link OutputStream}.  *<p>  * The default {@link #IntEncoder() constructor} is provided for convenience  * only. One must call {@link #reInit(OutputStream)} before calling  * {@link #encode(int)} or {@link #close()}.  *<p>  * For convenience, each encoder implements {@link #createMatchingDecoder()} for  * easy access to the matching decoder.  *<p>  *<b>NOTE:</b> some implementations may buffer the encoded values in memory  * (such as {@link IntEncoderFilter} implementations) and encoding will happen  * only upon calling {@link #close()}. Therefore it is important to always call  * {@link #close()} on the encoder at hand.  *<p>  *<b>NOTE:</b> encoders are usually not thread safe, unless specifically  * documented otherwise by an implementation.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntEncoder
specifier|public
specifier|abstract
class|class
name|IntEncoder
block|{
DECL|field|out
specifier|protected
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
comment|/**    * Default constructor, provided here for robustness: if in the future a    * constructor with parameters will be added, this might break custom    * implementations of this class which call this implicit constructor. So we    * make it explicit to avoid any such issue in the future.    */
DECL|method|IntEncoder
specifier|public
name|IntEncoder
parameter_list|()
block|{   }
comment|/**    * Instructs the encoder to finish the encoding process. This method closes    * the output stream which was specified by {@link #reInit(OutputStream)    * reInit}. An implementation may do here additional cleanup required to    * complete the encoding, such as flushing internal buffers, etc.<br>    * Once this method was called, no further calls to {@link #encode(int)    * encode} should be made before first calling {@link #reInit(OutputStream)    * reInit}.    *<p>    *<b>NOTE:</b> overriding classes should make sure they either call    *<code>super.close()</code> or close the output stream themselves.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Encodes an integer to the output stream given in    * {@link #reInit(OutputStream) reInit}    */
DECL|method|encode
specifier|public
specifier|abstract
name|void
name|encode
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an {@link IntDecoder} which matches this encoder. Every encoder    * must return an {@link IntDecoder} and<code>null</code> is not a valid    * value. If an encoder is just a filter, it should at least return its    * wrapped encoder's matching decoder.    *<p>    *<b>NOTE:</b> this method should create a new instance of the matching    * decoder and leave the instance sharing to the caller. Returning the same    * instance over and over is risky because encoders and decoders are not    * thread safe.    */
DECL|method|createMatchingDecoder
specifier|public
specifier|abstract
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
function_decl|;
comment|/**    * Reinitializes the encoder with the give {@link OutputStream}. For    * re-usability it can be changed without the need to reconstruct a new    * object.    *<p>    *<b>NOTE:</b> after calling {@link #close()}, one<u><i>must</i></u> call    * this method even if the output stream itself hasn't changed. An example    * case is that the output stream wraps a byte[], and the output stream itself    * is reset, but its instance hasn't changed. Some implementations of    * {@link IntEncoder} may write some metadata about themselves to the output    * stream, and therefore it is imperative that one calls this method before    * encoding any data.    */
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
block|}
end_class

end_unit

