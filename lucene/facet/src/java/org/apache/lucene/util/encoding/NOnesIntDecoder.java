begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
package|;
end_package

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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Decodes values encoded encoded with {@link NOnesIntEncoder}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NOnesIntDecoder
specifier|public
class|class
name|NOnesIntDecoder
extends|extends
name|FourFlagsIntDecoder
block|{
comment|// Number of consecutive '1's to generate upon decoding a '2'
DECL|field|n
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
DECL|field|internalBuffer
specifier|private
specifier|final
name|IntsRef
name|internalBuffer
decl_stmt|;
comment|/**    * Constructs a decoder with a given N (Number of consecutive '1's which are    * translated into a single target value '2'.    */
DECL|method|NOnesIntDecoder
specifier|public
name|NOnesIntDecoder
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
comment|// initial size (room for 100 integers)
name|internalBuffer
operator|=
operator|new
name|IntsRef
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|()
block|{
name|internalBuffer
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doDecode
specifier|protected
name|void
name|doDecode
parameter_list|(
name|BytesRef
name|buf
parameter_list|,
name|IntsRef
name|values
parameter_list|,
name|int
name|upto
parameter_list|)
block|{
name|super
operator|.
name|doDecode
argument_list|(
name|buf
argument_list|,
name|internalBuffer
argument_list|,
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|ints
operator|.
name|length
operator|<
name|internalBuffer
operator|.
name|length
condition|)
block|{
comment|// need space for internalBuffer.length to internalBuffer.length*N,
comment|// grow mildly at first
name|values
operator|.
name|grow
argument_list|(
name|internalBuffer
operator|.
name|length
operator|*
name|n
operator|/
literal|2
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
name|internalBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|decode
init|=
name|internalBuffer
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|decode
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|==
name|values
operator|.
name|ints
operator|.
name|length
condition|)
block|{
name|values
operator|.
name|grow
argument_list|(
name|values
operator|.
name|length
operator|+
literal|10
argument_list|)
expr_stmt|;
comment|// grow by few items, however not too many
block|}
comment|// 1 is 1
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|decode
operator|==
literal|2
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|+
name|n
operator|>=
name|values
operator|.
name|ints
operator|.
name|length
condition|)
block|{
name|values
operator|.
name|grow
argument_list|(
name|values
operator|.
name|length
operator|+
name|n
argument_list|)
expr_stmt|;
comment|// grow by few items, however not too many
block|}
comment|// '2' means N 1's
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|n
condition|;
name|j
operator|++
control|)
block|{
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|==
name|values
operator|.
name|ints
operator|.
name|length
condition|)
block|{
name|values
operator|.
name|grow
argument_list|(
name|values
operator|.
name|length
operator|+
literal|10
argument_list|)
expr_stmt|;
comment|// grow by few items, however not too many
block|}
comment|// any other value is val-1
name|values
operator|.
name|ints
index|[
name|values
operator|.
name|length
operator|++
index|]
operator|=
name|decode
operator|-
literal|1
expr_stmt|;
block|}
block|}
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
literal|"NOnes ("
operator|+
name|n
operator|+
literal|") ("
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

