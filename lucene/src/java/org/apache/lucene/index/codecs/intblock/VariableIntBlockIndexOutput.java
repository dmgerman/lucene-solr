begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.intblock
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|intblock
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Naive int block API that writes vInts.  This is  *  expected to give poor performance; it's really only for  *  testing the pluggability.  One should typically use pfor instead. */
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
name|index
operator|.
name|codecs
operator|.
name|sep
operator|.
name|IntIndexOutput
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
name|IndexOutput
import|;
end_import

begin_comment
comment|// TODO: much of this can be shared code w/ the fixed case
end_comment

begin_comment
comment|/** Abstract base class that writes variable-size blocks of ints  *  to an IndexOutput.  While this is a simple approach, a  *  more performant approach would directly create an impl  *  of IntIndexOutput inside Directory.  Wrapping a generic  *  IndexInput will likely cost performance.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|VariableIntBlockIndexOutput
specifier|public
specifier|abstract
class|class
name|VariableIntBlockIndexOutput
extends|extends
name|IntIndexOutput
block|{
DECL|field|out
specifier|protected
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|field|hitExcDuringWrite
specifier|private
name|boolean
name|hitExcDuringWrite
decl_stmt|;
comment|// TODO what Var-Var codecs exist in practice... and what are there blocksizes like?
comment|// if its less than 128 we should set that as max and use byte?
comment|/** NOTE: maxBlockSize must be the maximum block size     *  plus the max non-causal lookahead of your codec.  EG Simple9    *  requires lookahead=1 because on seeing the Nth value    *  it knows it must now encode the N-1 values before it. */
DECL|method|VariableIntBlockIndexOutput
specifier|protected
name|VariableIntBlockIndexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|maxBlockSize
argument_list|)
expr_stmt|;
block|}
comment|/** Called one value at a time.  Return the number of    *  buffered input values that have been written to out. */
DECL|method|add
specifier|protected
specifier|abstract
name|int
name|add
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Index
argument_list|()
return|;
block|}
DECL|class|Index
specifier|private
class|class
name|Index
extends|extends
name|IntIndexOutput
operator|.
name|Index
block|{
DECL|field|fp
name|long
name|fp
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|lastFP
name|long
name|lastFP
decl_stmt|;
DECL|field|lastUpto
name|int
name|lastUpto
decl_stmt|;
annotation|@
name|Override
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|()
throws|throws
name|IOException
block|{
name|fp
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|upto
operator|=
name|VariableIntBlockIndexOutput
operator|.
name|this
operator|.
name|upto
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|IntIndexOutput
operator|.
name|Index
name|other
parameter_list|,
name|boolean
name|copyLast
parameter_list|)
throws|throws
name|IOException
block|{
name|Index
name|idx
init|=
operator|(
name|Index
operator|)
name|other
decl_stmt|;
name|fp
operator|=
name|idx
operator|.
name|fp
expr_stmt|;
name|upto
operator|=
name|idx
operator|.
name|upto
expr_stmt|;
if|if
condition|(
name|copyLast
condition|)
block|{
name|lastFP
operator|=
name|fp
expr_stmt|;
name|lastUpto
operator|=
name|upto
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|IndexOutput
name|indexOut
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|upto
operator|>=
literal|0
assert|;
if|if
condition|(
name|absolute
condition|)
block|{
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fp
operator|==
name|lastFP
condition|)
block|{
comment|// same block
assert|assert
name|upto
operator|>=
name|lastUpto
assert|;
name|int
name|uptoDelta
init|=
name|upto
operator|-
name|lastUpto
decl_stmt|;
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|uptoDelta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// new block
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|upto
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|fp
operator|-
name|lastFP
argument_list|)
expr_stmt|;
block|}
name|lastUpto
operator|=
name|upto
expr_stmt|;
name|lastFP
operator|=
name|fp
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|hitExcDuringWrite
operator|=
literal|true
expr_stmt|;
name|upto
operator|-=
name|add
argument_list|(
name|v
argument_list|)
operator|-
literal|1
expr_stmt|;
name|hitExcDuringWrite
operator|=
literal|false
expr_stmt|;
assert|assert
name|upto
operator|>=
literal|0
assert|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|hitExcDuringWrite
condition|)
block|{
comment|// stuff 0s in until the "real" data is flushed:
name|int
name|stuffed
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|>
name|stuffed
condition|)
block|{
name|upto
operator|-=
name|add
argument_list|(
literal|0
argument_list|)
operator|-
literal|1
expr_stmt|;
assert|assert
name|upto
operator|>=
literal|0
assert|;
name|stuffed
operator|+=
literal|1
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

