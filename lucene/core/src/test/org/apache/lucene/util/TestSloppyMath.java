begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|cos
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
name|util
operator|.
name|SloppyMath
operator|.
name|asin
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
name|util
operator|.
name|SloppyMath
operator|.
name|haversinMeters
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
name|util
operator|.
name|SloppyMath
operator|.
name|haversinSortKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|geo
operator|.
name|GeoTestUtil
import|;
end_import

begin_class
DECL|class|TestSloppyMath
specifier|public
class|class
name|TestSloppyMath
extends|extends
name|LuceneTestCase
block|{
comment|// accuracy for cos()
DECL|field|COS_DELTA
specifier|static
name|double
name|COS_DELTA
init|=
literal|1E
operator|-
literal|15
decl_stmt|;
comment|// accuracy for asin()
DECL|field|ASIN_DELTA
specifier|static
name|double
name|ASIN_DELTA
init|=
literal|1E
operator|-
literal|7
decl_stmt|;
comment|// accuracy for haversinMeters()
DECL|field|HAVERSIN_DELTA
specifier|static
name|double
name|HAVERSIN_DELTA
init|=
literal|38E
operator|-
literal|2
decl_stmt|;
comment|// accuracy for haversinMeters() for "reasonable" distances (< 1000km)
DECL|field|REASONABLE_HAVERSIN_DELTA
specifier|static
name|double
name|REASONABLE_HAVERSIN_DELTA
init|=
literal|1E
operator|-
literal|5
decl_stmt|;
DECL|method|testCos
specifier|public
name|void
name|testCos
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|cos
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|cos
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|cos
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
literal|1
argument_list|)
argument_list|,
name|cos
argument_list|(
literal|1
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
literal|0
argument_list|)
argument_list|,
name|cos
argument_list|(
literal|0
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
argument_list|)
argument_list|,
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
argument_list|)
argument_list|,
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
argument_list|,
name|cos
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
argument_list|,
name|cos
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
comment|// testing purely random longs is inefficent, as for stupid parameters we just
comment|// pass thru to Math.cos() instead of doing some huperduper arg reduction
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|d
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|SloppyMath
operator|.
name|SIN_COS_MAX_VALUE_FOR_INT_MODULO
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|d
operator|=
operator|-
name|d
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|cos
argument_list|(
name|d
argument_list|)
argument_list|,
name|cos
argument_list|(
name|d
argument_list|)
argument_list|,
name|COS_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAsin
specifier|public
name|void
name|testAsin
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|asin
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|asin
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|asin
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|,
name|asin
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|,
name|asin
argument_list|(
operator|-
literal|0.8660254
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|,
name|asin
argument_list|(
operator|-
literal|0.7071068
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|,
name|asin
argument_list|(
operator|-
literal|0.5
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|asin
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|,
name|asin
argument_list|(
literal|0.5
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|,
name|asin
argument_list|(
literal|0.7071068
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|,
name|asin
argument_list|(
literal|0.8660254
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|,
name|asin
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
comment|// only values -1..1 are useful
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|d
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|d
operator|=
operator|-
name|d
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|StrictMath
operator|.
name|asin
argument_list|(
name|d
argument_list|)
argument_list|,
name|asin
argument_list|(
name|d
argument_list|)
argument_list|,
name|ASIN_DELTA
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|asin
argument_list|(
name|d
argument_list|)
operator|>=
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|asin
argument_list|(
name|d
argument_list|)
operator|<=
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testHaversin
specifier|public
name|void
name|testHaversin
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|haversinMeters
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|haversinMeters
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|Double
operator|.
name|NaN
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|haversinMeters
argument_list|(
literal|1
argument_list|,
name|Double
operator|.
name|NaN
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|isNaN
argument_list|(
name|haversinMeters
argument_list|(
name|Double
operator|.
name|NaN
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|,
operator|-
literal|180
argument_list|,
literal|0
argument_list|,
operator|-
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|,
operator|-
literal|180
argument_list|,
literal|0
argument_list|,
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|,
literal|180
argument_list|,
literal|0
argument_list|,
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|90
argument_list|,
literal|0
argument_list|,
literal|90
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|90
argument_list|,
operator|-
literal|180
argument_list|,
literal|90
argument_list|,
operator|-
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|90
argument_list|,
operator|-
literal|180
argument_list|,
literal|90
argument_list|,
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|90
argument_list|,
literal|180
argument_list|,
literal|90
argument_list|,
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
comment|// Test half a circle on the equator, using WGS84 mean earth radius in meters
name|double
name|earthRadiusMs
init|=
literal|6_371_008.7714
decl_stmt|;
name|double
name|halfCircle
init|=
name|earthRadiusMs
operator|*
name|Math
operator|.
name|PI
decl_stmt|;
name|assertEquals
argument_list|(
name|halfCircle
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|180
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|double
name|randomLat1
init|=
literal|40.7143528
operator|+
operator|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|-
literal|5
operator|)
operator|*
literal|360
decl_stmt|;
name|double
name|randomLon1
init|=
operator|-
literal|74.0059731
operator|+
operator|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|-
literal|5
operator|)
operator|*
literal|360
decl_stmt|;
name|double
name|randomLat2
init|=
literal|40.65
operator|+
operator|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|-
literal|5
operator|)
operator|*
literal|360
decl_stmt|;
name|double
name|randomLon2
init|=
operator|-
literal|73.95
operator|+
operator|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|-
literal|5
operator|)
operator|*
literal|360
decl_stmt|;
name|assertEquals
argument_list|(
literal|8_572.1137
argument_list|,
name|haversinMeters
argument_list|(
name|randomLat1
argument_list|,
name|randomLon1
argument_list|,
name|randomLat2
argument_list|,
name|randomLon2
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
comment|// from solr and ES tests (with their respective epsilons)
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5_285.89
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.759011
argument_list|,
operator|-
literal|73.9844722
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|462.10
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.718266
argument_list|,
operator|-
literal|74.007819
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1_054.98
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.7051157
argument_list|,
operator|-
literal|74.0088305
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1_258.12
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.7247222
argument_list|,
operator|-
literal|74
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2_028.52
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.731033
argument_list|,
operator|-
literal|73.9962255
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8_572.11
argument_list|,
name|haversinMeters
argument_list|(
literal|40.7143528
argument_list|,
operator|-
literal|74.0059731
argument_list|,
literal|40.65
argument_list|,
operator|-
literal|73.95
argument_list|)
argument_list|,
literal|0.01D
argument_list|)
expr_stmt|;
block|}
comment|/** Test this method sorts the same way as real haversin */
DECL|method|testHaversinSortKey
specifier|public
name|void
name|testHaversinSortKey
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|centerLat
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|centerLon
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|lat1
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon1
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|lat2
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon2
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
name|Integer
operator|.
name|signum
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|actual
init|=
name|Integer
operator|.
name|signum
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|haversinSortKey
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
name|haversinSortKey
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|,
name|haversinMeters
argument_list|(
name|haversinSortKey
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat1
argument_list|,
name|lon1
argument_list|)
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|,
name|haversinMeters
argument_list|(
name|haversinSortKey
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testHaversinFromSortKey
specifier|public
name|void
name|testHaversinFromSortKey
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|haversinMeters
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
block|}
DECL|method|testAgainstSlowVersion
specifier|public
name|void
name|testAgainstSlowVersion
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
literal|100_000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lat1
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon1
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|lat2
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon2
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|expected
init|=
name|slowHaversin
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
name|double
name|actual
init|=
name|haversinMeters
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|HAVERSIN_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Step across the whole world to find huge absolute errors.    * Don't rely on random number generator to pick these massive distances. */
DECL|method|testAcrossWholeWorldSteps
specifier|public
name|void
name|testAcrossWholeWorldSteps
parameter_list|()
block|{
for|for
control|(
name|int
name|lat1
init|=
operator|-
literal|90
init|;
name|lat1
operator|<=
literal|90
condition|;
name|lat1
operator|+=
literal|10
control|)
block|{
for|for
control|(
name|int
name|lon1
init|=
operator|-
literal|180
init|;
name|lon1
operator|<=
literal|180
condition|;
name|lon1
operator|+=
literal|10
control|)
block|{
for|for
control|(
name|int
name|lat2
init|=
operator|-
literal|90
init|;
name|lat2
operator|<=
literal|90
condition|;
name|lat2
operator|+=
literal|10
control|)
block|{
for|for
control|(
name|int
name|lon2
init|=
operator|-
literal|180
init|;
name|lon2
operator|<=
literal|180
condition|;
name|lon2
operator|+=
literal|10
control|)
block|{
name|double
name|expected
init|=
name|slowHaversin
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
name|double
name|actual
init|=
name|haversinMeters
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|HAVERSIN_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|testAgainstSlowVersionReasonable
specifier|public
name|void
name|testAgainstSlowVersionReasonable
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
literal|100_000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lat1
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon1
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|lat2
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon2
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|expected
init|=
name|haversinMeters
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|<
literal|1_000_000
condition|)
block|{
name|double
name|actual
init|=
name|slowHaversin
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|REASONABLE_HAVERSIN_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// simple incorporation of the wikipedia formula
DECL|method|slowHaversin
specifier|private
specifier|static
name|double
name|slowHaversin
parameter_list|(
name|double
name|lat1
parameter_list|,
name|double
name|lon1
parameter_list|,
name|double
name|lat2
parameter_list|,
name|double
name|lon2
parameter_list|)
block|{
name|double
name|h1
init|=
operator|(
literal|1
operator|-
name|StrictMath
operator|.
name|cos
argument_list|(
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lat2
argument_list|)
operator|-
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lat1
argument_list|)
argument_list|)
operator|)
operator|/
literal|2
decl_stmt|;
name|double
name|h2
init|=
operator|(
literal|1
operator|-
name|StrictMath
operator|.
name|cos
argument_list|(
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lon2
argument_list|)
operator|-
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lon1
argument_list|)
argument_list|)
operator|)
operator|/
literal|2
decl_stmt|;
name|double
name|h
init|=
name|h1
operator|+
name|StrictMath
operator|.
name|cos
argument_list|(
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lat1
argument_list|)
argument_list|)
operator|*
name|StrictMath
operator|.
name|cos
argument_list|(
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lat2
argument_list|)
argument_list|)
operator|*
name|h2
decl_stmt|;
return|return
literal|2
operator|*
literal|6371008.7714
operator|*
name|StrictMath
operator|.
name|asin
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
name|h
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

