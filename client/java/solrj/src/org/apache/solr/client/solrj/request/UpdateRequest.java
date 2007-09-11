begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|Collection
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
name|SolrServer
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
name|SolrServerException
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
name|UpdateResponse
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
name|util
operator|.
name|ClientUtils
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
name|ModifiableSolrParams
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
name|SolrParams
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
name|UpdateParams
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
name|ContentStream
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
name|XML
import|;
end_import

begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|UpdateRequest
specifier|public
class|class
name|UpdateRequest
extends|extends
name|RequestBase
block|{
DECL|enum|ACTION
specifier|public
enum|enum
name|ACTION
block|{
DECL|enum constant|COMMIT
name|COMMIT
block|,
DECL|enum constant|OPTIMIZE
name|OPTIMIZE
block|}
empty_stmt|;
DECL|field|waitFlush
specifier|private
name|boolean
name|waitFlush
init|=
literal|true
decl_stmt|;
DECL|field|waitSearcher
specifier|private
name|boolean
name|waitSearcher
init|=
literal|true
decl_stmt|;
DECL|field|allowDups
specifier|private
name|boolean
name|allowDups
init|=
literal|false
decl_stmt|;
DECL|field|overwriteCommitted
specifier|private
name|boolean
name|overwriteCommitted
init|=
literal|false
decl_stmt|;
DECL|field|overwritePending
specifier|private
name|boolean
name|overwritePending
init|=
literal|false
decl_stmt|;
DECL|field|action
specifier|private
name|ACTION
name|action
init|=
literal|null
decl_stmt|;
DECL|field|documents
specifier|private
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|documents
init|=
literal|null
decl_stmt|;
DECL|field|deleteById
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deleteById
init|=
literal|null
decl_stmt|;
DECL|field|deleteQuery
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deleteQuery
init|=
literal|null
decl_stmt|;
DECL|method|UpdateRequest
specifier|public
name|UpdateRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|POST
argument_list|,
literal|"/update"
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------
comment|/**    * clear the pending documents and delete commands    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|documents
operator|!=
literal|null
condition|)
block|{
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|deleteById
operator|!=
literal|null
condition|)
block|{
name|deleteById
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|deleteQuery
operator|!=
literal|null
condition|)
block|{
name|deleteQuery
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|//---------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|SolrInputDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|UpdateRequest
name|add
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|)
block|{
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
name|docs
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|documents
operator|.
name|addAll
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateRequest
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|deleteById
operator|==
literal|null
condition|)
block|{
name|deleteById
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|deleteById
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|deleteByQuery
specifier|public
name|UpdateRequest
name|deleteByQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
if|if
condition|(
name|deleteQuery
operator|==
literal|null
condition|)
block|{
name|deleteQuery
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|deleteQuery
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAction
specifier|public
name|UpdateRequest
name|setAction
parameter_list|(
name|ACTION
name|action
parameter_list|,
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|waitFlush
operator|=
name|waitFlush
expr_stmt|;
name|this
operator|.
name|waitSearcher
operator|=
name|waitSearcher
expr_stmt|;
return|return
name|this
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ClientUtils
operator|.
name|toContentStreams
argument_list|(
name|getXML
argument_list|()
argument_list|,
name|ClientUtils
operator|.
name|TEXT_XML
argument_list|)
return|;
block|}
DECL|method|getXML
specifier|public
name|String
name|getXML
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|documents
operator|!=
literal|null
operator|&&
name|documents
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<add "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"allowDups=\""
operator|+
name|allowDups
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"overwriteCommitted=\""
operator|+
name|overwriteCommitted
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"overwritePending=\""
operator|+
name|overwritePending
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|documents
control|)
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|ClientUtils
operator|.
name|writeXML
argument_list|(
name|doc
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
block|}
comment|// Add the delete commands
if|if
condition|(
name|deleteById
operator|!=
literal|null
operator|||
name|deleteQuery
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<delete>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteById
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|deleteById
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<id>"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
name|id
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"</id>"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|deleteQuery
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|q
range|:
name|deleteQuery
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"<query>"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
name|q
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"</query>"
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"</delete>"
argument_list|)
expr_stmt|;
block|}
comment|// If action is COMMIT or OPTIMIZE, it is sent with params
name|String
name|xml
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|(
name|xml
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|xml
else|:
literal|null
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|action
operator|==
name|ACTION
operator|.
name|OPTIMIZE
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|OPTIMIZE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|action
operator|==
name|ACTION
operator|.
name|COMMIT
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|COMMIT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|WAIT_FLUSH
argument_list|,
name|waitFlush
operator|+
literal|""
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
name|waitSearcher
operator|+
literal|""
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|process
specifier|public
name|UpdateResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|UpdateResponse
name|res
init|=
operator|new
name|UpdateResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//
comment|//--------------------------------------------------------------------------
DECL|method|setOverwrite
specifier|public
name|void
name|setOverwrite
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|allowDups
operator|=
operator|!
name|v
expr_stmt|;
name|overwriteCommitted
operator|=
name|v
expr_stmt|;
name|overwritePending
operator|=
name|v
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
comment|//
comment|//--------------------------------------------------------------------------
DECL|method|isWaitFlush
specifier|public
name|boolean
name|isWaitFlush
parameter_list|()
block|{
return|return
name|waitFlush
return|;
block|}
DECL|method|isWaitSearcher
specifier|public
name|boolean
name|isWaitSearcher
parameter_list|()
block|{
return|return
name|waitSearcher
return|;
block|}
DECL|method|getAction
specifier|public
name|ACTION
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
DECL|method|isAllowDups
specifier|public
name|boolean
name|isAllowDups
parameter_list|()
block|{
return|return
name|allowDups
return|;
block|}
comment|/**    * Use setOverwrite()    */
annotation|@
name|Deprecated
DECL|method|setAllowDups
specifier|public
name|void
name|setAllowDups
parameter_list|(
name|boolean
name|allowDups
parameter_list|)
block|{
name|this
operator|.
name|allowDups
operator|=
name|allowDups
expr_stmt|;
block|}
DECL|method|isOverwriteCommitted
specifier|public
name|boolean
name|isOverwriteCommitted
parameter_list|()
block|{
return|return
name|overwriteCommitted
return|;
block|}
comment|/**    * Use setOverwrite()    */
annotation|@
name|Deprecated
DECL|method|setOverwriteCommitted
specifier|public
name|void
name|setOverwriteCommitted
parameter_list|(
name|boolean
name|overwriteCommitted
parameter_list|)
block|{
name|this
operator|.
name|overwriteCommitted
operator|=
name|overwriteCommitted
expr_stmt|;
block|}
DECL|method|isOverwritePending
specifier|public
name|boolean
name|isOverwritePending
parameter_list|()
block|{
return|return
name|overwritePending
return|;
block|}
comment|/**    * Use setOverwrite()    */
annotation|@
name|Deprecated
DECL|method|setOverwritePending
specifier|public
name|void
name|setOverwritePending
parameter_list|(
name|boolean
name|overwritePending
parameter_list|)
block|{
name|this
operator|.
name|overwritePending
operator|=
name|overwritePending
expr_stmt|;
block|}
DECL|method|setWaitFlush
specifier|public
name|void
name|setWaitFlush
parameter_list|(
name|boolean
name|waitFlush
parameter_list|)
block|{
name|this
operator|.
name|waitFlush
operator|=
name|waitFlush
expr_stmt|;
block|}
DECL|method|setWaitSearcher
specifier|public
name|void
name|setWaitSearcher
parameter_list|(
name|boolean
name|waitSearcher
parameter_list|)
block|{
name|this
operator|.
name|waitSearcher
operator|=
name|waitSearcher
expr_stmt|;
block|}
block|}
end_class

end_unit

