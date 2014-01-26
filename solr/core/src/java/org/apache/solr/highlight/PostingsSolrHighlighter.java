begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|text
operator|.
name|BreakIterator
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
name|Locale
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
name|index
operator|.
name|StoredDocument
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
name|postingshighlight
operator|.
name|DefaultPassageFormatter
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
name|postingshighlight
operator|.
name|Passage
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
name|postingshighlight
operator|.
name|PassageFormatter
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
name|postingshighlight
operator|.
name|PassageScorer
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
name|postingshighlight
operator|.
name|PostingsHighlighter
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
name|postingshighlight
operator|.
name|WholeBreakIterator
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
name|PluginInfo
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
name|plugin
operator|.
name|PluginInfoInitialized
import|;
end_import

begin_comment
comment|/**   * Highlighter impl that uses {@link PostingsHighlighter}  *<p>  * Example configuration:  *<pre class="prettyprint">  *&lt;requestHandler name="standard" class="solr.StandardRequestHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;int name="hl.snippets"&gt;1&lt;/int&gt;  *&lt;str name="hl.tag.pre"&gt;&amp;lt;em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.tag.post"&gt;&amp;lt;/em&amp;gt;&lt;/str&gt;  *&lt;str name="hl.tag.ellipsis"&gt;...&lt;/str&gt;  *&lt;bool name="hl.defaultSummary"&gt;true&lt;/bool&gt;  *&lt;str name="hl.encoder"&gt;simple&lt;/str&gt;  *&lt;float name="hl.score.k1"&gt;1.2&lt;/float&gt;  *&lt;float name="hl.score.b"&gt;0.75&lt;/float&gt;  *&lt;float name="hl.score.pivot"&gt;87&lt;/float&gt;  *&lt;str name="hl.bs.language"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.country"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.variant"&gt;&lt;/str&gt;  *&lt;str name="hl.bs.type"&gt;SENTENCE&lt;/str&gt;  *&lt;int name="hl.maxAnalyzedChars"&gt;10000&lt;/int&gt;  *&lt;str name="hl.multiValuedSeparatorChar"&gt;&lt;/str&gt;  *&lt;bool name="hl.highlightMultiTerm"&gt;false&lt;/bool&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  * ...  *<pre class="prettyprint">  *&lt;searchComponent class="solr.HighlightComponent" name="highlight"&gt;  *&lt;highlighting class="org.apache.solr.highlight.PostingsSolrHighlighter"/&gt;  *&lt;/searchComponent&gt;  *</pre>  *<p>  * Notes:  *<ul>  *<li>fields to highlight must be configured with storeOffsetsWithPositions="true"  *<li>hl.q (string) can specify the query  *<li>hl.fl (string) specifies the field list.  *<li>hl.snippets (int) specifies how many underlying passages form the resulting snippet.  *<li>hl.tag.pre (string) specifies text which appears before a highlighted term.  *<li>hl.tag.post (string) specifies text which appears after a highlighted term.  *<li>hl.tag.ellipsis (string) specifies text which joins non-adjacent passages.  *<li>hl.defaultSummary (bool) specifies if a field should have a default summary.  *<li>hl.encoder (string) can be 'html' (html escapes content) or 'simple' (no escaping).  *<li>hl.score.k1 (float) specifies bm25 scoring parameter 'k1'  *<li>hl.score.b (float) specifies bm25 scoring parameter 'b'  *<li>hl.score.pivot (float) specifies bm25 scoring parameter 'avgdl'  *<li>hl.bs.type (string) specifies how to divide text into passages: [SENTENCE, LINE, WORD, CHAR, WHOLE]  *<li>hl.bs.language (string) specifies language code for BreakIterator. default is empty string (root locale)  *<li>hl.bs.country (string) specifies country code for BreakIterator. default is empty string (root locale)  *<li>hl.bs.variant (string) specifies country code for BreakIterator. default is empty string (root locale)  *<li>hl.maxAnalyzedChars specifies how many characters at most will be processed in a document.  *<li>hl.multiValuedSeparatorChar specifies the logical separator between values for multi-valued fields.  *<li>hl.highlightMultiTerm enables highlighting for range/wildcard/fuzzy/prefix queries.  *        NOTE: currently hl.maxAnalyzedChars cannot yet be specified per-field  *</ul>  *    * @lucene.experimental   */
end_comment

begin_class
DECL|class|PostingsSolrHighlighter
specifier|public
class|class
name|PostingsSolrHighlighter
extends|extends
name|SolrHighlighter
implements|implements
name|PluginInfoInitialized
block|{
annotation|@
name|Override
DECL|method|initalize
specifier|public
name|void
name|initalize
parameter_list|(
name|SolrConfig
name|config
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{}
annotation|@
name|Override
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
specifier|final
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
comment|// if highlighting isnt enabled, then why call doHighlighting?
if|if
condition|(
name|isHighlightingEnabled
argument_list|(
name|params
argument_list|)
condition|)
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|int
index|[]
name|docIDs
init|=
name|toDocIDs
argument_list|(
name|docs
argument_list|)
decl_stmt|;
comment|// fetch the unique keys
name|String
index|[]
name|keys
init|=
name|getUniqueKeys
argument_list|(
name|searcher
argument_list|,
name|docIDs
argument_list|)
decl_stmt|;
comment|// query-time parameters
name|int
name|maxLength
init|=
name|params
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|MAX_CHARS
argument_list|,
name|PostingsHighlighter
operator|.
name|DEFAULT_MAX_LENGTH
argument_list|)
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
name|int
name|maxPassages
index|[]
init|=
operator|new
name|int
index|[
name|fieldNames
operator|.
name|length
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
name|fieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|maxPassages
index|[
name|i
index|]
operator|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldNames
index|[
name|i
index|]
argument_list|,
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|PostingsHighlighter
name|highlighter
init|=
operator|new
name|PostingsHighlighter
argument_list|(
name|maxLength
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Passage
index|[]
name|getEmptyHighlight
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BreakIterator
name|bi
parameter_list|,
name|int
name|maxPassages
parameter_list|)
block|{
name|boolean
name|defaultSummary
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|DEFAULT_SUMMARY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultSummary
condition|)
block|{
return|return
name|super
operator|.
name|getEmptyHighlight
argument_list|(
name|fieldName
argument_list|,
name|bi
argument_list|,
name|maxPassages
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Passage
index|[
literal|0
index|]
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|PassageFormatter
name|getFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|String
name|preTag
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_PRE
argument_list|,
literal|"<em>"
argument_list|)
decl_stmt|;
name|String
name|postTag
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_POST
argument_list|,
literal|"</em>"
argument_list|)
decl_stmt|;
name|String
name|ellipsis
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|TAG_ELLIPSIS
argument_list|,
literal|"... "
argument_list|)
decl_stmt|;
name|String
name|encoder
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|ENCODER
argument_list|,
literal|"simple"
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultPassageFormatter
argument_list|(
name|preTag
argument_list|,
name|postTag
argument_list|,
name|ellipsis
argument_list|,
literal|"html"
operator|.
name|equals
argument_list|(
name|encoder
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PassageScorer
name|getScorer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|float
name|k1
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_K1
argument_list|,
literal|1.2f
argument_list|)
decl_stmt|;
name|float
name|b
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_B
argument_list|,
literal|0.75f
argument_list|)
decl_stmt|;
name|float
name|pivot
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SCORE_PIVOT
argument_list|,
literal|87f
argument_list|)
decl_stmt|;
return|return
operator|new
name|PassageScorer
argument_list|(
name|k1
argument_list|,
name|b
argument_list|,
name|pivot
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|String
name|language
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_LANGUAGE
argument_list|)
decl_stmt|;
name|String
name|country
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_COUNTRY
argument_list|)
decl_stmt|;
name|String
name|variant
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_VARIANT
argument_list|)
decl_stmt|;
name|Locale
name|locale
init|=
name|parseLocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|BS_TYPE
argument_list|)
decl_stmt|;
return|return
name|parseBreakIterator
argument_list|(
name|type
argument_list|,
name|locale
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|char
name|getMultiValuedSeparator
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|String
name|sep
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|MULTI_VALUED_SEPARATOR
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|sep
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|HighlightParams
operator|.
name|MULTI_VALUED_SEPARATOR
operator|+
literal|" must be exactly one character."
argument_list|)
throw|;
block|}
return|return
name|sep
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Analyzer
name|getIndexAnalyzer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|HighlightParams
operator|.
name|HIGHLIGHT_MULTI_TERM
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|schema
operator|.
name|getAnalyzer
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|snippets
init|=
name|highlighter
operator|.
name|highlightFields
argument_list|(
name|fieldNames
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|docIDs
argument_list|,
name|maxPassages
argument_list|)
decl_stmt|;
return|return
name|encodeSnippets
argument_list|(
name|keys
argument_list|,
name|fieldNames
argument_list|,
name|snippets
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**     * Encodes the resulting snippets into a namedlist    * @param keys the document unique keys    * @param fieldNames field names to highlight in the order    * @param snippets map from field name to snippet array for the docs    * @return encoded namedlist of summaries    */
DECL|method|encodeSnippets
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|encodeSnippets
parameter_list|(
name|String
index|[]
name|keys
parameter_list|,
name|String
index|[]
name|fieldNames
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|snippets
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|summary
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldNames
control|)
block|{
name|String
name|snippet
init|=
name|snippets
operator|.
name|get
argument_list|(
name|field
argument_list|)
index|[
name|i
index|]
decl_stmt|;
comment|// box in an array to match the format of existing highlighters,
comment|// even though its always one element.
if|if
condition|(
name|snippet
operator|==
literal|null
condition|)
block|{
name|summary
operator|.
name|add
argument_list|(
name|field
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|summary
operator|.
name|add
argument_list|(
name|field
argument_list|,
operator|new
name|String
index|[]
block|{
name|snippet
block|}
argument_list|)
expr_stmt|;
block|}
block|}
name|list
operator|.
name|add
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/** Converts solr's DocList to the int[] docIDs */
DECL|method|toDocIDs
specifier|protected
name|int
index|[]
name|toDocIDs
parameter_list|(
name|DocList
name|docs
parameter_list|)
block|{
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
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
name|docIDs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|docIDs
index|[
name|i
index|]
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
name|docIDs
return|;
block|}
comment|/** Retrieves the unique keys for the topdocs to key the results */
DECL|method|getUniqueKeys
specifier|protected
name|String
index|[]
name|getUniqueKeys
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
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
name|keyField
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|selector
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|uniqueKeys
index|[]
init|=
operator|new
name|String
index|[
name|docIDs
operator|.
name|length
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
name|docIDs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docid
init|=
name|docIDs
index|[
name|i
index|]
decl_stmt|;
name|StoredDocument
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|docid
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|uniqueKeys
index|[
name|i
index|]
operator|=
name|id
expr_stmt|;
block|}
return|return
name|uniqueKeys
return|;
block|}
else|else
block|{
return|return
operator|new
name|String
index|[
name|docIDs
operator|.
name|length
index|]
return|;
block|}
block|}
comment|/** parse a break iterator type for the specified locale */
DECL|method|parseBreakIterator
specifier|protected
name|BreakIterator
name|parseBreakIterator
parameter_list|(
name|String
name|type
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
literal|"SENTENCE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"LINE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getLineInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"WORD"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"CHARACTER"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BreakIterator
operator|.
name|getCharacterInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"WHOLE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|new
name|WholeBreakIterator
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown "
operator|+
name|HighlightParams
operator|.
name|BS_TYPE
operator|+
literal|": "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/** parse a locale from a language+country+variant spec */
DECL|method|parseLocale
specifier|protected
name|Locale
name|parseLocale
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|country
parameter_list|,
name|String
name|variant
parameter_list|)
block|{
if|if
condition|(
name|language
operator|==
literal|null
operator|&&
name|country
operator|==
literal|null
operator|&&
name|variant
operator|==
literal|null
condition|)
block|{
return|return
name|Locale
operator|.
name|ROOT
return|;
block|}
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|==
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"To specify variant, country is required"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

