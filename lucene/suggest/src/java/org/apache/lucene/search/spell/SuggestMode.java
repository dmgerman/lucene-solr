begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Set of strategies for suggesting related terms  * @lucene.experimental  */
end_comment

begin_enum
DECL|enum|SuggestMode
specifier|public
enum|enum
name|SuggestMode
block|{
comment|/**    * Generate suggestions only for terms not in the index (default)    */
DECL|enum constant|SUGGEST_WHEN_NOT_IN_INDEX
name|SUGGEST_WHEN_NOT_IN_INDEX
block|,
comment|/**    * Return only suggested words that are as frequent or more frequent than the    * searched word    */
DECL|enum constant|SUGGEST_MORE_POPULAR
name|SUGGEST_MORE_POPULAR
block|,
comment|/**    * Always attempt to offer suggestions (however, other parameters may limit    * suggestions. For example, see    * {@link DirectSpellChecker#setMaxQueryFrequency(float)} ).    */
DECL|enum constant|SUGGEST_ALWAYS
name|SUGGEST_ALWAYS
block|}
end_enum

end_unit

