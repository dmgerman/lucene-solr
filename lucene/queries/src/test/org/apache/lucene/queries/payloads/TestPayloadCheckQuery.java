begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|payloads
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|analysis
operator|.
name|SimplePayloadFilter
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
name|Tokenizer
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
name|CheckHits
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
name|WildcardQuery
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
name|SpanMultiTermQueryWrapper
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
name|SpanPositionRangeQuery
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
name|English
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

begin_comment
comment|/** basic test of payload-spans */
end_comment

begin_class
DECL|class|TestPayloadCheckQuery
specifier|public
class|class
name|TestPayloadCheckQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
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
name|Analyzer
name|simplePayloadAnalyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|SimplePayloadFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
name|simplePayloadAnalyzer
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
comment|//writer.infoStream = System.out;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
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
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|searcher
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
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|()
argument_list|,
name|query
argument_list|,
literal|"field"
argument_list|,
name|searcher
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanPayloadCheck
specifier|public
name|void
name|testSpanPayloadCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|term1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|pay
init|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|5
argument_list|)
decl_stmt|;
name|SpanQuery
name|query
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|term1
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|pay
argument_list|)
argument_list|)
decl_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1125
block|,
literal|1135
block|,
literal|1145
block|,
literal|1155
block|,
literal|1165
block|,
literal|1175
block|,
literal|1185
block|,
literal|1195
block|,
literal|1225
block|,
literal|1235
block|,
literal|1245
block|,
literal|1255
block|,
literal|1265
block|,
literal|1275
block|,
literal|1285
block|,
literal|1295
block|,
literal|1325
block|,
literal|1335
block|,
literal|1345
block|,
literal|1355
block|,
literal|1365
block|,
literal|1375
block|,
literal|1385
block|,
literal|1395
block|,
literal|1425
block|,
literal|1435
block|,
literal|1445
block|,
literal|1455
block|,
literal|1465
block|,
literal|1475
block|,
literal|1485
block|,
literal|1495
block|,
literal|1525
block|,
literal|1535
block|,
literal|1545
block|,
literal|1555
block|,
literal|1565
block|,
literal|1575
block|,
literal|1585
block|,
literal|1595
block|,
literal|1625
block|,
literal|1635
block|,
literal|1645
block|,
literal|1655
block|,
literal|1665
block|,
literal|1675
block|,
literal|1685
block|,
literal|1695
block|,
literal|1725
block|,
literal|1735
block|,
literal|1745
block|,
literal|1755
block|,
literal|1765
block|,
literal|1775
block|,
literal|1785
block|,
literal|1795
block|,
literal|1825
block|,
literal|1835
block|,
literal|1845
block|,
literal|1855
block|,
literal|1865
block|,
literal|1875
block|,
literal|1885
block|,
literal|1895
block|,
literal|1925
block|,
literal|1935
block|,
literal|1945
block|,
literal|1955
block|,
literal|1965
block|,
literal|1975
block|,
literal|1985
block|,
literal|1995
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
literal|1125
argument_list|)
operator|.
name|getValue
argument_list|()
operator|>
literal|0.0f
argument_list|)
expr_stmt|;
name|SpanTermQuery
name|term2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hundred"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|snq
decl_stmt|;
name|SpanQuery
index|[]
name|clauses
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|list
decl_stmt|;
name|BytesRef
name|pay2
decl_stmt|;
name|clauses
operator|=
operator|new
name|SpanQuery
index|[
literal|2
index|]
expr_stmt|;
name|clauses
index|[
literal|0
index|]
operator|=
name|term1
expr_stmt|;
name|clauses
index|[
literal|1
index|]
operator|=
name|term2
expr_stmt|;
name|snq
operator|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pay
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|0
argument_list|)
expr_stmt|;
name|pay2
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|1
argument_list|)
expr_stmt|;
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|pay
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|pay2
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|snq
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|500
block|,
literal|501
block|,
literal|502
block|,
literal|503
block|,
literal|504
block|,
literal|505
block|,
literal|506
block|,
literal|507
block|,
literal|508
block|,
literal|509
block|,
literal|510
block|,
literal|511
block|,
literal|512
block|,
literal|513
block|,
literal|514
block|,
literal|515
block|,
literal|516
block|,
literal|517
block|,
literal|518
block|,
literal|519
block|,
literal|520
block|,
literal|521
block|,
literal|522
block|,
literal|523
block|,
literal|524
block|,
literal|525
block|,
literal|526
block|,
literal|527
block|,
literal|528
block|,
literal|529
block|,
literal|530
block|,
literal|531
block|,
literal|532
block|,
literal|533
block|,
literal|534
block|,
literal|535
block|,
literal|536
block|,
literal|537
block|,
literal|538
block|,
literal|539
block|,
literal|540
block|,
literal|541
block|,
literal|542
block|,
literal|543
block|,
literal|544
block|,
literal|545
block|,
literal|546
block|,
literal|547
block|,
literal|548
block|,
literal|549
block|,
literal|550
block|,
literal|551
block|,
literal|552
block|,
literal|553
block|,
literal|554
block|,
literal|555
block|,
literal|556
block|,
literal|557
block|,
literal|558
block|,
literal|559
block|,
literal|560
block|,
literal|561
block|,
literal|562
block|,
literal|563
block|,
literal|564
block|,
literal|565
block|,
literal|566
block|,
literal|567
block|,
literal|568
block|,
literal|569
block|,
literal|570
block|,
literal|571
block|,
literal|572
block|,
literal|573
block|,
literal|574
block|,
literal|575
block|,
literal|576
block|,
literal|577
block|,
literal|578
block|,
literal|579
block|,
literal|580
block|,
literal|581
block|,
literal|582
block|,
literal|583
block|,
literal|584
block|,
literal|585
block|,
literal|586
block|,
literal|587
block|,
literal|588
block|,
literal|589
block|,
literal|590
block|,
literal|591
block|,
literal|592
block|,
literal|593
block|,
literal|594
block|,
literal|595
block|,
literal|596
block|,
literal|597
block|,
literal|598
block|,
literal|599
block|}
argument_list|)
expr_stmt|;
name|clauses
operator|=
operator|new
name|SpanQuery
index|[
literal|3
index|]
expr_stmt|;
name|clauses
index|[
literal|0
index|]
operator|=
name|term1
expr_stmt|;
name|clauses
index|[
literal|1
index|]
operator|=
name|term2
expr_stmt|;
name|clauses
index|[
literal|2
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|snq
operator|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pay
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|0
argument_list|)
expr_stmt|;
name|pay2
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|1
argument_list|)
expr_stmt|;
name|BytesRef
name|pay3
init|=
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|2
argument_list|)
decl_stmt|;
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|pay
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|pay2
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|pay3
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|snq
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|505
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnorderedPayloadChecks
specifier|public
name|void
name|testUnorderedPayloadChecks
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanTermQuery
name|term5
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|term100
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hundred"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|term4
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|nearQuery
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term5
block|,
name|term100
block|,
name|term4
block|}
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|payloads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|SpanPayloadCheckQuery
name|payloadQuery
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|nearQuery
argument_list|,
name|payloads
argument_list|)
decl_stmt|;
name|checkHits
argument_list|(
name|payloadQuery
argument_list|,
operator|new
name|int
index|[]
block|{
literal|405
block|}
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|clear
argument_list|()
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: "
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|payloadQuery
operator|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|nearQuery
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|payloadQuery
argument_list|,
operator|new
name|int
index|[]
block|{
literal|504
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplexSpanChecks
specifier|public
name|void
name|testComplexSpanChecks
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanTermQuery
name|one
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|thous
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"thousand"
argument_list|)
argument_list|)
decl_stmt|;
comment|//should be one position in between
name|SpanTermQuery
name|hundred
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hundred"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|three
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|oneThous
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|one
block|,
name|thous
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|hundredThree
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|hundred
block|,
name|three
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|oneThousHunThree
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|oneThous
block|,
name|hundredThree
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanQuery
name|query
decl_stmt|;
comment|//this one's too small
name|query
operator|=
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|oneThousHunThree
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
comment|//this one's just right
name|query
operator|=
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|oneThousHunThree
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1103
block|,
literal|1203
block|,
literal|1303
block|,
literal|1403
block|,
literal|1503
block|,
literal|1603
block|,
literal|1703
block|,
literal|1803
block|,
literal|1903
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|payloads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BytesRef
name|pay
init|=
operator|new
name|BytesRef
argument_list|(
operator|(
literal|"pos: "
operator|+
literal|0
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|pay2
init|=
operator|new
name|BytesRef
argument_list|(
operator|(
literal|"pos: "
operator|+
literal|1
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|pay3
init|=
operator|new
name|BytesRef
argument_list|(
operator|(
literal|"pos: "
operator|+
literal|3
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|pay4
init|=
operator|new
name|BytesRef
argument_list|(
operator|(
literal|"pos: "
operator|+
literal|4
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|payloads
operator|.
name|add
argument_list|(
name|pay
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
name|pay2
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
name|pay3
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
name|pay4
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|oneThousHunThree
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1103
block|,
literal|1203
block|,
literal|1303
block|,
literal|1403
block|,
literal|1503
block|,
literal|1603
block|,
literal|1703
block|,
literal|1803
block|,
literal|1903
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquality
specifier|public
name|void
name|testEquality
parameter_list|()
block|{
name|SpanQuery
name|sq1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|sq2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|payload1
init|=
operator|new
name|BytesRef
argument_list|(
literal|"pay1"
argument_list|)
decl_stmt|;
name|BytesRef
name|payload2
init|=
operator|new
name|BytesRef
argument_list|(
literal|"pay2"
argument_list|)
decl_stmt|;
name|SpanQuery
name|query1
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq1
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|payload1
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|query2
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq2
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|payload1
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|query3
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq1
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|payload2
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|query4
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq2
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|payload2
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|query5
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq1
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|payload1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|query1
argument_list|,
name|query5
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query1
operator|.
name|equals
argument_list|(
name|query2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query1
operator|.
name|equals
argument_list|(
name|query3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query1
operator|.
name|equals
argument_list|(
name|query4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query2
operator|.
name|equals
argument_list|(
name|query3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query2
operator|.
name|equals
argument_list|(
name|query4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query3
operator|.
name|equals
argument_list|(
name|query4
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewrite
specifier|public
name|void
name|testRewrite
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanMultiTermQueryWrapper
name|fiv
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"fiv*"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SpanMultiTermQueryWrapper
name|hund
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"hund*"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SpanMultiTermQueryWrapper
name|twent
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"twent*"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SpanMultiTermQueryWrapper
name|nin
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"nin*"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|sq
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|fiv
block|,
name|hund
block|,
name|twent
block|,
name|nin
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|payloads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: 0"
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: 2"
argument_list|)
argument_list|)
expr_stmt|;
name|payloads
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"pos: 3"
argument_list|)
argument_list|)
expr_stmt|;
name|SpanPayloadCheckQuery
name|query
init|=
operator|new
name|SpanPayloadCheckQuery
argument_list|(
name|sq
argument_list|,
name|payloads
argument_list|)
decl_stmt|;
comment|// if query wasn't rewritten properly, the query would have failed with "Rewrite first!"
name|checkHits
argument_list|(
name|query
argument_list|,
operator|new
name|int
index|[]
block|{
literal|529
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

