begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Standardized measure of distance from independence  *<p>  * Described as:  * "good at tasks that require high recall and high precision, especially   *  against short queries composed of a few words as in the case of Internet   *  searches"  * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndependenceStandardized
specifier|public
class|class
name|IndependenceStandardized
extends|extends
name|Independence
block|{
comment|/**    * Sole constructor.    */
DECL|method|IndependenceStandardized
specifier|public
name|IndependenceStandardized
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|float
name|freq
parameter_list|,
name|float
name|expected
parameter_list|)
block|{
return|return
operator|(
name|freq
operator|-
name|expected
operator|)
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|expected
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Standardized"
return|;
block|}
block|}
end_class

end_unit

