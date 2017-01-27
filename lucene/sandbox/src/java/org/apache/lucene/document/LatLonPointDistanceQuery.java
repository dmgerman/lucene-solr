begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
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
name|GeoUtils
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
name|index
operator|.
name|FieldInfo
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
name|LeafReader
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
name|LeafReaderContext
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
name|PointValues
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
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|search
operator|.
name|ConstantScoreScorer
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
name|search
operator|.
name|ConstantScoreWeight
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|ScorerSupplier
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
name|search
operator|.
name|Weight
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
name|DocIdSetBuilder
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
name|NumericUtils
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
name|StringHelper
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
name|GeoEncodingUtils
operator|.
name|decodeLatitude
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
name|GeoEncodingUtils
operator|.
name|decodeLongitude
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
name|GeoEncodingUtils
operator|.
name|encodeLatitude
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
name|GeoEncodingUtils
operator|.
name|encodeLongitude
import|;
end_import

begin_comment
comment|/**  * Distance query for {@link LatLonPoint}.  */
end_comment

begin_class
DECL|class|LatLonPointDistanceQuery
specifier|final
class|class
name|LatLonPointDistanceQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|latitude
specifier|final
name|double
name|latitude
decl_stmt|;
DECL|field|longitude
specifier|final
name|double
name|longitude
decl_stmt|;
DECL|field|radiusMeters
specifier|final
name|double
name|radiusMeters
decl_stmt|;
DECL|method|LatLonPointDistanceQuery
specifier|public
name|LatLonPointDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Double
operator|.
name|isFinite
argument_list|(
name|radiusMeters
argument_list|)
operator|==
literal|false
operator|||
name|radiusMeters
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"radiusMeters: '"
operator|+
name|radiusMeters
operator|+
literal|"' is invalid"
argument_list|)
throw|;
block|}
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|longitude
expr_stmt|;
name|this
operator|.
name|radiusMeters
operator|=
name|radiusMeters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|Rectangle
name|box
init|=
name|Rectangle
operator|.
name|fromPointDistance
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
comment|// create bounding box(es) for the distance range
comment|// these are pre-encoded with LatLonPoint's encoding
specifier|final
name|byte
name|minLat
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|maxLat
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|minLon
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|maxLon
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
comment|// second set of longitude ranges to check (for cross-dateline case)
specifier|final
name|byte
name|minLon2
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLatitude
argument_list|(
name|box
operator|.
name|minLat
argument_list|)
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLatitude
argument_list|(
name|box
operator|.
name|maxLat
argument_list|)
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// crosses dateline: split
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
comment|// box1
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// box2
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// disable box2
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// compute exact sort key: avoid any asin() computations
specifier|final
name|double
name|sortKey
init|=
name|GeoUtils
operator|.
name|distanceQuerySortKey
argument_list|(
name|radiusMeters
argument_list|)
decl_stmt|;
specifier|final
name|double
name|axisLat
init|=
name|Rectangle
operator|.
name|axisLat
argument_list|(
name|latitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
argument_list|)
block|{
specifier|final
name|GeoEncodingUtils
operator|.
name|DistancePredicate
name|distancePredicate
init|=
name|GeoEncodingUtils
operator|.
name|createDistancePredicate
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ScorerSupplier
name|scorerSupplier
init|=
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorerSupplier
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|scorerSupplier
operator|.
name|get
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScorerSupplier
name|scorerSupplier
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|PointValues
name|values
init|=
name|reader
operator|.
name|getPointValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment had any points fields
return|return
literal|null
return|;
block|}
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed this field at all
return|return
literal|null
return|;
block|}
name|LatLonPoint
operator|.
name|checkCompatible
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|// matching docids
name|DocIdSetBuilder
name|result
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|values
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|IntersectVisitor
name|visitor
init|=
operator|new
name|IntersectVisitor
argument_list|()
block|{
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|adder
operator|=
name|result
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|adder
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
comment|// bounding box check
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// latitude out of bounding box range
return|return;
block|}
if|if
condition|(
operator|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// longitude out of bounding box range
return|return;
block|}
name|int
name|docLatitude
init|=
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|docLongitude
init|=
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|distancePredicate
operator|.
name|apply
argument_list|(
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
condition|)
block|{
name|adder
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
comment|// algorithm: we create a bounding box (two bounding boxes if we cross the dateline).
comment|// 1. check our bounding box(es) first. if the subtree is entirely outside of those, bail.
comment|// 2. check if the subtree is disjoint. it may cross the bounding box but not intersect with circle
comment|// 3. see if the subtree is fully contained. if the subtree is enormous along the x axis, wrapping half way around the world, etc: then this can't work, just go to step 4.
comment|// 4. recurse naively (subtrees crossing over circle edge)
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|minPackedValue
argument_list|,
literal|0
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
literal|0
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// latitude out of bounding box range
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
if|if
condition|(
operator|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// longitude out of bounding box range
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
name|double
name|latMin
init|=
name|decodeLatitude
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|lonMin
init|=
name|decodeLongitude
argument_list|(
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|latMax
init|=
name|decodeLatitude
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|lonMax
init|=
name|decodeLongitude
argument_list|(
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
return|return
name|GeoUtils
operator|.
name|relate
argument_list|(
name|latMin
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|,
name|lonMax
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|,
name|sortKey
argument_list|,
name|axisLat
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Weight
name|weight
init|=
name|this
decl_stmt|;
return|return
operator|new
name|ScorerSupplier
argument_list|()
block|{
name|long
name|cost
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|.
name|intersect
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|weight
argument_list|,
name|score
argument_list|()
argument_list|,
name|result
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
if|if
condition|(
name|cost
operator|==
operator|-
literal|1
condition|)
block|{
name|cost
operator|=
name|values
operator|.
name|estimatePointCount
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
assert|assert
name|cost
operator|>=
literal|0
assert|;
return|return
name|cost
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getLatitude
specifier|public
name|double
name|getLatitude
parameter_list|()
block|{
return|return
name|latitude
return|;
block|}
DECL|method|getLongitude
specifier|public
name|double
name|getLongitude
parameter_list|()
block|{
return|return
name|longitude
return|;
block|}
DECL|method|getRadiusMeters
specifier|public
name|double
name|getRadiusMeters
parameter_list|()
block|{
return|return
name|radiusMeters
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|classHash
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|LatLonPointDistanceQuery
name|other
parameter_list|)
block|{
return|return
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|latitude
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|longitude
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radiusMeters
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|radiusMeters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" +/- "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" meters"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

