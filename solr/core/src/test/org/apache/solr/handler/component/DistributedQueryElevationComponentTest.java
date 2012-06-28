begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|apache
operator|.
name|solr
operator|.
name|BaseDistributedSearchTestCase
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
name|CommonParams
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

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|DistributedQueryElevationComponentTest
specifier|public
class|class
name|DistributedQueryElevationComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedQueryElevationComponentTest
specifier|public
name|DistributedQueryElevationComponentTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|3
expr_stmt|;
name|stress
operator|=
literal|0
expr_stmt|;
comment|// TODO: a better way to do this?
name|configString
operator|=
literal|"solrconfig-elevate.xml"
expr_stmt|;
name|schemaString
operator|=
literal|"schema11.xml"
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"elevate.data.file"
argument_list|,
literal|"elevate.xml"
argument_list|)
expr_stmt|;
name|File
name|parent
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"elevate.data.file"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"int_i"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"XXXX XXXX"
argument_list|,
literal|"field_t"
argument_list|,
literal|"anything"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"int_i"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"YYYY YYYY"
argument_list|,
literal|"plow_t"
argument_list|,
literal|"rake"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"int_i"
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ ZZZZ"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"int_i"
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"XXXX XXXX"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"int_i"
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ ZZZZ ZZZZ"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"int_i"
argument_list|,
literal|"6"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"int_i"
argument_list|,
literal|"7"
argument_list|,
literal|"text"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards.qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id, score, [elevated]"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"ZZZZ"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"int_i desc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"solr"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"int_i asc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"ZZZZ"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
block|}
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

