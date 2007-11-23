begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * TermPositions provides an interface for enumerating the&lt;document,  * frequency,&lt;position&gt;*&gt; tuples for a term.<p> The document and  * frequency are the same as for a TermDocs.  The positions portion lists the ordinal  * positions of each occurrence of a term in a document.  *  * @see IndexReader#termPositions()  */
end_comment

begin_interface
DECL|interface|TermPositions
specifier|public
interface|interface
name|TermPositions
extends|extends
name|TermDocs
block|{
comment|/** Returns next position in the current document.  It is an error to call     this more than {@link #freq()} times     without calling {@link #next()}<p> This is     invalid until {@link #next()} is called for     the first time.     */
DECL|method|nextPosition
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**       * Returns the length of the payload at the current term position.      * This is invalid until {@link #nextPosition()} is called for      * the first time.<br>      * @return length of the current payload in number of bytes      */
DECL|method|getPayloadLength
name|int
name|getPayloadLength
parameter_list|()
function_decl|;
comment|/**       * Returns the payload data at the current term position.      * This is invalid until {@link #nextPosition()} is called for      * the first time.      * This method must not be called more than once after each call      * of {@link #nextPosition()}. However, payloads are loaded lazily,      * so if the payload data for the current position is not needed,      * this method may not be called at all for performance reasons.<br>      *       * @param data the array into which the data of this payload is to be      *             stored, if it is big enough; otherwise, a new byte[] array      *             is allocated for this purpose.       * @param offset the offset in the array into which the data of this payload      *               is to be stored.      * @return a byte[] array containing the data of this payload      * @throws IOException      */
DECL|method|getPayload
name|byte
index|[]
name|getPayload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if a payload can be loaded at this position.    *<p>    * Payloads can only be loaded once per call to     * {@link #nextPosition()}.    *     * @return true if there is a payload available at this position that can be loaded    */
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

