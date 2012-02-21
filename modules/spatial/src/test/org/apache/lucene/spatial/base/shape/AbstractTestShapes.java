begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.base.shape
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
package|;
end_package

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
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|distance
operator|.
name|DistanceCalculator
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
name|LuceneTestCase
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author David Smiley - dsmiley@mitre.org  */
end_comment

begin_class
DECL|class|AbstractTestShapes
specifier|public
specifier|abstract
class|class
name|AbstractTestShapes
extends|extends
name|LuceneTestCase
block|{
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|EPS
specifier|private
specifier|static
specifier|final
name|double
name|EPS
init|=
literal|10e-9
decl_stmt|;
annotation|@
name|Before
DECL|method|beforeClass
specifier|public
name|void
name|beforeClass
parameter_list|()
block|{
name|ctx
operator|=
name|getContext
argument_list|()
expr_stmt|;
block|}
DECL|method|assertRelation
specifier|protected
name|void
name|assertRelation
parameter_list|(
name|String
name|msg
parameter_list|,
name|SpatialRelation
name|expected
parameter_list|,
name|Shape
name|a
parameter_list|,
name|Shape
name|b
parameter_list|)
block|{
name|msg
operator|=
name|a
operator|+
literal|" intersect "
operator|+
name|b
expr_stmt|;
comment|//use different msg
name|_assertIntersect
argument_list|(
name|msg
argument_list|,
name|expected
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
comment|//check flipped a& b w/ transpose(), while we're at it
name|_assertIntersect
argument_list|(
literal|"(transposed) "
operator|+
name|msg
argument_list|,
name|expected
operator|.
name|transpose
argument_list|()
argument_list|,
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|_assertIntersect
specifier|private
name|void
name|_assertIntersect
parameter_list|(
name|String
name|msg
parameter_list|,
name|SpatialRelation
name|expected
parameter_list|,
name|Shape
name|a
parameter_list|,
name|Shape
name|b
parameter_list|)
block|{
name|SpatialRelation
name|sect
init|=
name|a
operator|.
name|relate
argument_list|(
name|b
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|sect
operator|==
name|expected
condition|)
return|return;
if|if
condition|(
name|expected
operator|==
name|WITHIN
operator|||
name|expected
operator|==
name|CONTAINS
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|b
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
comment|// they are the same shape type
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
else|else
block|{
comment|//they are effectively points or lines that are the same location
name|assertTrue
argument_list|(
name|msg
argument_list|,
operator|!
name|a
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
operator|!
name|b
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|Rectangle
name|aBBox
init|=
name|a
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|Rectangle
name|bBBox
init|=
name|b
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|aBBox
operator|.
name|getHeight
argument_list|()
operator|==
literal|0
operator|&&
name|bBBox
operator|.
name|getHeight
argument_list|()
operator|==
literal|0
operator|&&
operator|(
name|aBBox
operator|.
name|getMaxY
argument_list|()
operator|==
literal|90
operator|&&
name|bBBox
operator|.
name|getMaxY
argument_list|()
operator|==
literal|90
operator|||
name|aBBox
operator|.
name|getMinY
argument_list|()
operator|==
operator|-
literal|90
operator|&&
name|bBBox
operator|.
name|getMinY
argument_list|()
operator|==
operator|-
literal|90
operator|)
condition|)
empty_stmt|;
comment|//== a point at the pole
else|else
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|aBBox
argument_list|,
name|bBBox
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|expected
argument_list|,
name|sect
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertEqualsRatio
specifier|private
name|void
name|assertEqualsRatio
parameter_list|(
name|String
name|msg
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|actual
parameter_list|)
block|{
name|double
name|delta
init|=
name|Math
operator|.
name|abs
argument_list|(
name|actual
operator|-
name|expected
argument_list|)
decl_stmt|;
name|double
name|base
init|=
name|Math
operator|.
name|min
argument_list|(
name|actual
argument_list|,
name|expected
argument_list|)
decl_stmt|;
name|double
name|deltaRatio
init|=
name|base
operator|==
literal|0
condition|?
name|delta
else|:
name|Math
operator|.
name|min
argument_list|(
name|delta
argument_list|,
name|delta
operator|/
name|base
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|0
argument_list|,
name|deltaRatio
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
block|}
DECL|method|testRectangle
specifier|protected
name|void
name|testRectangle
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|width
parameter_list|,
name|double
name|minY
parameter_list|,
name|double
name|height
parameter_list|)
block|{
name|Rectangle
name|r
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|minX
argument_list|,
name|minX
operator|+
name|width
argument_list|,
name|minY
argument_list|,
name|minY
operator|+
name|height
argument_list|)
decl_stmt|;
comment|//test equals& hashcode of duplicate
name|Rectangle
name|r2
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|minX
argument_list|,
name|minX
operator|+
name|width
argument_list|,
name|minY
argument_list|,
name|minY
operator|+
name|height
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|r2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|hashCode
argument_list|()
argument_list|,
name|r2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
name|r
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|width
operator|!=
literal|0
operator|&&
name|height
operator|!=
literal|0
argument_list|,
name|r
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|width
operator|!=
literal|0
operator|&&
name|height
operator|!=
literal|0
argument_list|,
name|r
operator|.
name|getArea
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|height
argument_list|,
name|r
operator|.
name|getHeight
argument_list|()
argument_list|)
expr_stmt|;
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|width
argument_list|,
name|r
operator|.
name|getWidth
argument_list|()
argument_list|)
expr_stmt|;
name|Point
name|center
init|=
name|r
operator|.
name|getCenter
argument_list|()
decl_stmt|;
name|msg
operator|+=
literal|" ctr:"
operator|+
name|center
expr_stmt|;
comment|//System.out.println(msg);
name|assertRelation
argument_list|(
name|msg
argument_list|,
name|CONTAINS
argument_list|,
name|r
argument_list|,
name|center
argument_list|)
expr_stmt|;
name|DistanceCalculator
name|dc
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
decl_stmt|;
name|double
name|dUR
init|=
name|dc
operator|.
name|distance
argument_list|(
name|center
argument_list|,
name|r
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dLR
init|=
name|dc
operator|.
name|distance
argument_list|(
name|center
argument_list|,
name|r
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|r
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dUL
init|=
name|dc
operator|.
name|distance
argument_list|(
name|center
argument_list|,
name|r
operator|.
name|getMinX
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dLL
init|=
name|dc
operator|.
name|distance
argument_list|(
name|center
argument_list|,
name|r
operator|.
name|getMinX
argument_list|()
argument_list|,
name|r
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|width
operator|!=
literal|0
operator|||
name|height
operator|!=
literal|0
argument_list|,
name|dUR
operator|!=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|dUR
operator|!=
literal|0
condition|)
name|assertTrue
argument_list|(
name|dUR
operator|>
literal|0
operator|&&
name|dLL
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|dUR
argument_list|,
name|dUL
argument_list|)
expr_stmt|;
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|dLR
argument_list|,
name|dLL
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ctx
operator|.
name|isGeo
argument_list|()
operator|||
name|center
operator|.
name|getY
argument_list|()
operator|==
literal|0
condition|)
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|dUR
argument_list|,
name|dLL
argument_list|)
expr_stmt|;
block|}
DECL|method|testRectIntersect
specifier|protected
name|void
name|testRectIntersect
parameter_list|()
block|{
specifier|final
name|double
name|INCR
init|=
literal|45
decl_stmt|;
specifier|final
name|double
name|Y
init|=
literal|10
decl_stmt|;
for|for
control|(
name|double
name|left
init|=
operator|-
literal|180
init|;
name|left
operator|<=
literal|180
condition|;
name|left
operator|+=
name|INCR
control|)
block|{
for|for
control|(
name|double
name|right
init|=
name|left
init|;
name|right
operator|-
name|left
operator|<=
literal|360
condition|;
name|right
operator|+=
name|INCR
control|)
block|{
name|Rectangle
name|r
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
operator|-
name|Y
argument_list|,
name|Y
argument_list|)
decl_stmt|;
comment|//test contains (which also tests within)
for|for
control|(
name|double
name|left2
init|=
name|left
init|;
name|left2
operator|<=
name|right
condition|;
name|left2
operator|+=
name|INCR
control|)
block|{
for|for
control|(
name|double
name|right2
init|=
name|left2
init|;
name|right2
operator|<=
name|right
condition|;
name|right2
operator|+=
name|INCR
control|)
block|{
name|Rectangle
name|r2
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|left2
argument_list|,
name|right2
argument_list|,
operator|-
name|Y
argument_list|,
name|Y
argument_list|)
decl_stmt|;
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|SpatialRelation
operator|.
name|CONTAINS
argument_list|,
name|r
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
block|}
comment|//test point contains
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|SpatialRelation
operator|.
name|CONTAINS
argument_list|,
name|r
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
name|left
argument_list|,
name|Y
argument_list|)
argument_list|)
expr_stmt|;
comment|//test disjoint
for|for
control|(
name|double
name|left2
init|=
name|right
operator|+
name|INCR
init|;
name|left2
operator|-
name|left
operator|<
literal|360
condition|;
name|left2
operator|+=
name|INCR
control|)
block|{
for|for
control|(
name|double
name|right2
init|=
name|left2
init|;
name|right2
operator|-
name|left
operator|<
literal|360
condition|;
name|right2
operator|+=
name|INCR
control|)
block|{
name|Rectangle
name|r2
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|left2
argument_list|,
name|right2
argument_list|,
operator|-
name|Y
argument_list|,
name|Y
argument_list|)
decl_stmt|;
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|SpatialRelation
operator|.
name|DISJOINT
argument_list|,
name|r
argument_list|,
name|r2
argument_list|)
expr_stmt|;
comment|//test point disjoint
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|SpatialRelation
operator|.
name|DISJOINT
argument_list|,
name|r
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
name|left2
argument_list|,
name|Y
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//test intersect
for|for
control|(
name|double
name|left2
init|=
name|left
operator|+
name|INCR
init|;
name|left2
operator|<=
name|right
condition|;
name|left2
operator|+=
name|INCR
control|)
block|{
for|for
control|(
name|double
name|right2
init|=
name|right
operator|+
name|INCR
init|;
name|right2
operator|-
name|left
operator|<
literal|360
condition|;
name|right2
operator|+=
name|INCR
control|)
block|{
name|Rectangle
name|r2
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|left2
argument_list|,
name|right2
argument_list|,
operator|-
name|Y
argument_list|,
name|Y
argument_list|)
decl_stmt|;
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|SpatialRelation
operator|.
name|INTERSECTS
argument_list|,
name|r
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|testCircle
specifier|protected
name|void
name|testCircle
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|dist
parameter_list|)
block|{
name|Circle
name|c
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|c
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Circle
name|c2
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|c
argument_list|,
name|c2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c
operator|.
name|hashCode
argument_list|()
argument_list|,
name|c2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|dist
operator|>
literal|0
argument_list|,
name|c
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Rectangle
name|bbox
init|=
name|c
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|dist
operator|>
literal|0
argument_list|,
name|bbox
operator|.
name|getArea
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
comment|//if not geo then units of dist == units of x,y
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|bbox
operator|.
name|getHeight
argument_list|()
argument_list|,
name|dist
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertEqualsRatio
argument_list|(
name|msg
argument_list|,
name|bbox
operator|.
name|getWidth
argument_list|()
argument_list|,
name|dist
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
name|assertRelation
argument_list|(
name|msg
argument_list|,
name|CONTAINS
argument_list|,
name|c
argument_list|,
name|c
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
name|assertRelation
argument_list|(
name|msg
argument_list|,
name|CONTAINS
argument_list|,
name|bbox
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|testCircleIntersect
specifier|protected
name|void
name|testCircleIntersect
parameter_list|()
block|{
comment|//Now do some randomized tests:
name|int
name|i_C
init|=
literal|0
decl_stmt|,
name|i_I
init|=
literal|0
decl_stmt|,
name|i_W
init|=
literal|0
decl_stmt|,
name|i_O
init|=
literal|0
decl_stmt|;
comment|//counters for the different intersection cases
name|int
name|laps
init|=
literal|0
decl_stmt|;
name|int
name|MINLAPSPERCASE
init|=
literal|20
decl_stmt|;
while|while
condition|(
name|i_C
operator|<
name|MINLAPSPERCASE
operator|||
name|i_I
operator|<
name|MINLAPSPERCASE
operator|||
name|i_W
operator|<
name|MINLAPSPERCASE
operator|||
name|i_O
operator|<
name|MINLAPSPERCASE
condition|)
block|{
name|laps
operator|++
expr_stmt|;
name|double
name|cX
init|=
name|randRange
argument_list|(
operator|-
literal|180
argument_list|,
literal|179
argument_list|)
decl_stmt|;
name|double
name|cY
init|=
name|randRange
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|)
decl_stmt|;
name|double
name|cR
init|=
name|randRange
argument_list|(
literal|0
argument_list|,
literal|180
argument_list|)
decl_stmt|;
name|double
name|cR_dist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|cR
argument_list|)
decl_stmt|;
name|Circle
name|c
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|cX
argument_list|,
name|cY
argument_list|,
name|cR_dist
argument_list|)
decl_stmt|;
name|double
name|rX
init|=
name|randRange
argument_list|(
operator|-
literal|180
argument_list|,
literal|179
argument_list|)
decl_stmt|;
name|double
name|rW
init|=
name|randRange
argument_list|(
literal|0
argument_list|,
literal|360
argument_list|)
decl_stmt|;
name|double
name|rY1
init|=
name|randRange
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|)
decl_stmt|;
name|double
name|rY2
init|=
name|randRange
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|)
decl_stmt|;
name|double
name|rYmin
init|=
name|Math
operator|.
name|min
argument_list|(
name|rY1
argument_list|,
name|rY2
argument_list|)
decl_stmt|;
name|double
name|rYmax
init|=
name|Math
operator|.
name|max
argument_list|(
name|rY1
argument_list|,
name|rY2
argument_list|)
decl_stmt|;
name|Rectangle
name|r
init|=
name|ctx
operator|.
name|makeRect
argument_list|(
name|rX
argument_list|,
name|rX
operator|+
name|rW
argument_list|,
name|rYmin
argument_list|,
name|rYmax
argument_list|)
decl_stmt|;
name|SpatialRelation
name|ic
init|=
name|c
operator|.
name|relate
argument_list|(
name|r
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Point
name|p
decl_stmt|;
switch|switch
condition|(
name|ic
condition|)
block|{
case|case
name|CONTAINS
case|:
name|i_C
operator|++
expr_stmt|;
name|p
operator|=
name|randomPointWithin
argument_list|(
name|random
argument_list|,
name|r
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|c
operator|.
name|relate
argument_list|(
name|p
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTERSECTS
case|:
name|i_I
operator|++
expr_stmt|;
comment|//hard to test anything here; instead we'll test it separately
break|break;
case|case
name|WITHIN
case|:
name|i_W
operator|++
expr_stmt|;
name|p
operator|=
name|randomPointWithin
argument_list|(
name|random
argument_list|,
name|c
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|r
operator|.
name|relate
argument_list|(
name|p
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DISJOINT
case|:
name|i_O
operator|++
expr_stmt|;
name|p
operator|=
name|randomPointWithin
argument_list|(
name|random
argument_list|,
name|r
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DISJOINT
argument_list|,
name|c
operator|.
name|relate
argument_list|(
name|p
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|""
operator|+
name|ic
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Laps: "
operator|+
name|laps
argument_list|)
expr_stmt|;
comment|//TODO deliberately test INTERSECTS based on known intersection point
block|}
comment|/** Returns a random integer between [start, end] with a limited number of possibilities instead of end-start+1. */
DECL|method|randRange
specifier|private
name|int
name|randRange
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
comment|//I tested this.
name|double
name|r
init|=
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|int
name|BUCKETS
init|=
literal|91
decl_stmt|;
name|int
name|ir
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|r
operator|*
operator|(
name|BUCKETS
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
comment|//put into buckets
name|int
name|result
init|=
call|(
name|int
call|)
argument_list|(
call|(
name|double
call|)
argument_list|(
operator|(
name|end
operator|-
name|start
operator|)
operator|*
name|ir
argument_list|)
operator|/
call|(
name|double
call|)
argument_list|(
name|BUCKETS
operator|-
literal|1
argument_list|)
operator|+
operator|(
name|double
operator|)
name|start
argument_list|)
decl_stmt|;
assert|assert
name|result
operator|>=
name|start
operator|&&
name|result
operator|<=
name|end
assert|;
return|return
name|result
return|;
block|}
DECL|method|randomPointWithin
specifier|private
name|Point
name|randomPointWithin
parameter_list|(
name|Random
name|random
parameter_list|,
name|Circle
name|c
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|double
name|d
init|=
name|c
operator|.
name|getDistance
argument_list|()
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|angleDEG
init|=
literal|360
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|Point
name|p
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|pointOnBearing
argument_list|(
name|c
operator|.
name|getCenter
argument_list|()
argument_list|,
name|d
argument_list|,
name|angleDEG
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|c
operator|.
name|relate
argument_list|(
name|p
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|randomPointWithin
specifier|private
name|Point
name|randomPointWithin
parameter_list|(
name|Random
name|random
parameter_list|,
name|Rectangle
name|r
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|double
name|x
init|=
name|r
operator|.
name|getMinX
argument_list|()
operator|+
name|random
operator|.
name|nextDouble
argument_list|()
operator|*
name|r
operator|.
name|getWidth
argument_list|()
decl_stmt|;
name|double
name|y
init|=
name|r
operator|.
name|getMinY
argument_list|()
operator|+
name|random
operator|.
name|nextDouble
argument_list|()
operator|*
name|r
operator|.
name|getHeight
argument_list|()
decl_stmt|;
name|Point
name|p
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|r
operator|.
name|relate
argument_list|(
name|p
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|getContext
specifier|protected
specifier|abstract
name|SpatialContext
name|getContext
parameter_list|()
function_decl|;
block|}
end_class

end_unit

