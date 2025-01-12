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
name|TestRerankBase
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
name|FieldValueFeature
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
name|ValueFeature
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
name|model
operator|.
name|LinearModel
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
name|search
operator|.
name|LTRQParserPlugin
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
name|ManagedResourceStorage
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
name|RestManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestModelManager
specifier|public
class|class
name|TestModelManager
extends|extends
name|TestRerankBase
block|{
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|setuptest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|tmpSolrHome
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RestManager
operator|.
name|Registry
name|registry
init|=
name|loader
operator|.
name|getManagedResourceRegistry
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Expected a non-null RestManager.Registry from the SolrResourceLoader!"
argument_list|,
name|registry
argument_list|)
expr_stmt|;
specifier|final
name|String
name|resourceId
init|=
literal|"/schema/fstore1"
decl_stmt|;
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|resourceId
argument_list|,
name|ManagedFeatureStore
operator|.
name|class
argument_list|,
operator|new
name|LTRQParserPlugin
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|resourceId2
init|=
literal|"/schema/mstore1"
decl_stmt|;
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|resourceId2
argument_list|,
name|ManagedModelStore
operator|.
name|class
argument_list|,
operator|new
name|LTRQParserPlugin
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RestManager
name|restManager
init|=
operator|new
name|RestManager
argument_list|()
decl_stmt|;
name|restManager
operator|.
name|init
argument_list|(
name|loader
argument_list|,
name|initArgs
argument_list|,
operator|new
name|ManagedResourceStorage
operator|.
name|InMemoryStorageIO
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ManagedResource
name|res
init|=
name|restManager
operator|.
name|getManagedResource
argument_list|(
name|resourceId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|res
operator|instanceof
name|ManagedFeatureStore
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|res
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestManagerEndpoints
specifier|public
name|void
name|testRestManagerEndpoints
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|TEST_FEATURE_STORE_NAME
init|=
literal|"TEST"
decl_stmt|;
comment|// relies on these ManagedResources being activated in the
comment|// schema-rest.xml used by this test
name|assertJQ
argument_list|(
literal|"/schema/managed"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|valueFeatureClassName
init|=
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|// Add features
name|String
name|feature
init|=
literal|"{\"name\": \"test1\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
decl_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|feature
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|feature
operator|=
literal|"{\"name\": \"test2\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
expr_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|feature
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|feature
operator|=
literal|"{\"name\": \"test3\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
expr_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|feature
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|feature
operator|=
literal|"{\"name\": \"test33\", \"store\": \""
operator|+
name|TEST_FEATURE_STORE_NAME
operator|+
literal|"\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
expr_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|feature
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|multipleFeatures
init|=
literal|"[{\"name\": \"test4\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
operator|+
literal|",{\"name\": \"test5\", \"class\": \""
operator|+
name|valueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} } ]"
decl_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|multipleFeatures
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fieldValueFeatureClassName
init|=
name|FieldValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|// Add bad feature (wrong params)_
specifier|final
name|String
name|badfeature
init|=
literal|"{\"name\": \"fvalue\", \"class\": \""
operator|+
name|fieldValueFeatureClassName
operator|+
literal|"\", \"params\": {\"value\": 1} }"
decl_stmt|;
name|assertJPut
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
name|badfeature
argument_list|,
literal|"/error/msg/=='No setter corrresponding to \\'value\\' in "
operator|+
name|fieldValueFeatureClassName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|linearModelClassName
init|=
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|// Add models
name|String
name|model
init|=
literal|"{ \"name\":\"testmodel1\", \"class\":\""
operator|+
name|linearModelClassName
operator|+
literal|"\", \"features\":[] }"
decl_stmt|;
comment|// fails since it does not have features
name|assertJPut
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
name|model
argument_list|,
literal|"/responseHeader/status==400"
argument_list|)
expr_stmt|;
comment|// fails since it does not have weights
name|model
operator|=
literal|"{ \"name\":\"testmodel2\", \"class\":\""
operator|+
name|linearModelClassName
operator|+
literal|"\", \"features\":[{\"name\":\"test1\"}, {\"name\":\"test2\"}] }"
expr_stmt|;
name|assertJPut
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
name|model
argument_list|,
literal|"/responseHeader/status==400"
argument_list|)
expr_stmt|;
comment|// success
name|model
operator|=
literal|"{ \"name\":\"testmodel3\", \"class\":\""
operator|+
name|linearModelClassName
operator|+
literal|"\", \"features\":[{\"name\":\"test1\"}, {\"name\":\"test2\"}],\"params\":{\"weights\":{\"test1\":1.5,\"test2\":2.0}}}"
expr_stmt|;
name|assertJPut
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
name|model
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// success
specifier|final
name|String
name|multipleModels
init|=
literal|"[{ \"name\":\"testmodel4\", \"class\":\""
operator|+
name|linearModelClassName
operator|+
literal|"\", \"features\":[{\"name\":\"test1\"}, {\"name\":\"test2\"}],\"params\":{\"weights\":{\"test1\":1.5,\"test2\":2.0}} }\n"
operator|+
literal|",{ \"name\":\"testmodel5\", \"class\":\""
operator|+
name|linearModelClassName
operator|+
literal|"\", \"features\":[{\"name\":\"test1\"}, {\"name\":\"test2\"}],\"params\":{\"weights\":{\"test1\":1.5,\"test2\":2.0}} } ]"
decl_stmt|;
name|assertJPut
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
name|multipleModels
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|qryResult
init|=
name|JQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|qryResult
operator|.
name|contains
argument_list|(
literal|"\"name\":\"testmodel3\""
argument_list|)
operator|&&
name|qryResult
operator|.
name|contains
argument_list|(
literal|"\"name\":\"testmodel4\""
argument_list|)
operator|&&
name|qryResult
operator|.
name|contains
argument_list|(
literal|"\"name\":\"testmodel5\""
argument_list|)
operator|)
assert|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/models/[0]/name=='testmodel3'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/models/[1]/name=='testmodel4'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/models/[2]/name=='testmodel5'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/testmodel3"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/testmodel4"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/testmodel5"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|,
literal|"/models==[]'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/featureStores==['"
operator|+
name|TEST_FEATURE_STORE_NAME
operator|+
literal|"','"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|,
literal|"/features/[0]/name=='test1'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|TEST_FEATURE_STORE_NAME
argument_list|,
literal|"/features/[0]/name=='test33'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|TEST_FEATURE_STORE_NAME
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/featureStores==[]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEndpointsFromFile
specifier|public
name|void
name|testEndpointsFromFile
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeatures
argument_list|(
literal|"features-linear.json"
argument_list|)
expr_stmt|;
name|loadModels
argument_list|(
literal|"linear-model.json"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|modelName
init|=
literal|"6029760550880411648"
decl_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
argument_list|,
literal|"/models/[0]/name=='"
operator|+
name|modelName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|,
literal|"/features/[0]/name=='title'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|,
literal|"/features/[1]/name=='description'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|modelName
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|delete
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/"
operator|+
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

