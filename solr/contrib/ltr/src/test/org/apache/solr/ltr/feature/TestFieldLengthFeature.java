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
DECL|class|TestFieldLengthFeature
specifier|public
class|class
name|TestFieldLengthFeature
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
argument_list|,
literal|"description"
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
literal|"w2 2asd asdd didid"
argument_list|,
literal|"description"
argument_list|,
literal|"w2 2asd asdd didid"
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
argument_list|,
literal|"description"
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
argument_list|,
literal|"description"
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
argument_list|,
literal|"description"
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
argument_list|,
literal|"description"
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
argument_list|,
literal|"description"
argument_list|,
literal|"w1 w2 w3 w4 w5 w8"
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
literal|"w1 w1 w1 w2 w2 w8"
argument_list|,
literal|"description"
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
DECL|method|testIfFieldIsMissingInDocumentLengthIsZero
specifier|public
name|void
name|testIfFieldIsMissingInDocumentLengthIsZero
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add a document without the field 'description'
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"title"
argument_list|,
literal|"w10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"description-length2"
argument_list|,
name|FieldLengthFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"{\"field\":\"description\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"description-model2"
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
literal|"description-length2"
block|}
argument_list|,
literal|"{\"weights\":{\"description-length2\":1.0}}"
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
literal|"title:w10"
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
literal|"rq"
argument_list|,
literal|"{!ltr model=description-model2 reRankDocs=8}"
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
literal|"/response/docs/[0]/score==0.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIfFieldIsEmptyLengthIsZero
specifier|public
name|void
name|testIfFieldIsEmptyLengthIsZero
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add a document without the field 'description'
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"title"
argument_list|,
literal|"w11"
argument_list|,
literal|"description"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"description-length3"
argument_list|,
name|FieldLengthFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"{\"field\":\"description\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"description-model3"
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
literal|"description-length3"
block|}
argument_list|,
literal|"{\"weights\":{\"description-length3\":1.0}}"
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
literal|"title:w11"
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
literal|"rq"
argument_list|,
literal|"{!ltr model=description-model3 reRankDocs=8}"
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
literal|"/response/docs/[0]/score==0.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRanking
specifier|public
name|void
name|testRanking
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"title-length"
argument_list|,
name|FieldLengthFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"{\"field\":\"title\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"title-model"
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
literal|"title-length"
block|}
argument_list|,
literal|"{\"weights\":{\"title-length\":1.0}}"
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
comment|// Normal term match
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/response/numFound/==4"
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
literal|"/response/docs/[0]/id=='1'"
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
literal|"/response/docs/[1]/id=='8'"
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
literal|"/response/docs/[2]/id=='6'"
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
literal|"/response/docs/[3]/id=='7'"
argument_list|)
expr_stmt|;
comment|// Normal term match
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=title-model reRankDocs=4}"
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
literal|"/response/numFound/==4"
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
literal|"/response/docs/[0]/id=='8'"
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
literal|"/response/docs/[1]/id=='7'"
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
literal|"/response/docs/[2]/id=='6'"
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
literal|"/response/docs/[3]/id=='1'"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"rows"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"rq"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr model=title-model reRankDocs=8}"
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
literal|"/response/docs/[0]/id=='8'"
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
literal|"/response/docs/[1]/id=='7'"
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
literal|"/response/docs/[2]/id=='2'"
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
literal|"/response/docs/[3]/id=='6'"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"description-length"
argument_list|,
name|FieldLengthFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"{\"field\":\"description\"}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"description-model"
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
literal|"description-length"
block|}
argument_list|,
literal|"{\"weights\":{\"description-length\":1.0}}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:w1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"rq"
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"rows"
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
literal|"rq"
argument_list|,
literal|"{!ltr model=description-model reRankDocs=4}"
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
literal|"/response/numFound/==4"
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
literal|"/response/docs/[0]/id=='7'"
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
literal|"/response/docs/[1]/id=='8'"
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
literal|"/response/docs/[2]/id=='6'"
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
literal|"/response/docs/[3]/id=='1'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

