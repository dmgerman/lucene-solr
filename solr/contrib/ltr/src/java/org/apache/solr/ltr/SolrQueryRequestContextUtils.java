begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_class
DECL|class|SolrQueryRequestContextUtils
specifier|public
class|class
name|SolrQueryRequestContextUtils
block|{
comment|/** key prefix to reduce possibility of clash with other code's key choices **/
DECL|field|LTR_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|LTR_PREFIX
init|=
literal|"ltr."
decl_stmt|;
comment|/** key of the feature logger in the request context **/
DECL|field|FEATURE_LOGGER
specifier|private
specifier|static
specifier|final
name|String
name|FEATURE_LOGGER
init|=
name|LTR_PREFIX
operator|+
literal|"feature_logger"
decl_stmt|;
comment|/** key of the scoring query in the request context **/
DECL|field|SCORING_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|SCORING_QUERY
init|=
name|LTR_PREFIX
operator|+
literal|"scoring_query"
decl_stmt|;
comment|/** key of the isExtractingFeatures flag in the request context **/
DECL|field|IS_EXTRACTING_FEATURES
specifier|private
specifier|static
specifier|final
name|String
name|IS_EXTRACTING_FEATURES
init|=
name|LTR_PREFIX
operator|+
literal|"isExtractingFeatures"
decl_stmt|;
comment|/** key of the feature vector store name in the request context **/
DECL|field|STORE
specifier|private
specifier|static
specifier|final
name|String
name|STORE
init|=
name|LTR_PREFIX
operator|+
literal|"store"
decl_stmt|;
comment|/** feature logger accessors **/
DECL|method|setFeatureLogger
specifier|public
specifier|static
name|void
name|setFeatureLogger
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|FeatureLogger
argument_list|<
name|?
argument_list|>
name|featureLogger
parameter_list|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|FEATURE_LOGGER
argument_list|,
name|featureLogger
argument_list|)
expr_stmt|;
block|}
DECL|method|getFeatureLogger
specifier|public
specifier|static
name|FeatureLogger
argument_list|<
name|?
argument_list|>
name|getFeatureLogger
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|(
name|FeatureLogger
argument_list|<
name|?
argument_list|>
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|FEATURE_LOGGER
argument_list|)
return|;
block|}
comment|/** scoring query accessors **/
DECL|method|setScoringQuery
specifier|public
specifier|static
name|void
name|setScoringQuery
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|LTRScoringQuery
name|scoringQuery
parameter_list|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|SCORING_QUERY
argument_list|,
name|scoringQuery
argument_list|)
expr_stmt|;
block|}
DECL|method|getScoringQuery
specifier|public
specifier|static
name|LTRScoringQuery
name|getScoringQuery
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|(
name|LTRScoringQuery
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|SCORING_QUERY
argument_list|)
return|;
block|}
comment|/** isExtractingFeatures flag accessors **/
DECL|method|setIsExtractingFeatures
specifier|public
specifier|static
name|void
name|setIsExtractingFeatures
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|IS_EXTRACTING_FEATURES
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
DECL|method|clearIsExtractingFeatures
specifier|public
specifier|static
name|void
name|clearIsExtractingFeatures
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|IS_EXTRACTING_FEATURES
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
DECL|method|isExtractingFeatures
specifier|public
specifier|static
name|boolean
name|isExtractingFeatures
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|IS_EXTRACTING_FEATURES
argument_list|)
argument_list|)
return|;
block|}
comment|/** feature vector store name accessors **/
DECL|method|setFvStoreName
specifier|public
specifier|static
name|void
name|setFvStoreName
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|fvStoreName
parameter_list|)
block|{
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|STORE
argument_list|,
name|fvStoreName
argument_list|)
expr_stmt|;
block|}
DECL|method|getFvStoreName
specifier|public
specifier|static
name|String
name|getFvStoreName
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|STORE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

