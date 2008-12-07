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
name|FacetParams
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrQueryTest
specifier|public
class|class
name|SolrQueryTest
extends|extends
name|TestCase
block|{
DECL|method|testSolrQueryMethods
specifier|public
name|void
name|testSolrQueryMethods
parameter_list|()
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"dog"
argument_list|)
decl_stmt|;
name|boolean
name|b
init|=
literal|false
decl_stmt|;
name|q
operator|.
name|setFacetLimit
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|q
operator|.
name|addFacetField
argument_list|(
literal|"price"
argument_list|)
expr_stmt|;
name|q
operator|.
name|addFacetField
argument_list|(
literal|"state"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|.
name|getFacetFields
argument_list|()
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|q
operator|.
name|addFacetQuery
argument_list|(
literal|"instock:true"
argument_list|)
expr_stmt|;
name|q
operator|.
name|addFacetQuery
argument_list|(
literal|"instock:false"
argument_list|)
expr_stmt|;
name|q
operator|.
name|addFacetQuery
argument_list|(
literal|"a:b"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|.
name|getFacetQuery
argument_list|()
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetField
argument_list|(
literal|"price"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetField
argument_list|(
literal|"price2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetField
argument_list|(
literal|"state"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|q
operator|.
name|getFacetFields
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetQuery
argument_list|(
literal|"instock:true"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetQuery
argument_list|(
literal|"instock:false"
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetQuery
argument_list|(
literal|"a:c"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|b
operator|=
name|q
operator|.
name|removeFacetQuery
argument_list|(
literal|"a:b"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|q
operator|.
name|getFacetQuery
argument_list|()
argument_list|)
expr_stmt|;
name|q
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
name|q
operator|.
name|addSortField
argument_list|(
literal|"date"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|q
operator|.
name|addSortField
argument_list|(
literal|"qty"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|q
operator|.
name|removeSortField
argument_list|(
literal|"date"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|q
operator|.
name|getSortFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|.
name|removeSortField
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
name|q
operator|.
name|removeSortField
argument_list|(
literal|"qty"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|q
operator|.
name|getSortFields
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|addHighlightField
argument_list|(
literal|"hl1"
argument_list|)
expr_stmt|;
name|q
operator|.
name|addHighlightField
argument_list|(
literal|"hl2"
argument_list|)
expr_stmt|;
name|q
operator|.
name|setHighlightSnippets
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|q
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|q
operator|.
name|getHighlightFragsize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|.
name|getHighlightSnippets
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|q
operator|.
name|removeHighlightField
argument_list|(
literal|"hl1"
argument_list|)
expr_stmt|;
name|q
operator|.
name|removeHighlightField
argument_list|(
literal|"hl3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|q
operator|.
name|getHighlightFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|q
operator|.
name|removeHighlightField
argument_list|(
literal|"hl2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|q
operator|.
name|getHighlightFields
argument_list|()
argument_list|)
expr_stmt|;
comment|// check to see that the removes are properly clearing the cgi params
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|.
name|toString
argument_list|()
argument_list|,
literal|"q=dog"
argument_list|)
expr_stmt|;
comment|//Add time allowed param
name|q
operator|.
name|setTimeAllowed
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|Integer
operator|)
literal|1000
argument_list|,
name|q
operator|.
name|getTimeAllowed
argument_list|()
argument_list|)
expr_stmt|;
comment|//Adding a null should remove it
name|q
operator|.
name|setTimeAllowed
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|q
operator|.
name|getTimeAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
DECL|method|testFacetSort
specifier|public
name|void
name|testFacetSort
parameter_list|()
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"dog"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"count"
argument_list|,
name|q
operator|.
name|getFacetSortString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|setFacetSort
argument_list|(
literal|"lex"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lex"
argument_list|,
name|q
operator|.
name|getFacetSortString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFacetSortLegacy
specifier|public
name|void
name|testFacetSortLegacy
parameter_list|()
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"dog"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected default value to be true"
argument_list|,
name|q
operator|.
name|getFacetSort
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|setFacetSort
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected set value to be false"
argument_list|,
name|q
operator|.
name|getFacetSort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSettersGetters
specifier|public
name|void
name|testSettersGetters
parameter_list|()
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setFacetLimit
argument_list|(
literal|10
argument_list|)
operator|.
name|getFacetLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setFacetMinCount
argument_list|(
literal|10
argument_list|)
operator|.
name|getFacetMinCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lex"
argument_list|,
name|q
operator|.
name|setFacetSort
argument_list|(
literal|"lex"
argument_list|)
operator|.
name|getFacetSortString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setHighlightSnippets
argument_list|(
literal|10
argument_list|)
operator|.
name|getHighlightSnippets
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setHighlightFragsize
argument_list|(
literal|10
argument_list|)
operator|.
name|getHighlightFragsize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|q
operator|.
name|setHighlightRequireFieldMatch
argument_list|(
literal|true
argument_list|)
operator|.
name|getHighlightRequireFieldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setHighlightSimplePre
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getHighlightSimplePre
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setHighlightSimplePost
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getHighlightSimplePost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|q
operator|.
name|setHighlight
argument_list|(
literal|true
argument_list|)
operator|.
name|getHighlight
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setQuery
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setRows
argument_list|(
literal|10
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setStart
argument_list|(
literal|10
argument_list|)
operator|.
name|getStart
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setQueryType
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getQueryType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|q
operator|.
name|setTimeAllowed
argument_list|(
literal|10
argument_list|)
operator|.
name|getTimeAllowed
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// non-standard
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setFacetPrefix
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|(
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|q
operator|.
name|setFacetPrefix
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|getFieldParam
argument_list|(
literal|"a"
argument_list|,
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|q
operator|.
name|setMissing
argument_list|(
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET_MISSING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|q
operator|.
name|setFacetMissing
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET_MISSING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|q
operator|.
name|setParam
argument_list|(
literal|"xxx"
argument_list|,
literal|true
argument_list|)
operator|.
name|getParams
argument_list|(
literal|"xxx"
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrder
specifier|public
name|void
name|testOrder
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
operator|.
name|reverse
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
operator|.
name|reverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

