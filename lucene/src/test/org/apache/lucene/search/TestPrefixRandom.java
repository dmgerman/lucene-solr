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
name|TermsEnum
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
name|RandomIndexWriter
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
name|codecs
operator|.
name|CodecProvider
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

begin_comment
comment|/**  * Create an index with random unicode terms  * Generates random prefix queries, and validates against a simple impl.  */
end_comment

begin_class
DECL|class|TestPrefixRandom
specifier|public
class|class
name|TestPrefixRandom
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|50
argument_list|,
literal|1000
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
name|Field
name|field
init|=
name|newField
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// we generate aweful prefixes: good for testing.
comment|// but for preflex codec, the test can be very slow, so use less iterations.
name|String
name|codec
init|=
name|CodecProvider
operator|.
name|getDefaultCodec
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|codec
operator|.
name|equals
argument_list|(
literal|"PreFlex"
argument_list|)
condition|?
literal|200
operator|*
name|RANDOM_MULTIPLIER
else|:
literal|2000
operator|*
name|RANDOM_MULTIPLIER
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setValue
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
literal|10
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
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
comment|/** a stupid prefix query that just blasts thru the terms */
DECL|class|DumbPrefixQuery
specifier|private
class|class
name|DumbPrefixQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|prefix
specifier|private
specifier|final
name|BytesRef
name|prefix
decl_stmt|;
DECL|method|DumbPrefixQuery
name|DumbPrefixQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|prefix
operator|=
name|term
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimplePrefixTermsEnum
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|prefix
argument_list|)
return|;
block|}
DECL|class|SimplePrefixTermsEnum
specifier|private
class|class
name|SimplePrefixTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|prefix
specifier|private
specifier|final
name|BytesRef
name|prefix
decl_stmt|;
DECL|method|SimplePrefixTermsEnum
specifier|private
name|SimplePrefixTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|setInitialSeekTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|term
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|prefix
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** test a bunch of random prefixes */
DECL|method|testPrefixes
specifier|public
name|void
name|testPrefixes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
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
name|num
condition|;
name|i
operator|++
control|)
name|assertSame
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** check that the # of hits is the same as from a very    * simple prefixquery implementation.    */
DECL|method|assertSame
specifier|private
name|void
name|assertSame
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|PrefixQuery
name|smart
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|prefix
argument_list|)
argument_list|)
decl_stmt|;
name|DumbPrefixQuery
name|dumb
init|=
operator|new
name|DumbPrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|prefix
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|smartDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|smart
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|TopDocs
name|dumbDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|dumb
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|smart
argument_list|,
name|smartDocs
operator|.
name|scoreDocs
argument_list|,
name|dumbDocs
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

