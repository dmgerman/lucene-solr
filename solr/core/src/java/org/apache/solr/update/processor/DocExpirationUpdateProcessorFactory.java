begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
operator|.
name|*
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
name|cloud
operator|.
name|Replica
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
name|cloud
operator|.
name|Slice
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
name|CloseHook
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
name|cloud
operator|.
name|CloudDescriptor
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
name|cloud
operator|.
name|ZkController
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
name|SolrRequestInfo
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
name|response
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
name|schema
operator|.
name|TrieDateField
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
name|AddUpdateCommand
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
name|CommitUpdateCommand
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
name|DeleteUpdateCommand
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
name|DateMathParser
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
name|DefaultSolrThreadFactory
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
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|RejectedExecutionHandler
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
name|ScheduledThreadPoolExecutor
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
name|ThreadPoolExecutor
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * Update Processor Factory for managing automatic "expiration" of documents.  *</p>  *   *<p>  * The<code>DocExpirationUpdateProcessorFactory</code> provides two features related   * to the "expiration" of documents which can be used individually, or in combination:  *</p>  *<ol>  *<li>Computing expiration field values for documents from a "time to live" (TTL)</li>  *<li>Periodically delete documents from the index based on an expiration field</li>  *</ol>  *   *<p>  * Documents with expiration field values computed from a TTL can be be excluded from   * searchers using simple date based filters relative to<code>NOW</code>, or completely   * removed from the index using the periodic delete function of this factory.  Alternatively,   * the periodic delete function of this factory can be used to remove any document with an   * expiration value - even if that expiration was explicitly set with-out leveraging the TTL   * feature of this factory.  *</p>  *  *<p>  * The following configuration options are supported:  *</p>  *  *<ul>  *<li><code>expirationFieldName</code> - The name of the expiration field to use   *      in any operations (mandatory).  *</li>  *<li><code>ttlFieldName</code> - Name of a field this process should look   *      for in each document processed, defaulting to<code>_ttl_</code>.    *      If the specified field name exists in a document, the document field value   *      will be parsed as a {@linkplain DateMathParser Date Math Expression} relative to   *<code>NOW</code> and the result will be added to the document using the   *<code>expirationFieldName</code>.  Use<code>&lt;null name="ttlFieldName"/&gt;</code>  *      to disable this feature.  *</li>  *<li><code>ttlParamName</code> - Name of an update request param this process should   *      look for in each request when processing document additions, defaulting to    *<code>_ttl_</code>. If the the specified param name exists in an update request,   *      the param value will be parsed as a {@linkplain DateMathParser Date Math Expression}  *      relative to<code>NOW</code> and the result will be used as a default for any   *      document included in that request that does not already have a value in the   *      field specified by<code>ttlFieldName</code>.  Use   *<code>&lt;null name="ttlParamName"/&gt;</code> to disable this feature.  *</li>  *<li><code>autoDeletePeriodSeconds</code> - Optional numeric value indicating how   *      often this factory should trigger a delete to remove documents.  If this option is   *      used, and specifies a non-negative numeric value, a background thread will be   *      created that will execute recurring<code>deleteByQuery</code> commands using the   *      specified period.  The delete query will remove all documents with an   *<code>expirationFieldName</code> up to<code>NOW</code>.  *</li>  *<li><code>autoDeleteChainName</code> - Optional name of an   *<code>updateRequestProcessorChain</code> to use when executing automatic deletes.    *      If not specified, or<code>&lt;null/&gt;</code>, the default   *<code>updateRequestProcessorChain</code> for this collection is used.    *      This option is ignored unless<code>autoDeletePeriodSeconds</code> is configured   *      and is non-negative.  *</li>  *</ul>  *  *<p>  * For example: The configuration below will cause any document with a field named   *<code>_ttl_</code> to have a Date field named<code>_expire_at_</code> computed   * for it when added -- but no automatic deletion will happen.  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.processor.DocExpirationUpdateProcessorFactory"&gt;  *&lt;str name="expirationFieldName"&gt;_expire_at_&lt;/str&gt;  *&lt;/processor&gt;</pre>  *  *<p>  * Alternatively, in this configuration deletes will occur automatically against the   *<code>_expire_at_</code> field every 5 minutes - but this processor will not   * automatically populate the<code>_expire_at_</code> using any sort of TTL expression.    * Only documents that were added with an explicit<code>_expire_at_</code> field value   * will ever be deleted.  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.processor.DocExpirationUpdateProcessorFactory"&gt;  *&lt;null name="ttlFieldName"/&gt;  *&lt;null name="ttlParamName"/&gt;  *&lt;int name="autoDeletePeriodSeconds"&gt;300&lt;/int&gt;  *&lt;str name="expirationFieldName"&gt;_expire_at_&lt;/str&gt;  *&lt;/processor&gt;</pre>  *  *<p>  * This last example shows the combination of both features using a custom   *<code>ttlFieldName</code>: Documents with a<code>my_ttl</code> field will   * have an<code>_expire_at_</code> field computed, and deletes will be triggered   * every 5 minutes to remove documents whose   *<code>_expire_at_</code> field value is in the past.  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.processor.DocExpirationUpdateProcessorFactory"&gt;  *&lt;int name="autoDeletePeriodSeconds"&gt;300&lt;/int&gt;  *&lt;str name="ttlFieldName"&gt;my_ttl&lt;/str&gt;  *&lt;null name="ttlParamName"/&gt;  *&lt;str name="expirationFieldName"&gt;_expire_at_&lt;/str&gt;  *&lt;/processor&gt;</pre>   */
end_comment

begin_class
DECL|class|DocExpirationUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|DocExpirationUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocExpirationUpdateProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEF_TTL_KEY
specifier|private
specifier|static
specifier|final
name|String
name|DEF_TTL_KEY
init|=
literal|"_ttl_"
decl_stmt|;
DECL|field|EXP_FIELD_NAME_CONF
specifier|private
specifier|static
specifier|final
name|String
name|EXP_FIELD_NAME_CONF
init|=
literal|"expirationFieldName"
decl_stmt|;
DECL|field|TTL_FIELD_NAME_CONF
specifier|private
specifier|static
specifier|final
name|String
name|TTL_FIELD_NAME_CONF
init|=
literal|"ttlFieldName"
decl_stmt|;
DECL|field|TTL_PARAM_NAME_CONF
specifier|private
specifier|static
specifier|final
name|String
name|TTL_PARAM_NAME_CONF
init|=
literal|"ttlParamName"
decl_stmt|;
DECL|field|DEL_CHAIN_NAME_CONF
specifier|private
specifier|static
specifier|final
name|String
name|DEL_CHAIN_NAME_CONF
init|=
literal|"autoDeleteChainName"
decl_stmt|;
DECL|field|DEL_PERIOD_SEC_CONF
specifier|private
specifier|static
specifier|final
name|String
name|DEL_PERIOD_SEC_CONF
init|=
literal|"autoDeletePeriodSeconds"
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|executor
specifier|private
name|ScheduledThreadPoolExecutor
name|executor
decl_stmt|;
DECL|field|expireField
specifier|private
name|String
name|expireField
init|=
literal|null
decl_stmt|;
DECL|field|ttlField
specifier|private
name|String
name|ttlField
init|=
literal|null
decl_stmt|;
DECL|field|ttlParam
specifier|private
name|String
name|ttlParam
init|=
literal|null
decl_stmt|;
DECL|field|deleteChainName
specifier|private
name|String
name|deleteChainName
init|=
literal|null
decl_stmt|;
DECL|field|deletePeriodSeconds
specifier|private
name|long
name|deletePeriodSeconds
init|=
operator|-
literal|1L
decl_stmt|;
DECL|method|confErr
specifier|private
name|SolrException
name|confErr
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
return|return
name|confErr
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|confErr
specifier|private
name|SolrException
name|confErr
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|,
name|SolrException
name|root
parameter_list|)
block|{
return|return
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|msg
argument_list|,
name|root
argument_list|)
return|;
block|}
DECL|method|removeArgStr
specifier|private
name|String
name|removeArgStr
parameter_list|(
specifier|final
name|NamedList
name|args
parameter_list|,
specifier|final
name|String
name|arg
parameter_list|,
specifier|final
name|String
name|def
parameter_list|,
specifier|final
name|String
name|errMsg
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|indexOf
argument_list|(
name|arg
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
return|return
name|def
return|;
name|Object
name|tmp
init|=
name|args
operator|.
name|remove
argument_list|(
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|tmp
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
return|return
name|tmp
operator|.
name|toString
argument_list|()
return|;
throw|throw
name|confErr
argument_list|(
name|arg
operator|+
literal|" "
operator|+
name|errMsg
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|deleteChainName
operator|=
name|removeArgStr
argument_list|(
name|args
argument_list|,
name|DEL_CHAIN_NAME_CONF
argument_list|,
literal|null
argument_list|,
literal|"must be a<str> or<null/> for default chain"
argument_list|)
expr_stmt|;
name|ttlField
operator|=
name|removeArgStr
argument_list|(
name|args
argument_list|,
name|TTL_FIELD_NAME_CONF
argument_list|,
name|DEF_TTL_KEY
argument_list|,
literal|"must be a<str> or<null/> to disable"
argument_list|)
expr_stmt|;
name|ttlParam
operator|=
name|removeArgStr
argument_list|(
name|args
argument_list|,
name|TTL_PARAM_NAME_CONF
argument_list|,
name|DEF_TTL_KEY
argument_list|,
literal|"must be a<str> or<null/> to disable"
argument_list|)
expr_stmt|;
name|expireField
operator|=
name|removeArgStr
argument_list|(
name|args
argument_list|,
name|EXP_FIELD_NAME_CONF
argument_list|,
literal|null
argument_list|,
literal|"must be a<str>"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|expireField
condition|)
block|{
throw|throw
name|confErr
argument_list|(
name|EXP_FIELD_NAME_CONF
operator|+
literal|" must be configured"
argument_list|)
throw|;
block|}
name|Object
name|tmp
init|=
name|args
operator|.
name|remove
argument_list|(
name|DEL_PERIOD_SEC_CONF
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|tmp
operator|instanceof
name|Number
operator|)
condition|)
block|{
throw|throw
name|confErr
argument_list|(
name|DEL_PERIOD_SEC_CONF
operator|+
literal|" must be an<int> or<long>"
argument_list|)
throw|;
block|}
name|deletePeriodSeconds
operator|=
operator|(
operator|(
name|Number
operator|)
name|tmp
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|expireField
argument_list|)
condition|)
block|{
comment|// TODO: check for managed schema and auto-add as a date field?
throw|throw
name|confErr
argument_list|(
name|EXP_FIELD_NAME_CONF
operator|+
literal|" does not exist in schema: "
operator|+
name|expireField
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|<
name|deletePeriodSeconds
condition|)
block|{
comment|// validate that we have a chain we can work with
try|try
block|{
name|Object
name|ignored
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|deleteChainName
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
name|confErr
argument_list|(
name|DEL_CHAIN_NAME_CONF
operator|+
literal|" does not exist: "
operator|+
name|deleteChainName
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// schedule recuring deletion
name|initDeleteExpiredDocsScheduler
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initDeleteExpiredDocsScheduler
specifier|private
name|void
name|initDeleteExpiredDocsScheduler
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|executor
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"autoExpireDocs"
argument_list|)
argument_list|,
operator|new
name|RejectedExecutionHandler
argument_list|()
block|{
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|ThreadPoolExecutor
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping execution of '{}' using '{}'"
argument_list|,
name|r
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|core
operator|.
name|addCloseHook
argument_list|(
operator|new
name|CloseHook
argument_list|()
block|{
specifier|public
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
comment|// update handler is gone, hard terminiate anything that's left.
if|if
condition|(
name|executor
operator|.
name|isTerminating
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Triggering hard close of DocExpiration Executor"
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Triggering Graceful close of DocExpiration Executor"
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|setExecuteExistingDelayedTasksAfterShutdownPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|executor
operator|.
name|setContinueExistingPeriodicTasksAfterShutdownPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we don't want this firing right away, since the core may not be ready
specifier|final
name|long
name|initialDelay
init|=
name|deletePeriodSeconds
decl_stmt|;
comment|// TODO: should we make initialDelay configurable
comment|// TODO: should we make initialDelay some fraction of the period?
name|executor
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|DeleteExpiredDocsRunnable
argument_list|(
name|this
argument_list|)
argument_list|,
name|deletePeriodSeconds
argument_list|,
name|deletePeriodSeconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|String
name|defaultTtl
init|=
operator|(
literal|null
operator|==
name|ttlParam
operator|)
condition|?
literal|null
else|:
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ttlParam
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|ttlField
operator|&&
literal|null
operator|==
name|defaultTtl
condition|)
block|{
comment|// nothing to do, shortcircut ourselves out of the chain.
return|return
name|next
return|;
block|}
else|else
block|{
return|return
operator|new
name|TTLUpdateProcessor
argument_list|(
name|defaultTtl
argument_list|,
name|expireField
argument_list|,
name|ttlField
argument_list|,
name|next
argument_list|)
return|;
block|}
block|}
DECL|class|TTLUpdateProcessor
specifier|private
specifier|static
specifier|final
class|class
name|TTLUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|defaultTtl
specifier|final
name|String
name|defaultTtl
decl_stmt|;
DECL|field|expireField
specifier|final
name|String
name|expireField
decl_stmt|;
DECL|field|ttlField
specifier|final
name|String
name|ttlField
decl_stmt|;
DECL|method|TTLUpdateProcessor
specifier|public
name|TTLUpdateProcessor
parameter_list|(
specifier|final
name|String
name|defaultTtl
parameter_list|,
specifier|final
name|String
name|expireField
parameter_list|,
specifier|final
name|String
name|ttlField
parameter_list|,
specifier|final
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultTtl
operator|=
name|defaultTtl
expr_stmt|;
name|this
operator|.
name|expireField
operator|=
name|expireField
expr_stmt|;
name|this
operator|.
name|ttlField
operator|=
name|ttlField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SolrInputDocument
name|doc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
specifier|final
name|String
name|math
init|=
name|doc
operator|.
name|containsKey
argument_list|(
name|ttlField
argument_list|)
condition|?
name|doc
operator|.
name|getFieldValue
argument_list|(
name|ttlField
argument_list|)
operator|.
name|toString
argument_list|()
else|:
name|defaultTtl
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|math
condition|)
block|{
try|try
block|{
specifier|final
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|()
decl_stmt|;
comment|// TODO: should we try to accept things like "1DAY" as well as "+1DAY" ?
comment|// How?
comment|// 'startsWith("+")' is a bad idea because it would cause porblems with
comment|// things like "/DAY+1YEAR"
comment|// Maybe catch ParseException and rety with "+" prepended?
name|doc
operator|.
name|addField
argument_list|(
name|expireField
argument_list|,
name|dmp
operator|.
name|parseMath
argument_list|(
name|math
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
literal|"Can't parse ttl as date math: "
operator|+
name|math
argument_list|,
name|pe
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *<p>    * Runnable that uses the the<code>deleteChainName</code> configured for     * this factory to execute a delete by query (using the configured     *<code>expireField</code>) followed by a soft commit to re-open searchers (if needed)    *</p>    *<p>    * This logic is all wrapped up in a new SolrRequestInfo context with     * some logging to help make it obvious this background activity is happening.    *</p>    *<p>    * In cloud mode, this runner only triggers deletes if     * {@link #iAmInChargeOfPeriodicDeletes} is true.    * (logging is minimal in this situation)    *</p>    *    * @see #iAmInChargeOfPeriodicDeletes    */
DECL|class|DeleteExpiredDocsRunnable
specifier|private
specifier|static
specifier|final
class|class
name|DeleteExpiredDocsRunnable
implements|implements
name|Runnable
block|{
DECL|field|factory
specifier|final
name|DocExpirationUpdateProcessorFactory
name|factory
decl_stmt|;
DECL|field|core
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|deleteChainName
specifier|final
name|String
name|deleteChainName
decl_stmt|;
DECL|field|expireField
specifier|final
name|String
name|expireField
decl_stmt|;
DECL|method|DeleteExpiredDocsRunnable
specifier|public
name|DeleteExpiredDocsRunnable
parameter_list|(
specifier|final
name|DocExpirationUpdateProcessorFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|factory
operator|.
name|core
expr_stmt|;
name|this
operator|.
name|deleteChainName
operator|=
name|factory
operator|.
name|deleteChainName
expr_stmt|;
name|this
operator|.
name|expireField
operator|=
name|factory
operator|.
name|expireField
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// setup the request context early so the logging (including any from
comment|// shouldWeDoPeriodicDelete() ) includes the core context info
specifier|final
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|factory
operator|.
name|core
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
index|[]
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|factory
operator|.
name|iAmInChargeOfPeriodicDeletes
argument_list|()
condition|)
block|{
comment|// No-Op
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Begining periodic deletion of expired docs"
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|chain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|deleteChainName
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|proc
init|=
name|chain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|proc
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No active processors, skipping automatic deletion "
operator|+
literal|"of expired docs using chain: {}"
argument_list|,
name|deleteChainName
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|DeleteUpdateCommand
name|del
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|del
operator|.
name|setQuery
argument_list|(
literal|"{!cache=false}"
operator|+
name|expireField
operator|+
literal|":[* TO "
operator|+
name|TrieDateField
operator|.
name|formatExternal
argument_list|(
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
operator|.
name|getNOW
argument_list|()
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|proc
operator|.
name|processDelete
argument_list|(
name|del
argument_list|)
expr_stmt|;
comment|// TODO: should this be more configurable?
comment|// TODO: in particular: should hard commit be optional?
name|CommitUpdateCommand
name|commit
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|commit
operator|.
name|softCommit
operator|=
literal|true
expr_stmt|;
name|commit
operator|.
name|openSearcher
operator|=
literal|true
expr_stmt|;
name|proc
operator|.
name|processCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|proc
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Finished periodic deletion of expired docs"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"IOException in periodic deletion of expired docs: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
comment|// DO NOT RETHROW: ScheduledExecutor will supress subsequent executions
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Runtime error in periodic deletion of expired docs: "
operator|+
name|re
operator|.
name|getMessage
argument_list|()
argument_list|,
name|re
argument_list|)
expr_stmt|;
comment|// DO NOT RETHROW: ScheduledExecutor will supress subsequent executions
block|}
finally|finally
block|{
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    *<p>    * Helper method that returns true if the Runnable managed by this factory     * should be responseible of doing periodica deletes.    *</p>    *<p>    * In simple standalone instalations this method always returns true,     * but in cloud mode it will be true if and only if we are currently the leader     * of the (active) slice with the first name (lexigraphically).    *</p>    *<p>    * If this method returns false, it may have also logged a message letting the user     * know why we aren't attempting period deletion (but it will attempt to not log     * this excessively)    *</p>    */
DECL|method|iAmInChargeOfPeriodicDeletes
specifier|private
name|boolean
name|iAmInChargeOfPeriodicDeletes
parameter_list|()
block|{
name|ZkController
name|zk
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|zk
condition|)
return|return
literal|true
return|;
comment|// This is a lot simpler then doing our own "leader" election across all replicas
comment|// of all shards since:
comment|//   a) we already have a per shard leader
comment|//   b) shard names must be unique
comment|//   c) ClusterState is already being "watched" by ZkController, no additional zk hits
comment|//   d) there might be multiple instances of this factory (in multiple chains) per
comment|//      collection, so picking an ephemeral node name for our election would be tricky
name|CloudDescriptor
name|desc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
name|String
name|col
init|=
name|desc
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|Slice
argument_list|>
argument_list|(
name|zk
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|col
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|slices
argument_list|,
name|COMPARE_SLICES_BY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|slices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Collection {} has no active Slices?"
argument_list|,
name|col
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|Replica
name|firstSliceLeader
init|=
name|slices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLeader
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|firstSliceLeader
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Slice in charge of periodic deletes for {} does not currently have a leader"
argument_list|,
name|col
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|String
name|leaderInCharge
init|=
name|firstSliceLeader
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|myCoreNodeName
init|=
name|desc
operator|.
name|getCoreNodeName
argument_list|()
decl_stmt|;
name|boolean
name|inChargeOfDeletesRightNow
init|=
name|leaderInCharge
operator|.
name|equals
argument_list|(
name|myCoreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|previouslyInChargeOfDeletes
operator|&&
operator|!
name|inChargeOfDeletesRightNow
condition|)
block|{
comment|// don't spam the logs constantly, just log when we know that we're not the guy
comment|// (the first time -- or anytime we were, but no longer are)
name|log
operator|.
name|info
argument_list|(
literal|"Not currently in charge of periodic deletes for this collection, "
operator|+
literal|"will not trigger delete or log again until this changes"
argument_list|)
expr_stmt|;
block|}
name|previouslyInChargeOfDeletes
operator|=
name|inChargeOfDeletesRightNow
expr_stmt|;
return|return
name|inChargeOfDeletesRightNow
return|;
block|}
comment|/** @see #iAmInChargeOfPeriodicDeletes */
DECL|field|previouslyInChargeOfDeletes
specifier|private
specifier|volatile
name|boolean
name|previouslyInChargeOfDeletes
init|=
literal|true
decl_stmt|;
DECL|field|COMPARE_SLICES_BY_NAME
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Slice
argument_list|>
name|COMPARE_SLICES_BY_NAME
init|=
operator|new
name|Comparator
argument_list|<
name|Slice
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Slice
name|a
parameter_list|,
name|Slice
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

