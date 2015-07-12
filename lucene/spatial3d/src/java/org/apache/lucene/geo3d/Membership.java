begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Implemented by Geo3D shapes that can calculate if a point is within it or not.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Membership
specifier|public
interface|interface
name|Membership
block|{
comment|/**    * Check if a point is within this shape.    *    * @param point is the point to check.    * @return true if the point is within this shape    */
DECL|method|isWithin
specifier|public
specifier|default
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
comment|/**    * Check if a point is within this shape.    *    * @param x is x coordinate of point to check.    * @param y is y coordinate of point to check.    * @param z is z coordinate of point to check.    * @return true if the point is within this shape    */
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
function_decl|;
block|}
end_interface

end_unit

