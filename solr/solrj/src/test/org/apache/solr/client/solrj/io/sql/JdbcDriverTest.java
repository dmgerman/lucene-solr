begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.sql
package|package
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
name|io
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the connection string part of the JDBC Driver  **/
end_comment

begin_class
DECL|class|JdbcDriverTest
specifier|public
class|class
name|JdbcDriverTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SQLException
operator|.
name|class
argument_list|)
DECL|method|testNullZKConnectionString
specifier|public
name|void
name|testNullZKConnectionString
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://?collection=collection1"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SQLException
operator|.
name|class
argument_list|)
DECL|method|testInvalidJDBCConnectionString
specifier|public
name|void
name|testInvalidJDBCConnectionString
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:mysql://"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SQLException
operator|.
name|class
argument_list|)
DECL|method|testNoCollectionProvidedInURL
specifier|public
name|void
name|testNoCollectionProvidedInURL
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://?collection=collection1"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SQLException
operator|.
name|class
argument_list|)
DECL|method|testNoCollectionProvidedInProperties
specifier|public
name|void
name|testNoCollectionProvidedInProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessUrl
specifier|public
name|void
name|testProcessUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|DriverImpl
name|driver
init|=
operator|new
name|DriverImpl
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|zkHostStrings
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"zoo1"
argument_list|,
literal|"zoo1:9983"
argument_list|,
literal|"zoo1,zoo2,zoo3"
argument_list|,
literal|"zoo1:9983,zoo2:9983,zoo3:9983"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|chroots
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|""
argument_list|,
literal|"/"
argument_list|,
literal|"/foo"
argument_list|,
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paramStrings
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|""
argument_list|,
literal|"collection=collection1"
argument_list|,
literal|"collection=collection1&test=test1"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|zkHostString
range|:
name|zkHostStrings
control|)
block|{
for|for
control|(
name|String
name|chroot
range|:
name|chroots
control|)
block|{
for|for
control|(
name|String
name|paramString
range|:
name|paramStrings
control|)
block|{
name|String
name|url
init|=
literal|"jdbc:solr://"
operator|+
name|zkHostString
operator|+
name|chroot
operator|+
literal|"?"
operator|+
name|paramString
decl_stmt|;
name|URI
name|uri
init|=
name|driver
operator|.
name|processUrl
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|zkHostString
argument_list|,
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chroot
argument_list|,
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|paramString
argument_list|,
name|uri
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

