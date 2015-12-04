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
name|*
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
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * SecondPassGroupingCollector is the second of two passes  * necessary to collect grouped docs.  This pass gathers the  * top N documents per top group computed from the  * first pass. Concrete subclasses define what a group is and how it  * is internally collected.  *  *<p>See {@link org.apache.lucene.search.grouping} for more  * details including a full code example.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AbstractSecondPassGroupingCollector
specifier|public
specifier|abstract
class|class
name|AbstractSecondPassGroupingCollector
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
extends|extends
name|SimpleCollector
block|{
DECL|field|groups
specifier|private
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
argument_list|>
name|groups
decl_stmt|;
DECL|field|groupSort
specifier|private
specifier|final
name|Sort
name|groupSort
decl_stmt|;
DECL|field|withinGroupSort
specifier|private
specifier|final
name|Sort
name|withinGroupSort
decl_stmt|;
DECL|field|maxDocsPerGroup
specifier|private
specifier|final
name|int
name|maxDocsPerGroup
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|groupMap
specifier|protected
specifier|final
name|Map
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|,
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|groupDocs
specifier|protected
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
index|[]
name|groupDocs
decl_stmt|;
DECL|field|totalHitCount
specifier|private
name|int
name|totalHitCount
decl_stmt|;
DECL|field|totalGroupedHitCount
specifier|private
name|int
name|totalGroupedHitCount
decl_stmt|;
DECL|method|AbstractSecondPassGroupingCollector
specifier|public
name|AbstractSecondPassGroupingCollector
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
argument_list|>
name|groups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("SP init");
if|if
condition|(
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no groups to collect (groups is empty)"
argument_list|)
throw|;
block|}
name|this
operator|.
name|groups
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSort
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|groupSort
argument_list|)
expr_stmt|;
name|this
operator|.
name|withinGroupSort
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|withinGroupSort
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDocsPerGroup
operator|=
name|maxDocsPerGroup
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|getScores
operator|||
name|getMaxScores
operator|||
name|withinGroupSort
operator|.
name|needsScores
argument_list|()
expr_stmt|;
name|this
operator|.
name|groupMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|group
range|:
name|groups
control|)
block|{
comment|//System.out.println("  prep group=" + (group.groupValue == null ? "null" : group.groupValue.utf8ToString()));
specifier|final
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
name|collector
decl_stmt|;
if|if
condition|(
name|withinGroupSort
operator|.
name|equals
argument_list|(
name|Sort
operator|.
name|RELEVANCE
argument_list|)
condition|)
block|{
comment|// optimize to use TopScoreDocCollector
comment|// Sort by score
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|maxDocsPerGroup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Sort by fields
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|fillSortFields
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|)
expr_stmt|;
block|}
name|groupMap
operator|.
name|put
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
operator|new
name|SearchGroupDocs
argument_list|<>
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
name|collector
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|group
operator|.
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
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
name|totalHitCount
operator|++
expr_stmt|;
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|group
init|=
name|retrieveGroup
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|totalGroupedHitCount
operator|++
expr_stmt|;
name|group
operator|.
name|leafCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the group the specified doc belongs to or<code>null</code> if no group could be retrieved.    *    * @param doc The specified doc    * @return the group the specified doc belongs to or<code>null</code> if no group could be retrieved    * @throws IOException If an I/O related error occurred    */
DECL|method|retrieveGroup
specifier|protected
specifier|abstract
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
comment|//System.out.println("SP.setNextReader");
for|for
control|(
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|group
operator|.
name|leafCollector
operator|=
name|group
operator|.
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTopGroups
specifier|public
name|TopGroups
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|getTopGroups
parameter_list|(
name|int
name|withinGroupOffset
parameter_list|)
block|{
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
name|GROUP_VALUE_TYPE
argument_list|>
index|[]
name|groupDocsResult
init|=
operator|(
name|GroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
index|[]
operator|)
operator|new
name|GroupDocs
index|[
name|groups
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|groupIDX
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
name|SearchGroup
argument_list|<
name|?
argument_list|>
name|group
range|:
name|groups
control|)
block|{
specifier|final
name|SearchGroupDocs
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|groupDocs
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|topDocs
init|=
name|groupDocs
operator|.
name|collector
operator|.
name|topDocs
argument_list|(
name|withinGroupOffset
argument_list|,
name|maxDocsPerGroup
argument_list|)
decl_stmt|;
name|groupDocsResult
index|[
name|groupIDX
operator|++
index|]
operator|=
operator|new
name|GroupDocs
argument_list|<>
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|,
name|topDocs
operator|.
name|scoreDocs
argument_list|,
name|groupDocs
operator|.
name|groupValue
argument_list|,
name|group
operator|.
name|sortValues
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
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|withinGroupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|groupDocsResult
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
comment|// TODO: merge with SearchGroup or not?
comment|// ad: don't need to build a new hashmap
comment|// disad: blows up the size of SearchGroup if we need many of them, and couples implementations
DECL|class|SearchGroupDocs
specifier|public
class|class
name|SearchGroupDocs
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
block|{
DECL|field|groupValue
specifier|public
specifier|final
name|GROUP_VALUE_TYPE
name|groupValue
decl_stmt|;
DECL|field|collector
specifier|public
specifier|final
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
name|collector
decl_stmt|;
DECL|field|leafCollector
specifier|public
name|LeafCollector
name|leafCollector
decl_stmt|;
DECL|method|SearchGroupDocs
specifier|public
name|SearchGroupDocs
parameter_list|(
name|GROUP_VALUE_TYPE
name|groupValue
parameter_list|,
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
name|collector
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

