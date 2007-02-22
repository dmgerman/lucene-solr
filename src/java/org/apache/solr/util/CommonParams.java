begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

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
name|SolrParams
import|;
end_import

begin_comment
comment|/**  * A collection on common params, both for Plugin initialization and  * for Requests.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|CommonParams
specifier|public
class|class
name|CommonParams
block|{
annotation|@
name|Deprecated
DECL|field|FL
specifier|public
specifier|static
name|String
name|FL
init|=
literal|"fl"
decl_stmt|;
comment|/** default query field */
annotation|@
name|Deprecated
DECL|field|DF
specifier|public
specifier|static
name|String
name|DF
init|=
literal|"df"
decl_stmt|;
comment|/** whether to include debug data */
annotation|@
name|Deprecated
DECL|field|DEBUG_QUERY
specifier|public
specifier|static
name|String
name|DEBUG_QUERY
init|=
literal|"debugQuery"
decl_stmt|;
comment|/** another query to explain against */
annotation|@
name|Deprecated
DECL|field|EXPLAIN_OTHER
specifier|public
specifier|static
name|String
name|EXPLAIN_OTHER
init|=
literal|"explainOther"
decl_stmt|;
comment|/** the default field list to be used */
DECL|field|fl
specifier|public
name|String
name|fl
init|=
literal|null
decl_stmt|;
comment|/** the default field to query */
DECL|field|df
specifier|public
name|String
name|df
init|=
literal|null
decl_stmt|;
comment|/** do not debug by default **/
DECL|field|debugQuery
specifier|public
name|String
name|debugQuery
init|=
literal|null
decl_stmt|;
comment|/** no default other explanation query **/
DECL|field|explainOther
specifier|public
name|String
name|explainOther
init|=
literal|null
decl_stmt|;
comment|/** whether to highlight */
DECL|field|highlight
specifier|public
name|boolean
name|highlight
init|=
literal|false
decl_stmt|;
comment|/** fields to highlight */
DECL|field|highlightFields
specifier|public
name|String
name|highlightFields
init|=
literal|null
decl_stmt|;
comment|/** maximum highlight fragments to return */
DECL|field|maxSnippets
specifier|public
name|int
name|maxSnippets
init|=
literal|1
decl_stmt|;
comment|/** override default highlight Formatter class */
DECL|field|highlightFormatterClass
specifier|public
name|String
name|highlightFormatterClass
init|=
literal|null
decl_stmt|;
DECL|method|CommonParams
specifier|public
name|CommonParams
parameter_list|()
block|{
comment|/* :NOOP: */
block|}
comment|/** @see #setValues */
DECL|method|CommonParams
specifier|public
name|CommonParams
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setValues
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the params using values from a NamedList, usefull in the    * init method for your handler.    *    *<p>    * If any param is not of the expected type, a severe error is    * logged,and the param is skipped.    *</p>    *    *<p>    * If any param is not of in the NamedList, it is skipped and the    * old value is left alone.    *</p>    *    */
DECL|method|setValues
specifier|public
name|void
name|setValues
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Object
name|tmp
decl_stmt|;
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|FL
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|fl
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|SolrParams
operator|.
name|FL
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|DF
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|df
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|SolrParams
operator|.
name|DF
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|DEBUG_QUERY
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|debugQuery
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|SolrParams
operator|.
name|DEBUG_QUERY
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|EXPLAIN_OTHER
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|explainOther
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|SolrParams
operator|.
name|EXPLAIN_OTHER
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

