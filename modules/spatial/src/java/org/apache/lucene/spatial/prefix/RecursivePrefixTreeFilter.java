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
name|*
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
name|Filter
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
name|OpenBitSet
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
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * Performs a spatial intersection filter against a field indexed with {@link SpatialPrefixTree}, a Trie.  * SPT yields terms (grids) at length 1 and at greater lengths corresponding to greater precisions.  * This filter recursively traverses each grid length and uses methods on {@link Shape} to efficiently know  * that all points at a prefix fit in the shape or not to either short-circuit unnecessary traversals or to efficiently  * load all enclosed points.  */
end_comment

begin_class
DECL|class|RecursivePrefixTreeFilter
specifier|public
class|class
name|RecursivePrefixTreeFilter
extends|extends
name|Filter
block|{
comment|/* TODOs for future:  Can a polygon query shape be optimized / made-simpler at recursive depths (e.g. intersection of shape + cell box)  RE "scan" threshold:   // IF configured to do so, we could use term.freq() as an estimate on the number of places at this depth.  OR, perhaps   //  make estimates based on the total known term count at this level?   if (!scan) {     //Make some estimations on how many points there are at this level and how few there would need to be to set     // !scan to false.     long termsThreshold = (long) estimateNumberIndexedTerms(cell.length(),queryShape.getDocFreqExpenseThreshold(cell));     long thisOrd = termsEnum.ord();     scan = (termsEnum.seek(thisOrd+termsThreshold+1) == TermsEnum.SeekStatus.END             || !cell.contains(termsEnum.term()));     termsEnum.seek(thisOrd);//return to last position   }    */
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|grid
specifier|private
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|queryShape
specifier|private
specifier|final
name|Shape
name|queryShape
decl_stmt|;
DECL|field|prefixGridScanLevel
specifier|private
specifier|final
name|int
name|prefixGridScanLevel
decl_stmt|;
comment|//at least one less than grid.getMaxLevels()
DECL|field|detailLevel
specifier|private
specifier|final
name|int
name|detailLevel
decl_stmt|;
DECL|method|RecursivePrefixTreeFilter
specifier|public
name|RecursivePrefixTreeFilter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|Shape
name|queryShape
parameter_list|,
name|int
name|prefixGridScanLevel
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
name|this
operator|.
name|queryShape
operator|=
name|queryShape
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
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
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
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|ctx
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|AtomicReader
name|reader
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
comment|//cached for termsEnum.docs() calls
name|Node
name|scanCell
init|=
literal|null
decl_stmt|;
comment|//cells is treated like a stack. LinkedList conveniently has bulk add to beginning. It's in sorted order so that we
comment|//  always advance forward through the termsEnum index.
name|LinkedList
argument_list|<
name|Node
argument_list|>
name|cells
init|=
operator|new
name|LinkedList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|grid
operator|.
name|getWorldNode
argument_list|()
operator|.
name|getSubCells
argument_list|(
name|queryShape
argument_list|)
argument_list|)
decl_stmt|;
comment|//This is a recursive algorithm that starts with one or more "big" cells, and then recursively dives down into the
comment|// first such cell that intersects with the query shape.  It's a depth first traversal because we don't move onto
comment|// the next big cell (breadth) until we're completely done considering all smaller cells beneath it. For a given
comment|// cell, if it's *within* the query shape then we can conveniently short-circuit the depth traversal and
comment|// grab all documents assigned to this cell/term.  For an intersection of the cell and query shape, we either
comment|// recursively step down another grid level or we decide heuristically (via prefixGridScanLevel) that there aren't
comment|// that many points, and so we scan through all terms within this cell (i.e. the term starts with the cell's term),
comment|// seeing which ones are within the query shape.
while|while
condition|(
operator|!
name|cells
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Node
name|cell
init|=
name|cells
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|cellTerm
init|=
operator|new
name|BytesRef
argument_list|(
name|cell
operator|.
name|getTokenBytes
argument_list|()
argument_list|)
decl_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|seekStat
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|cellTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|seekStat
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
break|break;
if|if
condition|(
name|seekStat
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
condition|)
continue|continue;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
operator|||
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|docsEnum
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//any other intersection
comment|//If the next indexed term is the leaf marker, then add all of them
name|BytesRef
name|nextCellTerm
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|nextCellTerm
argument_list|,
name|cellTerm
argument_list|)
assert|;
name|scanCell
operator|=
name|grid
operator|.
name|getNode
argument_list|(
name|nextCellTerm
operator|.
name|bytes
argument_list|,
name|nextCellTerm
operator|.
name|offset
argument_list|,
name|nextCellTerm
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
name|isLeaf
argument_list|()
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|docsEnum
argument_list|,
name|bits
argument_list|)
expr_stmt|;
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//move pointer to avoid potential redundant addDocs() below
block|}
comment|//Decide whether to continue to divide& conquer, or whether it's time to scan through terms beneath this cell.
comment|// Scanning is a performance optimization trade-off.
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
comment|//Divide& conquer
name|cells
operator|.
name|addAll
argument_list|(
literal|0
argument_list|,
name|cell
operator|.
name|getSubCells
argument_list|(
name|queryShape
argument_list|)
argument_list|)
expr_stmt|;
comment|//add to beginning
block|}
else|else
block|{
comment|//Scan through all terms within this cell to see if they are within the queryShape. No seek()s.
for|for
control|(
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|term
argument_list|()
init|;
name|term
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|cellTerm
argument_list|)
condition|;
name|term
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
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
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
name|detailLevel
condition|)
continue|continue;
if|if
condition|(
name|termLevel
operator|==
name|detailLevel
operator|||
name|scanCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
comment|//TODO should put more thought into implications of box vs point
name|Shape
name|cShape
init|=
name|termLevel
operator|==
name|grid
operator|.
name|getMaxLevels
argument_list|()
condition|?
name|scanCell
operator|.
name|getCenter
argument_list|()
else|:
name|scanCell
operator|.
name|getShape
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryShape
operator|.
name|relate
argument_list|(
name|cShape
argument_list|,
name|grid
operator|.
name|getSpatialContext
argument_list|()
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
condition|)
continue|continue;
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|docsEnum
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
comment|//term loop
block|}
block|}
block|}
comment|//cell loop
return|return
name|bits
return|;
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|DocsEnum
name|docsEnum
parameter_list|,
name|OpenBitSet
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docid
decl_stmt|;
while|while
condition|(
operator|(
name|docid
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|bits
operator|.
name|fastSet
argument_list|(
name|docid
argument_list|)
expr_stmt|;
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
literal|"GeoFilter{fieldName='"
operator|+
name|fieldName
operator|+
literal|'\''
operator|+
literal|", shape="
operator|+
name|queryShape
operator|+
literal|'}'
return|;
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
name|RecursivePrefixTreeFilter
name|that
init|=
operator|(
name|RecursivePrefixTreeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
comment|//note that we don't need to look at grid since for the same field it should be the same
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
if|if
condition|(
name|detailLevel
operator|!=
name|that
operator|.
name|detailLevel
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|queryShape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryShape
argument_list|)
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
name|fieldName
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
name|queryShape
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
name|detailLevel
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

