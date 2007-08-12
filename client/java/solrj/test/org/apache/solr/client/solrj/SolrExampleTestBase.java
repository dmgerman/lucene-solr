begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

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
name|io
operator|.
name|StringWriter
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
name|Collection
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
name|util
operator|.
name|ClientUtils
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
name|XML
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
comment|/**  * This should include tests against the example solr config  *   * This lets us try various SolrServer implementations with the same tests.  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrExampleTestBase
specifier|abstract
specifier|public
class|class
name|SolrExampleTestBase
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"../../../example/solr/conf/schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"../../../example/solr/conf/solrconfig.xml"
return|;
block|}
comment|/**    * Subclasses need to initialize the server impl    */
DECL|method|getSolrServer
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrServer
parameter_list|()
function_decl|;
comment|/**    * query the example    */
DECL|method|testExampleConfig
specifier|public
name|void
name|testExampleConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|// Empty the database...
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
comment|// Now add something...
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|docID
init|=
literal|"1112211111"
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docID
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"my name!"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|UpdateResponse
name|upres
init|=
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ADD:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|upres
operator|=
name|server
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"COMMIT:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|upres
operator|=
name|server
operator|.
name|optimize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OPTIMIZE:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"id:"
operator|+
name|docID
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now add a few docs for facet testing...
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"inStock"
argument_list|,
literal|true
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|2
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"timestamp"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc3
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"inStock"
argument_list|,
literal|false
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|3
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"timestamp"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc4
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"inStock"
argument_list|,
literal|true
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|4
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"timestamp"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc4
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc5
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc5
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc5
operator|.
name|addField
argument_list|(
literal|"inStock"
argument_list|,
literal|false
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc5
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|5
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc5
operator|.
name|addField
argument_list|(
literal|"timestamp"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc5
argument_list|)
expr_stmt|;
name|upres
operator|=
name|server
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ADD:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|upres
operator|=
name|server
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"COMMIT:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|upres
operator|=
name|server
operator|.
name|optimize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OPTIMIZE:"
operator|+
name|upres
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|upres
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetQuery
argument_list|(
literal|"price:[* TO 2]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetQuery
argument_list|(
literal|"price:[2 TO 4]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetQuery
argument_list|(
literal|"price:[5 TO *]"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetField
argument_list|(
literal|"inStock"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetField
argument_list|(
literal|"price"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetField
argument_list|(
literal|"timestamp"
argument_list|)
expr_stmt|;
name|query
operator|.
name|removeFilterQuery
argument_list|(
literal|"inStock:true"
argument_list|)
expr_stmt|;
name|response
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|response
operator|.
name|getFacetQuery
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getFacetField
argument_list|(
literal|"inStock"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|response
operator|.
name|getFacetField
argument_list|(
literal|"price"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// test a second query, test making a copy of the main query
name|SolrQuery
name|query2
init|=
name|query
operator|.
name|getCopy
argument_list|()
decl_stmt|;
name|query2
operator|.
name|addFilterQuery
argument_list|(
literal|"inStock:true"
argument_list|)
expr_stmt|;
name|response
operator|=
name|server
operator|.
name|query
argument_list|(
name|query2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|query2
operator|.
name|getFilterQueries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|query
operator|.
name|getFilterQueries
argument_list|()
operator|==
name|query2
operator|.
name|getFilterQueries
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * query the example    */
DECL|method|testAddRetrieve
specifier|public
name|void
name|testAddRetrieve
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|// Empty the database...
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
comment|// Now add something...
name|SolrInputDocument
name|doc1
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id1"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"doc1"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id2"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"doc2"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
comment|// Add the documents
name|server
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addSortField
argument_list|(
literal|"price"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now do it again
name|server
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNumFound
specifier|protected
name|void
name|assertNumFound
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|QueryResponse
name|rsp
init|=
name|getSolrServer
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|!=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"expected: "
operator|+
name|num
operator|+
literal|" but had: "
operator|+
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|+
literal|" :: "
operator|+
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAddDelete
specifier|public
name|void
name|testAddDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|// Empty the database...
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|SolrInputDocument
index|[]
name|doc
init|=
operator|new
name|SolrInputDocument
index|[
literal|3
index|]
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|doc
index|[
name|i
index|]
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
index|[
name|i
index|]
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
literal|"& 222"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
name|String
name|id
init|=
operator|(
name|String
operator|)
name|doc
index|[
literal|0
index|]
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
decl_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// make sure it got in there
name|server
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
comment|// add it back
name|server
operator|.
name|add
argument_list|(
name|doc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// make sure it got in
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"id:\""
operator|+
name|ClientUtils
operator|.
name|escapeQueryChars
argument_list|(
name|id
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
comment|// Add two documents
for|for
control|(
name|SolrInputDocument
name|d
range|:
name|doc
control|)
block|{
name|server
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// should be able to handle multiple delete commands in a single go
name|StringWriter
name|xml
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|xml
operator|.
name|append
argument_list|(
literal|"<delete>"
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrInputDocument
name|d
range|:
name|doc
control|)
block|{
name|xml
operator|.
name|append
argument_list|(
literal|"<id>"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
operator|(
name|String
operator|)
name|d
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|xml
operator|.
name|append
argument_list|(
literal|"</id>"
argument_list|)
expr_stmt|;
block|}
name|xml
operator|.
name|append
argument_list|(
literal|"</delete>"
argument_list|)
expr_stmt|;
name|DirectXmlUpdateRequest
name|up
init|=
operator|new
name|DirectXmlUpdateRequest
argument_list|(
literal|"/update"
argument_list|,
name|xml
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|server
operator|.
name|request
argument_list|(
name|up
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
block|}
block|}
end_class

end_unit

