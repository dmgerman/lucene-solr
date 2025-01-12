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
name|SolrTestCaseJ4
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
name|feature
operator|.
name|FeatureException
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
name|OriginalScoreFeature
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
name|ltr
operator|.
name|store
operator|.
name|rest
operator|.
name|ManagedFeatureStore
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
DECL|class|TestManagedFeatureStore
specifier|public
class|class
name|TestManagedFeatureStore
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|fstore
specifier|private
specifier|static
name|ManagedFeatureStore
name|fstore
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-ltr.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|fstore
operator|=
name|ManagedFeatureStore
operator|.
name|getManagedFeatureStore
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|createMap
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ManagedFeatureStore
operator|.
name|NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ManagedFeatureStore
operator|.
name|CLASS_KEY
argument_list|,
name|className
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|ManagedFeatureStore
operator|.
name|PARAMS_KEY
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
annotation|@
name|Test
DECL|method|testDefaultFeatureStoreName
specifier|public
name|void
name|testDefaultFeatureStoreName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"_DEFAULT_"
argument_list|,
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|)
expr_stmt|;
specifier|final
name|FeatureStore
name|expectedFeatureStore
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|)
decl_stmt|;
specifier|final
name|FeatureStore
name|actualFeatureStore
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getFeatureStore(null) should return the default feature store"
argument_list|,
name|expectedFeatureStore
argument_list|,
name|actualFeatureStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFeatureStoreAdd
specifier|public
name|void
name|testFeatureStoreAdd
parameter_list|()
throws|throws
name|FeatureException
block|{
specifier|final
name|FeatureStore
name|fs
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
literal|"fstore-testFeature"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
literal|"c"
operator|+
name|i
decl_stmt|;
name|fstore
operator|.
name|addFeature
argument_list|(
name|createMap
argument_list|(
name|name
argument_list|,
name|OriginalScoreFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fstore-testFeature"
argument_list|)
expr_stmt|;
specifier|final
name|Feature
name|f
init|=
name|fs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fs
operator|.
name|getFeatures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFeatureStoreGet
specifier|public
name|void
name|testFeatureStoreGet
parameter_list|()
throws|throws
name|FeatureException
block|{
specifier|final
name|FeatureStore
name|fs
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
literal|"fstore-testFeature2"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
name|i
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"c"
operator|+
name|i
decl_stmt|;
name|fstore
operator|.
name|addFeature
argument_list|(
name|createMap
argument_list|(
name|name
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|params
argument_list|)
argument_list|,
literal|"fstore-testFeature2"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Feature
name|f
init|=
name|fs
operator|.
name|get
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"c"
operator|+
name|i
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|instanceof
name|ValueFeature
argument_list|)
expr_stmt|;
specifier|final
name|ValueFeature
name|vf
init|=
operator|(
name|ValueFeature
operator|)
name|f
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|vf
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMissingFeatureReturnsNull
specifier|public
name|void
name|testMissingFeatureReturnsNull
parameter_list|()
block|{
specifier|final
name|FeatureStore
name|fs
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
literal|"fstore-testFeature3"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
name|i
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"testc"
operator|+
operator|(
name|float
operator|)
name|i
decl_stmt|;
name|fstore
operator|.
name|addFeature
argument_list|(
name|createMap
argument_list|(
name|name
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|params
argument_list|)
argument_list|,
literal|"fstore-testFeature3"
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|fs
operator|.
name|get
argument_list|(
literal|"missing_feature_name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getInstanceTest
specifier|public
name|void
name|getInstanceTest
parameter_list|()
throws|throws
name|FeatureException
block|{
name|fstore
operator|.
name|addFeature
argument_list|(
name|createMap
argument_list|(
literal|"test"
argument_list|,
name|OriginalScoreFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"testFstore"
argument_list|)
expr_stmt|;
specifier|final
name|Feature
name|feature
init|=
name|fstore
operator|.
name|getFeatureStore
argument_list|(
literal|"testFstore"
argument_list|)
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|feature
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|feature
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OriginalScoreFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|feature
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getInvalidInstanceTest
specifier|public
name|void
name|getInvalidInstanceTest
parameter_list|()
block|{
specifier|final
name|String
name|nonExistingClassName
init|=
literal|"org.apache.solr.ltr.feature.LOLFeature"
decl_stmt|;
specifier|final
name|ClassNotFoundException
name|expectedException
init|=
operator|new
name|ClassNotFoundException
argument_list|(
name|nonExistingClassName
argument_list|)
decl_stmt|;
try|try
block|{
name|fstore
operator|.
name|addFeature
argument_list|(
name|createMap
argument_list|(
literal|"test"
argument_list|,
name|nonExistingClassName
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"testFstore2"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"getInvalidInstanceTest failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|actualException
parameter_list|)
block|{
name|Throwable
name|rootError
init|=
name|getRootCause
argument_list|(
name|actualException
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|rootError
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

