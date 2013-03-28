begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|Collector
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
name|Scorer
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|PriorityQueue
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
name|*
import|;
end_import

begin_comment
comment|/**  * Base class for computing grouped facets.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AbstractGroupFacetCollector
specifier|public
specifier|abstract
class|class
name|AbstractGroupFacetCollector
extends|extends
name|Collector
block|{
DECL|field|groupField
specifier|protected
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|facetField
specifier|protected
specifier|final
name|String
name|facetField
decl_stmt|;
DECL|field|facetPrefix
specifier|protected
specifier|final
name|BytesRef
name|facetPrefix
decl_stmt|;
DECL|field|segmentResults
specifier|protected
specifier|final
name|List
argument_list|<
name|SegmentResult
argument_list|>
name|segmentResults
decl_stmt|;
DECL|field|segmentFacetCounts
specifier|protected
name|int
index|[]
name|segmentFacetCounts
decl_stmt|;
DECL|field|segmentTotalCount
specifier|protected
name|int
name|segmentTotalCount
decl_stmt|;
DECL|field|startFacetOrd
specifier|protected
name|int
name|startFacetOrd
decl_stmt|;
DECL|field|endFacetOrd
specifier|protected
name|int
name|endFacetOrd
decl_stmt|;
DECL|method|AbstractGroupFacetCollector
specifier|protected
name|AbstractGroupFacetCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|facetField
parameter_list|,
name|BytesRef
name|facetPrefix
parameter_list|)
block|{
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|facetField
operator|=
name|facetField
expr_stmt|;
name|this
operator|.
name|facetPrefix
operator|=
name|facetPrefix
expr_stmt|;
name|segmentResults
operator|=
operator|new
name|ArrayList
argument_list|<
name|SegmentResult
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns grouped facet results that were computed over zero or more segments.    * Grouped facet counts are merged from zero or more segment results.    *    * @param size The total number of facets to include. This is typically offset + limit    * @param minCount The minimum count a facet entry should have to be included in the grouped facet result    * @param orderByCount Whether to sort the facet entries by facet entry count. If<code>false</code> then the facets    *                     are sorted lexicographically in ascending order.    * @return grouped facet results    * @throws IOException If I/O related errors occur during merging segment grouped facet counts.    */
DECL|method|mergeSegmentResults
specifier|public
name|GroupedFacetResult
name|mergeSegmentResults
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|minCount
parameter_list|,
name|boolean
name|orderByCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentFacetCounts
operator|!=
literal|null
condition|)
block|{
name|segmentResults
operator|.
name|add
argument_list|(
name|createSegmentResult
argument_list|()
argument_list|)
expr_stmt|;
name|segmentFacetCounts
operator|=
literal|null
expr_stmt|;
comment|// reset
block|}
name|int
name|totalCount
init|=
literal|0
decl_stmt|;
name|int
name|missingCount
init|=
literal|0
decl_stmt|;
name|SegmentResultPriorityQueue
name|segments
init|=
operator|new
name|SegmentResultPriorityQueue
argument_list|(
name|segmentResults
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SegmentResult
name|segmentResult
range|:
name|segmentResults
control|)
block|{
name|missingCount
operator|+=
name|segmentResult
operator|.
name|missing
expr_stmt|;
if|if
condition|(
name|segmentResult
operator|.
name|mergePos
operator|>=
name|segmentResult
operator|.
name|maxTermPos
condition|)
block|{
continue|continue;
block|}
name|totalCount
operator|+=
name|segmentResult
operator|.
name|total
expr_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|segmentResult
argument_list|)
expr_stmt|;
block|}
name|GroupedFacetResult
name|facetResult
init|=
operator|new
name|GroupedFacetResult
argument_list|(
name|size
argument_list|,
name|minCount
argument_list|,
name|orderByCount
argument_list|,
name|totalCount
argument_list|,
name|missingCount
argument_list|)
decl_stmt|;
while|while
condition|(
name|segments
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SegmentResult
name|segmentResult
init|=
name|segments
operator|.
name|top
argument_list|()
decl_stmt|;
name|BytesRef
name|currentFacetValue
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|segmentResult
operator|.
name|mergeTerm
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|+=
name|segmentResult
operator|.
name|counts
index|[
name|segmentResult
operator|.
name|mergePos
operator|++
index|]
expr_stmt|;
if|if
condition|(
name|segmentResult
operator|.
name|mergePos
operator|<
name|segmentResult
operator|.
name|maxTermPos
condition|)
block|{
name|segmentResult
operator|.
name|nextTerm
argument_list|()
expr_stmt|;
name|segmentResult
operator|=
name|segments
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|segments
operator|.
name|pop
argument_list|()
expr_stmt|;
name|segmentResult
operator|=
name|segments
operator|.
name|top
argument_list|()
expr_stmt|;
if|if
condition|(
name|segmentResult
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
do|while
condition|(
name|currentFacetValue
operator|.
name|equals
argument_list|(
name|segmentResult
operator|.
name|mergeTerm
argument_list|)
condition|)
do|;
name|facetResult
operator|.
name|addFacetCount
argument_list|(
name|currentFacetValue
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|facetResult
return|;
block|}
DECL|method|createSegmentResult
specifier|protected
specifier|abstract
name|SegmentResult
name|createSegmentResult
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * The grouped facet result. Containing grouped facet entries, total count and total missing count.    */
DECL|class|GroupedFacetResult
specifier|public
specifier|static
class|class
name|GroupedFacetResult
block|{
DECL|field|orderByCountAndValue
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|FacetEntry
argument_list|>
name|orderByCountAndValue
init|=
operator|new
name|Comparator
argument_list|<
name|FacetEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetEntry
name|a
parameter_list|,
name|FacetEntry
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|b
operator|.
name|count
operator|-
name|a
operator|.
name|count
decl_stmt|;
comment|// Highest count first!
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
return|return
name|a
operator|.
name|value
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|orderByValue
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|FacetEntry
argument_list|>
name|orderByValue
init|=
operator|new
name|Comparator
argument_list|<
name|FacetEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetEntry
name|a
parameter_list|,
name|FacetEntry
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|value
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|facetEntries
specifier|private
specifier|final
name|NavigableSet
argument_list|<
name|FacetEntry
argument_list|>
name|facetEntries
decl_stmt|;
DECL|field|totalMissingCount
specifier|private
specifier|final
name|int
name|totalMissingCount
decl_stmt|;
DECL|field|totalCount
specifier|private
specifier|final
name|int
name|totalCount
decl_stmt|;
DECL|field|currentMin
specifier|private
name|int
name|currentMin
decl_stmt|;
DECL|method|GroupedFacetResult
specifier|public
name|GroupedFacetResult
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|minCount
parameter_list|,
name|boolean
name|orderByCount
parameter_list|,
name|int
name|totalCount
parameter_list|,
name|int
name|totalMissingCount
parameter_list|)
block|{
name|this
operator|.
name|facetEntries
operator|=
operator|new
name|TreeSet
argument_list|<
name|FacetEntry
argument_list|>
argument_list|(
name|orderByCount
condition|?
name|orderByCountAndValue
else|:
name|orderByValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalMissingCount
operator|=
name|totalMissingCount
expr_stmt|;
name|this
operator|.
name|totalCount
operator|=
name|totalCount
expr_stmt|;
name|maxSize
operator|=
name|size
expr_stmt|;
name|currentMin
operator|=
name|minCount
expr_stmt|;
block|}
DECL|method|addFacetCount
specifier|public
name|void
name|addFacetCount
parameter_list|(
name|BytesRef
name|facetValue
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<
name|currentMin
condition|)
block|{
return|return;
block|}
name|FacetEntry
name|facetEntry
init|=
operator|new
name|FacetEntry
argument_list|(
name|facetValue
argument_list|,
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetEntries
operator|.
name|size
argument_list|()
operator|==
name|maxSize
condition|)
block|{
if|if
condition|(
name|facetEntries
operator|.
name|higher
argument_list|(
name|facetEntry
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|facetEntries
operator|.
name|pollLast
argument_list|()
expr_stmt|;
block|}
name|facetEntries
operator|.
name|add
argument_list|(
name|facetEntry
argument_list|)
expr_stmt|;
if|if
condition|(
name|facetEntries
operator|.
name|size
argument_list|()
operator|==
name|maxSize
condition|)
block|{
name|currentMin
operator|=
name|facetEntries
operator|.
name|last
argument_list|()
operator|.
name|count
expr_stmt|;
block|}
block|}
comment|/**      * Returns a list of facet entries to be rendered based on the specified offset and limit.      * The facet entries are retrieved from the facet entries collected during merging.      *      * @param offset The offset in the collected facet entries during merging      * @param limit The number of facets to return starting from the offset.      * @return a list of facet entries to be rendered based on the specified offset and limit      */
DECL|method|getFacetEntries
specifier|public
name|List
argument_list|<
name|FacetEntry
argument_list|>
name|getFacetEntries
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|List
argument_list|<
name|FacetEntry
argument_list|>
name|entries
init|=
operator|new
name|LinkedList
argument_list|<
name|FacetEntry
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|skipped
init|=
literal|0
decl_stmt|;
name|int
name|included
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetEntry
name|facetEntry
range|:
name|facetEntries
control|)
block|{
if|if
condition|(
name|skipped
operator|<
name|offset
condition|)
block|{
name|skipped
operator|++
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|included
operator|++
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
name|entries
operator|.
name|add
argument_list|(
name|facetEntry
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
comment|/**      * Returns the sum of all facet entries counts.      *      * @return the sum of all facet entries counts      */
DECL|method|getTotalCount
specifier|public
name|int
name|getTotalCount
parameter_list|()
block|{
return|return
name|totalCount
return|;
block|}
comment|/**      * Returns the number of groups that didn't have a facet value.      *      * @return the number of groups that didn't have a facet value      */
DECL|method|getTotalMissingCount
specifier|public
name|int
name|getTotalMissingCount
parameter_list|()
block|{
return|return
name|totalMissingCount
return|;
block|}
block|}
comment|/**    * Represents a facet entry with a value and a count.    */
DECL|class|FacetEntry
specifier|public
specifier|static
class|class
name|FacetEntry
block|{
DECL|field|value
specifier|private
specifier|final
name|BytesRef
name|value
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
DECL|method|FacetEntry
specifier|public
name|FacetEntry
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FacetEntry
name|that
init|=
operator|(
name|FacetEntry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|count
operator|!=
name|that
operator|.
name|count
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
name|that
operator|.
name|value
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|value
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|count
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FacetEntry{"
operator|+
literal|"value="
operator|+
name|value
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|", count="
operator|+
name|count
operator|+
literal|'}'
return|;
block|}
comment|/**      * @return The value of this facet entry      */
DECL|method|getValue
specifier|public
name|BytesRef
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * @return The count (number of groups) of this facet entry.      */
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
comment|/**    * Contains the local grouped segment counts for a particular segment.    * Each<code>SegmentResult</code> must be added together.    */
DECL|class|SegmentResult
specifier|protected
specifier|abstract
specifier|static
class|class
name|SegmentResult
block|{
DECL|field|counts
specifier|protected
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|total
specifier|protected
specifier|final
name|int
name|total
decl_stmt|;
DECL|field|missing
specifier|protected
specifier|final
name|int
name|missing
decl_stmt|;
DECL|field|maxTermPos
specifier|protected
specifier|final
name|int
name|maxTermPos
decl_stmt|;
DECL|field|mergeTerm
specifier|protected
name|BytesRef
name|mergeTerm
decl_stmt|;
DECL|field|mergePos
specifier|protected
name|int
name|mergePos
decl_stmt|;
DECL|method|SegmentResult
specifier|protected
name|SegmentResult
parameter_list|(
name|int
index|[]
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|missing
parameter_list|,
name|int
name|maxTermPos
parameter_list|)
block|{
name|this
operator|.
name|counts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
name|this
operator|.
name|missing
operator|=
name|missing
expr_stmt|;
name|this
operator|.
name|maxTermPos
operator|=
name|maxTermPos
expr_stmt|;
block|}
comment|/**      * Go to next term in this<code>SegmentResult</code> in order to retrieve the grouped facet counts.      *      * @throws IOException If I/O related errors occur      */
DECL|method|nextTerm
specifier|protected
specifier|abstract
name|void
name|nextTerm
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|SegmentResultPriorityQueue
specifier|private
specifier|static
class|class
name|SegmentResultPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SegmentResult
argument_list|>
block|{
DECL|method|SegmentResultPriorityQueue
name|SegmentResultPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SegmentResult
name|a
parameter_list|,
name|SegmentResult
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|mergeTerm
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|mergeTerm
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

