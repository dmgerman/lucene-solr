begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|*
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|similarities
operator|.
name|DefaultSimilarityProvider
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
name|similarities
operator|.
name|Similarity
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
name|similarities
operator|.
name|SimilarityProvider
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
name|MultiSpansWrapper
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
name|search
operator|.
name|spans
operator|.
name|Spans
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
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|FieldInvertState
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
name|Payload
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|TestPayloadTermQuery
specifier|public
class|class
name|TestPayloadTermQuery
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
DECL|field|similarityProvider
specifier|private
specifier|static
name|SimilarityProvider
name|similarityProvider
init|=
operator|new
name|BoostingSimilarityProvider
argument_list|()
decl_stmt|;
DECL|field|payloadField
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|payloadField
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|}
decl_stmt|;
DECL|field|payloadMultiField1
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|payloadMultiField1
init|=
operator|new
name|byte
index|[]
block|{
literal|2
block|}
decl_stmt|;
DECL|field|payloadMultiField2
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|payloadMultiField2
init|=
operator|new
name|byte
index|[]
block|{
literal|4
block|}
decl_stmt|;
DECL|field|directory
specifier|protected
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|class|PayloadAnalyzer
specifier|private
specifier|static
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|PayloadAnalyzer
specifier|private
name|PayloadAnalyzer
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|PerFieldReuseStrategy
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
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
name|result
argument_list|,
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|,
name|fieldName
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|PayloadFilter
specifier|private
specifier|static
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|numSeen
specifier|private
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
DECL|field|payloadAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|method|PayloadFilter
specifier|public
name|PayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|hasNext
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasNext
condition|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"field"
argument_list|)
condition|)
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|payloadField
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"multiField"
argument_list|)
condition|)
block|{
if|if
condition|(
name|numSeen
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|payloadMultiField1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|payloadMultiField2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numSeen
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|numSeen
operator|=
literal|0
expr_stmt|;
block|}
block|}
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
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|PayloadAnalyzer
argument_list|()
argument_list|)
operator|.
name|setSimilarityProvider
argument_list|(
name|similarityProvider
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
literal|1000
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
name|Field
name|noPayloadField
init|=
name|newField
argument_list|(
name|PayloadHelper
operator|.
name|NO_PAYLOAD_FIELD
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
comment|//noPayloadField.setBoost(0);
name|doc
operator|.
name|add
argument_list|(
name|noPayloadField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
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
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"multiField"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|"  "
operator|+
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
name|searcher
operator|.
name|setSimilarityProvider
argument_list|(
name|similarityProvider
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
literal|null
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadTermQuery
name|query
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"seventy"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits Size: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" is not: "
operator|+
literal|100
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|100
argument_list|)
expr_stmt|;
comment|//they should all have the exact same score, because they all contain seventy once, and we set
comment|//all the other similarity factors to be 1
name|assertTrue
argument_list|(
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|==
literal|1
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
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|query
argument_list|,
name|PayloadHelper
operator|.
name|FIELD
argument_list|,
name|searcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"spans is null and it shouldn't be"
argument_list|,
name|spans
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|/*float score = hits.score(0);     for (int i =1; i< hits.length(); i++)     {       assertTrue("scores are not equal and they should be", score == hits.score(i));     }*/
block|}
DECL|method|testQuery
specifier|public
name|void
name|testQuery
parameter_list|()
block|{
name|PayloadTermQuery
name|boostingFuncTermQuery
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|MULTI_FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|boostingFuncTermQuery
argument_list|)
expr_stmt|;
name|SpanTermQuery
name|spanTermQuery
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|MULTI_FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|boostingFuncTermQuery
operator|.
name|equals
argument_list|(
name|spanTermQuery
argument_list|)
operator|==
name|spanTermQuery
operator|.
name|equals
argument_list|(
name|boostingFuncTermQuery
argument_list|)
argument_list|)
expr_stmt|;
name|PayloadTermQuery
name|boostingFuncTermQuery2
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|MULTI_FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|boostingFuncTermQuery
argument_list|,
name|boostingFuncTermQuery2
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleMatchesPerDoc
specifier|public
name|void
name|testMultipleMatchesPerDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|PayloadTermQuery
name|query
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|MULTI_FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits Size: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" is not: "
operator|+
literal|100
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|100
argument_list|)
expr_stmt|;
comment|//they should all have the exact same score, because they all contain seventy once, and we set
comment|//all the other similarity factors to be 1
comment|//System.out.println("Hash: " + seventyHash + " Twice Hash: " + 2*seventyHash);
name|assertTrue
argument_list|(
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|4.0
argument_list|,
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|==
literal|4.0
argument_list|)
expr_stmt|;
comment|//there should be exactly 10 items that score a 4, all the rest should score a 2
comment|//The 10 items are: 70 + i*100 where i in [0-9]
name|int
name|numTens
init|=
literal|0
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
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|doc
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|numTens
operator|++
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|4.0
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|4.0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|numTens
operator|+
literal|" does not equal: "
operator|+
literal|10
argument_list|,
name|numTens
operator|==
literal|10
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|query
argument_list|,
literal|"field"
argument_list|,
name|searcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"spans is null and it shouldn't be"
argument_list|,
name|spans
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be two matches per document
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|//100 hits times 2 matches per hit, we should have 200 in count
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|+
literal|" does not equal: "
operator|+
literal|200
argument_list|,
name|count
operator|==
literal|200
argument_list|)
expr_stmt|;
block|}
comment|//Set includeSpanScore to false, in which case just the payload score comes through.
DECL|method|testIgnoreSpanScorer
specifier|public
name|void
name|testIgnoreSpanScorer
parameter_list|()
throws|throws
name|Exception
block|{
name|PayloadTermQuery
name|query
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|MULTI_FIELD
argument_list|,
literal|"seventy"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexSearcher
name|theSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|theSearcher
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|DefaultSimilarityProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|FullSimilarity
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits Size: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" is not: "
operator|+
literal|100
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|100
argument_list|)
expr_stmt|;
comment|//they should all have the exact same score, because they all contain seventy once, and we set
comment|//all the other similarity factors to be 1
comment|//System.out.println("Hash: " + seventyHash + " Twice Hash: " + 2*seventyHash);
name|assertTrue
argument_list|(
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|4.0
argument_list|,
name|hits
operator|.
name|getMaxScore
argument_list|()
operator|==
literal|4.0
argument_list|)
expr_stmt|;
comment|//there should be exactly 10 items that score a 4, all the rest should score a 2
comment|//The 10 items are: 70 + i*100 where i in [0-9]
name|int
name|numTens
init|=
literal|0
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
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|doc
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|numTens
operator|++
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|4.0
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|4.0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|numTens
operator|+
literal|" does not equal: "
operator|+
literal|10
argument_list|,
name|numTens
operator|==
literal|10
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|query
argument_list|,
literal|"field"
argument_list|,
name|searcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"spans is null and it shouldn't be"
argument_list|,
name|spans
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be two matches per document
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|//100 hits times 2 matches per hit, we should have 200 in count
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|theSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNoMatch
specifier|public
name|void
name|testNoMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|PayloadTermQuery
name|query
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|FIELD
argument_list|,
literal|"junk"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits Size: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" is not: "
operator|+
literal|0
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoPayload
specifier|public
name|void
name|testNoPayload
parameter_list|()
throws|throws
name|Exception
block|{
name|PayloadTermQuery
name|q1
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|NO_PAYLOAD_FIELD
argument_list|,
literal|"zero"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|PayloadTermQuery
name|q2
init|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PayloadHelper
operator|.
name|NO_PAYLOAD_FIELD
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|BooleanClause
name|c1
init|=
operator|new
name|BooleanClause
argument_list|(
name|q1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
decl_stmt|;
name|BooleanClause
name|c2
init|=
operator|new
name|BooleanClause
argument_list|(
name|q2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
decl_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits Size: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|1
argument_list|)
expr_stmt|;
name|int
index|[]
name|results
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|results
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
comment|//hits.scoreDocs[0].doc;
name|CheckHits
operator|.
name|checkHitCollector
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|PayloadHelper
operator|.
name|NO_PAYLOAD_FIELD
argument_list|,
name|searcher
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
comment|// must be static for weight serialization tests
DECL|class|BoostingSimilarityProvider
specifier|static
class|class
name|BoostingSimilarityProvider
implements|implements
name|SimilarityProvider
block|{
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
comment|//we know it is size 4 here, so ignore the offset/length
return|return
name|payload
operator|.
name|bytes
index|[
name|payload
operator|.
name|offset
index|]
return|;
block|}
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
comment|//Make everything else 1 so we see the effect of the payload
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
annotation|@
name|Override
specifier|public
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
name|freq
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|FullSimilarity
specifier|static
class|class
name|FullSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|//we know it is size 4 here, so ignore the offset/length
return|return
name|payload
index|[
name|offset
index|]
return|;
block|}
block|}
block|}
end_class

end_unit

