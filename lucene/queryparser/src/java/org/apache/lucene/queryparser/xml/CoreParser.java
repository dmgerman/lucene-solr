begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|xml
operator|.
name|builders
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Assembles a QueryBuilder which uses only core Lucene Query objects  */
end_comment

begin_class
DECL|class|CoreParser
specifier|public
class|class
name|CoreParser
implements|implements
name|QueryBuilder
block|{
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|parser
specifier|protected
name|QueryParser
name|parser
decl_stmt|;
DECL|field|queryFactory
specifier|protected
name|QueryBuilderFactory
name|queryFactory
decl_stmt|;
DECL|field|filterFactory
specifier|protected
name|FilterBuilderFactory
name|filterFactory
decl_stmt|;
comment|//Controls the max size of the LRU cache used for QueryFilter objects parsed.
DECL|field|maxNumCachedFilters
specifier|public
specifier|static
name|int
name|maxNumCachedFilters
init|=
literal|20
decl_stmt|;
comment|/**    * Construct an XML parser that uses a single instance QueryParser for handling    * UserQuery tags - all parse operations are synchronised on this parser    *    * @param parser A QueryParser which will be synchronized on during parse calls.    */
DECL|method|CoreParser
specifier|public
name|CoreParser
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|QueryParser
name|parser
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|analyzer
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an XML parser that creates a QueryParser for each UserQuery request.    *    * @param defaultField The default field name used by QueryParsers constructed for UserQuery tags    */
DECL|method|CoreParser
specifier|public
name|CoreParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CoreParser
specifier|protected
name|CoreParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|QueryParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|filterFactory
operator|=
operator|new
name|FilterBuilderFactory
argument_list|()
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"RangeFilter"
argument_list|,
operator|new
name|RangeFilterBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"NumericRangeFilter"
argument_list|,
operator|new
name|NumericRangeFilterBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|queryFactory
operator|=
operator|new
name|QueryBuilderFactory
argument_list|()
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"TermQuery"
argument_list|,
operator|new
name|TermQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"TermsQuery"
argument_list|,
operator|new
name|TermsQueryBuilder
argument_list|(
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"MatchAllDocsQuery"
argument_list|,
operator|new
name|MatchAllDocsQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"BooleanQuery"
argument_list|,
operator|new
name|BooleanQueryBuilder
argument_list|(
name|queryFactory
argument_list|)
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"NumericRangeQuery"
argument_list|,
operator|new
name|NumericRangeQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"DisjunctionMaxQuery"
argument_list|,
operator|new
name|DisjunctionMaxQueryBuilder
argument_list|(
name|queryFactory
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"UserQuery"
argument_list|,
operator|new
name|UserInputQueryBuilder
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"UserQuery"
argument_list|,
operator|new
name|UserInputQueryBuilder
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"FilteredQuery"
argument_list|,
operator|new
name|FilteredQueryBuilder
argument_list|(
name|filterFactory
argument_list|,
name|queryFactory
argument_list|)
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"ConstantScoreQuery"
argument_list|,
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|filterFactory
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"CachedFilter"
argument_list|,
operator|new
name|CachedFilterBuilder
argument_list|(
name|queryFactory
argument_list|,
name|filterFactory
argument_list|,
name|maxNumCachedFilters
argument_list|)
argument_list|)
expr_stmt|;
name|SpanQueryBuilderFactory
name|sqof
init|=
operator|new
name|SpanQueryBuilderFactory
argument_list|()
decl_stmt|;
name|SpanNearBuilder
name|snb
init|=
operator|new
name|SpanNearBuilder
argument_list|(
name|sqof
argument_list|)
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanNear"
argument_list|,
name|snb
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanNear"
argument_list|,
name|snb
argument_list|)
expr_stmt|;
name|BoostingTermBuilder
name|btb
init|=
operator|new
name|BoostingTermBuilder
argument_list|()
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"BoostingTermQuery"
argument_list|,
name|btb
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"BoostingTermQuery"
argument_list|,
name|btb
argument_list|)
expr_stmt|;
name|SpanTermBuilder
name|snt
init|=
operator|new
name|SpanTermBuilder
argument_list|()
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanTerm"
argument_list|,
name|snt
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanTerm"
argument_list|,
name|snt
argument_list|)
expr_stmt|;
name|SpanOrBuilder
name|sot
init|=
operator|new
name|SpanOrBuilder
argument_list|(
name|sqof
argument_list|)
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanOr"
argument_list|,
name|sot
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanOr"
argument_list|,
name|sot
argument_list|)
expr_stmt|;
name|SpanOrTermsBuilder
name|sots
init|=
operator|new
name|SpanOrTermsBuilder
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanOrTerms"
argument_list|,
name|sots
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanOrTerms"
argument_list|,
name|sots
argument_list|)
expr_stmt|;
name|SpanFirstBuilder
name|sft
init|=
operator|new
name|SpanFirstBuilder
argument_list|(
name|sqof
argument_list|)
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanFirst"
argument_list|,
name|sft
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanFirst"
argument_list|,
name|sft
argument_list|)
expr_stmt|;
name|SpanNotBuilder
name|snot
init|=
operator|new
name|SpanNotBuilder
argument_list|(
name|sqof
argument_list|)
decl_stmt|;
name|sqof
operator|.
name|addBuilder
argument_list|(
literal|"SpanNot"
argument_list|,
name|snot
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"SpanNot"
argument_list|,
name|snot
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|InputStream
name|xmlStream
parameter_list|)
throws|throws
name|ParserException
block|{
return|return
name|getQuery
argument_list|(
name|parseXML
argument_list|(
name|xmlStream
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addQueryBuilder
specifier|public
name|void
name|addQueryBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
block|{
name|queryFactory
operator|.
name|addBuilder
argument_list|(
name|nodeName
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|addFilterBuilder
specifier|public
name|void
name|addFilterBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|FilterBuilder
name|builder
parameter_list|)
block|{
name|filterFactory
operator|.
name|addBuilder
argument_list|(
name|nodeName
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|parseXML
specifier|private
specifier|static
name|Document
name|parseXML
parameter_list|(
name|InputStream
name|pXmlFile
parameter_list|)
throws|throws
name|ParserException
block|{
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"XML Parser configuration error"
argument_list|,
name|se
argument_list|)
throw|;
block|}
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|db
operator|.
name|parse
argument_list|(
name|pXmlFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"Error parsing XML stream:"
operator|+
name|se
argument_list|,
name|se
argument_list|)
throw|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
return|return
name|queryFactory
operator|.
name|getQuery
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

