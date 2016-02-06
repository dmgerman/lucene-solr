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
comment|/**  * This interface should be implemented by {@link QueryNode} that holds a field  * and an arbitrary value.  *   * @see FieldableNode  * @see ValueQueryNode  */
end_comment

begin_interface
DECL|interface|FieldValuePairQueryNode
specifier|public
interface|interface
name|FieldValuePairQueryNode
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
extends|extends
name|FieldableNode
extends|,
name|ValueQueryNode
argument_list|<
name|T
argument_list|>
block|{  }
end_interface

end_unit

