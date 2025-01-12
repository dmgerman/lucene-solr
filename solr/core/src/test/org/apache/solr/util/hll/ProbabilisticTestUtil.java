begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package

begin_comment
comment|/**  * A collection of test utilities for constructing input values to HLLs and for  * computing their serialized size.  */
end_comment

begin_class
DECL|class|ProbabilisticTestUtil
specifier|public
class|class
name|ProbabilisticTestUtil
block|{
comment|/**      * Constructs a value that when added raw to a HLL will set the register at      *<code>registerIndex</code> to<code>registerValue</code>.      *      * @param  log2m the log-base-2 of the number of registers in the HLL      * @param  registerIndex the index of the register to set      * @param  registerValue the value to set the register to      * @return the value      */
DECL|method|constructHLLValue
specifier|public
specifier|static
name|long
name|constructHLLValue
parameter_list|(
specifier|final
name|int
name|log2m
parameter_list|,
specifier|final
name|int
name|registerIndex
parameter_list|,
specifier|final
name|int
name|registerValue
parameter_list|)
block|{
specifier|final
name|long
name|partition
init|=
name|registerIndex
decl_stmt|;
specifier|final
name|long
name|substreamValue
init|=
operator|(
literal|1L
operator|<<
operator|(
name|registerValue
operator|-
literal|1
operator|)
operator|)
decl_stmt|;
return|return
operator|(
name|substreamValue
operator|<<
name|log2m
operator|)
operator||
name|partition
return|;
block|}
comment|/**      * Extracts the HLL register index from a raw value.      */
DECL|method|getRegisterIndex
specifier|public
specifier|static
name|short
name|getRegisterIndex
parameter_list|(
specifier|final
name|long
name|rawValue
parameter_list|,
specifier|final
name|int
name|log2m
parameter_list|)
block|{
specifier|final
name|long
name|mBitsMask
init|=
operator|(
literal|1
operator|<<
name|log2m
operator|)
operator|-
literal|1
decl_stmt|;
specifier|final
name|short
name|j
init|=
call|(
name|short
call|)
argument_list|(
name|rawValue
operator|&
name|mBitsMask
argument_list|)
decl_stmt|;
return|return
name|j
return|;
block|}
comment|/**      * Extracts the HLL register value from a raw value.      */
DECL|method|getRegisterValue
specifier|public
specifier|static
name|byte
name|getRegisterValue
parameter_list|(
specifier|final
name|long
name|rawValue
parameter_list|,
specifier|final
name|int
name|log2m
parameter_list|)
block|{
specifier|final
name|long
name|substreamValue
init|=
operator|(
name|rawValue
operator|>>>
name|log2m
operator|)
decl_stmt|;
specifier|final
name|byte
name|p_w
decl_stmt|;
if|if
condition|(
name|substreamValue
operator|==
literal|0L
condition|)
block|{
comment|// The paper does not cover p(0x0), so the special value 0 is used.
comment|// 0 is the original initialization value of the registers, so by
comment|// doing this the HLL simply ignores it. This is acceptable
comment|// because the probability is 1/(2^(2^registerSizeInBits)).
name|p_w
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|p_w
operator|=
operator|(
name|byte
operator|)
name|Math
operator|.
name|min
argument_list|(
literal|1
operator|+
name|BitUtil
operator|.
name|leastSignificantBit
argument_list|(
name|substreamValue
argument_list|)
argument_list|,
literal|31
argument_list|)
expr_stmt|;
block|}
return|return
name|p_w
return|;
block|}
comment|/**      * @return the number of bytes required to pack<code>registerCount</code>      *         registers of width<code>shortWordLength</code>.      */
DECL|method|getRequiredBytes
specifier|public
specifier|static
name|int
name|getRequiredBytes
parameter_list|(
specifier|final
name|int
name|shortWordLength
parameter_list|,
specifier|final
name|int
name|registerCount
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|registerCount
operator|*
name|shortWordLength
operator|)
operator|/
operator|(
name|float
operator|)
literal|8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

