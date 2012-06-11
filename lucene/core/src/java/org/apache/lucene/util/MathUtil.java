begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Math static utility methods.  */
end_comment

begin_class
DECL|class|MathUtil
specifier|public
specifier|final
class|class
name|MathUtil
block|{
comment|// No instance:
DECL|method|MathUtil
specifier|private
name|MathUtil
parameter_list|()
block|{   }
comment|/** returns x == 0 ? 0 : Math.floor(Math.log(x) / Math.log(base)) */
DECL|method|log
specifier|public
specifier|static
name|int
name|log
parameter_list|(
name|long
name|x
parameter_list|,
name|int
name|base
parameter_list|)
block|{
assert|assert
name|base
operator|>
literal|1
assert|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|x
operator|>=
name|base
condition|)
block|{
name|x
operator|/=
name|base
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

