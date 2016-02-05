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

begin_comment
comment|/**  * Represents a grid cell. Cell instances are generally very transient and may be re-used  * internally.  To get an instance, you could start with {@link SpatialPrefixTree#getWorldCell()}.  * And from there you could either traverse down the tree with {@link #getNextLevelCells(com.spatial4j.core.shape.Shape)},  * or you could read an indexed term via {@link SpatialPrefixTree#readCell(org.apache.lucene.util.BytesRef,Cell)}.  * When a cell is read from a term, it is comprised of just the base bytes plus optionally a leaf flag.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Cell
specifier|public
interface|interface
name|Cell
block|{
comment|//  If we bring this back; perhaps do so as a method that un-shares its internal state: void unshare();
comment|//  /** Resets the state of this cell such that it is identical to {@code source}. This can be used for
comment|//   * cloning a cell to have a safe copy, and it also might be used to position this cell
comment|//   * before calling {@link #readCell(org.apache.lucene.util.BytesRef)} in a loop if you know the first term
comment|//   * is going to be close to some other cell, thereby saving some computations. */
comment|//  void copyFrom(Cell source);
comment|/** Gets the relationship this cell has with the shape from which it was filtered from, assuming it came from a    * {@link CellIterator}. Arguably it belongs there but it's very convenient here. */
DECL|method|getShapeRel
name|SpatialRelation
name|getShapeRel
parameter_list|()
function_decl|;
comment|/** See {@link #getShapeRel()}.    * @lucene.internal */
DECL|method|setShapeRel
name|void
name|setShapeRel
parameter_list|(
name|SpatialRelation
name|rel
parameter_list|)
function_decl|;
comment|/**    * Some cells are flagged as leaves, which are indexed as such. A leaf cell is either within some    * shape or it both intersects and the cell is at an accuracy threshold such that no smaller cells    * for the shape will be represented.    */
DECL|method|isLeaf
name|boolean
name|isLeaf
parameter_list|()
function_decl|;
comment|/** Set this cell to be a leaf. Warning: never call on a cell    * initialized to reference the same bytes from termsEnum, which should be treated as immutable.    * Note: not supported at level 0.    * @lucene.internal */
DECL|method|setLeaf
name|void
name|setLeaf
parameter_list|()
function_decl|;
comment|/**    * Returns the bytes for this cell, with a leaf byte<em>if this is a leaf cell</em>.    * The result param is used to save object allocation, though its bytes aren't used.    * @param result where the result goes, or null to create new    */
DECL|method|getTokenBytesWithLeaf
name|BytesRef
name|getTokenBytesWithLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
function_decl|;
comment|/**    * Returns the bytes for this cell, without a leaf set. The bytes should sort before    * {@link #getTokenBytesWithLeaf(org.apache.lucene.util.BytesRef)}.    * The result param is used to save object allocation, though its bytes aren't used.    * @param result where the result goes, or null to create new    */
DECL|method|getTokenBytesNoLeaf
name|BytesRef
name|getTokenBytesNoLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
function_decl|;
comment|/** Level 0 is the world (and has no parent), from then on a higher level means a smaller    * cell than the level before it.    */
DECL|method|getLevel
name|int
name|getLevel
parameter_list|()
function_decl|;
comment|/**    * Gets the cells at the next grid cell level underneath this one, optionally filtered by    * {@code shapeFilter}. The returned cells should have {@link #getShapeRel()} set to    * their relation with {@code shapeFilter}.  In addition, for non-points {@link #isLeaf()}    * must be true when that relation is WITHIN.    *<p>    * IMPORTANT: Cells returned from this iterator can be shared, as well as the bytes.    *<p>    * Precondition: Never called when getLevel() == maxLevel.    *    * @param shapeFilter an optional filter for the returned cells.    * @return A set of cells (no dups), sorted. Not Modifiable.    */
DECL|method|getNextLevelCells
name|CellIterator
name|getNextLevelCells
parameter_list|(
name|Shape
name|shapeFilter
parameter_list|)
function_decl|;
comment|/** Gets the shape for this cell; typically a Rectangle. */
DECL|method|getShape
name|Shape
name|getShape
parameter_list|()
function_decl|;
comment|/**    * Returns if the target term is within/underneath this cell; not necessarily a direct    * descendant.    * @param c the term    */
DECL|method|isPrefixOf
name|boolean
name|isPrefixOf
parameter_list|(
name|Cell
name|c
parameter_list|)
function_decl|;
comment|/** Equivalent to {@code this.getTokenBytesNoLeaf(null).compareTo(fromCell.getTokenBytesNoLeaf(null))}. */
DECL|method|compareToNoLeaf
name|int
name|compareToNoLeaf
parameter_list|(
name|Cell
name|fromCell
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

