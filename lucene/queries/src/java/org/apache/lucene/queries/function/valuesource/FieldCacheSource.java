begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_comment
comment|/**  * A base class for ValueSource implementations that retrieve values for  * a single field from DocValues.  *  *  */
end_comment

begin_class
DECL|class|FieldCacheSource
specifier|public
specifier|abstract
class|class
name|FieldCacheSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|FieldCacheSource
specifier|public
name|FieldCacheSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FieldCacheSource
operator|)
condition|)
return|return
literal|false
return|;
name|FieldCacheSource
name|other
init|=
operator|(
name|FieldCacheSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

