begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.config
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
name|config
package|;
end_package

begin_comment
comment|/**  * This class represents a field configuration.  */
end_comment

begin_class
DECL|class|FieldConfig
specifier|public
class|class
name|FieldConfig
extends|extends
name|AbstractQueryConfig
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
comment|/**    * Constructs a {@link FieldConfig}    *     * @param fieldName the field name, it cannot be null    * @throws IllegalArgumentException if the field name is null    */
DECL|method|FieldConfig
specifier|public
name|FieldConfig
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name should not be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**    * Returns the field name this configuration represents.    *     * @return the field name    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<fieldconfig name=\""
operator|+
name|this
operator|.
name|fieldName
operator|+
literal|"\" configurations=\""
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"\"/>"
return|;
block|}
block|}
end_class

end_unit

