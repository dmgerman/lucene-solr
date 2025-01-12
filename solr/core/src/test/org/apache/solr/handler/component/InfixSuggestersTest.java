begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|common
operator|.
name|util
operator|.
name|ExecutorUtil
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
name|spelling
operator|.
name|suggest
operator|.
name|RandomTestDictionaryFactory
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
name|spelling
operator|.
name|suggest
operator|.
name|SuggesterParams
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
name|update
operator|.
name|SolrCoreState
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
DECL|class|InfixSuggestersTest
specifier|public
class|class
name|InfixSuggestersTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|rh_analyzing_short
specifier|private
specifier|static
specifier|final
name|String
name|rh_analyzing_short
init|=
literal|"/suggest_analyzing_infix_short_dictionary"
decl_stmt|;
DECL|field|rh_analyzing_long
specifier|private
specifier|static
specifier|final
name|String
name|rh_analyzing_long
init|=
literal|"/suggest_analyzing_infix_long_dictionary"
decl_stmt|;
DECL|field|rh_blended_short
specifier|private
specifier|static
specifier|final
name|String
name|rh_blended_short
init|=
literal|"/suggest_blended_infix_short_dictionary"
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
literal|"solrconfig-infixsuggesters.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test2xBuildReload
specifier|public
name|void
name|test2xBuildReload
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTwoSuggestersBuildThenReload
specifier|public
name|void
name|testTwoSuggestersBuildThenReload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_blended_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildThen2xReload
specifier|public
name|void
name|testBuildThen2xReload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAnalyzingInfixSuggesterBuildThenReload
specifier|public
name|void
name|testAnalyzingInfixSuggesterBuildThenReload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlendedInfixSuggesterBuildThenReload
specifier|public
name|void
name|testBlendedInfixSuggesterBuildThenReload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_blended_short
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReloadDuringBuild
specifier|public
name|void
name|testReloadDuringBuild
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|executor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
literal|"AnalyzingInfixSuggesterTest"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Build the suggester in the background with a long dictionary
name|Future
name|job
init|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
name|SolrCoreState
operator|.
name|CoreIsClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_long
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|// Stop the dictionary's input iterator
name|System
operator|.
name|clearProperty
argument_list|(
name|RandomTestDictionaryFactory
operator|.
name|RandomTestDictionary
operator|.
name|getEnabledSysProp
argument_list|(
literal|"longRandomAnalyzingInfixSuggester"
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testShutdownDuringBuild
specifier|public
name|void
name|testShutdownDuringBuild
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|executor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
literal|"AnalyzingInfixSuggesterTest"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Build the suggester in the background with a long dictionary
name|Future
name|job
init|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
name|SolrCoreState
operator|.
name|CoreIsClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh_analyzing_long
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='command'][.='buildAll']"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// TODO: is there a better way to ensure that the build has begun?
name|h
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Stop the dictionary's input iterator
name|System
operator|.
name|clearProperty
argument_list|(
name|RandomTestDictionaryFactory
operator|.
name|RandomTestDictionary
operator|.
name|getEnabledSysProp
argument_list|(
literal|"longRandomAnalyzingInfixSuggester"
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-infixsuggesters.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
comment|// put the core back for other tests
block|}
block|}
block|}
end_class

end_unit

