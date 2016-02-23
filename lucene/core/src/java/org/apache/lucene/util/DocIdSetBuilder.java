begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
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
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/**  * A builder of {@link DocIdSet}s.  At first it uses a sparse structure to gather  * documents, and then upgrades to a non-sparse bit set once enough hits match.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|DocIdSetBuilder
specifier|public
specifier|final
class|class
name|DocIdSetBuilder
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|threshold
specifier|private
specifier|final
name|int
name|threshold
decl_stmt|;
DECL|field|buffer
specifier|private
name|int
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
decl_stmt|;
DECL|field|bitSet
specifier|private
name|BitSet
name|bitSet
decl_stmt|;
comment|/**    * Create a builder that can contain doc IDs between {@code 0} and {@code maxDoc}.    */
DECL|method|DocIdSetBuilder
specifier|public
name|DocIdSetBuilder
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
comment|// For ridiculously small sets, we'll just use a sorted int[]
comment|// maxDoc>>> 7 is a good value if you want to save memory, lower values
comment|// such as maxDoc>>> 11 should provide faster building but at the expense
comment|// of using a full bitset even for quite sparse data
name|this
operator|.
name|threshold
operator|=
name|maxDoc
operator|>>>
literal|7
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|int
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bitSet
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|upgradeToBitSet
specifier|private
name|void
name|upgradeToBitSet
parameter_list|()
block|{
assert|assert
name|bitSet
operator|==
literal|null
assert|;
name|bitSet
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
name|bufferSize
condition|;
operator|++
name|i
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Grows the buffer to at least minSize, but never larger than threshold. */
DECL|method|growBuffer
specifier|private
name|void
name|growBuffer
parameter_list|(
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|<
name|threshold
assert|;
if|if
condition|(
name|buffer
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
name|int
name|nextSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|threshold
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|int
index|[]
name|newBuffer
init|=
operator|new
name|int
index|[
name|nextSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
block|}
comment|/**    * Add the content of the provided {@link DocIdSetIterator} to this builder.    * NOTE: if you need to build a {@link DocIdSet} out of a single    * {@link DocIdSetIterator}, you should rather use {@link RoaringDocIdSet.Builder}.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|grow
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|iter
operator|.
name|cost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|bitSet
operator|!=
literal|null
condition|)
block|{
name|bitSet
operator|.
name|or
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
literal|true
condition|)
block|{
assert|assert
name|buffer
operator|.
name|length
operator|<=
name|threshold
assert|;
specifier|final
name|int
name|end
init|=
name|buffer
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bufferSize
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|bufferSize
operator|=
name|i
expr_stmt|;
return|return;
block|}
name|buffer
index|[
name|bufferSize
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
name|bufferSize
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|bufferSize
operator|+
literal|1
operator|>=
name|threshold
condition|)
block|{
break|break;
block|}
name|growBuffer
argument_list|(
name|bufferSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|upgradeToBitSet
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Reserve space so that this builder can hold {@code numDocs} MORE documents.    */
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
if|if
condition|(
name|bitSet
operator|==
literal|null
condition|)
block|{
specifier|final
name|long
name|newLength
init|=
name|bufferSize
operator|+
name|numDocs
decl_stmt|;
if|if
condition|(
name|newLength
operator|<
name|threshold
condition|)
block|{
name|growBuffer
argument_list|(
operator|(
name|int
operator|)
name|newLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|upgradeToBitSet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add a document to this builder.    * NOTE: doc IDs do not need to be provided in order.    * NOTE: if you plan on adding several docs at once, look into using    * {@link #grow(int)} to reserve space.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|bitSet
operator|!=
literal|null
condition|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|bufferSize
operator|+
literal|1
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|bufferSize
operator|+
literal|1
operator|>=
name|threshold
condition|)
block|{
name|upgradeToBitSet
argument_list|()
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return;
block|}
name|growBuffer
argument_list|(
name|bufferSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|buffer
index|[
name|bufferSize
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
block|}
DECL|method|dedup
specifier|private
specifier|static
name|int
name|dedup
parameter_list|(
name|int
index|[]
name|arr
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|l
init|=
literal|1
decl_stmt|;
name|int
name|previous
init|=
name|arr
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|value
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|value
operator|>=
name|previous
assert|;
if|if
condition|(
name|value
operator|!=
name|previous
condition|)
block|{
name|arr
index|[
name|l
operator|++
index|]
operator|=
name|value
expr_stmt|;
name|previous
operator|=
name|value
expr_stmt|;
block|}
block|}
return|return
name|l
return|;
block|}
comment|/**    * Build a {@link DocIdSet} from the accumulated doc IDs.    */
DECL|method|build
specifier|public
name|DocIdSet
name|build
parameter_list|()
block|{
return|return
name|build
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Expert: build a {@link DocIdSet} with a hint on the cost that the resulting    * {@link DocIdSet} would have.    */
DECL|method|build
specifier|public
name|DocIdSet
name|build
parameter_list|(
name|long
name|costHint
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|bitSet
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|costHint
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|bitSet
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|bitSet
argument_list|,
name|costHint
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|LSBRadixSorter
name|sorter
init|=
operator|new
name|LSBRadixSorter
argument_list|()
decl_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
specifier|final
name|int
name|l
init|=
name|dedup
argument_list|(
name|buffer
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
assert|assert
name|l
operator|<=
name|bufferSize
assert|;
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|l
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buffer
index|[
name|l
index|]
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
return|return
operator|new
name|IntArrayDocIdSet
argument_list|(
name|buffer
argument_list|,
name|l
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bitSet
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

