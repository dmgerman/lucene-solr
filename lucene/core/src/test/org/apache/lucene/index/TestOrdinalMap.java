begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|codecs
operator|.
name|lucene410
operator|.
name|Lucene410DocValuesFormat
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
name|SortedDocValuesField
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
name|SortedSetDocValuesField
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
name|MultiDocValues
operator|.
name|MultiSortedDocValues
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
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
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
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|LongValues
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
name|RamUsageTester
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

begin_class
DECL|class|TestOrdinalMap
specifier|public
class|class
name|TestOrdinalMap
extends|extends
name|LuceneTestCase
block|{
DECL|field|ORDINAL_MAP_OWNER_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|ORDINAL_MAP_OWNER_FIELD
decl_stmt|;
static|static
block|{
try|try
block|{
name|ORDINAL_MAP_OWNER_FIELD
operator|=
name|OrdinalMap
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"owner"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|()
throw|;
block|}
block|}
DECL|field|ORDINAL_MAP_ACCUMULATOR
specifier|private
specifier|static
specifier|final
name|RamUsageTester
operator|.
name|Accumulator
name|ORDINAL_MAP_ACCUMULATOR
init|=
operator|new
name|RamUsageTester
operator|.
name|Accumulator
argument_list|()
block|{
specifier|public
name|long
name|accumulateObject
parameter_list|(
name|Object
name|o
parameter_list|,
name|long
name|shallowSize
parameter_list|,
name|java
operator|.
name|util
operator|.
name|Map
argument_list|<
name|Field
argument_list|,
name|Object
argument_list|>
name|fieldValues
parameter_list|,
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
argument_list|>
name|queue
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|LongValues
operator|.
name|IDENTITY
condition|)
block|{
return|return
literal|0L
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|OrdinalMap
condition|)
block|{
name|fieldValues
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|fieldValues
argument_list|)
expr_stmt|;
name|fieldValues
operator|.
name|remove
argument_list|(
name|ORDINAL_MAP_OWNER_FIELD
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|accumulateObject
argument_list|(
name|o
argument_list|,
name|shallowSize
argument_list|,
name|fieldValues
argument_list|,
name|queue
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testRamBytesUsed
specifier|public
name|void
name|testRamBytesUsed
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
name|IndexWriterConfig
name|cfg
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
operator|new
name|Lucene410DocValuesFormat
argument_list|()
argument_list|)
argument_list|)
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
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxTermLength
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|4
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"sdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxTermLength
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numSortedSet
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
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
name|numSortedSet
condition|;
operator|++
name|j
control|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"ssdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxTermLength
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|iw
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|ar
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|SortedDocValues
name|sdv
init|=
name|ar
operator|.
name|getSortedDocValues
argument_list|(
literal|"sdv"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdv
operator|instanceof
name|MultiSortedDocValues
condition|)
block|{
name|OrdinalMap
name|map
init|=
operator|(
operator|(
name|MultiSortedDocValues
operator|)
name|sdv
operator|)
operator|.
name|mapping
decl_stmt|;
name|assertEquals
argument_list|(
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|map
argument_list|,
name|ORDINAL_MAP_ACCUMULATOR
argument_list|)
argument_list|,
name|map
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SortedSetDocValues
name|ssdv
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"ssdv"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ssdv
operator|instanceof
name|MultiSortedSetDocValues
condition|)
block|{
name|OrdinalMap
name|map
init|=
operator|(
operator|(
name|MultiSortedSetDocValues
operator|)
name|ssdv
operator|)
operator|.
name|mapping
decl_stmt|;
name|assertEquals
argument_list|(
name|RamUsageTester
operator|.
name|sizeOf
argument_list|(
name|map
argument_list|,
name|ORDINAL_MAP_ACCUMULATOR
argument_list|)
argument_list|,
name|map
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
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

