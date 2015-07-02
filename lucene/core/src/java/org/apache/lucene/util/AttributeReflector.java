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
comment|/**  * This interface is used to reflect contents of {@link AttributeSource} or {@link AttributeImpl}.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|AttributeReflector
specifier|public
interface|interface
name|AttributeReflector
block|{
comment|/**    * This method gets called for every property in an {@link AttributeImpl}/{@link AttributeSource}    * passing the class name of the {@link Attribute}, a key and the actual value.    * E.g., an invocation of {@link org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl#reflectWith}    * would call this method once using {@code org.apache.lucene.analysis.tokenattributes.CharTermAttribute.class}    * as attribute class, {@code "term"} as key and the actual value as a String.    */
DECL|method|reflect
specifier|public
name|void
name|reflect
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

