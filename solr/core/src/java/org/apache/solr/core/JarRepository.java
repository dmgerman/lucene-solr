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
name|SERVICE_UNAVAILABLE
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|BASE_URL_PROP
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|nio
operator|.
name|ByteBuffer
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Random
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
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
name|cloud
operator|.
name|ClusterState
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
name|DocCollection
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
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
name|admin
operator|.
name|CollectionsHandler
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
name|CryptoKeys
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
name|SimplePostTool
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
comment|/**  * The purpose of this class is to store the Jars loaded in memory and to keep only one copy of the Jar in a single node.  */
end_comment

begin_class
DECL|class|JarRepository
specifier|public
class|class
name|JarRepository
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JarRepository
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RANDOM
specifier|static
specifier|final
name|Random
name|RANDOM
decl_stmt|;
static|static
block|{
comment|// We try to make things reproducible in the context of our tests by initializing the random instance
comment|// based on the current seed
name|String
name|seed
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.seed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|seed
operator|==
literal|null
condition|)
block|{
name|RANDOM
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RANDOM
operator|=
operator|new
name|Random
argument_list|(
name|seed
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|jars
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|JarContent
argument_list|>
name|jars
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|JarRepository
specifier|public
name|JarRepository
parameter_list|(
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
comment|/**    * Returns the contents of a jar and increments a reference count. Please return the same object to decrease the refcount    *    * @param key it is a combination of blobname and version like blobName/version    * @return The reference of a jar    */
DECL|method|getJarIncRef
specifier|public
name|JarContentRef
name|getJarIncRef
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|JarContent
name|jar
init|=
name|jars
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|jar
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|Replica
name|replica
init|=
name|getSystemCollReplica
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
operator|+
literal|"/.system/blob/"
operator|+
name|key
operator|+
literal|"?wt=filestream"
decl_stmt|;
name|HttpClient
name|httpClient
init|=
name|coreContainer
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|HttpGet
name|httpGet
init|=
operator|new
name|HttpGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|ByteBuffer
name|b
decl_stmt|;
try|try
block|{
name|HttpResponse
name|entity
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|httpGet
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|entity
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusCode
operator|!=
literal|200
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
name|NOT_FOUND
argument_list|,
literal|"no such blob or version available: "
operator|+
name|key
argument_list|)
throw|;
block|}
name|b
operator|=
name|SimplePostTool
operator|.
name|inputStreamToByteArray
argument_list|(
name|entity
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
throw|throw
operator|(
name|SolrException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"could not load : "
operator|+
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|httpGet
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
name|jars
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|jar
operator|=
operator|new
name|JarContent
argument_list|(
name|key
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Jar loading is not supported in non-cloud mode"
argument_list|)
throw|;
comment|// todo
block|}
block|}
name|JarContentRef
name|ref
init|=
operator|new
name|JarContentRef
argument_list|(
name|jar
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|jar
operator|.
name|references
init|)
block|{
name|jar
operator|.
name|references
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
DECL|method|getSystemCollReplica
specifier|private
name|Replica
name|getSystemCollReplica
parameter_list|()
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|this
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|cs
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|coll
init|=
name|cs
operator|.
name|getCollectionOrNull
argument_list|(
name|CollectionsHandler
operator|.
name|SYSTEM_COLL
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
literal|".system collection not available"
argument_list|)
throw|;
name|ArrayList
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|coll
operator|.
name|getActiveSlices
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"No active slices for .system collection"
argument_list|)
throw|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|slices
argument_list|,
name|RANDOM
argument_list|)
expr_stmt|;
comment|//do load balancing
name|Replica
name|replica
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|replicas
argument_list|,
name|RANDOM
argument_list|)
expr_stmt|;
for|for
control|(
name|Replica
name|r
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|ZkStateReader
operator|.
name|ACTIVE
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|contains
argument_list|(
name|r
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|replica
operator|=
name|r
expr_stmt|;
break|break;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"replica {} says it is active but not a member of live nodes"
argument_list|,
name|r
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
literal|".no active replica available for .system collection"
argument_list|)
throw|;
block|}
return|return
name|replica
return|;
block|}
comment|/**    * This is to decrement a ref count    *    * @param ref The reference that is already there. Doing multiple calls with same ref will not matter    */
DECL|method|decrementJarRefCount
specifier|public
name|void
name|decrementJarRefCount
parameter_list|(
name|JarContentRef
name|ref
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
return|return;
synchronized|synchronized
init|(
name|ref
operator|.
name|jar
operator|.
name|references
init|)
block|{
if|if
condition|(
operator|!
name|ref
operator|.
name|jar
operator|.
name|references
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Multiple releases for the same reference"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ref
operator|.
name|jar
operator|.
name|references
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|jars
operator|.
name|remove
argument_list|(
name|ref
operator|.
name|jar
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|JarContent
specifier|public
specifier|static
class|class
name|JarContent
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
comment|// TODO move this off-heap
DECL|field|buffer
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
comment|// ref counting mechanism
DECL|field|references
specifier|private
specifier|final
name|Set
argument_list|<
name|JarContentRef
argument_list|>
name|references
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|JarContent
specifier|public
name|JarContent
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
DECL|method|getFileContent
specifier|public
name|ByteBuffer
name|getFileContent
parameter_list|(
name|String
name|entryName
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|zipContents
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|buffer
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|limit
argument_list|()
argument_list|)
decl_stmt|;
name|ZipInputStream
name|zis
init|=
operator|new
name|ZipInputStream
argument_list|(
name|zipContents
argument_list|)
decl_stmt|;
try|try
block|{
name|ZipEntry
name|entry
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zis
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entryName
operator|==
literal|null
operator|||
name|entryName
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|SimplePostTool
operator|.
name|BAOS
name|out
init|=
operator|new
name|SimplePostTool
operator|.
name|BAOS
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|int
name|size
decl_stmt|;
while|while
condition|(
operator|(
name|size
operator|=
name|zis
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|getByteBuffer
argument_list|()
return|;
block|}
block|}
block|}
finally|finally
block|{
name|zis
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|checkSignature
specifier|public
name|String
name|checkSignature
parameter_list|(
name|String
name|base64Sig
parameter_list|,
name|CryptoKeys
name|keys
parameter_list|)
block|{
return|return
name|keys
operator|.
name|verify
argument_list|(
name|base64Sig
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
DECL|class|JarContentRef
specifier|public
specifier|static
class|class
name|JarContentRef
block|{
DECL|field|jar
specifier|public
specifier|final
name|JarContent
name|jar
decl_stmt|;
DECL|method|JarContentRef
specifier|private
name|JarContentRef
parameter_list|(
name|JarContent
name|jar
parameter_list|)
block|{
name|this
operator|.
name|jar
operator|=
name|jar
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

