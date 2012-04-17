begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
package|;
end_package

begin_comment
comment|/**  * Internal SmartChineseAnalyzer character type constants.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CharType
specifier|public
class|class
name|CharType
block|{
comment|/**    * Punctuation Characters    */
DECL|field|DELIMITER
specifier|public
specifier|final
specifier|static
name|int
name|DELIMITER
init|=
literal|0
decl_stmt|;
comment|/**    * Letters    */
DECL|field|LETTER
specifier|public
specifier|final
specifier|static
name|int
name|LETTER
init|=
literal|1
decl_stmt|;
comment|/**    * Numeric Digits    */
DECL|field|DIGIT
specifier|public
specifier|final
specifier|static
name|int
name|DIGIT
init|=
literal|2
decl_stmt|;
comment|/**    * Han Ideographs    */
DECL|field|HANZI
specifier|public
specifier|final
specifier|static
name|int
name|HANZI
init|=
literal|3
decl_stmt|;
comment|/**    * Characters that act as a space    */
DECL|field|SPACE_LIKE
specifier|public
specifier|final
specifier|static
name|int
name|SPACE_LIKE
init|=
literal|4
decl_stmt|;
comment|/**    * Full-Width letters    */
DECL|field|FULLWIDTH_LETTER
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_LETTER
init|=
literal|5
decl_stmt|;
comment|/**    * Full-Width alphanumeric characters    */
DECL|field|FULLWIDTH_DIGIT
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_DIGIT
init|=
literal|6
decl_stmt|;
comment|/**    * Other (not fitting any of the other categories)    */
DECL|field|OTHER
specifier|public
specifier|final
specifier|static
name|int
name|OTHER
init|=
literal|7
decl_stmt|;
block|}
end_class

end_unit

