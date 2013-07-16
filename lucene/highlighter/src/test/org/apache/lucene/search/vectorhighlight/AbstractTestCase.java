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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Collection
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
name|TermToBytesRefAttribute
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|AbstractTestCase
specifier|public
specifier|abstract
class|class
name|AbstractTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|F
specifier|protected
specifier|final
name|String
name|F
init|=
literal|"f"
decl_stmt|;
DECL|field|F1
specifier|protected
specifier|final
name|String
name|F1
init|=
literal|"f1"
decl_stmt|;
DECL|field|F2
specifier|protected
specifier|final
name|String
name|F2
init|=
literal|"f2"
decl_stmt|;
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
DECL|field|analyzerW
specifier|protected
name|Analyzer
name|analyzerW
decl_stmt|;
DECL|field|analyzerB
specifier|protected
name|Analyzer
name|analyzerB
decl_stmt|;
DECL|field|analyzerK
specifier|protected
name|Analyzer
name|analyzerK
decl_stmt|;
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|shortMVValues
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|shortMVValues
init|=
block|{
literal|""
block|,
literal|""
block|,
literal|"a b c"
block|,
literal|""
block|,
comment|// empty data in multi valued field
literal|"d e"
block|}
decl_stmt|;
DECL|field|longMVValues
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|longMVValues
init|=
block|{
literal|"Followings are the examples of customizable parameters and actual examples of customization:"
block|,
literal|"The most search engines use only one of these methods. Even the search engines that says they can use the both methods basically"
block|}
decl_stmt|;
comment|// test data for LUCENE-1448 bug
DECL|field|biMVValues
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|biMVValues
init|=
block|{
literal|"\nLucene/Solr does not require such additional hardware."
block|,
literal|"\nWhen you talk about processing speed, the"
block|}
decl_stmt|;
DECL|field|strMVValues
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|strMVValues
init|=
block|{
literal|"abc"
block|,
literal|"defg"
block|,
literal|"hijkl"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|analyzerW
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|analyzerB
operator|=
operator|new
name|BigramAnalyzer
argument_list|()
expr_stmt|;
name|analyzerK
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|tq
argument_list|(
literal|1F
argument_list|,
name|text
argument_list|)
return|;
block|}
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|float
name|boost
parameter_list|,
name|String
name|text
parameter_list|)
block|{
return|return
name|tq
argument_list|(
name|boost
argument_list|,
name|F
argument_list|,
name|text
argument_list|)
return|;
block|}
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
return|return
name|tq
argument_list|(
literal|1F
argument_list|,
name|field
argument_list|,
name|text
argument_list|)
return|;
block|}
DECL|method|tq
specifier|protected
name|Query
name|tq
parameter_list|(
name|float
name|boost
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
DECL|method|pqF
specifier|protected
name|Query
name|pqF
parameter_list|(
name|String
modifier|...
name|texts
parameter_list|)
block|{
return|return
name|pqF
argument_list|(
literal|1F
argument_list|,
name|texts
argument_list|)
return|;
block|}
DECL|method|pqF
specifier|protected
name|Query
name|pqF
parameter_list|(
name|float
name|boost
parameter_list|,
name|String
modifier|...
name|texts
parameter_list|)
block|{
return|return
name|pqF
argument_list|(
name|boost
argument_list|,
literal|0
argument_list|,
name|texts
argument_list|)
return|;
block|}
DECL|method|pqF
specifier|protected
name|Query
name|pqF
parameter_list|(
name|float
name|boost
parameter_list|,
name|int
name|slop
parameter_list|,
name|String
modifier|...
name|texts
parameter_list|)
block|{
return|return
name|pq
argument_list|(
name|boost
argument_list|,
name|slop
argument_list|,
name|F
argument_list|,
name|texts
argument_list|)
return|;
block|}
DECL|method|pq
specifier|protected
name|Query
name|pq
parameter_list|(
name|String
name|field
parameter_list|,
name|String
modifier|...
name|texts
parameter_list|)
block|{
return|return
name|pq
argument_list|(
literal|1F
argument_list|,
literal|0
argument_list|,
name|field
argument_list|,
name|texts
argument_list|)
return|;
block|}
DECL|method|pq
specifier|protected
name|Query
name|pq
parameter_list|(
name|float
name|boost
parameter_list|,
name|String
name|field
parameter_list|,
name|String
modifier|...
name|texts
parameter_list|)
block|{
return|return
name|pq
argument_list|(
name|boost
argument_list|,
literal|0
argument_list|,
name|field
argument_list|,
name|texts
argument_list|)
return|;
block|}
DECL|method|pq
specifier|protected
name|Query
name|pq
parameter_list|(
name|float
name|boost
parameter_list|,
name|int
name|slop
parameter_list|,
name|String
name|field
parameter_list|,
name|String
modifier|...
name|texts
parameter_list|)
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|texts
control|)
block|{
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
DECL|method|dmq
specifier|protected
name|Query
name|dmq
parameter_list|(
name|Query
modifier|...
name|queries
parameter_list|)
block|{
return|return
name|dmq
argument_list|(
literal|0.0F
argument_list|,
name|queries
argument_list|)
return|;
block|}
DECL|method|dmq
specifier|protected
name|Query
name|dmq
parameter_list|(
name|float
name|tieBreakerMultiplier
parameter_list|,
name|Query
modifier|...
name|queries
parameter_list|)
block|{
name|DisjunctionMaxQuery
name|query
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|tieBreakerMultiplier
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|q
range|:
name|queries
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|assertCollectionQueries
specifier|protected
name|void
name|assertCollectionQueries
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|actual
parameter_list|,
name|Query
modifier|...
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|expected
control|)
block|{
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|analyze
specifier|protected
name|List
argument_list|<
name|BytesRef
argument_list|>
name|analyze
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|bytesRefs
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|TermToBytesRefAttribute
name|termAttribute
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytesRef
init|=
name|termAttribute
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|termAttribute
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
name|bytesRefs
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytesRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
name|tokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bytesRefs
return|;
block|}
DECL|method|toPhraseQuery
specifier|protected
name|PhraseQuery
name|toPhraseQuery
parameter_list|(
name|List
argument_list|<
name|BytesRef
argument_list|>
name|bytesRefs
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|bytesRef
range|:
name|bytesRefs
control|)
block|{
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytesRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|phraseQuery
return|;
block|}
DECL|class|BigramAnalyzer
specifier|static
specifier|final
class|class
name|BigramAnalyzer
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
name|BasicNGramTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|BasicNGramTokenizer
specifier|static
specifier|final
class|class
name|BasicNGramTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_N_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_N_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|DEFAULT_DELIMITERS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELIMITERS
init|=
literal|" \t\n.,"
decl_stmt|;
DECL|field|n
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
DECL|field|delimiters
specifier|private
specifier|final
name|String
name|delimiters
decl_stmt|;
DECL|field|startTerm
specifier|private
name|int
name|startTerm
decl_stmt|;
DECL|field|lenTerm
specifier|private
name|int
name|lenTerm
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|nextStartOffset
specifier|private
name|int
name|nextStartOffset
decl_stmt|;
DECL|field|ch
specifier|private
name|int
name|ch
decl_stmt|;
DECL|field|snippet
specifier|private
name|String
name|snippet
decl_stmt|;
DECL|field|snippetBuffer
specifier|private
name|StringBuilder
name|snippetBuffer
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|charBuffer
specifier|private
name|char
index|[]
name|charBuffer
decl_stmt|;
DECL|field|charBufferIndex
specifier|private
name|int
name|charBufferIndex
decl_stmt|;
DECL|field|charBufferLen
specifier|private
name|int
name|charBufferLen
decl_stmt|;
DECL|method|BasicNGramTokenizer
specifier|public
name|BasicNGramTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|DEFAULT_N_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|BasicNGramTokenizer
specifier|public
name|BasicNGramTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|n
argument_list|,
name|DEFAULT_DELIMITERS
argument_list|)
expr_stmt|;
block|}
DECL|method|BasicNGramTokenizer
specifier|public
name|BasicNGramTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|,
name|String
name|delimiters
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|DEFAULT_N_SIZE
argument_list|,
name|delimiters
argument_list|)
expr_stmt|;
block|}
DECL|method|BasicNGramTokenizer
specifier|public
name|BasicNGramTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|,
name|int
name|n
parameter_list|,
name|String
name|delimiters
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|delimiters
operator|=
name|delimiters
expr_stmt|;
name|startTerm
operator|=
literal|0
expr_stmt|;
name|nextStartOffset
operator|=
literal|0
expr_stmt|;
name|snippet
operator|=
literal|null
expr_stmt|;
name|snippetBuffer
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|charBuffer
operator|=
operator|new
name|char
index|[
name|BUFFER_SIZE
index|]
expr_stmt|;
name|charBufferIndex
operator|=
name|BUFFER_SIZE
expr_stmt|;
name|charBufferLen
operator|=
literal|0
expr_stmt|;
name|ch
operator|=
literal|0
expr_stmt|;
block|}
DECL|field|termAtt
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
if|if
condition|(
operator|!
name|getNextPartialSnippet
argument_list|()
condition|)
return|return
literal|false
return|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|snippet
argument_list|,
name|startTerm
argument_list|,
name|startTerm
operator|+
name|lenTerm
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|startOffset
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|startOffset
operator|+
name|lenTerm
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|getFinalOffset
specifier|private
name|int
name|getFinalOffset
parameter_list|()
block|{
return|return
name|nextStartOffset
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|getFinalOffset
argument_list|()
argument_list|,
name|getFinalOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNextPartialSnippet
specifier|protected
name|boolean
name|getNextPartialSnippet
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|snippet
operator|!=
literal|null
operator|&&
name|snippet
operator|.
name|length
argument_list|()
operator|>=
name|startTerm
operator|+
literal|1
operator|+
name|n
condition|)
block|{
name|startTerm
operator|++
expr_stmt|;
name|startOffset
operator|++
expr_stmt|;
name|lenTerm
operator|=
name|n
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
name|getNextSnippet
argument_list|()
return|;
block|}
DECL|method|getNextSnippet
specifier|protected
name|boolean
name|getNextSnippet
parameter_list|()
throws|throws
name|IOException
block|{
name|startTerm
operator|=
literal|0
expr_stmt|;
name|startOffset
operator|=
name|nextStartOffset
expr_stmt|;
name|snippetBuffer
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|snippetBuffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|ch
operator|!=
operator|-
literal|1
condition|)
name|ch
operator|=
name|readCharFromBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
break|break;
elseif|else
if|if
condition|(
operator|!
name|isDelimiter
argument_list|(
name|ch
argument_list|)
condition|)
name|snippetBuffer
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|snippetBuffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
break|break;
else|else
name|startOffset
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|snippetBuffer
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|snippet
operator|=
name|snippetBuffer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|lenTerm
operator|=
name|snippet
operator|.
name|length
argument_list|()
operator|>=
name|n
condition|?
name|n
else|:
name|snippet
operator|.
name|length
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|readCharFromBuffer
specifier|protected
name|int
name|readCharFromBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|charBufferIndex
operator|>=
name|charBufferLen
condition|)
block|{
name|charBufferLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|charBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|charBufferLen
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|charBufferIndex
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|c
init|=
name|charBuffer
index|[
name|charBufferIndex
operator|++
index|]
decl_stmt|;
name|nextStartOffset
operator|++
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|isDelimiter
specifier|protected
name|boolean
name|isDelimiter
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|delimiters
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
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
name|startTerm
operator|=
literal|0
expr_stmt|;
name|nextStartOffset
operator|=
literal|0
expr_stmt|;
name|snippet
operator|=
literal|null
expr_stmt|;
name|snippetBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|charBufferIndex
operator|=
name|BUFFER_SIZE
expr_stmt|;
name|charBufferLen
operator|=
literal|0
expr_stmt|;
name|ch
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|make1d1fIndex
specifier|protected
name|void
name|make1d1fIndex
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|make1dmfIndex
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|make1d1fIndexB
specifier|protected
name|void
name|make1d1fIndexB
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|make1dmfIndexB
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|make1dmfIndex
specifier|protected
name|void
name|make1dmfIndex
parameter_list|(
name|String
modifier|...
name|values
parameter_list|)
throws|throws
name|Exception
block|{
name|make1dmfIndex
argument_list|(
name|analyzerW
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|make1dmfIndexB
specifier|protected
name|void
name|make1dmfIndexB
parameter_list|(
name|String
modifier|...
name|values
parameter_list|)
throws|throws
name|Exception
block|{
name|make1dmfIndex
argument_list|(
name|analyzerB
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|// make 1 doc with multi valued field
DECL|method|make1dmfIndex
specifier|protected
name|void
name|make1dmfIndex
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
throws|throws
name|Exception
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
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
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
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
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
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|F
argument_list|,
name|value
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// make 1 doc with multi valued& not analyzed field
DECL|method|make1dmfIndexNA
specifier|protected
name|void
name|make1dmfIndexNA
parameter_list|(
name|String
modifier|...
name|values
parameter_list|)
throws|throws
name|Exception
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
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzerK
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
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
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
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
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|F
argument_list|,
name|value
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
comment|//doc.add( new Field( F, value, Store.YES, Index.NOT_ANALYZED, TermVector.WITH_POSITIONS_OFFSETS ) );
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndexShortMV
specifier|protected
name|void
name|makeIndexShortMV
parameter_list|()
throws|throws
name|Exception
block|{
comment|//  0
comment|// ""
comment|//  1
comment|// ""
comment|//  234567
comment|// "a b c"
comment|//  0 1 2
comment|//  8
comment|// ""
comment|//   111
comment|//  9012
comment|// "d e"
comment|//  3 4
name|make1dmfIndex
argument_list|(
name|shortMVValues
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndexLongMV
specifier|protected
name|void
name|makeIndexLongMV
parameter_list|()
throws|throws
name|Exception
block|{
comment|//           11111111112222222222333333333344444444445555555555666666666677777777778888888888999
comment|// 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012
comment|// Followings are the examples of customizable parameters and actual examples of customization:
comment|// 0          1   2   3        4  5            6          7   8      9        10 11
comment|//        1                                                                                                   2
comment|// 999999900000000001111111111222222222233333333334444444444555555555566666666667777777777888888888899999999990000000000111111111122
comment|// 345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901
comment|// The most search engines use only one of these methods. Even the search engines that says they can use the both methods basically
comment|// 12  13  (14)   (15)     16  17   18  19 20    21       22   23 (24)   (25)     26   27   28   29  30  31  32   33      34
name|make1dmfIndex
argument_list|(
name|longMVValues
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndexLongMVB
specifier|protected
name|void
name|makeIndexLongMVB
parameter_list|()
throws|throws
name|Exception
block|{
comment|// "*" ... LF
comment|//           1111111111222222222233333333334444444444555555
comment|// 01234567890123456789012345678901234567890123456789012345
comment|// *Lucene/Solr does not require such additional hardware.
comment|//  Lu 0        do 10    re 15   su 21       na 31
comment|//   uc 1        oe 11    eq 16   uc 22       al 32
comment|//    ce 2        es 12    qu 17   ch 23         ha 33
comment|//     en 3          no 13  ui 18     ad 24       ar 34
comment|//      ne 4          ot 14  ir 19     dd 25       rd 35
comment|//       e/ 5                 re 20     di 26       dw 36
comment|//        /S 6                           it 27       wa 37
comment|//         So 7                           ti 28       ar 38
comment|//          ol 8                           io 29       re 39
comment|//           lr 9                           on 30
comment|// 5555666666666677777777778888888888999999999
comment|// 6789012345678901234567890123456789012345678
comment|// *When you talk about processing speed, the
comment|//  Wh 40         ab 48     es 56         th 65
comment|//   he 41         bo 49     ss 57         he 66
comment|//    en 42         ou 50     si 58
comment|//       yo 43       ut 51     in 59
comment|//        ou 44         pr 52   ng 60
comment|//           ta 45       ro 53     sp 61
comment|//            al 46       oc 54     pe 62
comment|//             lk 47       ce 55     ee 63
comment|//                                    ed 64
name|make1dmfIndexB
argument_list|(
name|biMVValues
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndexStrMV
specifier|protected
name|void
name|makeIndexStrMV
parameter_list|()
throws|throws
name|Exception
block|{
comment|//  0123
comment|// "abc"
comment|//  34567
comment|// "defg"
comment|//     111
comment|//  789012
comment|// "hijkl"
name|make1dmfIndexNA
argument_list|(
name|strMVValues
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

