begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|HashSet
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
name|Set
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
name|store
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
name|document
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestStressAdvance
specifier|public
class|class
name|TestStressAdvance
extends|extends
name|LuceneTestCase
block|{
DECL|method|testStressAdvance
specifier|public
name|void
name|testStressAdvance
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|3
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|aDocs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|f
init|=
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|4097
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: numDocs="
operator|+
name|num
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|num
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
literal|4
argument_list|)
operator|==
literal|3
condition|)
block|{
name|f
operator|.
name|setStringValue
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|aDocs
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|.
name|setStringValue
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
name|idField
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|id
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: doc upto "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|aDocIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|bDocIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|idToDocID
init|=
operator|new
name|int
index|[
name|r
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|idToDocID
operator|.
name|length
condition|;
name|docID
operator|++
control|)
block|{
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|r
operator|.
name|document
argument_list|(
name|docID
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|aDocs
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|aDocIDs
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bDocIDs
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|TermsEnum
name|te
init|=
name|getOnlyLeafReader
argument_list|(
name|r
argument_list|)
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|PostingsEnum
name|de
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|10
condition|;
name|iter2
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" iter2="
operator|+
name|iter2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|te
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|de
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
name|de
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|testOne
argument_list|(
name|de
argument_list|,
name|aDocIDs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|te
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|de
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
name|de
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|testOne
argument_list|(
name|de
argument_list|,
name|bDocIDs
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
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
block|}
DECL|method|testOne
specifier|private
name|void
name|testOne
parameter_list|(
name|PostingsEnum
name|docs
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|int
name|upto
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|expected
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  cycle upto="
operator|+
name|upto
operator|+
literal|" of "
operator|+
name|expected
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|docID
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|1
operator|||
name|upto
operator|==
name|expected
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// test nextDoc()
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    do nextDoc"
argument_list|)
expr_stmt|;
block|}
name|upto
operator|++
expr_stmt|;
name|docID
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// test advance()
specifier|final
name|int
name|inc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|expected
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|-
name|upto
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    do advance inc="
operator|+
name|inc
argument_list|)
expr_stmt|;
block|}
name|upto
operator|+=
name|inc
expr_stmt|;
name|docID
operator|=
name|docs
operator|.
name|advance
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upto
operator|==
name|expected
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expect docID="
operator|+
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|+
literal|" actual="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expect docID="
operator|+
name|expected
operator|.
name|get
argument_list|(
name|upto
argument_list|)
operator|+
literal|" actual="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docID
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|upto
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

