begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Extension of Bits for live documents.  */
end_comment

begin_interface
DECL|interface|MutableBits
specifier|public
interface|interface
name|MutableBits
extends|extends
name|Bits
block|{
comment|/**     * Sets the bit specified by<code>index</code> to false.     * @param index index, should be non-negative and&lt; {@link #length()}.    *        The result of passing negative or out of bounds values is undefined    *        by this interface,<b>just don't do it!</b>    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

