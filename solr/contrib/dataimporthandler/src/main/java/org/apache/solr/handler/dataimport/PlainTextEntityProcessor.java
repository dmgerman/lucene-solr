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
name|XPathEntityProcessor
operator|.
name|URL
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
name|IOException
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
name|io
operator|.
name|StringWriter
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
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>An implementation of {@link EntityProcessor} which reads data from a url/file and give out a row which contains one String  * value. The name of the field is 'plainText'.  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|PlainTextEntityProcessor
specifier|public
class|class
name|PlainTextEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
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
name|PlainTextEntityProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ended
specifier|private
name|boolean
name|ended
init|=
literal|false
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|ended
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|ended
condition|)
return|return
literal|null
return|;
name|DataSource
argument_list|<
name|Reader
argument_list|>
name|ds
init|=
name|context
operator|.
name|getDataSource
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|context
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|URL
argument_list|)
argument_list|)
decl_stmt|;
name|Reader
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|r
operator|=
name|ds
operator|.
name|getData
argument_list|(
name|url
argument_list|)
expr_stmt|;
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
literal|"Exception reading url : "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|len
init|=
literal|0
decl_stmt|;
try|try
block|{
name|len
operator|=
name|r
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Exception reading url : "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<=
literal|0
condition|)
break|break;
name|sw
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|row
operator|.
name|put
argument_list|(
name|PLAIN_TEXT
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ended
operator|=
literal|true
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|row
return|;
block|}
DECL|field|PLAIN_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|PLAIN_TEXT
init|=
literal|"plainText"
decl_stmt|;
block|}
end_class

end_unit

