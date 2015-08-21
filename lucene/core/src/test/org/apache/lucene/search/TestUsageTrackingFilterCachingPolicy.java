begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|MultiReader
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
name|SlowCompositeReaderWrapper
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
name|Term
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestUsageTrackingFilterCachingPolicy
specifier|public
class|class
name|TestUsageTrackingFilterCachingPolicy
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCostlyFilter
specifier|public
name|void
name|testCostlyFilter
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|UsageTrackingQueryCachingPolicy
operator|.
name|isCostly
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"prefix"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UsageTrackingQueryCachingPolicy
operator|.
name|isCostly
argument_list|(
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
literal|"intField"
argument_list|,
literal|8
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UsageTrackingQueryCachingPolicy
operator|.
name|isCostly
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostIgnored
specifier|public
name|void
name|testBoostIgnored
parameter_list|()
block|{
name|Query
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|q1
operator|.
name|setBoost
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
name|q1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|q2
operator|.
name|setBoost
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Query
name|q3
init|=
name|q1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|q3
operator|.
name|setBoost
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|UsageTrackingQueryCachingPolicy
name|policy
init|=
operator|new
name|UsageTrackingQueryCachingPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|onUse
argument_list|(
name|q1
argument_list|)
expr_stmt|;
name|policy
operator|.
name|onUse
argument_list|(
name|q2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|policy
operator|.
name|frequency
argument_list|(
name|q3
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNeverCacheMatchAll
specifier|public
name|void
name|testNeverCacheMatchAll
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|UsageTrackingQueryCachingPolicy
name|policy
init|=
operator|new
name|UsageTrackingQueryCachingPolicy
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|policy
operator|.
name|onUse
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|policy
operator|.
name|shouldCache
argument_list|(
name|q
argument_list|,
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
operator|.
name|getContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

