begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TooManyListenersException
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
name|document
operator|.
name|DateTools
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
name|core
operator|.
name|QueryParserHelper
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|builders
operator|.
name|StandardQueryTreeBuilder
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
name|FuzzyConfig
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
name|NumericConfig
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
name|ConfigurationKeys
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
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
operator|.
name|StandardQueryNodeProcessorPipeline
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
name|FuzzyQuery
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
name|MultiTermQuery
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

begin_comment
comment|/**  *<p>  * This class is a helper that enables users to easily use the Lucene query  * parser.  *</p>  *<p>  * To construct a Query object from a query string, use the  * {@link #parse(String, String)} method:  *<ul>  * StandardQueryParser queryParserHelper = new StandardQueryParser();<br/>  * Query query = queryParserHelper.parse("a AND b", "defaultField");  *</ul>  *<p>  * To change any configuration before parsing the query string do, for example:  *<p/>  *<ul>  * // the query config handler returned by {@link StandardQueryParser} is a  * {@link StandardQueryConfigHandler}<br/>  * queryParserHelper.getQueryConfigHandler().setAnalyzer(new  * WhitespaceAnalyzer());  *</ul>  *<p>  * The syntax for query strings is as follows (copied from the old QueryParser  * javadoc):  *<ul>  * A Query is a series of clauses. A clause may be prefixed by:  *<ul>  *<li>a plus (<code>+</code>) or a minus (<code>-</code>) sign, indicating that  * the clause is required or prohibited respectively; or  *<li>a term followed by a colon, indicating the field to be searched. This  * enables one to construct queries which search multiple fields.  *</ul>  *   * A clause may be either:  *<ul>  *<li>a term, indicating all the documents that contain this term; or  *<li>a nested query, enclosed in parentheses. Note that this may be used with  * a<code>+</code>/<code>-</code> prefix to require any of a set of terms.  *</ul>  *   * Thus, in BNF, the query grammar is:  *   *<pre>  *   Query  ::= ( Clause )*  *   Clause ::= [&quot;+&quot;,&quot;-&quot;] [&lt;TERM&gt;&quot;:&quot;] (&lt;TERM&gt; |&quot;(&quot; Query&quot;)&quot; )  *</pre>  *   *<p>  * Examples of appropriately formatted queries can be found in the<a  * href="{@docRoot}/org/apache/lucene/queryparser/classic/package-summary.html#package_description">  * query syntax documentation</a>.  *</p>  *</ul>  *<p>  * The text parser used by this helper is a {@link StandardSyntaxParser}.  *<p/>  *<p>  * The query node processor used by this helper is a  * {@link StandardQueryNodeProcessorPipeline}.  *<p/>  *<p>  * The builder used by this helper is a {@link StandardQueryTreeBuilder}.  *<p/>  *   * @see StandardQueryParser  * @see StandardQueryConfigHandler  * @see StandardSyntaxParser  * @see StandardQueryNodeProcessorPipeline  * @see StandardQueryTreeBuilder  */
end_comment

begin_class
DECL|class|StandardQueryParser
specifier|public
class|class
name|StandardQueryParser
extends|extends
name|QueryParserHelper
implements|implements
name|CommonQueryParserConfiguration
block|{
comment|/**    * Constructs a {@link StandardQueryParser} object.    */
DECL|method|StandardQueryParser
specifier|public
name|StandardQueryParser
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|StandardQueryConfigHandler
argument_list|()
argument_list|,
operator|new
name|StandardSyntaxParser
argument_list|()
argument_list|,
operator|new
name|StandardQueryNodeProcessorPipeline
argument_list|(
literal|null
argument_list|)
argument_list|,
operator|new
name|StandardQueryTreeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a {@link StandardQueryParser} object and sets an    * {@link Analyzer} to it. The same as:    *     *<ul>    * StandardQueryParser qp = new StandardQueryParser();    * qp.getQueryConfigHandler().setAnalyzer(analyzer);    *</ul>    *     * @param analyzer    *          the analyzer to be used by this query parser helper    */
DECL|method|StandardQueryParser
specifier|public
name|StandardQueryParser
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<StandardQueryParser config=\""
operator|+
name|this
operator|.
name|getQueryConfigHandler
argument_list|()
operator|+
literal|"\"/>"
return|;
block|}
comment|/**    * Overrides {@link QueryParserHelper#parse(String, String)} so it casts the    * return object to {@link Query}. For more reference about this method, check    * {@link QueryParserHelper#parse(String, String)}.    *     * @param query    *          the query string    * @param defaultField    *          the default field used by the text parser    *     * @return the object built from the query    *     * @throws QueryNodeException    *           if something wrong happens along the three phases    */
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|defaultField
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
operator|(
name|Query
operator|)
name|super
operator|.
name|parse
argument_list|(
name|query
argument_list|,
name|defaultField
argument_list|)
return|;
block|}
comment|/**    * Gets implicit operator setting, which will be either {@link Operator#AND}    * or {@link Operator#OR}.    */
DECL|method|getDefaultOperator
specifier|public
name|StandardQueryConfigHandler
operator|.
name|Operator
name|getDefaultOperator
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|DEFAULT_OPERATOR
argument_list|)
return|;
block|}
comment|/**    * Sets the boolean operator of the QueryParser. In default mode (    * {@link Operator#OR}) terms without any modifiers are considered optional:    * for example<code>capital of Hungary</code> is equal to    *<code>capital OR of OR Hungary</code>.<br/>    * In {@link Operator#AND} mode terms are considered to be in conjunction: the    * above mentioned query is parsed as<code>capital AND of AND Hungary</code>    */
DECL|method|setDefaultOperator
specifier|public
name|void
name|setDefaultOperator
parameter_list|(
name|StandardQueryConfigHandler
operator|.
name|Operator
name|operator
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|DEFAULT_OPERATOR
argument_list|,
name|operator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set to<code>true</code> to allow leading wildcard characters.    *<p>    * When set,<code>*</code> or<code>?</code> are allowed as the first    * character of a PrefixQuery and WildcardQuery. Note that this can produce    * very slow queries on big indexes.    *<p>    * Default: false.    */
DECL|method|setLowercaseExpandedTerms
specifier|public
name|void
name|setLowercaseExpandedTerms
parameter_list|(
name|boolean
name|lowercaseExpandedTerms
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|LOWERCASE_EXPANDED_TERMS
argument_list|,
name|lowercaseExpandedTerms
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see #setLowercaseExpandedTerms(boolean)    */
DECL|method|getLowercaseExpandedTerms
specifier|public
name|boolean
name|getLowercaseExpandedTerms
parameter_list|()
block|{
name|Boolean
name|lowercaseExpandedTerms
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|LOWERCASE_EXPANDED_TERMS
argument_list|)
decl_stmt|;
if|if
condition|(
name|lowercaseExpandedTerms
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|lowercaseExpandedTerms
return|;
block|}
block|}
comment|/**    * Set to<code>true</code> to allow leading wildcard characters.    *<p>    * When set,<code>*</code> or<code>?</code> are allowed as the first    * character of a PrefixQuery and WildcardQuery. Note that this can produce    * very slow queries on big indexes.    *<p>    * Default: false.    */
DECL|method|setAllowLeadingWildcard
specifier|public
name|void
name|setAllowLeadingWildcard
parameter_list|(
name|boolean
name|allowLeadingWildcard
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ALLOW_LEADING_WILDCARD
argument_list|,
name|allowLeadingWildcard
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set to<code>true</code> to enable position increments in result query.    *<p>    * When set, result phrase and multi-phrase queries will be aware of position    * increments. Useful when e.g. a StopFilter increases the position increment    * of the token that follows an omitted token.    *<p>    * Default: false.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ENABLE_POSITION_INCREMENTS
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
name|Boolean
name|enablePositionsIncrements
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|ENABLE_POSITION_INCREMENTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|enablePositionsIncrements
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|enablePositionsIncrements
return|;
block|}
block|}
comment|/**    * By default, it uses    * {@link MultiTermQuery#CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} when creating a    * prefix, wildcard and range queries. This implementation is generally    * preferable because it a) Runs faster b) Does not have the scarcity of terms    * unduly influence score c) avoids any {@link TooManyListenersException}    * exception. However, if your application really needs to use the    * old-fashioned boolean queries expansion rewriting and the above points are    * not relevant then use this change the rewrite method.    */
DECL|method|setMultiTermRewriteMethod
specifier|public
name|void
name|setMultiTermRewriteMethod
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_TERM_REWRITE_METHOD
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see #setMultiTermRewriteMethod(org.apache.lucene.search.MultiTermQuery.RewriteMethod)    */
DECL|method|getMultiTermRewriteMethod
specifier|public
name|MultiTermQuery
operator|.
name|RewriteMethod
name|getMultiTermRewriteMethod
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_TERM_REWRITE_METHOD
argument_list|)
return|;
block|}
comment|/**    * Set the fields a query should be expanded to when the field is    *<code>null</code>    *     * @param fields the fields used to expand the query    */
DECL|method|setMultiFields
specifier|public
name|void
name|setMultiFields
parameter_list|(
name|CharSequence
index|[]
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|CharSequence
index|[
literal|0
index|]
expr_stmt|;
block|}
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_FIELDS
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the fields used to expand the query when the field for a    * certain query is<code>null</code>    *     * @param fields the fields used to expand the query    */
DECL|method|getMultiFields
specifier|public
name|void
name|getMultiFields
parameter_list|(
name|CharSequence
index|[]
name|fields
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_FIELDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the prefix length for fuzzy queries. Default is 0.    *     * @param fuzzyPrefixLength    *          The fuzzyPrefixLength to set.    */
DECL|method|setFuzzyPrefixLength
specifier|public
name|void
name|setFuzzyPrefixLength
parameter_list|(
name|int
name|fuzzyPrefixLength
parameter_list|)
block|{
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
name|FuzzyConfig
name|fuzzyConfig
init|=
name|config
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|fuzzyConfig
operator|==
literal|null
condition|)
block|{
name|fuzzyConfig
operator|=
operator|new
name|FuzzyConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|,
name|fuzzyConfig
argument_list|)
expr_stmt|;
block|}
name|fuzzyConfig
operator|.
name|setPrefixLength
argument_list|(
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumericConfigMap
specifier|public
name|void
name|setNumericConfigMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|NumericConfig
argument_list|>
name|numericConfigMap
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG_MAP
argument_list|,
name|numericConfigMap
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumericConfigMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|NumericConfig
argument_list|>
name|getNumericConfigMap
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG_MAP
argument_list|)
return|;
block|}
comment|/**    * Set locale used by date range parsing.    */
DECL|method|setLocale
specifier|public
name|void
name|setLocale
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|LOCALE
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns current locale, allowing access by subclasses.    */
DECL|method|getLocale
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|LOCALE
argument_list|)
return|;
block|}
DECL|method|setTimeZone
specifier|public
name|void
name|setTimeZone
parameter_list|(
name|TimeZone
name|timeZone
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|TIMEZONE
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
block|}
DECL|method|getTimeZone
specifier|public
name|TimeZone
name|getTimeZone
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|TIMEZONE
argument_list|)
return|;
block|}
comment|/**    * Sets the default slop for phrases. If zero, then exact phrase matches are    * required. Default value is zero.    *     * @deprecated renamed to {@link #setPhraseSlop(int)}    */
annotation|@
name|Deprecated
DECL|method|setDefaultPhraseSlop
specifier|public
name|void
name|setDefaultPhraseSlop
parameter_list|(
name|int
name|defaultPhraseSlop
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|PHRASE_SLOP
argument_list|,
name|defaultPhraseSlop
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the default slop for phrases. If zero, then exact phrase matches are    * required. Default value is zero.    */
DECL|method|setPhraseSlop
specifier|public
name|void
name|setPhraseSlop
parameter_list|(
name|int
name|defaultPhraseSlop
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|PHRASE_SLOP
argument_list|,
name|defaultPhraseSlop
argument_list|)
expr_stmt|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ANALYZER
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|ANALYZER
argument_list|)
return|;
block|}
comment|/**    * @see #setAllowLeadingWildcard(boolean)    */
DECL|method|getAllowLeadingWildcard
specifier|public
name|boolean
name|getAllowLeadingWildcard
parameter_list|()
block|{
name|Boolean
name|allowLeadingWildcard
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|ALLOW_LEADING_WILDCARD
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowLeadingWildcard
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|allowLeadingWildcard
return|;
block|}
block|}
comment|/**    * Get the minimal similarity for fuzzy queries.    */
DECL|method|getFuzzyMinSim
specifier|public
name|float
name|getFuzzyMinSim
parameter_list|()
block|{
name|FuzzyConfig
name|fuzzyConfig
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|fuzzyConfig
operator|==
literal|null
condition|)
block|{
return|return
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
return|;
block|}
else|else
block|{
return|return
name|fuzzyConfig
operator|.
name|getMinSimilarity
argument_list|()
return|;
block|}
block|}
comment|/**    * Get the prefix length for fuzzy queries.    *     * @return Returns the fuzzyPrefixLength.    */
DECL|method|getFuzzyPrefixLength
specifier|public
name|int
name|getFuzzyPrefixLength
parameter_list|()
block|{
name|FuzzyConfig
name|fuzzyConfig
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|fuzzyConfig
operator|==
literal|null
condition|)
block|{
return|return
name|FuzzyQuery
operator|.
name|defaultPrefixLength
return|;
block|}
else|else
block|{
return|return
name|fuzzyConfig
operator|.
name|getPrefixLength
argument_list|()
return|;
block|}
block|}
comment|/**    * Gets the default slop for phrases.    */
DECL|method|getPhraseSlop
specifier|public
name|int
name|getPhraseSlop
parameter_list|()
block|{
name|Integer
name|phraseSlop
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|PHRASE_SLOP
argument_list|)
decl_stmt|;
if|if
condition|(
name|phraseSlop
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|phraseSlop
return|;
block|}
block|}
comment|/**    * Set the minimum similarity for fuzzy queries. Default is defined on    * {@link FuzzyQuery#defaultMinSimilarity}.    */
DECL|method|setFuzzyMinSim
specifier|public
name|void
name|setFuzzyMinSim
parameter_list|(
name|float
name|fuzzyMinSim
parameter_list|)
block|{
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
name|FuzzyConfig
name|fuzzyConfig
init|=
name|config
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|fuzzyConfig
operator|==
literal|null
condition|)
block|{
name|fuzzyConfig
operator|=
operator|new
name|FuzzyConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|,
name|fuzzyConfig
argument_list|)
expr_stmt|;
block|}
name|fuzzyConfig
operator|.
name|setMinSimilarity
argument_list|(
name|fuzzyMinSim
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the boost used for each field.    *     * @param boosts a collection that maps a field to its boost     */
DECL|method|setFieldsBoost
specifier|public
name|void
name|setFieldsBoost
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boosts
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_BOOST_MAP
argument_list|,
name|boosts
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the field to boost map used to set boost for each field.    *     * @return the field to boost map     */
DECL|method|getFieldsBoost
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getFieldsBoost
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_BOOST_MAP
argument_list|)
return|;
block|}
comment|/**    * Sets the default {@link Resolution} used for certain field when    * no {@link Resolution} is defined for this field.    *     * @param dateResolution the default {@link Resolution}    */
DECL|method|setDateResolution
specifier|public
name|void
name|setDateResolution
parameter_list|(
name|DateTools
operator|.
name|Resolution
name|dateResolution
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|DATE_RESOLUTION
argument_list|,
name|dateResolution
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the default {@link Resolution} used for certain field when    * no {@link Resolution} is defined for this field.    *     * @return the default {@link Resolution}    */
DECL|method|getDateResolution
specifier|public
name|DateTools
operator|.
name|Resolution
name|getDateResolution
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|DATE_RESOLUTION
argument_list|)
return|;
block|}
comment|/**    * Sets the {@link Resolution} used for each field    *     * @param dateRes a collection that maps a field to its {@link Resolution}    *     * @deprecated this method was renamed to {@link #setDateResolutionMap(Map)}     */
annotation|@
name|Deprecated
DECL|method|setDateResolution
specifier|public
name|void
name|setDateResolution
parameter_list|(
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
name|dateRes
parameter_list|)
block|{
name|setDateResolutionMap
argument_list|(
name|dateRes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the field to {@link Resolution} map used to normalize each date field.    *     * @return the field to {@link Resolution} map    */
DECL|method|getDateResolutionMap
specifier|public
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
name|getDateResolutionMap
parameter_list|()
block|{
return|return
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_DATE_RESOLUTION_MAP
argument_list|)
return|;
block|}
comment|/**    * Sets the {@link Resolution} used for each field    *     * @param dateRes a collection that maps a field to its {@link Resolution}    */
DECL|method|setDateResolutionMap
specifier|public
name|void
name|setDateResolutionMap
parameter_list|(
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
name|dateRes
parameter_list|)
block|{
name|getQueryConfigHandler
argument_list|()
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_DATE_RESOLUTION_MAP
argument_list|,
name|dateRes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

