begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package

begin_comment
comment|/**  * An implementer of this interface is capable of computing the described "distance" values,  * which are meant to provide both actual distance values, as well as  * distance estimates that can be computed more cheaply.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|GeoDistance
specifier|public
interface|interface
name|GeoDistance
extends|extends
name|Membership
block|{
comment|// The following methods compute distances from the shape to a point
comment|// expected to be INSIDE the shape.  Typically a value of Double.MAX_VALUE
comment|// is returned for points that happen to be outside the shape.
comment|/**    * Compute this shape's<em>internal</em> "distance" to the GeoPoint.    * Implementations should clarify how this is computed when it's non-obvious.    * A return value of Double.MAX_VALUE should be returned for    * points outside of the shape.    *    * @param distanceStyle is the distance style.    * @param point is the point to compute the distance to.    * @return the distance.    */
DECL|method|computeDistance
specifier|public
specifier|default
name|double
name|computeDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|computeDistance
argument_list|(
name|distanceStyle
argument_list|,
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
comment|/**    * Compute this shape's<em>internal</em> "distance" to the GeoPoint.    * Implementations should clarify how this is computed when it's non-obvious.    * A return value of Double.MAX_VALUE should be returned for    * points outside of the shape.    *    * @param x is the point's unit x coordinate (using U.S. convention).    * @param y is the point's unit y coordinate (using U.S. convention).    * @param z is the point's unit z coordinate (using U.S. convention).    * @return the distance.    */
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
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
block|}
end_interface

end_unit

