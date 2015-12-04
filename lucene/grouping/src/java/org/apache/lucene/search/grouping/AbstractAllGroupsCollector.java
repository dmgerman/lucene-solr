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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A collector that collects all groups that match the  * query. Only the group value is collected, and the order  * is undefined.  This collector does not determine  * the most relevant document of a group.  *<p>  * This is an abstract version. Concrete implementations define  * what a group actually is and how it is internally collected.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AbstractAllGroupsCollector
specifier|public
specifier|abstract
class|class
name|AbstractAllGroupsCollector
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
extends|extends
name|SimpleCollector
block|{
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
comment|/**    * Returns the group values    *<p>    * This is an unordered collections of group values. For each group that matched the query there is a {@link BytesRef}    * representing a group value.    *    * @return the group values    */
DECL|method|getGroups
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|getGroups
parameter_list|()
function_decl|;
comment|// Empty not necessary
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

