begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
package|;
end_package

begin_comment
comment|/**  * Dictionary interface for retrieving morphological data  * by id.  */
end_comment

begin_interface
DECL|interface|Dictionary
specifier|public
interface|interface
name|Dictionary
block|{
DECL|field|INTERNAL_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|INTERNAL_SEPARATOR
init|=
literal|"\u0000"
decl_stmt|;
comment|/**    * Get left id of specified word    * @return left id    */
DECL|method|getLeftId
specifier|public
name|int
name|getLeftId
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|/**    * Get right id of specified word    * @return right id    */
DECL|method|getRightId
specifier|public
name|int
name|getRightId
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|/**    * Get word cost of specified word    * @return word's cost    */
DECL|method|getWordCost
specifier|public
name|int
name|getWordCost
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|/**    * Get Part-Of-Speech of tokens    * @param wordId word ID of token    * @return Part-Of-Speech of the token    */
DECL|method|getPartOfSpeech
specifier|public
name|String
name|getPartOfSpeech
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|/**    * Get reading of tokens    * @param wordId word ID of token    * @return Reading of the token    */
DECL|method|getReading
specifier|public
name|String
name|getReading
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**    * Get base form of word    * @param wordId word ID of token    * @return Base form (only different for inflected words, otherwise null)    */
DECL|method|getBaseForm
specifier|public
name|String
name|getBaseForm
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**    * Get pronunciation of tokens    * @param wordId word ID of token    * @return Pronunciation of the token    */
DECL|method|getPronunciation
specifier|public
name|String
name|getPronunciation
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**    * Get inflection type of tokens    * @param wordId word ID of token    * @return inflection type, or null    */
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|/**    * Get inflection form of tokens    * @param wordId word ID of token    * @return inflection form, or null    */
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|(
name|int
name|wordId
parameter_list|)
function_decl|;
comment|// TODO: maybe we should have a optimal method, a non-typesafe
comment|// 'getAdditionalData' if other dictionaries like unidic have additional data
block|}
end_interface

end_unit

