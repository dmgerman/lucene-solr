begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link DataSource} which reads from local files  *</p>  *<p>  * The file is read with the default platform encoding. It can be overriden by  * specifying the encoding in solrconfig.xml  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|FileDataSource
specifier|public
class|class
name|FileDataSource
extends|extends
name|DataSource
argument_list|<
name|Reader
argument_list|>
block|{
DECL|field|BASE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|BASE_PATH
init|=
literal|"basePath"
decl_stmt|;
comment|/**    * The basePath for this data source    */
DECL|field|basePath
specifier|protected
name|String
name|basePath
decl_stmt|;
comment|/**    * The encoding using which the given file should be read    */
DECL|field|encoding
specifier|protected
name|String
name|encoding
init|=
literal|null
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
name|FileDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|basePath
operator|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|BASE_PATH
argument_list|)
expr_stmt|;
if|if
condition|(
name|initProps
operator|.
name|get
argument_list|(
name|URLDataSource
operator|.
name|ENCODING
argument_list|)
operator|!=
literal|null
condition|)
name|encoding
operator|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|URLDataSource
operator|.
name|ENCODING
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Returns a reader for the given file.    *</p>    *<p>    * If the given file is not absolute, we try to construct an absolute path    * using basePath configuration. If that fails, then the relative path is    * tried. If file is not found a RuntimeException is thrown.    *</p>    *<p>    *<b>It is the responsibility of the calling method to properly close the    * returned Reader</b>    *</p>    */
annotation|@
name|Override
DECL|method|getData
specifier|public
name|Reader
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|basePath
argument_list|,
name|query
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|openStream
argument_list|(
name|f
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to open File : "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getFile
specifier|static
name|File
name|getFile
parameter_list|(
name|String
name|basePath
parameter_list|,
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|File
name|file0
init|=
operator|new
name|File
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|File
name|file
init|=
name|file0
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
name|file
operator|=
operator|new
name|File
argument_list|(
name|basePath
operator|+
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
operator|&&
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Accessing File: "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
elseif|else
if|if
condition|(
name|file
operator|!=
name|file0
condition|)
if|if
condition|(
name|file0
operator|.
name|isFile
argument_list|()
operator|&&
name|file0
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Accessing File0: "
operator|+
name|file0
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|file0
return|;
block|}
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Could not find file: "
operator|+
name|query
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Open a {@link java.io.Reader} for the given file name    *    * @param file a {@link java.io.File} instance    * @return a Reader on the given file    * @throws FileNotFoundException if the File does not exist    * @throws UnsupportedEncodingException if the encoding is unsupported    * @since solr 1.4    */
DECL|method|openStream
specifier|protected
name|Reader
name|openStream
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnsupportedEncodingException
block|{
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{    }
block|}
end_class

end_unit

