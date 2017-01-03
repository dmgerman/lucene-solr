begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|SolrQuery
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
name|CursorMarkParams
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
name|dataimport
operator|.
name|SolrEntityProcessor
operator|.
name|SolrDocumentListIterator
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
comment|/**  * Unit test of SolrEntityProcessor. A very basic test outside of the DIH.  */
end_comment

begin_class
DECL|class|TestSolrEntityProcessorUnit
specifier|public
class|class
name|TestSolrEntityProcessorUnit
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|class|NoNextMockProcessor
specifier|private
specifier|static
specifier|final
class|class
name|NoNextMockProcessor
extends|extends
name|SolrEntityProcessor
block|{
annotation|@
name|Override
DECL|method|nextPage
specifier|protected
name|void
name|nextPage
parameter_list|()
block|{     }
block|}
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
DECL|method|testQuery
specifier|public
name|void
name|testQuery
parameter_list|()
block|{
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
init|=
name|generateUniqueDocs
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|MockSolrEntityProcessor
name|processor
init|=
name|createAndInit
argument_list|(
name|docs
argument_list|)
decl_stmt|;
try|try
block|{
name|assertExpectedDocs
argument_list|(
name|docs
argument_list|,
name|processor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|processor
operator|.
name|getQueryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAndInit
specifier|private
name|MockSolrEntityProcessor
name|createAndInit
parameter_list|(
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
parameter_list|)
block|{
return|return
name|createAndInit
argument_list|(
name|docs
argument_list|,
name|SolrEntityProcessor
operator|.
name|ROWS_DEFAULT
argument_list|)
return|;
block|}
DECL|method|testNumDocsGreaterThanRows
specifier|public
name|void
name|testNumDocsGreaterThanRows
parameter_list|()
block|{
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
init|=
name|generateUniqueDocs
argument_list|(
literal|44
argument_list|)
decl_stmt|;
name|int
name|rowsNum
init|=
literal|10
decl_stmt|;
name|MockSolrEntityProcessor
name|processor
init|=
name|createAndInit
argument_list|(
name|docs
argument_list|,
name|rowsNum
argument_list|)
decl_stmt|;
try|try
block|{
name|assertExpectedDocs
argument_list|(
name|docs
argument_list|,
name|processor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|processor
operator|.
name|getQueryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAndInit
specifier|private
name|MockSolrEntityProcessor
name|createAndInit
parameter_list|(
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
parameter_list|,
name|int
name|rowsNum
parameter_list|)
block|{
name|MockSolrEntityProcessor
name|processor
init|=
operator|new
name|MockSolrEntityProcessor
argument_list|(
name|docs
argument_list|,
name|rowsNum
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|SolrEntityProcessor
operator|.
name|SOLR_SERVER
argument_list|,
literal|"http://route:66/no"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|processor
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|entityAttrs
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|processor
return|;
block|}
DECL|method|testMultiValuedFields
specifier|public
name|void
name|testMultiValuedFields
parameter_list|()
block|{
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FldType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
name|ID
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
literal|"description"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
literal|'c'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Doc
name|testDoc
init|=
name|createDoc
argument_list|(
name|types
argument_list|)
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|MockSolrEntityProcessor
name|processor
init|=
name|createAndInit
argument_list|(
name|docs
argument_list|)
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
init|=
name|processor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|next
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Comparable
argument_list|>
name|multiField
init|=
operator|(
name|List
argument_list|<
name|Comparable
argument_list|>
operator|)
name|next
operator|.
name|get
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testDoc
operator|.
name|getValues
argument_list|(
literal|"description"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|multiField
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testDoc
operator|.
name|getValues
argument_list|(
literal|"description"
argument_list|)
argument_list|,
name|multiField
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|processor
operator|.
name|getQueryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|processor
operator|.
name|nextRow
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DataImportHandlerException
operator|.
name|class
argument_list|)
DECL|method|testNoQuery
specifier|public
name|void
name|testNoQuery
parameter_list|()
block|{
name|SolrEntityProcessor
name|processor
init|=
operator|new
name|SolrEntityProcessor
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|SolrEntityProcessor
operator|.
name|SOLR_SERVER
argument_list|,
literal|"http://route:66/no"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|processor
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|entityAttrs
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|processor
operator|.
name|buildIterator
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testPagingQuery
specifier|public
name|void
name|testPagingQuery
parameter_list|()
block|{
name|SolrEntityProcessor
name|processor
init|=
operator|new
name|NoNextMockProcessor
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|SolrEntityProcessor
operator|.
name|SOLR_SERVER
argument_list|,
literal|"http://route:66/no"
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|noCursor
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|""
argument_list|,
literal|"false"
argument_list|,
name|CursorMarkParams
operator|.
name|CURSOR_MARK_START
argument_list|)
decl_stmt|;
comment|//only 'true' not '*'
name|Collections
operator|.
name|shuffle
argument_list|(
name|noCursor
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
argument_list|,
name|noCursor
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|processor
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|entityAttrs
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|processor
operator|.
name|buildIterator
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SolrDocumentListIterator
operator|)
name|processor
operator|.
name|rowIterator
operator|)
operator|.
name|passNextPage
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|query
operator|.
name|get
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|TIME_ALLOWED
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCursorQuery
specifier|public
name|void
name|testCursorQuery
parameter_list|()
block|{
name|SolrEntityProcessor
name|processor
init|=
operator|new
name|NoNextMockProcessor
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|SolrEntityProcessor
operator|.
name|SOLR_SERVER
argument_list|,
literal|"http://route:66/no"
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|processor
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|entityAttrs
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|processor
operator|.
name|buildIterator
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SolrDocumentListIterator
operator|)
name|processor
operator|.
name|rowIterator
operator|)
operator|.
name|passNextPage
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_START
argument_list|,
name|query
operator|.
name|get
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|TIME_ALLOWED
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|processor
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|generateUniqueDocs
specifier|private
name|List
argument_list|<
name|Doc
argument_list|>
name|generateUniqueDocs
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|List
argument_list|<
name|FldType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
name|ID
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
literal|40
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
literal|"description"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
literal|'c'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Comparable
argument_list|>
name|previousIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Doc
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numDocs
argument_list|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Doc
name|doc
init|=
name|createDoc
argument_list|(
name|types
argument_list|)
decl_stmt|;
while|while
condition|(
name|previousIds
operator|.
name|contains
argument_list|(
name|doc
operator|.
name|id
argument_list|)
condition|)
block|{
name|doc
operator|=
name|createDoc
argument_list|(
name|types
argument_list|)
expr_stmt|;
block|}
name|previousIds
operator|.
name|add
argument_list|(
name|doc
operator|.
name|id
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
DECL|method|assertExpectedDocs
specifier|private
specifier|static
name|void
name|assertExpectedDocs
parameter_list|(
name|List
argument_list|<
name|Doc
argument_list|>
name|expectedDocs
parameter_list|,
name|SolrEntityProcessor
name|processor
parameter_list|)
block|{
for|for
control|(
name|Doc
name|expectedDoc
range|:
name|expectedDocs
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
init|=
name|processor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDoc
operator|.
name|id
argument_list|,
name|next
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDoc
operator|.
name|getValues
argument_list|(
literal|"description"
argument_list|)
argument_list|,
name|next
operator|.
name|get
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|processor
operator|.
name|nextRow
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

