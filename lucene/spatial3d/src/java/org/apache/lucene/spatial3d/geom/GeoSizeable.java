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
comment|/**  * Some shapes can compute radii of a geocircle in which they are inscribed.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|GeoSizeable
specifier|public
interface|interface
name|GeoSizeable
block|{
comment|/**    * Returns the radius of a circle into which the GeoSizeable area can    * be inscribed.    *    * @return the radius.    */
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
function_decl|;
comment|/**    * Returns the center of a circle into which the area will be inscribed.    *    * @return the center.    */
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

