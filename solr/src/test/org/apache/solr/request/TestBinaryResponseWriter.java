begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

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
name|SolrDocument
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
name|SolrDocumentList
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
name|common
operator|.
name|util
operator|.
name|JavaBinCodec
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
name|BinaryQueryResponseWriter
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

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
name|ByteArrayOutputStream
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
name|UUID
import|;
end_import

begin_comment
comment|/**  * Test for BinaryResponseWriter  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestBinaryResponseWriter
specifier|public
class|class
name|TestBinaryResponseWriter
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema12.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
comment|/**    * Tests known types implementation by asserting correct encoding/decoding of UUIDField    */
DECL|method|testUUID
specifier|public
name|void
name|testUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
literal|"uuid"
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|req
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
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|BinaryQueryResponseWriter
name|writer
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
literal|"javabin"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|baos
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|res
init|=
operator|(
name|NamedList
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
operator|(
name|SolrDocumentList
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|doc
range|:
name|docs
control|)
block|{
name|SolrDocument
name|document
init|=
operator|(
name|SolrDocument
operator|)
name|doc
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned object must be a string"
argument_list|,
literal|"java.lang.String"
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"uuid"
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong UUID string returned"
argument_list|,
name|s
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"uuid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

