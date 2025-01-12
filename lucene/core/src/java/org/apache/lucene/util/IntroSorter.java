begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * {@link Sorter} implementation based on a variant of the quicksort algorithm  * called<a href="http://en.wikipedia.org/wiki/Introsort">introsort</a>: when  * the recursion level exceeds the log of the length of the array to sort, it  * falls back to heapsort. This prevents quicksort from running into its  * worst-case quadratic runtime. Small arrays are sorted with  * insertion sort.  * @lucene.internal  */
end_comment

begin_class
DECL|class|IntroSorter
specifier|public
specifier|abstract
class|class
name|IntroSorter
extends|extends
name|Sorter
block|{
comment|/** Create a new {@link IntroSorter}. */
DECL|method|IntroSorter
specifier|public
name|IntroSorter
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|sort
specifier|public
specifier|final
name|void
name|sort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|checkRange
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|quicksort
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|2
operator|*
name|MathUtil
operator|.
name|log
argument_list|(
name|to
operator|-
name|from
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|quicksort
name|void
name|quicksort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
if|if
condition|(
name|to
operator|-
name|from
operator|<
name|BINARY_SORT_THRESHOLD
condition|)
block|{
name|binarySort
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
operator|--
name|maxDepth
operator|<
literal|0
condition|)
block|{
name|heapSort
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|mid
init|=
operator|(
name|from
operator|+
name|to
operator|)
operator|>>>
literal|1
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compare
argument_list|(
name|mid
argument_list|,
name|to
operator|-
literal|1
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|mid
argument_list|,
name|to
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|left
init|=
name|from
operator|+
literal|1
decl_stmt|;
name|int
name|right
init|=
name|to
operator|-
literal|2
decl_stmt|;
name|setPivot
argument_list|(
name|mid
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
while|while
condition|(
name|comparePivot
argument_list|(
name|right
argument_list|)
operator|<
literal|0
condition|)
block|{
operator|--
name|right
expr_stmt|;
block|}
while|while
condition|(
name|left
operator|<
name|right
operator|&&
name|comparePivot
argument_list|(
name|left
argument_list|)
operator|>=
literal|0
condition|)
block|{
operator|++
name|left
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|<
name|right
condition|)
block|{
name|swap
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
operator|--
name|right
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|quicksort
argument_list|(
name|from
argument_list|,
name|left
operator|+
literal|1
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
name|quicksort
argument_list|(
name|left
operator|+
literal|1
argument_list|,
name|to
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
block|}
comment|// Don't rely on the slow default impl of setPivot/comparePivot since
comment|// quicksort relies on these methods to be fast for good performance
annotation|@
name|Override
DECL|method|setPivot
specifier|protected
specifier|abstract
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|comparePivot
specifier|protected
specifier|abstract
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|setPivot
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|comparePivot
argument_list|(
name|j
argument_list|)
return|;
block|}
block|}
end_class

end_unit

