begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
import|;
end_import

begin_comment
comment|/**  * An FST {@link Outputs} implementation where each output  * is one or two non-negative long values.  If it's a  * single output, Long is returned; else, TwoLongs.  Order  * is preserved in the TwoLongs case, ie .first is the first  * input/output added to Builder, and .second is the  * second.  You cannot store 0 output with this (that's  * reserved to mean "no output")!  *  *<p>NOTE: the only way to create a TwoLongs output is to  * add the same input to the FST twice in a row.  This is  * how the FST maps a single input to two outputs (e.g. you  * cannot pass a TwoLongs to {@link Builder#add}.  If you  * need more than two then use {@link ListOfOutputs}, but if  * you only have at most 2 then this implementation will  * require fewer bytes as it steals one bit from each long  * value.  *  *<p>NOTE: the resulting FST is not guaranteed to be minimal!  * See {@link Builder}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|UpToTwoPositiveIntOutputs
specifier|public
specifier|final
class|class
name|UpToTwoPositiveIntOutputs
extends|extends
name|Outputs
argument_list|<
name|Object
argument_list|>
block|{
comment|/** Holds two long outputs. */
DECL|class|TwoLongs
specifier|public
specifier|final
specifier|static
class|class
name|TwoLongs
block|{
DECL|field|first
specifier|public
specifier|final
name|long
name|first
decl_stmt|;
DECL|field|second
specifier|public
specifier|final
name|long
name|second
decl_stmt|;
DECL|method|TwoLongs
specifier|public
name|TwoLongs
parameter_list|(
name|long
name|first
parameter_list|,
name|long
name|second
parameter_list|)
block|{
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
name|this
operator|.
name|second
operator|=
name|second
expr_stmt|;
assert|assert
name|first
operator|>=
literal|0
assert|;
assert|assert
name|second
operator|>=
literal|0
assert|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TwoLongs:"
operator|+
name|first
operator|+
literal|","
operator|+
name|second
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
operator|instanceof
name|TwoLongs
condition|)
block|{
specifier|final
name|TwoLongs
name|other
init|=
operator|(
name|TwoLongs
operator|)
name|_other
decl_stmt|;
return|return
name|first
operator|==
name|other
operator|.
name|first
operator|&&
name|second
operator|==
name|other
operator|.
name|second
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
call|(
name|int
call|)
argument_list|(
operator|(
name|first
operator|^
operator|(
name|first
operator|>>>
literal|32
operator|)
operator|)
operator|^
operator|(
name|second
operator|^
operator|(
name|second
operator|>>
literal|32
operator|)
operator|)
argument_list|)
return|;
block|}
block|}
DECL|field|NO_OUTPUT
specifier|private
specifier|final
specifier|static
name|Long
name|NO_OUTPUT
init|=
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|doShare
specifier|private
specifier|final
name|boolean
name|doShare
decl_stmt|;
DECL|field|singletonShare
specifier|private
specifier|final
specifier|static
name|UpToTwoPositiveIntOutputs
name|singletonShare
init|=
operator|new
name|UpToTwoPositiveIntOutputs
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|singletonNoShare
specifier|private
specifier|final
specifier|static
name|UpToTwoPositiveIntOutputs
name|singletonNoShare
init|=
operator|new
name|UpToTwoPositiveIntOutputs
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|UpToTwoPositiveIntOutputs
specifier|private
name|UpToTwoPositiveIntOutputs
parameter_list|(
name|boolean
name|doShare
parameter_list|)
block|{
name|this
operator|.
name|doShare
operator|=
name|doShare
expr_stmt|;
block|}
DECL|method|getSingleton
specifier|public
specifier|static
name|UpToTwoPositiveIntOutputs
name|getSingleton
parameter_list|(
name|boolean
name|doShare
parameter_list|)
block|{
return|return
name|doShare
condition|?
name|singletonShare
else|:
name|singletonNoShare
return|;
block|}
DECL|method|get
specifier|public
name|Long
name|get
parameter_list|(
name|long
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
DECL|method|get
specifier|public
name|TwoLongs
name|get
parameter_list|(
name|long
name|first
parameter_list|,
name|long
name|second
parameter_list|)
block|{
return|return
operator|new
name|TwoLongs
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Long
name|common
parameter_list|(
name|Object
name|_output1
parameter_list|,
name|Object
name|_output2
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|_output1
argument_list|,
literal|false
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|_output2
argument_list|,
literal|false
argument_list|)
assert|;
specifier|final
name|Long
name|output1
init|=
operator|(
name|Long
operator|)
name|_output1
decl_stmt|;
specifier|final
name|Long
name|output2
init|=
operator|(
name|Long
operator|)
name|_output2
decl_stmt|;
if|if
condition|(
name|output1
operator|==
name|NO_OUTPUT
operator|||
name|output2
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
elseif|else
if|if
condition|(
name|doShare
condition|)
block|{
assert|assert
name|output1
operator|>
literal|0
assert|;
assert|assert
name|output2
operator|>
literal|0
assert|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|output1
argument_list|,
name|output2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|output1
operator|.
name|equals
argument_list|(
name|output2
argument_list|)
condition|)
block|{
return|return
name|output1
return|;
block|}
else|else
block|{
return|return
name|NO_OUTPUT
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Long
name|subtract
parameter_list|(
name|Object
name|_output
parameter_list|,
name|Object
name|_inc
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|_output
argument_list|,
literal|false
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|_inc
argument_list|,
literal|false
argument_list|)
assert|;
specifier|final
name|Long
name|output
init|=
operator|(
name|Long
operator|)
name|_output
decl_stmt|;
specifier|final
name|Long
name|inc
init|=
operator|(
name|Long
operator|)
name|_inc
decl_stmt|;
assert|assert
name|output
operator|>=
name|inc
assert|;
if|if
condition|(
name|inc
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|output
operator|.
name|equals
argument_list|(
name|inc
argument_list|)
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
name|output
operator|-
name|inc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Object
name|add
parameter_list|(
name|Object
name|_prefix
parameter_list|,
name|Object
name|_output
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|_prefix
argument_list|,
literal|false
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|_output
argument_list|,
literal|true
argument_list|)
assert|;
specifier|final
name|Long
name|prefix
init|=
operator|(
name|Long
operator|)
name|_prefix
decl_stmt|;
if|if
condition|(
name|_output
operator|instanceof
name|Long
condition|)
block|{
specifier|final
name|Long
name|output
init|=
operator|(
name|Long
operator|)
name|_output
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|output
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|prefix
return|;
block|}
else|else
block|{
return|return
name|prefix
operator|+
name|output
return|;
block|}
block|}
else|else
block|{
specifier|final
name|TwoLongs
name|output
init|=
operator|(
name|TwoLongs
operator|)
name|_output
decl_stmt|;
specifier|final
name|long
name|v
init|=
name|prefix
decl_stmt|;
return|return
operator|new
name|TwoLongs
argument_list|(
name|output
operator|.
name|first
operator|+
name|v
argument_list|,
name|output
operator|.
name|second
operator|+
name|v
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Object
name|_output
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|valid
argument_list|(
name|_output
argument_list|,
literal|true
argument_list|)
assert|;
if|if
condition|(
name|_output
operator|instanceof
name|Long
condition|)
block|{
specifier|final
name|Long
name|output
init|=
operator|(
name|Long
operator|)
name|_output
decl_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|output
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TwoLongs
name|output
init|=
operator|(
name|TwoLongs
operator|)
name|_output
decl_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
operator|(
name|output
operator|.
name|first
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|output
operator|.
name|second
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Object
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|code
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
comment|// single long
specifier|final
name|long
name|v
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// two longs
specifier|final
name|long
name|first
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|long
name|second
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
return|return
operator|new
name|TwoLongs
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
DECL|method|valid
specifier|private
name|boolean
name|valid
parameter_list|(
name|Long
name|o
parameter_list|)
block|{
assert|assert
name|o
operator|!=
literal|null
assert|;
assert|assert
name|o
operator|instanceof
name|Long
assert|;
assert|assert
name|o
operator|==
name|NO_OUTPUT
operator|||
name|o
operator|>
literal|0
assert|;
return|return
literal|true
return|;
block|}
comment|// Used only by assert
DECL|method|valid
specifier|private
name|boolean
name|valid
parameter_list|(
name|Object
name|_o
parameter_list|,
name|boolean
name|allowDouble
parameter_list|)
block|{
if|if
condition|(
operator|!
name|allowDouble
condition|)
block|{
assert|assert
name|_o
operator|instanceof
name|Long
assert|;
return|return
name|valid
argument_list|(
operator|(
name|Long
operator|)
name|_o
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|_o
operator|instanceof
name|TwoLongs
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|valid
argument_list|(
operator|(
name|Long
operator|)
name|_o
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Object
name|getNoOutput
parameter_list|()
block|{
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|Object
name|output
parameter_list|)
block|{
return|return
name|output
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|Object
name|merge
parameter_list|(
name|Object
name|first
parameter_list|,
name|Object
name|second
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|first
argument_list|,
literal|false
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|second
argument_list|,
literal|false
argument_list|)
assert|;
return|return
operator|new
name|TwoLongs
argument_list|(
operator|(
name|Long
operator|)
name|first
argument_list|,
operator|(
name|Long
operator|)
name|second
argument_list|)
return|;
block|}
block|}
end_class

end_unit

