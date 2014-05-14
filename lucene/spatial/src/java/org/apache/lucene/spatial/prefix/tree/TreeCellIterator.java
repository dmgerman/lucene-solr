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
name|Shape
import|;
end_import

begin_comment
comment|/**  * Navigates a {@link org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree} from a given cell (typically the world  * cell) down to a maximum number of configured levels, filtered by a given shape. Intermediate non-leaf cells are  * returned. It supports {@link #remove()} for skipping traversal of subcells of the current cell.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|TreeCellIterator
class|class
name|TreeCellIterator
extends|extends
name|CellIterator
block|{
comment|//This class uses a stack approach, which is more efficient than creating linked nodes. And it might more easily
comment|// pave the way for re-using Cell& CellIterator at a given level in the future.
DECL|field|shapeFilter
specifier|private
specifier|final
name|Shape
name|shapeFilter
decl_stmt|;
comment|//possibly null
DECL|field|iterStack
specifier|private
specifier|final
name|CellIterator
index|[]
name|iterStack
decl_stmt|;
comment|//starts at level 1
DECL|field|stackIdx
specifier|private
name|int
name|stackIdx
decl_stmt|;
comment|//-1 when done
DECL|field|descend
specifier|private
name|boolean
name|descend
decl_stmt|;
DECL|method|TreeCellIterator
specifier|public
name|TreeCellIterator
parameter_list|(
name|Shape
name|shapeFilter
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|Cell
name|parentCell
parameter_list|)
block|{
name|this
operator|.
name|shapeFilter
operator|=
name|shapeFilter
expr_stmt|;
assert|assert
name|parentCell
operator|.
name|getLevel
argument_list|()
operator|==
literal|0
assert|;
name|iterStack
operator|=
operator|new
name|CellIterator
index|[
name|detailLevel
index|]
expr_stmt|;
name|iterStack
index|[
literal|0
index|]
operator|=
name|parentCell
operator|.
name|getNextLevelCells
argument_list|(
name|shapeFilter
argument_list|)
expr_stmt|;
name|stackIdx
operator|=
literal|0
expr_stmt|;
comment|//always points to an iter (non-null)
comment|//note: not obvious but needed to visit the first cell before trying to descend
name|descend
operator|=
literal|false
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
return|return
literal|true
return|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|stackIdx
operator|==
operator|-
literal|1
condition|)
comment|//the only condition in which we return false
return|return
literal|false
return|;
comment|//If we can descend...
if|if
condition|(
name|descend
operator|&&
operator|!
operator|(
name|stackIdx
operator|==
name|iterStack
operator|.
name|length
operator|-
literal|1
operator|||
name|iterStack
index|[
name|stackIdx
index|]
operator|.
name|thisCell
argument_list|()
operator|.
name|isLeaf
argument_list|()
operator|)
condition|)
block|{
name|CellIterator
name|nextIter
init|=
name|iterStack
index|[
name|stackIdx
index|]
operator|.
name|thisCell
argument_list|()
operator|.
name|getNextLevelCells
argument_list|(
name|shapeFilter
argument_list|)
decl_stmt|;
comment|//push stack
name|iterStack
index|[
operator|++
name|stackIdx
index|]
operator|=
name|nextIter
expr_stmt|;
block|}
comment|//Get sibling...
if|if
condition|(
name|iterStack
index|[
name|stackIdx
index|]
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|nextCell
operator|=
name|iterStack
index|[
name|stackIdx
index|]
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//at detailLevel
if|if
condition|(
name|stackIdx
operator|==
name|iterStack
operator|.
name|length
operator|-
literal|1
operator|&&
operator|!
operator|(
name|shapeFilter
operator|instanceof
name|Point
operator|)
condition|)
comment|//point check is a kludge
name|nextCell
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
comment|//because at bottom
break|break;
block|}
comment|//Couldn't get next; go up...
comment|//pop stack
name|iterStack
index|[
name|stackIdx
operator|--
index|]
operator|=
literal|null
expr_stmt|;
name|descend
operator|=
literal|false
expr_stmt|;
comment|//so that we don't re-descend where we just were
block|}
assert|assert
name|nextCell
operator|!=
literal|null
assert|;
name|descend
operator|=
literal|true
expr_stmt|;
comment|//reset
return|return
literal|true
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
assert|assert
name|thisCell
argument_list|()
operator|!=
literal|null
operator|&&
name|nextCell
operator|==
literal|null
assert|;
name|descend
operator|=
literal|false
expr_stmt|;
block|}
comment|//TODO implement a smart nextFrom() that looks at the parent's bytes first
block|}
end_class

end_unit

