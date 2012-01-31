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
name|Locale
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
name|util
operator|.
name|TreeSet
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
DECL|class|CheckHits
specifier|public
class|class
name|CheckHits
block|{
comment|/**    * Some explains methods calculate their values though a slightly    * different  order of operations from the actual scoring method ...    * this allows for a small amount of relative variation    */
DECL|field|EXPLAIN_SCORE_TOLERANCE_DELTA
specifier|public
specifier|static
name|float
name|EXPLAIN_SCORE_TOLERANCE_DELTA
init|=
literal|0.001f
decl_stmt|;
comment|/**    * In general we use a relative epsilon, but some tests do crazy things    * like boost documents with 0, creating tiny tiny scores where the    * relative difference is large but the absolute difference is tiny.    * we ensure the the epsilon is always at least this big.    */
DECL|field|EXPLAIN_SCORE_TOLERANCE_MINIMUM
specifier|public
specifier|static
name|float
name|EXPLAIN_SCORE_TOLERANCE_MINIMUM
init|=
literal|1e-6f
decl_stmt|;
comment|/**    * Tests that all documents up to maxDoc which are *not* in the    * expected result set, have an explanation which indicates that     * the document does not match    */
DECL|method|checkNoMatchExplanations
specifier|public
specifier|static
name|void
name|checkNoMatchExplanations
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|d
init|=
name|q
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|ignore
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ignore
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|results
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|ignore
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|doc
argument_list|)
argument_list|)
condition|)
continue|continue;
name|Explanation
name|exp
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Explanation of [["
operator|+
name|d
operator|+
literal|"]] for #"
operator|+
name|doc
operator|+
literal|" is null"
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Explanation of [["
operator|+
name|d
operator|+
literal|"]] for #"
operator|+
name|doc
operator|+
literal|" doesn't indicate non-match: "
operator|+
name|exp
operator|.
name|toString
argument_list|()
argument_list|,
name|exp
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests that a query matches the an expected set of documents using a    * HitCollector.    *    *<p>    * Note that when using the HitCollector API, documents will be collected    * if they "match" regardless of what their score is.    *</p>    * @param query the query to test    * @param searcher the searcher to test the query against    * @param defaultFieldName used for displaying the query in assertion messages    * @param results a list of documentIds that must match the query    * @see Searcher#search(Query,Collector)    * @see #checkHits    */
DECL|method|checkHitCollector
specifier|public
specifier|static
name|void
name|checkHitCollector
parameter_list|(
name|Random
name|random
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|correct
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|correct
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|results
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|actual
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Collector
name|c
init|=
operator|new
name|SetCollector
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Simple: "
operator|+
name|query
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
argument_list|,
name|correct
argument_list|,
name|actual
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|-
literal|1
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|actual
operator|.
name|clear
argument_list|()
expr_stmt|;
name|IndexSearcher
name|s
init|=
name|QueryUtils
operator|.
name|wrapUnderlyingReader
argument_list|(
name|random
argument_list|,
name|searcher
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Wrap Reader "
operator|+
name|i
operator|+
literal|": "
operator|+
name|query
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
argument_list|,
name|correct
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|purgeFieldCache
argument_list|(
name|s
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
comment|// our wrapping can create insanity otherwise
block|}
block|}
DECL|class|SetCollector
specifier|public
specifier|static
class|class
name|SetCollector
extends|extends
name|Collector
block|{
DECL|field|bag
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|bag
decl_stmt|;
DECL|method|SetCollector
specifier|public
name|SetCollector
parameter_list|(
name|Set
argument_list|<
name|Integer
argument_list|>
name|bag
parameter_list|)
block|{
name|this
operator|.
name|bag
operator|=
name|bag
expr_stmt|;
block|}
DECL|field|base
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bag
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|doc
operator|+
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Tests that a query matches the an expected set of documents using Hits.    *    *<p>    * Note that when using the Hits API, documents will only be returned    * if they have a positive normalized score.    *</p>    * @param query the query to test    * @param searcher the searcher to test the query against    * @param defaultFieldName used for displaing the query in assertion messages    * @param results a list of documentIds that must match the query    * @see Searcher#search(Query, int)    * @see #checkHitCollector    */
DECL|method|checkHits
specifier|public
specifier|static
name|void
name|checkHits
parameter_list|(
name|Random
name|random
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|correct
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|correct
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|results
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Integer
argument_list|>
name|actual
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
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
name|hits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
argument_list|,
name|correct
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that a Hits has an expected order of documents */
DECL|method|checkDocIds
specifier|public
specifier|static
name|void
name|checkDocIds
parameter_list|(
name|String
name|mes
parameter_list|,
name|int
index|[]
name|results
parameter_list|,
name|ScoreDoc
index|[]
name|hits
parameter_list|)
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|mes
operator|+
literal|" nr of hits"
argument_list|,
name|hits
operator|.
name|length
argument_list|,
name|results
operator|.
name|length
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|mes
operator|+
literal|" doc nrs for hit "
operator|+
name|i
argument_list|,
name|results
index|[
name|i
index|]
argument_list|,
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests that two queries have an expected order of documents,    * and that the two queries have the same score values.    */
DECL|method|checkHitsQuery
specifier|public
specifier|static
name|void
name|checkHitsQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|ScoreDoc
index|[]
name|hits1
parameter_list|,
name|ScoreDoc
index|[]
name|hits2
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|checkDocIds
argument_list|(
literal|"hits1"
argument_list|,
name|results
argument_list|,
name|hits1
argument_list|)
expr_stmt|;
name|checkDocIds
argument_list|(
literal|"hits2"
argument_list|,
name|results
argument_list|,
name|hits2
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|query
argument_list|,
name|hits1
argument_list|,
name|hits2
argument_list|)
expr_stmt|;
block|}
DECL|method|checkEqual
specifier|public
specifier|static
name|void
name|checkEqual
parameter_list|(
name|Query
name|query
parameter_list|,
name|ScoreDoc
index|[]
name|hits1
parameter_list|,
name|ScoreDoc
index|[]
name|hits2
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|float
name|scoreTolerance
init|=
literal|1.0e-6f
decl_stmt|;
if|if
condition|(
name|hits1
operator|.
name|length
operator|!=
name|hits2
operator|.
name|length
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unequal lengths: hits1="
operator|+
name|hits1
operator|.
name|length
operator|+
literal|",hits2="
operator|+
name|hits2
operator|.
name|length
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
name|hits1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|hits1
index|[
name|i
index|]
operator|.
name|doc
operator|!=
name|hits2
index|[
name|i
index|]
operator|.
name|doc
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|" docnumbers don't match\n"
operator|+
name|hits2str
argument_list|(
name|hits1
argument_list|,
name|hits2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
literal|"for query:"
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|hits1
index|[
name|i
index|]
operator|.
name|doc
operator|!=
name|hits2
index|[
name|i
index|]
operator|.
name|doc
operator|)
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|hits1
index|[
name|i
index|]
operator|.
name|score
operator|-
name|hits2
index|[
name|i
index|]
operator|.
name|score
argument_list|)
operator|>
name|scoreTolerance
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|", doc nrs "
operator|+
name|hits1
index|[
name|i
index|]
operator|.
name|doc
operator|+
literal|" and "
operator|+
name|hits2
index|[
name|i
index|]
operator|.
name|doc
operator|+
literal|"\nunequal       : "
operator|+
name|hits1
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|"\n           and: "
operator|+
name|hits2
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|"\nfor query:"
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|hits2str
specifier|public
specifier|static
name|String
name|hits2str
parameter_list|(
name|ScoreDoc
index|[]
name|hits1
parameter_list|,
name|ScoreDoc
index|[]
name|hits2
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|len1
init|=
name|hits1
operator|==
literal|null
condition|?
literal|0
else|:
name|hits1
operator|.
name|length
decl_stmt|;
name|int
name|len2
init|=
name|hits2
operator|==
literal|null
condition|?
literal|0
else|:
name|hits2
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|end
operator|<=
literal|0
condition|)
block|{
name|end
operator|=
name|Math
operator|.
name|max
argument_list|(
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"Hits length1="
argument_list|)
operator|.
name|append
argument_list|(
name|len1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\tlength2="
argument_list|)
operator|.
name|append
argument_list|(
name|len2
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"hit="
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|len1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" doc"
argument_list|)
operator|.
name|append
argument_list|(
name|hits1
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|hits1
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"               "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|",\t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|len2
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" doc"
argument_list|)
operator|.
name|append
argument_list|(
name|hits2
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|hits2
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|topdocsString
specifier|public
specifier|static
name|String
name|topdocsString
parameter_list|(
name|TopDocs
name|docs
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"TopDocs totalHits="
argument_list|)
operator|.
name|append
argument_list|(
name|docs
operator|.
name|totalHits
argument_list|)
operator|.
name|append
argument_list|(
literal|" top="
argument_list|)
operator|.
name|append
argument_list|(
name|docs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|end
operator|<=
literal|0
condition|)
name|end
operator|=
name|docs
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
else|else
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|end
argument_list|,
name|docs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|") doc="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\tscore="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Asserts that the explanation value for every document matching a    * query corresponds with the true score.     *    * @see ExplanationAsserter    * @see #checkExplanations(Query, String, IndexSearcher, boolean) for a    * "deep" testing of the explanation details.    *       * @param query the query to test    * @param searcher the searcher to test the query against    * @param defaultFieldName used for displaing the query in assertion messages    */
DECL|method|checkExplanations
specifier|public
specifier|static
name|void
name|checkExplanations
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|checkExplanations
argument_list|(
name|query
argument_list|,
name|defaultFieldName
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that the explanation value for every document matching a    * query corresponds with the true score.  Optionally does "deep"     * testing of the explanation details.    *    * @see ExplanationAsserter    * @param query the query to test    * @param searcher the searcher to test the query against    * @param defaultFieldName used for displaing the query in assertion messages    * @param deep indicates whether a deep comparison of sub-Explanation details should be executed    */
DECL|method|checkExplanations
specifier|public
specifier|static
name|void
name|checkExplanations
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|deep
parameter_list|)
throws|throws
name|IOException
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|ExplanationAsserter
argument_list|(
name|query
argument_list|,
name|defaultFieldName
argument_list|,
name|searcher
argument_list|,
name|deep
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** returns a reasonable epsilon for comparing two floats,    *  where minor differences are acceptable such as score vs. explain */
DECL|method|explainToleranceDelta
specifier|public
specifier|static
name|float
name|explainToleranceDelta
parameter_list|(
name|float
name|f1
parameter_list|,
name|float
name|f2
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|EXPLAIN_SCORE_TOLERANCE_MINIMUM
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|f1
argument_list|)
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|f2
argument_list|)
argument_list|)
operator|*
name|EXPLAIN_SCORE_TOLERANCE_DELTA
argument_list|)
return|;
block|}
comment|/**     * Assert that an explanation has the expected score, and optionally that its    * sub-details max/sum/factor match to that score.    *    * @param q String representation of the query for assertion messages    * @param doc Document ID for assertion messages    * @param score Real score value of doc with query q    * @param deep indicates whether a deep comparison of sub-Explanation details should be executed    * @param expl The Explanation to match against score    */
DECL|method|verifyExplanation
specifier|public
specifier|static
name|void
name|verifyExplanation
parameter_list|(
name|String
name|q
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|Explanation
name|expl
parameter_list|)
block|{
name|float
name|value
init|=
name|expl
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|+
literal|": score(doc="
operator|+
name|doc
operator|+
literal|")="
operator|+
name|score
operator|+
literal|" != explanationScore="
operator|+
name|value
operator|+
literal|" Explanation: "
operator|+
name|expl
argument_list|,
name|score
argument_list|,
name|value
argument_list|,
name|explainToleranceDelta
argument_list|(
name|score
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|deep
condition|)
return|return;
name|Explanation
name|detail
index|[]
init|=
name|expl
operator|.
name|getDetails
argument_list|()
decl_stmt|;
comment|// TODO: can we improve this entire method? its really geared to work only with TF/IDF
if|if
condition|(
name|expl
operator|.
name|getDescription
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"computed from:"
argument_list|)
condition|)
block|{
return|return;
comment|// something more complicated.
block|}
if|if
condition|(
name|detail
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|detail
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// simple containment, unless its a freq of: (which lets a query explain how the freq is calculated),
comment|// just verify contained expl has same score
if|if
condition|(
operator|!
name|expl
operator|.
name|getDescription
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"with freq of:"
argument_list|)
condition|)
name|verifyExplanation
argument_list|(
name|q
argument_list|,
name|doc
argument_list|,
name|score
argument_list|,
name|deep
argument_list|,
name|detail
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// explanation must either:
comment|// - end with one of: "product of:", "sum of:", "max of:", or
comment|// - have "max plus<x> times others" (where<x> is float).
name|float
name|x
init|=
literal|0
decl_stmt|;
name|String
name|descr
init|=
name|expl
operator|.
name|getDescription
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|boolean
name|productOf
init|=
name|descr
operator|.
name|endsWith
argument_list|(
literal|"product of:"
argument_list|)
decl_stmt|;
name|boolean
name|sumOf
init|=
name|descr
operator|.
name|endsWith
argument_list|(
literal|"sum of:"
argument_list|)
decl_stmt|;
name|boolean
name|maxOf
init|=
name|descr
operator|.
name|endsWith
argument_list|(
literal|"max of:"
argument_list|)
decl_stmt|;
name|boolean
name|maxTimesOthers
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|productOf
operator|||
name|sumOf
operator|||
name|maxOf
operator|)
condition|)
block|{
comment|// maybe 'max plus x times others'
name|int
name|k1
init|=
name|descr
operator|.
name|indexOf
argument_list|(
literal|"max plus "
argument_list|)
decl_stmt|;
if|if
condition|(
name|k1
operator|>=
literal|0
condition|)
block|{
name|k1
operator|+=
literal|"max plus "
operator|.
name|length
argument_list|()
expr_stmt|;
name|int
name|k2
init|=
name|descr
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|,
name|k1
argument_list|)
decl_stmt|;
try|try
block|{
name|x
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|descr
operator|.
name|substring
argument_list|(
name|k1
argument_list|,
name|k2
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|descr
operator|.
name|substring
argument_list|(
name|k2
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"times others of:"
argument_list|)
condition|)
block|{
name|maxTimesOthers
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{             }
block|}
block|}
comment|// TODO: this is a TERRIBLE assertion!!!!
name|Assert
operator|.
name|assertTrue
argument_list|(
name|q
operator|+
literal|": multi valued explanation description=\""
operator|+
name|descr
operator|+
literal|"\" must be 'max of plus x times others' or end with 'product of'"
operator|+
literal|" or 'sum of:' or 'max of:' - "
operator|+
name|expl
argument_list|,
name|productOf
operator|||
name|sumOf
operator|||
name|maxOf
operator|||
name|maxTimesOthers
argument_list|)
expr_stmt|;
name|float
name|sum
init|=
literal|0
decl_stmt|;
name|float
name|product
init|=
literal|1
decl_stmt|;
name|float
name|max
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
name|detail
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|float
name|dval
init|=
name|detail
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|verifyExplanation
argument_list|(
name|q
argument_list|,
name|doc
argument_list|,
name|dval
argument_list|,
name|deep
argument_list|,
name|detail
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|product
operator|*=
name|dval
expr_stmt|;
name|sum
operator|+=
name|dval
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|dval
argument_list|)
expr_stmt|;
block|}
name|float
name|combined
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|productOf
condition|)
block|{
name|combined
operator|=
name|product
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sumOf
condition|)
block|{
name|combined
operator|=
name|sum
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maxOf
condition|)
block|{
name|combined
operator|=
name|max
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maxTimesOthers
condition|)
block|{
name|combined
operator|=
name|max
operator|+
name|x
operator|*
operator|(
name|sum
operator|-
name|max
operator|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"should never get here!"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q
operator|+
literal|": actual subDetails combined=="
operator|+
name|combined
operator|+
literal|" != value="
operator|+
name|value
operator|+
literal|" Explanation: "
operator|+
name|expl
argument_list|,
name|combined
argument_list|,
name|value
argument_list|,
name|explainToleranceDelta
argument_list|(
name|combined
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * an IndexSearcher that implicitly checks hte explanation of every match    * whenever it executes a search.    *    * @see ExplanationAsserter    */
DECL|class|ExplanationAssertingSearcher
specifier|public
specifier|static
class|class
name|ExplanationAssertingSearcher
extends|extends
name|IndexSearcher
block|{
DECL|method|ExplanationAssertingSearcher
specifier|public
name|ExplanationAssertingSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
DECL|method|checkExplanations
specifier|protected
name|void
name|checkExplanations
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
operator|new
name|ExplanationAsserter
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|checkExplanations
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Collector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|checkExplanations
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|super
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|checkExplanations
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|super
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|checkExplanations
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
return|;
block|}
block|}
comment|/**    * Asserts that the score explanation for every document matching a    * query corresponds with the true score.    *    * NOTE: this HitCollector should only be used with the Query and Searcher    * specified at when it is constructed.    *    * @see CheckHits#verifyExplanation    */
DECL|class|ExplanationAsserter
specifier|public
specifier|static
class|class
name|ExplanationAsserter
extends|extends
name|Collector
block|{
DECL|field|q
name|Query
name|q
decl_stmt|;
DECL|field|s
name|IndexSearcher
name|s
decl_stmt|;
DECL|field|d
name|String
name|d
decl_stmt|;
DECL|field|deep
name|boolean
name|deep
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|base
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
comment|/** Constructs an instance which does shallow tests on the Explanation */
DECL|method|ExplanationAsserter
specifier|public
name|ExplanationAsserter
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|q
argument_list|,
name|defaultFieldName
argument_list|,
name|s
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|ExplanationAsserter
specifier|public
name|ExplanationAsserter
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|defaultFieldName
parameter_list|,
name|IndexSearcher
name|s
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|d
operator|=
name|q
operator|.
name|toString
argument_list|(
name|defaultFieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|deep
operator|=
name|deep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|exp
init|=
literal|null
decl_stmt|;
name|doc
operator|=
name|doc
operator|+
name|base
expr_stmt|;
try|try
block|{
name|exp
operator|=
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception in hitcollector of [["
operator|+
name|d
operator|+
literal|"]] for #"
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Explanation of [["
operator|+
name|d
operator|+
literal|"]] for #"
operator|+
name|doc
operator|+
literal|" is null"
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|verifyExplanation
argument_list|(
name|d
argument_list|,
name|doc
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
name|deep
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Explanation of [["
operator|+
name|d
operator|+
literal|"]] for #"
operator|+
name|doc
operator|+
literal|" does not indicate match: "
operator|+
name|exp
operator|.
name|toString
argument_list|()
argument_list|,
name|exp
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

