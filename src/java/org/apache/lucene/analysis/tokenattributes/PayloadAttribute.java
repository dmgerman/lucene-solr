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
name|index
operator|.
name|Payload
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
name|Attribute
import|;
end_import

begin_comment
comment|/**  * The payload of a Token. See also {@link Payload}.  */
end_comment

begin_interface
DECL|interface|PayloadAttribute
specifier|public
interface|interface
name|PayloadAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Returns this Token's payload.    */
DECL|method|getPayload
specifier|public
name|Payload
name|getPayload
parameter_list|()
function_decl|;
comment|/**     * Sets this Token's payload.    */
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|Payload
name|payload
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

