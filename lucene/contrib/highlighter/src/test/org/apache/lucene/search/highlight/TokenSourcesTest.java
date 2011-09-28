begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|document
operator|.
name|FieldType
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
name|TextField
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
name|index
operator|.
name|TermPositionVector
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
name|DisjunctionMaxQuery
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
name|Query
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
name|TopDocs
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|// LUCENE-2874
end_comment

begin_class
DECL|class|TokenSourcesTest
specifier|public
class|class
name|TokenSourcesTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"text"
decl_stmt|;
DECL|class|OverlapAnalyzer
specifier|private
specifier|static
specifier|final
class|class
name|OverlapAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|TokenStreamOverlap
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|TokenStreamOverlap
specifier|private
specifier|static
specifier|final
class|class
name|TokenStreamOverlap
extends|extends
name|Tokenizer
block|{
DECL|field|tokens
specifier|private
name|Token
index|[]
name|tokens
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|positionIncrementAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|positionIncrementAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TokenStreamOverlap
specifier|public
name|TokenStreamOverlap
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|i
operator|>=
name|this
operator|.
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|startOffset
argument_list|()
argument_list|,
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|this
operator|.
name|tokens
index|[
name|i
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|i
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
operator|new
name|Token
index|[]
block|{
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'t'
block|,
literal|'h'
block|,
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'{'
block|,
literal|'f'
block|,
literal|'o'
block|,
literal|'x'
block|,
literal|'}'
block|}
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|7
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'f'
block|,
literal|'o'
block|,
literal|'x'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'d'
block|,
literal|'i'
block|,
literal|'d'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|8
argument_list|,
literal|11
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'n'
block|,
literal|'o'
block|,
literal|'t'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|12
argument_list|,
literal|15
argument_list|)
block|,
operator|new
name|Token
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'j'
block|,
literal|'u'
block|,
literal|'m'
block|,
literal|'p'
block|}
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
block|}
expr_stmt|;
name|this
operator|.
name|tokens
index|[
literal|1
index|]
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOverlapWithOffset
specifier|public
name|void
name|testOverlapWithOffset
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|OverlapAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamOverlap
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|DisjunctionMaxQuery
name|query
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"{fox}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// final Query phraseQuery = new SpanNearQuery(new SpanQuery[] {
comment|// new SpanTermQuery(new Term(FIELD, "{fox}")),
comment|// new SpanTermQuery(new Term(FIELD, "fox")) }, 0, true);
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|indexReader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<B>the fox</B> did not jump"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testOverlapWithPositionsAndOffset
specifier|public
name|void
name|testOverlapWithPositionsAndOffset
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|OverlapAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamOverlap
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|DisjunctionMaxQuery
name|query
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"{fox}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// final Query phraseQuery = new SpanNearQuery(new SpanQuery[] {
comment|// new SpanTermQuery(new Term(FIELD, "{fox}")),
comment|// new SpanTermQuery(new Term(FIELD, "fox")) }, 0, true);
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|indexReader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<B>the fox</B> did not jump"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testOverlapWithOffsetExactPhrase
specifier|public
name|void
name|testOverlapWithOffsetExactPhrase
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|OverlapAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamOverlap
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
try|try
block|{
comment|// final DisjunctionMaxQuery query = new DisjunctionMaxQuery(1);
comment|// query.add(new SpanTermQuery(new Term(FIELD, "{fox}")));
comment|// query.add(new SpanTermQuery(new Term(FIELD, "fox")));
specifier|final
name|Query
name|phraseQuery
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"the"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|indexReader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<B>the fox</B> did not jump"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexReader
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
block|}
DECL|method|testOverlapWithPositionsAndOffsetExactPhrase
specifier|public
name|void
name|testOverlapWithPositionsAndOffsetExactPhrase
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
specifier|final
name|String
name|TEXT
init|=
literal|"the fox did not jump"
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|OverlapAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
operator|new
name|TokenStreamOverlap
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
try|try
block|{
comment|// final DisjunctionMaxQuery query = new DisjunctionMaxQuery(1);
comment|// query.add(new SpanTermQuery(new Term(FIELD, "the")));
comment|// query.add(new SpanTermQuery(new Term(FIELD, "fox")));
specifier|final
name|Query
name|phraseQuery
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"the"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"fox"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|phraseQuery
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|tokenStream
init|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|indexReader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|FIELD
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<B>the fox</B> did not jump"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|tokenStream
argument_list|,
name|TEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexReader
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
block|}
block|}
end_class

end_unit

