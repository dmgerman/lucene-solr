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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
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
name|Node
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

begin_comment
comment|/**  * Traverses a {@link SpatialPrefixTree} indexed field, using the template&  * visitor design patterns for subclasses to guide the traversal and collect  * matching documents.  *<p/>  * Subclasses implement {@link #getDocIdSet(org.apache.lucene.index.AtomicReaderContext,  * org.apache.lucene.util.Bits)} by instantiating a custom {@link  * VisitorTemplate} subclass (i.e. an anonymous inner class) and implement the  * required methods.  *  * @lucene.internal  */
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
literal|1
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
name|AbstractVisitingPrefixTreeFilter
name|that
init|=
operator|(
name|AbstractVisitingPrefixTreeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|prefixGridScanLevel
operator|!=
name|that
operator|.
name|prefixGridScanLevel
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
name|prefixGridScanLevel
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * An abstract class designed to make it easy to implement predicates or    * other operations on a {@link SpatialPrefixTree} indexed field. An instance    * of this class is not designed to be re-used across AtomicReaderContext    * instances so simply create a new one for each call to, say a {@link    * org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.AtomicReaderContext, org.apache.lucene.util.Bits)}.    * The {@link #getDocIdSet()} method here starts the work. It first checks    * that there are indexed terms; if not it quickly returns null. Then it calls    * {@link #start()} so a subclass can set up a return value, like an    * {@link org.apache.lucene.util.OpenBitSet}. Then it starts the traversal    * process, calling {@link #findSubCellsToVisit(org.apache.lucene.spatial.prefix.tree.Node)}    * which by default finds the top cells that intersect {@code queryShape}. If    * there isn't an indexed cell for a corresponding cell returned for this    * method then it's short-circuited until it finds one, at which point    * {@link #visit(org.apache.lucene.spatial.prefix.tree.Node)} is called. At    * some depths, of the tree, the algorithm switches to a scanning mode that    * finds calls {@link #visitScanned(org.apache.lucene.spatial.prefix.tree.Node, com.spatial4j.core.shape.Shape)}    * for each leaf cell found.    *    * @lucene.internal    */
DECL|class|VisitorTemplate
specifier|public
specifier|abstract
class|class
name|VisitorTemplate
extends|extends
name|BaseTermsEnumTraverser
block|{
comment|/* Future potential optimizations:    * Can a polygon query shape be optimized / made-simpler at recursive depths     (e.g. intersection of shape + cell box)    * RE "scan" vs divide& conquer performance decision:     We should use termsEnum.docFreq() as an estimate on the number of places at     this depth.  It would be nice if termsEnum knew how many terms     start with the current term without having to repeatedly next()& test to find out.    */
DECL|field|hasIndexedLeaves
specifier|protected
specifier|final
name|boolean
name|hasIndexedLeaves
decl_stmt|;
comment|//if false then we can skip looking for them
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
comment|//curVNode.cell's term.
DECL|field|scanCell
specifier|private
name|Node
name|scanCell
decl_stmt|;
DECL|field|thisTerm
specifier|private
name|BytesRef
name|thisTerm
decl_stmt|;
comment|//the result of termsEnum.term()
DECL|method|VisitorTemplate
specifier|public
name|VisitorTemplate
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|boolean
name|hasIndexedLeaves
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
name|this
operator|.
name|hasIndexedLeaves
operator|=
name|hasIndexedLeaves
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
comment|//advance
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
literal|null
return|;
comment|// all done
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
name|getWorldNode
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
name|curVNodeTerm
operator|.
name|bytes
operator|=
name|curVNode
operator|.
name|cell
operator|.
name|getTokenBytes
argument_list|()
expr_stmt|;
name|curVNodeTerm
operator|.
name|length
operator|=
name|curVNodeTerm
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
name|int
name|compare
init|=
name|termsEnum
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|thisTerm
argument_list|,
name|curVNodeTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|>
literal|0
condition|)
block|{
comment|// leap frog (termsEnum is beyond where we would otherwise seek)
assert|assert
operator|!
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
operator|.
name|seekExact
argument_list|(
name|curVNodeTerm
argument_list|,
literal|false
argument_list|)
operator|:
literal|"should be absent"
assert|;
block|}
else|else
block|{
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
comment|// Seek !
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
argument_list|,
literal|true
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
continue|continue;
comment|// leap frog
block|}
block|}
comment|// Visit!
name|boolean
name|descend
init|=
name|visit
argument_list|(
name|curVNode
operator|.
name|cell
argument_list|)
decl_stmt|;
comment|//advance
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
break|break;
comment|// all done
if|if
condition|(
name|descend
condition|)
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
comment|/** Called initially, and whenever {@link #visit(org.apache.lucene.spatial.prefix.tree.Node)}      * returns true. */
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
name|Node
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
comment|//Check for adjacent leaf (happens for indexed non-point shapes)
assert|assert
operator|!
name|cell
operator|.
name|isLeaf
argument_list|()
assert|;
if|if
condition|(
name|hasIndexedLeaves
operator|&&
name|cell
operator|.
name|getLevel
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//If the next indexed term just adds a leaf marker ('+') to cell,
comment|// then add all of those docs
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|thisTerm
argument_list|,
name|curVNodeTerm
argument_list|)
assert|;
name|scanCell
operator|=
name|grid
operator|.
name|getNode
argument_list|(
name|thisTerm
operator|.
name|bytes
argument_list|,
name|thisTerm
operator|.
name|offset
argument_list|,
name|thisTerm
operator|.
name|length
argument_list|,
name|scanCell
argument_list|)
expr_stmt|;
if|if
condition|(
name|scanCell
operator|.
name|getLevel
argument_list|()
operator|==
name|cell
operator|.
name|getLevel
argument_list|()
operator|&&
name|scanCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|visitLeaf
argument_list|(
name|scanCell
argument_list|)
expr_stmt|;
comment|//advance
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
return|return;
comment|// all done
block|}
block|}
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
name|Node
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
comment|/**      * Called when doing a divide& conquer to find the next intersecting cells      * of the query shape that are beneath {@code cell}. {@code cell} is      * guaranteed to have an intersection and thus this must return some number      * of nodes.      */
DECL|method|findSubCellsToVisit
specifier|protected
name|Iterator
argument_list|<
name|Node
argument_list|>
name|findSubCellsToVisit
parameter_list|(
name|Node
name|cell
parameter_list|)
block|{
return|return
name|cell
operator|.
name|getSubCells
argument_list|(
name|queryShape
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Scans ({@code termsEnum.next()}) terms until a term is found that does      * not start with curVNode's cell. If it finds a leaf cell or a cell at      * level {@code scanDetailLevel} then it calls {@link      * #visitScanned(org.apache.lucene.spatial.prefix.tree.Node,      * com.spatial4j.core.shape.Shape)}.      */
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
for|for
control|(
init|;
name|thisTerm
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|thisTerm
argument_list|,
name|curVNodeTerm
argument_list|)
condition|;
name|thisTerm
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
name|scanCell
operator|=
name|grid
operator|.
name|getNode
argument_list|(
name|thisTerm
operator|.
name|bytes
argument_list|,
name|thisTerm
operator|.
name|offset
argument_list|,
name|thisTerm
operator|.
name|length
argument_list|,
name|scanCell
argument_list|)
expr_stmt|;
name|int
name|termLevel
init|=
name|scanCell
operator|.
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|termLevel
operator|>
name|scanDetailLevel
condition|)
continue|continue;
if|if
condition|(
name|termLevel
operator|==
name|scanDetailLevel
operator|||
name|scanCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|Shape
name|cShape
decl_stmt|;
comment|//if this cell represents a point, use the cell center vs the box
comment|// (points never have isLeaf())
if|if
condition|(
name|termLevel
operator|==
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|&&
operator|!
name|scanCell
operator|.
name|isLeaf
argument_list|()
condition|)
name|cShape
operator|=
name|scanCell
operator|.
name|getCenter
argument_list|()
expr_stmt|;
else|else
name|cShape
operator|=
name|scanCell
operator|.
name|getShape
argument_list|()
expr_stmt|;
name|visitScanned
argument_list|(
name|scanCell
argument_list|,
name|cShape
argument_list|)
expr_stmt|;
block|}
block|}
comment|//term loop
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
name|Node
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
name|Node
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
comment|/**      * Visit an indexed cell returned from      * {@link #findSubCellsToVisit(org.apache.lucene.spatial.prefix.tree.Node)}.      *      * @param cell An intersecting cell.      * @return true to descend to more levels. It is an error to return true      * if cell.level == detailLevel      */
DECL|method|visit
specifier|protected
specifier|abstract
name|boolean
name|visit
parameter_list|(
name|Node
name|cell
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called after visit() returns true and an indexed leaf cell is found. An      * indexed leaf cell means associated documents generally won't be found at      * further detail levels.      */
DECL|method|visitLeaf
specifier|protected
specifier|abstract
name|void
name|visitLeaf
parameter_list|(
name|Node
name|cell
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * The cell is either indexed as a leaf or is the last level of detail. It      * might not even intersect the query shape, so be sure to check for that.      * Use {@code cellShape} instead of {@code cell.getCellShape} for the cell's      * shape.      */
DECL|method|visitScanned
specifier|protected
specifier|abstract
name|void
name|visitScanned
parameter_list|(
name|Node
name|cell
parameter_list|,
name|Shape
name|cellShape
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
comment|/**    * A Visitor Node/Cell found via the query shape for {@link VisitorTemplate}.    * Sometimes these are reset(cell). It's like a LinkedList node but forms a    * tree.    *    * @lucene.internal    */
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
name|Node
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
name|Node
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

