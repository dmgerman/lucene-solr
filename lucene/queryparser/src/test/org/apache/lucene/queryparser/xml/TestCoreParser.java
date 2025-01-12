begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
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
name|MockTokenFilter
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
name|Document
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
name|IndexReader
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
name|IndexSearcher
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
name|ScoreDoc
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
name|TopDocs
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
name|spans
operator|.
name|SpanQuery
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
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

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
name|InputStream
import|;
end_import

begin_class
DECL|class|TestCoreParser
specifier|public
class|class
name|TestCoreParser
extends|extends
name|LuceneTestCase
block|{
DECL|field|defaultField
specifier|final
specifier|private
specifier|static
name|String
name|defaultField
init|=
literal|"contents"
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|static
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|coreParser
specifier|private
specifier|static
name|CoreParser
name|coreParser
decl_stmt|;
DECL|field|indexData
specifier|private
specifier|static
name|CoreParserTestIndexData
name|indexData
decl_stmt|;
DECL|method|newAnalyzer
specifier|protected
name|Analyzer
name|newAnalyzer
parameter_list|()
block|{
comment|// TODO: rewrite test (this needs to set QueryParser.enablePositionIncrements, too, for work with CURRENT):
return|return
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
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
return|;
block|}
DECL|method|newCoreParser
specifier|protected
name|CoreParser
name|newCoreParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
operator|new
name|CoreParser
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|indexData
operator|!=
literal|null
condition|)
block|{
name|indexData
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexData
operator|=
literal|null
expr_stmt|;
block|}
name|coreParser
operator|=
literal|null
expr_stmt|;
name|analyzer
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testTermQueryXML
specifier|public
name|void
name|testTermQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermQueryEmptyXML
specifier|public
name|void
name|testTermQueryEmptyXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|parseShouldFail
argument_list|(
literal|"TermQueryEmpty.xml"
argument_list|,
literal|"TermQuery has no text"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermsQueryXML
specifier|public
name|void
name|testTermsQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermsQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanQueryXML
specifier|public
name|void
name|testBooleanQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"BooleanQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisjunctionMaxQueryXML
specifier|public
name|void
name|testDisjunctionMaxQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"DisjunctionMaxQuery.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|d
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|q
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0f
argument_list|,
name|d
operator|.
name|getTieBreakerMultiplier
argument_list|()
argument_list|,
literal|0.0001f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|d
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|ndq
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|d
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.2f
argument_list|,
name|ndq
operator|.
name|getTieBreakerMultiplier
argument_list|()
argument_list|,
literal|0.0001f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ndq
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeQueryXML
specifier|public
name|void
name|testRangeQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"RangeQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"RangeQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testUserQueryXML
specifier|public
name|void
name|testUserQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"UserInputQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"UserInput with Filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomFieldUserQueryXML
specifier|public
name|void
name|testCustomFieldUserQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"UserInputQueryCustomField.xml"
argument_list|)
decl_stmt|;
name|int
name|h
init|=
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|"UserInputQueryCustomField should produce 0 result "
argument_list|,
literal|0
argument_list|,
name|h
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostingTermQueryXML
specifier|public
name|void
name|testBoostingTermQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BoostingTermQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"BoostingTermQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanTermXML
specifier|public
name|void
name|testSpanTermXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"SpanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Span Query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|SpanQuery
name|sq
init|=
name|parseAsSpan
argument_list|(
literal|"SpanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Span Query"
argument_list|,
name|sq
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|sq
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstantScoreQueryXML
specifier|public
name|void
name|testConstantScoreQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"ConstantScoreQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"ConstantScoreQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testMatchAllDocsPlusFilterXML
specifier|public
name|void
name|testMatchAllDocsPlusFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"MatchAllDocsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"MatchAllDocsQuery with range filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedBooleanQuery
specifier|public
name|void
name|testNestedBooleanQuery
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"NestedBooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Nested Boolean query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testPointRangeQuery
specifier|public
name|void
name|testPointRangeQuery
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"PointRangeQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"PointRangeQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testPointRangeQueryWithoutLowerTerm
specifier|public
name|void
name|testPointRangeQueryWithoutLowerTerm
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"PointRangeQueryWithoutLowerTerm.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"PointRangeQueryWithoutLowerTerm"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testPointRangeQueryWithoutUpperTerm
specifier|public
name|void
name|testPointRangeQueryWithoutUpperTerm
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"PointRangeQueryWithoutUpperTerm.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"PointRangeQueryWithoutUpperTerm"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testPointRangeQueryWithoutRange
specifier|public
name|void
name|testPointRangeQueryWithoutRange
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"PointRangeQueryWithoutRange.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"PointRangeQueryWithoutRange"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|//================= Helper methods ===================================
DECL|method|defaultField
specifier|protected
name|String
name|defaultField
parameter_list|()
block|{
return|return
name|defaultField
return|;
block|}
DECL|method|analyzer
specifier|protected
name|Analyzer
name|analyzer
parameter_list|()
block|{
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|newAnalyzer
argument_list|()
expr_stmt|;
block|}
return|return
name|analyzer
return|;
block|}
DECL|method|coreParser
specifier|protected
name|CoreParser
name|coreParser
parameter_list|()
block|{
if|if
condition|(
name|coreParser
operator|==
literal|null
condition|)
block|{
name|coreParser
operator|=
name|newCoreParser
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|coreParser
return|;
block|}
DECL|method|indexData
specifier|private
name|CoreParserTestIndexData
name|indexData
parameter_list|()
block|{
if|if
condition|(
name|indexData
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|indexData
operator|=
operator|new
name|CoreParserTestIndexData
argument_list|(
name|analyzer
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
name|fail
argument_list|(
literal|"caught Exception "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indexData
return|;
block|}
DECL|method|reader
specifier|protected
name|IndexReader
name|reader
parameter_list|()
block|{
return|return
name|indexData
argument_list|()
operator|.
name|reader
return|;
block|}
DECL|method|searcher
specifier|protected
name|IndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|indexData
argument_list|()
operator|.
name|searcher
return|;
block|}
DECL|method|parseShouldFail
specifier|protected
name|void
name|parseShouldFail
parameter_list|(
name|String
name|xmlFileName
parameter_list|,
name|String
name|expectedParserExceptionMessage
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
literal|null
decl_stmt|;
name|ParserException
name|pe
init|=
literal|null
decl_stmt|;
try|try
block|{
name|q
operator|=
name|parse
argument_list|(
name|xmlFileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserException
name|e
parameter_list|)
block|{
name|pe
operator|=
name|e
expr_stmt|;
block|}
name|assertNull
argument_list|(
literal|"for "
operator|+
name|xmlFileName
operator|+
literal|" unexpectedly got "
operator|+
name|q
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"expected a ParserException for "
operator|+
name|xmlFileName
argument_list|,
name|pe
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected different ParserException for "
operator|+
name|xmlFileName
argument_list|,
name|expectedParserExceptionMessage
argument_list|,
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|protected
name|Query
name|parse
parameter_list|(
name|String
name|xmlFileName
parameter_list|)
throws|throws
name|ParserException
throws|,
name|IOException
block|{
return|return
name|implParse
argument_list|(
name|xmlFileName
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|parseAsSpan
specifier|protected
name|SpanQuery
name|parseAsSpan
parameter_list|(
name|String
name|xmlFileName
parameter_list|)
throws|throws
name|ParserException
throws|,
name|IOException
block|{
return|return
operator|(
name|SpanQuery
operator|)
name|implParse
argument_list|(
name|xmlFileName
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|implParse
specifier|private
name|Query
name|implParse
parameter_list|(
name|String
name|xmlFileName
parameter_list|,
name|boolean
name|span
parameter_list|)
throws|throws
name|ParserException
throws|,
name|IOException
block|{
try|try
init|(
name|InputStream
name|xmlStream
init|=
name|TestCoreParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|xmlFileName
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Test XML file "
operator|+
name|xmlFileName
operator|+
literal|" cannot be found"
argument_list|,
name|xmlStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|span
condition|)
block|{
return|return
name|coreParser
argument_list|()
operator|.
name|parseAsSpanQuery
argument_list|(
name|xmlStream
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|coreParser
argument_list|()
operator|.
name|parse
argument_list|(
name|xmlStream
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|rewrite
specifier|protected
name|Query
name|rewrite
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|()
argument_list|)
return|;
block|}
DECL|method|dumpResults
specifier|protected
name|void
name|dumpResults
parameter_list|(
name|String
name|qType
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: qType="
operator|+
name|qType
operator|+
literal|" numDocs="
operator|+
name|numDocs
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" query="
operator|+
name|q
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexSearcher
name|searcher
init|=
name|searcher
argument_list|()
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|producedResults
init|=
operator|(
name|hits
operator|.
name|totalHits
operator|>
literal|0
operator|)
decl_stmt|;
if|if
condition|(
operator|!
name|producedResults
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: qType="
operator|+
name|qType
operator|+
literal|" numDocs="
operator|+
name|numDocs
operator|+
literal|" "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" query="
operator|+
name|q
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|hits
operator|.
name|scoreDocs
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
name|Math
operator|.
name|min
argument_list|(
name|numDocs
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|ldoc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"date"
argument_list|)
operator|+
literal|"]"
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|qType
operator|+
literal|" produced no results"
argument_list|,
name|producedResults
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

