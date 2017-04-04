begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|AllGroupsCollector
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
name|FirstPassGroupingCollector
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
name|function
operator|.
name|FunctionAllGroupsCollector
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
name|function
operator|.
name|FunctionFirstPassGroupingCollector
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
name|TermFirstPassGroupingCollector
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
name|*
import|;
end_import

begin_comment
comment|/**  * Creates all the collectors needed for the first phase and how to handle the results.  */
end_comment

begin_class
DECL|class|SearchGroupsFieldCommand
specifier|public
class|class
name|SearchGroupsFieldCommand
implements|implements
name|Command
argument_list|<
name|SearchGroupsFieldCommandResult
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
DECL|field|topNGroups
specifier|private
name|Integer
name|topNGroups
decl_stmt|;
DECL|field|includeGroupCount
specifier|private
name|boolean
name|includeGroupCount
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
DECL|method|setTopNGroups
specifier|public
name|Builder
name|setTopNGroups
parameter_list|(
name|int
name|topNGroups
parameter_list|)
block|{
name|this
operator|.
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIncludeGroupCount
specifier|public
name|Builder
name|setIncludeGroupCount
parameter_list|(
name|boolean
name|includeGroupCount
parameter_list|)
block|{
name|this
operator|.
name|includeGroupCount
operator|=
name|includeGroupCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|SearchGroupsFieldCommand
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
name|topNGroups
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All fields must be set"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SearchGroupsFieldCommand
argument_list|(
name|field
argument_list|,
name|groupSort
argument_list|,
name|topNGroups
argument_list|,
name|includeGroupCount
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
DECL|field|topNGroups
specifier|private
specifier|final
name|int
name|topNGroups
decl_stmt|;
DECL|field|includeGroupCount
specifier|private
specifier|final
name|boolean
name|includeGroupCount
decl_stmt|;
DECL|field|firstPassGroupingCollector
specifier|private
name|FirstPassGroupingCollector
name|firstPassGroupingCollector
decl_stmt|;
DECL|field|allGroupsCollector
specifier|private
name|AllGroupsCollector
name|allGroupsCollector
decl_stmt|;
DECL|method|SearchGroupsFieldCommand
specifier|private
name|SearchGroupsFieldCommand
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|,
name|boolean
name|includeGroupCount
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
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
name|this
operator|.
name|includeGroupCount
operator|=
name|includeGroupCount
expr_stmt|;
block|}
annotation|@
name|Override
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
specifier|final
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|FieldType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|topNGroups
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|fieldType
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ValueSource
name|vs
init|=
name|fieldType
operator|.
name|getValueSource
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|firstPassGroupingCollector
operator|=
operator|new
name|FunctionFirstPassGroupingCollector
argument_list|(
name|vs
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|firstPassGroupingCollector
operator|=
operator|new
name|TermFirstPassGroupingCollector
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
expr_stmt|;
block|}
name|collectors
operator|.
name|add
argument_list|(
name|firstPassGroupingCollector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeGroupCount
condition|)
block|{
if|if
condition|(
name|fieldType
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ValueSource
name|vs
init|=
name|fieldType
operator|.
name|getValueSource
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|allGroupsCollector
operator|=
operator|new
name|FunctionAllGroupsCollector
argument_list|(
name|vs
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
name|collectors
operator|.
name|add
argument_list|(
name|allGroupsCollector
argument_list|)
expr_stmt|;
block|}
return|return
name|collectors
return|;
block|}
annotation|@
name|Override
DECL|method|result
specifier|public
name|SearchGroupsFieldCommandResult
name|result
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|topGroups
decl_stmt|;
if|if
condition|(
name|firstPassGroupingCollector
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|topGroups
operator|=
name|GroupConverter
operator|.
name|fromMutable
argument_list|(
name|field
argument_list|,
name|firstPassGroupingCollector
operator|.
name|getTopGroups
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|topGroups
operator|=
name|firstPassGroupingCollector
operator|.
name|getTopGroups
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|topGroups
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Integer
name|groupCount
decl_stmt|;
if|if
condition|(
name|allGroupsCollector
operator|!=
literal|null
condition|)
block|{
name|groupCount
operator|=
name|allGroupsCollector
operator|.
name|getGroupCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|groupCount
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|SearchGroupsFieldCommandResult
argument_list|(
name|groupCount
argument_list|,
name|topGroups
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWithinGroupSort
specifier|public
name|Sort
name|getWithinGroupSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
block|}
end_class

end_unit

