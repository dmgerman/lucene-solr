begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|Geo3DUtil
class|class
name|Geo3DUtil
block|{
comment|/** Clips the incoming value to the allowed min/max range before encoding, instead of throwing an exception. */
DECL|method|encodeValueLenient
specifier|public
specifier|static
name|int
name|encodeValueLenient
parameter_list|(
name|double
name|planetMax
parameter_list|,
name|double
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|>
name|planetMax
condition|)
block|{
name|x
operator|=
name|planetMax
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|x
operator|<
operator|-
name|planetMax
condition|)
block|{
name|x
operator|=
operator|-
name|planetMax
expr_stmt|;
block|}
return|return
name|encodeValue
argument_list|(
name|planetMax
argument_list|,
name|x
argument_list|)
return|;
block|}
DECL|method|encodeValue
specifier|public
specifier|static
name|int
name|encodeValue
parameter_list|(
name|double
name|planetMax
parameter_list|,
name|double
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|>
name|planetMax
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value="
operator|+
name|x
operator|+
literal|" is out-of-bounds (greater than planetMax="
operator|+
name|planetMax
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|x
operator|<
operator|-
name|planetMax
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value="
operator|+
name|x
operator|+
literal|" is out-of-bounds (less than than -planetMax="
operator|+
operator|-
name|planetMax
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|long
name|y
init|=
name|Math
operator|.
name|round
argument_list|(
name|x
operator|*
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
name|planetMax
operator|)
argument_list|)
decl_stmt|;
assert|assert
name|y
operator|>=
name|Integer
operator|.
name|MIN_VALUE
assert|;
assert|assert
name|y
operator|<=
name|Integer
operator|.
name|MAX_VALUE
assert|;
return|return
operator|(
name|int
operator|)
name|y
return|;
block|}
comment|/** Center decode */
DECL|method|decodeValueCenter
specifier|public
specifier|static
name|double
name|decodeValueCenter
parameter_list|(
name|double
name|planetMax
parameter_list|,
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|*
operator|(
name|planetMax
operator|/
name|Integer
operator|.
name|MAX_VALUE
operator|)
return|;
block|}
comment|/** More negative decode, at bottom of cell */
DECL|method|decodeValueMin
specifier|public
specifier|static
name|double
name|decodeValueMin
parameter_list|(
name|double
name|planetMax
parameter_list|,
name|int
name|x
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
name|double
operator|)
name|x
operator|)
operator|-
literal|0.5
operator|)
operator|*
operator|(
name|planetMax
operator|/
name|Integer
operator|.
name|MAX_VALUE
operator|)
return|;
block|}
comment|/** More positive decode, at top of cell  */
DECL|method|decodeValueMax
specifier|public
specifier|static
name|double
name|decodeValueMax
parameter_list|(
name|double
name|planetMax
parameter_list|,
name|int
name|x
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
name|double
operator|)
name|x
operator|)
operator|+
literal|0.5
operator|)
operator|*
operator|(
name|planetMax
operator|/
name|Integer
operator|.
name|MAX_VALUE
operator|)
return|;
block|}
block|}
end_class

end_unit

