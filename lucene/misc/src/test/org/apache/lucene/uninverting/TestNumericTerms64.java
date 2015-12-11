begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.uninverting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|uninverting
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|LegacyLongField
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
name|LegacyNumericRangeQuery
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
name|ScoreDoc
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
name|Sort
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
name|SortField
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestNumericTerms64
specifier|public
class|class
name|TestNumericTerms64
extends|extends
name|LuceneTestCase
block|{
comment|// distance of entries
DECL|field|distance
specifier|private
specifier|static
name|long
name|distance
decl_stmt|;
comment|// shift the starting of the values to the left, to also have negative values:
DECL|field|startOffset
specifier|private
specifier|static
specifier|final
name|long
name|startOffset
init|=
operator|-
literal|1L
operator|<<
literal|31
decl_stmt|;
comment|// number of docs to generate for testing
DECL|field|noDocs
specifier|private
specifier|static
name|int
name|noDocs
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
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
name|noDocs
operator|=
name|atLeast
argument_list|(
literal|4096
argument_list|)
expr_stmt|;
name|distance
operator|=
operator|(
literal|1L
operator|<<
literal|60
operator|)
operator|/
name|noDocs
expr_stmt|;
name|directory
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
argument_list|()
argument_list|,
name|directory
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
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|1000
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
specifier|final
name|FieldType
name|storedLong
init|=
operator|new
name|FieldType
argument_list|(
name|LegacyLongField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|storedLong
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|storedLong
operator|.
name|freeze
argument_list|()
expr_stmt|;
specifier|final
name|FieldType
name|storedLong8
init|=
operator|new
name|FieldType
argument_list|(
name|storedLong
argument_list|)
decl_stmt|;
name|storedLong8
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|8
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|storedLong4
init|=
operator|new
name|FieldType
argument_list|(
name|storedLong
argument_list|)
decl_stmt|;
name|storedLong4
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|4
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|storedLong6
init|=
operator|new
name|FieldType
argument_list|(
name|storedLong
argument_list|)
decl_stmt|;
name|storedLong6
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|6
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|storedLong2
init|=
operator|new
name|FieldType
argument_list|(
name|storedLong
argument_list|)
decl_stmt|;
name|storedLong2
operator|.
name|setNumericPrecisionStep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|LegacyLongField
name|field8
init|=
operator|new
name|LegacyLongField
argument_list|(
literal|"field8"
argument_list|,
literal|0L
argument_list|,
name|storedLong8
argument_list|)
decl_stmt|,
name|field6
init|=
operator|new
name|LegacyLongField
argument_list|(
literal|"field6"
argument_list|,
literal|0L
argument_list|,
name|storedLong6
argument_list|)
decl_stmt|,
name|field4
init|=
operator|new
name|LegacyLongField
argument_list|(
literal|"field4"
argument_list|,
literal|0L
argument_list|,
name|storedLong4
argument_list|)
decl_stmt|,
name|field2
init|=
operator|new
name|LegacyLongField
argument_list|(
literal|"field2"
argument_list|,
literal|0L
argument_list|,
name|storedLong2
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// add fields, that have a distance to test general functionality
name|doc
operator|.
name|add
argument_list|(
name|field8
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field6
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field4
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
argument_list|)
expr_stmt|;
comment|// Add a series of noDocs docs with increasing long values, by updating the fields
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|noDocs
condition|;
name|l
operator|++
control|)
block|{
name|long
name|val
init|=
name|distance
operator|*
name|l
operator|+
name|startOffset
decl_stmt|;
name|field8
operator|.
name|setLongValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|field6
operator|.
name|setLongValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|field4
operator|.
name|setLongValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setLongValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|val
operator|=
name|l
operator|-
operator|(
name|noDocs
operator|/
literal|2
operator|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field2"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field4"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field6"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"field8"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|reader
operator|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
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
name|searcher
operator|=
literal|null
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSorting
specifier|private
name|void
name|testSorting
parameter_list|(
name|int
name|precisionStep
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|precisionStep
decl_stmt|;
comment|// 10 random tests, the index order is ascending,
comment|// so using a reverse sort field should retun descending documents
name|int
name|num
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
literal|20
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|long
name|lower
init|=
call|(
name|long
call|)
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|noDocs
operator|*
name|distance
argument_list|)
operator|+
name|startOffset
decl_stmt|;
name|long
name|upper
init|=
call|(
name|long
call|)
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|noDocs
operator|*
name|distance
argument_list|)
operator|+
name|startOffset
decl_stmt|;
if|if
condition|(
name|lower
operator|>
name|upper
condition|)
block|{
name|long
name|a
init|=
name|lower
decl_stmt|;
name|lower
operator|=
name|upper
expr_stmt|;
name|upper
operator|=
name|a
expr_stmt|;
block|}
name|Query
name|tq
init|=
name|LegacyNumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|noDocs
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|topDocs
operator|.
name|totalHits
operator|==
literal|0
condition|)
continue|continue;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertNotNull
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|long
name|last
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
name|field
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|long
name|act
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
name|field
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Docs should be sorted backwards"
argument_list|,
name|last
operator|>
name|act
argument_list|)
expr_stmt|;
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSorting_8bit
specifier|public
name|void
name|testSorting_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSorting_6bit
specifier|public
name|void
name|testSorting_6bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSorting_4bit
specifier|public
name|void
name|testSorting_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSorting_2bit
specifier|public
name|void
name|testSorting_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

