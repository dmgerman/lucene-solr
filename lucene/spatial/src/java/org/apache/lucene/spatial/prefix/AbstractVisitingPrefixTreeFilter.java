begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TermsEnum
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
name|Bits
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
comment|/**  * Traverses a {@link SpatialPrefixTree} indexed field, using the template and  * visitor design patterns for subclasses to guide the traversal and collect  * matching documents.  *<p>  * Subclasses implement {@link #getDocIdSet(org.apache.lucene.index.LeafReaderContext,  * org.apache.lucene.util.Bits)} by instantiating a custom {@link  * VisitorTemplate} subclass (i.e. an anonymous inner class) and implement the  * required methods.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|AbstractVisitingPrefixTreeFilter
specifier|public
specifier|abstract
class|class
name|AbstractVisitingPrefixTreeFilter
extends|extends
name|AbstractPrefixTreeFilter
block|{
comment|//Historical note: this code resulted from a refactoring of RecursivePrefixTreeFilter,
comment|// which in turn came out of SOLR-2155
comment|//This class perhaps could have been implemented in terms of FilteredTermsEnum& MultiTermQuery
comment|//& MultiTermQueryWrapperFilter.  Maybe so for simple Intersects predicate but not for when we want to collect terms
comment|//  differently depending on cell state like IsWithin and for fuzzy/accurate collection planned improvements.  At
comment|//  least it would just make things more complicated.
DECL|field|prefixGridScanLevel
specifier|protected
specifier|final
name|int
name|prefixGridScanLevel
decl_stmt|;
comment|//at least one less than grid.getMaxLevels()
DECL|method|AbstractVisitingPrefixTreeFilter
specifier|public
name|AbstractVisitingPrefixTreeFilter
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefixGridScanLevel
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|prefixGridScanLevel
argument_list|,
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|detailLevel
operator|<=
name|grid
operator|.
name|getMaxLevels
argument_list|()
assert|;
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
comment|//checks getClass == o.getClass& instanceof
comment|//Ignore hasIndexedLeaves as it's fixed for a specific field, which super.equals compares
comment|//Ignore prefixGridScanLevel as it is merely a tuning parameter.
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * An abstract class designed to make it easy to implement predicates or    * other operations on a {@link SpatialPrefixTree} indexed field. An instance    * of this class is not designed to be re-used across LeafReaderContext    * instances so simply create a new one for each call to, say a {@link    * org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.LeafReaderContext, org.apache.lucene.util.Bits)}.    * The {@link #getDocIdSet()} method here starts the work. It first checks    * that there are indexed terms; if not it quickly returns null. Then it calls    * {@link #start()} so a subclass can set up a return value, like an    * {@link org.apache.lucene.util.FixedBitSet}. Then it starts the traversal    * process, calling {@link #findSubCellsToVisit(org.apache.lucene.spatial.prefix.tree.Cell)}    * which by default finds the top cells that intersect {@code queryShape}. If    * there isn't an indexed cell for a corresponding cell returned for this    * method then it's short-circuited until it finds one, at which point    * {@link #visitPrefix(org.apache.lucene.spatial.prefix.tree.Cell)} is called. At    * some depths, of the tree, the algorithm switches to a scanning mode that    * calls {@link #visitScanned(org.apache.lucene.spatial.prefix.tree.Cell)}    * for each leaf cell found.    *    * @lucene.internal    */
DECL|class|VisitorTemplate
specifier|public
specifier|abstract
class|class
name|VisitorTemplate
extends|extends
name|BaseTermsEnumTraverser
block|{
comment|/* Future potential optimizations:    * Can a polygon query shape be optimized / made-simpler at recursive depths     (e.g. intersection of shape + cell box)    * RE "scan" vs divide& conquer performance decision:     We should use termsEnum.docFreq() as an estimate on the number of places at     this depth.  It would be nice if termsEnum knew how many terms     start with the current term without having to repeatedly next()& test to find out.    * Perhaps don't do intermediate seek()'s to cells above detailLevel that have Intersects     relation because we won't be collecting those docs any way.  However seeking     does act as a short-circuit.  So maybe do some percent of the time or when the level     is above some threshold.    */
comment|//
comment|//  TODO MAJOR REFACTOR SIMPLIFICATION BASED ON TreeCellIterator  TODO
comment|//
DECL|field|curVNode
specifier|private
name|VNode
name|curVNode
decl_stmt|;
comment|//current pointer, derived from query shape
DECL|field|curVNodeTerm
specifier|private
name|BytesRef
name|curVNodeTerm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|//curVNode.cell's term, without leaf. in main loop only
DECL|field|thisTerm
specifier|private
name|BytesRef
name|thisTerm
decl_stmt|;
comment|//the result of termsEnum.term()
DECL|field|indexedCell
specifier|private
name|Cell
name|indexedCell
decl_stmt|;
comment|//Cell wrapper of thisTerm. Always updated when thisTerm is.
DECL|method|VisitorTemplate
specifier|public
name|VisitorTemplate
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|curVNode
operator|==
literal|null
operator|:
literal|"Called more than once?"
assert|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
block|{
comment|//advances
return|return
literal|null
return|;
block|}
name|curVNode
operator|=
operator|new
name|VNode
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|curVNode
operator|.
name|reset
argument_list|(
name|grid
operator|.
name|getWorldCell
argument_list|()
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|addIntersectingChildren
argument_list|()
expr_stmt|;
name|main
label|:
while|while
condition|(
name|thisTerm
operator|!=
literal|null
condition|)
block|{
comment|//terminates for other reasons too!
comment|//Advance curVNode pointer
if|if
condition|(
name|curVNode
operator|.
name|children
operator|!=
literal|null
condition|)
block|{
comment|//-- HAVE CHILDREN: DESCEND
assert|assert
name|curVNode
operator|.
name|children
operator|.
name|hasNext
argument_list|()
assert|;
comment|//if we put it there then it has something
name|preSiblings
argument_list|(
name|curVNode
argument_list|)
expr_stmt|;
name|curVNode
operator|=
name|curVNode
operator|.
name|children
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//-- NO CHILDREN: ADVANCE TO NEXT SIBLING
name|VNode
name|parentVNode
init|=
name|curVNode
operator|.
name|parent
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|parentVNode
operator|==
literal|null
condition|)
break|break
name|main
break|;
comment|// all done
if|if
condition|(
name|parentVNode
operator|.
name|children
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|//advance next sibling
name|curVNode
operator|=
name|parentVNode
operator|.
name|children
operator|.
name|next
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|//reached end of siblings; pop up
name|postSiblings
argument_list|(
name|parentVNode
argument_list|)
expr_stmt|;
name|parentVNode
operator|.
name|children
operator|=
literal|null
expr_stmt|;
comment|//GC
name|parentVNode
operator|=
name|parentVNode
operator|.
name|parent
expr_stmt|;
block|}
block|}
block|}
comment|//Seek to curVNode's cell (or skip if termsEnum has moved beyond)
specifier|final
name|int
name|compare
init|=
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|curVNode
operator|.
name|cell
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|>
literal|0
condition|)
block|{
comment|// The indexed cell is after; continue loop to next query cell
continue|continue;
block|}
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
comment|// The indexed cell is before; seek ahead to query cell:
comment|//      Seek !
name|curVNode
operator|.
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
name|curVNodeTerm
argument_list|)
expr_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|curVNodeTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|seekStatus
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
break|break;
comment|// all done
name|thisTerm
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
name|indexedCell
operator|=
name|grid
operator|.
name|readCell
argument_list|(
name|thisTerm
argument_list|,
name|indexedCell
argument_list|)
expr_stmt|;
if|if
condition|(
name|seekStatus
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
condition|)
block|{
comment|// Did we find a leaf of the cell we were looking for or something after?
if|if
condition|(
operator|!
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|||
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|curVNode
operator|.
name|cell
argument_list|)
operator|!=
literal|0
condition|)
continue|continue;
comment|// The indexed cell is after; continue loop to next query cell
block|}
block|}
comment|// indexedCell == queryCell (disregarding leaf).
comment|// If indexedCell is a leaf then there's no prefix (prefix sorts before) -- just visit and continue
if|if
condition|(
name|indexedCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|visitLeaf
argument_list|(
name|indexedCell
argument_list|)
expr_stmt|;
comment|//TODO or query cell? Though shouldn't matter.
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
break|break;
continue|continue;
block|}
comment|// If a prefix (non-leaf) then visit; see if we descend.
specifier|final
name|boolean
name|descend
init|=
name|visitPrefix
argument_list|(
name|curVNode
operator|.
name|cell
argument_list|)
decl_stmt|;
comment|//need to use curVNode.cell not indexedCell
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
break|break;
comment|// Check for adjacent leaf with the same prefix
if|if
condition|(
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|&&
name|indexedCell
operator|.
name|getLevel
argument_list|()
operator|==
name|curVNode
operator|.
name|cell
operator|.
name|getLevel
argument_list|()
condition|)
block|{
name|visitLeaf
argument_list|(
name|indexedCell
argument_list|)
expr_stmt|;
comment|//TODO or query cell? Though shouldn't matter.
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
break|break;
block|}
if|if
condition|(
name|descend
condition|)
block|{
name|addIntersectingChildren
argument_list|()
expr_stmt|;
block|}
block|}
comment|//main loop
return|return
name|finish
argument_list|()
return|;
block|}
comment|/** Called initially, and whenever {@link #visitPrefix(org.apache.lucene.spatial.prefix.tree.Cell)}      * returns true. */
DECL|method|addIntersectingChildren
specifier|private
name|void
name|addIntersectingChildren
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|thisTerm
operator|!=
literal|null
assert|;
name|Cell
name|cell
init|=
name|curVNode
operator|.
name|cell
decl_stmt|;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|>=
name|detailLevel
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Spatial logic error"
argument_list|)
throw|;
comment|//Decide whether to continue to divide& conquer, or whether it's time to
comment|// scan through terms beneath this cell.
comment|// Scanning is a performance optimization trade-off.
comment|//TODO use termsEnum.docFreq() as heuristic
name|boolean
name|scan
init|=
name|cell
operator|.
name|getLevel
argument_list|()
operator|>=
name|prefixGridScanLevel
decl_stmt|;
comment|//simple heuristic
if|if
condition|(
operator|!
name|scan
condition|)
block|{
comment|//Divide& conquer (ultimately termsEnum.seek())
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|subCellsIter
init|=
name|findSubCellsToVisit
argument_list|(
name|cell
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subCellsIter
operator|.
name|hasNext
argument_list|()
condition|)
comment|//not expected
return|return;
name|curVNode
operator|.
name|children
operator|=
operator|new
name|VNodeCellIterator
argument_list|(
name|subCellsIter
argument_list|,
operator|new
name|VNode
argument_list|(
name|curVNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//Scan (loop of termsEnum.next())
name|scan
argument_list|(
name|detailLevel
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called when doing a divide and conquer to find the next intersecting cells      * of the query shape that are beneath {@code cell}. {@code cell} is      * guaranteed to have an intersection and thus this must return some number      * of nodes.      */
DECL|method|findSubCellsToVisit
specifier|protected
name|CellIterator
name|findSubCellsToVisit
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
return|return
name|cell
operator|.
name|getNextLevelCells
argument_list|(
name|queryShape
argument_list|)
return|;
block|}
comment|/**      * Scans ({@code termsEnum.next()}) terms until a term is found that does      * not start with curVNode's cell. If it finds a leaf cell or a cell at      * level {@code scanDetailLevel} then it calls {@link      * #visitScanned(org.apache.lucene.spatial.prefix.tree.Cell)}.      */
DECL|method|scan
specifier|protected
name|void
name|scan
parameter_list|(
name|int
name|scanDetailLevel
parameter_list|)
throws|throws
name|IOException
block|{
comment|//note: this can be a do-while instead in 6x; 5x has a back-compat with redundant leaves -- LUCENE-4942
while|while
condition|(
name|curVNode
operator|.
name|cell
operator|.
name|isPrefixOf
argument_list|(
name|indexedCell
argument_list|)
condition|)
block|{
if|if
condition|(
name|indexedCell
operator|.
name|getLevel
argument_list|()
operator|==
name|scanDetailLevel
operator|||
operator|(
name|indexedCell
operator|.
name|getLevel
argument_list|()
operator|<
name|scanDetailLevel
operator|&&
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|)
condition|)
block|{
name|visitScanned
argument_list|(
name|indexedCell
argument_list|)
expr_stmt|;
block|}
comment|//advance
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
break|break;
block|}
block|}
DECL|method|nextTerm
specifier|private
name|boolean
name|nextTerm
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|thisTerm
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|indexedCell
operator|=
name|grid
operator|.
name|readCell
argument_list|(
name|thisTerm
argument_list|,
name|indexedCell
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Used for {@link VNode#children}. */
DECL|class|VNodeCellIterator
specifier|private
class|class
name|VNodeCellIterator
implements|implements
name|Iterator
argument_list|<
name|VNode
argument_list|>
block|{
DECL|field|cellIter
specifier|final
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|cellIter
decl_stmt|;
DECL|field|vNode
specifier|private
specifier|final
name|VNode
name|vNode
decl_stmt|;
DECL|method|VNodeCellIterator
name|VNodeCellIterator
parameter_list|(
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|cellIter
parameter_list|,
name|VNode
name|vNode
parameter_list|)
block|{
name|this
operator|.
name|cellIter
operator|=
name|cellIter
expr_stmt|;
name|this
operator|.
name|vNode
operator|=
name|vNode
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
return|return
name|cellIter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|VNode
name|next
parameter_list|()
block|{
assert|assert
name|hasNext
argument_list|()
assert|;
name|vNode
operator|.
name|reset
argument_list|(
name|cellIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|vNode
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
comment|//it always removes
block|}
block|}
comment|/** Called first to setup things. */
DECL|method|start
specifier|protected
specifier|abstract
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Called last to return the result. */
DECL|method|finish
specifier|protected
specifier|abstract
name|DocIdSet
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Visit an indexed non-leaf cell. The presence of a prefix cell implies      * there are leaf cells at further levels. The cell passed should have it's      * {@link org.apache.lucene.spatial.prefix.tree.Cell#getShapeRel()} set      * relative to the filtered shape.      *      * @param cell An intersecting cell; not a leaf.      * @return true to descend to more levels.      */
DECL|method|visitPrefix
specifier|protected
specifier|abstract
name|boolean
name|visitPrefix
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called when an indexed leaf cell is found. An      * indexed leaf cell usually means associated documents won't be found at      * further detail levels.  However, if a document has      * multiple overlapping shapes at different resolutions, then this isn't true.      */
DECL|method|visitLeaf
specifier|protected
specifier|abstract
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * The cell is either indexed as a leaf or is the last level of detail. It      * might not even intersect the query shape, so be sure to check for that.      * The default implementation will check that and if passes then call      * {@link #visitLeaf(org.apache.lucene.spatial.prefix.tree.Cell)} or      * {@link #visitPrefix(org.apache.lucene.spatial.prefix.tree.Cell)}.      */
DECL|method|visitScanned
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
specifier|final
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
name|relate
operator|.
name|intersects
argument_list|()
condition|)
block|{
name|cell
operator|.
name|setShapeRel
argument_list|(
name|relate
argument_list|)
expr_stmt|;
comment|//just being pedantic
if|if
condition|(
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|visitLeaf
argument_list|(
name|cell
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|visitPrefix
argument_list|(
name|cell
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|preSiblings
specifier|protected
name|void
name|preSiblings
parameter_list|(
name|VNode
name|vNode
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|postSiblings
specifier|protected
name|void
name|postSiblings
parameter_list|(
name|VNode
name|vNode
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
comment|//class VisitorTemplate
comment|/**    * A visitor node/cell found via the query shape for {@link VisitorTemplate}.    * Sometimes these are reset(cell). It's like a LinkedList node but forms a    * tree.    *    * @lucene.internal    */
DECL|class|VNode
specifier|protected
specifier|static
class|class
name|VNode
block|{
comment|//Note: The VNode tree adds more code to debug/maintain v.s. a flattened
comment|// LinkedList that we used to have. There is more opportunity here for
comment|// custom behavior (see preSiblings& postSiblings) but that's not
comment|// leveraged yet. Maybe this is slightly more GC friendly.
DECL|field|parent
specifier|final
name|VNode
name|parent
decl_stmt|;
comment|//only null at the root
DECL|field|children
name|Iterator
argument_list|<
name|VNode
argument_list|>
name|children
decl_stmt|;
comment|//null, then sometimes set, then null
DECL|field|cell
name|Cell
name|cell
decl_stmt|;
comment|//not null (except initially before reset())
comment|/**      * call reset(cell) after to set the cell.      */
DECL|method|VNode
name|VNode
parameter_list|(
name|VNode
name|parent
parameter_list|)
block|{
comment|// remember to call reset(cell) after
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
assert|assert
name|cell
operator|!=
literal|null
assert|;
name|this
operator|.
name|cell
operator|=
name|cell
expr_stmt|;
assert|assert
name|children
operator|==
literal|null
assert|;
block|}
block|}
block|}
end_class

end_unit

