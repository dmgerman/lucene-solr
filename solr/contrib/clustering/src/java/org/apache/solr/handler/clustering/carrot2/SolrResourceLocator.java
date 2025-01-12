begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package

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
name|io
operator|.
name|InputStream
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|carrot2
operator|.
name|util
operator|.
name|resource
operator|.
name|IResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|resource
operator|.
name|IResourceLocator
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
comment|/**  * A {@link IResourceLocator} that delegates resource searches to {@link SolrCore}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SolrResourceLocator
class|class
name|SolrResourceLocator
implements|implements
name|IResourceLocator
block|{
DECL|field|resourceLoader
specifier|private
specifier|final
name|SolrResourceLoader
name|resourceLoader
decl_stmt|;
DECL|field|carrot2ResourcesDir
specifier|private
specifier|final
name|String
name|carrot2ResourcesDir
decl_stmt|;
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
DECL|method|SolrResourceLocator
specifier|public
name|SolrResourceLocator
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|initParams
parameter_list|)
block|{
name|resourceLoader
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
expr_stmt|;
name|String
name|resourcesDir
init|=
name|initParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|RESOURCES_DIR
argument_list|)
decl_stmt|;
name|carrot2ResourcesDir
operator|=
name|firstNonNull
argument_list|(
name|resourcesDir
argument_list|,
name|CarrotClusteringEngine
operator|.
name|CARROT_RESOURCES_PREFIX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|firstNonNull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|firstNonNull
parameter_list|(
name|T
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
name|T
name|t
range|:
name|args
control|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
name|t
return|;
block|}
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"At least one element has to be non-null."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAll
specifier|public
name|IResource
index|[]
name|getAll
parameter_list|(
specifier|final
name|String
name|resource
parameter_list|)
block|{
specifier|final
name|String
name|resourceName
init|=
name|carrot2ResourcesDir
operator|+
literal|"/"
operator|+
name|resource
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Looking for Solr resource: "
operator|+
name|resourceName
argument_list|)
expr_stmt|;
name|InputStream
name|resourceStream
init|=
literal|null
decl_stmt|;
specifier|final
name|byte
index|[]
name|asBytes
decl_stmt|;
try|try
block|{
name|resourceStream
operator|=
name|resourceLoader
operator|.
name|openResource
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
name|asBytes
operator|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|resourceStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Resource not found in Solr's config: "
operator|+
name|resourceName
operator|+
literal|". Using the default "
operator|+
name|resource
operator|+
literal|" from Carrot JAR."
argument_list|)
expr_stmt|;
return|return
operator|new
name|IResource
index|[]
block|{}
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|resourceStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|resourceStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Loaded Solr resource: "
operator|+
name|resourceName
argument_list|)
expr_stmt|;
specifier|final
name|IResource
name|foundResource
init|=
operator|new
name|IResource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|open
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|asBytes
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// In case multiple resources are found they will be deduped, but we don't use it in Solr,
comment|// so simply rely on instance equivalence.
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// In case multiple resources are found they will be deduped, but we don't use it in Solr,
comment|// so simply rely on instance equivalence.
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Solr config resource: "
operator|+
name|resourceName
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|IResource
index|[]
block|{
name|foundResource
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// In case multiple locations are used locators will be deduped, but we don't use it in Solr,
comment|// so simply rely on instance equivalence.
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// In case multiple locations are used locators will be deduped, but we don't use it in Solr,
comment|// so simply rely on instance equivalence.
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|configDir
init|=
literal|""
decl_stmt|;
try|try
block|{
name|configDir
operator|=
literal|"configDir="
operator|+
operator|new
name|File
argument_list|(
name|resourceLoader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|", "
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
comment|// If we get the exception, the resource loader implementation
comment|// probably does not support getConfigDir(). Not a big problem.
block|}
return|return
literal|"SolrResourceLocator, "
operator|+
name|configDir
operator|+
literal|"Carrot2 relative lexicalResourcesDir="
operator|+
name|carrot2ResourcesDir
return|;
block|}
block|}
end_class

end_unit

