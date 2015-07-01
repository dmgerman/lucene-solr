begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|LuceneTestCase
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
name|core
operator|.
name|ConfigOverlay
operator|.
name|isEditableProp
import|;
end_import

begin_class
DECL|class|TestConfigOverlay
specifier|public
class|class
name|TestConfigOverlay
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPaths
specifier|public
name|void
name|testPaths
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/autoCommit/maxDocs"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/autoCommit/maxTime"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/autoCommit/openSearcher"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/autoSoftCommit/maxDocs"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/autoSoftCommit/maxTime"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/commitWithin/softCommit"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler/indexWriter/closeWaitsForMerges"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.autoCommit.maxDocs"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.autoCommit.maxTime"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.autoCommit.openSearcher"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.autoSoftCommit.maxDocs"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.autoSoftCommit.maxTime"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.commitWithin.softCommit"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"updateHandler.indexWriter.closeWaitsForMerges"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.useFilterForSortedQuery"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.queryResultWindowSize"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.queryResultMaxDocsCached"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.enableLazyFieldLoading"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.boolTofilterOptimizer"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"jmx.agentId"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"jmx.serviceUrl"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"jmx.rootName"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"requestDispatcher.requestParsers.multipartUploadLimitInKB"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"requestDispatcher.requestParsers.formdataUploadLimitInKB"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"requestDispatcher.requestParsers.enableRemoteStreaming"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"requestDispatcher.requestParsers.addHttpRequestToContext"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"requestDispatcher.handleSelect"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.filterCache.initialSize"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isEditableProp
argument_list|(
literal|"query.filterCache"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isEditableProp
argument_list|(
literal|"query/filterCache/@initialSize"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isEditableProp
argument_list|(
literal|"query/filterCache/@initialSize1"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetProperty
specifier|public
name|void
name|testSetProperty
parameter_list|()
block|{
name|ConfigOverlay
name|overlay
init|=
operator|new
name|ConfigOverlay
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|overlay
operator|=
name|overlay
operator|.
name|setProperty
argument_list|(
literal|"query.filterCache.initialSize"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|overlay
operator|.
name|getXPathProperty
argument_list|(
literal|"query/filterCache/@initialSize"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|overlay
operator|.
name|getEditableSubProperties
argument_list|(
literal|"query/filterCache"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"initialSize"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

