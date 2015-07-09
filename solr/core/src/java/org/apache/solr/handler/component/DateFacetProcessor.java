begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|SolrException
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
name|FacetParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|SimpleFacets
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|TrieDateField
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
name|DocSet
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
name|SyntaxError
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
name|DateMathParser
import|;
end_import

begin_comment
comment|/**  * Process date facets  *  * @deprecated the whole date faceting feature is deprecated. Use range facets instead which can  * already work with dates.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|DateFacetProcessor
specifier|public
class|class
name|DateFacetProcessor
extends|extends
name|SimpleFacets
block|{
DECL|method|DateFacetProcessor
specifier|public
name|DateFacetProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|docs
argument_list|,
name|params
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated Use getFacetRangeCounts which is more generalized    */
annotation|@
name|Deprecated
DECL|method|getFacetDateCounts
specifier|public
name|void
name|getFacetDateCounts
parameter_list|(
name|String
name|dateFacet
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resOuter
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|ParsedParams
name|parsed
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parsed
operator|=
name|parseParams
argument_list|(
name|FacetParams
operator|.
name|FACET_DATE
argument_list|,
name|dateFacet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|syntaxError
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|syntaxError
argument_list|)
throw|;
block|}
specifier|final
name|SolrParams
name|params
init|=
name|parsed
operator|.
name|params
decl_stmt|;
specifier|final
name|SolrParams
name|required
init|=
name|parsed
operator|.
name|required
decl_stmt|;
specifier|final
name|String
name|key
init|=
name|parsed
operator|.
name|key
decl_stmt|;
specifier|final
name|String
name|f
init|=
name|parsed
operator|.
name|facetValue
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resInner
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|resOuter
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|resInner
argument_list|)
expr_stmt|;
specifier|final
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|sf
operator|.
name|getType
argument_list|()
operator|instanceof
name|TrieDateField
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can not date facet on a field which is not a TrieDateField: "
operator|+
name|f
argument_list|)
throw|;
block|}
specifier|final
name|TrieDateField
name|ft
init|=
operator|(
name|TrieDateField
operator|)
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|String
name|startS
init|=
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_START
argument_list|)
decl_stmt|;
specifier|final
name|Date
name|start
decl_stmt|;
try|try
block|{
name|start
operator|=
name|ft
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|startS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet 'start' is not a valid Date string: "
operator|+
name|startS
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|String
name|endS
init|=
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_END
argument_list|)
decl_stmt|;
name|Date
name|end
decl_stmt|;
comment|// not final, hardend may change this
try|try
block|{
name|end
operator|=
name|ft
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|endS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet 'end' is not a valid Date string: "
operator|+
name|endS
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|end
operator|.
name|before
argument_list|(
name|start
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet 'end' comes before 'start': "
operator|+
name|endS
operator|+
literal|"< "
operator|+
name|startS
argument_list|)
throw|;
block|}
specifier|final
name|String
name|gap
init|=
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_GAP
argument_list|)
decl_stmt|;
specifier|final
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|()
decl_stmt|;
specifier|final
name|int
name|minCount
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_MINCOUNT
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
index|[]
name|iStrs
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_INCLUDE
argument_list|)
decl_stmt|;
comment|// Legacy support for default of [lower,upper,edge] for date faceting
comment|// this is not handled by FacetRangeInclude.parseParam because
comment|// range faceting has differnet defaults
specifier|final
name|EnumSet
argument_list|<
name|FacetParams
operator|.
name|FacetRangeInclude
argument_list|>
name|include
init|=
operator|(
literal|null
operator|==
name|iStrs
operator|||
literal|0
operator|==
name|iStrs
operator|.
name|length
operator|)
condition|?
name|EnumSet
operator|.
name|of
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|,
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|,
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
else|:
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|parseParam
argument_list|(
name|iStrs
argument_list|)
decl_stmt|;
try|try
block|{
name|Date
name|low
init|=
name|start
decl_stmt|;
while|while
condition|(
name|low
operator|.
name|before
argument_list|(
name|end
argument_list|)
condition|)
block|{
name|dmp
operator|.
name|setNow
argument_list|(
name|low
argument_list|)
expr_stmt|;
name|String
name|label
init|=
name|ft
operator|.
name|toExternal
argument_list|(
name|low
argument_list|)
decl_stmt|;
name|Date
name|high
init|=
name|dmp
operator|.
name|parseMath
argument_list|(
name|gap
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|.
name|before
argument_list|(
name|high
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_HARD_END
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|high
operator|=
name|end
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
name|high
expr_stmt|;
block|}
block|}
if|if
condition|(
name|high
operator|.
name|before
argument_list|(
name|low
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet infinite loop (is gap negative?)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|high
operator|.
name|equals
argument_list|(
name|low
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet infinite loop: gap is effectively zero"
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|includeLower
init|=
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|&&
name|low
operator|.
name|equals
argument_list|(
name|start
argument_list|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|boolean
name|includeUpper
init|=
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|&&
name|high
operator|.
name|equals
argument_list|(
name|end
argument_list|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|rangeCount
argument_list|(
name|parsed
argument_list|,
name|sf
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|minCount
condition|)
block|{
name|resInner
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|low
operator|=
name|high
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|text
operator|.
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"date facet 'gap' is not a valid Date Math string: "
operator|+
name|gap
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// explicitly return the gap and end so all the counts
comment|// (including before/after/between) are meaningful - even if mincount
comment|// has removed the neighboring ranges
name|resInner
operator|.
name|add
argument_list|(
literal|"gap"
argument_list|,
name|gap
argument_list|)
expr_stmt|;
name|resInner
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|resInner
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|end
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|othersP
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_DATE_OTHER
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|othersP
operator|&&
literal|0
operator|<
name|othersP
operator|.
name|length
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|FacetParams
operator|.
name|FacetRangeOther
argument_list|>
name|others
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|o
range|:
name|othersP
control|)
block|{
name|others
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|get
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// no matter what other values are listed, we don't do
comment|// anything if "none" is specified.
if|if
condition|(
operator|!
name|others
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|NONE
argument_list|)
condition|)
block|{
name|boolean
name|all
init|=
name|others
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|ALL
argument_list|)
decl_stmt|;
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|BEFORE
argument_list|)
condition|)
block|{
comment|// include upper bound if "outer" or if first gap doesn't already include it
name|resInner
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|BEFORE
operator|.
name|toString
argument_list|()
argument_list|,
name|rangeCount
argument_list|(
name|parsed
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
literal|false
argument_list|,
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|OUTER
argument_list|)
operator|||
operator|(
operator|!
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
operator|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|AFTER
argument_list|)
condition|)
block|{
comment|// include lower bound if "outer" or if last gap doesn't already include it
name|resInner
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|AFTER
operator|.
name|toString
argument_list|()
argument_list|,
name|rangeCount
argument_list|(
name|parsed
argument_list|,
name|sf
argument_list|,
name|end
argument_list|,
literal|null
argument_list|,
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|OUTER
argument_list|)
operator|||
operator|(
operator|!
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
operator|)
operator|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|BETWEEN
argument_list|)
condition|)
block|{
name|resInner
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FacetRangeOther
operator|.
name|BETWEEN
operator|.
name|toString
argument_list|()
argument_list|,
name|rangeCount
argument_list|(
name|parsed
argument_list|,
name|sf
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
argument_list|,
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetParams
operator|.
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Returns a list of value constraints and the associated facet counts    * for each facet date field, range, and interval specified in the    * SolrParams    *    * @see FacetParams#FACET_DATE    * @deprecated Use getFacetRangeCounts which is more generalized    */
annotation|@
name|Deprecated
DECL|method|getFacetDateCounts
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getFacetDateCounts
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resOuter
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|fields
init|=
name|global
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_DATE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fields
operator|||
literal|0
operator|==
name|fields
operator|.
name|length
condition|)
return|return
name|resOuter
return|;
for|for
control|(
name|String
name|f
range|:
name|fields
control|)
block|{
name|getFacetDateCounts
argument_list|(
name|f
argument_list|,
name|resOuter
argument_list|)
expr_stmt|;
block|}
return|return
name|resOuter
return|;
block|}
comment|/**    * @deprecated Use rangeCount(SchemaField,String,String,boolean,boolean) which is more generalized    */
annotation|@
name|Deprecated
DECL|method|rangeCount
specifier|protected
name|int
name|rangeCount
parameter_list|(
name|ParsedParams
name|parsed
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|Date
name|low
parameter_list|,
name|Date
name|high
parameter_list|,
name|boolean
name|iLow
parameter_list|,
name|boolean
name|iHigh
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|rangeQ
init|=
operator|(
call|(
name|TrieDateField
call|)
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|sf
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|iLow
argument_list|,
name|iHigh
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|numDocs
argument_list|(
name|rangeQ
argument_list|,
name|parsed
operator|.
name|docs
argument_list|)
return|;
block|}
block|}
end_class

end_unit
