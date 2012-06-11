begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Internal SmartChineseAnalyzer token type constants  * @lucene.experimental  */
end_comment

begin_class
DECL|class|WordType
specifier|public
class|class
name|WordType
block|{
comment|/**    * Start of a Sentence    */
DECL|field|SENTENCE_BEGIN
specifier|public
specifier|final
specifier|static
name|int
name|SENTENCE_BEGIN
init|=
literal|0
decl_stmt|;
comment|/**    * End of a Sentence    */
DECL|field|SENTENCE_END
specifier|public
specifier|final
specifier|static
name|int
name|SENTENCE_END
init|=
literal|1
decl_stmt|;
comment|/**    * Chinese Word     */
DECL|field|CHINESE_WORD
specifier|public
specifier|final
specifier|static
name|int
name|CHINESE_WORD
init|=
literal|2
decl_stmt|;
comment|/**    * ASCII String    */
DECL|field|STRING
specifier|public
specifier|final
specifier|static
name|int
name|STRING
init|=
literal|3
decl_stmt|;
comment|/**    * ASCII Alphanumeric     */
DECL|field|NUMBER
specifier|public
specifier|final
specifier|static
name|int
name|NUMBER
init|=
literal|4
decl_stmt|;
comment|/**    * Punctuation Symbol    */
DECL|field|DELIMITER
specifier|public
specifier|final
specifier|static
name|int
name|DELIMITER
init|=
literal|5
decl_stmt|;
comment|/**    * Full-Width String    */
DECL|field|FULLWIDTH_STRING
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_STRING
init|=
literal|6
decl_stmt|;
comment|/**    * Full-Width Alphanumeric    */
DECL|field|FULLWIDTH_NUMBER
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_NUMBER
init|=
literal|7
decl_stmt|;
block|}
end_class

end_unit

