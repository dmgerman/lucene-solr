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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|surround
operator|.
name|parser
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
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
operator|.
name|*
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

begin_comment
comment|/**  * Plugin for lucene/contrib Surround query parser, bringing SpanQuery support  * to Solr.  *<p>  *&lt;queryParser name="surround"  * class="org.apache.solr.search.SurroundQParserPlugin" /&gt;  *<p>  * Note that the query string is not analyzed in any way  *   * @see QueryParser  * @since 4.0  */
end_comment

begin_class
DECL|class|SurroundQParserPlugin
specifier|public
class|class
name|SurroundQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"surround"
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
name|SurroundQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|SurroundQParser
specifier|static
class|class
name|SurroundQParser
extends|extends
name|QParser
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|SurroundQParserPlugin
operator|.
name|log
decl_stmt|;
DECL|field|DEFMAXBASICQUERIES
specifier|static
specifier|final
name|int
name|DEFMAXBASICQUERIES
init|=
literal|1000
decl_stmt|;
DECL|field|MBQParam
specifier|static
specifier|final
name|String
name|MBQParam
init|=
literal|"maxBasicQueries"
decl_stmt|;
DECL|field|sortStr
name|String
name|sortStr
decl_stmt|;
DECL|field|lparser
name|SolrQueryParser
name|lparser
decl_stmt|;
DECL|field|maxBasicQueries
name|int
name|maxBasicQueries
decl_stmt|;
DECL|method|SurroundQParser
specifier|public
name|SurroundQParser
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
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|SrndQuery
name|sq
decl_stmt|;
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qstr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|mbqparam
init|=
name|getParam
argument_list|(
name|MBQParam
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbqparam
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|maxBasicQueries
operator|=
name|DEFMAXBASICQUERIES
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|this
operator|.
name|maxBasicQueries
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|mbqparam
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
name|warn
argument_list|(
literal|"Couldn't parse maxBasicQueries value "
operator|+
name|mbqparam
operator|+
literal|", using default of 1000"
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxBasicQueries
operator|=
name|DEFMAXBASICQUERIES
expr_stmt|;
block|}
block|}
comment|// ugh .. colliding ParseExceptions
try|try
block|{
name|sq
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|parser
operator|.
name|QueryParser
operator|.
name|parse
argument_list|(
name|qstr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|parser
operator|.
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|pe
argument_list|)
throw|;
block|}
comment|// so what do we do with the SrndQuery ??
comment|// processing based on example in LIA Ch 9
name|BasicQueryFactory
name|bqFactory
init|=
operator|new
name|BasicQueryFactory
argument_list|(
name|this
operator|.
name|maxBasicQueries
argument_list|)
decl_stmt|;
name|String
name|defaultField
init|=
name|getParam
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
decl_stmt|;
name|Query
name|lquery
init|=
name|sq
operator|.
name|makeLuceneQueryField
argument_list|(
name|defaultField
argument_list|,
name|bqFactory
argument_list|)
decl_stmt|;
return|return
name|lquery
return|;
block|}
block|}
block|}
end_class

end_unit

