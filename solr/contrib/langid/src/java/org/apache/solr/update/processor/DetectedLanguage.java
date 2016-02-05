begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_comment
comment|/**  * Bean holding a language and a detection certainty   */
end_comment

begin_class
DECL|class|DetectedLanguage
specifier|public
class|class
name|DetectedLanguage
block|{
DECL|field|langCode
specifier|private
specifier|final
name|String
name|langCode
decl_stmt|;
DECL|field|certainty
specifier|private
specifier|final
name|Double
name|certainty
decl_stmt|;
DECL|method|DetectedLanguage
name|DetectedLanguage
parameter_list|(
name|String
name|lang
parameter_list|,
name|Double
name|certainty
parameter_list|)
block|{
name|this
operator|.
name|langCode
operator|=
name|lang
expr_stmt|;
name|this
operator|.
name|certainty
operator|=
name|certainty
expr_stmt|;
block|}
comment|/**    * Returns the detected language code    * @return language code as a string    */
DECL|method|getLangCode
specifier|public
name|String
name|getLangCode
parameter_list|()
block|{
return|return
name|langCode
return|;
block|}
comment|/**    * Returns the detected certainty for this language    * @return certainty as a value between 0.0 and 1.0 where 1.0 is 100% certain    */
DECL|method|getCertainty
specifier|public
name|Double
name|getCertainty
parameter_list|()
block|{
return|return
name|certainty
return|;
block|}
block|}
end_class

end_unit

