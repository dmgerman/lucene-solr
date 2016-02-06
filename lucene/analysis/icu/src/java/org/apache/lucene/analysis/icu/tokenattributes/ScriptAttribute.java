begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UScript
import|;
end_import

begin_comment
comment|// javadoc @link
end_comment

begin_comment
comment|/**  * This attribute stores the UTR #24 script value for a token of text.  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|ScriptAttribute
specifier|public
interface|interface
name|ScriptAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Get the numeric code for this script value.    * This is the constant value from {@link UScript}.    * @return numeric code    */
DECL|method|getCode
specifier|public
name|int
name|getCode
parameter_list|()
function_decl|;
comment|/**    * Set the numeric code for this script value.    * This is the constant value from {@link UScript}.    * @param code numeric code    */
DECL|method|setCode
specifier|public
name|void
name|setCode
parameter_list|(
name|int
name|code
parameter_list|)
function_decl|;
comment|/**    * Get the full name.    * @return UTR #24 full name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the abbreviated name.    * @return UTR #24 abbreviated name.    */
DECL|method|getShortName
specifier|public
name|String
name|getShortName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

