begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/**  * Base class of a family of 3D rectangles, bounded on six sides by X,Y,Z limits  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|BaseXYZSolid
specifier|public
specifier|abstract
class|class
name|BaseXYZSolid
extends|extends
name|BasePlanetObject
implements|implements
name|XYZSolid
block|{
comment|/** Unit vector in x */
DECL|field|xUnitVector
specifier|protected
specifier|static
specifier|final
name|Vector
name|xUnitVector
init|=
operator|new
name|Vector
argument_list|(
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
comment|/** Unit vector in y */
DECL|field|yUnitVector
specifier|protected
specifier|static
specifier|final
name|Vector
name|yUnitVector
init|=
operator|new
name|Vector
argument_list|(
literal|0.0
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
comment|/** Unit vector in z */
DECL|field|zUnitVector
specifier|protected
specifier|static
specifier|final
name|Vector
name|zUnitVector
init|=
operator|new
name|Vector
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
comment|/** Vertical plane normal to x unit vector passing through origin */
DECL|field|xVerticalPlane
specifier|protected
specifier|static
specifier|final
name|Plane
name|xVerticalPlane
init|=
operator|new
name|Plane
argument_list|(
literal|0.0
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
comment|/** Vertical plane normal to y unit vector passing through origin */
DECL|field|yVerticalPlane
specifier|protected
specifier|static
specifier|final
name|Plane
name|yVerticalPlane
init|=
operator|new
name|Plane
argument_list|(
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
comment|/** Empty point vector */
DECL|field|EMPTY_POINTS
specifier|protected
specifier|static
specifier|final
name|GeoPoint
index|[]
name|EMPTY_POINTS
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Base solid constructor.    *@param planetModel is the planet model.    */
DECL|method|BaseXYZSolid
specifier|public
name|BaseXYZSolid
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a single array from a number of individual arrays.    * @param pointArrays is the array of point arrays.    * @return the single unified array.    */
DECL|method|glueTogether
specifier|protected
specifier|static
name|GeoPoint
index|[]
name|glueTogether
parameter_list|(
specifier|final
name|GeoPoint
index|[]
modifier|...
name|pointArrays
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
index|[]
name|pointArray
range|:
name|pointArrays
control|)
block|{
name|count
operator|+=
name|pointArray
operator|.
name|length
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|rval
init|=
operator|new
name|GeoPoint
index|[
name|count
index|]
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
index|[]
name|pointArray
range|:
name|pointArrays
control|)
block|{
for|for
control|(
specifier|final
name|GeoPoint
name|point
range|:
name|pointArray
control|)
block|{
name|rval
index|[
name|count
operator|++
index|]
operator|=
name|point
expr_stmt|;
block|}
block|}
return|return
name|rval
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|Vector
name|point
parameter_list|)
block|{
return|return
name|isWithin
argument_list|(
name|point
operator|.
name|x
argument_list|,
name|point
operator|.
name|y
argument_list|,
name|point
operator|.
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
specifier|abstract
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
function_decl|;
comment|// Signals for relationship of edge points to shape
comment|/** All edgepoints inside shape */
DECL|field|ALL_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|ALL_INSIDE
init|=
literal|0
decl_stmt|;
comment|/** Some edgepoints inside shape */
DECL|field|SOME_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|SOME_INSIDE
init|=
literal|1
decl_stmt|;
comment|/** No edgepoints inside shape */
DECL|field|NONE_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|NONE_INSIDE
init|=
literal|2
decl_stmt|;
comment|/** No edgepoints at all (means a shape that is the whole world) */
DECL|field|NO_EDGEPOINTS
specifier|protected
specifier|final
specifier|static
name|int
name|NO_EDGEPOINTS
init|=
literal|3
decl_stmt|;
comment|/** Determine the relationship between this area and the provided    * shape's edgepoints.    *@param path is the shape.    *@return the relationship.    */
DECL|method|isShapeInsideArea
specifier|protected
name|int
name|isShapeInsideArea
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
specifier|final
name|GeoPoint
index|[]
name|pathPoints
init|=
name|path
operator|.
name|getEdgePoints
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathPoints
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|NO_EDGEPOINTS
return|;
name|boolean
name|foundOutside
init|=
literal|false
decl_stmt|;
name|boolean
name|foundInside
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
name|p
range|:
name|pathPoints
control|)
block|{
if|if
condition|(
name|isWithin
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|foundInside
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|foundOutside
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|foundInside
operator|&&
name|foundOutside
condition|)
block|{
return|return
name|SOME_INSIDE
return|;
block|}
block|}
if|if
condition|(
operator|!
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|NONE_INSIDE
return|;
if|if
condition|(
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|ALL_INSIDE
return|;
if|if
condition|(
name|foundOutside
operator|&&
operator|!
name|foundInside
condition|)
return|return
name|NONE_INSIDE
return|;
return|return
name|SOME_INSIDE
return|;
block|}
comment|/** Determine the relationship between a shape and this area's    * edgepoints.    *@param path is the shape.    *@return the relationship.    */
DECL|method|isAreaInsideShape
specifier|protected
name|int
name|isAreaInsideShape
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
specifier|final
name|GeoPoint
index|[]
name|edgePoints
init|=
name|getEdgePoints
argument_list|()
decl_stmt|;
if|if
condition|(
name|edgePoints
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|NO_EDGEPOINTS
return|;
block|}
name|boolean
name|foundOutside
init|=
literal|false
decl_stmt|;
name|boolean
name|foundInside
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
name|p
range|:
name|edgePoints
control|)
block|{
if|if
condition|(
name|path
operator|.
name|isWithin
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|foundInside
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|foundOutside
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|foundInside
operator|&&
name|foundOutside
condition|)
block|{
return|return
name|SOME_INSIDE
return|;
block|}
block|}
if|if
condition|(
operator|!
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|NONE_INSIDE
return|;
if|if
condition|(
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|ALL_INSIDE
return|;
if|if
condition|(
name|foundOutside
operator|&&
operator|!
name|foundInside
condition|)
return|return
name|NONE_INSIDE
return|;
return|return
name|SOME_INSIDE
return|;
block|}
comment|/** Get the edge points for this shape.    *@return the edge points.    */
DECL|method|getEdgePoints
specifier|protected
specifier|abstract
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
function_decl|;
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
name|BaseXYZSolid
operator|)
condition|)
return|return
literal|false
return|;
name|BaseXYZSolid
name|other
init|=
operator|(
name|BaseXYZSolid
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
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
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

