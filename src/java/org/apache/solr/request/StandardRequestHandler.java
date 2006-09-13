begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|*
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|StrUtils
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
name|util
operator|.
name|HighlightingUtils
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
name|apache
operator|.
name|solr
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
name|core
operator|.
name|SolrInfoMBean
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
name|core
operator|.
name|SolrException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrParams
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author yonik  * @version $Id$  *  * All of the following options may be configured for this handler  * in the solrconfig as defaults, and may be overriden as request parameters.  * (TODO: complete documentation of request parameters here, rather than only  * on the wiki).  *</p>  *  *<ul>  *<li> highlight - Set to any value not .equal() to "false" to enable highlight  * generation</li>  *<li> highlightFields - Set to a comma- or space-delimited list of fields to  * highlight.  If unspecified, uses the default query field</li>  *<li> maxSnippets - maximum number of snippets to generate per field-highlight.  *</li>  *</ul>  *  */
end_comment

begin_class
DECL|class|StandardRequestHandler
specifier|public
class|class
name|StandardRequestHandler
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
comment|// statistics
comment|// TODO: should we bother synchronizing these, or is an off-by-one error
comment|// acceptable every million requests or so?
DECL|field|numRequests
name|long
name|numRequests
decl_stmt|;
DECL|field|numErrors
name|long
name|numErrors
decl_stmt|;
DECL|field|defaults
name|SolrParams
name|defaults
decl_stmt|;
DECL|field|appends
name|SolrParams
name|appends
decl_stmt|;
DECL|field|invariants
name|SolrParams
name|invariants
decl_stmt|;
comment|/** shorten the class references for utilities */
DECL|class|U
specifier|private
specifier|static
class|class
name|U
extends|extends
name|SolrPluginUtils
block|{
comment|/* :NOOP */
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Object
name|o
init|=
name|args
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"appends"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|appends
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"invariants"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|invariants
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
try|try
block|{
name|U
operator|.
name|setDefaults
argument_list|(
name|req
argument_list|,
name|defaults
argument_list|,
name|appends
argument_list|,
name|invariants
argument_list|)
expr_stmt|;
name|SolrParams
name|p
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|sreq
init|=
name|p
operator|.
name|get
argument_list|(
name|Q
argument_list|)
decl_stmt|;
name|String
name|defaultField
init|=
name|p
operator|.
name|get
argument_list|(
name|DF
argument_list|)
decl_stmt|;
comment|// find fieldnames to return (fieldlist)
name|String
name|fl
init|=
name|p
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|FL
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fl
operator|!=
literal|null
condition|)
block|{
name|flags
operator||=
name|U
operator|.
name|setReturnFields
argument_list|(
name|fl
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sreq
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"Missing queryString"
argument_list|)
throw|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|sreq
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
name|String
name|qs
init|=
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|1
condition|?
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|""
decl_stmt|;
name|Query
name|query
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|qs
argument_list|,
name|defaultField
argument_list|,
name|p
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
comment|// If the first non-query, non-filter command is a simple sort on an indexed field, then
comment|// we can use the Lucene sort ability.
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|QueryParsing
operator|.
name|SortSpec
name|sortSpec
init|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortSpec
operator|!=
literal|null
condition|)
block|{
name|sort
operator|=
name|sortSpec
operator|.
name|getSort
argument_list|()
expr_stmt|;
comment|// ignore the count for now... it's currently only controlled by start& limit on req
comment|// count = sortSpec.getCount();
block|}
block|}
name|DocListAndSet
name|results
init|=
operator|new
name|DocListAndSet
argument_list|()
decl_stmt|;
name|NamedList
name|facetInfo
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|filters
init|=
name|U
operator|.
name|parseFilterQueries
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|s
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getBool
argument_list|(
name|FACET
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|results
operator|=
name|s
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
name|filters
argument_list|,
name|sort
argument_list|,
name|p
operator|.
name|getInt
argument_list|(
name|START
argument_list|,
literal|0
argument_list|)
argument_list|,
name|p
operator|.
name|getInt
argument_list|(
name|ROWS
argument_list|,
literal|10
argument_list|)
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|facetInfo
operator|=
name|getFacetInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|results
operator|.
name|docSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|.
name|docList
operator|=
name|s
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|filters
argument_list|,
name|sort
argument_list|,
name|p
operator|.
name|getInt
argument_list|(
name|START
argument_list|,
literal|0
argument_list|)
argument_list|,
name|p
operator|.
name|getInt
argument_list|(
name|ROWS
argument_list|,
literal|10
argument_list|)
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|results
operator|.
name|docList
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetInfo
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"facet_counts"
argument_list|,
name|facetInfo
argument_list|)
expr_stmt|;
try|try
block|{
name|NamedList
name|dbg
init|=
name|U
operator|.
name|doStandardDebug
argument_list|(
name|req
argument_list|,
name|qs
argument_list|,
name|query
argument_list|,
name|results
operator|.
name|docList
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|dbg
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|filters
condition|)
block|{
name|dbg
operator|.
name|add
argument_list|(
literal|"filter_queries"
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|FQ
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fqs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|fq
range|:
name|filters
control|)
block|{
name|fqs
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|toString
argument_list|(
name|fq
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dbg
operator|.
name|add
argument_list|(
literal|"parsed_filter_queries"
argument_list|,
name|fqs
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|dbg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception durring debug"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"exception_during_debug"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NamedList
name|sumData
init|=
name|HighlightingUtils
operator|.
name|doHighlighting
argument_list|(
name|results
operator|.
name|docList
argument_list|,
name|query
argument_list|,
name|req
argument_list|,
operator|new
name|String
index|[]
block|{
name|defaultField
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|sumData
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"highlighting"
argument_list|,
name|sumData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
return|return;
block|}
block|}
comment|/**    * Fetches information about Facets for this request.    *    * Subclasses may with to override this method to provide more     * advanced faceting behavior.    * @see SimpleFacets#getFacetCounts    */
DECL|method|getFacetInfo
specifier|protected
name|NamedList
name|getFacetInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|DocSet
name|mainSet
parameter_list|)
block|{
name|SimpleFacets
name|f
init|=
operator|new
name|SimpleFacets
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|mainSet
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getFacetCounts
argument_list|()
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|StandardRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"The standard Solr request handler"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class

end_unit

