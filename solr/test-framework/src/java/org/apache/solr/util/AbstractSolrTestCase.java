begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|HashSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|QuickPatchThreadsFilter
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
name|SolrIgnoredThreadsFilter
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
name|core
operator|.
name|SolrConfig
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import

begin_comment
comment|/**  * An Abstract base class that makes writing Solr JUnit tests "easier"  *  *<p>  * Test classes that subclass this need only specify the path to the  * schema.xml file (:TODO: the solrconfig.xml as well) and write some  * testMethods.  This class takes care of creating/destroying the index,  * and provides several assert methods to assist you.  *</p>  *  * @see #setUp  * @see #tearDown  */
end_comment

begin_class
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|SolrIgnoredThreadsFilter
operator|.
name|class
block|,
name|QuickPatchThreadsFilter
operator|.
name|class
block|}
argument_list|)
DECL|class|AbstractSolrTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSolrTestCase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrConfig
specifier|protected
name|SolrConfig
name|solrConfig
decl_stmt|;
comment|/**    * Subclasses can override this to change a test's solr home    * (default is in test-files)    */
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
return|;
block|}
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
comment|/** Causes an exception matching the regex pattern to not be logged. */
DECL|method|ignoreException
specifier|public
specifier|static
name|void
name|ignoreException
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
if|if
condition|(
name|SolrException
operator|.
name|ignorePatterns
operator|==
literal|null
condition|)
name|SolrException
operator|.
name|ignorePatterns
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|SolrException
operator|.
name|ignorePatterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
DECL|method|resetExceptionIgnores
specifier|public
specifier|static
name|void
name|resetExceptionIgnores
parameter_list|()
block|{
name|SolrException
operator|.
name|ignorePatterns
operator|=
literal|null
expr_stmt|;
name|ignoreException
argument_list|(
literal|"ignore_exception"
argument_list|)
expr_stmt|;
comment|// always ignore "ignore_exception"
block|}
comment|/** Subclasses that override setUp can optionally call this method    * to log the fact that their setUp process has ended.    */
annotation|@
name|Override
DECL|method|postSetUp
specifier|public
name|void
name|postSetUp
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####POSTSETUP "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Subclasses that override tearDown can optionally call this method    * to log the fact that the tearDown process has started.  This is necessary    * since subclasses will want to call super.tearDown() at the *end* of their    * tearDown method.    */
annotation|@
name|Override
DECL|method|preTearDown
specifier|public
name|void
name|preTearDown
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####PRETEARDOWN "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generates a simple&lt;add&gt;&lt;doc&gt;... XML String with the    * commitWithin attribute.    *    * @param commitWithin the value of the commitWithin attribute     * @param fieldsAndValues 0th and Even numbered args are fields names odds are field values.    * @see #add    * @see #doc    */
DECL|method|adoc
specifier|public
name|String
name|adoc
parameter_list|(
name|int
name|commitWithin
parameter_list|,
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
name|XmlDoc
name|d
init|=
name|doc
argument_list|(
name|fieldsAndValues
argument_list|)
decl_stmt|;
return|return
name|add
argument_list|(
name|d
argument_list|,
literal|"commitWithin"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|commitWithin
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an ID    *    * @see TestHarness#deleteById    */
DECL|method|delI
specifier|public
name|String
name|delI
parameter_list|(
name|String
name|id
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|TestHarness
operator|.
name|deleteById
argument_list|(
name|id
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an query    *    * @see TestHarness#deleteByQuery    */
DECL|method|delQ
specifier|public
name|String
name|delQ
parameter_list|(
name|String
name|q
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|TestHarness
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/** @see SolrTestCaseJ4#getFile */
DECL|method|getFile
specifier|public
specifier|static
name|File
name|getFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

