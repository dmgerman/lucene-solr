begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
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
operator|.
name|tree
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
name|Collection
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
name|NoSuchElementException
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|impl
operator|.
name|RectangleImpl
import|;
end_import

begin_comment
comment|/**  * Uses a compact binary representation of 8 bytes to encode a spatial quad trie.  *  * The binary representation is as follows:  *<pre>  * CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCDDDDDL  *  * Where C = Cell bits (2 per quad)  *       D = Depth bits (5 with max of 29 levels)  *       L = isLeaf bit  *</pre>  *  * It includes a built-in "pruneLeafyBranches" setting (true by default) similar to  * {@link org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy#setPruneLeafyBranches(boolean)} although  * this one only prunes at the target detail level (where it has the most effect).  Usually you should disable RPT's  * prune, since it is very memory in-efficient.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PackedQuadPrefixTree
specifier|public
class|class
name|PackedQuadPrefixTree
extends|extends
name|QuadPrefixTree
block|{
DECL|field|MAX_LEVELS_POSSIBLE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LEVELS_POSSIBLE
init|=
literal|29
decl_stmt|;
DECL|field|QUAD
specifier|protected
specifier|static
specifier|final
name|byte
index|[]
name|QUAD
init|=
operator|new
name|byte
index|[]
block|{
literal|0x00
block|,
literal|0x01
block|,
literal|0x02
block|,
literal|0x03
block|}
decl_stmt|;
DECL|field|leafyPrune
specifier|protected
name|boolean
name|leafyPrune
init|=
literal|true
decl_stmt|;
comment|/**    * Factory for creating {@link PackedQuadPrefixTree} instances with useful defaults.    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|QuadPrefixTree
operator|.
name|Factory
block|{
annotation|@
name|Override
DECL|method|newSPT
specifier|protected
name|SpatialPrefixTree
name|newSPT
parameter_list|()
block|{
return|return
operator|new
name|PackedQuadPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
operator|!=
literal|null
condition|?
name|maxLevels
else|:
name|MAX_LEVELS_POSSIBLE
argument_list|)
return|;
block|}
block|}
DECL|method|PackedQuadPrefixTree
specifier|public
name|PackedQuadPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLevels
operator|>
name|MAX_LEVELS_POSSIBLE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLevels of "
operator|+
name|maxLevels
operator|+
literal|" exceeds limit of "
operator|+
name|MAX_LEVELS_POSSIBLE
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(maxLevels:"
operator|+
name|maxLevels
operator|+
literal|",ctx:"
operator|+
name|ctx
operator|+
literal|",prune:"
operator|+
name|leafyPrune
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getWorldCell
specifier|public
name|Cell
name|getWorldCell
parameter_list|()
block|{
return|return
operator|new
name|PackedQuadCell
argument_list|(
literal|0x0L
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCell
specifier|public
name|Cell
name|getCell
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|build
argument_list|(
name|xmid
argument_list|,
name|ymid
argument_list|,
literal|0
argument_list|,
name|cells
argument_list|,
literal|0x0L
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|)
argument_list|,
name|level
argument_list|)
expr_stmt|;
return|return
name|cells
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
comment|//note cells could be longer if p on edge
block|}
DECL|method|build
specifier|protected
name|void
name|build
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|int
name|level
parameter_list|,
name|List
argument_list|<
name|Cell
argument_list|>
name|matches
parameter_list|,
name|long
name|term
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|maxLevel
parameter_list|)
block|{
name|double
name|w
init|=
name|levelW
index|[
name|level
index|]
operator|/
literal|2
decl_stmt|;
name|double
name|h
init|=
name|levelH
index|[
name|level
index|]
operator|/
literal|2
decl_stmt|;
comment|// Z-Order
comment|// http://en.wikipedia.org/wiki/Z-order_%28curve%29
name|checkBattenberg
argument_list|(
name|QUAD
index|[
literal|0
index|]
argument_list|,
name|x
operator|-
name|w
argument_list|,
name|y
operator|+
name|h
argument_list|,
name|level
argument_list|,
name|matches
argument_list|,
name|term
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
name|QUAD
index|[
literal|1
index|]
argument_list|,
name|x
operator|+
name|w
argument_list|,
name|y
operator|+
name|h
argument_list|,
name|level
argument_list|,
name|matches
argument_list|,
name|term
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
name|QUAD
index|[
literal|2
index|]
argument_list|,
name|x
operator|-
name|w
argument_list|,
name|y
operator|-
name|h
argument_list|,
name|level
argument_list|,
name|matches
argument_list|,
name|term
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
name|QUAD
index|[
literal|3
index|]
argument_list|,
name|x
operator|+
name|w
argument_list|,
name|y
operator|-
name|h
argument_list|,
name|level
argument_list|,
name|matches
argument_list|,
name|term
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
block|}
DECL|method|checkBattenberg
specifier|protected
name|void
name|checkBattenberg
parameter_list|(
name|byte
name|quad
parameter_list|,
name|double
name|cx
parameter_list|,
name|double
name|cy
parameter_list|,
name|int
name|level
parameter_list|,
name|List
argument_list|<
name|Cell
argument_list|>
name|matches
parameter_list|,
name|long
name|term
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|maxLevel
parameter_list|)
block|{
comment|// short-circuit if we find a match for the point (no need to continue recursion)
if|if
condition|(
name|shape
operator|instanceof
name|Point
operator|&&
operator|!
name|matches
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
name|double
name|w
init|=
name|levelW
index|[
name|level
index|]
operator|/
literal|2
decl_stmt|;
name|double
name|h
init|=
name|levelH
index|[
name|level
index|]
operator|/
literal|2
decl_stmt|;
name|SpatialRelation
name|v
init|=
name|shape
operator|.
name|relate
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|cx
operator|-
name|w
argument_list|,
name|cx
operator|+
name|w
argument_list|,
name|cy
operator|-
name|h
argument_list|,
name|cy
operator|+
name|h
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|SpatialRelation
operator|.
name|DISJOINT
operator|==
name|v
condition|)
block|{
return|return;
block|}
comment|// set bits for next level
name|term
operator||=
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|quad
argument_list|)
operator|)
operator|<<
operator|(
literal|64
operator|-
operator|(
operator|++
name|level
operator|<<
literal|1
operator|)
operator|)
operator|)
expr_stmt|;
comment|// increment level
name|term
operator|=
operator|(
operator|(
name|term
operator|>>>
literal|1
operator|)
operator|+
literal|1
operator|)
operator|<<
literal|1
expr_stmt|;
if|if
condition|(
name|SpatialRelation
operator|.
name|CONTAINS
operator|==
name|v
operator|||
operator|(
name|level
operator|>=
name|maxLevel
operator|)
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
operator|new
name|PackedQuadCell
argument_list|(
name|term
argument_list|,
name|v
operator|.
name|transpose
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// SpatialRelation.WITHIN, SpatialRelation.INTERSECTS
name|build
argument_list|(
name|cx
argument_list|,
name|cy
argument_list|,
name|level
argument_list|,
name|matches
argument_list|,
name|term
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readCell
specifier|public
name|Cell
name|readCell
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|Cell
name|scratch
parameter_list|)
block|{
name|PackedQuadCell
name|cell
init|=
operator|(
name|PackedQuadCell
operator|)
name|scratch
decl_stmt|;
if|if
condition|(
name|cell
operator|==
literal|null
condition|)
name|cell
operator|=
operator|(
name|PackedQuadCell
operator|)
name|getWorldCell
argument_list|()
expr_stmt|;
name|cell
operator|.
name|readCell
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|cell
return|;
block|}
annotation|@
name|Override
DECL|method|getTreeCellIterator
specifier|public
name|CellIterator
name|getTreeCellIterator
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
if|if
condition|(
name|detailLevel
operator|>
name|maxLevels
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"detailLevel:"
operator|+
name|detailLevel
operator|+
literal|" exceed max: "
operator|+
name|maxLevels
argument_list|)
throw|;
block|}
return|return
operator|new
name|PrefixTreeIterator
argument_list|(
name|shape
argument_list|,
operator|(
name|short
operator|)
name|detailLevel
argument_list|)
return|;
block|}
DECL|method|isPruneLeafyBranches
specifier|public
name|boolean
name|isPruneLeafyBranches
parameter_list|()
block|{
return|return
name|leafyPrune
return|;
block|}
comment|/** Like {@link org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy#setPruneLeafyBranches(boolean)}    * but more memory efficient and only applies to the detailLevel, where it has the most effect. */
DECL|method|setPruneLeafyBranches
specifier|public
name|void
name|setPruneLeafyBranches
parameter_list|(
name|boolean
name|pruneLeafyBranches
parameter_list|)
block|{
name|this
operator|.
name|leafyPrune
operator|=
name|pruneLeafyBranches
expr_stmt|;
block|}
comment|/** See binary representation in the javadocs of {@link PackedQuadPrefixTree}. */
DECL|class|PackedQuadCell
specifier|protected
class|class
name|PackedQuadCell
extends|extends
name|QuadCell
block|{
DECL|field|term
specifier|private
name|long
name|term
decl_stmt|;
DECL|method|PackedQuadCell
name|PackedQuadCell
parameter_list|(
name|long
name|term
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|longToByteArray
argument_list|(
name|this
operator|.
name|term
argument_list|,
operator|new
name|byte
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
literal|8
expr_stmt|;
name|readLeafAdjust
argument_list|()
expr_stmt|;
block|}
DECL|method|PackedQuadCell
name|PackedQuadCell
parameter_list|(
name|long
name|term
parameter_list|,
name|SpatialRelation
name|shapeRel
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|shapeRel
operator|=
name|shapeRel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readCell
specifier|protected
name|void
name|readCell
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
name|shapeRel
operator|=
literal|null
expr_stmt|;
name|shape
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
operator|.
name|bytes
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
name|bytes
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
operator|(
name|short
operator|)
name|bytes
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|longFromByteArray
argument_list|(
name|this
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
expr_stmt|;
name|readLeafAdjust
argument_list|()
expr_stmt|;
block|}
DECL|method|getShiftForLevel
specifier|private
specifier|final
name|int
name|getShiftForLevel
parameter_list|(
specifier|final
name|int
name|level
parameter_list|)
block|{
return|return
literal|64
operator|-
operator|(
name|level
operator|<<
literal|1
operator|)
return|;
block|}
DECL|method|isEnd
specifier|public
name|boolean
name|isEnd
parameter_list|(
specifier|final
name|int
name|level
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
return|return
operator|(
name|term
operator|!=
literal|0x0L
operator|&&
operator|(
operator|(
operator|(
operator|(
literal|0x1L
operator|<<
operator|(
name|level
operator|<<
literal|1
operator|)
operator|)
operator|-
literal|1
operator|)
operator|-
operator|(
name|term
operator|>>>
name|shift
operator|)
operator|)
operator|==
literal|0x0L
operator|)
operator|)
return|;
block|}
comment|/**      * Get the next cell in the tree without using recursion. descend parameter requests traversal to the child nodes,      * setting this to false will step to the next sibling.      * Note: This complies with lexicographical ordering, once you've moved to the next sibling there is no backtracking.      */
DECL|method|nextCell
specifier|public
name|PackedQuadCell
name|nextCell
parameter_list|(
name|boolean
name|descend
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
name|getLevel
argument_list|()
decl_stmt|;
specifier|final
name|int
name|shift
init|=
name|getShiftForLevel
argument_list|(
name|level
argument_list|)
decl_stmt|;
comment|// base case: can't go further
if|if
condition|(
operator|(
operator|!
name|descend
operator|&&
name|isEnd
argument_list|(
name|level
argument_list|,
name|shift
argument_list|)
operator|)
operator|||
name|isEnd
argument_list|(
name|maxLevels
argument_list|,
name|getShiftForLevel
argument_list|(
name|maxLevels
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|newTerm
decl_stmt|;
specifier|final
name|boolean
name|isLeaf
init|=
operator|(
name|term
operator|&
literal|0x1L
operator|)
operator|==
literal|0x1L
decl_stmt|;
comment|// if descend requested&& we're not at the maxLevel
if|if
condition|(
operator|(
name|descend
operator|&&
operator|!
name|isLeaf
operator|&&
operator|(
name|level
operator|!=
name|maxLevels
operator|)
operator|)
operator|||
name|level
operator|==
literal|0
condition|)
block|{
comment|// simple case: increment level bits (next level)
name|newTerm
operator|=
operator|(
operator|(
name|term
operator|>>>
literal|1
operator|)
operator|+
literal|0x1L
operator|)
operator|<<
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// we're not descending or we can't descend
name|newTerm
operator|=
name|term
operator|+
operator|(
literal|0x1L
operator|<<
name|shift
operator|)
expr_stmt|;
comment|// we're at the last sibling...force descend
if|if
condition|(
operator|(
operator|(
name|term
operator|>>>
name|shift
operator|)
operator|&
literal|0x3L
operator|)
operator|==
literal|0x3L
condition|)
block|{
comment|// adjust level for number popping up
name|newTerm
operator|=
operator|(
operator|(
name|newTerm
operator|>>>
literal|1
operator|)
operator|-
operator|(
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|newTerm
operator|>>>
name|shift
argument_list|)
operator|>>>
literal|1
operator|)
operator|)
operator|<<
literal|1
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PackedQuadCell
argument_list|(
name|newTerm
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readLeafAdjust
specifier|protected
name|void
name|readLeafAdjust
parameter_list|()
block|{
name|isLeaf
operator|=
operator|(
operator|(
literal|0x1L
operator|)
operator|&
name|term
operator|)
operator|==
literal|0x1L
expr_stmt|;
if|if
condition|(
name|getLevel
argument_list|()
operator|==
name|getMaxLevels
argument_list|()
condition|)
block|{
name|isLeaf
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTokenBytesWithLeaf
specifier|public
name|BytesRef
name|getTokenBytesWithLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|=
name|getTokenBytesNoLeaf
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
block|{
name|result
operator|.
name|bytes
index|[
literal|8
operator|-
literal|1
index|]
operator||=
literal|0x1L
expr_stmt|;
comment|// set leaf
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenBytesNoLeaf
specifier|public
name|BytesRef
name|getTokenBytesNoLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|BytesRef
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|bytes
operator|.
name|length
operator|<
literal|8
condition|)
block|{
name|result
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
literal|8
index|]
expr_stmt|;
block|}
name|result
operator|.
name|bytes
operator|=
name|longToByteArray
argument_list|(
name|this
operator|.
name|term
argument_list|,
name|result
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
literal|8
expr_stmt|;
comment|// no leaf
name|result
operator|.
name|bytes
index|[
literal|8
operator|-
literal|1
index|]
operator|&=
operator|~
literal|1
expr_stmt|;
comment|// clear last bit (leaf bit)
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|compareToNoLeaf
specifier|public
name|int
name|compareToNoLeaf
parameter_list|(
name|Cell
name|fromCell
parameter_list|)
block|{
name|PackedQuadCell
name|b
init|=
operator|(
name|PackedQuadCell
operator|)
name|fromCell
decl_stmt|;
comment|//TODO clear last bit without the condition
specifier|final
name|long
name|thisTerm
init|=
operator|(
operator|(
operator|(
literal|0x1L
operator|)
operator|&
name|term
operator|)
operator|==
literal|0x1L
operator|)
condition|?
name|term
operator|-
literal|1
else|:
name|term
decl_stmt|;
specifier|final
name|long
name|fromTerm
init|=
operator|(
operator|(
operator|(
literal|0x1L
operator|)
operator|&
name|b
operator|.
name|term
operator|)
operator|==
literal|0x1L
operator|)
condition|?
name|b
operator|.
name|term
operator|-
literal|1
else|:
name|b
operator|.
name|term
decl_stmt|;
specifier|final
name|int
name|result
init|=
name|Long
operator|.
name|compareUnsigned
argument_list|(
name|thisTerm
argument_list|,
name|fromTerm
argument_list|)
decl_stmt|;
assert|assert
name|Math
operator|.
name|signum
argument_list|(
name|result
argument_list|)
operator|==
name|Math
operator|.
name|signum
argument_list|(
name|compare
argument_list|(
name|longToByteArray
argument_list|(
name|thisTerm
argument_list|,
operator|new
name|byte
index|[
literal|8
index|]
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|,
name|longToByteArray
argument_list|(
name|fromTerm
argument_list|,
operator|new
name|byte
index|[
literal|8
index|]
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
argument_list|)
assert|;
comment|// TODO remove
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getLevel
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
name|int
name|l
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|term
operator|>>>
literal|1
operator|)
operator|&
literal|0x1FL
argument_list|)
decl_stmt|;
return|return
name|l
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCells
specifier|protected
name|Collection
argument_list|<
name|Cell
argument_list|>
name|getSubCells
parameter_list|()
block|{
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|PackedQuadCell
name|pqc
init|=
operator|(
operator|new
name|PackedQuadCell
argument_list|(
operator|(
operator|(
name|term
operator|&
literal|0x1
operator|)
operator|==
literal|0x1
operator|)
condition|?
name|this
operator|.
name|term
operator|-
literal|1
else|:
name|this
operator|.
name|term
argument_list|)
operator|)
operator|.
name|nextCell
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|cells
operator|.
name|add
argument_list|(
name|pqc
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|(
name|pqc
operator|=
name|pqc
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|(
name|pqc
operator|=
name|pqc
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
name|pqc
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cells
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCell
specifier|protected
name|QuadCell
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
return|return
operator|(
name|PackedQuadCell
operator|)
name|PackedQuadPrefixTree
operator|.
name|this
operator|.
name|getCell
argument_list|(
name|p
argument_list|,
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
return|;
comment|//not performant!
block|}
annotation|@
name|Override
DECL|method|isPrefixOf
specifier|public
name|boolean
name|isPrefixOf
parameter_list|(
name|Cell
name|c
parameter_list|)
block|{
name|PackedQuadCell
name|cell
init|=
operator|(
name|PackedQuadCell
operator|)
name|c
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|term
operator|==
literal|0x0L
operator|)
operator|||
name|isInternalPrefix
argument_list|(
name|cell
argument_list|)
return|;
block|}
DECL|method|isInternalPrefix
specifier|protected
name|boolean
name|isInternalPrefix
parameter_list|(
name|PackedQuadCell
name|c
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
literal|64
operator|-
operator|(
name|getLevel
argument_list|()
operator|<<
literal|1
operator|)
decl_stmt|;
return|return
operator|(
operator|(
name|term
operator|>>>
name|shift
operator|)
operator|-
operator|(
name|c
operator|.
name|term
operator|>>>
name|shift
operator|)
operator|)
operator|==
literal|0x0L
return|;
block|}
DECL|method|concat
specifier|protected
name|long
name|concat
parameter_list|(
name|byte
name|postfix
parameter_list|)
block|{
comment|// extra leaf bit
return|return
name|this
operator|.
name|term
operator||
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|postfix
argument_list|)
operator|)
operator|<<
operator|(
operator|(
name|getMaxLevels
argument_list|()
operator|-
name|getLevel
argument_list|()
operator|<<
literal|1
operator|)
operator|+
literal|6
operator|)
operator|)
return|;
block|}
comment|/**      * Constructs a bounding box shape out of the encoded cell      */
annotation|@
name|Override
DECL|method|makeShape
specifier|protected
name|Rectangle
name|makeShape
parameter_list|()
block|{
name|double
name|xmin
init|=
name|PackedQuadPrefixTree
operator|.
name|this
operator|.
name|xmin
decl_stmt|;
name|double
name|ymin
init|=
name|PackedQuadPrefixTree
operator|.
name|this
operator|.
name|ymin
decl_stmt|;
name|int
name|level
init|=
name|getLevel
argument_list|()
decl_stmt|;
name|byte
name|b
decl_stmt|;
for|for
control|(
name|short
name|l
init|=
literal|0
init|,
name|i
init|=
literal|1
init|;
name|l
operator|<
name|level
condition|;
operator|++
name|l
operator|,
operator|++
name|i
control|)
block|{
name|b
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|term
operator|>>>
operator|(
literal|64
operator|-
operator|(
name|i
operator|<<
literal|1
operator|)
operator|)
operator|)
operator|&
literal|0x3L
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|b
condition|)
block|{
case|case
literal|0x00
case|:
name|ymin
operator|+=
name|levelH
index|[
name|l
index|]
expr_stmt|;
break|break;
case|case
literal|0x01
case|:
name|xmin
operator|+=
name|levelW
index|[
name|l
index|]
expr_stmt|;
name|ymin
operator|+=
name|levelH
index|[
name|l
index|]
expr_stmt|;
break|break;
case|case
literal|0x02
case|:
break|break;
comment|//nothing really
case|case
literal|0x03
case|:
name|xmin
operator|+=
name|levelW
index|[
name|l
index|]
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected quadrant"
argument_list|)
throw|;
block|}
block|}
name|double
name|width
decl_stmt|,
name|height
decl_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|levelW
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
name|height
operator|=
name|levelH
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|width
operator|=
name|gridW
expr_stmt|;
name|height
operator|=
name|gridH
expr_stmt|;
block|}
return|return
operator|new
name|RectangleImpl
argument_list|(
name|xmin
argument_list|,
name|xmin
operator|+
name|width
argument_list|,
name|ymin
argument_list|,
name|ymin
operator|+
name|height
argument_list|,
name|ctx
argument_list|)
return|;
block|}
DECL|method|fromBytes
specifier|private
name|long
name|fromBytes
parameter_list|(
name|byte
name|b1
parameter_list|,
name|byte
name|b2
parameter_list|,
name|byte
name|b3
parameter_list|,
name|byte
name|b4
parameter_list|,
name|byte
name|b5
parameter_list|,
name|byte
name|b6
parameter_list|,
name|byte
name|b7
parameter_list|,
name|byte
name|b8
parameter_list|)
block|{
return|return
operator|(
operator|(
name|long
operator|)
name|b1
operator|&
literal|255L
operator|)
operator|<<
literal|56
operator||
operator|(
operator|(
name|long
operator|)
name|b2
operator|&
literal|255L
operator|)
operator|<<
literal|48
operator||
operator|(
operator|(
name|long
operator|)
name|b3
operator|&
literal|255L
operator|)
operator|<<
literal|40
operator||
operator|(
operator|(
name|long
operator|)
name|b4
operator|&
literal|255L
operator|)
operator|<<
literal|32
operator||
operator|(
operator|(
name|long
operator|)
name|b5
operator|&
literal|255L
operator|)
operator|<<
literal|24
operator||
operator|(
operator|(
name|long
operator|)
name|b6
operator|&
literal|255L
operator|)
operator|<<
literal|16
operator||
operator|(
operator|(
name|long
operator|)
name|b7
operator|&
literal|255L
operator|)
operator|<<
literal|8
operator||
operator|(
name|long
operator|)
name|b8
operator|&
literal|255L
return|;
block|}
DECL|method|longToByteArray
specifier|private
name|byte
index|[]
name|longToByteArray
parameter_list|(
name|long
name|value
parameter_list|,
name|byte
index|[]
name|result
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|7
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
call|(
name|int
call|)
argument_list|(
name|value
operator|&
literal|255L
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|>>=
literal|8
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|longFromByteArray
specifier|private
name|long
name|longFromByteArray
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|ofs
parameter_list|)
block|{
assert|assert
name|bytes
operator|.
name|length
operator|>=
literal|8
assert|;
return|return
name|fromBytes
argument_list|(
name|bytes
index|[
literal|0
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|1
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|2
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|3
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|4
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|5
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|6
operator|+
name|ofs
index|]
argument_list|,
name|bytes
index|[
literal|7
operator|+
name|ofs
index|]
argument_list|)
return|;
block|}
comment|/**      * Used for debugging, this will print the bits of the cell      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|(
literal|64
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numberOfLeadingZeros
init|=
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|term
argument_list|)
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
name|numberOfLeadingZeros
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|term
operator|!=
literal|0
condition|)
name|s
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toBinaryString
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|// PackedQuadCell
comment|/** This is a streamlined version of TreeCellIterator, with built-in support to prune at detailLevel    * (but not recursively upwards). */
DECL|class|PrefixTreeIterator
specifier|protected
class|class
name|PrefixTreeIterator
extends|extends
name|CellIterator
block|{
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
DECL|field|thisCell
specifier|private
name|PackedQuadCell
name|thisCell
decl_stmt|;
DECL|field|nextCell
specifier|private
name|PackedQuadCell
name|nextCell
decl_stmt|;
DECL|field|level
specifier|private
name|short
name|level
decl_stmt|;
DECL|field|detailLevel
specifier|private
specifier|final
name|short
name|detailLevel
decl_stmt|;
DECL|field|pruneIter
specifier|private
name|CellIterator
name|pruneIter
decl_stmt|;
DECL|method|PrefixTreeIterator
name|PrefixTreeIterator
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|short
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
name|this
operator|.
name|thisCell
operator|=
operator|(
call|(
name|PackedQuadCell
call|)
argument_list|(
name|getWorldCell
argument_list|()
argument_list|)
operator|)
operator|.
name|nextCell
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
name|this
operator|.
name|nextCell
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|nextCell
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|SpatialRelation
name|rel
decl_stmt|;
comment|// loop until we're at the end of the quad tree or we hit a relation
while|while
condition|(
name|thisCell
operator|!=
literal|null
condition|)
block|{
name|rel
operator|=
name|thisCell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|shape
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
condition|)
block|{
name|thisCell
operator|=
name|thisCell
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// within || intersects || contains
name|thisCell
operator|.
name|setShapeRel
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|nextCell
operator|=
name|thisCell
expr_stmt|;
if|if
condition|(
name|rel
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
block|{
name|thisCell
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
name|thisCell
operator|=
name|thisCell
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// intersects || contains
name|level
operator|=
call|(
name|short
call|)
argument_list|(
name|thisCell
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|==
name|detailLevel
operator|||
name|pruned
argument_list|(
name|rel
argument_list|)
condition|)
block|{
name|thisCell
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
block|{
name|thisCell
operator|.
name|setShapeRel
argument_list|(
name|SpatialRelation
operator|.
name|WITHIN
argument_list|)
expr_stmt|;
name|thisCell
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|thisCell
operator|=
name|thisCell
operator|.
name|nextCell
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|thisCell
operator|=
name|thisCell
operator|.
name|nextCell
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
return|return
name|nextCell
operator|!=
literal|null
return|;
block|}
DECL|method|pruned
specifier|private
name|boolean
name|pruned
parameter_list|(
name|SpatialRelation
name|rel
parameter_list|)
block|{
name|int
name|leaves
decl_stmt|;
if|if
condition|(
name|rel
operator|==
name|SpatialRelation
operator|.
name|INTERSECTS
operator|&&
name|leafyPrune
operator|&&
name|level
operator|==
name|detailLevel
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|leaves
operator|=
literal|0
operator|,
name|pruneIter
operator|=
name|thisCell
operator|.
name|getNextLevelCells
argument_list|(
name|shape
argument_list|)
init|;
name|pruneIter
operator|.
name|hasNext
argument_list|()
condition|;
name|pruneIter
operator|.
name|next
argument_list|()
operator|,
operator|++
name|leaves
control|)
empty_stmt|;
return|return
name|leaves
operator|==
literal|4
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Cell
name|next
parameter_list|()
block|{
if|if
condition|(
name|nextCell
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
comment|// overriding since this implementation sets thisCell in hasNext
name|Cell
name|temp
init|=
name|nextCell
decl_stmt|;
name|nextCell
operator|=
literal|null
expr_stmt|;
return|return
name|temp
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//no-op
block|}
block|}
block|}
end_class

end_unit

