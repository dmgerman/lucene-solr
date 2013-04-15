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
name|ScoreDoc
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
name|TopDocs
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

begin_comment
comment|/** Represents result returned by a grouping search.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|TopGroups
specifier|public
class|class
name|TopGroups
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
block|{
comment|/** Number of documents matching the search */
DECL|field|totalHitCount
specifier|public
specifier|final
name|int
name|totalHitCount
decl_stmt|;
comment|/** Number of documents grouped into the topN groups */
DECL|field|totalGroupedHitCount
specifier|public
specifier|final
name|int
name|totalGroupedHitCount
decl_stmt|;
comment|/** The total number of unique groups. If<code>null</code> this value is not computed. */
DECL|field|totalGroupCount
specifier|public
specifier|final
name|Integer
name|totalGroupCount
decl_stmt|;
comment|/** Group results in groupSort order */
DECL|field|groups
specifier|public
specifier|final
name|GroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
index|[]
name|groups
decl_stmt|;
comment|/** How groups are sorted against each other */
DECL|field|groupSort
specifier|public
specifier|final
name|SortField
index|[]
name|groupSort
decl_stmt|;
comment|/** How docs are sorted within each group */
DECL|field|withinGroupSort
specifier|public
specifier|final
name|SortField
index|[]
name|withinGroupSort
decl_stmt|;
comment|/** Highest score across all hits, or    *<code>Float.NaN</code> if scores were not computed. */
DECL|field|maxScore
specifier|public
specifier|final
name|float
name|maxScore
decl_stmt|;
DECL|method|TopGroups
specifier|public
name|TopGroups
parameter_list|(
name|SortField
index|[]
name|groupSort
parameter_list|,
name|SortField
index|[]
name|withinGroupSort
parameter_list|,
name|int
name|totalHitCount
parameter_list|,
name|int
name|totalGroupedHitCount
parameter_list|,
name|GroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
index|[]
name|groups
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
name|this
operator|.
name|withinGroupSort
operator|=
name|withinGroupSort
expr_stmt|;
name|this
operator|.
name|totalHitCount
operator|=
name|totalHitCount
expr_stmt|;
name|this
operator|.
name|totalGroupedHitCount
operator|=
name|totalGroupedHitCount
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|totalGroupCount
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
DECL|method|TopGroups
specifier|public
name|TopGroups
parameter_list|(
name|TopGroups
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|oldTopGroups
parameter_list|,
name|Integer
name|totalGroupCount
parameter_list|)
block|{
name|this
operator|.
name|groupSort
operator|=
name|oldTopGroups
operator|.
name|groupSort
expr_stmt|;
name|this
operator|.
name|withinGroupSort
operator|=
name|oldTopGroups
operator|.
name|withinGroupSort
expr_stmt|;
name|this
operator|.
name|totalHitCount
operator|=
name|oldTopGroups
operator|.
name|totalHitCount
expr_stmt|;
name|this
operator|.
name|totalGroupedHitCount
operator|=
name|oldTopGroups
operator|.
name|totalGroupedHitCount
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|oldTopGroups
operator|.
name|groups
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|oldTopGroups
operator|.
name|maxScore
expr_stmt|;
name|this
operator|.
name|totalGroupCount
operator|=
name|totalGroupCount
expr_stmt|;
block|}
comment|/** How the GroupDocs score (if any) should be merged. */
DECL|enum|ScoreMergeMode
specifier|public
enum|enum
name|ScoreMergeMode
block|{
comment|/** Set score to Float.NaN */
DECL|enum constant|None
name|None
block|,
comment|/* Sum score across all shards for this group. */
DECL|enum constant|Total
name|Total
block|,
comment|/* Avg score across all shards for this group. */
DECL|enum constant|Avg
name|Avg
block|,   }
comment|/** Merges an array of TopGroups, for example obtained    *  from the second-pass collector across multiple    *  shards.  Each TopGroups must have been sorted by the    *  same groupSort and docSort, and the top groups passed    *  to all second-pass collectors must be the same.    *    *<b>NOTE</b>: We can't always compute an exact totalGroupCount.    * Documents belonging to a group may occur on more than    * one shard and thus the merged totalGroupCount can be    * higher than the actual totalGroupCount. In this case the    * totalGroupCount represents a upper bound. If the documents    * of one group do only reside in one shard then the    * totalGroupCount is exact.    *    *<b>NOTE</b>: the topDocs in each GroupDocs is actually    * an instance of TopDocsAndShards    */
DECL|method|merge
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|TopGroups
argument_list|<
name|T
argument_list|>
name|merge
parameter_list|(
name|TopGroups
argument_list|<
name|T
argument_list|>
index|[]
name|shardGroups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|docSort
parameter_list|,
name|int
name|docOffset
parameter_list|,
name|int
name|docTopN
parameter_list|,
name|ScoreMergeMode
name|scoreMergeMode
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("TopGroups.merge");
if|if
condition|(
name|shardGroups
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|totalHitCount
init|=
literal|0
decl_stmt|;
name|int
name|totalGroupedHitCount
init|=
literal|0
decl_stmt|;
comment|// Optionally merge the totalGroupCount.
name|Integer
name|totalGroupCount
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numGroups
init|=
name|shardGroups
index|[
literal|0
index|]
operator|.
name|groups
operator|.
name|length
decl_stmt|;
for|for
control|(
name|TopGroups
argument_list|<
name|T
argument_list|>
name|shard
range|:
name|shardGroups
control|)
block|{
if|if
condition|(
name|numGroups
operator|!=
name|shard
operator|.
name|groups
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"number of groups differs across shards; you must pass same top groups to all shards' second-pass collector"
argument_list|)
throw|;
block|}
name|totalHitCount
operator|+=
name|shard
operator|.
name|totalHitCount
expr_stmt|;
name|totalGroupedHitCount
operator|+=
name|shard
operator|.
name|totalGroupedHitCount
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|totalGroupCount
operator|==
literal|null
condition|)
block|{
name|totalGroupCount
operator|=
literal|0
expr_stmt|;
block|}
name|totalGroupCount
operator|+=
name|shard
operator|.
name|totalGroupCount
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|final
name|GroupDocs
argument_list|<
name|T
argument_list|>
index|[]
name|mergedGroupDocs
init|=
operator|new
name|GroupDocs
index|[
name|numGroups
index|]
decl_stmt|;
specifier|final
name|TopDocs
index|[]
name|shardTopDocs
init|=
operator|new
name|TopDocs
index|[
name|shardGroups
operator|.
name|length
index|]
decl_stmt|;
name|float
name|totalMaxScore
init|=
name|Float
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|int
name|groupIDX
init|=
literal|0
init|;
name|groupIDX
operator|<
name|numGroups
condition|;
name|groupIDX
operator|++
control|)
block|{
specifier|final
name|T
name|groupValue
init|=
name|shardGroups
index|[
literal|0
index|]
operator|.
name|groups
index|[
name|groupIDX
index|]
operator|.
name|groupValue
decl_stmt|;
comment|//System.out.println("  merge groupValue=" + groupValue + " sortValues=" + Arrays.toString(shardGroups[0].groups[groupIDX].groupSortValues));
name|float
name|maxScore
init|=
name|Float
operator|.
name|MIN_VALUE
decl_stmt|;
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
name|double
name|scoreSum
init|=
literal|0.0
decl_stmt|;
for|for
control|(
name|int
name|shardIDX
init|=
literal|0
init|;
name|shardIDX
operator|<
name|shardGroups
operator|.
name|length
condition|;
name|shardIDX
operator|++
control|)
block|{
comment|//System.out.println("    shard=" + shardIDX);
specifier|final
name|TopGroups
argument_list|<
name|T
argument_list|>
name|shard
init|=
name|shardGroups
index|[
name|shardIDX
index|]
decl_stmt|;
specifier|final
name|GroupDocs
argument_list|<
name|?
argument_list|>
name|shardGroupDocs
init|=
name|shard
operator|.
name|groups
index|[
name|groupIDX
index|]
decl_stmt|;
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|shardGroupDocs
operator|.
name|groupValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"group values differ across shards; you must pass same top groups to all shards' second-pass collector"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|groupValue
operator|.
name|equals
argument_list|(
name|shardGroupDocs
operator|.
name|groupValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"group values differ across shards; you must pass same top groups to all shards' second-pass collector"
argument_list|)
throw|;
block|}
comment|/*         for(ScoreDoc sd : shardGroupDocs.scoreDocs) {           System.out.println("      doc=" + sd.doc);         }         */
name|shardTopDocs
index|[
name|shardIDX
index|]
operator|=
operator|new
name|TopDocs
argument_list|(
name|shardGroupDocs
operator|.
name|totalHits
argument_list|,
name|shardGroupDocs
operator|.
name|scoreDocs
argument_list|,
name|shardGroupDocs
operator|.
name|maxScore
argument_list|)
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|shardGroupDocs
operator|.
name|maxScore
argument_list|)
expr_stmt|;
name|totalHits
operator|+=
name|shardGroupDocs
operator|.
name|totalHits
expr_stmt|;
name|scoreSum
operator|+=
name|shardGroupDocs
operator|.
name|score
expr_stmt|;
block|}
specifier|final
name|TopDocs
name|mergedTopDocs
init|=
name|TopDocs
operator|.
name|merge
argument_list|(
name|docSort
argument_list|,
name|docOffset
operator|+
name|docTopN
argument_list|,
name|shardTopDocs
argument_list|)
decl_stmt|;
comment|// Slice;
specifier|final
name|ScoreDoc
index|[]
name|mergedScoreDocs
decl_stmt|;
if|if
condition|(
name|docOffset
operator|==
literal|0
condition|)
block|{
name|mergedScoreDocs
operator|=
name|mergedTopDocs
operator|.
name|scoreDocs
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docOffset
operator|>=
name|mergedTopDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
name|mergedScoreDocs
operator|=
operator|new
name|ScoreDoc
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|mergedScoreDocs
operator|=
operator|new
name|ScoreDoc
index|[
name|mergedTopDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|-
name|docOffset
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mergedTopDocs
operator|.
name|scoreDocs
argument_list|,
name|docOffset
argument_list|,
name|mergedScoreDocs
argument_list|,
literal|0
argument_list|,
name|mergedTopDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|-
name|docOffset
argument_list|)
expr_stmt|;
block|}
specifier|final
name|float
name|groupScore
decl_stmt|;
switch|switch
condition|(
name|scoreMergeMode
condition|)
block|{
case|case
name|None
case|:
name|groupScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
break|break;
case|case
name|Avg
case|:
if|if
condition|(
name|totalHits
operator|>
literal|0
condition|)
block|{
name|groupScore
operator|=
call|(
name|float
call|)
argument_list|(
name|scoreSum
operator|/
name|totalHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
break|break;
case|case
name|Total
case|:
name|groupScore
operator|=
operator|(
name|float
operator|)
name|scoreSum
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't handle ScoreMergeMode "
operator|+
name|scoreMergeMode
argument_list|)
throw|;
block|}
comment|//System.out.println("SHARDS=" + Arrays.toString(mergedTopDocs.shardIndex));
name|mergedGroupDocs
index|[
name|groupIDX
index|]
operator|=
operator|new
name|GroupDocs
argument_list|<>
argument_list|(
name|groupScore
argument_list|,
name|maxScore
argument_list|,
name|totalHits
argument_list|,
name|mergedScoreDocs
argument_list|,
name|groupValue
argument_list|,
name|shardGroups
index|[
literal|0
index|]
operator|.
name|groups
index|[
name|groupIDX
index|]
operator|.
name|groupSortValues
argument_list|)
expr_stmt|;
name|totalMaxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|totalMaxScore
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
name|TopGroups
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|TopGroups
argument_list|<>
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|docSort
operator|==
literal|null
condition|?
literal|null
else|:
name|docSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|mergedGroupDocs
argument_list|,
name|totalMaxScore
argument_list|)
decl_stmt|;
return|return
operator|new
name|TopGroups
argument_list|<>
argument_list|(
name|result
argument_list|,
name|totalGroupCount
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TopGroups
argument_list|<>
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|docSort
operator|==
literal|null
condition|?
literal|null
else|:
name|docSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|mergedGroupDocs
argument_list|,
name|totalMaxScore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

