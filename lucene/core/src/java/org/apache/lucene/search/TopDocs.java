begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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

begin_comment
comment|/** Represents hits returned by {@link  * IndexSearcher#search(Query,Filter,int)} and {@link  * IndexSearcher#search(Query,int)}. */
end_comment

begin_class
DECL|class|TopDocs
specifier|public
class|class
name|TopDocs
block|{
comment|/** The total number of hits for the query. */
DECL|field|totalHits
specifier|public
name|int
name|totalHits
decl_stmt|;
comment|/** The top hits for the query. */
DECL|field|scoreDocs
specifier|public
name|ScoreDoc
index|[]
name|scoreDocs
decl_stmt|;
comment|/** Stores the maximum score value encountered, needed for normalizing. */
DECL|field|maxScore
specifier|private
name|float
name|maxScore
decl_stmt|;
comment|/**    * Returns the maximum score value encountered. Note that in case    * scores are not tracked, this returns {@link Float#NaN}.    */
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
comment|/** Sets the maximum score value encountered. */
DECL|method|setMaxScore
specifier|public
name|void
name|setMaxScore
parameter_list|(
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
comment|/** Constructs a TopDocs with a default maxScore=Float.NaN. */
DECL|method|TopDocs
name|TopDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|)
block|{
name|this
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
DECL|method|TopDocs
specifier|public
name|TopDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
name|this
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
comment|// Refers to one hit:
DECL|class|ShardRef
specifier|private
specifier|static
class|class
name|ShardRef
block|{
comment|// Which shard (index into shardHits[]):
DECL|field|shardIndex
specifier|final
name|int
name|shardIndex
decl_stmt|;
comment|// Which hit within the shard:
DECL|field|hitIndex
name|int
name|hitIndex
decl_stmt|;
DECL|method|ShardRef
specifier|public
name|ShardRef
parameter_list|(
name|int
name|shardIndex
parameter_list|)
block|{
name|this
operator|.
name|shardIndex
operator|=
name|shardIndex
expr_stmt|;
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
literal|"ShardRef(shardIndex="
operator|+
name|shardIndex
operator|+
literal|" hitIndex="
operator|+
name|hitIndex
operator|+
literal|")"
return|;
block|}
block|}
empty_stmt|;
comment|// Specialized MergeSortQueue that just merges by
comment|// relevance score, descending:
DECL|class|ScoreMergeSortQueue
specifier|private
specifier|static
class|class
name|ScoreMergeSortQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ShardRef
argument_list|>
block|{
DECL|field|shardHits
specifier|final
name|ScoreDoc
index|[]
index|[]
name|shardHits
decl_stmt|;
DECL|method|ScoreMergeSortQueue
specifier|public
name|ScoreMergeSortQueue
parameter_list|(
name|TopDocs
index|[]
name|shardHits
parameter_list|)
block|{
name|super
argument_list|(
name|shardHits
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardHits
operator|=
operator|new
name|ScoreDoc
index|[
name|shardHits
operator|.
name|length
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|shardIDX
init|=
literal|0
init|;
name|shardIDX
operator|<
name|shardHits
operator|.
name|length
condition|;
name|shardIDX
operator|++
control|)
block|{
name|this
operator|.
name|shardHits
index|[
name|shardIDX
index|]
operator|=
name|shardHits
index|[
name|shardIDX
index|]
operator|.
name|scoreDocs
expr_stmt|;
block|}
block|}
comment|// Returns true if first is< second
annotation|@
name|Override
DECL|method|lessThan
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|ShardRef
name|first
parameter_list|,
name|ShardRef
name|second
parameter_list|)
block|{
assert|assert
name|first
operator|!=
name|second
assert|;
specifier|final
name|float
name|firstScore
init|=
name|shardHits
index|[
name|first
operator|.
name|shardIndex
index|]
index|[
name|first
operator|.
name|hitIndex
index|]
operator|.
name|score
decl_stmt|;
specifier|final
name|float
name|secondScore
init|=
name|shardHits
index|[
name|second
operator|.
name|shardIndex
index|]
index|[
name|second
operator|.
name|hitIndex
index|]
operator|.
name|score
decl_stmt|;
if|if
condition|(
name|firstScore
operator|<
name|secondScore
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|firstScore
operator|>
name|secondScore
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Tie break: earlier shard wins
if|if
condition|(
name|first
operator|.
name|shardIndex
operator|<
name|second
operator|.
name|shardIndex
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|first
operator|.
name|shardIndex
operator|>
name|second
operator|.
name|shardIndex
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// Tie break in same shard: resolve however the
comment|// shard had resolved it:
assert|assert
name|first
operator|.
name|hitIndex
operator|!=
name|second
operator|.
name|hitIndex
assert|;
return|return
name|first
operator|.
name|hitIndex
operator|<
name|second
operator|.
name|hitIndex
return|;
block|}
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|class|MergeSortQueue
specifier|private
specifier|static
class|class
name|MergeSortQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ShardRef
argument_list|>
block|{
comment|// These are really FieldDoc instances:
DECL|field|shardHits
specifier|final
name|ScoreDoc
index|[]
index|[]
name|shardHits
decl_stmt|;
DECL|field|comparators
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
index|[]
name|comparators
decl_stmt|;
DECL|field|reverseMul
specifier|final
name|int
index|[]
name|reverseMul
decl_stmt|;
DECL|method|MergeSortQueue
specifier|public
name|MergeSortQueue
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|TopDocs
index|[]
name|shardHits
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|shardHits
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardHits
operator|=
operator|new
name|ScoreDoc
index|[
name|shardHits
operator|.
name|length
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|shardIDX
init|=
literal|0
init|;
name|shardIDX
operator|<
name|shardHits
operator|.
name|length
condition|;
name|shardIDX
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
index|[]
name|shard
init|=
name|shardHits
index|[
name|shardIDX
index|]
operator|.
name|scoreDocs
decl_stmt|;
comment|//System.out.println("  init shardIdx=" + shardIDX + " hits=" + shard);
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|shardHits
index|[
name|shardIDX
index|]
operator|=
name|shard
expr_stmt|;
comment|// Fail gracefully if API is misused:
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|shard
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
name|sd
init|=
name|shard
index|[
name|hitIDX
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|sd
operator|instanceof
name|FieldDoc
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shard "
operator|+
name|shardIDX
operator|+
literal|" was not sorted by the provided Sort (expected FieldDoc but got ScoreDoc)"
argument_list|)
throw|;
block|}
specifier|final
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|sd
decl_stmt|;
if|if
condition|(
name|fd
operator|.
name|fields
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shard "
operator|+
name|shardIDX
operator|+
literal|" did not set sort field values (FieldDoc.fields is null); you must pass fillFields=true to IndexSearcher.search on each shard"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sort
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
name|reverseMul
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
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|sortFields
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|SortField
name|sortField
init|=
name|sortFields
index|[
name|compIDX
index|]
decl_stmt|;
name|comparators
index|[
name|compIDX
index|]
operator|=
name|sortField
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|compIDX
argument_list|)
expr_stmt|;
name|reverseMul
index|[
name|compIDX
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
block|}
comment|// Returns true if first is< second
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|lessThan
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|ShardRef
name|first
parameter_list|,
name|ShardRef
name|second
parameter_list|)
block|{
assert|assert
name|first
operator|!=
name|second
assert|;
specifier|final
name|FieldDoc
name|firstFD
init|=
operator|(
name|FieldDoc
operator|)
name|shardHits
index|[
name|first
operator|.
name|shardIndex
index|]
index|[
name|first
operator|.
name|hitIndex
index|]
decl_stmt|;
specifier|final
name|FieldDoc
name|secondFD
init|=
operator|(
name|FieldDoc
operator|)
name|shardHits
index|[
name|second
operator|.
name|shardIndex
index|]
index|[
name|second
operator|.
name|hitIndex
index|]
decl_stmt|;
comment|//System.out.println("  lessThan:\n     first=" + first + " doc=" + firstFD.doc + " score=" + firstFD.score + "\n    second=" + second + " doc=" + secondFD.doc + " score=" + secondFD.score);
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
specifier|final
name|FieldComparator
name|comp
init|=
name|comparators
index|[
name|compIDX
index|]
decl_stmt|;
comment|//System.out.println("    cmp idx=" + compIDX + " cmp1=" + firstFD.fields[compIDX] + " cmp2=" + secondFD.fields[compIDX] + " reverse=" + reverseMul[compIDX]);
specifier|final
name|int
name|cmp
init|=
name|reverseMul
index|[
name|compIDX
index|]
operator|*
name|comp
operator|.
name|compareValues
argument_list|(
name|firstFD
operator|.
name|fields
index|[
name|compIDX
index|]
argument_list|,
name|secondFD
operator|.
name|fields
index|[
name|compIDX
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
comment|//System.out.println("    return " + (cmp< 0));
return|return
name|cmp
operator|<
literal|0
return|;
block|}
block|}
comment|// Tie break: earlier shard wins
if|if
condition|(
name|first
operator|.
name|shardIndex
operator|<
name|second
operator|.
name|shardIndex
condition|)
block|{
comment|//System.out.println("    return tb true");
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|first
operator|.
name|shardIndex
operator|>
name|second
operator|.
name|shardIndex
condition|)
block|{
comment|//System.out.println("    return tb false");
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// Tie break in same shard: resolve however the
comment|// shard had resolved it:
comment|//System.out.println("    return tb " + (first.hitIndex< second.hitIndex));
assert|assert
name|first
operator|.
name|hitIndex
operator|!=
name|second
operator|.
name|hitIndex
assert|;
return|return
name|first
operator|.
name|hitIndex
operator|<
name|second
operator|.
name|hitIndex
return|;
block|}
block|}
block|}
comment|/** Returns a new TopDocs, containing topN results across    *  the provided TopDocs, sorting by the specified {@link    *  Sort}.  Each of the TopDocs must have been sorted by    *  the same Sort, and sort field values must have been    *  filled (ie,<code>fillFields=true</code> must be    *  passed to {@link    *  TopFieldCollector#create}.    *    *<p>Pass sort=null to merge sort by score descending.    *    * @lucene.experimental */
DECL|method|merge
specifier|public
specifier|static
name|TopDocs
name|merge
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|int
name|topN
parameter_list|,
name|TopDocs
index|[]
name|shardHits
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PriorityQueue
argument_list|<
name|ShardRef
argument_list|>
name|queue
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|queue
operator|=
operator|new
name|ScoreMergeSortQueue
argument_list|(
name|shardHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|=
operator|new
name|MergeSortQueue
argument_list|(
name|sort
argument_list|,
name|shardHits
argument_list|)
expr_stmt|;
block|}
name|int
name|totalHitCount
init|=
literal|0
decl_stmt|;
name|int
name|availHitCount
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|MIN_VALUE
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
name|shardHits
operator|.
name|length
condition|;
name|shardIDX
operator|++
control|)
block|{
specifier|final
name|TopDocs
name|shard
init|=
name|shardHits
index|[
name|shardIDX
index|]
decl_stmt|;
comment|// totalHits can be non-zero even if no hits were
comment|// collected, when searchAfter was used:
name|totalHitCount
operator|+=
name|shard
operator|.
name|totalHits
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|scoreDocs
operator|!=
literal|null
operator|&&
name|shard
operator|.
name|scoreDocs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|availHitCount
operator|+=
name|shard
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|ShardRef
argument_list|(
name|shardIDX
argument_list|)
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
name|shard
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("  maxScore now " + maxScore + " vs " + shard.getMaxScore());
block|}
block|}
if|if
condition|(
name|availHitCount
operator|==
literal|0
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
specifier|final
name|ScoreDoc
index|[]
name|hits
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|availHitCount
argument_list|)
index|]
decl_stmt|;
name|int
name|hitUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|hitUpto
operator|<
name|hits
operator|.
name|length
condition|)
block|{
assert|assert
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|ShardRef
name|ref
init|=
name|queue
operator|.
name|pop
argument_list|()
decl_stmt|;
specifier|final
name|ScoreDoc
name|hit
init|=
name|shardHits
index|[
name|ref
operator|.
name|shardIndex
index|]
operator|.
name|scoreDocs
index|[
name|ref
operator|.
name|hitIndex
operator|++
index|]
decl_stmt|;
name|hit
operator|.
name|shardIndex
operator|=
name|ref
operator|.
name|shardIndex
expr_stmt|;
name|hits
index|[
name|hitUpto
index|]
operator|=
name|hit
expr_stmt|;
comment|//System.out.println("  hitUpto=" + hitUpto);
comment|//System.out.println("    doc=" + hits[hitUpto].doc + " score=" + hits[hitUpto].score);
name|hitUpto
operator|++
expr_stmt|;
if|if
condition|(
name|ref
operator|.
name|hitIndex
operator|<
name|shardHits
index|[
name|ref
operator|.
name|shardIndex
index|]
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
comment|// Not done with this these TopDocs yet:
name|queue
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHitCount
argument_list|,
name|hits
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|totalHitCount
argument_list|,
name|hits
argument_list|,
name|sort
operator|.
name|getSort
argument_list|()
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

