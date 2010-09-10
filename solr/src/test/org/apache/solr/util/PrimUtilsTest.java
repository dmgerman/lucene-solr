begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|PrimUtilsTest
specifier|public
class|class
name|PrimUtilsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|int
name|maxSize
init|=
literal|100
decl_stmt|;
name|int
name|maxVal
init|=
literal|100
decl_stmt|;
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|maxSize
index|]
decl_stmt|;
name|int
index|[]
name|b
init|=
operator|new
name|int
index|[
name|maxSize
index|]
decl_stmt|;
name|PrimUtils
operator|.
name|IntComparator
name|comparator
init|=
operator|new
name|PrimUtils
operator|.
name|IntComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
name|b
operator|-
name|a
return|;
comment|// sort in reverse
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|start
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|start
operator|==
name|maxSize
condition|?
name|maxSize
else|:
name|start
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|-
name|start
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|b
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxVal
argument_list|)
expr_stmt|;
block|}
name|PrimUtils
operator|.
name|sort
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|a
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|b
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|a
index|[
name|i
index|]
argument_list|,
name|b
index|[
name|end
operator|-
operator|(
name|i
operator|-
name|start
operator|+
literal|1
operator|)
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLongPriorityQueue
specifier|public
name|void
name|testLongPriorityQueue
parameter_list|()
block|{
name|int
name|maxSize
init|=
literal|100
decl_stmt|;
name|long
index|[]
name|a
init|=
operator|new
name|long
index|[
name|maxSize
index|]
decl_stmt|;
name|long
index|[]
name|discards
init|=
operator|new
name|long
index|[
name|maxSize
index|]
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|discardCount
init|=
literal|0
decl_stmt|;
name|int
name|startSize
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|endSize
init|=
name|startSize
operator|==
name|maxSize
condition|?
name|maxSize
else|:
name|startSize
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|-
name|startSize
argument_list|)
decl_stmt|;
name|int
name|adds
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// System.out.println("startSize=" + startSize + " endSize=" + endSize + " adds="+adds);
name|LongPriorityQueue
name|pq
init|=
operator|new
name|LongPriorityQueue
argument_list|(
name|startSize
argument_list|,
name|endSize
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
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
name|adds
condition|;
name|i
operator|++
control|)
block|{
name|long
name|v
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|a
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|long
name|out
init|=
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|endSize
condition|)
block|{
name|assertEquals
argument_list|(
name|out
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|discards
index|[
name|discardCount
operator|++
index|]
operator|=
name|out
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|adds
argument_list|,
name|endSize
argument_list|)
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|adds
argument_list|,
name|pq
operator|.
name|size
argument_list|()
operator|+
name|discardCount
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|adds
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|discards
argument_list|,
literal|0
argument_list|,
name|discardCount
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|discardCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|a
index|[
name|i
index|]
argument_list|,
name|discards
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|discardCount
init|;
name|i
operator|<
name|adds
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|a
index|[
name|i
index|]
argument_list|,
name|pq
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

