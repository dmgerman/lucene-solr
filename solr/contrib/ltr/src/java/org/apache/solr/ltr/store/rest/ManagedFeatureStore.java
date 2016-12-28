begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.store.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|store
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|feature
operator|.
name|Feature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|store
operator|.
name|FeatureStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|BaseSolrResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|ManagedResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|ManagedResourceObserver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|ManagedResourceStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Managed resource for a storing a feature.  */
end_comment

begin_class
DECL|class|ManagedFeatureStore
specifier|public
class|class
name|ManagedFeatureStore
extends|extends
name|ManagedResource
implements|implements
name|ManagedResource
operator|.
name|ChildResourceSupport
block|{
DECL|method|registerManagedFeatureStore
specifier|public
specifier|static
name|void
name|registerManagedFeatureStore
parameter_list|(
name|SolrResourceLoader
name|solrResourceLoader
parameter_list|,
name|ManagedResourceObserver
name|managedResourceObserver
parameter_list|)
block|{
name|solrResourceLoader
operator|.
name|getManagedResourceRegistry
argument_list|()
operator|.
name|registerManagedResource
argument_list|(
name|REST_END_POINT
argument_list|,
name|ManagedFeatureStore
operator|.
name|class
argument_list|,
name|managedResourceObserver
argument_list|)
expr_stmt|;
block|}
DECL|method|getManagedFeatureStore
specifier|public
specifier|static
name|ManagedFeatureStore
name|getManagedFeatureStore
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
return|return
operator|(
name|ManagedFeatureStore
operator|)
name|core
operator|.
name|getRestManager
argument_list|()
operator|.
name|getManagedResource
argument_list|(
name|REST_END_POINT
argument_list|)
return|;
block|}
comment|/** the feature store rest endpoint **/
DECL|field|REST_END_POINT
specifier|public
specifier|static
specifier|final
name|String
name|REST_END_POINT
init|=
literal|"/schema/feature-store"
decl_stmt|;
comment|/** name of the attribute containing the feature class **/
DECL|field|CLASS_KEY
specifier|static
specifier|final
name|String
name|CLASS_KEY
init|=
literal|"class"
decl_stmt|;
comment|/** name of the attribute containing the feature name **/
DECL|field|NAME_KEY
specifier|static
specifier|final
name|String
name|NAME_KEY
init|=
literal|"name"
decl_stmt|;
comment|/** name of the attribute containing the feature params **/
DECL|field|PARAMS_KEY
specifier|static
specifier|final
name|String
name|PARAMS_KEY
init|=
literal|"params"
decl_stmt|;
comment|/** name of the attribute containing the feature store used **/
DECL|field|FEATURE_STORE_NAME_KEY
specifier|static
specifier|final
name|String
name|FEATURE_STORE_NAME_KEY
init|=
literal|"store"
decl_stmt|;
DECL|field|stores
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FeatureStore
argument_list|>
name|stores
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Managed feature store: the name of the attribute containing all the feature    * stores    **/
DECL|field|FEATURE_STORE_JSON_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FEATURE_STORE_JSON_FIELD
init|=
literal|"featureStores"
decl_stmt|;
comment|/**    * Managed feature store: the name of the attribute containing all the    * features of a feature store    **/
DECL|field|FEATURES_JSON_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FEATURES_JSON_FIELD
init|=
literal|"features"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|ManagedFeatureStore
specifier|public
name|ManagedFeatureStore
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|ManagedResourceStorage
operator|.
name|StorageIO
name|storageIO
parameter_list|)
throws|throws
name|SolrException
block|{
name|super
argument_list|(
name|resourceId
argument_list|,
name|loader
argument_list|,
name|storageIO
argument_list|)
expr_stmt|;
block|}
DECL|method|getFeatureStore
specifier|public
specifier|synchronized
name|FeatureStore
name|getFeatureStore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|stores
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|stores
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|FeatureStore
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stores
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onManagedDataLoadedFromStorage
specifier|protected
name|void
name|onManagedDataLoadedFromStorage
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|managedInitArgs
parameter_list|,
name|Object
name|managedData
parameter_list|)
throws|throws
name|SolrException
block|{
name|stores
operator|.
name|clear
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"------ managed feature ~ loading ------"
argument_list|)
expr_stmt|;
if|if
condition|(
name|managedData
operator|instanceof
name|List
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|up
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|managedData
decl_stmt|;
for|for
control|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|u
range|:
name|up
control|)
block|{
specifier|final
name|String
name|featureStore
init|=
operator|(
name|String
operator|)
name|u
operator|.
name|get
argument_list|(
name|FEATURE_STORE_NAME_KEY
argument_list|)
decl_stmt|;
name|addFeature
argument_list|(
name|u
argument_list|,
name|featureStore
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addFeature
specifier|public
specifier|synchronized
name|void
name|addFeature
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|featureStore
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"register feature based on {}"
argument_list|,
name|map
argument_list|)
expr_stmt|;
specifier|final
name|FeatureStore
name|fstore
init|=
name|getFeatureStore
argument_list|(
name|featureStore
argument_list|)
decl_stmt|;
specifier|final
name|Feature
name|feature
init|=
name|fromFeatureMap
argument_list|(
name|solrResourceLoader
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|fstore
operator|.
name|add
argument_list|(
name|feature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|applyUpdatesToManagedData
specifier|public
name|Object
name|applyUpdatesToManagedData
parameter_list|(
name|Object
name|updates
parameter_list|)
block|{
if|if
condition|(
name|updates
operator|instanceof
name|List
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|up
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|updates
decl_stmt|;
for|for
control|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|u
range|:
name|up
control|)
block|{
specifier|final
name|String
name|featureStore
init|=
operator|(
name|String
operator|)
name|u
operator|.
name|get
argument_list|(
name|FEATURE_STORE_NAME_KEY
argument_list|)
decl_stmt|;
name|addFeature
argument_list|(
name|u
argument_list|,
name|featureStore
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|updates
operator|instanceof
name|Map
condition|)
block|{
comment|// a unique feature
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|updatesMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|updates
decl_stmt|;
specifier|final
name|String
name|featureStore
init|=
operator|(
name|String
operator|)
name|updatesMap
operator|.
name|get
argument_list|(
name|FEATURE_STORE_NAME_KEY
argument_list|)
decl_stmt|;
name|addFeature
argument_list|(
name|updatesMap
argument_list|,
name|featureStore
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|features
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FeatureStore
name|fs
range|:
name|stores
operator|.
name|values
argument_list|()
control|)
block|{
name|features
operator|.
name|addAll
argument_list|(
name|featuresAsManagedResources
argument_list|(
name|fs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|features
return|;
block|}
annotation|@
name|Override
DECL|method|doDeleteChild
specifier|public
specifier|synchronized
name|void
name|doDeleteChild
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{
if|if
condition|(
name|stores
operator|.
name|containsKey
argument_list|(
name|childId
argument_list|)
condition|)
block|{
name|stores
operator|.
name|remove
argument_list|(
name|childId
argument_list|)
expr_stmt|;
block|}
name|storeManagedData
argument_list|(
name|applyUpdatesToManagedData
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called to retrieve a named part (the given childId) of the resource at the    * given endpoint. Note: since we have a unique child feature store we ignore    * the childId.    */
annotation|@
name|Override
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{
specifier|final
name|SolrQueryResponse
name|response
init|=
name|endpoint
operator|.
name|getSolrResponse
argument_list|()
decl_stmt|;
comment|// If no feature store specified, show all the feature stores available
if|if
condition|(
name|childId
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|add
argument_list|(
name|FEATURE_STORE_JSON_FIELD
argument_list|,
name|stores
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|FeatureStore
name|store
init|=
name|getFeatureStore
argument_list|(
name|childId
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"missing feature store ["
operator|+
name|childId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|response
operator|.
name|add
argument_list|(
name|FEATURES_JSON_FIELD
argument_list|,
name|featuresAsManagedResources
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|featuresAsManagedResources
specifier|private
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|featuresAsManagedResources
parameter_list|(
name|FeatureStore
name|store
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|storedFeatures
init|=
name|store
operator|.
name|getFeatures
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|features
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|storedFeatures
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Feature
name|f
range|:
name|storedFeatures
control|)
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|toFeatureMap
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|FEATURE_STORE_NAME_KEY
argument_list|,
name|store
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|features
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|features
return|;
block|}
DECL|method|toFeatureMap
specifier|private
specifier|static
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toFeatureMap
parameter_list|(
name|Feature
name|feat
parameter_list|)
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|o
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|4
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
comment|// 1 extra for caller to add store
name|o
operator|.
name|put
argument_list|(
name|NAME_KEY
argument_list|,
name|feat
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|o
operator|.
name|put
argument_list|(
name|CLASS_KEY
argument_list|,
name|feat
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|o
operator|.
name|put
argument_list|(
name|PARAMS_KEY
argument_list|,
name|feat
operator|.
name|paramsToMap
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|o
return|;
block|}
DECL|method|fromFeatureMap
specifier|private
specifier|static
name|Feature
name|fromFeatureMap
parameter_list|(
name|SolrResourceLoader
name|solrResourceLoader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|featureMap
parameter_list|)
block|{
specifier|final
name|String
name|className
init|=
operator|(
name|String
operator|)
name|featureMap
operator|.
name|get
argument_list|(
name|CLASS_KEY
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
operator|(
name|String
operator|)
name|featureMap
operator|.
name|get
argument_list|(
name|NAME_KEY
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|featureMap
operator|.
name|get
argument_list|(
name|PARAMS_KEY
argument_list|)
decl_stmt|;
return|return
name|Feature
operator|.
name|getInstance
argument_list|(
name|solrResourceLoader
argument_list|,
name|className
argument_list|,
name|name
argument_list|,
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

