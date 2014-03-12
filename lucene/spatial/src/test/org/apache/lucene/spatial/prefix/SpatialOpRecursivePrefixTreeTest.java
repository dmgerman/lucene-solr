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
name|context
operator|.
name|SpatialContextFactory
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
name|ShapeCollection
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|impl
operator|.
name|RectangleImpl
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|StoredField
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
name|document
operator|.
name|StringField
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
name|QuadPrefixTree
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|query
operator|.
name|SpatialOperation
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
name|Arrays
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|CONTAINS
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|DISJOINT
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|INTERSECTS
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|WITHIN
import|;
end_import

begin_class
DECL|class|SpatialOpRecursivePrefixTreeTest
specifier|public
class|class
name|SpatialOpRecursivePrefixTreeTest
extends|extends
name|StrategyTestCase
block|{
DECL|field|ITERATIONS
specifier|static
specifier|final
name|int
name|ITERATIONS
init|=
literal|10
decl_stmt|;
comment|//Test Iterations
DECL|field|grid
specifier|private
name|SpatialPrefixTree
name|grid
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
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|mySetup
specifier|public
name|void
name|mySetup
parameter_list|(
name|int
name|maxLevels
parameter_list|)
throws|throws
name|IOException
block|{
comment|//non-geospatial makes this test a little easier (in gridSnap), and using boundary values 2^X raises
comment|// the prospect of edge conditions we want to test, plus makes for simpler numbers (no decimals).
name|SpatialContextFactory
name|factory
init|=
operator|new
name|SpatialContextFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|geo
operator|=
literal|false
expr_stmt|;
name|factory
operator|.
name|worldBounds
operator|=
operator|new
name|RectangleImpl
argument_list|(
literal|0
argument_list|,
literal|256
argument_list|,
operator|-
literal|128
argument_list|,
literal|128
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|factory
operator|.
name|newSpatialContext
argument_list|()
expr_stmt|;
comment|//A fairly shallow grid, and default 2.5% distErrPct
if|if
condition|(
name|maxLevels
operator|==
operator|-
literal|1
condition|)
name|maxLevels
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|this
operator|.
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
comment|//((PrefixTreeStrategy) strategy).setDistErrPct(0);//fully precise to grid
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Strategy: "
operator|+
name|strategy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testIntersects
specifier|public
name|void
name|testIntersects
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testWithin
specifier|public
name|void
name|testWithin
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testContains
specifier|public
name|void
name|testContains
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|SpatialOperation
operator|.
name|Contains
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testDisjoint
specifier|public
name|void
name|testDisjoint
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
argument_list|)
expr_stmt|;
block|}
comment|/** See LUCENE-5062, {@link ContainsPrefixTreeFilter#multiOverlappingIndexedShapes}. */
annotation|@
name|Test
DECL|method|testContainsPairOverlap
specifier|public
name|void
name|testContainsPairOverlap
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"0"
argument_list|,
operator|new
name|ShapePair
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|33
argument_list|,
operator|-
literal|128
argument_list|,
literal|128
argument_list|)
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|33
argument_list|,
literal|128
argument_list|,
operator|-
literal|128
argument_list|,
literal|128
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Contains
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|128
argument_list|,
operator|-
literal|16
argument_list|,
literal|128
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResults
name|searchResults
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchResults
operator|.
name|numFound
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithinDisjointParts
specifier|public
name|void
name|testWithinDisjointParts
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
literal|7
argument_list|)
expr_stmt|;
comment|//one shape comprised of two parts, quite separated apart
name|adoc
argument_list|(
literal|"0"
argument_list|,
operator|new
name|ShapePair
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|,
operator|-
literal|120
argument_list|,
operator|-
literal|100
argument_list|)
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|220
argument_list|,
literal|240
argument_list|,
literal|110
argument_list|,
literal|125
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|//query surrounds only the second part of the indexed shape
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|210
argument_list|,
literal|245
argument_list|,
literal|105
argument_list|,
literal|128
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResults
name|searchResults
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|//we shouldn't find it because it's not completely within
name|assertTrue
argument_list|(
name|searchResults
operator|.
name|numFound
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/** LUCENE-4916 */
DECL|method|testWithinLeafApproxRule
specifier|public
name|void
name|testWithinLeafApproxRule
parameter_list|()
throws|throws
name|IOException
block|{
name|mySetup
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|//4x4 grid
comment|//indexed shape will simplify to entire right half (2 top cells)
name|adoc
argument_list|(
literal|"0"
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|192
argument_list|,
literal|204
argument_list|,
operator|-
literal|128
argument_list|,
literal|128
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//query does NOT contain it; both indexed cells are leaves to the query, and
comment|// when expanded to the full grid cells, the top one's top row is disjoint
comment|// from the query and thus not a match.
name|assertTrue
argument_list|(
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|38
argument_list|,
literal|192
argument_list|,
operator|-
literal|72
argument_list|,
literal|56
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|numFound
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|//no-match
comment|//this time the rect is a little bigger and is considered a match. It's a
comment|// an acceptable false-positive because of the grid approximation.
name|assertTrue
argument_list|(
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|38
argument_list|,
literal|192
argument_list|,
operator|-
literal|72
argument_list|,
literal|80
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|numFound
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|//match
block|}
comment|//Override so we can index parts of a pair separately, resulting in the detailLevel
comment|// being independent for each shape vs the whole thing
annotation|@
name|Override
DECL|method|newDoc
specifier|protected
name|Document
name|newDoc
parameter_list|(
name|String
name|id
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|Shape
argument_list|>
name|shapes
decl_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|ShapePair
condition|)
block|{
name|shapes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|shapes
operator|.
name|add
argument_list|(
operator|(
operator|(
name|ShapePair
operator|)
name|shape
operator|)
operator|.
name|shape1
argument_list|)
expr_stmt|;
name|shapes
operator|.
name|add
argument_list|(
operator|(
operator|(
name|ShapePair
operator|)
name|shape
operator|)
operator|.
name|shape2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|shapes
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Shape
name|shapei
range|:
name|shapes
control|)
block|{
for|for
control|(
name|Field
name|f
range|:
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shapei
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storeShape
condition|)
comment|//just for diagnostics
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|shape
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
specifier|final
name|SpatialOperation
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
comment|//first show that when there's no data, a query will result in no results
block|{
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|operation
argument_list|,
name|randomRectangle
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResults
name|searchResults
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searchResults
operator|.
name|numFound
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|biasContains
init|=
operator|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|Contains
operator|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Shape
argument_list|>
name|indexedShapes
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Shape
argument_list|>
name|indexedShapesGS
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//grid snapped
specifier|final
name|int
name|numIndexedShapes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|6
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
name|String
name|id
init|=
literal|""
operator|+
name|i
decl_stmt|;
name|Shape
name|indexedShape
decl_stmt|;
name|Shape
name|indexedShapeGS
decl_stmt|;
comment|//(grid-snapped)
name|int
name|R
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|12
argument_list|)
decl_stmt|;
if|if
condition|(
name|R
operator|==
literal|0
condition|)
block|{
comment|//1 in 12
name|indexedShape
operator|=
literal|null
expr_stmt|;
comment|//no shape for this doc
name|indexedShapeGS
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|R
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
comment|//4 in 12 (0,3,6,9)
comment|//comprised of more than one shape
name|Rectangle
name|shape1
init|=
name|randomRectangle
argument_list|()
decl_stmt|;
name|Rectangle
name|shape2
init|=
name|randomRectangle
argument_list|()
decl_stmt|;
name|indexedShape
operator|=
operator|new
name|ShapePair
argument_list|(
name|shape1
argument_list|,
name|shape2
argument_list|,
name|biasContains
argument_list|)
expr_stmt|;
name|indexedShapeGS
operator|=
operator|new
name|ShapePair
argument_list|(
name|gridSnap
argument_list|(
name|shape1
argument_list|)
argument_list|,
name|gridSnap
argument_list|(
name|shape2
argument_list|)
argument_list|,
name|biasContains
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//just one shape
name|indexedShape
operator|=
name|randomRectangle
argument_list|()
expr_stmt|;
name|indexedShapeGS
operator|=
name|gridSnap
argument_list|(
name|indexedShape
argument_list|)
expr_stmt|;
block|}
comment|//TODO sometimes index a point. Need to fix LUCENE-4978 first though.
name|indexedShapes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|indexedShape
argument_list|)
expr_stmt|;
name|indexedShapesGS
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|indexedShapeGS
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
name|id
argument_list|,
name|indexedShape
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
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
name|indexedShapes
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|id
init|=
name|idIter
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|id
argument_list|)
expr_stmt|;
name|idIter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|indexedShapesGS
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numQueryShapes
init|=
name|atLeast
argument_list|(
literal|20
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
name|numQueryShapes
condition|;
name|i
operator|++
control|)
block|{
name|int
name|scanLevel
init|=
name|randomInt
argument_list|(
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|scanLevel
argument_list|)
expr_stmt|;
specifier|final
name|Shape
name|queryShape
init|=
name|randomRectangle
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|opIsDisjoint
init|=
name|operation
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
decl_stmt|;
comment|//Generate truth via brute force:
comment|// We ensure true-positive matches (if the predicate on the raw shapes match
comment|//  then the search should find those same matches).
comment|// approximations, false-positive matches
name|Set
argument_list|<
name|String
argument_list|>
name|expectedIds
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|//true-positives
name|Set
argument_list|<
name|String
argument_list|>
name|secondaryIds
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|//false-positives (unless disjoint)
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Shape
argument_list|>
name|entry
range|:
name|indexedShapes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Shape
name|indexedShapeCompare
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexedShapeCompare
operator|==
literal|null
condition|)
continue|continue;
name|Shape
name|queryShapeCompare
init|=
name|queryShape
decl_stmt|;
if|if
condition|(
name|operation
operator|.
name|evaluate
argument_list|(
name|indexedShapeCompare
argument_list|,
name|queryShapeCompare
argument_list|)
condition|)
block|{
name|expectedIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|opIsDisjoint
condition|)
block|{
comment|//if no longer intersect after buffering them, for disjoint, remember this
name|indexedShapeCompare
operator|=
name|indexedShapesGS
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|queryShapeCompare
operator|=
name|gridSnap
argument_list|(
name|queryShape
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|operation
operator|.
name|evaluate
argument_list|(
name|indexedShapeCompare
argument_list|,
name|queryShapeCompare
argument_list|)
condition|)
name|secondaryIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|opIsDisjoint
condition|)
block|{
comment|//buffer either the indexed or query shape (via gridSnap) and try again
if|if
condition|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|Intersects
condition|)
block|{
name|indexedShapeCompare
operator|=
name|indexedShapesGS
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|queryShapeCompare
operator|=
name|gridSnap
argument_list|(
name|queryShape
argument_list|)
expr_stmt|;
comment|//TODO Unfortunately, grid-snapping both can result in intersections that otherwise
comment|// wouldn't happen when the grids are adjacent. Not a big deal but our test is just a
comment|// bit more lenient.
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|Contains
condition|)
block|{
name|indexedShapeCompare
operator|=
name|indexedShapesGS
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|IsWithin
condition|)
block|{
name|queryShapeCompare
operator|=
name|gridSnap
argument_list|(
name|queryShape
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operation
operator|.
name|evaluate
argument_list|(
name|indexedShapeCompare
argument_list|,
name|queryShapeCompare
argument_list|)
condition|)
name|secondaryIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Search and verify results
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|operation
argument_list|,
name|queryShape
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|remainingExpectedIds
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|expectedIds
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchResult
name|result
range|:
name|got
operator|.
name|results
control|)
block|{
name|String
name|id
init|=
name|result
operator|.
name|getId
argument_list|()
decl_stmt|;
name|boolean
name|removed
init|=
name|remainingExpectedIds
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|removed
operator|&&
operator|(
operator|!
name|opIsDisjoint
operator|&&
operator|!
name|secondaryIds
operator|.
name|contains
argument_list|(
name|id
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"Shouldn't match"
argument_list|,
name|id
argument_list|,
name|indexedShapes
argument_list|,
name|indexedShapesGS
argument_list|,
name|queryShape
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|opIsDisjoint
condition|)
name|remainingExpectedIds
operator|.
name|removeAll
argument_list|(
name|secondaryIds
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|remainingExpectedIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|id
init|=
name|remainingExpectedIds
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"Should have matched"
argument_list|,
name|id
argument_list|,
name|indexedShapes
argument_list|,
name|indexedShapesGS
argument_list|,
name|queryShape
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|fail
specifier|private
name|void
name|fail
parameter_list|(
name|String
name|label
parameter_list|,
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Shape
argument_list|>
name|indexedShapes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Shape
argument_list|>
name|indexedShapesGS
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Ig:"
operator|+
name|indexedShapesGS
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|+
literal|" Qg:"
operator|+
name|gridSnap
argument_list|(
name|queryShape
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|label
operator|+
literal|" I #"
operator|+
name|id
operator|+
literal|":"
operator|+
name|indexedShapes
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|+
literal|" Q:"
operator|+
name|queryShape
argument_list|)
expr_stmt|;
block|}
comment|//  private Rectangle inset(Rectangle r) {
comment|//    //typically inset by 1 (whole numbers are easy to read)
comment|//    double d = Math.min(1.0, grid.getDistanceForLevel(grid.getMaxLevels()) / 4);
comment|//    return ctx.makeRectangle(r.getMinX() + d, r.getMaxX() - d, r.getMinY() + d, r.getMaxY() - d);
comment|//  }
DECL|method|gridSnap
specifier|protected
name|Rectangle
name|gridSnap
parameter_list|(
name|Shape
name|snapMe
parameter_list|)
block|{
comment|//The next 4 lines mimic PrefixTreeStrategy.createIndexableFields()
name|double
name|distErrPct
init|=
operator|(
operator|(
name|PrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|getDistErrPct
argument_list|()
decl_stmt|;
name|double
name|distErr
init|=
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|snapMe
argument_list|,
name|distErrPct
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|distErr
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
name|grid
operator|.
name|getCells
argument_list|(
name|snapMe
argument_list|,
name|detailLevel
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//calc bounding box of cells.
name|List
argument_list|<
name|Shape
argument_list|>
name|cellShapes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cells
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Cell
name|cell
range|:
name|cells
control|)
block|{
name|cellShapes
operator|.
name|add
argument_list|(
name|cell
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ShapeCollection
argument_list|<>
argument_list|(
name|cellShapes
argument_list|,
name|ctx
argument_list|)
operator|.
name|getBoundingBox
argument_list|()
return|;
block|}
comment|/**    * An aggregate of 2 shapes. Unfortunately we can't simply use a ShapeCollection because:    * (a) ambiguity between CONTAINS& WITHIN for equal shapes, and    * (b) adjacent pairs could as a whole contain the input shape.    * The tests here are sensitive to these matters, although in practice ShapeCollection    * is fine.    */
DECL|class|ShapePair
specifier|private
class|class
name|ShapePair
extends|extends
name|ShapeCollection
argument_list|<
name|Rectangle
argument_list|>
block|{
DECL|field|shape1
DECL|field|shape2
specifier|final
name|Rectangle
name|shape1
decl_stmt|,
name|shape2
decl_stmt|;
DECL|field|biasContainsThenWithin
specifier|final
name|boolean
name|biasContainsThenWithin
decl_stmt|;
comment|//a hack
DECL|method|ShapePair
specifier|public
name|ShapePair
parameter_list|(
name|Rectangle
name|shape1
parameter_list|,
name|Rectangle
name|shape2
parameter_list|,
name|boolean
name|containsThenWithin
parameter_list|)
block|{
name|super
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|shape1
argument_list|,
name|shape2
argument_list|)
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|shape1
operator|=
name|shape1
expr_stmt|;
name|this
operator|.
name|shape2
operator|=
name|shape2
expr_stmt|;
name|biasContainsThenWithin
operator|=
name|containsThenWithin
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Shape
name|other
parameter_list|)
block|{
name|SpatialRelation
name|r
init|=
name|relateApprox
argument_list|(
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|CONTAINS
condition|)
return|return
name|r
return|;
if|if
condition|(
name|r
operator|==
name|DISJOINT
condition|)
return|return
name|r
return|;
if|if
condition|(
name|r
operator|==
name|WITHIN
operator|&&
operator|!
name|biasContainsThenWithin
condition|)
return|return
name|r
return|;
comment|//See if the correct answer is actually Contains, when the indexed shapes are adjacent,
comment|// creating a larger shape that contains the input shape.
name|boolean
name|pairTouches
init|=
name|shape1
operator|.
name|relate
argument_list|(
name|shape2
argument_list|)
operator|.
name|intersects
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pairTouches
condition|)
return|return
name|r
return|;
comment|//test all 4 corners
name|Rectangle
name|oRect
init|=
operator|(
name|Rectangle
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|relate
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|oRect
operator|.
name|getMinX
argument_list|()
argument_list|,
name|oRect
operator|.
name|getMinY
argument_list|()
argument_list|)
argument_list|)
operator|==
name|CONTAINS
operator|&&
name|relate
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|oRect
operator|.
name|getMinX
argument_list|()
argument_list|,
name|oRect
operator|.
name|getMaxY
argument_list|()
argument_list|)
argument_list|)
operator|==
name|CONTAINS
operator|&&
name|relate
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|oRect
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|oRect
operator|.
name|getMinY
argument_list|()
argument_list|)
argument_list|)
operator|==
name|CONTAINS
operator|&&
name|relate
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|oRect
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|oRect
operator|.
name|getMaxY
argument_list|()
argument_list|)
argument_list|)
operator|==
name|CONTAINS
condition|)
return|return
name|CONTAINS
return|;
return|return
name|r
return|;
block|}
DECL|method|relateApprox
specifier|private
name|SpatialRelation
name|relateApprox
parameter_list|(
name|Shape
name|other
parameter_list|)
block|{
if|if
condition|(
name|biasContainsThenWithin
condition|)
block|{
if|if
condition|(
name|shape1
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|CONTAINS
operator|||
name|shape1
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|||
name|shape2
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|CONTAINS
operator|||
name|shape2
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
return|return
name|CONTAINS
return|;
if|if
condition|(
name|shape1
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|WITHIN
operator|&&
name|shape2
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|WITHIN
condition|)
return|return
name|WITHIN
return|;
block|}
else|else
block|{
if|if
condition|(
operator|(
name|shape1
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|WITHIN
operator|||
name|shape1
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|)
operator|&&
operator|(
name|shape2
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|WITHIN
operator|||
name|shape2
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|)
condition|)
return|return
name|WITHIN
return|;
if|if
condition|(
name|shape1
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|CONTAINS
operator|||
name|shape2
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|==
name|CONTAINS
condition|)
return|return
name|CONTAINS
return|;
block|}
if|if
condition|(
name|shape1
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|.
name|intersects
argument_list|()
operator|||
name|shape2
operator|.
name|relate
argument_list|(
name|other
argument_list|)
operator|.
name|intersects
argument_list|()
condition|)
return|return
name|INTERSECTS
return|;
comment|//might actually be 'CONTAINS' if the pair are adjacent but we handle that later
return|return
name|DISJOINT
return|;
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
literal|"ShapePair("
operator|+
name|shape1
operator|+
literal|" , "
operator|+
name|shape2
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

