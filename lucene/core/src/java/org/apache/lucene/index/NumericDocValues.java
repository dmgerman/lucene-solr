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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A per-document numeric value.  */
end_comment

begin_class
DECL|class|NumericDocValues
specifier|public
specifier|abstract
class|class
name|NumericDocValues
extends|extends
name|DocValuesIterator
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|NumericDocValues
specifier|protected
name|NumericDocValues
parameter_list|()
block|{}
comment|/**    * Returns the numeric value for the current document ID.    * It is illegal to call this method after {@link #advanceExact(int)}    * returned {@code false}.    * @return numeric value    */
DECL|method|longValue
specifier|public
specifier|abstract
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

