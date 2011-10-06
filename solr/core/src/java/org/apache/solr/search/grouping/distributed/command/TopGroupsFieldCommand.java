begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.command
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
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
name|grouping
operator|.
name|SearchGroup
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
name|grouping
operator|.
name|term
operator|.
name|TermAllGroupsCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermSecondPassGroupingCollector
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
name|grouping
operator|.
name|TopGroups
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
name|search
operator|.
name|grouping
operator|.
name|Command
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
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TopGroupsFieldCommand
specifier|public
class|class
name|TopGroupsFieldCommand
implements|implements
name|Command
argument_list|<
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|field
specifier|private
name|SchemaField
name|field
decl_stmt|;
DECL|field|groupSort
specifier|private
name|Sort
name|groupSort
decl_stmt|;
DECL|field|sortWithinGroup
specifier|private
name|Sort
name|sortWithinGroup
decl_stmt|;
DECL|field|firstPhaseGroups
specifier|private
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|firstPhaseGroups
decl_stmt|;
DECL|field|maxDocPerGroup
specifier|private
name|Integer
name|maxDocPerGroup
decl_stmt|;
DECL|field|needScores
specifier|private
name|boolean
name|needScores
init|=
literal|false
decl_stmt|;
DECL|field|needMaxScore
specifier|private
name|boolean
name|needMaxScore
init|=
literal|false
decl_stmt|;
DECL|field|needGroupCount
specifier|private
name|boolean
name|needGroupCount
init|=
literal|false
decl_stmt|;
DECL|method|setField
specifier|public
name|Builder
name|setField
parameter_list|(
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
return|return
name|this
return|;
block|}
DECL|method|setGroupSort
specifier|public
name|Builder
name|setGroupSort
parameter_list|(
name|Sort
name|groupSort
parameter_list|)
block|{
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSortWithinGroup
specifier|public
name|Builder
name|setSortWithinGroup
parameter_list|(
name|Sort
name|sortWithinGroup
parameter_list|)
block|{
name|this
operator|.
name|sortWithinGroup
operator|=
name|sortWithinGroup
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFirstPhaseGroups
specifier|public
name|Builder
name|setFirstPhaseGroups
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|firstPhaseGroups
parameter_list|)
block|{
name|this
operator|.
name|firstPhaseGroups
operator|=
name|firstPhaseGroups
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMaxDocPerGroup
specifier|public
name|Builder
name|setMaxDocPerGroup
parameter_list|(
name|int
name|maxDocPerGroup
parameter_list|)
block|{
name|this
operator|.
name|maxDocPerGroup
operator|=
name|maxDocPerGroup
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNeedScores
specifier|public
name|Builder
name|setNeedScores
parameter_list|(
name|Boolean
name|needScores
parameter_list|)
block|{
name|this
operator|.
name|needScores
operator|=
name|needScores
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNeedMaxScore
specifier|public
name|Builder
name|setNeedMaxScore
parameter_list|(
name|Boolean
name|needMaxScore
parameter_list|)
block|{
name|this
operator|.
name|needMaxScore
operator|=
name|needMaxScore
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNeedGroupCount
specifier|public
name|Builder
name|setNeedGroupCount
parameter_list|(
name|Boolean
name|needGroupCount
parameter_list|)
block|{
name|this
operator|.
name|needGroupCount
operator|=
name|needGroupCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|TopGroupsFieldCommand
name|build
parameter_list|()
block|{
if|if
condition|(
name|field
operator|==
literal|null
operator|||
name|groupSort
operator|==
literal|null
operator|||
name|sortWithinGroup
operator|==
literal|null
operator|||
name|firstPhaseGroups
operator|==
literal|null
operator|||
name|maxDocPerGroup
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All required fields must be set"
argument_list|)
throw|;
block|}
return|return
operator|new
name|TopGroupsFieldCommand
argument_list|(
name|field
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|firstPhaseGroups
argument_list|,
name|maxDocPerGroup
argument_list|,
name|needScores
argument_list|,
name|needMaxScore
argument_list|,
name|needGroupCount
argument_list|)
return|;
block|}
block|}
DECL|field|field
specifier|private
specifier|final
name|SchemaField
name|field
decl_stmt|;
DECL|field|groupSort
specifier|private
specifier|final
name|Sort
name|groupSort
decl_stmt|;
DECL|field|sortWithinGroup
specifier|private
specifier|final
name|Sort
name|sortWithinGroup
decl_stmt|;
DECL|field|firstPhaseGroups
specifier|private
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|firstPhaseGroups
decl_stmt|;
DECL|field|maxDocPerGroup
specifier|private
specifier|final
name|int
name|maxDocPerGroup
decl_stmt|;
DECL|field|needScores
specifier|private
specifier|final
name|boolean
name|needScores
decl_stmt|;
DECL|field|needMaxScore
specifier|private
specifier|final
name|boolean
name|needMaxScore
decl_stmt|;
DECL|field|needGroupCount
specifier|private
specifier|final
name|boolean
name|needGroupCount
decl_stmt|;
DECL|field|secondPassCollector
specifier|private
name|TermSecondPassGroupingCollector
name|secondPassCollector
decl_stmt|;
DECL|field|allGroupsCollector
specifier|private
name|TermAllGroupsCollector
name|allGroupsCollector
decl_stmt|;
DECL|method|TopGroupsFieldCommand
specifier|private
name|TopGroupsFieldCommand
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|firstPhaseGroups
parameter_list|,
name|int
name|maxDocPerGroup
parameter_list|,
name|boolean
name|needScores
parameter_list|,
name|boolean
name|needMaxScore
parameter_list|,
name|boolean
name|needGroupCount
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
name|this
operator|.
name|sortWithinGroup
operator|=
name|sortWithinGroup
expr_stmt|;
name|this
operator|.
name|firstPhaseGroups
operator|=
name|firstPhaseGroups
expr_stmt|;
name|this
operator|.
name|maxDocPerGroup
operator|=
name|maxDocPerGroup
expr_stmt|;
name|this
operator|.
name|needScores
operator|=
name|needScores
expr_stmt|;
name|this
operator|.
name|needMaxScore
operator|=
name|needMaxScore
expr_stmt|;
name|this
operator|.
name|needGroupCount
operator|=
name|needGroupCount
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|List
argument_list|<
name|Collector
argument_list|>
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<
name|Collector
argument_list|>
argument_list|()
decl_stmt|;
name|secondPassCollector
operator|=
operator|new
name|TermSecondPassGroupingCollector
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|firstPhaseGroups
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|maxDocPerGroup
argument_list|,
name|needScores
argument_list|,
name|needMaxScore
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|collectors
operator|.
name|add
argument_list|(
name|secondPassCollector
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|needGroupCount
condition|)
block|{
return|return
name|collectors
return|;
block|}
name|allGroupsCollector
operator|=
operator|new
name|TermAllGroupsCollector
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|collectors
operator|.
name|add
argument_list|(
name|allGroupsCollector
argument_list|)
expr_stmt|;
return|return
name|collectors
return|;
block|}
DECL|method|result
specifier|public
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|result
parameter_list|()
block|{
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|result
init|=
name|secondPassCollector
operator|.
name|getTopGroups
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|allGroupsCollector
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|result
argument_list|,
name|allGroupsCollector
operator|.
name|getGroupCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|field
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getGroupSort
specifier|public
name|Sort
name|getGroupSort
parameter_list|()
block|{
return|return
name|groupSort
return|;
block|}
DECL|method|getSortWithinGroup
specifier|public
name|Sort
name|getSortWithinGroup
parameter_list|()
block|{
return|return
name|sortWithinGroup
return|;
block|}
block|}
end_class

end_unit

