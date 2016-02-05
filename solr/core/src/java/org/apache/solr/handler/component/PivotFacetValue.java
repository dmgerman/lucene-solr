begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|Locale
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
name|util
operator|.
name|DateFormatUtil
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
name|PivotListEntry
import|;
end_import

begin_comment
comment|/**  * Models a single (value, count) pair that will exist in the collection of values for a   * {@link PivotFacetField} parent.  This<code>PivotFacetValue</code> may itself have a   * nested {@link PivotFacetField} child  *  * @see PivotFacetField  * @see PivotFacetFieldValueCollection  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|PivotFacetValue
specifier|public
class|class
name|PivotFacetValue
block|{
DECL|field|sourceShards
specifier|private
specifier|final
name|BitSet
name|sourceShards
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
DECL|field|parentPivot
specifier|private
specifier|final
name|PivotFacetField
name|parentPivot
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Comparable
name|value
decl_stmt|;
comment|// child can't be final, circular ref on construction
DECL|field|childPivot
specifier|private
name|PivotFacetField
name|childPivot
init|=
literal|null
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|// mutable
DECL|field|statsValues
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|statsValues
init|=
literal|null
decl_stmt|;
comment|// named list with objects because depending on how big the counts are we may get either a long or an int
DECL|field|queryCounts
specifier|private
name|NamedList
argument_list|<
name|Number
argument_list|>
name|queryCounts
decl_stmt|;
DECL|field|rangeCounts
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
operator|.
name|DistribRangeFacet
argument_list|>
name|rangeCounts
decl_stmt|;
DECL|method|PivotFacetValue
specifier|private
name|PivotFacetValue
parameter_list|(
name|PivotFacetField
name|parent
parameter_list|,
name|Comparable
name|val
parameter_list|)
block|{
name|this
operator|.
name|parentPivot
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|val
expr_stmt|;
block|}
comment|/**     * The value of the asssocated field modeled by this<code>PivotFacetValue</code>.     * May be null if this<code>PivotFacetValue</code> models the count for docs     * "missing" the field value.    *    * @see FacetParams#FACET_MISSING    */
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/** The count corrisponding to the value modeled by this<code>PivotFacetValue</code> */
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
comment|/**     * The {@link PivotFacetField} corrisponding to the nested child pivot for this     *<code>PivotFacetValue</code>. May be null if this object is the leaf of a pivot.    */
DECL|method|getChildPivot
specifier|public
name|PivotFacetField
name|getChildPivot
parameter_list|()
block|{
return|return
name|childPivot
return|;
block|}
comment|/**     * A recursive method that walks up the tree of pivot fields/values to build     * a list of the String representations of the values that lead down to this     * PivotFacetValue.    *    * @return a mutable List of the pivot value Strings leading down to and including     *      this pivot value, will never be null but may contain nulls    * @see PivotFacetField#getValuePath    */
DECL|method|getValuePath
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getValuePath
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|out
init|=
name|parentPivot
operator|.
name|getValuePath
argument_list|()
decl_stmt|;
comment|// Note: this code doesn't play nice with custom FieldTypes -- see SOLR-6330
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
operator|(
name|Date
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|add
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
comment|/**    * A recursive method to construct a new<code>PivotFacetValue</code> object from     * the contents of the {@link NamedList} provided by the specified shard, relative     * to the specified field.      *    * If the<code>NamedList</code> contains data for a child {@link PivotFacetField}     * that will be recursively built as well.    *    * @see PivotFacetField#createFromListOfNamedLists    * @param shardNumber the id of the shard that provided this data    * @param rb The response builder of the current request    * @param parentField the parent field in the current pivot associated with this value    * @param pivotData the data from the specified shard for this pivot value    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createFromNamedList
specifier|public
specifier|static
name|PivotFacetValue
name|createFromNamedList
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|PivotFacetField
name|parentField
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|pivotData
parameter_list|)
block|{
name|Comparable
name|pivotVal
init|=
literal|null
decl_stmt|;
name|int
name|pivotCount
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|childPivotData
init|=
literal|null
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
name|statsValues
init|=
literal|null
decl_stmt|;
name|NamedList
argument_list|<
name|Number
argument_list|>
name|queryCounts
init|=
literal|null
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|ranges
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pivotData
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|pivotData
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|pivotData
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PivotListEntry
name|entry
init|=
name|PivotListEntry
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|entry
condition|)
block|{
case|case
name|VALUE
case|:
name|pivotVal
operator|=
operator|(
name|Comparable
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|FIELD
case|:
assert|assert
name|parentField
operator|.
name|field
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|:
literal|"Parent Field mismatch: "
operator|+
name|parentField
operator|.
name|field
operator|+
literal|"!="
operator|+
name|value
assert|;
break|break;
case|case
name|COUNT
case|:
name|pivotCount
operator|=
operator|(
name|Integer
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|PIVOT
case|:
name|childPivotData
operator|=
operator|(
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|STATS
case|:
name|statsValues
operator|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|QUERIES
case|:
name|queryCounts
operator|=
operator|(
name|NamedList
argument_list|<
name|Number
argument_list|>
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|RANGES
case|:
name|ranges
operator|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|value
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"PivotListEntry contains unaccounted for item: "
operator|+
name|entry
argument_list|)
throw|;
block|}
block|}
name|PivotFacetValue
name|newPivotFacet
init|=
operator|new
name|PivotFacetValue
argument_list|(
name|parentField
argument_list|,
name|pivotVal
argument_list|)
decl_stmt|;
name|newPivotFacet
operator|.
name|count
operator|=
name|pivotCount
expr_stmt|;
name|newPivotFacet
operator|.
name|sourceShards
operator|.
name|set
argument_list|(
name|shardNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|statsValues
operator|!=
literal|null
condition|)
block|{
name|newPivotFacet
operator|.
name|statsValues
operator|=
name|PivotFacetHelper
operator|.
name|mergeStats
argument_list|(
literal|null
argument_list|,
name|statsValues
argument_list|,
name|rb
operator|.
name|_statsInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryCounts
operator|!=
literal|null
condition|)
block|{
name|newPivotFacet
operator|.
name|queryCounts
operator|=
name|PivotFacetHelper
operator|.
name|mergeQueryCounts
argument_list|(
literal|null
argument_list|,
name|queryCounts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ranges
operator|!=
literal|null
condition|)
block|{
name|newPivotFacet
operator|.
name|rangeCounts
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|RangeFacetRequest
operator|.
name|DistribRangeFacet
operator|.
name|mergeFacetRangesFromShardResponse
argument_list|(
name|newPivotFacet
operator|.
name|rangeCounts
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
name|newPivotFacet
operator|.
name|childPivot
operator|=
name|PivotFacetField
operator|.
name|createFromListOfNamedLists
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|newPivotFacet
argument_list|,
name|childPivotData
argument_list|)
expr_stmt|;
return|return
name|newPivotFacet
return|;
block|}
comment|/**     * A<b>NON-Recursive</b> method indicating if the specified shard has already    * contributed to the count for this value.    */
DECL|method|shardHasContributed
specifier|public
name|boolean
name|shardHasContributed
parameter_list|(
name|int
name|shardNum
parameter_list|)
block|{
return|return
name|sourceShards
operator|.
name|get
argument_list|(
name|shardNum
argument_list|)
return|;
block|}
comment|/**     * A recursive method for generating a NamedList from this value suitable for     * including in a pivot facet response to the original distributed request.    *    * @see PivotFacetField#convertToListOfNamedLists    */
DECL|method|convertToNamedList
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|convertToNamedList
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|newList
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|FIELD
operator|.
name|getName
argument_list|()
argument_list|,
name|parentPivot
operator|.
name|field
argument_list|)
expr_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|VALUE
operator|.
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|COUNT
operator|.
name|getName
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryCounts
operator|!=
literal|null
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|QUERIES
operator|.
name|getName
argument_list|()
argument_list|,
name|queryCounts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rangeCounts
operator|!=
literal|null
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|rangeFacetOutput
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RangeFacetRequest
operator|.
name|DistribRangeFacet
argument_list|>
name|entry
range|:
name|rangeCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RangeFacetRequest
operator|.
name|DistribRangeFacet
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|rangeFacetOutput
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
operator|.
name|rangeFacet
argument_list|)
expr_stmt|;
block|}
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|RANGES
operator|.
name|getName
argument_list|()
argument_list|,
name|rangeFacetOutput
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|childPivot
operator|!=
literal|null
operator|&&
name|childPivot
operator|.
name|convertToListOfNamedLists
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|PIVOT
operator|.
name|getName
argument_list|()
argument_list|,
name|childPivot
operator|.
name|convertToListOfNamedLists
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|statsValues
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|PivotListEntry
operator|.
name|STATS
operator|.
name|getName
argument_list|()
argument_list|,
name|StatsComponent
operator|.
name|convertToResponse
argument_list|(
name|statsValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newList
return|;
block|}
comment|/**    * Merges in the count contributions from the specified shard for each.    * This method is recursive if the shard data includes sub-pivots    *    * @see PivotFacetField#contributeFromShard    * @see PivotFacetField#createFromListOfNamedLists    */
DECL|method|mergeContributionFromShard
specifier|public
name|void
name|mergeContributionFromShard
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|value
parameter_list|)
block|{
assert|assert
literal|null
operator|!=
name|value
operator|:
literal|"can't merge in null data"
assert|;
if|if
condition|(
operator|!
name|shardHasContributed
argument_list|(
name|shardNumber
argument_list|)
condition|)
block|{
name|sourceShards
operator|.
name|set
argument_list|(
name|shardNumber
argument_list|)
expr_stmt|;
name|count
operator|+=
name|PivotFacetHelper
operator|.
name|getCount
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
name|stats
init|=
name|PivotFacetHelper
operator|.
name|getStats
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|!=
literal|null
condition|)
block|{
name|statsValues
operator|=
name|PivotFacetHelper
operator|.
name|mergeStats
argument_list|(
name|statsValues
argument_list|,
name|stats
argument_list|,
name|rb
operator|.
name|_statsInfo
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Number
argument_list|>
name|shardQueryCounts
init|=
name|PivotFacetHelper
operator|.
name|getQueryCounts
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardQueryCounts
operator|!=
literal|null
condition|)
block|{
name|queryCounts
operator|=
name|PivotFacetHelper
operator|.
name|mergeQueryCounts
argument_list|(
name|queryCounts
argument_list|,
name|shardQueryCounts
argument_list|)
expr_stmt|;
block|}
name|SimpleOrderedMap
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|shardRanges
init|=
name|PivotFacetHelper
operator|.
name|getRanges
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardRanges
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rangeCounts
operator|==
literal|null
condition|)
block|{
name|rangeCounts
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|shardRanges
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
name|RangeFacetRequest
operator|.
name|DistribRangeFacet
operator|.
name|mergeFacetRangesFromShardResponse
argument_list|(
name|rangeCounts
argument_list|,
name|shardRanges
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|shardChildPivots
init|=
name|PivotFacetHelper
operator|.
name|getPivots
argument_list|(
name|value
argument_list|)
decl_stmt|;
comment|// sub pivot -- we may not have seen this yet depending on refinement
if|if
condition|(
literal|null
operator|==
name|childPivot
condition|)
block|{
name|childPivot
operator|=
name|PivotFacetField
operator|.
name|createFromListOfNamedLists
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|this
argument_list|,
name|shardChildPivots
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childPivot
operator|.
name|contributeFromShard
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|shardChildPivots
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"F:%s V:%s Co:%d Ch?:%s"
argument_list|,
name|parentPivot
operator|.
name|field
argument_list|,
name|value
argument_list|,
name|count
argument_list|,
operator|(
name|this
operator|.
name|childPivot
operator|!=
literal|null
operator|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

