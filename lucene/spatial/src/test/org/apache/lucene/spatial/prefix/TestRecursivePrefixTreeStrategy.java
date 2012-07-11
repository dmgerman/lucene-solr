begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|simple
operator|.
name|PointImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|util
operator|.
name|GeohashUtils
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|StoredField
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
name|document
operator|.
name|StringField
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
name|index
operator|.
name|IndexableField
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
name|spatial
operator|.
name|SpatialMatchConcern
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
name|spatial
operator|.
name|StrategyTestCase
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|toRadians
import|;
end_import

begin_class
DECL|class|TestRecursivePrefixTreeStrategy
specifier|public
class|class
name|TestRecursivePrefixTreeStrategy
extends|extends
name|StrategyTestCase
block|{
DECL|field|maxLength
specifier|private
name|int
name|maxLength
decl_stmt|;
comment|//Tests should call this first.
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|maxLength
parameter_list|)
block|{
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|SimpleSpatialContext
operator|.
name|GEO_KM
expr_stmt|;
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilterWithVariableScanLevel
specifier|public
name|void
name|testFilterWithVariableScanLevel
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
comment|//execute queries for each prefix grid scan level
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|maxLength
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_Cities_IsWithin_BBox
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|geohashRecursiveRandom
specifier|public
name|void
name|geohashRecursiveRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
literal|12
argument_list|)
expr_stmt|;
comment|//1. Iterate test with the cluster at some worldly point of interest
name|Point
index|[]
name|clusterCenters
init|=
operator|new
name|Point
index|[]
block|{
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
block|,
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
block|,
operator|new
name|PointImpl
argument_list|(
literal|0
argument_list|,
operator|-
literal|90
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Point
name|clusterCenter
range|:
name|clusterCenters
control|)
block|{
comment|//2. Iterate on size of cluster (a really small one and a large one)
name|String
name|hashCenter
init|=
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|clusterCenter
operator|.
name|getY
argument_list|()
argument_list|,
name|clusterCenter
operator|.
name|getX
argument_list|()
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
comment|//calculate the number of degrees in the smallest grid box size (use for both lat& lon)
name|String
name|smallBox
init|=
name|hashCenter
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hashCenter
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//chop off leaf precision
name|Rectangle
name|clusterDims
init|=
name|GeohashUtils
operator|.
name|decodeBoundary
argument_list|(
name|smallBox
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|double
name|smallDegrees
init|=
name|Math
operator|.
name|max
argument_list|(
name|clusterDims
operator|.
name|getMaxX
argument_list|()
operator|-
name|clusterDims
operator|.
name|getMinX
argument_list|()
argument_list|,
name|clusterDims
operator|.
name|getMaxY
argument_list|()
operator|-
name|clusterDims
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|smallDegrees
operator|<
literal|1
assert|;
name|double
name|largeDegrees
init|=
literal|20d
decl_stmt|;
comment|//good large size; don't use>=45 for this test code to work
name|double
index|[]
name|sideDegrees
init|=
block|{
name|largeDegrees
block|,
name|smallDegrees
block|}
decl_stmt|;
for|for
control|(
name|double
name|sideDegree
range|:
name|sideDegrees
control|)
block|{
comment|//3. Index random points in this cluster box
name|deleteAll
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Point
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<
name|Point
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|double
name|x
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|sideDegree
operator|-
name|sideDegree
operator|/
literal|2
operator|+
name|clusterCenter
operator|.
name|getX
argument_list|()
decl_stmt|;
name|double
name|y
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|sideDegree
operator|-
name|sideDegree
operator|/
literal|2
operator|+
name|clusterCenter
operator|.
name|getY
argument_list|()
decl_stmt|;
specifier|final
name|Point
name|pt
init|=
name|normPointXY
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
name|pt
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|newDoc
argument_list|(
literal|""
operator|+
name|i
argument_list|,
name|pt
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
comment|//3. Use 4 query centers. Each is radially out from each corner of cluster box by twice distance to box edge.
for|for
control|(
name|double
name|qcXoff
range|:
operator|new
name|double
index|[]
block|{
name|sideDegree
block|,
operator|-
name|sideDegree
block|}
control|)
block|{
comment|//query-center X offset from cluster center
for|for
control|(
name|double
name|qcYoff
range|:
operator|new
name|double
index|[]
block|{
name|sideDegree
block|,
operator|-
name|sideDegree
block|}
control|)
block|{
comment|//query-center Y offset from cluster center
name|Point
name|queryCenter
init|=
name|normPointXY
argument_list|(
name|qcXoff
operator|+
name|clusterCenter
operator|.
name|getX
argument_list|()
argument_list|,
name|qcYoff
operator|+
name|clusterCenter
operator|.
name|getY
argument_list|()
argument_list|)
decl_stmt|;
name|double
index|[]
name|distRange
init|=
name|calcDistRange
argument_list|(
name|queryCenter
argument_list|,
name|clusterCenter
argument_list|,
name|sideDegree
argument_list|)
decl_stmt|;
comment|//4.1 query a small box getting nothing
name|checkHits
argument_list|(
name|queryCenter
argument_list|,
name|distRange
index|[
literal|0
index|]
operator|*
literal|0.99
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//4.2 Query a large box enclosing the cluster, getting everything
name|checkHits
argument_list|(
name|queryCenter
argument_list|,
name|distRange
index|[
literal|1
index|]
operator|*
literal|1.01
argument_list|,
name|points
operator|.
name|size
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//4.3 Query a medium box getting some (calculate the correct solution and verify)
name|double
name|queryDist
init|=
name|distRange
index|[
literal|0
index|]
operator|+
operator|(
name|distRange
index|[
literal|1
index|]
operator|-
name|distRange
index|[
literal|0
index|]
operator|)
operator|/
literal|2
decl_stmt|;
comment|//average
comment|//Find matching points.  Put into int[] of doc ids which is the same thing as the index into points list.
name|int
index|[]
name|ids
init|=
operator|new
name|int
index|[
name|points
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|ids_sz
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Point
name|point
init|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|queryCenter
argument_list|,
name|point
argument_list|)
operator|<=
name|queryDist
condition|)
name|ids
index|[
name|ids_sz
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
name|ids
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|,
name|ids_sz
argument_list|)
expr_stmt|;
comment|//assert ids_sz> 0 (can't because randomness keeps us from being able to)
name|checkHits
argument_list|(
name|queryCenter
argument_list|,
name|queryDist
argument_list|,
name|ids
operator|.
name|length
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//for sideDegree
block|}
comment|//for clusterCenter
block|}
comment|//randomTest()
comment|//TODO can we use super.runTestQueries() ?
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|dist
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
index|[]
name|assertIds
parameter_list|)
block|{
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
name|args
operator|.
name|setDistPrecision
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|shape
argument_list|,
name|assertNumFound
argument_list|,
name|got
operator|.
name|numFound
argument_list|)
expr_stmt|;
if|if
condition|(
name|assertIds
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|gotIds
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|result
range|:
name|got
operator|.
name|results
control|)
block|{
name|gotIds
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|result
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|assertId
range|:
name|assertIds
control|)
block|{
name|assertTrue
argument_list|(
literal|"has "
operator|+
name|assertId
argument_list|,
name|gotIds
operator|.
name|contains
argument_list|(
name|assertId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//
DECL|method|newDoc
specifier|private
name|Document
name|newDoc
parameter_list|(
name|String
name|id
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|strategy
operator|.
name|createFields
argument_list|(
name|shape
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeShape
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|ctx
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|calcDistRange
specifier|private
name|double
index|[]
name|calcDistRange
parameter_list|(
name|Point
name|startPoint
parameter_list|,
name|Point
name|targetCenter
parameter_list|,
name|double
name|targetSideDegrees
parameter_list|)
block|{
name|double
name|min
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|double
name|max
init|=
name|Double
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|double
name|xLen
range|:
operator|new
name|double
index|[]
block|{
name|targetSideDegrees
block|,
operator|-
name|targetSideDegrees
block|}
control|)
block|{
for|for
control|(
name|double
name|yLen
range|:
operator|new
name|double
index|[]
block|{
name|targetSideDegrees
block|,
operator|-
name|targetSideDegrees
block|}
control|)
block|{
name|Point
name|p2
init|=
name|normPointXY
argument_list|(
name|targetCenter
operator|.
name|getX
argument_list|()
operator|+
name|xLen
operator|/
literal|2
argument_list|,
name|targetCenter
operator|.
name|getY
argument_list|()
operator|+
name|yLen
operator|/
literal|2
argument_list|)
decl_stmt|;
name|double
name|d
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|startPoint
argument_list|,
name|p2
argument_list|)
decl_stmt|;
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|double
index|[]
block|{
name|min
block|,
name|max
block|}
return|;
block|}
comment|/** Normalize x& y (put in lon-lat ranges)& ensure geohash round-trip for given precision. */
DECL|method|normPointXY
specifier|private
name|Point
name|normPointXY
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
comment|//put x,y as degrees into double[] as radians
name|double
index|[]
name|latLon
init|=
block|{
name|y
operator|*
name|DistanceUtils
operator|.
name|DEG_180_AS_RADS
block|,
name|toRadians
argument_list|(
name|x
argument_list|)
block|}
decl_stmt|;
name|DistanceUtils
operator|.
name|normLatRAD
argument_list|(
name|latLon
argument_list|)
expr_stmt|;
name|DistanceUtils
operator|.
name|normLatRAD
argument_list|(
name|latLon
argument_list|)
expr_stmt|;
name|double
name|x2
init|=
name|Math
operator|.
name|toDegrees
argument_list|(
name|latLon
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|double
name|y2
init|=
name|Math
operator|.
name|toDegrees
argument_list|(
name|latLon
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|//overwrite latLon, units is now degrees
return|return
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|y2
argument_list|,
name|x2
argument_list|,
name|maxLength
argument_list|)
argument_list|,
name|ctx
argument_list|)
return|;
block|}
block|}
end_class

end_unit

