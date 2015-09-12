begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|BeanInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|IntrospectionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|Introspector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyDescriptor
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|PlatformManagedObject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|RuntimeMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|lucene
operator|.
name|LucenePackage
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
name|Constants
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
name|SimpleOrderedMap
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
name|handler
operator|.
name|RequestHandlerBase
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
name|IndexSchema
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
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  * This handler returns system info  *   * @since solr 1.2  */
end_comment

begin_class
DECL|class|SystemInfoHandler
specifier|public
class|class
name|SystemInfoHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SystemInfoHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// on some platforms, resolving canonical hostname can cause the thread
comment|// to block for several seconds if nameservices aren't available
comment|// so resolve this once per handler instance
comment|//(ie: not static, so core reload will refresh)
DECL|field|hostname
specifier|private
name|String
name|hostname
init|=
literal|null
decl_stmt|;
DECL|field|cc
specifier|private
name|CoreContainer
name|cc
decl_stmt|;
DECL|method|SystemInfoHandler
specifier|public
name|SystemInfoHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|SystemInfoHandler
specifier|public
name|SystemInfoHandler
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|cc
operator|=
name|cc
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
block|{
try|try
block|{
name|InetAddress
name|addr
init|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
decl_stmt|;
name|hostname
operator|=
name|addr
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|//default to null
block|}
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"core"
argument_list|,
name|getCoreInfo
argument_list|(
name|core
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|solrCloudMode
init|=
name|getCoreContainer
argument_list|(
name|req
argument_list|,
name|core
argument_list|)
operator|.
name|isZooKeeperAware
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"mode"
argument_list|,
name|solrCloudMode
condition|?
literal|"solrcloud"
else|:
literal|"std"
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrCloudMode
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"zkHost"
argument_list|,
name|getCoreContainer
argument_list|(
name|req
argument_list|,
name|core
argument_list|)
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkServerAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"solr_home"
argument_list|,
name|cc
operator|.
name|getSolrHome
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"lucene"
argument_list|,
name|getLuceneInfo
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"jvm"
argument_list|,
name|getJvmInfo
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"system"
argument_list|,
name|getSystemInfo
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreContainer
specifier|private
name|CoreContainer
name|getCoreContainer
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|CoreContainer
name|coreContainer
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|coreContainer
operator|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|coreContainer
operator|=
name|cc
expr_stmt|;
block|}
return|return
name|coreContainer
return|;
block|}
comment|/**    * Get system info    */
DECL|method|getCoreInfo
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getCoreInfo
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"schema"
argument_list|,
name|schema
operator|!=
literal|null
condition|?
name|schema
operator|.
name|getSchemaName
argument_list|()
else|:
literal|"no schema!"
argument_list|)
expr_stmt|;
comment|// Host
name|info
operator|.
name|add
argument_list|(
literal|"host"
argument_list|,
name|hostname
argument_list|)
expr_stmt|;
comment|// Now
name|info
operator|.
name|add
argument_list|(
literal|"now"
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
comment|// Start Time
name|info
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|core
operator|.
name|getStartTimeStamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// Solr Home
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|dirs
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"cwd"
argument_list|,
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"instance"
argument_list|,
operator|new
name|File
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|dirs
operator|.
name|add
argument_list|(
literal|"data"
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|normalize
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|)
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
name|warn
argument_list|(
literal|"Problem getting the normalized data directory path"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"data"
argument_list|,
literal|"N/A"
argument_list|)
expr_stmt|;
block|}
name|dirs
operator|.
name|add
argument_list|(
literal|"dirimpl"
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|dirs
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|normalize
argument_list|(
name|core
operator|.
name|getIndexDir
argument_list|()
argument_list|)
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
name|warn
argument_list|(
literal|"Problem getting the normalized index directory path"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
literal|"N/A"
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|add
argument_list|(
literal|"directory"
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
comment|/**    * Get system info    */
DECL|method|getSystemInfo
specifier|public
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getSystemInfo
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|OperatingSystemMXBean
name|os
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|os
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// add at least this one
try|try
block|{
comment|// add remaining ones dynamically using Java Beans API
name|addMXBeanProperties
argument_list|(
name|os
argument_list|,
name|OperatingSystemMXBean
operator|.
name|class
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IntrospectionException
decl||
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to fetch properties of OperatingSystemMXBean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// There are some additional beans we want to add (not available on all JVMs):
for|for
control|(
name|String
name|clazz
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"com.sun.management.OperatingSystemMXBean"
argument_list|,
literal|"com.sun.management.UnixOperatingSystemMXBean"
argument_list|,
literal|"com.ibm.lang.management.OperatingSystemMXBean"
argument_list|)
control|)
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|PlatformManagedObject
argument_list|>
name|intf
init|=
name|Class
operator|.
name|forName
argument_list|(
name|clazz
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|PlatformManagedObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMXBeanProperties
argument_list|(
name|os
argument_list|,
name|intf
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|IntrospectionException
decl||
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to fetch properties of JVM-specific OperatingSystemMXBean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Try some command line things:
try|try
block|{
if|if
condition|(
operator|!
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"uname"
argument_list|,
name|execute
argument_list|(
literal|"uname -a"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"uptime"
argument_list|,
name|execute
argument_list|(
literal|"uptime"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to execute command line tools to get operating system properties."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
comment|/**    * Add all bean properties of a {@link PlatformManagedObject} to the given {@link NamedList}.    *<p>    * If you are running a OpenJDK/Oracle JVM, there are nice properties in:    * {@code com.sun.management.UnixOperatingSystemMXBean} and    * {@code com.sun.management.OperatingSystemMXBean}    */
DECL|method|addMXBeanProperties
specifier|static
parameter_list|<
name|T
extends|extends
name|PlatformManagedObject
parameter_list|>
name|void
name|addMXBeanProperties
parameter_list|(
name|T
name|obj
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|intf
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
throws|throws
name|IntrospectionException
throws|,
name|ReflectiveOperationException
block|{
if|if
condition|(
name|intf
operator|.
name|isInstance
argument_list|(
name|obj
argument_list|)
condition|)
block|{
specifier|final
name|BeanInfo
name|beanInfo
init|=
name|Introspector
operator|.
name|getBeanInfo
argument_list|(
name|intf
argument_list|,
name|intf
operator|.
name|getSuperclass
argument_list|()
argument_list|,
name|Introspector
operator|.
name|IGNORE_ALL_BEANINFO
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|PropertyDescriptor
name|desc
range|:
name|beanInfo
operator|.
name|getPropertyDescriptors
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|desc
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|v
init|=
name|desc
operator|.
name|getReadMethod
argument_list|()
operator|.
name|invoke
argument_list|(
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|info
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Utility function to execute a function    */
DECL|method|execute
specifier|private
specifier|static
name|String
name|execute
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|Process
name|process
init|=
literal|null
decl_stmt|;
try|try
block|{
name|process
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|in
operator|=
name|process
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
comment|// use default charset from locale here, because the command invoked also uses the default locale:
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// ignore - log.warn("Error executing command", ex);
return|return
literal|"(error executing: "
operator|+
name|cmd
operator|+
literal|")"
return|;
block|}
catch|catch
parameter_list|(
name|Error
name|err
parameter_list|)
block|{
if|if
condition|(
name|err
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"posix_spawn"
argument_list|)
operator|||
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"UNIXProcess"
argument_list|)
operator|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error forking command due to JVM locale bug (see https://issues.apache.org/jira/browse/SOLR-6387): "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|"(error executing: "
operator|+
name|cmd
operator|+
literal|")"
return|;
block|}
throw|throw
name|err
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|process
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|process
operator|.
name|getErrorStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get JVM Info - including memory info    */
DECL|method|getJvmInfo
specifier|public
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getJvmInfo
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|jvm
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
name|javaVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.specification.version"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|javaVendor
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.specification.vendor"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|javaName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.specification.name"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jreVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jreVendor
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|vmVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.version"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|vmVendor
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.vendor"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|vmName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.name"
argument_list|,
literal|"unknown"
argument_list|)
decl_stmt|;
comment|// Summary Info
name|jvm
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|jreVersion
operator|+
literal|" "
operator|+
name|vmVersion
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|jreVendor
operator|+
literal|" "
operator|+
name|vmName
argument_list|)
expr_stmt|;
comment|// details
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|java
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|java
operator|.
name|add
argument_list|(
literal|"vendor"
argument_list|,
name|javaVendor
argument_list|)
expr_stmt|;
name|java
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|javaName
argument_list|)
expr_stmt|;
name|java
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|javaVersion
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"spec"
argument_list|,
name|java
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|jre
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|jre
operator|.
name|add
argument_list|(
literal|"vendor"
argument_list|,
name|jreVendor
argument_list|)
expr_stmt|;
name|jre
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|jreVersion
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"jre"
argument_list|,
name|jre
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|vm
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|vm
operator|.
name|add
argument_list|(
literal|"vendor"
argument_list|,
name|vmVendor
argument_list|)
expr_stmt|;
name|vm
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|vmName
argument_list|)
expr_stmt|;
name|vm
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|vmVersion
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"vm"
argument_list|,
name|vm
argument_list|)
expr_stmt|;
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"processors"
argument_list|,
name|runtime
operator|.
name|availableProcessors
argument_list|()
argument_list|)
expr_stmt|;
comment|// not thread safe, but could be thread local
name|DecimalFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"#.#"
argument_list|,
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|mem
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|raw
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|free
init|=
name|runtime
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|long
name|max
init|=
name|runtime
operator|.
name|maxMemory
argument_list|()
decl_stmt|;
name|long
name|total
init|=
name|runtime
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
name|long
name|used
init|=
name|total
operator|-
name|free
decl_stmt|;
name|double
name|percentUsed
init|=
operator|(
call|(
name|double
call|)
argument_list|(
name|used
argument_list|)
operator|/
operator|(
name|double
operator|)
name|max
operator|)
operator|*
literal|100
decl_stmt|;
name|raw
operator|.
name|add
argument_list|(
literal|"free"
argument_list|,
name|free
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"free"
argument_list|,
name|humanReadableUnits
argument_list|(
name|free
argument_list|,
name|df
argument_list|)
argument_list|)
expr_stmt|;
name|raw
operator|.
name|add
argument_list|(
literal|"total"
argument_list|,
name|total
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"total"
argument_list|,
name|humanReadableUnits
argument_list|(
name|total
argument_list|,
name|df
argument_list|)
argument_list|)
expr_stmt|;
name|raw
operator|.
name|add
argument_list|(
literal|"max"
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"max"
argument_list|,
name|humanReadableUnits
argument_list|(
name|max
argument_list|,
name|df
argument_list|)
argument_list|)
expr_stmt|;
name|raw
operator|.
name|add
argument_list|(
literal|"used"
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"used"
argument_list|,
name|humanReadableUnits
argument_list|(
name|used
argument_list|,
name|df
argument_list|)
operator|+
literal|" (%"
operator|+
name|df
operator|.
name|format
argument_list|(
name|percentUsed
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|raw
operator|.
name|add
argument_list|(
literal|"used%"
argument_list|,
name|percentUsed
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
literal|"raw"
argument_list|,
name|raw
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|add
argument_list|(
literal|"memory"
argument_list|,
name|mem
argument_list|)
expr_stmt|;
comment|// JMX properties -- probably should be moved to a different handler
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|jmx
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|RuntimeMXBean
name|mx
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
decl_stmt|;
if|if
condition|(
name|mx
operator|.
name|isBootClassPathSupported
argument_list|()
condition|)
block|{
name|jmx
operator|.
name|add
argument_list|(
literal|"bootclasspath"
argument_list|,
name|mx
operator|.
name|getBootClassPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|jmx
operator|.
name|add
argument_list|(
literal|"classpath"
argument_list|,
name|mx
operator|.
name|getClassPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// the input arguments passed to the Java virtual machine
comment|// which does not include the arguments to the main method.
name|jmx
operator|.
name|add
argument_list|(
literal|"commandLineArgs"
argument_list|,
name|mx
operator|.
name|getInputArguments
argument_list|()
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|(
name|mx
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|add
argument_list|(
literal|"upTimeMS"
argument_list|,
name|mx
operator|.
name|getUptime
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
name|log
operator|.
name|warn
argument_list|(
literal|"Error getting JMX properties"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|jvm
operator|.
name|add
argument_list|(
literal|"jmx"
argument_list|,
name|jmx
argument_list|)
expr_stmt|;
return|return
name|jvm
return|;
block|}
DECL|method|getLuceneInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getLuceneInfo
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Package
name|p
init|=
name|SolrCore
operator|.
name|class
operator|.
name|getPackage
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"solr-spec-version"
argument_list|,
name|p
operator|.
name|getSpecificationVersion
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"solr-impl-version"
argument_list|,
name|p
operator|.
name|getImplementationVersion
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|=
name|LucenePackage
operator|.
name|class
operator|.
name|getPackage
argument_list|()
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"lucene-spec-version"
argument_list|,
name|p
operator|.
name|getSpecificationVersion
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"lucene-impl-version"
argument_list|,
name|p
operator|.
name|getImplementationVersion
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Get System Info"
return|;
block|}
DECL|field|ONE_KB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_KB
init|=
literal|1024
decl_stmt|;
DECL|field|ONE_MB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_MB
init|=
name|ONE_KB
operator|*
name|ONE_KB
decl_stmt|;
DECL|field|ONE_GB
specifier|private
specifier|static
specifier|final
name|long
name|ONE_GB
init|=
name|ONE_KB
operator|*
name|ONE_MB
decl_stmt|;
comment|/**    * Return good default units based on byte size.    */
DECL|method|humanReadableUnits
specifier|private
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|,
name|DecimalFormat
name|df
parameter_list|)
block|{
name|String
name|newSizeAndUnits
decl_stmt|;
if|if
condition|(
name|bytes
operator|/
name|ONE_GB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_GB
argument_list|)
argument_list|)
operator|+
literal|" GB"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_MB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_MB
argument_list|)
argument_list|)
operator|+
literal|" MB"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_KB
operator|>
literal|0
condition|)
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_KB
argument_list|)
argument_list|)
operator|+
literal|" KB"
expr_stmt|;
block|}
else|else
block|{
name|newSizeAndUnits
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|bytes
argument_list|)
operator|+
literal|" bytes"
expr_stmt|;
block|}
return|return
name|newSizeAndUnits
return|;
block|}
block|}
end_class

end_unit

