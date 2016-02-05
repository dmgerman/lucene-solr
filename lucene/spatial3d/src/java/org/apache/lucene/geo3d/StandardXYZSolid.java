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
comment|/**  * 3D rectangle, bounded on six sides by X,Y,Z limits  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|StandardXYZSolid
specifier|public
class|class
name|StandardXYZSolid
extends|extends
name|BaseXYZSolid
block|{
comment|/** Whole world? */
DECL|field|isWholeWorld
specifier|protected
specifier|final
name|boolean
name|isWholeWorld
decl_stmt|;
comment|/** Min-X plane */
DECL|field|minXPlane
specifier|protected
specifier|final
name|SidedPlane
name|minXPlane
decl_stmt|;
comment|/** Max-X plane */
DECL|field|maxXPlane
specifier|protected
specifier|final
name|SidedPlane
name|maxXPlane
decl_stmt|;
comment|/** Min-Y plane */
DECL|field|minYPlane
specifier|protected
specifier|final
name|SidedPlane
name|minYPlane
decl_stmt|;
comment|/** Max-Y plane */
DECL|field|maxYPlane
specifier|protected
specifier|final
name|SidedPlane
name|maxYPlane
decl_stmt|;
comment|/** Min-Z plane */
DECL|field|minZPlane
specifier|protected
specifier|final
name|SidedPlane
name|minZPlane
decl_stmt|;
comment|/** Max-Z plane */
DECL|field|maxZPlane
specifier|protected
specifier|final
name|SidedPlane
name|maxZPlane
decl_stmt|;
comment|/** These are the edge points of the shape, which are defined to be at least one point on    * each surface area boundary.  In the case of a solid, this includes points which represent    * the intersection of XYZ bounding planes and the planet, as well as points representing    * the intersection of single bounding planes with the planet itself.    */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/** Notable points for minXPlane */
DECL|field|notableMinXPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMinXPoints
decl_stmt|;
comment|/** Notable points for maxXPlane */
DECL|field|notableMaxXPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMaxXPoints
decl_stmt|;
comment|/** Notable points for minYPlane */
DECL|field|notableMinYPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMinYPoints
decl_stmt|;
comment|/** Notable points for maxYPlane */
DECL|field|notableMaxYPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMaxYPoints
decl_stmt|;
comment|/** Notable points for minZPlane */
DECL|field|notableMinZPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMinZPoints
decl_stmt|;
comment|/** Notable points for maxZPlane */
DECL|field|notableMaxZPoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|notableMaxZPoints
decl_stmt|;
comment|/**    * Sole constructor    *    *@param planetModel is the planet model.    *@param minX is the minimum X value.    *@param maxX is the maximum X value.    *@param minY is the minimum Y value.    *@param maxY is the maximum Y value.    *@param minZ is the minimum Z value.    *@param maxZ is the maximum Z value.    */
DECL|method|StandardXYZSolid
specifier|public
name|StandardXYZSolid
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|minX
parameter_list|,
specifier|final
name|double
name|maxX
parameter_list|,
specifier|final
name|double
name|minY
parameter_list|,
specifier|final
name|double
name|maxY
parameter_list|,
specifier|final
name|double
name|minZ
parameter_list|,
specifier|final
name|double
name|maxZ
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
comment|// Argument checking
if|if
condition|(
name|maxX
operator|-
name|minX
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"X values in wrong order or identical"
argument_list|)
throw|;
if|if
condition|(
name|maxY
operator|-
name|minY
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Y values in wrong order or identical"
argument_list|)
throw|;
if|if
condition|(
name|maxZ
operator|-
name|minZ
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Z values in wrong order or identical"
argument_list|)
throw|;
specifier|final
name|double
name|worldMinX
init|=
name|planetModel
operator|.
name|getMinimumXValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|worldMaxX
init|=
name|planetModel
operator|.
name|getMaximumXValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|worldMinY
init|=
name|planetModel
operator|.
name|getMinimumYValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|worldMaxY
init|=
name|planetModel
operator|.
name|getMaximumYValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|worldMinZ
init|=
name|planetModel
operator|.
name|getMinimumZValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|worldMaxZ
init|=
name|planetModel
operator|.
name|getMaximumZValue
argument_list|()
decl_stmt|;
comment|// We must distinguish between the case where the solid represents the entire world,
comment|// and when the solid has no overlap with any part of the surface.  In both cases,
comment|// there will be no edgepoints.
name|isWholeWorld
operator|=
operator|(
name|minX
operator|-
name|worldMinX
operator|<
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
operator|&&
operator|(
name|maxX
operator|-
name|worldMaxX
operator|>
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
operator|&&
operator|(
name|minY
operator|-
name|worldMinY
operator|<
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
operator|&&
operator|(
name|maxY
operator|-
name|worldMaxY
operator|>
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
operator|&&
operator|(
name|minZ
operator|-
name|worldMinZ
operator|<
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
operator|&&
operator|(
name|maxZ
operator|-
name|worldMaxZ
operator|>
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|)
expr_stmt|;
if|if
condition|(
name|isWholeWorld
condition|)
block|{
name|minXPlane
operator|=
literal|null
expr_stmt|;
name|maxXPlane
operator|=
literal|null
expr_stmt|;
name|minYPlane
operator|=
literal|null
expr_stmt|;
name|maxYPlane
operator|=
literal|null
expr_stmt|;
name|minZPlane
operator|=
literal|null
expr_stmt|;
name|maxZPlane
operator|=
literal|null
expr_stmt|;
name|notableMinXPoints
operator|=
literal|null
expr_stmt|;
name|notableMaxXPoints
operator|=
literal|null
expr_stmt|;
name|notableMinYPoints
operator|=
literal|null
expr_stmt|;
name|notableMaxYPoints
operator|=
literal|null
expr_stmt|;
name|notableMinZPoints
operator|=
literal|null
expr_stmt|;
name|notableMaxZPoints
operator|=
literal|null
expr_stmt|;
name|edgePoints
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// Construct the planes
name|minXPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|maxX
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|xUnitVector
argument_list|,
operator|-
name|minX
argument_list|)
expr_stmt|;
name|maxXPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|minX
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|xUnitVector
argument_list|,
operator|-
name|maxX
argument_list|)
expr_stmt|;
name|minYPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
name|maxY
argument_list|,
literal|0.0
argument_list|,
name|yUnitVector
argument_list|,
operator|-
name|minY
argument_list|)
expr_stmt|;
name|maxYPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
name|minY
argument_list|,
literal|0.0
argument_list|,
name|yUnitVector
argument_list|,
operator|-
name|maxY
argument_list|)
expr_stmt|;
name|minZPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|maxZ
argument_list|,
name|zUnitVector
argument_list|,
operator|-
name|minZ
argument_list|)
expr_stmt|;
name|maxZPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|minZ
argument_list|,
name|zUnitVector
argument_list|,
operator|-
name|maxZ
argument_list|)
expr_stmt|;
comment|// We need at least one point on the planet surface for each manifestation of the shape.
comment|// There can be up to 2 (on opposite sides of the world).  But we have to go through
comment|// 12 combinations of adjacent planes in order to find out if any have 2 intersection solution.
comment|// Typically, this requires 12 square root operations.
specifier|final
name|GeoPoint
index|[]
name|minXminY
init|=
name|minXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minYPlane
argument_list|,
name|maxXPlane
argument_list|,
name|maxYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|minXmaxY
init|=
name|minXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxYPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|minXminZ
init|=
name|minXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minZPlane
argument_list|,
name|maxXPlane
argument_list|,
name|maxZPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|minXmaxZ
init|=
name|minXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxZPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minZPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxXminY
init|=
name|maxXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minYPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxXmaxY
init|=
name|maxXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxYPlane
argument_list|,
name|minXPlane
argument_list|,
name|minYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxXminZ
init|=
name|maxXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxZPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxXmaxZ
init|=
name|maxXPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxZPlane
argument_list|,
name|minXPlane
argument_list|,
name|minZPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|minYminZ
init|=
name|minYPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minZPlane
argument_list|,
name|maxYPlane
argument_list|,
name|maxZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|minYmaxZ
init|=
name|minYPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxZPlane
argument_list|,
name|maxYPlane
argument_list|,
name|minZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxYminZ
init|=
name|maxYPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|minZPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|maxYmaxZ
init|=
name|maxYPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|maxZPlane
argument_list|,
name|minYPlane
argument_list|,
name|minZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|)
decl_stmt|;
name|notableMinXPoints
operator|=
name|glueTogether
argument_list|(
name|minXminY
argument_list|,
name|minXmaxY
argument_list|,
name|minXminZ
argument_list|,
name|minXmaxZ
argument_list|)
expr_stmt|;
name|notableMaxXPoints
operator|=
name|glueTogether
argument_list|(
name|maxXminY
argument_list|,
name|maxXmaxY
argument_list|,
name|maxXminZ
argument_list|,
name|maxXmaxZ
argument_list|)
expr_stmt|;
name|notableMinYPoints
operator|=
name|glueTogether
argument_list|(
name|minXminY
argument_list|,
name|maxXminY
argument_list|,
name|minYminZ
argument_list|,
name|minYmaxZ
argument_list|)
expr_stmt|;
name|notableMaxYPoints
operator|=
name|glueTogether
argument_list|(
name|minXmaxY
argument_list|,
name|maxXmaxY
argument_list|,
name|maxYminZ
argument_list|,
name|maxYmaxZ
argument_list|)
expr_stmt|;
name|notableMinZPoints
operator|=
name|glueTogether
argument_list|(
name|minXminZ
argument_list|,
name|maxXminZ
argument_list|,
name|minYminZ
argument_list|,
name|maxYminZ
argument_list|)
expr_stmt|;
name|notableMaxZPoints
operator|=
name|glueTogether
argument_list|(
name|minXmaxZ
argument_list|,
name|maxXmaxZ
argument_list|,
name|minYmaxZ
argument_list|,
name|maxYmaxZ
argument_list|)
expr_stmt|;
comment|// Now, compute the edge points.
comment|// This is the trickiest part of setting up an XYZSolid.  We've computed intersections already, so
comment|// we'll start there.
comment|// There can be a number of shapes, each of which needs an edgepoint.  Each side by itself might contribute
comment|// an edgepoint, for instance, if the plane describing that side intercepts the planet in such a way that the ellipse
comment|// of interception does not meet any other planes.  Plane intersections can each contribute 0, 1, or 2 edgepoints.
comment|//
comment|// All of this makes for a lot of potential edgepoints, but I believe these can be pruned back with careful analysis.
comment|// I haven't yet done that analysis, however, so I will treat them all as individual edgepoints.
comment|// The cases we are looking for are when the four corner points for any given
comment|// plane are all outside of the world, AND that plane intersects the world.
comment|// There are eight corner points all told; we must evaluate these WRT the planet surface.
specifier|final
name|boolean
name|minXminYminZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|minX
argument_list|,
name|minY
argument_list|,
name|minZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|minXminYmaxZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|minX
argument_list|,
name|minY
argument_list|,
name|maxZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|minXmaxYminZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|minX
argument_list|,
name|maxY
argument_list|,
name|minZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|minXmaxYmaxZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|minX
argument_list|,
name|maxY
argument_list|,
name|maxZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|maxXminYminZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|maxX
argument_list|,
name|minY
argument_list|,
name|minZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|maxXminYmaxZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|maxX
argument_list|,
name|minY
argument_list|,
name|maxZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|maxXmaxYminZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|maxX
argument_list|,
name|maxY
argument_list|,
name|minZ
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|maxXmaxYmaxZ
init|=
name|planetModel
operator|.
name|pointOutside
argument_list|(
name|maxX
argument_list|,
name|maxY
argument_list|,
name|maxZ
argument_list|)
decl_stmt|;
comment|// Look at single-plane/world intersections.
comment|// We detect these by looking at the world model and noting its x, y, and z bounds.
specifier|final
name|GeoPoint
index|[]
name|minXEdges
decl_stmt|;
if|if
condition|(
name|minX
operator|-
name|worldMinX
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minX
operator|-
name|worldMaxX
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minY
argument_list|<
literal|0.0
operator|&&
name|maxY
argument_list|>
literal|0.0
operator|&&
name|minZ
argument_list|<
literal|0.0
operator|&&
name|maxZ
argument_list|>
literal|0.0
operator|&&
name|minXminYminZ
operator|&&
name|minXminYmaxZ
operator|&&
name|minXmaxYminZ
operator|&&
name|minXmaxYmaxZ
condition|)
block|{
comment|// Find any point on the minX plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (1,0,0)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|minXPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|xVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|minXEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// No intersection found?
name|minXEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|minXEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|maxXEdges
decl_stmt|;
if|if
condition|(
name|maxX
operator|-
name|worldMinX
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|maxX
operator|-
name|worldMaxX
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minY
argument_list|<
literal|0.0
operator|&&
name|maxY
argument_list|>
literal|0.0
operator|&&
name|minZ
argument_list|<
literal|0.0
operator|&&
name|maxZ
argument_list|>
literal|0.0
operator|&&
name|maxXminYminZ
operator|&&
name|maxXminYmaxZ
operator|&&
name|maxXmaxYminZ
operator|&&
name|maxXmaxYmaxZ
condition|)
block|{
comment|// Find any point on the maxX plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (1,0,0)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|maxXPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|xVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|maxXEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
name|maxXEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|maxXEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|minYEdges
decl_stmt|;
if|if
condition|(
name|minY
operator|-
name|worldMinY
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minY
operator|-
name|worldMaxY
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minX
argument_list|<
literal|0.0
operator|&&
name|maxX
argument_list|>
literal|0.0
operator|&&
name|minZ
argument_list|<
literal|0.0
operator|&&
name|maxZ
argument_list|>
literal|0.0
operator|&&
name|minXminYminZ
operator|&&
name|minXminYmaxZ
operator|&&
name|maxXminYminZ
operator|&&
name|maxXminYmaxZ
condition|)
block|{
comment|// Find any point on the minY plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (0,1,0)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|minYPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|yVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|minYEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
name|minYEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|minYEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|maxYEdges
decl_stmt|;
if|if
condition|(
name|maxY
operator|-
name|worldMinY
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|maxY
operator|-
name|worldMaxY
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minX
argument_list|<
literal|0.0
operator|&&
name|maxX
argument_list|>
literal|0.0
operator|&&
name|minZ
argument_list|<
literal|0.0
operator|&&
name|maxZ
argument_list|>
literal|0.0
operator|&&
name|minXmaxYminZ
operator|&&
name|minXmaxYmaxZ
operator|&&
name|maxXmaxYminZ
operator|&&
name|maxXmaxYmaxZ
condition|)
block|{
comment|// Find any point on the maxY plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (0,1,0)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|maxYPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|yVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|maxYEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
name|maxYEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|maxYEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|minZEdges
decl_stmt|;
if|if
condition|(
name|minZ
operator|-
name|worldMinZ
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minZ
operator|-
name|worldMaxZ
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minX
argument_list|<
literal|0.0
operator|&&
name|maxX
argument_list|>
literal|0.0
operator|&&
name|minY
argument_list|<
literal|0.0
operator|&&
name|maxY
argument_list|>
literal|0.0
operator|&&
name|minXminYminZ
operator|&&
name|minXmaxYminZ
operator|&&
name|maxXminYminZ
operator|&&
name|maxXmaxYminZ
condition|)
block|{
comment|// Find any point on the minZ plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (1,0,0)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|minZPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|xVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|minZEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
name|minZEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|minZEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
specifier|final
name|GeoPoint
index|[]
name|maxZEdges
decl_stmt|;
if|if
condition|(
name|maxZ
operator|-
name|worldMinZ
operator|>=
operator|-
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|maxZ
operator|-
name|worldMaxZ
operator|<=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|minX
argument_list|<
literal|0.0
operator|&&
name|maxX
argument_list|>
literal|0.0
operator|&&
name|minY
argument_list|<
literal|0.0
operator|&&
name|maxY
argument_list|>
literal|0.0
operator|&&
name|minXminYmaxZ
operator|&&
name|minXmaxYmaxZ
operator|&&
name|maxXminYmaxZ
operator|&&
name|maxXmaxYmaxZ
condition|)
block|{
comment|// Find any point on the maxZ plane that intersects the world
comment|// First construct a perpendicular plane that will allow us to find a sample point.
comment|// This plane is vertical and goes through the points (0,0,0) and (1,0,0) (that is, its orientation doesn't matter)
comment|// Then use it to compute a sample point.
specifier|final
name|GeoPoint
name|intPoint
init|=
name|maxZPlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|xVerticalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|intPoint
operator|!=
literal|null
condition|)
block|{
name|maxZEdges
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|intPoint
block|}
expr_stmt|;
block|}
else|else
block|{
name|maxZEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
block|}
else|else
block|{
name|maxZEdges
operator|=
name|EMPTY_POINTS
expr_stmt|;
block|}
comment|// Glue everything together.  This is not a minimal set of edgepoints, as of now, but it does completely describe all shapes on the
comment|// planet.
name|this
operator|.
name|edgePoints
operator|=
name|glueTogether
argument_list|(
name|minXminY
argument_list|,
name|minXmaxY
argument_list|,
name|minXminZ
argument_list|,
name|minXmaxZ
argument_list|,
name|maxXminY
argument_list|,
name|maxXmaxY
argument_list|,
name|maxXminZ
argument_list|,
name|maxXmaxZ
argument_list|,
name|minYminZ
argument_list|,
name|minYmaxZ
argument_list|,
name|maxYminZ
argument_list|,
name|maxYmaxZ
argument_list|,
name|minXEdges
argument_list|,
name|maxXEdges
argument_list|,
name|minYEdges
argument_list|,
name|maxYEdges
argument_list|,
name|minZEdges
argument_list|,
name|maxZEdges
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|protected
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
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
block|{
if|if
condition|(
name|isWholeWorld
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|minXPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|maxXPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|minYPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|maxYPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|minZPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|maxZPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
if|if
condition|(
name|isWholeWorld
condition|)
block|{
if|if
condition|(
name|path
operator|.
name|getEdgePoints
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
return|return
name|WITHIN
return|;
return|return
name|OVERLAPS
return|;
block|}
comment|/*     for (GeoPoint p : getEdgePoints()) {       System.err.println(" Edge point "+p+" path.isWithin()? "+path.isWithin(p));     }          for (GeoPoint p : path.getEdgePoints()) {       System.err.println(" path edge point "+p+" isWithin()? "+isWithin(p)+" minx="+minXPlane.evaluate(p)+" maxx="+maxXPlane.evaluate(p)+" miny="+minYPlane.evaluate(p)+" maxy="+maxYPlane.evaluate(p)+" minz="+minZPlane.evaluate(p)+" maxz="+maxZPlane.evaluate(p));     }     */
comment|//System.err.println(this+" getrelationship with "+path);
specifier|final
name|int
name|insideRectangle
init|=
name|isShapeInsideArea
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideRectangle
operator|==
name|SOME_INSIDE
condition|)
block|{
comment|//System.err.println(" some shape points inside area");
return|return
name|OVERLAPS
return|;
block|}
comment|// Figure out if the entire XYZArea is contained by the shape.
specifier|final
name|int
name|insideShape
init|=
name|isAreaInsideShape
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideShape
operator|==
name|SOME_INSIDE
condition|)
block|{
comment|//System.err.println(" some area points inside shape");
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
operator|&&
name|insideShape
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" inside of each other");
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|minXPlane
argument_list|,
name|notableMinXPoints
argument_list|,
name|maxXPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|maxXPlane
argument_list|,
name|notableMaxXPoints
argument_list|,
name|minXPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|minYPlane
argument_list|,
name|notableMinYPoints
argument_list|,
name|maxYPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|maxYPlane
argument_list|,
name|notableMaxYPoints
argument_list|,
name|minYPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minZPlane
argument_list|,
name|maxZPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|minZPlane
argument_list|,
name|notableMinZPoints
argument_list|,
name|maxZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|maxZPlane
argument_list|,
name|notableMaxZPoints
argument_list|,
name|minZPlane
argument_list|,
name|minXPlane
argument_list|,
name|maxXPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
condition|)
block|{
comment|//System.err.println(" edges intersect");
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" all shape points inside area");
return|return
name|WITHIN
return|;
block|}
if|if
condition|(
name|insideShape
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" all area points inside shape");
return|return
name|CONTAINS
return|;
block|}
comment|//System.err.println(" disjoint");
return|return
name|DISJOINT
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
name|StandardXYZSolid
operator|)
condition|)
return|return
literal|false
return|;
name|StandardXYZSolid
name|other
init|=
operator|(
name|StandardXYZSolid
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|||
name|other
operator|.
name|isWholeWorld
operator|!=
name|isWholeWorld
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isWholeWorld
condition|)
block|{
return|return
name|other
operator|.
name|minXPlane
operator|.
name|equals
argument_list|(
name|minXPlane
argument_list|)
operator|&&
name|other
operator|.
name|maxXPlane
operator|.
name|equals
argument_list|(
name|maxXPlane
argument_list|)
operator|&&
name|other
operator|.
name|minYPlane
operator|.
name|equals
argument_list|(
name|minYPlane
argument_list|)
operator|&&
name|other
operator|.
name|maxYPlane
operator|.
name|equals
argument_list|(
name|maxYPlane
argument_list|)
operator|&&
name|other
operator|.
name|minZPlane
operator|.
name|equals
argument_list|(
name|minZPlane
argument_list|)
operator|&&
name|other
operator|.
name|maxZPlane
operator|.
name|equals
argument_list|(
name|maxZPlane
argument_list|)
return|;
block|}
return|return
literal|true
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|isWholeWorld
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
if|if
condition|(
operator|!
name|isWholeWorld
condition|)
block|{
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|minXPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|maxXPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|minYPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|maxYPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|minZPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|maxZPlane
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|result
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
literal|"StandardXYZSolid: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", isWholeWorld="
operator|+
name|isWholeWorld
operator|+
literal|", minXplane="
operator|+
name|minXPlane
operator|+
literal|", maxXplane="
operator|+
name|maxXPlane
operator|+
literal|", minYplane="
operator|+
name|minYPlane
operator|+
literal|", maxYplane="
operator|+
name|maxYPlane
operator|+
literal|", minZplane="
operator|+
name|minZPlane
operator|+
literal|", maxZplane="
operator|+
name|maxZPlane
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

