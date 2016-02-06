begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * A list of per-document numeric values, sorted   * according to {@link Long#compare(long, long)}.  */
end_comment

begin_class
DECL|class|SortedNumericDocValues
specifier|public
specifier|abstract
class|class
name|SortedNumericDocValues
block|{
comment|/** Sole constructor. (For invocation by subclass     * constructors, typically implicit.) */
DECL|method|SortedNumericDocValues
specifier|protected
name|SortedNumericDocValues
parameter_list|()
block|{}
comment|/**     * Positions to the specified document     */
DECL|method|setDocument
specifier|public
specifier|abstract
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**     * Retrieve the value for the current document at the specified index.     * An index ranges from {@code 0} to {@code count()-1}.     */
DECL|method|valueAt
specifier|public
specifier|abstract
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**     * Retrieves the count of values for the current document.     * This may be zero if a document has no values.    */
DECL|method|count
specifier|public
specifier|abstract
name|int
name|count
parameter_list|()
function_decl|;
block|}
end_class

end_unit

