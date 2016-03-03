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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|index
operator|.
name|PostingsEnum
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
name|RamUsageEstimator
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
name|SentinelIntSet
import|;
end_import

begin_comment
comment|/**  * Finds docs where its indexed shape {@link org.apache.lucene.spatial.query.SpatialOperation#Contains  * CONTAINS} the query shape. For use on {@link RecursivePrefixTreeStrategy}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ContainsPrefixTreeQuery
specifier|public
class|class
name|ContainsPrefixTreeQuery
extends|extends
name|AbstractPrefixTreeQuery
block|{
comment|/**    * If the spatial data for a document is comprised of multiple overlapping or adjacent parts,    * it might fail to match a query shape when doing the CONTAINS predicate when the sum of    * those shapes contain the query shape but none do individually.  Set this to false to    * increase performance if you don't care about that circumstance (such as if your indexed    * data doesn't even have such conditions).  See LUCENE-5062.    */
DECL|field|multiOverlappingIndexedShapes
specifier|protected
specifier|final
name|boolean
name|multiOverlappingIndexedShapes
decl_stmt|;
DECL|method|ContainsPrefixTreeQuery
specifier|public
name|ContainsPrefixTreeQuery
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
name|boolean
name|multiOverlappingIndexedShapes
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
name|multiOverlappingIndexedShapes
operator|=
name|multiOverlappingIndexedShapes
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
return|return
name|multiOverlappingIndexedShapes
operator|==
operator|(
operator|(
name|ContainsPrefixTreeQuery
operator|)
name|o
operator|)
operator|.
name|multiOverlappingIndexedShapes
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
return|return
name|super
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|multiOverlappingIndexedShapes
condition|?
literal|1
else|:
literal|0
operator|)
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
literal|"multiOverlappingIndexedShapes="
operator|+
name|multiOverlappingIndexedShapes
operator|+
literal|")"
return|;
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
name|ContainsVisitor
argument_list|(
name|context
argument_list|)
operator|.
name|visit
argument_list|(
name|grid
operator|.
name|getWorldCell
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|class|ContainsVisitor
specifier|private
class|class
name|ContainsVisitor
extends|extends
name|BaseTermsEnumTraverser
block|{
DECL|method|ContainsVisitor
specifier|public
name|ContainsVisitor
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|termsEnum
operator|!=
literal|null
condition|)
block|{
name|nextTerm
argument_list|()
expr_stmt|;
comment|//advance to first
block|}
block|}
DECL|field|seekTerm
name|BytesRef
name|seekTerm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|//temp; see seek()
DECL|field|thisTerm
name|BytesRef
name|thisTerm
decl_stmt|;
comment|//current term in termsEnum
DECL|field|indexedCell
name|Cell
name|indexedCell
decl_stmt|;
comment|//the cell wrapper around thisTerm
comment|/** This is the primary algorithm; recursive.  Returns null if finds none. */
DECL|method|visit
specifier|private
name|SmallDocSet
name|visit
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|Bits
name|acceptContains
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|thisTerm
operator|==
literal|null
condition|)
comment|//signals all done
return|return
literal|null
return|;
comment|// Get the AND of all child results (into combinedSubResults)
name|SmallDocSet
name|combinedSubResults
init|=
literal|null
decl_stmt|;
comment|//   Optimization: use null subCellsFilter when we know cell is within the query shape.
name|Shape
name|subCellsFilter
init|=
name|queryShape
decl_stmt|;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|!=
literal|0
operator|&&
operator|(
operator|(
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
literal|null
operator|||
name|cell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
operator|)
operator|)
condition|)
block|{
name|subCellsFilter
operator|=
literal|null
expr_stmt|;
assert|assert
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|queryShape
argument_list|)
operator|==
name|SpatialRelation
operator|.
name|WITHIN
assert|;
block|}
name|CellIterator
name|subCells
init|=
name|cell
operator|.
name|getNextLevelCells
argument_list|(
name|subCellsFilter
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
name|seek
argument_list|(
name|subCell
argument_list|)
condition|)
block|{
name|combinedSubResults
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subCell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|combinedSubResults
operator|=
name|getDocs
argument_list|(
name|subCell
argument_list|,
name|acceptContains
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|multiOverlappingIndexedShapes
operator|&&
name|subCell
operator|.
name|getShapeRel
argument_list|()
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
block|{
name|combinedSubResults
operator|=
name|getLeafDocs
argument_list|(
name|subCell
argument_list|,
name|acceptContains
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//OR the leaf docs with all child results
name|SmallDocSet
name|leafDocs
init|=
name|getLeafDocs
argument_list|(
name|subCell
argument_list|,
name|acceptContains
argument_list|)
decl_stmt|;
name|SmallDocSet
name|subDocs
init|=
name|visit
argument_list|(
name|subCell
argument_list|,
name|acceptContains
argument_list|)
decl_stmt|;
comment|//recursion
name|combinedSubResults
operator|=
name|union
argument_list|(
name|leafDocs
argument_list|,
name|subDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|combinedSubResults
operator|==
literal|null
condition|)
break|break;
name|acceptContains
operator|=
name|combinedSubResults
expr_stmt|;
comment|//has the 'AND' effect on next iteration
block|}
return|return
name|combinedSubResults
return|;
block|}
DECL|method|seek
specifier|private
name|boolean
name|seek
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|thisTerm
operator|==
literal|null
condition|)
return|return
literal|false
return|;
specifier|final
name|int
name|compare
init|=
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
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
return|return
literal|false
return|;
comment|//leap-frog effect
block|}
elseif|else
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
comment|// already there!
block|}
else|else
block|{
comment|//compare> 0
comment|//seek!
name|seekTerm
operator|=
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
name|seekTerm
argument_list|)
expr_stmt|;
specifier|final
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|seekTerm
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
block|{
name|thisTerm
operator|=
literal|null
expr_stmt|;
comment|//all done
return|return
literal|false
return|;
block|}
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
name|FOUND
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|&&
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|cell
argument_list|)
operator|==
literal|0
return|;
block|}
block|}
comment|/** Get prefix& leaf docs at this cell. */
DECL|method|getDocs
specifier|private
name|SmallDocSet
name|getDocs
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|Bits
name|acceptContains
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|cell
argument_list|)
operator|==
literal|0
assert|;
comment|//called when we've reached detailLevel.
if|if
condition|(
name|indexedCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
comment|//only a leaf
name|SmallDocSet
name|result
init|=
name|collectDocs
argument_list|(
name|acceptContains
argument_list|)
decl_stmt|;
name|nextTerm
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
block|{
name|SmallDocSet
name|docsAtPrefix
init|=
name|collectDocs
argument_list|(
name|acceptContains
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
condition|)
block|{
return|return
name|docsAtPrefix
return|;
block|}
comment|//collect leaf too
if|if
condition|(
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|&&
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|cell
argument_list|)
operator|==
literal|0
condition|)
block|{
name|SmallDocSet
name|docsAtLeaf
init|=
name|collectDocs
argument_list|(
name|acceptContains
argument_list|)
decl_stmt|;
name|nextTerm
argument_list|()
expr_stmt|;
return|return
name|union
argument_list|(
name|docsAtPrefix
argument_list|,
name|docsAtLeaf
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|docsAtPrefix
return|;
block|}
block|}
block|}
comment|/** Gets docs on the leaf of the given cell, _if_ there is a leaf cell, otherwise null. */
DECL|method|getLeafDocs
specifier|private
name|SmallDocSet
name|getLeafDocs
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|Bits
name|acceptContains
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexedCell
operator|.
name|compareToNoLeaf
argument_list|(
name|cell
argument_list|)
operator|==
literal|0
assert|;
comment|//Advance past prefix if we're at a prefix; return null if no leaf
if|if
condition|(
operator|!
name|indexedCell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|nextTerm
argument_list|()
operator|||
operator|!
name|indexedCell
operator|.
name|isLeaf
argument_list|()
operator|||
name|indexedCell
operator|.
name|getLevel
argument_list|()
operator|!=
name|cell
operator|.
name|getLevel
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
name|SmallDocSet
name|result
init|=
name|collectDocs
argument_list|(
name|acceptContains
argument_list|)
decl_stmt|;
name|nextTerm
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
DECL|method|union
specifier|private
name|SmallDocSet
name|union
parameter_list|(
name|SmallDocSet
name|aSet
parameter_list|,
name|SmallDocSet
name|bSet
parameter_list|)
block|{
if|if
condition|(
name|bSet
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|aSet
operator|==
literal|null
condition|)
return|return
name|bSet
return|;
return|return
name|aSet
operator|.
name|union
argument_list|(
name|bSet
argument_list|)
return|;
comment|//union is 'or'
block|}
return|return
name|aSet
return|;
block|}
DECL|method|collectDocs
specifier|private
name|SmallDocSet
name|collectDocs
parameter_list|(
name|Bits
name|acceptContains
parameter_list|)
throws|throws
name|IOException
block|{
name|SmallDocSet
name|set
init|=
literal|null
decl_stmt|;
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|int
name|docid
decl_stmt|;
while|while
condition|(
operator|(
name|docid
operator|=
name|postingsEnum
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
if|if
condition|(
name|acceptContains
operator|!=
literal|null
operator|&&
name|acceptContains
operator|.
name|get
argument_list|(
name|docid
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|int
name|size
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
name|size
operator|=
literal|16
expr_stmt|;
name|set
operator|=
operator|new
name|SmallDocSet
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|set
argument_list|(
name|docid
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
comment|//class ContainsVisitor
comment|/** A hash based mutable set of docIds. If this were Solr code then we might    * use a combination of HashDocSet and SortedIntDocSet instead. */
comment|// TODO use DocIdSetBuilder?
DECL|class|SmallDocSet
specifier|private
specifier|static
class|class
name|SmallDocSet
extends|extends
name|DocIdSet
implements|implements
name|Bits
block|{
DECL|field|intSet
specifier|private
specifier|final
name|SentinelIntSet
name|intSet
decl_stmt|;
DECL|field|maxInt
specifier|private
name|int
name|maxInt
init|=
literal|0
decl_stmt|;
DECL|method|SmallDocSet
specifier|public
name|SmallDocSet
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|intSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|size
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|intSet
operator|.
name|exists
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|intSet
operator|.
name|put
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
name|maxInt
condition|)
name|maxInt
operator|=
name|index
expr_stmt|;
block|}
comment|/** Largest docid. */
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxInt
return|;
block|}
comment|/** Number of docids. */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|intSet
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** NOTE: modifies and returns either "this" or "other" */
DECL|method|union
specifier|public
name|SmallDocSet
name|union
parameter_list|(
name|SmallDocSet
name|other
parameter_list|)
block|{
name|SmallDocSet
name|bigger
decl_stmt|;
name|SmallDocSet
name|smaller
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|intSet
operator|.
name|size
argument_list|()
operator|>
name|this
operator|.
name|intSet
operator|.
name|size
argument_list|()
condition|)
block|{
name|bigger
operator|=
name|other
expr_stmt|;
name|smaller
operator|=
name|this
expr_stmt|;
block|}
else|else
block|{
name|bigger
operator|=
name|this
expr_stmt|;
name|smaller
operator|=
name|other
expr_stmt|;
block|}
comment|//modify bigger
for|for
control|(
name|int
name|v
range|:
name|smaller
operator|.
name|intSet
operator|.
name|keys
control|)
block|{
if|if
condition|(
name|v
operator|==
name|smaller
operator|.
name|intSet
operator|.
name|emptyVal
condition|)
continue|continue;
name|bigger
operator|.
name|set
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|bigger
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
comment|//if the # of docids is super small, return null since iteration is going
comment|// to be faster
return|return
name|size
argument_list|()
operator|>
literal|4
condition|?
name|this
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|//copy the unsorted values to a new array then sort them
name|int
name|d
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|intSet
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|v
range|:
name|intSet
operator|.
name|keys
control|)
block|{
if|if
condition|(
name|v
operator|==
name|intSet
operator|.
name|emptyVal
condition|)
continue|continue;
name|docs
index|[
name|d
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
assert|assert
name|d
operator|==
name|intSet
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|int
name|size
init|=
name|d
decl_stmt|;
comment|//sort them
name|Arrays
operator|.
name|sort
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|<
name|size
condition|)
block|{
return|return
name|docs
index|[
name|idx
index|]
return|;
block|}
else|else
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|idx
operator|<
name|size
condition|)
return|return
name|docs
index|[
name|idx
index|]
return|;
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|//for this small set this is likely faster vs. a binary search
comment|// into the sorted array
return|return
name|slowAdvance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
operator|+
name|intSet
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
block|}
comment|//class SmallDocSet
block|}
end_class

end_unit

