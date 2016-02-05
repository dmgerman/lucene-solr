begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Interface for string distances.  */
end_comment

begin_interface
DECL|interface|StringDistance
specifier|public
interface|interface
name|StringDistance
block|{
comment|/**    * Returns a float between 0 and 1 based on how similar the specified strings are to one another.      * Returning a value of 1 means the specified strings are identical and 0 means the    * string are maximally different.    * @param s1 The first string.    * @param s2 The second string.    * @return a float between 0 and 1 based on how similar the specified strings are to one another.    */
DECL|method|getDistance
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

