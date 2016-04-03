begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
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
name|geo
operator|.
name|Polygon
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLatitude
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLatitudeAround
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLongitude
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLongitudeAround
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextPolygon
import|;
end_import

begin_class
DECL|class|TestPolygon
specifier|public
class|class
name|TestPolygon
extends|extends
name|LuceneTestCase
block|{
comment|/** null polyLats not allowed */
DECL|method|testPolygonNullPolyLats
specifier|public
name|void
name|testPolygonNullPolyLats
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
literal|null
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
block|)
class|;
end_class

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"polyLats must not be null"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** null polyLons not allowed */
end_comment

begin_function
DECL|method|testPolygonNullPolyLons
unit|public
name|void
name|testPolygonNullPolyLons
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
operator|,
literal|18
block|}
operator|,
literal|null
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"polyLons must not be null"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon needs at least 3 vertices */
end_comment

begin_function
DECL|method|testPolygonLine
unit|public
name|void
name|testPolygonLine
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|18
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon needs same number of latitudes as longitudes */
end_comment

begin_function
DECL|method|testPolygonBogus
unit|public
name|void
name|testPolygonBogus
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must be equal length"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon must be closed */
end_comment

begin_function
DECL|method|testPolygonNotClosed
unit|public
name|void
name|testPolygonNotClosed
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
operator|,
literal|19
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|67
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"it must close itself"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** Three boxes, an island inside a hole inside a shape */
end_comment

begin_function
DECL|method|testMultiPolygon
unit|public
name|void
name|testMultiPolygon
parameter_list|()
block|{
name|Polygon
name|hole
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|10
block|,
operator|-
literal|10
block|,
literal|10
block|,
literal|10
block|,
operator|-
literal|10
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|10
block|,
literal|10
block|,
literal|10
block|,
operator|-
literal|10
block|,
operator|-
literal|10
block|}
argument_list|)
decl_stmt|;
name|Polygon
name|outer
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|50
block|,
operator|-
literal|50
block|,
literal|50
block|,
literal|50
block|,
operator|-
literal|50
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|50
block|,
literal|50
block|,
literal|50
block|,
operator|-
literal|50
block|,
operator|-
literal|50
block|}
argument_list|,
name|hole
argument_list|)
decl_stmt|;
name|Polygon
name|island
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|5
block|,
operator|-
literal|5
block|,
literal|5
block|,
literal|5
block|,
operator|-
literal|5
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|5
block|,
literal|5
block|,
literal|5
block|,
operator|-
literal|5
block|,
operator|-
literal|5
block|}
argument_list|)
decl_stmt|;
name|Polygon
name|polygons
index|[]
init|=
operator|new
name|Polygon
index|[]
block|{
name|outer
block|,
name|island
block|}
decl_stmt|;
comment|// contains(point)
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the island
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|6
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the hole
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|25
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the mainland
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|51
argument_list|,
literal|51
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the ocean
comment|// contains(box): this can conservatively return false
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the island
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the hole
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|24
argument_list|,
literal|25
argument_list|,
literal|24
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the mainland
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|51
argument_list|,
literal|52
argument_list|,
literal|51
argument_list|,
literal|52
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the ocean
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
operator|-
literal|60
argument_list|,
literal|60
argument_list|,
operator|-
literal|60
argument_list|,
literal|60
argument_list|)
argument_list|)
expr_stmt|;
comment|// enclosing us completely
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|49
argument_list|,
literal|51
argument_list|,
literal|49
argument_list|,
literal|51
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the mainland
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the hole
name|assertFalse
argument_list|(
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the island
comment|// crosses(box): this can conservatively return true
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|crosses
argument_list|(
name|polygons
argument_list|,
operator|-
literal|60
argument_list|,
literal|60
argument_list|,
operator|-
literal|60
argument_list|,
literal|60
argument_list|)
argument_list|)
expr_stmt|;
comment|// enclosing us completely
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|crosses
argument_list|(
name|polygons
argument_list|,
literal|49
argument_list|,
literal|51
argument_list|,
literal|49
argument_list|,
literal|51
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the mainland and ocean
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|crosses
argument_list|(
name|polygons
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the hole and mainland
name|assertTrue
argument_list|(
name|Polygon
operator|.
name|crosses
argument_list|(
name|polygons
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the island
block|}
end_function

begin_function
DECL|method|testPacMan
specifier|public
name|void
name|testPacMan
parameter_list|()
throws|throws
name|Exception
block|{
comment|// pacman
name|double
index|[]
name|px
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|,
operator|-
literal|8
block|,
operator|-
literal|10
block|,
operator|-
literal|8
block|,
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|py
init|=
block|{
literal|0
block|,
literal|5
block|,
literal|9
block|,
literal|10
block|,
literal|9
block|,
literal|0
block|,
operator|-
literal|9
block|,
operator|-
literal|10
block|,
operator|-
literal|9
block|,
operator|-
literal|5
block|,
literal|0
block|}
decl_stmt|;
comment|// candidate crosses cell
name|double
name|xMin
init|=
literal|2
decl_stmt|;
comment|//-5;
name|double
name|xMax
init|=
literal|11
decl_stmt|;
comment|//0.000001;
name|double
name|yMin
init|=
operator|-
literal|1
decl_stmt|;
comment|//0;
name|double
name|yMax
init|=
literal|1
decl_stmt|;
comment|//5;
comment|// test cell crossing poly
name|Polygon
name|polygon
init|=
operator|new
name|Polygon
argument_list|(
name|py
argument_list|,
name|px
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|polygon
operator|.
name|crosses
argument_list|(
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testBoundingBox
specifier|public
name|void
name|testBoundingBox
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|latitude
init|=
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitude
argument_list|()
decl_stmt|;
comment|// if the point is within poly, then it should be in our bounding box
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|latitude
operator|>=
name|polygon
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|polygon
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longitude
operator|>=
name|polygon
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|polygon
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_function

begin_function
DECL|method|testBoundingBoxEdgeCases
specifier|public
name|void
name|testBoundingBoxEdgeCases
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|latitude
init|=
name|nextLatitudeAround
argument_list|(
name|polygon
operator|.
name|minLat
argument_list|,
name|polygon
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitudeAround
argument_list|(
name|polygon
operator|.
name|minLon
argument_list|,
name|polygon
operator|.
name|maxLon
argument_list|)
decl_stmt|;
comment|// if the point is within poly, then it should be in our bounding box
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|latitude
operator|>=
name|polygon
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|polygon
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longitude
operator|>=
name|polygon
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|polygon
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/** If polygon.contains(box) returns true, then any point in that box should return true as well */
end_comment

begin_function
DECL|method|testContainsRandom
specifier|public
name|void
name|testContainsRandom
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextSimpleBox
argument_list|()
decl_stmt|;
comment|// allowed to conservatively return false
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1000
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|nextLatitudeAround
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitudeAround
argument_list|(
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/** If polygon.contains(box) returns true, then any point in that box should return true as well */
end_comment

begin_comment
comment|// different from testContainsRandom in that its not a purely random test. we iterate the vertices of the polygon
end_comment

begin_comment
comment|// and generate boxes near each one of those to try to be more efficient.
end_comment

begin_function
DECL|method|testContainsEdgeCases
specifier|public
name|void
name|testContainsEdgeCases
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|double
name|polyLats
index|[]
init|=
name|polygon
operator|.
name|getPolyLats
argument_list|()
decl_stmt|;
name|double
name|polyLons
index|[]
init|=
name|polygon
operator|.
name|getPolyLons
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|vertex
init|=
literal|0
init|;
name|vertex
operator|<
name|polyLats
operator|.
name|length
condition|;
name|vertex
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextSimpleBoxNear
argument_list|(
name|polyLats
index|[
name|vertex
index|]
argument_list|,
name|polyLons
index|[
name|vertex
index|]
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return false
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|nextLatitudeAround
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitudeAround
argument_list|(
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/** If polygon.intersects(box) returns false, then any point in that box should return false as well */
end_comment

begin_function
DECL|method|testIntersectRandom
specifier|public
name|void
name|testIntersectRandom
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextSimpleBox
argument_list|()
decl_stmt|;
comment|// allowed to conservatively return true.
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
literal|false
operator|&&
name|polygon
operator|.
name|crosses
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1000
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|nextLatitudeAround
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitudeAround
argument_list|(
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/** If polygon.intersects(box) returns false, then any point in that box should return false as well */
end_comment

begin_comment
comment|// different from testIntersectsRandom in that its not a purely random test. we iterate the vertices of the polygon
end_comment

begin_comment
comment|// and generate boxes near each one of those to try to be more efficient.
end_comment

begin_function
DECL|method|testIntersectEdgeCases
specifier|public
name|void
name|testIntersectEdgeCases
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|double
name|polyLats
index|[]
init|=
name|polygon
operator|.
name|getPolyLats
argument_list|()
decl_stmt|;
name|double
name|polyLons
index|[]
init|=
name|polygon
operator|.
name|getPolyLons
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|vertex
init|=
literal|0
init|;
name|vertex
operator|<
name|polyLats
operator|.
name|length
condition|;
name|vertex
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextSimpleBoxNear
argument_list|(
name|polyLats
index|[
name|vertex
index|]
argument_list|,
name|polyLons
index|[
name|vertex
index|]
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return true.
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
literal|false
operator|&&
name|polygon
operator|.
name|crosses
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|nextLatitudeAround
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitudeAround
argument_list|(
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_function

unit|}
end_unit

