begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|JsonUpdateRequestHandler
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
name|schema
operator|.
name|IndexSchema
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
name|servlet
operator|.
name|DirectSolrConnection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestJoin
specifier|public
class|class
name|TestJoin
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
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJoin
specifier|public
name|void
name|testJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"john"
argument_list|,
literal|"title"
argument_list|,
literal|"Director"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"mark"
argument_list|,
literal|"title"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Marketing"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"nancy"
argument_list|,
literal|"title"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Sales"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"dave"
argument_list|,
literal|"title"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Support"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"name"
argument_list|,
literal|"tina"
argument_list|,
literal|"title"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys develop stuff"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Marketing"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys make you look good"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Sales"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys sell stuff"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Support"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys help customers"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// test debugging
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=dept_s to=dept_id_s}title:MTS"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/debug/join/{!join from=dept_s to=dept_id_s}title:MTS=={'_MATCH_':'fromSetSize,toSetSize', 'fromSetSize':2, 'toSetSize':3}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=dept_s to=dept_id_s}title:MTS"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':3,'start':0,'docs':[{'id':'10'},{'id':'12'},{'id':'13'}]}"
argument_list|)
expr_stmt|;
comment|// empty from
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=noexist_s to=dept_id_s}*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':0,'start':0,'docs':[]}"
argument_list|)
expr_stmt|;
comment|// empty to
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=dept_s to=noexist_s}*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':0,'start':0,'docs':[]}"
argument_list|)
expr_stmt|;
comment|// self join... return everyone with she same title as Dave
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=title to=title}name:dave"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':2,'start':0,'docs':[{'id':'3'},{'id':'4'}]}"
argument_list|)
expr_stmt|;
comment|// find people that develop stuff
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=dept_id_s to=dept_s}text:develop"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':3,'start':0,'docs':[{'id':'1'},{'id':'4'},{'id':'5'}]}"
argument_list|)
expr_stmt|;
comment|// self join on multivalued text field
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=title to=title}name:dave"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/response=={'numFound':2,'start':0,'docs':[{'id':'3'},{'id':'4'}]}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!join from=dept_s to=dept_id_s}title:MTS"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/response=={'numFound':3,'start':0,'docs':[{'id':'10'},{'id':'12'},{'id':'13'}]}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomJoin
specifier|public
name|void
name|testRandomJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|indexIter
init|=
literal|50
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|queryIter
init|=
literal|50
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
comment|// groups of fields that have any chance of matching... used to
comment|// increase test effectiveness by avoiding 0 resultsets much of the time.
name|String
index|[]
index|[]
name|compat
init|=
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"small_s"
block|,
literal|"small2_s"
block|,
literal|"small2_ss"
block|,
literal|"small3_ss"
block|}
block|,
block|{
literal|"small_i"
block|,
literal|"small2_i"
block|,
literal|"small2_is"
block|,
literal|"small3_is"
block|}
block|}
decl_stmt|;
while|while
condition|(
operator|--
name|indexIter
operator|>=
literal|0
condition|)
block|{
name|int
name|indexSize
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|20
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FldType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|FldType
argument_list|>
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"id"
argument_list|,
name|ONE_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'A'
argument_list|,
literal|'Z'
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"score_f"
argument_list|,
name|ONE_ONE
argument_list|,
operator|new
name|FVal
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// field used to score
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small_s"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_s"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_ss"
argument_list|,
name|ZERO_TWO
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small3_ss"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'A'
argument_list|,
literal|'z'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small_i"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_i"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_is"
argument_list|,
name|ZERO_TWO
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small3_is"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Doc
argument_list|>
name|model
init|=
name|indexDocs
argument_list|(
name|types
argument_list|,
literal|null
argument_list|,
name|indexSize
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
argument_list|>
name|pivots
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|qiter
init|=
literal|0
init|;
name|qiter
operator|<
name|queryIter
condition|;
name|qiter
operator|++
control|)
block|{
name|String
name|fromField
decl_stmt|;
name|String
name|toField
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|5
condition|)
block|{
comment|// pick random fields 5% of the time
name|fromField
operator|=
name|types
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|types
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
operator|.
name|fname
expr_stmt|;
comment|// pick the same field 50% of the time we pick a random field (since other fields won't match anything)
name|toField
operator|=
operator|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|50
operator|)
condition|?
name|fromField
else|:
name|types
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|types
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
operator|.
name|fname
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise, pick compatible fields that have a chance of matching indexed tokens
name|String
index|[]
name|group
init|=
name|compat
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|compat
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|fromField
operator|=
name|group
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|group
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
name|toField
operator|=
name|group
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|group
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
block|}
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|pivot
init|=
name|pivots
operator|.
name|get
argument_list|(
name|fromField
operator|+
literal|"/"
operator|+
name|toField
argument_list|)
decl_stmt|;
if|if
condition|(
name|pivot
operator|==
literal|null
condition|)
block|{
name|pivot
operator|=
name|createJoinMap
argument_list|(
name|model
argument_list|,
name|fromField
argument_list|,
name|toField
argument_list|)
expr_stmt|;
name|pivots
operator|.
name|put
argument_list|(
name|fromField
operator|+
literal|"/"
operator|+
name|toField
argument_list|,
name|pivot
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|Doc
argument_list|>
name|fromDocs
init|=
name|model
operator|.
name|values
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Comparable
argument_list|>
name|docs
init|=
name|join
argument_list|(
name|fromDocs
argument_list|,
name|pivot
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Doc
argument_list|>
name|docList
init|=
operator|new
name|ArrayList
argument_list|<
name|Doc
argument_list|>
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Comparable
name|id
range|:
name|docs
control|)
name|docList
operator|.
name|add
argument_list|(
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|docList
argument_list|,
name|createComparator
argument_list|(
literal|"_docid_"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|sortedDocs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Doc
name|doc
range|:
name|docList
control|)
block|{
if|if
condition|(
name|sortedDocs
operator|.
name|size
argument_list|()
operator|>=
literal|10
condition|)
break|break;
name|sortedDocs
operator|.
name|add
argument_list|(
name|doc
operator|.
name|toObject
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|resultSet
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|resultSet
operator|.
name|put
argument_list|(
literal|"numFound"
argument_list|,
name|docList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|put
argument_list|(
literal|"start"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|put
argument_list|(
literal|"docs"
argument_list|,
name|sortedDocs
argument_list|)
expr_stmt|;
comment|// todo: use different join queries for better coverage
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"echoParams"
argument_list|,
literal|"all"
argument_list|,
literal|"q"
argument_list|,
literal|"{!join from="
operator|+
name|fromField
operator|+
literal|" to="
operator|+
name|toField
operator|+
operator|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|?
literal|" fromIndex=collection1"
else|:
literal|""
operator|)
operator|+
literal|"}*:*"
argument_list|)
decl_stmt|;
name|String
name|strResponse
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|Object
name|realResponse
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|strResponse
argument_list|)
decl_stmt|;
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|matchObj
argument_list|(
literal|"/response"
argument_list|,
name|realResponse
argument_list|,
name|resultSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"JOIN MISMATCH: "
operator|+
name|err
operator|+
literal|"\n\trequest="
operator|+
name|req
operator|+
literal|"\n\tresult="
operator|+
name|strResponse
operator|+
literal|"\n\texpected="
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|resultSet
argument_list|)
operator|+
literal|"\n\tmodel="
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|model
argument_list|)
argument_list|)
expr_stmt|;
comment|// re-execute the request... good for putting a breakpoint here for debugging
name|String
name|rsp
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|createJoinMap
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|createJoinMap
parameter_list|(
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Doc
argument_list|>
name|model
parameter_list|,
name|String
name|fromField
parameter_list|,
name|String
name|toField
parameter_list|)
block|{
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|id_to_id
init|=
operator|new
name|HashMap
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Comparable
argument_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|value_to_id
init|=
name|invertField
argument_list|(
name|model
argument_list|,
name|toField
argument_list|)
decl_stmt|;
for|for
control|(
name|Comparable
name|fromId
range|:
name|model
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Doc
name|doc
init|=
name|model
operator|.
name|get
argument_list|(
name|fromId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Comparable
argument_list|>
name|vals
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|fromField
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
continue|continue;
for|for
control|(
name|Comparable
name|val
range|:
name|vals
control|)
block|{
name|List
argument_list|<
name|Comparable
argument_list|>
name|toIds
init|=
name|value_to_id
operator|.
name|get
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|toIds
operator|==
literal|null
condition|)
continue|continue;
name|Set
argument_list|<
name|Comparable
argument_list|>
name|ids
init|=
name|id_to_id
operator|.
name|get
argument_list|(
name|fromId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<
name|Comparable
argument_list|>
argument_list|()
expr_stmt|;
name|id_to_id
operator|.
name|put
argument_list|(
name|fromId
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Comparable
name|toId
range|:
name|toIds
control|)
name|ids
operator|.
name|add
argument_list|(
name|toId
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|id_to_id
return|;
block|}
DECL|method|join
name|Set
argument_list|<
name|Comparable
argument_list|>
name|join
parameter_list|(
name|Collection
argument_list|<
name|Doc
argument_list|>
name|input
parameter_list|,
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Set
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|joinMap
parameter_list|)
block|{
name|Set
argument_list|<
name|Comparable
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|Comparable
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Doc
name|doc
range|:
name|input
control|)
block|{
name|Collection
argument_list|<
name|Comparable
argument_list|>
name|output
init|=
name|joinMap
operator|.
name|get
argument_list|(
name|doc
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|output
operator|==
literal|null
condition|)
continue|continue;
name|ids
operator|.
name|addAll
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
block|}
end_class

end_unit

