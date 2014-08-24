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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NRTCachingDirectory
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
name|store
operator|.
name|blockcache
operator|.
name|BlockCache
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
name|store
operator|.
name|blockcache
operator|.
name|BlockDirectory
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
name|store
operator|.
name|blockcache
operator|.
name|BlockDirectoryCache
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
name|store
operator|.
name|blockcache
operator|.
name|BufferStore
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
name|store
operator|.
name|blockcache
operator|.
name|Cache
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
name|store
operator|.
name|blockcache
operator|.
name|Metrics
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
name|store
operator|.
name|hdfs
operator|.
name|HdfsDirectory
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
name|HdfsUtil
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
name|IOUtils
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

begin_class
DECL|class|HdfsDirectoryFactory
specifier|public
class|class
name|HdfsDirectoryFactory
extends|extends
name|CachingDirectoryFactory
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCKCACHE_SLAB_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_SLAB_COUNT
init|=
literal|"solr.hdfs.blockcache.slab.count"
decl_stmt|;
DECL|field|BLOCKCACHE_DIRECT_MEMORY_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_DIRECT_MEMORY_ALLOCATION
init|=
literal|"solr.hdfs.blockcache.direct.memory.allocation"
decl_stmt|;
DECL|field|BLOCKCACHE_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_ENABLED
init|=
literal|"solr.hdfs.blockcache.enabled"
decl_stmt|;
DECL|field|BLOCKCACHE_GLOBAL
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_GLOBAL
init|=
literal|"solr.hdfs.blockcache.global"
decl_stmt|;
DECL|field|BLOCKCACHE_READ_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_READ_ENABLED
init|=
literal|"solr.hdfs.blockcache.read.enabled"
decl_stmt|;
DECL|field|BLOCKCACHE_WRITE_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|BLOCKCACHE_WRITE_ENABLED
init|=
literal|"solr.hdfs.blockcache.write.enabled"
decl_stmt|;
DECL|field|NRTCACHINGDIRECTORY_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|NRTCACHINGDIRECTORY_ENABLE
init|=
literal|"solr.hdfs.nrtcachingdirectory.enable"
decl_stmt|;
DECL|field|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
specifier|public
specifier|static
specifier|final
name|String
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
init|=
literal|"solr.hdfs.nrtcachingdirectory.maxmergesizemb"
decl_stmt|;
DECL|field|NRTCACHINGDIRECTORY_MAXCACHEMB
specifier|public
specifier|static
specifier|final
name|String
name|NRTCACHINGDIRECTORY_MAXCACHEMB
init|=
literal|"solr.hdfs.nrtcachingdirectory.maxcachedmb"
decl_stmt|;
DECL|field|NUMBEROFBLOCKSPERBANK
specifier|public
specifier|static
specifier|final
name|String
name|NUMBEROFBLOCKSPERBANK
init|=
literal|"solr.hdfs.blockcache.blocksperbank"
decl_stmt|;
DECL|field|KERBEROS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_ENABLED
init|=
literal|"solr.hdfs.security.kerberos.enabled"
decl_stmt|;
DECL|field|KERBEROS_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_KEYTAB
init|=
literal|"solr.hdfs.security.kerberos.keytabfile"
decl_stmt|;
DECL|field|KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_PRINCIPAL
init|=
literal|"solr.hdfs.security.kerberos.principal"
decl_stmt|;
DECL|field|HDFS_HOME
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_HOME
init|=
literal|"solr.hdfs.home"
decl_stmt|;
DECL|field|CONFIG_DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_DIRECTORY
init|=
literal|"solr.hdfs.confdir"
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|hdfsDataDir
specifier|private
name|String
name|hdfsDataDir
decl_stmt|;
DECL|field|confDir
specifier|private
name|String
name|confDir
decl_stmt|;
DECL|field|globalBlockCache
specifier|private
specifier|static
name|BlockCache
name|globalBlockCache
decl_stmt|;
DECL|field|metrics
specifier|public
specifier|static
name|Metrics
name|metrics
decl_stmt|;
DECL|field|kerberosInit
specifier|private
specifier|static
name|Boolean
name|kerberosInit
decl_stmt|;
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
name|params
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|hdfsDataDir
operator|=
name|params
operator|.
name|get
argument_list|(
name|HDFS_HOME
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|hdfsDataDir
operator|!=
literal|null
operator|&&
name|this
operator|.
name|hdfsDataDir
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|hdfsDataDir
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|HDFS_HOME
operator|+
literal|"="
operator|+
name|this
operator|.
name|hdfsDataDir
argument_list|)
expr_stmt|;
block|}
name|boolean
name|kerberosEnabled
init|=
name|params
operator|.
name|getBool
argument_list|(
name|KERBEROS_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Solr Kerberos Authentication "
operator|+
operator|(
name|kerberosEnabled
condition|?
literal|"enabled"
else|:
literal|"disabled"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|kerberosEnabled
condition|)
block|{
name|initKerberos
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"creating directory factory for path {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|metrics
operator|==
literal|null
condition|)
block|{
name|metrics
operator|=
operator|new
name|Metrics
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|boolean
name|blockCacheEnabled
init|=
name|params
operator|.
name|getBool
argument_list|(
name|BLOCKCACHE_ENABLED
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|blockCacheGlobal
init|=
name|params
operator|.
name|getBool
argument_list|(
name|BLOCKCACHE_GLOBAL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// default to false for back compat
name|boolean
name|blockCacheReadEnabled
init|=
name|params
operator|.
name|getBool
argument_list|(
name|BLOCKCACHE_READ_ENABLED
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|blockCacheWriteEnabled
init|=
name|params
operator|.
name|getBool
argument_list|(
name|BLOCKCACHE_WRITE_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockCacheWriteEnabled
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Using "
operator|+
name|BLOCKCACHE_WRITE_ENABLED
operator|+
literal|" is currently buggy and can result in readers seeing a corrupted view of the index."
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|blockCacheEnabled
operator|&&
name|dirContext
operator|!=
name|DirContext
operator|.
name|META_DATA
condition|)
block|{
name|int
name|numberOfBlocksPerBank
init|=
name|params
operator|.
name|getInt
argument_list|(
name|NUMBEROFBLOCKSPERBANK
argument_list|,
literal|16384
argument_list|)
decl_stmt|;
name|int
name|blockSize
init|=
name|BlockDirectory
operator|.
name|BLOCK_SIZE
decl_stmt|;
name|int
name|bankCount
init|=
name|params
operator|.
name|getInt
argument_list|(
name|BLOCKCACHE_SLAB_COUNT
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|directAllocation
init|=
name|params
operator|.
name|getBool
argument_list|(
name|BLOCKCACHE_DIRECT_MEMORY_ALLOCATION
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|slabSize
init|=
name|numberOfBlocksPerBank
operator|*
name|blockSize
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of slabs of block cache [{}] with direct memory allocation set to [{}]"
argument_list|,
name|bankCount
argument_list|,
name|directAllocation
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Block cache target memory usage, slab size of [{}] will allocate [{}] slabs and use ~[{}] bytes"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|slabSize
block|,
name|bankCount
block|,
operator|(
operator|(
name|long
operator|)
name|bankCount
operator|*
operator|(
name|long
operator|)
name|slabSize
operator|)
block|}
argument_list|)
expr_stmt|;
name|int
name|bufferSize
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"solr.hdfs.blockcache.bufferstore.buffersize"
argument_list|,
literal|128
argument_list|)
decl_stmt|;
name|int
name|bufferCount
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"solr.hdfs.blockcache.bufferstore.buffercount"
argument_list|,
literal|128
operator|*
literal|128
argument_list|)
decl_stmt|;
name|BlockCache
name|blockCache
init|=
name|getBlockDirectoryCache
argument_list|(
name|numberOfBlocksPerBank
argument_list|,
name|blockSize
argument_list|,
name|bankCount
argument_list|,
name|directAllocation
argument_list|,
name|slabSize
argument_list|,
name|bufferSize
argument_list|,
name|bufferCount
argument_list|,
name|blockCacheGlobal
argument_list|)
decl_stmt|;
name|Cache
name|cache
init|=
operator|new
name|BlockDirectoryCache
argument_list|(
name|blockCache
argument_list|,
name|path
argument_list|,
name|metrics
argument_list|,
name|blockCacheGlobal
argument_list|)
decl_stmt|;
name|HdfsDirectory
name|hdfsDirectory
init|=
operator|new
name|HdfsDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|dir
operator|=
operator|new
name|BlockDirectory
argument_list|(
name|path
argument_list|,
name|hdfsDirectory
argument_list|,
name|cache
argument_list|,
literal|null
argument_list|,
name|blockCacheReadEnabled
argument_list|,
name|blockCacheWriteEnabled
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
operator|new
name|HdfsDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|boolean
name|nrtCachingDirectory
init|=
name|params
operator|.
name|getBool
argument_list|(
name|NRTCACHINGDIRECTORY_ENABLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|nrtCachingDirectory
condition|)
block|{
name|double
name|nrtCacheMaxMergeSizeMB
init|=
name|params
operator|.
name|getInt
argument_list|(
name|NRTCACHINGDIRECTORY_MAXMERGESIZEMB
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|double
name|nrtCacheMaxCacheMB
init|=
name|params
operator|.
name|getInt
argument_list|(
name|NRTCACHINGDIRECTORY_MAXCACHEMB
argument_list|,
literal|192
argument_list|)
decl_stmt|;
return|return
operator|new
name|NRTCachingDirectory
argument_list|(
name|dir
argument_list|,
name|nrtCacheMaxMergeSizeMB
argument_list|,
name|nrtCacheMaxCacheMB
argument_list|)
return|;
block|}
return|return
name|dir
return|;
block|}
DECL|method|getBlockDirectoryCache
specifier|private
name|BlockCache
name|getBlockDirectoryCache
parameter_list|(
name|int
name|numberOfBlocksPerBank
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|int
name|bankCount
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|int
name|slabSize
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|int
name|bufferCount
parameter_list|,
name|boolean
name|staticBlockCache
parameter_list|)
block|{
if|if
condition|(
operator|!
name|staticBlockCache
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new single instance HDFS BlockCache"
argument_list|)
expr_stmt|;
return|return
name|createBlockCache
argument_list|(
name|numberOfBlocksPerBank
argument_list|,
name|blockSize
argument_list|,
name|bankCount
argument_list|,
name|directAllocation
argument_list|,
name|slabSize
argument_list|,
name|bufferSize
argument_list|,
name|bufferCount
argument_list|)
return|;
block|}
synchronized|synchronized
init|(
name|HdfsDirectoryFactory
operator|.
name|class
init|)
block|{
if|if
condition|(
name|globalBlockCache
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new global HDFS BlockCache"
argument_list|)
expr_stmt|;
name|globalBlockCache
operator|=
name|createBlockCache
argument_list|(
name|numberOfBlocksPerBank
argument_list|,
name|blockSize
argument_list|,
name|bankCount
argument_list|,
name|directAllocation
argument_list|,
name|slabSize
argument_list|,
name|bufferSize
argument_list|,
name|bufferCount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|globalBlockCache
return|;
block|}
DECL|method|createBlockCache
specifier|private
name|BlockCache
name|createBlockCache
parameter_list|(
name|int
name|numberOfBlocksPerBank
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|int
name|bankCount
parameter_list|,
name|boolean
name|directAllocation
parameter_list|,
name|int
name|slabSize
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|int
name|bufferCount
parameter_list|)
block|{
name|BufferStore
operator|.
name|initNewBuffer
argument_list|(
name|bufferSize
argument_list|,
name|bufferCount
argument_list|)
expr_stmt|;
name|long
name|totalMemory
init|=
operator|(
name|long
operator|)
name|bankCount
operator|*
operator|(
name|long
operator|)
name|numberOfBlocksPerBank
operator|*
operator|(
name|long
operator|)
name|blockSize
decl_stmt|;
name|BlockCache
name|blockCache
decl_stmt|;
try|try
block|{
name|blockCache
operator|=
operator|new
name|BlockCache
argument_list|(
name|metrics
argument_list|,
name|directAllocation
argument_list|,
name|totalMemory
argument_list|,
name|slabSize
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The max direct memory is likely too low.  Either increase it (by adding -XX:MaxDirectMemorySize=<size>g -XX:+UseLargePages to your containers startup args)"
operator|+
literal|" or disable direct allocation using solr.hdfs.blockcache.direct.memory.allocation=false in solrconfig.xml. If you are putting the block cache on the heap,"
operator|+
literal|" your java heap size might not be large enough."
operator|+
literal|" Failed allocating ~"
operator|+
name|totalMemory
operator|/
literal|1000000.0
operator|+
literal|" MB."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|blockCache
return|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Path
name|hdfsDirPath
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fileSystem
operator|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|hdfsDirPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|fileSystem
operator|.
name|exists
argument_list|(
name|hdfsDirPath
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error checking if hdfs path exists"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error checking if hdfs path exists"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConf
specifier|private
name|Configuration
name|getConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|confDir
operator|=
name|params
operator|.
name|get
argument_list|(
name|CONFIG_DIRECTORY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|HdfsUtil
operator|.
name|addHdfsResources
argument_list|(
name|conf
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|removeDirectory
specifier|protected
specifier|synchronized
name|void
name|removeDirectory
parameter_list|(
name|CacheValue
name|cacheValue
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fileSystem
operator|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
name|cacheValue
operator|.
name|path
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|fileSystem
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|cacheValue
operator|.
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not remove directory"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not remove directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not remove directory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|startsWith
argument_list|(
literal|"hdfs:/"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isSharedStorage
specifier|public
name|boolean
name|isSharedStorage
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|searchersReserveCommitPoints
specifier|public
name|boolean
name|searchersReserveCommitPoints
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDataHome
specifier|public
name|String
name|getDataHome
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hdfsDataDir
operator|==
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
literal|"You must set the "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" param "
operator|+
name|HDFS_HOME
operator|+
literal|" for relative dataDir paths to work"
argument_list|)
throw|;
block|}
comment|// by default, we go off the instance directory
name|String
name|path
decl_stmt|;
if|if
condition|(
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
operator|+
literal|"/"
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|cd
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|normalize
argument_list|(
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|ZkController
operator|.
name|trimLeadingAndTrailingSlashes
argument_list|(
name|hdfsDataDir
argument_list|)
operator|+
literal|"/"
operator|+
name|path
operator|+
literal|"/"
operator|+
name|cd
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getConfDir
specifier|public
name|String
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
return|;
block|}
DECL|method|initKerberos
specifier|private
name|void
name|initKerberos
parameter_list|()
block|{
name|String
name|keytabFile
init|=
name|params
operator|.
name|get
argument_list|(
name|KERBEROS_KEYTAB
argument_list|,
literal|""
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|keytabFile
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|KERBEROS_KEYTAB
operator|+
literal|" required because "
operator|+
name|KERBEROS_ENABLED
operator|+
literal|" set to true"
argument_list|)
throw|;
block|}
name|String
name|principal
init|=
name|params
operator|.
name|get
argument_list|(
name|KERBEROS_PRINCIPAL
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|principal
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|KERBEROS_PRINCIPAL
operator|+
literal|" required because "
operator|+
name|KERBEROS_ENABLED
operator|+
literal|" set to true"
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|HdfsDirectoryFactory
operator|.
name|class
init|)
block|{
if|if
condition|(
name|kerberosInit
operator|==
literal|null
condition|)
block|{
name|kerberosInit
operator|=
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
specifier|final
name|String
name|authVal
init|=
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
decl_stmt|;
specifier|final
name|String
name|kerberos
init|=
literal|"kerberos"
decl_stmt|;
if|if
condition|(
name|authVal
operator|!=
literal|null
operator|&&
operator|!
name|authVal
operator|.
name|equals
argument_list|(
name|kerberos
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
operator|+
literal|" set to: "
operator|+
name|authVal
operator|+
literal|", not kerberos, but attempting to "
operator|+
literal|" connect to HDFS via kerberos"
argument_list|)
throw|;
block|}
comment|// let's avoid modifying the supplied configuration, just to be conservative
specifier|final
name|Configuration
name|ugiConf
init|=
operator|new
name|Configuration
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|ugiConf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
name|kerberos
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|ugiConf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to acquire kerberos ticket with keytab: {}, principal: {} "
argument_list|,
name|keytabFile
argument_list|,
name|principal
argument_list|)
expr_stmt|;
try|try
block|{
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principal
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Got Kerberos ticket"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

