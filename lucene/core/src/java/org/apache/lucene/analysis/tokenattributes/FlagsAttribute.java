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
name|analysis
operator|.
name|Tokenizer
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
comment|/**  * This attribute can be used to pass different flags down the {@link Tokenizer} chain,  * e.g. from one TokenFilter to another one.   *<p>  * This is completely distinct from {@link TypeAttribute}, although they do share similar purposes.  * The flags can be used to encode information about the token for use by other   * {@link org.apache.lucene.analysis.TokenFilter}s.  * @lucene.experimental While we think this is here to stay, we may want to change it to be a long.  */
end_comment

begin_interface
DECL|interface|FlagsAttribute
specifier|public
interface|interface
name|FlagsAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Get the bitset for any bits that have been set.      * @return The bits    * @see #getFlags()    */
DECL|method|getFlags
specifier|public
name|int
name|getFlags
parameter_list|()
function_decl|;
comment|/**    * Set the flags to a new bitset.    * @see #getFlags()    */
DECL|method|setFlags
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

