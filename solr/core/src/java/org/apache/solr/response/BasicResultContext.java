begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|search
operator|.
name|Query
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|ReturnFields
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_class
DECL|class|BasicResultContext
specifier|public
class|class
name|BasicResultContext
extends|extends
name|ResultContext
block|{
DECL|field|docList
specifier|private
name|DocList
name|docList
decl_stmt|;
DECL|field|returnFields
specifier|private
name|ReturnFields
name|returnFields
decl_stmt|;
DECL|field|searcher
specifier|private
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|req
specifier|private
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|method|BasicResultContext
specifier|public
name|BasicResultContext
parameter_list|(
name|DocList
name|docList
parameter_list|,
name|ReturnFields
name|returnFields
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|docList
operator|=
name|docList
expr_stmt|;
name|this
operator|.
name|returnFields
operator|=
name|returnFields
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
DECL|method|BasicResultContext
specifier|public
name|BasicResultContext
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|this
argument_list|(
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|rb
operator|.
name|rsp
operator|.
name|getReturnFields
argument_list|()
argument_list|,
literal|null
argument_list|,
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|,
name|rb
operator|.
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|BasicResultContext
specifier|public
name|BasicResultContext
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|DocList
name|docList
parameter_list|)
block|{
name|this
argument_list|(
name|docList
argument_list|,
name|rb
operator|.
name|rsp
operator|.
name|getReturnFields
argument_list|()
argument_list|,
literal|null
argument_list|,
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|,
name|rb
operator|.
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocList
specifier|public
name|DocList
name|getDocList
parameter_list|()
block|{
return|return
name|docList
return|;
block|}
annotation|@
name|Override
DECL|method|getReturnFields
specifier|public
name|ReturnFields
name|getReturnFields
parameter_list|()
block|{
return|return
name|returnFields
return|;
block|}
annotation|@
name|Override
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
return|return
name|searcher
return|;
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
return|return
name|req
operator|.
name|getSearcher
argument_list|()
return|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getRequest
specifier|public
name|SolrQueryRequest
name|getRequest
parameter_list|()
block|{
return|return
name|req
return|;
block|}
block|}
end_class

end_unit

