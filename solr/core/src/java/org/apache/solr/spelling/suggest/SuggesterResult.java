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
name|HashMap
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
name|List
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
name|search
operator|.
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
import|;
end_import

begin_comment
comment|/**   * Encapsulates the results returned by the suggester in {@link SolrSuggester}  * */
end_comment

begin_class
DECL|class|SuggesterResult
specifier|public
class|class
name|SuggesterResult
block|{
DECL|method|SuggesterResult
specifier|public
name|SuggesterResult
parameter_list|()
block|{}
comment|/** token -> lookup results mapping*/
DECL|field|suggestionsMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LookupResult
argument_list|>
argument_list|>
argument_list|>
name|suggestionsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Add suggestion results for<code>token</code> */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|suggesterName
parameter_list|,
name|String
name|token
parameter_list|,
name|List
argument_list|<
name|LookupResult
argument_list|>
name|results
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LookupResult
argument_list|>
argument_list|>
name|suggesterRes
init|=
name|this
operator|.
name|suggestionsMap
operator|.
name|get
argument_list|(
name|suggesterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggesterRes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|suggestionsMap
operator|.
name|put
argument_list|(
name|suggesterName
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LookupResult
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
name|this
operator|.
name|suggestionsMap
operator|.
name|get
argument_list|(
name|suggesterName
argument_list|)
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
name|results
expr_stmt|;
name|this
operator|.
name|suggestionsMap
operator|.
name|get
argument_list|(
name|suggesterName
argument_list|)
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Get a list of lookup result for a given<code>token</code>    * null can be returned, if there are no lookup results    * for the<code>token</code>    * */
DECL|method|getLookupResult
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|getLookupResult
parameter_list|(
name|String
name|suggesterName
parameter_list|,
name|String
name|token
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|suggestionsMap
operator|.
name|containsKey
argument_list|(
name|suggesterName
argument_list|)
operator|)
condition|?
name|this
operator|.
name|suggestionsMap
operator|.
name|get
argument_list|(
name|suggesterName
argument_list|)
operator|.
name|get
argument_list|(
name|token
argument_list|)
else|:
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
return|;
block|}
comment|/**    * Get the set of tokens that are present in the    * instance    */
DECL|method|getTokens
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTokens
parameter_list|(
name|String
name|suggesterName
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|suggestionsMap
operator|.
name|containsKey
argument_list|(
name|suggesterName
argument_list|)
operator|)
condition|?
name|this
operator|.
name|suggestionsMap
operator|.
name|get
argument_list|(
name|suggesterName
argument_list|)
operator|.
name|keySet
argument_list|()
else|:
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
comment|/**    * Get the set of suggesterNames for which this    * instance holds results    */
DECL|method|getSuggesterNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSuggesterNames
parameter_list|()
block|{
return|return
name|this
operator|.
name|suggestionsMap
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

