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
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineCompilationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineRuntimeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Configs
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

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|ConfigFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|ConfigRenderOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|ConfigUtil
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
name|impl
operator|.
name|CloudSolrServer
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
name|SolrZkClient
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
name|SystemIdResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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

begin_comment
comment|/**  * Set of configuration parameters that identify the location and schema of a Solr server or  * SolrCloud; Based on this information this class can return the schema and a corresponding  * {@link DocumentLoader}.  */
end_comment

begin_class
DECL|class|SolrLocator
specifier|public
class|class
name|SolrLocator
block|{
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
DECL|field|context
specifier|private
name|MorphlineContext
name|context
decl_stmt|;
DECL|field|collectionName
specifier|private
name|String
name|collectionName
decl_stmt|;
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
DECL|field|solrUrl
specifier|private
name|String
name|solrUrl
decl_stmt|;
DECL|field|solrHomeDir
specifier|private
name|String
name|solrHomeDir
decl_stmt|;
DECL|field|batchSize
specifier|private
name|int
name|batchSize
init|=
literal|1000
decl_stmt|;
DECL|field|SOLR_HOME_PROPERTY_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME_PROPERTY_NAME
init|=
literal|"solr.solr.home"
decl_stmt|;
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
name|SolrLocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SolrLocator
specifier|protected
name|SolrLocator
parameter_list|(
name|MorphlineContext
name|context
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|SolrLocator
specifier|public
name|SolrLocator
parameter_list|(
name|Config
name|config
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|Configs
name|configs
init|=
operator|new
name|Configs
argument_list|()
decl_stmt|;
name|collectionName
operator|=
name|configs
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"collection"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|zkHost
operator|=
name|configs
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"zkHost"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|solrHomeDir
operator|=
name|configs
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"solrHomeDir"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|solrUrl
operator|=
name|configs
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"solrUrl"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|batchSize
operator|=
name|configs
operator|.
name|getInt
argument_list|(
name|config
argument_list|,
literal|"batchSize"
argument_list|,
name|batchSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Constructed solrLocator: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|configs
operator|.
name|validateArguments
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|getLoader
specifier|public
name|DocumentLoader
name|getLoader
parameter_list|()
block|{
if|if
condition|(
name|context
operator|instanceof
name|SolrMorphlineContext
condition|)
block|{
name|DocumentLoader
name|loader
init|=
operator|(
operator|(
name|SolrMorphlineContext
operator|)
name|context
operator|)
operator|.
name|getDocumentLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
return|return
name|loader
return|;
block|}
block|}
if|if
condition|(
name|zkHost
operator|!=
literal|null
operator|&&
name|zkHost
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|collectionName
operator|==
literal|null
operator|||
name|collectionName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Parameter 'zkHost' requires that you also pass parameter 'collection'"
argument_list|,
name|config
argument_list|)
throw|;
block|}
name|CloudSolrServer
name|cloudSolrServer
init|=
operator|new
name|CloudSolrServer
argument_list|(
name|zkHost
argument_list|)
decl_stmt|;
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
operator|new
name|SolrServerDocumentLoader
argument_list|(
name|cloudSolrServer
argument_list|,
name|batchSize
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|solrUrl
operator|==
literal|null
operator|||
name|solrUrl
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Missing parameter 'solrUrl'"
argument_list|,
name|config
argument_list|)
throw|;
block|}
name|int
name|solrServerNumThreads
init|=
literal|2
decl_stmt|;
name|int
name|solrServerQueueLength
init|=
name|solrServerNumThreads
decl_stmt|;
name|SolrServer
name|server
init|=
operator|new
name|SafeConcurrentUpdateSolrServer
argument_list|(
name|solrUrl
argument_list|,
name|solrServerQueueLength
argument_list|,
name|solrServerNumThreads
argument_list|)
decl_stmt|;
comment|// SolrServer server = new HttpSolrServer(solrServerUrl);
comment|// SolrServer server = new ConcurrentUpdateSolrServer(solrServerUrl, solrServerQueueLength, solrServerNumThreads);
comment|// server.setParser(new XMLResponseParser()); // binary parser is used by default
return|return
operator|new
name|SolrServerDocumentLoader
argument_list|(
name|server
argument_list|,
name|batchSize
argument_list|)
return|;
block|}
block|}
DECL|method|getIndexSchema
specifier|public
name|IndexSchema
name|getIndexSchema
parameter_list|()
block|{
if|if
condition|(
name|context
operator|instanceof
name|SolrMorphlineContext
condition|)
block|{
name|IndexSchema
name|schema
init|=
operator|(
operator|(
name|SolrMorphlineContext
operator|)
name|context
operator|)
operator|.
name|getIndexSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|!=
literal|null
condition|)
block|{
name|validateSchema
argument_list|(
name|schema
argument_list|)
expr_stmt|;
return|return
name|schema
return|;
block|}
block|}
comment|// If solrHomeDir isn't defined and zkHost and collectionName are defined
comment|// then download schema.xml and solrconfig.xml, etc from zk and use that as solrHomeDir
name|String
name|oldSolrHomeDir
init|=
literal|null
decl_stmt|;
name|String
name|mySolrHomeDir
init|=
name|solrHomeDir
decl_stmt|;
if|if
condition|(
name|solrHomeDir
operator|==
literal|null
operator|||
name|solrHomeDir
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|zkHost
operator|==
literal|null
operator|||
name|zkHost
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// TODO: implement download from solrUrl if specified
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Downloading a Solr schema requires either parameter 'solrHomeDir' or parameters 'zkHost' and 'collection'"
argument_list|,
name|config
argument_list|)
throw|;
block|}
if|if
condition|(
name|collectionName
operator|==
literal|null
operator|||
name|collectionName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Parameter 'zkHost' requires that you also pass parameter 'collection'"
argument_list|,
name|config
argument_list|)
throw|;
block|}
name|ZooKeeperDownloader
name|zki
init|=
operator|new
name|ZooKeeperDownloader
argument_list|()
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|zki
operator|.
name|getZkClient
argument_list|(
name|zkHost
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|configName
init|=
name|zki
operator|.
name|readConfigName
argument_list|(
name|zkClient
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
name|File
name|downloadedSolrHomeDir
init|=
name|zki
operator|.
name|downloadConfigDir
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|)
decl_stmt|;
name|mySolrHomeDir
operator|=
name|downloadedSolrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Cannot download schema.xml from ZooKeeper"
argument_list|,
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Cannot download schema.xml from ZooKeeper"
argument_list|,
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Cannot download schema.xml from ZooKeeper"
argument_list|,
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|oldSolrHomeDir
operator|=
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROPERTY_NAME
argument_list|,
name|mySolrHomeDir
argument_list|)
expr_stmt|;
try|try
block|{
name|SolrConfig
name|solrConfig
init|=
operator|new
name|SolrConfig
argument_list|()
decl_stmt|;
comment|// TODO use SolrResourceLoader ala TikaMapper?
comment|// SolrConfig solrConfig = new SolrConfig("solrconfig.xml");
comment|// SolrConfig solrConfig = new
comment|// SolrConfig("/cloud/apache-solr-4.0.0-BETA/example/solr/collection1",
comment|// "solrconfig.xml", null);
comment|// SolrConfig solrConfig = new
comment|// SolrConfig("/cloud/apache-solr-4.0.0-BETA/example/solr/collection1/conf/solrconfig.xml");
name|SolrResourceLoader
name|loader
init|=
name|solrConfig
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|loader
operator|.
name|openSchema
argument_list|(
literal|"schema.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|is
operator|.
name|setSystemId
argument_list|(
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
operator|new
name|IndexSchema
argument_list|(
name|solrConfig
argument_list|,
literal|"schema.xml"
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|validateSchema
argument_list|(
name|schema
argument_list|)
expr_stmt|;
return|return
name|schema
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// restore old global state
if|if
condition|(
name|solrHomeDir
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|oldSolrHomeDir
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|SOLR_HOME_PROPERTY_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROPERTY_NAME
argument_list|,
name|oldSolrHomeDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|validateSchema
specifier|private
name|void
name|validateSchema
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Solr schema.xml is missing unique key field"
argument_list|,
name|config
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|isRequired
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Solr schema.xml must contain a required unique key field"
argument_list|,
name|config
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toConfig
argument_list|(
literal|null
argument_list|)
operator|.
name|root
argument_list|()
operator|.
name|render
argument_list|(
name|ConfigRenderOptions
operator|.
name|concise
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toConfig
specifier|public
name|Config
name|toConfig
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|json
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|json
operator|=
name|toJson
argument_list|(
name|key
argument_list|)
operator|+
literal|" : "
expr_stmt|;
block|}
name|json
operator|+=
literal|"{"
operator|+
literal|" collection : "
operator|+
name|toJson
argument_list|(
name|collectionName
argument_list|)
operator|+
literal|", "
operator|+
literal|" zkHost : "
operator|+
name|toJson
argument_list|(
name|zkHost
argument_list|)
operator|+
literal|", "
operator|+
literal|" solrUrl : "
operator|+
name|toJson
argument_list|(
name|solrUrl
argument_list|)
operator|+
literal|", "
operator|+
literal|" solrHomeDir : "
operator|+
name|toJson
argument_list|(
name|solrHomeDir
argument_list|)
operator|+
literal|", "
operator|+
literal|" batchSize : "
operator|+
name|toJson
argument_list|(
name|batchSize
argument_list|)
operator|+
literal|" "
operator|+
literal|"}"
expr_stmt|;
return|return
name|ConfigFactory
operator|.
name|parseString
argument_list|(
name|json
argument_list|)
return|;
block|}
DECL|method|toJson
specifier|private
name|String
name|toJson
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|String
name|str
init|=
name|key
operator|==
literal|null
condition|?
literal|""
else|:
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
name|str
operator|=
name|ConfigUtil
operator|.
name|quoteString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|str
return|;
block|}
DECL|method|getCollectionName
specifier|public
name|String
name|getCollectionName
parameter_list|()
block|{
return|return
name|this
operator|.
name|collectionName
return|;
block|}
DECL|method|setCollectionName
specifier|public
name|void
name|setCollectionName
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
name|this
operator|.
name|collectionName
operator|=
name|collectionName
expr_stmt|;
block|}
DECL|method|getZkHost
specifier|public
name|String
name|getZkHost
parameter_list|()
block|{
return|return
name|this
operator|.
name|zkHost
return|;
block|}
DECL|method|setZkHost
specifier|public
name|void
name|setZkHost
parameter_list|(
name|String
name|zkHost
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
block|}
DECL|method|getSolrHomeDir
specifier|public
name|String
name|getSolrHomeDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|solrHomeDir
return|;
block|}
DECL|method|setSolrHomeDir
specifier|public
name|void
name|setSolrHomeDir
parameter_list|(
name|String
name|solrHomeDir
parameter_list|)
block|{
name|this
operator|.
name|solrHomeDir
operator|=
name|solrHomeDir
expr_stmt|;
block|}
DECL|method|getServerUrl
specifier|public
name|String
name|getServerUrl
parameter_list|()
block|{
return|return
name|this
operator|.
name|solrUrl
return|;
block|}
DECL|method|setServerUrl
specifier|public
name|void
name|setServerUrl
parameter_list|(
name|String
name|solrUrl
parameter_list|)
block|{
name|this
operator|.
name|solrUrl
operator|=
name|solrUrl
expr_stmt|;
block|}
DECL|method|getBatchSize
specifier|public
name|int
name|getBatchSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|batchSize
return|;
block|}
DECL|method|setBatchSize
specifier|public
name|void
name|setBatchSize
parameter_list|(
name|int
name|batchSize
parameter_list|)
block|{
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
block|}
end_class

end_unit

