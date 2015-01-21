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
name|util
operator|.
name|SolrPluginUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
name|List
import|;
end_import

begin_class
DECL|class|SolrHighlighter
specifier|public
specifier|abstract
class|class
name|SolrHighlighter
block|{
DECL|field|DEFAULT_PHRASE_LIMIT
specifier|public
specifier|static
name|int
name|DEFAULT_PHRASE_LIMIT
init|=
literal|5000
decl_stmt|;
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrHighlighter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Check whether Highlighting is enabled for this request.    * @param params The params controlling Highlighting    * @return<code>true</code> if highlighting enabled,<code>false</code> if not.    */
DECL|method|isHighlightingEnabled
specifier|public
name|boolean
name|isHighlightingEnabled
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
return|return
name|params
operator|.
name|getBool
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Return a String array of the fields to be highlighted.    * Falls back to the programatic defaults, or the default search field if the list of fields    * is not specified in either the handler configuration or the request.    * @param query The current Query    * @param request The current SolrQueryRequest    * @param defaultFields Programmatic default highlight fields, used if nothing is specified in the handler config or the request.    */
DECL|method|getHighlightFields
specifier|public
name|String
index|[]
name|getHighlightFields
parameter_list|(
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
block|{
name|String
name|fields
index|[]
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|)
decl_stmt|;
comment|// if no fields specified in the request, or the handler, fall back to programmatic default, or default search field.
if|if
condition|(
name|emptyArray
argument_list|(
name|fields
argument_list|)
condition|)
block|{
comment|// use default search field if highlight fieldlist not specified.
if|if
condition|(
name|emptyArray
argument_list|(
name|defaultFields
argument_list|)
condition|)
block|{
name|String
name|defaultSearchField
init|=
name|request
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
decl_stmt|;
name|fields
operator|=
literal|null
operator|==
name|defaultSearchField
condition|?
operator|new
name|String
index|[]
block|{}
else|:
operator|new
name|String
index|[]
block|{
name|defaultSearchField
block|}
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|=
name|defaultFields
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|fields
index|[
literal|0
index|]
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
comment|// create a Java regular expression from the wildcard string
name|String
name|fieldRegex
init|=
name|fields
index|[
literal|0
index|]
operator|.
name|replaceAll
argument_list|(
literal|"\\*"
argument_list|,
literal|".*"
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|storedHighlightFieldNames
init|=
name|request
operator|.
name|getSearcher
argument_list|()
operator|.
name|getStoredHighlightFieldNames
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|storedFieldsToHighlight
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|storedFieldName
range|:
name|storedHighlightFieldNames
control|)
block|{
if|if
condition|(
name|storedFieldName
operator|.
name|matches
argument_list|(
name|fieldRegex
argument_list|)
condition|)
block|{
name|storedFieldsToHighlight
operator|.
name|add
argument_list|(
name|storedFieldName
argument_list|)
expr_stmt|;
block|}
block|}
name|fields
operator|=
name|storedFieldsToHighlight
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if there's a single request/handler value, it may be a space/comma separated list
name|fields
operator|=
name|SolrPluginUtils
operator|.
name|split
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
DECL|method|emptyArray
specifier|protected
name|boolean
name|emptyArray
parameter_list|(
name|String
index|[]
name|arr
parameter_list|)
block|{
return|return
operator|(
name|arr
operator|==
literal|null
operator|||
name|arr
operator|.
name|length
operator|==
literal|0
operator|||
name|arr
index|[
literal|0
index|]
operator|==
literal|null
operator|||
name|arr
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
comment|/**    * Generates a list of Highlighted query fragments for each item in a list    * of documents, or returns null if highlighting is disabled.    *    * @param docs query results    * @param query the query    * @param req the current request    * @param defaultFields default list of fields to summarize    *    * @return NamedList containing a NamedList for each document, which in    * turns contains sets (field, summary) pairs.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doHighlighting
specifier|public
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

