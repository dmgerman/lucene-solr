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

begin_comment
comment|/**  * Defines a group, for use by grouping collectors  *  * A GroupSelector acts as an iterator over documents.  For each segment, clients  * should call {@link #setNextReader(LeafReaderContext)}, and then {@link #advanceTo(int)}  * for each matching document.  *  * @param<T> the type of the group value  */
end_comment

begin_class
DECL|class|GroupSelector
specifier|public
specifier|abstract
class|class
name|GroupSelector
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * What to do with the current value    */
DECL|enum|State
DECL|enum constant|SKIP
DECL|enum constant|ACCEPT
specifier|public
enum|enum
name|State
block|{
name|SKIP
block|,
name|ACCEPT
block|}
comment|/**    * Set the LeafReaderContext    */
DECL|method|setNextReader
specifier|public
specifier|abstract
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Advance the GroupSelector's iterator to the given document    */
DECL|method|advanceTo
specifier|public
specifier|abstract
name|State
name|advanceTo
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the group value of the current document    *    * N.B. this object may be reused, for a persistent version use {@link #copyValue()}    */
DECL|method|currentValue
specifier|public
specifier|abstract
name|T
name|currentValue
parameter_list|()
function_decl|;
comment|/**    * @return a copy of the group value of the current document    */
DECL|method|copyValue
specifier|public
specifier|abstract
name|T
name|copyValue
parameter_list|()
function_decl|;
comment|/**    * Set a restriction on the group values returned by this selector    *    * If the selector is positioned on a document whose group value is not contained    * within this set, then {@link #advanceTo(int)} will return {@link State#SKIP}    *    * @param groups a set of {@link SearchGroup} objects to limit selections to    */
DECL|method|setGroups
specifier|public
specifier|abstract
name|void
name|setGroups
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|T
argument_list|>
argument_list|>
name|groups
parameter_list|)
function_decl|;
block|}
end_class

end_unit

