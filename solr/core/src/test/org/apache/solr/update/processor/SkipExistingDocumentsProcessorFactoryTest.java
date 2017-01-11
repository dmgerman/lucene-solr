begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|util
operator|.
name|BytesRef
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|SkipExistingDocumentsProcessorFactory
operator|.
name|SkipExistingDocumentsUpdateProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|SkipExistingDocumentsProcessorFactoryTest
specifier|public
class|class
name|SkipExistingDocumentsProcessorFactoryTest
block|{
DECL|field|docId
specifier|private
name|BytesRef
name|docId
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|defaultRequest
specifier|private
name|SolrQueryRequest
name|defaultRequest
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
operator|new
name|NamedList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Tests for logic in the factory
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testExceptionIfSkipInsertParamNonBoolean
specifier|public
name|void
name|testExceptionIfSkipInsertParamNonBoolean
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testExceptionIfSkipUpdateParamNonBoolean
specifier|public
name|void
name|testExceptionIfSkipUpdateParamNonBoolean
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testExceptionIfNextProcessorIsNull
specifier|public
name|void
name|testExceptionIfNextProcessorIsNull
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testExceptionIfNextProcessorNotDistributed
specifier|public
name|void
name|testExceptionIfNextProcessorNotDistributed
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoExceptionIfNextProcessorIsDistributed
specifier|public
name|void
name|testNoExceptionIfNextProcessorIsDistributed
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoExceptionIfNextNextProcessorIsDistributed
specifier|public
name|void
name|testNoExceptionIfNextNextProcessorIsDistributed
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|distProcessor
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|next
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
name|distProcessor
argument_list|)
decl_stmt|;
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipInsertsAndUpdatesDefaultToTrueIfNotConfigured
specifier|public
name|void
name|testSkipInsertsAndUpdatesDefaultToTrueIfNotConfigured
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipInsertIfExists to be true"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipUpdateIfMissing to be true"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipInsertsFalseIfInInitArgs
specifier|public
name|void
name|testSkipInsertsFalseIfInInitArgs
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipInsertIfExists to be false"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipUpdateIfMissing to be true"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipUpdatesFalseIfInInitArgs
specifier|public
name|void
name|testSkipUpdatesFalseIfInInitArgs
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipInsertIfExists to be true"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipUpdateIfMissing to be false"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipBothFalseIfInInitArgs
specifier|public
name|void
name|testSkipBothFalseIfInInitArgs
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|defaultRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipInsertIfExists to be false"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipUpdateIfMissing to be false"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipInsertsFalseIfInitArgsTrueButFalseStringInRequest
specifier|public
name|void
name|testSkipInsertsFalseIfInitArgsTrueButFalseStringInRequest
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|requestArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|requestArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
name|requestArgs
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipInsertIfExists to be false"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipUpdateIfMissing to be true"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipUpdatesFalseIfInitArgsTrueButFalseBooleanInRequest
specifier|public
name|void
name|testSkipUpdatesFalseIfInitArgsTrueButFalseBooleanInRequest
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|requestArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|requestArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
name|requestArgs
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipInsertIfExists to be true"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Expected skipUpdateIfMissing to be false"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipUpdatesTrueIfInitArgsFalseButTrueStringInRequest
specifier|public
name|void
name|testSkipUpdatesTrueIfInitArgsFalseButTrueStringInRequest
parameter_list|()
block|{
name|SkipExistingDocumentsProcessorFactory
name|factory
init|=
operator|new
name|SkipExistingDocumentsProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipInsertIfExists"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|requestArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|requestArgs
operator|.
name|add
argument_list|(
literal|"skipUpdateIfMissing"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
name|requestArgs
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipInsertIfExists to be true"
argument_list|,
name|processor
operator|.
name|isSkipInsertIfExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected skipUpdateIfMissing to be true"
argument_list|,
name|processor
operator|.
name|isSkipUpdateIfMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Tests for logic in the processor
annotation|@
name|Test
DECL|method|testSkippableInsertIsNotSkippedIfNotLeader
specifier|public
name|void
name|testSkippableInsertIsNotSkippedIfNotLeader
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createInsertUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkippableInsertIsNotSkippedIfSkipInsertsFalse
specifier|public
name|void
name|testSkippableInsertIsNotSkippedIfSkipInsertsFalse
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createInsertUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkippableInsertIsSkippedIfSkipInsertsTrue
specifier|public
name|void
name|testSkippableInsertIsSkippedIfSkipInsertsTrue
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createInsertUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonSkippableInsertIsNotSkippedIfSkipInsertsTrue
specifier|public
name|void
name|testNonSkippableInsertIsNotSkippedIfSkipInsertsTrue
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createInsertUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkippableUpdateIsNotSkippedIfNotLeader
specifier|public
name|void
name|testSkippableUpdateIsNotSkippedIfNotLeader
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createAtomicUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkippableUpdateIsNotSkippedIfSkipUpdatesFalse
specifier|public
name|void
name|testSkippableUpdateIsNotSkippedIfSkipUpdatesFalse
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createAtomicUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkippableUpdateIsSkippedIfSkipUpdatesTrue
specifier|public
name|void
name|testSkippableUpdateIsSkippedIfSkipUpdatesTrue
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createAtomicUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonSkippableUpdateIsNotSkippedIfSkipUpdatesTrue
specifier|public
name|void
name|testNonSkippableUpdateIsNotSkippedIfSkipUpdatesTrue
parameter_list|()
throws|throws
name|IOException
block|{
name|UpdateRequestProcessor
name|next
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SkipExistingDocumentsUpdateProcessor
name|processor
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|defaultRequest
argument_list|,
name|next
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
name|createAtomicUpdateCmd
argument_list|(
name|defaultRequest
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|doesDocumentExist
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|next
argument_list|)
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|createInsertUpdateCmd
specifier|private
name|AddUpdateCommand
name|createInsertUpdateCmd
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|setIndexedId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|AtomicUpdateDocumentMerger
operator|.
name|isAtomicUpdate
argument_list|(
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cmd
return|;
block|}
DECL|method|createAtomicUpdateCmd
specifier|private
name|AddUpdateCommand
name|createAtomicUpdateCmd
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|setIndexedId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"last_name"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"set"
argument_list|,
literal|"Smith"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AtomicUpdateDocumentMerger
operator|.
name|isAtomicUpdate
argument_list|(
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cmd
return|;
block|}
block|}
end_class

end_unit

