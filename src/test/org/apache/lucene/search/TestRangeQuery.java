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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author goller  */
end_comment

begin_class
DECL|class|TestRangeQuery
specifier|public
class|class
name|TestRangeQuery
extends|extends
name|TestCase
block|{
DECL|field|docCount
specifier|private
name|int
name|docCount
init|=
literal|0
decl_stmt|;
DECL|field|dir
specifier|private
name|RAMDirectory
name|dir
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
DECL|method|testExclusive
specifier|public
name|void
name|testExclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"A,B,C,D, only B in range"
argument_list|,
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
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"A,B,D, only B in range"
argument_list|,
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
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"C added, still only B in range"
argument_list|,
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
block|}
DECL|method|testInclusive
specifier|public
name|void
name|testInclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"A,B,C,D - A,B,C in range"
argument_list|,
literal|3
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
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"A,B,D - A and B in range"
argument_list|,
literal|2
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
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|"C added - A, B, C in range"
argument_list|,
literal|3
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
block|}
DECL|method|testEqualsHashcode
specifier|public
name|void
name|testEqualsHashcode
parameter_list|()
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|Query
name|other
init|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query equals itself is true"
argument_list|,
name|query
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries are equal"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different boost queries are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"notcontent"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"notcontent"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different fields are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"X"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"Z"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different upper terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null lowerterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null upperterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode returns same value"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different upper and lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different inclusive are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeIndex
specifier|private
name|void
name|initializeIndex
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
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
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|insertDoc
specifier|private
name|void
name|insertDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|content
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
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"id"
operator|+
name|docCount
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
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
name|docCount
operator|++
expr_stmt|;
block|}
block|}
end_class

end_unit

