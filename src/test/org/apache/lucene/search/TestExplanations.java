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
comment|/**  * Copyright 2006 Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|queryParser
operator|.
name|QueryParser
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
name|queryParser
operator|.
name|ParseException
import|;
end_import

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

begin_comment
comment|/**  * Tests primative queries (ie: that rewrite to themselves) to  * insure they match the expected set of docs, and that the score of each  * match is equal to the value of the scores explanation.  *  *<p>  * The assumption is that if all of the "primative" queries work well,  * then anythingthat rewrites to a primative will work well also.  *</p>  *  * @see "Subclasses for actual tests"  */
end_comment

begin_class
DECL|class|TestExplanations
specifier|public
class|class
name|TestExplanations
extends|extends
name|TestCase
block|{
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|qp
specifier|public
specifier|static
specifier|final
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|FIELD
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
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
name|docFields
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
name|docFields
index|[
name|i
index|]
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
name|TOKENIZED
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
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
DECL|field|docFields
specifier|protected
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3 zz"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3 zz"
block|}
decl_stmt|;
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|String
name|queryText
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|qp
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
return|;
block|}
DECL|method|qtest
specifier|public
name|void
name|qtest
parameter_list|(
name|String
name|queryText
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
name|makeQuery
argument_list|(
name|queryText
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|qtest
specifier|public
name|void
name|qtest
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
comment|// check that the expDocNrs first, then check the explanations
name|CheckHits
operator|.
name|checkHitCollector
argument_list|(
name|q
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|q
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests a query using qtest after wrapping it with both optB and reqB    * @see #qtest    * @see #reqB    * @see #optB    */
DECL|method|bqtest
specifier|public
name|void
name|bqtest
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
name|reqB
argument_list|(
name|q
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|optB
argument_list|(
name|q
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests a query using qtest after wrapping it with both optB and reqB    * @see #qtest    * @see #reqB    * @see #optB    */
DECL|method|bqtest
specifier|public
name|void
name|bqtest
parameter_list|(
name|String
name|queryText
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|bqtest
argument_list|(
name|makeQuery
argument_list|(
name|queryText
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
comment|/** A filter that only lets the specified document numbers pass */
DECL|class|ItemizedFilter
specifier|public
specifier|static
class|class
name|ItemizedFilter
extends|extends
name|Filter
block|{
DECL|field|docs
name|int
index|[]
name|docs
decl_stmt|;
DECL|method|ItemizedFilter
specifier|public
name|ItemizedFilter
parameter_list|(
name|int
index|[]
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|BitSet
name|b
init|=
operator|new
name|BitSet
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|set
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
block|}
comment|/** helper for generating MultiPhraseQueries */
DECL|method|ta
specifier|public
specifier|static
name|Term
index|[]
name|ta
parameter_list|(
name|String
index|[]
name|s
parameter_list|)
block|{
name|Term
index|[]
name|t
init|=
operator|new
name|Term
index|[
name|s
operator|.
name|length
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
name|s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
comment|/** MACRO for SpanTermQuery */
DECL|method|st
specifier|public
name|SpanTermQuery
name|st
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNotQuery */
DECL|method|snot
specifier|public
name|SpanNotQuery
name|snot
parameter_list|(
name|SpanQuery
name|i
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
operator|new
name|SpanNotQuery
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanTerm queries */
DECL|method|sor
specifier|public
name|SpanOrQuery
name|sor
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|e
parameter_list|)
block|{
return|return
name|sor
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanQueries */
DECL|method|sor
specifier|public
name|SpanOrQuery
name|sor
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|s
block|,
name|e
block|}
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing three SpanTerm queries */
DECL|method|sor
specifier|public
name|SpanOrQuery
name|sor
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|m
parameter_list|,
name|String
name|e
parameter_list|)
block|{
return|return
name|sor
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|m
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanQueries */
DECL|method|sor
specifier|public
name|SpanOrQuery
name|sor
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|m
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|s
block|,
name|m
block|,
name|e
block|}
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing two SpanTerm queries */
DECL|method|snear
specifier|public
name|SpanNearQuery
name|snear
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|snear
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing two SpanQueries */
DECL|method|snear
specifier|public
name|SpanNearQuery
name|snear
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|s
block|,
name|e
block|}
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing three SpanTerm queries */
DECL|method|snear
specifier|public
name|SpanNearQuery
name|snear
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|m
parameter_list|,
name|String
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|snear
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|m
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing three SpanQueries */
DECL|method|snear
specifier|public
name|SpanNearQuery
name|snear
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|m
parameter_list|,
name|SpanQuery
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|s
block|,
name|m
block|,
name|e
block|}
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanFirst(SpanTermQuery) */
DECL|method|sf
specifier|public
name|SpanFirstQuery
name|sf
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
operator|new
name|SpanFirstQuery
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is optional, along    * with a second prohibited clause which will never match anything    */
DECL|method|optB
specifier|public
name|Query
name|optB
parameter_list|(
name|String
name|q
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|optB
argument_list|(
name|makeQuery
argument_list|(
name|q
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is optional, along    * with a second prohibited clause which will never match anything    */
DECL|method|optB
specifier|public
name|Query
name|optB
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
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
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"NEVER"
argument_list|,
literal|"MATCH"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is required, along    * with a second optional clause which will match everything    */
DECL|method|reqB
specifier|public
name|Query
name|reqB
parameter_list|(
name|String
name|q
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|reqB
argument_list|(
name|makeQuery
argument_list|(
name|q
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is required, along    * with a second optional clause which will match everything    */
DECL|method|reqB
specifier|public
name|Query
name|reqB
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
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
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|/**    * Placeholder: JUnit freaks if you don't have one test ... making    * class abstract doesn't help    */
DECL|method|testNoop
specifier|public
name|void
name|testNoop
parameter_list|()
block|{
comment|/* NOOP */
block|}
block|}
end_class

end_unit

