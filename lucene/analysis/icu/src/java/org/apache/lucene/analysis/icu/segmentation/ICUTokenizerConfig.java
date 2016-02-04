begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
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
name|segmentation
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_comment
comment|/**  * Class that allows for tailored Unicode Text Segmentation on  * a per-writing system basis.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ICUTokenizerConfig
specifier|public
specifier|abstract
class|class
name|ICUTokenizerConfig
block|{
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|ICUTokenizerConfig
specifier|public
name|ICUTokenizerConfig
parameter_list|()
block|{}
comment|/** Return a breakiterator capable of processing a given script. */
DECL|method|getBreakIterator
specifier|public
specifier|abstract
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|int
name|script
parameter_list|)
function_decl|;
comment|/** Return a token type value for a given script and BreakIterator    *  rule status. */
DECL|method|getType
specifier|public
specifier|abstract
name|String
name|getType
parameter_list|(
name|int
name|script
parameter_list|,
name|int
name|ruleStatus
parameter_list|)
function_decl|;
comment|/** true if Han, Hiragana, and Katakana scripts should all be returned as Japanese */
DECL|method|combineCJ
specifier|public
specifier|abstract
name|boolean
name|combineCJ
parameter_list|()
function_decl|;
block|}
end_class

end_unit

