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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_comment
comment|/**  * Emcapsulates a collection of {@link PivotFacetValue}s associated with a   * {@link PivotFacetField} withs pecial tracking of a {@link PivotFacetValue}   * corrisponding to the<code>null</code> value when {@link FacetParams#FACET_MISSING}   * is used.  *  * @see #markDirty  * @see PivotFacetField  * @see PivotFacetValue  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|PivotFacetFieldValueCollection
specifier|public
class|class
name|PivotFacetFieldValueCollection
implements|implements
name|Iterable
argument_list|<
name|PivotFacetValue
argument_list|>
block|{
DECL|field|explicitValues
specifier|private
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|explicitValues
decl_stmt|;
DECL|field|missingValue
specifier|private
name|PivotFacetValue
name|missingValue
decl_stmt|;
DECL|field|valuesMap
specifier|private
name|Map
argument_list|<
name|Comparable
argument_list|,
name|PivotFacetValue
argument_list|>
name|valuesMap
decl_stmt|;
DECL|field|dirty
specifier|private
name|boolean
name|dirty
init|=
literal|true
decl_stmt|;
comment|//Facet parameters relating to this field
DECL|field|facetFieldMinimumCount
specifier|private
specifier|final
name|int
name|facetFieldMinimumCount
decl_stmt|;
DECL|field|facetFieldOffset
specifier|private
specifier|final
name|int
name|facetFieldOffset
decl_stmt|;
DECL|field|facetFieldLimit
specifier|private
specifier|final
name|int
name|facetFieldLimit
decl_stmt|;
DECL|field|facetFieldSort
specifier|private
specifier|final
name|String
name|facetFieldSort
decl_stmt|;
DECL|method|PivotFacetFieldValueCollection
specifier|public
name|PivotFacetFieldValueCollection
parameter_list|(
name|int
name|minCount
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|String
name|fieldSort
parameter_list|)
block|{
name|this
operator|.
name|explicitValues
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|valuesMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|facetFieldMinimumCount
operator|=
name|minCount
expr_stmt|;
name|this
operator|.
name|facetFieldOffset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|facetFieldLimit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|facetFieldSort
operator|=
name|fieldSort
expr_stmt|;
block|}
comment|/**    * Indicates that the values in this collection have been modified by the caller.    *    * Any caller that manipulates the {@link PivotFacetValue}s contained in this collection    * must call this method after doing so.    */
DECL|method|markDirty
specifier|public
name|void
name|markDirty
parameter_list|()
block|{
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * The {@link PivotFacetValue} with corisponding to a a value of     *<code>null</code> when {@link FacetParams#FACET_MISSING} is used.    *     * @return the appropriate<code>PivotFacetValue</code> object, may be null     *         if we "missing" is not in use, or if it does not meat the mincount.    */
DECL|method|getMissingValue
specifier|public
name|PivotFacetValue
name|getMissingValue
parameter_list|()
block|{
return|return
name|missingValue
return|;
block|}
comment|/**     * Read-Only access to the Collection of {@link PivotFacetValue}s corrisponding to     * non-missing values.    *    * @see #getMissingValue    */
DECL|method|getExplicitValuesList
specifier|public
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|getExplicitValuesList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|explicitValues
argument_list|)
return|;
block|}
comment|/**     * Size of {@link #getExplicitValuesList}    */
DECL|method|getExplicitValuesListSize
specifier|public
name|int
name|getExplicitValuesListSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|explicitValues
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**     * Total number of {@link PivotFacetValue}s, including the "missing" value if used.    *    * @see #getMissingValue    * @see #getExplicitValuesList    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|getExplicitValuesListSize
argument_list|()
operator|+
operator|(
name|this
operator|.
name|missingValue
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
comment|/**    * Returns the appropriate sub-list of the explicit values that need to be refined,     * based on the {@link FacetParams#FACET_OFFSET}&amp; {@link FacetParams#FACET_LIMIT}     * for this field.    *    * @see #getExplicitValuesList    * @see List#subList    */
DECL|method|getNextLevelValuesToRefine
specifier|public
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|getNextLevelValuesToRefine
parameter_list|()
block|{
specifier|final
name|int
name|numRefinableValues
init|=
name|getExplicitValuesListSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|facetFieldOffset
operator|<
name|numRefinableValues
condition|)
block|{
specifier|final
name|int
name|offsetPlusCount
init|=
operator|(
name|facetFieldLimit
operator|>=
literal|0
operator|)
condition|?
name|Math
operator|.
name|min
argument_list|(
name|facetFieldLimit
operator|+
name|facetFieldOffset
argument_list|,
name|numRefinableValues
argument_list|)
else|:
name|numRefinableValues
decl_stmt|;
return|return
name|getExplicitValuesList
argument_list|()
operator|.
name|subList
argument_list|(
name|facetFieldOffset
argument_list|,
name|offsetPlusCount
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
expr|<
name|PivotFacetValue
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
comment|/**    * Fast lookup to retrieve a {@link PivotFacetValue} from this collection if it     * exists    *    * @param value of the<code>PivotFacetValue</code> to lookup, if     *<code>null</code> this returns the same as {@link #getMissingValue}    * @return the corrisponding<code>PivotFacetValue</code> or null if there is     *        no<code>PivotFacetValue</code> in this collection corrisponding to     *        the specified value.    */
DECL|method|get
specifier|public
name|PivotFacetValue
name|get
parameter_list|(
name|Comparable
name|value
parameter_list|)
block|{
return|return
name|valuesMap
operator|.
name|get
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**    * Fetchs a {@link PivotFacetValue} from this collection via the index, may not     * be used to fetch the<code>PivotFacetValue</code> corrisponding to the missing-value.    *    * @see #getExplicitValuesList    * @see List#get(int)    * @see #getMissingValue    */
DECL|method|getAt
specifier|public
name|PivotFacetValue
name|getAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|explicitValues
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Adds a {@link PivotFacetValue} to this collection -- callers must not use this     * method if a {@link PivotFacetValue} with the same value already exists in this collection    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|PivotFacetValue
name|pfValue
parameter_list|)
block|{
name|Comparable
name|val
init|=
name|pfValue
operator|.
name|getValue
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|this
operator|.
name|valuesMap
operator|.
name|containsKey
argument_list|(
name|val
argument_list|)
operator|:
literal|"Must not add duplicate PivotFacetValue with redundent inner value"
assert|;
name|dirty
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|val
condition|)
block|{
name|this
operator|.
name|missingValue
operator|=
name|pfValue
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|explicitValues
operator|.
name|add
argument_list|(
name|pfValue
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|valuesMap
operator|.
name|put
argument_list|(
name|val
argument_list|,
name|pfValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Destructive method that recursively prunes values from the data structure     * based on the counts for those values and the effective sort, mincount, limit,     * and offset being used for each field.    *<p>    * This method should only be called after all refinement is completed.    *</p>    *    * @see PivotFacetField#trim    * @see PivotFacet#getTrimmedPivotsAsListOfNamedLists    */
DECL|method|trim
specifier|public
name|void
name|trim
parameter_list|()
block|{
comment|// NOTE: destructive
comment|// TODO: see comment in PivotFacetField about potential optimization
comment|// (ie: trim as we refine)
name|trimNonNullValues
argument_list|()
expr_stmt|;
name|trimNullValue
argument_list|()
expr_stmt|;
block|}
DECL|method|trimNullValue
specifier|private
name|void
name|trimNullValue
parameter_list|()
block|{
if|if
condition|(
name|missingValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|missingValue
operator|.
name|getCount
argument_list|()
operator|>=
name|facetFieldMinimumCount
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|missingValue
operator|.
name|getChildPivot
argument_list|()
condition|)
block|{
name|missingValue
operator|.
name|getChildPivot
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// missing count less than mincount
name|missingValue
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|trimNonNullValues
specifier|private
name|void
name|trimNonNullValues
parameter_list|()
block|{
if|if
condition|(
name|explicitValues
operator|!=
literal|null
operator|&&
name|explicitValues
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sort
argument_list|()
expr_stmt|;
name|ArrayList
argument_list|<
name|PivotFacetValue
argument_list|>
name|trimmedValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|facetsSkipped
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PivotFacetValue
name|pivotValue
range|:
name|explicitValues
control|)
block|{
if|if
condition|(
name|pivotValue
operator|.
name|getCount
argument_list|()
operator|>=
name|facetFieldMinimumCount
condition|)
block|{
if|if
condition|(
name|facetsSkipped
operator|>=
name|facetFieldOffset
condition|)
block|{
name|trimmedValues
operator|.
name|add
argument_list|(
name|pivotValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|pivotValue
operator|.
name|getChildPivot
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pivotValue
operator|.
name|getChildPivot
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|facetFieldLimit
operator|>
literal|0
operator|&&
name|trimmedValues
operator|.
name|size
argument_list|()
operator|>=
name|facetFieldLimit
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
name|facetsSkipped
operator|++
expr_stmt|;
block|}
block|}
block|}
name|explicitValues
operator|=
name|trimmedValues
expr_stmt|;
name|valuesMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Sorts the collection and recursively sorts the collections assocaited with     * any sub-pivots.    *    * @see FacetParams#FACET_SORT    * @see PivotFacetField#sort    */
DECL|method|sort
specifier|public
name|void
name|sort
parameter_list|()
block|{
if|if
condition|(
name|dirty
condition|)
block|{
if|if
condition|(
name|facetFieldSort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|this
operator|.
name|explicitValues
argument_list|,
operator|new
name|PivotFacetCountComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|facetFieldSort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_INDEX
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|this
operator|.
name|explicitValues
argument_list|,
operator|new
name|PivotFacetValueComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dirty
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
name|PivotFacetValue
name|value
range|:
name|this
operator|.
name|explicitValues
control|)
if|if
condition|(
name|value
operator|.
name|getChildPivot
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|value
operator|.
name|getChildPivot
argument_list|()
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|missingValue
operator|!=
literal|null
operator|&&
name|missingValue
operator|.
name|getChildPivot
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|.
name|getChildPivot
argument_list|()
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Iterator over all elements in this Collection, including the result of     * {@link #getMissingValue} as the last element (if it exists)    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|PivotFacetValue
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|PivotFacetValue
argument_list|>
name|it
init|=
operator|new
name|Iterator
argument_list|<
name|PivotFacetValue
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
name|valuesIterator
init|=
name|explicitValues
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|shouldGiveMissingValue
init|=
operator|(
name|missingValue
operator|!=
literal|null
operator|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|valuesIterator
operator|.
name|hasNext
argument_list|()
operator|||
name|shouldGiveMissingValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|PivotFacetValue
name|next
parameter_list|()
block|{
while|while
condition|(
name|valuesIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|(
name|PivotFacetValue
operator|)
name|valuesIterator
operator|.
name|next
argument_list|()
return|;
block|}
comment|//else
if|if
condition|(
name|shouldGiveMissingValue
condition|)
block|{
name|shouldGiveMissingValue
operator|=
literal|false
expr_stmt|;
return|return
name|missingValue
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Can't remove from this iterator"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
return|return
name|it
return|;
block|}
comment|/** Sorts {@link PivotFacetValue} instances by their count */
DECL|class|PivotFacetCountComparator
specifier|public
specifier|static
class|class
name|PivotFacetCountComparator
implements|implements
name|Comparator
argument_list|<
name|PivotFacetValue
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|PivotFacetValue
name|left
parameter_list|,
name|PivotFacetValue
name|right
parameter_list|)
block|{
name|int
name|countCmp
init|=
name|right
operator|.
name|getCount
argument_list|()
operator|-
name|left
operator|.
name|getCount
argument_list|()
decl_stmt|;
return|return
operator|(
literal|0
operator|!=
name|countCmp
operator|)
condition|?
name|countCmp
else|:
name|compareWithNullLast
argument_list|(
name|left
operator|.
name|getValue
argument_list|()
argument_list|,
name|right
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Sorts {@link PivotFacetValue} instances by their value */
DECL|class|PivotFacetValueComparator
specifier|public
specifier|static
class|class
name|PivotFacetValueComparator
implements|implements
name|Comparator
argument_list|<
name|PivotFacetValue
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|PivotFacetValue
name|left
parameter_list|,
name|PivotFacetValue
name|right
parameter_list|)
block|{
return|return
name|compareWithNullLast
argument_list|(
name|left
operator|.
name|getValue
argument_list|()
argument_list|,
name|right
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * A helper method for use in<code>Comparator</code> classes where object properties     * are<code>Comparable</code> but may be null.    */
DECL|method|compareWithNullLast
specifier|static
name|int
name|compareWithNullLast
parameter_list|(
specifier|final
name|Comparable
name|o1
parameter_list|,
specifier|final
name|Comparable
name|o2
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|o1
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|o2
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
literal|1
return|;
comment|// o1 is null, o2 is not
block|}
if|if
condition|(
literal|null
operator|==
name|o2
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// o2 is null, o1 is not
block|}
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
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
literal|"Values:%s | Missing:%s "
argument_list|,
name|explicitValues
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

