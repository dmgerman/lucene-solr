begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|ArrayList
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
name|List
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|TrieField
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
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_class
DECL|class|FacetRange
specifier|public
class|class
name|FacetRange
extends|extends
name|FacetRequest
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|start
name|Object
name|start
decl_stmt|;
DECL|field|end
name|Object
name|end
decl_stmt|;
DECL|field|gap
name|Object
name|gap
decl_stmt|;
DECL|field|hardend
name|boolean
name|hardend
init|=
literal|false
decl_stmt|;
DECL|field|include
name|EnumSet
argument_list|<
name|FacetParams
operator|.
name|FacetRangeInclude
argument_list|>
name|include
decl_stmt|;
DECL|field|others
name|EnumSet
argument_list|<
name|FacetParams
operator|.
name|FacetRangeOther
argument_list|>
name|others
decl_stmt|;
DECL|field|mincount
name|long
name|mincount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|createFacetProcessor
specifier|public
name|FacetProcessor
name|createFacetProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
return|return
operator|new
name|FacetRangeProcessor
argument_list|(
name|fcontext
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|FacetRangeMerger
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|FacetRangeProcessor
class|class
name|FacetRangeProcessor
extends|extends
name|FacetProcessor
argument_list|<
name|FacetRange
argument_list|>
block|{
DECL|field|sf
name|SchemaField
name|sf
decl_stmt|;
DECL|field|calc
name|Calc
name|calc
decl_stmt|;
DECL|field|rangeList
name|List
argument_list|<
name|Range
argument_list|>
name|rangeList
decl_stmt|;
DECL|field|otherList
name|List
argument_list|<
name|Range
argument_list|>
name|otherList
decl_stmt|;
DECL|field|effectiveMincount
name|long
name|effectiveMincount
decl_stmt|;
DECL|method|FacetRangeProcessor
name|FacetRangeProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetRange
name|freq
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Under the normal mincount=0, each shard will need to return 0 counts since we don't calculate buckets at the top level.
comment|// But if mincount>0 then our sub mincount can be set to 1.
name|effectiveMincount
operator|=
name|fcontext
operator|.
name|isShard
argument_list|()
condition|?
operator|(
name|freq
operator|.
name|mincount
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
else|:
name|freq
operator|.
name|mincount
expr_stmt|;
name|sf
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|freq
operator|.
name|field
argument_list|)
expr_stmt|;
name|response
operator|=
name|getRangeCounts
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponse
specifier|public
name|Object
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
DECL|class|Range
specifier|private
specifier|static
class|class
name|Range
block|{
DECL|field|label
name|Object
name|label
decl_stmt|;
DECL|field|low
name|Comparable
name|low
decl_stmt|;
DECL|field|high
name|Comparable
name|high
decl_stmt|;
DECL|field|includeLower
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
name|boolean
name|includeUpper
decl_stmt|;
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|Object
name|label
parameter_list|,
name|Comparable
name|low
parameter_list|,
name|Comparable
name|high
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
name|this
operator|.
name|low
operator|=
name|low
expr_stmt|;
name|this
operator|.
name|high
operator|=
name|high
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
block|}
block|}
DECL|method|getRangeCounts
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getRangeCounts
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|TrieField
condition|)
block|{
specifier|final
name|TrieField
name|trie
init|=
operator|(
name|TrieField
operator|)
name|ft
decl_stmt|;
switch|switch
condition|(
name|trie
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|FLOAT
case|:
name|calc
operator|=
operator|new
name|FloatCalc
argument_list|(
name|sf
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|calc
operator|=
operator|new
name|DoubleCalc
argument_list|(
name|sf
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|calc
operator|=
operator|new
name|IntCalc
argument_list|(
name|sf
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|calc
operator|=
operator|new
name|LongCalc
argument_list|(
name|sf
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|calc
operator|=
operator|new
name|DateCalc
argument_list|(
name|sf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
default|default:
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
literal|"Unable to range facet on tried field of unexpected type:"
operator|+
name|freq
operator|.
name|field
argument_list|)
throw|;
block|}
block|}
else|else
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
literal|"Unable to range facet on field:"
operator|+
name|sf
argument_list|)
throw|;
block|}
name|createRangeList
argument_list|()
expr_stmt|;
return|return
name|getRangeCountsIndexed
argument_list|()
return|;
block|}
DECL|method|createRangeList
specifier|private
name|void
name|createRangeList
parameter_list|()
throws|throws
name|IOException
block|{
name|rangeList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|otherList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Comparable
name|start
init|=
name|calc
operator|.
name|getValue
argument_list|(
name|freq
operator|.
name|start
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Comparable
name|end
init|=
name|calc
operator|.
name|getValue
argument_list|(
name|freq
operator|.
name|end
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|FacetParams
operator|.
name|FacetRangeInclude
argument_list|>
name|include
init|=
name|freq
operator|.
name|include
decl_stmt|;
name|String
name|gap
init|=
name|freq
operator|.
name|gap
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Comparable
name|low
init|=
name|start
decl_stmt|;
while|while
condition|(
name|low
operator|.
name|compareTo
argument_list|(
name|end
argument_list|)
operator|<
literal|0
condition|)
block|{
name|Comparable
name|high
init|=
name|calc
operator|.
name|addGap
argument_list|(
name|low
argument_list|,
name|gap
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|.
name|compareTo
argument_list|(
name|high
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|freq
operator|.
name|hardend
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
name|compareTo
argument_list|(
name|low
argument_list|)
operator|<
literal|0
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
literal|"range facet infinite loop (is gap negative? did the math overflow?)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|high
operator|.
name|compareTo
argument_list|(
name|low
argument_list|)
operator|==
literal|0
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
literal|"range facet infinite loop: gap is either zero, or too small relative start/end and caused underflow: "
operator|+
name|low
operator|+
literal|" + "
operator|+
name|gap
operator|+
literal|" = "
operator|+
name|high
argument_list|)
throw|;
block|}
name|boolean
name|incLower
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
literal|0
operator|==
name|low
operator|.
name|compareTo
argument_list|(
name|start
argument_list|)
operator|)
operator|)
decl_stmt|;
name|boolean
name|incUpper
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
literal|0
operator|==
name|high
operator|.
name|compareTo
argument_list|(
name|end
argument_list|)
operator|)
operator|)
decl_stmt|;
name|Range
name|range
init|=
operator|new
name|Range
argument_list|(
name|low
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|incLower
argument_list|,
name|incUpper
argument_list|)
decl_stmt|;
name|rangeList
operator|.
name|add
argument_list|(
name|range
argument_list|)
expr_stmt|;
name|low
operator|=
name|high
expr_stmt|;
block|}
comment|// no matter what other values are listed, we don't do
comment|// anything if "none" is specified.
if|if
condition|(
operator|!
name|freq
operator|.
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
name|freq
operator|.
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
name|freq
operator|.
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
name|boolean
name|incUpper
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
decl_stmt|;
name|otherList
operator|.
name|add
argument_list|(
operator|new
name|Range
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
literal|null
argument_list|,
name|start
argument_list|,
literal|false
argument_list|,
name|incUpper
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|||
name|freq
operator|.
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
name|boolean
name|incLower
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
decl_stmt|;
name|otherList
operator|.
name|add
argument_list|(
operator|new
name|Range
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
name|end
argument_list|,
literal|null
argument_list|,
name|incLower
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
name|freq
operator|.
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
name|boolean
name|incLower
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
decl_stmt|;
name|boolean
name|incUpper
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
decl_stmt|;
name|otherList
operator|.
name|add
argument_list|(
operator|new
name|Range
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
name|start
argument_list|,
name|end
argument_list|,
name|incLower
argument_list|,
name|incUpper
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRangeCountsIndexed
specifier|private
name|SimpleOrderedMap
name|getRangeCountsIndexed
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|slotCount
init|=
name|rangeList
operator|.
name|size
argument_list|()
operator|+
name|otherList
operator|.
name|size
argument_list|()
decl_stmt|;
name|intersections
operator|=
operator|new
name|DocSet
index|[
name|slotCount
index|]
expr_stmt|;
name|createAccs
argument_list|(
name|fcontext
operator|.
name|base
operator|.
name|size
argument_list|()
argument_list|,
name|slotCount
argument_list|)
expr_stmt|;
name|prepareForCollection
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|rangeList
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|rangeStats
argument_list|(
name|rangeList
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|otherList
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|rangeStats
argument_list|(
name|otherList
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|,
name|rangeList
operator|.
name|size
argument_list|()
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SimpleOrderedMap
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"buckets"
argument_list|,
name|buckets
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|rangeList
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|effectiveMincount
operator|>
literal|0
operator|&&
name|countAcc
operator|.
name|getCount
argument_list|(
name|idx
argument_list|)
operator|<
name|effectiveMincount
condition|)
continue|continue;
name|Range
name|range
init|=
name|rangeList
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
name|bucket
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|buckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|add
argument_list|(
literal|"val"
argument_list|,
name|range
operator|.
name|label
argument_list|)
expr_stmt|;
name|addStats
argument_list|(
name|bucket
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|doSubs
argument_list|(
name|bucket
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|otherList
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
comment|// we dont' skip these buckets based on mincount
name|Range
name|range
init|=
name|otherList
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
name|bucket
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|range
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|addStats
argument_list|(
name|bucket
argument_list|,
name|rangeList
operator|.
name|size
argument_list|()
operator|+
name|idx
argument_list|)
expr_stmt|;
name|doSubs
argument_list|(
name|bucket
argument_list|,
name|rangeList
operator|.
name|size
argument_list|()
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|field|intersections
specifier|private
name|DocSet
index|[]
name|intersections
decl_stmt|;
DECL|method|rangeStats
specifier|private
name|void
name|rangeStats
parameter_list|(
name|Range
name|range
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|rangeQ
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|sf
argument_list|,
name|range
operator|.
name|low
operator|==
literal|null
condition|?
literal|null
else|:
name|calc
operator|.
name|formatValue
argument_list|(
name|range
operator|.
name|low
argument_list|)
argument_list|,
name|range
operator|.
name|high
operator|==
literal|null
condition|?
literal|null
else|:
name|calc
operator|.
name|formatValue
argument_list|(
name|range
operator|.
name|high
argument_list|)
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|)
decl_stmt|;
comment|// TODO: specialize count only
name|DocSet
name|intersection
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSet
argument_list|(
name|rangeQ
argument_list|,
name|fcontext
operator|.
name|base
argument_list|)
decl_stmt|;
name|intersections
index|[
name|slot
index|]
operator|=
name|intersection
expr_stmt|;
comment|// save for later
name|int
name|num
init|=
name|collect
argument_list|(
name|intersection
argument_list|,
name|slot
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|slot
argument_list|,
name|num
argument_list|)
expr_stmt|;
comment|// TODO: roll this into collect()
block|}
DECL|method|doSubs
specifier|private
name|void
name|doSubs
parameter_list|(
name|SimpleOrderedMap
name|bucket
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
comment|// handle sub-facets for this bucket
if|if
condition|(
name|freq
operator|.
name|getSubFacets
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DocSet
name|subBase
init|=
name|intersections
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|subBase
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|FacetContext
name|subContext
init|=
name|fcontext
operator|.
name|sub
argument_list|()
decl_stmt|;
name|subContext
operator|.
name|base
operator|=
name|subBase
expr_stmt|;
try|try
block|{
name|fillBucketSubs
argument_list|(
name|bucket
argument_list|,
name|subContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// subContext.base.decref();  // OFF-HEAP
comment|// subContext.base = null;  // do not modify context after creation... there may be deferred execution (i.e. streaming)
block|}
block|}
block|}
DECL|method|rangeStats
specifier|private
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|rangeStats
parameter_list|(
name|Range
name|range
parameter_list|,
name|boolean
name|special
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// typically the start value of the range, but null for before/after/between
if|if
condition|(
operator|!
name|special
condition|)
block|{
name|bucket
operator|.
name|add
argument_list|(
literal|"val"
argument_list|,
name|range
operator|.
name|label
argument_list|)
expr_stmt|;
block|}
name|Query
name|rangeQ
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|sf
argument_list|,
name|range
operator|.
name|low
operator|==
literal|null
condition|?
literal|null
else|:
name|calc
operator|.
name|formatValue
argument_list|(
name|range
operator|.
name|low
argument_list|)
argument_list|,
name|range
operator|.
name|high
operator|==
literal|null
condition|?
literal|null
else|:
name|calc
operator|.
name|formatValue
argument_list|(
name|range
operator|.
name|high
argument_list|)
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|)
decl_stmt|;
name|fillBucket
argument_list|(
name|bucket
argument_list|,
name|rangeQ
argument_list|)
expr_stmt|;
return|return
name|bucket
return|;
block|}
comment|// Essentially copied from SimpleFacets...
comment|// would be nice to unify this stuff w/ analytics component...
comment|/**    * Perhaps someday instead of having a giant "instanceof" case    * statement to pick an impl, we can add a "RangeFacetable" marker    * interface to FieldTypes and they can return instances of these    * directly from some method -- but until then, keep this locked down    * and private.    */
DECL|class|Calc
specifier|private
specifier|static
specifier|abstract
class|class
name|Calc
block|{
DECL|field|field
specifier|protected
specifier|final
name|SchemaField
name|field
decl_stmt|;
DECL|method|Calc
specifier|public
name|Calc
parameter_list|(
specifier|final
name|SchemaField
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/**      * Formats a Range endpoint for use as a range label name in the response.      * Default Impl just uses toString()      */
DECL|method|formatValue
specifier|public
name|String
name|formatValue
parameter_list|(
specifier|final
name|Comparable
name|val
parameter_list|)
block|{
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Parses a String param into an Range endpoint value throwing      * an exception if not possible      */
DECL|method|getValue
specifier|public
specifier|final
name|Comparable
name|getValue
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
block|{
try|try
block|{
return|return
name|parseStr
argument_list|(
name|rawval
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Can't parse value "
operator|+
name|rawval
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parses a String param into an Range endpoint.      * Can throw a low level format exception as needed.      */
DECL|method|parseStr
specifier|protected
specifier|abstract
name|Comparable
name|parseStr
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
function_decl|;
comment|/**      * Parses a String param into a value that represents the gap and      * can be included in the response, throwing      * a useful exception if not possible.      *      * Note: uses Object as the return type instead of T for things like      * Date where gap is just a DateMathParser string      */
DECL|method|getGap
specifier|public
specifier|final
name|Object
name|getGap
parameter_list|(
specifier|final
name|String
name|gap
parameter_list|)
block|{
try|try
block|{
return|return
name|parseGap
argument_list|(
name|gap
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Can't parse gap "
operator|+
name|gap
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parses a String param into a value that represents the gap and      * can be included in the response.      * Can throw a low level format exception as needed.      *      * Default Impl calls parseVal      */
DECL|method|parseGap
specifier|protected
name|Object
name|parseGap
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
return|return
name|parseStr
argument_list|(
name|rawval
argument_list|)
return|;
block|}
comment|/**      * Adds the String gap param to a low Range endpoint value to determine      * the corrisponding high Range endpoint value, throwing      * a useful exception if not possible.      */
DECL|method|addGap
specifier|public
specifier|final
name|Comparable
name|addGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
try|try
block|{
return|return
name|parseAndAddGap
argument_list|(
name|value
argument_list|,
name|gap
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Can't add gap "
operator|+
name|gap
operator|+
literal|" to value "
operator|+
name|value
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds the String gap param to a low Range endpoint value to determine      * the corrisponding high Range endpoint value.      * Can throw a low level format exception as needed.      */
DECL|method|parseAndAddGap
specifier|protected
specifier|abstract
name|Comparable
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
function_decl|;
block|}
DECL|class|FloatCalc
specifier|private
specifier|static
class|class
name|FloatCalc
extends|extends
name|Calc
block|{
DECL|method|FloatCalc
specifier|public
name|FloatCalc
parameter_list|(
specifier|final
name|SchemaField
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseStr
specifier|protected
name|Float
name|parseStr
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Float
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Float
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
operator|+
name|Float
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|DoubleCalc
specifier|private
specifier|static
class|class
name|DoubleCalc
extends|extends
name|Calc
block|{
DECL|method|DoubleCalc
specifier|public
name|DoubleCalc
parameter_list|(
specifier|final
name|SchemaField
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseStr
specifier|protected
name|Double
name|parseStr
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Double
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
operator|+
name|Double
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|IntCalc
specifier|private
specifier|static
class|class
name|IntCalc
extends|extends
name|Calc
block|{
DECL|method|IntCalc
specifier|public
name|IntCalc
parameter_list|(
specifier|final
name|SchemaField
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseStr
specifier|protected
name|Integer
name|parseStr
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Integer
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Integer
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
operator|+
name|Integer
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|LongCalc
specifier|private
specifier|static
class|class
name|LongCalc
extends|extends
name|Calc
block|{
DECL|method|LongCalc
specifier|public
name|LongCalc
parameter_list|(
specifier|final
name|SchemaField
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseStr
specifier|protected
name|Long
name|parseStr
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Long
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Long
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
operator|+
name|Long
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|DateCalc
specifier|private
specifier|static
class|class
name|DateCalc
extends|extends
name|Calc
block|{
DECL|field|now
specifier|private
specifier|final
name|Date
name|now
decl_stmt|;
DECL|method|DateCalc
specifier|public
name|DateCalc
parameter_list|(
specifier|final
name|SchemaField
name|f
parameter_list|,
specifier|final
name|Date
name|now
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|now
operator|=
name|now
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|field
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
name|IllegalArgumentException
argument_list|(
literal|"SchemaField must use field type extending TrieDateField or DateRangeField"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|formatValue
specifier|public
name|String
name|formatValue
parameter_list|(
name|Comparable
name|val
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TrieDateField
operator|)
name|field
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|toExternal
argument_list|(
operator|(
name|Date
operator|)
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseStr
specifier|protected
name|Date
name|parseStr
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TrieDateField
operator|)
name|field
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|parseMath
argument_list|(
name|now
argument_list|,
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseGap
specifier|protected
name|Object
name|parseGap
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
block|{
return|return
name|rawval
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Date
name|parseAndAddGap
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
specifier|final
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|()
decl_stmt|;
name|dmp
operator|.
name|setNow
argument_list|(
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
return|return
name|dmp
operator|.
name|parseMath
argument_list|(
name|gap
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

