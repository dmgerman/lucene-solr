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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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

begin_comment
comment|/**  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|TestOmitPositions
specifier|public
class|class
name|TestOmitPositions
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|"this is a test test"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PostingsEnum
name|de
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
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
decl_stmt|;
while|while
condition|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|de
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|// Tests whether the DocumentWriter correctly enable the
comment|// omitTermFreqAndPositions bit in the FieldInfo
DECL|method|testPositions
specifier|public
name|void
name|testPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// f1,f2,f3: docs only
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|Field
name|f3
init|=
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f3
argument_list|)
expr_stmt|;
name|FieldType
name|ft2
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
comment|// f4,f5,f6 docs and freqs
name|Field
name|f4
init|=
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f4
argument_list|)
expr_stmt|;
name|Field
name|f5
init|=
name|newField
argument_list|(
literal|"f5"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f5
argument_list|)
expr_stmt|;
name|Field
name|f6
init|=
name|newField
argument_list|(
literal|"f6"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f6
argument_list|)
expr_stmt|;
name|FieldType
name|ft3
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft3
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
comment|// f7,f8,f9 docs/freqs/positions
name|Field
name|f7
init|=
name|newField
argument_list|(
literal|"f7"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f7
argument_list|)
expr_stmt|;
name|Field
name|f8
init|=
name|newField
argument_list|(
literal|"f8"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f8
argument_list|)
expr_stmt|;
name|Field
name|f9
init|=
name|newField
argument_list|(
literal|"f9"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f9
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// now we add another document which has docs-only for f1, f4, f7, docs/freqs for f2, f5, f8,
comment|// and docs/freqs/positions for f3, f6, f9
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// f1,f4,f7: docs only
name|f1
operator|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|f4
operator|=
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f4
argument_list|)
expr_stmt|;
name|f7
operator|=
name|newField
argument_list|(
literal|"f7"
argument_list|,
literal|"This field has docs only"
argument_list|,
name|ft
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f7
argument_list|)
expr_stmt|;
comment|// f2, f5, f8: docs and freqs
name|f2
operator|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|f5
operator|=
name|newField
argument_list|(
literal|"f5"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f5
argument_list|)
expr_stmt|;
name|f8
operator|=
name|newField
argument_list|(
literal|"f8"
argument_list|,
literal|"This field has docs and freqs"
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f8
argument_list|)
expr_stmt|;
comment|// f3, f6, f9: docs and freqs and positions
name|f3
operator|=
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f3
argument_list|)
expr_stmt|;
name|f6
operator|=
name|newField
argument_list|(
literal|"f6"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f6
argument_list|)
expr_stmt|;
name|f9
operator|=
name|newField
argument_list|(
literal|"f9"
argument_list|,
literal|"This field has docs and freqs and positions"
argument_list|,
name|ft3
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f9
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|LeafReader
name|reader
init|=
name|getOnlyLeafReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|ram
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
comment|// docs + docs = docs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs + docs/freqs = docs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs + docs/freqs/pos = docs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f3"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs + docs = docs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f4"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs + docs/freqs = docs/freqs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f5"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs + docs/freqs/pos = docs/freqs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f6"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs/pos + docs = docs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f7"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs/pos + docs/freqs = docs/freqs
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f8"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
comment|// docs/freqs/pos + docs/freqs/pos = docs/freqs/pos
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f9"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNoPrx
specifier|private
name|void
name|assertNoPrx
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Throwable
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".prx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".pos"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Verifies no *.prx exists when all fields omit term positions:
DECL|method|testNoPrxFile
specifier|public
name|void
name|testNoPrxFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has term freqs"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNoPrx
argument_list|(
name|ram
argument_list|)
expr_stmt|;
comment|// now add some documents with positions, and check there is no prox after optimization
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|f1
operator|=
name|newTextField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has positions"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNoPrx
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** make sure we downgrade positions and payloads correctly */
DECL|method|testMixing
specifier|public
name|void
name|testMixing
parameter_list|()
throws|throws
name|Exception
block|{
comment|// no positions
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
literal|20
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
if|if
condition|(
name|i
operator|<
literal|19
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|50
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
literal|"i have positions"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|50
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"i have no positions"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|FieldInfos
name|fis
init|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|,
name|fis
operator|.
name|fieldInfo
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fis
operator|.
name|fieldInfo
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// checkindex
block|}
block|}
end_class

end_unit

