begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
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
name|SolrPingResponse
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
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_comment
comment|/**  * A vehicle to load a list of Solr documents into some kind of destination,  * such as a SolrServer or MapReduce RecordWriter.  */
end_comment

begin_interface
DECL|interface|DocumentLoader
specifier|public
interface|interface
name|DocumentLoader
block|{
comment|/** Begins a transaction */
DECL|method|beginTransaction
specifier|public
name|void
name|beginTransaction
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
comment|/** Loads the given document into the destination */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
comment|/**    * Sends any outstanding documents to the destination and waits for a positive    * or negative ack (i.e. exception). Depending on the outcome the caller    * should then commit or rollback the current flume transaction    * correspondingly.    *     * @throws IOException    *           If there is a low-level I/O error.    */
DECL|method|commitTransaction
specifier|public
name|void
name|commitTransaction
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
comment|/**    * Performs a rollback of all non-committed documents pending.    *<p>    * Note that this is not a true rollback as in databases. Content you have    * previously added may have already been committed due to autoCommit, buffer    * full, other client performing a commit etc. So this is only a best-effort    * rollback.    *     * @throws IOException    *           If there is a low-level I/O error.    */
DECL|method|rollbackTransaction
specifier|public
name|UpdateResponse
name|rollbackTransaction
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
comment|/** Releases allocated resources */
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
comment|/**    * Issues a ping request to check if the server is alive    *     * @throws IOException    *           If there is a low-level I/O error.    */
DECL|method|ping
specifier|public
name|SolrPingResponse
name|ping
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
function_decl|;
block|}
end_interface

end_unit

