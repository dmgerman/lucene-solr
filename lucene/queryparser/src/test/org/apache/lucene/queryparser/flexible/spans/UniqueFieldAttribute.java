begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.spans
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
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
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
operator|.
name|FieldableNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * This attribute is used by the {@link UniqueFieldQueryNodeProcessor}  * processor. It holds a value that defines which is the unique field name that  * should be set in every {@link FieldableNode}.  *   * @see UniqueFieldQueryNodeProcessor  */
end_comment

begin_interface
DECL|interface|UniqueFieldAttribute
specifier|public
interface|interface
name|UniqueFieldAttribute
extends|extends
name|Attribute
block|{
DECL|method|setUniqueField
specifier|public
name|void
name|setUniqueField
parameter_list|(
name|CharSequence
name|uniqueField
parameter_list|)
function_decl|;
DECL|method|getUniqueField
specifier|public
name|CharSequence
name|getUniqueField
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

