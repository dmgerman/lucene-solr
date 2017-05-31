begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
package|;
end_package

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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
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
name|CollectionAdminRequest
operator|.
name|CreateAlias
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
name|CollectionAdminRequest
operator|.
name|CreateShard
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link CollectionAdminRequest}.  */
end_comment

begin_class
DECL|class|TestCollectionAdminRequest
specifier|public
class|class
name|TestCollectionAdminRequest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testInvalidCollectionNameRejectedWhenCreatingCollection
specifier|public
name|void
name|testInvalidCollectionNameRejectedWhenCreatingCollection
parameter_list|()
block|{
specifier|final
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"invalid$collection@name"
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$collection@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidShardNamesRejectedWhenCreatingImplicitCollection
specifier|public
name|void
name|testInvalidShardNamesRejectedWhenCreatingImplicitCollection
parameter_list|()
block|{
specifier|final
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
literal|"fine"
argument_list|,
literal|"fine"
argument_list|,
literal|"invalid$shard@name"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$shard@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidShardNamesRejectedWhenCallingSetShards
specifier|public
name|void
name|testInvalidShardNamesRejectedWhenCallingSetShards
parameter_list|()
block|{
name|CollectionAdminRequest
operator|.
name|Create
name|request
init|=
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
literal|"fine"
argument_list|,
literal|null
argument_list|,
literal|"fine"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|request
operator|.
name|setShards
argument_list|(
literal|"invalid$shard@name"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$shard@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidAliasNameRejectedWhenCreatingAlias
specifier|public
name|void
name|testInvalidAliasNameRejectedWhenCreatingAlias
parameter_list|()
block|{
specifier|final
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CreateAlias
name|createAliasRequest
init|=
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"invalid$alias@name"
argument_list|,
literal|"ignored"
argument_list|)
decl_stmt|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid alias"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$alias@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidShardNameRejectedWhenCreatingShard
specifier|public
name|void
name|testInvalidShardNameRejectedWhenCreatingShard
parameter_list|()
block|{
specifier|final
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CreateShard
name|createShardRequest
init|=
name|CollectionAdminRequest
operator|.
name|createShard
argument_list|(
literal|"ignored"
argument_list|,
literal|"invalid$shard@name"
argument_list|)
decl_stmt|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$shard@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

