begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_interface
DECL|interface|SortableBytesRefArray
interface|interface
name|SortableBytesRefArray
block|{
comment|/** Append a new value */
DECL|method|append
name|int
name|append
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
function_decl|;
comment|/** Clear all previously stored values */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
comment|/** Returns the number of values appended so far */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/** Sort all values by the provided comparator and return an iterator over the sorted values */
DECL|method|iterator
name|BytesRefIterator
name|iterator
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

