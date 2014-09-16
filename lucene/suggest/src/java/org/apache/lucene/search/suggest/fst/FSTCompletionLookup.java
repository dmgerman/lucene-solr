begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
name|fst
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|InputIterator
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
name|Lookup
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
name|fst
operator|.
name|FSTCompletion
operator|.
name|Completion
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
name|tst
operator|.
name|TSTLookup
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
name|DataInput
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
name|DataOutput
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
name|Accountables
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
name|ArrayUtil
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
name|BytesRefBuilder
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
name|CharsRef
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
name|IOUtils
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
name|OfflineSorter
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
name|OfflineSorter
operator|.
name|SortInfo
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
name|RamUsageEstimator
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
name|UnicodeUtil
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
name|NoOutputs
import|;
end_import

begin_comment
comment|/**  * An adapter from {@link Lookup} API to {@link FSTCompletion}.  *   *<p>This adapter differs from {@link FSTCompletion} in that it attempts  * to discretize any "weights" as passed from in {@link InputIterator#weight()}  * to match the number of buckets. For the rationale for bucketing, see  * {@link FSTCompletion}.  *   *<p><b>Note:</b>Discretization requires an additional sorting pass.  *   *<p>The range of weights for bucketing/ discretization is determined   * by sorting the input by weight and then dividing into  * equal ranges. Then, scores within each range are assigned to that bucket.   *   *<p>Note that this means that even large differences in weights may be lost   * during automaton construction, but the overall distinction between "classes"  * of weights will be preserved regardless of the distribution of weights.   *   *<p>For fine-grained control over which weights are assigned to which buckets,  * use {@link FSTCompletion} directly or {@link TSTLookup}, for example.  *   * @see FSTCompletion  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FSTCompletionLookup
specifier|public
class|class
name|FSTCompletionLookup
extends|extends
name|Lookup
implements|implements
name|Accountable
block|{
comment|/**     * An invalid bucket count if we're creating an object    * of this class from an existing FST.    *     * @see #FSTCompletionLookup(FSTCompletion, boolean)    */
DECL|field|INVALID_BUCKETS_COUNT
specifier|private
specifier|static
name|int
name|INVALID_BUCKETS_COUNT
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Shared tail length for conflating in the created automaton. Setting this    * to larger values ({@link Integer#MAX_VALUE}) will create smaller (or minimal)     * automata at the cost of RAM for keeping nodes hash in the {@link FST}.     *      *<p>Empirical pick.    */
DECL|field|sharedTailLength
specifier|private
specifier|final
specifier|static
name|int
name|sharedTailLength
init|=
literal|5
decl_stmt|;
DECL|field|buckets
specifier|private
name|int
name|buckets
decl_stmt|;
DECL|field|exactMatchFirst
specifier|private
name|boolean
name|exactMatchFirst
decl_stmt|;
comment|/**    * Automaton used for completions with higher weights reordering.    */
DECL|field|higherWeightsCompletion
specifier|private
name|FSTCompletion
name|higherWeightsCompletion
decl_stmt|;
comment|/**    * Automaton used for normal completions.    */
DECL|field|normalCompletion
specifier|private
name|FSTCompletion
name|normalCompletion
decl_stmt|;
comment|/** Number of entries the lookup was built with */
DECL|field|count
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
comment|/**    * This constructor prepares for creating a suggested FST using the    * {@link #build(InputIterator)} method. The number of weight    * discretization buckets is set to {@link FSTCompletion#DEFAULT_BUCKETS} and    * exact matches are promoted to the top of the suggestions list.    */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|()
block|{
name|this
argument_list|(
name|FSTCompletion
operator|.
name|DEFAULT_BUCKETS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * This constructor prepares for creating a suggested FST using the    * {@link #build(InputIterator)} method.    *     * @param buckets    *          The number of weight discretization buckets (see    *          {@link FSTCompletion} for details).    *     * @param exactMatchFirst    *          If<code>true</code> exact matches are promoted to the top of the    *          suggestions list. Otherwise they appear in the order of    *          discretized weight and alphabetical within the bucket.    */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|(
name|int
name|buckets
parameter_list|,
name|boolean
name|exactMatchFirst
parameter_list|)
block|{
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|exactMatchFirst
operator|=
name|exactMatchFirst
expr_stmt|;
block|}
comment|/**    * This constructor takes a pre-built automaton.    *     *  @param completion     *          An instance of {@link FSTCompletion}.    *  @param exactMatchFirst    *          If<code>true</code> exact matches are promoted to the top of the    *          suggestions list. Otherwise they appear in the order of    *          discretized weight and alphabetical within the bucket.    */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|(
name|FSTCompletion
name|completion
parameter_list|,
name|boolean
name|exactMatchFirst
parameter_list|)
block|{
name|this
argument_list|(
name|INVALID_BUCKETS_COUNT
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|this
operator|.
name|higherWeightsCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|true
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|InputIterator
name|iterator
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iterator
operator|.
name|hasPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support payloads"
argument_list|)
throw|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasContexts
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support contexts"
argument_list|)
throw|;
block|}
name|Path
name|tempInput
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
name|FSTCompletionLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|".input"
argument_list|)
decl_stmt|;
name|Path
name|tempSorted
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
name|FSTCompletionLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|".sorted"
argument_list|)
decl_stmt|;
name|OfflineSorter
operator|.
name|ByteSequencesWriter
name|writer
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|tempInput
argument_list|)
decl_stmt|;
name|OfflineSorter
operator|.
name|ByteSequencesReader
name|reader
init|=
literal|null
decl_stmt|;
name|ExternalRefSorter
name|sorter
init|=
literal|null
decl_stmt|;
comment|// Push floats up front before sequences to sort them. For now, assume they are non-negative.
comment|// If negative floats are allowed some trickery needs to be done to find their byte order.
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0
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
name|BytesRef
name|spare
decl_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|spare
operator|.
name|length
operator|+
literal|4
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|spare
operator|.
name|length
operator|+
literal|4
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|encodeWeight
argument_list|(
name|iterator
operator|.
name|weight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
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
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We don't know the distribution of scores and we need to bucket them, so we'll sort
comment|// and divide into equal buckets.
name|SortInfo
name|info
init|=
operator|new
name|OfflineSorter
argument_list|()
operator|.
name|sort
argument_list|(
name|tempInput
argument_list|,
name|tempSorted
argument_list|)
decl_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|tempInput
argument_list|)
expr_stmt|;
name|FSTCompletionBuilder
name|builder
init|=
operator|new
name|FSTCompletionBuilder
argument_list|(
name|buckets
argument_list|,
name|sorter
operator|=
operator|new
name|ExternalRefSorter
argument_list|(
operator|new
name|OfflineSorter
argument_list|()
argument_list|)
argument_list|,
name|sharedTailLength
argument_list|)
decl_stmt|;
specifier|final
name|int
name|inputLines
init|=
name|info
operator|.
name|lines
decl_stmt|;
name|reader
operator|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesReader
argument_list|(
name|tempSorted
argument_list|)
expr_stmt|;
name|long
name|line
init|=
literal|0
decl_stmt|;
name|int
name|previousBucket
init|=
literal|0
decl_stmt|;
name|int
name|previousScore
init|=
literal|0
decl_stmt|;
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
name|BytesRefBuilder
name|tmp1
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|BytesRef
name|tmp2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|read
argument_list|(
name|tmp1
argument_list|)
condition|)
block|{
name|input
operator|.
name|reset
argument_list|(
name|tmp1
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|currentScore
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|int
name|bucket
decl_stmt|;
if|if
condition|(
name|line
operator|>
literal|0
operator|&&
name|currentScore
operator|==
name|previousScore
condition|)
block|{
name|bucket
operator|=
name|previousBucket
expr_stmt|;
block|}
else|else
block|{
name|bucket
operator|=
call|(
name|int
call|)
argument_list|(
name|line
operator|*
name|buckets
operator|/
name|inputLines
argument_list|)
expr_stmt|;
block|}
name|previousScore
operator|=
name|currentScore
expr_stmt|;
name|previousBucket
operator|=
name|bucket
expr_stmt|;
comment|// Only append the input, discard the weight.
name|tmp2
operator|.
name|bytes
operator|=
name|tmp1
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|tmp2
operator|.
name|offset
operator|=
name|input
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|tmp2
operator|.
name|length
operator|=
name|tmp1
operator|.
name|length
argument_list|()
operator|-
name|input
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|tmp2
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|line
operator|++
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|// The two FSTCompletions share the same automaton.
name|this
operator|.
name|higherWeightsCompletion
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|,
name|sorter
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|tempSorted
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|tempInput
argument_list|,
name|tempSorted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** weight -> cost */
DECL|method|encodeWeight
specifier|private
specifier|static
name|int
name|encodeWeight
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
argument_list|<
name|Integer
operator|.
name|MIN_VALUE
operator|||
name|value
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
name|value
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|boolean
name|higherWeightsFirst
parameter_list|,
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|contexts
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support contexts"
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|Completion
argument_list|>
name|completions
decl_stmt|;
if|if
condition|(
name|higherWeightsFirst
condition|)
block|{
name|completions
operator|=
name|higherWeightsCompletion
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|completions
operator|=
name|normalCompletion
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|completions
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Completion
name|c
range|:
name|completions
control|)
block|{
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|c
operator|.
name|utf8
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|,
name|c
operator|.
name|bucket
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**    * Returns the bucket (weight) as a Long for the provided key if it exists,    * otherwise null if it does not.    */
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
specifier|final
name|int
name|bucket
init|=
name|normalCompletion
operator|.
name|getBucket
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|bucket
operator|==
operator|-
literal|1
condition|?
literal|null
else|:
name|Long
operator|.
name|valueOf
argument_list|(
name|bucket
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
specifier|synchronized
name|boolean
name|store
parameter_list|(
name|DataOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|normalCompletion
operator|==
literal|null
operator|||
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|.
name|save
argument_list|(
name|output
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
specifier|synchronized
name|boolean
name|load
parameter_list|(
name|DataInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|=
name|input
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|higherWeightsCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
operator|new
name|FST
argument_list|<>
argument_list|(
name|input
argument_list|,
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|mem
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|this
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|normalCompletion
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|higherWeightsCompletion
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalCompletion
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|higherWeightsCompletion
operator|!=
literal|null
operator|&&
operator|(
name|normalCompletion
operator|==
literal|null
operator|||
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|!=
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
operator|)
condition|)
block|{
comment|// the fst should be shared between the 2 completion instances, don't count it twice
name|mem
operator|+=
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|mem
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|normalCompletion
operator|!=
literal|null
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"fst"
argument_list|,
name|normalCompletion
operator|.
name|getFST
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|higherWeightsCompletion
operator|!=
literal|null
operator|&&
operator|(
name|normalCompletion
operator|==
literal|null
operator|||
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|!=
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
operator|)
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"higher weights fst"
argument_list|,
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resources
return|;
block|}
annotation|@
name|Override
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

