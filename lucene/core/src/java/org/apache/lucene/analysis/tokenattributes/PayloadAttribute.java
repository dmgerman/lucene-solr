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
name|util
operator|.
name|Attribute
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * The payload of a Token.  *<p>  * The payload is stored in the index at each position, and can  * be used to influence scoring when using Payload-based queries   * in the {@link org.apache.lucene.search.payloads} and  * {@link org.apache.lucene.search.spans} packages.  *<p>  * NOTE: because the payload will be stored at each position, it's usually  * best to use the minimum number of bytes necessary. Some codec implementations  * may optimize payload storage when all payloads have the same length.  *   * @see org.apache.lucene.index.PostingsEnum  */
end_comment

begin_interface
DECL|interface|PayloadAttribute
specifier|public
interface|interface
name|PayloadAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Returns this Token's payload.    * @see #setPayload(BytesRef)    */
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
function_decl|;
comment|/**     * Sets this Token's payload.    * @see #getPayload()    */
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

