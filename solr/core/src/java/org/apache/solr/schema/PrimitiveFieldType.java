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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Abstract class defining shared behavior for primitive types  * Intended to be used as base class for non-analyzed fields like  * int, float, string, date etc, and set proper defaults for them   */
end_comment

begin_class
DECL|class|PrimitiveFieldType
specifier|public
specifier|abstract
class|class
name|PrimitiveFieldType
extends|extends
name|FieldType
block|{
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|getVersion
argument_list|()
operator|>
literal|1.4F
operator|&&
comment|// only override if it's not explicitly false
literal|0
operator|==
operator|(
name|falseProperties
operator|&
name|OMIT_NORMS
operator|)
condition|)
block|{
name|properties
operator||=
name|OMIT_NORMS
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

