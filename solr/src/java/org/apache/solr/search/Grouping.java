begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|function
operator|.
name|DocValues
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
name|ValueSource
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

begin_class
DECL|class|Grouping
specifier|public
class|class
name|Grouping
block|{
DECL|class|Command
specifier|public
specifier|static
class|class
name|Command
block|{
DECL|field|key
specifier|public
name|String
name|key
decl_stmt|;
comment|// the name to use for this group in the response
DECL|field|groupSort
specifier|public
name|Sort
name|groupSort
decl_stmt|;
comment|// the sort of the documents *within* a single group.
DECL|field|docsPerGroup
specifier|public
name|int
name|docsPerGroup
decl_stmt|;
comment|// how many docs in each group - from "group.limit" param, default=1
DECL|field|numGroups
specifier|public
name|int
name|numGroups
decl_stmt|;
comment|// how many groups - defaults to the "rows" parameter
block|}
DECL|class|CommandQuery
specifier|public
specifier|static
class|class
name|CommandQuery
extends|extends
name|Command
block|{
DECL|field|query
specifier|public
name|Query
name|query
decl_stmt|;
block|}
DECL|class|CommandFunc
specifier|public
specifier|static
class|class
name|CommandFunc
extends|extends
name|Command
block|{
DECL|field|groupBy
specifier|public
name|ValueSource
name|groupBy
decl_stmt|;
comment|// todo - find a better place to store these
DECL|field|context
specifier|transient
name|Map
name|context
decl_stmt|;
DECL|field|collector
specifier|transient
name|Collector
name|collector
decl_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SearchGroup
class|class
name|SearchGroup
block|{
DECL|field|groupValue
specifier|public
name|MutableValue
name|groupValue
decl_stmt|;
DECL|field|matches
name|int
name|matches
decl_stmt|;
DECL|field|topDoc
name|int
name|topDoc
decl_stmt|;
comment|// float topDocScore;  // currently unused
DECL|field|comparatorSlot
name|int
name|comparatorSlot
decl_stmt|;
comment|// currently only used when sort != sort.group
DECL|field|sortGroupComparators
name|FieldComparator
index|[]
name|sortGroupComparators
decl_stmt|;
DECL|field|sortGroupReversed
name|int
index|[]
name|sortGroupReversed
decl_stmt|;
comment|/***   @Override   public int hashCode() {     return super.hashCode();   }    @Override   public boolean equals(Object obj) {     return groupValue.equalsSameType(((SearchGroup)obj).groupValue);   }   ***/
block|}
end_class

begin_class
DECL|class|GroupCollector
specifier|abstract
class|class
name|GroupCollector
extends|extends
name|Collector
block|{
comment|/** get the number of matches before grouping or limiting have been applied */
DECL|method|getMatches
specifier|public
specifier|abstract
name|int
name|getMatches
parameter_list|()
function_decl|;
block|}
end_class

begin_class
DECL|class|FilterCollector
class|class
name|FilterCollector
extends|extends
name|GroupCollector
block|{
DECL|field|filter
specifier|private
specifier|final
name|DocSet
name|filter
decl_stmt|;
DECL|field|collector
specifier|private
specifier|final
name|TopFieldCollector
name|collector
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|matches
specifier|private
name|int
name|matches
decl_stmt|;
DECL|method|FilterCollector
specifier|public
name|FilterCollector
parameter_list|(
name|DocSet
name|filter
parameter_list|,
name|TopFieldCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
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
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
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
name|matches
operator|++
expr_stmt|;
if|if
condition|(
name|filter
operator|.
name|exists
argument_list|(
name|doc
operator|+
name|docBase
argument_list|)
condition|)
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMatches
specifier|public
name|int
name|getMatches
parameter_list|()
block|{
return|return
name|matches
return|;
block|}
DECL|method|getTopFieldCollector
name|TopFieldCollector
name|getTopFieldCollector
parameter_list|()
block|{
return|return
name|collector
return|;
block|}
block|}
end_class

begin_comment
comment|/** Finds the top set of groups, grouped by groupByVS when sort == group.sort */
end_comment

begin_class
DECL|class|TopGroupCollector
class|class
name|TopGroupCollector
extends|extends
name|GroupCollector
block|{
DECL|field|nGroups
specifier|final
name|int
name|nGroups
decl_stmt|;
DECL|field|groupMap
specifier|final
name|HashMap
argument_list|<
name|MutableValue
argument_list|,
name|SearchGroup
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|orderedGroups
name|TreeSet
argument_list|<
name|SearchGroup
argument_list|>
name|orderedGroups
decl_stmt|;
DECL|field|vs
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|field|context
specifier|final
name|Map
name|context
decl_stmt|;
DECL|field|comparators
specifier|final
name|FieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reversed
specifier|final
name|int
index|[]
name|reversed
decl_stmt|;
DECL|field|docValues
name|DocValues
name|docValues
decl_stmt|;
DECL|field|filler
name|DocValues
operator|.
name|ValueFiller
name|filler
decl_stmt|;
DECL|field|mval
name|MutableValue
name|mval
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|docBase
name|int
name|docBase
decl_stmt|;
DECL|field|spareSlot
name|int
name|spareSlot
decl_stmt|;
DECL|field|matches
name|int
name|matches
decl_stmt|;
DECL|method|TopGroupCollector
specifier|public
name|TopGroupCollector
parameter_list|(
name|ValueSource
name|groupByVS
parameter_list|,
name|Map
name|vsContext
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|nGroups
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|vs
operator|=
name|groupByVS
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|vsContext
expr_stmt|;
name|this
operator|.
name|nGroups
operator|=
name|nGroups
expr_stmt|;
name|SortField
index|[]
name|sortFields
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|this
operator|.
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
name|this
operator|.
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
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|i
index|]
decl_stmt|;
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
comment|// use nGroups + 1 so we have a spare slot to use for comparing (tracked by this.spareSlot)
name|comparators
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
name|nGroups
operator|+
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|spareSlot
operator|=
name|nGroups
expr_stmt|;
name|this
operator|.
name|groupMap
operator|=
operator|new
name|HashMap
argument_list|<
name|MutableValue
argument_list|,
name|SearchGroup
argument_list|>
argument_list|(
name|nGroups
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
name|fc
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
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
name|matches
operator|++
expr_stmt|;
comment|// if orderedGroups != null, then we already have collected N groups and
comment|// can short circuit by comparing this document to the smallest group
comment|// without having to even find what group this document belongs to.
comment|// Even if this document belongs to a group in the top N, we know that
comment|// we don't have to update that group.
comment|//
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
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reversed
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
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
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|comparators
operator|.
name|length
operator|-
literal|1
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
block|}
name|filler
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|SearchGroup
name|group
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|mval
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|int
name|num
init|=
name|groupMap
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupMap
operator|.
name|size
argument_list|()
operator|<
name|nGroups
condition|)
block|{
name|SearchGroup
name|sg
init|=
operator|new
name|SearchGroup
argument_list|()
decl_stmt|;
name|sg
operator|.
name|groupValue
operator|=
name|mval
operator|.
name|duplicate
argument_list|()
expr_stmt|;
name|sg
operator|.
name|comparatorSlot
operator|=
name|num
operator|++
expr_stmt|;
name|sg
operator|.
name|matches
operator|=
literal|1
expr_stmt|;
name|sg
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// sg.topDocScore = scorer.score();
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
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
name|nGroups
condition|)
block|{
name|buildSet
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
comment|// we already tested that the document is competitive, so replace
comment|// the smallest group with this new group.
comment|// remove current smallest group
name|SearchGroup
name|smallest
init|=
name|orderedGroups
operator|.
name|pollLast
argument_list|()
decl_stmt|;
name|groupMap
operator|.
name|remove
argument_list|(
name|smallest
operator|.
name|groupValue
argument_list|)
expr_stmt|;
comment|// reuse the removed SearchGroup
name|smallest
operator|.
name|groupValue
operator|.
name|copy
argument_list|(
name|mval
argument_list|)
expr_stmt|;
name|smallest
operator|.
name|matches
operator|=
literal|1
expr_stmt|;
name|smallest
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// smallest.topDocScore = scorer.score();
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
name|fc
operator|.
name|copy
argument_list|(
name|smallest
operator|.
name|comparatorSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|groupMap
operator|.
name|put
argument_list|(
name|smallest
operator|.
name|groupValue
argument_list|,
name|smallest
argument_list|)
expr_stmt|;
name|orderedGroups
operator|.
name|add
argument_list|(
name|smallest
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
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
return|return;
block|}
comment|//
comment|// update existing group
comment|//
name|group
operator|.
name|matches
operator|++
expr_stmt|;
comment|// TODO: these aren't valid if the group is every discarded then re-added.  keep track if there have been discards?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
name|FieldComparator
name|fc
init|=
name|comparators
index|[
name|i
index|]
decl_stmt|;
name|fc
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
name|i
index|]
operator|*
name|fc
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
comment|// Definitely competitive.
comment|// Set remaining comparators
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|comparators
operator|.
name|length
condition|;
name|j
operator|++
control|)
name|comparators
index|[
name|j
index|]
operator|.
name|copy
argument_list|(
name|spareSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|comparators
operator|.
name|length
operator|-
literal|1
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
comment|// remove before updating the group since lookup is done via comparators
comment|// TODO: optimize this
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
name|orderedGroups
operator|.
name|remove
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// group.topDocScore = scorer.score();
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
comment|// swap slots
comment|// re-add the changed group
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
name|orderedGroups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
DECL|method|buildSet
name|void
name|buildSet
parameter_list|()
block|{
name|Comparator
argument_list|<
name|SearchGroup
argument_list|>
name|comparator
init|=
operator|new
name|Comparator
argument_list|<
name|SearchGroup
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|SearchGroup
name|o1
parameter_list|,
name|SearchGroup
name|o2
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
name|FieldComparator
name|fc
init|=
name|comparators
index|[
name|i
index|]
decl_stmt|;
name|int
name|c
init|=
name|reversed
index|[
name|i
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
name|i
operator|==
name|comparators
operator|.
name|length
operator|-
literal|1
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
decl_stmt|;
name|orderedGroups
operator|=
operator|new
name|TreeSet
argument_list|<
name|SearchGroup
argument_list|>
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
if|if
condition|(
name|orderedGroups
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
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
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|docValues
operator|=
name|vs
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|filler
operator|=
name|docValues
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|mval
operator|=
name|filler
operator|.
name|getValue
argument_list|()
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
name|comparators
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getMatches
specifier|public
name|int
name|getMatches
parameter_list|()
block|{
return|return
name|matches
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * This class allows a different sort within a group than what is used between groups.  * Sorting between groups is done by the sort value of the first (highest ranking)  * document in that group.  */
end_comment

begin_class
DECL|class|TopGroupSortCollector
class|class
name|TopGroupSortCollector
extends|extends
name|TopGroupCollector
block|{
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|groupSort
name|Sort
name|groupSort
decl_stmt|;
DECL|method|TopGroupSortCollector
specifier|public
name|TopGroupSortCollector
parameter_list|(
name|ValueSource
name|groupByVS
parameter_list|,
name|Map
name|vsContext
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|nGroups
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupByVS
argument_list|,
name|vsContext
argument_list|,
name|sort
argument_list|,
name|nGroups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
block|}
DECL|method|constructComparators
name|void
name|constructComparators
parameter_list|(
name|FieldComparator
index|[]
name|comparators
parameter_list|,
name|int
index|[]
name|reversed
parameter_list|,
name|SortField
index|[]
name|sortFields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
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
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|i
index|]
decl_stmt|;
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
name|comparators
index|[
name|i
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
name|size
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
name|comparators
index|[
name|i
index|]
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|comparators
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
name|searchGroup
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|FieldComparator
name|fc
range|:
name|searchGroup
operator|.
name|sortGroupComparators
control|)
block|{
name|fc
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
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
name|matches
operator|++
expr_stmt|;
name|filler
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|SearchGroup
name|group
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|mval
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|int
name|num
init|=
name|groupMap
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupMap
operator|.
name|size
argument_list|()
operator|<
name|nGroups
condition|)
block|{
name|SearchGroup
name|sg
init|=
operator|new
name|SearchGroup
argument_list|()
decl_stmt|;
name|SortField
index|[]
name|sortGroupFields
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|sg
operator|.
name|sortGroupComparators
operator|=
operator|new
name|FieldComparator
index|[
name|sortGroupFields
operator|.
name|length
index|]
expr_stmt|;
name|sg
operator|.
name|sortGroupReversed
operator|=
operator|new
name|int
index|[
name|sortGroupFields
operator|.
name|length
index|]
expr_stmt|;
name|constructComparators
argument_list|(
name|sg
operator|.
name|sortGroupComparators
argument_list|,
name|sg
operator|.
name|sortGroupReversed
argument_list|,
name|sortGroupFields
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|sg
operator|.
name|groupValue
operator|=
name|mval
operator|.
name|duplicate
argument_list|()
expr_stmt|;
name|sg
operator|.
name|comparatorSlot
operator|=
name|num
operator|++
expr_stmt|;
name|sg
operator|.
name|matches
operator|=
literal|1
expr_stmt|;
name|sg
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// sg.topDocScore = scorer.score();
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
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
for|for
control|(
name|FieldComparator
name|fc
range|:
name|sg
operator|.
name|sortGroupComparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setBottom
argument_list|(
literal|0
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
return|return;
block|}
if|if
condition|(
name|orderedGroups
operator|==
literal|null
condition|)
block|{
name|buildSet
argument_list|()
expr_stmt|;
block|}
name|SearchGroup
name|leastSignificantGroup
init|=
name|orderedGroups
operator|.
name|last
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|leastSignificantGroup
operator|.
name|sortGroupReversed
index|[
name|i
index|]
operator|*
name|leastSignificantGroup
operator|.
name|sortGroupComparators
index|[
name|i
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
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|leastSignificantGroup
operator|.
name|sortGroupComparators
operator|.
name|length
operator|-
literal|1
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
comment|// remove current smallest group
name|SearchGroup
name|smallest
init|=
name|orderedGroups
operator|.
name|pollLast
argument_list|()
decl_stmt|;
name|groupMap
operator|.
name|remove
argument_list|(
name|smallest
operator|.
name|groupValue
argument_list|)
expr_stmt|;
comment|// reuse the removed SearchGroup
name|smallest
operator|.
name|groupValue
operator|.
name|copy
argument_list|(
name|mval
argument_list|)
expr_stmt|;
name|smallest
operator|.
name|matches
operator|=
literal|1
expr_stmt|;
name|smallest
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// smallest.topDocScore = scorer.score();
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
name|fc
operator|.
name|copy
argument_list|(
name|smallest
operator|.
name|comparatorSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|smallest
operator|.
name|sortGroupComparators
control|)
block|{
name|fc
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|fc
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|groupMap
operator|.
name|put
argument_list|(
name|smallest
operator|.
name|groupValue
argument_list|,
name|smallest
argument_list|)
expr_stmt|;
name|orderedGroups
operator|.
name|add
argument_list|(
name|smallest
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldComparator
name|fc
range|:
name|comparators
control|)
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
for|for
control|(
name|FieldComparator
name|fc
range|:
name|smallest
operator|.
name|sortGroupComparators
control|)
name|fc
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//
comment|// update existing group
comment|//
name|group
operator|.
name|matches
operator|++
expr_stmt|;
comment|// TODO: these aren't valid if the group is every discarded then re-added.  keep track if there have been discards?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
name|FieldComparator
name|fc
init|=
name|group
operator|.
name|sortGroupComparators
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|c
init|=
name|group
operator|.
name|sortGroupReversed
index|[
name|i
index|]
operator|*
name|fc
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
comment|// Definitely competitive.
comment|// Set remaining comparators
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|group
operator|.
name|sortGroupComparators
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|group
operator|.
name|sortGroupComparators
index|[
name|j
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|group
operator|.
name|sortGroupComparators
index|[
name|j
index|]
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldComparator
name|comparator
range|:
name|comparators
control|)
name|comparator
operator|.
name|copy
argument_list|(
name|spareSlot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|group
operator|.
name|sortGroupComparators
operator|.
name|length
operator|-
literal|1
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
return|return;
block|}
block|}
comment|// remove before updating the group since lookup is done via comparators
comment|// TODO: optimize this
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
name|orderedGroups
operator|.
name|remove
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|topDoc
operator|=
name|docBase
operator|+
name|doc
expr_stmt|;
comment|// group.topDocScore = scorer.score();
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
comment|// swap slots
comment|// re-add the changed group
if|if
condition|(
name|orderedGroups
operator|!=
literal|null
condition|)
name|orderedGroups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
for|for
control|(
name|SearchGroup
name|searchGroup
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchGroup
operator|.
name|sortGroupComparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|searchGroup
operator|.
name|sortGroupComparators
index|[
name|i
index|]
operator|=
name|searchGroup
operator|.
name|sortGroupComparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|Phase2GroupCollector
class|class
name|Phase2GroupCollector
extends|extends
name|Collector
block|{
DECL|field|groupMap
specifier|final
name|HashMap
argument_list|<
name|MutableValue
argument_list|,
name|SearchGroupDocs
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|vs
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|field|context
specifier|final
name|Map
name|context
decl_stmt|;
DECL|field|docValues
name|DocValues
name|docValues
decl_stmt|;
DECL|field|filler
name|DocValues
operator|.
name|ValueFiller
name|filler
decl_stmt|;
DECL|field|mval
name|MutableValue
name|mval
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|docBase
name|int
name|docBase
decl_stmt|;
comment|// TODO: may want to decouple from the phase1 collector
DECL|method|Phase2GroupCollector
specifier|public
name|Phase2GroupCollector
parameter_list|(
name|TopGroupCollector
name|topGroups
parameter_list|,
name|ValueSource
name|groupByVS
parameter_list|,
name|Map
name|vsContext
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|docsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|getSortFields
init|=
literal|false
decl_stmt|;
name|groupMap
operator|=
operator|new
name|HashMap
argument_list|<
name|MutableValue
argument_list|,
name|SearchGroupDocs
argument_list|>
argument_list|(
name|topGroups
operator|.
name|groupMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
name|group
range|:
name|topGroups
operator|.
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|SearchGroupDocs
name|groupDocs
init|=
operator|new
name|SearchGroupDocs
argument_list|()
decl_stmt|;
name|groupDocs
operator|.
name|groupValue
operator|=
name|group
operator|.
name|groupValue
expr_stmt|;
name|groupDocs
operator|.
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|docsPerGroup
argument_list|,
name|getSortFields
argument_list|,
name|getScores
argument_list|,
name|getScores
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|groupMap
operator|.
name|put
argument_list|(
name|groupDocs
operator|.
name|groupValue
argument_list|,
name|groupDocs
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|vs
operator|=
name|groupByVS
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|vsContext
expr_stmt|;
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
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|SearchGroupDocs
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
name|group
operator|.
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
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
name|filler
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|SearchGroupDocs
name|group
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|mval
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
return|return;
name|group
operator|.
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|docValues
operator|=
name|vs
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|filler
operator|=
name|docValues
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|mval
operator|=
name|filler
operator|.
name|getValue
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchGroupDocs
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
name|group
operator|.
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// TODO: merge with SearchGroup or not?
end_comment

begin_comment
comment|// ad: don't need to build a new hashmap
end_comment

begin_comment
comment|// disad: blows up the size of SearchGroup if we need many of them, and couples implementations
end_comment

begin_class
DECL|class|SearchGroupDocs
class|class
name|SearchGroupDocs
block|{
DECL|field|groupValue
specifier|public
name|MutableValue
name|groupValue
decl_stmt|;
DECL|field|collector
name|TopFieldCollector
name|collector
decl_stmt|;
block|}
end_class

end_unit

