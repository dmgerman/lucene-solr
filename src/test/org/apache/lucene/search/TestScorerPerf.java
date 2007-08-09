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

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  * @version $Id$  */
end_comment

begin_class
DECL|class|TestScorerPerf
specifier|public
class|class
name|TestScorerPerf
extends|extends
name|TestCase
block|{
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|validate
name|boolean
name|validate
init|=
literal|true
decl_stmt|;
comment|// set to false when doing performance testing
DECL|field|sets
name|BitSet
index|[]
name|sets
decl_stmt|;
DECL|field|s
name|IndexSearcher
name|s
decl_stmt|;
DECL|method|createDummySearcher
specifier|public
name|void
name|createDummySearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a dummy index with nothing in it.
comment|// This could possibly fail if Lucene starts checking for docid ranges...
name|RAMDirectory
name|rd
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|rd
argument_list|)
expr_stmt|;
block|}
DECL|method|createRandomTerms
specifier|public
name|void
name|createRandomTerms
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|int
name|nTerms
parameter_list|,
name|double
name|power
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
name|int
index|[]
name|freq
init|=
operator|new
name|int
index|[
name|nTerms
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
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|int
name|f
init|=
operator|(
name|nTerms
operator|+
literal|1
operator|)
operator|-
name|i
decl_stmt|;
comment|// make first terms less frequent
name|freq
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
name|f
argument_list|,
name|power
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|iw
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|123
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
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
name|nTerms
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
name|freq
index|[
name|j
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|j
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randBitSet
specifier|public
name|BitSet
name|randBitSet
parameter_list|(
name|int
name|sz
parameter_list|,
name|int
name|numBitsToSet
parameter_list|)
block|{
name|BitSet
name|set
init|=
operator|new
name|BitSet
argument_list|(
name|sz
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
name|numBitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|randBitSets
specifier|public
name|BitSet
index|[]
name|randBitSets
parameter_list|(
name|int
name|numSets
parameter_list|,
name|int
name|setSize
parameter_list|)
block|{
name|BitSet
index|[]
name|sets
init|=
operator|new
name|BitSet
index|[
name|numSets
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
name|sets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sets
index|[
name|i
index|]
operator|=
name|randBitSet
argument_list|(
name|setSize
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
name|setSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
DECL|class|BitSetFilter
specifier|public
specifier|static
class|class
name|BitSetFilter
extends|extends
name|Filter
block|{
DECL|field|set
specifier|public
name|BitSet
name|set
decl_stmt|;
DECL|method|BitSetFilter
specifier|public
name|BitSetFilter
parameter_list|(
name|BitSet
name|set
parameter_list|)
block|{
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|set
return|;
block|}
block|}
DECL|class|CountingHitCollector
specifier|public
specifier|static
class|class
name|CountingHitCollector
extends|extends
name|HitCollector
block|{
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|sum
name|int
name|sum
init|=
literal|0
decl_stmt|;
DECL|method|collect
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
name|count
operator|++
expr_stmt|;
name|sum
operator|+=
name|doc
expr_stmt|;
comment|// use it to avoid any possibility of being optimized away
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getSum
specifier|public
name|int
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
block|}
DECL|class|MatchingHitCollector
specifier|public
specifier|static
class|class
name|MatchingHitCollector
extends|extends
name|CountingHitCollector
block|{
DECL|field|answer
name|BitSet
name|answer
decl_stmt|;
DECL|field|pos
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|MatchingHitCollector
specifier|public
name|MatchingHitCollector
parameter_list|(
name|BitSet
name|answer
parameter_list|)
block|{
name|this
operator|.
name|answer
operator|=
name|answer
expr_stmt|;
block|}
DECL|method|collect
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
name|pos
operator|=
name|answer
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|!=
name|doc
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expected doc "
operator|+
name|pos
operator|+
literal|" but got "
operator|+
name|doc
argument_list|)
throw|;
block|}
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addClause
name|BitSet
name|addClause
parameter_list|(
name|BooleanQuery
name|bq
parameter_list|,
name|BitSet
name|result
parameter_list|)
block|{
name|BitSet
name|rnd
init|=
name|sets
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|sets
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|BitSetFilter
argument_list|(
name|rnd
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
if|if
condition|(
name|validate
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|result
operator|=
operator|(
name|BitSet
operator|)
name|rnd
operator|.
name|clone
argument_list|()
expr_stmt|;
else|else
name|result
operator|.
name|and
argument_list|(
name|rnd
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|doConjunctions
specifier|public
name|int
name|doConjunctions
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|maxClauses
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BitSet
name|result
init|=
literal|null
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
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|result
operator|=
name|addClause
argument_list|(
name|bq
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|CountingHitCollector
name|hc
init|=
name|validate
condition|?
operator|new
name|MatchingHitCollector
argument_list|(
name|result
argument_list|)
else|:
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
if|if
condition|(
name|validate
condition|)
name|assertEquals
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hc
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(hc.getCount());
block|}
return|return
name|ret
return|;
block|}
DECL|method|doNestedConjunctions
specifier|public
name|int
name|doNestedConjunctions
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|maxOuterClauses
parameter_list|,
name|int
name|maxClauses
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|oClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxOuterClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
name|BooleanQuery
name|oq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BitSet
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|o
init|=
literal|0
init|;
name|o
operator|<
name|oClauses
condition|;
name|o
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
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
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|result
operator|=
name|addClause
argument_list|(
name|bq
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|oq
operator|.
name|add
argument_list|(
name|bq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// outer
name|CountingHitCollector
name|hc
init|=
name|validate
condition|?
operator|new
name|MatchingHitCollector
argument_list|(
name|result
argument_list|)
else|:
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|oq
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
if|if
condition|(
name|validate
condition|)
name|assertEquals
argument_list|(
name|result
operator|.
name|cardinality
argument_list|()
argument_list|,
name|hc
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(hc.getCount());
block|}
return|return
name|ret
return|;
block|}
DECL|method|doTermConjunctions
specifier|public
name|int
name|doTermConjunctions
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BitSet
name|terms
init|=
operator|new
name|BitSet
argument_list|(
name|termsInIndex
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
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
decl_stmt|;
comment|// don't pick same clause twice
do|do
block|{
name|tnum
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|terms
operator|.
name|get
argument_list|(
name|tnum
argument_list|)
condition|)
do|;
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|tnum
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|doNestedTermConjunctions
specifier|public
name|int
name|doNestedTermConjunctions
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxOuterClauses
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|oClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxOuterClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
name|BooleanQuery
name|oq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|o
init|=
literal|0
init|;
name|o
operator|<
name|oClauses
condition|;
name|o
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BitSet
name|terms
init|=
operator|new
name|BitSet
argument_list|(
name|termsInIndex
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
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
decl_stmt|;
comment|// don't pick same clause twice
do|do
block|{
name|tnum
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|terms
operator|.
name|get
argument_list|(
name|tnum
argument_list|)
condition|)
do|;
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|tnum
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// inner
name|oq
operator|.
name|add
argument_list|(
name|bq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|// outer
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|oq
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|doSloppyPhrase
specifier|public
name|int
name|doSloppyPhrase
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|int
name|termsInIndex
parameter_list|,
name|int
name|maxClauses
parameter_list|,
name|int
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nClauses
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxClauses
operator|-
literal|1
argument_list|)
operator|+
literal|2
decl_stmt|;
comment|// min 2 clauses
name|PhraseQuery
name|q
init|=
operator|new
name|PhraseQuery
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
name|nClauses
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tnum
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|termsInIndex
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|tnum
argument_list|)
argument_list|)
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
name|q
operator|.
name|setSlop
argument_list|(
name|termsInIndex
argument_list|)
expr_stmt|;
comment|// this could be random too
name|CountingHitCollector
name|hc
init|=
operator|new
name|CountingHitCollector
argument_list|()
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|hc
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|hc
operator|.
name|getSum
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|testConjunctions
specifier|public
name|void
name|testConjunctions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test many small sets... the bugs will be found on boundary conditions
name|createDummySearcher
argument_list|()
expr_stmt|;
name|validate
operator|=
literal|true
expr_stmt|;
name|sets
operator|=
name|randBitSets
argument_list|(
literal|1000
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|doConjunctions
argument_list|(
literal|10000
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|doNestedConjunctions
argument_list|(
literal|10000
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/***   int bigIter=6;   public void testConjunctionPerf() throws Exception {     createDummySearcher();     validate=false;     sets=randBitSets(32,1000000);     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doConjunctions(500,6);       long end = System.currentTimeMillis();       System.out.println("milliseconds="+(end-start));     }     s.close();   }    public void testNestedConjunctionPerf() throws Exception {     createDummySearcher();     validate=false;     sets=randBitSets(32,1000000);     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doNestedConjunctions(500,3,3);       long end = System.currentTimeMillis();       System.out.println("milliseconds="+(end-start));     }     s.close();   }    public void testConjunctionTerms() throws Exception {     validate=false;     RAMDirectory dir = new RAMDirectory();     System.out.println("Creating index");     createRandomTerms(100000,25,2, dir);     s = new IndexSearcher(dir);     System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doTermConjunctions(s,25,5,10000);       long end = System.currentTimeMillis();       System.out.println("milliseconds="+(end-start));     }     s.close();   }    public void testNestedConjunctionTerms() throws Exception {     validate=false;         RAMDirectory dir = new RAMDirectory();     System.out.println("Creating index");     createRandomTerms(100000,25,2, dir);     s = new IndexSearcher(dir);     System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doNestedTermConjunctions(s,25,5,5,1000);       long end = System.currentTimeMillis();       System.out.println("milliseconds="+(end-start));     }     s.close();   }     public void testSloppyPhrasePerf() throws Exception {     validate=false;         RAMDirectory dir = new RAMDirectory();     System.out.println("Creating index");     createRandomTerms(100000,25,2,dir);     s = new IndexSearcher(dir);     System.out.println("Starting performance test");     for (int i=0; i<bigIter; i++) {       long start = System.currentTimeMillis();       doSloppyPhrase(s,25,2,1000);       long end = System.currentTimeMillis();       System.out.println("milliseconds="+(end-start));     }     s.close();    }    ***/
block|}
end_class

end_unit

