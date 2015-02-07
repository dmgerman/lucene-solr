begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * SuggestWord, used in suggestSimilar method in SpellChecker class.  *<p>  * Default sort is first by score, then by frequency.  */
end_comment

begin_class
DECL|class|SuggestWord
specifier|public
specifier|final
class|class
name|SuggestWord
block|{
comment|/**    * Creates a new empty suggestion with null text.    */
DECL|method|SuggestWord
specifier|public
name|SuggestWord
parameter_list|()
block|{}
comment|/**    * the score of the word    */
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
comment|/**    * The freq of the word    */
DECL|field|freq
specifier|public
name|int
name|freq
decl_stmt|;
comment|/**    * the suggested word    */
DECL|field|string
specifier|public
name|String
name|string
decl_stmt|;
block|}
end_class

end_unit

