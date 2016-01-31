begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|QueryValueSource
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
name|*
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
name|function
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Create a range query over a function.  *<br>Other parameters:  *<br><code>l</code>, the lower bound, optional)  *<br><code>u</code>, the upper bound, optional)  *<br><code>incl</code>, include the lower bound: true/false, optional, default=true  *<br><code>incu</code>, include the upper bound: true/false, optional, default=true  *<br>Example:<code>{!frange l=1000 u=50000}myfield</code>  *<br>Filter query example:<code>fq={!frange l=0 u=2.2}sum(user_ranking,editor_ranking)</code>   */
end_comment

begin_class
DECL|class|FunctionRangeQParserPlugin
specifier|public
class|class
name|FunctionRangeQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"frange"
decl_stmt|;
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|QParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
block|{
name|ValueSource
name|vs
decl_stmt|;
name|String
name|funcStr
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|funcStr
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Query
name|funcQ
init|=
name|subQuery
argument_list|(
name|funcStr
argument_list|,
name|FunctionQParserPlugin
operator|.
name|NAME
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|funcQ
operator|instanceof
name|FunctionQuery
condition|)
block|{
name|vs
operator|=
operator|(
operator|(
name|FunctionQuery
operator|)
name|funcQ
operator|)
operator|.
name|getValueSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|QueryValueSource
argument_list|(
name|funcQ
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|String
name|l
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"l"
argument_list|)
decl_stmt|;
name|String
name|u
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"u"
argument_list|)
decl_stmt|;
name|boolean
name|includeLower
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"incl"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|includeUpper
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"incu"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// TODO: add a score=val option to allow score to be the value
name|ValueSourceRangeFilter
name|rf
init|=
operator|new
name|ValueSourceRangeFilter
argument_list|(
name|vs
argument_list|,
name|l
argument_list|,
name|u
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
decl_stmt|;
name|FunctionRangeQuery
name|frq
init|=
operator|new
name|FunctionRangeQuery
argument_list|(
name|rf
argument_list|)
decl_stmt|;
return|return
name|frq
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

