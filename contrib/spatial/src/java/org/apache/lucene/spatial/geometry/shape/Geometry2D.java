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
comment|/**  * Common set of operations available on 2d shapes.  */
end_comment

begin_interface
DECL|interface|Geometry2D
specifier|public
interface|interface
name|Geometry2D
block|{
comment|/**    * Translate according to the vector    * @param v    */
DECL|method|translate
specifier|public
name|void
name|translate
parameter_list|(
name|Vector2D
name|v
parameter_list|)
function_decl|;
comment|/**    * Does the shape contain the given point    * @param p    * @return    */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Point2D
name|p
parameter_list|)
function_decl|;
comment|/**    * Return the area    * @return    */
DECL|method|area
specifier|public
name|double
name|area
parameter_list|()
function_decl|;
comment|/**    * Return the centroid    * @return    */
DECL|method|centroid
specifier|public
name|Point2D
name|centroid
parameter_list|()
function_decl|;
comment|/**    * Returns information about how this shape intersects the given rectangle    * @param r    * @return    */
DECL|method|intersect
specifier|public
name|IntersectCase
name|intersect
parameter_list|(
name|Rectangle
name|r
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

