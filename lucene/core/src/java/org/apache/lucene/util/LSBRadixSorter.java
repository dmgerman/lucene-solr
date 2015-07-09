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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * A LSB Radix sorter for unsigned int values.  */
end_comment

begin_class
DECL|class|LSBRadixSorter
specifier|final
class|class
name|LSBRadixSorter
block|{
DECL|field|INSERTION_SORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|INSERTION_SORT_THRESHOLD
init|=
literal|30
decl_stmt|;
DECL|field|HISTOGRAM_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|HISTOGRAM_SIZE
init|=
literal|256
decl_stmt|;
DECL|field|histogram
specifier|private
specifier|final
name|int
index|[]
name|histogram
init|=
operator|new
name|int
index|[
name|HISTOGRAM_SIZE
index|]
decl_stmt|;
DECL|field|buffer
specifier|private
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|method|buildHistogram
specifier|private
specifier|static
name|void
name|buildHistogram
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|int
index|[]
name|histogram
parameter_list|,
name|int
name|shift
parameter_list|)
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
name|len
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|b
init|=
operator|(
name|array
index|[
name|off
operator|+
name|i
index|]
operator|>>>
name|shift
operator|)
operator|&
literal|0xFF
decl_stmt|;
name|histogram
index|[
name|b
index|]
operator|+=
literal|1
expr_stmt|;
block|}
block|}
DECL|method|sumHistogram
specifier|private
specifier|static
name|void
name|sumHistogram
parameter_list|(
name|int
index|[]
name|histogram
parameter_list|)
block|{
name|int
name|accum
init|=
literal|0
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
name|HISTOGRAM_SIZE
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|count
init|=
name|histogram
index|[
name|i
index|]
decl_stmt|;
name|histogram
index|[
name|i
index|]
operator|=
name|accum
expr_stmt|;
name|accum
operator|+=
name|count
expr_stmt|;
block|}
block|}
DECL|method|reorder
specifier|private
specifier|static
name|void
name|reorder
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|int
index|[]
name|histogram
parameter_list|,
name|int
name|shift
parameter_list|,
name|int
index|[]
name|dest
parameter_list|,
name|int
name|destOff
parameter_list|)
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
name|len
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|v
init|=
name|array
index|[
name|off
operator|+
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|b
init|=
operator|(
name|v
operator|>>>
name|shift
operator|)
operator|&
literal|0xFF
decl_stmt|;
name|dest
index|[
name|destOff
operator|+
name|histogram
index|[
name|b
index|]
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
block|}
DECL|method|sort
specifier|private
specifier|static
name|boolean
name|sort
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|int
index|[]
name|histogram
parameter_list|,
name|int
name|shift
parameter_list|,
name|int
index|[]
name|dest
parameter_list|,
name|int
name|destOff
parameter_list|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|histogram
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buildHistogram
argument_list|(
name|array
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|histogram
argument_list|,
name|shift
argument_list|)
expr_stmt|;
if|if
condition|(
name|histogram
index|[
literal|0
index|]
operator|==
name|len
condition|)
block|{
return|return
literal|false
return|;
block|}
name|sumHistogram
argument_list|(
name|histogram
argument_list|)
expr_stmt|;
name|reorder
argument_list|(
name|array
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|histogram
argument_list|,
name|shift
argument_list|,
name|dest
argument_list|,
name|destOff
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|insertionSort
specifier|private
specifier|static
name|void
name|insertionSort
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|off
operator|+
literal|1
init|,
name|end
init|=
name|off
operator|+
name|len
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>
name|off
condition|;
operator|--
name|j
control|)
block|{
if|if
condition|(
name|array
index|[
name|j
operator|-
literal|1
index|]
operator|>
name|array
index|[
name|j
index|]
condition|)
block|{
name|int
name|tmp
init|=
name|array
index|[
name|j
operator|-
literal|1
index|]
decl_stmt|;
name|array
index|[
name|j
operator|-
literal|1
index|]
operator|=
name|array
index|[
name|j
index|]
expr_stmt|;
name|array
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
DECL|method|sort
specifier|public
name|void
name|sort
parameter_list|(
specifier|final
name|int
index|[]
name|array
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<
name|INSERTION_SORT_THRESHOLD
condition|)
block|{
name|insertionSort
argument_list|(
name|array
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return;
block|}
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|int
index|[]
name|arr
init|=
name|array
decl_stmt|;
name|int
name|arrOff
init|=
name|off
decl_stmt|;
name|int
index|[]
name|buf
init|=
name|buffer
decl_stmt|;
name|int
name|bufOff
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
name|shift
operator|<=
literal|24
condition|;
name|shift
operator|+=
literal|8
control|)
block|{
if|if
condition|(
name|sort
argument_list|(
name|arr
argument_list|,
name|arrOff
argument_list|,
name|len
argument_list|,
name|histogram
argument_list|,
name|shift
argument_list|,
name|buf
argument_list|,
name|bufOff
argument_list|)
condition|)
block|{
comment|// swap arrays
name|int
index|[]
name|tmp
init|=
name|arr
decl_stmt|;
name|int
name|tmpOff
init|=
name|arrOff
decl_stmt|;
name|arr
operator|=
name|buf
expr_stmt|;
name|arrOff
operator|=
name|bufOff
expr_stmt|;
name|buf
operator|=
name|tmp
expr_stmt|;
name|bufOff
operator|=
name|tmpOff
expr_stmt|;
block|}
block|}
if|if
condition|(
name|array
operator|==
name|buf
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|arrOff
argument_list|,
name|array
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

