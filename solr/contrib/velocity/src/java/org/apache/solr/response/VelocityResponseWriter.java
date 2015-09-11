begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|StandardCharsets
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ResourceBundle
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
name|lang
operator|.
name|StringUtils
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
name|SolrResponse
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
name|response
operator|.
name|QueryResponse
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
name|response
operator|.
name|SolrResponseBase
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
name|CommonParams
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|Template
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|VelocityContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|app
operator|.
name|VelocityEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|loader
operator|.
name|ClasspathResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|ConversionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|ComparisonDateTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|DisplayTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|EscapeTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|ListTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|MathTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|NumberTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|ResourceTool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|SortTool
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
DECL|class|VelocityResponseWriter
specifier|public
class|class
name|VelocityResponseWriter
implements|implements
name|QueryResponseWriter
implements|,
name|SolrCoreAware
block|{
comment|// init param names, these are _only_ loaded at init time (no per-request control of these)
comment|//   - multiple different named writers could be created with different init params
DECL|field|TEMPLATE_BASE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TEMPLATE_BASE_DIR
init|=
literal|"template.base.dir"
decl_stmt|;
DECL|field|PARAMS_RESOURCE_LOADER_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|PARAMS_RESOURCE_LOADER_ENABLED
init|=
literal|"params.resource.loader.enabled"
decl_stmt|;
DECL|field|SOLR_RESOURCE_LOADER_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_RESOURCE_LOADER_ENABLED
init|=
literal|"solr.resource.loader.enabled"
decl_stmt|;
DECL|field|PROPERTIES_FILE
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTIES_FILE
init|=
literal|"init.properties.file"
decl_stmt|;
comment|// request param names
DECL|field|TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|TEMPLATE
init|=
literal|"v.template"
decl_stmt|;
DECL|field|LAYOUT
specifier|public
specifier|static
specifier|final
name|String
name|LAYOUT
init|=
literal|"v.layout"
decl_stmt|;
DECL|field|LAYOUT_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|LAYOUT_ENABLED
init|=
literal|"v.layout.enabled"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"v.contentType"
decl_stmt|;
DECL|field|JSON
specifier|public
specifier|static
specifier|final
name|String
name|JSON
init|=
literal|"v.json"
decl_stmt|;
DECL|field|LOCALE
specifier|public
specifier|static
specifier|final
name|String
name|LOCALE
init|=
literal|"v.locale"
decl_stmt|;
DECL|field|TEMPLATE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TEMPLATE_EXTENSION
init|=
literal|".vm"
decl_stmt|;
DECL|field|DEFAULT_CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONTENT_TYPE
init|=
literal|"text/html;charset=UTF-8"
decl_stmt|;
DECL|field|JSON_CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|JSON_CONTENT_TYPE
init|=
literal|"application/json;charset=UTF-8"
decl_stmt|;
DECL|field|fileResourceLoaderBaseDir
specifier|private
name|File
name|fileResourceLoaderBaseDir
decl_stmt|;
DECL|field|paramsResourceLoaderEnabled
specifier|private
name|boolean
name|paramsResourceLoaderEnabled
decl_stmt|;
DECL|field|solrResourceLoaderEnabled
specifier|private
name|boolean
name|solrResourceLoaderEnabled
decl_stmt|;
DECL|field|initPropertiesFileName
specifier|private
name|String
name|initPropertiesFileName
decl_stmt|;
comment|// used just to hold from init() to inform()
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
name|VelocityResponseWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|velocityLogger
specifier|private
specifier|static
specifier|final
name|SolrVelocityLogger
name|velocityLogger
init|=
operator|new
name|SolrVelocityLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|velocityInitProps
specifier|private
name|Properties
name|velocityInitProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|customTools
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|customTools
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
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
name|fileResourceLoaderBaseDir
operator|=
literal|null
expr_stmt|;
name|String
name|templateBaseDir
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
name|TEMPLATE_BASE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|templateBaseDir
operator|!=
literal|null
operator|&&
operator|!
name|templateBaseDir
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fileResourceLoaderBaseDir
operator|=
operator|new
name|File
argument_list|(
name|templateBaseDir
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|fileResourceLoaderBaseDir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// "*not* exists" condition!
name|log
operator|.
name|warn
argument_list|(
name|TEMPLATE_BASE_DIR
operator|+
literal|" specified does not exist: "
operator|+
name|fileResourceLoaderBaseDir
argument_list|)
expr_stmt|;
name|fileResourceLoaderBaseDir
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|fileResourceLoaderBaseDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// "*not* a directory" condition
name|log
operator|.
name|warn
argument_list|(
name|TEMPLATE_BASE_DIR
operator|+
literal|" specified is not a directory: "
operator|+
name|fileResourceLoaderBaseDir
argument_list|)
expr_stmt|;
name|fileResourceLoaderBaseDir
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|// params resource loader: off by default
name|Boolean
name|prle
init|=
name|args
operator|.
name|getBooleanArg
argument_list|(
name|PARAMS_RESOURCE_LOADER_ENABLED
argument_list|)
decl_stmt|;
name|paramsResourceLoaderEnabled
operator|=
operator|(
literal|null
operator|==
name|prle
condition|?
literal|false
else|:
name|prle
operator|)
expr_stmt|;
comment|// solr resource loader: on by default
name|Boolean
name|srle
init|=
name|args
operator|.
name|getBooleanArg
argument_list|(
name|SOLR_RESOURCE_LOADER_ENABLED
argument_list|)
decl_stmt|;
name|solrResourceLoaderEnabled
operator|=
operator|(
literal|null
operator|==
name|srle
condition|?
literal|true
else|:
name|srle
operator|)
expr_stmt|;
name|initPropertiesFileName
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
name|PROPERTIES_FILE
argument_list|)
expr_stmt|;
name|NamedList
name|tools
init|=
operator|(
name|NamedList
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"tools"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tools
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|t
range|:
name|tools
control|)
block|{
name|Map
operator|.
name|Entry
name|tool
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|t
decl_stmt|;
name|customTools
operator|.
name|put
argument_list|(
name|tool
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|tool
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
comment|// need to leverage SolrResourceLoader, so load init.properties.file here instead of init()
if|if
condition|(
name|initPropertiesFileName
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|velocityInitProps
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|initPropertiesFileName
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
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
literal|"Error loading "
operator|+
name|PROPERTIES_FILE
operator|+
literal|" specified property file: "
operator|+
name|initPropertiesFileName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
name|String
name|contentType
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
comment|// Use the v.contentType specified, or either of the default content types depending on the presence of v.json
return|return
operator|(
name|contentType
operator|!=
literal|null
operator|)
condition|?
name|contentType
else|:
operator|(
operator|(
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|JSON
argument_list|)
operator|==
literal|null
operator|)
condition|?
name|DEFAULT_CONTENT_TYPE
else|:
name|JSON_CONTENT_TYPE
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|VelocityEngine
name|engine
init|=
name|createEngine
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// TODO: have HTTP headers available for configuring engine
name|Template
name|template
init|=
name|getTemplate
argument_list|(
name|engine
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|VelocityContext
name|context
init|=
name|createContext
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"engine"
argument_list|,
name|engine
argument_list|)
expr_stmt|;
comment|// for $engine.resourceExists(...)
name|String
name|layoutTemplate
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|LAYOUT
argument_list|)
decl_stmt|;
name|boolean
name|layoutEnabled
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|LAYOUT_ENABLED
argument_list|,
literal|true
argument_list|)
operator|&&
name|layoutTemplate
operator|!=
literal|null
decl_stmt|;
name|String
name|jsonWrapper
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|JSON
argument_list|)
decl_stmt|;
name|boolean
name|wrapResponse
init|=
name|layoutEnabled
operator|||
name|jsonWrapper
operator|!=
literal|null
decl_stmt|;
comment|// create output
if|if
condition|(
operator|!
name|wrapResponse
condition|)
block|{
comment|// straight-forward template/context merge to output
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// merge to a string buffer, then wrap with layout and finally as JSON
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|layoutEnabled
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"content"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stringWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
try|try
block|{
name|engine
operator|.
name|getTemplate
argument_list|(
name|layoutTemplate
operator|+
name|TEMPLATE_EXTENSION
argument_list|)
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|jsonWrapper
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|jsonWrapper
operator|+
literal|"("
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|getJSONWrap
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// using a layout, but not JSON wrapping
name|writer
operator|.
name|write
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createContext
specifier|private
name|VelocityContext
name|createContext
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
name|VelocityContext
name|context
init|=
operator|new
name|VelocityContext
argument_list|()
decl_stmt|;
comment|// Register useful Velocity "tools"
name|context
operator|.
name|put
argument_list|(
literal|"log"
argument_list|,
name|log
argument_list|)
expr_stmt|;
comment|// TODO: add test; TODO: should this be overridable with a custom "log" named tool?
name|context
operator|.
name|put
argument_list|(
literal|"esc"
argument_list|,
operator|new
name|EscapeTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
operator|new
name|ComparisonDateTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"list"
argument_list|,
operator|new
name|ListTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"math"
argument_list|,
operator|new
name|MathTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"number"
argument_list|,
operator|new
name|NumberTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
operator|new
name|SortTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"display"
argument_list|,
operator|new
name|DisplayTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"resource"
argument_list|,
operator|new
name|SolrVelocityResourceTool
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|LOCALE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|/*     // Custom tools, specified in config as:<queryResponseWriter name="velocityWithCustomTools" class="solr.VelocityResponseWriter"><lst name="tools"><str name="mytool">com.example.solr.velocity.MyTool</str></lst></queryResponseWriter>   */
comment|// Custom tools can override any of the built-in tools provided above, by registering one with the same name
for|for
control|(
name|String
name|name
range|:
name|customTools
operator|.
name|keySet
argument_list|()
control|)
block|{
name|context
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|SolrCore
operator|.
name|createInstance
argument_list|(
name|customTools
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|,
name|Object
operator|.
name|class
argument_list|,
literal|"VrW custom tool"
argument_list|,
name|request
operator|.
name|getCore
argument_list|()
argument_list|,
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// custom tools _cannot_ override context objects added below, like $request and $response
comment|// TODO: at least log a warning when one of the *fixed* tools classes in name with a custom one, currently silently ignored
comment|// Turn the SolrQueryResponse into a SolrResponse.
comment|// QueryResponse has lots of conveniences suitable for a view
comment|// Problem is, which SolrResponse class to use?
comment|// One patch to SOLR-620 solved this by passing in a class name as
comment|// as a parameter and using reflection and Solr's class loader to
comment|// create a new instance.  But for now the implementation simply
comment|// uses QueryResponse, and if it chokes in a known way, fall back
comment|// to bare bones SolrResponseBase.
comment|// Can this writer know what the handler class is?  With echoHandler=true it can get its string name at least
name|SolrResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|parsedResponse
init|=
name|BinaryResponseWriter
operator|.
name|getParsedResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
try|try
block|{
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
comment|// page only injected if QueryResponse works
name|context
operator|.
name|put
argument_list|(
literal|"page"
argument_list|,
operator|new
name|PageTool
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
comment|// page tool only makes sense for a SearchHandler request
name|context
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
operator|(
operator|(
name|QueryResponse
operator|)
name|rsp
operator|)
operator|.
name|getDebugMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
comment|// known edge case where QueryResponse's extraction assumes "response" is a SolrDocumentList
comment|// (AnalysisRequestHandler emits a "response")
name|rsp
operator|=
operator|new
name|SolrResponseBase
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|put
argument_list|(
literal|"request"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
DECL|method|createEngine
specifier|private
name|VelocityEngine
name|createEngine
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|VelocityEngine
name|engine
init|=
operator|new
name|VelocityEngine
argument_list|()
decl_stmt|;
comment|// route all Velocity logging through Solr's logging facility
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RUNTIME_LOG_LOGSYSTEM
argument_list|,
name|velocityLogger
argument_list|)
expr_stmt|;
comment|// Set some engine properties that improve the experience
comment|//   - these could be considered in the future for parameterization, but can also be overridden by using
comment|//     the init.properties.file setting.  (TODO: add a test for this properties set here overridden)
comment|// load the built-in _macros.vm first, then load VM_global_library.vm for legacy (pre-5.0) support,
comment|// and finally allow macros.vm to have the final say and override anything defined in the preceding files.
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|VM_LIBRARY
argument_list|,
literal|"_macros.vm,VM_global_library.vm,macros.vm"
argument_list|)
expr_stmt|;
comment|// Standard templates autoload, but not the macro one(s), by default, so let's just make life
comment|// easier, and consistent, for macro development too.
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|VM_LIBRARY_AUTORELOAD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|/*       Set up Velocity resource loader(s)        terminology note: "resource loader" is overloaded here, there is Solr's resource loader facility for plugins,        and there are Velocity template resource loaders.  It's confusing, they overlap: there is a Velocity resource        loader that loads templates from Solr's resource loader (SolrVelocityResourceLoader).        The Velocity resource loader order is [params,][file,][solr], intentionally ordered in this manner, and each       one optional and individually enable-able.  By default, only "solr" (resource loader) is used, parsing templates       from a velocity/ sub-tree in either the classpath or under conf/.        A common usage would be to enable the file template loader, keeping the solr loader enabled; the Velocity resource       loader path would then be "file,solr" (params is disabled by default).  The basic browse templates are built into       this plugin, but can be individually overridden by placing a same-named template in the template.base.dir specified       directory.      */
name|ArrayList
argument_list|<
name|String
argument_list|>
name|loaders
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|paramsResourceLoaderEnabled
condition|)
block|{
name|loaders
operator|.
name|add
argument_list|(
literal|"params"
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"params.resource.loader.instance"
argument_list|,
operator|new
name|SolrParamResourceLoader
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fileResourceLoaderBaseDir
operator|!=
literal|null
condition|)
block|{
name|loaders
operator|.
name|add
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|FILE_RESOURCE_LOADER_PATH
argument_list|,
name|fileResourceLoaderBaseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|solrResourceLoaderEnabled
condition|)
block|{
comment|// The solr resource loader serves templates under a velocity/ subtree from<lib>, conf/,
comment|// or SolrCloud's configuration tree.  Or rather the other way around, other resource loaders are rooted
comment|// from the top, whereas this is velocity/ sub-tree rooted.
name|loaders
operator|.
name|add
argument_list|(
literal|"solr"
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"solr.resource.loader.instance"
argument_list|,
operator|new
name|SolrVelocityResourceLoader
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Always have the built-in classpath loader.  This is needed when using VM_LIBRARY macros, as they are required
comment|// to be present if specified, and we want to have a nice macros facility built-in for users to use easily, and to
comment|// extend in custom ways.
name|loaders
operator|.
name|add
argument_list|(
literal|"builtin"
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"builtin.resource.loader.instance"
argument_list|,
operator|new
name|ClasspathResourceLoader
argument_list|()
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RESOURCE_LOADER
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|loaders
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|INPUT_ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// bring in any custom properties too
name|engine
operator|.
name|init
argument_list|(
name|velocityInitProps
argument_list|)
expr_stmt|;
return|return
name|engine
return|;
block|}
DECL|method|getTemplate
specifier|private
name|Template
name|getTemplate
parameter_list|(
name|VelocityEngine
name|engine
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Template
name|template
decl_stmt|;
name|String
name|templateName
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|TEMPLATE
argument_list|)
decl_stmt|;
name|String
name|qt
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|templateName
operator|==
literal|null
operator|&&
name|path
operator|!=
literal|null
condition|)
block|{
name|templateName
operator|=
name|path
expr_stmt|;
block|}
comment|// TODO: path is never null, so qt won't get picked up  maybe special case for '/select' to use qt, otherwise use path?
if|if
condition|(
name|templateName
operator|==
literal|null
operator|&&
name|qt
operator|!=
literal|null
condition|)
block|{
name|templateName
operator|=
name|qt
expr_stmt|;
block|}
if|if
condition|(
name|templateName
operator|==
literal|null
condition|)
name|templateName
operator|=
literal|"index"
expr_stmt|;
try|try
block|{
name|template
operator|=
name|engine
operator|.
name|getTemplate
argument_list|(
name|templateName
operator|+
name|TEMPLATE_EXTENSION
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|template
return|;
block|}
DECL|method|getJSONWrap
specifier|private
name|String
name|getJSONWrap
parameter_list|(
name|String
name|xmlResult
parameter_list|)
block|{
comment|// maybe noggit or Solr's JSON utilities can make this cleaner?
comment|// escape the double quotes and backslashes
name|String
name|replace1
init|=
name|xmlResult
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"\\\\\\\\"
argument_list|)
decl_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|"\\\\n"
argument_list|)
expr_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\r"
argument_list|,
literal|"\\\\r"
argument_list|)
expr_stmt|;
name|String
name|replaced
init|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\\\\""
argument_list|)
decl_stmt|;
comment|// wrap it in a JSON object
return|return
literal|"{\"result\":\""
operator|+
name|replaced
operator|+
literal|"\"}"
return|;
block|}
comment|// see: http://svn.apache.org/repos/asf/velocity/tools/branches/2.0.x/src/main/java/org/apache/velocity/tools/generic/ResourceTool.java
DECL|class|SolrVelocityResourceTool
specifier|private
class|class
name|SolrVelocityResourceTool
extends|extends
name|ResourceTool
block|{
DECL|field|solrClassLoader
specifier|private
name|ClassLoader
name|solrClassLoader
decl_stmt|;
DECL|method|SolrVelocityResourceTool
specifier|public
name|SolrVelocityResourceTool
parameter_list|(
name|ClassLoader
name|cl
parameter_list|,
name|String
name|localeString
parameter_list|)
block|{
name|this
operator|.
name|solrClassLoader
operator|=
name|cl
expr_stmt|;
name|Locale
name|l
init|=
name|toLocale
argument_list|(
name|localeString
argument_list|)
decl_stmt|;
name|this
operator|.
name|setLocale
argument_list|(
name|l
operator|==
literal|null
condition|?
name|Locale
operator|.
name|ROOT
else|:
name|l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBundle
specifier|protected
name|ResourceBundle
name|getBundle
parameter_list|(
name|String
name|baseName
parameter_list|,
name|Object
name|loc
parameter_list|)
block|{
comment|// resource bundles for this tool must be in velocity "package"
return|return
name|ResourceBundle
operator|.
name|getBundle
argument_list|(
literal|"velocity."
operator|+
name|baseName
argument_list|,
operator|(
name|loc
operator|==
literal|null
operator|)
condition|?
name|this
operator|.
name|getLocale
argument_list|()
else|:
name|this
operator|.
name|toLocale
argument_list|(
name|loc
argument_list|)
argument_list|,
name|solrClassLoader
argument_list|)
return|;
block|}
comment|// Why did Velocity Tools make this private?  Copied from ResourceTools.java
DECL|method|toLocale
specifier|private
name|Locale
name|toLocale
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|Locale
condition|)
block|{
return|return
operator|(
name|Locale
operator|)
name|obj
return|;
block|}
name|String
name|s
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
decl_stmt|;
return|return
name|ConversionUtils
operator|.
name|toLocale
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

