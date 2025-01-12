begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|index
operator|.
name|IndexOptions
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
name|IndexableField
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
comment|/**   * Tests special cases of BlockPostingsFormat   */
end_comment

begin_class
DECL|class|TestBlockPostingsFormat2
specifier|public
class|class
name|TestBlockPostingsFormat2
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|iw
name|RandomIndexWriter
name|iw
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
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"testDFBlockSize"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene50PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setDoRandomForceMerge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we will ourselves
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// for some extra coverage, checkIndex before we forceMerge
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene50PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// just force a checkindex for now
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|newDocument
specifier|private
name|Document
name|newDocument
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexOptions
name|option
range|:
name|IndexOptions
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|option
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
continue|continue;
block|}
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
comment|// turn on tvs for a cross-check, since we rely upon checkindex in this test (for now)
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPayloads
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|option
operator|.
name|toString
argument_list|()
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
comment|/** tests terms with df = blocksize */
DECL|method|testDFBlockSize
specifier|public
name|void
name|testDFBlockSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|newDocument
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
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
operator|(
operator|(
name|Field
operator|)
name|f
operator|)
operator|.
name|setStringValue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2"
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests terms with df % blocksize = 0 */
DECL|method|testDFBlockSizeMultiple
specifier|public
name|void
name|testDFBlockSizeMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|newDocument
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
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
operator|*
literal|16
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
operator|(
operator|(
name|Field
operator|)
name|f
operator|)
operator|.
name|setStringValue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2"
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests terms with ttf = blocksize */
DECL|method|testTTFBlockSize
specifier|public
name|void
name|testTTFBlockSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|newDocument
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
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
operator|(
operator|(
name|Field
operator|)
name|f
operator|)
operator|.
name|setStringValue
argument_list|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2 "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2"
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** tests terms with ttf % blocksize = 0 */
DECL|method|testTTFBlockSizeMultiple
specifier|public
name|void
name|testTTFBlockSizeMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|newDocument
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
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|String
name|proto
init|=
operator|(
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2 "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2 "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2 "
operator|+
name|f
operator|.
name|name
argument_list|()
operator|+
literal|"_2"
operator|)
decl_stmt|;
name|StringBuilder
name|val
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|16
condition|;
name|j
operator|++
control|)
block|{
name|val
operator|.
name|append
argument_list|(
name|proto
argument_list|)
expr_stmt|;
name|val
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|Field
operator|)
name|f
operator|)
operator|.
name|setStringValue
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

