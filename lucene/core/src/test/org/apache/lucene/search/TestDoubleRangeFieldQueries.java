begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|DoubleRange
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
name|IndexReader
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
name|RandomIndexWriter
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * Random testing for RangeFieldQueries.  */
end_comment

begin_class
DECL|class|TestDoubleRangeFieldQueries
specifier|public
class|class
name|TestDoubleRangeFieldQueries
extends|extends
name|BaseRangeFieldQueryTestCase
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"doubleRangeField"
decl_stmt|;
DECL|method|nextDoubleInternal
specifier|private
name|double
name|nextDoubleInternal
parameter_list|()
block|{
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Double
operator|.
name|NEGATIVE_INFINITY
return|;
case|case
literal|1
case|:
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
default|default:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
operator|-
literal|7
operator|)
operator|/
literal|3d
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|nextRange
specifier|protected
name|Range
name|nextRange
parameter_list|(
name|int
name|dimensions
parameter_list|)
throws|throws
name|Exception
block|{
name|double
index|[]
name|min
init|=
operator|new
name|double
index|[
name|dimensions
index|]
decl_stmt|;
name|double
index|[]
name|max
init|=
operator|new
name|double
index|[
name|dimensions
index|]
decl_stmt|;
name|double
name|minV
decl_stmt|,
name|maxV
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|dimensions
condition|;
operator|++
name|d
control|)
block|{
name|minV
operator|=
name|nextDoubleInternal
argument_list|()
expr_stmt|;
name|maxV
operator|=
name|nextDoubleInternal
argument_list|()
expr_stmt|;
name|min
index|[
name|d
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minV
argument_list|,
name|maxV
argument_list|)
expr_stmt|;
name|max
index|[
name|d
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minV
argument_list|,
name|maxV
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DoubleTestRange
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newRangeField
specifier|protected
name|DoubleRange
name|newRangeField
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newIntersectsQuery
specifier|protected
name|Query
name|newIntersectsQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|DoubleRange
operator|.
name|newIntersectsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newContainsQuery
specifier|protected
name|Query
name|newContainsQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|DoubleRange
operator|.
name|newContainsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newWithinQuery
specifier|protected
name|Query
name|newWithinQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|DoubleRange
operator|.
name|newWithinQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newCrossesQuery
specifier|protected
name|Query
name|newCrossesQuery
parameter_list|(
name|Range
name|r
parameter_list|)
block|{
return|return
name|DoubleRange
operator|.
name|newCrossesQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|min
argument_list|,
operator|(
operator|(
name|DoubleTestRange
operator|)
name|r
operator|)
operator|.
name|max
argument_list|)
return|;
block|}
comment|/** Basic test */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// intersects (within)
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|10.0
block|,
operator|-
literal|10.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|9.1
block|,
literal|10.1
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// intersects (crosses)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|10.0
block|,
operator|-
literal|10.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|20.0
block|,
literal|10.0
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// intersects (contains, crosses)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|20.0
block|,
operator|-
literal|20.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|30.0
block|,
literal|30.1
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// intersects (crosses)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.1
block|,
operator|-
literal|11.2
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|1.23
block|,
literal|11.5
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// intersects (crosses)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
literal|12.33
block|,
literal|1.2
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15.1
block|,
literal|29.9
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// disjoint
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|122.33
block|,
literal|1.2
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|115.1
block|,
literal|29.9
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// intersects (crosses)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
literal|1.2
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.0
block|,
literal|29.9
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// equal (within, contains, intersects)
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleRange
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11
block|,
operator|-
literal|15
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15
block|,
literal|20
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|DoubleRange
operator|.
name|newIntersectsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.0
block|,
operator|-
literal|15.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15.0
block|,
literal|20.0
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|DoubleRange
operator|.
name|newWithinQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.0
block|,
operator|-
literal|15.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15.0
block|,
literal|20.0
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|DoubleRange
operator|.
name|newContainsQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.0
block|,
operator|-
literal|15.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15.0
block|,
literal|20.0
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|DoubleRange
operator|.
name|newCrossesQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|11.0
block|,
operator|-
literal|15.0
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
literal|15.0
block|,
literal|20.0
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** DoubleRange test class implementation - use to validate DoubleRange */
DECL|class|DoubleTestRange
specifier|private
class|class
name|DoubleTestRange
extends|extends
name|Range
block|{
DECL|field|min
name|double
index|[]
name|min
decl_stmt|;
DECL|field|max
name|double
index|[]
name|max
decl_stmt|;
DECL|method|DoubleTestRange
name|DoubleTestRange
parameter_list|(
name|double
index|[]
name|min
parameter_list|,
name|double
index|[]
name|max
parameter_list|)
block|{
assert|assert
name|min
operator|!=
literal|null
operator|&&
name|max
operator|!=
literal|null
operator|&&
name|min
operator|.
name|length
operator|>
literal|0
operator|&&
name|max
operator|.
name|length
operator|>
literal|0
operator|:
literal|"test box: min/max cannot be null or empty"
assert|;
assert|assert
name|min
operator|.
name|length
operator|==
name|max
operator|.
name|length
operator|:
literal|"test box: min/max length do not agree"
assert|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDimensions
specifier|protected
name|int
name|numDimensions
parameter_list|()
block|{
return|return
name|min
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|protected
name|Double
name|getMin
parameter_list|(
name|int
name|dim
parameter_list|)
block|{
return|return
name|min
index|[
name|dim
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|setMin
specifier|protected
name|void
name|setMin
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|double
name|v
init|=
operator|(
name|Double
operator|)
name|val
decl_stmt|;
if|if
condition|(
name|min
index|[
name|dim
index|]
operator|<
name|v
condition|)
block|{
name|max
index|[
name|dim
index|]
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
name|min
index|[
name|dim
index|]
operator|=
name|v
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|protected
name|Double
name|getMax
parameter_list|(
name|int
name|dim
parameter_list|)
block|{
return|return
name|max
index|[
name|dim
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|setMax
specifier|protected
name|void
name|setMax
parameter_list|(
name|int
name|dim
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|double
name|v
init|=
operator|(
name|Double
operator|)
name|val
decl_stmt|;
if|if
condition|(
name|max
index|[
name|dim
index|]
operator|>
name|v
condition|)
block|{
name|min
index|[
name|dim
index|]
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
name|max
index|[
name|dim
index|]
operator|=
name|v
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isEqual
specifier|protected
name|boolean
name|isEqual
parameter_list|(
name|Range
name|other
parameter_list|)
block|{
name|DoubleTestRange
name|o
init|=
operator|(
name|DoubleTestRange
operator|)
name|other
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|min
argument_list|,
name|o
operator|.
name|min
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|max
argument_list|,
name|o
operator|.
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isDisjoint
specifier|protected
name|boolean
name|isDisjoint
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|DoubleTestRange
name|other
init|=
operator|(
name|DoubleTestRange
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|this
operator|.
name|min
operator|.
name|length
condition|;
operator|++
name|d
control|)
block|{
if|if
condition|(
name|this
operator|.
name|min
index|[
name|d
index|]
operator|>
name|other
operator|.
name|max
index|[
name|d
index|]
operator|||
name|this
operator|.
name|max
index|[
name|d
index|]
operator|<
name|other
operator|.
name|min
index|[
name|d
index|]
condition|)
block|{
comment|// disjoint:
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|protected
name|boolean
name|isWithin
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|DoubleTestRange
name|other
init|=
operator|(
name|DoubleTestRange
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|this
operator|.
name|min
operator|.
name|length
condition|;
operator|++
name|d
control|)
block|{
if|if
condition|(
operator|(
name|this
operator|.
name|min
index|[
name|d
index|]
operator|>=
name|other
operator|.
name|min
index|[
name|d
index|]
operator|&&
name|this
operator|.
name|max
index|[
name|d
index|]
operator|<=
name|other
operator|.
name|max
index|[
name|d
index|]
operator|)
operator|==
literal|false
condition|)
block|{
comment|// not within:
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|protected
name|boolean
name|contains
parameter_list|(
name|Range
name|o
parameter_list|)
block|{
name|DoubleTestRange
name|other
init|=
operator|(
name|DoubleTestRange
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|this
operator|.
name|min
operator|.
name|length
condition|;
operator|++
name|d
control|)
block|{
if|if
condition|(
operator|(
name|this
operator|.
name|min
index|[
name|d
index|]
operator|<=
name|other
operator|.
name|min
index|[
name|d
index|]
operator|&&
name|this
operator|.
name|max
index|[
name|d
index|]
operator|>=
name|other
operator|.
name|max
index|[
name|d
index|]
operator|)
operator|==
literal|false
condition|)
block|{
comment|// not contains:
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
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
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"Box("
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|min
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|max
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|1
init|;
name|d
operator|<
name|min
operator|.
name|length
condition|;
operator|++
name|d
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|min
index|[
name|d
index|]
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|max
index|[
name|d
index|]
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

