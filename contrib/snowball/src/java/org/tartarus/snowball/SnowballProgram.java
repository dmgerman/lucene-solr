begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.tartarus.snowball
package|package
name|org
operator|.
name|tartarus
operator|.
name|snowball
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_comment
comment|/**  * This is the rev 500 of the Snowball SVN trunk,  * but modified:  * made abstract and introduced abstract method stem  * to avoid expensive   */
end_comment

begin_class
DECL|class|SnowballProgram
specifier|public
specifier|abstract
class|class
name|SnowballProgram
block|{
DECL|method|SnowballProgram
specifier|protected
name|SnowballProgram
parameter_list|()
block|{
name|current
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|setCurrent
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|stem
specifier|public
specifier|abstract
name|boolean
name|stem
parameter_list|()
function_decl|;
comment|/**      * Set the current string.      */
DECL|method|setCurrent
specifier|public
name|void
name|setCurrent
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|current
operator|.
name|replace
argument_list|(
literal|0
argument_list|,
name|current
operator|.
name|length
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|cursor
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
name|current
operator|.
name|length
argument_list|()
expr_stmt|;
name|limit_backward
operator|=
literal|0
expr_stmt|;
name|bra
operator|=
name|cursor
expr_stmt|;
name|ket
operator|=
name|limit
expr_stmt|;
block|}
comment|/**      * Get the current string.      */
DECL|method|getCurrent
specifier|public
name|String
name|getCurrent
parameter_list|()
block|{
name|String
name|result
init|=
name|current
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Make a new StringBuffer.  If we reuse the old one, and a user of
comment|// the library keeps a reference to the buffer returned (for example,
comment|// by converting it to a String in a way which doesn't force a copy),
comment|// the buffer size will not decrease, and we will risk wasting a large
comment|// amount of memory.
comment|// Thanks to Wolfram Esser for spotting this problem.
name|current
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// current string
DECL|field|current
specifier|protected
name|StringBuffer
name|current
decl_stmt|;
DECL|field|cursor
specifier|protected
name|int
name|cursor
decl_stmt|;
DECL|field|limit
specifier|protected
name|int
name|limit
decl_stmt|;
DECL|field|limit_backward
specifier|protected
name|int
name|limit_backward
decl_stmt|;
DECL|field|bra
specifier|protected
name|int
name|bra
decl_stmt|;
DECL|field|ket
specifier|protected
name|int
name|ket
decl_stmt|;
DECL|method|copy_from
specifier|protected
name|void
name|copy_from
parameter_list|(
name|SnowballProgram
name|other
parameter_list|)
block|{
name|current
operator|=
name|other
operator|.
name|current
expr_stmt|;
name|cursor
operator|=
name|other
operator|.
name|cursor
expr_stmt|;
name|limit
operator|=
name|other
operator|.
name|limit
expr_stmt|;
name|limit_backward
operator|=
name|other
operator|.
name|limit_backward
expr_stmt|;
name|bra
operator|=
name|other
operator|.
name|bra
expr_stmt|;
name|ket
operator|=
name|other
operator|.
name|ket
expr_stmt|;
block|}
DECL|method|in_grouping
specifier|protected
name|boolean
name|in_grouping
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
return|return
literal|false
return|;
name|ch
operator|-=
name|min
expr_stmt|;
if|if
condition|(
operator|(
name|s
index|[
name|ch
operator|>>
literal|3
index|]
operator|&
operator|(
literal|0X1
operator|<<
operator|(
name|ch
operator|&
literal|0X7
operator|)
operator|)
operator|)
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|cursor
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|in_grouping_b
specifier|protected
name|boolean
name|in_grouping_b
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
return|return
literal|false
return|;
name|ch
operator|-=
name|min
expr_stmt|;
if|if
condition|(
operator|(
name|s
index|[
name|ch
operator|>>
literal|3
index|]
operator|&
operator|(
literal|0X1
operator|<<
operator|(
name|ch
operator|&
literal|0X7
operator|)
operator|)
operator|)
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|cursor
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|out_grouping
specifier|protected
name|boolean
name|out_grouping
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
block|{
name|cursor
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|ch
operator|-=
name|min
expr_stmt|;
if|if
condition|(
operator|(
name|s
index|[
name|ch
operator|>>
literal|3
index|]
operator|&
operator|(
literal|0X1
operator|<<
operator|(
name|ch
operator|&
literal|0X7
operator|)
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
name|cursor
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|out_grouping_b
specifier|protected
name|boolean
name|out_grouping_b
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
block|{
name|cursor
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
name|ch
operator|-=
name|min
expr_stmt|;
if|if
condition|(
operator|(
name|s
index|[
name|ch
operator|>>
literal|3
index|]
operator|&
operator|(
literal|0X1
operator|<<
operator|(
name|ch
operator|&
literal|0X7
operator|)
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
name|cursor
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|in_range
specifier|protected
name|boolean
name|in_range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
return|return
literal|false
return|;
name|cursor
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|in_range_b
specifier|protected
name|boolean
name|in_range_b
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
condition|)
return|return
literal|false
return|;
name|cursor
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|out_range
specifier|protected
name|boolean
name|out_range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
operator|)
condition|)
return|return
literal|false
return|;
name|cursor
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|out_range_b
specifier|protected
name|boolean
name|out_range_b
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
condition|)
return|return
literal|false
return|;
name|char
name|ch
init|=
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ch
operator|>
name|max
operator|||
name|ch
operator|<
name|min
operator|)
condition|)
return|return
literal|false
return|;
name|cursor
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|eq_s
specifier|protected
name|boolean
name|eq_s
parameter_list|(
name|int
name|s_size
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|-
name|cursor
operator|<
name|s_size
condition|)
return|return
literal|false
return|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|!=
name|s_size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|+
name|i
argument_list|)
operator|!=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
name|cursor
operator|+=
name|s_size
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|eq_s_b
specifier|protected
name|boolean
name|eq_s_b
parameter_list|(
name|int
name|s_size
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|cursor
operator|-
name|limit_backward
operator|<
name|s_size
condition|)
return|return
literal|false
return|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|!=
name|s_size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|current
operator|.
name|charAt
argument_list|(
name|cursor
operator|-
name|s_size
operator|+
name|i
argument_list|)
operator|!=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
name|cursor
operator|-=
name|s_size
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|eq_v
specifier|protected
name|boolean
name|eq_v
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
return|return
name|eq_s
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|s
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|eq_v_b
specifier|protected
name|boolean
name|eq_v_b
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
return|return
name|eq_s_b
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|s
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|find_among
specifier|protected
name|int
name|find_among
parameter_list|(
name|Among
name|v
index|[]
parameter_list|,
name|int
name|v_size
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
init|=
name|v_size
decl_stmt|;
name|int
name|c
init|=
name|cursor
decl_stmt|;
name|int
name|l
init|=
name|limit
decl_stmt|;
name|int
name|common_i
init|=
literal|0
decl_stmt|;
name|int
name|common_j
init|=
literal|0
decl_stmt|;
name|boolean
name|first_key_inspected
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|k
init|=
name|i
operator|+
operator|(
operator|(
name|j
operator|-
name|i
operator|)
operator|>>
literal|1
operator|)
decl_stmt|;
name|int
name|diff
init|=
literal|0
decl_stmt|;
name|int
name|common
init|=
name|common_i
operator|<
name|common_j
condition|?
name|common_i
else|:
name|common_j
decl_stmt|;
comment|// smaller
name|Among
name|w
init|=
name|v
index|[
name|k
index|]
decl_stmt|;
name|int
name|i2
decl_stmt|;
for|for
control|(
name|i2
operator|=
name|common
init|;
name|i2
operator|<
name|w
operator|.
name|s_size
condition|;
name|i2
operator|++
control|)
block|{
if|if
condition|(
name|c
operator|+
name|common
operator|==
name|l
condition|)
block|{
name|diff
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
name|diff
operator|=
name|current
operator|.
name|charAt
argument_list|(
name|c
operator|+
name|common
argument_list|)
operator|-
name|w
operator|.
name|s
operator|.
name|charAt
argument_list|(
name|i2
argument_list|)
expr_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
break|break;
name|common
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
name|common_j
operator|=
name|common
expr_stmt|;
block|}
else|else
block|{
name|i
operator|=
name|k
expr_stmt|;
name|common_i
operator|=
name|common
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|-
name|i
operator|<=
literal|1
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
break|break;
comment|// v->s has been inspected
if|if
condition|(
name|j
operator|==
name|i
condition|)
break|break;
comment|// only one item in v
comment|// - but now we need to go round once more to get
comment|// v->s inspected. This looks messy, but is actually
comment|// the optimal approach.
if|if
condition|(
name|first_key_inspected
condition|)
break|break;
name|first_key_inspected
operator|=
literal|true
expr_stmt|;
block|}
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|Among
name|w
init|=
name|v
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|common_i
operator|>=
name|w
operator|.
name|s_size
condition|)
block|{
name|cursor
operator|=
name|c
operator|+
name|w
operator|.
name|s_size
expr_stmt|;
if|if
condition|(
name|w
operator|.
name|method
operator|==
literal|null
condition|)
return|return
name|w
operator|.
name|result
return|;
name|boolean
name|res
decl_stmt|;
try|try
block|{
name|Object
name|resobj
init|=
name|w
operator|.
name|method
operator|.
name|invoke
argument_list|(
name|w
operator|.
name|methodobject
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|res
operator|=
name|resobj
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
comment|// FIXME - debug message
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
comment|// FIXME - debug message
block|}
name|cursor
operator|=
name|c
operator|+
name|w
operator|.
name|s_size
expr_stmt|;
if|if
condition|(
name|res
condition|)
return|return
name|w
operator|.
name|result
return|;
block|}
name|i
operator|=
name|w
operator|.
name|substring_i
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
return|return
literal|0
return|;
block|}
block|}
comment|// find_among_b is for backwards processing. Same comments apply
DECL|method|find_among_b
specifier|protected
name|int
name|find_among_b
parameter_list|(
name|Among
name|v
index|[]
parameter_list|,
name|int
name|v_size
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
init|=
name|v_size
decl_stmt|;
name|int
name|c
init|=
name|cursor
decl_stmt|;
name|int
name|lb
init|=
name|limit_backward
decl_stmt|;
name|int
name|common_i
init|=
literal|0
decl_stmt|;
name|int
name|common_j
init|=
literal|0
decl_stmt|;
name|boolean
name|first_key_inspected
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|k
init|=
name|i
operator|+
operator|(
operator|(
name|j
operator|-
name|i
operator|)
operator|>>
literal|1
operator|)
decl_stmt|;
name|int
name|diff
init|=
literal|0
decl_stmt|;
name|int
name|common
init|=
name|common_i
operator|<
name|common_j
condition|?
name|common_i
else|:
name|common_j
decl_stmt|;
name|Among
name|w
init|=
name|v
index|[
name|k
index|]
decl_stmt|;
name|int
name|i2
decl_stmt|;
for|for
control|(
name|i2
operator|=
name|w
operator|.
name|s_size
operator|-
literal|1
operator|-
name|common
init|;
name|i2
operator|>=
literal|0
condition|;
name|i2
operator|--
control|)
block|{
if|if
condition|(
name|c
operator|-
name|common
operator|==
name|lb
condition|)
block|{
name|diff
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
name|diff
operator|=
name|current
operator|.
name|charAt
argument_list|(
name|c
operator|-
literal|1
operator|-
name|common
argument_list|)
operator|-
name|w
operator|.
name|s
operator|.
name|charAt
argument_list|(
name|i2
argument_list|)
expr_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
break|break;
name|common
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
name|common_j
operator|=
name|common
expr_stmt|;
block|}
else|else
block|{
name|i
operator|=
name|k
expr_stmt|;
name|common_i
operator|=
name|common
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|-
name|i
operator|<=
literal|1
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
break|break;
if|if
condition|(
name|j
operator|==
name|i
condition|)
break|break;
if|if
condition|(
name|first_key_inspected
condition|)
break|break;
name|first_key_inspected
operator|=
literal|true
expr_stmt|;
block|}
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|Among
name|w
init|=
name|v
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|common_i
operator|>=
name|w
operator|.
name|s_size
condition|)
block|{
name|cursor
operator|=
name|c
operator|-
name|w
operator|.
name|s_size
expr_stmt|;
if|if
condition|(
name|w
operator|.
name|method
operator|==
literal|null
condition|)
return|return
name|w
operator|.
name|result
return|;
name|boolean
name|res
decl_stmt|;
try|try
block|{
name|Object
name|resobj
init|=
name|w
operator|.
name|method
operator|.
name|invoke
argument_list|(
name|w
operator|.
name|methodobject
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|res
operator|=
name|resobj
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
comment|// FIXME - debug message
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
comment|// FIXME - debug message
block|}
name|cursor
operator|=
name|c
operator|-
name|w
operator|.
name|s_size
expr_stmt|;
if|if
condition|(
name|res
condition|)
return|return
name|w
operator|.
name|result
return|;
block|}
name|i
operator|=
name|w
operator|.
name|substring_i
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
return|return
literal|0
return|;
block|}
block|}
comment|/* to replace chars between c_bra and c_ket in current by the      * chars in s.      */
DECL|method|replace_s
specifier|protected
name|int
name|replace_s
parameter_list|(
name|int
name|c_bra
parameter_list|,
name|int
name|c_ket
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|int
name|adjustment
init|=
name|s
operator|.
name|length
argument_list|()
operator|-
operator|(
name|c_ket
operator|-
name|c_bra
operator|)
decl_stmt|;
name|current
operator|.
name|replace
argument_list|(
name|c_bra
argument_list|,
name|c_ket
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|limit
operator|+=
name|adjustment
expr_stmt|;
if|if
condition|(
name|cursor
operator|>=
name|c_ket
condition|)
name|cursor
operator|+=
name|adjustment
expr_stmt|;
elseif|else
if|if
condition|(
name|cursor
operator|>
name|c_bra
condition|)
name|cursor
operator|=
name|c_bra
expr_stmt|;
return|return
name|adjustment
return|;
block|}
DECL|method|slice_check
specifier|protected
name|void
name|slice_check
parameter_list|()
block|{
if|if
condition|(
name|bra
argument_list|<
literal|0
operator|||
name|bra
argument_list|>
name|ket
operator|||
name|ket
operator|>
name|limit
operator|||
name|limit
operator|>
name|current
operator|.
name|length
argument_list|()
condition|)
comment|// this line could be removed
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"faulty slice operation"
argument_list|)
expr_stmt|;
comment|// FIXME: report error somehow.
comment|/* 	    fprintf(stderr, "faulty slice operation:\n"); 	    debug(z, -1, 0); 	    exit(1); 	    */
block|}
block|}
DECL|method|slice_from
specifier|protected
name|void
name|slice_from
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|slice_check
argument_list|()
expr_stmt|;
name|replace_s
argument_list|(
name|bra
argument_list|,
name|ket
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|slice_from
specifier|protected
name|void
name|slice_from
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
name|slice_from
argument_list|(
name|s
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|slice_del
specifier|protected
name|void
name|slice_del
parameter_list|()
block|{
name|slice_from
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|insert
specifier|protected
name|void
name|insert
parameter_list|(
name|int
name|c_bra
parameter_list|,
name|int
name|c_ket
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|int
name|adjustment
init|=
name|replace_s
argument_list|(
name|c_bra
argument_list|,
name|c_ket
argument_list|,
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|c_bra
operator|<=
name|bra
condition|)
name|bra
operator|+=
name|adjustment
expr_stmt|;
if|if
condition|(
name|c_bra
operator|<=
name|ket
condition|)
name|ket
operator|+=
name|adjustment
expr_stmt|;
block|}
DECL|method|insert
specifier|protected
name|void
name|insert
parameter_list|(
name|int
name|c_bra
parameter_list|,
name|int
name|c_ket
parameter_list|,
name|StringBuffer
name|s
parameter_list|)
block|{
name|insert
argument_list|(
name|c_bra
argument_list|,
name|c_ket
argument_list|,
name|s
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* Copy the slice into the supplied StringBuffer */
DECL|method|slice_to
specifier|protected
name|StringBuffer
name|slice_to
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
name|slice_check
argument_list|()
expr_stmt|;
name|int
name|len
init|=
name|ket
operator|-
name|bra
decl_stmt|;
name|s
operator|.
name|replace
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|current
operator|.
name|substring
argument_list|(
name|bra
argument_list|,
name|ket
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|assign_to
specifier|protected
name|StringBuffer
name|assign_to
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
name|s
operator|.
name|replace
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|current
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/* extern void debug(struct SN_env * z, int number, int line_count) {   int i;     int limit = SIZE(z->p);     //if (number>= 0) printf("%3d (line %4d): '", number, line_count);     if (number>= 0) printf("%3d (line %4d): [%d]'", number, line_count,limit);     for (i = 0; i<= limit; i++)     {   if (z->lb == i) printf("{");         if (z->bra == i) printf("[");         if (z->c == i) printf("|");         if (z->ket == i) printf("]");         if (z->l == i) printf("}");         if (i< limit)         {   int ch = z->p[i];             if (ch == 0) ch = '#';             printf("%c", ch);         }     }     printf("'\n"); } */
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

