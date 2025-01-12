begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|MapSolrParams
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|UpdateRequestHandler
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
name|SolrQueryRequestBase
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
annotation|@
name|Slow
DECL|class|HardAutoCommitTest
specifier|public
class|class
name|HardAutoCommitTest
extends|extends
name|AbstractSolrTestCase
block|{
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.commitwithin.softcommit"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.commitwithin.softcommit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
comment|// reload the core to clear stats
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommitWithin
specifier|public
name|void
name|testCommitWithin
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NewSearcherListener
name|trigger
init|=
operator|new
name|NewSearcherListener
argument_list|()
decl_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
name|DirectUpdateHandler2
name|updater
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|CommitTracker
name|tracker
init|=
name|updater
operator|.
name|commitTracker
decl_stmt|;
name|tracker
operator|.
name|setTimeUpperBound
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|MapSolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add a single document with commitWithin == 2 second
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|AutoCommitTest
operator|.
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|2000
argument_list|,
literal|"id"
argument_list|,
literal|"529"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Check it isn't in the index
name|assertQ
argument_list|(
literal|"shouldn't find any"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Wait longer than the commitWithin time
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add one document without commitWithin
name|req
operator|.
name|setContentStreams
argument_list|(
name|AutoCommitTest
operator|.
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"530"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Check it isn't in the index
name|assertQ
argument_list|(
literal|"shouldn't find any"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Delete one document with commitWithin
name|trigger
operator|.
name|pause
argument_list|()
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|AutoCommitTest
operator|.
name|toContentStreams
argument_list|(
name|delI
argument_list|(
literal|"529"
argument_list|,
literal|"commitWithin"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Now make sure we can find it
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|unpause
argument_list|()
expr_stmt|;
comment|// Wait for the commit to happen
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now we shouldn't find it
name|assertQ
argument_list|(
literal|"should find none"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// ... but we should find the new one
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// now make the call 10 times really fast and make sure it
comment|// only commits once
name|req
operator|.
name|setContentStreams
argument_list|(
name|AutoCommitTest
operator|.
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|2000
argument_list|,
literal|"id"
argument_list|,
literal|"500"
argument_list|)
argument_list|,
literal|null
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
literal|"should not be there yet"
argument_list|,
name|req
argument_list|(
literal|"id:500"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// the same for the delete
name|req
operator|.
name|setContentStreams
argument_list|(
name|AutoCommitTest
operator|.
name|toContentStreams
argument_list|(
name|delI
argument_list|(
literal|"530"
argument_list|,
literal|"commitWithin"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
literal|null
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
literal|"should be there"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should be there"
argument_list|,
name|req
argument_list|(
literal|"id:500"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should not be there"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

