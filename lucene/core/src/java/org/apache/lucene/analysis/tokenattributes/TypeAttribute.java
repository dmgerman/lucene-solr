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
comment|/**  * A Token's lexical type. The Default value is "word".   */
end_comment

begin_interface
DECL|interface|TypeAttribute
specifier|public
interface|interface
name|TypeAttribute
extends|extends
name|Attribute
block|{
comment|/** the default type */
DECL|field|DEFAULT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TYPE
init|=
literal|"word"
decl_stmt|;
comment|/**     * Returns this Token's lexical type.  Defaults to "word".     * @see #setType(String)    */
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
function_decl|;
comment|/**     * Set the lexical type.    * @see #type()     */
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

