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
name|index
operator|.
name|DocValues
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
name|SortedNumericDocValues
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
name|FieldComparator
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
name|LeafFieldComparator
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
name|util
operator|.
name|SloppyMath
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
comment|/**  * Compares documents by distance from an origin point  *<p>  * When the least competitive item on the priority queue changes (setBottom), we recompute  * a bounding box representing competitive distance to the top-N. Then in compareBottom, we can  * quickly reject hits based on bounding box alone without computing distance for every element.  */
end_comment

begin_class
DECL|class|LatLonPointDistanceComparator
class|class
name|LatLonPointDistanceComparator
extends|extends
name|FieldComparator
argument_list|<
name|Double
argument_list|>
implements|implements
name|LeafFieldComparator
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
DECL|field|values
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|field|bottom
name|double
name|bottom
decl_stmt|;
DECL|field|topValue
name|double
name|topValue
decl_stmt|;
DECL|field|currentDocs
name|SortedNumericDocValues
name|currentDocs
decl_stmt|;
comment|// current bounding box(es) for the bottom distance on the PQ.
comment|// these are pre-encoded with LatLonPoint's encoding and
comment|// used to exclude uncompetitive hits faster.
DECL|field|minLon
name|int
name|minLon
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|maxLon
name|int
name|maxLon
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|minLat
name|int
name|minLat
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|maxLat
name|int
name|maxLat
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|// second set of longitude ranges to check (for cross-dateline case)
DECL|field|minLon2
name|int
name|minLon2
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|// the number of times setBottom has been called (adversary protection)
DECL|field|setBottomCounter
name|int
name|setBottomCounter
init|=
literal|0
decl_stmt|;
DECL|method|LatLonPointDistanceComparator
specifier|public
name|LatLonPointDistanceComparator
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
name|int
name|numHits
parameter_list|)
block|{
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
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|values
index|[
name|slot1
index|]
argument_list|,
name|values
index|[
name|slot2
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|bottom
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
comment|// make bounding box(es) to exclude non-competitive hits, but start
comment|// sampling if we get called way too much: don't make gobs of bounding
comment|// boxes if comparator hits a worst case order (e.g. backwards distance order)
if|if
condition|(
name|setBottomCounter
operator|<
literal|1024
operator|||
operator|(
name|setBottomCounter
operator|&
literal|0x3F
operator|)
operator|==
literal|0x3F
condition|)
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
name|haversin2
argument_list|(
name|bottom
argument_list|)
argument_list|)
decl_stmt|;
comment|// pre-encode our box to our integer encoding, so we don't have to decode
comment|// to double values for uncompetitive hits. This has some cost!
name|minLat
operator|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|maxLat
argument_list|)
expr_stmt|;
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
comment|// box1
name|minLon
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
name|maxLon
operator|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
expr_stmt|;
comment|// box2
name|minLon2
operator|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|minLon
operator|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
expr_stmt|;
comment|// disable box2
name|minLon2
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
block|}
name|setBottomCounter
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTopValue
specifier|public
name|void
name|setTopValue
parameter_list|(
name|Double
name|value
parameter_list|)
block|{
name|topValue
operator|=
name|value
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|currentDocs
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|currentDocs
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numValues
operator|==
literal|0
condition|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
return|;
block|}
name|int
name|cmp
init|=
operator|-
literal|1
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|long
name|encoded
init|=
name|currentDocs
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// test bounding box
name|int
name|latitudeBits
init|=
call|(
name|int
call|)
argument_list|(
name|encoded
operator|>>
literal|32
argument_list|)
decl_stmt|;
if|if
condition|(
name|latitudeBits
argument_list|<
name|minLat
operator|||
name|latitudeBits
argument_list|>
name|maxLat
condition|)
block|{
continue|continue;
block|}
name|int
name|longitudeBits
init|=
call|(
name|int
call|)
argument_list|(
name|encoded
operator|&
literal|0xFFFFFFFF
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|longitudeBits
argument_list|<
name|minLon
operator|||
name|longitudeBits
argument_list|>
name|maxLon
operator|)
operator|&&
operator|(
name|longitudeBits
operator|<
name|minLon2
operator|)
condition|)
block|{
continue|continue;
block|}
comment|// only compute actual distance if its inside "competitive bounding box"
name|double
name|docLatitude
init|=
name|decodeLatitude
argument_list|(
name|latitudeBits
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|decodeLongitude
argument_list|(
name|longitudeBits
argument_list|)
decl_stmt|;
name|cmp
operator|=
name|Math
operator|.
name|max
argument_list|(
name|cmp
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// once we compete in the PQ, no need to continue.
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
name|cmp
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|values
index|[
name|slot
index|]
operator|=
name|sortKey
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafComparator
specifier|public
name|LeafFieldComparator
name|getLeafComparator
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
name|FieldInfo
name|info
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
name|info
operator|!=
literal|null
condition|)
block|{
name|LatLonDocValuesField
operator|.
name|checkCompatible
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|currentDocs
operator|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Double
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|haversin2
argument_list|(
name|values
index|[
name|slot
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|topValue
argument_list|,
name|haversin2
argument_list|(
name|sortKey
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|// TODO: optimize for single-valued case?
comment|// TODO: do all kinds of other optimizations!
DECL|method|sortKey
name|double
name|sortKey
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|currentDocs
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|double
name|minValue
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|int
name|numValues
init|=
name|currentDocs
operator|.
name|count
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|long
name|encoded
init|=
name|currentDocs
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|double
name|docLatitude
init|=
name|decodeLatitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|>>
literal|32
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|decodeLongitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|&
literal|0xFFFFFFFF
argument_list|)
argument_list|)
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|minValue
return|;
block|}
comment|// second half of the haversin calculation, used to convert results from haversin1 (used internally
comment|// for sorting) for display purposes.
DECL|method|haversin2
specifier|static
name|double
name|haversin2
parameter_list|(
name|double
name|partial
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|partial
argument_list|)
condition|)
block|{
return|return
name|partial
return|;
block|}
return|return
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|partial
argument_list|)
return|;
block|}
block|}
end_class

end_unit

