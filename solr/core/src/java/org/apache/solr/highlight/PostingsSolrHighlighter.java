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
comment|/**   * Highlighter impl that uses {@link PostingsHighlighter}  *<p>  * Example configuration:  *<pre class="prettyprint">  *&lt;searchComponent class="solr.HighlightComponent" name="highlight"&gt;  *&lt;highlighting class="org.apache.solr.highlight.PostingsSolrHighlighter"  *                      preTag="&amp;lt;em&amp;gt;"  *                      postTag="&amp;lt;/em&amp;gt;"  *                      ellipsis="... "  *                      k1="1.2"  *                      b="0.75"  *                      pivot="87"  *                      maxLength=10000  *                      summarizeEmpty=true/&gt;  *&lt;/searchComponent&gt;  *</pre>  *<p>  * Notes:  *<ul>  *<li>fields to highlight must be configured with storeOffsetsWithPositions="true"  *<li>hl.fl specifies the field list.  *<li>hl.snippets specifies how many underlying sentence fragments form the resulting snippet.  *</ul>  *    * @lucene.experimental   */
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
DECL|field|highlighter
specifier|protected
name|PostingsHighlighter
name|highlighter
decl_stmt|;
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
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|info
operator|.
name|attributes
decl_stmt|;
name|BreakIterator
name|breakIterator
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
comment|// scorer parameters: k1/b/pivot
name|String
name|k1
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"k1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|k1
operator|==
literal|null
condition|)
block|{
name|k1
operator|=
literal|"1.2"
expr_stmt|;
block|}
name|String
name|b
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
name|b
operator|=
literal|"0.75"
expr_stmt|;
block|}
name|String
name|pivot
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"pivot"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pivot
operator|==
literal|null
condition|)
block|{
name|pivot
operator|=
literal|"87"
expr_stmt|;
block|}
name|PassageScorer
name|scorer
init|=
operator|new
name|PassageScorer
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|k1
argument_list|)
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|b
argument_list|)
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|pivot
argument_list|)
argument_list|)
decl_stmt|;
comment|// formatter parameters: preTag/postTag/ellipsis
name|String
name|preTag
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"preTag"
argument_list|)
decl_stmt|;
if|if
condition|(
name|preTag
operator|==
literal|null
condition|)
block|{
name|preTag
operator|=
literal|"<em>"
expr_stmt|;
block|}
name|String
name|postTag
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"postTag"
argument_list|)
decl_stmt|;
if|if
condition|(
name|postTag
operator|==
literal|null
condition|)
block|{
name|postTag
operator|=
literal|"</em>"
expr_stmt|;
block|}
name|String
name|ellipsis
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"ellipsis"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ellipsis
operator|==
literal|null
condition|)
block|{
name|ellipsis
operator|=
literal|"... "
expr_stmt|;
block|}
name|PassageFormatter
name|formatter
init|=
operator|new
name|PassageFormatter
argument_list|(
name|preTag
argument_list|,
name|postTag
argument_list|,
name|ellipsis
argument_list|)
decl_stmt|;
name|String
name|summarizeEmpty
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"summarizeEmpty"
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|summarizeEmptyBoolean
decl_stmt|;
if|if
condition|(
name|summarizeEmpty
operator|==
literal|null
condition|)
block|{
name|summarizeEmptyBoolean
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|summarizeEmptyBoolean
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|summarizeEmpty
argument_list|)
expr_stmt|;
block|}
comment|// maximum content size to process
name|int
name|maxLength
init|=
name|PostingsHighlighter
operator|.
name|DEFAULT_MAX_LENGTH
decl_stmt|;
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|"maxLength"
argument_list|)
condition|)
block|{
name|maxLength
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
literal|"maxLength"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|highlighter
operator|=
operator|new
name|PostingsHighlighter
argument_list|(
name|maxLength
argument_list|,
name|breakIterator
argument_list|,
name|scorer
argument_list|,
name|formatter
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
if|if
condition|(
name|summarizeEmptyBoolean
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
block|}
expr_stmt|;
block|}
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
name|numSnippets
init|=
name|params
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
literal|1
argument_list|)
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
name|numSnippets
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
block|}
end_class

end_unit

