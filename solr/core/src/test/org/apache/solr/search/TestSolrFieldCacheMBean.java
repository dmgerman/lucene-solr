begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_class
DECL|class|TestSolrFieldCacheMBean
specifier|public
class|class
name|TestSolrFieldCacheMBean
extends|extends
name|SolrTestCaseJ4
block|{
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntryList
specifier|public
name|void
name|testEntryList
parameter_list|()
throws|throws
name|Exception
block|{
comment|// ensure entries to FieldCache
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"id0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// Test with entry list enabled
name|assertEntryListIncluded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Test again with entry list disabled
name|System
operator|.
name|setProperty
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryList"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEntryListNotIncluded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryList"
argument_list|)
expr_stmt|;
block|}
comment|// Test with entry list enabled for jmx
name|assertEntryListIncluded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Test with entry list disabled for jmx
name|System
operator|.
name|setProperty
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryListJmx"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEntryListNotIncluded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"disableSolrFieldCacheMBeanEntryListJmx"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEntryListIncluded
specifier|private
name|void
name|assertEntryListIncluded
parameter_list|(
name|boolean
name|checkJmx
parameter_list|)
block|{
name|SolrFieldCacheMBean
name|mbean
init|=
operator|new
name|SolrFieldCacheMBean
argument_list|()
decl_stmt|;
name|NamedList
name|stats
init|=
name|checkJmx
condition|?
name|mbean
operator|.
name|getStatisticsForJmx
argument_list|()
else|:
name|mbean
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"entries_count"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
assert|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"total_size"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"entry#0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEntryListNotIncluded
specifier|private
name|void
name|assertEntryListNotIncluded
parameter_list|(
name|boolean
name|checkJmx
parameter_list|)
block|{
name|SolrFieldCacheMBean
name|mbean
init|=
operator|new
name|SolrFieldCacheMBean
argument_list|()
decl_stmt|;
name|NamedList
name|stats
init|=
name|checkJmx
condition|?
name|mbean
operator|.
name|getStatisticsForJmx
argument_list|()
else|:
name|mbean
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"entries_count"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
assert|;
name|assertNull
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"total_size"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"entry#0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

