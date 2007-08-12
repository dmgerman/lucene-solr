begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|util
operator|.
name|SolrPluginUtils
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
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|PhraseQuery
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
name|search
operator|.
name|DisjunctionMaxQuery
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Tests that the functions in SolrPluginUtils work as advertised.  */
end_comment

begin_class
DECL|class|SolrPluginUtilsTest
specifier|public
class|class
name|SolrPluginUtilsTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
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
DECL|method|testPartialEscape
specifier|public
name|void
name|testPartialEscape
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|pe
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|pe
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo\\:bar"
argument_list|,
name|pe
argument_list|(
literal|"foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+foo\\:bar"
argument_list|,
name|pe
argument_list|(
literal|"+foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo \\! bar"
argument_list|,
name|pe
argument_list|(
literal|"foo ! bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo\\?"
argument_list|,
name|pe
argument_list|(
literal|"foo?"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo \"bar\""
argument_list|,
name|pe
argument_list|(
literal|"foo \"bar\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo\\! \"bar\""
argument_list|,
name|pe
argument_list|(
literal|"foo! \"bar\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStripUnbalancedQuotes
specifier|public
name|void
name|testStripUnbalancedQuotes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|strip
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|strip
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo \"bar\""
argument_list|,
name|strip
argument_list|(
literal|"foo \"bar\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"42"
argument_list|,
name|strip
argument_list|(
literal|"42\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"how now brown cow?\""
argument_list|,
name|strip
argument_list|(
literal|"\"how now brown cow?\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"you go\" \"now!\""
argument_list|,
name|strip
argument_list|(
literal|"\"you go\" \"now!\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseFieldBoosts
specifier|public
name|void
name|testParseFieldBoosts
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|e1
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
name|e1
operator|.
name|put
argument_list|(
literal|"fieldOne"
argument_list|,
literal|2.3f
argument_list|)
expr_stmt|;
name|e1
operator|.
name|put
argument_list|(
literal|"fieldTwo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|e1
operator|.
name|put
argument_list|(
literal|"fieldThree"
argument_list|,
operator|-
literal|0.4f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"basic e1"
argument_list|,
name|e1
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"fieldOne^2.3 fieldTwo fieldThree^-0.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"spacey e1"
argument_list|,
name|e1
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"  fieldOne^2.3   fieldTwo fieldThree^-0.4   "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"really spacey e1"
argument_list|,
name|e1
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|" \t fieldOne^2.3 \n  fieldTwo fieldThree^-0.4   "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"really spacey e1"
argument_list|,
name|e1
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
operator|new
name|String
index|[]
block|{
literal|" \t fieldOne^2.3 \n"
block|,
literal|"  fieldTwo fieldThree^-0.4   "
block|,
literal|" "
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|e2
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"empty e2"
argument_list|,
name|e2
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"spacey e2"
argument_list|,
name|e2
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"   \t   "
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisjunctionMaxQueryParser
specifier|public
name|void
name|testDisjunctionMaxQueryParser
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|out
decl_stmt|;
name|String
name|t
decl_stmt|;
name|DisjunctionMaxQueryParser
name|qp
init|=
operator|new
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|qp
operator|.
name|addAlias
argument_list|(
literal|"hoss"
argument_list|,
literal|0.01f
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"title^2.0 title_stemmed name^1.2 subject^0.5"
argument_list|)
argument_list|)
expr_stmt|;
name|qp
operator|.
name|addAlias
argument_list|(
literal|"test"
argument_list|,
literal|0.01f
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"text^2.0"
argument_list|)
argument_list|)
expr_stmt|;
name|qp
operator|.
name|addAlias
argument_list|(
literal|"unused"
argument_list|,
literal|1.0f
argument_list|,
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
literal|"subject^0.5 sind^1.5"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* first some sanity tests that don't use aliasing at all */
name|t
operator|=
literal|"XXXXXXXX"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" sanity test gave back null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" sanity test isn't TermQuery: "
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" sanity test is wrong field"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
argument_list|,
operator|(
operator|(
name|TermQuery
operator|)
name|out
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
literal|"subject:XXXXXXXX"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" sanity test gave back null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" sanity test isn't TermQuery: "
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" sanity test is wrong field"
argument_list|,
literal|"subject"
argument_list|,
operator|(
operator|(
name|TermQuery
operator|)
name|out
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
comment|/* field has untokenzied type, so this should be a term anyway */
name|t
operator|=
literal|"sind:\"simple phrase\""
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" sanity test gave back null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" sanity test isn't TermQuery: "
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" sanity test is wrong field"
argument_list|,
literal|"sind"
argument_list|,
operator|(
operator|(
name|TermQuery
operator|)
name|out
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
literal|"subject:\"simple phrase\""
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" sanity test gave back null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" sanity test isn't PhraseQuery: "
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|PhraseQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" sanity test is wrong field"
argument_list|,
literal|"subject"
argument_list|,
operator|(
operator|(
name|PhraseQuery
operator|)
name|out
operator|)
operator|.
name|getTerms
argument_list|()
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
comment|/* now some tests that use aliasing */
comment|/* basic usage of single "term" */
name|t
operator|=
literal|"hoss:XXXXXXXX"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" was null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" wasn't a DMQ:"
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" wrong number of clauses"
argument_list|,
literal|4
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|out
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/* odd case, but should still work, DMQ of one clause */
name|t
operator|=
literal|"test:YYYYY"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" was null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" wasn't a DMQ:"
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" wrong number of clauses"
argument_list|,
literal|1
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|out
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/* basic usage of multiple "terms" */
name|t
operator|=
literal|"hoss:XXXXXXXX test:YYYYY"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" was null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" wasn't a boolean:"
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|out
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|bq
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" wrong number of clauses"
argument_list|,
literal|2
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|sub
init|=
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" first wasn't a DMQ:"
operator|+
name|sub
operator|.
name|getClass
argument_list|()
argument_list|,
name|sub
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" first had wrong number of clauses"
argument_list|,
literal|4
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|sub
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sub
operator|=
name|clauses
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" second wasn't a DMQ:"
operator|+
name|sub
operator|.
name|getClass
argument_list|()
argument_list|,
name|sub
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" second had wrong number of clauses"
argument_list|,
literal|1
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|sub
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* a phrase, and a term that is a stop word for some fields */
name|t
operator|=
literal|"hoss:\"XXXXXX YYYYY\" hoss:the"
expr_stmt|;
name|out
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|+
literal|" was null"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" wasn't a boolean:"
operator|+
name|out
operator|.
name|getClass
argument_list|()
argument_list|,
name|out
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|out
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|bq
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" wrong number of clauses"
argument_list|,
literal|2
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|sub
init|=
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" first wasn't a DMQ:"
operator|+
name|sub
operator|.
name|getClass
argument_list|()
argument_list|,
name|sub
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" first had wrong number of clauses"
argument_list|,
literal|4
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|sub
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sub
operator|=
name|clauses
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|+
literal|" second wasn't a DMQ:"
operator|+
name|sub
operator|.
name|getClass
argument_list|()
argument_list|,
name|sub
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|+
literal|" second had wrong number of clauses (stop words)"
argument_list|,
literal|2
argument_list|,
name|countItems
argument_list|(
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|sub
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|countItems
specifier|private
specifier|static
name|int
name|countItems
parameter_list|(
name|Iterator
name|i
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|testMinShouldMatchCalculator
specifier|public
name|void
name|testMinShouldMatchCalculator
parameter_list|()
block|{
comment|/* zero is zero is zero */
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"0%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"-5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"-100%"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* basic integers */
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"-5"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* positive percentages with rounding */
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|4
argument_list|,
literal|"25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|calcMSM
argument_list|(
literal|10
argument_list|,
literal|"25%"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* negative percentages with rounding */
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"-25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|4
argument_list|,
literal|"-25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"-25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|calcMSM
argument_list|(
literal|10
argument_list|,
literal|"-25%"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* conditional */
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|1
argument_list|,
literal|"3<0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|calcMSM
argument_list|(
literal|2
argument_list|,
literal|"3<0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"3<0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|4
argument_list|,
literal|"3<0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"3<0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|1
argument_list|,
literal|"3<25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|calcMSM
argument_list|(
literal|2
argument_list|,
literal|"3<25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"3<25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|4
argument_list|,
literal|"3<25%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"3<25%"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* multiple conditionals */
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|calcMSM
argument_list|(
literal|1
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|calcMSM
argument_list|(
literal|2
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|3
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|calcMSM
argument_list|(
literal|4
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|calcMSM
argument_list|(
literal|5
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|calcMSM
argument_list|(
literal|6
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|calcMSM
argument_list|(
literal|7
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|calcMSM
argument_list|(
literal|8
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|calcMSM
argument_list|(
literal|9
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|calcMSM
argument_list|(
literal|10
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|calcMSM
argument_list|(
literal|11
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|calcMSM
argument_list|(
literal|12
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|97
argument_list|,
name|calcMSM
argument_list|(
literal|100
argument_list|,
literal|"3<-25% 10<-3"
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|q
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|q
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|q
argument_list|,
literal|"50%"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|q
argument_list|,
literal|"99"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"f"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|q
argument_list|,
literal|"50%"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** macro */
DECL|method|pe
specifier|public
name|String
name|pe
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
return|return
name|SolrPluginUtils
operator|.
name|partialEscape
argument_list|(
name|s
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** macro */
DECL|method|strip
specifier|public
name|String
name|strip
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
return|return
name|SolrPluginUtils
operator|.
name|stripUnbalancedQuotes
argument_list|(
name|s
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** macro */
DECL|method|calcMSM
specifier|public
name|int
name|calcMSM
parameter_list|(
name|int
name|clauses
parameter_list|,
name|String
name|spec
parameter_list|)
block|{
return|return
name|SolrPluginUtils
operator|.
name|calculateMinShouldMatch
argument_list|(
name|clauses
argument_list|,
name|spec
argument_list|)
return|;
block|}
block|}
end_class

end_unit

