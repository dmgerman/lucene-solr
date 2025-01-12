begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

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
name|search
operator|.
name|DocIdSetIterator
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
name|TestUtil
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
name|Version
import|;
end_import

begin_class
DECL|class|TestSegmentTermDocs
specifier|public
class|class
name|TestSegmentTermDocs
extends|extends
name|LuceneTestCase
block|{
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|info
specifier|private
name|SegmentCommitInfo
name|info
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|info
operator|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|testDoc
argument_list|)
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermDocs
specifier|public
name|void
name|testTermDocs
parameter_list|()
throws|throws
name|IOException
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|major
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|TermsEnum
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|terms
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
name|PostingsEnum
name|termDocs
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
decl_stmt|;
if|if
condition|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|docId
init|=
name|termDocs
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docId
operator|==
literal|0
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|termDocs
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBadSeek
specifier|public
name|void
name|testBadSeek
parameter_list|()
throws|throws
name|IOException
block|{
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|major
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|PostingsEnum
name|termDocs
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
literal|"textField2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bad"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|termDocs
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|{
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|major
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|PostingsEnum
name|termDocs
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
literal|"junk"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bad"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|termDocs
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSkipTo
specifier|public
name|void
name|testSkipTo
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Term
name|ta
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
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
literal|10
condition|;
name|i
operator|++
control|)
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa aaa aaa aaa"
argument_list|)
expr_stmt|;
name|Term
name|tb
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
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
literal|16
condition|;
name|i
operator|++
control|)
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"bbb bbb bbb bbb"
argument_list|)
expr_stmt|;
name|Term
name|tc
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
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
literal|50
condition|;
name|i
operator|++
control|)
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"ccc ccc ccc ccc"
argument_list|)
expr_stmt|;
comment|// assure that we deal with a single segment
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|PostingsEnum
name|tdocs
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|ta
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|ta
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
decl_stmt|;
comment|// without optimization (assumption skipInterval == 16)
comment|// with next
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|2
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|4
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|9
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|10
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// without next
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|ta
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|ta
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|0
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|4
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|9
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|10
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// exactly skipInterval documents and therefore with optimization
comment|// with next
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|tb
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|tb
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|12
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|15
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|24
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|25
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|26
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// without next
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|tb
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|tb
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|5
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|15
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|24
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|25
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|26
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// much more than skipInterval documents and therefore with optimization
comment|// with next
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|tc
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|tc
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|28
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|40
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|57
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|74
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|75
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|76
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|//without next
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|tc
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|tc
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|5
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|40
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|57
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|74
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|75
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|tdocs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdocs
operator|.
name|advance
argument_list|(
literal|76
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|reader
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|value
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
name|newTextField
argument_list|(
literal|"content"
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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

