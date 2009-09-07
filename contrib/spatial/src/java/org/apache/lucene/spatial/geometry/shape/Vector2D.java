begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geometry.shape
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
operator|.
name|shape
package|;
end_package

begin_comment
comment|/**  * 2D vector  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|Vector2D
specifier|public
class|class
name|Vector2D
block|{
DECL|field|x
specifier|private
name|double
name|x
decl_stmt|;
DECL|field|y
specifier|private
name|double
name|y
decl_stmt|;
comment|/**    * Create a vector from the origin of the coordinate system to the given    * point    *     * @param x    * @param y    */
DECL|method|Vector2D
specifier|public
name|Vector2D
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
comment|/**    * Create a vector from the origin of the coordinate system to the given    * point    */
DECL|method|Vector2D
specifier|public
name|Vector2D
parameter_list|(
name|Point2D
name|p
parameter_list|)
block|{
name|this
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a vector from one point to another    *     * @param from    * @param to    */
DECL|method|Vector2D
specifier|public
name|Vector2D
parameter_list|(
name|Point2D
name|from
parameter_list|,
name|Point2D
name|to
parameter_list|)
block|{
name|this
argument_list|(
name|to
operator|.
name|getX
argument_list|()
operator|-
name|from
operator|.
name|getX
argument_list|()
argument_list|,
name|to
operator|.
name|getY
argument_list|()
operator|-
name|from
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|Vector2D
specifier|public
name|Vector2D
parameter_list|()
block|{
name|this
operator|.
name|x
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|y
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|Vector2D
specifier|public
name|Vector2D
parameter_list|(
name|Vector2D
name|other
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|other
operator|.
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|other
operator|.
name|y
expr_stmt|;
block|}
DECL|method|getX
specifier|public
name|double
name|getX
parameter_list|()
block|{
return|return
name|x
return|;
block|}
DECL|method|getY
specifier|public
name|double
name|getY
parameter_list|()
block|{
return|return
name|y
return|;
block|}
DECL|method|setX
specifier|public
name|void
name|setX
parameter_list|(
name|double
name|x
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
block|}
DECL|method|setY
specifier|public
name|void
name|setY
parameter_list|(
name|double
name|y
parameter_list|)
block|{
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Vector2D
name|other
parameter_list|)
block|{
return|return
name|other
operator|!=
literal|null
operator|&&
name|x
operator|==
name|other
operator|.
name|x
operator|&&
name|y
operator|==
name|other
operator|.
name|y
return|;
block|}
DECL|method|dot
specifier|public
name|double
name|dot
parameter_list|(
name|Vector2D
name|in
parameter_list|)
block|{
return|return
operator|(
operator|(
name|x
operator|)
operator|*
name|in
operator|.
name|x
operator|)
operator|+
operator|(
name|y
operator|*
name|in
operator|.
name|y
operator|)
return|;
block|}
comment|/**    * Vector length (magnitude) squared    */
DECL|method|normSqr
specifier|public
name|double
name|normSqr
parameter_list|()
block|{
comment|// Cast to F to prevent overflows
return|return
operator|(
name|x
operator|*
name|x
operator|)
operator|+
operator|(
name|y
operator|*
name|y
operator|)
return|;
block|}
DECL|method|norm
specifier|public
name|double
name|norm
parameter_list|()
block|{
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|normSqr
argument_list|()
argument_list|)
return|;
block|}
DECL|method|mult
specifier|public
name|Vector2D
name|mult
parameter_list|(
name|double
name|d
parameter_list|)
block|{
return|return
operator|new
name|Vector2D
argument_list|(
name|x
operator|*
name|d
argument_list|,
name|y
operator|*
name|d
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Vector2D
name|other
init|=
operator|(
name|Vector2D
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|x
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|x
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|y
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|y
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

