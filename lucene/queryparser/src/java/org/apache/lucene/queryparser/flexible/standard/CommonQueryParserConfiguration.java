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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Configuration options common across queryparser implementations.  */
end_comment

begin_interface
DECL|interface|CommonQueryParserConfiguration
specifier|public
interface|interface
name|CommonQueryParserConfiguration
block|{
comment|/**    * Whether terms of multi-term queries (e.g., wildcard,    * prefix, fuzzy and range) should be automatically    * lower-cased or not.  Default is<code>true</code>.    */
DECL|method|setLowercaseExpandedTerms
specifier|public
name|void
name|setLowercaseExpandedTerms
parameter_list|(
name|boolean
name|lowercaseExpandedTerms
parameter_list|)
function_decl|;
comment|/**    * @see #setLowercaseExpandedTerms(boolean)    */
DECL|method|getLowercaseExpandedTerms
specifier|public
name|boolean
name|getLowercaseExpandedTerms
parameter_list|()
function_decl|;
comment|/**    * Set to<code>true</code> to allow leading wildcard characters.    *<p>    * When set,<code>*</code> or<code>?</code> are allowed as the first    * character of a PrefixQuery and WildcardQuery. Note that this can produce    * very slow queries on big indexes.    *<p>    * Default: false.    */
DECL|method|setAllowLeadingWildcard
specifier|public
name|void
name|setAllowLeadingWildcard
parameter_list|(
name|boolean
name|allowLeadingWildcard
parameter_list|)
function_decl|;
comment|/**    * Set to<code>true</code> to enable position increments in result query.    *<p>    * When set, result phrase and multi-phrase queries will be aware of position    * increments. Useful when e.g. a StopFilter increases the position increment    * of the token that follows an omitted token.    *<p>    * Default: false.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enabled
parameter_list|)
function_decl|;
comment|/**    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
function_decl|;
comment|/**    * By default, it uses    * {@link MultiTermQuery#CONSTANT_SCORE_FILTER_REWRITE} when creating a    * prefix, wildcard and range queries. This implementation is generally    * preferable because it a) Runs faster b) Does not have the scarcity of terms    * unduly influence score c) avoids any {@link TooManyListenersException}    * exception. However, if your application really needs to use the    * old-fashioned boolean queries expansion rewriting and the above points are    * not relevant then use this change the rewrite method.    */
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
function_decl|;
comment|/**    * @see #setMultiTermRewriteMethod(org.apache.lucene.search.MultiTermQuery.RewriteMethod)    */
DECL|method|getMultiTermRewriteMethod
specifier|public
name|MultiTermQuery
operator|.
name|RewriteMethod
name|getMultiTermRewriteMethod
parameter_list|()
function_decl|;
comment|/**    * Set the prefix length for fuzzy queries. Default is 0.    *     * @param fuzzyPrefixLength    *          The fuzzyPrefixLength to set.    */
DECL|method|setFuzzyPrefixLength
specifier|public
name|void
name|setFuzzyPrefixLength
parameter_list|(
name|int
name|fuzzyPrefixLength
parameter_list|)
function_decl|;
comment|/**    * Set locale used by date range parsing.    */
DECL|method|setLocale
specifier|public
name|void
name|setLocale
parameter_list|(
name|Locale
name|locale
parameter_list|)
function_decl|;
comment|/**    * Returns current locale, allowing access by subclasses.    */
DECL|method|getLocale
specifier|public
name|Locale
name|getLocale
parameter_list|()
function_decl|;
DECL|method|setTimeZone
specifier|public
name|void
name|setTimeZone
parameter_list|(
name|TimeZone
name|timeZone
parameter_list|)
function_decl|;
DECL|method|getTimeZone
specifier|public
name|TimeZone
name|getTimeZone
parameter_list|()
function_decl|;
comment|/**    * Sets the default slop for phrases. If zero, then exact phrase matches are    * required. Default value is zero.    */
DECL|method|setPhraseSlop
specifier|public
name|void
name|setPhraseSlop
parameter_list|(
name|int
name|defaultPhraseSlop
parameter_list|)
function_decl|;
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
function_decl|;
comment|/**    * @see #setAllowLeadingWildcard(boolean)    */
DECL|method|getAllowLeadingWildcard
specifier|public
name|boolean
name|getAllowLeadingWildcard
parameter_list|()
function_decl|;
comment|/**    * Get the minimal similarity for fuzzy queries.    */
DECL|method|getFuzzyMinSim
specifier|public
name|float
name|getFuzzyMinSim
parameter_list|()
function_decl|;
comment|/**    * Get the prefix length for fuzzy queries.    *     * @return Returns the fuzzyPrefixLength.    */
DECL|method|getFuzzyPrefixLength
specifier|public
name|int
name|getFuzzyPrefixLength
parameter_list|()
function_decl|;
comment|/**    * Gets the default slop for phrases.    */
DECL|method|getPhraseSlop
specifier|public
name|int
name|getPhraseSlop
parameter_list|()
function_decl|;
comment|/**    * Set the minimum similarity for fuzzy queries. Default is defined on    * {@link FuzzyQuery#defaultMinSimilarity}.    */
DECL|method|setFuzzyMinSim
specifier|public
name|void
name|setFuzzyMinSim
parameter_list|(
name|float
name|fuzzyMinSim
parameter_list|)
function_decl|;
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
function_decl|;
block|}
end_interface

end_unit

