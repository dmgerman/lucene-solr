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

begin_comment
comment|/**  * Subclasses of StringInterner are required to  * return the same single String object for all equal strings.  * Depending on the implementation, this may not be  * the same object returned as String.intern().  *  * This StringInterner base class simply delegates to String.intern().  */
end_comment

begin_class
DECL|class|StringInterner
specifier|public
class|class
name|StringInterner
block|{
comment|/** Returns a single object instance for each equal string. */
DECL|method|intern
specifier|public
name|String
name|intern
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|intern
argument_list|()
return|;
block|}
comment|/** Returns a single object instance for each equal string. */
DECL|method|intern
specifier|public
name|String
name|intern
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|intern
argument_list|(
operator|new
name|String
argument_list|(
name|arr
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

