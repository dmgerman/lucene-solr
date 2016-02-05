begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

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
name|Arrays
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|RequiredSolrParams
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
name|search
operator|.
name|QueryParsing
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
name|SolrReturnFields
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
name|StrParser
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

begin_class
DECL|class|LegacyFacet
specifier|public
class|class
name|LegacyFacet
block|{
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|json
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
decl_stmt|;
DECL|field|currentCommand
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currentCommand
init|=
literal|null
decl_stmt|;
comment|// always points to the current facet command
DECL|field|currentSubs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currentSubs
decl_stmt|;
comment|// always points to the current facet:{} block
DECL|field|facetValue
name|String
name|facetValue
decl_stmt|;
DECL|field|key
name|String
name|key
decl_stmt|;
DECL|field|localParams
name|SolrParams
name|localParams
decl_stmt|;
DECL|field|orig
name|SolrParams
name|orig
decl_stmt|;
DECL|field|required
name|SolrParams
name|required
decl_stmt|;
DECL|field|subFacets
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Subfacet
argument_list|>
argument_list|>
name|subFacets
decl_stmt|;
comment|// only parsed once
DECL|method|LegacyFacet
specifier|public
name|LegacyFacet
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|orig
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|json
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentSubs
operator|=
name|json
expr_stmt|;
block|}
DECL|method|getLegacy
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getLegacy
parameter_list|()
block|{
name|subFacets
operator|=
name|parseSubFacets
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries
init|=
name|params
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|queries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|q
range|:
name|queries
control|)
block|{
name|addQueryFacet
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|fields
init|=
name|params
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|addFieldFacet
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|ranges
init|=
name|params
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_RANGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranges
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|range
range|:
name|ranges
control|)
block|{
name|addRangeFacet
argument_list|(
name|range
argument_list|)
expr_stmt|;
block|}
block|}
comment|// SolrCore.log.error("###################### JSON FACET:" + json);
return|return
name|json
return|;
block|}
DECL|class|Subfacet
specifier|protected
specifier|static
class|class
name|Subfacet
block|{
DECL|field|parentKey
specifier|public
name|String
name|parentKey
decl_stmt|;
DECL|field|type
specifier|public
name|String
name|type
decl_stmt|;
comment|// query, range, field
DECL|field|value
specifier|public
name|String
name|value
decl_stmt|;
comment|// the actual field or the query, including possible local params
block|}
DECL|method|parseSubFacets
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Subfacet
argument_list|>
argument_list|>
name|parseSubFacets
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Subfacet
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
name|String
name|SUBFACET
init|=
literal|"subfacet."
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|SUBFACET
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|key
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|!=
literal|3
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
literal|"expected subfacet parameter name of the form subfacet.mykey.field, got:"
operator|+
name|key
argument_list|)
throw|;
block|}
name|Subfacet
name|sub
init|=
operator|new
name|Subfacet
argument_list|()
decl_stmt|;
name|sub
operator|.
name|parentKey
operator|=
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|sub
operator|.
name|type
operator|=
name|parts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|sub
operator|.
name|value
operator|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Subfacet
argument_list|>
name|subs
init|=
name|map
operator|.
name|get
argument_list|(
name|sub
operator|.
name|parentKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
block|{
name|subs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|subs
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|sub
operator|.
name|parentKey
argument_list|,
name|subs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
DECL|method|addQueryFacet
specifier|protected
name|void
name|addQueryFacet
parameter_list|(
name|String
name|q
parameter_list|)
block|{
name|parseParams
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cmd
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|type
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|type
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|addSub
argument_list|(
name|key
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|handleSubs
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|addRangeFacet
specifier|protected
name|void
name|addRangeFacet
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|parseParams
argument_list|(
name|FacetParams
operator|.
name|FACET_RANGE
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cmd
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|type
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|type
operator|.
name|put
argument_list|(
literal|"range"
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|key
decl_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
name|facetValue
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"start"
argument_list|,
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_START
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"end"
argument_list|,
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_END
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"gap"
argument_list|,
name|required
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_GAP
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|p
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_OTHER
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"other"
argument_list|,
name|p
operator|.
name|length
operator|==
literal|1
condition|?
name|p
index|[
literal|0
index|]
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_INCLUDE
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"include"
argument_list|,
name|p
operator|.
name|length
operator|==
literal|1
condition|?
name|p
index|[
literal|0
index|]
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|mincount
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
name|cmd
operator|.
name|put
argument_list|(
literal|"mincount"
argument_list|,
name|mincount
argument_list|)
expr_stmt|;
name|boolean
name|hardend
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_RANGE_HARD_END
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|hardend
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"hardend"
argument_list|,
name|hardend
argument_list|)
expr_stmt|;
name|addSub
argument_list|(
name|key
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|handleSubs
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|addFieldFacet
specifier|protected
name|void
name|addFieldFacet
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|parseParams
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|key
decl_stmt|;
comment|// the parameter to use for per-field parameters... f.key.facet.limit=10
name|int
name|offset
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_OFFSET
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|limit
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_LIMIT
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|mincount
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
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|missing
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_MISSING
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// default to sorting if there is a limit.
name|String
name|sort
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_SORT
argument_list|,
name|limit
operator|>
literal|0
condition|?
name|FacetParams
operator|.
name|FACET_SORT_COUNT
else|:
name|FacetParams
operator|.
name|FACET_SORT_INDEX
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|f
argument_list|,
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cmd
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
name|facetValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|0
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"offset"
argument_list|,
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|limit
operator|!=
literal|10
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"limit"
argument_list|,
name|limit
argument_list|)
expr_stmt|;
if|if
condition|(
name|mincount
operator|!=
literal|1
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"mincount"
argument_list|,
name|mincount
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
name|cmd
operator|.
name|put
argument_list|(
literal|"prefix"
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|sort
operator|.
name|equals
argument_list|(
literal|"count"
argument_list|)
condition|)
block|{
comment|// our default
block|}
elseif|else
if|if
condition|(
name|sort
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
literal|"index asc"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
name|sort
argument_list|)
expr_stmt|;
comment|// can be sort by one of our stats
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|type
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|type
operator|.
name|put
argument_list|(
literal|"terms"
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|addSub
argument_list|(
name|key
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|handleSubs
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|handleSubs
specifier|private
name|void
name|handleSubs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cmd
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|savedCmd
init|=
name|currentCommand
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|savedSubs
init|=
name|currentSubs
decl_stmt|;
try|try
block|{
name|currentCommand
operator|=
name|cmd
expr_stmt|;
name|currentSubs
operator|=
literal|null
expr_stmt|;
comment|// parse stats for this facet
name|String
index|[]
name|stats
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|key
argument_list|,
literal|"facet.stat"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|stat
range|:
name|stats
control|)
block|{
name|addStat
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Subfacet
argument_list|>
name|subs
init|=
name|subFacets
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Subfacet
name|subfacet
range|:
name|subs
control|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|subfacet
operator|.
name|type
argument_list|)
condition|)
block|{
name|addFieldFacet
argument_list|(
name|subfacet
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|subfacet
operator|.
name|type
argument_list|)
condition|)
block|{
name|addQueryFacet
argument_list|(
name|subfacet
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"range"
operator|.
name|equals
argument_list|(
name|subfacet
operator|.
name|type
argument_list|)
condition|)
block|{
name|addQueryFacet
argument_list|(
name|subfacet
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|currentCommand
operator|=
name|savedCmd
expr_stmt|;
name|currentSubs
operator|=
name|savedSubs
expr_stmt|;
block|}
block|}
DECL|method|addStat
specifier|private
name|void
name|addStat
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|StrParser
name|sp
init|=
operator|new
name|StrParser
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
if|if
condition|(
name|sp
operator|.
name|pos
operator|>=
name|sp
operator|.
name|end
condition|)
name|addStat
argument_list|(
name|val
argument_list|,
name|val
argument_list|)
expr_stmt|;
comment|// try key:func() format
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|funcStr
init|=
name|val
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|SolrReturnFields
operator|.
name|getFieldName
argument_list|(
name|sp
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
name|sp
operator|.
name|opt
argument_list|(
literal|':'
argument_list|)
condition|)
block|{
comment|// OK, we got the key
name|funcStr
operator|=
name|val
operator|.
name|substring
argument_list|(
name|sp
operator|.
name|pos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// an invalid key... it must not be present.
name|sp
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|key
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|funcStr
expr_stmt|;
comment|// not really ideal
block|}
name|addStat
argument_list|(
name|key
argument_list|,
name|funcStr
argument_list|)
expr_stmt|;
block|}
DECL|method|addStat
specifier|private
name|void
name|addStat
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
operator|||
literal|"count()"
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
return|return;
comment|// we no longer have a count function, we always return the count
name|getCurrentSubs
argument_list|()
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|addSub
specifier|private
name|void
name|addSub
parameter_list|(
name|String
name|key
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sub
parameter_list|)
block|{
name|getCurrentSubs
argument_list|()
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
DECL|method|getCurrentSubs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getCurrentSubs
parameter_list|()
block|{
if|if
condition|(
name|currentSubs
operator|==
literal|null
condition|)
block|{
name|currentSubs
operator|=
operator|new
name|LinkedHashMap
argument_list|()
expr_stmt|;
name|currentCommand
operator|.
name|put
argument_list|(
literal|"facet"
argument_list|,
name|currentSubs
argument_list|)
expr_stmt|;
block|}
return|return
name|currentSubs
return|;
block|}
DECL|method|parseParams
specifier|protected
name|void
name|parseParams
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|facetValue
operator|=
name|param
expr_stmt|;
name|key
operator|=
name|param
expr_stmt|;
try|try
block|{
name|localParams
operator|=
name|QueryParsing
operator|.
name|getLocalParams
argument_list|(
name|param
argument_list|,
name|orig
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|==
literal|null
condition|)
block|{
name|params
operator|=
name|orig
expr_stmt|;
name|required
operator|=
operator|new
name|RequiredSolrParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// setupStats();
return|return;
block|}
name|params
operator|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|localParams
argument_list|,
name|orig
argument_list|)
expr_stmt|;
name|required
operator|=
operator|new
name|RequiredSolrParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// remove local params unless it's a query
if|if
condition|(
name|type
operator|!=
name|FacetParams
operator|.
name|FACET_QUERY
condition|)
block|{
name|facetValue
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|VALUE
argument_list|)
expr_stmt|;
block|}
comment|// reset set the default key now that localParams have been removed
name|key
operator|=
name|facetValue
expr_stmt|;
comment|// allow explicit set of the key
name|key
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|OUTPUT_KEY
argument_list|,
name|key
argument_list|)
expr_stmt|;
comment|// setupStats();
block|}
catch|catch
parameter_list|(
name|SyntaxError
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

