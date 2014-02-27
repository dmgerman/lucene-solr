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
name|impl
operator|.
name|BinaryRequestWriter
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
name|params
operator|.
name|MultiMapSolrParams
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
name|SolrParams
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
name|UpdateParams
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
name|ContentStream
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
name|BinaryUpdateRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|SignatureUpdateProcessorFactoryTest
specifier|public
class|class
name|SignatureUpdateProcessorFactoryTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|betterNotBeJ9
specifier|public
specifier|static
name|void
name|betterNotBeJ9
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"FIXME: SOLR-5793: This test fails under J9"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.info"
argument_list|,
literal|"<?>"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM J9"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** modified by tests as needed */
DECL|field|chain
specifier|private
name|String
name|chain
init|=
literal|"dedupe"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
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
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|chain
operator|=
literal|"dedupe"
expr_stmt|;
comment|// set the default that most tests expect
block|}
DECL|method|checkNumDocs
specifier|static
name|void
name|checkNumDocs
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|n
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDupeAllFieldsDetection
specifier|public
name|void
name|testDupeAllFieldsDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|chain
operator|=
literal|"dedupe-allfields"
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|this
operator|.
name|chain
argument_list|)
decl_stmt|;
name|SignatureUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|SignatureUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"name1'"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"name2'"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDupeDetection
specifier|public
name|void
name|testDupeDetection
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
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"dedupe"
argument_list|)
decl_stmt|;
name|SignatureUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|SignatureUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi'"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man . -"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"name"
argument_list|,
literal|"ali babi'"
argument_list|,
literal|"id"
argument_list|,
literal|"3a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3b"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"t_field"
argument_list|,
literal|"fake value galore"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5a"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi"
argument_list|,
literal|"v_t"
argument_list|,
literal|"MMMMM"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|delI
argument_list|(
literal|"5a"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5a"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi"
argument_list|,
literal|"v_t"
argument_list|,
literal|"MMMMM"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"same"
argument_list|,
literal|"name"
argument_list|,
literal|"baryy white"
argument_list|,
literal|"v_t"
argument_list|,
literal|"random1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"same"
argument_list|,
literal|"name"
argument_list|,
literal|"bishop black"
argument_list|,
literal|"v_t"
argument_list|,
literal|"random2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiThreaded
specifier|public
name|void
name|testMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequestProcessorChain
name|chained
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"dedupe"
argument_list|)
decl_stmt|;
name|SignatureUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|SignatureUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
index|[]
name|threads
init|=
literal|null
decl_stmt|;
name|Thread
index|[]
name|threads2
init|=
literal|null
decl_stmt|;
name|threads
operator|=
operator|new
name|Thread
index|[
literal|7
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
block|{
comment|// h.update(adoc("id", Integer.toString(1+ i), "v_t",
comment|// "Goodbye Dude girl!"));
try|try
block|{
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|1
operator|+
name|i
argument_list|)
argument_list|,
literal|"v_t"
argument_list|,
literal|"Goodbye Dude girl!"
argument_list|)
argument_list|)
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"testThread-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|threads2
operator|=
operator|new
name|Thread
index|[
literal|3
index|]
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
name|threads2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads2
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
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
comment|// h.update(adoc("id" , Integer.toString(1+ i + 10000), "v_t",
comment|// "Goodbye Dude girl"));
comment|// h.update(commit());
try|try
block|{
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|1
operator|+
name|i
argument_list|)
argument_list|,
literal|"v_t"
argument_list|,
literal|"Goodbye Dude girl!"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
expr_stmt|;
name|threads2
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"testThread2-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads2
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads2
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * a non-indexed signatureField is fine as long as overwriteDupes==false    */
annotation|@
name|Test
DECL|method|testNonIndexedSignatureField
specifier|public
name|void
name|testNonIndexedSignatureField
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
name|checkNumDocs
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|chain
operator|=
literal|"stored_sig"
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi'"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2b"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude man!"
argument_list|,
literal|"name"
argument_list|,
literal|"ali babi'"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailNonIndexedSigWithOverwriteDupes
specifier|public
name|void
name|testFailNonIndexedSigWithOverwriteDupes
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
name|SignatureUpdateProcessorFactory
name|f
init|=
operator|new
name|SignatureUpdateProcessorFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"overwriteDupes"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
literal|"signatureField"
argument_list|,
literal|"signatureField_sS"
argument_list|)
expr_stmt|;
name|f
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|boolean
name|exception_ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|f
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception_ok
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Should have gotten an exception from inform(SolrCore)"
argument_list|,
name|exception_ok
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonStringFieldsValues
specifier|public
name|void
name|testNonStringFieldsValues
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|chain
operator|=
literal|"dedupe-allfields"
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|SignatureUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|SignatureUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
name|factory
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
name|mmparams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
operator|new
name|String
index|[]
block|{
name|chain
block|}
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
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
name|addField
argument_list|(
literal|"v_t"
argument_list|,
literal|"same"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"weight"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
literal|34
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
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
name|addField
argument_list|(
literal|"v_t"
argument_list|,
literal|"same"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"weight"
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
literal|66
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|{
comment|// A and B should have same sig as eachother
comment|// even though the particulars of how the the ints_is list are built
name|SolrInputDocument
name|docA
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|docB
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|UnusualList
argument_list|<
name|Integer
argument_list|>
name|ints
init|=
operator|new
name|UnusualList
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
operator|new
name|int
index|[]
block|{
literal|42
block|,
literal|66
block|,
literal|34
block|}
control|)
block|{
name|docA
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
operator|new
name|Integer
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|ints
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|docB
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
name|ints
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrInputDocument
name|doc
range|:
operator|new
name|SolrInputDocument
index|[]
block|{
name|docA
block|,
name|docB
block|}
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"v_t"
argument_list|,
literal|"same"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"weight"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|{
comment|// now add another doc with the same values as A& B above,
comment|// but diff ints_is collection (diff order)
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v_t"
argument_list|,
literal|"same"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"weight"
argument_list|,
literal|3.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|val
range|:
operator|new
name|int
index|[]
block|{
literal|66
block|,
literal|42
block|,
literal|34
block|}
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"ints_is"
argument_list|,
operator|new
name|Integer
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
operator|.
name|getContentStream
argument_list|(
name|ureq
argument_list|)
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|mmparams
argument_list|)
decl_stmt|;
try|try
block|{
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|UpdateRequestHandler
name|h
init|=
operator|new
name|UpdateRequestHandler
argument_list|()
decl_stmt|;
name|h
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|addDoc
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkNumDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/** A list with an unusual toString */
DECL|class|UnusualList
specifier|private
specifier|static
specifier|final
class|class
name|UnusualList
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ArrayList
argument_list|<
name|T
argument_list|>
block|{
DECL|method|UnusualList
specifier|public
name|UnusualList
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UNUSUAL:"
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
name|addDoc
argument_list|(
name|doc
argument_list|,
name|chain
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|static
name|void
name|addDoc
parameter_list|(
name|String
name|doc
parameter_list|,
name|String
name|chain
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
name|mmparams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
operator|new
name|String
index|[]
block|{
name|chain
block|}
argument_list|)
expr_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|(
name|SolrParams
operator|)
name|mmparams
argument_list|)
block|{     }
decl_stmt|;
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
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

