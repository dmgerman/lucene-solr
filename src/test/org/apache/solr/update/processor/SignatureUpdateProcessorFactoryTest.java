begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|XmlUpdateRequestHandler
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

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|SignatureUpdateProcessorFactoryTest
specifier|public
class|class
name|SignatureUpdateProcessorFactoryTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
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
annotation|@
name|Override
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
name|assertEquals
argument_list|(
literal|1l
argument_list|,
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
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
name|assertEquals
argument_list|(
literal|2l
argument_list|,
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
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
name|assertEquals
argument_list|(
literal|3l
argument_list|,
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
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
name|assertEquals
argument_list|(
literal|4l
argument_list|,
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
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
name|assertEquals
argument_list|(
literal|1l
argument_list|,
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
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
name|UPDATE_PROCESSOR
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dedupe"
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
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
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
block|}
block|}
end_class

end_unit

