begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|MockAnalyzer
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
name|analysis
operator|.
name|MockTokenizer
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
name|document
operator|.
name|DateTools
operator|.
name|Resolution
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|Operator
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
name|queryparser
operator|.
name|util
operator|.
name|QueryParserTestBase
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
name|WildcardQuery
import|;
end_import

begin_comment
comment|/**  * Tests QueryParser.  */
end_comment

begin_class
DECL|class|TestStandardQP
specifier|public
class|class
name|TestStandardQP
extends|extends
name|QueryParserTestBase
block|{
DECL|method|getParser
specifier|public
name|StandardQueryParser
name|getParser
parameter_list|(
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
return|return
name|qp
return|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|StandardQueryParser
name|qp
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|,
name|getDefaultField
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParserConfig
specifier|public
name|CommonQueryParserConfiguration
name|getParserConfig
parameter_list|(
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getParser
argument_list|(
name|a
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|cqpC
operator|!=
literal|null
operator|:
literal|"Parameter must not be null"
assert|;
assert|assert
operator|(
name|cqpC
operator|instanceof
name|StandardQueryParser
operator|)
operator|:
literal|"Parameter must be instance of StandardQueryParser"
assert|;
name|StandardQueryParser
name|qp
init|=
operator|(
name|StandardQueryParser
operator|)
name|cqpC
decl_stmt|;
return|return
name|parse
argument_list|(
name|query
argument_list|,
name|qp
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|parse
argument_list|(
name|query
argument_list|,
name|getParser
argument_list|(
name|a
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isQueryParserException
specifier|public
name|boolean
name|isQueryParserException
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
return|return
name|exception
operator|instanceof
name|QueryNodeException
return|;
block|}
annotation|@
name|Override
DECL|method|setDefaultOperatorOR
specifier|public
name|void
name|setDefaultOperatorOR
parameter_list|(
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|)
block|{
assert|assert
operator|(
name|cqpC
operator|instanceof
name|StandardQueryParser
operator|)
assert|;
name|StandardQueryParser
name|qp
init|=
operator|(
name|StandardQueryParser
operator|)
name|cqpC
decl_stmt|;
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDefaultOperatorAND
specifier|public
name|void
name|setDefaultOperatorAND
parameter_list|(
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|)
block|{
assert|assert
operator|(
name|cqpC
operator|instanceof
name|StandardQueryParser
operator|)
assert|;
name|StandardQueryParser
name|qp
init|=
operator|(
name|StandardQueryParser
operator|)
name|cqpC
decl_stmt|;
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setAnalyzeRangeTerms
specifier|public
name|void
name|setAnalyzeRangeTerms
parameter_list|(
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setAutoGeneratePhraseQueries
specifier|public
name|void
name|setAutoGeneratePhraseQueries
parameter_list|(
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setDateResolution
specifier|public
name|void
name|setDateResolution
parameter_list|(
name|CommonQueryParserConfiguration
name|cqpC
parameter_list|,
name|CharSequence
name|field
parameter_list|,
name|Resolution
name|value
parameter_list|)
block|{
assert|assert
operator|(
name|cqpC
operator|instanceof
name|StandardQueryParser
operator|)
assert|;
name|StandardQueryParser
name|qp
init|=
operator|(
name|StandardQueryParser
operator|)
name|cqpC
decl_stmt|;
name|qp
operator|.
name|getDateResolutionMap
argument_list|()
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testOperatorVsWhitespace
specifier|public
name|void
name|testOperatorVsWhitespace
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-2566 is not implemented for StandardQueryParser
comment|// TODO implement LUCENE-2566 and remove this (override)method
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a - b"
argument_list|,
name|a
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a + b"
argument_list|,
name|a
argument_list|,
literal|"a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a ! b"
argument_list|,
name|a
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRangeWithPhrase
specifier|public
name|void
name|testRangeWithPhrase
parameter_list|()
throws|throws
name|Exception
block|{
comment|// StandardSyntaxParser does not differentiate between a term and a
comment|// one-term-phrase in a range query.
comment|// Is this an issue? Should StandardSyntaxParser mark the text as
comment|// wasEscaped=true ?
name|assertQueryEquals
argument_list|(
literal|"[\\* TO \"*\"]"
argument_list|,
literal|null
argument_list|,
literal|"[\\* TO *]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testEscapedVsQuestionMarkAsWildcard
specifier|public
name|void
name|testEscapedVsQuestionMarkAsWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a:b\\-?c"
argument_list|,
name|a
argument_list|,
literal|"a:b-?c"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a:b\\+?c"
argument_list|,
name|a
argument_list|,
literal|"a:b+?c"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a:b\\:?c"
argument_list|,
name|a
argument_list|,
literal|"a:b:?c"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a:b\\\\?c"
argument_list|,
name|a
argument_list|,
literal|"a:b\\?c"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testEscapedWildcard
specifier|public
name|void
name|testEscapedWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|CommonQueryParserConfiguration
name|qp
init|=
name|getParserConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|WildcardQuery
name|q
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo?ba?r"
argument_list|)
argument_list|)
decl_stmt|;
comment|//TODO not correct!!
name|assertEquals
argument_list|(
name|q
argument_list|,
name|getQuery
argument_list|(
literal|"foo\\?ba?r"
argument_list|,
name|qp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testCollatedRange
specifier|public
name|void
name|testCollatedRange
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|setAnalyzeRangeTerms
argument_list|(
name|getParser
argument_list|(
literal|null
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCollatedRange
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testAutoGeneratePhraseQueriesOn
specifier|public
name|void
name|testAutoGeneratePhraseQueriesOn
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|setAutoGeneratePhraseQueries
argument_list|(
name|getParser
argument_list|(
literal|null
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|testAutoGeneratePhraseQueriesOn
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testStarParsing
specifier|public
name|void
name|testStarParsing
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|testDefaultOperator
specifier|public
name|void
name|testDefaultOperator
parameter_list|()
throws|throws
name|Exception
block|{
name|StandardQueryParser
name|qp
init|=
name|getParser
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// make sure OR is the default:
name|assertEquals
argument_list|(
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|OR
argument_list|,
name|qp
operator|.
name|getDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
name|setDefaultOperatorAND
argument_list|(
name|qp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|AND
argument_list|,
name|qp
operator|.
name|getDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
name|setDefaultOperatorOR
argument_list|(
name|qp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|OR
argument_list|,
name|qp
operator|.
name|getDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testNewFieldQuery
specifier|public
name|void
name|testNewFieldQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|/** ordinary behavior, synonyms form uncoordinated boolean query */
name|StandardQueryParser
name|dumb
init|=
name|getParser
argument_list|(
operator|new
name|Analyzer1
argument_list|()
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|expanded
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|expanded
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expanded
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"dogs"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|expanded
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expanded
operator|.
name|build
argument_list|()
argument_list|,
name|dumb
operator|.
name|parse
argument_list|(
literal|"\"dogs\""
argument_list|,
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
comment|/** even with the phrase operator the behavior is the same */
name|assertEquals
argument_list|(
name|expanded
operator|.
name|build
argument_list|()
argument_list|,
name|dumb
operator|.
name|parse
argument_list|(
literal|"dogs"
argument_list|,
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
comment|/**      * custom behavior, the synonyms are expanded, unless you use quote operator      */
comment|//TODO test something like "SmartQueryParser()"
block|}
block|}
end_class

end_unit

