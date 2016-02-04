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
comment|/**  * Estimates how {@link RamUsageEstimator} estimates physical memory consumption  * of Java objects.   */
end_comment

begin_class
DECL|class|StressRamUsageEstimator
specifier|public
class|class
name|StressRamUsageEstimator
extends|extends
name|LuceneTestCase
block|{
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|o
name|Object
name|o
decl_stmt|;
DECL|field|next
name|Entry
name|next
decl_stmt|;
DECL|method|createNext
specifier|public
name|Entry
name|createNext
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Entry
name|e
init|=
operator|new
name|Entry
argument_list|()
decl_stmt|;
name|e
operator|.
name|o
operator|=
name|o
expr_stmt|;
name|e
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|e
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
DECL|field|guard
specifier|volatile
name|Object
name|guard
decl_stmt|;
comment|// This shows an easy stack overflow because we're counting recursively.
DECL|method|testLargeSetOfByteArrays
specifier|public
name|void
name|testLargeSetOfByteArrays
parameter_list|()
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|long
name|before
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
name|Object
index|[]
name|all
init|=
operator|new
name|Object
index|[
literal|1000000
index|]
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
name|all
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|all
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
index|]
expr_stmt|;
block|}
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|long
name|after
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"mx:  "
operator|+
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|after
operator|-
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"rue: "
operator|+
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|shallowSizeOf
argument_list|(
name|all
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|guard
operator|=
name|all
expr_stmt|;
block|}
DECL|method|shallowSizeOf
specifier|private
name|long
name|shallowSizeOf
parameter_list|(
name|Object
index|[]
name|all
parameter_list|)
block|{
name|long
name|s
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|all
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|all
control|)
block|{
name|s
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|shallowSizeOf
specifier|private
name|long
name|shallowSizeOf
parameter_list|(
name|Object
index|[]
index|[]
name|all
parameter_list|)
block|{
name|long
name|s
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|all
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
index|[]
name|o
range|:
name|all
control|)
block|{
name|s
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|o
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o2
range|:
name|o
control|)
block|{
name|s
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|o2
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|s
return|;
block|}
DECL|method|testSimpleByteArrays
specifier|public
name|void
name|testSimpleByteArrays
parameter_list|()
block|{
name|Object
index|[]
index|[]
name|all
init|=
operator|new
name|Object
index|[
literal|0
index|]
index|[]
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|// Check the current memory consumption and provide the estimate.
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|long
name|estimated
init|=
name|shallowSizeOf
argument_list|(
name|all
argument_list|)
decl_stmt|;
if|if
condition|(
name|estimated
operator|>
literal|50
operator|*
name|RamUsageEstimator
operator|.
name|ONE_MB
condition|)
block|{
break|break;
block|}
comment|// Make another batch of objects.
name|Object
index|[]
name|seg
init|=
operator|new
name|Object
index|[
literal|10000
index|]
decl_stmt|;
name|all
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|all
argument_list|,
name|all
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|all
index|[
name|all
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|seg
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
name|seg
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|seg
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
index|]
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
comment|// Release and quit.
block|}
block|}
block|}
end_class

end_unit

