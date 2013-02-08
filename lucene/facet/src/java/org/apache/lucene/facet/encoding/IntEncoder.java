begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
comment|/**  * Encodes integers to a set {@link BytesRef}. For convenience, each encoder  * implements {@link #createMatchingDecoder()} for easy access to the matching  * decoder.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntEncoder
specifier|public
specifier|abstract
class|class
name|IntEncoder
block|{
DECL|method|IntEncoder
specifier|public
name|IntEncoder
parameter_list|()
block|{}
comment|/**    * Encodes the values to the given buffer. Note that the buffer's offset and    * length are set to 0.    */
DECL|method|encode
specifier|public
specifier|abstract
name|void
name|encode
parameter_list|(
name|IntsRef
name|values
parameter_list|,
name|BytesRef
name|buf
parameter_list|)
function_decl|;
comment|/**    * Returns an {@link IntDecoder} which can decode the values that were encoded    * with this encoder.    */
DECL|method|createMatchingDecoder
specifier|public
specifier|abstract
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
function_decl|;
block|}
end_class

end_unit

