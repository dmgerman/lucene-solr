begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|SpatialContext
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
name|SpatialRelation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * A {@link SpatialPrefixTree} which uses a  *<a href="http://en.wikipedia.org/wiki/Quadtree">quad tree</a> in which an  * indexed term will be generated for each node, 'A', 'B', 'C', 'D'.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|QuadPrefixTree
specifier|public
class|class
name|QuadPrefixTree
extends|extends
name|SpatialPrefixTree
block|{
comment|/**    * Factory for creating {@link QuadPrefixTree} instances with useful defaults    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|SpatialPrefixTreeFactory
block|{
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|protected
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|degrees
parameter_list|)
block|{
name|QuadPrefixTree
name|grid
init|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
name|MAX_LEVELS_POSSIBLE
argument_list|)
decl_stmt|;
return|return
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
return|;
block|}
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
name|QuadPrefixTree
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
DECL|field|MAX_LEVELS_POSSIBLE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LEVELS_POSSIBLE
init|=
literal|50
decl_stmt|;
comment|//not really sure how big this should be
DECL|field|DEFAULT_MAX_LEVELS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_LEVELS
init|=
literal|12
decl_stmt|;
DECL|field|xmin
specifier|private
specifier|final
name|double
name|xmin
decl_stmt|;
DECL|field|xmax
specifier|private
specifier|final
name|double
name|xmax
decl_stmt|;
DECL|field|ymin
specifier|private
specifier|final
name|double
name|ymin
decl_stmt|;
DECL|field|ymax
specifier|private
specifier|final
name|double
name|ymax
decl_stmt|;
DECL|field|xmid
specifier|private
specifier|final
name|double
name|xmid
decl_stmt|;
DECL|field|ymid
specifier|private
specifier|final
name|double
name|ymid
decl_stmt|;
DECL|field|gridW
specifier|private
specifier|final
name|double
name|gridW
decl_stmt|;
DECL|field|gridH
specifier|public
specifier|final
name|double
name|gridH
decl_stmt|;
DECL|field|levelW
specifier|final
name|double
index|[]
name|levelW
decl_stmt|;
DECL|field|levelH
specifier|final
name|double
index|[]
name|levelH
decl_stmt|;
DECL|field|levelS
specifier|final
name|int
index|[]
name|levelS
decl_stmt|;
comment|// side
DECL|field|levelN
specifier|final
name|int
index|[]
name|levelN
decl_stmt|;
comment|// number
DECL|method|QuadPrefixTree
specifier|public
name|QuadPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|Rectangle
name|bounds
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
name|this
operator|.
name|xmin
operator|=
name|bounds
operator|.
name|getMinX
argument_list|()
expr_stmt|;
name|this
operator|.
name|xmax
operator|=
name|bounds
operator|.
name|getMaxX
argument_list|()
expr_stmt|;
name|this
operator|.
name|ymin
operator|=
name|bounds
operator|.
name|getMinY
argument_list|()
expr_stmt|;
name|this
operator|.
name|ymax
operator|=
name|bounds
operator|.
name|getMaxY
argument_list|()
expr_stmt|;
name|levelW
operator|=
operator|new
name|double
index|[
name|maxLevels
index|]
expr_stmt|;
name|levelH
operator|=
operator|new
name|double
index|[
name|maxLevels
index|]
expr_stmt|;
name|levelS
operator|=
operator|new
name|int
index|[
name|maxLevels
index|]
expr_stmt|;
name|levelN
operator|=
operator|new
name|int
index|[
name|maxLevels
index|]
expr_stmt|;
name|gridW
operator|=
name|xmax
operator|-
name|xmin
expr_stmt|;
name|gridH
operator|=
name|ymax
operator|-
name|ymin
expr_stmt|;
name|this
operator|.
name|xmid
operator|=
name|xmin
operator|+
name|gridW
operator|/
literal|2.0
expr_stmt|;
name|this
operator|.
name|ymid
operator|=
name|ymin
operator|+
name|gridH
operator|/
literal|2.0
expr_stmt|;
name|levelW
index|[
literal|0
index|]
operator|=
name|gridW
operator|/
literal|2.0
expr_stmt|;
name|levelH
index|[
literal|0
index|]
operator|=
name|gridH
operator|/
literal|2.0
expr_stmt|;
name|levelS
index|[
literal|0
index|]
operator|=
literal|2
expr_stmt|;
name|levelN
index|[
literal|0
index|]
operator|=
literal|4
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|levelW
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|levelW
index|[
name|i
index|]
operator|=
name|levelW
index|[
name|i
operator|-
literal|1
index|]
operator|/
literal|2.0
expr_stmt|;
name|levelH
index|[
name|i
index|]
operator|=
name|levelH
index|[
name|i
operator|-
literal|1
index|]
operator|/
literal|2.0
expr_stmt|;
name|levelS
index|[
name|i
index|]
operator|=
name|levelS
index|[
name|i
operator|-
literal|1
index|]
operator|*
literal|2
expr_stmt|;
name|levelN
index|[
name|i
index|]
operator|=
name|levelN
index|[
name|i
operator|-
literal|1
index|]
operator|*
literal|4
expr_stmt|;
block|}
block|}
DECL|method|QuadPrefixTree
specifier|public
name|QuadPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
argument_list|(
name|ctx
argument_list|,
name|DEFAULT_MAX_LEVELS
argument_list|)
expr_stmt|;
block|}
DECL|method|QuadPrefixTree
specifier|public
name|QuadPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
name|this
argument_list|(
name|ctx
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
block|}
DECL|method|printInfo
specifier|public
name|void
name|printInfo
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|nf
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxLevels
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|"]\t"
operator|+
name|nf
operator|.
name|format
argument_list|(
name|levelW
index|[
name|i
index|]
argument_list|)
operator|+
literal|"\t"
operator|+
name|nf
operator|.
name|format
argument_list|(
name|levelH
index|[
name|i
index|]
argument_list|)
operator|+
literal|"\t"
operator|+
name|levelS
index|[
name|i
index|]
operator|+
literal|"\t"
operator|+
operator|(
name|levelS
index|[
name|i
index|]
operator|*
name|levelS
index|[
name|i
index|]
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|public
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|dist
parameter_list|)
block|{
if|if
condition|(
name|dist
operator|==
literal|0
condition|)
comment|//short circuit
return|return
name|maxLevels
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxLevels
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|//note: level[i] is actually a lookup for level i+1
if|if
condition|(
name|dist
operator|>
name|levelW
index|[
name|i
index|]
operator|&&
name|dist
operator|>
name|levelH
index|[
name|i
index|]
condition|)
block|{
return|return
name|i
operator|+
literal|1
return|;
block|}
block|}
return|return
name|maxLevels
return|;
block|}
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
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
name|Node
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
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
operator|new
name|StringBuilder
argument_list|()
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
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
operator|new
name|QuadCell
argument_list|(
name|token
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
operator|new
name|QuadCell
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|//for performance
DECL|method|getNodes
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|getNodes
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|boolean
name|inclParents
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
return|return
name|super
operator|.
name|getNodesAltPoint
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|)
return|;
else|else
return|return
name|super
operator|.
name|getNodes
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|)
return|;
block|}
DECL|method|build
specifier|private
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
name|Node
argument_list|>
name|matches
parameter_list|,
name|StringBuilder
name|str
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|maxLevel
parameter_list|)
block|{
assert|assert
name|str
operator|.
name|length
argument_list|()
operator|==
name|level
assert|;
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
literal|'A'
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
name|str
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
literal|'B'
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
name|str
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
literal|'C'
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
name|str
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
name|checkBattenberg
argument_list|(
literal|'D'
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
name|str
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
comment|// possibly consider hilbert curve
comment|// http://en.wikipedia.org/wiki/Hilbert_curve
comment|// http://blog.notdot.net/2009/11/Damn-Cool-Algorithms-Spatial-indexing-with-Quadtrees-and-Hilbert-Curves
comment|// if we actually use the range property in the query, this could be useful
block|}
DECL|method|checkBattenberg
specifier|private
name|void
name|checkBattenberg
parameter_list|(
name|char
name|c
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
name|Node
argument_list|>
name|matches
parameter_list|,
name|StringBuilder
name|str
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|maxLevel
parameter_list|)
block|{
assert|assert
name|str
operator|.
name|length
argument_list|()
operator|==
name|level
assert|;
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
name|int
name|strlen
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|Rectangle
name|rectangle
init|=
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
decl_stmt|;
name|SpatialRelation
name|v
init|=
name|shape
operator|.
name|relate
argument_list|(
name|rectangle
argument_list|)
decl_stmt|;
if|if
condition|(
name|SpatialRelation
operator|.
name|CONTAINS
operator|==
name|v
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|//str.append(SpatialPrefixGrid.COVER);
name|matches
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|str
operator|.
name|toString
argument_list|()
argument_list|,
name|v
operator|.
name|transpose
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SpatialRelation
operator|.
name|DISJOINT
operator|==
name|v
condition|)
block|{
comment|// nothing
block|}
else|else
block|{
comment|// SpatialRelation.WITHIN, SpatialRelation.INTERSECTS
name|str
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|int
name|nextLevel
init|=
name|level
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|nextLevel
operator|>=
name|maxLevel
condition|)
block|{
comment|//str.append(SpatialPrefixGrid.INTERSECTS);
name|matches
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|str
operator|.
name|toString
argument_list|()
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
name|build
argument_list|(
name|cx
argument_list|,
name|cy
argument_list|,
name|nextLevel
argument_list|,
name|matches
argument_list|,
name|str
argument_list|,
name|shape
argument_list|,
name|maxLevel
argument_list|)
expr_stmt|;
block|}
block|}
name|str
operator|.
name|setLength
argument_list|(
name|strlen
argument_list|)
expr_stmt|;
block|}
DECL|class|QuadCell
class|class
name|QuadCell
extends|extends
name|Node
block|{
DECL|method|QuadCell
specifier|public
name|QuadCell
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|QuadPrefixTree
operator|.
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|QuadCell
specifier|public
name|QuadCell
parameter_list|(
name|String
name|token
parameter_list|,
name|SpatialRelation
name|shapeRel
parameter_list|)
block|{
name|super
argument_list|(
name|QuadPrefixTree
operator|.
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|this
operator|.
name|shapeRel
operator|=
name|shapeRel
expr_stmt|;
block|}
DECL|method|QuadCell
name|QuadCell
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
argument_list|(
name|QuadPrefixTree
operator|.
name|this
argument_list|,
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|shape
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubCells
specifier|public
name|Collection
argument_list|<
name|Node
argument_list|>
name|getSubCells
parameter_list|()
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|getTokenString
argument_list|()
operator|+
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|getTokenString
argument_list|()
operator|+
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|getTokenString
argument_list|()
operator|+
literal|"C"
argument_list|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
operator|new
name|QuadCell
argument_list|(
name|getTokenString
argument_list|()
operator|+
literal|"D"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cells
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCellsSize
specifier|public
name|int
name|getSubCellsSize
parameter_list|()
block|{
return|return
literal|4
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCell
specifier|public
name|Node
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
return|return
name|QuadPrefixTree
operator|.
name|this
operator|.
name|getNode
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
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
comment|//cache
annotation|@
name|Override
DECL|method|getShape
specifier|public
name|Shape
name|getShape
parameter_list|()
block|{
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
name|shape
operator|=
name|makeShape
argument_list|()
expr_stmt|;
return|return
name|shape
return|;
block|}
DECL|method|makeShape
specifier|private
name|Rectangle
name|makeShape
parameter_list|()
block|{
name|String
name|token
init|=
name|getTokenString
argument_list|()
decl_stmt|;
name|double
name|xmin
init|=
name|QuadPrefixTree
operator|.
name|this
operator|.
name|xmin
decl_stmt|;
name|double
name|ymin
init|=
name|QuadPrefixTree
operator|.
name|this
operator|.
name|ymin
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
name|token
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|token
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|'A'
operator|==
name|c
operator|||
literal|'a'
operator|==
name|c
condition|)
block|{
name|ymin
operator|+=
name|levelH
index|[
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|'B'
operator|==
name|c
operator|||
literal|'b'
operator|==
name|c
condition|)
block|{
name|xmin
operator|+=
name|levelW
index|[
name|i
index|]
expr_stmt|;
name|ymin
operator|+=
name|levelH
index|[
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|'C'
operator|==
name|c
operator|||
literal|'c'
operator|==
name|c
condition|)
block|{
comment|// nothing really
block|}
elseif|else
if|if
condition|(
literal|'D'
operator|==
name|c
operator|||
literal|'d'
operator|==
name|c
condition|)
block|{
name|xmin
operator|+=
name|levelW
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected char: "
operator|+
name|c
argument_list|)
throw|;
block|}
block|}
name|int
name|len
init|=
name|token
operator|.
name|length
argument_list|()
decl_stmt|;
name|double
name|width
decl_stmt|,
name|height
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|levelW
index|[
name|len
operator|-
literal|1
index|]
expr_stmt|;
name|height
operator|=
name|levelH
index|[
name|len
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
name|ctx
operator|.
name|makeRectangle
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
argument_list|)
return|;
block|}
block|}
comment|//QuadCell
block|}
end_class

end_unit

