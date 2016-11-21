begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * F1LOG is defined as Sum(tf(term_doc_freq)*ln(docLen)*IDF(term))  * where IDF(t) = ln((N+1)/df(t)) N=total num of docs, df=doc freq  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AxiomaticF1LOG
specifier|public
class|class
name|AxiomaticF1LOG
extends|extends
name|Axiomatic
block|{
comment|/**    * Constructor setting s only, letting k and queryLen to default    *    * @param s hyperparam for the growth function    */
DECL|method|AxiomaticF1LOG
specifier|public
name|AxiomaticF1LOG
parameter_list|(
name|float
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**    * Default constructor    */
DECL|method|AxiomaticF1LOG
specifier|public
name|AxiomaticF1LOG
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
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
literal|"F1LOG"
return|;
block|}
comment|/**    * compute the term frequency component    */
annotation|@
name|Override
DECL|method|tf
specifier|protected
name|float
name|tf
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
if|if
condition|(
name|freq
operator|<=
literal|0.0
condition|)
return|return
literal|0f
return|;
return|return
call|(
name|float
call|)
argument_list|(
literal|1
operator|+
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
name|Math
operator|.
name|log
argument_list|(
name|freq
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * compute the document length component    */
annotation|@
name|Override
DECL|method|ln
specifier|protected
name|float
name|ln
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
return|return
operator|(
name|stats
operator|.
name|getAvgFieldLength
argument_list|()
operator|+
name|this
operator|.
name|s
operator|)
operator|/
operator|(
name|stats
operator|.
name|getAvgFieldLength
argument_list|()
operator|+
name|docLen
operator|*
name|this
operator|.
name|s
operator|)
return|;
block|}
comment|/**    * compute the mixed term frequency and document length component    */
annotation|@
name|Override
DECL|method|tfln
specifier|protected
name|float
name|tfln
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
comment|/**    * compute the inverted document frequency component    */
annotation|@
name|Override
DECL|method|idf
specifier|protected
name|float
name|idf
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
operator|(
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
operator|+
literal|1.0
operator|)
operator|/
name|stats
operator|.
name|getDocFreq
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * compute the gamma component    */
annotation|@
name|Override
DECL|method|gamma
specifier|protected
name|float
name|gamma
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
return|return
literal|0f
return|;
block|}
block|}
end_class

end_unit

