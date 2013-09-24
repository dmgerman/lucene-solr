begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|java
operator|.
name|io
operator|.
name|File
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|TestImplicitCoreProperties
specifier|public
class|class
name|TestImplicitCoreProperties
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|SOLRXML
specifier|public
specifier|static
specifier|final
name|String
name|SOLRXML
init|=
literal|"<solr><cores><core name=\"collection1\" instanceDir=\"collection1\" config=\"solrconfig-implicitproperties.xml\"/></cores></solr>"
decl_stmt|;
annotation|@
name|Test
DECL|method|testImplicitPropertiesAreSubstitutedInSolrConfig
specifier|public
name|void
name|testImplicitPropertiesAreSubstitutedInSolrConfig
parameter_list|()
block|{
name|CoreContainer
name|cc
init|=
name|createCoreContainer
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
name|SOLRXML
argument_list|)
decl_stmt|;
try|try
block|{
name|cc
operator|.
name|load
argument_list|()
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
literal|"//str[@name='dummy2'][.='data"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"']"
argument_list|,
literal|"//str[@name='dummy3'][.='solrconfig-implicitproperties.xml']"
argument_list|,
literal|"//str[@name='dummy4'][.='schema.xml']"
argument_list|,
literal|"//str[@name='dummy5'][.='false']"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

