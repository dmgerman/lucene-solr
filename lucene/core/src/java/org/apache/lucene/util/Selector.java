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

begin_comment
comment|/** An implementation of a selection algorithm, ie. computing the k-th greatest  *  value from a collection. */
end_comment

begin_class
DECL|class|Selector
specifier|public
specifier|abstract
class|class
name|Selector
block|{
comment|/** Reorder elements so that the element at position {@code k} is the same    *  as if all elements were sorted and all other elements are partitioned    *  around it: {@code [from, k)} only contains elements that are less than    *  or equal to {@code k} and {@code (k, to)} only contains elements that    *  are greater than or equal to {@code k}. */
DECL|method|select
specifier|public
specifier|abstract
name|void
name|select
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|k
parameter_list|)
function_decl|;
DECL|method|checkArgs
name|void
name|checkArgs
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|k
parameter_list|)
block|{
if|if
condition|(
name|k
operator|<
name|from
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"k must be>= from"
argument_list|)
throw|;
block|}
if|if
condition|(
name|k
operator|>=
name|to
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"k must be< to"
argument_list|)
throw|;
block|}
block|}
comment|/** Swap values at slots<code>i</code> and<code>j</code>. */
DECL|method|swap
specifier|protected
specifier|abstract
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
function_decl|;
block|}
end_class

end_unit

