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
comment|/** {@link Sorter} implementation based on the merge-sort algorithm that merges  *  in place (no extra memory will be allocated). Small arrays are sorted with  *  insertion sort.  *  @lucene.internal */
end_comment

begin_class
DECL|class|InPlaceMergeSorter
specifier|public
specifier|abstract
class|class
name|InPlaceMergeSorter
extends|extends
name|Sorter
block|{
comment|/** Create a new {@link InPlaceMergeSorter} */
DECL|method|InPlaceMergeSorter
specifier|public
name|InPlaceMergeSorter
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
name|mergeSort
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
DECL|method|mergeSort
name|void
name|mergeSort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|-
name|from
operator|<
name|THRESHOLD
condition|)
block|{
name|insertionSort
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|mergeSort
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
expr_stmt|;
name|mergeSort
argument_list|(
name|mid
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|mergeInPlace
argument_list|(
name|from
argument_list|,
name|mid
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

