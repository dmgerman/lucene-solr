begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|TermVectorParams
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
name|SimpleOrderedMap
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
name|SolrRequestHandler
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|TermVectorComponentTest
specifier|public
class|class
name|TermVectorComponentTest
extends|extends
name|SolrTestCaseJ4
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"This is a title and another title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_notv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_postv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"This is a document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"another document"
argument_list|)
argument_list|)
expr_stmt|;
comment|//bunch of docs that are variants on blue
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blud"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"boue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"glue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blah"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|tv
specifier|static
name|String
name|tv
init|=
literal|"tvrh"
decl_stmt|;
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'doc-0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_offtv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_posofftv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_postv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptions
specifier|public
name|void
name|testOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors/doc-0/test_posofftv/anoth=={'tf':1, 'offsets':{'start':20, 'end':27}, 'positions':{'position':1}, 'df':2, 'tf-idf':0.5}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPerField
specifier|public
name|void
name|testPerField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|FIELDS
argument_list|,
literal|"test_basictv,test_notv,test_postv,test_offtv,test_posofftv"
argument_list|,
literal|"f.test_posofftv."
operator|+
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_offtv."
operator|+
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"/termVectors/doc-0/test_basictv=={'anoth':{},'titl':{}}"
argument_list|,
literal|"/termVectors/doc-0/test_postv/anoth=={'tf':1, 'positions':{'position':1}, 'df':2, 'tf-idf':0.5}"
argument_list|,
literal|"/termVectors/doc-0/test_offtv/anoth=={'tf':1, 'df':2, 'tf-idf':0.5}"
argument_list|,
literal|"/termVectors/warnings=={ 'noTermVectors':['test_notv'], 'noPositions':['test_basictv', 'test_offtv'], 'noOffsets':['test_basictv', 'test_postv']}"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: this test is really fragile since it pokes around in solr's guts and makes many assumptions.
comment|// it should be rewritten to use the real distributed interface
annotation|@
name|Test
DECL|method|testDistributed
specifier|public
name|void
name|testDistributed
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
name|TermVectorComponent
name|tvComp
init|=
operator|(
name|TermVectorComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"tvComponent"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tvComp is null and it shouldn't be"
argument_list|,
name|tvComp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|()
decl_stmt|;
name|rb
operator|.
name|stage
operator|=
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
expr_stmt|;
name|rb
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
literal|"localhost:0"
block|,
literal|"localhost:1"
block|,
literal|"localhost:2"
block|,
literal|"localhost:3"
block|}
expr_stmt|;
comment|//we don't actually call these, since we are going to invoke distributedProcess directly
name|rb
operator|.
name|resultIds
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|components
operator|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|components
operator|.
name|add
argument_list|(
name|tvComp
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:0"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"tvrh"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|rb
operator|.
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|rb
operator|.
name|outgoing
operator|=
operator|new
name|ArrayList
argument_list|<
name|ShardRequest
argument_list|>
argument_list|()
expr_stmt|;
comment|//one doc per shard, but make sure there are enough docs to go around
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rb
operator|.
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ShardDoc
name|doc
init|=
operator|new
name|ShardDoc
argument_list|()
decl_stmt|;
name|doc
operator|.
name|id
operator|=
name|i
expr_stmt|;
comment|//must be a valid doc that was indexed.
name|doc
operator|.
name|score
operator|=
literal|1
operator|-
operator|(
name|i
operator|/
operator|(
name|float
operator|)
name|rb
operator|.
name|shards
operator|.
name|length
operator|)
expr_stmt|;
name|doc
operator|.
name|positionInResponse
operator|=
name|i
expr_stmt|;
name|doc
operator|.
name|shard
operator|=
name|rb
operator|.
name|shards
index|[
name|i
index|]
expr_stmt|;
name|doc
operator|.
name|orderInShard
operator|=
literal|0
expr_stmt|;
name|rb
operator|.
name|resultIds
operator|.
name|put
argument_list|(
name|doc
operator|.
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|result
init|=
name|tvComp
operator|.
name|distributedProcess
argument_list|(
name|rb
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|+
literal|" does not equal: "
operator|+
name|ResponseBuilder
operator|.
name|STAGE_DONE
argument_list|,
name|result
operator|==
name|ResponseBuilder
operator|.
name|STAGE_DONE
argument_list|)
expr_stmt|;
comment|//one outgoing per shard
name|assertTrue
argument_list|(
literal|"rb.outgoing Size: "
operator|+
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|rb
operator|.
name|shards
operator|.
name|length
argument_list|,
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|==
name|rb
operator|.
name|shards
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRequest
name|request
range|:
name|rb
operator|.
name|outgoing
control|)
block|{
name|ModifiableSolrParams
name|solrParams
init|=
name|request
operator|.
name|params
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Shard: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|request
operator|.
name|shards
argument_list|)
operator|+
literal|" Params: "
operator|+
name|solrParams
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/* *<field name="test_basictv" type="text" termVectors="true"/><field name="test_notv" type="text" termVectors="false"/><field name="test_postv" type="text" termVectors="true" termPositions="true"/><field name="test_offtv" type="text" termVectors="true" termOffsets="true"/><field name="test_posofftv" type="text" termVectors="true"      termPositions="true" termOffsets="true"/> * * */
end_comment

end_unit

