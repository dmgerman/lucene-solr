begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link CountingAggregator} used during complement counting.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ComplementCountingAggregator
specifier|public
class|class
name|ComplementCountingAggregator
extends|extends
name|CountingAggregator
block|{
DECL|method|ComplementCountingAggregator
specifier|public
name|ComplementCountingAggregator
parameter_list|(
name|int
index|[]
name|counterArray
parameter_list|)
block|{
name|super
argument_list|(
name|counterArray
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
assert|assert
name|counterArray
index|[
name|ordinal
index|]
operator|!=
literal|0
operator|:
literal|"complement aggregation: count is about to become negative for ordinal "
operator|+
name|ordinal
assert|;
operator|--
name|counterArray
index|[
name|ordinal
index|]
expr_stmt|;
block|}
block|}
end_class

end_unit

