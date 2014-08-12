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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|BytesRefIterator
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
name|Counter
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestBytesRefArray
specifier|public
class|class
name|TestBytesRefArray
extends|extends
name|LuceneTestCase
block|{
DECL|method|testAppend
specifier|public
name|void
name|testAppend
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|BytesRefArray
name|list
init|=
operator|new
name|BytesRefArray
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|stringList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|0
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stringList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|int
name|entries
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|int
name|initSize
init|=
name|list
operator|.
name|size
argument_list|()
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
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|randomRealisticUnicodeString
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|randomRealisticUnicodeString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
name|initSize
argument_list|,
name|list
operator|.
name|append
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stringList
operator|.
name|add
argument_list|(
name|randomRealisticUnicodeString
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|spare
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"entry "
operator|+
name|i
operator|+
literal|" doesn't match"
argument_list|,
name|stringList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|spare
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check random
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|int
name|e
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|spare
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"entry "
operator|+
name|i
operator|+
literal|" doesn't match"
argument_list|,
name|stringList
operator|.
name|get
argument_list|(
name|e
argument_list|)
argument_list|,
name|spare
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|BytesRefIterator
name|iterator
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|stringList
control|)
block|{
name|assertEquals
argument_list|(
name|string
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|BytesRefArray
name|list
init|=
operator|new
name|BytesRefArray
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|stringList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|0
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stringList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|int
name|entries
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|initSize
init|=
name|list
operator|.
name|size
argument_list|()
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
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|randomRealisticUnicodeString
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|randomRealisticUnicodeString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initSize
operator|+
name|i
argument_list|,
name|list
operator|.
name|append
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stringList
operator|.
name|add
argument_list|(
name|randomRealisticUnicodeString
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|stringList
argument_list|)
expr_stmt|;
name|BytesRefIterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|BytesRef
name|next
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"entry "
operator|+
name|i
operator|+
literal|" doesn't match"
argument_list|,
name|stringList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|next
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|stringList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

