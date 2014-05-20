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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * An FST {@link Outputs} implementation where each output  * is a sequence of bytes.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ByteSequenceOutputs
specifier|public
specifier|final
class|class
name|ByteSequenceOutputs
extends|extends
name|Outputs
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|private
specifier|final
specifier|static
name|BytesRef
name|NO_OUTPUT
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|singleton
specifier|private
specifier|final
specifier|static
name|ByteSequenceOutputs
name|singleton
init|=
operator|new
name|ByteSequenceOutputs
argument_list|()
decl_stmt|;
DECL|method|ByteSequenceOutputs
specifier|private
name|ByteSequenceOutputs
parameter_list|()
block|{   }
DECL|method|getSingleton
specifier|public
specifier|static
name|ByteSequenceOutputs
name|getSingleton
parameter_list|()
block|{
return|return
name|singleton
return|;
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|BytesRef
name|common
parameter_list|(
name|BytesRef
name|output1
parameter_list|,
name|BytesRef
name|output2
parameter_list|)
block|{
assert|assert
name|output1
operator|!=
literal|null
assert|;
assert|assert
name|output2
operator|!=
literal|null
assert|;
name|int
name|pos1
init|=
name|output1
operator|.
name|offset
decl_stmt|;
name|int
name|pos2
init|=
name|output2
operator|.
name|offset
decl_stmt|;
name|int
name|stopAt1
init|=
name|pos1
operator|+
name|Math
operator|.
name|min
argument_list|(
name|output1
operator|.
name|length
argument_list|,
name|output2
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|pos1
operator|<
name|stopAt1
condition|)
block|{
if|if
condition|(
name|output1
operator|.
name|bytes
index|[
name|pos1
index|]
operator|!=
name|output2
operator|.
name|bytes
index|[
name|pos2
index|]
condition|)
block|{
break|break;
block|}
name|pos1
operator|++
expr_stmt|;
name|pos2
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pos1
operator|==
name|output1
operator|.
name|offset
condition|)
block|{
comment|// no common prefix
return|return
name|NO_OUTPUT
return|;
block|}
elseif|else
if|if
condition|(
name|pos1
operator|==
name|output1
operator|.
name|offset
operator|+
name|output1
operator|.
name|length
condition|)
block|{
comment|// output1 is a prefix of output2
return|return
name|output1
return|;
block|}
elseif|else
if|if
condition|(
name|pos2
operator|==
name|output2
operator|.
name|offset
operator|+
name|output2
operator|.
name|length
condition|)
block|{
comment|// output2 is a prefix of output1
return|return
name|output2
return|;
block|}
else|else
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|output1
operator|.
name|bytes
argument_list|,
name|output1
operator|.
name|offset
argument_list|,
name|pos1
operator|-
name|output1
operator|.
name|offset
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|BytesRef
name|subtract
parameter_list|(
name|BytesRef
name|output
parameter_list|,
name|BytesRef
name|inc
parameter_list|)
block|{
assert|assert
name|output
operator|!=
literal|null
assert|;
assert|assert
name|inc
operator|!=
literal|null
assert|;
if|if
condition|(
name|inc
operator|==
name|NO_OUTPUT
condition|)
block|{
comment|// no prefix removed
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|inc
operator|.
name|length
operator|==
name|output
operator|.
name|length
condition|)
block|{
comment|// entire output removed
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
assert|assert
name|inc
operator|.
name|length
operator|<
name|output
operator|.
name|length
operator|:
literal|"inc.length="
operator|+
name|inc
operator|.
name|length
operator|+
literal|" vs output.length="
operator|+
name|output
operator|.
name|length
assert|;
assert|assert
name|inc
operator|.
name|length
operator|>
literal|0
assert|;
return|return
operator|new
name|BytesRef
argument_list|(
name|output
operator|.
name|bytes
argument_list|,
name|output
operator|.
name|offset
operator|+
name|inc
operator|.
name|length
argument_list|,
name|output
operator|.
name|length
operator|-
name|inc
operator|.
name|length
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|BytesRef
name|add
parameter_list|(
name|BytesRef
name|prefix
parameter_list|,
name|BytesRef
name|output
parameter_list|)
block|{
assert|assert
name|prefix
operator|!=
literal|null
assert|;
assert|assert
name|output
operator|!=
literal|null
assert|;
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
assert|assert
name|prefix
operator|.
name|length
operator|>
literal|0
assert|;
assert|assert
name|output
operator|.
name|length
operator|>
literal|0
assert|;
name|BytesRef
name|result
init|=
operator|new
name|BytesRef
argument_list|(
name|prefix
operator|.
name|length
operator|+
name|output
operator|.
name|length
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|prefix
operator|.
name|bytes
argument_list|,
name|prefix
operator|.
name|offset
argument_list|,
name|result
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|prefix
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
operator|.
name|bytes
argument_list|,
name|output
operator|.
name|offset
argument_list|,
name|result
operator|.
name|bytes
argument_list|,
name|prefix
operator|.
name|length
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|prefix
operator|.
name|length
operator|+
name|output
operator|.
name|length
expr_stmt|;
return|return
name|result
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
name|BytesRef
name|prefix
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|prefix
operator|!=
literal|null
assert|;
name|out
operator|.
name|writeVInt
argument_list|(
name|prefix
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|prefix
operator|.
name|bytes
argument_list|,
name|prefix
operator|.
name|offset
argument_list|,
name|prefix
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|BytesRef
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
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
specifier|final
name|BytesRef
name|output
init|=
operator|new
name|BytesRef
argument_list|(
name|len
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|output
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|output
operator|.
name|length
operator|=
name|len
expr_stmt|;
return|return
name|output
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|skipOutput
specifier|public
name|void
name|skipOutput
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
literal|0
condition|)
block|{
name|in
operator|.
name|skipBytes
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|BytesRef
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
name|BytesRef
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
block|}
end_class

end_unit

