begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * CharStream adds {@link #correctOffset}  * functionality over {@link Reader}.  All Tokenizers accept a  * CharStream instead of {@link Reader} as input, which enables  * arbitrary character based filtering before tokenization.   * The {@link #correctOffset} method fixed offsets to account for  * removal or insertion of characters, so that the offsets  * reported in the tokens match the character offsets of the  * original Reader.  */
end_comment

begin_class
DECL|class|CharStream
specifier|public
specifier|abstract
class|class
name|CharStream
extends|extends
name|Reader
block|{
comment|/**    * Called by CharFilter(s) and Tokenizer to correct token offset.    *    * @param currentOff offset as seen in the output    * @return corrected offset based on the input    */
DECL|method|correctOffset
specifier|public
specifier|abstract
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
function_decl|;
block|}
end_class

end_unit

