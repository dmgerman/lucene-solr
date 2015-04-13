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

begin_comment
comment|/** Generic shape.  This describes methods that help GeoAreas figure out * how they interact with a shape, for the purposes of coming up with a * set of geo hash values. */
end_comment

begin_interface
DECL|interface|GeoShape
specifier|public
interface|interface
name|GeoShape
extends|extends
name|Membership
block|{
comment|/** Return a sample point that is inside the shape.      *@return an interior point.      */
DECL|method|getInteriorPoint
specifier|public
name|GeoPoint
name|getInteriorPoint
parameter_list|()
function_decl|;
comment|/** Assess whether a plane, within the provided bounds, intersects      * with the shape.      *@param plane is the plane to assess for intersection with the shape's edges or      *  bounding curves.      *@param bounds are a set of bounds that define an area that an      *  intersection must be within in order to qualify (provided by a GeoArea).      *@return true if there's such an intersection, false if not.      */
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|Plane
name|plane
parameter_list|,
name|Membership
modifier|...
name|bounds
parameter_list|)
function_decl|;
comment|/** Compute longitude/latitude bounds for the shape.     *@param bounds is the optional input bounds object.  If this is null,     * a bounds object will be created.  Otherwise, the input object will be modified.     *@return a Bounds object describing the shape's bounds.  If the bounds cannot     * be computed, then return a Bounds object with noLongitudeBound,     * noTopLatitudeBound, and noBottomLatitudeBound.     */
DECL|method|getBounds
specifier|public
name|Bounds
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
function_decl|;
comment|/** Equals */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

