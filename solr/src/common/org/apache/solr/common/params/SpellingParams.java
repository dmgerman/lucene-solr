begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**    * The count of suggestions needed for a given query.    *<p/>    * If this parameter is absent in the request then only one suggestion is    * returned. If it is more than one then a maximum of given suggestions are    * returned for each token in the query.    */
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
comment|/**    * When this parameter is set to true and the misspelled word exists in the    * user field, only words that occur more frequently in the Solr field than    * the one given will be returned. The default value is false.    *<p/>    *<b>This is applicable only for dictionaries built from Solr fields.</b>    */
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
comment|/**    * Whether to use the extended response format, which is more complicated but    * richer. Returns the document frequency for each suggestion and returns one    * suggestion block for each term in the query string. Default is false.    *<p/>    *<b>This is applicable only for dictionaries built from Solr fields.</b>    */
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
comment|/**    * Use the value for this parameter as the query to spell check.    *<p/>    * This parameter is<b>optional</b>. If absent, then the q parameter is    * used.    */
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
block|}
end_interface

end_unit

