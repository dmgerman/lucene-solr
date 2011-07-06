begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**   * Declares an interface for heap (and heap alike) structures,   * handling a given type T  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Heap
specifier|public
interface|interface
name|Heap
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Get and remove the top of the Heap<BR>    * NOTE: Once {@link #pop()} is called no other {@link #add(Object)} or    * {@link #insertWithOverflow(Object)} should be called.    */
DECL|method|pop
specifier|public
name|T
name|pop
parameter_list|()
function_decl|;
comment|/** Get (But not remove) the top of the Heap */
DECL|method|top
specifier|public
name|T
name|top
parameter_list|()
function_decl|;
comment|/**    * Insert a new value, returning the overflowen object<br>    * NOTE: This method should not be called after invoking {@link #pop()}    */
DECL|method|insertWithOverflow
specifier|public
name|T
name|insertWithOverflow
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**     * Add a new value to the heap, return the new top().<br>    * Some implementations may choose to not implement this functionality.     * In such a case<code>null</code> should be returned.<BR>     * NOTE: This method should not be called after invoking {@link #pop()}    */
DECL|method|add
specifier|public
name|T
name|add
parameter_list|(
name|T
name|frn
parameter_list|)
function_decl|;
comment|/** Clear the heap */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/** Return the amount of objects currently in the heap */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

