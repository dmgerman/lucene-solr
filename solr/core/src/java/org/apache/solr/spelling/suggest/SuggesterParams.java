begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_interface
DECL|interface|SuggesterParams
specifier|public
interface|interface
name|SuggesterParams
block|{
DECL|field|SUGGEST_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_PREFIX
init|=
literal|"suggest."
decl_stmt|;
comment|/**    * The name of the dictionary to be used for giving the suggestion for a    * request. The value for this parameter is configured in solrconfig.xml    */
DECL|field|SUGGEST_DICT
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_DICT
init|=
name|SUGGEST_PREFIX
operator|+
literal|"dictionary"
decl_stmt|;
comment|/**    * The count of suggestions to return for each query term not in the index and/or dictionary.    *<p>    * If this parameter is absent in the request then only one suggestion is    * returned. If it is more than one then a maximum of given suggestions are    * returned for each token in the query.    */
DECL|field|SUGGEST_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_COUNT
init|=
name|SUGGEST_PREFIX
operator|+
literal|"count"
decl_stmt|;
comment|/**    * Use the value for this parameter as the query to spell check.    *<p>    * This parameter is<b>optional</b>. If absent, then the q parameter is    * used.    */
DECL|field|SUGGEST_Q
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_Q
init|=
name|SUGGEST_PREFIX
operator|+
literal|"q"
decl_stmt|;
comment|/**    * Whether to build the index or not. Optional and false by default.    */
DECL|field|SUGGEST_BUILD
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_BUILD
init|=
name|SUGGEST_PREFIX
operator|+
literal|"build"
decl_stmt|;
comment|/**    * Whether to build the index or not for all suggesters in the component.    * Optional and false by default.    * This parameter does not need any suggest dictionary names to be specified    */
DECL|field|SUGGEST_BUILD_ALL
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_BUILD_ALL
init|=
name|SUGGEST_PREFIX
operator|+
literal|"buildAll"
decl_stmt|;
comment|/**    * Whether to reload the index. Optional and false by default.    */
DECL|field|SUGGEST_RELOAD
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_RELOAD
init|=
name|SUGGEST_PREFIX
operator|+
literal|"reload"
decl_stmt|;
comment|/**    * Whether to reload the index or not for all suggesters in the component.    * Optional and false by default.    * This parameter does not need any suggest dictionary names to be specified    */
DECL|field|SUGGEST_RELOAD_ALL
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST_RELOAD_ALL
init|=
name|SUGGEST_PREFIX
operator|+
literal|"reloadAll"
decl_stmt|;
block|}
end_interface

end_unit

