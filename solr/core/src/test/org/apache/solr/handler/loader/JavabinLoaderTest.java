begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.loader
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|loader
package|;
end_package

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|JavaBinUpdateRequestCodec
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|SolrInputDocument
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
name|ContentStreamBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|BufferingRequestProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|JavabinLoaderTest
specifier|public
class|class
name|JavabinLoaderTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies the isLastDocInBatch flag gets set correctly for a batch of docs and for a request with a single doc.    */
DECL|method|testLastDocInBatchFlag
specifier|public
name|void
name|testLastDocInBatchFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestLastDocInBatchFlag
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// single doc
name|doTestLastDocInBatchFlag
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// multiple docs
block|}
DECL|method|doTestLastDocInBatchFlag
specifier|protected
name|void
name|doTestLastDocInBatchFlag
parameter_list|(
name|int
name|numDocsInBatch
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numDocsInBatch
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDocsInBatch
condition|;
name|d
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|updateRequest
operator|.
name|add
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateRequest
operator|.
name|add
argument_list|(
name|batch
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// client-side SolrJ would do this ...
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|(
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|)
operator|.
name|marshal
argument_list|(
name|updateRequest
argument_list|,
name|os
argument_list|)
expr_stmt|;
comment|// need to override the processAdd method b/c JavabinLoader calls
comment|// clear on the addCmd after it is passed on to the handler ... a simple clone will suffice for this test
name|BufferingRequestProcessor
name|mockUpdateProcessor
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|addCommands
operator|.
name|add
argument_list|(
operator|(
name|AddUpdateCommand
operator|)
name|cmd
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
operator|(
operator|new
name|JavabinLoader
argument_list|()
operator|)
operator|.
name|load
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|ByteArrayStream
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|mockUpdateProcessor
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|mockUpdateProcessor
operator|.
name|addCommands
operator|.
name|size
argument_list|()
operator|==
name|numDocsInBatch
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocsInBatch
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|assertFalse
argument_list|(
name|mockUpdateProcessor
operator|.
name|addCommands
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isLastDocInBatch
argument_list|)
expr_stmt|;
comment|// not last doc in batch
comment|// last doc should have the flag set
name|assertTrue
argument_list|(
name|mockUpdateProcessor
operator|.
name|addCommands
operator|.
name|get
argument_list|(
name|batch
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|isLastDocInBatch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

