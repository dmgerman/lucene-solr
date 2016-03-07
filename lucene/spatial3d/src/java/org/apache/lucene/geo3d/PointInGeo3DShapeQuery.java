begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
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

begin_comment
comment|/** Finds all previously indexed points that fall within the specified polygon.  *  *<p>The field must be indexed using {@link Geo3DPoint}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PointInGeo3DShapeQuery
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
name|shape
operator|=
name|shape
expr_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|BasePlanetObject
condition|)
block|{
name|BasePlanetObject
name|planetObject
init|=
operator|(
name|BasePlanetObject
operator|)
name|shape
decl_stmt|;
if|if
condition|(
name|planetObject
operator|.
name|getPlanetModel
argument_list|()
operator|.
name|equals
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this qurey requires PlanetModel.WGS84, but got: "
operator|+
name|planetObject
operator|.
name|getPlanetModel
argument_list|()
argument_list|)
throw|;
block|}
block|}
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
name|PointValues
name|values
init|=
name|reader
operator|.
name|getPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|/*         XYZBounds bounds = new XYZBounds();         shape.getBounds(bounds);          final double planetMax = planetModel.getMaximumMagnitude();         if (planetMax != treeDV.planetMax) {           throw new IllegalStateException(planetModel + " is not the same one used during indexing: planetMax=" + planetMax + " vs indexing planetMax=" + treeDV.planetMax);         }         */
comment|/*         GeoArea xyzSolid = GeoAreaFactory.makeGeoArea(planetModel,                                                       bounds.getMinimumX(),                                                       bounds.getMaximumX(),                                                       bounds.getMinimumY(),                                                       bounds.getMaximumY(),                                                       bounds.getMinimumZ(),                                                       bounds.getMaximumZ());          assert xyzSolid.getRelationship(shape) == GeoArea.WITHIN || xyzSolid.getRelationship(shape) == GeoArea.OVERLAPS: "expected WITHIN (1) or OVERLAPS (2) but got " + xyzSolid.getRelationship(shape) + "; shape="+shape+"; XYZSolid="+xyzSolid;         */
name|double
name|planetMax
init|=
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumMagnitude
argument_list|()
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|values
operator|.
name|intersect
argument_list|(
name|field
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
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
name|result
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
assert|assert
name|packedValue
operator|.
name|length
operator|==
literal|12
assert|;
name|double
name|x
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|y
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|z
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
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
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// Because the dimensional format operates in quantized (64 bit -> 32 bit) space, and the cell bounds
comment|// here are inclusive, we need to extend the bounds to the largest un-quantized values that
comment|// could quantize into these bounds.  The encoding (Geo3DUtil.encodeValue) does
comment|// a Math.round from double to long, so e.g. 1.4 -> 1, and -1.4 -> -1:
name|double
name|xMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueMin
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|xMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueMax
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|yMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueMin
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|1
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|yMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueMax
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|1
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|zMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueMin
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|zMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueMax
argument_list|(
name|planetMax
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println("  compare: x=" + cellXMin + "-" + cellXMax + " y=" + cellYMin + "-" + cellYMax + " z=" + cellZMin + "-" + cellZMax);
assert|assert
name|xMin
operator|<=
name|xMax
assert|;
assert|assert
name|yMin
operator|<=
name|yMax
assert|;
assert|assert
name|zMin
operator|<=
name|zMax
assert|;
name|GeoArea
name|xyzSolid
init|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|,
name|yMin
argument_list|,
name|yMax
argument_list|,
name|zMin
argument_list|,
name|zMax
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
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
case|case
name|GeoArea
operator|.
name|OVERLAPS
case|:
comment|// They do overlap but neither contains the other:
comment|//System.out.println("    crosses1");
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
case|case
name|GeoArea
operator|.
name|WITHIN
case|:
comment|// Cell fully contains the shape:
comment|//System.out.println("    crosses2");
comment|// return Relation.SHAPE_INSIDE_CELL;
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
case|case
name|GeoArea
operator|.
name|DISJOINT
case|:
comment|// They do not overlap at all
comment|//System.out.println("    outside");
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
default|default:
assert|assert
literal|false
assert|;
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
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
DECL|method|getShape
specifier|public
name|GeoShape
name|getShape
parameter_list|()
block|{
return|return
name|shape
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

