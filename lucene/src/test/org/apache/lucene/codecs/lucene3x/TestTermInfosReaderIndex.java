begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Random
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|FieldInfosReader
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
name|codecs
operator|.
name|lucene3x
operator|.
name|Lucene3xPostingsFormat
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
name|codecs
operator|.
name|lucene3x
operator|.
name|PreFlexRWCodec
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
name|codecs
operator|.
name|lucene3x
operator|.
name|SegmentTermEnum
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
name|codecs
operator|.
name|lucene3x
operator|.
name|TermInfosReaderIndex
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
name|StringField
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
name|FieldInfos
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
name|FieldsEnum
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
name|IndexFileNames
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
name|LogMergePolicy
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
name|MultiFields
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
name|SegmentReader
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
name|Terms
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
name|store
operator|.
name|IOContext
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
name|IndexInput
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|TestTermInfosReaderIndex
specifier|public
class|class
name|TestTermInfosReaderIndex
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUMBER_OF_DOCUMENTS
specifier|private
specifier|static
name|int
name|NUMBER_OF_DOCUMENTS
decl_stmt|;
DECL|field|NUMBER_OF_FIELDS
specifier|private
specifier|static
name|int
name|NUMBER_OF_FIELDS
decl_stmt|;
DECL|field|index
specifier|private
specifier|static
name|TermInfosReaderIndex
name|index
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|termEnum
specifier|private
specifier|static
name|SegmentTermEnum
name|termEnum
decl_stmt|;
DECL|field|indexDivisor
specifier|private
specifier|static
name|int
name|indexDivisor
decl_stmt|;
DECL|field|termIndexInterval
specifier|private
specifier|static
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|sampleTerms
specifier|private
specifier|static
name|List
argument_list|<
name|Term
argument_list|>
name|sampleTerms
decl_stmt|;
comment|/** we will manually instantiate preflex-rw here */
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
operator|=
literal|true
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|termIndexInterval
operator|=
name|config
operator|.
name|getTermIndexInterval
argument_list|()
expr_stmt|;
name|indexDivisor
operator|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NUMBER_OF_DOCUMENTS
operator|=
name|atLeast
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|NUMBER_OF_FIELDS
operator|=
name|atLeast
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|10
argument_list|,
literal|3
operator|*
name|termIndexInterval
operator|*
name|indexDivisor
operator|/
name|NUMBER_OF_DOCUMENTS
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|config
operator|.
name|setCodec
argument_list|(
operator|new
name|PreFlexRWCodec
argument_list|()
argument_list|)
expr_stmt|;
comment|// turn off compound file, this test will open some index files directly.
name|LogMergePolicy
name|mp
init|=
name|newLogMergePolicy
argument_list|()
decl_stmt|;
name|mp
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
expr_stmt|;
name|populate
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r0
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|SegmentReader
name|r
init|=
name|LuceneTestCase
operator|.
name|getOnlySegmentReader
argument_list|(
name|r0
argument_list|)
decl_stmt|;
name|String
name|segment
init|=
name|r
operator|.
name|getSegmentName
argument_list|()
decl_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|FieldInfosReader
name|infosReader
init|=
operator|new
name|PreFlexRWCodec
argument_list|()
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosReader
argument_list|()
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|infosReader
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
name|String
name|segmentFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene3xPostingsFormat
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|long
name|tiiFileLength
init|=
name|directory
operator|.
name|fileLength
argument_list|(
name|segmentFileName
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|segmentFileName
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|termEnum
operator|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene3xPostingsFormat
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|totalIndexInterval
init|=
name|termEnum
operator|.
name|indexInterval
operator|*
name|indexDivisor
decl_stmt|;
name|SegmentTermEnum
name|indexEnum
init|=
operator|new
name|SegmentTermEnum
argument_list|(
name|input
argument_list|,
name|fieldInfos
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|index
operator|=
operator|new
name|TermInfosReaderIndex
argument_list|(
name|indexEnum
argument_list|,
name|indexDivisor
argument_list|,
name|tiiFileLength
argument_list|,
name|totalIndexInterval
argument_list|)
expr_stmt|;
name|indexEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|sampleTerms
operator|=
name|sample
argument_list|(
name|reader
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
name|index
operator|=
literal|null
expr_stmt|;
name|sampleTerms
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSeekEnum
specifier|public
name|void
name|testSeekEnum
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|int
name|indexPosition
init|=
literal|3
decl_stmt|;
name|SegmentTermEnum
name|clone
init|=
operator|(
name|SegmentTermEnum
operator|)
name|termEnum
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Term
name|term
init|=
name|findTermThatWouldBeAtIndex
argument_list|(
name|clone
argument_list|,
name|indexPosition
argument_list|)
decl_stmt|;
name|SegmentTermEnum
name|enumerator
init|=
name|clone
decl_stmt|;
name|index
operator|.
name|seekEnum
argument_list|(
name|enumerator
argument_list|,
name|indexPosition
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCompareTo
specifier|public
name|void
name|testCompareTo
parameter_list|()
throws|throws
name|IOException
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"field"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|NUMBER_OF_FIELDS
argument_list|)
argument_list|,
name|getText
argument_list|()
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
name|index
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|t
init|=
name|index
operator|.
name|getTerm
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|compareTo
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|compareTo
argument_list|,
name|index
operator|.
name|compareTo
argument_list|(
name|term
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomSearchPerformance
specifier|public
name|void
name|testRandomSearchPerformance
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|sampleTerms
control|)
block|{
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
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
name|assertTrue
argument_list|(
name|topDocs
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sample
specifier|private
specifier|static
name|List
argument_list|<
name|Term
argument_list|>
name|sample
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Term
argument_list|>
name|sample
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|FieldsEnum
name|fieldsEnum
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|field
decl_stmt|;
while|while
condition|(
operator|(
name|field
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sample
operator|.
name|size
argument_list|()
operator|>=
name|size
condition|)
block|{
name|int
name|pos
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|sample
operator|.
name|set
argument_list|(
name|pos
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sample
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|sample
argument_list|)
expr_stmt|;
return|return
name|sample
return|;
block|}
DECL|method|findTermThatWouldBeAtIndex
specifier|private
name|Term
name|findTermThatWouldBeAtIndex
parameter_list|(
name|SegmentTermEnum
name|termEnum
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|termPosition
init|=
name|index
operator|*
name|termIndexInterval
operator|*
name|indexDivisor
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
name|termPosition
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO: this test just uses random terms, so this is always possible
name|assumeTrue
argument_list|(
literal|"ran out of terms."
argument_list|,
name|termEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|termEnum
operator|.
name|term
argument_list|()
return|;
block|}
DECL|method|populate
specifier|private
specifier|static
name|void
name|populate
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriterConfig
name|config
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|config
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
name|NUMBER_OF_DOCUMENTS
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|f
init|=
literal|0
init|;
name|f
operator|<
name|NUMBER_OF_FIELDS
condition|;
name|f
operator|++
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
operator|+
name|f
argument_list|,
name|getText
argument_list|()
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getText
specifier|private
specifier|static
name|String
name|getText
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
return|;
block|}
block|}
end_class

end_unit

