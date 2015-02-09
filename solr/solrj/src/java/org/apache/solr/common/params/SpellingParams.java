begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Parameters used for spellchecking  *   * @since solr 1.3  */
end_comment

begin_interface
DECL|interface|SpellingParams
specifier|public
interface|interface
name|SpellingParams
block|{
DECL|field|SPELLCHECK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_PREFIX
init|=
literal|"spellcheck."
decl_stmt|;
comment|/**    * The name of the dictionary to be used for giving the suggestion for a    * request. The value for this parameter is configured in solrconfig.xml    */
DECL|field|SPELLCHECK_DICT
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_DICT
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"dictionary"
decl_stmt|;
comment|/**    * The count of suggestions to return for each query term not in the index and/or dictionary.    *<p>    * If this parameter is absent in the request then only one suggestion is    * returned. If it is more than one then a maximum of given suggestions are    * returned for each token in the query.    */
DECL|field|SPELLCHECK_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_COUNT
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"count"
decl_stmt|;
comment|/**    * The count of suggestions to return for each query term existing in the index and/or dictionary.    *<p>    * If this parameter is absent in the request then no suggestions are generated.  This parameter allows    * for receiving alternative terms to use in context-sensitive spelling corrections.    */
DECL|field|SPELLCHECK_ALTERNATIVE_TERM_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_ALTERNATIVE_TERM_COUNT
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"alternativeTermCount"
decl_stmt|;
comment|/**    *<p>    * The maximum number of hits the request can return in order to both     * generate spelling suggestions and set the "correctlySpelled" element to "false".       * Note that this parameter is typically of use only in conjunction with "spellcheck.alternativeTermCount".    *</p>    *<p>    * If left unspecified, the default behavior will prevail.  That is, "correctlySpelled" will be false and suggestions    * will be returned only if one or more of the query terms are absent from the dictionary and/or index.  If set to zero,    * the "correctlySpelled" flag will be false only if the response returns zero hits.  If set to a value greater than zero,     * suggestions will be returned even if hits are returned (up to the specified number).  This number also will serve as    * the threshold in determining the value of "correctlySpelled".  Specifying a value greater than zero is useful     * for creating "did-you-mean" suggestions for queries that return a low number of hits.    *</p>    */
DECL|field|SPELLCHECK_MAX_RESULTS_FOR_SUGGEST
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_MAX_RESULTS_FOR_SUGGEST
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"maxResultsForSuggest"
decl_stmt|;
comment|/**    * When this parameter is set to true and the misspelled word exists in the    * user field, only words that occur more frequently in the Solr field than    * the one given will be returned. The default value is false.    *<p>    *<b>This is applicable only for dictionaries built from Solr fields.</b>    */
DECL|field|SPELLCHECK_ONLY_MORE_POPULAR
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_ONLY_MORE_POPULAR
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"onlyMorePopular"
decl_stmt|;
comment|/**    * Whether to use the extended response format, which is more complicated but    * richer. Returns the document frequency for each suggestion and returns one    * suggestion block for each term in the query string. Default is false.    *<p>    *<b>This is applicable only for dictionaries built from Solr fields.</b>    */
DECL|field|SPELLCHECK_EXTENDED_RESULTS
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_EXTENDED_RESULTS
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"extendedResults"
decl_stmt|;
comment|/**    * Use the value for this parameter as the query to spell check.    *<p>    * This parameter is<b>optional</b>. If absent, then the q parameter is    * used.    */
DECL|field|SPELLCHECK_Q
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_Q
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"q"
decl_stmt|;
comment|/**    * Whether to build the index or not. Optional and false by default.    */
DECL|field|SPELLCHECK_BUILD
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_BUILD
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"build"
decl_stmt|;
comment|/**    * Whether to reload the index. Optional and false by default.    */
DECL|field|SPELLCHECK_RELOAD
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_RELOAD
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"reload"
decl_stmt|;
comment|/**    * Take the top suggestion for each token and create a new query from it    */
DECL|field|SPELLCHECK_COLLATE
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_COLLATE
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"collate"
decl_stmt|;
comment|/**    *<p>    * The maximum number of collations to return.  Default=1.  Ignored if "spellcheck.collate" is false.    *</p>    */
DECL|field|SPELLCHECK_MAX_COLLATIONS
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_MAX_COLLATIONS
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"maxCollations"
decl_stmt|;
comment|/**    *<p>    * The maximum number of collations to test by querying against the index.       * When testing, the collation is substituted for the original query's "q" param.  Any "qf"s are retained.    * If this is set to zero, does not test for hits before returning collations (returned collations may result in zero hits).    * Default=0. Ignored of "spellcheck.collate" is false.     *</p>    */
DECL|field|SPELLCHECK_MAX_COLLATION_TRIES
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_MAX_COLLATION_TRIES
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"maxCollationTries"
decl_stmt|;
comment|/**    *<p>    * The maximum number of word correction combinations to rank and evaluate prior to deciding which collation    * candidates to test against the index.  This is a performance safety-net in cases a user enters a query with    * many misspelled words.  The default is 10,000 combinations.     *</p>    */
DECL|field|SPELLCHECK_MAX_COLLATION_EVALUATIONS
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_MAX_COLLATION_EVALUATIONS
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"maxCollationEvaluations"
decl_stmt|;
comment|/**    *<p>    * For use with {@link SpellingParams#SPELLCHECK_MAX_COLLATION_TRIES} and     * {@link SpellingParams#SPELLCHECK_COLLATE_EXTENDED_RESULTS}.    * A performance optimization in cases where the exact number of hits a collation would return is not needed.      * Specify "0" to return the exact # of hits, otherwise give the maximum documents Lucene should collect     * with which to base an estimate.  The higher the value the more likely the estimates will be accurate     * (at expense of performance).     *</p>    *     *<p>    * The default is 0 (report exact hit-counts) when {@link SpellingParams#SPELLCHECK_COLLATE_EXTENDED_RESULTS} is TRUE.    * When {@link SpellingParams#SPELLCHECK_COLLATE_EXTENDED_RESULTS} is FALSE, this optimization is always performed.    *</p>    */
DECL|field|SPELLCHECK_COLLATE_MAX_COLLECT_DOCS
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_COLLATE_MAX_COLLECT_DOCS
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"collateMaxCollectDocs"
decl_stmt|;
comment|/**    *<p>    * Whether to use the Extended Results Format for collations.     * Includes "before&gt;after" pairs to easily allow clients to generate messages like "no results for PORK.  did you mean POLK?"    * Also indicates the # of hits each collation will return on re-query.  Default=false, which retains 1.4-compatible output.    *</p>    *<p>    * Note: that if {@link SpellingParams#SPELLCHECK_COLLATE_MAX_COLLECT_DOCS} is set to a value greater than 0,     * then the hit counts returned by this will be estimated.    *</p>    */
DECL|field|SPELLCHECK_COLLATE_EXTENDED_RESULTS
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"collateExtendedResults"
decl_stmt|;
comment|/**    *<p>    * For use with {@link SpellingParams#SPELLCHECK_MAX_COLLATION_TRIES}, use this to override any original query parameters    * when issuing test queries.  For instance, if the original query has "mm=1" but it is preferred to test collations    * with "mm=100%", then use "spellcheck.collateParam.mm=100%".    *</p>    */
DECL|field|SPELLCHECK_COLLATE_PARAM_OVERRIDE
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_COLLATE_PARAM_OVERRIDE
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"collateParam."
decl_stmt|;
comment|/**    * Certain spelling implementations may allow for an accuracy setting.    */
DECL|field|SPELLCHECK_ACCURACY
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK_ACCURACY
init|=
name|SPELLCHECK_PREFIX
operator|+
literal|"accuracy"
decl_stmt|;
block|}
end_interface

end_unit

