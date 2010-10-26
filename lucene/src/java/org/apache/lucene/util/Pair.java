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
comment|/**  * Simple Pair  * @lucene.internal  */
end_comment

begin_class
DECL|class|Pair
specifier|public
class|class
name|Pair
parameter_list|<
name|Cur
parameter_list|,
name|Cud
parameter_list|>
block|{
DECL|field|cur
specifier|public
specifier|final
name|Cur
name|cur
decl_stmt|;
DECL|field|cud
specifier|public
specifier|final
name|Cud
name|cud
decl_stmt|;
comment|/**    * Create a simple pair    * @param cur the first element     * @param cud the second element    */
DECL|method|Pair
specifier|public
name|Pair
parameter_list|(
name|Cur
name|cur
parameter_list|,
name|Cud
name|cud
parameter_list|)
block|{
name|this
operator|.
name|cur
operator|=
name|cur
expr_stmt|;
name|this
operator|.
name|cud
operator|=
name|cud
expr_stmt|;
block|}
block|}
end_class

end_unit

