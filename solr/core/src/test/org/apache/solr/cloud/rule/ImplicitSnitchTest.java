begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|LuceneTestCase
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
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
name|cloud
operator|.
name|rule
operator|.
name|ImplicitSnitch
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
name|cloud
operator|.
name|rule
operator|.
name|RemoteCallback
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
name|cloud
operator|.
name|rule
operator|.
name|SnitchContext
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
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|ImplicitSnitchTest
specifier|public
class|class
name|ImplicitSnitchTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|snitch
specifier|private
name|ImplicitSnitch
name|snitch
decl_stmt|;
DECL|field|context
specifier|private
name|SnitchContext
name|context
decl_stmt|;
DECL|field|IP_1
specifier|private
specifier|static
specifier|final
name|String
name|IP_1
init|=
literal|"ip_1"
decl_stmt|;
DECL|field|IP_2
specifier|private
specifier|static
specifier|final
name|String
name|IP_2
init|=
literal|"ip_2"
decl_stmt|;
DECL|field|IP_3
specifier|private
specifier|static
specifier|final
name|String
name|IP_3
init|=
literal|"ip_3"
decl_stmt|;
DECL|field|IP_4
specifier|private
specifier|static
specifier|final
name|String
name|IP_4
init|=
literal|"ip_4"
decl_stmt|;
annotation|@
name|Before
DECL|method|beforeImplicitSnitchTest
specifier|public
name|void
name|beforeImplicitSnitchTest
parameter_list|()
block|{
name|snitch
operator|=
operator|new
name|ImplicitSnitch
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withAllIPv4RequestedTags_with_omitted_zeros_returns_four_tags
specifier|public
name|void
name|testGetTags_withAllIPv4RequestedTags_with_omitted_zeros_returns_four_tags
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"5:8983_solr"
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_1
argument_list|,
name|IP_2
argument_list|,
name|IP_3
argument_list|,
name|IP_4
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_3
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_4
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withAllIPv4RequestedTags_returns_four_tags
specifier|public
name|void
name|testGetTags_withAllIPv4RequestedTags_returns_four_tags
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"192.168.1.2:8983_solr"
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_1
argument_list|,
name|IP_2
argument_list|,
name|IP_3
argument_list|,
name|IP_4
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_3
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"168"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_4
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"192"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withIPv4RequestedTags_ip2_and_ip4_returns_two_tags
specifier|public
name|void
name|testGetTags_withIPv4RequestedTags_ip2_and_ip4_returns_two_tags
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"192.168.1.2:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_2
argument_list|,
name|IP_4
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_4
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"192"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_with_wrong_ipv4_format_ip_returns_nothing
specifier|public
name|void
name|testGetTags_with_wrong_ipv4_format_ip_returns_nothing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"192.168.1.2.1:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_1
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_with_correct_ipv6_format_ip_returns_nothing
specifier|public
name|void
name|testGetTags_with_correct_ipv6_format_ip_returns_nothing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"[0:0:0:0:0:0:0:1]:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_1
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//This will fail when IPv6 is implemented
block|}
annotation|@
name|Test
DECL|method|testGetTags_withEmptyRequestedTag_returns_nothing
specifier|public
name|void
name|testGetTags_withEmptyRequestedTag_returns_nothing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"192.168.1.2:8983_solr"
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withAllHostNameRequestedTags_returns_all_Tags
specifier|public
name|void
name|testGetTags_withAllHostNameRequestedTags_returns_all_Tags
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"serv01.dc01.london.uk.apache.org:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//We need mocking here otherwise, we would need proper DNS entry for this test to pass
name|ImplicitSnitch
name|mockedSnitch
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|snitch
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockedSnitch
operator|.
name|getHostIp
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"10.11.12.13"
argument_list|)
expr_stmt|;
name|mockedSnitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_1
argument_list|,
name|IP_2
argument_list|,
name|IP_3
argument_list|,
name|IP_4
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"13"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_3
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"11"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_4
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withHostNameRequestedTag_ip3_returns_1_tag
specifier|public
name|void
name|testGetTags_withHostNameRequestedTag_ip3_returns_1_tag
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"serv01.dc01.london.uk.apache.org:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//We need mocking here otherwise, we would need proper DNS entry for this test to pass
name|ImplicitSnitch
name|mockedSnitch
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|snitch
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockedSnitch
operator|.
name|getHostIp
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"10.11.12.13"
argument_list|)
expr_stmt|;
name|mockedSnitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|IP_3
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|IP_3
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"11"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTags_withHostNameRequestedTag_ip99999_returns_nothing
specifier|public
name|void
name|testGetTags_withHostNameRequestedTag_ip99999_returns_nothing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|node
init|=
literal|"serv01.dc01.london.uk.apache.org:8983_solr"
decl_stmt|;
name|SnitchContext
name|context
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//We need mocking here otherwise, we would need proper DNS entry for this test to pass
name|ImplicitSnitch
name|mockedSnitch
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|snitch
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockedSnitch
operator|.
name|getHostIp
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"10.11.12.13"
argument_list|)
expr_stmt|;
name|mockedSnitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"ip_99999"
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
name|context
operator|.
name|getTags
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsKnownTag_ip1
specifier|public
name|void
name|testIsKnownTag_ip1
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
literal|"ip_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
name|IP_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
name|IP_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
name|IP_3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
name|IP_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|snitch
operator|.
name|isKnownTag
argument_list|(
literal|"ip_5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
throws|throws
name|Exception
block|{
name|ImplicitSnitch
name|implicitSnitch
init|=
operator|new
name|ImplicitSnitch
argument_list|()
decl_stmt|;
name|ServerSnitchContext
name|noNodeExceptionSnitch
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Map
name|getZkJson
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|KeeperException
operator|.
name|NoNodeException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|ROLE
argument_list|)
argument_list|,
name|noNodeExceptionSnitch
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|noNodeExceptionSnitch
operator|.
name|retrieve
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|)
decl_stmt|;
comment|// todo it the key really supposed to /roles.json?
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|NODEROLE
argument_list|)
argument_list|,
name|noNodeExceptionSnitch
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|noNodeExceptionSnitch
operator|.
name|retrieve
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|)
expr_stmt|;
comment|// todo it the key really supposed to /roles.json?
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ServerSnitchContext
name|keeperExceptionSnitch
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Map
name|getZkJson
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|KeeperException
operator|.
name|ConnectionLossException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
name|KeeperException
operator|.
name|ConnectionLossException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|ROLE
argument_list|)
argument_list|,
name|keeperExceptionSnitch
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
name|KeeperException
operator|.
name|ConnectionLossException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|NODEROLE
argument_list|)
argument_list|,
name|keeperExceptionSnitch
argument_list|)
argument_list|)
expr_stmt|;
name|ServerSnitchContext
name|remoteExceptionSnitch
init|=
operator|new
name|ServerSnitchContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|invokeRemote
parameter_list|(
name|String
name|node
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|klas
parameter_list|,
name|RemoteCallback
name|callback
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|CORES
argument_list|)
argument_list|,
name|remoteExceptionSnitch
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|DISK
argument_list|)
argument_list|,
name|remoteExceptionSnitch
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|implicitSnitch
operator|.
name|getTags
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|ImplicitSnitch
operator|.
name|SYSPROP
operator|+
literal|"xyz"
argument_list|)
argument_list|,
name|remoteExceptionSnitch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

