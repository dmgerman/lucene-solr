begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|SolrTestCaseJ4
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

begin_class
DECL|class|TestFileDictionaryLookup
specifier|public
class|class
name|TestFileDictionaryLookup
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|REQUEST_URI
specifier|static
specifier|final
name|String
name|REQUEST_URI
init|=
literal|"/fuzzy_suggest_analyzing_with_file_dict"
decl_stmt|;
DECL|field|DICT_NAME
specifier|static
specifier|final
name|String
name|DICT_NAME
init|=
literal|"fuzzy_suggest_analyzing_with_file_dict"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-phrasesuggest.xml"
argument_list|,
literal|"schema-phrasesuggest.xml"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|""
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefault
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests to demonstrate default maxEdit parameter (value: 1), control for testWithMaxEdit2
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|"chagn"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chagn']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chagn']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='chance']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chagn']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='change']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|"chacn"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chacn']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chacn']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='chance']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chacn']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='change']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|"chagr"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chagr']/int[@name='numFound'][.='1']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chagr']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='charge']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|"chanr"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='chanr']/int[@name='numFound'][.='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|REQUEST_URI
argument_list|,
literal|"q"
argument_list|,
literal|"cyhnce"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
name|DICT_NAME
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='"
operator|+
name|DICT_NAME
operator|+
literal|"']/lst[@name='cyhnce']/int[@name='numFound'][.='0']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

