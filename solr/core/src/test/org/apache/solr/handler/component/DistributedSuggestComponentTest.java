begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|BaseDistributedSearchTestCase
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
name|response
operator|.
name|QueryResponse
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
name|spelling
operator|.
name|suggest
operator|.
name|SuggesterParams
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
comment|/**  * Test for SuggestComponent's distributed querying  *  * @see org.apache.solr.handler.component.SuggestComponent  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|DistributedSuggestComponentTest
specifier|public
class|class
name|DistributedSuggestComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedSuggestComponentTest
specifier|public
name|DistributedSuggestComponentTest
parameter_list|()
block|{
comment|//Helpful for debugging
comment|//fixShardCount=true;
comment|//shardCount=2;
comment|//stress=0;
comment|//deadServers=null;
name|configString
operator|=
literal|"solrconfig-suggestercomponent.xml"
expr_stmt|;
block|}
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
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// need an FS factory
block|}
annotation|@
name|Override
DECL|method|validateControlData
specifier|public
name|void
name|validateControlData
parameter_list|(
name|QueryResponse
name|control
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
name|control
operator|.
name|getResponse
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|sc
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"suggest"
argument_list|)
decl_stmt|;
name|String
name|command
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"command"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sc
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|command
operator|==
literal|null
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Control data did not return any suggestions or execute any command"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"cat"
argument_list|,
literal|"This is another title"
argument_list|,
literal|"price"
argument_list|,
literal|"10"
argument_list|,
literal|"weight"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"cat"
argument_list|,
literal|"Yet another"
argument_list|,
literal|"price"
argument_list|,
literal|"15"
argument_list|,
literal|"weight"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"cat"
argument_list|,
literal|"Yet another title"
argument_list|,
literal|"price"
argument_list|,
literal|"20"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"cat"
argument_list|,
literal|"suggestions for suggest"
argument_list|,
literal|"price"
argument_list|,
literal|"25"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"cat"
argument_list|,
literal|"Red fox"
argument_list|,
literal|"price"
argument_list|,
literal|"30"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"cat"
argument_list|,
literal|"Rad fox"
argument_list|,
literal|"price"
argument_list|,
literal|"35"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"cat"
argument_list|,
literal|"example data"
argument_list|,
literal|"price"
argument_list|,
literal|"40"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"cat"
argument_list|,
literal|"example inputdata"
argument_list|,
literal|"price"
argument_list|,
literal|"45"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"cat"
argument_list|,
literal|"blah in blah"
argument_list|,
literal|"price"
argument_list|,
literal|"50"
argument_list|,
literal|"weight"
argument_list|,
literal|"40"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"cat"
argument_list|,
literal|"another blah in blah"
argument_list|,
literal|"price"
argument_list|,
literal|"55"
argument_list|,
literal|"weight"
argument_list|,
literal|"40"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|String
name|requestHandlerName
init|=
literal|"/suggest"
decl_stmt|;
name|String
name|docDictName
init|=
literal|"suggest_fuzzy_doc_dict"
decl_stmt|;
name|String
name|docExprDictName
init|=
literal|"suggest_fuzzy_doc_expr_dict"
decl_stmt|;
comment|//Shortcut names
name|String
name|build
init|=
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
decl_stmt|;
name|String
name|buildAll
init|=
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
decl_stmt|;
name|String
name|count
init|=
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
decl_stmt|;
name|String
name|dictionaryName
init|=
name|SuggesterParams
operator|.
name|SUGGEST_DICT
decl_stmt|;
comment|//Build the suggest dictionary
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// build all the suggesters in one go
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|buildAll
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// build suggesters individually
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|build
argument_list|,
literal|"true"
argument_list|,
name|dictionaryName
argument_list|,
name|docDictName
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|build
argument_list|,
literal|"true"
argument_list|,
name|dictionaryName
argument_list|,
name|docExprDictName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Test Basic Functionality
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"exampel"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|dictionaryName
argument_list|,
name|docDictName
argument_list|,
name|count
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"Yet"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|dictionaryName
argument_list|,
name|docExprDictName
argument_list|,
name|count
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"blah"
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|dictionaryName
argument_list|,
name|docExprDictName
argument_list|,
name|count
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"blah"
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|dictionaryName
argument_list|,
name|docDictName
argument_list|,
name|count
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test multiSuggester
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"exampel"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|dictionaryName
argument_list|,
name|docDictName
argument_list|,
name|dictionaryName
argument_list|,
name|docExprDictName
argument_list|,
name|count
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildRequest
specifier|private
name|Object
index|[]
name|buildRequest
parameter_list|(
name|String
name|q
parameter_list|,
name|boolean
name|useSuggestQ
parameter_list|,
name|String
name|handlerName
parameter_list|,
name|String
modifier|...
name|addlParams
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|useSuggestQ
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"suggest.q"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handlerName
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"shards.qt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handlerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|addlParams
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|addlParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|params
operator|.
name|toArray
argument_list|(
operator|new
name|Object
index|[
name|params
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

