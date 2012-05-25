begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A class to hold "phrase slop" and "boost" parameters for pf, pf2, pf3 parameters  **/
end_comment

begin_class
DECL|class|FieldParams
specifier|public
class|class
name|FieldParams
block|{
DECL|field|wordGrams
specifier|private
specifier|final
name|int
name|wordGrams
decl_stmt|;
comment|// make bigrams if 2, trigrams if 3, or all if 0
DECL|field|slop
specifier|private
specifier|final
name|int
name|slop
decl_stmt|;
comment|// null defaults to ps parameter
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|FieldParams
specifier|public
name|FieldParams
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|wordGrams
parameter_list|,
name|int
name|slop
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|wordGrams
operator|=
name|wordGrams
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getWordGrams
specifier|public
name|int
name|getWordGrams
parameter_list|()
block|{
return|return
name|wordGrams
return|;
block|}
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
block|}
end_class

end_unit

