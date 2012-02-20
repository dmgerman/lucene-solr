begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.base.shape
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_interface
DECL|interface|Shape
specifier|public
interface|interface
name|Shape
block|{
comment|/**    * Describe the relationship between the two objects.  For example    *    *   this is WITHIN other    *   this CONTAINS other    *   this is DISJOINT other    *   this INTERSECTS other    *    * The context object is optional -- it may include spatial reference.    */
DECL|method|relate
name|SpatialRelation
name|relate
parameter_list|(
name|Shape
name|other
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Get the bounding box for this Shape    */
DECL|method|getBoundingBox
name|Rectangle
name|getBoundingBox
parameter_list|()
function_decl|;
comment|/**    * @return true if the shape has area.  This will be false for points and lines    */
DECL|method|hasArea
name|boolean
name|hasArea
parameter_list|()
function_decl|;
DECL|method|getCenter
name|Point
name|getCenter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

