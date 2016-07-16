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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|LogWatcherConfig
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
name|UpdateShardHandlerConfig
import|;
end_import

begin_class
DECL|class|NodeConfig
specifier|public
class|class
name|NodeConfig
block|{
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|coreRootDirectory
specifier|private
specifier|final
name|Path
name|coreRootDirectory
decl_stmt|;
DECL|field|configSetBaseDirectory
specifier|private
specifier|final
name|Path
name|configSetBaseDirectory
decl_stmt|;
DECL|field|sharedLibDirectory
specifier|private
specifier|final
name|String
name|sharedLibDirectory
decl_stmt|;
DECL|field|shardHandlerFactoryConfig
specifier|private
specifier|final
name|PluginInfo
name|shardHandlerFactoryConfig
decl_stmt|;
DECL|field|updateShardHandlerConfig
specifier|private
specifier|final
name|UpdateShardHandlerConfig
name|updateShardHandlerConfig
decl_stmt|;
DECL|field|coreAdminHandlerClass
specifier|private
specifier|final
name|String
name|coreAdminHandlerClass
decl_stmt|;
DECL|field|collectionsAdminHandlerClass
specifier|private
specifier|final
name|String
name|collectionsAdminHandlerClass
decl_stmt|;
DECL|field|infoHandlerClass
specifier|private
specifier|final
name|String
name|infoHandlerClass
decl_stmt|;
DECL|field|configSetsHandlerClass
specifier|private
specifier|final
name|String
name|configSetsHandlerClass
decl_stmt|;
DECL|field|logWatcherConfig
specifier|private
specifier|final
name|LogWatcherConfig
name|logWatcherConfig
decl_stmt|;
DECL|field|cloudConfig
specifier|private
specifier|final
name|CloudConfig
name|cloudConfig
decl_stmt|;
DECL|field|coreLoadThreads
specifier|private
specifier|final
name|Integer
name|coreLoadThreads
decl_stmt|;
DECL|field|transientCacheSize
specifier|private
specifier|final
name|int
name|transientCacheSize
decl_stmt|;
DECL|field|useSchemaCache
specifier|private
specifier|final
name|boolean
name|useSchemaCache
decl_stmt|;
DECL|field|managementPath
specifier|private
specifier|final
name|String
name|managementPath
decl_stmt|;
DECL|field|backupRepositoryPlugins
specifier|private
specifier|final
name|PluginInfo
index|[]
name|backupRepositoryPlugins
decl_stmt|;
DECL|method|NodeConfig
specifier|private
name|NodeConfig
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|Path
name|coreRootDirectory
parameter_list|,
name|Path
name|configSetBaseDirectory
parameter_list|,
name|String
name|sharedLibDirectory
parameter_list|,
name|PluginInfo
name|shardHandlerFactoryConfig
parameter_list|,
name|UpdateShardHandlerConfig
name|updateShardHandlerConfig
parameter_list|,
name|String
name|coreAdminHandlerClass
parameter_list|,
name|String
name|collectionsAdminHandlerClass
parameter_list|,
name|String
name|infoHandlerClass
parameter_list|,
name|String
name|configSetsHandlerClass
parameter_list|,
name|LogWatcherConfig
name|logWatcherConfig
parameter_list|,
name|CloudConfig
name|cloudConfig
parameter_list|,
name|Integer
name|coreLoadThreads
parameter_list|,
name|int
name|transientCacheSize
parameter_list|,
name|boolean
name|useSchemaCache
parameter_list|,
name|String
name|managementPath
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|Properties
name|solrProperties
parameter_list|,
name|PluginInfo
index|[]
name|backupRepositoryPlugins
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|coreRootDirectory
operator|=
name|coreRootDirectory
expr_stmt|;
name|this
operator|.
name|configSetBaseDirectory
operator|=
name|configSetBaseDirectory
expr_stmt|;
name|this
operator|.
name|sharedLibDirectory
operator|=
name|sharedLibDirectory
expr_stmt|;
name|this
operator|.
name|shardHandlerFactoryConfig
operator|=
name|shardHandlerFactoryConfig
expr_stmt|;
name|this
operator|.
name|updateShardHandlerConfig
operator|=
name|updateShardHandlerConfig
expr_stmt|;
name|this
operator|.
name|coreAdminHandlerClass
operator|=
name|coreAdminHandlerClass
expr_stmt|;
name|this
operator|.
name|collectionsAdminHandlerClass
operator|=
name|collectionsAdminHandlerClass
expr_stmt|;
name|this
operator|.
name|infoHandlerClass
operator|=
name|infoHandlerClass
expr_stmt|;
name|this
operator|.
name|configSetsHandlerClass
operator|=
name|configSetsHandlerClass
expr_stmt|;
name|this
operator|.
name|logWatcherConfig
operator|=
name|logWatcherConfig
expr_stmt|;
name|this
operator|.
name|cloudConfig
operator|=
name|cloudConfig
expr_stmt|;
name|this
operator|.
name|coreLoadThreads
operator|=
name|coreLoadThreads
expr_stmt|;
name|this
operator|.
name|transientCacheSize
operator|=
name|transientCacheSize
expr_stmt|;
name|this
operator|.
name|useSchemaCache
operator|=
name|useSchemaCache
expr_stmt|;
name|this
operator|.
name|managementPath
operator|=
name|managementPath
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|solrProperties
operator|=
name|solrProperties
expr_stmt|;
name|this
operator|.
name|backupRepositoryPlugins
operator|=
name|backupRepositoryPlugins
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|cloudConfig
operator|!=
literal|null
operator|&&
name|this
operator|.
name|getCoreLoadThreadCount
argument_list|(
name|NodeConfigBuilder
operator|.
name|DEFAULT_CORE_LOAD_THREADS
argument_list|)
operator|<
literal|2
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
name|SERVER_ERROR
argument_list|,
literal|"SolrCloud requires a value of at least 2 for coreLoadThreads (configured value = "
operator|+
name|this
operator|.
name|coreLoadThreads
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
DECL|method|getCoreRootDirectory
specifier|public
name|Path
name|getCoreRootDirectory
parameter_list|()
block|{
return|return
name|coreRootDirectory
return|;
block|}
DECL|method|getShardHandlerFactoryPluginInfo
specifier|public
name|PluginInfo
name|getShardHandlerFactoryPluginInfo
parameter_list|()
block|{
return|return
name|shardHandlerFactoryConfig
return|;
block|}
DECL|method|getUpdateShardHandlerConfig
specifier|public
name|UpdateShardHandlerConfig
name|getUpdateShardHandlerConfig
parameter_list|()
block|{
return|return
name|updateShardHandlerConfig
return|;
block|}
DECL|method|getCoreLoadThreadCount
specifier|public
name|int
name|getCoreLoadThreadCount
parameter_list|(
name|int
name|def
parameter_list|)
block|{
return|return
name|coreLoadThreads
operator|==
literal|null
condition|?
name|def
else|:
name|coreLoadThreads
return|;
block|}
DECL|method|getSharedLibDirectory
specifier|public
name|String
name|getSharedLibDirectory
parameter_list|()
block|{
return|return
name|sharedLibDirectory
return|;
block|}
DECL|method|getCoreAdminHandlerClass
specifier|public
name|String
name|getCoreAdminHandlerClass
parameter_list|()
block|{
return|return
name|coreAdminHandlerClass
return|;
block|}
DECL|method|getCollectionsHandlerClass
specifier|public
name|String
name|getCollectionsHandlerClass
parameter_list|()
block|{
return|return
name|collectionsAdminHandlerClass
return|;
block|}
DECL|method|getInfoHandlerClass
specifier|public
name|String
name|getInfoHandlerClass
parameter_list|()
block|{
return|return
name|infoHandlerClass
return|;
block|}
DECL|method|getConfigSetsHandlerClass
specifier|public
name|String
name|getConfigSetsHandlerClass
parameter_list|()
block|{
return|return
name|configSetsHandlerClass
return|;
block|}
DECL|method|hasSchemaCache
specifier|public
name|boolean
name|hasSchemaCache
parameter_list|()
block|{
return|return
name|useSchemaCache
return|;
block|}
DECL|method|getManagementPath
specifier|public
name|String
name|getManagementPath
parameter_list|()
block|{
return|return
name|managementPath
return|;
block|}
DECL|method|getConfigSetBaseDirectory
specifier|public
name|Path
name|getConfigSetBaseDirectory
parameter_list|()
block|{
return|return
name|configSetBaseDirectory
return|;
block|}
DECL|method|getLogWatcherConfig
specifier|public
name|LogWatcherConfig
name|getLogWatcherConfig
parameter_list|()
block|{
return|return
name|logWatcherConfig
return|;
block|}
DECL|method|getCloudConfig
specifier|public
name|CloudConfig
name|getCloudConfig
parameter_list|()
block|{
return|return
name|cloudConfig
return|;
block|}
DECL|method|getTransientCacheSize
specifier|public
name|int
name|getTransientCacheSize
parameter_list|()
block|{
return|return
name|transientCacheSize
return|;
block|}
DECL|field|loader
specifier|protected
specifier|final
name|SolrResourceLoader
name|loader
decl_stmt|;
DECL|field|solrProperties
specifier|protected
specifier|final
name|Properties
name|solrProperties
decl_stmt|;
DECL|method|getSolrProperties
specifier|public
name|Properties
name|getSolrProperties
parameter_list|()
block|{
return|return
name|solrProperties
return|;
block|}
DECL|method|getSolrResourceLoader
specifier|public
name|SolrResourceLoader
name|getSolrResourceLoader
parameter_list|()
block|{
return|return
name|loader
return|;
block|}
DECL|method|getBackupRepositoryPlugins
specifier|public
name|PluginInfo
index|[]
name|getBackupRepositoryPlugins
parameter_list|()
block|{
return|return
name|backupRepositoryPlugins
return|;
block|}
DECL|class|NodeConfigBuilder
specifier|public
specifier|static
class|class
name|NodeConfigBuilder
block|{
DECL|field|coreRootDirectory
specifier|private
name|Path
name|coreRootDirectory
decl_stmt|;
DECL|field|configSetBaseDirectory
specifier|private
name|Path
name|configSetBaseDirectory
decl_stmt|;
DECL|field|sharedLibDirectory
specifier|private
name|String
name|sharedLibDirectory
init|=
literal|"lib"
decl_stmt|;
DECL|field|shardHandlerFactoryConfig
specifier|private
name|PluginInfo
name|shardHandlerFactoryConfig
decl_stmt|;
DECL|field|updateShardHandlerConfig
specifier|private
name|UpdateShardHandlerConfig
name|updateShardHandlerConfig
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|coreAdminHandlerClass
specifier|private
name|String
name|coreAdminHandlerClass
init|=
name|DEFAULT_ADMINHANDLERCLASS
decl_stmt|;
DECL|field|collectionsAdminHandlerClass
specifier|private
name|String
name|collectionsAdminHandlerClass
init|=
name|DEFAULT_COLLECTIONSHANDLERCLASS
decl_stmt|;
DECL|field|infoHandlerClass
specifier|private
name|String
name|infoHandlerClass
init|=
name|DEFAULT_INFOHANDLERCLASS
decl_stmt|;
DECL|field|configSetsHandlerClass
specifier|private
name|String
name|configSetsHandlerClass
init|=
name|DEFAULT_CONFIGSETSHANDLERCLASS
decl_stmt|;
DECL|field|logWatcherConfig
specifier|private
name|LogWatcherConfig
name|logWatcherConfig
init|=
operator|new
name|LogWatcherConfig
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|50
argument_list|)
decl_stmt|;
DECL|field|cloudConfig
specifier|private
name|CloudConfig
name|cloudConfig
decl_stmt|;
DECL|field|coreLoadThreads
specifier|private
name|Integer
name|coreLoadThreads
decl_stmt|;
DECL|field|transientCacheSize
specifier|private
name|int
name|transientCacheSize
init|=
name|DEFAULT_TRANSIENT_CACHE_SIZE
decl_stmt|;
DECL|field|useSchemaCache
specifier|private
name|boolean
name|useSchemaCache
init|=
literal|false
decl_stmt|;
DECL|field|managementPath
specifier|private
name|String
name|managementPath
decl_stmt|;
DECL|field|solrProperties
specifier|private
name|Properties
name|solrProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|backupRepositoryPlugins
specifier|private
name|PluginInfo
index|[]
name|backupRepositoryPlugins
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|SolrResourceLoader
name|loader
decl_stmt|;
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|DEFAULT_CORE_LOAD_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CORE_LOAD_THREADS
init|=
literal|3
decl_stmt|;
comment|//No:of core load threads in cloud mode is set to a default of 24
DECL|field|DEFAULT_CORE_LOAD_THREADS_IN_CLOUD
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CORE_LOAD_THREADS_IN_CLOUD
init|=
literal|24
decl_stmt|;
DECL|field|DEFAULT_TRANSIENT_CACHE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_TRANSIENT_CACHE_SIZE
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|DEFAULT_ADMINHANDLERCLASS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ADMINHANDLERCLASS
init|=
literal|"org.apache.solr.handler.admin.CoreAdminHandler"
decl_stmt|;
DECL|field|DEFAULT_INFOHANDLERCLASS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_INFOHANDLERCLASS
init|=
literal|"org.apache.solr.handler.admin.InfoHandler"
decl_stmt|;
DECL|field|DEFAULT_COLLECTIONSHANDLERCLASS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_COLLECTIONSHANDLERCLASS
init|=
literal|"org.apache.solr.handler.admin.CollectionsHandler"
decl_stmt|;
DECL|field|DEFAULT_CONFIGSETSHANDLERCLASS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CONFIGSETSHANDLERCLASS
init|=
literal|"org.apache.solr.handler.admin.ConfigSetsHandler"
decl_stmt|;
DECL|method|NodeConfigBuilder
specifier|public
name|NodeConfigBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|coreRootDirectory
operator|=
name|loader
operator|.
name|getInstancePath
argument_list|()
expr_stmt|;
name|this
operator|.
name|configSetBaseDirectory
operator|=
name|loader
operator|.
name|getInstancePath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
expr_stmt|;
block|}
DECL|method|setCoreRootDirectory
specifier|public
name|NodeConfigBuilder
name|setCoreRootDirectory
parameter_list|(
name|String
name|coreRootDirectory
parameter_list|)
block|{
name|this
operator|.
name|coreRootDirectory
operator|=
name|loader
operator|.
name|getInstancePath
argument_list|()
operator|.
name|resolve
argument_list|(
name|coreRootDirectory
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConfigSetBaseDirectory
specifier|public
name|NodeConfigBuilder
name|setConfigSetBaseDirectory
parameter_list|(
name|String
name|configSetBaseDirectory
parameter_list|)
block|{
name|this
operator|.
name|configSetBaseDirectory
operator|=
name|loader
operator|.
name|getInstancePath
argument_list|()
operator|.
name|resolve
argument_list|(
name|configSetBaseDirectory
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSharedLibDirectory
specifier|public
name|NodeConfigBuilder
name|setSharedLibDirectory
parameter_list|(
name|String
name|sharedLibDirectory
parameter_list|)
block|{
name|this
operator|.
name|sharedLibDirectory
operator|=
name|sharedLibDirectory
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setShardHandlerFactoryConfig
specifier|public
name|NodeConfigBuilder
name|setShardHandlerFactoryConfig
parameter_list|(
name|PluginInfo
name|shardHandlerFactoryConfig
parameter_list|)
block|{
name|this
operator|.
name|shardHandlerFactoryConfig
operator|=
name|shardHandlerFactoryConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setUpdateShardHandlerConfig
specifier|public
name|NodeConfigBuilder
name|setUpdateShardHandlerConfig
parameter_list|(
name|UpdateShardHandlerConfig
name|updateShardHandlerConfig
parameter_list|)
block|{
name|this
operator|.
name|updateShardHandlerConfig
operator|=
name|updateShardHandlerConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCoreAdminHandlerClass
specifier|public
name|NodeConfigBuilder
name|setCoreAdminHandlerClass
parameter_list|(
name|String
name|coreAdminHandlerClass
parameter_list|)
block|{
name|this
operator|.
name|coreAdminHandlerClass
operator|=
name|coreAdminHandlerClass
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCollectionsAdminHandlerClass
specifier|public
name|NodeConfigBuilder
name|setCollectionsAdminHandlerClass
parameter_list|(
name|String
name|collectionsAdminHandlerClass
parameter_list|)
block|{
name|this
operator|.
name|collectionsAdminHandlerClass
operator|=
name|collectionsAdminHandlerClass
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setInfoHandlerClass
specifier|public
name|NodeConfigBuilder
name|setInfoHandlerClass
parameter_list|(
name|String
name|infoHandlerClass
parameter_list|)
block|{
name|this
operator|.
name|infoHandlerClass
operator|=
name|infoHandlerClass
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConfigSetsHandlerClass
specifier|public
name|NodeConfigBuilder
name|setConfigSetsHandlerClass
parameter_list|(
name|String
name|configSetsHandlerClass
parameter_list|)
block|{
name|this
operator|.
name|configSetsHandlerClass
operator|=
name|configSetsHandlerClass
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLogWatcherConfig
specifier|public
name|NodeConfigBuilder
name|setLogWatcherConfig
parameter_list|(
name|LogWatcherConfig
name|logWatcherConfig
parameter_list|)
block|{
name|this
operator|.
name|logWatcherConfig
operator|=
name|logWatcherConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCloudConfig
specifier|public
name|NodeConfigBuilder
name|setCloudConfig
parameter_list|(
name|CloudConfig
name|cloudConfig
parameter_list|)
block|{
name|this
operator|.
name|cloudConfig
operator|=
name|cloudConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCoreLoadThreads
specifier|public
name|NodeConfigBuilder
name|setCoreLoadThreads
parameter_list|(
name|int
name|coreLoadThreads
parameter_list|)
block|{
name|this
operator|.
name|coreLoadThreads
operator|=
name|coreLoadThreads
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTransientCacheSize
specifier|public
name|NodeConfigBuilder
name|setTransientCacheSize
parameter_list|(
name|int
name|transientCacheSize
parameter_list|)
block|{
name|this
operator|.
name|transientCacheSize
operator|=
name|transientCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setUseSchemaCache
specifier|public
name|NodeConfigBuilder
name|setUseSchemaCache
parameter_list|(
name|boolean
name|useSchemaCache
parameter_list|)
block|{
name|this
operator|.
name|useSchemaCache
operator|=
name|useSchemaCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setManagementPath
specifier|public
name|NodeConfigBuilder
name|setManagementPath
parameter_list|(
name|String
name|managementPath
parameter_list|)
block|{
name|this
operator|.
name|managementPath
operator|=
name|managementPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSolrProperties
specifier|public
name|NodeConfigBuilder
name|setSolrProperties
parameter_list|(
name|Properties
name|solrProperties
parameter_list|)
block|{
name|this
operator|.
name|solrProperties
operator|=
name|solrProperties
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBackupRepositoryPlugins
specifier|public
name|NodeConfigBuilder
name|setBackupRepositoryPlugins
parameter_list|(
name|PluginInfo
index|[]
name|backupRepositoryPlugins
parameter_list|)
block|{
name|this
operator|.
name|backupRepositoryPlugins
operator|=
name|backupRepositoryPlugins
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|NodeConfig
name|build
parameter_list|()
block|{
return|return
operator|new
name|NodeConfig
argument_list|(
name|nodeName
argument_list|,
name|coreRootDirectory
argument_list|,
name|configSetBaseDirectory
argument_list|,
name|sharedLibDirectory
argument_list|,
name|shardHandlerFactoryConfig
argument_list|,
name|updateShardHandlerConfig
argument_list|,
name|coreAdminHandlerClass
argument_list|,
name|collectionsAdminHandlerClass
argument_list|,
name|infoHandlerClass
argument_list|,
name|configSetsHandlerClass
argument_list|,
name|logWatcherConfig
argument_list|,
name|cloudConfig
argument_list|,
name|coreLoadThreads
argument_list|,
name|transientCacheSize
argument_list|,
name|useSchemaCache
argument_list|,
name|managementPath
argument_list|,
name|loader
argument_list|,
name|solrProperties
argument_list|,
name|backupRepositoryPlugins
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

