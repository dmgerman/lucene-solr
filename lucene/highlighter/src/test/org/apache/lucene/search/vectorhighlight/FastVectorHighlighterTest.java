begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|MockTokenFilter
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
name|DirectoryReader
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
name|queries
operator|.
name|CommonTermsQuery
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|PhraseQuery
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
name|TermQuery
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|FastVectorHighlighterTest
specifier|public
class|class
name|FastVectorHighlighterTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleHighlightTest
specifier|public
name|void
name|testSimpleHighlightTest
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|type
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"This is a test where foo is highlighed and should be highlighted"
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|docId
init|=
literal|0
decl_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
literal|"field"
argument_list|,
literal|54
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|"This is a test where<b>foo</b> is highlighed and should be highlighted"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
literal|"field"
argument_list|,
literal|52
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"This is a test where<b>foo</b> is highlighed and should be"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
literal|"field"
argument_list|,
literal|30
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a test where<b>foo</b> is highlighed"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
DECL|method|testPhraseHighlightLongTextTest
specifier|public
name|void
name|testPhraseHighlightLongTextTest
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|type
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|Field
name|text
init|=
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
literal|"Netscape was the general name for a series of web browsers originally produced by Netscape Communications Corporation, now a subsidiary of AOL The original browser was once the dominant browser in terms of usage share, but as a result of the first browser war it lost virtually all of its share to Internet Explorer Netscape was discontinued and support for all Netscape browsers and client products was terminated on March 1, 2008 Netscape Navigator was the name of Netscape\u0027s web browser from versions 1.0 through 4.8 The first beta release versions of the browser were released in 1994 and known as Mosaic and then Mosaic Netscape until a legal challenge from the National Center for Supercomputing Applications (makers of NCSA Mosaic, which many of Netscape\u0027s founders used to develop), led to the name change to Netscape Navigator The company\u0027s name also changed from Mosaic Communications Corporation to Netscape Communications Corporation The browser was easily the most advanced..."
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|docId
init|=
literal|0
decl_stmt|;
name|String
name|field
init|=
literal|"text"
decl_stmt|;
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"internet"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"explorer"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|128
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first browser war it lost virtually all of its share to<b>Internet</b><b>Explorer</b> Netscape was discontinued and support for all Netscape browsers"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"internet"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"explorer"
argument_list|)
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|128
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first browser war it lost virtually all of its share to<b>Internet Explorer</b> Netscape was discontinued and support for all Netscape browsers"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
comment|// see LUCENE-4899
DECL|method|testPhraseHighlightTest
specifier|public
name|void
name|testPhraseHighlightTest
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|type
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|Field
name|longTermField
init|=
operator|new
name|Field
argument_list|(
literal|"long_term"
argument_list|,
literal|"This is a test thisisaverylongwordandmakessurethisfails where foo is highlighed and should be highlighted"
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|Field
name|noLongTermField
init|=
operator|new
name|Field
argument_list|(
literal|"no_long_term"
argument_list|,
literal|"This is a test where foo is highlighed and should be highlighted"
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|longTermField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|noLongTermField
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|docId
init|=
literal|0
decl_stmt|;
name|String
name|field
init|=
literal|"no_long_term"
decl_stmt|;
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>foo</b> is<b>highlighed</b> and"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|setSlop
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pq
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|30
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a<b>test</b> where<b>foo</b> is<b>highlighed</b> and"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|30
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a<b>test</b> where<b>foo</b> is<b>highlighed</b> and"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighted"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|setSlop
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|BooleanQuery
name|inner
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|pq
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|inner
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|inner
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pq
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|30
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a<b>test</b> where<b>foo</b> is<b>highlighed</b> and"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|field
operator|=
literal|"long_term"
expr_stmt|;
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"thisisaverylongwordandmakessurethisfails"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"highlighed"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// highlighted results are centered
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bestFragments
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>thisisaverylongwordandmakessurethisfails</b>"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
DECL|method|testCommonTermsQueryHighlightTest
specifier|public
name|void
name|testCommonTermsQueryHighlightTest
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|type
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|String
index|[]
name|texts
init|=
block|{
literal|"Hello this is a piece of text that is very long and contains too much preamble and the meat is really here which says kennedy has been shot"
block|,
literal|"This piece of text refers to Kennedy at the beginning then has a longer piece of text that is very long in the middle and finally ends with another reference to Kennedy"
block|,
literal|"JFK has been shot"
block|,
literal|"John Kennedy has been shot"
block|,
literal|"This text has a typo in referring to Keneddy"
block|,
literal|"wordx wordy wordz wordx wordy wordx worda wordb wordy wordc"
block|,
literal|"y z x y z a b"
block|,
literal|"lets is a the lets is a the lets is a the lets"
block|}
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
name|texts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
name|texts
index|[
name|i
index|]
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
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
name|CommonTermsQuery
name|query
init|=
operator|new
name|CommonTermsQuery
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"long"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"very"
argument_list|)
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|FieldQuery
name|fieldQuery
init|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|String
index|[]
name|bestFragments
init|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"field"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"This piece of<b>text</b> refers to Kennedy at the beginning then has a longer piece of<b>text</b> that is<b>very</b><b>long</b> in the middle and finally ends with another reference to Kennedy"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fieldQuery
operator|=
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|bestFragments
operator|=
name|highlighter
operator|.
name|getBestFragments
argument_list|(
name|fieldQuery
argument_list|,
name|reader
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|,
literal|"field"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello this is a piece of<b>text</b> that is<b>very</b><b>long</b> and contains too much preamble and the meat is really here which says kennedy has been shot"
argument_list|,
name|bestFragments
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
end_class

end_unit

