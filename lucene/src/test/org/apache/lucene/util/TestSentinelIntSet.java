begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|TestSentinelIntSet
specifier|public
class|class
name|TestSentinelIntSet
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|SentinelIntSet
name|set
init|=
operator|new
name|SentinelIntSet
argument_list|(
literal|10
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|set
operator|.
name|exists
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|exists
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|11
argument_list|,
name|set
operator|.
name|find
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|set
operator|.
name|hash
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
comment|//force a rehash
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|put
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|set
operator|.
name|rehashCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRehash
specifier|public
name|void
name|testRehash
parameter_list|()
throws|throws
name|Exception
block|{
name|SentinelIntSet
name|set
init|=
operator|new
name|SentinelIntSet
argument_list|(
literal|3
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|set
operator|.
name|put
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|set
operator|.
name|find
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|set
operator|.
name|find
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|set
operator|.
name|find
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|set
operator|.
name|put
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|set
operator|.
name|find
argument_list|(
literal|99
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|initSz
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
decl_stmt|;
name|int
name|maxVal
init|=
operator|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
else|:
name|random
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|)
operator|+
literal|1
decl_stmt|;
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|a
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|initSz
argument_list|)
decl_stmt|;
name|SentinelIntSet
name|b
init|=
operator|new
name|SentinelIntSet
argument_list|(
name|initSz
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|int
name|val
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxVal
argument_list|)
decl_stmt|;
name|boolean
name|exists
init|=
operator|!
name|a
operator|.
name|add
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|boolean
name|existsB
init|=
name|b
operator|.
name|exists
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|exists
argument_list|,
name|existsB
argument_list|)
expr_stmt|;
name|int
name|slot
init|=
name|b
operator|.
name|find
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|exists
argument_list|,
name|slot
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|b
operator|.
name|put
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

