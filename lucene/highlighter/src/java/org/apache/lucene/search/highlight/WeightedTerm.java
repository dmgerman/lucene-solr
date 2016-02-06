begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_comment
comment|/** Lightweight class to hold term and a weight value used for scoring this term  */
end_comment

begin_class
DECL|class|WeightedTerm
specifier|public
class|class
name|WeightedTerm
block|{
DECL|field|weight
name|float
name|weight
decl_stmt|;
comment|// multiplier
DECL|field|term
name|String
name|term
decl_stmt|;
comment|//stemmed form
DECL|method|WeightedTerm
specifier|public
name|WeightedTerm
parameter_list|(
name|float
name|weight
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|/**    * @return the term value (stemmed)    */
DECL|method|getTerm
specifier|public
name|String
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
comment|/**    * @return the weight associated with this term    */
DECL|method|getWeight
specifier|public
name|float
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
comment|/**    * @param term the term value (stemmed)    */
DECL|method|setTerm
specifier|public
name|void
name|setTerm
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|/**    * @param weight the weight associated with this term    */
DECL|method|setWeight
specifier|public
name|void
name|setWeight
parameter_list|(
name|float
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
block|}
end_class

end_unit

