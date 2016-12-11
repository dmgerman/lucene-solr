begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/** A {@link Collector} which computes statistics for a DocValues field. */
end_comment

begin_class
DECL|class|DocValuesStatsCollector
specifier|public
class|class
name|DocValuesStatsCollector
implements|implements
name|Collector
block|{
DECL|field|stats
specifier|private
specifier|final
name|DocValuesStats
argument_list|<
name|?
argument_list|>
name|stats
decl_stmt|;
comment|/** Creates a collector to compute statistics for a DocValues field using the given {@code stats}. */
DECL|method|DocValuesStatsCollector
specifier|public
name|DocValuesStatsCollector
parameter_list|(
name|DocValuesStats
argument_list|<
name|?
argument_list|>
name|stats
parameter_list|)
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|shouldProcess
init|=
name|stats
operator|.
name|init
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|shouldProcess
condition|)
block|{
comment|// Stats cannot be computed for this segment, therefore consider all matching documents as a 'miss'.
return|return
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
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
comment|// All matching documents in this reader are missing a value
name|stats
operator|.
name|addMissing
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
return|return
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
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
name|stats
operator|.
name|accumulate
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
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
block|}
block|}
end_class

end_unit

