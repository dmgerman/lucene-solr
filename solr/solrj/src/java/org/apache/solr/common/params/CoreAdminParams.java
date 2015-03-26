begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * @since solr 1.3  */
end_comment

begin_class
DECL|class|CoreAdminParams
specifier|public
specifier|abstract
class|class
name|CoreAdminParams
block|{
comment|/** What Core are we talking about **/
DECL|field|CORE
specifier|public
specifier|final
specifier|static
name|String
name|CORE
init|=
literal|"core"
decl_stmt|;
comment|/** Should the STATUS request include index info **/
DECL|field|INDEX_INFO
specifier|public
specifier|final
specifier|static
name|String
name|INDEX_INFO
init|=
literal|"indexInfo"
decl_stmt|;
comment|/** Persistent -- should it save the cores state? **/
DECL|field|PERSISTENT
specifier|public
specifier|final
specifier|static
name|String
name|PERSISTENT
init|=
literal|"persistent"
decl_stmt|;
comment|/** If you rename something, what is the new name **/
DECL|field|NAME
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
comment|/** Core data directory **/
DECL|field|DATA_DIR
specifier|public
specifier|final
specifier|static
name|String
name|DATA_DIR
init|=
literal|"dataDir"
decl_stmt|;
comment|/** Core updatelog directory **/
DECL|field|ULOG_DIR
specifier|public
specifier|final
specifier|static
name|String
name|ULOG_DIR
init|=
literal|"ulogDir"
decl_stmt|;
comment|/** Name of the other core in actions involving 2 cores **/
DECL|field|OTHER
specifier|public
specifier|final
specifier|static
name|String
name|OTHER
init|=
literal|"other"
decl_stmt|;
comment|/** What action **/
DECL|field|ACTION
specifier|public
specifier|final
specifier|static
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
comment|/** If you specify a schema, what is its name **/
DECL|field|SCHEMA
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA
init|=
literal|"schema"
decl_stmt|;
comment|/** If you specify a configset, what is its name **/
DECL|field|CONFIGSET
specifier|public
specifier|final
specifier|static
name|String
name|CONFIGSET
init|=
literal|"configSet"
decl_stmt|;
comment|/** If you specify a config, what is its name **/
DECL|field|CONFIG
specifier|public
specifier|final
specifier|static
name|String
name|CONFIG
init|=
literal|"config"
decl_stmt|;
comment|/** Specifies a core instance dir. */
DECL|field|INSTANCE_DIR
specifier|public
specifier|final
specifier|static
name|String
name|INSTANCE_DIR
init|=
literal|"instanceDir"
decl_stmt|;
comment|/** If you specify a file, what is its name **/
DECL|field|FILE
specifier|public
specifier|final
specifier|static
name|String
name|FILE
init|=
literal|"file"
decl_stmt|;
comment|/** If you merge indexes, what are the index directories.    * The directories are specified by multiple indexDir parameters. */
DECL|field|INDEX_DIR
specifier|public
specifier|final
specifier|static
name|String
name|INDEX_DIR
init|=
literal|"indexDir"
decl_stmt|;
comment|/** If you merge indexes, what is the source core's name    * More than one source core can be specified by multiple srcCore parameters */
DECL|field|SRC_CORE
specifier|public
specifier|final
specifier|static
name|String
name|SRC_CORE
init|=
literal|"srcCore"
decl_stmt|;
comment|/** The collection name in solr cloud */
DECL|field|COLLECTION
specifier|public
specifier|final
specifier|static
name|String
name|COLLECTION
init|=
literal|"collection"
decl_stmt|;
comment|/** The replica name in solr cloud */
DECL|field|REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|REPLICA
init|=
literal|"replica"
decl_stmt|;
comment|/** The shard id in solr cloud */
DECL|field|SHARD
specifier|public
specifier|final
specifier|static
name|String
name|SHARD
init|=
literal|"shard"
decl_stmt|;
comment|/** The shard range in solr cloud */
DECL|field|SHARD_RANGE
specifier|public
specifier|final
specifier|static
name|String
name|SHARD_RANGE
init|=
literal|"shard.range"
decl_stmt|;
comment|/** The shard range in solr cloud */
DECL|field|SHARD_STATE
specifier|public
specifier|final
specifier|static
name|String
name|SHARD_STATE
init|=
literal|"shard.state"
decl_stmt|;
comment|/** The parent shard if applicable */
DECL|field|SHARD_PARENT
specifier|public
specifier|final
specifier|static
name|String
name|SHARD_PARENT
init|=
literal|"shard.parent"
decl_stmt|;
comment|/** The target core to which a split index should be written to    * Multiple targetCores can be specified by multiple targetCore parameters */
DECL|field|TARGET_CORE
specifier|public
specifier|final
specifier|static
name|String
name|TARGET_CORE
init|=
literal|"targetCore"
decl_stmt|;
comment|/** The hash ranges to be used to split a shard or an index */
DECL|field|RANGES
specifier|public
specifier|final
specifier|static
name|String
name|RANGES
init|=
literal|"ranges"
decl_stmt|;
DECL|field|ROLES
specifier|public
specifier|static
specifier|final
name|String
name|ROLES
init|=
literal|"roles"
decl_stmt|;
DECL|field|REQUESTID
specifier|public
specifier|static
specifier|final
name|String
name|REQUESTID
init|=
literal|"requestid"
decl_stmt|;
DECL|field|CORE_NODE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NODE_NAME
init|=
literal|"coreNodeName"
decl_stmt|;
comment|/** Prefix for core property name=value pair **/
DECL|field|PROPERTY_PREFIX
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_PREFIX
init|=
literal|"property."
decl_stmt|;
comment|/** If you unload a core, delete the index too */
DECL|field|DELETE_INDEX
specifier|public
specifier|final
specifier|static
name|String
name|DELETE_INDEX
init|=
literal|"deleteIndex"
decl_stmt|;
DECL|field|DELETE_DATA_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_DATA_DIR
init|=
literal|"deleteDataDir"
decl_stmt|;
DECL|field|DELETE_INSTANCE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_INSTANCE_DIR
init|=
literal|"deleteInstanceDir"
decl_stmt|;
DECL|field|LOAD_ON_STARTUP
specifier|public
specifier|static
specifier|final
name|String
name|LOAD_ON_STARTUP
init|=
literal|"loadOnStartup"
decl_stmt|;
DECL|field|TRANSIENT
specifier|public
specifier|static
specifier|final
name|String
name|TRANSIENT
init|=
literal|"transient"
decl_stmt|;
DECL|enum|CoreAdminAction
specifier|public
enum|enum
name|CoreAdminAction
block|{
DECL|enum constant|STATUS
name|STATUS
block|,
DECL|enum constant|LOAD
name|LOAD
block|,
DECL|enum constant|UNLOAD
name|UNLOAD
block|,
DECL|enum constant|RELOAD
name|RELOAD
block|,
DECL|enum constant|CREATE
name|CREATE
block|,
DECL|enum constant|PERSIST
name|PERSIST
block|,
DECL|enum constant|SWAP
name|SWAP
block|,
DECL|enum constant|RENAME
name|RENAME
block|,
DECL|enum constant|MERGEINDEXES
name|MERGEINDEXES
block|,
DECL|enum constant|SPLIT
name|SPLIT
block|,
DECL|enum constant|PREPRECOVERY
name|PREPRECOVERY
block|,
DECL|enum constant|REQUESTRECOVERY
name|REQUESTRECOVERY
block|,
DECL|enum constant|REQUESTSYNCSHARD
name|REQUESTSYNCSHARD
block|,
DECL|enum constant|CREATEALIAS
name|CREATEALIAS
block|,
DECL|enum constant|DELETEALIAS
name|DELETEALIAS
block|,
DECL|enum constant|REQUESTBUFFERUPDATES
name|REQUESTBUFFERUPDATES
block|,
DECL|enum constant|REQUESTAPPLYUPDATES
name|REQUESTAPPLYUPDATES
block|,
DECL|enum constant|LOAD_ON_STARTUP
name|LOAD_ON_STARTUP
block|,
DECL|enum constant|TRANSIENT
name|TRANSIENT
block|,
DECL|enum constant|OVERSEEROP
name|OVERSEEROP
block|,
DECL|enum constant|REQUESTSTATUS
name|REQUESTSTATUS
block|,
DECL|enum constant|REJOINLEADERELECTION
name|REJOINLEADERELECTION
block|;
DECL|method|get
specifier|public
specifier|static
name|CoreAdminAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|CoreAdminAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

