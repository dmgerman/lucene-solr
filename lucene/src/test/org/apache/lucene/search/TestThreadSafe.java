begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|_TestUtil
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
name|RAMDirectory
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|analysis
operator|.
name|MockAnalyzer
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestThreadSafe
specifier|public
class|class
name|TestThreadSafe
extends|extends
name|LuceneTestCase
block|{
DECL|field|r
name|Random
name|r
decl_stmt|;
DECL|field|dir1
name|Directory
name|dir1
decl_stmt|;
DECL|field|dir2
name|Directory
name|dir2
decl_stmt|;
DECL|field|ir1
name|IndexReader
name|ir1
decl_stmt|;
DECL|field|ir2
name|IndexReader
name|ir2
decl_stmt|;
DECL|field|failure
name|String
name|failure
init|=
literal|null
decl_stmt|;
DECL|class|Thr
class|class
name|Thr
extends|extends
name|Thread
block|{
DECL|field|iter
specifier|final
name|int
name|iter
decl_stmt|;
DECL|field|rand
specifier|final
name|Random
name|rand
decl_stmt|;
comment|// pass in random in case we want to make things reproducable
DECL|method|Thr
specifier|public
name|Thr
parameter_list|(
name|int
name|iter
parameter_list|,
name|Random
name|rand
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|iter
expr_stmt|;
name|this
operator|.
name|rand
operator|=
name|rand
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
comment|/*** future            // pick a random index reader... a shared one, or create your own            IndexReader ir;            ***/
switch|switch
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|loadDoc
argument_list|(
name|ir1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|failure
operator|=
name|th
operator|.
name|toString
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadDoc
name|void
name|loadDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// beware of deleted docs in the future
name|Document
name|doc
init|=
name|ir
operator|.
name|document
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FieldSelector
argument_list|()
block|{
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
switch|switch
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|FieldSelectorResult
operator|.
name|LAZY_LOAD
return|;
case|case
literal|1
case|:
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
comment|// TODO: add other options
default|default:
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Fieldable
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Fieldable
name|f
range|:
name|fields
control|)
block|{
name|validateField
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|validateField
name|void
name|validateField
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
name|String
name|val
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|val
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
operator|||
operator|!
name|val
operator|.
name|endsWith
argument_list|(
literal|"$"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid field:"
operator|+
name|f
operator|.
name|toString
argument_list|()
operator|+
literal|" val="
operator|+
name|val
argument_list|)
throw|;
block|}
block|}
DECL|field|words
name|String
index|[]
name|words
init|=
literal|"now is the time for all good men to come to the aid of their country"
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
DECL|method|buildDir
name|void
name|buildDir
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|int
name|maxFields
parameter_list|,
name|int
name|maxFieldLen
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
argument_list|)
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
name|nDocs
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|nFields
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxFields
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
name|nFields
condition|;
name|i
operator|++
control|)
block|{
name|int
name|flen
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxFieldLen
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"^ "
argument_list|)
decl_stmt|;
while|while
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|<
name|flen
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|words
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|words
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" $"
argument_list|)
expr_stmt|;
name|Field
operator|.
name|Store
name|store
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
comment|// make random later
name|Field
operator|.
name|Index
name|index
init|=
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
decl_stmt|;
comment|// make random later
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
operator|+
name|i
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|store
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doTest
name|void
name|doTest
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|nThreads
parameter_list|)
throws|throws
name|Exception
block|{
name|Thr
index|[]
name|tarr
init|=
operator|new
name|Thr
index|[
name|nThreads
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
name|nThreads
condition|;
name|i
operator|++
control|)
block|{
name|tarr
index|[
name|i
index|]
operator|=
operator|new
name|Thr
argument_list|(
name|iter
argument_list|,
operator|new
name|Random
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tarr
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
name|nThreads
condition|;
name|i
operator|++
control|)
block|{
name|tarr
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLazyLoadThreadSafety
specifier|public
name|void
name|testLazyLoadThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|r
operator|=
name|newRandom
argument_list|()
expr_stmt|;
name|dir1
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
comment|// test w/ field sizes bigger than the buffer of an index input
name|buildDir
argument_list|(
name|dir1
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
comment|// do many small tests so the thread locals go away inbetween
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ir1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

