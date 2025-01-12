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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|UpdateRequestProcessorFactory
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
name|UpdateCommand
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
name|CommitUpdateCommand
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
name|DeleteUpdateCommand
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Tests various configurations of DocExpirationUpdateProcessorFactory  */
end_comment

begin_class
DECL|class|DocExpirationUpdateProcessorFactoryTest
specifier|public
class|class
name|DocExpirationUpdateProcessorFactoryTest
extends|extends
name|UpdateProcessorTestBase
block|{
DECL|field|CONFIG_XML
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_XML
init|=
literal|"solrconfig-doc-expire-update-processor.xml"
decl_stmt|;
DECL|field|SCHEMA_XML
specifier|public
specifier|static
specifier|final
name|String
name|SCHEMA_XML
init|=
literal|"schema15.xml"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
name|CONFIG_XML
argument_list|,
name|SCHEMA_XML
argument_list|)
expr_stmt|;
block|}
DECL|method|testTTLDefaultsConversion
specifier|public
name|void
name|testTTLDefaultsConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|d
init|=
literal|null
decl_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
literal|"convert-ttl-defaults"
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
literal|"convert-ttl-defaults"
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTTLFieldConversion
specifier|public
name|void
name|testTTLFieldConversion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|chain
init|=
literal|"convert-ttl-field"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
literal|null
decl_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"2222"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"+27MINUTES"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394061250042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"3333"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"+1YEAR"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1425595630042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"/DAY+1YEAR"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1425513600000L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
comment|// default ttlParamName is disabled, this should not convert...
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTTLParamConversion
specifier|public
name|void
name|testTTLParamConversion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|chain
init|=
literal|"convert-ttl-param"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
literal|null
decl_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_param_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_param_"
argument_list|,
literal|"+27MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"2222"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394061250042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
comment|// default ttlFieldName is disabled, param should be used...
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_param_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"+999MINUTES"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
comment|// default ttlFieldName is disabled, this should not convert...
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_"
argument_list|,
literal|"/DAY+1YEAR"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTTLFieldConversionWithDefaultParam
specifier|public
name|void
name|testTTLFieldConversionWithDefaultParam
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|chain
init|=
literal|"convert-ttl-field-with-param-default"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
literal|null
decl_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_param_"
argument_list|,
literal|"+999MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"_ttl_field_"
argument_list|,
literal|"+5MINUTES"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394059930042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|params
argument_list|(
literal|"NOW"
argument_list|,
literal|"1394059630042"
argument_list|,
literal|"_ttl_param_"
argument_list|,
literal|"+27MINUTES"
argument_list|)
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"2222"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Date
argument_list|(
literal|1394061250042L
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"_expire_at_tdt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAutomaticDeletes
specifier|public
name|void
name|testAutomaticDeletes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// get a handle on our recorder
name|UpdateRequestProcessorChain
name|chain
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"scheduled-delete"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|factories
init|=
name|chain
operator|.
name|getProcessors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"did number of processors configured in chain get changed?"
argument_list|,
literal|5
argument_list|,
name|factories
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected [1] RecordingUpdateProcessorFactory: "
operator|+
name|factories
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|,
name|factories
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|RecordingUpdateProcessorFactory
argument_list|)
expr_stmt|;
name|RecordingUpdateProcessorFactory
name|recorder
init|=
operator|(
name|RecordingUpdateProcessorFactory
operator|)
name|factories
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// now start recording, and monitor for the expected commands
try|try
block|{
name|recorder
operator|.
name|startRecording
argument_list|()
expr_stmt|;
comment|// more then one iter to verify it's recurring
specifier|final
name|int
name|numItersToCheck
init|=
literal|1
operator|+
name|RANDOM_MULTIPLIER
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
name|numItersToCheck
condition|;
name|i
operator|++
control|)
block|{
name|UpdateCommand
name|tmp
decl_stmt|;
comment|// be generous in how long we wait, some jenkins machines are slooooow
name|tmp
operator|=
name|recorder
operator|.
name|commandQueue
operator|.
name|poll
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// we can be confident in the order because DocExpirationUpdateProcessorFactory
comment|// uses the same request for both the delete& the commit -- and both
comment|// RecordingUpdateProcessorFactory's getInstance& startRecording methods are
comment|// synchronized.  So it should not be possible to start recording in the
comment|// middle of the two commands
name|assertTrue
argument_list|(
literal|"expected DeleteUpdateCommand: "
operator|+
name|tmp
operator|.
name|getClass
argument_list|()
argument_list|,
name|tmp
operator|instanceof
name|DeleteUpdateCommand
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delete
init|=
operator|(
name|DeleteUpdateCommand
operator|)
name|tmp
decl_stmt|;
name|assertFalse
argument_list|(
name|delete
operator|.
name|isDeleteById
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|delete
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delete
operator|.
name|getQuery
argument_list|()
argument_list|,
name|delete
operator|.
name|getQuery
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"{!cache=false}eXpField_tdt:[* TO "
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit should be immediately after the delete
name|tmp
operator|=
name|recorder
operator|.
name|commandQueue
operator|.
name|poll
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected CommitUpdateCommand: "
operator|+
name|tmp
operator|.
name|getClass
argument_list|()
argument_list|,
name|tmp
operator|instanceof
name|CommitUpdateCommand
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|commit
init|=
operator|(
name|CommitUpdateCommand
operator|)
name|tmp
decl_stmt|;
name|assertTrue
argument_list|(
name|commit
operator|.
name|softCommit
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|commit
operator|.
name|openSearcher
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|recorder
operator|.
name|stopRecording
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

