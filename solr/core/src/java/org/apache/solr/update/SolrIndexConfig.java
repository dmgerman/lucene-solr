begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|DelegatingAnalyzerWrapper
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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|index
operator|.
name|IndexWriter
operator|.
name|IndexReaderWarmer
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|MergePolicy
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
name|index
operator|.
name|MergeScheduler
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
name|search
operator|.
name|Sort
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
name|util
operator|.
name|InfoStream
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
name|util
operator|.
name|Version
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
name|Utils
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
name|common
operator|.
name|MapSerializable
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
name|PluginInfo
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
name|SolrConfig
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
name|SolrResourceLoader
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
name|index
operator|.
name|DefaultMergePolicyFactory
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
name|index
operator|.
name|MergePolicyFactory
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
name|index
operator|.
name|MergePolicyFactoryArgs
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
name|index
operator|.
name|SortingMergePolicy
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
name|IndexSchema
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
name|SolrPluginUtils
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|Config
operator|.
name|assertWarnOrFail
import|;
end_import

begin_comment
comment|/**  * This config object encapsulates IndexWriter config params,  * defined in the&lt;indexConfig&gt; section of solrconfig.xml  */
end_comment

begin_class
DECL|class|SolrIndexConfig
specifier|public
class|class
name|SolrIndexConfig
implements|implements
name|MapSerializable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NO_SUB_PACKAGES
specifier|private
specifier|static
specifier|final
name|String
name|NO_SUB_PACKAGES
index|[]
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|DEFAULT_MERGE_POLICY_FACTORY_CLASSNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_MERGE_POLICY_FACTORY_CLASSNAME
init|=
name|DefaultMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_MERGE_SCHEDULER_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
init|=
name|ConcurrentMergeScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|luceneVersion
specifier|public
specifier|final
name|Version
name|luceneVersion
decl_stmt|;
DECL|field|useCompoundFile
specifier|public
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|public
specifier|final
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|ramBufferSizeMB
specifier|public
specifier|final
name|double
name|ramBufferSizeMB
decl_stmt|;
DECL|field|writeLockTimeout
specifier|public
specifier|final
name|int
name|writeLockTimeout
decl_stmt|;
DECL|field|lockType
specifier|public
specifier|final
name|String
name|lockType
decl_stmt|;
DECL|field|mergePolicyFactoryInfo
specifier|public
specifier|final
name|PluginInfo
name|mergePolicyFactoryInfo
decl_stmt|;
DECL|field|mergeSchedulerInfo
specifier|public
specifier|final
name|PluginInfo
name|mergeSchedulerInfo
decl_stmt|;
DECL|field|metricsInfo
specifier|public
specifier|final
name|PluginInfo
name|metricsInfo
decl_stmt|;
DECL|field|mergedSegmentWarmerInfo
specifier|public
specifier|final
name|PluginInfo
name|mergedSegmentWarmerInfo
decl_stmt|;
DECL|field|infoStream
specifier|public
name|InfoStream
name|infoStream
init|=
name|InfoStream
operator|.
name|NO_OUTPUT
decl_stmt|;
comment|/**    * Internal constructor for setting defaults based on Lucene Version    */
DECL|method|SolrIndexConfig
specifier|private
name|SolrIndexConfig
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|)
block|{
name|luceneVersion
operator|=
name|solrConfig
operator|.
name|luceneMatchVersion
expr_stmt|;
name|useCompoundFile
operator|=
literal|false
expr_stmt|;
name|maxBufferedDocs
operator|=
operator|-
literal|1
expr_stmt|;
name|ramBufferSizeMB
operator|=
literal|100
expr_stmt|;
name|writeLockTimeout
operator|=
operator|-
literal|1
expr_stmt|;
name|lockType
operator|=
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NATIVE
expr_stmt|;
name|mergePolicyFactoryInfo
operator|=
literal|null
expr_stmt|;
name|mergeSchedulerInfo
operator|=
literal|null
expr_stmt|;
name|mergedSegmentWarmerInfo
operator|=
literal|null
expr_stmt|;
comment|// enable coarse-grained metrics by default
name|metricsInfo
operator|=
operator|new
name|PluginInfo
argument_list|(
literal|"metrics"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a SolrIndexConfig which parses the Lucene related config params in solrconfig.xml    * @param solrConfig the overall SolrConfig object    * @param prefix the XPath prefix for which section to parse (mandatory)    * @param def a SolrIndexConfig instance to pick default values from (optional)    */
DECL|method|SolrIndexConfig
specifier|public
name|SolrIndexConfig
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|prefix
parameter_list|,
name|SolrIndexConfig
name|def
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
literal|"indexConfig"
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Defaulting to prefix \""
operator|+
name|prefix
operator|+
literal|"\" for index configuration"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
name|def
operator|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|)
expr_stmt|;
block|}
comment|// sanity check: this will throw an error for us if there is more then one
comment|// config section
name|Object
name|unused
init|=
name|solrConfig
operator|.
name|getNode
argument_list|(
name|prefix
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|luceneVersion
operator|=
name|solrConfig
operator|.
name|luceneMatchVersion
expr_stmt|;
comment|// Assert that end-of-life parameters or syntax is not in our config.
comment|// Warn for luceneMatchVersion's before LUCENE_3_6, fail fast above
name|assertWarnOrFail
argument_list|(
literal|"The<mergeScheduler>myclass</mergeScheduler> syntax is no longer supported in solrconfig.xml. Please use syntax<mergeScheduler class=\"myclass\"/> instead."
argument_list|,
operator|!
operator|(
operator|(
name|solrConfig
operator|.
name|getNode
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler"
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler/@class"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
operator|)
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"Beginning with Solr 7.0,<mergePolicy>myclass</mergePolicy> is no longer supported, use<mergePolicyFactory> instead."
argument_list|,
operator|!
operator|(
operator|(
name|solrConfig
operator|.
name|getNode
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy"
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy/@class"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
operator|)
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"The<luceneAutoCommit>true|false</luceneAutoCommit> parameter is no longer valid in solrconfig.xml."
argument_list|,
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/luceneAutoCommit"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|useCompoundFile
operator|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/useCompoundFile"
argument_list|,
name|def
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
name|maxBufferedDocs
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxBufferedDocs"
argument_list|,
name|def
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|solrConfig
operator|.
name|getDouble
argument_list|(
name|prefix
operator|+
literal|"/ramBufferSizeMB"
argument_list|,
name|def
operator|.
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
name|writeLockTimeout
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/writeLockTimeout"
argument_list|,
name|def
operator|.
name|writeLockTimeout
argument_list|)
expr_stmt|;
name|lockType
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/lockType"
argument_list|,
name|def
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|infos
init|=
name|solrConfig
operator|.
name|readPluginInfos
argument_list|(
name|prefix
operator|+
literal|"/metrics"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|infos
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|metricsInfo
operator|=
name|def
operator|.
name|metricsInfo
expr_stmt|;
block|}
else|else
block|{
name|metricsInfo
operator|=
name|infos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|mergeSchedulerInfo
operator|=
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler"
argument_list|,
name|solrConfig
argument_list|,
name|def
operator|.
name|mergeSchedulerInfo
argument_list|)
expr_stmt|;
name|mergePolicyFactoryInfo
operator|=
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergePolicyFactory"
argument_list|,
name|solrConfig
argument_list|,
name|def
operator|.
name|mergePolicyFactoryInfo
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"Beginning with Solr 7.0,<mergePolicy> is no longer supported, use<mergePolicyFactory> instead."
argument_list|,
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy"
argument_list|,
name|solrConfig
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"Beginning with Solr 7.0,<maxMergeDocs> is no longer supported, configure it on the relevant<mergePolicyFactory> instead."
argument_list|,
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxMergeDocs"
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"Beginning with Solr 7.0,<mergeFactor> is no longer supported, configure it on the relevant<mergePolicyFactory> instead."
argument_list|,
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/mergeFactor"
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|val
init|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/termIndexInterval"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal parameter 'termIndexInterval'"
argument_list|)
throw|;
block|}
name|boolean
name|infoStreamEnabled
init|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/infoStream"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStreamEnabled
condition|)
block|{
name|String
name|infoStreamFile
init|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/infoStream/@file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStreamFile
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"IndexWriter infoStream solr logging is enabled"
argument_list|)
expr_stmt|;
name|infoStream
operator|=
operator|new
name|LoggingInfoStream
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Remove @file from<infoStream> to output messages to solr's logfile"
argument_list|)
throw|;
block|}
block|}
name|mergedSegmentWarmerInfo
operator|=
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergedSegmentWarmer"
argument_list|,
name|solrConfig
argument_list|,
name|def
operator|.
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"Beginning with Solr 5.0,<checkIntegrityAtMerge> option is no longer supported and should be removed from solrconfig.xml (these integrity checks are now automatic)"
argument_list|,
operator|(
literal|null
operator|==
name|solrConfig
operator|.
name|getNode
argument_list|(
name|prefix
operator|+
literal|"/checkIntegrityAtMerge"
argument_list|,
literal|false
argument_list|)
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"useCompoundFile"
argument_list|,
name|useCompoundFile
argument_list|,
literal|"maxBufferedDocs"
argument_list|,
name|maxBufferedDocs
argument_list|,
literal|"ramBufferSizeMB"
argument_list|,
name|ramBufferSizeMB
argument_list|,
literal|"writeLockTimeout"
argument_list|,
name|writeLockTimeout
argument_list|,
literal|"lockType"
argument_list|,
name|lockType
argument_list|,
literal|"infoStreamEnabled"
argument_list|,
name|infoStream
operator|!=
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeSchedulerInfo
operator|!=
literal|null
condition|)
name|m
operator|.
name|put
argument_list|(
literal|"mergeScheduler"
argument_list|,
name|mergeSchedulerInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|metricsInfo
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"metrics"
argument_list|,
name|metricsInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergePolicyFactoryInfo
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
literal|"mergePolicyFactory"
argument_list|,
name|mergePolicyFactoryInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergedSegmentWarmerInfo
operator|!=
literal|null
condition|)
name|m
operator|.
name|put
argument_list|(
literal|"mergedSegmentWarmer"
argument_list|,
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|getPluginInfo
specifier|private
name|PluginInfo
name|getPluginInfo
parameter_list|(
name|String
name|path
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|PluginInfo
name|def
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|l
init|=
name|solrConfig
operator|.
name|readPluginInfos
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|l
operator|.
name|isEmpty
argument_list|()
condition|?
name|def
else|:
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|class|DelayedSchemaAnalyzer
specifier|private
specifier|static
class|class
name|DelayedSchemaAnalyzer
extends|extends
name|DelegatingAnalyzerWrapper
block|{
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|method|DelayedSchemaAnalyzer
specifier|public
name|DelayedSchemaAnalyzer
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|super
argument_list|(
name|PER_FIELD_REUSE_STRATEGY
argument_list|)
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWrappedAnalyzer
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getIndexAnalyzer
argument_list|()
return|;
block|}
block|}
DECL|method|toIndexWriterConfig
specifier|public
name|IndexWriterConfig
name|toIndexWriterConfig
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|DelayedSchemaAnalyzer
argument_list|(
name|core
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxBufferedDocs
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|ramBufferSizeMB
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setSimilarity
argument_list|(
name|schema
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|MergePolicy
name|mergePolicy
init|=
name|buildMergePolicy
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|mergePolicy
argument_list|)
expr_stmt|;
name|MergeScheduler
name|mergeScheduler
init|=
name|buildMergeScheduler
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
name|mergeScheduler
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergePolicy
operator|instanceof
name|SortingMergePolicy
condition|)
block|{
name|Sort
name|indexSort
init|=
operator|(
operator|(
name|SortingMergePolicy
operator|)
name|mergePolicy
operator|)
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexSort
argument_list|(
name|indexSort
argument_list|)
expr_stmt|;
block|}
name|iwc
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergedSegmentWarmerInfo
operator|!=
literal|null
condition|)
block|{
comment|// TODO: add infostream -> normal logging system (there is an issue somewhere)
name|IndexReaderWarmer
name|warmer
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|mergedSegmentWarmerInfo
operator|.
name|className
argument_list|,
name|IndexReaderWarmer
operator|.
name|class
argument_list|,
literal|null
argument_list|,
operator|new
name|Class
index|[]
block|{
name|InfoStream
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|iwc
operator|.
name|getInfoStream
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergedSegmentWarmer
argument_list|(
name|warmer
argument_list|)
expr_stmt|;
block|}
return|return
name|iwc
return|;
block|}
comment|/**    * Builds a MergePolicy using the configured MergePolicyFactory    * or if no factory is configured uses the configured mergePolicy PluginInfo.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|buildMergePolicy
specifier|private
name|MergePolicy
name|buildMergePolicy
parameter_list|(
specifier|final
name|IndexSchema
name|schema
parameter_list|)
block|{
specifier|final
name|String
name|mpfClassName
decl_stmt|;
specifier|final
name|MergePolicyFactoryArgs
name|mpfArgs
decl_stmt|;
if|if
condition|(
name|mergePolicyFactoryInfo
operator|==
literal|null
condition|)
block|{
name|mpfClassName
operator|=
name|DEFAULT_MERGE_POLICY_FACTORY_CLASSNAME
expr_stmt|;
name|mpfArgs
operator|=
operator|new
name|MergePolicyFactoryArgs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|mpfClassName
operator|=
name|mergePolicyFactoryInfo
operator|.
name|className
expr_stmt|;
name|mpfArgs
operator|=
operator|new
name|MergePolicyFactoryArgs
argument_list|(
name|mergePolicyFactoryInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SolrResourceLoader
name|resourceLoader
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
specifier|final
name|MergePolicyFactory
name|mpf
init|=
name|resourceLoader
operator|.
name|newInstance
argument_list|(
name|mpfClassName
argument_list|,
name|MergePolicyFactory
operator|.
name|class
argument_list|,
name|NO_SUB_PACKAGES
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SolrResourceLoader
operator|.
name|class
block|,
name|MergePolicyFactoryArgs
operator|.
name|class
block|,
name|IndexSchema
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|resourceLoader
block|,
name|mpfArgs
block|,
name|schema
block|}
argument_list|)
decl_stmt|;
return|return
name|mpf
operator|.
name|getMergePolicy
argument_list|()
return|;
block|}
DECL|method|buildMergeScheduler
specifier|private
name|MergeScheduler
name|buildMergeScheduler
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|String
name|msClassName
init|=
name|mergeSchedulerInfo
operator|==
literal|null
condition|?
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
else|:
name|mergeSchedulerInfo
operator|.
name|className
decl_stmt|;
name|MergeScheduler
name|scheduler
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|msClassName
argument_list|,
name|MergeScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeSchedulerInfo
operator|!=
literal|null
condition|)
block|{
comment|// LUCENE-5080: these two setters are removed, so we have to invoke setMaxMergesAndThreads
comment|// if someone has them configured.
if|if
condition|(
name|scheduler
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
block|{
name|NamedList
name|args
init|=
name|mergeSchedulerInfo
operator|.
name|initArgs
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Integer
name|maxMergeCount
init|=
operator|(
name|Integer
operator|)
name|args
operator|.
name|remove
argument_list|(
literal|"maxMergeCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxMergeCount
operator|==
literal|null
condition|)
block|{
name|maxMergeCount
operator|=
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|scheduler
operator|)
operator|.
name|getMaxMergeCount
argument_list|()
expr_stmt|;
block|}
name|Integer
name|maxThreadCount
init|=
operator|(
name|Integer
operator|)
name|args
operator|.
name|remove
argument_list|(
literal|"maxThreadCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxThreadCount
operator|==
literal|null
condition|)
block|{
name|maxThreadCount
operator|=
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|scheduler
operator|)
operator|.
name|getMaxThreadCount
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|scheduler
operator|)
operator|.
name|setMaxMergesAndThreads
argument_list|(
name|maxMergeCount
argument_list|,
name|maxThreadCount
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|scheduler
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|scheduler
argument_list|,
name|mergeSchedulerInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|scheduler
return|;
block|}
block|}
end_class

end_unit

