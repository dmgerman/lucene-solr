begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.xslt
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|xslt
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|TimeOut
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Templates
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|XMLErrorLogger
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

begin_comment
comment|/** Singleton that creates a Transformer for the XSLTServletFilter.  *  For now, only caches the last created Transformer, but  *  could evolve to use an LRU cache of Transformers.  *    *  See http://www.javaworld.com/javaworld/jw-05-2003/jw-0502-xsl_p.html for  *  one possible way of improving caching.   */
end_comment

begin_class
DECL|class|TransformerProvider
specifier|public
class|class
name|TransformerProvider
block|{
DECL|field|lastFilename
specifier|private
name|String
name|lastFilename
decl_stmt|;
DECL|field|lastTemplates
specifier|private
name|Templates
name|lastTemplates
init|=
literal|null
decl_stmt|;
DECL|field|cacheExpiresTimeout
specifier|private
name|TimeOut
name|cacheExpiresTimeout
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
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|instance
specifier|public
specifier|static
name|TransformerProvider
name|instance
init|=
operator|new
name|TransformerProvider
argument_list|()
decl_stmt|;
comment|/** singleton */
DECL|method|TransformerProvider
specifier|private
name|TransformerProvider
parameter_list|()
block|{
comment|// tell'em: currently, we only cache the last used XSLT transform, and blindly recompile it
comment|// once cacheLifetimeSeconds expires
name|log
operator|.
name|warn
argument_list|(
literal|"The TransformerProvider's simplistic XSLT caching mechanism is not appropriate "
operator|+
literal|"for high load scenarios, unless a single XSLT transform is used"
operator|+
literal|" and xsltCacheLifetimeSeconds is set to a sufficiently high value."
argument_list|)
expr_stmt|;
block|}
comment|/** Return a new Transformer, possibly created from our cached Templates object      * @throws IOException If there is a low-level I/O error.    */
DECL|method|getTransformer
specifier|public
specifier|synchronized
name|Transformer
name|getTransformer
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|filename
parameter_list|,
name|int
name|cacheLifetimeSeconds
parameter_list|)
throws|throws
name|IOException
block|{
comment|// For now, the Templates are blindly reloaded once cacheExpires is over.
comment|// It'd be better to check the file modification time to reload only if needed.
if|if
condition|(
name|lastTemplates
operator|!=
literal|null
operator|&&
name|filename
operator|.
name|equals
argument_list|(
name|lastFilename
argument_list|)
operator|&&
name|cacheExpiresTimeout
operator|!=
literal|null
operator|&&
operator|!
name|cacheExpiresTimeout
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Using cached Templates:"
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|lastTemplates
operator|=
name|getTemplates
argument_list|(
name|solrConfig
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|filename
argument_list|,
name|cacheLifetimeSeconds
argument_list|)
expr_stmt|;
block|}
name|Transformer
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|lastTemplates
operator|.
name|newTransformer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerConfigurationException
name|tce
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"getTransformer"
argument_list|,
name|tce
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"newTransformer fails ( "
operator|+
name|lastFilename
operator|+
literal|")"
argument_list|,
name|tce
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/** Return a Templates object for the given filename */
DECL|method|getTemplates
specifier|private
name|Templates
name|getTemplates
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|filename
parameter_list|,
name|int
name|cacheLifetimeSeconds
parameter_list|)
throws|throws
name|IOException
block|{
name|Templates
name|result
init|=
literal|null
decl_stmt|;
name|lastFilename
operator|=
literal|null
expr_stmt|;
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"compiling XSLT templates:"
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|fn
init|=
literal|"xslt/"
operator|+
name|filename
decl_stmt|;
specifier|final
name|TransformerFactory
name|tFactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|tFactory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|SystemIdResolver
argument_list|(
name|loader
argument_list|)
operator|.
name|asURIResolver
argument_list|()
argument_list|)
expr_stmt|;
name|tFactory
operator|.
name|setErrorListener
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
specifier|final
name|StreamSource
name|src
init|=
operator|new
name|StreamSource
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|fn
argument_list|)
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|fn
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|result
operator|=
name|tFactory
operator|.
name|newTemplates
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// some XML parsers are broken and don't close the byte stream (but they should according to spec)
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|src
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"newTemplates"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to initialize Templates '"
operator|+
name|filename
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|lastFilename
operator|=
name|filename
expr_stmt|;
name|lastTemplates
operator|=
name|result
expr_stmt|;
name|cacheExpiresTimeout
operator|=
operator|new
name|TimeOut
argument_list|(
name|cacheLifetimeSeconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

