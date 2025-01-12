begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|facet
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_class
DECL|class|FieldFacetExtrasTest
specifier|public
class|class
name|FieldFacetExtrasTest
extends|extends
name|AbstractAnalyticsFacetTest
block|{
DECL|field|fileName
specifier|static
name|String
name|fileName
init|=
literal|"/analytics/requestFiles/fieldFacetExtras.txt"
decl_stmt|;
DECL|field|INT
specifier|public
specifier|static
specifier|final
name|int
name|INT
init|=
literal|21
decl_stmt|;
DECL|field|LONG
specifier|public
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|22
decl_stmt|;
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|int
name|FLOAT
init|=
literal|23
decl_stmt|;
DECL|field|DOUBLE
specifier|public
specifier|static
specifier|final
name|int
name|DOUBLE
init|=
literal|24
decl_stmt|;
DECL|field|DATE
specifier|public
specifier|static
specifier|final
name|int
name|DATE
init|=
literal|25
decl_stmt|;
DECL|field|STRING
specifier|public
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|26
decl_stmt|;
DECL|field|NUM_LOOPS
specifier|public
specifier|static
specifier|final
name|int
name|NUM_LOOPS
init|=
literal|100
decl_stmt|;
comment|//INT
DECL|field|intLongTestStart
specifier|static
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|intLongTestStart
decl_stmt|;
DECL|field|intFloatTestStart
specifier|static
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|intFloatTestStart
decl_stmt|;
DECL|field|intDoubleTestStart
specifier|static
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|intDoubleTestStart
decl_stmt|;
DECL|field|intStringTestStart
specifier|static
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|intStringTestStart
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-analytics.xml"
argument_list|)
expr_stmt|;
name|h
operator|.
name|update
argument_list|(
literal|"<delete><query>*:*</query></delete>"
argument_list|)
expr_stmt|;
comment|//INT
name|intLongTestStart
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|intFloatTestStart
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|intDoubleTestStart
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|intStringTestStart
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_LOOPS
condition|;
operator|++
name|j
control|)
block|{
name|int
name|i
init|=
name|j
operator|%
name|INT
decl_stmt|;
name|long
name|l
init|=
name|j
operator|%
name|LONG
decl_stmt|;
name|float
name|f
init|=
name|j
operator|%
name|FLOAT
decl_stmt|;
name|double
name|d
init|=
name|j
operator|%
name|DOUBLE
decl_stmt|;
name|int
name|dt
init|=
name|j
operator|%
name|DATE
decl_stmt|;
name|int
name|s
init|=
name|j
operator|%
name|STRING
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|j
argument_list|,
literal|"int_id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"long_ld"
argument_list|,
literal|""
operator|+
name|l
argument_list|,
literal|"float_fd"
argument_list|,
literal|""
operator|+
name|f
argument_list|,
literal|"double_dd"
argument_list|,
literal|""
operator|+
name|d
argument_list|,
literal|"date_dtd"
argument_list|,
operator|(
literal|1800
operator|+
name|dt
operator|)
operator|+
literal|"-12-31T23:59:59.999Z"
argument_list|,
literal|"string_sd"
argument_list|,
literal|"abc"
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
comment|//Long
if|if
condition|(
name|j
operator|-
name|LONG
operator|<
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list1
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|intLongTestStart
operator|.
name|add
argument_list|(
name|list1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|intLongTestStart
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|l
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|//String
if|if
condition|(
name|j
operator|-
name|FLOAT
operator|<
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list1
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|intFloatTestStart
operator|.
name|add
argument_list|(
name|list1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|intFloatTestStart
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|f
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|//String
if|if
condition|(
name|j
operator|-
name|DOUBLE
operator|<
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list1
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|intDoubleTestStart
operator|.
name|add
argument_list|(
name|list1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|intDoubleTestStart
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|d
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|//String
if|if
condition|(
name|j
operator|-
name|STRING
operator|<
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list1
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|intStringTestStart
operator|.
name|add
argument_list|(
name|list1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|intStringTestStart
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// to have several segments
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|setResponse
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|request
argument_list|(
name|fileToStringArr
argument_list|(
name|FieldFacetExtrasTest
operator|.
name|class
argument_list|,
name|fileName
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|limitTest
specifier|public
name|void
name|limitTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|Double
argument_list|>
name|lon
init|=
name|getDoubleList
argument_list|(
literal|"lr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|lon
operator|.
name|size
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Double
argument_list|>
name|flo
init|=
name|getDoubleList
argument_list|(
literal|"lr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"float_fd"
argument_list|,
literal|"double"
argument_list|,
literal|"median"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|flo
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Long
argument_list|>
name|doub
init|=
name|getLongList
argument_list|(
literal|"lr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"double_dd"
argument_list|,
literal|"long"
argument_list|,
literal|"count"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|doub
operator|.
name|size
argument_list|()
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Integer
argument_list|>
name|string
init|=
name|getIntegerList
argument_list|(
literal|"lr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"string_sd"
argument_list|,
literal|"int"
argument_list|,
literal|"percentile_20"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|string
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|offsetTest
specifier|public
name|void
name|offsetTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|Double
argument_list|>
name|lon
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|lon
operator|=
name|getDoubleList
argument_list|(
literal|"off0"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|lon
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Double
index|[]
block|{
literal|1.5
block|,
literal|2.0
block|}
argument_list|,
name|lon
operator|.
name|toArray
argument_list|(
operator|new
name|Double
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|lon
operator|=
name|getDoubleList
argument_list|(
literal|"off1"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|lon
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Double
index|[]
block|{
literal|3.0
block|,
literal|4.0
block|}
argument_list|,
name|lon
operator|.
name|toArray
argument_list|(
operator|new
name|Double
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|lon
operator|=
name|getDoubleList
argument_list|(
literal|"off2"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|lon
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Double
index|[]
block|{
literal|5.0
block|,
literal|5.75
block|,
literal|6.0
block|}
argument_list|,
name|lon
operator|.
name|toArray
argument_list|(
operator|new
name|Double
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|lon
operator|=
name|getDoubleList
argument_list|(
literal|"offAll"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|lon
operator|.
name|size
argument_list|()
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|all
operator|.
name|toArray
argument_list|(
operator|new
name|Double
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|lon
operator|.
name|toArray
argument_list|(
operator|new
name|Double
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|sortTest
specifier|public
name|void
name|sortTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|Double
argument_list|>
name|lon
init|=
name|getDoubleList
argument_list|(
literal|"sr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"long_ld"
argument_list|,
literal|"double"
argument_list|,
literal|"mean"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Double
argument_list|>
name|longTest
init|=
name|calculateNumberStat
argument_list|(
name|intLongTestStart
argument_list|,
literal|"mean"
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|longTest
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|longTest
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Double
argument_list|>
name|flo
init|=
name|getDoubleList
argument_list|(
literal|"sr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"float_fd"
argument_list|,
literal|"double"
argument_list|,
literal|"median"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Double
argument_list|>
name|floatTest
init|=
name|calculateNumberStat
argument_list|(
name|intFloatTestStart
argument_list|,
literal|"median"
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|floatTest
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|floatTest
argument_list|,
name|flo
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Long
argument_list|>
name|doub
init|=
name|getLongList
argument_list|(
literal|"sr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"double_dd"
argument_list|,
literal|"long"
argument_list|,
literal|"count"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|doubleTest
init|=
operator|(
name|ArrayList
argument_list|<
name|Long
argument_list|>
operator|)
name|calculateStat
argument_list|(
name|intDoubleTestStart
argument_list|,
literal|"count"
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|doubleTest
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|doubleTest
argument_list|,
name|doub
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Integer
argument_list|>
name|string
init|=
name|getIntegerList
argument_list|(
literal|"sr"
argument_list|,
literal|"fieldFacets"
argument_list|,
literal|"string_sd"
argument_list|,
literal|"int"
argument_list|,
literal|"percentile_20"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|stringTest
init|=
operator|(
name|ArrayList
argument_list|<
name|Integer
argument_list|>
operator|)
name|calculateStat
argument_list|(
name|intStringTestStart
argument_list|,
literal|"perc_20"
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|stringTest
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|stringTest
argument_list|,
name|string
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

