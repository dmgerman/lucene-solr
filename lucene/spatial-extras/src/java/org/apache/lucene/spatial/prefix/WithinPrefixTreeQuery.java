begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
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
name|distance
operator|.
name|DistanceUtils
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
name|Circle
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
name|Point
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
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
name|SpatialRelation
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|CellIterator
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
name|SpatialPrefixTree
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
name|BitDocIdSet
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
name|FixedBitSet
import|;
end_import

begin_comment
comment|/**  * Finds docs where its indexed shape is {@link org.apache.lucene.spatial.query.SpatialOperation#IsWithin  * WITHIN} the query shape.  It works by looking at cells outside of the query  * shape to ensure documents there are excluded. By default, it will  * examine all cells, and it's fairly slow.  If you know that the indexed shapes  * are never comprised of multiple disjoint parts (which also means it is not multi-valued),  * then you can pass {@code SpatialPrefixTree.getDistanceForLevel(maxLevels)} as  * the {@code queryBuffer} constructor parameter to minimally look this distance  * beyond the query shape's edge.  Even if the indexed shapes are sometimes  * comprised of multiple disjoint parts, you might want to use this option with  * a large buffer as a faster approximation with minimal false-positives.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|WithinPrefixTreeQuery
specifier|public
class|class
name|WithinPrefixTreeQuery
extends|extends
name|AbstractVisitingPrefixTreeQuery
block|{
comment|//TODO LUCENE-4869: implement faster algorithm based on filtering out false-positives of a
comment|//  minimal query buffer by looking in a DocValues cache holding a representative
comment|//  point of each disjoint component of a document's shape(s).
comment|//TODO Could the recursion in allCellsIntersectQuery() be eliminated when non-fuzzy or other
comment|//  circumstances?
DECL|field|bufferedQueryShape
specifier|private
specifier|final
name|Shape
name|bufferedQueryShape
decl_stmt|;
comment|//if null then the whole world
comment|/**    * See {@link AbstractVisitingPrefixTreeQuery#AbstractVisitingPrefixTreeQuery(org.locationtech.spatial4j.shape.Shape, String, org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree, int, int)}.    * {@code queryBuffer} is the (minimum) distance beyond the query shape edge    * where non-matching documents are looked for so they can be excluded. If    * -1 is used then the whole world is examined (a good default for correctness).    */
DECL|method|WithinPrefixTreeQuery
specifier|public
name|WithinPrefixTreeQuery
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|,
name|double
name|queryBuffer
parameter_list|)
block|{
name|super
argument_list|(
name|queryShape
argument_list|,
name|fieldName
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferedQueryShape
operator|=
name|queryBuffer
operator|==
operator|-
literal|1
condition|?
literal|null
else|:
name|bufferShape
argument_list|(
name|queryShape
argument_list|,
name|queryBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
comment|//checks getClass == o.getClass& instanceof
name|WithinPrefixTreeQuery
name|that
init|=
operator|(
name|WithinPrefixTreeQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|bufferedQueryShape
operator|!=
literal|null
condition|?
operator|!
name|bufferedQueryShape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|bufferedQueryShape
argument_list|)
else|:
name|that
operator|.
name|bufferedQueryShape
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
operator|(
name|bufferedQueryShape
operator|!=
literal|null
condition|?
name|bufferedQueryShape
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
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
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
literal|"fieldName="
operator|+
name|fieldName
operator|+
literal|","
operator|+
literal|"queryShape="
operator|+
name|queryShape
operator|+
literal|","
operator|+
literal|"detailLevel="
operator|+
name|detailLevel
operator|+
literal|","
operator|+
literal|"prefixGridScanLevel="
operator|+
name|prefixGridScanLevel
operator|+
literal|")"
return|;
block|}
comment|/** Returns a new shape that is larger than shape by at distErr.    */
comment|//TODO move this generic code elsewhere?  Spatial4j?
DECL|method|bufferShape
specifier|protected
name|Shape
name|bufferShape
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|double
name|distErr
parameter_list|)
block|{
if|if
condition|(
name|distErr
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"distErr must be> 0"
argument_list|)
throw|;
name|SpatialContext
name|ctx
init|=
name|grid
operator|.
name|getSpatialContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
block|{
return|return
name|ctx
operator|.
name|makeCircle
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|,
name|distErr
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|shape
operator|instanceof
name|Circle
condition|)
block|{
name|Circle
name|circle
init|=
operator|(
name|Circle
operator|)
name|shape
decl_stmt|;
name|double
name|newDist
init|=
name|circle
operator|.
name|getRadius
argument_list|()
operator|+
name|distErr
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|isGeo
argument_list|()
operator|&&
name|newDist
operator|>
literal|180
condition|)
name|newDist
operator|=
literal|180
expr_stmt|;
return|return
name|ctx
operator|.
name|makeCircle
argument_list|(
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|,
name|newDist
argument_list|)
return|;
block|}
else|else
block|{
name|Rectangle
name|bbox
init|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|double
name|newMinX
init|=
name|bbox
operator|.
name|getMinX
argument_list|()
operator|-
name|distErr
decl_stmt|;
name|double
name|newMaxX
init|=
name|bbox
operator|.
name|getMaxX
argument_list|()
operator|+
name|distErr
decl_stmt|;
name|double
name|newMinY
init|=
name|bbox
operator|.
name|getMinY
argument_list|()
operator|-
name|distErr
decl_stmt|;
name|double
name|newMaxY
init|=
name|bbox
operator|.
name|getMaxY
argument_list|()
operator|+
name|distErr
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
if|if
condition|(
name|newMinY
operator|<
operator|-
literal|90
condition|)
name|newMinY
operator|=
operator|-
literal|90
expr_stmt|;
if|if
condition|(
name|newMaxY
operator|>
literal|90
condition|)
name|newMaxY
operator|=
literal|90
expr_stmt|;
if|if
condition|(
name|newMinY
operator|==
operator|-
literal|90
operator|||
name|newMaxY
operator|==
literal|90
operator|||
name|bbox
operator|.
name|getWidth
argument_list|()
operator|+
literal|2
operator|*
name|distErr
operator|>
literal|360
condition|)
block|{
name|newMinX
operator|=
operator|-
literal|180
expr_stmt|;
name|newMaxX
operator|=
literal|180
expr_stmt|;
block|}
else|else
block|{
name|newMinX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|newMinX
argument_list|)
expr_stmt|;
name|newMaxX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|newMaxX
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//restrict to world bounds
name|newMinX
operator|=
name|Math
operator|.
name|max
argument_list|(
name|newMinX
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMinX
argument_list|()
argument_list|)
expr_stmt|;
name|newMaxX
operator|=
name|Math
operator|.
name|min
argument_list|(
name|newMaxX
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMaxX
argument_list|()
argument_list|)
expr_stmt|;
name|newMinY
operator|=
name|Math
operator|.
name|max
argument_list|(
name|newMinY
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
name|newMaxY
operator|=
name|Math
operator|.
name|min
argument_list|(
name|newMaxY
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getMaxY
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|newMinX
argument_list|,
name|newMaxX
argument_list|,
name|newMinY
argument_list|,
name|newMaxY
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|protected
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VisitorTemplate
argument_list|(
name|context
argument_list|)
block|{
specifier|private
name|FixedBitSet
name|inside
decl_stmt|;
specifier|private
name|FixedBitSet
name|outside
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|start
parameter_list|()
block|{
name|inside
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|outside
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocIdSet
name|finish
parameter_list|()
block|{
name|inside
operator|.
name|andNot
argument_list|(
name|outside
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|inside
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|CellIterator
name|findSubCellsToVisit
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
comment|//use buffered query shape instead of orig.  Works with null too.
return|return
name|cell
operator|.
name|getNextLevelCells
argument_list|(
name|bufferedQueryShape
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|visitPrefix
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
comment|//cell.relate is based on the bufferedQueryShape; we need to examine what
comment|// the relation is against the queryShape
name|SpatialRelation
name|visitRelation
init|=
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
decl_stmt|;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|collectDocs
argument_list|(
name|visitRelation
operator|.
name|intersects
argument_list|()
condition|?
name|inside
else|:
name|outside
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|visitRelation
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
block|{
name|collectDocs
argument_list|(
name|inside
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|visitRelation
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
condition|)
block|{
name|collectDocs
argument_list|(
name|outside
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|allCellsIntersectQuery
argument_list|(
name|cell
argument_list|)
condition|)
name|collectDocs
argument_list|(
name|inside
argument_list|)
expr_stmt|;
else|else
name|collectDocs
argument_list|(
name|outside
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true if the provided cell, and all its sub-cells down to        * detailLevel all intersect the queryShape.        */
specifier|private
name|boolean
name|allCellsIntersectQuery
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
name|SpatialRelation
name|relate
init|=
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
decl_stmt|;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
return|return
name|relate
operator|.
name|intersects
argument_list|()
return|;
if|if
condition|(
name|relate
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|relate
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
condition|)
return|return
literal|false
return|;
comment|// Note: Generating all these cells just to determine intersection is not ideal.
comment|// The real solution is LUCENE-4869.
name|CellIterator
name|subCells
init|=
name|cell
operator|.
name|getNextLevelCells
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|subCells
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Cell
name|subCell
init|=
name|subCells
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|allCellsIntersectQuery
argument_list|(
name|subCell
argument_list|)
condition|)
comment|//recursion
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitScanned
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
name|visitLeaf
argument_list|(
name|cell
argument_list|)
expr_stmt|;
comment|//collects as we want, even if not a leaf
comment|//        if (cell.isLeaf()) {
comment|//          visitLeaf(cell);
comment|//        } else {
comment|//          visitPrefix(cell);
comment|//        }
block|}
block|}
operator|.
name|getDocIdSet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

