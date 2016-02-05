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
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|DateRangePrefixTreeTest
specifier|public
class|class
name|DateRangePrefixTreeTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|tree
specifier|private
name|DateRangePrefixTree
name|tree
init|=
name|DateRangePrefixTree
operator|.
name|INSTANCE
decl_stmt|;
DECL|method|testRoundTrip
specifier|public
name|void
name|testRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
name|Calendar
name|cal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"*"
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
argument_list|)
expr_stmt|;
comment|//test no underflow
name|assertTrue
argument_list|(
name|tree
operator|.
name|toShape
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Some arbitrary date
name|cal
operator|.
name|set
argument_list|(
literal|2014
argument_list|,
name|Calendar
operator|.
name|MAY
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014-05-09"
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
argument_list|)
expr_stmt|;
comment|//Earliest date
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
comment|//Farthest date
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
comment|//1BC is "0000".
name|cal
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|ERA
argument_list|,
name|GregorianCalendar
operator|.
name|BC
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0000"
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
argument_list|)
expr_stmt|;
comment|//adding a "+" parses to the same; and a trailing 'Z' is fine too
name|assertEquals
argument_list|(
name|cal
argument_list|,
name|tree
operator|.
name|parseCalendar
argument_list|(
literal|"+0000Z"
argument_list|)
argument_list|)
expr_stmt|;
comment|//2BC is "-0001"
name|cal
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|ERA
argument_list|,
name|GregorianCalendar
operator|.
name|BC
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-0001"
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
argument_list|)
expr_stmt|;
comment|//1AD is "0001"
name|cal
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0001"
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
argument_list|)
expr_stmt|;
comment|//test random
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|roundTrip
argument_list|(
name|cal
argument_list|)
expr_stmt|;
block|}
comment|//copies from DateRangePrefixTree
DECL|field|CAL_FIELDS
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|CAL_FIELDS
init|=
block|{
name|Calendar
operator|.
name|YEAR
block|,
name|Calendar
operator|.
name|MONTH
block|,
name|Calendar
operator|.
name|DAY_OF_MONTH
block|,
name|Calendar
operator|.
name|HOUR_OF_DAY
block|,
name|Calendar
operator|.
name|MINUTE
block|,
name|Calendar
operator|.
name|SECOND
block|,
name|Calendar
operator|.
name|MILLISECOND
block|}
decl_stmt|;
DECL|method|roundTrip
specifier|private
name|void
name|roundTrip
parameter_list|(
name|Calendar
name|calOrig
parameter_list|)
throws|throws
name|ParseException
block|{
name|Calendar
name|cal
init|=
operator|(
name|Calendar
operator|)
name|calOrig
operator|.
name|clone
argument_list|()
decl_stmt|;
name|String
name|lastString
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|calString
init|=
name|tree
operator|.
name|toString
argument_list|(
name|cal
argument_list|)
decl_stmt|;
assert|assert
name|lastString
operator|==
literal|null
operator|||
name|calString
operator|.
name|length
argument_list|()
operator|<
name|lastString
operator|.
name|length
argument_list|()
assert|;
comment|//test parseCalendar
name|assertEquals
argument_list|(
name|cal
argument_list|,
name|tree
operator|.
name|parseCalendar
argument_list|(
name|calString
argument_list|)
argument_list|)
expr_stmt|;
comment|//to Shape and back to Cal
name|UnitNRShape
name|shape
init|=
name|tree
operator|.
name|toShape
argument_list|(
name|cal
argument_list|)
decl_stmt|;
name|Calendar
name|cal2
init|=
name|tree
operator|.
name|toCalendar
argument_list|(
name|shape
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|calString
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|calString
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
comment|//not world cell
comment|//to Term and back to Cell
name|Cell
name|cell
init|=
operator|(
name|Cell
operator|)
name|shape
decl_stmt|;
name|BytesRef
name|term
init|=
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Cell
name|cell2
init|=
name|tree
operator|.
name|readCell
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|calString
argument_list|,
name|cell
argument_list|,
name|cell2
argument_list|)
expr_stmt|;
name|Calendar
name|cal3
init|=
name|tree
operator|.
name|toCalendar
argument_list|(
operator|(
name|UnitNRShape
operator|)
name|cell2
operator|.
name|getShape
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|calString
argument_list|,
name|tree
operator|.
name|toString
argument_list|(
name|cal3
argument_list|)
argument_list|)
expr_stmt|;
comment|// setLeaf comparison
name|cell2
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
name|BytesRef
name|termLeaf
init|=
name|cell2
operator|.
name|getTokenBytesWithLeaf
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|term
operator|.
name|compareTo
argument_list|(
name|termLeaf
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|termLeaf
operator|.
name|length
argument_list|,
name|term
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termLeaf
operator|.
name|bytes
index|[
name|termLeaf
operator|.
name|offset
operator|+
name|termLeaf
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cell
operator|.
name|isPrefixOf
argument_list|(
name|cell2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//end of loop; decide if should loop again with lower precision
specifier|final
name|int
name|calPrecField
init|=
name|tree
operator|.
name|getCalPrecisionField
argument_list|(
name|cal
argument_list|)
decl_stmt|;
if|if
condition|(
name|calPrecField
operator|==
operator|-
literal|1
condition|)
break|break;
name|int
name|fieldIdx
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|CAL_FIELDS
argument_list|,
name|calPrecField
argument_list|)
decl_stmt|;
assert|assert
name|fieldIdx
operator|>=
literal|0
assert|;
name|int
name|prevPrecField
init|=
operator|(
name|fieldIdx
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|CAL_FIELDS
index|[
operator|--
name|fieldIdx
index|]
operator|)
decl_stmt|;
try|try
block|{
name|tree
operator|.
name|clearFieldsAfter
argument_list|(
name|cal
argument_list|,
name|prevPrecField
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
return|return;
throw|throw
name|e
throw|;
block|}
name|lastString
operator|=
name|calString
expr_stmt|;
block|}
block|}
DECL|method|testShapeRelations
specifier|public
name|void
name|testShapeRelations
parameter_list|()
throws|throws
name|ParseException
block|{
comment|//note: left range is 264000 at the thousand year level whereas right value is exact year
name|assertEquals
argument_list|(
name|SpatialRelation
operator|.
name|WITHIN
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[-264000 TO -264000-11-20]"
argument_list|)
operator|.
name|relate
argument_list|(
name|tree
operator|.
name|parseShape
argument_list|(
literal|"-264000"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Shape
name|shapeA
init|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[3122-01-23 TO 3122-11-27]"
argument_list|)
decl_stmt|;
name|Shape
name|shapeB
init|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[3122-08 TO 3122-11]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SpatialRelation
operator|.
name|INTERSECTS
argument_list|,
name|shapeA
operator|.
name|relate
argument_list|(
name|shapeB
argument_list|)
argument_list|)
expr_stmt|;
name|shapeA
operator|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"3122"
argument_list|)
expr_stmt|;
name|shapeB
operator|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[* TO 3122-10-31]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SpatialRelation
operator|.
name|INTERSECTS
argument_list|,
name|shapeA
operator|.
name|relate
argument_list|(
name|shapeB
argument_list|)
argument_list|)
expr_stmt|;
name|shapeA
operator|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[3122-05-28 TO 3122-06-29]"
argument_list|)
expr_stmt|;
name|shapeB
operator|=
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[3122 TO 3122-04]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SpatialRelation
operator|.
name|DISJOINT
argument_list|,
name|shapeA
operator|.
name|relate
argument_list|(
name|shapeB
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testShapeRangeOptimizer
specifier|public
name|void
name|testShapeRangeOptimizer
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertEquals
argument_list|(
literal|"[2014-08 TO 2014-09]"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-08-01 TO 2014-09-30]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-01-01 TO 2014-12-31]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-01 TO 2014]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014-01"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014 TO 2014-01]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014-12"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-12 TO 2014]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[2014 TO 2014-04-06]"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-01 TO 2014-04-06]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"*"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[* TO *]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2014-08-01"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-08-01 TO 2014-08-01]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[2014 TO 2014-09-15]"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014 TO 2014-09-15]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[* TO 2014-09-15]"
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[* TO 2014-09-15]"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

