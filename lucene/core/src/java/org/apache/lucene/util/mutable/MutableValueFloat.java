begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.mutable
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|mutable
package|;
end_package

begin_comment
comment|/**  * {@link MutableValue} implementation of type<code>float</code>.  * When mutating instances of this object, the caller is responsible for ensuring   * that any instance where<code>exists</code> is set to<code>false</code> must also   *<code>value</code> set to<code>0.0F</code> for proper operation.  */
end_comment

begin_class
DECL|class|MutableValueFloat
specifier|public
class|class
name|MutableValueFloat
extends|extends
name|MutableValue
block|{
DECL|field|value
specifier|public
name|float
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|()
block|{
assert|assert
name|exists
operator|||
literal|0.0F
operator|==
name|value
assert|;
return|return
name|exists
condition|?
name|value
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|MutableValue
name|source
parameter_list|)
block|{
name|MutableValueFloat
name|s
init|=
operator|(
name|MutableValueFloat
operator|)
name|source
decl_stmt|;
name|value
operator|=
name|s
operator|.
name|value
expr_stmt|;
name|exists
operator|=
name|s
operator|.
name|exists
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|duplicate
specifier|public
name|MutableValue
name|duplicate
parameter_list|()
block|{
name|MutableValueFloat
name|v
init|=
operator|new
name|MutableValueFloat
argument_list|()
decl_stmt|;
name|v
operator|.
name|value
operator|=
name|this
operator|.
name|value
expr_stmt|;
name|v
operator|.
name|exists
operator|=
name|this
operator|.
name|exists
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|equalsSameType
specifier|public
name|boolean
name|equalsSameType
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
assert|assert
name|exists
operator|||
literal|0.0F
operator|==
name|value
assert|;
name|MutableValueFloat
name|b
init|=
operator|(
name|MutableValueFloat
operator|)
name|other
decl_stmt|;
return|return
name|value
operator|==
name|b
operator|.
name|value
operator|&&
name|exists
operator|==
name|b
operator|.
name|exists
return|;
block|}
annotation|@
name|Override
DECL|method|compareSameType
specifier|public
name|int
name|compareSameType
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
assert|assert
name|exists
operator|||
literal|0.0F
operator|==
name|value
assert|;
name|MutableValueFloat
name|b
init|=
operator|(
name|MutableValueFloat
operator|)
name|other
decl_stmt|;
name|int
name|c
init|=
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|b
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
return|return
name|c
return|;
if|if
condition|(
name|exists
operator|==
name|b
operator|.
name|exists
condition|)
return|return
literal|0
return|;
return|return
name|exists
condition|?
literal|1
else|:
operator|-
literal|1
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
assert|assert
name|exists
operator|||
literal|0.0F
operator|==
name|value
assert|;
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

