begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Resolves an ordinal's value to given the {@link FacetArrays}.  * Implementations of this class are encouraged to initialize the needed array  * from {@link FacetArrays} in the constructor.  */
end_comment

begin_class
DECL|class|OrdinalValueResolver
specifier|public
specifier|abstract
class|class
name|OrdinalValueResolver
block|{
comment|/**    * An {@link OrdinalValueResolver} which resolves ordinals value from    * {@link FacetArrays#getIntArray()}, by returning the value in the array.    */
DECL|class|IntValueResolver
specifier|public
specifier|static
specifier|final
class|class
name|IntValueResolver
extends|extends
name|OrdinalValueResolver
block|{
DECL|field|values
specifier|private
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|method|IntValueResolver
specifier|public
name|IntValueResolver
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|)
block|{
name|super
argument_list|(
name|arrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|arrays
operator|.
name|getIntArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valueOf
specifier|public
specifier|final
name|double
name|valueOf
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|values
index|[
name|ordinal
index|]
return|;
block|}
block|}
comment|/**    * An {@link OrdinalValueResolver} which resolves ordinals value from    * {@link FacetArrays#getFloatArray()}, by returning the value in the array.    */
DECL|class|FloatValueResolver
specifier|public
specifier|static
specifier|final
class|class
name|FloatValueResolver
extends|extends
name|OrdinalValueResolver
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|FloatValueResolver
specifier|public
name|FloatValueResolver
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|)
block|{
name|super
argument_list|(
name|arrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|arrays
operator|.
name|getFloatArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valueOf
specifier|public
specifier|final
name|double
name|valueOf
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|values
index|[
name|ordinal
index|]
return|;
block|}
block|}
DECL|field|arrays
specifier|protected
specifier|final
name|FacetArrays
name|arrays
decl_stmt|;
DECL|method|OrdinalValueResolver
specifier|protected
name|OrdinalValueResolver
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|)
block|{
name|this
operator|.
name|arrays
operator|=
name|arrays
expr_stmt|;
block|}
comment|/** Returns the value of the given ordinal. */
DECL|method|valueOf
specifier|public
specifier|abstract
name|double
name|valueOf
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
block|}
end_class

end_unit

