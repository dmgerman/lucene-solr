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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|RAMDirectory
import|;
end_import

begin_class
DECL|class|TestTermScorer
specifier|public
class|class
name|TestTermScorer
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|protected
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|values
specifier|protected
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"all"
block|,
literal|"dogs dogs"
block|,
literal|"like"
block|,
literal|"playing"
block|,
literal|"fetch"
block|,
literal|"all"
block|}
decl_stmt|;
DECL|field|indexSearcher
specifier|protected
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|indexReader
specifier|protected
name|IndexReader
name|indexReader
decl_stmt|;
DECL|method|TestTermScorer
specifier|public
name|TestTermScorer
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
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
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
name|values
operator|.
name|length
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
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
name|values
index|[
name|i
index|]
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|indexReader
operator|=
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
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
name|Term
name|allTerm
init|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|allTerm
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|termQuery
operator|.
name|weight
argument_list|(
name|indexSearcher
argument_list|)
decl_stmt|;
name|TermScorer
name|ts
init|=
operator|new
name|TermScorer
argument_list|(
name|weight
argument_list|,
name|indexReader
operator|.
name|termDocs
argument_list|(
name|allTerm
argument_list|)
argument_list|,
name|indexSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|indexReader
operator|.
name|norms
argument_list|(
name|FIELD
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ts is null and it shouldn't be"
argument_list|,
name|ts
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//we have 2 documents with the term all in them, one document for all the other values
specifier|final
name|List
name|docs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|//must call next first
name|ts
operator|.
name|score
argument_list|(
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|docs
operator|.
name|add
argument_list|(
operator|new
name|TestHit
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"score "
operator|+
name|score
operator|+
literal|" is not greater than 0"
argument_list|,
name|score
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Doc: "
operator|+
name|doc
operator|+
literal|" does not equal: "
operator|+
literal|0
operator|+
literal|" or doc does not equaal: "
operator|+
literal|5
argument_list|,
name|doc
operator|==
literal|0
operator|||
name|doc
operator|==
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"docs Size: "
operator|+
name|docs
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|docs
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|TestHit
name|doc0
init|=
operator|(
name|TestHit
operator|)
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TestHit
name|doc5
init|=
operator|(
name|TestHit
operator|)
name|docs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|//The scores should be the same
name|assertTrue
argument_list|(
name|doc0
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
name|doc5
operator|.
name|score
argument_list|,
name|doc0
operator|.
name|score
operator|==
name|doc5
operator|.
name|score
argument_list|)
expr_stmt|;
comment|/*         Score should be (based on Default Sim.:         All floats are approximate         tf = 1         numDocs = 6         docFreq(all) = 2         idf = ln(6/3) + 1 = 1.693147         idf ^ 2 = 2.8667         boost = 1         lengthNorm = 1 //there is 1 term in every document         coord = 1         sumOfSquaredWeights = (idf * boost) ^ 2 = 1.693147 ^ 2 = 2.8667         queryNorm = 1 / (sumOfSquaredWeights)^0.5 = 1 /(1.693147) = 0.590           score = 1 * 2.8667 * 1 * 1 * 0.590 = 1.69          */
name|assertTrue
argument_list|(
name|doc0
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|1.6931472f
argument_list|,
name|doc0
operator|.
name|score
operator|==
literal|1.6931472f
argument_list|)
expr_stmt|;
block|}
DECL|method|testNext
specifier|public
name|void
name|testNext
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|allTerm
init|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|allTerm
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|termQuery
operator|.
name|weight
argument_list|(
name|indexSearcher
argument_list|)
decl_stmt|;
name|TermScorer
name|ts
init|=
operator|new
name|TermScorer
argument_list|(
name|weight
argument_list|,
name|indexReader
operator|.
name|termDocs
argument_list|(
name|allTerm
argument_list|)
argument_list|,
name|indexSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|indexReader
operator|.
name|norms
argument_list|(
name|FIELD
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ts is null and it shouldn't be"
argument_list|,
name|ts
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"next did not return a doc"
argument_list|,
name|ts
operator|.
name|next
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"score is not correct"
argument_list|,
name|ts
operator|.
name|score
argument_list|()
operator|==
literal|1.6931472f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"next did not return a doc"
argument_list|,
name|ts
operator|.
name|next
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"score is not correct"
argument_list|,
name|ts
operator|.
name|score
argument_list|()
operator|==
literal|1.6931472f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"next returned a doc and it should not have"
argument_list|,
name|ts
operator|.
name|next
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testSkipTo
specifier|public
name|void
name|testSkipTo
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|allTerm
init|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|allTerm
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|termQuery
operator|.
name|weight
argument_list|(
name|indexSearcher
argument_list|)
decl_stmt|;
name|TermScorer
name|ts
init|=
operator|new
name|TermScorer
argument_list|(
name|weight
argument_list|,
name|indexReader
operator|.
name|termDocs
argument_list|(
name|allTerm
argument_list|)
argument_list|,
name|indexSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|indexReader
operator|.
name|norms
argument_list|(
name|FIELD
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ts is null and it shouldn't be"
argument_list|,
name|ts
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't skip"
argument_list|,
name|ts
operator|.
name|skipTo
argument_list|(
literal|3
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
comment|//The next doc should be doc 5
name|assertTrue
argument_list|(
literal|"doc should be number 5"
argument_list|,
name|ts
operator|.
name|doc
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testExplain
specifier|public
name|void
name|testExplain
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|allTerm
init|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|allTerm
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|termQuery
operator|.
name|weight
argument_list|(
name|indexSearcher
argument_list|)
decl_stmt|;
name|TermScorer
name|ts
init|=
operator|new
name|TermScorer
argument_list|(
name|weight
argument_list|,
name|indexReader
operator|.
name|termDocs
argument_list|(
name|allTerm
argument_list|)
argument_list|,
name|indexSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|indexReader
operator|.
name|norms
argument_list|(
name|FIELD
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ts is null and it shouldn't be"
argument_list|,
name|ts
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|ts
operator|.
name|explain
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"explanation is null and it shouldn't be"
argument_list|,
name|explanation
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Explanation: " + explanation.toString());
comment|//All this Explain does is return the term frequency
name|assertTrue
argument_list|(
literal|"term frq is not 1"
argument_list|,
name|explanation
operator|.
name|getValue
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|explanation
operator|=
name|ts
operator|.
name|explain
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"explanation is null and it shouldn't be"
argument_list|,
name|explanation
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Explanation: " + explanation.toString());
comment|//All this Explain does is return the term frequency
name|assertTrue
argument_list|(
literal|"term frq is not 0"
argument_list|,
name|explanation
operator|.
name|getValue
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Term
name|dogsTerm
init|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"dogs"
argument_list|)
decl_stmt|;
name|termQuery
operator|=
operator|new
name|TermQuery
argument_list|(
name|dogsTerm
argument_list|)
expr_stmt|;
name|weight
operator|=
name|termQuery
operator|.
name|weight
argument_list|(
name|indexSearcher
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|TermScorer
argument_list|(
name|weight
argument_list|,
name|indexReader
operator|.
name|termDocs
argument_list|(
name|dogsTerm
argument_list|)
argument_list|,
name|indexSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|indexReader
operator|.
name|norms
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ts is null and it shouldn't be"
argument_list|,
name|ts
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|explanation
operator|=
name|ts
operator|.
name|explain
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"explanation is null and it shouldn't be"
argument_list|,
name|explanation
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Explanation: " + explanation.toString());
comment|//All this Explain does is return the term frequency
name|float
name|sqrtTwo
init|=
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|2.0f
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"term frq: "
operator|+
name|explanation
operator|.
name|getValue
argument_list|()
operator|+
literal|" is not the square root of 2"
argument_list|,
name|explanation
operator|.
name|getValue
argument_list|()
operator|==
name|sqrtTwo
argument_list|)
expr_stmt|;
name|explanation
operator|=
name|ts
operator|.
name|explain
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|//try a doc out of range
name|assertTrue
argument_list|(
literal|"explanation is null and it shouldn't be"
argument_list|,
name|explanation
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Explanation: " + explanation.toString());
comment|//All this Explain does is return the term frequency
name|assertTrue
argument_list|(
literal|"term frq: "
operator|+
name|explanation
operator|.
name|getValue
argument_list|()
operator|+
literal|" is not 0"
argument_list|,
name|explanation
operator|.
name|getValue
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|class|TestHit
specifier|private
class|class
name|TestHit
block|{
DECL|field|doc
specifier|public
name|int
name|doc
decl_stmt|;
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|method|TestHit
specifier|public
name|TestHit
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TestHit{"
operator|+
literal|"doc="
operator|+
name|doc
operator|+
literal|", score="
operator|+
name|score
operator|+
literal|"}"
return|;
block|}
block|}
block|}
end_class

end_unit

