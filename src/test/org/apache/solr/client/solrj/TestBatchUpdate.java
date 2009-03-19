begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
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
name|embedded
operator|.
name|JettySolrRunner
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CommonsHttpSolrServer
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryRequestWriter
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|RequestWriter
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
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Test for SOLR-1038  *  * @since solr 1.4  * @version $Id$  */
end_comment

begin_class
DECL|class|TestBatchUpdate
specifier|public
class|class
name|TestBatchUpdate
extends|extends
name|SolrExampleTestBase
block|{
DECL|field|numdocs
specifier|static
specifier|final
name|int
name|numdocs
init|=
literal|1000
decl_stmt|;
DECL|field|server
name|SolrServer
name|server
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|context
specifier|static
specifier|final
name|String
name|context
init|=
literal|"/example"
decl_stmt|;
DECL|method|testWithXml
specifier|public
name|void
name|testWithXml
parameter_list|()
throws|throws
name|Exception
block|{
name|CommonsHttpSolrServer
name|commonsHttpSolrServer
init|=
operator|(
name|CommonsHttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|commonsHttpSolrServer
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|RequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|commonsHttpSolrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|commonsHttpSolrServer
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithBinary
specifier|public
name|void
name|testWithBinary
parameter_list|()
throws|throws
name|Exception
block|{
name|CommonsHttpSolrServer
name|commonsHttpSolrServer
init|=
operator|(
name|CommonsHttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|commonsHttpSolrServer
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|commonsHttpSolrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|commonsHttpSolrServer
argument_list|)
expr_stmt|;
block|}
DECL|method|doIt
specifier|private
name|void
name|doIt
parameter_list|(
name|CommonsHttpSolrServer
name|commonsHttpSolrServer
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
specifier|final
name|int
index|[]
name|counter
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|counter
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|commonsHttpSolrServer
operator|.
name|addAndCommit
argument_list|(
operator|new
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|counter
index|[
literal|0
index|]
operator|<
name|numdocs
return|;
block|}
specifier|public
name|SolrInputDocument
name|next
parameter_list|()
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
operator|(
operator|++
name|counter
index|[
literal|0
index|]
operator|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"cat"
argument_list|,
literal|"foocat"
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//do nothing
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|commonsHttpSolrServer
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numdocs
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|context
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|=
name|this
operator|.
name|createNewSolrServer
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// stop the server
block|}
annotation|@
name|Override
DECL|method|getSolrServer
specifier|protected
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|protected
name|SolrServer
name|createNewSolrServer
parameter_list|()
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
name|context
decl_stmt|;
name|CommonsHttpSolrServer
name|s
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|s
operator|.
name|setConnectionTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// 1/10th sec
name|s
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|s
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

