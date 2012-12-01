begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|SolrTestCaseJ4
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
name|common
operator|.
name|cloud
operator|.
name|DocRouter
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
name|common
operator|.
name|cloud
operator|.
name|DocRouter
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
name|common
operator|.
name|cloud
operator|.
name|DocRouter
operator|.
name|Range
import|;
end_import

begin_class
DECL|class|TestHashPartitioner
specifier|public
class|class
name|TestHashPartitioner
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testMapHashes
specifier|public
name|void
name|testMapHashes
parameter_list|()
throws|throws
name|Exception
block|{
name|DocRouter
name|hp
init|=
operator|new
name|DocRouter
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
decl_stmt|;
comment|// make sure the partitioner uses the "natural" boundaries and doesn't suffer from an off-by-one
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
name|hp
operator|.
name|fullRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x80000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xffffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x00000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x7fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|0x7fffffff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x00000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x3fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x40000000
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x7fffffff
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|max
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|30000
condition|;
name|i
operator|+=
literal|13
control|)
block|{
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
name|i
argument_list|,
name|hp
operator|.
name|fullRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First range does not start before "
operator|+
name|Integer
operator|.
name|MIN_VALUE
operator|+
literal|" it is:"
operator|+
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|min
operator|<=
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Last range does not end after "
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|" it is:"
operator|+
name|ranges
operator|.
name|get
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|max
argument_list|,
name|ranges
operator|.
name|get
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|max
operator|>=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|String
name|s
init|=
name|range
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Range
name|newRange
init|=
name|hp
operator|.
name|fromString
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|range
argument_list|,
name|newRange
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

