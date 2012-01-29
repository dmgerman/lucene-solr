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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|AtomicReader
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
operator|.
name|ReaderContext
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
name|DisjunctionMaxQuery
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
name|FilteredQuery
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
name|SpanOrQuery
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
name|util
operator|.
name|ReaderUtil
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
name|TermContext
import|;
end_import

begin_comment
comment|/**  * Experimental class to get set of payloads for most standard Lucene queries.  * Operates like Highlighter - IndexReader should only contain doc of interest,  * best to use MemoryIndex.  *  * @lucene.experimental  *   */
end_comment

begin_class
DECL|class|PayloadSpanUtil
specifier|public
class|class
name|PayloadSpanUtil
block|{
DECL|field|context
specifier|private
name|ReaderContext
name|context
decl_stmt|;
comment|/**    * @param context    *          that contains doc with payloads to extract    *              * @see IndexReader#getTopReaderContext()    */
DECL|method|PayloadSpanUtil
specifier|public
name|PayloadSpanUtil
parameter_list|(
name|ReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Query should be rewritten for wild/fuzzy support.    *     * @param query    * @return payloads Collection    * @throws IOException    */
DECL|method|getPayloadsForQuery
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayloadsForQuery
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloads
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|queryToSpanQuery
argument_list|(
name|query
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
return|return
name|payloads
return|;
block|}
DECL|method|queryToSpanQuery
specifier|private
name|void
name|queryToSpanQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloads
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanClause
index|[]
name|queryClauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|getClauses
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
name|queryClauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|queryClauses
index|[
name|i
index|]
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|queryToSpanQuery
argument_list|(
name|queryClauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|Term
index|[]
name|phraseQueryTerms
init|=
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
name|phraseQueryTerms
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
name|phraseQueryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
name|phraseQueryTerms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|slop
init|=
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|getSlop
argument_list|()
decl_stmt|;
name|boolean
name|inorder
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
block|{
name|inorder
operator|=
literal|true
expr_stmt|;
block|}
name|SpanNearQuery
name|sp
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
name|slop
argument_list|,
name|inorder
argument_list|)
decl_stmt|;
name|sp
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|getPayloads
argument_list|(
name|payloads
argument_list|,
name|sp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|SpanTermQuery
name|stq
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|)
decl_stmt|;
name|stq
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|getPayloads
argument_list|(
name|payloads
argument_list|,
name|stq
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|SpanQuery
condition|)
block|{
name|getPayloads
argument_list|(
name|payloads
argument_list|,
operator|(
name|SpanQuery
operator|)
name|query
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FilteredQuery
condition|)
block|{
name|queryToSpanQuery
argument_list|(
operator|(
operator|(
name|FilteredQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|DisjunctionMaxQuery
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Query
argument_list|>
name|iterator
init|=
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|queryToSpanQuery
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|MultiPhraseQuery
condition|)
block|{
specifier|final
name|MultiPhraseQuery
name|mpq
init|=
operator|(
name|MultiPhraseQuery
operator|)
name|query
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays
init|=
name|mpq
operator|.
name|getTermArrays
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|positions
init|=
name|mpq
operator|.
name|getPositions
argument_list|()
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|maxPosition
init|=
name|positions
index|[
name|positions
operator|.
name|length
operator|-
literal|1
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
name|positions
operator|.
name|length
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|positions
index|[
name|i
index|]
operator|>
name|maxPosition
condition|)
block|{
name|maxPosition
operator|=
name|positions
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Query
argument_list|>
index|[]
name|disjunctLists
init|=
operator|new
name|List
index|[
name|maxPosition
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|distinctPositions
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
name|termArrays
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Term
index|[]
name|termArray
init|=
name|termArrays
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|disjuncts
init|=
name|disjunctLists
index|[
name|positions
index|[
name|i
index|]
index|]
decl_stmt|;
if|if
condition|(
name|disjuncts
operator|==
literal|null
condition|)
block|{
name|disjuncts
operator|=
operator|(
name|disjunctLists
index|[
name|positions
index|[
name|i
index|]
index|]
operator|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|(
name|termArray
operator|.
name|length
argument_list|)
operator|)
expr_stmt|;
operator|++
name|distinctPositions
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Term
name|term
range|:
name|termArray
control|)
block|{
name|disjuncts
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|positionGaps
init|=
literal|0
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
specifier|final
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
name|distinctPositions
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
name|disjunctLists
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|disjuncts
init|=
name|disjunctLists
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|disjuncts
operator|!=
literal|null
condition|)
block|{
name|clauses
index|[
name|position
operator|++
index|]
operator|=
operator|new
name|SpanOrQuery
argument_list|(
name|disjuncts
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|disjuncts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|++
name|positionGaps
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|slop
init|=
name|mpq
operator|.
name|getSlop
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|inorder
init|=
operator|(
name|slop
operator|==
literal|0
operator|)
decl_stmt|;
name|SpanNearQuery
name|sp
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
name|slop
operator|+
name|positionGaps
argument_list|,
name|inorder
argument_list|)
decl_stmt|;
name|sp
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|getPayloads
argument_list|(
name|payloads
argument_list|,
name|sp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPayloads
specifier|private
name|void
name|getPayloads
parameter_list|(
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloads
parameter_list|,
name|SpanQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
init|=
operator|new
name|HashMap
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|query
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|termContexts
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|TermContext
operator|.
name|build
argument_list|(
name|context
argument_list|,
name|term
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomicReaderContext
range|:
name|leaves
control|)
block|{
specifier|final
name|Spans
name|spans
init|=
name|query
operator|.
name|getSpans
argument_list|(
name|atomicReaderContext
argument_list|,
name|atomicReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|termContexts
argument_list|)
decl_stmt|;
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
operator|==
literal|true
condition|)
block|{
if|if
condition|(
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payload
init|=
name|spans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|bytes
range|:
name|payload
control|)
block|{
name|payloads
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

