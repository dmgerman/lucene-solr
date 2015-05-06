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
comment|/**  * A GeoArea represents a standard 2-D breakdown of a part of sphere.  It can  * be bounded in latitude, or bounded in both latitude and longitude, or not  * bounded at all.  The purpose of the interface is to describe bounding shapes used for  * computation of geo hashes.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|GeoArea
specifier|public
interface|interface
name|GeoArea
extends|extends
name|Membership
block|{
comment|// Since we don't know what each GeoArea's constraints are,
comment|// we put the onus on the GeoArea implementation to do the right thing.
comment|// This will, of course, rely heavily on methods provided by
comment|// the underlying GeoShape class.
DECL|field|CONTAINS
specifier|public
specifier|static
specifier|final
name|int
name|CONTAINS
init|=
literal|0
decl_stmt|;
DECL|field|WITHIN
specifier|public
specifier|static
specifier|final
name|int
name|WITHIN
init|=
literal|1
decl_stmt|;
DECL|field|OVERLAPS
specifier|public
specifier|static
specifier|final
name|int
name|OVERLAPS
init|=
literal|2
decl_stmt|;
DECL|field|DISJOINT
specifier|public
specifier|static
specifier|final
name|int
name|DISJOINT
init|=
literal|3
decl_stmt|;
comment|/**    * Find the spatial relationship between a shape and the current geo area.    * Note: return value is how the GeoShape relates to the GeoArea, not the    * other way around. For example, if this GeoArea is entirely within the    * shape, then CONTAINS should be returned.  If the shape is entirely enclosed    * by this GeoArea, then WITHIN should be returned.    * Note well: When a shape consists of multiple independent overlapping subshapes,    * it is sometimes impossible to determine the distinction between    * OVERLAPS and CONTAINS.  In that case, OVERLAPS may be returned even    * though the proper result would in fact be CONTAINS.  Code accordingly.    *    * @param shape is the shape to consider.    * @return the relationship, from the perspective of the shape.    */
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
name|GeoShape
name|shape
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

