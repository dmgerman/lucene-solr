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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test basic plane functionality.  */
end_comment

begin_class
DECL|class|GeoModelTest
specifier|public
class|class
name|GeoModelTest
block|{
DECL|field|scaledModel
specifier|protected
specifier|final
specifier|static
name|PlanetModel
name|scaledModel
init|=
operator|new
name|PlanetModel
argument_list|(
literal|1.2
argument_list|,
literal|1.5
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testBasicCircle
specifier|public
name|void
name|testBasicCircle
parameter_list|()
block|{
comment|// The point of this test is just to make sure nothing blows up doing normal things with a quite non-spherical model
comment|// Make sure that the north pole is in the circle, and south pole isn't
specifier|final
name|GeoPoint
name|northPole
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|southPole
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|point1
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|point2
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.125
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
name|GeoCircle
name|circle
init|=
operator|new
name|GeoCircle
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
literal|0.01
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|northPole
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|southPole
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|point1
argument_list|)
argument_list|)
expr_stmt|;
name|Bounds
name|bounds
init|=
name|circle
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|-
literal|0.01
argument_list|,
name|bounds
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|circle
operator|=
operator|new
name|GeoCircle
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.0
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|point1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|northPole
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|southPole
argument_list|)
argument_list|)
expr_stmt|;
name|bounds
operator|=
name|circle
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|+
literal|0.01
argument_list|,
name|bounds
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.01
argument_list|,
name|bounds
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.0125
argument_list|,
name|bounds
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0125
argument_list|,
name|bounds
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|circle
operator|=
operator|new
name|GeoCircle
argument_list|(
name|scaledModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.125
argument_list|,
literal|0.0
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|point2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|northPole
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|circle
operator|.
name|isWithin
argument_list|(
name|southPole
argument_list|)
argument_list|)
expr_stmt|;
name|bounds
operator|=
name|circle
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
comment|// Symmetric, as expected
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.125
operator|-
literal|0.01
argument_list|,
name|bounds
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.125
operator|+
literal|0.01
argument_list|,
name|bounds
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.0089
argument_list|,
name|bounds
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0089
argument_list|,
name|bounds
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicRectangle
specifier|public
name|void
name|testBasicRectangle
parameter_list|()
block|{
specifier|final
name|GeoBBox
name|bbox
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|scaledModel
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|insidePoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
literal|0.5
argument_list|,
literal|0.5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bbox
operator|.
name|isWithin
argument_list|(
name|insidePoint
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoPoint
name|topOutsidePoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
literal|1.01
argument_list|,
literal|0.5
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bbox
operator|.
name|isWithin
argument_list|(
name|topOutsidePoint
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoPoint
name|bottomOutsidePoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
operator|-
literal|0.01
argument_list|,
literal|0.5
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bbox
operator|.
name|isWithin
argument_list|(
name|bottomOutsidePoint
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoPoint
name|leftOutsidePoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
literal|0.5
argument_list|,
operator|-
literal|0.01
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bbox
operator|.
name|isWithin
argument_list|(
name|leftOutsidePoint
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoPoint
name|rightOutsidePoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|scaledModel
argument_list|,
literal|0.5
argument_list|,
literal|1.01
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bbox
operator|.
name|isWithin
argument_list|(
name|rightOutsidePoint
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Bounds
name|bounds
init|=
name|bbox
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|bounds
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|bounds
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|bounds
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|bounds
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

