begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|solr
operator|.
name|core
operator|.
name|Config
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
DECL|field|tFactory
specifier|private
specifier|final
name|TransformerFactory
name|tFactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
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
DECL|field|cacheExpires
specifier|private
name|long
name|cacheExpires
init|=
literal|0
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
decl_stmt|;
comment|/** singleton */
DECL|method|TransformerProvider
specifier|private
name|TransformerProvider
parameter_list|()
block|{
name|log
operator|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TransformerProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// tell'em: currently, we only cache the last used XSLT transform, and blindly recompile it
comment|// once cacheLifetimeSeconds expires
name|log
operator|.
name|warning
argument_list|(
literal|"The TransformerProvider's simplistic XSLT caching mechanism is not appropriate "
operator|+
literal|"for high load scenarios, unless a single XSLT transform is used"
operator|+
literal|" and xsltCacheLifetimeSeconds is set to a sufficiently high value."
argument_list|)
expr_stmt|;
block|}
comment|/** Return a new Transformer, possibly created from our cached Templates object      * @throws TransformerConfigurationException     */
DECL|method|getTransformer
specifier|public
specifier|synchronized
name|Transformer
name|getTransformer
parameter_list|(
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
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|cacheExpires
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|log
operator|.
name|fine
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
name|throwing
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
specifier|final
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"newTransformer fails ( "
operator|+
name|lastFilename
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|tce
argument_list|)
expr_stmt|;
throw|throw
name|ioe
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
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"compiling XSLT templates:"
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InputStream
name|xsltStream
init|=
name|Config
operator|.
name|openResource
argument_list|(
literal|"xslt/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|result
operator|=
name|tFactory
operator|.
name|newTemplates
argument_list|(
operator|new
name|StreamSource
argument_list|(
name|xsltStream
argument_list|)
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
name|throwing
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
specifier|final
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"Unable to initialize Templates '"
operator|+
name|filename
operator|+
literal|"'"
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ioe
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
name|cacheExpires
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
name|cacheLifetimeSeconds
operator|*
literal|1000
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

