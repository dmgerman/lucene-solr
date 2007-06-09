begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|net.sf.snowball
package|package
name|net
operator|.
name|sf
operator|.
name|snowball
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_class
DECL|class|Among
specifier|public
class|class
name|Among
block|{
DECL|method|Among
specifier|public
name|Among
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|substring_i
parameter_list|,
name|int
name|result
parameter_list|,
name|String
name|methodname
parameter_list|,
name|SnowballProgram
name|methodobject
parameter_list|)
block|{
name|this
operator|.
name|s_size
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|substring_i
operator|=
name|substring_i
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|methodobject
operator|=
name|methodobject
expr_stmt|;
if|if
condition|(
name|methodname
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|method
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|this
operator|.
name|method
operator|=
name|methodobject
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
name|methodname
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// FIXME - debug message
name|this
operator|.
name|method
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|field|s_size
specifier|public
name|int
name|s_size
decl_stmt|;
comment|/* search string */
DECL|field|s
specifier|public
name|String
name|s
decl_stmt|;
comment|/* search string */
DECL|field|substring_i
specifier|public
name|int
name|substring_i
decl_stmt|;
comment|/* index to longest matching substring */
DECL|field|result
specifier|public
name|int
name|result
decl_stmt|;
comment|/* result of the lookup */
DECL|field|method
specifier|public
name|Method
name|method
decl_stmt|;
comment|/* method to use if substring matches */
DECL|field|methodobject
specifier|public
name|SnowballProgram
name|methodobject
decl_stmt|;
comment|/* object to invoke method on */
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

