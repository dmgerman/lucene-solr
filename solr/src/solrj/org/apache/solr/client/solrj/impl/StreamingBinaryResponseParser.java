begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
name|impl
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
name|client
operator|.
name|solrj
operator|.
name|StreamingResponseCallback
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
name|SolrException
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
name|FastInputStream
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
name|List
import|;
end_import

begin_comment
comment|/**  * A BinaryResponseParser that sends callback events rather then build  * a large response   *   *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|StreamingBinaryResponseParser
specifier|public
class|class
name|StreamingBinaryResponseParser
extends|extends
name|BinaryResponseParser
block|{
DECL|field|callback
specifier|final
name|StreamingResponseCallback
name|callback
decl_stmt|;
DECL|method|StreamingBinaryResponseParser
specifier|public
name|StreamingBinaryResponseParser
parameter_list|(
name|StreamingResponseCallback
name|cb
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|cb
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
try|try
block|{
name|JavaBinCodec
name|codec
init|=
operator|new
name|JavaBinCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SolrDocument
name|readSolrDocument
parameter_list|(
name|FastInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrDocument
name|doc
init|=
name|super
operator|.
name|readSolrDocument
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|callback
operator|.
name|streamSolrDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrDocumentList
name|readSolrDocumentList
parameter_list|(
name|FastInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrDocumentList
name|solrDocs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|readVal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|solrDocs
operator|.
name|setNumFound
argument_list|(
operator|(
name|Long
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|solrDocs
operator|.
name|setStart
argument_list|(
operator|(
name|Long
operator|)
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|solrDocs
operator|.
name|setMaxScore
argument_list|(
operator|(
name|Float
operator|)
name|list
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|streamDocListInfo
argument_list|(
name|solrDocs
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|solrDocs
operator|.
name|getStart
argument_list|()
argument_list|,
name|solrDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
comment|// Read the Array
name|tagByte
operator|=
name|dis
operator|.
name|readByte
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|tagByte
operator|>>>
literal|5
operator|)
operator|!=
operator|(
name|ARR
operator|>>>
literal|5
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"doclist must have an array"
argument_list|)
throw|;
block|}
name|int
name|sz
init|=
name|readSize
argument_list|(
name|dis
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
comment|// must be a SolrDocument
name|readVal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
block|}
return|return
name|solrDocs
return|;
block|}
block|}
decl_stmt|;
return|return
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|codec
operator|.
name|unmarshal
argument_list|(
name|body
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"parsing error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

