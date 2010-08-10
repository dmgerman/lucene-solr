begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|analysis
operator|.
name|MockTokenizer
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
name|core
operator|.
name|WhitespaceAnalyzer
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
name|MockRAMDirectory
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

begin_comment
comment|/**  * Test case for LuceneDictionary.  * It first creates a simple index and then a couple of instances of LuceneDictionary  * on different fields and checks if all the right text comes back.  */
end_comment

begin_class
DECL|class|TestLuceneDictionary
specifier|public
class|class
name|TestLuceneDictionary
extends|extends
name|LuceneTestCase
block|{
DECL|field|store
specifier|private
name|Directory
name|store
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
DECL|field|indexReader
specifier|private
name|IndexReader
name|indexReader
init|=
literal|null
decl_stmt|;
DECL|field|ld
specifier|private
name|LuceneDictionary
name|ld
decl_stmt|;
DECL|field|it
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|store
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
decl_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"aaa"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"aaa"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"Tom"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"Jerry"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"zzz"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFieldNonExistent
specifier|public
name|void
name|testFieldNonExistent
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"nonexistent_field"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"More elements than expected"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFieldAaa
specifier|public
name|void
name|testFieldAaa
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element doesn't exist."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"More elements than expected"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFieldContents_1
specifier|public
name|void
name|testFieldContents_1
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"contents"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element doesn't exist."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Jerry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Second element doesn't exist."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Second element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Tom"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"More elements than expected"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"contents"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
name|int
name|counter
init|=
literal|2
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|counter
operator|--
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Number of words incorrect"
argument_list|,
name|counter
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFieldContents_2
specifier|public
name|void
name|testFieldContents_2
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"contents"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
comment|// hasNext() should have no side effects
name|assertTrue
argument_list|(
literal|"First element isn't were it should be."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element isn't were it should be."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element isn't were it should be."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// just iterate through words
name|assertTrue
argument_list|(
literal|"First element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Jerry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Second element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Tom"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// hasNext() should still have no side effects ...
name|assertFalse
argument_list|(
literal|"There should be any more elements"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"There should be any more elements"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"There should be any more elements"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// .. and there are really no more words
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFieldZzz
specifier|public
name|void
name|testFieldZzz
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ld
operator|=
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"zzz"
argument_list|)
expr_stmt|;
name|it
operator|=
name|ld
operator|.
name|getWordsIterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element doesn't exist."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"First element isn't correct"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"More elements than expected"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Nonexistent element is really null"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSpellchecker
specifier|public
name|void
name|testSpellchecker
parameter_list|()
throws|throws
name|IOException
block|{
name|SpellChecker
name|sc
init|=
operator|new
name|SpellChecker
argument_list|(
operator|new
name|MockRAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sc
operator|.
name|indexDictionary
argument_list|(
operator|new
name|LuceneDictionary
argument_list|(
name|indexReader
argument_list|,
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|suggestions
init|=
name|sc
operator|.
name|suggestSimilar
argument_list|(
literal|"Tam"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suggestions
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Tom"
argument_list|,
name|suggestions
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|sc
operator|.
name|suggestSimilar
argument_list|(
literal|"Jarry"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suggestions
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jerry"
argument_list|,
name|suggestions
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

