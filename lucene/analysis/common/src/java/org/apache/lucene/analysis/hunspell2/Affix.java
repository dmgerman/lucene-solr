begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell2
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell2
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Wrapper class representing a hunspell affix  */
end_comment

begin_class
DECL|class|Affix
specifier|final
class|class
name|Affix
block|{
DECL|field|appendFlags
specifier|private
name|char
name|appendFlags
index|[]
decl_stmt|;
comment|// continuation class flags
DECL|field|strip
specifier|private
name|String
name|strip
decl_stmt|;
DECL|field|conditionPattern
specifier|private
name|Pattern
name|conditionPattern
decl_stmt|;
DECL|field|flag
specifier|private
name|char
name|flag
decl_stmt|;
DECL|field|crossProduct
specifier|private
name|boolean
name|crossProduct
decl_stmt|;
comment|/**    * Checks whether the given text matches the conditional pattern on this affix    *    * @param text Text to check if it matches the affix's conditional pattern    * @return {@code true} if the text meets the condition, {@code false} otherwise    */
DECL|method|checkCondition
specifier|public
name|boolean
name|checkCondition
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
return|return
name|conditionPattern
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
comment|/**    * Returns the flags defined for the affix append    *    * @return Flags defined for the affix append    */
DECL|method|getAppendFlags
specifier|public
name|char
index|[]
name|getAppendFlags
parameter_list|()
block|{
return|return
name|appendFlags
return|;
block|}
comment|/**    * Sets the flags defined for the affix append    *    * @param appendFlags Flags defined for the affix append    */
DECL|method|setAppendFlags
specifier|public
name|void
name|setAppendFlags
parameter_list|(
name|char
index|[]
name|appendFlags
parameter_list|)
block|{
name|this
operator|.
name|appendFlags
operator|=
name|appendFlags
expr_stmt|;
block|}
comment|/**    * Returns the stripping characters defined for the affix    *    * @return Stripping characters defined for the affix    */
DECL|method|getStrip
specifier|public
name|String
name|getStrip
parameter_list|()
block|{
return|return
name|strip
return|;
block|}
comment|/**    * Sets the stripping characters defined for the affix    *    * @param strip Stripping characters defined for the affix    */
DECL|method|setStrip
specifier|public
name|void
name|setStrip
parameter_list|(
name|String
name|strip
parameter_list|)
block|{
name|this
operator|.
name|strip
operator|=
name|strip
expr_stmt|;
block|}
comment|/**    * Sets the condition that must be met before the affix can be applied    *    * @param pattern Condition as a regular expression pattern    */
DECL|method|setCondition
specifier|public
name|void
name|setCondition
parameter_list|(
name|Pattern
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|conditionPattern
operator|=
name|pattern
expr_stmt|;
block|}
comment|/**    * Returns the affix flag    *    * @return Affix flag    */
DECL|method|getFlag
specifier|public
name|char
name|getFlag
parameter_list|()
block|{
return|return
name|flag
return|;
block|}
comment|/**    * Sets the affix flag    *    * @param flag Affix flag    */
DECL|method|setFlag
specifier|public
name|void
name|setFlag
parameter_list|(
name|char
name|flag
parameter_list|)
block|{
name|this
operator|.
name|flag
operator|=
name|flag
expr_stmt|;
block|}
comment|/**    * Returns whether the affix is defined as cross product    *    * @return {@code true} if the affix is cross product, {@code false} otherwise    */
DECL|method|isCrossProduct
specifier|public
name|boolean
name|isCrossProduct
parameter_list|()
block|{
return|return
name|crossProduct
return|;
block|}
comment|/**    * Sets whether the affix is defined as cross product    *    * @param crossProduct Whether the affix is defined as cross product    */
DECL|method|setCrossProduct
specifier|public
name|void
name|setCrossProduct
parameter_list|(
name|boolean
name|crossProduct
parameter_list|)
block|{
name|this
operator|.
name|crossProduct
operator|=
name|crossProduct
expr_stmt|;
block|}
block|}
end_class

end_unit

