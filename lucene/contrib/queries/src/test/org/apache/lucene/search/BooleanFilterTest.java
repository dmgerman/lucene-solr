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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|SlowMultiReaderWrapper
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
DECL|class|BooleanFilterTest
specifier|public
class|class
name|BooleanFilterTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
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
argument_list|,
name|directory
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
comment|//Add series of docs with filterable fields : acces rights, prices, dates and "in-stock" flags
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin guest"
argument_list|,
literal|"010"
argument_list|,
literal|"20040101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"guest"
argument_list|,
literal|"020"
argument_list|,
literal|"20040101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"guest"
argument_list|,
literal|"020"
argument_list|,
literal|"20050101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin"
argument_list|,
literal|"020"
argument_list|,
literal|"20050101"
argument_list|,
literal|"Maybe"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin guest"
argument_list|,
literal|"030"
argument_list|,
literal|"20050101"
argument_list|,
literal|"N"
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|SlowMultiReaderWrapper
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
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
specifier|public
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
name|directory
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|RandomIndexWriter
name|writer
parameter_list|,
name|String
name|accessRights
parameter_list|,
name|String
name|price
parameter_list|,
name|String
name|date
parameter_list|,
name|String
name|inStock
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
name|newField
argument_list|(
literal|"accessRights"
argument_list|,
name|accessRights
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"price"
argument_list|,
name|price
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"inStock"
argument_list|,
name|inStock
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
DECL|method|getRangeFilter
specifier|private
name|Filter
name|getRangeFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|lowerPrice
parameter_list|,
name|String
name|upperPrice
parameter_list|)
block|{
name|Filter
name|f
init|=
operator|new
name|TermRangeFilter
argument_list|(
name|field
argument_list|,
name|lowerPrice
argument_list|,
name|upperPrice
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|f
return|;
block|}
DECL|method|getTermsFilter
specifier|private
name|Filter
name|getTermsFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|TermsFilter
name|tf
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|tf
operator|.
name|addTerm
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
return|return
name|tf
return|;
block|}
DECL|method|tstFilterCard
specifier|private
name|void
name|tstFilterCard
parameter_list|(
name|String
name|mes
parameter_list|,
name|int
name|expected
parameter_list|,
name|Filter
name|filt
parameter_list|)
throws|throws
name|Throwable
block|{
name|DocIdSetIterator
name|disi
init|=
name|filt
operator|.
name|getDocIdSet
argument_list|(
operator|new
name|AtomicReaderContext
argument_list|(
name|reader
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|actual
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|actual
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|mes
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|testShould
specifier|public
name|void
name|testShould
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Should retrieves only 1 doc"
argument_list|,
literal|1
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testShoulds
specifier|public
name|void
name|testShoulds
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds are Ored together"
argument_list|,
literal|5
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMustNot
specifier|public
name|void
name|testShouldsAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds Ored but AndNot"
argument_list|,
literal|4
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"Maybe"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds Ored but AndNots"
argument_list|,
literal|3
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMust
specifier|public
name|void
name|testShouldsAndMust
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds Ored but MUST"
argument_list|,
literal|3
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMusts
specifier|public
name|void
name|testShouldsAndMusts
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"date"
argument_list|,
literal|"20040101"
argument_list|,
literal|"20041231"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds Ored but MUSTs ANDED"
argument_list|,
literal|1
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMustsAndMustNot
specifier|public
name|void
name|testShouldsAndMustsAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|,
literal|"040"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"date"
argument_list|,
literal|"20050101"
argument_list|,
literal|"20051231"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"Shoulds Ored but MUSTs ANDED and MustNot"
argument_list|,
literal|0
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testJustMust
specifier|public
name|void
name|testJustMust
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"MUST"
argument_list|,
literal|3
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testJustMustNot
specifier|public
name|void
name|testJustMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"MUST_NOT"
argument_list|,
literal|4
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
DECL|method|testMustAndMustNot
specifier|public
name|void
name|testMustAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|tstFilterCard
argument_list|(
literal|"MUST_NOT wins over MUST for same docs"
argument_list|,
literal|0
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

