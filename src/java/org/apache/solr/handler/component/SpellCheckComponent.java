begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|SpellingParams
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
name|core
operator|.
name|SolrCore
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
name|SolrEventListener
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
name|SolrResourceLoader
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
name|FieldType
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
name|spelling
operator|.
name|AbstractLuceneSpellChecker
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
name|spelling
operator|.
name|IndexBasedSpellChecker
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
name|spelling
operator|.
name|QueryConverter
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
name|spelling
operator|.
name|SolrSpellChecker
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
name|spelling
operator|.
name|SpellingResult
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
name|RefCounted
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
name|NamedListPluginLoader
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
name|SolrCoreAware
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * A SearchComponent implementation which provides support for spell checking  * and suggestions using the Lucene contributed SpellChecker.  *  *<p>  * Refer to http://wiki.apache.org/solr/SpellCheckComponent for more details  *</p>  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|SpellCheckComponent
specifier|public
class|class
name|SpellCheckComponent
extends|extends
name|SearchComponent
implements|implements
name|SolrCoreAware
implements|,
name|SpellingParams
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SpellCheckComponent
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_ONLY_MORE_POPULAR
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ONLY_MORE_POPULAR
init|=
literal|false
decl_stmt|;
comment|/**    * Base name for all spell checker query parameters. This name is also used to    * register this component with SearchHandler.    */
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"spellcheck"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|initParams
specifier|protected
name|NamedList
name|initParams
decl_stmt|;
comment|/**    * Key is the dictionary, value is the SpellChecker for that dictionary name    */
DECL|field|spellCheckers
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|SolrSpellChecker
argument_list|>
name|spellCheckers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|SolrSpellChecker
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|queryConverter
specifier|protected
name|QueryConverter
name|queryConverter
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|initParams
operator|=
name|args
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|SolrSpellChecker
name|spellChecker
init|=
name|getSpellChecker
argument_list|(
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|SPELLCHECK_BUILD
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|spellChecker
operator|.
name|build
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getCore
argument_list|()
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"command"
argument_list|,
literal|"build"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|SPELLCHECK_RELOAD
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|spellChecker
operator|.
name|reload
argument_list|()
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"command"
argument_list|,
literal|"reload"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
operator|||
name|spellCheckers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|q
init|=
name|params
operator|.
name|get
argument_list|(
name|SPELLCHECK_Q
argument_list|)
decl_stmt|;
name|SolrSpellChecker
name|spellChecker
init|=
name|getSpellChecker
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
comment|//we have a spell check param, tokenize it with the query analyzer applicable for this spellchecker
name|tokens
operator|=
name|getTokens
argument_list|(
name|q
argument_list|,
name|spellChecker
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|q
operator|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tokens
operator|!=
literal|null
operator|&&
name|tokens
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|spellChecker
operator|!=
literal|null
condition|)
block|{
name|int
name|count
init|=
name|params
operator|.
name|getInt
argument_list|(
name|SPELLCHECK_COUNT
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|onlyMorePopular
init|=
name|params
operator|.
name|getBool
argument_list|(
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
name|DEFAULT_ONLY_MORE_POPULAR
argument_list|)
decl_stmt|;
name|boolean
name|extendedResults
init|=
name|params
operator|.
name|getBool
argument_list|(
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NamedList
name|response
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|boolean
name|collate
init|=
name|params
operator|.
name|getBool
argument_list|(
name|SPELLCHECK_COLLATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpellingResult
name|spellingResult
init|=
name|spellChecker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
name|count
argument_list|,
name|onlyMorePopular
argument_list|,
name|extendedResults
argument_list|)
decl_stmt|;
if|if
condition|(
name|spellingResult
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|add
argument_list|(
literal|"suggestions"
argument_list|,
name|toNamedList
argument_list|(
name|spellingResult
argument_list|,
name|q
argument_list|,
name|extendedResults
argument_list|,
name|collate
argument_list|)
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"spellcheck"
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Specified dictionary does not exist."
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getTokens
specifier|private
name|Collection
argument_list|<
name|Token
argument_list|>
name|getTokens
parameter_list|(
name|String
name|q
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|Token
name|token
init|=
literal|null
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|q
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getSpellChecker
specifier|protected
name|SolrSpellChecker
name|getSpellChecker
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|String
name|dictName
init|=
name|params
operator|.
name|get
argument_list|(
name|SPELLCHECK_DICT
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictName
operator|==
literal|null
condition|)
block|{
name|dictName
operator|=
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
expr_stmt|;
block|}
return|return
name|spellCheckers
operator|.
name|get
argument_list|(
name|dictName
argument_list|)
return|;
block|}
comment|/**    * @return the spellchecker registered to a given name    */
DECL|method|getSpellChecker
specifier|public
name|SolrSpellChecker
name|getSpellChecker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|spellCheckers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|toNamedList
specifier|protected
name|NamedList
name|toNamedList
parameter_list|(
name|SpellingResult
name|spellingResult
parameter_list|,
name|String
name|origQuery
parameter_list|,
name|boolean
name|extendedResults
parameter_list|,
name|boolean
name|collate
parameter_list|)
block|{
name|NamedList
name|result
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|suggestions
init|=
name|spellingResult
operator|.
name|getSuggestions
argument_list|()
decl_stmt|;
name|boolean
name|hasFreqInfo
init|=
name|spellingResult
operator|.
name|hasTokenFrequencyInfo
argument_list|()
decl_stmt|;
name|boolean
name|isCorrectlySpelled
init|=
literal|true
decl_stmt|;
name|Map
argument_list|<
name|Token
argument_list|,
name|String
argument_list|>
name|best
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collate
operator|==
literal|true
condition|)
block|{
name|best
operator|=
operator|new
name|HashMap
argument_list|<
name|Token
argument_list|,
name|String
argument_list|>
argument_list|(
name|suggestions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|suggestions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Token
name|inputToken
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|theSuggestions
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|theSuggestions
operator|!=
literal|null
operator|&&
name|theSuggestions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SimpleOrderedMap
name|suggestionList
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
literal|"numFound"
argument_list|,
name|theSuggestions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
literal|"startOffset"
argument_list|,
name|inputToken
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
literal|"endOffset"
argument_list|,
name|inputToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|extendedResults
operator|&&
name|hasFreqInfo
condition|)
block|{
name|suggestionList
operator|.
name|add
argument_list|(
literal|"origFreq"
argument_list|,
name|spellingResult
operator|.
name|getTokenFrequency
argument_list|(
name|inputToken
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggEntry
range|:
name|theSuggestions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|suggestionItem
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|suggestionItem
operator|.
name|add
argument_list|(
literal|"frequency"
argument_list|,
name|suggEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|suggestionItem
operator|.
name|add
argument_list|(
literal|"word"
argument_list|,
name|suggEntry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
literal|"suggestion"
argument_list|,
name|suggestionItem
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|suggestionList
operator|.
name|add
argument_list|(
literal|"suggestion"
argument_list|,
name|theSuggestions
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collate
operator|==
literal|true
condition|)
block|{
comment|//set aside the best suggestion for this token
name|best
operator|.
name|put
argument_list|(
name|inputToken
argument_list|,
name|theSuggestions
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasFreqInfo
condition|)
block|{
name|isCorrectlySpelled
operator|=
name|isCorrectlySpelled
operator|&&
name|spellingResult
operator|.
name|getTokenFrequency
argument_list|(
name|inputToken
argument_list|)
operator|>
literal|0
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|inputToken
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|inputToken
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|,
name|suggestionList
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasFreqInfo
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|"correctlySpelled"
argument_list|,
name|isCorrectlySpelled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collate
operator|==
literal|true
condition|)
block|{
name|StringBuilder
name|collation
init|=
operator|new
name|StringBuilder
argument_list|(
name|origQuery
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|String
argument_list|>
argument_list|>
name|bestIter
init|=
name|best
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|bestIter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|bestIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Token
name|tok
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|collation
operator|.
name|replace
argument_list|(
name|tok
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|collVal
init|=
name|collation
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|collVal
operator|.
name|equals
argument_list|(
name|origQuery
argument_list|)
operator|==
literal|false
condition|)
block|{
name|LOG
operator|.
name|fine
argument_list|(
literal|"Collation:"
operator|+
name|collation
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"collation"
argument_list|,
name|collVal
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|initParams
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing spell checkers"
argument_list|)
expr_stmt|;
name|boolean
name|hasDefault
init|=
literal|false
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
name|initParams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|initParams
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
literal|"spellchecker"
argument_list|)
condition|)
block|{
name|NamedList
name|spellchecker
init|=
operator|(
name|NamedList
operator|)
name|initParams
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|className
init|=
operator|(
name|String
operator|)
name|spellchecker
operator|.
name|get
argument_list|(
literal|"classname"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
name|className
operator|=
name|IndexBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|SolrSpellChecker
name|checker
init|=
operator|(
name|SolrSpellChecker
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|checker
operator|!=
literal|null
condition|)
block|{
name|String
name|dictionary
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictionary
operator|!=
literal|null
condition|)
block|{
name|boolean
name|isDefault
init|=
name|dictionary
operator|.
name|equals
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|hasDefault
operator|==
literal|false
condition|)
block|{
name|hasDefault
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|hasDefault
operator|==
literal|true
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one dictionary is missing name."
argument_list|)
throw|;
block|}
name|spellCheckers
operator|.
name|put
argument_list|(
name|dictionary
argument_list|,
name|checker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|hasDefault
operator|==
literal|false
condition|)
block|{
name|spellCheckers
operator|.
name|put
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|,
name|checker
argument_list|)
expr_stmt|;
name|hasDefault
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one dictionary is missing name."
argument_list|)
throw|;
block|}
block|}
comment|// Register event listeners for this SpellChecker
name|core
operator|.
name|registerFirstSearcherListener
argument_list|(
operator|new
name|SpellCheckerListener
argument_list|(
name|core
argument_list|,
name|checker
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
operator|(
name|String
operator|)
name|spellchecker
operator|.
name|get
argument_list|(
literal|"buildOnCommit"
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering newSearcher listener for spellchecker: "
operator|+
name|checker
operator|.
name|getDictionaryName
argument_list|()
argument_list|)
expr_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
operator|new
name|SpellCheckerListener
argument_list|(
name|core
argument_list|,
name|checker
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't load spell checker: "
operator|+
name|className
argument_list|)
throw|;
block|}
block|}
block|}
name|String
name|xpath
init|=
literal|"queryConverter"
decl_stmt|;
name|SolrConfig
name|solrConfig
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|solrConfig
operator|.
name|evaluate
argument_list|(
name|xpath
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|QueryConverter
argument_list|>
name|queryConverters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryConverter
argument_list|>
argument_list|()
decl_stmt|;
name|NamedListPluginLoader
argument_list|<
name|QueryConverter
argument_list|>
name|loader
init|=
operator|new
name|NamedListPluginLoader
argument_list|<
name|QueryConverter
argument_list|>
argument_list|(
literal|"[solrconfig.xml] "
operator|+
name|xpath
argument_list|,
name|queryConverters
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|solrConfig
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
comment|//there should only be one
if|if
condition|(
name|queryConverters
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|queryConverter
operator|=
name|queryConverters
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|String
name|fieldTypeName
init|=
operator|(
name|String
operator|)
name|initParams
operator|.
name|get
argument_list|(
literal|"queryAnalyzerFieldType"
argument_list|)
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|fieldTypeName
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|fieldType
operator|==
literal|null
condition|?
operator|new
name|WhitespaceAnalyzer
argument_list|()
else|:
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
comment|//TODO: There's got to be a better way!  Where's Spring when you need it?
name|queryConverter
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: Is there a better way?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"One and only one queryConverter may be defined"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|SpellCheckerListener
specifier|private
specifier|static
class|class
name|SpellCheckerListener
implements|implements
name|SolrEventListener
block|{
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|checker
specifier|private
specifier|final
name|SolrSpellChecker
name|checker
decl_stmt|;
DECL|field|firstSearcher
specifier|private
specifier|final
name|boolean
name|firstSearcher
decl_stmt|;
DECL|method|SpellCheckerListener
specifier|public
name|SpellCheckerListener
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrSpellChecker
name|checker
parameter_list|,
name|boolean
name|firstSearcher
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|checker
operator|=
name|checker
expr_stmt|;
name|this
operator|.
name|firstSearcher
operator|=
name|firstSearcher
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{     }
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
if|if
condition|(
name|firstSearcher
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading spell index for spellchecker: "
operator|+
name|checker
operator|.
name|getDictionaryName
argument_list|()
argument_list|)
expr_stmt|;
name|checker
operator|.
name|reload
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|"Exception in reloading spell check index for spellchecker: "
operator|+
name|checker
operator|.
name|getDictionaryName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// newSearcher event
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Building spell index for spell checker: "
operator|+
name|checker
operator|.
name|getDictionaryName
argument_list|()
argument_list|)
expr_stmt|;
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|newSearcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|"Exception in building spell check index for spellchecker: "
operator|+
name|checker
operator|.
name|getDictionaryName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{     }
block|}
DECL|method|getSpellCheckers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SolrSpellChecker
argument_list|>
name|getSpellCheckers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|spellCheckers
argument_list|)
return|;
block|}
comment|// ///////////////////////////////////////////
comment|// / SolrInfoMBean
comment|// //////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Spell Checker component"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id:$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL:$"
return|;
block|}
block|}
end_class

end_unit

