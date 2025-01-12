begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|mlt
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
name|io
operator|.
name|StringReader
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
name|Arrays
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
name|BooleanClause
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
name|BooleanQuery
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
name|BoostQuery
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
name|QueryUtils
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestMoreLikeThis
specifier|public
class|class
name|TestMoreLikeThis
extends|extends
name|LuceneTestCase
block|{
DECL|field|SHOP_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|SHOP_TYPE
init|=
literal|"type"
decl_stmt|;
DECL|field|FOR_SALE
specifier|private
specifier|static
specifier|final
name|String
name|FOR_SALE
init|=
literal|"weSell"
decl_stmt|;
DECL|field|NOT_FOR_SALE
specifier|private
specifier|static
specifier|final
name|String
name|NOT_FOR_SALE
init|=
literal|"weDontSell"
decl_stmt|;
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
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
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
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
comment|// Add series of docs with specific information for MoreLikeThis
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"lucene release"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"apache"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"apache lucene"
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
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
name|text
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
literal|"text"
argument_list|,
name|text
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|RandomIndexWriter
name|writer
parameter_list|,
name|String
index|[]
name|texts
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
for|for
control|(
name|String
name|text
range|:
name|texts
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
block|}
DECL|method|testBoostFactor
specifier|public
name|void
name|testBoostFactor
parameter_list|()
throws|throws
name|Throwable
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|originalValues
init|=
name|getOriginalValues
argument_list|()
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// this mean that every term boost factor will be multiplied by this
comment|// number
name|float
name|boostFactor
init|=
literal|5
decl_stmt|;
name|mlt
operator|.
name|setBoostFactor
argument_list|(
name|boostFactor
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"lucene release"
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected "
operator|+
name|originalValues
operator|.
name|size
argument_list|()
operator|+
literal|" clauses."
argument_list|,
name|originalValues
operator|.
name|size
argument_list|()
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|BoostQuery
name|bq
init|=
operator|(
name|BoostQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|bq
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Float
name|termBoost
init|=
name|originalValues
operator|.
name|get
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Expected term "
operator|+
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|termBoost
argument_list|)
expr_stmt|;
name|float
name|totalBoost
init|=
name|termBoost
operator|*
name|boostFactor
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected boost of "
operator|+
name|totalBoost
operator|+
literal|" for term '"
operator|+
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
operator|+
literal|"' got "
operator|+
name|bq
operator|.
name|getBoost
argument_list|()
argument_list|,
name|totalBoost
argument_list|,
name|bq
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getOriginalValues
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getOriginalValues
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|originalValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"lucene release"
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|BoostQuery
name|bq
init|=
operator|(
name|BoostQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|bq
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|originalValues
operator|.
name|put
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|bq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|originalValues
return|;
block|}
comment|// LUCENE-3326
DECL|method|testMultiFields
specifier|public
name|void
name|testMultiFields
parameter_list|()
throws|throws
name|Exception
block|{
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|,
literal|"foobar"
block|}
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|like
argument_list|(
literal|"foobar"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5725
DECL|method|testMultiValues
specifier|public
name|void
name|testMultiValues
parameter_list|()
throws|throws
name|Exception
block|{
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"lucene"
argument_list|)
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"lucene release"
argument_list|)
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"apache"
argument_list|)
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"apache lucene"
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 2 clauses only!"
argument_list|,
literal|2
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|Term
name|term
init|=
operator|(
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"apache"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// just basic equals/hashcode etc
DECL|method|testMoreLikeThisQuery
specifier|public
name|void
name|testMoreLikeThisQuery
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Query
name|query
init|=
operator|new
name|MoreLikeThisQuery
argument_list|(
literal|"this is a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|,
name|analyzer
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTopN
specifier|public
name|void
name|testTopN
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
literal|100
decl_stmt|;
name|int
name|topN
init|=
literal|25
decl_stmt|;
comment|// add series of docs with terms of decreasing df
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|generateStrSeq
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// setup MLT query
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|topN
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
comment|// perform MLT query
name|String
name|likeText
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|generateStrSeq
argument_list|(
literal|0
argument_list|,
name|numDocs
argument_list|)
control|)
block|{
name|likeText
operator|+=
name|text
operator|+
literal|" "
expr_stmt|;
block|}
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|likeText
argument_list|)
argument_list|)
decl_stmt|;
comment|// check best terms are topN of highest idf
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected"
operator|+
name|topN
operator|+
literal|"clauses only!"
argument_list|,
name|topN
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Term
index|[]
name|expectedTerms
init|=
operator|new
name|Term
index|[
name|topN
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|generateStrSeq
argument_list|(
name|numDocs
operator|-
name|topN
argument_list|,
name|topN
argument_list|)
control|)
block|{
name|expectedTerms
index|[
name|idx
operator|++
index|]
operator|=
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|Term
name|term
init|=
operator|(
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedTerms
argument_list|)
operator|.
name|contains
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// clean up
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
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|generateStrSeq
specifier|private
name|String
index|[]
name|generateStrSeq
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|String
index|[]
name|generatedStrings
init|=
operator|new
name|String
index|[
name|size
index|]
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
name|generatedStrings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|generatedStrings
index|[
name|i
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|from
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|generatedStrings
return|;
block|}
DECL|method|addShopDoc
specifier|private
name|int
name|addShopDoc
parameter_list|(
name|RandomIndexWriter
name|writer
parameter_list|,
name|String
name|type
parameter_list|,
name|String
index|[]
name|weSell
parameter_list|,
name|String
index|[]
name|weDontSell
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
name|SHOP_TYPE
argument_list|,
name|type
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|item
range|:
name|weSell
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|FOR_SALE
argument_list|,
name|item
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|item
range|:
name|weDontSell
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|NOT_FOR_SALE
argument_list|,
name|item
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
return|return
name|writer
operator|.
name|numDocs
argument_list|()
operator|-
literal|1
return|;
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-7161"
argument_list|)
DECL|method|testMultiFieldShouldReturnPerFieldBooleanQuery
specifier|public
name|void
name|testMultiFieldShouldReturnPerFieldBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|Directory
name|dir
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
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|maxQueryTerms
init|=
literal|25
decl_stmt|;
name|String
index|[]
name|itShopItemForSale
init|=
operator|new
name|String
index|[]
block|{
literal|"watch"
block|,
literal|"ipod"
block|,
literal|"asrock"
block|,
literal|"imac"
block|,
literal|"macbookpro"
block|,
literal|"monitor"
block|,
literal|"keyboard"
block|,
literal|"mouse"
block|,
literal|"speakers"
block|}
decl_stmt|;
name|String
index|[]
name|itShopItemNotForSale
init|=
operator|new
name|String
index|[]
block|{
literal|"tie"
block|,
literal|"trousers"
block|,
literal|"shoes"
block|,
literal|"skirt"
block|,
literal|"hat"
block|}
decl_stmt|;
name|String
index|[]
name|clothesShopItemForSale
init|=
operator|new
name|String
index|[]
block|{
literal|"tie"
block|,
literal|"trousers"
block|,
literal|"shoes"
block|,
literal|"skirt"
block|,
literal|"hat"
block|}
decl_stmt|;
name|String
index|[]
name|clothesShopItemNotForSale
init|=
operator|new
name|String
index|[]
block|{
literal|"watch"
block|,
literal|"ipod"
block|,
literal|"asrock"
block|,
literal|"imac"
block|,
literal|"macbookpro"
block|,
literal|"monitor"
block|,
literal|"keyboard"
block|,
literal|"mouse"
block|,
literal|"speakers"
block|}
decl_stmt|;
comment|// add series of shop docs
name|RandomIndexWriter
name|writer
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
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|addShopDoc
argument_list|(
name|writer
argument_list|,
literal|"it"
argument_list|,
name|itShopItemForSale
argument_list|,
name|itShopItemNotForSale
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|addShopDoc
argument_list|(
name|writer
argument_list|,
literal|"clothes"
argument_list|,
name|clothesShopItemForSale
argument_list|,
name|clothesShopItemNotForSale
argument_list|)
expr_stmt|;
block|}
comment|// Input Document is a clothes shop
name|int
name|inputDocId
init|=
name|addShopDoc
argument_list|(
name|writer
argument_list|,
literal|"clothes"
argument_list|,
name|clothesShopItemForSale
argument_list|,
name|clothesShopItemNotForSale
argument_list|)
decl_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// setup MLT query
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|maxQueryTerms
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|FOR_SALE
block|,
name|NOT_FOR_SALE
block|}
argument_list|)
expr_stmt|;
comment|// perform MLT query
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
name|inputDocId
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|BooleanClause
argument_list|>
name|expectedClothesShopClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|itemForSale
range|:
name|clothesShopItemForSale
control|)
block|{
name|BooleanClause
name|booleanClause
init|=
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FOR_SALE
argument_list|,
name|itemForSale
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
decl_stmt|;
name|expectedClothesShopClauses
operator|.
name|add
argument_list|(
name|booleanClause
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|itemNotForSale
range|:
name|clothesShopItemNotForSale
control|)
block|{
name|BooleanClause
name|booleanClause
init|=
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|NOT_FOR_SALE
argument_list|,
name|itemNotForSale
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
decl_stmt|;
name|expectedClothesShopClauses
operator|.
name|add
argument_list|(
name|booleanClause
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|BooleanClause
name|expectedClause
range|:
name|expectedClothesShopClauses
control|)
block|{
name|assertTrue
argument_list|(
name|clauses
operator|.
name|contains
argument_list|(
name|expectedClause
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// clean up
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
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: add tests for the MoreLikeThisQuery
block|}
end_class

end_unit

