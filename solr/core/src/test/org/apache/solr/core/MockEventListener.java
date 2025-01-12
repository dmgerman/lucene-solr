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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|MockEventListener
specifier|public
class|class
name|MockEventListener
implements|implements
name|SolrEventListener
block|{
DECL|field|createCounter
specifier|final
specifier|static
name|AtomicInteger
name|createCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|getCreateCount
specifier|public
specifier|static
specifier|final
name|int
name|getCreateCount
parameter_list|()
block|{
return|return
name|createCounter
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|method|MockEventListener
specifier|public
name|MockEventListener
parameter_list|()
block|{
name|createCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|postSoftCommit
specifier|public
name|void
name|postSoftCommit
parameter_list|()
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
comment|/* NOOP */
block|}
block|}
end_class

end_unit

