begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|Replica
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ShardParams
import|;
end_import

begin_interface
DECL|interface|ReplicaListTransformer
specifier|public
interface|interface
name|ReplicaListTransformer
block|{
comment|/**    * Transforms the passed in list of choices. Transformations can include (but are not limited to)    * reordering of elements (e.g. via shuffling) and removal of elements (i.e. filtering).    *    * @param choices - a list of choices to transform, typically the choices are {@link Replica} objects but choices    * can also be {@link String} objects such as URLs passed in via the {@link ShardParams#SHARDS} parameter.    */
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|choices
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

