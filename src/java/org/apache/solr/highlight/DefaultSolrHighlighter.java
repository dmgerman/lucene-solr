begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|StringReader
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ListIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CachingTokenFilter
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
name|Token
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
name|TokenFilter
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
name|TokenStream
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
name|highlight
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
name|common
operator|.
name|SolrException
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
name|ResourceLoader
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
name|HighlightParams
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
name|SolrParams
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
name|core
operator|.
name|SolrConfig
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|DocIterator
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|DefaultSolrHighlighter
specifier|public
class|class
name|DefaultSolrHighlighter
extends|extends
name|SolrHighlighter
block|{
DECL|method|initalize
specifier|public
name|void
name|initalize
parameter_list|(
name|SolrConfig
name|config
parameter_list|)
block|{
name|formatters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fragmenters
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Load the fragmenters
name|ResourceLoader
name|loader
init|=
name|config
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|SolrFragmenter
name|frag
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SolrConfig
operator|.
name|PluginInfo
name|info
range|:
name|config
operator|.
name|getHighlightingFragmenterInfo
argument_list|()
control|)
block|{
name|SolrFragmenter
name|fragmenter
init|=
operator|(
name|SolrFragmenter
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|info
operator|.
name|className
argument_list|)
decl_stmt|;
name|fragmenter
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|isDefault
condition|)
name|frag
operator|=
name|fragmenter
expr_stmt|;
name|fragmenters
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|fragmenter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|frag
operator|==
literal|null
condition|)
block|{
name|frag
operator|=
operator|new
name|GapFragmenter
argument_list|()
expr_stmt|;
block|}
name|fragmenters
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|frag
argument_list|)
expr_stmt|;
name|fragmenters
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|frag
argument_list|)
expr_stmt|;
comment|// Load the formatters
name|SolrFormatter
name|fmt
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SolrConfig
operator|.
name|PluginInfo
name|info
range|:
name|config
operator|.
name|getHighlightingFormatterInfo
argument_list|()
control|)
block|{
name|SolrFormatter
name|formatter
init|=
operator|(
name|SolrFormatter
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|info
operator|.
name|className
argument_list|)
decl_stmt|;
name|formatter
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
name|formatters
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|formatter
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|isDefault
condition|)
name|fmt
operator|=
name|formatter
expr_stmt|;
block|}
if|if
condition|(
name|fmt
operator|==
literal|null
condition|)
block|{
name|fmt
operator|=
operator|new
name|HtmlFormatter
argument_list|()
expr_stmt|;
block|}
name|formatters
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|fmt
argument_list|)
expr_stmt|;
name|formatters
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|fmt
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a phrase Highlighter appropriate for this field.    * @param query The current Query    * @param fieldName The name of the field    * @param request The current SolrQueryRequest    * @param tokenStream document text CachingTokenStream    * @throws IOException     */
DECL|method|getPhraseHighlighter
specifier|protected
name|Highlighter
name|getPhraseHighlighter
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|CachingTokenFilter
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|Highlighter
name|highlighter
init|=
literal|null
decl_stmt|;
name|highlighter
operator|=
operator|new
name|Highlighter
argument_list|(
name|getFormatter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
argument_list|,
name|getSpanQueryScorer
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|tokenStream
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|highlighter
operator|.
name|setTextFragmenter
argument_list|(
name|getFragmenter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|highlighter
return|;
block|}
comment|/**    * Return a Highlighter appropriate for this field.    * @param query The current Query    * @param fieldName The name of the field    * @param request The current SolrQueryRequest    */
DECL|method|getHighlighter
specifier|protected
name|Highlighter
name|getHighlighter
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
name|getFormatter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
argument_list|,
name|getQueryScorer
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|request
argument_list|)
argument_list|)
decl_stmt|;
name|highlighter
operator|.
name|setTextFragmenter
argument_list|(
name|getFragmenter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|highlighter
return|;
block|}
comment|/**    * Return a SpanScorer suitable for this Query and field.    * @param query The current query    * @param tokenStream document text CachingTokenStream    * @param fieldName The name of the field    * @param request The SolrQueryRequest    * @throws IOException     */
DECL|method|getSpanQueryScorer
specifier|private
name|SpanScorer
name|getSpanQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|CachingTokenFilter
name|tokenStream
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|reqFieldMatch
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Boolean
name|highlightMultiTerm
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT_MULTI_TERM
argument_list|)
decl_stmt|;
if|if
condition|(
name|highlightMultiTerm
operator|==
literal|null
condition|)
block|{
name|highlightMultiTerm
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|reqFieldMatch
condition|)
block|{
return|return
operator|new
name|SpanScorer
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|tokenStream
argument_list|,
name|highlightMultiTerm
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SpanScorer
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|tokenStream
argument_list|,
name|highlightMultiTerm
argument_list|)
return|;
block|}
block|}
comment|/**    * Return a QueryScorer suitable for this Query and field.    * @param query The current query    * @param fieldName The name of the field    * @param request The SolrQueryRequest    */
DECL|method|getQueryScorer
specifier|protected
name|QueryScorer
name|getQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|boolean
name|reqFieldMatch
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqFieldMatch
condition|)
block|{
return|return
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|,
name|request
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
comment|/**    * Return the max number of snippets for this field. If this has not    * been configured for this field, fall back to the configured default    * or the solr default.    * @param fieldName The name of the field    * @param params The params controlling Highlighting    */
DECL|method|getMaxSnippets
specifier|protected
name|int
name|getMaxSnippets
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Return whether adjacent fragments should be merged.    * @param fieldName The name of the field    * @param params The params controlling Highlighting    */
DECL|method|isMergeContiguousFragments
specifier|protected
name|boolean
name|isMergeContiguousFragments
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|MERGE_CONTIGUOUS_FRAGMENTS
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Return a formatter appropriate for this field. If a formatter    * has not been configured for this field, fall back to the configured    * default or the solr default (SimpleHTMLFormatter).    *     * @param fieldName The name of the field    * @param params The params controlling Highlighting    * @return An appropriate Formatter.    */
DECL|method|getFormatter
specifier|protected
name|Formatter
name|getFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|String
name|str
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FORMATTER
argument_list|)
decl_stmt|;
name|SolrFormatter
name|formatter
init|=
name|formatters
operator|.
name|get
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown formatter: "
operator|+
name|str
argument_list|)
throw|;
block|}
return|return
name|formatter
operator|.
name|getFormatter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
return|;
block|}
comment|/**    * Return a fragmenter appropriate for this field. If a fragmenter    * has not been configured for this field, fall back to the configured    * default or the solr default (GapFragmenter).    *     * @param fieldName The name of the field    * @param params The params controlling Highlighting    * @return An appropriate Fragmenter.    */
DECL|method|getFragmenter
specifier|protected
name|Fragmenter
name|getFragmenter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|String
name|fmt
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FRAGMENTER
argument_list|)
decl_stmt|;
name|SolrFragmenter
name|frag
init|=
name|fragmenters
operator|.
name|get
argument_list|(
name|fmt
argument_list|)
decl_stmt|;
if|if
condition|(
name|frag
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown fragmenter: "
operator|+
name|fmt
argument_list|)
throw|;
block|}
return|return
name|frag
operator|.
name|getFragmenter
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
return|;
block|}
comment|/**    * Generates a list of Highlighted query fragments for each item in a list    * of documents, or returns null if highlighting is disabled.    *    * @param docs query results    * @param query the query    * @param req the current request    * @param defaultFields default list of fields to summarize    *    * @return NamedList containing a NamedList for each document, which in     * turns contains sets (field, summary) pairs.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doHighlighting
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|doHighlighting
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isHighlightingEnabled
argument_list|(
name|params
argument_list|)
condition|)
return|return
literal|null
return|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|NamedList
name|fragments
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|String
index|[]
name|fieldNames
init|=
name|getHighlightFields
argument_list|(
name|query
argument_list|,
name|req
argument_list|,
name|defaultFields
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fset
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
block|{
comment|// pre-fetch documents using the Searcher's doc cache
for|for
control|(
name|String
name|f
range|:
name|fieldNames
control|)
block|{
name|fset
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|// fetch unique key if one exists.
name|SchemaField
name|keyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|keyField
condition|)
name|fset
operator|.
name|add
argument_list|(
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Highlight each document
name|DocIterator
name|iterator
init|=
name|docs
operator|.
name|iterator
argument_list|()
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docId
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|docId
argument_list|,
name|fset
argument_list|)
decl_stmt|;
name|NamedList
name|docSummaries
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|fieldName
operator|=
name|fieldName
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
index|[]
name|docTexts
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|docTexts
operator|==
literal|null
condition|)
continue|continue;
name|TokenStream
name|tstream
init|=
literal|null
decl_stmt|;
name|int
name|numFragments
init|=
name|getMaxSnippets
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|boolean
name|mergeContiguousFragments
init|=
name|isMergeContiguousFragments
argument_list|(
name|fieldName
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|String
index|[]
name|summaries
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|TextFragment
argument_list|>
name|frags
init|=
operator|new
name|ArrayList
argument_list|<
name|TextFragment
argument_list|>
argument_list|()
decl_stmt|;
name|TermOffsetsTokenStream
name|tots
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docTexts
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// create TokenStream
try|try
block|{
comment|// attempt term vectors
if|if
condition|(
name|tots
operator|==
literal|null
condition|)
name|tots
operator|=
operator|new
name|TermOffsetsTokenStream
argument_list|(
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|searcher
operator|.
name|getReader
argument_list|()
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|tstream
operator|=
name|tots
operator|.
name|getMultiValuedTokenStream
argument_list|(
name|docTexts
index|[
name|j
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// fall back to anaylzer
name|tstream
operator|=
operator|new
name|TokenOrderingFilter
argument_list|(
name|schema
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|docTexts
index|[
name|j
index|]
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|Highlighter
name|highlighter
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|USE_PHRASE_HIGHLIGHTER
argument_list|)
argument_list|)
condition|)
block|{
comment|// wrap CachingTokenFilter around TokenStream for reuse
name|tstream
operator|=
operator|new
name|CachingTokenFilter
argument_list|(
name|tstream
argument_list|)
expr_stmt|;
comment|// get highlighter
name|highlighter
operator|=
name|getPhraseHighlighter
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|req
argument_list|,
operator|(
name|CachingTokenFilter
operator|)
name|tstream
argument_list|)
expr_stmt|;
comment|// after highlighter initialization, reset tstream since construction of highlighter already used it
name|tstream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// use "the old way"
name|highlighter
operator|=
name|getHighlighter
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
name|int
name|maxCharsToAnalyze
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|MAX_CHARS
argument_list|,
name|Highlighter
operator|.
name|DEFAULT_MAX_CHARS_TO_ANALYZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxCharsToAnalyze
operator|<
literal|0
condition|)
block|{
name|highlighter
operator|.
name|setMaxDocCharsToAnalyze
argument_list|(
name|docTexts
index|[
name|j
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|highlighter
operator|.
name|setMaxDocCharsToAnalyze
argument_list|(
name|maxCharsToAnalyze
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TextFragment
index|[]
name|bestTextFragments
init|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|tstream
argument_list|,
name|docTexts
index|[
name|j
index|]
argument_list|,
name|mergeContiguousFragments
argument_list|,
name|numFragments
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|bestTextFragments
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|bestTextFragments
index|[
name|k
index|]
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|bestTextFragments
index|[
name|k
index|]
operator|.
name|getScore
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|frags
operator|.
name|add
argument_list|(
name|bestTextFragments
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidTokenOffsetsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// sort such that the fragments with the highest score come first
name|Collections
operator|.
name|sort
argument_list|(
name|frags
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TextFragment
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|TextFragment
name|arg0
parameter_list|,
name|TextFragment
name|arg1
parameter_list|)
block|{
return|return
name|Math
operator|.
name|round
argument_list|(
name|arg1
operator|.
name|getScore
argument_list|()
operator|-
name|arg0
operator|.
name|getScore
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// convert fragments back into text
comment|// TODO: we can include score and position information in output as snippet attributes
if|if
condition|(
name|frags
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fragTexts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TextFragment
name|fragment
range|:
name|frags
control|)
block|{
if|if
condition|(
operator|(
name|fragment
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|fragment
operator|.
name|getScore
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fragTexts
operator|.
name|add
argument_list|(
name|fragment
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fragTexts
operator|.
name|size
argument_list|()
operator|>=
name|numFragments
condition|)
break|break;
block|}
name|summaries
operator|=
name|fragTexts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|summaries
operator|.
name|length
operator|>
literal|0
condition|)
name|docSummaries
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|summaries
argument_list|)
expr_stmt|;
block|}
comment|// no summeries made, copy text from alternate field
if|if
condition|(
name|summaries
operator|==
literal|null
operator|||
name|summaries
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|String
name|alternateField
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|ALTERNATE_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|alternateField
operator|!=
literal|null
operator|&&
name|alternateField
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|altTexts
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|alternateField
argument_list|)
decl_stmt|;
if|if
condition|(
name|altTexts
operator|!=
literal|null
operator|&&
name|altTexts
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|alternateFieldLen
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|ALTERNATE_FIELD_LENGTH
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|alternateFieldLen
operator|<=
literal|0
condition|)
block|{
name|docSummaries
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|altTexts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|altList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|altText
range|:
name|altTexts
control|)
block|{
name|altList
operator|.
name|add
argument_list|(
name|len
operator|+
name|altText
operator|.
name|length
argument_list|()
operator|>
name|alternateFieldLen
condition|?
operator|new
name|String
argument_list|(
name|altText
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|alternateFieldLen
operator|-
name|len
argument_list|)
argument_list|)
else|:
name|altText
argument_list|)
expr_stmt|;
name|len
operator|+=
name|altText
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|len
operator|>=
name|alternateFieldLen
condition|)
break|break;
block|}
name|docSummaries
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|altList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|String
name|printId
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
name|printId
operator|==
literal|null
condition|?
literal|null
else|:
name|printId
argument_list|,
name|docSummaries
argument_list|)
expr_stmt|;
block|}
return|return
name|fragments
return|;
block|}
block|}
end_class

begin_comment
comment|/** Orders Tokens in a window first by their startOffset ascending.  * endOffset is currently ignored.  * This is meant to work around fickleness in the highlighter only.  It  * can mess up token positions and should not be used for indexing or querying.  */
end_comment

begin_class
DECL|class|TokenOrderingFilter
class|class
name|TokenOrderingFilter
extends|extends
name|TokenFilter
block|{
DECL|field|windowSize
specifier|private
specifier|final
name|int
name|windowSize
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Token
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|method|TokenOrderingFilter
specifier|protected
name|TokenOrderingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|windowSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|windowSize
operator|=
name|windowSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
operator|!
name|done
operator|&&
name|queue
operator|.
name|size
argument_list|()
operator|<
name|windowSize
condition|)
block|{
name|Token
name|newTok
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|newTok
operator|==
literal|null
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
comment|// reverse iterating for better efficiency since we know the
comment|// list is already sorted, and most token start offsets will be too.
name|ListIterator
argument_list|<
name|Token
argument_list|>
name|iter
init|=
name|queue
operator|.
name|listIterator
argument_list|(
name|queue
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasPrevious
argument_list|()
condition|)
block|{
if|if
condition|(
name|newTok
operator|.
name|startOffset
argument_list|()
operator|>=
name|iter
operator|.
name|previous
argument_list|()
operator|.
name|startOffset
argument_list|()
condition|)
block|{
comment|// insertion will be before what next() would return (what
comment|// we just compared against), so move back one so the insertion
comment|// will be after.
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|iter
operator|.
name|add
argument_list|(
name|newTok
argument_list|)
expr_stmt|;
block|}
return|return
name|queue
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|queue
operator|.
name|removeFirst
argument_list|()
return|;
block|}
block|}
end_class

begin_class
DECL|class|TermOffsetsTokenStream
class|class
name|TermOffsetsTokenStream
block|{
DECL|field|bufferedTokenStream
name|TokenStream
name|bufferedTokenStream
init|=
literal|null
decl_stmt|;
DECL|field|bufferedToken
name|Token
name|bufferedToken
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|method|TermOffsetsTokenStream
specifier|public
name|TermOffsetsTokenStream
parameter_list|(
name|TokenStream
name|tstream
parameter_list|)
block|{
name|bufferedTokenStream
operator|=
name|tstream
expr_stmt|;
name|startOffset
operator|=
literal|0
expr_stmt|;
name|bufferedToken
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getMultiValuedTokenStream
specifier|public
name|TokenStream
name|getMultiValuedTokenStream
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
name|endOffset
operator|=
name|startOffset
operator|+
name|length
expr_stmt|;
return|return
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|token
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|bufferedToken
operator|==
literal|null
condition|)
name|bufferedToken
operator|=
name|bufferedTokenStream
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufferedToken
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|startOffset
operator|<=
name|bufferedToken
operator|.
name|startOffset
argument_list|()
operator|&&
name|bufferedToken
operator|.
name|endOffset
argument_list|()
operator|<=
name|endOffset
condition|)
block|{
name|token
operator|=
name|bufferedToken
expr_stmt|;
name|bufferedToken
operator|=
literal|null
expr_stmt|;
name|token
operator|.
name|setStartOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|token
operator|.
name|setEndOffset
argument_list|(
name|token
operator|.
name|endOffset
argument_list|()
operator|-
name|startOffset
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
elseif|else
if|if
condition|(
name|bufferedToken
operator|.
name|endOffset
argument_list|()
operator|>
name|endOffset
condition|)
block|{
name|startOffset
operator|+=
name|length
operator|+
literal|1
expr_stmt|;
return|return
literal|null
return|;
block|}
name|bufferedToken
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

