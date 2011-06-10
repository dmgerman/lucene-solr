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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<p> A data source implementation which can be used to read character files using HTTP.</p><p/><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|URLDataSource
specifier|public
class|class
name|URLDataSource
extends|extends
name|DataSource
argument_list|<
name|Reader
argument_list|>
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|URLDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|encoding
specifier|private
name|String
name|encoding
decl_stmt|;
DECL|field|connectionTimeout
specifier|private
name|int
name|connectionTimeout
init|=
name|CONNECTION_TIMEOUT
decl_stmt|;
DECL|field|readTimeout
specifier|private
name|int
name|readTimeout
init|=
name|READ_TIMEOUT
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|initProps
specifier|private
name|Properties
name|initProps
decl_stmt|;
DECL|method|URLDataSource
specifier|public
name|URLDataSource
parameter_list|()
block|{   }
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
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|initProps
operator|=
name|initProps
expr_stmt|;
name|baseUrl
operator|=
name|getInitPropWithReplacements
argument_list|(
name|BASE_URL
argument_list|)
expr_stmt|;
if|if
condition|(
name|getInitPropWithReplacements
argument_list|(
name|ENCODING
argument_list|)
operator|!=
literal|null
condition|)
name|encoding
operator|=
name|getInitPropWithReplacements
argument_list|(
name|ENCODING
argument_list|)
expr_stmt|;
name|String
name|cTimeout
init|=
name|getInitPropWithReplacements
argument_list|(
name|CONNECTION_TIMEOUT_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|rTimeout
init|=
name|getInitPropWithReplacements
argument_list|(
name|READ_TIMEOUT_FIELD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|cTimeout
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connectionTimeout
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cTimeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid connection timeout: "
operator|+
name|cTimeout
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rTimeout
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|readTimeout
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|rTimeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid read timeout: "
operator|+
name|rTimeout
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|URL
name|url
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|URIMETHOD
operator|.
name|matcher
argument_list|(
name|query
argument_list|)
operator|.
name|find
argument_list|()
condition|)
name|url
operator|=
operator|new
name|URL
argument_list|(
name|query
argument_list|)
expr_stmt|;
else|else
name|url
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
operator|+
name|query
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Accessing URL: "
operator|+
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|URLConnection
name|conn
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
name|connectionTimeout
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
name|readTimeout
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|String
name|enc
init|=
name|encoding
decl_stmt|;
if|if
condition|(
name|enc
operator|==
literal|null
condition|)
block|{
name|String
name|cType
init|=
name|conn
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|cType
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|m
init|=
name|CHARSET_PATTERN
operator|.
name|matcher
argument_list|(
name|cType
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|enc
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|enc
operator|==
literal|null
condition|)
name|enc
operator|=
name|UTF_8
expr_stmt|;
name|DataImporter
operator|.
name|QUERY_COUNT
operator|.
name|get
argument_list|()
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|enc
argument_list|)
return|;
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
literal|"Exception thrown while getting data"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Exception in invoking url "
operator|+
name|url
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|baseUrl
return|;
block|}
DECL|method|getInitPropWithReplacements
specifier|private
name|String
name|getInitPropWithReplacements
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
specifier|final
name|String
name|expr
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|context
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
return|;
block|}
DECL|field|URIMETHOD
specifier|static
specifier|final
name|Pattern
name|URIMETHOD
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\w{3,}:/"
argument_list|)
decl_stmt|;
DECL|field|CHARSET_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|CHARSET_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?charset=(.*)$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|ENCODING
specifier|public
specifier|static
specifier|final
name|String
name|ENCODING
init|=
literal|"encoding"
decl_stmt|;
DECL|field|BASE_URL
specifier|public
specifier|static
specifier|final
name|String
name|BASE_URL
init|=
literal|"baseUrl"
decl_stmt|;
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|String
name|UTF_8
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|CONNECTION_TIMEOUT_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_TIMEOUT_FIELD_NAME
init|=
literal|"connectionTimeout"
decl_stmt|;
DECL|field|READ_TIMEOUT_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|READ_TIMEOUT_FIELD_NAME
init|=
literal|"readTimeout"
decl_stmt|;
DECL|field|CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|CONNECTION_TIMEOUT
init|=
literal|5000
decl_stmt|;
DECL|field|READ_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|READ_TIMEOUT
init|=
literal|10000
decl_stmt|;
block|}
end_class

end_unit

