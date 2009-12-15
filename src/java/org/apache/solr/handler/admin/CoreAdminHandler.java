begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|params
operator|.
name|CoreAdminParams
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
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
name|NamedList
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
name|SimpleOrderedMap
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|CoreDescriptor
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|DirectoryFactory
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
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryResponse
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
name|LocalSolrQueryRequest
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RefCounted
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
name|update
operator|.
name|MergeIndexesCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|store
operator|.
name|Directory
import|;
end_import

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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|CoreAdminHandler
specifier|public
class|class
name|CoreAdminHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|CoreAdminHandler
specifier|public
name|CoreAdminHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Unlike most request handlers, CoreContainer initialization
comment|// should happen in the constructor...
name|this
operator|.
name|coreContainer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|CoreAdminHandler
specifier|public
name|CoreAdminHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|final
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"CoreAdminHandler should not be configured in solrconf.xml\n"
operator|+
literal|"it is a special Handler configured directly by the RequestDispatcher"
argument_list|)
throw|;
block|}
comment|/**    * The instance of CoreContainer this handler handles. This should be the CoreContainer instance that created this    * handler.    *    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|coreContainer
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Make sure the cores is enabled
name|CoreContainer
name|cores
init|=
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
comment|// Pick the action
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|CoreAdminAction
name|action
init|=
name|CoreAdminAction
operator|.
name|STATUS
decl_stmt|;
name|String
name|a
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleCustomAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|CREATE
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleCreateAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|RENAME
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleRenameAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|ALIAS
case|:
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"'ALIAS' is not supported "
operator|+
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
argument_list|)
throw|;
block|}
case|case
name|UNLOAD
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleUnloadAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|STATUS
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleStatusAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|PERSIST
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handlePersistAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|RELOAD
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleReloadAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SWAP
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleSwapAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|MERGEINDEXES
case|:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleMergeAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
block|{
name|doPersist
operator|=
name|this
operator|.
name|handleCustomAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|LOAD
case|:
break|break;
block|}
block|}
comment|// Should we persist the changes?
if|if
condition|(
name|doPersist
condition|)
block|{
name|cores
operator|.
name|persist
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"saved"
argument_list|,
name|cores
operator|.
name|getConfigFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|handleMergeAction
specifier|protected
name|boolean
name|handleMergeAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|required
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|doPersist
operator|=
name|coreContainer
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
name|String
index|[]
name|dirNames
init|=
name|required
operator|.
name|getParams
argument_list|(
name|CoreAdminParams
operator|.
name|INDEX_DIR
argument_list|)
decl_stmt|;
name|DirectoryFactory
name|dirFactory
init|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
decl_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|dirNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
name|dirFactory
operator|.
name|open
argument_list|(
name|dirNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|UpdateRequestProcessorChain
name|processorChain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_PROCESSOR
argument_list|)
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|wrappedReq
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|wrappedReq
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processMergeIndexes
argument_list|(
operator|new
name|MergeIndexesCommand
argument_list|(
name|dirs
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|doPersist
return|;
block|}
comment|/**    * Handle Custom Action.    *<p/>    * This method could be overridden by derived classes to handle custom actions.<br> By default - this method throws a    * solr exception. Derived classes are free to write their derivation if necessary.    */
DECL|method|handleCustomAction
specifier|protected
name|boolean
name|handleCustomAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unsupported operation: "
operator|+
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
argument_list|)
throw|;
block|}
comment|/**    * Handle 'CREATE' action.    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    *    * @throws SolrException in case of a configuration error.    */
DECL|method|handleCreateAction
specifier|protected
name|boolean
name|handleCreateAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
try|try
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|CoreDescriptor
name|dcore
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|coreContainer
argument_list|,
name|name
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|)
argument_list|)
decl_stmt|;
comment|//  fillup optional parameters
name|String
name|opts
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
name|dcore
operator|.
name|setConfigName
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|opts
operator|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|)
expr_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
name|dcore
operator|.
name|setSchemaName
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|opts
operator|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|)
expr_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
name|dcore
operator|.
name|setDataDir
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|dcore
operator|.
name|setCoreProperties
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|create
argument_list|(
name|dcore
argument_list|)
decl_stmt|;
name|coreContainer
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"core"
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|coreContainer
operator|.
name|isPersistent
argument_list|()
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error executing default implementation of CREATE"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Handle "RENAME" Action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    *    * @throws SolrException    */
DECL|method|handleRenameAction
specifier|protected
name|boolean
name|handleRenameAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|doPersist
return|;
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|doPersist
operator|=
name|coreContainer
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
name|coreContainer
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|coreContainer
operator|.
name|remove
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|doPersist
return|;
block|}
comment|/**    * Handle "ALIAS" action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    */
annotation|@
name|Deprecated
DECL|method|handleAliasAction
specifier|protected
name|boolean
name|handleAliasAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|doPersist
return|;
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|doPersist
operator|=
name|coreContainer
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
name|coreContainer
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// no core.close() since each entry in the cores map should increase the ref
block|}
return|return
name|doPersist
return|;
block|}
comment|/**    * Handle "UNLOAD" Action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    */
DECL|method|handleUnloadAction
specifier|protected
name|boolean
name|handleUnloadAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|remove
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No such core exists '"
operator|+
name|cname
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|coreContainer
operator|.
name|isPersistent
argument_list|()
return|;
block|}
comment|/**    * Handle "STATUS" action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    */
DECL|method|handleStatusAction
specifier|protected
name|boolean
name|handleStatusAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|status
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|coreContainer
operator|.
name|getCoreNames
argument_list|()
control|)
block|{
name|status
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|getCoreStatus
argument_list|(
name|coreContainer
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|.
name|add
argument_list|(
name|cname
argument_list|,
name|getCoreStatus
argument_list|(
name|coreContainer
argument_list|,
name|cname
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|doPersist
operator|=
literal|false
expr_stmt|;
comment|// no state change
return|return
name|doPersist
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error handling 'status' action "
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Handler "PERSIST" action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    *    * @throws SolrException    */
DECL|method|handlePersistAction
specifier|protected
name|boolean
name|handlePersistAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|boolean
name|doPersist
init|=
literal|false
decl_stmt|;
name|String
name|fileName
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|coreContainer
operator|.
name|getConfigFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|coreContainer
operator|.
name|persistFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"saved"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|doPersist
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|coreContainer
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Persistence is not enabled"
argument_list|)
throw|;
block|}
else|else
name|doPersist
operator|=
literal|true
expr_stmt|;
return|return
name|doPersist
return|;
block|}
comment|/**    * Handler "RELOAD" action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    */
DECL|method|handleReloadAction
specifier|protected
name|boolean
name|handleReloadAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
try|try
block|{
name|coreContainer
operator|.
name|reload
argument_list|(
name|cname
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
comment|// no change on reload
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error handling 'reload' action"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Handle "SWAP" action    *    * @param req    * @param rsp    *    * @return true if a modification has resulted that requires persistance     *         of the CoreContainer configuration.    */
DECL|method|handleSwapAction
specifier|protected
name|boolean
name|handleSwapAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
specifier|final
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
specifier|final
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
specifier|final
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|boolean
name|doPersist
init|=
name|params
operator|.
name|getBool
argument_list|(
name|CoreAdminParams
operator|.
name|PERSISTENT
argument_list|,
name|coreContainer
operator|.
name|isPersistent
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|other
init|=
name|required
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
name|coreContainer
operator|.
name|swap
argument_list|(
name|cname
argument_list|,
name|other
argument_list|)
expr_stmt|;
return|return
name|doPersist
return|;
block|}
DECL|method|getCoreStatus
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getCoreStatus
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|String
name|cname
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"instanceDir"
argument_list|,
name|normalizePath
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"dataDir"
argument_list|,
name|normalizePath
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|(
name|core
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"uptime"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|core
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|info
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|LukeRequestHandler
operator|.
name|getIndexInfo
argument_list|(
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|info
return|;
block|}
DECL|method|normalizePath
specifier|protected
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Manage Multiple Solr Cores"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

