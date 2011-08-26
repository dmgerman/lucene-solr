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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * A random that tracks if its been initialized properly,  * and throws an exception if it hasn't.  */
end_comment

begin_class
DECL|class|SmartRandom
specifier|public
class|class
name|SmartRandom
extends|extends
name|Random
block|{
DECL|field|initialized
name|boolean
name|initialized
decl_stmt|;
DECL|method|SmartRandom
name|SmartRandom
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|protected
name|int
name|next
parameter_list|(
name|int
name|bits
parameter_list|)
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"!!! WARNING: test is using random from static initializer !!!"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|dumpStack
argument_list|()
expr_stmt|;
comment|// I wish, but it causes JRE crashes
comment|// throw new IllegalStateException("you cannot use this random from a static initializer in your test");
block|}
return|return
name|super
operator|.
name|next
argument_list|(
name|bits
argument_list|)
return|;
block|}
block|}
end_class

end_unit

