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
comment|/** Determines how many positions this  *  token spans.  Very few analyzer components actually  *  produce this attribute, and indexing ignores it, but  *  it's useful to express the graph structure naturally  *  produced by decompounding, word splitting/joining,  *  synonym filtering, etc.  *  *<p>NOTE: this is optional, and most analyzers  *  don't change the default value (1). */
end_comment

begin_interface
DECL|interface|PositionLengthAttribute
specifier|public
interface|interface
name|PositionLengthAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Set the position length of this Token.    *<p>    * The default value is one.     * @param positionLength how many positions this token    *  spans.     * @throws IllegalArgumentException if<code>positionLength</code>     *         is zero or negative.    * @see #getPositionLength()    */
DECL|method|setPositionLength
specifier|public
name|void
name|setPositionLength
parameter_list|(
name|int
name|positionLength
parameter_list|)
function_decl|;
comment|/** Returns the position length of this Token.    * @see #setPositionLength    */
DECL|method|getPositionLength
specifier|public
name|int
name|getPositionLength
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

