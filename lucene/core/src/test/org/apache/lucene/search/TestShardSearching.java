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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|CompositeReader
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
name|MultiFields
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
name|MultiReader
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
name|index
operator|.
name|TermsEnum
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
operator|.
name|SuppressCodecs
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
name|_TestUtil
import|;
end_import

begin_comment
comment|// TODO
end_comment

begin_comment
comment|//   - other queries besides PrefixQuery& TermQuery (but:
end_comment

begin_comment
comment|//     FuzzyQ will be problematic... the top N terms it
end_comment

begin_comment
comment|//     takes means results will differ)
end_comment

begin_comment
comment|//   - NRQ/F
end_comment

begin_comment
comment|//   - BQ, negated clauses, negated prefix clauses
end_comment

begin_comment
comment|//   - test pulling docs in 2nd round trip...
end_comment

begin_comment
comment|//   - filter too
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
DECL|class|TestShardSearching
specifier|public
class|class
name|TestShardSearching
extends|extends
name|ShardSearchingTestBase
block|{
DECL|class|PreviousSearchState
specifier|private
specifier|static
class|class
name|PreviousSearchState
block|{
DECL|field|searchTimeNanos
specifier|public
specifier|final
name|long
name|searchTimeNanos
decl_stmt|;
DECL|field|versions
specifier|public
specifier|final
name|long
index|[]
name|versions
decl_stmt|;
DECL|field|searchAfterLocal
specifier|public
specifier|final
name|ScoreDoc
name|searchAfterLocal
decl_stmt|;
DECL|field|searchAfterShard
specifier|public
specifier|final
name|ScoreDoc
name|searchAfterShard
decl_stmt|;
DECL|field|sort
specifier|public
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|query
specifier|public
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|numHitsPaged
specifier|public
specifier|final
name|int
name|numHitsPaged
decl_stmt|;
DECL|method|PreviousSearchState
specifier|public
name|PreviousSearchState
parameter_list|(
name|Query
name|query
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|ScoreDoc
name|searchAfterLocal
parameter_list|,
name|ScoreDoc
name|searchAfterShard
parameter_list|,
name|long
index|[]
name|versions
parameter_list|,
name|int
name|numHitsPaged
parameter_list|)
block|{
name|this
operator|.
name|versions
operator|=
name|versions
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|searchAfterLocal
operator|=
name|searchAfterLocal
expr_stmt|;
name|this
operator|.
name|searchAfterShard
operator|=
name|searchAfterShard
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|numHitsPaged
operator|=
name|numHitsPaged
expr_stmt|;
name|searchTimeNanos
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numNodes
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|double
name|runTimeSec
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|int
name|minDocsToMakeTerms
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxSearcherAgeSeconds
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: numNodes="
operator|+
name|numNodes
operator|+
literal|" runTimeSec="
operator|+
name|runTimeSec
operator|+
literal|" maxSearcherAgeSeconds="
operator|+
name|maxSearcherAgeSeconds
argument_list|)
expr_stmt|;
block|}
name|start
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestShardSearching"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|numNodes
argument_list|,
name|runTimeSec
argument_list|,
name|maxSearcherAgeSeconds
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PreviousSearchState
argument_list|>
name|priorSearches
init|=
operator|new
name|ArrayList
argument_list|<
name|PreviousSearchState
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|endTimeNanos
condition|)
block|{
specifier|final
name|boolean
name|doFollowon
init|=
name|priorSearches
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
operator|==
literal|1
decl_stmt|;
comment|// Pick a random node; we will run the query on this node:
specifier|final
name|int
name|myNodeID
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
operator|.
name|ShardIndexSearcher
name|localShardSearcher
decl_stmt|;
specifier|final
name|PreviousSearchState
name|prevSearchState
decl_stmt|;
if|if
condition|(
name|doFollowon
condition|)
block|{
comment|// Pretend user issued a followon query:
name|prevSearchState
operator|=
name|priorSearches
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|priorSearches
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: follow-on query age="
operator|+
operator|(
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|prevSearchState
operator|.
name|searchTimeNanos
operator|)
operator|/
literal|1000000000.0
operator|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|localShardSearcher
operator|=
name|nodes
index|[
name|myNodeID
index|]
operator|.
name|acquire
argument_list|(
name|prevSearchState
operator|.
name|versions
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearcherExpiredException
name|see
parameter_list|)
block|{
comment|// Expected, sometimes; in a "real" app we would
comment|// either forward this error to the user ("too
comment|// much time has passed; please re-run your
comment|// search") or sneakily just switch to newest
comment|// searcher w/o telling them...
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  searcher expired during local shard searcher init: "
operator|+
name|see
argument_list|)
expr_stmt|;
block|}
name|priorSearches
operator|.
name|remove
argument_list|(
name|prevSearchState
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: fresh query"
argument_list|)
expr_stmt|;
block|}
comment|// Do fresh query:
name|localShardSearcher
operator|=
name|nodes
index|[
name|myNodeID
index|]
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|prevSearchState
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|IndexReader
index|[]
name|subs
init|=
operator|new
name|IndexReader
index|[
name|numNodes
index|]
decl_stmt|;
name|PreviousSearchState
name|searchState
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Mock: now make a single reader (MultiReader) from all node
comment|// searchers.  In a real shard env you can't do this... we
comment|// do it to confirm results from the shard searcher
comment|// are correct:
name|int
name|docCount
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|nodeID
init|=
literal|0
init|;
name|nodeID
operator|<
name|numNodes
condition|;
name|nodeID
operator|++
control|)
block|{
specifier|final
name|long
name|subVersion
init|=
name|localShardSearcher
operator|.
name|nodeVersions
index|[
name|nodeID
index|]
decl_stmt|;
specifier|final
name|IndexSearcher
name|sub
init|=
name|nodes
index|[
name|nodeID
index|]
operator|.
name|searchers
operator|.
name|acquire
argument_list|(
name|subVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|nodeID
operator|--
expr_stmt|;
while|while
condition|(
name|nodeID
operator|>=
literal|0
condition|)
block|{
name|subs
index|[
name|nodeID
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|subs
index|[
name|nodeID
index|]
operator|=
literal|null
expr_stmt|;
name|nodeID
operator|--
expr_stmt|;
block|}
throw|throw
operator|new
name|SearcherExpiredException
argument_list|(
literal|"nodeID="
operator|+
name|nodeID
operator|+
literal|" version="
operator|+
name|subVersion
argument_list|)
throw|;
block|}
name|subs
index|[
name|nodeID
index|]
operator|=
name|sub
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|docCount
operator|+=
name|subs
index|[
name|nodeID
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SearcherExpiredException
name|see
parameter_list|)
block|{
comment|// Expected
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  searcher expired during mock reader init: "
operator|+
name|see
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
specifier|final
name|IndexReader
name|mockReader
init|=
operator|new
name|MultiReader
argument_list|(
name|subs
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|mockSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|mockReader
argument_list|)
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|Sort
name|sort
decl_stmt|;
if|if
condition|(
name|prevSearchState
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|prevSearchState
operator|.
name|query
expr_stmt|;
name|sort
operator|=
name|prevSearchState
operator|.
name|sort
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|terms
operator|==
literal|null
operator|&&
name|docCount
operator|>
name|minDocsToMakeTerms
condition|)
block|{
comment|// TODO: try to "focus" on high freq terms sometimes too
comment|// TODO: maybe also periodically reset the terms...?
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|mockReader
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: init terms: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" terms"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|terms
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  maxDoc="
operator|+
name|mockReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|t
init|=
name|terms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|length
argument_list|()
operator|<=
literal|1
condition|)
block|{
name|prefix
operator|=
name|t
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
name|t
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|sort
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: sort by more than 1 field
specifier|final
name|int
name|what
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|what
operator|==
literal|0
condition|)
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|what
operator|==
literal|1
condition|)
block|{
comment|// TODO: this sort doesn't merge
comment|// correctly... it's tricky because you
comment|// could have> 2.1B docs across all shards:
comment|//sort = new Sort(SortField.FIELD_DOC);
name|sort
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|what
operator|==
literal|2
condition|)
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
index|[]
block|{
operator|new
name|SortField
argument_list|(
literal|"docid"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
index|[]
block|{
operator|new
name|SortField
argument_list|(
literal|"title"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|query
operator|=
literal|null
expr_stmt|;
name|sort
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|searchState
operator|=
name|assertSame
argument_list|(
name|mockSearcher
argument_list|,
name|localShardSearcher
argument_list|,
name|query
argument_list|,
name|sort
argument_list|,
name|prevSearchState
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SearcherExpiredException
name|see
parameter_list|)
block|{
comment|// Expected; in a "real" app we would
comment|// either forward this error to the user ("too
comment|// much time has passed; please re-run your
comment|// search") or sneakily just switch to newest
comment|// searcher w/o telling them...
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  searcher expired during search: "
operator|+
name|see
argument_list|)
expr_stmt|;
name|see
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
assert|assert
name|prevSearchState
operator|!=
literal|null
assert|;
name|priorSearches
operator|.
name|remove
argument_list|(
name|prevSearchState
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|nodes
index|[
name|myNodeID
index|]
operator|.
name|release
argument_list|(
name|localShardSearcher
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|sub
range|:
name|subs
control|)
block|{
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|searchState
operator|!=
literal|null
operator|&&
name|searchState
operator|.
name|searchAfterLocal
operator|!=
literal|null
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
name|priorSearches
operator|.
name|add
argument_list|(
name|searchState
argument_list|)
expr_stmt|;
if|if
condition|(
name|priorSearches
operator|.
name|size
argument_list|()
operator|>
literal|200
condition|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|priorSearches
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|priorSearches
operator|.
name|subList
argument_list|(
literal|100
argument_list|,
name|priorSearches
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSame
specifier|private
name|PreviousSearchState
name|assertSame
parameter_list|(
name|IndexSearcher
name|mockSearcher
parameter_list|,
name|NodeState
operator|.
name|ShardIndexSearcher
name|shardSearcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|PreviousSearchState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numHits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|searchAfterLocal
operator|==
literal|null
condition|)
block|{
comment|// In addition to what we last searched:
name|numHits
operator|+=
name|state
operator|.
name|numHitsPaged
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: query="
operator|+
name|q
operator|+
literal|" sort="
operator|+
name|sort
operator|+
literal|" numHits="
operator|+
name|numHits
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  prev: searchAfterLocal="
operator|+
name|state
operator|.
name|searchAfterLocal
operator|+
literal|" searchAfterShard="
operator|+
name|state
operator|.
name|searchAfterShard
operator|+
literal|" numHitsPaged="
operator|+
name|state
operator|.
name|numHitsPaged
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Single (mock local) searcher:
specifier|final
name|TopDocs
name|hits
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|searchAfterLocal
operator|!=
literal|null
condition|)
block|{
name|hits
operator|=
name|mockSearcher
operator|.
name|searchAfter
argument_list|(
name|state
operator|.
name|searchAfterLocal
argument_list|,
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
name|mockSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|hits
operator|=
name|mockSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
comment|// Shard searcher
specifier|final
name|TopDocs
name|shardHits
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|searchAfterShard
operator|!=
literal|null
condition|)
block|{
name|shardHits
operator|=
name|shardSearcher
operator|.
name|searchAfter
argument_list|(
name|state
operator|.
name|searchAfterShard
argument_list|,
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|shardHits
operator|=
name|shardSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|shardHits
operator|=
name|shardSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numNodes
init|=
name|shardSearcher
operator|.
name|nodeVersions
operator|.
name|length
decl_stmt|;
name|int
index|[]
name|base
init|=
operator|new
name|int
index|[
name|numNodes
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|subs
init|=
operator|(
operator|(
name|CompositeReader
operator|)
name|mockSearcher
operator|.
name|getIndexReader
argument_list|()
operator|)
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numNodes
argument_list|,
name|subs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|nodeID
init|=
literal|0
init|;
name|nodeID
operator|<
name|numNodes
condition|;
name|nodeID
operator|++
control|)
block|{
name|base
index|[
name|nodeID
index|]
operator|=
name|docCount
expr_stmt|;
name|docCount
operator|+=
name|subs
operator|.
name|get
argument_list|(
name|nodeID
argument_list|)
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|/*       for(int shardID=0;shardID<shardSearchers.length;shardID++) {         System.out.println("  shard=" + shardID + " maxDoc=" + shardSearchers[shardID].searcher.getIndexReader().maxDoc());       }       */
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  single searcher: "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" totalHits maxScore="
operator|+
name|hits
operator|.
name|getMaxScore
argument_list|()
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
specifier|final
name|ScoreDoc
name|sd
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" score="
operator|+
name|sd
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  shard searcher: "
operator|+
name|shardHits
operator|.
name|totalHits
operator|+
literal|" totalHits maxScore="
operator|+
name|shardHits
operator|.
name|getMaxScore
argument_list|()
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
name|shardHits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
name|sd
init|=
name|shardHits
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" (rebased: "
operator|+
operator|(
name|sd
operator|.
name|doc
operator|+
name|base
index|[
name|sd
operator|.
name|shardIndex
index|]
operator|)
operator|+
literal|") score="
operator|+
name|sd
operator|.
name|score
operator|+
literal|" shard="
operator|+
name|sd
operator|.
name|shardIndex
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numHitsPaged
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|searchAfterLocal
operator|!=
literal|null
condition|)
block|{
name|numHitsPaged
operator|=
name|hits
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|numHitsPaged
operator|+=
name|state
operator|.
name|numHitsPaged
expr_stmt|;
block|}
block|}
else|else
block|{
name|numHitsPaged
operator|=
name|hits
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
block|}
specifier|final
name|boolean
name|moreHits
decl_stmt|;
specifier|final
name|ScoreDoc
name|bottomHit
decl_stmt|;
specifier|final
name|ScoreDoc
name|bottomHitShards
decl_stmt|;
if|if
condition|(
name|numHitsPaged
operator|<
name|hits
operator|.
name|totalHits
condition|)
block|{
comment|// More hits to page through
name|moreHits
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|bottomHit
operator|=
name|hits
operator|.
name|scoreDocs
index|[
name|hits
operator|.
name|scoreDocs
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
specifier|final
name|ScoreDoc
name|sd
init|=
name|shardHits
operator|.
name|scoreDocs
index|[
name|shardHits
operator|.
name|scoreDocs
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
comment|// Must copy because below we rebase:
name|bottomHitShards
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|sd
operator|.
name|doc
argument_list|,
name|sd
operator|.
name|score
argument_list|,
name|sd
operator|.
name|shardIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  save bottomHit="
operator|+
name|bottomHit
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|bottomHit
operator|=
literal|null
expr_stmt|;
name|bottomHitShards
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|hits
operator|.
name|totalHits
argument_list|,
name|numHitsPaged
argument_list|)
expr_stmt|;
name|bottomHit
operator|=
literal|null
expr_stmt|;
name|bottomHitShards
operator|=
literal|null
expr_stmt|;
name|moreHits
operator|=
literal|false
expr_stmt|;
block|}
comment|// Must rebase so assertEquals passes:
for|for
control|(
name|int
name|hitID
init|=
literal|0
init|;
name|hitID
operator|<
name|shardHits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitID
operator|++
control|)
block|{
specifier|final
name|ScoreDoc
name|sd
init|=
name|shardHits
operator|.
name|scoreDocs
index|[
name|hitID
index|]
decl_stmt|;
name|sd
operator|.
name|doc
operator|+=
name|base
index|[
name|sd
operator|.
name|shardIndex
index|]
expr_stmt|;
block|}
name|_TestUtil
operator|.
name|assertEquals
argument_list|(
name|hits
argument_list|,
name|shardHits
argument_list|)
expr_stmt|;
if|if
condition|(
name|moreHits
condition|)
block|{
comment|// Return a continuation:
return|return
operator|new
name|PreviousSearchState
argument_list|(
name|q
argument_list|,
name|sort
argument_list|,
name|bottomHit
argument_list|,
name|bottomHitShards
argument_list|,
name|shardSearcher
operator|.
name|nodeVersions
argument_list|,
name|numHitsPaged
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

