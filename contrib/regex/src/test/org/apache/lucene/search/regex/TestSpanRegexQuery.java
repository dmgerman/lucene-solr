begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
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
name|SimpleAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|CorruptIndexException
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
name|search
operator|.
name|IndexSearcher
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
name|MultiSearcher
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
name|spans
operator|.
name|SpanFirstQuery
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
name|LockObtainFailedException
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

begin_class
DECL|class|TestSpanRegexQuery
specifier|public
class|class
name|TestSpanRegexQuery
extends|extends
name|TestCase
block|{
DECL|field|indexStoreA
name|Directory
name|indexStoreA
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|indexStoreB
name|Directory
name|indexStoreB
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|method|testSpanRegex
specifier|public
name|void
name|testSpanRegex
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
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// doc.add(new Field("field", "the quick brown fox jumps over the lazy dog",
comment|// Field.Store.NO, Field.Index.ANALYZED));
comment|// writer.addDocument(doc);
comment|// doc = new Document();
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"auto update"
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
literal|"field"
argument_list|,
literal|"first auto update"
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanRegexQuery
name|srq
init|=
operator|new
name|SpanRegexQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aut.*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanFirstQuery
name|sfq
init|=
operator|new
name|SpanFirstQuery
argument_list|(
name|srq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// SpanNearQuery query = new SpanNearQuery(new SpanQuery[] {srq, stq}, 6,
comment|// true);
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|sfq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanRegexBug
specifier|public
name|void
name|testSpanRegexBug
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|createRAMDirectories
argument_list|()
expr_stmt|;
name|SpanRegexQuery
name|srq
init|=
operator|new
name|SpanRegexQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a.*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanRegexQuery
name|stq
init|=
operator|new
name|SpanRegexQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b.*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|query
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|srq
block|,
name|stq
block|}
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// 1. Search the same store which works
name|IndexSearcher
index|[]
name|arrSearcher
init|=
operator|new
name|IndexSearcher
index|[
literal|2
index|]
decl_stmt|;
name|arrSearcher
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|arrSearcher
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MultiSearcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|arrSearcher
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|arrSearcher
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|arrSearcher
index|[
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Will fail here
comment|// We expect 2 but only one matched
comment|// The rewriter function only write it once on the first IndexSearcher
comment|// So it's using term: a1 b1 to search on the second IndexSearcher
comment|// As a result, it won't match the document in the second IndexSearcher
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|indexStoreA
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStoreB
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createRAMDirectories
specifier|private
name|void
name|createRAMDirectories
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
comment|// creating a document to store
name|Document
name|lDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"a1 b1"
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
name|ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating a document to store
name|Document
name|lDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"a2 b2"
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
name|ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating first index writer
name|IndexWriter
name|writerA
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreA
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerA
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// creating second index writer
name|IndexWriter
name|writerB
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writerB
operator|.
name|addDocument
argument_list|(
name|lDoc2
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

