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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * FeatureLogger can be registered in a model and provide a strategy for logging  * the feature values.  */
end_comment

begin_class
DECL|class|FeatureLogger
specifier|public
specifier|abstract
class|class
name|FeatureLogger
block|{
comment|/** the name of the cache using for storing the feature value **/
DECL|field|fvCacheName
specifier|private
specifier|final
name|String
name|fvCacheName
decl_stmt|;
DECL|enum|FeatureFormat
DECL|enum constant|DENSE
DECL|enum constant|SPARSE
specifier|public
enum|enum
name|FeatureFormat
block|{
name|DENSE
block|,
name|SPARSE
block|}
empty_stmt|;
DECL|field|featureFormat
specifier|protected
specifier|final
name|FeatureFormat
name|featureFormat
decl_stmt|;
DECL|method|FeatureLogger
specifier|protected
name|FeatureLogger
parameter_list|(
name|String
name|fvCacheName
parameter_list|,
name|FeatureFormat
name|f
parameter_list|)
block|{
name|this
operator|.
name|fvCacheName
operator|=
name|fvCacheName
expr_stmt|;
name|this
operator|.
name|featureFormat
operator|=
name|f
expr_stmt|;
block|}
comment|/**    * Log will be called every time that the model generates the feature values    * for a document and a query.    *    * @param docid    *          Solr document id whose features we are saving    * @param featuresInfo    *          List of all the {@link LTRScoringQuery.FeatureInfo} objects which contain name and value    *          for all the features triggered by the result set    * @return true if the logger successfully logged the features, false    *         otherwise.    */
DECL|method|log
specifier|public
name|boolean
name|log
parameter_list|(
name|int
name|docid
parameter_list|,
name|LTRScoringQuery
name|scoringQuery
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|LTRScoringQuery
operator|.
name|FeatureInfo
index|[]
name|featuresInfo
parameter_list|)
block|{
specifier|final
name|String
name|featureVector
init|=
name|makeFeatureVector
argument_list|(
name|featuresInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|featureVector
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|searcher
operator|.
name|cacheInsert
argument_list|(
name|fvCacheName
argument_list|,
name|fvCacheKey
argument_list|(
name|scoringQuery
argument_list|,
name|docid
argument_list|)
argument_list|,
name|featureVector
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|makeFeatureVector
specifier|public
specifier|abstract
name|String
name|makeFeatureVector
parameter_list|(
name|LTRScoringQuery
operator|.
name|FeatureInfo
index|[]
name|featuresInfo
parameter_list|)
function_decl|;
DECL|method|fvCacheKey
specifier|private
specifier|static
name|int
name|fvCacheKey
parameter_list|(
name|LTRScoringQuery
name|scoringQuery
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
return|return
name|scoringQuery
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
literal|31
operator|*
name|docid
operator|)
return|;
block|}
comment|/**    * populate the document with its feature vector    *    * @param docid    *          Solr document id    * @return String representation of the list of features calculated for docid    */
DECL|method|getFeatureVector
specifier|public
name|String
name|getFeatureVector
parameter_list|(
name|int
name|docid
parameter_list|,
name|LTRScoringQuery
name|scoringQuery
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|searcher
operator|.
name|cacheLookup
argument_list|(
name|fvCacheName
argument_list|,
name|fvCacheKey
argument_list|(
name|scoringQuery
argument_list|,
name|docid
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

