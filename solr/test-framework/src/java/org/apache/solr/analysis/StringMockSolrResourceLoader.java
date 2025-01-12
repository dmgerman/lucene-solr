begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_class
DECL|class|StringMockSolrResourceLoader
class|class
name|StringMockSolrResourceLoader
implements|implements
name|ResourceLoader
block|{
DECL|field|text
name|String
name|text
decl_stmt|;
DECL|method|StringMockSolrResourceLoader
name|StringMockSolrResourceLoader
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findClass
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|findClass
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|cname
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot load class: "
operator|+
name|cname
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
init|=
name|findClass
argument_list|(
name|cname
argument_list|,
name|expectedType
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot create instance: "
operator|+
name|cname
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

