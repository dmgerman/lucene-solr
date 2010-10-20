begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|spans
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|javax
operator|.
name|management
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|OrQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|parser
operator|.
name|SyntaxParser
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
name|queryParser
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorPipeline
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
name|queryParser
operator|.
name|standard
operator|.
name|parser
operator|.
name|StandardSyntaxParser
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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

begin_comment
comment|/**  * This test case demonstrates how the new query parser can be used.<br/>  *<br/>  *   * It tests queries likes "term", "field:term" "term1 term2" "term1 OR term2",  * which are all already supported by the current syntax parser (  * {@link StandardSyntaxParser}).<br/>  *<br/>  *   * The goals is to create a new query parser that supports only the pair  * "field:term" or a list of pairs separated or not by an OR operator, and from  * this query generate {@link SpanQuery} objects instead of the regular  * {@link Query} objects. Basically, every pair will be converted to a  * {@link SpanTermQuery} object and if there are more than one pair they will be  * grouped by an {@link OrQueryNode}.<br/>  *<br/>  *   * Another functionality that will be added is the ability to convert every  * field defined in the query to an unique specific field.<br/>  *<br/>  *   * The query generation is divided in three different steps: parsing (syntax),  * processing (semantic) and building.<br/>  *<br/>  *   * The parsing phase, as already mentioned will be performed by the current  * query parser: {@link StandardSyntaxParser}.<br/>  *<br/>  *   * The processing phase will be performed by a processor pipeline which is  * compound by 2 processors: {@link SpansValidatorQueryNodeProcessor} and  * {@link UniqueFieldQueryNodeProcessor}.  *   *<pre>  *   *   {@link SpansValidatorQueryNodeProcessor}: as it's going to use the current   *   query parser to parse the syntax, it will support more features than we want,  *   this processor basically validates the query node tree generated by the parser  *   and just let got through the elements we want, all the other elements as   *   wildcards, range queries, etc...if found, an exception is thrown.  *     *   {@link UniqueFieldQueryNodeProcessor}: this processor will take care of reading  *   what is the&quot;unique field&quot; from the configuration and convert every field defined  *   in every pair to this&quot;unique field&quot;. For that, a {@link SpansQueryConfigHandler} is  *   used, which has the {@link UniqueFieldAttribute} defined in it.  *</pre>  *   * The building phase is performed by the {@link SpansQueryTreeBuilder}, which  * basically contains a map that defines which builder will be used to generate  * {@link SpanQuery} objects from {@link QueryNode} objects.<br/>  *<br/>  *   * @see TestSpanQueryParser for a more advanced example  *   * @see SpansQueryConfigHandler  * @see SpansQueryTreeBuilder  * @see SpansValidatorQueryNodeProcessor  * @see SpanOrQueryNodeBuilder  * @see SpanTermQueryNodeBuilder  * @see StandardSyntaxParser  * @see UniqueFieldQueryNodeProcessor  * @see UniqueFieldAttribute  *   */
end_comment

begin_class
DECL|class|TestSpanQueryParserSimpleSample
specifier|public
class|class
name|TestSpanQueryParserSimpleSample
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasicDemo
specifier|public
name|void
name|testBasicDemo
parameter_list|()
throws|throws
name|Exception
block|{
name|SyntaxParser
name|queryParser
init|=
operator|new
name|StandardSyntaxParser
argument_list|()
decl_stmt|;
comment|// convert the CharSequence into a QueryNode tree
name|QueryNode
name|queryTree
init|=
name|queryParser
operator|.
name|parse
argument_list|(
literal|"body:text"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// create a config handler with a attribute used in
comment|// UniqueFieldQueryNodeProcessor
name|QueryConfigHandler
name|spanQueryConfigHandler
init|=
operator|new
name|SpansQueryConfigHandler
argument_list|()
decl_stmt|;
name|UniqueFieldAttribute
name|uniqueFieldAtt
init|=
name|spanQueryConfigHandler
operator|.
name|getAttribute
argument_list|(
name|UniqueFieldAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|uniqueFieldAtt
operator|.
name|setUniqueField
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
comment|// set up the processor pipeline with the ConfigHandler
comment|// and create the pipeline for this simple demo
name|QueryNodeProcessorPipeline
name|spanProcessorPipeline
init|=
operator|new
name|QueryNodeProcessorPipeline
argument_list|(
name|spanQueryConfigHandler
argument_list|)
decl_stmt|;
comment|// @see SpansValidatorQueryNodeProcessor
name|spanProcessorPipeline
operator|.
name|add
argument_list|(
operator|new
name|SpansValidatorQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
comment|// @see UniqueFieldQueryNodeProcessor
name|spanProcessorPipeline
operator|.
name|add
argument_list|(
operator|new
name|UniqueFieldQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
comment|// print to show out the QueryNode tree before being processed
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
comment|// Process the QueryTree using our new Processors
name|queryTree
operator|=
name|spanProcessorPipeline
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
comment|// print to show out the QueryNode tree after being processed
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
comment|// create a instance off the Builder
name|SpansQueryTreeBuilder
name|spansQueryTreeBuilder
init|=
operator|new
name|SpansQueryTreeBuilder
argument_list|()
decl_stmt|;
comment|// convert QueryNode tree to span query Objects
name|SpanQuery
name|spanquery
init|=
name|spansQueryTreeBuilder
operator|.
name|build
argument_list|(
name|queryTree
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|spanquery
operator|instanceof
name|SpanTermQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|spanquery
operator|.
name|toString
argument_list|()
argument_list|,
literal|"index:text"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

