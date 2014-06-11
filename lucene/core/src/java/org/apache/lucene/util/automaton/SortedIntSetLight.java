begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ArrayUtil
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|// Just holds a set of int[] states, plus a corresponding
end_comment

begin_comment
comment|// int[] count per state.  Used by
end_comment

begin_comment
comment|// BasicOperations.determinize
end_comment

begin_class
DECL|class|SortedIntSetLight
specifier|final
class|class
name|SortedIntSetLight
block|{
DECL|field|values
name|int
index|[]
name|values
decl_stmt|;
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|hashCode
specifier|private
name|int
name|hashCode
decl_stmt|;
comment|// If we hold more than this many states, we switch from
comment|// O(N^2) linear ops to O(N log(N)) TreeMap
DECL|field|TREE_MAP_CUTOVER
specifier|private
specifier|final
specifier|static
name|int
name|TREE_MAP_CUTOVER
init|=
literal|30
decl_stmt|;
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|useTreeMap
specifier|private
name|boolean
name|useTreeMap
decl_stmt|;
DECL|field|state
name|int
name|state
decl_stmt|;
DECL|method|SortedIntSetLight
specifier|public
name|SortedIntSetLight
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|values
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|// Adds this state to the set
DECL|method|incr
specifier|public
name|void
name|incr
parameter_list|(
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|useTreeMap
condition|)
block|{
specifier|final
name|Integer
name|key
init|=
name|num
decl_stmt|;
name|Integer
name|val
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|1
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|upto
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|values
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|values
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
name|counts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|counts
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|==
name|num
condition|)
block|{
name|counts
index|[
name|i
index|]
operator|++
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|num
operator|<
name|values
index|[
name|i
index|]
condition|)
block|{
comment|// insert here
name|int
name|j
init|=
name|upto
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|>=
name|i
condition|)
block|{
name|values
index|[
literal|1
operator|+
name|j
index|]
operator|=
name|values
index|[
name|j
index|]
expr_stmt|;
name|counts
index|[
literal|1
operator|+
name|j
index|]
operator|=
name|counts
index|[
name|j
index|]
expr_stmt|;
name|j
operator|--
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|num
expr_stmt|;
name|counts
index|[
name|i
index|]
operator|=
literal|1
expr_stmt|;
name|upto
operator|++
expr_stmt|;
return|return;
block|}
block|}
comment|// append
name|values
index|[
name|upto
index|]
operator|=
name|num
expr_stmt|;
name|counts
index|[
name|upto
index|]
operator|=
literal|1
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|TREE_MAP_CUTOVER
condition|)
block|{
name|useTreeMap
operator|=
literal|true
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
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|counts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Removes this state from the set, if count decrs to 0
DECL|method|decr
specifier|public
name|void
name|decr
parameter_list|(
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|useTreeMap
condition|)
block|{
specifier|final
name|int
name|count
init|=
name|map
operator|.
name|get
argument_list|(
name|num
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|num
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Fall back to simple arrays once we touch zero again
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|useTreeMap
operator|=
literal|false
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
block|}
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|==
name|num
condition|)
block|{
name|counts
index|[
name|i
index|]
operator|--
expr_stmt|;
if|if
condition|(
name|counts
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|limit
init|=
name|upto
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|limit
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|counts
index|[
name|i
index|]
operator|=
name|counts
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|upto
operator|=
name|limit
expr_stmt|;
block|}
return|return;
block|}
block|}
assert|assert
literal|false
assert|;
block|}
DECL|method|computeHash
specifier|public
name|void
name|computeHash
parameter_list|()
block|{
if|if
condition|(
name|useTreeMap
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>
name|values
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|size
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
decl_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
name|hashCode
operator|=
name|map
operator|.
name|size
argument_list|()
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|state
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|hashCode
operator|=
literal|683
operator|*
name|hashCode
operator|+
name|state
expr_stmt|;
name|values
index|[
name|upto
operator|++
index|]
operator|=
name|state
expr_stmt|;
block|}
block|}
else|else
block|{
name|hashCode
operator|=
name|upto
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
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|hashCode
operator|=
literal|683
operator|*
name|hashCode
operator|+
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
DECL|method|freeze
specifier|public
name|FrozenIntSetLight
name|freeze
parameter_list|(
name|int
name|state
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|c
init|=
operator|new
name|int
index|[
name|upto
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|c
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
return|return
operator|new
name|FrozenIntSetLight
argument_list|(
name|c
argument_list|,
name|hashCode
argument_list|,
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|_other
operator|instanceof
name|FrozenIntSetLight
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FrozenIntSetLight
name|other
init|=
operator|(
name|FrozenIntSetLight
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|hashCode
operator|!=
name|other
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|values
operator|.
name|length
operator|!=
name|upto
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|other
operator|.
name|values
index|[
name|i
index|]
operator|!=
name|values
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|'['
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
name|upto
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|counts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|FrozenIntSetLight
specifier|public
specifier|final
specifier|static
class|class
name|FrozenIntSetLight
block|{
DECL|field|values
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|field|hashCode
specifier|final
name|int
name|hashCode
decl_stmt|;
DECL|field|state
specifier|final
name|int
name|state
decl_stmt|;
DECL|method|FrozenIntSetLight
specifier|public
name|FrozenIntSetLight
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|hashCode
parameter_list|,
name|int
name|state
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|hashCode
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|FrozenIntSetLight
specifier|public
name|FrozenIntSetLight
parameter_list|(
name|int
name|num
parameter_list|,
name|int
name|state
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
operator|new
name|int
index|[]
block|{
name|num
block|}
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
literal|683
operator|+
name|num
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|_other
operator|instanceof
name|FrozenIntSetLight
condition|)
block|{
name|FrozenIntSetLight
name|other
init|=
operator|(
name|FrozenIntSetLight
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|hashCode
operator|!=
name|other
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|values
operator|.
name|length
operator|!=
name|values
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|other
operator|.
name|values
index|[
name|i
index|]
operator|!=
name|values
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|_other
operator|instanceof
name|SortedIntSetLight
condition|)
block|{
name|SortedIntSetLight
name|other
init|=
operator|(
name|SortedIntSetLight
operator|)
name|_other
decl_stmt|;
if|if
condition|(
name|hashCode
operator|!=
name|other
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|values
operator|.
name|length
operator|!=
name|values
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|other
operator|.
name|values
index|[
name|i
index|]
operator|!=
name|values
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|'['
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

