begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
comment|/**  * A native int set where one value is reserved to mean "EMPTY"  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|SentinelIntSet
specifier|public
class|class
name|SentinelIntSet
block|{
DECL|field|keys
specifier|public
name|int
index|[]
name|keys
decl_stmt|;
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|field|emptyVal
specifier|public
specifier|final
name|int
name|emptyVal
decl_stmt|;
DECL|field|rehashCount
specifier|public
name|int
name|rehashCount
decl_stmt|;
comment|// the count at which a rehash should be done
DECL|method|SentinelIntSet
specifier|public
name|SentinelIntSet
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|emptyVal
parameter_list|)
block|{
name|this
operator|.
name|emptyVal
operator|=
name|emptyVal
expr_stmt|;
name|int
name|tsize
init|=
name|Math
operator|.
name|max
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitUtil
operator|.
name|nextHighestPowerOfTwo
argument_list|(
name|size
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|rehashCount
operator|=
name|tsize
operator|-
operator|(
name|tsize
operator|>>
literal|2
operator|)
expr_stmt|;
if|if
condition|(
name|size
operator|>=
name|rehashCount
condition|)
block|{
comment|// should be able to hold "size" w/o rehashing
name|tsize
operator|<<=
literal|1
expr_stmt|;
name|rehashCount
operator|=
name|tsize
operator|-
operator|(
name|tsize
operator|>>
literal|2
operator|)
expr_stmt|;
block|}
name|keys
operator|=
operator|new
name|int
index|[
name|tsize
index|]
expr_stmt|;
if|if
condition|(
name|emptyVal
operator|!=
literal|0
condition|)
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|keys
argument_list|,
name|emptyVal
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|hash
specifier|public
name|int
name|hash
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|key
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/** returns the slot for this key */
DECL|method|getSlot
specifier|public
name|int
name|getSlot
parameter_list|(
name|int
name|key
parameter_list|)
block|{
assert|assert
name|key
operator|!=
name|emptyVal
assert|;
name|int
name|h
init|=
name|hash
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|s
init|=
name|h
operator|&
operator|(
name|keys
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|keys
index|[
name|s
index|]
operator|==
name|key
operator|||
name|keys
index|[
name|s
index|]
operator|==
name|emptyVal
condition|)
return|return
name|s
return|;
name|int
name|increment
init|=
operator|(
name|h
operator|>>
literal|7
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|s
operator|=
operator|(
name|s
operator|+
name|increment
operator|)
operator|&
operator|(
name|keys
operator|.
name|length
operator|-
literal|1
operator|)
expr_stmt|;
block|}
do|while
condition|(
name|keys
index|[
name|s
index|]
operator|!=
name|key
operator|&&
name|keys
index|[
name|s
index|]
operator|!=
name|emptyVal
condition|)
do|;
return|return
name|s
return|;
block|}
comment|/** returns the slot for this key, or -slot-1 if not found */
DECL|method|find
specifier|public
name|int
name|find
parameter_list|(
name|int
name|key
parameter_list|)
block|{
assert|assert
name|key
operator|!=
name|emptyVal
assert|;
name|int
name|h
init|=
name|hash
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|s
init|=
name|h
operator|&
operator|(
name|keys
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|keys
index|[
name|s
index|]
operator|==
name|key
condition|)
return|return
name|s
return|;
if|if
condition|(
name|keys
index|[
name|s
index|]
operator|==
name|emptyVal
condition|)
return|return
operator|-
name|s
operator|-
literal|1
return|;
name|int
name|increment
init|=
operator|(
name|h
operator|>>
literal|7
operator|)
operator||
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|s
operator|=
operator|(
name|s
operator|+
name|increment
operator|)
operator|&
operator|(
name|keys
operator|.
name|length
operator|-
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|keys
index|[
name|s
index|]
operator|==
name|key
condition|)
return|return
name|s
return|;
if|if
condition|(
name|keys
index|[
name|s
index|]
operator|==
name|emptyVal
condition|)
return|return
operator|-
name|s
operator|-
literal|1
return|;
block|}
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|key
argument_list|)
operator|>=
literal|0
return|;
block|}
DECL|method|put
specifier|public
name|int
name|put
parameter_list|(
name|int
name|key
parameter_list|)
block|{
name|int
name|s
init|=
name|find
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|count
operator|>=
name|rehashCount
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
name|s
operator|=
name|getSlot
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
operator|-
name|s
operator|-
literal|1
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
name|keys
index|[
name|s
index|]
operator|=
name|key
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|rehash
specifier|public
name|void
name|rehash
parameter_list|()
block|{
name|int
name|newSize
init|=
name|keys
operator|.
name|length
operator|<<
literal|1
decl_stmt|;
name|int
index|[]
name|oldKeys
init|=
name|keys
decl_stmt|;
name|keys
operator|=
operator|new
name|int
index|[
name|newSize
index|]
expr_stmt|;
if|if
condition|(
name|emptyVal
operator|!=
literal|0
condition|)
name|Arrays
operator|.
name|fill
argument_list|(
name|keys
argument_list|,
name|emptyVal
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
name|oldKeys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|key
init|=
name|oldKeys
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|key
operator|==
name|emptyVal
condition|)
continue|continue;
name|int
name|newSlot
init|=
name|getSlot
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|keys
index|[
name|newSlot
index|]
operator|=
name|key
expr_stmt|;
block|}
name|rehashCount
operator|=
name|newSize
operator|-
operator|(
name|newSize
operator|>>
literal|2
operator|)
expr_stmt|;
block|}
block|}
end_class

end_unit

