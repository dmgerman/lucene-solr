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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * A collector that collects all groups that match the  * query. Only the group value is collected, and the order  * is undefined.  This collector does not determine  * the most relevant document of a group.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AllGroupsCollector
specifier|public
class|class
name|AllGroupsCollector
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
DECL|field|groups
specifier|private
specifier|final
name|Set
argument_list|<
name|T
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Create a new AllGroupsCollector    * @param groupSelector the GroupSelector to determine groups    */
DECL|method|AllGroupsCollector
specifier|public
name|AllGroupsCollector
parameter_list|(
name|GroupSelector
argument_list|<
name|T
argument_list|>
name|groupSelector
parameter_list|)
block|{
name|this
operator|.
name|groupSelector
operator|=
name|groupSelector
expr_stmt|;
block|}
comment|/**    * Returns the total number of groups for the executed search.    * This is a convenience method. The following code snippet has the same effect:<pre>getGroups().size()</pre>    *    * @return The total number of groups for the executed search    */
DECL|method|getGroupCount
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
return|return
name|getGroups
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns the group values    *<p>    * This is an unordered collections of group values.    *    * @return the group values    */
DECL|method|getGroups
specifier|public
name|Collection
argument_list|<
name|T
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
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
block|{}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|groupSelector
operator|.
name|setNextReader
argument_list|(
name|context
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
name|groupSelector
operator|.
name|advanceTo
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|groupSelector
operator|.
name|currentValue
argument_list|()
argument_list|)
condition|)
return|return;
name|groups
operator|.
name|add
argument_list|(
name|groupSelector
operator|.
name|copyValue
argument_list|()
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
literal|false
return|;
comment|// the result is unaffected by relevancy
block|}
block|}
end_class

end_unit

