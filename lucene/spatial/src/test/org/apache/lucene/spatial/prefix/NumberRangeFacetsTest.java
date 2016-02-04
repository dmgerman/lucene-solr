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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
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
name|Term
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
name|queries
operator|.
name|TermsQuery
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
name|SimpleCollector
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
name|StrategyTestCase
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
name|NumberRangePrefixTreeStrategy
operator|.
name|Facets
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
name|DateRangePrefixTree
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
name|NumberRangePrefixTree
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
name|NumberRangePrefixTree
operator|.
name|UnitNRShape
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
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomInt
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomIntBetween
import|;
end_import

begin_class
DECL|class|NumberRangeFacetsTest
specifier|public
class|class
name|NumberRangeFacetsTest
extends|extends
name|StrategyTestCase
block|{
DECL|field|tree
name|DateRangePrefixTree
name|tree
decl_stmt|;
DECL|field|randomCalWindowField
name|int
name|randomCalWindowField
decl_stmt|;
DECL|field|randomCalWindowMs
name|long
name|randomCalWindowMs
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tree
operator|=
name|DateRangePrefixTree
operator|.
name|INSTANCE
expr_stmt|;
name|strategy
operator|=
operator|new
name|NumberRangePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
literal|"dateRange"
argument_list|)
expr_stmt|;
name|Calendar
name|tmpCal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|randomCalWindowField
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Calendar
operator|.
name|ZONE_OFFSET
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//we're not allowed to add zone offset
name|tmpCal
operator|.
name|add
argument_list|(
name|randomCalWindowField
argument_list|,
literal|2_000
argument_list|)
expr_stmt|;
name|randomCalWindowMs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|2000L
argument_list|,
name|tmpCal
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|20
argument_list|)
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
comment|//generate test data
name|List
argument_list|<
name|Shape
argument_list|>
name|indexedShapes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIndexedShapes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
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
name|numIndexedShapes
condition|;
name|i
operator|++
control|)
block|{
name|indexedShapes
operator|.
name|add
argument_list|(
name|randomShape
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Main index loop:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexedShapes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Shape
name|shape
init|=
name|indexedShapes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|adoc
argument_list|(
literal|""
operator|+
name|i
argument_list|,
name|shape
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
name|commit
argument_list|()
expr_stmt|;
comment|//intermediate commit, produces extra segments
block|}
comment|//delete some documents randomly
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|indexedShapes
operator|.
name|size
argument_list|()
condition|;
name|id
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|deleteDoc
argument_list|(
literal|""
operator|+
name|id
argument_list|)
expr_stmt|;
name|indexedShapes
operator|.
name|set
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|commit
argument_list|()
expr_stmt|;
comment|//Main query loop:
for|for
control|(
name|int
name|queryIdx
init|=
literal|0
init|;
name|queryIdx
operator|<
literal|10
condition|;
name|queryIdx
operator|++
control|)
block|{
name|preQueryHavoc
argument_list|()
expr_stmt|;
comment|// We need to have a facet range window to do the facets between (a start time& end time). We randomly
comment|// pick a date, decide the level we want to facet on, and then pick a right end time that is up to 2 thousand
comment|// values later.
name|int
name|calFieldFacet
init|=
name|randomCalWindowField
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|calFieldFacet
operator|>
literal|1
operator|&&
name|rarely
argument_list|()
condition|)
block|{
name|calFieldFacet
operator|--
expr_stmt|;
block|}
specifier|final
name|Calendar
name|leftCal
init|=
name|randomCalendar
argument_list|()
decl_stmt|;
name|leftCal
operator|.
name|add
argument_list|(
name|calFieldFacet
argument_list|,
operator|-
literal|1
operator|*
name|randomInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|Calendar
name|rightCal
init|=
operator|(
name|Calendar
operator|)
name|leftCal
operator|.
name|clone
argument_list|()
decl_stmt|;
name|rightCal
operator|.
name|add
argument_list|(
name|calFieldFacet
argument_list|,
name|randomInt
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Pick facet detail level based on cal field.
name|int
name|detailLevel
init|=
name|tree
operator|.
name|getTreeLevelForCalendarField
argument_list|(
name|calFieldFacet
argument_list|)
decl_stmt|;
if|if
condition|(
name|detailLevel
operator|<
literal|0
condition|)
block|{
comment|//no exact match
name|detailLevel
operator|=
operator|-
literal|1
operator|*
name|detailLevel
expr_stmt|;
block|}
comment|//Randomly pick a filter/acceptDocs
name|Bits
name|topAcceptDocs
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|acceptFieldIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
comment|//get all possible IDs into a list, random shuffle it, then randomly choose how many of the first we use to
comment|// replace the list.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexedShapes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|indexedShapes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// we deleted this one
continue|continue;
block|}
name|acceptFieldIds
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|acceptFieldIds
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|acceptFieldIds
operator|=
name|acceptFieldIds
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|randomInt
argument_list|(
name|acceptFieldIds
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|acceptFieldIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|acceptDocId
range|:
name|acceptFieldIds
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|acceptDocId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|topAcceptDocs
operator|=
name|searchForDocBits
argument_list|(
operator|new
name|TermsQuery
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Lets do it!
name|NumberRangePrefixTree
operator|.
name|NRShape
name|facetRange
init|=
name|tree
operator|.
name|toRangeShape
argument_list|(
name|tree
operator|.
name|toShape
argument_list|(
name|leftCal
argument_list|)
argument_list|,
name|tree
operator|.
name|toShape
argument_list|(
name|rightCal
argument_list|)
argument_list|)
decl_stmt|;
name|Facets
name|facets
init|=
operator|(
operator|(
name|NumberRangePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|calcFacets
argument_list|(
name|indexSearcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|topAcceptDocs
argument_list|,
name|facetRange
argument_list|,
name|detailLevel
argument_list|)
decl_stmt|;
comment|//System.out.println("Q: " + queryIdx + " " + facets);
comment|//Verify results. We do it by looping over indexed shapes and reducing the facet counts.
name|Shape
name|facetShapeRounded
init|=
name|facetRange
operator|.
name|roundToLevel
argument_list|(
name|detailLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|indexedShapeId
init|=
literal|0
init|;
name|indexedShapeId
operator|<
name|indexedShapes
operator|.
name|size
argument_list|()
condition|;
name|indexedShapeId
operator|++
control|)
block|{
if|if
condition|(
name|topAcceptDocs
operator|!=
literal|null
operator|&&
operator|!
name|acceptFieldIds
operator|.
name|contains
argument_list|(
name|indexedShapeId
argument_list|)
condition|)
block|{
continue|continue;
comment|// this doc was filtered out via acceptDocs
block|}
name|Shape
name|indexedShape
init|=
name|indexedShapes
operator|.
name|get
argument_list|(
name|indexedShapeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexedShape
operator|==
literal|null
condition|)
block|{
comment|//was deleted
continue|continue;
block|}
name|Shape
name|indexedShapeRounded
init|=
operator|(
operator|(
name|NumberRangePrefixTree
operator|.
name|NRShape
operator|)
name|indexedShape
operator|)
operator|.
name|roundToLevel
argument_list|(
name|detailLevel
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexedShapeRounded
operator|.
name|relate
argument_list|(
name|facetShapeRounded
argument_list|)
operator|.
name|intersects
argument_list|()
condition|)
block|{
comment|// no intersection at all
continue|continue;
block|}
comment|// walk the cells
specifier|final
name|CellIterator
name|cellIterator
init|=
name|tree
operator|.
name|getTreeCellIterator
argument_list|(
name|indexedShape
argument_list|,
name|detailLevel
argument_list|)
decl_stmt|;
while|while
condition|(
name|cellIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Cell
name|cell
init|=
name|cellIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|facetShapeRounded
argument_list|)
operator|.
name|intersects
argument_list|()
condition|)
block|{
name|cellIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|//no intersection; prune
continue|continue;
block|}
assert|assert
name|cell
operator|.
name|getLevel
argument_list|()
operator|<=
name|detailLevel
assert|;
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
comment|//count it
name|UnitNRShape
name|shape
init|=
operator|(
name|UnitNRShape
operator|)
name|cell
operator|.
name|getShape
argument_list|()
decl_stmt|;
specifier|final
name|UnitNRShape
name|parentShape
init|=
name|shape
operator|.
name|getShapeAtLevel
argument_list|(
name|detailLevel
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//get parent
specifier|final
name|Facets
operator|.
name|FacetParentVal
name|facetParentVal
init|=
name|facets
operator|.
name|parents
operator|.
name|get
argument_list|(
name|parentShape
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|facetParentVal
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|shape
operator|.
name|getValAtLevel
argument_list|(
name|shape
operator|.
name|getLevel
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|facetParentVal
operator|.
name|childCounts
argument_list|)
expr_stmt|;
assert|assert
name|facetParentVal
operator|.
name|childCounts
index|[
name|index
index|]
operator|>
literal|0
assert|;
name|facetParentVal
operator|.
name|childCounts
index|[
name|index
index|]
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
comment|//count it, and remove/prune.
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|<
name|detailLevel
operator|-
literal|1
condition|)
block|{
assert|assert
name|facets
operator|.
name|topLeaves
operator|>
literal|0
assert|;
name|facets
operator|.
name|topLeaves
operator|--
expr_stmt|;
block|}
else|else
block|{
name|UnitNRShape
name|shape
init|=
operator|(
name|UnitNRShape
operator|)
name|cell
operator|.
name|getShape
argument_list|()
decl_stmt|;
specifier|final
name|UnitNRShape
name|parentShape
init|=
name|shape
operator|.
name|getShapeAtLevel
argument_list|(
name|detailLevel
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//get parent
specifier|final
name|Facets
operator|.
name|FacetParentVal
name|facetParentVal
init|=
name|facets
operator|.
name|parents
operator|.
name|get
argument_list|(
name|parentShape
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|facetParentVal
argument_list|)
expr_stmt|;
assert|assert
name|facetParentVal
operator|.
name|parentLeaves
operator|>
literal|0
assert|;
name|facetParentVal
operator|.
name|parentLeaves
operator|--
expr_stmt|;
block|}
name|cellIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// At this point; all counts should be down to zero.
name|assertTrue
argument_list|(
name|facets
operator|.
name|topLeaves
operator|==
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Facets
operator|.
name|FacetParentVal
name|facetParentVal
range|:
name|facets
operator|.
name|parents
operator|.
name|values
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|facetParentVal
operator|.
name|parentLeaves
operator|==
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|facetParentVal
operator|.
name|childCounts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|childCount
range|:
name|facetParentVal
operator|.
name|childCounts
control|)
block|{
name|assertTrue
argument_list|(
name|childCount
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|searchForDocBits
specifier|private
name|Bits
name|searchForDocBits
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|bitSet
init|=
operator|new
name|FixedBitSet
argument_list|(
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
name|int
name|leafDocBase
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|leafDocBase
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|leafDocBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|bitSet
return|;
block|}
DECL|method|preQueryHavoc
specifier|private
name|void
name|preQueryHavoc
parameter_list|()
block|{
if|if
condition|(
name|strategy
operator|instanceof
name|RecursivePrefixTreeStrategy
condition|)
block|{
name|RecursivePrefixTreeStrategy
name|rpts
init|=
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
decl_stmt|;
name|int
name|scanLevel
init|=
name|randomInt
argument_list|(
name|rpts
operator|.
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
decl_stmt|;
name|rpts
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|scanLevel
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomShape
specifier|protected
name|Shape
name|randomShape
parameter_list|()
block|{
name|Calendar
name|cal1
init|=
name|randomCalendar
argument_list|()
decl_stmt|;
name|UnitNRShape
name|s1
init|=
name|tree
operator|.
name|toShape
argument_list|(
name|cal1
argument_list|)
decl_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
return|return
name|s1
return|;
block|}
try|try
block|{
name|Calendar
name|cal2
init|=
name|randomCalendar
argument_list|()
decl_stmt|;
name|UnitNRShape
name|s2
init|=
name|tree
operator|.
name|toShape
argument_list|(
name|cal2
argument_list|)
decl_stmt|;
if|if
condition|(
name|cal1
operator|.
name|compareTo
argument_list|(
name|cal2
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|tree
operator|.
name|toRangeShape
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|tree
operator|.
name|toRangeShape
argument_list|(
name|s2
argument_list|,
name|s1
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
assert|assert
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Differing precision"
argument_list|)
assert|;
return|return
name|s1
return|;
block|}
block|}
DECL|method|randomCalendar
specifier|private
name|Calendar
name|randomCalendar
parameter_list|()
block|{
name|Calendar
name|cal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
operator|%
name|randomCalWindowMs
argument_list|)
expr_stmt|;
try|try
block|{
name|tree
operator|.
name|clearFieldsAfter
argument_list|(
name|cal
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Calendar
operator|.
name|FIELD_COUNT
operator|+
literal|1
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Calendar underflow"
argument_list|)
condition|)
throw|throw
name|e
throw|;
block|}
return|return
name|cal
return|;
block|}
block|}
end_class

end_unit

