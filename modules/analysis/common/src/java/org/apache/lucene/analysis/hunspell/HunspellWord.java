begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|HunspellWord
specifier|public
class|class
name|HunspellWord
block|{
DECL|field|flags
specifier|private
specifier|final
name|char
name|flags
index|[]
decl_stmt|;
comment|// sorted, can we represent more concisely?
comment|/**    * Creates a new HunspellWord with no associated flags    */
DECL|method|HunspellWord
specifier|public
name|HunspellWord
parameter_list|()
block|{
name|flags
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Constructs a new HunspellWord with the given flags    *    * @param flags Flags to associate with the word    */
DECL|method|HunspellWord
specifier|public
name|HunspellWord
parameter_list|(
name|char
index|[]
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/**    * Checks whether the word has the given flag associated with it    *    * @param flag Flag to check whether it is associated with the word    * @return {@code true} if the flag is associated, {@code false} otherwise    */
DECL|method|hasFlag
specifier|public
name|boolean
name|hasFlag
parameter_list|(
name|char
name|flag
parameter_list|)
block|{
return|return
name|flags
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|flags
argument_list|,
name|flag
argument_list|)
operator|>=
literal|0
return|;
block|}
comment|/**    * Returns the flags associated with the word    *    * @return Flags asssociated with the word    */
DECL|method|getFlags
specifier|public
name|char
index|[]
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
block|}
end_class

end_unit

