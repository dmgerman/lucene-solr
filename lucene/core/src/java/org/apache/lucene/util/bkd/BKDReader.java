begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
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
name|Arrays
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
name|codecs
operator|.
name|CodecUtil
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
name|DimensionalValues
operator|.
name|IntersectVisitor
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
name|DimensionalValues
operator|.
name|Relation
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
name|NumericUtils
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
name|StringHelper
import|;
end_import

begin_comment
comment|/** Handles intersection of an multi-dimensional shape in byte[] space with a block KD-tree previously written with {@link BKDWriter}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|BKDReader
specifier|public
class|class
name|BKDReader
implements|implements
name|Accountable
block|{
comment|// Packed array of byte[] holding all split values in the full binary tree:
DECL|field|splitPackedValues
specifier|final
specifier|private
name|byte
index|[]
name|splitPackedValues
decl_stmt|;
DECL|field|leafBlockFPs
specifier|final
name|long
index|[]
name|leafBlockFPs
decl_stmt|;
DECL|field|leafNodeOffset
specifier|final
specifier|private
name|int
name|leafNodeOffset
decl_stmt|;
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|maxPointsInLeafNode
specifier|final
name|int
name|maxPointsInLeafNode
decl_stmt|;
DECL|field|packedBytesLength
specifier|protected
specifier|final
name|int
name|packedBytesLength
decl_stmt|;
comment|/** Caller must pre-seek the provided {@link IndexInput} to the index location that {@link BKDWriter#finish} returned */
DECL|method|BKDReader
specifier|public
name|BKDReader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|BKDWriter
operator|.
name|CODEC_NAME
argument_list|,
name|BKDWriter
operator|.
name|VERSION_START
argument_list|,
name|BKDWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|numDims
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxPointsInLeafNode
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|bytesPerDim
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|packedBytesLength
operator|=
name|numDims
operator|*
name|bytesPerDim
expr_stmt|;
comment|// Read index:
name|int
name|numLeaves
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|leafNodeOffset
operator|=
name|numLeaves
expr_stmt|;
name|splitPackedValues
operator|=
operator|new
name|byte
index|[
operator|(
literal|1
operator|+
name|bytesPerDim
operator|)
operator|*
name|numLeaves
index|]
expr_stmt|;
comment|// TODO: don't write split packed values[0]!
name|in
operator|.
name|readBytes
argument_list|(
name|splitPackedValues
argument_list|,
literal|0
argument_list|,
name|splitPackedValues
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Read the file pointers to the start of each leaf block:
name|long
index|[]
name|leafBlockFPs
init|=
operator|new
name|long
index|[
name|numLeaves
index|]
decl_stmt|;
name|long
name|lastFP
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
name|numLeaves
condition|;
name|i
operator|++
control|)
block|{
name|long
name|delta
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|leafBlockFPs
index|[
name|i
index|]
operator|=
name|lastFP
operator|+
name|delta
expr_stmt|;
name|lastFP
operator|+=
name|delta
expr_stmt|;
block|}
comment|// Possibly rotate the leaf block FPs, if the index not fully balanced binary tree (only happens
comment|// if it was created by BKDWriter.merge).  In this case the leaf nodes may straddle the two bottom
comment|// levels of the binary tree:
if|if
condition|(
name|numDims
operator|==
literal|1
operator|&&
name|numLeaves
operator|>
literal|1
condition|)
block|{
comment|//System.out.println("BKDR: numLeaves=" + numLeaves);
name|int
name|levelCount
init|=
literal|2
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  cycle levelCount=" + levelCount);
if|if
condition|(
name|numLeaves
operator|>=
name|levelCount
operator|&&
name|numLeaves
operator|<=
literal|2
operator|*
name|levelCount
condition|)
block|{
name|int
name|lastLevel
init|=
literal|2
operator|*
operator|(
name|numLeaves
operator|-
name|levelCount
operator|)
decl_stmt|;
assert|assert
name|lastLevel
operator|>=
literal|0
assert|;
comment|/*           System.out.println("BKDR: lastLevel=" + lastLevel + " vs " + levelCount);           System.out.println("FPs before:");           for(int i=0;i<leafBlockFPs.length;i++) {             System.out.println("  " + i + " " + leafBlockFPs[i]);           }           */
if|if
condition|(
name|lastLevel
operator|!=
literal|0
condition|)
block|{
comment|// Last level is partially filled, so we must rotate the leaf FPs to match.  We do this here, after loading
comment|// at read-time, so that we can still delta code them on disk at write:
comment|//System.out.println("BKDR: now rotate index");
name|long
index|[]
name|newLeafBlockFPs
init|=
operator|new
name|long
index|[
name|numLeaves
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|leafBlockFPs
argument_list|,
name|lastLevel
argument_list|,
name|newLeafBlockFPs
argument_list|,
literal|0
argument_list|,
name|leafBlockFPs
operator|.
name|length
operator|-
name|lastLevel
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|leafBlockFPs
argument_list|,
literal|0
argument_list|,
name|newLeafBlockFPs
argument_list|,
name|leafBlockFPs
operator|.
name|length
operator|-
name|lastLevel
argument_list|,
name|lastLevel
argument_list|)
expr_stmt|;
name|leafBlockFPs
operator|=
name|newLeafBlockFPs
expr_stmt|;
block|}
comment|/*           System.out.println("FPs:");           for(int i=0;i<leafBlockFPs.length;i++) {             System.out.println("  " + i + " " + leafBlockFPs[i]);           }           */
break|break;
block|}
name|levelCount
operator|*=
literal|2
expr_stmt|;
block|}
block|}
name|this
operator|.
name|leafBlockFPs
operator|=
name|leafBlockFPs
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/** Called by consumers that have their own on-disk format for the index */
DECL|method|BKDReader
specifier|protected
name|BKDReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|numDims
parameter_list|,
name|int
name|maxPointsInLeafNode
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|long
index|[]
name|leafBlockFPs
parameter_list|,
name|byte
index|[]
name|splitPackedValues
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
name|this
operator|.
name|maxPointsInLeafNode
operator|=
name|maxPointsInLeafNode
expr_stmt|;
name|this
operator|.
name|bytesPerDim
operator|=
name|bytesPerDim
expr_stmt|;
name|packedBytesLength
operator|=
name|numDims
operator|*
name|bytesPerDim
expr_stmt|;
name|this
operator|.
name|leafNodeOffset
operator|=
name|leafBlockFPs
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|leafBlockFPs
operator|=
name|leafBlockFPs
expr_stmt|;
name|this
operator|.
name|splitPackedValues
operator|=
name|splitPackedValues
expr_stmt|;
block|}
DECL|class|VerifyVisitor
specifier|private
specifier|static
class|class
name|VerifyVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|cellMinPacked
name|byte
index|[]
name|cellMinPacked
decl_stmt|;
DECL|field|cellMaxPacked
name|byte
index|[]
name|cellMaxPacked
decl_stmt|;
DECL|field|lastPackedValue
name|byte
index|[]
name|lastPackedValue
decl_stmt|;
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|VerifyVisitor
specifier|public
name|VerifyVisitor
parameter_list|(
name|int
name|numDims
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
name|this
operator|.
name|bytesPerDim
operator|=
name|bytesPerDim
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"docID="
operator|+
name|docID
operator|+
literal|" is out of bounds of 0.."
operator|+
name|maxDoc
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|cellMinPacked
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|)
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
operator|+
literal|" for docID="
operator|+
name|docID
operator|+
literal|" dim="
operator|+
name|dim
operator|+
literal|" is less than this leaf block's minimum="
operator|+
operator|new
name|BytesRef
argument_list|(
name|cellMinPacked
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|cellMaxPacked
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
operator|+
literal|" for docID="
operator|+
name|docID
operator|+
literal|" dim="
operator|+
name|dim
operator|+
literal|" is greater than this leaf block's maximum="
operator|+
operator|new
name|BytesRef
argument_list|(
name|cellMaxPacked
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|numDims
operator|==
literal|1
condition|)
block|{
comment|// With only 1D, all values should always be in sorted order
if|if
condition|(
name|lastPackedValue
operator|==
literal|null
condition|)
block|{
name|lastPackedValue
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|packedValue
argument_list|,
name|packedValue
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NumericUtils
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|lastPackedValue
argument_list|,
literal|0
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|packedValue
argument_list|)
operator|+
literal|" for docID="
operator|+
name|docID
operator|+
literal|" dim=0"
operator|+
literal|" sorts before last value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|lastPackedValue
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|,
name|lastPackedValue
argument_list|,
literal|0
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/** Only used for debugging, to make sure all values in each leaf block fall within the range expected by the index */
comment|// TODO: maybe we can get this into CheckIndex?
DECL|method|verify
specifier|public
name|void
name|verify
parameter_list|(
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("BKDR.verify this=" + this);
comment|// Visits every doc in every leaf block and confirms that
comment|// their values agree with the index:
name|byte
index|[]
name|rootMinPacked
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
name|byte
index|[]
name|rootMaxPacked
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|rootMaxPacked
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|IntersectState
name|state
init|=
operator|new
name|IntersectState
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|numDims
argument_list|,
name|packedBytesLength
argument_list|,
name|maxPointsInLeafNode
argument_list|,
operator|new
name|VerifyVisitor
argument_list|(
name|numDims
argument_list|,
name|bytesPerDim
argument_list|,
name|maxDoc
argument_list|)
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|state
argument_list|,
literal|1
argument_list|,
name|rootMinPacked
argument_list|,
name|rootMaxPacked
argument_list|)
expr_stmt|;
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|IntersectState
name|state
parameter_list|,
name|int
name|nodeID
parameter_list|,
name|byte
index|[]
name|cellMinPacked
parameter_list|,
name|byte
index|[]
name|cellMaxPacked
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodeID
operator|>=
name|leafNodeOffset
condition|)
block|{
name|int
name|leafID
init|=
name|nodeID
operator|-
name|leafNodeOffset
decl_stmt|;
comment|// In the unbalanced case it's possible the left most node only has one child:
if|if
condition|(
name|leafID
operator|<
name|leafBlockFPs
operator|.
name|length
condition|)
block|{
comment|//System.out.println("CHECK nodeID=" + nodeID + " leaf=" + (nodeID-leafNodeOffset) + " offset=" + leafNodeOffset + " fp=" + leafBlockFPs[leafID]);
comment|//System.out.println("BKDR.verify leafID=" + leafID + " nodeID=" + nodeID + " fp=" + leafBlockFPs[leafID] + " min=" + new BytesRef(cellMinPacked) + " max=" + new BytesRef(cellMaxPacked));
comment|// Leaf node: check that all values are in fact in bounds:
name|VerifyVisitor
name|visitor
init|=
operator|(
name|VerifyVisitor
operator|)
name|state
operator|.
name|visitor
decl_stmt|;
name|visitor
operator|.
name|cellMinPacked
operator|=
name|cellMinPacked
expr_stmt|;
name|visitor
operator|.
name|cellMaxPacked
operator|=
name|cellMaxPacked
expr_stmt|;
name|int
name|count
init|=
name|readDocIDs
argument_list|(
name|state
operator|.
name|in
argument_list|,
name|leafBlockFPs
index|[
name|leafID
index|]
argument_list|,
name|state
operator|.
name|scratchDocIDs
argument_list|)
decl_stmt|;
name|visitDocValues
argument_list|(
name|state
operator|.
name|commonPrefixLengths
argument_list|,
name|state
operator|.
name|scratchPackedValue
argument_list|,
name|state
operator|.
name|in
argument_list|,
name|state
operator|.
name|scratchDocIDs
argument_list|,
name|count
argument_list|,
name|state
operator|.
name|visitor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("BKDR.verify skip leafID=" + leafID);
block|}
block|}
else|else
block|{
comment|// Non-leaf node:
name|int
name|address
init|=
name|nodeID
operator|*
operator|(
name|bytesPerDim
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|splitDim
init|=
name|splitPackedValues
index|[
name|address
index|]
operator|&
literal|0xff
decl_stmt|;
assert|assert
name|splitDim
operator|<
name|numDims
assert|;
name|byte
index|[]
name|splitPackedValue
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
comment|// Recurse on left sub-tree:
name|System
operator|.
name|arraycopy
argument_list|(
name|cellMaxPacked
argument_list|,
literal|0
argument_list|,
name|splitPackedValue
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|splitPackedValues
argument_list|,
name|address
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|splitDim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
argument_list|,
name|cellMinPacked
argument_list|,
name|splitPackedValue
argument_list|)
expr_stmt|;
comment|// Recurse on right sub-tree:
name|System
operator|.
name|arraycopy
argument_list|(
name|cellMinPacked
argument_list|,
literal|0
argument_list|,
name|splitPackedValue
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|splitPackedValues
argument_list|,
name|address
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|splitDim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|cellMaxPacked
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IntersectState
specifier|static
specifier|final
class|class
name|IntersectState
block|{
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|scratchDocIDs
specifier|final
name|int
index|[]
name|scratchDocIDs
decl_stmt|;
DECL|field|scratchPackedValue
specifier|final
name|byte
index|[]
name|scratchPackedValue
decl_stmt|;
DECL|field|commonPrefixLengths
specifier|final
name|int
index|[]
name|commonPrefixLengths
decl_stmt|;
DECL|field|visitor
specifier|final
name|IntersectVisitor
name|visitor
decl_stmt|;
DECL|method|IntersectState
specifier|public
name|IntersectState
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|numDims
parameter_list|,
name|int
name|packedBytesLength
parameter_list|,
name|int
name|maxPointsInLeafNode
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|visitor
operator|=
name|visitor
expr_stmt|;
name|this
operator|.
name|commonPrefixLengths
operator|=
operator|new
name|int
index|[
name|numDims
index|]
expr_stmt|;
name|this
operator|.
name|scratchDocIDs
operator|=
operator|new
name|int
index|[
name|maxPointsInLeafNode
index|]
expr_stmt|;
name|this
operator|.
name|scratchPackedValue
operator|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
expr_stmt|;
block|}
block|}
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|IntersectState
name|state
init|=
operator|new
name|IntersectState
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|numDims
argument_list|,
name|packedBytesLength
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|visitor
argument_list|)
decl_stmt|;
name|byte
index|[]
name|rootMinPacked
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
name|byte
index|[]
name|rootMaxPacked
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|rootMaxPacked
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|intersect
argument_list|(
name|state
argument_list|,
literal|1
argument_list|,
name|rootMinPacked
argument_list|,
name|rootMaxPacked
argument_list|)
expr_stmt|;
block|}
comment|/** Fast path: this is called when the query box fully encompasses all cells under this node. */
DECL|method|addAll
specifier|private
name|void
name|addAll
parameter_list|(
name|IntersectState
name|state
parameter_list|,
name|int
name|nodeID
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("R: addAll nodeID=" + nodeID);
if|if
condition|(
name|nodeID
operator|>=
name|leafNodeOffset
condition|)
block|{
comment|//System.out.println("ADDALL");
name|visitDocIDs
argument_list|(
name|state
operator|.
name|in
argument_list|,
name|leafBlockFPs
index|[
name|nodeID
operator|-
name|leafNodeOffset
index|]
argument_list|,
name|state
operator|.
name|visitor
argument_list|)
expr_stmt|;
comment|// TODO: we can assert that the first value here in fact matches what the index claimed?
block|}
else|else
block|{
name|addAll
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
argument_list|)
expr_stmt|;
name|addAll
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|visitDocIDs
specifier|protected
name|void
name|visitDocIDs
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|blockFP
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Leaf node
name|in
operator|.
name|seek
argument_list|(
name|blockFP
argument_list|)
expr_stmt|;
comment|// How many points are stored in this leaf cell:
name|int
name|count
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|visitor
operator|.
name|grow
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readDocIDs
specifier|protected
name|int
name|readDocIDs
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|blockFP
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|blockFP
argument_list|)
expr_stmt|;
comment|// How many points are stored in this leaf cell:
name|int
name|count
init|=
name|in
operator|.
name|readVInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|docIDs
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|visitDocValues
specifier|protected
name|void
name|visitDocValues
parameter_list|(
name|int
index|[]
name|commonPrefixLengths
parameter_list|,
name|byte
index|[]
name|scratchPackedValue
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|prefix
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|commonPrefixLengths
index|[
name|dim
index|]
operator|=
name|prefix
expr_stmt|;
if|if
condition|(
name|prefix
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|readBytes
argument_list|(
name|scratchPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("R: " + dim + " of " + numDims + " prefix=" + prefix);
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
name|count
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|prefix
init|=
name|commonPrefixLengths
index|[
name|dim
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|scratchPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
operator|+
name|prefix
argument_list|,
name|bytesPerDim
operator|-
name|prefix
argument_list|)
expr_stmt|;
block|}
name|visitor
operator|.
name|visit
argument_list|(
name|docIDs
index|[
name|i
index|]
argument_list|,
name|scratchPackedValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|intersect
specifier|private
name|void
name|intersect
parameter_list|(
name|IntersectState
name|state
parameter_list|,
name|int
name|nodeID
parameter_list|,
name|byte
index|[]
name|cellMinPacked
parameter_list|,
name|byte
index|[]
name|cellMaxPacked
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*     System.out.println("\nR: intersect nodeID=" + nodeID);     for(int dim=0;dim<numDims;dim++) {       System.out.println("  dim=" + dim + "\n    cellMin=" + new BytesRef(cellMinPacked, dim*bytesPerDim, bytesPerDim) + "\n    cellMax=" + new BytesRef(cellMaxPacked, dim*bytesPerDim, bytesPerDim));     }     */
name|Relation
name|r
init|=
name|state
operator|.
name|visitor
operator|.
name|compare
argument_list|(
name|cellMinPacked
argument_list|,
name|cellMaxPacked
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
condition|)
block|{
comment|// This cell is fully outside of the query shape: stop recursing
return|return;
block|}
elseif|else
if|if
condition|(
name|r
operator|==
name|Relation
operator|.
name|CELL_INSIDE_QUERY
condition|)
block|{
comment|// This cell is fully inside of the query shape: recursively add all points in this cell without filtering
name|addAll
argument_list|(
name|state
argument_list|,
name|nodeID
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|// The cell crosses the shape boundary, or the cell fully contains the query, so we fall through and do full filtering
block|}
if|if
condition|(
name|nodeID
operator|>=
name|leafNodeOffset
condition|)
block|{
comment|// TODO: we can assert that the first value here in fact matches what the index claimed?
name|int
name|leafID
init|=
name|nodeID
operator|-
name|leafNodeOffset
decl_stmt|;
comment|// In the unbalanced case it's possible the left most node only has one child:
if|if
condition|(
name|leafID
operator|<
name|leafBlockFPs
operator|.
name|length
condition|)
block|{
comment|// Leaf node; scan and filter all points in this block:
name|int
name|count
init|=
name|readDocIDs
argument_list|(
name|state
operator|.
name|in
argument_list|,
name|leafBlockFPs
index|[
name|leafID
index|]
argument_list|,
name|state
operator|.
name|scratchDocIDs
argument_list|)
decl_stmt|;
comment|// Again, this time reading values and checking with the visitor
name|visitDocValues
argument_list|(
name|state
operator|.
name|commonPrefixLengths
argument_list|,
name|state
operator|.
name|scratchPackedValue
argument_list|,
name|state
operator|.
name|in
argument_list|,
name|state
operator|.
name|scratchDocIDs
argument_list|,
name|count
argument_list|,
name|state
operator|.
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Non-leaf node: recurse on the split left and right nodes
comment|// TODO: save the unused 1 byte prefix (it's always 0) in the 1d case here:
name|int
name|address
init|=
name|nodeID
operator|*
operator|(
name|bytesPerDim
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|splitDim
init|=
name|splitPackedValues
index|[
name|address
index|]
operator|&
literal|0xff
decl_stmt|;
assert|assert
name|splitDim
operator|<
name|numDims
assert|;
comment|// TODO: can we alloc& reuse this up front?
comment|// TODO: can we alloc& reuse this up front?
name|byte
index|[]
name|splitPackedValue
init|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
decl_stmt|;
comment|// Recurse on left sub-tree:
name|System
operator|.
name|arraycopy
argument_list|(
name|cellMaxPacked
argument_list|,
literal|0
argument_list|,
name|splitPackedValue
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|splitPackedValues
argument_list|,
name|address
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|splitDim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|intersect
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
argument_list|,
name|cellMinPacked
argument_list|,
name|splitPackedValue
argument_list|)
expr_stmt|;
comment|// Recurse on right sub-tree:
name|System
operator|.
name|arraycopy
argument_list|(
name|cellMinPacked
argument_list|,
literal|0
argument_list|,
name|splitPackedValue
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|splitPackedValues
argument_list|,
name|address
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|splitDim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|intersect
argument_list|(
name|state
argument_list|,
literal|2
operator|*
name|nodeID
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|cellMaxPacked
argument_list|)
expr_stmt|;
block|}
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
name|splitPackedValues
operator|.
name|length
operator|+
name|leafBlockFPs
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
block|}
end_class

end_unit

