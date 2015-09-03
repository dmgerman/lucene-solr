begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.bkdtree3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|bkdtree3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
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
name|geo3d
operator|.
name|GeoAreaFactory
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
name|geo3d
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
name|geo3d
operator|.
name|PlanetModel
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
name|geo3d
operator|.
name|XYZBounds
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
name|BinaryDocValues
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|BytesRef
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
name|ToStringUtils
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

begin_comment
comment|/** Finds all previously indexed points that fall within the specified polygon.  *  *<p>The field must be indexed with {@link Geo3DDocValuesFormat}, and {@link Geo3DPointField} added per document.  *  *<p>Because this implementation cannot intersect each cell with the polygon, it will be costly especially for large polygons, as every  *   possible point must be checked.  *  *<p><b>NOTE</b>: for fastest performance, this allocates FixedBitSet(maxDoc) for each segment.  The score of each hit is the query boost.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PointInGeo3DShapeQuery
specifier|public
class|class
name|PointInGeo3DShapeQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|planetModel
specifier|final
name|PlanetModel
name|planetModel
decl_stmt|;
DECL|field|shape
specifier|final
name|GeoShape
name|shape
decl_stmt|;
comment|/** The lats/lons must be clockwise or counter-clockwise. */
DECL|method|PointInGeo3DShapeQuery
specifier|public
name|PointInGeo3DShapeQuery
parameter_list|(
name|PlanetModel
name|planetModel
parameter_list|,
name|String
name|field
parameter_list|,
name|GeoShape
name|shape
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
name|planetModel
operator|=
name|planetModel
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
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
parameter_list|)
throws|throws
name|IOException
block|{
comment|// I don't use RandomAccessWeight here: it's no good to approximate with "match all docs"; this is an inverted structure and should be
comment|// used in the first pass:
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
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
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|bdv
init|=
name|reader
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bdv
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment had this field
return|return
literal|null
return|;
block|}
if|if
condition|(
name|bdv
operator|instanceof
name|Geo3DBinaryDocValues
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" was not indexed with Geo3DBinaryDocValuesFormat: got: "
operator|+
name|bdv
argument_list|)
throw|;
block|}
specifier|final
name|Geo3DBinaryDocValues
name|treeDV
init|=
operator|(
name|Geo3DBinaryDocValues
operator|)
name|bdv
decl_stmt|;
name|BKD3DTreeReader
name|tree
init|=
name|treeDV
operator|.
name|getBKD3DTreeReader
argument_list|()
decl_stmt|;
name|XYZBounds
name|bounds
init|=
operator|new
name|XYZBounds
argument_list|()
decl_stmt|;
name|shape
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
specifier|final
name|double
name|planetMax
init|=
name|planetModel
operator|.
name|getMaximumMagnitude
argument_list|()
decl_stmt|;
if|if
condition|(
name|planetMax
operator|!=
name|treeDV
operator|.
name|planetMax
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|planetModel
operator|+
literal|" is not the same one used during indexing: planetMax="
operator|+
name|planetMax
operator|+
literal|" vs indexing planetMax="
operator|+
name|treeDV
operator|.
name|planetMax
argument_list|)
throw|;
block|}
comment|/*         GeoArea xyzSolid = GeoAreaFactory.makeGeoArea(planetModel,                                                       bounds.getMinimumX(),                                                       bounds.getMaximumX(),                                                       bounds.getMinimumY(),                                                       bounds.getMaximumY(),                                                       bounds.getMinimumZ(),                                                       bounds.getMaximumZ());          assert xyzSolid.getRelationship(shape) == GeoArea.WITHIN || xyzSolid.getRelationship(shape) == GeoArea.OVERLAPS: "expected WITHIN (1) or OVERLAPS (2) but got " + xyzSolid.getRelationship(shape) + "; shape="+shape+"; XYZSolid="+xyzSolid;         */
name|DocIdSet
name|result
init|=
name|tree
operator|.
name|intersect
argument_list|(
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMinimumX
argument_list|()
argument_list|)
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMaximumX
argument_list|()
argument_list|)
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMinimumY
argument_list|()
argument_list|)
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMaximumY
argument_list|()
argument_list|)
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMinimumZ
argument_list|()
argument_list|)
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|encodeValueLenient
argument_list|(
name|planetMax
argument_list|,
name|bounds
operator|.
name|getMaximumZ
argument_list|()
argument_list|)
argument_list|,
operator|new
name|BKD3DTreeReader
operator|.
name|ValueFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|//System.out.println("  accept? docID=" + docID);
name|BytesRef
name|bytes
init|=
name|treeDV
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("    false (null)");
return|return
literal|false
return|;
block|}
assert|assert
name|bytes
operator|.
name|length
operator|==
literal|12
assert|;
name|double
name|x
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueCenter
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|readInt
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|y
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueCenter
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|readInt
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|z
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueCenter
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|Geo3DDocValuesFormat
operator|.
name|readInt
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
literal|8
argument_list|)
argument_list|)
decl_stmt|;
comment|// System.out.println("  accept docID=" + docID + " point: x=" + x + " y=" + y + " z=" + z);
comment|// True if x,y,z is within shape
comment|//System.out.println("    x=" + x + " y=" + y + " z=" + z);
comment|//System.out.println("    ret: " + shape.isWithin(x, y, z));
return|return
name|shape
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BKD3DTreeReader
operator|.
name|Relation
name|compare
parameter_list|(
name|int
name|cellXMinEnc
parameter_list|,
name|int
name|cellXMaxEnc
parameter_list|,
name|int
name|cellYMinEnc
parameter_list|,
name|int
name|cellYMaxEnc
parameter_list|,
name|int
name|cellZMinEnc
parameter_list|,
name|int
name|cellZMaxEnc
parameter_list|)
block|{
assert|assert
name|cellXMinEnc
operator|<=
name|cellXMaxEnc
assert|;
assert|assert
name|cellYMinEnc
operator|<=
name|cellYMaxEnc
assert|;
assert|assert
name|cellZMinEnc
operator|<=
name|cellZMaxEnc
assert|;
comment|// Because the BKD tree operates in quantized (64 bit -> 32 bit) space, and the cell bounds
comment|// here are inclusive, we need to extend the bounds to the largest un-quantized values that
comment|// could quantize into these bounds.  The encoding (Geo3DDocValuesFormat.encodeValue) does
comment|// a Math.round from double to long, so e.g. 1.4 -> 1, and -1.4 -> -1:
name|double
name|cellXMin
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMin
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellXMinEnc
argument_list|)
decl_stmt|;
name|double
name|cellXMax
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMax
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellXMaxEnc
argument_list|)
decl_stmt|;
name|double
name|cellYMin
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMin
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellYMinEnc
argument_list|)
decl_stmt|;
name|double
name|cellYMax
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMax
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellYMaxEnc
argument_list|)
decl_stmt|;
name|double
name|cellZMin
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMin
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellZMinEnc
argument_list|)
decl_stmt|;
name|double
name|cellZMax
init|=
name|Geo3DDocValuesFormat
operator|.
name|decodeValueMax
argument_list|(
name|treeDV
operator|.
name|planetMax
argument_list|,
name|cellZMaxEnc
argument_list|)
decl_stmt|;
comment|//System.out.println("  compare: x=" + cellXMin + "-" + cellXMax + " y=" + cellYMin + "-" + cellYMax + " z=" + cellZMin + "-" + cellZMax);
name|GeoArea
name|xyzSolid
init|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|planetModel
argument_list|,
name|cellXMin
argument_list|,
name|cellXMax
argument_list|,
name|cellYMin
argument_list|,
name|cellYMax
argument_list|,
name|cellZMin
argument_list|,
name|cellZMax
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|xyzSolid
operator|.
name|getRelationship
argument_list|(
name|shape
argument_list|)
condition|)
block|{
case|case
name|GeoArea
operator|.
name|CONTAINS
case|:
comment|// Shape fully contains the cell
comment|//System.out.println("    inside");
return|return
name|BKD3DTreeReader
operator|.
name|Relation
operator|.
name|CELL_INSIDE_SHAPE
return|;
case|case
name|GeoArea
operator|.
name|OVERLAPS
case|:
comment|// They do overlap but neither contains the other:
comment|//System.out.println("    crosses1");
return|return
name|BKD3DTreeReader
operator|.
name|Relation
operator|.
name|SHAPE_CROSSES_CELL
return|;
case|case
name|GeoArea
operator|.
name|WITHIN
case|:
comment|// Cell fully contains the shape:
comment|//System.out.println("    crosses2");
return|return
name|BKD3DTreeReader
operator|.
name|Relation
operator|.
name|SHAPE_INSIDE_CELL
return|;
case|case
name|GeoArea
operator|.
name|DISJOINT
case|:
comment|// They do not overlap at all
comment|//System.out.println("    outside");
return|return
name|BKD3DTreeReader
operator|.
name|Relation
operator|.
name|SHAPE_OUTSIDE_CELL
return|;
default|default:
assert|assert
literal|false
assert|;
return|return
name|BKD3DTreeReader
operator|.
name|Relation
operator|.
name|SHAPE_CROSSES_CELL
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|disi
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|PointInGeo3DShapeQuery
name|that
init|=
operator|(
name|PointInGeo3DShapeQuery
operator|)
name|o
decl_stmt|;
return|return
name|planetModel
operator|.
name|equals
argument_list|(
name|that
operator|.
name|planetModel
argument_list|)
operator|&&
name|shape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|shape
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|planetModel
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|shape
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
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
literal|"PlanetModel: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" Shape: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
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
