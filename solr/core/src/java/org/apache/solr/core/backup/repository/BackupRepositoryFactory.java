begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core.backup.repository
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|backup
operator|.
name|repository
package|;
end_package

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
name|HashMap
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
name|SolrException
operator|.
name|ErrorCode
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
name|SolrResourceLoader
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
DECL|class|BackupRepositoryFactory
specifier|public
class|class
name|BackupRepositoryFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|field|backupRepoPluginByName
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PluginInfo
argument_list|>
name|backupRepoPluginByName
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|defaultBackupRepoPlugin
specifier|private
name|PluginInfo
name|defaultBackupRepoPlugin
init|=
literal|null
decl_stmt|;
DECL|method|BackupRepositoryFactory
specifier|public
name|BackupRepositoryFactory
parameter_list|(
name|PluginInfo
index|[]
name|backupRepoPlugins
parameter_list|)
block|{
if|if
condition|(
name|backupRepoPlugins
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|backupRepoPlugins
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|backupRepoPlugins
index|[
name|i
index|]
operator|.
name|name
decl_stmt|;
name|boolean
name|isDefault
init|=
name|backupRepoPlugins
index|[
name|i
index|]
operator|.
name|isDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|backupRepoPluginByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Duplicate backup repository with name "
operator|+
name|name
argument_list|)
throw|;
block|}
if|if
condition|(
name|isDefault
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|defaultBackupRepoPlugin
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"More than one backup repository is configured as default"
argument_list|)
throw|;
block|}
name|this
operator|.
name|defaultBackupRepoPlugin
operator|=
name|backupRepoPlugins
index|[
name|i
index|]
expr_stmt|;
block|}
name|backupRepoPluginByName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|backupRepoPlugins
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added backup repository with configuration params {}"
argument_list|,
name|backupRepoPlugins
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|backupRepoPlugins
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|this
operator|.
name|defaultBackupRepoPlugin
operator|=
name|backupRepoPlugins
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|defaultBackupRepoPlugin
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Default configuration for backup repository is with configuration params {}"
argument_list|,
name|defaultBackupRepoPlugin
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|newInstance
specifier|public
name|BackupRepository
name|newInstance
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|PluginInfo
name|repo
init|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|backupRepoPluginByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"Could not find a backup repository with name "
operator|+
name|name
argument_list|)
decl_stmt|;
name|BackupRepository
name|result
init|=
name|loader
operator|.
name|newInstance
argument_list|(
name|repo
operator|.
name|className
argument_list|,
name|BackupRepository
operator|.
name|class
argument_list|)
decl_stmt|;
name|result
operator|.
name|init
argument_list|(
name|repo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|newInstance
specifier|public
name|BackupRepository
name|newInstance
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
if|if
condition|(
name|defaultBackupRepoPlugin
operator|!=
literal|null
condition|)
block|{
return|return
name|newInstance
argument_list|(
name|loader
argument_list|,
name|defaultBackupRepoPlugin
operator|.
name|name
argument_list|)
return|;
block|}
name|LocalFileSystemRepository
name|repo
init|=
operator|new
name|LocalFileSystemRepository
argument_list|()
decl_stmt|;
name|repo
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|repo
return|;
block|}
block|}
end_class

end_unit

