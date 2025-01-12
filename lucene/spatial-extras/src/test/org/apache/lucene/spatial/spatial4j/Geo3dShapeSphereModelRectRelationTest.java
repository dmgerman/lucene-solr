begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.spatial4j
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
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoArea
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoBBox
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoBBoxFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoCircleFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPolygonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|GeoShape
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|Geo3dShapeSphereModelRectRelationTest
specifier|public
class|class
name|Geo3dShapeSphereModelRectRelationTest
extends|extends
name|Geo3dShapeRectRelationTestCase
block|{
DECL|method|Geo3dShapeSphereModelRectRelationTest
specifier|public
name|Geo3dShapeSphereModelRectRelationTest
parameter_list|()
block|{
name|super
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailure1
specifier|public
name|void
name|testFailure1
parameter_list|()
block|{
specifier|final
name|GeoBBox
name|rect
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
literal|88
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|30
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|30
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|62
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|30.4579218227
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|14.5238410082
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|43.684447915
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|46.2210986329
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|66.2465299717
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|29.1786158537
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoShape
name|path
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|planetModel
argument_list|,
name|points
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|34.2730264413182
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|82.75500168892472
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
comment|// Apparently the rectangle thinks the polygon is completely within it... "shape inside rectangle"
name|assertTrue
argument_list|(
name|GeoArea
operator|.
name|WITHIN
operator|==
name|rect
operator|.
name|getRelationship
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// Point is within path? Apparently not...
name|assertFalse
argument_list|(
name|path
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
comment|// If it is within the path, it must be within the rectangle, and similarly visa versa
name|assertFalse
argument_list|(
name|rect
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailure2_LUCENE6475
specifier|public
name|void
name|testFailure2_LUCENE6475
parameter_list|()
block|{
name|GeoShape
name|geo3dCircle
init|=
name|GeoCircleFactory
operator|.
name|makeGeoCircle
argument_list|(
name|planetModel
argument_list|,
literal|1.6282053147165243E
operator|-
literal|4
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|70.1600629789353
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|86
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
name|Geo3dShape
name|geo3dShape
init|=
operator|new
name|Geo3dShape
argument_list|(
name|planetModel
argument_list|,
name|geo3dCircle
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Rectangle
name|rect
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|118
argument_list|,
operator|-
literal|114
argument_list|,
operator|-
literal|2.0
argument_list|,
literal|32.0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|geo3dShape
operator|.
name|relate
argument_list|(
name|rect
argument_list|)
operator|.
name|intersects
argument_list|()
argument_list|)
expr_stmt|;
comment|// thus the bounding box must intersect too
name|assertTrue
argument_list|(
name|geo3dShape
operator|.
name|getBoundingBox
argument_list|()
operator|.
name|relate
argument_list|(
name|rect
argument_list|)
operator|.
name|intersects
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

