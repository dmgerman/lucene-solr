begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
package|;
end_package

begin_comment
comment|/**  * This interface should be implemented by a {@link QueryNode} that represents  * some kind of range query.  *  */
end_comment

begin_interface
DECL|interface|RangeQueryNode
specifier|public
interface|interface
name|RangeQueryNode
parameter_list|<
name|T
extends|extends
name|FieldValuePairQueryNode
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|FieldableNode
block|{
DECL|method|getLowerBound
name|T
name|getLowerBound
parameter_list|()
function_decl|;
DECL|method|getUpperBound
name|T
name|getUpperBound
parameter_list|()
function_decl|;
DECL|method|isLowerInclusive
name|boolean
name|isLowerInclusive
parameter_list|()
function_decl|;
DECL|method|isUpperInclusive
name|boolean
name|isUpperInclusive
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

