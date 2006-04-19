begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  * Provides a static refrence to a Config object modeling the main  * configuration data for a a Solr instance -- typically found in  * "solrconfig.xml".  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|SolrConfig
specifier|public
class|class
name|SolrConfig
block|{
DECL|field|DEFAULT_CONF_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONF_FILE
init|=
literal|"solrconfig.xml"
decl_stmt|;
comment|/**    * Singleton containing all configuration.    */
DECL|field|config
specifier|public
specifier|static
name|Config
name|config
decl_stmt|;
comment|/**    * (Re)loads the static configation information from the specified file.    *    *<p>    * This method is called implicitly on ClassLoad, but it may be    * called explicitly to change the Configuration used for the purpose    * of testing - in which case it should be called prior to initializing    * a SolrCore.    *</p>    *    *<p>    * This method should<b>only</b> be called for testing purposes.    * Because it modifies a singleton, it is not suitable for running    * multi-threaded tests.    *</p>    *    * @param file file name to load    * @see Config#openResource    */
DECL|method|initConfig
specifier|public
specifier|static
specifier|synchronized
name|void
name|initConfig
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|InputStream
name|is
init|=
name|Config
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|config
operator|=
operator|new
name|Config
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
literal|"/config/"
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|Config
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"Loaded SolrConfig: "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
static|static
block|{
try|try
block|{
name|initConfig
argument_list|(
name|DEFAULT_CONF_FILE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error in "
operator|+
name|DEFAULT_CONF_FILE
argument_list|,
name|ee
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

