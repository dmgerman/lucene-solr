begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/** * An interface that can be extended to provide a callback mechanism for * informing an {@link IndexSchema} instance of changes to it, dynamically * performed at runtime. * * @since SOLR-1131  *  **/
end_comment

begin_interface
DECL|interface|SchemaAware
specifier|public
interface|interface
name|SchemaAware
block|{
comment|/**    * Informs the {@link IndexSchema} provided by the<code>schema</code>    * parameter of an event (e.g., a new {@link FieldType} was added, etc.    *    * @param schema    *          The {@link IndexSchema} instance that inform of the update to.    *    * @since SOLR-1131    */
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

