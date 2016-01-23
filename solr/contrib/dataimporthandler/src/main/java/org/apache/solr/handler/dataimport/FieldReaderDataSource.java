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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
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

begin_comment
comment|/**  * This can be useful for users who have a DB field containing xml and wish to use a nested XPathEntityProcessor  *<p/>  * The datasouce may be configured as follows  *<p/>  *<datasource name="f1" type="FieldReaderDataSource" />  *<p/>  * The enity which uses this datasource must keep the url value as the variable name url="field-name"  *<p/>  * The fieldname must be resolvable from VariableResolver  *<p/>  * This may be used with any EntityProcessor which uses a DataSource<Reader> eg:XPathEntityProcessor  *<p/>  * Supports String, BLOB, CLOB data types and there is an extra field (in the entity) 'encoding' for BLOB types  *  * @version $Id$  * @since 1.4  */
end_comment

begin_class
DECL|class|FieldReaderDataSource
specifier|public
class|class
name|FieldReaderDataSource
extends|extends
name|DataSource
argument_list|<
name|Reader
argument_list|>
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
name|FieldReaderDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|vr
specifier|protected
name|VariableResolver
name|vr
decl_stmt|;
DECL|field|dataField
specifier|protected
name|String
name|dataField
decl_stmt|;
DECL|field|encoding
specifier|private
name|String
name|encoding
decl_stmt|;
DECL|field|entityProcessor
specifier|private
name|EntityProcessorWrapper
name|entityProcessor
decl_stmt|;
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
name|dataField
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"dataField"
argument_list|)
expr_stmt|;
name|encoding
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"encoding"
argument_list|)
expr_stmt|;
name|entityProcessor
operator|=
operator|(
name|EntityProcessorWrapper
operator|)
name|context
operator|.
name|getEntityProcessor
argument_list|()
expr_stmt|;
comment|/*no op*/
block|}
DECL|method|getData
specifier|public
name|Reader
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|Object
name|o
init|=
name|entityProcessor
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|resolve
argument_list|(
name|dataField
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"No field available for name : "
operator|+
name|dataField
argument_list|)
throw|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Clob
condition|)
block|{
name|Clob
name|clob
init|=
operator|(
name|Clob
operator|)
name|o
decl_stmt|;
try|try
block|{
comment|//Most of the JDBC drivers have getCharacterStream defined as public
comment|// so let us just check it
return|return
name|readCharStream
argument_list|(
name|clob
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
name|info
argument_list|(
literal|"Unable to get data from CLOB"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Blob
condition|)
block|{
name|Blob
name|blob
init|=
operator|(
name|Blob
operator|)
name|o
decl_stmt|;
try|try
block|{
comment|//Most of the JDBC drivers have getBinaryStream defined as public
comment|// so let us just check it
name|Method
name|m
init|=
name|blob
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getBinaryStream"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|getReader
argument_list|(
name|m
argument_list|,
name|blob
argument_list|)
return|;
block|}
else|else
block|{
comment|// force invoke
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|getReader
argument_list|(
name|m
argument_list|,
name|blob
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to get data from BLOB"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|readCharStream
specifier|static
name|Reader
name|readCharStream
parameter_list|(
name|Clob
name|clob
parameter_list|)
block|{
try|try
block|{
name|Method
name|m
init|=
name|clob
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getCharacterStream"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|Reader
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|clob
argument_list|)
return|;
block|}
else|else
block|{
comment|// force invoke
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Reader
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|clob
argument_list|)
return|;
block|}
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
literal|"Unable to get reader from clob"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//unreachable
block|}
block|}
DECL|method|getReader
specifier|private
name|Reader
name|getReader
parameter_list|(
name|Method
name|m
parameter_list|,
name|Blob
name|blob
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvocationTargetException
throws|,
name|UnsupportedEncodingException
block|{
name|InputStream
name|is
init|=
operator|(
name|InputStream
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|blob
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
return|return
operator|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
operator|)
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{    }
block|}
end_class

end_unit

