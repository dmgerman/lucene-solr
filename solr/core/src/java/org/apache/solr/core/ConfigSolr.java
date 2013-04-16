begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|handler
operator|.
name|component
operator|.
name|ShardHandlerFactory
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * ConfigSolr is a new interface  to aid us in obsoleting solr.xml and replacing it with solr.properties. The problem here  * is that the Config class is used for _all_ the xml file, e.g. solrconfig.xml and we can't mess with _that_ as part  * of this issue. Primarily used in CoreContainer at present.  *<p/>  * This is already deprecated, it's only intended to exist for while transitioning to properties-based replacement for  * solr.xml  *  * @since solr 4.3  */
end_comment

begin_interface
DECL|interface|ConfigSolr
specifier|public
interface|interface
name|ConfigSolr
block|{
comment|// Ugly for now, but we'll at least be able to centralize all of the differences between 4x and 5x.
DECL|enum|CfgProp
specifier|public
specifier|static
enum|enum
name|CfgProp
block|{
DECL|enum constant|SOLR_ADMINHANDLER
name|SOLR_ADMINHANDLER
block|,
DECL|enum constant|SOLR_CORELOADTHREADS
name|SOLR_CORELOADTHREADS
block|,
DECL|enum constant|SOLR_COREROOTDIRECTORY
name|SOLR_COREROOTDIRECTORY
block|,
DECL|enum constant|SOLR_DISTRIBUPDATECONNTIMEOUT
name|SOLR_DISTRIBUPDATECONNTIMEOUT
block|,
DECL|enum constant|SOLR_DISTRIBUPDATESOTIMEOUT
name|SOLR_DISTRIBUPDATESOTIMEOUT
block|,
DECL|enum constant|SOLR_HOST
name|SOLR_HOST
block|,
DECL|enum constant|SOLR_HOSTCONTEXT
name|SOLR_HOSTCONTEXT
block|,
DECL|enum constant|SOLR_HOSTPORT
name|SOLR_HOSTPORT
block|,
DECL|enum constant|SOLR_LEADERVOTEWAIT
name|SOLR_LEADERVOTEWAIT
block|,
DECL|enum constant|SOLR_LOGGING_CLASS
name|SOLR_LOGGING_CLASS
block|,
DECL|enum constant|SOLR_LOGGING_ENABLED
name|SOLR_LOGGING_ENABLED
block|,
DECL|enum constant|SOLR_LOGGING_WATCHER_SIZE
name|SOLR_LOGGING_WATCHER_SIZE
block|,
DECL|enum constant|SOLR_LOGGING_WATCHER_THRESHOLD
name|SOLR_LOGGING_WATCHER_THRESHOLD
block|,
DECL|enum constant|SOLR_MANAGEMENTPATH
name|SOLR_MANAGEMENTPATH
block|,
DECL|enum constant|SOLR_SHAREDLIB
name|SOLR_SHAREDLIB
block|,
DECL|enum constant|SOLR_SHARDHANDLERFACTORY_CLASS
name|SOLR_SHARDHANDLERFACTORY_CLASS
block|,
DECL|enum constant|SOLR_SHARDHANDLERFACTORY_CONNTIMEOUT
name|SOLR_SHARDHANDLERFACTORY_CONNTIMEOUT
block|,
DECL|enum constant|SOLR_SHARDHANDLERFACTORY_NAME
name|SOLR_SHARDHANDLERFACTORY_NAME
block|,
DECL|enum constant|SOLR_SHARDHANDLERFACTORY_SOCKETTIMEOUT
name|SOLR_SHARDHANDLERFACTORY_SOCKETTIMEOUT
block|,
DECL|enum constant|SOLR_SHARESCHEMA
name|SOLR_SHARESCHEMA
block|,
DECL|enum constant|SOLR_TRANSIENTCACHESIZE
name|SOLR_TRANSIENTCACHESIZE
block|,
DECL|enum constant|SOLR_ZKCLIENTTIMEOUT
name|SOLR_ZKCLIENTTIMEOUT
block|,
DECL|enum constant|SOLR_ZKHOST
name|SOLR_ZKHOST
block|,
comment|//TODO: Remove all of these elements for 5.0
DECL|enum constant|SOLR_PERSISTENT
name|SOLR_PERSISTENT
block|,
DECL|enum constant|SOLR_CORES_DEFAULT_CORE_NAME
name|SOLR_CORES_DEFAULT_CORE_NAME
block|,
DECL|enum constant|SOLR_ADMINPATH
name|SOLR_ADMINPATH
block|}
empty_stmt|;
DECL|field|CORE_PROP_FILE
specifier|public
specifier|final
specifier|static
name|String
name|CORE_PROP_FILE
init|=
literal|"core.properties"
decl_stmt|;
DECL|field|SOLR_XML_FILE
specifier|public
specifier|final
specifier|static
name|String
name|SOLR_XML_FILE
init|=
literal|"solr.xml"
decl_stmt|;
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|int
name|def
parameter_list|)
function_decl|;
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|boolean
name|defValue
parameter_list|)
function_decl|;
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|String
name|def
parameter_list|)
function_decl|;
DECL|method|getOrigProp
specifier|public
name|String
name|getOrigProp
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|String
name|def
parameter_list|)
function_decl|;
DECL|method|substituteProperties
specifier|public
name|void
name|substituteProperties
parameter_list|()
function_decl|;
DECL|method|initShardHandler
specifier|public
name|ShardHandlerFactory
name|initShardHandler
parameter_list|()
function_decl|;
DECL|method|getSolrProperties
specifier|public
name|Properties
name|getSolrProperties
parameter_list|(
name|ConfigSolr
name|cfg
parameter_list|,
name|String
name|context
parameter_list|)
function_decl|;
DECL|method|getSolrConfigFromZk
specifier|public
name|SolrConfig
name|getSolrConfigFromZk
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|String
name|zkConfigName
parameter_list|,
name|String
name|solrConfigFileName
parameter_list|,
name|SolrResourceLoader
name|resourceLoader
parameter_list|)
function_decl|;
DECL|method|initPersist
specifier|public
name|void
name|initPersist
parameter_list|()
function_decl|;
DECL|method|addPersistCore
specifier|public
name|void
name|addPersistCore
parameter_list|(
name|String
name|coreName
parameter_list|,
name|Properties
name|attribs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
function_decl|;
DECL|method|addPersistAllCores
specifier|public
name|void
name|addPersistAllCores
parameter_list|(
name|Properties
name|containerProperties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rootSolrAttribs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coresAttribs
parameter_list|,
name|File
name|file
parameter_list|)
function_decl|;
DECL|method|getCoreNameFromOrig
specifier|public
name|String
name|getCoreNameFromOrig
parameter_list|(
name|String
name|origCoreName
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|coreName
parameter_list|)
function_decl|;
DECL|method|getAllCoreNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
function_decl|;
DECL|method|getProperty
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|defaultVal
parameter_list|)
function_decl|;
DECL|method|readCoreProperties
specifier|public
name|Properties
name|readCoreProperties
parameter_list|(
name|String
name|coreName
parameter_list|)
function_decl|;
DECL|method|readCoreAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readCoreAttributes
parameter_list|(
name|String
name|coreName
parameter_list|)
function_decl|;
comment|// If the core is not to be loaded (say two cores defined with the same name or with the same data dir), return
comment|// the reason. If it's OK to load the core, return null.
DECL|method|getBadCoreMessage
specifier|public
name|String
name|getBadCoreMessage
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|is50OrLater
specifier|public
name|boolean
name|is50OrLater
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

