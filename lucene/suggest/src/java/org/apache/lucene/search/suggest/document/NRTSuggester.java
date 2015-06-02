begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|Collection
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
name|Comparator
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|FSTUtil
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|IndexInput
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
name|Accountable
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
name|CharsRefBuilder
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
name|fst
operator|.
name|ByteSequenceOutputs
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PairOutputs
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
name|fst
operator|.
name|PairOutputs
operator|.
name|Pair
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|NRTSuggester
operator|.
name|PayLoadProcessor
operator|.
name|parseDocID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|NRTSuggester
operator|.
name|PayLoadProcessor
operator|.
name|parseSurfaceForm
import|;
end_import

begin_comment
comment|/**  *<p>  * NRTSuggester executes Top N search on a weighted FST specified by a {@link CompletionScorer}  *<p>  * See {@link #lookup(CompletionScorer, TopSuggestDocsCollector)} for more implementation  * details.  *<p>  * FST Format:  *<ul>  *<li>Input: analyzed forms of input terms</li>  *<li>Output: Pair&lt;Long, BytesRef&gt; containing weight, surface form and docID</li>  *</ul>  *<p>  * NOTE:  *<ul>  *<li>having too many deletions or using a very restrictive filter can make the search inadmissible due to  *     over-pruning of potential paths. See {@link CompletionScorer#accept(int)}</li>  *<li>when matched documents are arbitrarily filtered ({@link CompletionScorer#filtered} set to<code>true</code>,  *     it is assumed that the filter will roughly filter out half the number of documents that match  *     the provided automaton</li>  *<li>lookup performance will degrade as more accepted completions lead to filtered out documents</li>  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NRTSuggester
specifier|public
specifier|final
class|class
name|NRTSuggester
implements|implements
name|Accountable
block|{
comment|/**    * FST<Weight,Surface>:    * input is the analyzed form, with a null byte between terms    * and a {@link NRTSuggesterBuilder#END_BYTE} to denote the    * end of the input    * weight is a long    * surface is the original, unanalyzed form followed by the docID    */
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
decl_stmt|;
comment|/**    * Highest number of analyzed paths we saw for any single    * input surface form. This can be> 1, when index analyzer    * creates graphs or if multiple surface form(s) yields the    * same analyzed form    */
DECL|field|maxAnalyzedPathsPerOutput
specifier|private
specifier|final
name|int
name|maxAnalyzedPathsPerOutput
decl_stmt|;
comment|/**    * Separator used between surface form and its docID in the FST output    */
DECL|field|payloadSep
specifier|private
specifier|final
name|int
name|payloadSep
decl_stmt|;
comment|/**    * Maximum queue depth for TopNSearcher    *    * NOTE: value should be<= Integer.MAX_VALUE    */
DECL|field|MAX_TOP_N_QUEUE_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|MAX_TOP_N_QUEUE_SIZE
init|=
literal|5000
decl_stmt|;
DECL|method|NRTSuggester
specifier|private
name|NRTSuggester
parameter_list|(
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
parameter_list|,
name|int
name|maxAnalyzedPathsPerOutput
parameter_list|,
name|int
name|payloadSep
parameter_list|)
block|{
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|this
operator|.
name|maxAnalyzedPathsPerOutput
operator|=
name|maxAnalyzedPathsPerOutput
expr_stmt|;
name|this
operator|.
name|payloadSep
operator|=
name|payloadSep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|fst
operator|==
literal|null
condition|?
literal|0
else|:
name|fst
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**    * Collects at most {@link TopSuggestDocsCollector#getCountToCollect()} completions that    * match the provided {@link CompletionScorer}.    *<p>    * The {@link CompletionScorer#automaton} is intersected with the {@link #fst}.    * {@link CompletionScorer#weight} is used to compute boosts and/or extract context    * for each matched partial paths. A top N search is executed on {@link #fst} seeded with    * the matched partial paths. Upon reaching a completed path, {@link CompletionScorer#accept(int)}    * and {@link CompletionScorer#score(float, float)} is used on the document id, index weight    * and query boost to filter and score the entry, before being collected via    * {@link TopSuggestDocsCollector#collect(int, CharSequence, CharSequence, float)}    */
DECL|method|lookup
specifier|public
name|void
name|lookup
parameter_list|(
specifier|final
name|CompletionScorer
name|scorer
parameter_list|,
specifier|final
name|TopSuggestDocsCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|double
name|liveDocsRatio
init|=
name|calculateLiveDocRatio
argument_list|(
name|scorer
operator|.
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|,
name|scorer
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|liveDocsRatio
operator|==
operator|-
literal|1
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|FSTUtil
operator|.
name|Path
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|prefixPaths
init|=
name|FSTUtil
operator|.
name|intersectPrefixPaths
argument_list|(
name|scorer
operator|.
name|automaton
argument_list|,
name|fst
argument_list|)
decl_stmt|;
specifier|final
name|int
name|queueSize
init|=
name|getMaxTopNSearcherQueueSize
argument_list|(
name|collector
operator|.
name|getCountToCollect
argument_list|()
operator|*
name|prefixPaths
operator|.
name|size
argument_list|()
argument_list|,
name|scorer
operator|.
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|,
name|liveDocsRatio
argument_list|,
name|scorer
operator|.
name|filtered
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|comparator
init|=
name|getComparator
argument_list|()
decl_stmt|;
name|Util
operator|.
name|TopNSearcher
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|searcher
init|=
operator|new
name|Util
operator|.
name|TopNSearcher
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|(
name|fst
argument_list|,
name|collector
operator|.
name|getCountToCollect
argument_list|()
argument_list|,
name|queueSize
argument_list|,
name|comparator
argument_list|,
operator|new
name|ScoringPathComparator
argument_list|(
name|scorer
argument_list|)
argument_list|)
block|{
specifier|private
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|acceptResult
parameter_list|(
name|Util
operator|.
name|FSTPath
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|path
parameter_list|)
block|{
name|int
name|payloadSepIndex
init|=
name|parseSurfaceForm
argument_list|(
name|path
operator|.
name|cost
operator|.
name|output2
argument_list|,
name|payloadSep
argument_list|,
name|spare
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
name|parseDocID
argument_list|(
name|path
operator|.
name|cost
operator|.
name|output2
argument_list|,
name|payloadSepIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|scorer
operator|.
name|accept
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|(
name|decode
argument_list|(
name|path
operator|.
name|cost
operator|.
name|output1
argument_list|)
argument_list|,
name|path
operator|.
name|boost
argument_list|)
decl_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|docID
argument_list|,
name|spare
operator|.
name|toCharsRef
argument_list|()
argument_list|,
name|path
operator|.
name|context
argument_list|,
name|score
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
for|for
control|(
name|FSTUtil
operator|.
name|Path
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|path
range|:
name|prefixPaths
control|)
block|{
name|scorer
operator|.
name|weight
operator|.
name|setNextMatch
argument_list|(
name|path
operator|.
name|input
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|addStartPaths
argument_list|(
name|path
operator|.
name|fstNode
argument_list|,
name|path
operator|.
name|output
argument_list|,
literal|false
argument_list|,
name|path
operator|.
name|input
argument_list|,
name|scorer
operator|.
name|weight
operator|.
name|boost
argument_list|()
argument_list|,
name|scorer
operator|.
name|weight
operator|.
name|context
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// hits are also returned by search()
comment|// we do not use it, instead collect at acceptResult
name|searcher
operator|.
name|search
argument_list|()
expr_stmt|;
comment|// search admissibility is not guaranteed
comment|// see comment on getMaxTopNSearcherQueueSize
comment|// assert  search.isComplete;
block|}
comment|/**    * Compares partial completion paths using {@link CompletionScorer#score(float, float)},    * breaks ties comparing path inputs    */
DECL|class|ScoringPathComparator
specifier|private
specifier|static
class|class
name|ScoringPathComparator
implements|implements
name|Comparator
argument_list|<
name|Util
operator|.
name|FSTPath
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
block|{
DECL|field|scorer
specifier|private
specifier|final
name|CompletionScorer
name|scorer
decl_stmt|;
DECL|method|ScoringPathComparator
specifier|public
name|ScoringPathComparator
parameter_list|(
name|CompletionScorer
name|scorer
parameter_list|)
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
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Util
operator|.
name|FSTPath
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|first
parameter_list|,
name|Util
operator|.
name|FSTPath
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|second
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Float
operator|.
name|compare
argument_list|(
name|scorer
operator|.
name|score
argument_list|(
name|decode
argument_list|(
name|second
operator|.
name|cost
operator|.
name|output1
argument_list|)
argument_list|,
name|second
operator|.
name|boost
argument_list|)
argument_list|,
name|scorer
operator|.
name|score
argument_list|(
name|decode
argument_list|(
name|first
operator|.
name|cost
operator|.
name|output1
argument_list|)
argument_list|,
name|first
operator|.
name|boost
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|cmp
operator|!=
literal|0
operator|)
condition|?
name|cmp
else|:
name|first
operator|.
name|input
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|second
operator|.
name|input
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getComparator
specifier|private
specifier|static
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
operator|new
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
name|o1
parameter_list|,
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|output1
argument_list|,
name|o2
operator|.
name|output1
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Simple heuristics to try to avoid over-pruning potential suggestions by the    * TopNSearcher. Since suggestion entries can be rejected if they belong    * to a deleted document, the length of the TopNSearcher queue has to    * be increased by some factor, to account for the filtered out suggestions.    * This heuristic will try to make the searcher admissible, but the search    * can still lead to over-pruning    *<p>    * If a<code>filter</code> is applied, the queue size is increased by    * half the number of live documents.    *<p>    * The maximum queue size is {@link #MAX_TOP_N_QUEUE_SIZE}    */
DECL|method|getMaxTopNSearcherQueueSize
specifier|private
name|int
name|getMaxTopNSearcherQueueSize
parameter_list|(
name|int
name|topN
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|double
name|liveDocsRatio
parameter_list|,
name|boolean
name|filterEnabled
parameter_list|)
block|{
name|long
name|maxQueueSize
init|=
name|topN
operator|*
name|maxAnalyzedPathsPerOutput
decl_stmt|;
comment|// liveDocRatio can be at most 1.0 (if no docs were deleted)
assert|assert
name|liveDocsRatio
operator|<=
literal|1.0d
assert|;
name|maxQueueSize
operator|=
call|(
name|long
call|)
argument_list|(
name|maxQueueSize
operator|/
name|liveDocsRatio
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterEnabled
condition|)
block|{
name|maxQueueSize
operator|=
name|maxQueueSize
operator|+
operator|(
name|numDocs
operator|/
literal|2
operator|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|MAX_TOP_N_QUEUE_SIZE
argument_list|,
name|maxQueueSize
argument_list|)
return|;
block|}
DECL|method|calculateLiveDocRatio
specifier|private
specifier|static
name|double
name|calculateLiveDocRatio
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|maxDocs
parameter_list|)
block|{
return|return
operator|(
name|numDocs
operator|>
literal|0
operator|)
condition|?
operator|(
operator|(
name|double
operator|)
name|numDocs
operator|/
name|maxDocs
operator|)
else|:
operator|-
literal|1
return|;
block|}
comment|/**    * Loads a {@link NRTSuggester} from {@link org.apache.lucene.store.IndexInput}    */
DECL|method|load
specifier|public
specifier|static
name|NRTSuggester
name|load
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
init|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|input
argument_list|,
operator|new
name|PairOutputs
argument_list|<>
argument_list|(
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|,
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|/* read some meta info */
name|int
name|maxAnalyzedPathsPerOutput
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|/*      * Label used to denote the end of an input in the FST and      * the beginning of dedup bytes      */
name|int
name|endByte
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|payloadSep
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
return|return
operator|new
name|NRTSuggester
argument_list|(
name|fst
argument_list|,
name|maxAnalyzedPathsPerOutput
argument_list|,
name|payloadSep
argument_list|)
return|;
block|}
DECL|method|encode
specifier|static
name|long
name|encode
parameter_list|(
name|long
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
argument_list|<
literal|0
operator|||
name|input
argument_list|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot encode value: "
operator|+
name|input
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|input
return|;
block|}
DECL|method|decode
specifier|static
name|long
name|decode
parameter_list|(
name|long
name|output
parameter_list|)
block|{
assert|assert
name|output
operator|>=
literal|0
operator|&&
name|output
operator|<=
name|Integer
operator|.
name|MAX_VALUE
operator|:
literal|"decoded output: "
operator|+
name|output
operator|+
literal|" is not within 0 and Integer.MAX_VALUE"
assert|;
return|return
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|output
return|;
block|}
comment|/**    * Helper to encode/decode payload (surface + PAYLOAD_SEP + docID) output    */
DECL|class|PayLoadProcessor
specifier|static
specifier|final
class|class
name|PayLoadProcessor
block|{
DECL|field|MAX_DOC_ID_LEN_WITH_SEP
specifier|final
specifier|static
specifier|private
name|int
name|MAX_DOC_ID_LEN_WITH_SEP
init|=
literal|6
decl_stmt|;
comment|// vint takes at most 5 bytes
DECL|method|parseSurfaceForm
specifier|static
name|int
name|parseSurfaceForm
parameter_list|(
specifier|final
name|BytesRef
name|output
parameter_list|,
name|int
name|payloadSep
parameter_list|,
name|CharsRefBuilder
name|spare
parameter_list|)
block|{
name|int
name|surfaceFormLen
init|=
operator|-
literal|1
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|output
operator|.
name|bytes
index|[
name|output
operator|.
name|offset
operator|+
name|i
index|]
operator|==
name|payloadSep
condition|)
block|{
name|surfaceFormLen
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
assert|assert
name|surfaceFormLen
operator|!=
operator|-
literal|1
operator|:
literal|"no payloadSep found, unable to determine surface form"
assert|;
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|output
operator|.
name|bytes
argument_list|,
name|output
operator|.
name|offset
argument_list|,
name|surfaceFormLen
argument_list|)
expr_stmt|;
return|return
name|surfaceFormLen
return|;
block|}
DECL|method|parseDocID
specifier|static
name|int
name|parseDocID
parameter_list|(
specifier|final
name|BytesRef
name|output
parameter_list|,
name|int
name|payloadSepIndex
parameter_list|)
block|{
assert|assert
name|payloadSepIndex
operator|!=
operator|-
literal|1
operator|:
literal|"payload sep index can not be -1"
assert|;
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|output
operator|.
name|bytes
argument_list|,
name|payloadSepIndex
operator|+
name|output
operator|.
name|offset
operator|+
literal|1
argument_list|,
name|output
operator|.
name|length
operator|-
operator|(
name|payloadSepIndex
operator|+
name|output
operator|.
name|offset
operator|)
argument_list|)
decl_stmt|;
return|return
name|input
operator|.
name|readVInt
argument_list|()
return|;
block|}
DECL|method|make
specifier|static
name|BytesRef
name|make
parameter_list|(
specifier|final
name|BytesRef
name|surface
parameter_list|,
name|int
name|docID
parameter_list|,
name|int
name|payloadSep
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|surface
operator|.
name|length
operator|+
name|MAX_DOC_ID_LEN_WITH_SEP
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|output
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|surface
operator|.
name|bytes
argument_list|,
name|surface
operator|.
name|length
operator|-
name|surface
operator|.
name|offset
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|payloadSep
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|getPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

