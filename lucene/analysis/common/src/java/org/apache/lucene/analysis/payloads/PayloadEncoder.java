begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
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

begin_comment
comment|/**  * Mainly for use with the DelimitedPayloadTokenFilter, converts char buffers to  * {@link BytesRef}.  *<p>  * NOTE: This interface is subject to change   *  **/
end_comment

begin_interface
DECL|interface|PayloadEncoder
specifier|public
interface|interface
name|PayloadEncoder
block|{
DECL|method|encode
name|BytesRef
name|encode
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|)
function_decl|;
comment|/**    * Convert a char array to a {@link BytesRef}    * @return encoded {@link BytesRef}    */
DECL|method|encode
name|BytesRef
name|encode
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
block|}
end_interface

end_unit

