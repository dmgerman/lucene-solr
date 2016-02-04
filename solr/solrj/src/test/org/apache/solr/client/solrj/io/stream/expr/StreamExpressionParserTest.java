begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.expr
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
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
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
name|util
operator|.
name|LuceneTestCase
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParser
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
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
comment|/**  **/
end_comment

begin_class
DECL|class|StreamExpressionParserTest
specifier|public
class|class
name|StreamExpressionParserTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|StreamExpressionParserTest
specifier|public
name|StreamExpressionParserTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamExpression
name|actual
decl_stmt|,
name|expected
decl_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"aliases(a_i=alias.a_i)"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"aliases"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"a_i"
argument_list|,
literal|"alias.a_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"search(a,b)"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"a"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"search(collection1, q=*:*, sort=\"fieldA desc, fieldB asc, fieldC asc\")"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"q"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldA desc, fieldB asc, fieldC asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"unique(search(collection1, q=*:*, sort=\"fieldA desc, fieldB asc, fieldC asc\"))"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"unique"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"q"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldA desc, fieldB asc, fieldC asc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"unique(search(collection1, q=*:*, sort=\"fieldA desc, fieldB asc, fieldC asc\"), alt=search(collection1, foo=bar))"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"unique"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"q"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldA desc, fieldB asc, fieldC asc"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"alt"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"innerJoin("
operator|+
literal|"left=search(collection1, q=*:*, fl=\"fieldA,fieldB,fieldC\", sort=\"fieldA asc, fieldB asc\"),"
operator|+
literal|"right=search(collection2, q=*:*, fl=\"fieldA,fieldD\", sort=fieldA asc),"
operator|+
literal|"on(equals(fieldA), notEquals(fieldC,fieldD))"
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|StreamExpression
argument_list|(
literal|"innerJoin"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"left"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"fl"
argument_list|,
literal|"fieldA,fieldB,fieldC"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|,
literal|"fieldA asc, fieldB asc"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"right"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"search"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"collection2"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"fl"
argument_list|,
literal|"fieldA,fieldD"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|,
literal|"fieldA asc"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"on"
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"equals"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldA"
argument_list|)
argument_list|)
operator|.
name|withParameter
argument_list|(
operator|new
name|StreamExpression
argument_list|(
literal|"notEquals"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldC"
argument_list|)
operator|.
name|withParameter
argument_list|(
literal|"fieldD"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

