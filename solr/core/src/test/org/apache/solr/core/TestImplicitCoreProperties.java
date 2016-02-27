begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
DECL|class|TestImplicitCoreProperties
specifier|public
class|class
name|TestImplicitCoreProperties
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|cc
specifier|private
specifier|static
name|CoreContainer
name|cc
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupContainer
specifier|public
specifier|static
name|void
name|setupContainer
parameter_list|()
block|{
name|cc
operator|=
name|createCoreContainer
argument_list|(
literal|"collection1"
argument_list|,
literal|"data"
argument_list|,
literal|"solrconfig-implicitproperties.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownContainer
specifier|public
specifier|static
name|void
name|teardownContainer
parameter_list|()
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testImplicitPropertiesAreSubstitutedInSolrConfig
specifier|public
name|void
name|testImplicitPropertiesAreSubstitutedInSolrConfig
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//str[@name='dummy1'][.='collection1']"
argument_list|,
literal|"//str[@name='dummy2'][.='data']"
argument_list|,
literal|"//str[@name='dummy3'][.='solrconfig-implicitproperties.xml']"
argument_list|,
literal|"//str[@name='dummy4'][.='schema.xml']"
argument_list|,
literal|"//str[@name='dummy5'][.='false']"
argument_list|)
expr_stmt|;
block|}
comment|// SOLR-5279
annotation|@
name|Test
DECL|method|testPropertiesArePersistedAcrossReload
specifier|public
name|void
name|testPropertiesArePersistedAcrossReload
parameter_list|()
block|{
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//str[@name='dummy1'][.='collection1']"
argument_list|,
literal|"//str[@name='dummy2'][.='data']"
argument_list|,
literal|"//str[@name='dummy3'][.='solrconfig-implicitproperties.xml']"
argument_list|,
literal|"//str[@name='dummy4'][.='schema.xml']"
argument_list|,
literal|"//str[@name='dummy5'][.='false']"
argument_list|)
expr_stmt|;
block|}
comment|// SOLR-8712
annotation|@
name|Test
DECL|method|testDefaultProperties
specifier|public
name|void
name|testDefaultProperties
parameter_list|()
block|{
name|Properties
name|props
init|=
name|cc
operator|.
name|getCoreDescriptor
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|getSubstitutableProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"collection1"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"solr.core.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"solr.core.instanceDir not set correctly"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"solr.core.instanceDir"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

