begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DoubleValuesSource
import|;
end_import

begin_comment
comment|/**  * Binds variable names in expressions to actual data.  *<p>  * These are typically DocValues fields/FieldCache, the document's   * relevance score, or other ValueSources.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|Bindings
specifier|public
specifier|abstract
class|class
name|Bindings
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|Bindings
specifier|protected
name|Bindings
parameter_list|()
block|{}
comment|/**    * Returns a DoubleValuesSource bound to the variable name    */
DECL|method|getDoubleValuesSource
specifier|public
specifier|abstract
name|DoubleValuesSource
name|getDoubleValuesSource
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

