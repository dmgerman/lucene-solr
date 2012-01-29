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
name|AtomicReader
operator|.
name|AtomicReaderContext
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
name|AbstractSecondPassGroupingCollector
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
name|util
operator|.
name|mutable
operator|.
name|MutableValue
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

begin_comment
comment|//javadoc
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Concrete implementation of {@link AbstractSecondPassGroupingCollector} that groups based on  * {@link ValueSource} instances.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FunctionSecondPassGroupingCollector
specifier|public
class|class
name|FunctionSecondPassGroupingCollector
extends|extends
name|AbstractSecondPassGroupingCollector
argument_list|<
name|MutableValue
argument_list|>
block|{
DECL|field|groupByVS
specifier|private
specifier|final
name|ValueSource
name|groupByVS
decl_stmt|;
DECL|field|vsContext
specifier|private
specifier|final
name|Map
name|vsContext
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
comment|/**    * Constructs a {@link FunctionSecondPassGroupingCollector} instance.    *    * @param searchGroups The {@link SearchGroup} instances collected during the first phase.    * @param groupSort The group sort    * @param withinGroupSort The sort inside a group    * @param maxDocsPerGroup The maximum number of documents to collect inside a group    * @param getScores Whether to include the scores    * @param getMaxScores Whether to include the maximum score    * @param fillSortFields Whether to fill the sort values in {@link TopGroups#withinGroupSort}    * @param groupByVS The {@link ValueSource} to group by    * @param vsContext The value source context    * @throws IOException IOException When I/O related errors occur    */
DECL|method|FunctionSecondPassGroupingCollector
specifier|public
name|FunctionSecondPassGroupingCollector
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|MutableValue
argument_list|>
argument_list|>
name|searchGroups
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
parameter_list|,
name|ValueSource
name|groupByVS
parameter_list|,
name|Map
name|vsContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searchGroups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupByVS
operator|=
name|groupByVS
expr_stmt|;
name|this
operator|.
name|vsContext
operator|=
name|vsContext
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|MutableValue
argument_list|>
name|retrieveGroup
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
return|return
name|groupMap
operator|.
name|get
argument_list|(
name|mval
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|FunctionValues
name|docValues
init|=
name|groupByVS
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
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
block|}
block|}
end_class

end_unit

