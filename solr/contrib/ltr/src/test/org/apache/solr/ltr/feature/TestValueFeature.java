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
DECL|class|TestValueFeature
specifier|public
class|class
name|TestValueFeature
extends|extends
name|TestRerankBase
block|{
annotation|@
name|BeforeClass
DECL|method|before
specifier|public
specifier|static
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|setuptest
argument_list|(
literal|"solrconfig-ltr.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"title"
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"title"
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"title"
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"title"
argument_list|,
literal|"w4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"title"
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"title"
argument_list|,
literal|"w1 w2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"title"
argument_list|,
literal|"w1 w2 w3 w4 w5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"title"
argument_list|,
literal|"w1 w1 w1 w2 w2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
DECL|method|testValueFeatureWithEmptyValue
specifier|public
name|void
name|testValueFeatureWithEmptyValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RuntimeException
name|expectedException
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"mismatch: '0'!='500' @ responseHeader/status"
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|"{\"value\":\"\"}"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testValueFeatureWithEmptyValue failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValueFeatureWithWhitespaceValue
specifier|public
name|void
name|testValueFeatureWithWhitespaceValue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RuntimeException
name|expectedException
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"mismatch: '0'!='500' @ responseHeader/status"
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|"{\"value\":\" \"}"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testValueFeatureWithWhitespaceValue failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRerankingWithConstantValueFeatureReplacesDocScore
specifier|public
name|void
name|testRerankingWithConstantValueFeatureReplacesDocScore
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"c3"
argument_list|,
literal|"{\"value\":2}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"m3"
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
literal|"c3"
block|}
argument_list|,
literal|"c3"
argument_list|,
literal|"{\"weights\":{\"c3\":1.0}}"
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
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*, score"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=m3 reRankDocs=4}"
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
literal|"/response/docs/[0]/score==2.0"
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
literal|"/response/docs/[1]/score==2.0"
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
literal|"/response/docs/[2]/score==2.0"
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
literal|"/response/docs/[3]/score==2.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRerankingWithEfiValueFeatureReplacesDocScore
specifier|public
name|void
name|testRerankingWithEfiValueFeatureReplacesDocScore
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c6"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"c6"
argument_list|,
literal|"{\"value\":\"${val6}\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"m6"
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
literal|"c6"
block|}
argument_list|,
literal|"c6"
argument_list|,
literal|"{\"weights\":{\"c6\":1.0}}"
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
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*, score"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=m6 reRankDocs=4 efi.val6='2'}"
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
literal|"/response/docs/[0]/score==2.0"
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
literal|"/response/docs/[1]/score==2.0"
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
literal|"/response/docs/[2]/score==2.0"
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
literal|"/response/docs/[3]/score==2.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValueFeatureImplicitlyNotRequiredShouldReturnOkStatusCode
specifier|public
name|void
name|testValueFeatureImplicitlyNotRequiredShouldReturnOkStatusCode
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c5"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"c5"
argument_list|,
literal|"{\"value\":\"${val6}\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"m5"
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
literal|"c5"
block|}
argument_list|,
literal|"c5"
argument_list|,
literal|"{\"weights\":{\"c5\":1.0}}"
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
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*, score,fvonly:[fvonly]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=m5 reRankDocs=4}"
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
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValueFeatureExplictlyNotRequiredShouldReturnOkStatusCode
specifier|public
name|void
name|testValueFeatureExplictlyNotRequiredShouldReturnOkStatusCode
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c7"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"c7"
argument_list|,
literal|"{\"value\":\"${val7}\",\"required\":false}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"m7"
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
literal|"c7"
block|}
argument_list|,
literal|"c7"
argument_list|,
literal|"{\"weights\":{\"c7\":1.0}}"
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
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*, score,fvonly:[fvonly]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=m7 reRankDocs=4}"
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
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValueFeatureRequiredShouldReturn400StatusCode
specifier|public
name|void
name|testValueFeatureRequiredShouldReturn400StatusCode
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"c8"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"c8"
argument_list|,
literal|"{\"value\":\"${val8}\",\"required\":true}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"m8"
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
literal|"c8"
block|}
argument_list|,
literal|"c8"
argument_list|,
literal|"{\"weights\":{\"c8\":1.0}}"
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
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*, score,fvonly:[fvonly]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=m8 reRankDocs=4}"
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
literal|"/responseHeader/status==400"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

