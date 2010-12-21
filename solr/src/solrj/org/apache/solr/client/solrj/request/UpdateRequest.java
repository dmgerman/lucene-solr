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
name|io
operator|.
name|Writer
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|SolrRequest
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
name|AbstractUpdateRequest
block|{
comment|/**    * Kept for back compatibility.    *    * @deprecated Use {@link AbstractUpdateRequest.ACTION} instead    */
annotation|@
name|Deprecated
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
DECL|field|docIterator
specifier|private
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|docIterator
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
DECL|method|UpdateRequest
specifier|public
name|UpdateRequest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|POST
argument_list|,
name|url
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
DECL|method|deleteById
specifier|public
name|UpdateRequest
name|deleteById
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
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
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|deleteById
operator|.
name|addAll
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
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
comment|/** Sets appropriate parameters for the given ACTION    *    * @deprecated Use {@link org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION} instead    * */
annotation|@
name|Deprecated
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
return|return
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    *    * @deprecated Use {@link org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION} instead    */
annotation|@
name|Deprecated
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
parameter_list|,
name|int
name|maxSegments
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
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
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|MAX_OPTIMIZE_SEGMENTS
argument_list|,
name|maxSegments
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
name|this
return|;
block|}
comment|/**    *    *    * @deprecated Use {@link org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION} instead    */
annotation|@
name|Deprecated
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
parameter_list|,
name|int
name|maxSegments
parameter_list|,
name|boolean
name|expungeDeletes
parameter_list|)
block|{
name|setAction
argument_list|(
name|action
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|EXPUNGE_DELETES
argument_list|,
literal|""
operator|+
name|expungeDeletes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDocIterator
specifier|public
name|void
name|setDocIterator
parameter_list|(
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|docIterator
parameter_list|)
block|{
name|this
operator|.
name|docIterator
operator|=
name|docIterator
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------
annotation|@
name|Override
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
name|writeXML
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// If action is COMMIT or OPTIMIZE, it is sent with params
name|String
name|xml
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//System.out.println( "SEND:"+xml );
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
comment|/**    * @since solr 1.4    */
DECL|method|writeXML
specifier|public
name|void
name|writeXML
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
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
operator|)
operator|||
name|docIterator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|commitWithin
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<add commitWithin=\""
operator|+
name|commitWithin
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|documents
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|docIterator
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|docIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SolrInputDocument
name|doc
init|=
name|docIterator
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|boolean
name|deleteI
init|=
name|deleteById
operator|!=
literal|null
operator|&&
name|deleteById
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
name|boolean
name|deleteQ
init|=
name|deleteQuery
operator|!=
literal|null
operator|&&
name|deleteQuery
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|deleteI
operator|||
name|deleteQ
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
name|deleteI
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
name|deleteQ
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
block|}
comment|//--------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------
comment|//
comment|//--------------------------------------------------------------------------
DECL|method|getDocuments
specifier|public
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|getDocuments
parameter_list|()
block|{
return|return
name|documents
return|;
block|}
DECL|method|getDocIterator
specifier|public
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|getDocIterator
parameter_list|()
block|{
return|return
name|docIterator
return|;
block|}
DECL|method|getDeleteById
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDeleteById
parameter_list|()
block|{
return|return
name|deleteById
return|;
block|}
DECL|method|getDeleteQuery
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDeleteQuery
parameter_list|()
block|{
return|return
name|deleteQuery
return|;
block|}
block|}
end_class

end_unit

