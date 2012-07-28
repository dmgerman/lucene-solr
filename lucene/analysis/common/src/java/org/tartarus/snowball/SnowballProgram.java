begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  Copyright (c) 2001, Dr Martin Porter Copyright (c) 2002, Richard Boulton All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:      * Redistributions of source code must retain the above copyright notice,     * this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright     * notice, this list of conditions and the following disclaimer in the     * documentation and/or other materials provided with the distribution.     * Neither the name of the copyright holders nor the names of its contributors     * may be used to endorse or promote products derived from this software     * without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.   */
end_comment

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
comment|/**  * This is the rev 502 of the Snowball SVN trunk,  * but modified:  * made abstract and introduced abstract method stem to avoid expensive reflection in filter class.  * refactored StringBuffers to StringBuilder  * uses char[] as buffer instead of StringBuffer/StringBuilder  * eq_s,eq_s_b,insert,replace_s take CharSequence like eq_v and eq_v_b  * reflection calls (Lovins, etc) use EMPTY_ARGS/EMPTY_PARAMS  */
end_comment

begin_class
DECL|class|SnowballProgram
specifier|public
specifier|abstract
class|class
name|SnowballProgram
block|{
DECL|field|EMPTY_ARGS
specifier|private
specifier|static
specifier|final
name|Object
index|[]
name|EMPTY_ARGS
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
DECL|method|SnowballProgram
specifier|protected
name|SnowballProgram
parameter_list|()
block|{
name|current
operator|=
operator|new
name|char
index|[
literal|8
index|]
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
operator|=
name|value
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|cursor
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
name|value
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
return|return
operator|new
name|String
argument_list|(
name|current
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
return|;
block|}
comment|/**      * Set the current string.      * @param text character array containing input      * @param length valid length of text.      */
DECL|method|setCurrent
specifier|public
name|void
name|setCurrent
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|current
operator|=
name|text
expr_stmt|;
name|cursor
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
name|length
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
comment|/**      * Get the current buffer containing the stem.      *<p>      * NOTE: this may be a reference to a different character array than the      * one originally provided with setCurrent, in the exceptional case that       * stemming produced a longer intermediate or result string.       *</p>      *<p>      * It is necessary to use {@link #getCurrentBufferLength()} to determine      * the valid length of the returned buffer. For example, many words are      * stemmed simply by subtracting from the length to remove suffixes.      *</p>      * @see #getCurrentBufferLength()      */
DECL|method|getCurrentBuffer
specifier|public
name|char
index|[]
name|getCurrentBuffer
parameter_list|()
block|{
return|return
name|current
return|;
block|}
comment|/**      * Get the valid length of the character array in       * {@link #getCurrentBuffer()}.       * @return valid length of the array.      */
DECL|method|getCurrentBufferLength
specifier|public
name|int
name|getCurrentBufferLength
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
comment|// current string
DECL|field|current
specifier|private
name|char
name|current
index|[]
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
index|[
name|cursor
index|]
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
index|[
name|cursor
operator|-
literal|1
index|]
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
index|[
name|cursor
index|]
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
index|[
name|cursor
operator|-
literal|1
index|]
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
index|[
name|cursor
index|]
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
index|[
name|cursor
operator|-
literal|1
index|]
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
index|[
name|cursor
index|]
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
index|[
name|cursor
operator|-
literal|1
index|]
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
name|CharSequence
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
index|[
name|cursor
operator|+
name|i
index|]
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
name|CharSequence
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
index|[
name|cursor
operator|-
name|s_size
operator|+
name|i
index|]
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
name|CharSequence
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
argument_list|)
return|;
block|}
DECL|method|eq_v_b
specifier|protected
name|boolean
name|eq_v_b
parameter_list|(
name|CharSequence
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
index|[
name|c
operator|+
name|common
index|]
operator|-
name|w
operator|.
name|s
index|[
name|i2
index|]
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
name|EMPTY_ARGS
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
index|[
name|c
operator|-
literal|1
operator|-
name|common
index|]
operator|-
name|w
operator|.
name|s
index|[
name|i2
index|]
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
name|EMPTY_ARGS
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
name|CharSequence
name|s
parameter_list|)
block|{
specifier|final
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
specifier|final
name|int
name|newLength
init|=
name|limit
operator|+
name|adjustment
decl_stmt|;
comment|//resize if necessary
if|if
condition|(
name|newLength
operator|>
name|current
operator|.
name|length
condition|)
block|{
name|char
name|newBuffer
index|[]
init|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newLength
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|current
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|current
operator|=
name|newBuffer
expr_stmt|;
block|}
comment|// if the substring being replaced is longer or shorter than the
comment|// replacement, need to shift things around
if|if
condition|(
name|adjustment
operator|!=
literal|0
operator|&&
name|c_ket
operator|<
name|limit
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|current
argument_list|,
name|c_ket
argument_list|,
name|current
argument_list|,
name|c_bra
operator|+
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|limit
operator|-
name|c_ket
argument_list|)
expr_stmt|;
block|}
comment|// insert the replacement text
comment|// Note, faster is s.getChars(0, s.length(), current, c_bra);
comment|// but would have to duplicate this method for both String and StringBuilder
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
name|current
index|[
name|c_bra
operator|+
name|i
index|]
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
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
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"faulty slice operation: bra="
operator|+
name|bra
operator|+
literal|",ket="
operator|+
name|ket
operator|+
literal|",limit="
operator|+
name|limit
argument_list|)
throw|;
comment|// FIXME: report error somehow.
comment|/* 	    fprintf(stderr, "faulty slice operation:\n"); 	    debug(z, -1, 0); 	    exit(1); 	    */
block|}
block|}
DECL|method|slice_from
specifier|protected
name|void
name|slice_from
parameter_list|(
name|CharSequence
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
DECL|method|slice_del
specifier|protected
name|void
name|slice_del
parameter_list|()
block|{
name|slice_from
argument_list|(
operator|(
name|CharSequence
operator|)
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
name|CharSequence
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
comment|/* Copy the slice into the supplied StringBuffer */
DECL|method|slice_to
specifier|protected
name|StringBuilder
name|slice_to
parameter_list|(
name|StringBuilder
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
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
name|current
argument_list|,
name|bra
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|assign_to
specifier|protected
name|StringBuilder
name|assign_to
parameter_list|(
name|StringBuilder
name|s
parameter_list|)
block|{
name|s
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
name|current
argument_list|,
literal|0
argument_list|,
name|limit
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

