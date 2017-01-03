begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.feature
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|feature
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|FeatureLoggerTestUtils
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
name|store
operator|.
name|FeatureStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
DECL|class|TestFeatureLogging
specifier|public
class|class
name|TestFeatureLogging
extends|extends
name|TestRerankBase
block|{
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
name|setuptest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after
specifier|public
specifier|static
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|aftertest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeneratedFeatures
specifier|public
name|void
name|testGeneratedFeatures
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"value\":1.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"c2"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"value\":2.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"c3"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"value\":3.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"pop"
argument_list|,
name|FieldValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"field\":\"popularity\"}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"nomatch"
argument_list|,
name|SolrFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"q\":\"{!terms f=title}foobarbat\"}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"yesmatch"
argument_list|,
name|SolrFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"q\":\"{!terms f=popularity}2\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"sum1"
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c1"
block|,
literal|"c2"
block|,
literal|"c3"
block|}
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"weights\":{\"c1\":1.0,\"c2\":1.0,\"c3\":1.0}}"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|docs0fv_dense_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"c1"
argument_list|,
literal|"1.0"
argument_list|,
literal|"c2"
argument_list|,
literal|"2.0"
argument_list|,
literal|"c3"
argument_list|,
literal|"3.0"
argument_list|,
literal|"pop"
argument_list|,
literal|"2.0"
argument_list|,
literal|"nomatch"
argument_list|,
literal|"0.0"
argument_list|,
literal|"yesmatch"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs0fv_sparse_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"c1"
argument_list|,
literal|"1.0"
argument_list|,
literal|"c2"
argument_list|,
literal|"2.0"
argument_list|,
literal|"c3"
argument_list|,
literal|"3.0"
argument_list|,
literal|"pop"
argument_list|,
literal|"2.0"
argument_list|,
literal|"yesmatch"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs0fv_default_csv
init|=
name|chooseDefaultFeatureVector
argument_list|(
name|docs0fv_dense_csv
argument_list|,
name|docs0fv_sparse_csv
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"title,description,id,popularity,[fv]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=3 model=sum1}"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|query
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'title':'bloomberg bloomberg ', 'description':'bloomberg','id':'7', 'popularity':2,  '[fv]':'"
operator|+
name|docs0fv_default_csv
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"[fv]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=3 model=sum1}"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|query
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'[fv]':'"
operator|+
name|docs0fv_default_csv
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultStoreFeatureExtraction
specifier|public
name|void
name|testDefaultStoreFeatureExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"defaultf1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|FeatureStore
operator|.
name|DEFAULT_FEATURE_STORE_NAME
argument_list|,
literal|"{\"value\":1.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"store8f1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"store8"
argument_list|,
literal|"{\"value\":2.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"store9f1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"store9"
argument_list|,
literal|"{\"value\":3.0}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"store9m1"
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"store9f1"
block|}
argument_list|,
literal|"store9"
argument_list|,
literal|"{\"weights\":{\"store9f1\":1.0}}"
argument_list|)
expr_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"id:7"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// No store specified, use default store for extraction
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"fv:[fv]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'fv':'"
operator|+
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"defaultf1"
argument_list|,
literal|"1.0"
argument_list|)
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
comment|// Store specified, use store for extraction
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"fv:[fv store=store8]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'fv':'"
operator|+
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"store8f1"
argument_list|,
literal|"2.0"
argument_list|)
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
comment|// Store specified + model specified, use store for extraction
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=3 model=store9m1}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'fv':'"
operator|+
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"store8f1"
argument_list|,
literal|"2.0"
argument_list|)
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
comment|// No store specified + model specified, use model store for extraction
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"fv:[fv]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/=={'fv':'"
operator|+
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"store9f1"
argument_list|,
literal|"3.0"
argument_list|)
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeneratedGroup
specifier|public
name|void
name|testGeneratedGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"testgroup"
argument_list|,
literal|"{\"value\":1.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"c2"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"testgroup"
argument_list|,
literal|"{\"value\":2.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"c3"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"testgroup"
argument_list|,
literal|"{\"value\":3.0}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"pop"
argument_list|,
name|FieldValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"testgroup"
argument_list|,
literal|"{\"field\":\"popularity\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"sumgroup"
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c1"
block|,
literal|"c2"
block|,
literal|"c3"
block|}
argument_list|,
literal|"testgroup"
argument_list|,
literal|"{\"weights\":{\"c1\":1.0,\"c2\":1.0,\"c3\":1.0}}"
argument_list|)
expr_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,[fv]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"fv:[fv]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"group"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"group.field"
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=3 model=sumgroup}"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|docs0fv_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"c1"
argument_list|,
literal|"1.0"
argument_list|,
literal|"c2"
argument_list|,
literal|"2.0"
argument_list|,
literal|"c3"
argument_list|,
literal|"3.0"
argument_list|,
literal|"pop"
argument_list|,
literal|"5.0"
argument_list|)
decl_stmt|;
name|restTestHarness
operator|.
name|query
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/grouped/title/groups/[0]/doclist/docs/[0]/=={'fv':'"
operator|+
name|docs0fv_csv
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSparseDenseFeatures
specifier|public
name|void
name|testSparseDenseFeatures
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"match"
argument_list|,
name|SolrFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test4"
argument_list|,
literal|"{\"q\":\"{!terms f=title}different\"}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"c4"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test4"
argument_list|,
literal|"{\"value\":1.0}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"sum4"
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"match"
block|}
argument_list|,
literal|"test4"
argument_list|,
literal|"{\"weights\":{\"match\":1.0}}"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|docs0fv_sparse_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"match"
argument_list|,
literal|"1.0"
argument_list|,
literal|"c4"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs1fv_sparse_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"c4"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs0fv_dense_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"match"
argument_list|,
literal|"1.0"
argument_list|,
literal|"c4"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs1fv_dense_csv
init|=
name|FeatureLoggerTestUtils
operator|.
name|toFeatureVector
argument_list|(
literal|"match"
argument_list|,
literal|"0.0"
argument_list|,
literal|"c4"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs0fv_default_csv
init|=
name|chooseDefaultFeatureVector
argument_list|(
name|docs0fv_dense_csv
argument_list|,
name|docs0fv_sparse_csv
argument_list|)
decl_stmt|;
specifier|final
name|String
name|docs1fv_default_csv
init|=
name|chooseDefaultFeatureVector
argument_list|(
name|docs1fv_dense_csv
argument_list|,
name|docs1fv_sparse_csv
argument_list|)
decl_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=10 model=sum4}"
argument_list|)
expr_stmt|;
comment|//csv - no feature format specified i.e. use default
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score,fv:[fv store=test4]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/fv/=='"
operator|+
name|docs0fv_default_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[1]/fv/=='"
operator|+
name|docs1fv_default_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//csv - sparse feature format check
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score,fv:[fv store=test4 format=sparse]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/fv/=='"
operator|+
name|docs0fv_sparse_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[1]/fv/=='"
operator|+
name|docs1fv_sparse_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//csv - dense feature format check
name|query
operator|.
name|remove
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score,fv:[fv store=test4 format=dense]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[0]/fv/=='"
operator|+
name|docs0fv_dense_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/docs/[1]/fv/=='"
operator|+
name|docs1fv_dense_csv
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

