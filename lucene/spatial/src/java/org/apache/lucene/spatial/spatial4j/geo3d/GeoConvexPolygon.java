begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * GeoConvexPolygon objects are generic building blocks of more complex structures.  * The only restrictions on these objects are: (1) they must be convex; (2) they must have  * a maximum extent no larger than PI.  Violating either one of these limits will  * cause the logic to fail.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoConvexPolygon
specifier|public
class|class
name|GeoConvexPolygon
extends|extends
name|GeoBaseExtendedShape
implements|implements
name|GeoMembershipShape
block|{
DECL|field|points
specifier|protected
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
decl_stmt|;
DECL|field|isInternalEdges
specifier|protected
specifier|final
name|BitSet
name|isInternalEdges
decl_stmt|;
DECL|field|edges
specifier|protected
name|SidedPlane
index|[]
name|edges
init|=
literal|null
decl_stmt|;
DECL|field|internalEdges
specifier|protected
name|boolean
index|[]
name|internalEdges
init|=
literal|null
decl_stmt|;
DECL|field|notableEdgePoints
specifier|protected
name|GeoPoint
index|[]
index|[]
name|notableEdgePoints
init|=
literal|null
decl_stmt|;
DECL|field|edgePoints
specifier|protected
name|GeoPoint
index|[]
name|edgePoints
init|=
literal|null
decl_stmt|;
DECL|field|fullDistance
specifier|protected
name|double
name|fullDistance
init|=
literal|0.0
decl_stmt|;
comment|/**    * Create a convex polygon from a list of points.  The first point must be on the    * external edge.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|)
block|{
name|this
operator|.
name|points
operator|=
name|pointList
expr_stmt|;
name|this
operator|.
name|isInternalEdges
operator|=
literal|null
expr_stmt|;
name|donePoints
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon from a list of points, keeping track of which boundaries    * are internal.  This is used when creating a polygon as a building block for another shape.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|,
specifier|final
name|BitSet
name|internalEdgeFlags
parameter_list|,
specifier|final
name|boolean
name|returnEdgeInternal
parameter_list|)
block|{
name|this
operator|.
name|points
operator|=
name|pointList
expr_stmt|;
name|this
operator|.
name|isInternalEdges
operator|=
name|internalEdgeFlags
expr_stmt|;
name|donePoints
argument_list|(
name|returnEdgeInternal
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon, with a starting latitude and longitude.    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|double
name|startLatitude
parameter_list|,
specifier|final
name|double
name|startLongitude
parameter_list|)
block|{
name|points
operator|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
expr_stmt|;
name|isInternalEdges
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
comment|// Argument checking
if|if
condition|(
name|startLatitude
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|startLatitude
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|startLongitude
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|startLongitude
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of range"
argument_list|)
throw|;
specifier|final
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|(
name|startLatitude
argument_list|,
name|startLongitude
argument_list|)
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a point to the polygon.    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}    *    * @param latitude       is the latitude of the next point.    * @param longitude      is the longitude of the next point.    * @param isInternalEdge is true if the edge just added should be considered "internal", and not    *                       intersected as part of the intersects() operation.    */
DECL|method|addPoint
specifier|public
name|void
name|addPoint
parameter_list|(
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|boolean
name|isInternalEdge
parameter_list|)
block|{
comment|// Argument checking
if|if
condition|(
name|latitude
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|latitude
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|longitude
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|longitude
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of range"
argument_list|)
throw|;
specifier|final
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
decl_stmt|;
name|isInternalEdges
operator|.
name|set
argument_list|(
name|points
operator|.
name|size
argument_list|()
argument_list|,
name|isInternalEdge
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish the polygon, by connecting the last added point with the starting point.    */
DECL|method|donePoints
specifier|public
name|void
name|donePoints
parameter_list|(
specifier|final
name|boolean
name|isInternalReturnEdge
parameter_list|)
block|{
comment|// If fewer than 3 points, can't do it.
if|if
condition|(
name|points
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon needs at least three points."
argument_list|)
throw|;
comment|// Time to construct the planes.  If the polygon is truly convex, then any adjacent point
comment|// to a segment can provide an interior measurement.
name|edges
operator|=
operator|new
name|SidedPlane
index|[
name|points
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|notableEdgePoints
operator|=
operator|new
name|GeoPoint
index|[
name|points
operator|.
name|size
argument_list|()
index|]
index|[]
expr_stmt|;
name|internalEdges
operator|=
operator|new
name|boolean
index|[
name|points
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|GeoPoint
name|start
init|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isInternalEdge
init|=
operator|(
name|isInternalEdges
operator|!=
literal|null
condition|?
operator|(
name|i
operator|==
name|isInternalEdges
operator|.
name|size
argument_list|()
condition|?
name|isInternalReturnEdge
else|:
name|isInternalEdges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
else|:
literal|false
operator|)
decl_stmt|;
specifier|final
name|GeoPoint
name|end
init|=
name|points
operator|.
name|get
argument_list|(
name|legalIndex
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|distance
init|=
name|start
operator|.
name|arcDistance
argument_list|(
name|end
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|>
name|fullDistance
condition|)
name|fullDistance
operator|=
name|distance
expr_stmt|;
specifier|final
name|GeoPoint
name|check
init|=
name|points
operator|.
name|get
argument_list|(
name|legalIndex
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SidedPlane
name|sp
init|=
operator|new
name|SidedPlane
argument_list|(
name|check
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
comment|//System.out.println("Created edge "+sp+" using start="+start+" end="+end+" check="+check);
name|edges
index|[
name|i
index|]
operator|=
name|sp
expr_stmt|;
name|notableEdgePoints
index|[
name|i
index|]
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|start
block|,
name|end
block|}
expr_stmt|;
name|internalEdges
index|[
name|i
index|]
operator|=
name|isInternalEdge
expr_stmt|;
block|}
name|createCenterPoint
argument_list|()
expr_stmt|;
block|}
DECL|method|createCenterPoint
specifier|protected
name|void
name|createCenterPoint
parameter_list|()
block|{
comment|// In order to naively confirm that the polygon is convex, I would need to
comment|// check every edge, and verify that every point (other than the edge endpoints)
comment|// is within the edge's sided plane.  This is an order n^2 operation.  That's still
comment|// not wrong, though, because everything else about polygons has a similar cost.
for|for
control|(
name|int
name|edgeIndex
init|=
literal|0
init|;
name|edgeIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|edgeIndex
operator|++
control|)
block|{
specifier|final
name|SidedPlane
name|edge
init|=
name|edges
index|[
name|edgeIndex
index|]
decl_stmt|;
for|for
control|(
name|int
name|pointIndex
init|=
literal|0
init|;
name|pointIndex
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|pointIndex
operator|++
control|)
block|{
if|if
condition|(
name|pointIndex
operator|!=
name|edgeIndex
operator|&&
name|pointIndex
operator|!=
name|legalIndex
argument_list|(
name|edgeIndex
operator|+
literal|1
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|points
operator|.
name|get
argument_list|(
name|pointIndex
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon is not convex: Point "
operator|+
name|points
operator|.
name|get
argument_list|(
name|pointIndex
argument_list|)
operator|+
literal|" Edge "
operator|+
name|edge
argument_list|)
throw|;
block|}
block|}
block|}
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|points
operator|.
name|get
argument_list|(
literal|0
argument_list|)
block|}
expr_stmt|;
block|}
DECL|method|legalIndex
specifier|protected
name|int
name|legalIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
while|while
condition|(
name|index
operator|>=
name|points
operator|.
name|size
argument_list|()
condition|)
name|index
operator|-=
name|points
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|index
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
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
if|if
condition|(
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
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
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
if|if
condition|(
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
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
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
comment|//System.err.println("Checking for polygon intersection with plane "+p+"...");
for|for
control|(
name|int
name|edgeIndex
init|=
literal|0
init|;
name|edgeIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|edgeIndex
operator|++
control|)
block|{
specifier|final
name|SidedPlane
name|edge
init|=
name|edges
index|[
name|edgeIndex
index|]
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|points
init|=
name|this
operator|.
name|notableEdgePoints
index|[
name|edgeIndex
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|internalEdges
index|[
name|edgeIndex
index|]
condition|)
block|{
comment|//System.err.println(" non-internal edge "+edge);
comment|// Edges flagged as 'internal only' are excluded from the matching
comment|// Construct boundaries
specifier|final
name|Membership
index|[]
name|membershipBounds
init|=
operator|new
name|Membership
index|[
name|edges
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|otherIndex
init|=
literal|0
init|;
name|otherIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|otherIndex
operator|++
control|)
block|{
if|if
condition|(
name|otherIndex
operator|!=
name|edgeIndex
condition|)
block|{
name|membershipBounds
index|[
name|count
operator|++
index|]
operator|=
name|edges
index|[
name|otherIndex
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|edge
operator|.
name|intersects
argument_list|(
name|p
argument_list|,
name|notablePoints
argument_list|,
name|points
argument_list|,
name|bounds
argument_list|,
name|membershipBounds
argument_list|)
condition|)
block|{
comment|//System.err.println(" intersects!");
return|return
literal|true
return|;
block|}
block|}
block|}
comment|//System.err.println(" no intersection");
return|return
literal|false
return|;
block|}
comment|/**    * Compute longitude/latitude bounds for the shape.    *    * @param bounds is the optional input bounds object.  If this is null,    *               a bounds object will be created.  Otherwise, the input object will be modified.    * @return a Bounds object describing the shape's bounds.  If the bounds cannot    * be computed, then return a Bounds object with noLongitudeBound,    * noTopLatitudeBound, and noBottomLatitudeBound.    */
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|Bounds
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|bounds
operator|=
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
comment|// Add all the points
for|for
control|(
specifier|final
name|GeoPoint
name|point
range|:
name|points
control|)
block|{
name|bounds
operator|.
name|addPoint
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
comment|// Add planes with membership.
for|for
control|(
name|int
name|edgeIndex
init|=
literal|0
init|;
name|edgeIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|edgeIndex
operator|++
control|)
block|{
specifier|final
name|SidedPlane
name|edge
init|=
name|edges
index|[
name|edgeIndex
index|]
decl_stmt|;
comment|// Construct boundaries
specifier|final
name|Membership
index|[]
name|membershipBounds
init|=
operator|new
name|Membership
index|[
name|edges
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|otherIndex
init|=
literal|0
init|;
name|otherIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|otherIndex
operator|++
control|)
block|{
if|if
condition|(
name|otherIndex
operator|!=
name|edgeIndex
condition|)
block|{
name|membershipBounds
index|[
name|count
operator|++
index|]
operator|=
name|edges
index|[
name|otherIndex
index|]
expr_stmt|;
block|}
block|}
name|edge
operator|.
name|recordBounds
argument_list|(
name|bounds
argument_list|,
name|membershipBounds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fullDistance
operator|>=
name|Math
operator|.
name|PI
condition|)
block|{
comment|// We can't reliably assume that bounds did its longitude calculation right, so we force it to be unbounded.
name|bounds
operator|.
name|noLongitudeBound
argument_list|()
expr_stmt|;
block|}
return|return
name|bounds
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
name|GeoConvexPolygon
operator|)
condition|)
return|return
literal|false
return|;
name|GeoConvexPolygon
name|other
init|=
operator|(
name|GeoConvexPolygon
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|points
operator|.
name|size
argument_list|()
operator|!=
name|points
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|other
operator|.
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
return|return
literal|false
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
return|return
name|points
operator|.
name|hashCode
argument_list|()
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
name|StringBuilder
name|edgeString
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|edges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|edgeString
operator|.
name|append
argument_list|(
name|edges
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|" internal? "
argument_list|)
operator|.
name|append
argument_list|(
name|internalEdges
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|edgeString
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
literal|"GeoConvexPolygon: {points="
operator|+
name|points
operator|+
literal|" edges="
operator|+
name|edgeString
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

