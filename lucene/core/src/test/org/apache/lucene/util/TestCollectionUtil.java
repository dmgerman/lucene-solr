begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ArrayList
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
name|LinkedList
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
name|Random
import|;
end_import

begin_class
DECL|class|TestCollectionUtil
specifier|public
class|class
name|TestCollectionUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|createRandomList
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|createRandomList
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|Integer
index|[]
name|a
init|=
operator|new
name|Integer
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
operator|+
literal|1
index|]
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|a
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|a
argument_list|)
return|;
block|}
DECL|method|testQuickSort
specifier|public
name|void
name|testQuickSort
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
name|createRandomList
argument_list|(
literal|1000
argument_list|)
decl_stmt|,
name|list2
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
decl_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
name|list1
operator|=
name|createRandomList
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|list2
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMergeSort
specifier|public
name|void
name|testMergeSort
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
name|createRandomList
argument_list|(
literal|1000
argument_list|)
decl_stmt|,
name|list2
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
decl_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
name|list1
operator|=
name|createRandomList
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|list2
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInsertionSort
specifier|public
name|void
name|testInsertionSort
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list1
init|=
name|createRandomList
argument_list|(
literal|30
argument_list|)
decl_stmt|,
name|list2
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
decl_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
name|list1
operator|=
name|createRandomList
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|list2
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list2
argument_list|,
name|list1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyListSort
specifier|public
name|void
name|testEmptyListSort
parameter_list|()
block|{
comment|// should produce no exceptions
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// LUCENE-2989
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
comment|// check that empty non-random access lists pass sorting without ex (as sorting is not needed)
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneElementListSort
specifier|public
name|void
name|testOneElementListSort
parameter_list|()
block|{
comment|// check that one-element non-random access lists pass sorting without ex (as sorting is not needed)
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|insertionSort
argument_list|(
name|list
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

