begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package

begin_comment
comment|/**  * This class represents a hyphenated word.  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.  */
end_comment

begin_class
DECL|class|Hyphenation
specifier|public
class|class
name|Hyphenation
block|{
DECL|field|hyphenPoints
specifier|private
name|int
index|[]
name|hyphenPoints
decl_stmt|;
comment|/**    * rawWord as made of alternating strings and {@link Hyphen Hyphen} instances    */
DECL|method|Hyphenation
name|Hyphenation
parameter_list|(
name|int
index|[]
name|points
parameter_list|)
block|{
name|hyphenPoints
operator|=
name|points
expr_stmt|;
block|}
comment|/**    * @return the number of hyphenation points in the word    */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|hyphenPoints
operator|.
name|length
return|;
block|}
comment|/**    * @return the hyphenation points    */
DECL|method|getHyphenationPoints
specifier|public
name|int
index|[]
name|getHyphenationPoints
parameter_list|()
block|{
return|return
name|hyphenPoints
return|;
block|}
block|}
end_class

end_unit

