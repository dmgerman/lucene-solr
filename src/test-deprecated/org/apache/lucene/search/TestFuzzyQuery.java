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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Term
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

begin_comment
comment|/**  * Tests {@link FuzzyQuery}.  *  * @author Daniel Naber  */
end_comment

begin_class
DECL|class|TestFuzzyQuery
specifier|public
class|class
name|TestFuzzyQuery
extends|extends
name|TestCase
block|{
DECL|method|testDefaultFuzziness
specifier|public
name|void
name|testDefaultFuzziness
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
literal|"aaaaa"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"aaaab"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"aaabb"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"aabbb"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"abbbb"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"bbbbb"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"ddddd"
argument_list|,
name|writer
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|FuzzyQuery
name|query
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaaaa"
argument_list|)
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// not similar enough:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaccc"
argument_list|)
argument_list|)
expr_stmt|;
comment|// edit distance to "aaaaa" = 3
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// query identical to a word in the index:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaaaa"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaaaa"
operator|)
argument_list|)
expr_stmt|;
comment|// default allows for up to two edits:
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaaab"
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaabb"
operator|)
argument_list|)
expr_stmt|;
comment|// query similar to a word in the index:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaaac"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaaaa"
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaaab"
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaabb"
operator|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"ddddX"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"ddddd"
operator|)
argument_list|)
expr_stmt|;
comment|// different field = no match:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"anotherfield"
argument_list|,
literal|"ddddX"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaultFuzzinessLong
specifier|public
name|void
name|testDefaultFuzzinessLong
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
literal|"aaaaaaa"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"segment"
argument_list|,
name|writer
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|FuzzyQuery
name|query
decl_stmt|;
comment|// not similar enough:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// edit distance to "aaaaaaa" = 3, this matches because the string is longer than
comment|// in testDefaultFuzziness so a bigger difference is allowed:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaaaccc"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|(
literal|"aaaaaaa"
operator|)
argument_list|)
expr_stmt|;
comment|// no match, more than half of the characters is wrong:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaacccc"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// "student" and "stellent" are indeed similar to "segment" by default:
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"student"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"stellent"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|text
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
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
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
name|text
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
block|}
block|}
end_class

end_unit

