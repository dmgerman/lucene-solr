begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping.function
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
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|grouping
operator|.
name|AbstractAllGroupsCollector
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
name|mutable
operator|.
name|MutableValue
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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

begin_comment
comment|/**  * A collector that collects all groups that match the  * query. Only the group value is collected, and the order  * is undefined.  This collector does not determine  * the most relevant document of a group.  *<p>  * Implementation detail: Uses {@link ValueSource} and {@link FunctionValues} to retrieve the  * field values to group by.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FunctionAllGroupsCollector
specifier|public
class|class
name|FunctionAllGroupsCollector
extends|extends
name|AbstractAllGroupsCollector
argument_list|<
name|MutableValue
argument_list|>
block|{
DECL|field|vsContext
specifier|private
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
decl_stmt|;
DECL|field|groupBy
specifier|private
specifier|final
name|ValueSource
name|groupBy
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|MutableValue
argument_list|>
name|groups
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|filler
specifier|private
name|FunctionValues
operator|.
name|ValueFiller
name|filler
decl_stmt|;
DECL|field|mval
specifier|private
name|MutableValue
name|mval
decl_stmt|;
comment|/**    * Constructs a {@link FunctionAllGroupsCollector} instance.    *    * @param groupBy The {@link ValueSource} to group by    * @param vsContext The ValueSource context    */
DECL|method|FunctionAllGroupsCollector
specifier|public
name|FunctionAllGroupsCollector
parameter_list|(
name|ValueSource
name|groupBy
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
parameter_list|)
block|{
name|this
operator|.
name|vsContext
operator|=
name|vsContext
expr_stmt|;
name|this
operator|.
name|groupBy
operator|=
name|groupBy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGroups
specifier|public
name|Collection
argument_list|<
name|MutableValue
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
if|if
condition|(
operator|!
name|groups
operator|.
name|contains
argument_list|(
name|mval
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|mval
operator|.
name|duplicate
argument_list|()
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
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|FunctionValues
name|values
init|=
name|groupBy
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|filler
operator|=
name|values
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
block|}
block|}
end_class

end_unit

