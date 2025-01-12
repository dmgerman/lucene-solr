begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collection
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
name|TreeSet
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
name|index
operator|.
name|LeafReaderContext
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
name|FieldComparator
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
name|LeafFieldComparator
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
name|search
operator|.
name|SimpleCollector
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
name|Sort
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
name|SortField
import|;
end_import

begin_comment
comment|/** FirstPassGroupingCollector is the first of two passes necessary  *  to collect grouped hits.  This pass gathers the top N sorted  *  groups. Groups are defined by a {@link GroupSelector}  *  *<p>See {@link org.apache.lucene.search.grouping} for more  *  details including a full code example.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FirstPassGroupingCollector
specifier|public
class|class
name|FirstPassGroupingCollector
parameter_list|<
name|T
parameter_list|>
extends|extends
name|SimpleCollector
block|{
DECL|field|groupSelector
specifier|private
specifier|final
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|groupSelector
decl_stmt|;
DECL|field|comparators
specifier|private
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
index|[]
name|comparators
decl_stmt|;
DECL|field|leafComparators
specifier|private
specifier|final
name|LeafFieldComparator
index|[]
name|leafComparators
decl_stmt|;
DECL|field|reversed
specifier|private
specifier|final
name|int
index|[]
name|reversed
decl_stmt|;
DECL|field|topNGroups
specifier|private
specifier|final
name|int
name|topNGroups
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|groupMap
specifier|private
specifier|final
name|HashMap
argument_list|<
name|T
argument_list|,
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|compIDXEnd
specifier|private
specifier|final
name|int
name|compIDXEnd
decl_stmt|;
comment|// Set once we reach topNGroups unique groups:
comment|/** @lucene.internal */
DECL|field|orderedGroups
specifier|protected
name|TreeSet
argument_list|<
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|orderedGroups
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|spareSlot
specifier|private
name|int
name|spareSlot
decl_stmt|;
comment|/**    * Create the first pass collector.    *    * @param groupSelector a GroupSelector used to defined groups    * @param groupSort The {@link Sort} used to sort the    *    groups.  The top sorted document within each group    *    according to groupSort, determines how that group    *    sorts against other groups.  This must be non-null,    *    ie, if you want to groupSort by relevance use    *    Sort.RELEVANCE.    * @param topNGroups How many top groups to keep.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|FirstPassGroupingCollector
specifier|public
name|FirstPassGroupingCollector
parameter_list|(
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|groupSelector
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|)
block|{
name|this
operator|.
name|groupSelector
operator|=
name|groupSelector
expr_stmt|;
if|if
condition|(
name|topNGroups
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"topNGroups must be>= 1 (got "
operator|+
name|topNGroups
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// TODO: allow null groupSort to mean "by relevance",
comment|// and specialize it?
name|this
operator|.
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|groupSort
operator|.
name|needsScores
argument_list|()
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
name|leafComparators
operator|=
operator|new
name|LeafFieldComparator
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
name|compIDXEnd
operator|=
name|comparators
operator|.
name|length
operator|-
literal|1
expr_stmt|;
name|reversed
operator|=
operator|new
name|int
index|[
name|sortFields
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|i
index|]
decl_stmt|;
comment|// use topNGroups + 1 so we have a spare slot to use for comparing (tracked by this.spareSlot):
name|comparators
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
name|topNGroups
operator|+
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|reversed
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
block|}
name|spareSlot
operator|=
name|topNGroups
expr_stmt|;
name|groupMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|topNGroups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|needsScores
return|;
block|}
comment|/**    * Returns top groups, starting from offset.  This may    * return null, if no groups were collected, or if the    * number of unique groups collected is&lt;= offset.    *    * @param groupOffset The offset in the collected groups    * @param fillFields Whether to fill to {@link SearchGroup#sortValues}    * @return top groups, starting from offset    */
DECL|method|getTopGroups
specifier|public
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|getTopGroups
parameter_list|(
name|int
name|groupOffset
parameter_list|,
name|boolean
name|fillFields
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("FP.getTopGroups groupOffset=" + groupOffset + " fillFields=" + fillFields + " groupMap.size()=" + groupMap.size());
if|if
condition|(
name|groupOffset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"groupOffset must be>= 0 (got "
operator|+
name|groupOffset
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|groupMap
operator|.
name|size
argument_list|()
operator|<=
name|groupOffset
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|orderedGroups
operator|==
literal|null
condition|)
block|{
name|buildSortedSet
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|sortFieldCount
init|=
name|comparators
operator|.
name|length
decl_stmt|;
for|for
control|(
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
name|group
range|:
name|orderedGroups
control|)
block|{
if|if
condition|(
name|upto
operator|++
operator|<
name|groupOffset
condition|)
block|{
continue|continue;
block|}
comment|// System.out.println("  group=" + (group.groupValue == null ? "null" : group.groupValue.toString()));
name|SearchGroup
argument_list|<
name|T
argument_list|>
name|searchGroup
init|=
operator|new
name|SearchGroup
argument_list|<>
argument_list|()
decl_stmt|;
name|searchGroup
operator|.
name|groupValue
operator|=
name|group
operator|.
name|groupValue
expr_stmt|;
if|if
condition|(
name|fillFields
condition|)
block|{
name|searchGroup
operator|.
name|sortValues
operator|=
operator|new
name|Object
index|[
name|sortFieldCount
index|]
expr_stmt|;
for|for
control|(
name|int
name|sortFieldIDX
init|=
literal|0
init|;
name|sortFieldIDX
operator|<
name|sortFieldCount
condition|;
name|sortFieldIDX
operator|++
control|)
block|{
name|searchGroup
operator|.
name|sortValues
index|[
name|sortFieldIDX
index|]
operator|=
name|comparators
index|[
name|sortFieldIDX
index|]
operator|.
name|value
argument_list|(
name|group
operator|.
name|comparatorSlot
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|add
argument_list|(
name|searchGroup
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("  return " + result.size() + " groups");
return|return
name|result
return|;
block|}
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
block|{
for|for
control|(
name|LeafFieldComparator
name|comparator
range|:
name|leafComparators
control|)
block|{
name|comparator
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isCompetitive
specifier|private
name|boolean
name|isCompetitive
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If orderedGroups != null we already have collected N groups and
comment|// can short circuit by comparing this document to the bottom group,
comment|// without having to find what group this document belongs to.
comment|// Even if this document belongs to a group in the top N, we'll know that
comment|// we don't have to update that group.
comment|// Downside: if the number of unique groups is very low, this is
comment|// wasted effort as we will most likely be updating an existing group.
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|leafComparators
index|[
name|compIDX
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive. So don't even bother to continue
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isCompetitive
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
return|return;
comment|// TODO: should we add option to mean "ignore docs that
comment|// don't have the group field" (instead of stuffing them
comment|// under null group)?
name|groupSelector
operator|.
name|advanceTo
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|T
name|groupValue
init|=
name|groupSelector
operator|.
name|currentValue
argument_list|()
decl_stmt|;
specifier|final
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
name|group
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this group, or, we've seen
comment|// it before but it fell out of the top N and is now
comment|// coming back
if|if
condition|(
name|groupMap
operator|.
name|size
argument_list|()
operator|<
name|topNGroups
condition|)
block|{
comment|// Still in startup transient: we have not
comment|// seen enough unique groups to start pruning them;
comment|// just keep collecting them
comment|// Add a new CollectedSearchGroup:
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
name|sg
init|=
operator|new
name|CollectedSearchGroup
argument_list|<>
argument_list|()
decl_stmt|;
name|sg
operator|.
name|groupValue
operator|=
name|groupSelector
operator|.
name|copyValue
argument_list|()
expr_stmt|;
name|sg
operator|.
name|comparatorSlot
operator|=
name|groupMap
operator|.
name|size
argument_list|()
expr_stmt|;
name|sg
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
for|for
control|(
name|LeafFieldComparator
name|fc
range|:
name|leafComparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
name|sg
operator|.
name|comparatorSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|groupMap
operator|.
name|put
argument_list|(
name|sg
operator|.
name|groupValue
argument_list|,
name|sg
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupMap
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
condition|)
block|{
comment|// End of startup transient: we now have max
comment|// number of groups; from here on we will drop
comment|// bottom group when we insert new one:
name|buildSortedSet
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
comment|// We already tested that the document is competitive, so replace
comment|// the bottom group with this new group.
specifier|final
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
name|bottomGroup
init|=
name|orderedGroups
operator|.
name|pollLast
argument_list|()
decl_stmt|;
assert|assert
name|orderedGroups
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
operator|-
literal|1
assert|;
name|groupMap
operator|.
name|remove
argument_list|(
name|bottomGroup
operator|.
name|groupValue
argument_list|)
expr_stmt|;
comment|// reuse the removed CollectedSearchGroup
name|bottomGroup
operator|.
name|groupValue
operator|=
name|groupSelector
operator|.
name|copyValue
argument_list|()
expr_stmt|;
name|bottomGroup
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
for|for
control|(
name|LeafFieldComparator
name|fc
range|:
name|leafComparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
name|bottomGroup
operator|.
name|comparatorSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|groupMap
operator|.
name|put
argument_list|(
name|bottomGroup
operator|.
name|groupValue
argument_list|,
name|bottomGroup
argument_list|)
expr_stmt|;
name|orderedGroups
operator|.
name|add
argument_list|(
name|bottomGroup
argument_list|)
expr_stmt|;
assert|assert
name|orderedGroups
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
assert|;
specifier|final
name|int
name|lastComparatorSlot
init|=
name|orderedGroups
operator|.
name|last
argument_list|()
operator|.
name|comparatorSlot
decl_stmt|;
for|for
control|(
name|LeafFieldComparator
name|fc
range|:
name|leafComparators
control|)
block|{
name|fc
operator|.
name|setBottom
argument_list|(
name|lastComparatorSlot
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// Update existing group:
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
name|leafComparators
index|[
name|compIDX
index|]
operator|.
name|copy
argument_list|(
name|spareSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compare
argument_list|(
name|group
operator|.
name|comparatorSlot
argument_list|,
name|spareSlot
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive.
return|return;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive; set remaining comparators:
for|for
control|(
name|int
name|compIDX2
init|=
name|compIDX
operator|+
literal|1
init|;
name|compIDX2
operator|<
name|comparators
operator|.
name|length
condition|;
name|compIDX2
operator|++
control|)
block|{
name|leafComparators
index|[
name|compIDX2
index|]
operator|.
name|copy
argument_list|(
name|spareSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
comment|// Remove before updating the group since lookup is done via comparators
comment|// TODO: optimize this
specifier|final
name|CollectedSearchGroup
argument_list|<
name|T
argument_list|>
name|prevLast
decl_stmt|;
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
block|{
name|prevLast
operator|=
name|orderedGroups
operator|.
name|last
argument_list|()
expr_stmt|;
name|orderedGroups
operator|.
name|remove
argument_list|(
name|group
argument_list|)
expr_stmt|;
assert|assert
name|orderedGroups
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
operator|-
literal|1
assert|;
block|}
else|else
block|{
name|prevLast
operator|=
literal|null
expr_stmt|;
block|}
name|group
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// Swap slots
specifier|final
name|int
name|tmp
init|=
name|spareSlot
decl_stmt|;
name|spareSlot
operator|=
name|group
operator|.
name|comparatorSlot
expr_stmt|;
name|group
operator|.
name|comparatorSlot
operator|=
name|tmp
expr_stmt|;
comment|// Re-add the changed group
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
block|{
name|orderedGroups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
assert|assert
name|orderedGroups
operator|.
name|size
argument_list|()
operator|==
name|topNGroups
assert|;
specifier|final
name|CollectedSearchGroup
argument_list|<
name|?
argument_list|>
name|newLast
init|=
name|orderedGroups
operator|.
name|last
argument_list|()
decl_stmt|;
comment|// If we changed the value of the last group, or changed which group was last, then update bottom:
if|if
condition|(
name|group
operator|==
name|newLast
operator|||
name|prevLast
operator|!=
name|newLast
condition|)
block|{
for|for
control|(
name|LeafFieldComparator
name|fc
range|:
name|leafComparators
control|)
block|{
name|fc
operator|.
name|setBottom
argument_list|(
name|newLast
operator|.
name|comparatorSlot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|buildSortedSet
specifier|private
name|void
name|buildSortedSet
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Comparator
argument_list|<
name|CollectedSearchGroup
argument_list|<
name|?
argument_list|>
argument_list|>
name|comparator
init|=
operator|new
name|Comparator
argument_list|<
name|CollectedSearchGroup
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|CollectedSearchGroup
argument_list|<
name|?
argument_list|>
name|o1
parameter_list|,
name|CollectedSearchGroup
argument_list|<
name|?
argument_list|>
name|o2
parameter_list|)
block|{
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
condition|;
name|compIDX
operator|++
control|)
block|{
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|fc
init|=
name|comparators
index|[
name|compIDX
index|]
decl_stmt|;
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|compIDX
index|]
operator|*
name|fc
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|comparatorSlot
argument_list|,
name|o2
operator|.
name|comparatorSlot
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
elseif|else
if|if
condition|(
name|compIDX
operator|==
name|compIDXEnd
condition|)
block|{
return|return
name|o1
operator|.
name|topDoc
operator|-
name|o2
operator|.
name|topDoc
return|;
block|}
block|}
block|}
block|}
empty_stmt|;
name|orderedGroups
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
name|orderedGroups
operator|.
name|addAll
argument_list|(
name|groupMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|orderedGroups
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
for|for
control|(
name|LeafFieldComparator
name|fc
range|:
name|leafComparators
control|)
block|{
name|fc
operator|.
name|setBottom
argument_list|(
name|orderedGroups
operator|.
name|last
argument_list|()
operator|.
name|comparatorSlot
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|readerContext
operator|.
name|docBase
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|leafComparators
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|getLeafComparator
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
name|groupSelector
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the GroupSelector used for this Collector    */
DECL|method|getGroupSelector
specifier|public
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|getGroupSelector
parameter_list|()
block|{
return|return
name|groupSelector
return|;
block|}
block|}
end_class

end_unit

