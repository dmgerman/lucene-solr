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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Language model based on the Jelinek-Mercer smoothing method. From Chengxiang  * Zhai and John Lafferty. 2001. A study of smoothing methods for language  * models applied to Ad Hoc information retrieval. In Proceedings of the 24th  * annual international ACM SIGIR conference on Research and development in  * information retrieval (SIGIR '01). ACM, New York, NY, USA, 334-342.  *<p>The model has a single parameter,&lambda;. According to said paper, the  * optimal value depends on both the collection and the query. The optimal value  * is around {@code 0.1} for title queries and {@code 0.7} for long queries.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|LMJelinekMercerSimilarity
specifier|public
class|class
name|LMJelinekMercerSimilarity
extends|extends
name|LMSimilarity
block|{
comment|/** The&lambda; parameter. */
DECL|field|lambda
specifier|private
specifier|final
name|float
name|lambda
decl_stmt|;
comment|/** Instantiates with the specified collectionModel and&lambda; parameter. */
DECL|method|LMJelinekMercerSimilarity
specifier|public
name|LMJelinekMercerSimilarity
parameter_list|(
name|CollectionModel
name|collectionModel
parameter_list|,
name|float
name|lambda
parameter_list|)
block|{
name|super
argument_list|(
name|collectionModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|lambda
operator|=
name|lambda
expr_stmt|;
block|}
comment|/** Instantiates with the specified&lambda; parameter. */
DECL|method|LMJelinekMercerSimilarity
specifier|public
name|LMJelinekMercerSimilarity
parameter_list|(
name|float
name|lambda
parameter_list|)
block|{
name|this
operator|.
name|lambda
operator|=
name|lambda
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|float
name|score
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
name|stats
operator|.
name|getBoost
argument_list|()
operator|*
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
operator|(
operator|(
literal|1
operator|-
name|lambda
operator|)
operator|*
name|freq
operator|/
name|docLen
operator|)
operator|/
operator|(
name|lambda
operator|*
operator|(
operator|(
name|LMStats
operator|)
name|stats
operator|)
operator|.
name|getCollectionProbability
argument_list|()
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
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
name|stats
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|match
argument_list|(
name|stats
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|match
argument_list|(
name|lambda
argument_list|,
literal|"lambda"
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|explain
argument_list|(
name|subs
argument_list|,
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the&lambda; parameter. */
DECL|method|getLambda
specifier|public
name|float
name|getLambda
parameter_list|()
block|{
return|return
name|lambda
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Jelinek-Mercer(%f)"
argument_list|,
name|getLambda
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

